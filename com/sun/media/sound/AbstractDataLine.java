package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

abstract class AbstractDataLine extends AbstractLine implements DataLine {
  private final AudioFormat defaultFormat;
  
  private final int defaultBufferSize;
  
  protected final Object lock = new Object();
  
  protected AudioFormat format;
  
  protected int bufferSize;
  
  protected boolean running = false;
  
  private boolean started = false;
  
  private boolean active = false;
  
  protected AbstractDataLine(DataLine.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl) { this(paramInfo, paramAbstractMixer, paramArrayOfControl, null, -1); }
  
  protected AbstractDataLine(DataLine.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl, AudioFormat paramAudioFormat, int paramInt) {
    super(paramInfo, paramAbstractMixer, paramArrayOfControl);
    if (paramAudioFormat != null) {
      this.defaultFormat = paramAudioFormat;
    } else {
      this.defaultFormat = new AudioFormat(44100.0F, 16, 2, true, Platform.isBigEndian());
    } 
    if (paramInt > 0) {
      this.defaultBufferSize = paramInt;
    } else {
      this.defaultBufferSize = (int)(this.defaultFormat.getFrameRate() / 2.0F) * this.defaultFormat.getFrameSize();
    } 
    this.format = this.defaultFormat;
    this.bufferSize = this.defaultBufferSize;
  }
  
  public final void open(AudioFormat paramAudioFormat, int paramInt) throws LineUnavailableException {
    synchronized (this.mixer) {
      if (!isOpen()) {
        Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
        this.mixer.open(this);
        try {
          implOpen(paramAudioFormat, paramInt);
          setOpen(true);
        } catch (LineUnavailableException lineUnavailableException) {
          this.mixer.close(this);
          throw lineUnavailableException;
        } 
      } else {
        if (!paramAudioFormat.matches(getFormat()))
          throw new IllegalStateException("Line is already open with format " + getFormat() + " and bufferSize " + getBufferSize()); 
        if (paramInt > 0)
          setBufferSize(paramInt); 
      } 
    } 
  }
  
  public final void open(AudioFormat paramAudioFormat) throws LineUnavailableException { open(paramAudioFormat, -1); }
  
  public int available() { return 0; }
  
  public void drain() {}
  
  public void flush() {}
  
  public final void start() {
    synchronized (this.mixer) {
      if (isOpen() && !isStartedRunning()) {
        this.mixer.start(this);
        implStart();
        this.running = true;
      } 
    } 
    synchronized (this.lock) {
      this.lock.notifyAll();
    } 
  }
  
  public final void stop() {
    synchronized (this.mixer) {
      if (isOpen() && isStartedRunning()) {
        implStop();
        this.mixer.stop(this);
        this.running = false;
        if (this.started && !isActive())
          setStarted(false); 
      } 
    } 
    synchronized (this.lock) {
      this.lock.notifyAll();
    } 
  }
  
  public final boolean isRunning() { return this.started; }
  
  public final boolean isActive() { return this.active; }
  
  public final long getMicrosecondPosition() {
    long l = getLongFramePosition();
    if (l != -1L)
      l = Toolkit.frames2micros(getFormat(), l); 
    return l;
  }
  
  public final AudioFormat getFormat() { return this.format; }
  
  public final int getBufferSize() { return this.bufferSize; }
  
  public final int setBufferSize(int paramInt) { return getBufferSize(); }
  
  public final float getLevel() { return -1.0F; }
  
  final boolean isStartedRunning() { return this.running; }
  
  final void setActive(boolean paramBoolean) {
    synchronized (this) {
      if (this.active != paramBoolean)
        this.active = paramBoolean; 
    } 
  }
  
  final void setStarted(boolean paramBoolean) {
    boolean bool = false;
    long l = getLongFramePosition();
    synchronized (this) {
      if (this.started != paramBoolean) {
        this.started = paramBoolean;
        bool = true;
      } 
    } 
    if (bool)
      if (paramBoolean) {
        sendEvents(new LineEvent(this, LineEvent.Type.START, l));
      } else {
        sendEvents(new LineEvent(this, LineEvent.Type.STOP, l));
      }  
  }
  
  final void setEOM() { setStarted(false); }
  
  public final void open() { open(this.format, this.bufferSize); }
  
  public final void close() {
    synchronized (this.mixer) {
      if (isOpen()) {
        stop();
        setOpen(false);
        implClose();
        this.mixer.close(this);
        this.format = this.defaultFormat;
        this.bufferSize = this.defaultBufferSize;
      } 
    } 
  }
  
  abstract void implOpen(AudioFormat paramAudioFormat, int paramInt) throws LineUnavailableException;
  
  abstract void implClose();
  
  abstract void implStart();
  
  abstract void implStop();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AbstractDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */