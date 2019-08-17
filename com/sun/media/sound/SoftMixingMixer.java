package com.sun.media.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingMixer implements Mixer {
  static final String INFO_NAME = "Gervill Sound Mixer";
  
  static final String INFO_VENDOR = "OpenJDK Proposal";
  
  static final String INFO_DESCRIPTION = "Software Sound Mixer";
  
  static final String INFO_VERSION = "1.0";
  
  static final Mixer.Info info = new Info();
  
  final Object control_mutex = this;
  
  boolean implicitOpen = false;
  
  private boolean open = false;
  
  private SoftMixingMainMixer mainmixer = null;
  
  private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  
  private SourceDataLine sourceDataLine = null;
  
  private SoftAudioPusher pusher = null;
  
  private AudioInputStream pusher_stream = null;
  
  private final float controlrate = 147.0F;
  
  private final long latency = 100000L;
  
  private final boolean jitter_correction = false;
  
  private final List<LineListener> listeners = new ArrayList();
  
  private final Line.Info[] sourceLineInfo = new Line.Info[2];
  
  public SoftMixingMixer() {
    ArrayList arrayList = new ArrayList();
    for (byte b = 1; b <= 2; b++) {
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, b, b, -1.0F, false));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, b, b, -1.0F, false));
      for (byte b1 = 16; b1 < 32; b1 += 8) {
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, b1, b, b * b1 / 8, -1.0F, false));
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, b1, b, b * b1 / 8, -1.0F, false));
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, b1, b, b * b1 / 8, -1.0F, true));
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, b1, b, b * b1 / 8, -1.0F, true));
      } 
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, b, b * 4, -1.0F, false));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, b, b * 4, -1.0F, true));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, b, b * 8, -1.0F, false));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, b, b * 8, -1.0F, true));
    } 
    AudioFormat[] arrayOfAudioFormat = (AudioFormat[])arrayList.toArray(new AudioFormat[arrayList.size()]);
    this.sourceLineInfo[0] = new DataLine.Info(SourceDataLine.class, arrayOfAudioFormat, -1, -1);
    this.sourceLineInfo[1] = new DataLine.Info(javax.sound.sampled.Clip.class, arrayOfAudioFormat, -1, -1);
  }
  
  public Line getLine(Line.Info paramInfo) throws LineUnavailableException {
    if (!isLineSupported(paramInfo))
      throw new IllegalArgumentException("Line unsupported: " + paramInfo); 
    if (paramInfo.getLineClass() == SourceDataLine.class)
      return new SoftMixingSourceDataLine(this, (DataLine.Info)paramInfo); 
    if (paramInfo.getLineClass() == javax.sound.sampled.Clip.class)
      return new SoftMixingClip(this, (DataLine.Info)paramInfo); 
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo) { return (paramInfo.getLineClass() == SourceDataLine.class) ? -1 : ((paramInfo.getLineClass() == javax.sound.sampled.Clip.class) ? -1 : 0); }
  
  public Mixer.Info getMixerInfo() { return info; }
  
  public Line.Info[] getSourceLineInfo() {
    Line.Info[] arrayOfInfo = new Line.Info[this.sourceLineInfo.length];
    System.arraycopy(this.sourceLineInfo, 0, arrayOfInfo, 0, this.sourceLineInfo.length);
    return arrayOfInfo;
  }
  
  public Line.Info[] getSourceLineInfo(Line.Info paramInfo) {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < this.sourceLineInfo.length; b++) {
      if (paramInfo.matches(this.sourceLineInfo[b]))
        arrayList.add(this.sourceLineInfo[b]); 
    } 
    return (Info[])arrayList.toArray(new Line.Info[arrayList.size()]);
  }
  
  public Line[] getSourceLines() {
    Line[] arrayOfLine;
    synchronized (this.control_mutex) {
      if (this.mainmixer == null)
        return new Line[0]; 
      SoftMixingDataLine[] arrayOfSoftMixingDataLine = this.mainmixer.getOpenLines();
      arrayOfLine = new Line[arrayOfSoftMixingDataLine.length];
      for (byte b = 0; b < arrayOfLine.length; b++)
        arrayOfLine[b] = arrayOfSoftMixingDataLine[b]; 
    } 
    return arrayOfLine;
  }
  
  public Line.Info[] getTargetLineInfo() { return new Line.Info[0]; }
  
  public Line.Info[] getTargetLineInfo(Line.Info paramInfo) { return new Line.Info[0]; }
  
  public Line[] getTargetLines() { return new Line[0]; }
  
  public boolean isLineSupported(Line.Info paramInfo) {
    if (paramInfo != null)
      for (byte b = 0; b < this.sourceLineInfo.length; b++) {
        if (paramInfo.matches(this.sourceLineInfo[b]))
          return true; 
      }  
    return false;
  }
  
  public boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean) { return false; }
  
  public void synchronize(Line[] paramArrayOfLine, boolean paramBoolean) { throw new IllegalArgumentException("Synchronization not supported by this mixer."); }
  
  public void unsynchronize(Line[] paramArrayOfLine) { throw new IllegalArgumentException("Synchronization not supported by this mixer."); }
  
  public void addLineListener(LineListener paramLineListener) {
    synchronized (this.control_mutex) {
      this.listeners.add(paramLineListener);
    } 
  }
  
  private void sendEvent(LineEvent paramLineEvent) {
    if (this.listeners.size() == 0)
      return; 
    LineListener[] arrayOfLineListener = (LineListener[])this.listeners.toArray(new LineListener[this.listeners.size()]);
    for (LineListener lineListener : arrayOfLineListener)
      lineListener.update(paramLineEvent); 
  }
  
  public void close() {
    if (!isOpen())
      return; 
    sendEvent(new LineEvent(this, LineEvent.Type.CLOSE, -1L));
    SoftAudioPusher softAudioPusher = null;
    AudioInputStream audioInputStream = null;
    synchronized (this.control_mutex) {
      if (this.pusher != null) {
        softAudioPusher = this.pusher;
        audioInputStream = this.pusher_stream;
        this.pusher = null;
        this.pusher_stream = null;
      } 
    } 
    if (softAudioPusher != null) {
      softAudioPusher.stop();
      try {
        audioInputStream.close();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
    } 
    synchronized (this.control_mutex) {
      if (this.mainmixer != null)
        this.mainmixer.close(); 
      this.open = false;
      if (this.sourceDataLine != null) {
        this.sourceDataLine.drain();
        this.sourceDataLine.close();
        this.sourceDataLine = null;
      } 
    } 
  }
  
  public Control getControl(Control.Type paramType) { throw new IllegalArgumentException("Unsupported control type : " + paramType); }
  
  public Control[] getControls() { return new Control[0]; }
  
  public Line.Info getLineInfo() { return new Line.Info(Mixer.class); }
  
  public boolean isControlSupported(Control.Type paramType) { return false; }
  
  public boolean isOpen() {
    synchronized (this.control_mutex) {
      return this.open;
    } 
  }
  
  public void open() {
    if (isOpen()) {
      this.implicitOpen = false;
      return;
    } 
    open(null);
  }
  
  public void open(SourceDataLine paramSourceDataLine) throws LineUnavailableException {
    if (isOpen()) {
      this.implicitOpen = false;
      return;
    } 
    synchronized (this.control_mutex) {
      try {
        if (paramSourceDataLine != null)
          this.format = paramSourceDataLine.getFormat(); 
        AudioInputStream audioInputStream = openStream(getFormat());
        if (paramSourceDataLine == null) {
          synchronized (SoftMixingMixerProvider.mutex) {
            SoftMixingMixerProvider.lockthread = Thread.currentThread();
          } 
          try {
            Mixer mixer = AudioSystem.getMixer(null);
            if (mixer != null) {
              DataLine.Info info1 = null;
              AudioFormat audioFormat = null;
              Line.Info[] arrayOfInfo = mixer.getSourceLineInfo();
              byte b;
              label89: for (b = 0; b < arrayOfInfo.length; b++) {
                if (arrayOfInfo[b].getLineClass() == SourceDataLine.class) {
                  DataLine.Info info2 = (DataLine.Info)arrayOfInfo[b];
                  AudioFormat[] arrayOfAudioFormat = info2.getFormats();
                  for (byte b1 = 0; b1 < arrayOfAudioFormat.length; b1++) {
                    AudioFormat audioFormat1 = arrayOfAudioFormat[b1];
                    if ((audioFormat1.getChannels() == 2 || audioFormat1.getChannels() == -1) && (audioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || audioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && (audioFormat1.getSampleRate() == -1.0F || audioFormat1.getSampleRate() == 48000.0D) && (audioFormat1.getSampleSizeInBits() == -1 || audioFormat1.getSampleSizeInBits() == 16)) {
                      info1 = info2;
                      int k = audioFormat1.getChannels();
                      boolean bool1 = audioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
                      float f = audioFormat1.getSampleRate();
                      boolean bool2 = audioFormat1.isBigEndian();
                      int m = audioFormat1.getSampleSizeInBits();
                      if (m == -1)
                        m = 16; 
                      if (k == -1)
                        k = 2; 
                      if (f == -1.0F)
                        f = 48000.0F; 
                      audioFormat = new AudioFormat(f, m, k, bool1, bool2);
                      break label89;
                    } 
                  } 
                } 
              } 
              if (audioFormat != null) {
                this.format = audioFormat;
                paramSourceDataLine = (SourceDataLine)mixer.getLine(info1);
              } 
            } 
            if (paramSourceDataLine == null)
              paramSourceDataLine = AudioSystem.getSourceDataLine(this.format); 
          } finally {
            synchronized (SoftMixingMixerProvider.mutex) {
              SoftMixingMixerProvider.lockthread = null;
            } 
          } 
          if (paramSourceDataLine == null)
            throw new IllegalArgumentException("No line matching " + info.toString() + " is supported."); 
        } 
        getClass();
        double d = 100000.0D;
        if (!paramSourceDataLine.isOpen()) {
          int k = getFormat().getFrameSize() * (int)(getFormat().getFrameRate() * d / 1000000.0D);
          paramSourceDataLine.open(getFormat(), k);
          this.sourceDataLine = paramSourceDataLine;
        } 
        if (!paramSourceDataLine.isActive())
          paramSourceDataLine.start(); 
        int i = 512;
        try {
          i = audioInputStream.available();
        } catch (IOException iOException) {}
        int j = paramSourceDataLine.getBufferSize();
        j -= j % i;
        if (j < 3 * i)
          j = 3 * i; 
        this.pusher = new SoftAudioPusher(paramSourceDataLine, audioInputStream, i);
        this.pusher_stream = audioInputStream;
        this.pusher.start();
      } catch (LineUnavailableException lineUnavailableException) {
        if (isOpen())
          close(); 
        throw new LineUnavailableException(lineUnavailableException.toString());
      } 
    } 
  }
  
  public AudioInputStream openStream(AudioFormat paramAudioFormat) throws LineUnavailableException {
    if (isOpen())
      throw new LineUnavailableException("Mixer is already open"); 
    synchronized (this.control_mutex) {
      this.open = true;
      this.implicitOpen = false;
      if (paramAudioFormat != null)
        this.format = paramAudioFormat; 
      this.mainmixer = new SoftMixingMainMixer(this);
      sendEvent(new LineEvent(this, LineEvent.Type.OPEN, -1L));
      return this.mainmixer.getInputStream();
    } 
  }
  
  public void removeLineListener(LineListener paramLineListener) {
    synchronized (this.control_mutex) {
      this.listeners.remove(paramLineListener);
    } 
  }
  
  public long getLatency() {
    synchronized (this.control_mutex) {
      return 100000L;
    } 
  }
  
  public AudioFormat getFormat() {
    synchronized (this.control_mutex) {
      return this.format;
    } 
  }
  
  float getControlRate() { return 147.0F; }
  
  SoftMixingMainMixer getMainMixer() { return !isOpen() ? null : this.mainmixer; }
  
  private static class Info extends Mixer.Info {
    Info() { super("Gervill Sound Mixer", "OpenJDK Proposal", "Software Sound Mixer", "1.0"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMixingMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */