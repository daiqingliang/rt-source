package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingSourceDataLine extends SoftMixingDataLine implements SourceDataLine {
  private boolean open = false;
  
  private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  
  private int framesize;
  
  private int bufferSize = -1;
  
  private float[] readbuffer;
  
  private boolean active = false;
  
  private byte[] cycling_buffer;
  
  private int cycling_read_pos = 0;
  
  private int cycling_write_pos = 0;
  
  private int cycling_avail = 0;
  
  private long cycling_framepos = 0L;
  
  private AudioFloatInputStream afis;
  
  private boolean _active = false;
  
  private AudioFormat outputformat;
  
  private int out_nrofchannels;
  
  private int in_nrofchannels;
  
  private float _rightgain;
  
  private float _leftgain;
  
  private float _eff1gain;
  
  private float _eff2gain;
  
  SoftMixingSourceDataLine(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo) { super(paramSoftMixingMixer, paramInfo); }
  
  public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (!isOpen())
      return 0; 
    if (paramInt2 % this.framesize != 0)
      throw new IllegalArgumentException("Number of bytes does not represent an integral number of sample frames."); 
    if (paramInt1 < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt1); 
    if (paramInt1 + paramInt2 > paramArrayOfByte.length)
      throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length); 
    byte[] arrayOfByte = this.cycling_buffer;
    int i = this.cycling_buffer.length;
    byte b = 0;
    while (b != paramInt2) {
      int j;
      synchronized (this.cycling_buffer) {
        int k = this.cycling_write_pos;
        j = this.cycling_avail;
        while (b != paramInt2 && j != i) {
          arrayOfByte[k++] = paramArrayOfByte[paramInt1++];
          b++;
          j++;
          if (k == i)
            k = 0; 
        } 
        this.cycling_avail = j;
        this.cycling_write_pos = k;
        if (b == paramInt2)
          return b; 
      } 
      if (j == i) {
        try {
          Thread.sleep(1L);
        } catch (InterruptedException interruptedException) {
          return b;
        } 
        if (!isRunning())
          return b; 
      } 
    } 
    return b;
  }
  
  protected void processControlLogic() {
    this._active = this.active;
    this._rightgain = this.rightgain;
    this._leftgain = this.leftgain;
    this._eff1gain = this.eff1gain;
    this._eff2gain = this.eff2gain;
  }
  
  protected void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer) {
    if (this._active) {
      float[] arrayOfFloat1 = paramArrayOfSoftAudioBuffer[0].array();
      float[] arrayOfFloat2 = paramArrayOfSoftAudioBuffer[1].array();
      int i = paramArrayOfSoftAudioBuffer[0].getSize();
      int j = i * this.in_nrofchannels;
      if (this.readbuffer == null || this.readbuffer.length < j)
        this.readbuffer = new float[j]; 
      int k = 0;
      try {
        k = this.afis.read(this.readbuffer);
        if (k != this.in_nrofchannels)
          Arrays.fill(this.readbuffer, k, j, 0.0F); 
      } catch (IOException iOException) {}
      int m = this.in_nrofchannels;
      byte b = 0;
      int n;
      for (n = 0; b < i; n += m) {
        arrayOfFloat1[b] = arrayOfFloat1[b] + this.readbuffer[n] * this._leftgain;
        b++;
      } 
      if (this.out_nrofchannels != 1)
        if (this.in_nrofchannels == 1) {
          b = 0;
          for (n = 0; b < i; n += m) {
            arrayOfFloat2[b] = arrayOfFloat2[b] + this.readbuffer[n] * this._rightgain;
            b++;
          } 
        } else {
          b = 0;
          for (n = 1; b < i; n += m) {
            arrayOfFloat2[b] = arrayOfFloat2[b] + this.readbuffer[n] * this._rightgain;
            b++;
          } 
        }  
      if (this._eff1gain > 1.0E-4D) {
        float[] arrayOfFloat = paramArrayOfSoftAudioBuffer[2].array();
        n = 0;
        int i1;
        for (i1 = 0; n < i; i1 += m) {
          arrayOfFloat[n] = arrayOfFloat[n] + this.readbuffer[i1] * this._eff1gain;
          n++;
        } 
        if (this.in_nrofchannels == 2) {
          n = 0;
          for (i1 = 1; n < i; i1 += m) {
            arrayOfFloat[n] = arrayOfFloat[n] + this.readbuffer[i1] * this._eff1gain;
            n++;
          } 
        } 
      } 
      if (this._eff2gain > 1.0E-4D) {
        float[] arrayOfFloat = paramArrayOfSoftAudioBuffer[3].array();
        n = 0;
        int i1;
        for (i1 = 0; n < i; i1 += m) {
          arrayOfFloat[n] = arrayOfFloat[n] + this.readbuffer[i1] * this._eff2gain;
          n++;
        } 
        if (this.in_nrofchannels == 2) {
          n = 0;
          for (i1 = 1; n < i; i1 += m) {
            arrayOfFloat[n] = arrayOfFloat[n] + this.readbuffer[i1] * this._eff2gain;
            n++;
          } 
        } 
      } 
    } 
  }
  
  public void open() { open(this.format); }
  
  public void open(AudioFormat paramAudioFormat) throws LineUnavailableException {
    if (this.bufferSize == -1)
      this.bufferSize = (int)(paramAudioFormat.getFrameRate() / 2.0F) * paramAudioFormat.getFrameSize(); 
    open(paramAudioFormat, this.bufferSize);
  }
  
  public void open(AudioFormat paramAudioFormat, int paramInt) throws LineUnavailableException {
    LineEvent lineEvent = null;
    if (paramInt < paramAudioFormat.getFrameSize() * 32)
      paramInt = paramAudioFormat.getFrameSize() * 32; 
    synchronized (this.control_mutex) {
      if (!isOpen()) {
        if (!this.mixer.isOpen()) {
          this.mixer.open();
          this.mixer.implicitOpen = true;
        } 
        lineEvent = new LineEvent(this, LineEvent.Type.OPEN, 0L);
        this.bufferSize = paramInt - paramInt % paramAudioFormat.getFrameSize();
        this.format = paramAudioFormat;
        this.framesize = paramAudioFormat.getFrameSize();
        this.outputformat = this.mixer.getFormat();
        this.out_nrofchannels = this.outputformat.getChannels();
        this.in_nrofchannels = paramAudioFormat.getChannels();
        this.open = true;
        this.mixer.getMainMixer().openLine(this);
        this.cycling_buffer = new byte[this.framesize * paramInt];
        this.cycling_read_pos = 0;
        this.cycling_write_pos = 0;
        this.cycling_avail = 0;
        this.cycling_framepos = 0L;
        InputStream inputStream = new InputStream() {
            public int read() {
              byte[] arrayOfByte = new byte[1];
              int i = read(arrayOfByte);
              return (i < 0) ? i : (arrayOfByte[0] & 0xFF);
            }
            
            public int available() {
              synchronized (SoftMixingSourceDataLine.this.cycling_buffer) {
                return SoftMixingSourceDataLine.this.cycling_avail;
              } 
            }
            
            public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
              synchronized (SoftMixingSourceDataLine.this.cycling_buffer) {
                if (param1Int2 > SoftMixingSourceDataLine.this.cycling_avail)
                  param1Int2 = SoftMixingSourceDataLine.this.cycling_avail; 
                int i = SoftMixingSourceDataLine.this.cycling_read_pos;
                byte[] arrayOfByte = SoftMixingSourceDataLine.this.cycling_buffer;
                int j = arrayOfByte.length;
                for (byte b = 0; b < param1Int2; b++) {
                  param1ArrayOfByte[param1Int1++] = arrayOfByte[i];
                  if (++i == j)
                    i = 0; 
                } 
                SoftMixingSourceDataLine.this.cycling_read_pos = i;
                SoftMixingSourceDataLine.this.cycling_avail = SoftMixingSourceDataLine.this.cycling_avail - param1Int2;
                SoftMixingSourceDataLine.this.cycling_framepos = SoftMixingSourceDataLine.this.cycling_framepos + (param1Int2 / SoftMixingSourceDataLine.this.framesize);
              } 
              return param1Int2;
            }
          };
        this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(inputStream, paramAudioFormat, -1L));
        this.afis = new NonBlockingFloatInputStream(this.afis);
        if (Math.abs(paramAudioFormat.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6D)
          this.afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(this.afis, this.outputformat); 
      } else if (!paramAudioFormat.matches(getFormat())) {
        throw new IllegalStateException("Line is already open with format " + getFormat() + " and bufferSize " + getBufferSize());
      } 
    } 
    if (lineEvent != null)
      sendEvent(lineEvent); 
  }
  
  public int available() {
    synchronized (this.cycling_buffer) {
      return this.cycling_buffer.length - this.cycling_avail;
    } 
  }
  
  public void drain() {
    while (true) {
      int i;
      synchronized (this.cycling_buffer) {
        i = this.cycling_avail;
      } 
      if (i != 0)
        return; 
      try {
        Thread.sleep(1L);
      } catch (InterruptedException interruptedException) {
        break;
      } 
    } 
  }
  
  public void flush() {
    synchronized (this.cycling_buffer) {
      this.cycling_read_pos = 0;
      this.cycling_write_pos = 0;
      this.cycling_avail = 0;
    } 
  }
  
  public int getBufferSize() {
    synchronized (this.control_mutex) {
      return this.bufferSize;
    } 
  }
  
  public AudioFormat getFormat() {
    synchronized (this.control_mutex) {
      return this.format;
    } 
  }
  
  public int getFramePosition() { return (int)getLongFramePosition(); }
  
  public float getLevel() { return -1.0F; }
  
  public long getLongFramePosition() {
    synchronized (this.cycling_buffer) {
      return this.cycling_framepos;
    } 
  }
  
  public long getMicrosecondPosition() { return (long)(getLongFramePosition() * 1000000.0D / getFormat().getSampleRate()); }
  
  public boolean isActive() {
    synchronized (this.control_mutex) {
      return this.active;
    } 
  }
  
  public boolean isRunning() {
    synchronized (this.control_mutex) {
      return this.active;
    } 
  }
  
  public void start() {
    LineEvent lineEvent = null;
    synchronized (this.control_mutex) {
      if (isOpen()) {
        if (this.active)
          return; 
        this.active = true;
        lineEvent = new LineEvent(this, LineEvent.Type.START, getLongFramePosition());
      } 
    } 
    if (lineEvent != null)
      sendEvent(lineEvent); 
  }
  
  public void stop() {
    LineEvent lineEvent = null;
    synchronized (this.control_mutex) {
      if (isOpen()) {
        if (!this.active)
          return; 
        this.active = false;
        lineEvent = new LineEvent(this, LineEvent.Type.STOP, getLongFramePosition());
      } 
    } 
    if (lineEvent != null)
      sendEvent(lineEvent); 
  }
  
  public void close() {
    LineEvent lineEvent = null;
    synchronized (this.control_mutex) {
      if (!isOpen())
        return; 
      stop();
      lineEvent = new LineEvent(this, LineEvent.Type.CLOSE, getLongFramePosition());
      this.open = false;
      this.mixer.getMainMixer().closeLine(this);
    } 
    if (lineEvent != null)
      sendEvent(lineEvent); 
  }
  
  public boolean isOpen() {
    synchronized (this.control_mutex) {
      return this.open;
    } 
  }
  
  private static class NonBlockingFloatInputStream extends AudioFloatInputStream {
    AudioFloatInputStream ais;
    
    NonBlockingFloatInputStream(AudioFloatInputStream param1AudioFloatInputStream) { this.ais = param1AudioFloatInputStream; }
    
    public int available() { return this.ais.available(); }
    
    public void close() { this.ais.close(); }
    
    public AudioFormat getFormat() { return this.ais.getFormat(); }
    
    public long getFrameLength() { return this.ais.getFrameLength(); }
    
    public void mark(int param1Int) { this.ais.mark(param1Int); }
    
    public boolean markSupported() { return this.ais.markSupported(); }
    
    public int read(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      int i = available();
      if (param1Int2 > i) {
        int j = this.ais.read(param1ArrayOfFloat, param1Int1, i);
        Arrays.fill(param1ArrayOfFloat, param1Int1 + j, param1Int1 + param1Int2, 0.0F);
        return param1Int2;
      } 
      return this.ais.read(param1ArrayOfFloat, param1Int1, param1Int2);
    }
    
    public void reset() { this.ais.reset(); }
    
    public long skip(long param1Long) throws IOException { return this.ais.skip(param1Long); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMixingSourceDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */