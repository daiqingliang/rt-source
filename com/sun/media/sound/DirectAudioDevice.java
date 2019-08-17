package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

final class DirectAudioDevice extends AbstractMixer {
  private static final int CLIP_BUFFER_TIME = 1000;
  
  private static final int DEFAULT_LINE_BUFFER_TIME = 500;
  
  private int deviceCountOpened = 0;
  
  private int deviceCountStarted = 0;
  
  DirectAudioDevice(DirectAudioDeviceProvider.DirectAudioDeviceInfo paramDirectAudioDeviceInfo) {
    super(paramDirectAudioDeviceInfo, null, null, null);
    DirectDLI directDLI1 = createDataLineInfo(true);
    if (directDLI1 != null) {
      this.sourceLineInfo = new Line.Info[2];
      this.sourceLineInfo[0] = directDLI1;
      this.sourceLineInfo[1] = new DirectDLI(Clip.class, directDLI1.getFormats(), directDLI1.getHardwareFormats(), 32, -1, null);
    } else {
      this.sourceLineInfo = new Line.Info[0];
    } 
    DirectDLI directDLI2 = createDataLineInfo(false);
    if (directDLI2 != null) {
      this.targetLineInfo = new Line.Info[1];
      this.targetLineInfo[0] = directDLI2;
    } else {
      this.targetLineInfo = new Line.Info[0];
    } 
  }
  
  private DirectDLI createDataLineInfo(boolean paramBoolean) {
    Vector vector = new Vector();
    AudioFormat[] arrayOfAudioFormat1 = null;
    AudioFormat[] arrayOfAudioFormat2 = null;
    synchronized (vector) {
      nGetFormats(getMixerIndex(), getDeviceID(), paramBoolean, vector);
      if (vector.size() > 0) {
        int i = vector.size();
        int j = i;
        arrayOfAudioFormat1 = new AudioFormat[i];
        byte b1;
        for (b1 = 0; b1 < i; b1++) {
          AudioFormat audioFormat = (AudioFormat)vector.elementAt(b1);
          arrayOfAudioFormat1[b1] = audioFormat;
          int k = audioFormat.getSampleSizeInBits();
          boolean bool1 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
          boolean bool2 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
          if (bool1 || bool2)
            j++; 
        } 
        arrayOfAudioFormat2 = new AudioFormat[j];
        b1 = 0;
        for (byte b2 = 0; b2 < i; b2++) {
          AudioFormat audioFormat = arrayOfAudioFormat1[b2];
          arrayOfAudioFormat2[b1++] = audioFormat;
          int k = audioFormat.getSampleSizeInBits();
          boolean bool1 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
          boolean bool2 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
          if (k == 8) {
            if (bool1) {
              arrayOfAudioFormat2[b1++] = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), k, audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getSampleRate(), audioFormat.isBigEndian());
            } else if (bool2) {
              arrayOfAudioFormat2[b1++] = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), k, audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getSampleRate(), audioFormat.isBigEndian());
            } 
          } else if (k > 8 && (bool1 || bool2)) {
            arrayOfAudioFormat2[b1++] = new AudioFormat(audioFormat.getEncoding(), audioFormat.getSampleRate(), k, audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getSampleRate(), !audioFormat.isBigEndian());
          } 
        } 
      } 
    } 
    return (arrayOfAudioFormat2 != null) ? new DirectDLI(paramBoolean ? SourceDataLine.class : TargetDataLine.class, arrayOfAudioFormat2, arrayOfAudioFormat1, 32, -1, null) : null;
  }
  
  public Line getLine(Line.Info paramInfo) throws LineUnavailableException {
    Line.Info info = getLineInfo(paramInfo);
    if (info == null)
      throw new IllegalArgumentException("Line unsupported: " + paramInfo); 
    if (info instanceof DataLine.Info) {
      AudioFormat audioFormat;
      DataLine.Info info1 = (DataLine.Info)info;
      int i = -1;
      AudioFormat[] arrayOfAudioFormat = null;
      if (paramInfo instanceof DataLine.Info) {
        arrayOfAudioFormat = ((DataLine.Info)paramInfo).getFormats();
        i = ((DataLine.Info)paramInfo).getMaxBufferSize();
      } 
      if (arrayOfAudioFormat == null || arrayOfAudioFormat.length == 0) {
        audioFormat = null;
      } else {
        audioFormat = arrayOfAudioFormat[arrayOfAudioFormat.length - 1];
        if (!Toolkit.isFullySpecifiedPCMFormat(audioFormat))
          audioFormat = null; 
      } 
      if (info1.getLineClass().isAssignableFrom(DirectSDL.class))
        return new DirectSDL(info1, audioFormat, i, this, null); 
      if (info1.getLineClass().isAssignableFrom(DirectClip.class))
        return new DirectClip(info1, audioFormat, i, this, null); 
      if (info1.getLineClass().isAssignableFrom(DirectTDL.class))
        return new DirectTDL(info1, audioFormat, i, this, null); 
    } 
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo) {
    Line.Info info = getLineInfo(paramInfo);
    return (info == null) ? 0 : ((info instanceof DataLine.Info) ? getMaxSimulLines() : 0);
  }
  
  protected void implOpen() throws LineUnavailableException {}
  
  protected void implClose() throws LineUnavailableException {}
  
  protected void implStart() throws LineUnavailableException {}
  
  protected void implStop() throws LineUnavailableException {}
  
  int getMixerIndex() { return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getIndex(); }
  
  int getDeviceID() { return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getDeviceID(); }
  
  int getMaxSimulLines() { return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getMaxSimulLines(); }
  
  private static void addFormat(Vector paramVector, int paramInt1, int paramInt2, int paramInt3, float paramFloat, int paramInt4, boolean paramBoolean1, boolean paramBoolean2) {
    AudioFormat.Encoding encoding = null;
    switch (paramInt4) {
      case 0:
        encoding = paramBoolean1 ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED;
        break;
      case 1:
        encoding = AudioFormat.Encoding.ULAW;
        if (paramInt1 != 8) {
          paramInt1 = 8;
          paramInt2 = paramInt3;
        } 
        break;
      case 2:
        encoding = AudioFormat.Encoding.ALAW;
        if (paramInt1 != 8) {
          paramInt1 = 8;
          paramInt2 = paramInt3;
        } 
        break;
    } 
    if (encoding == null)
      return; 
    if (paramInt2 <= 0)
      if (paramInt3 > 0) {
        paramInt2 = (paramInt1 + 7) / 8 * paramInt3;
      } else {
        paramInt2 = -1;
      }  
    paramVector.add(new AudioFormat(encoding, paramFloat, paramInt1, paramInt3, paramInt2, paramFloat, paramBoolean2));
  }
  
  protected static AudioFormat getSignOrEndianChangedFormat(AudioFormat paramAudioFormat) {
    boolean bool1 = paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
    boolean bool2 = paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
    return (paramAudioFormat.getSampleSizeInBits() > 8 && bool1) ? new AudioFormat(paramAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), !paramAudioFormat.isBigEndian()) : ((paramAudioFormat.getSampleSizeInBits() == 8 && (bool1 || bool2)) ? new AudioFormat(bool1 ? AudioFormat.Encoding.PCM_UNSIGNED : AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), paramAudioFormat.isBigEndian()) : null);
  }
  
  private static native void nGetFormats(int paramInt1, int paramInt2, boolean paramBoolean, Vector paramVector);
  
  private static native long nOpen(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, float paramFloat, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean2, boolean paramBoolean3, int paramInt7) throws LineUnavailableException;
  
  private static native void nStart(long paramLong, boolean paramBoolean);
  
  private static native void nStop(long paramLong, boolean paramBoolean);
  
  private static native void nClose(long paramLong, boolean paramBoolean);
  
  private static native int nWrite(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2);
  
  private static native int nRead(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int nGetBufferSize(long paramLong, boolean paramBoolean);
  
  private static native boolean nIsStillDraining(long paramLong, boolean paramBoolean);
  
  private static native void nFlush(long paramLong, boolean paramBoolean);
  
  private static native int nAvailable(long paramLong, boolean paramBoolean);
  
  private static native long nGetBytePosition(long paramLong1, boolean paramBoolean, long paramLong2);
  
  private static native void nSetBytePosition(long paramLong1, boolean paramBoolean, long paramLong2);
  
  private static native boolean nRequiresServicing(long paramLong, boolean paramBoolean);
  
  private static native void nService(long paramLong, boolean paramBoolean);
  
  private static class DirectBAOS extends ByteArrayOutputStream {
    public byte[] getInternalBuffer() { return this.buf; }
  }
  
  private static final class DirectClip extends DirectDL implements Clip, Runnable, AutoClosingClip {
    private boolean autoclosing = false;
    
    private DirectClip(DataLine.Info param1Info, AudioFormat param1AudioFormat, int param1Int, DirectAudioDevice param1DirectAudioDevice) { super(param1Info, param1DirectAudioDevice, param1AudioFormat, param1Int, param1DirectAudioDevice.getMixerIndex(), param1DirectAudioDevice.getDeviceID(), true); }
    
    public void open(AudioFormat param1AudioFormat, byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws LineUnavailableException {
      Toolkit.isFullySpecifiedAudioFormat(param1AudioFormat);
      byte[] arrayOfByte = new byte[param1Int2];
      System.arraycopy(param1ArrayOfByte, param1Int1, arrayOfByte, 0, param1Int2);
      open(param1AudioFormat, arrayOfByte, param1Int2 / param1AudioFormat.getFrameSize());
    }
    
    private void open(AudioFormat param1AudioFormat, byte[] param1ArrayOfByte, int param1Int) throws LineUnavailableException {
      Toolkit.isFullySpecifiedAudioFormat(param1AudioFormat);
      synchronized (this.mixer) {
        if (isOpen())
          throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength()); 
        this.audioData = param1ArrayOfByte;
        this.frameSize = param1AudioFormat.getFrameSize();
        this.m_lengthInFrames = param1Int;
        this.bytePosition = 0L;
        this.clipBytePosition = 0;
        this.newFramePosition = -1;
        this.loopStartFrame = 0;
        this.loopEndFrame = param1Int - 1;
        this.loopCount = 0;
        try {
          open(param1AudioFormat, (int)Toolkit.millis2bytes(param1AudioFormat, 1000L));
        } catch (LineUnavailableException lineUnavailableException) {
          this.audioData = null;
          throw lineUnavailableException;
        } catch (IllegalArgumentException illegalArgumentException) {
          this.audioData = null;
          throw illegalArgumentException;
        } 
        byte b = 6;
        this.thread = JSSecurityManager.createThread(this, "Direct Clip", true, b, false);
        this.thread.start();
      } 
      if (isAutoClosing())
        getEventDispatcher().autoClosingClipOpened(this); 
    }
    
    public void open(AudioInputStream param1AudioInputStream) throws LineUnavailableException, IOException {
      Toolkit.isFullySpecifiedAudioFormat(this.format);
      synchronized (this.mixer) {
        byte[] arrayOfByte = null;
        if (isOpen())
          throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength()); 
        int i = (int)param1AudioInputStream.getFrameLength();
        int j = 0;
        if (i != -1) {
          int k = i * param1AudioInputStream.getFormat().getFrameSize();
          arrayOfByte = new byte[k];
          int m = k;
          int n = 0;
          while (m > 0 && n) {
            n = param1AudioInputStream.read(arrayOfByte, j, m);
            if (n > 0) {
              j += n;
              m -= n;
              continue;
            } 
            if (n == 0)
              Thread.yield(); 
          } 
        } else {
          char c = '䀀';
          DirectAudioDevice.DirectBAOS directBAOS = new DirectAudioDevice.DirectBAOS();
          byte[] arrayOfByte1 = new byte[c];
          int k = 0;
          while (k) {
            k = param1AudioInputStream.read(arrayOfByte1, 0, arrayOfByte1.length);
            if (k > 0) {
              directBAOS.write(arrayOfByte1, 0, k);
              j += k;
              continue;
            } 
            if (k == 0)
              Thread.yield(); 
          } 
          arrayOfByte = directBAOS.getInternalBuffer();
        } 
        i = j / param1AudioInputStream.getFormat().getFrameSize();
        open(param1AudioInputStream.getFormat(), arrayOfByte, i);
      } 
    }
    
    public int getFrameLength() { return this.m_lengthInFrames; }
    
    public long getMicrosecondLength() { return Toolkit.frames2micros(getFormat(), getFrameLength()); }
    
    public void setFramePosition(int param1Int) {
      if (param1Int < 0) {
        param1Int = 0;
      } else if (param1Int >= getFrameLength()) {
        param1Int = getFrameLength();
      } 
      if (this.doIO) {
        this.newFramePosition = param1Int;
      } else {
        this.clipBytePosition = param1Int * this.frameSize;
        this.newFramePosition = -1;
      } 
      this.bytePosition = (param1Int * this.frameSize);
      flush();
      synchronized (this.lockNative) {
        DirectAudioDevice.nSetBytePosition(this.id, this.isSource, (param1Int * this.frameSize));
      } 
    }
    
    public long getLongFramePosition() { return super.getLongFramePosition(); }
    
    public void setMicrosecondPosition(long param1Long) {
      long l = Toolkit.micros2frames(getFormat(), param1Long);
      setFramePosition((int)l);
    }
    
    public void setLoopPoints(int param1Int1, int param1Int2) {
      if (param1Int1 < 0 || param1Int1 >= getFrameLength())
        throw new IllegalArgumentException("illegal value for start: " + param1Int1); 
      if (param1Int2 >= getFrameLength())
        throw new IllegalArgumentException("illegal value for end: " + param1Int2); 
      if (param1Int2 == -1) {
        param1Int2 = getFrameLength() - 1;
        if (param1Int2 < 0)
          param1Int2 = 0; 
      } 
      if (param1Int2 < param1Int1)
        throw new IllegalArgumentException("End position " + param1Int2 + "  preceeds start position " + param1Int1); 
      this.loopStartFrame = param1Int1;
      this.loopEndFrame = param1Int2;
    }
    
    public void loop(int param1Int) {
      this.loopCount = param1Int;
      start();
    }
    
    void implOpen(AudioFormat param1AudioFormat, int param1Int) throws LineUnavailableException {
      if (this.audioData == null)
        throw new IllegalArgumentException("illegal call to open() in interface Clip"); 
      super.implOpen(param1AudioFormat, param1Int);
    }
    
    void implClose() throws LineUnavailableException {
      Thread thread1 = this.thread;
      this.thread = null;
      this.doIO = false;
      if (thread1 != null) {
        synchronized (this.lock) {
          this.lock.notifyAll();
        } 
        try {
          thread1.join(2000L);
        } catch (InterruptedException interruptedException) {}
      } 
      super.implClose();
      this.audioData = null;
      this.newFramePosition = -1;
      getEventDispatcher().autoClosingClipClosed(this);
    }
    
    void implStart() throws LineUnavailableException { super.implStart(); }
    
    void implStop() throws LineUnavailableException {
      super.implStop();
      this.loopCount = 0;
    }
    
    public void run() throws LineUnavailableException {
      thread1 = Thread.currentThread();
      while (this.thread == thread1) {
        synchronized (this.lock) {
          if (!this.doIO)
            try {
              this.lock.wait();
            } catch (InterruptedException interruptedException) {
            
            } finally {
              if (this.thread != thread1)
                break; 
            }  
        } 
        while (this.doIO) {
          if (this.newFramePosition >= 0) {
            this.clipBytePosition = this.newFramePosition * this.frameSize;
            this.newFramePosition = -1;
          } 
          int i = getFrameLength() - 1;
          if (this.loopCount > 0 || this.loopCount == -1)
            i = this.loopEndFrame; 
          long l = (this.clipBytePosition / this.frameSize);
          int j = (int)(i - l + 1L);
          int k = j * this.frameSize;
          if (k > getBufferSize())
            k = Toolkit.align(getBufferSize(), this.frameSize); 
          int m = write(this.audioData, this.clipBytePosition, k);
          this.clipBytePosition += m;
          if (this.doIO && this.newFramePosition < 0 && m >= 0) {
            l = (this.clipBytePosition / this.frameSize);
            if (l > i) {
              if (this.loopCount > 0 || this.loopCount == -1) {
                if (this.loopCount != -1)
                  this.loopCount--; 
                this.newFramePosition = this.loopStartFrame;
                continue;
              } 
              drain();
              stop();
            } 
          } 
        } 
      } 
    }
    
    public boolean isAutoClosing() { return this.autoclosing; }
    
    public void setAutoClosing(boolean param1Boolean) {
      if (param1Boolean != this.autoclosing) {
        if (isOpen())
          if (param1Boolean) {
            getEventDispatcher().autoClosingClipOpened(this);
          } else {
            getEventDispatcher().autoClosingClipClosed(this);
          }  
        this.autoclosing = param1Boolean;
      } 
    }
    
    protected boolean requiresServicing() { return false; }
  }
  
  private static class DirectDL extends AbstractDataLine implements EventDispatcher.LineMonitor {
    protected final int mixerIndex;
    
    protected final int deviceID;
    
    protected long id;
    
    protected int waitTime;
    
    protected final boolean isSource;
    
    protected boolean monitoring = false;
    
    protected int softwareConversionSize = 0;
    
    protected AudioFormat hardwareFormat;
    
    private final Gain gainControl = new Gain(null);
    
    private final Mute muteControl = new Mute(null);
    
    private final Balance balanceControl = new Balance(null);
    
    private final Pan panControl = new Pan(null);
    
    private float leftGain;
    
    private float rightGain;
    
    protected final Object lockNative = new Object();
    
    protected DirectDL(DataLine.Info param1Info, DirectAudioDevice param1DirectAudioDevice, AudioFormat param1AudioFormat, int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      super(param1Info, param1DirectAudioDevice, null, param1AudioFormat, param1Int1);
      this.mixerIndex = param1Int2;
      this.deviceID = param1Int3;
      this.waitTime = 10;
      this.isSource = param1Boolean;
    }
    
    void implOpen(AudioFormat param1AudioFormat, int param1Int) throws LineUnavailableException {
      Toolkit.isFullySpecifiedAudioFormat(param1AudioFormat);
      if (!this.isSource)
        JSSecurityManager.checkRecordPermission(); 
      byte b = 0;
      if (param1AudioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
        b = 1;
      } else if (param1AudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
        b = 2;
      } 
      if (param1Int <= -1)
        param1Int = (int)Toolkit.millis2bytes(param1AudioFormat, 500L); 
      DirectAudioDevice.DirectDLI directDLI = null;
      if (this.info instanceof DirectAudioDevice.DirectDLI)
        directDLI = (DirectAudioDevice.DirectDLI)this.info; 
      if (this.isSource)
        if (!param1AudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !param1AudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
          this.controls = new javax.sound.sampled.Control[0];
        } else if (param1AudioFormat.getChannels() > 2 || param1AudioFormat.getSampleSizeInBits() > 16) {
          this.controls = new javax.sound.sampled.Control[0];
        } else {
          if (param1AudioFormat.getChannels() == 1) {
            this.controls = new javax.sound.sampled.Control[2];
          } else {
            this.controls = new javax.sound.sampled.Control[4];
            this.controls[2] = this.balanceControl;
            this.controls[3] = this.panControl;
          } 
          this.controls[0] = this.gainControl;
          this.controls[1] = this.muteControl;
        }  
      this.hardwareFormat = param1AudioFormat;
      this.softwareConversionSize = 0;
      if (directDLI != null && !directDLI.isFormatSupportedInHardware(param1AudioFormat)) {
        AudioFormat audioFormat = DirectAudioDevice.getSignOrEndianChangedFormat(param1AudioFormat);
        if (directDLI.isFormatSupportedInHardware(audioFormat)) {
          this.hardwareFormat = audioFormat;
          this.softwareConversionSize = param1AudioFormat.getFrameSize() / param1AudioFormat.getChannels();
        } 
      } 
      param1Int = param1Int / param1AudioFormat.getFrameSize() * param1AudioFormat.getFrameSize();
      this.id = DirectAudioDevice.nOpen(this.mixerIndex, this.deviceID, this.isSource, b, this.hardwareFormat.getSampleRate(), this.hardwareFormat.getSampleSizeInBits(), this.hardwareFormat.getFrameSize(), this.hardwareFormat.getChannels(), this.hardwareFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED), this.hardwareFormat.isBigEndian(), param1Int);
      if (this.id == 0L)
        throw new LineUnavailableException("line with format " + param1AudioFormat + " not supported."); 
      this.bufferSize = DirectAudioDevice.nGetBufferSize(this.id, this.isSource);
      if (this.bufferSize < 1)
        this.bufferSize = param1Int; 
      this.format = param1AudioFormat;
      this.waitTime = (int)Toolkit.bytes2millis(param1AudioFormat, this.bufferSize) / 4;
      if (this.waitTime < 10) {
        this.waitTime = 1;
      } else if (this.waitTime > 1000) {
        this.waitTime = 1000;
      } 
      this.bytePosition = 0L;
      this.stoppedWritten = false;
      this.doIO = false;
      calcVolume();
    }
    
    void implStart() throws LineUnavailableException {
      if (!this.isSource)
        JSSecurityManager.checkRecordPermission(); 
      synchronized (this.lockNative) {
        DirectAudioDevice.nStart(this.id, this.isSource);
      } 
      this.monitoring = requiresServicing();
      if (this.monitoring)
        getEventDispatcher().addLineMonitor(this); 
      this.doIO = true;
      if (this.isSource && this.stoppedWritten) {
        setStarted(true);
        setActive(true);
      } 
    }
    
    void implStop() throws LineUnavailableException {
      if (!this.isSource)
        JSSecurityManager.checkRecordPermission(); 
      if (this.monitoring) {
        getEventDispatcher().removeLineMonitor(this);
        this.monitoring = false;
      } 
      synchronized (this.lockNative) {
        DirectAudioDevice.nStop(this.id, this.isSource);
      } 
      synchronized (this.lock) {
        this.doIO = false;
        this.lock.notifyAll();
      } 
      setActive(false);
      setStarted(false);
      this.stoppedWritten = false;
    }
    
    void implClose() throws LineUnavailableException {
      if (!this.isSource)
        JSSecurityManager.checkRecordPermission(); 
      if (this.monitoring) {
        getEventDispatcher().removeLineMonitor(this);
        this.monitoring = false;
      } 
      this.doIO = false;
      long l = this.id;
      this.id = 0L;
      synchronized (this.lockNative) {
        DirectAudioDevice.nClose(l, this.isSource);
      } 
      this.bytePosition = 0L;
      this.softwareConversionSize = 0;
    }
    
    public int available() {
      int i;
      if (this.id == 0L)
        return 0; 
      synchronized (this.lockNative) {
        i = DirectAudioDevice.nAvailable(this.id, this.isSource);
      } 
      return i;
    }
    
    public void drain() throws LineUnavailableException {
      this.noService = true;
      byte b = 0;
      long l = getLongFramePosition();
      boolean bool = false;
      while (!this.drained) {
        synchronized (this.lockNative) {
          if (this.id == 0L || !this.doIO || !DirectAudioDevice.nIsStillDraining(this.id, this.isSource))
            break; 
        } 
        if (b % 5 == 4) {
          long l1 = getLongFramePosition();
          bool |= ((l1 != l) ? 1 : 0);
          if (b % 50 > 45) {
            if (!bool)
              break; 
            bool = false;
            l = l1;
          } 
        } 
        b++;
        synchronized (this.lock) {
          try {
            this.lock.wait(10L);
          } catch (InterruptedException interruptedException) {}
        } 
      } 
      if (this.doIO && this.id != 0L)
        this.drained = true; 
      this.noService = false;
    }
    
    public void flush() throws LineUnavailableException {
      if (this.id != 0L) {
        this.flushing = true;
        synchronized (this.lock) {
          this.lock.notifyAll();
        } 
        synchronized (this.lockNative) {
          if (this.id != 0L)
            DirectAudioDevice.nFlush(this.id, this.isSource); 
        } 
        this.drained = true;
      } 
    }
    
    public long getLongFramePosition() {
      long l;
      synchronized (this.lockNative) {
        l = DirectAudioDevice.nGetBytePosition(this.id, this.isSource, this.bytePosition);
      } 
      if (l < 0L)
        l = 0L; 
      return l / getFormat().getFrameSize();
    }
    
    public int write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      this.flushing = false;
      if (param1Int2 == 0)
        return 0; 
      if (param1Int2 < 0)
        throw new IllegalArgumentException("illegal len: " + param1Int2); 
      if (param1Int2 % getFormat().getFrameSize() != 0)
        throw new IllegalArgumentException("illegal request to write non-integral number of frames (" + param1Int2 + " bytes, frameSize = " + getFormat().getFrameSize() + " bytes)"); 
      if (param1Int1 < 0)
        throw new ArrayIndexOutOfBoundsException(param1Int1); 
      if (param1Int1 + param1Int2 > param1ArrayOfByte.length)
        throw new ArrayIndexOutOfBoundsException(param1ArrayOfByte.length); 
      if (!isActive() && this.doIO) {
        setActive(true);
        setStarted(true);
      } 
      int i = 0;
      while (!this.flushing) {
        int j;
        synchronized (this.lockNative) {
          j = DirectAudioDevice.nWrite(this.id, param1ArrayOfByte, param1Int1, param1Int2, this.softwareConversionSize, this.leftGain, this.rightGain);
          if (j < 0)
            break; 
          this.bytePosition += j;
          if (j > 0)
            this.drained = false; 
        } 
        param1Int2 -= j;
        i += j;
        if (this.doIO && param1Int2 > 0) {
          param1Int1 += j;
          synchronized (this.lock) {
            try {
              this.lock.wait(this.waitTime);
            } catch (InterruptedException interruptedException) {}
          } 
        } 
      } 
      if (i > 0 && !this.doIO)
        this.stoppedWritten = true; 
      return i;
    }
    
    protected boolean requiresServicing() { return DirectAudioDevice.nRequiresServicing(this.id, this.isSource); }
    
    public void checkLine() throws LineUnavailableException {
      synchronized (this.lockNative) {
        if (this.monitoring && this.doIO && this.id != 0L && !this.flushing && !this.noService)
          DirectAudioDevice.nService(this.id, this.isSource); 
      } 
    }
    
    private void calcVolume() throws LineUnavailableException {
      if (getFormat() == null)
        return; 
      if (this.muteControl.getValue()) {
        this.leftGain = 0.0F;
        this.rightGain = 0.0F;
        return;
      } 
      float f = this.gainControl.getLinearGain();
      if (getFormat().getChannels() == 1) {
        this.leftGain = f;
        this.rightGain = f;
      } else {
        float f1 = this.balanceControl.getValue();
        if (f1 < 0.0F) {
          this.leftGain = f;
          this.rightGain = f * (f1 + 1.0F);
        } else {
          this.leftGain = f * (1.0F - f1);
          this.rightGain = f;
        } 
      } 
    }
    
    private final class Balance extends FloatControl {
      private Balance() { super(FloatControl.Type.BALANCE, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right"); }
      
      public void setValue(float param2Float) {
        setValueImpl(param2Float);
        DirectAudioDevice.DirectDL.this.panControl.setValueImpl(param2Float);
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
      
      void setValueImpl(float param2Float) { super.setValue(param2Float); }
    }
    
    protected final class Gain extends FloatControl {
      private float linearGain = 1.0F;
      
      private Gain() { super(FloatControl.Type.MASTER_GAIN, Toolkit.linearToDB(0.0F), Toolkit.linearToDB(2.0F), Math.abs(Toolkit.linearToDB(1.0F) - Toolkit.linearToDB(0.0F)) / 128.0F, -1, 0.0F, "dB", "Minimum", "", "Maximum"); }
      
      public void setValue(float param2Float) {
        float f = Toolkit.dBToLinear(param2Float);
        super.setValue(Toolkit.linearToDB(f));
        this.linearGain = f;
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
      
      float getLinearGain() { return this.linearGain; }
    }
    
    private final class Mute extends BooleanControl {
      private Mute() { super(BooleanControl.Type.MUTE, false, "True", "False"); }
      
      public void setValue(boolean param2Boolean) {
        super.setValue(param2Boolean);
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
    }
    
    private final class Pan extends FloatControl {
      private Pan() { super(FloatControl.Type.PAN, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right"); }
      
      public void setValue(float param2Float) {
        setValueImpl(param2Float);
        DirectAudioDevice.DirectDL.this.balanceControl.setValueImpl(param2Float);
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
      
      void setValueImpl(float param2Float) { super.setValue(param2Float); }
    }
  }
  
  private static final class DirectDLI extends DataLine.Info {
    final AudioFormat[] hardwareFormats;
    
    private DirectDLI(Class param1Class, AudioFormat[] param1ArrayOfAudioFormat1, AudioFormat[] param1ArrayOfAudioFormat2, int param1Int1, int param1Int2) {
      super(param1Class, param1ArrayOfAudioFormat1, param1Int1, param1Int2);
      this.hardwareFormats = param1ArrayOfAudioFormat2;
    }
    
    public boolean isFormatSupportedInHardware(AudioFormat param1AudioFormat) {
      if (param1AudioFormat == null)
        return false; 
      for (byte b = 0; b < this.hardwareFormats.length; b++) {
        if (param1AudioFormat.matches(this.hardwareFormats[b]))
          return true; 
      } 
      return false;
    }
    
    private AudioFormat[] getHardwareFormats() { return this.hardwareFormats; }
  }
  
  private static final class DirectSDL extends DirectDL implements SourceDataLine {
    private DirectSDL(DataLine.Info param1Info, AudioFormat param1AudioFormat, int param1Int, DirectAudioDevice param1DirectAudioDevice) { super(param1Info, param1DirectAudioDevice, param1AudioFormat, param1Int, param1DirectAudioDevice.getMixerIndex(), param1DirectAudioDevice.getDeviceID(), true); }
  }
  
  private static final class DirectTDL extends DirectDL implements TargetDataLine {
    private DirectTDL(DataLine.Info param1Info, AudioFormat param1AudioFormat, int param1Int, DirectAudioDevice param1DirectAudioDevice) { super(param1Info, param1DirectAudioDevice, param1AudioFormat, param1Int, param1DirectAudioDevice.getMixerIndex(), param1DirectAudioDevice.getDeviceID(), false); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      this.flushing = false;
      if (param1Int2 == 0)
        return 0; 
      if (param1Int2 < 0)
        throw new IllegalArgumentException("illegal len: " + param1Int2); 
      if (param1Int2 % getFormat().getFrameSize() != 0)
        throw new IllegalArgumentException("illegal request to read non-integral number of frames (" + param1Int2 + " bytes, frameSize = " + getFormat().getFrameSize() + " bytes)"); 
      if (param1Int1 < 0)
        throw new ArrayIndexOutOfBoundsException(param1Int1); 
      if (param1Int1 + param1Int2 > param1ArrayOfByte.length)
        throw new ArrayIndexOutOfBoundsException(param1ArrayOfByte.length); 
      if (!isActive() && this.doIO) {
        setActive(true);
        setStarted(true);
      } 
      int i = 0;
      while (this.doIO && !this.flushing) {
        int j;
        synchronized (this.lockNative) {
          j = DirectAudioDevice.nRead(this.id, param1ArrayOfByte, param1Int1, param1Int2, this.softwareConversionSize);
          if (j < 0)
            break; 
          this.bytePosition += j;
          if (j > 0)
            this.drained = false; 
        } 
        param1Int2 -= j;
        i += j;
        if (param1Int2 > 0) {
          param1Int1 += j;
          synchronized (this.lock) {
            try {
              this.lock.wait(this.waitTime);
            } catch (InterruptedException interruptedException) {}
          } 
        } 
      } 
      if (this.flushing)
        i = 0; 
      return i;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DirectAudioDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */