package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

public final class SoftMixingClip extends SoftMixingDataLine implements Clip {
  private AudioFormat format;
  
  private int framesize;
  
  private byte[] data;
  
  private final InputStream datastream = new InputStream() {
      public int read() {
        byte[] arrayOfByte = new byte[1];
        int i = read(arrayOfByte);
        return (i < 0) ? i : (arrayOfByte[0] & 0xFF);
      }
      
      public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
        if (SoftMixingClip.this._loopcount != 0) {
          int k = SoftMixingClip.this._loopend * SoftMixingClip.this.framesize;
          int m = SoftMixingClip.this._loopstart * SoftMixingClip.this.framesize;
          int n = SoftMixingClip.this._frameposition * SoftMixingClip.this.framesize;
          if (n + param1Int2 >= k && n < k) {
            int i1 = param1Int1 + param1Int2;
            int i2 = param1Int1;
            while (param1Int1 != i1) {
              if (n == k) {
                if (SoftMixingClip.this._loopcount == 0)
                  break; 
                n = m;
                if (SoftMixingClip.this._loopcount != -1)
                  SoftMixingClip.this._loopcount--; 
              } 
              param1Int2 = i1 - param1Int1;
              int i3 = k - n;
              if (param1Int2 > i3)
                param1Int2 = i3; 
              System.arraycopy(SoftMixingClip.this.data, n, param1ArrayOfByte, param1Int1, param1Int2);
              param1Int1 += param1Int2;
            } 
            if (SoftMixingClip.this._loopcount == 0) {
              param1Int2 = i1 - param1Int1;
              int i3 = k - n;
              if (param1Int2 > i3)
                param1Int2 = i3; 
              System.arraycopy(SoftMixingClip.this.data, n, param1ArrayOfByte, param1Int1, param1Int2);
              param1Int1 += param1Int2;
            } 
            SoftMixingClip.this._frameposition = n / SoftMixingClip.this.framesize;
            return i2 - param1Int1;
          } 
        } 
        int i = SoftMixingClip.this._frameposition * SoftMixingClip.this.framesize;
        int j = SoftMixingClip.this.bufferSize - i;
        if (j == 0)
          return -1; 
        if (param1Int2 > j)
          param1Int2 = j; 
        System.arraycopy(SoftMixingClip.this.data, i, param1ArrayOfByte, param1Int1, param1Int2);
        SoftMixingClip.this._frameposition = SoftMixingClip.this._frameposition + param1Int2 / SoftMixingClip.this.framesize;
        return param1Int2;
      }
    };
  
  private int offset;
  
  private int bufferSize;
  
  private float[] readbuffer;
  
  private boolean open = false;
  
  private AudioFormat outputformat;
  
  private int out_nrofchannels;
  
  private int in_nrofchannels;
  
  private int frameposition = 0;
  
  private boolean frameposition_sg = false;
  
  private boolean active_sg = false;
  
  private int loopstart = 0;
  
  private int loopend = -1;
  
  private boolean active = false;
  
  private int loopcount = 0;
  
  private boolean _active = false;
  
  private int _frameposition = 0;
  
  private boolean loop_sg = false;
  
  private int _loopcount = 0;
  
  private int _loopstart = 0;
  
  private int _loopend = -1;
  
  private float _rightgain;
  
  private float _leftgain;
  
  private float _eff1gain;
  
  private float _eff2gain;
  
  private AudioFloatInputStream afis;
  
  SoftMixingClip(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo) { super(paramSoftMixingMixer, paramInfo); }
  
  protected void processControlLogic() {
    this._rightgain = this.rightgain;
    this._leftgain = this.leftgain;
    this._eff1gain = this.eff1gain;
    this._eff2gain = this.eff2gain;
    if (this.active_sg) {
      this._active = this.active;
      this.active_sg = false;
    } else {
      this.active = this._active;
    } 
    if (this.frameposition_sg) {
      this._frameposition = this.frameposition;
      this.frameposition_sg = false;
      this.afis = null;
    } else {
      this.frameposition = this._frameposition;
    } 
    if (this.loop_sg) {
      this._loopcount = this.loopcount;
      this._loopstart = this.loopstart;
      this._loopend = this.loopend;
    } 
    if (this.afis == null) {
      this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(this.datastream, this.format, -1L));
      if (Math.abs(this.format.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6D)
        this.afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(this.afis, this.outputformat); 
    } 
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
        if (k == -1) {
          this._active = false;
          return;
        } 
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
      if (this._eff1gain > 2.0E-4D) {
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
      if (this._eff2gain > 2.0E-4D) {
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
  
  public int getFrameLength() { return this.bufferSize / this.format.getFrameSize(); }
  
  public long getMicrosecondLength() { return (long)(getFrameLength() * 1000000.0D / getFormat().getSampleRate()); }
  
  public void loop(int paramInt) {
    LineEvent lineEvent = null;
    synchronized (this.control_mutex) {
      if (isOpen()) {
        if (this.active)
          return; 
        this.active = true;
        this.active_sg = true;
        this.loopcount = paramInt;
        lineEvent = new LineEvent(this, LineEvent.Type.START, getLongFramePosition());
      } 
    } 
    if (lineEvent != null)
      sendEvent(lineEvent); 
  }
  
  public void open(AudioInputStream paramAudioInputStream) throws LineUnavailableException, IOException {
    if (isOpen())
      throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength()); 
    if (AudioFloatConverter.getConverter(paramAudioInputStream.getFormat()) == null)
      throw new IllegalArgumentException("Invalid format : " + paramAudioInputStream.getFormat().toString()); 
    if (paramAudioInputStream.getFrameLength() != -1L) {
      byte[] arrayOfByte = new byte[(int)paramAudioInputStream.getFrameLength() * paramAudioInputStream.getFormat().getFrameSize()];
      int i = 512 * paramAudioInputStream.getFormat().getFrameSize();
      int j;
      for (j = 0; j != arrayOfByte.length; j += k) {
        if (i > arrayOfByte.length - j)
          i = arrayOfByte.length - j; 
        int k = paramAudioInputStream.read(arrayOfByte, j, i);
        if (k == -1)
          break; 
        if (k == 0)
          Thread.yield(); 
      } 
      open(paramAudioInputStream.getFormat(), arrayOfByte, 0, j);
    } else {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte[512 * paramAudioInputStream.getFormat().getFrameSize()];
      int i = 0;
      while ((i = paramAudioInputStream.read(arrayOfByte)) != -1) {
        if (i == 0)
          Thread.yield(); 
        byteArrayOutputStream.write(arrayOfByte, 0, i);
      } 
      open(paramAudioInputStream.getFormat(), byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
    } 
  }
  
  public void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws LineUnavailableException {
    synchronized (this.control_mutex) {
      if (isOpen())
        throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength()); 
      if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
        throw new IllegalArgumentException("Invalid format : " + paramAudioFormat.toString()); 
      if (paramInt2 % paramAudioFormat.getFrameSize() != 0)
        throw new IllegalArgumentException("Buffer size does not represent an integral number of sample frames!"); 
      if (paramArrayOfByte != null)
        this.data = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length); 
      this.offset = paramInt1;
      this.bufferSize = paramInt2;
      this.format = paramAudioFormat;
      this.framesize = paramAudioFormat.getFrameSize();
      this.loopstart = 0;
      this.loopend = -1;
      this.loop_sg = true;
      if (!this.mixer.isOpen()) {
        this.mixer.open();
        this.mixer.implicitOpen = true;
      } 
      this.outputformat = this.mixer.getFormat();
      this.out_nrofchannels = this.outputformat.getChannels();
      this.in_nrofchannels = paramAudioFormat.getChannels();
      this.open = true;
      this.mixer.getMainMixer().openLine(this);
    } 
  }
  
  public void setFramePosition(int paramInt) {
    synchronized (this.control_mutex) {
      this.frameposition_sg = true;
      this.frameposition = paramInt;
    } 
  }
  
  public void setLoopPoints(int paramInt1, int paramInt2) {
    synchronized (this.control_mutex) {
      if (paramInt2 != -1) {
        if (paramInt2 < paramInt1)
          throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2); 
        if (paramInt2 * this.framesize > this.bufferSize)
          throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2); 
      } 
      if (paramInt1 * this.framesize > this.bufferSize)
        throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2); 
      if (0 < paramInt1)
        throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2); 
      this.loopstart = paramInt1;
      this.loopend = paramInt2;
      this.loop_sg = true;
    } 
  }
  
  public void setMicrosecondPosition(long paramLong) { setFramePosition((int)(paramLong * getFormat().getSampleRate() / 1000000.0D)); }
  
  public int available() { return 0; }
  
  public void drain() {}
  
  public void flush() {}
  
  public int getBufferSize() { return this.bufferSize; }
  
  public AudioFormat getFormat() { return this.format; }
  
  public int getFramePosition() {
    synchronized (this.control_mutex) {
      return this.frameposition;
    } 
  }
  
  public float getLevel() { return -1.0F; }
  
  public long getLongFramePosition() { return getFramePosition(); }
  
  public long getMicrosecondPosition() { return (long)(getFramePosition() * 1000000.0D / getFormat().getSampleRate()); }
  
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
        this.active_sg = true;
        this.loopcount = 0;
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
        this.active_sg = true;
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
  
  public boolean isOpen() { return this.open; }
  
  public void open() {
    if (this.data == null)
      throw new IllegalArgumentException("Illegal call to open() in interface Clip"); 
    open(this.format, this.data, this.offset, this.bufferSize);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMixingClip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */