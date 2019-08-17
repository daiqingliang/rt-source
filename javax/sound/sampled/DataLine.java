package javax.sound.sampled;

import java.util.Arrays;

public interface DataLine extends Line {
  void drain();
  
  void flush();
  
  void start();
  
  void stop();
  
  boolean isRunning();
  
  boolean isActive();
  
  AudioFormat getFormat();
  
  int getBufferSize();
  
  int available();
  
  int getFramePosition();
  
  long getLongFramePosition();
  
  long getMicrosecondPosition();
  
  float getLevel();
  
  public static class Info extends Line.Info {
    private final AudioFormat[] formats;
    
    private final int minBufferSize;
    
    private final int maxBufferSize;
    
    public Info(Class<?> param1Class, AudioFormat[] param1ArrayOfAudioFormat, int param1Int1, int param1Int2) {
      super(param1Class);
      if (param1ArrayOfAudioFormat == null) {
        this.formats = new AudioFormat[0];
      } else {
        this.formats = (AudioFormat[])Arrays.copyOf(param1ArrayOfAudioFormat, param1ArrayOfAudioFormat.length);
      } 
      this.minBufferSize = param1Int1;
      this.maxBufferSize = param1Int2;
    }
    
    public Info(Class<?> param1Class, AudioFormat param1AudioFormat, int param1Int) {
      super(param1Class);
      if (param1AudioFormat == null) {
        this.formats = new AudioFormat[0];
      } else {
        this.formats = new AudioFormat[] { param1AudioFormat };
      } 
      this.minBufferSize = param1Int;
      this.maxBufferSize = param1Int;
    }
    
    public Info(Class<?> param1Class, AudioFormat param1AudioFormat) { this(param1Class, param1AudioFormat, -1); }
    
    public AudioFormat[] getFormats() { return (AudioFormat[])Arrays.copyOf(this.formats, this.formats.length); }
    
    public boolean isFormatSupported(AudioFormat param1AudioFormat) {
      for (byte b = 0; b < this.formats.length; b++) {
        if (param1AudioFormat.matches(this.formats[b]))
          return true; 
      } 
      return false;
    }
    
    public int getMinBufferSize() { return this.minBufferSize; }
    
    public int getMaxBufferSize() { return this.maxBufferSize; }
    
    public boolean matches(Line.Info param1Info) {
      if (!super.matches(param1Info))
        return false; 
      Info info = (Info)param1Info;
      if (getMaxBufferSize() >= 0 && info.getMaxBufferSize() >= 0 && getMaxBufferSize() > info.getMaxBufferSize())
        return false; 
      if (getMinBufferSize() >= 0 && info.getMinBufferSize() >= 0 && getMinBufferSize() < info.getMinBufferSize())
        return false; 
      AudioFormat[] arrayOfAudioFormat = getFormats();
      if (arrayOfAudioFormat != null)
        for (byte b = 0; b < arrayOfAudioFormat.length; b++) {
          if (arrayOfAudioFormat[b] != null && !info.isFormatSupported(arrayOfAudioFormat[b]))
            return false; 
        }  
      return true;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.formats.length == 1 && this.formats[false] != null) {
        stringBuffer.append(" supporting format " + this.formats[0]);
      } else if (getFormats().length > 1) {
        stringBuffer.append(" supporting " + getFormats().length + " audio formats");
      } 
      if (this.minBufferSize != -1 && this.maxBufferSize != -1) {
        stringBuffer.append(", and buffers of " + this.minBufferSize + " to " + this.maxBufferSize + " bytes");
      } else if (this.minBufferSize != -1 && this.minBufferSize > 0) {
        stringBuffer.append(", and buffers of at least " + this.minBufferSize + " bytes");
      } else if (this.maxBufferSize != -1) {
        stringBuffer.append(", and buffers of up to " + this.minBufferSize + " bytes");
      } 
      return new String(super.toString() + stringBuffer);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\DataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */