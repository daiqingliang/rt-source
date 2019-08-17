package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class AudioFloatInputStream {
  public static AudioFloatInputStream getInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException { return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(paramURL)); }
  
  public static AudioFloatInputStream getInputStream(File paramFile) throws UnsupportedAudioFileException, IOException { return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(paramFile)); }
  
  public static AudioFloatInputStream getInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException { return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(paramInputStream)); }
  
  public static AudioFloatInputStream getInputStream(AudioInputStream paramAudioInputStream) { return new DirectAudioFloatInputStream(paramAudioInputStream); }
  
  public static AudioFloatInputStream getInputStream(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    AudioFloatConverter audioFloatConverter = AudioFloatConverter.getConverter(paramAudioFormat);
    if (audioFloatConverter != null)
      return new BytaArrayAudioFloatInputStream(audioFloatConverter, paramArrayOfByte, paramInt1, paramInt2); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2);
    long l = (paramAudioFormat.getFrameSize() == -1) ? -1L : (paramInt2 / paramAudioFormat.getFrameSize());
    AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, paramAudioFormat, l);
    return getInputStream(audioInputStream);
  }
  
  public abstract AudioFormat getFormat();
  
  public abstract long getFrameLength();
  
  public abstract int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException;
  
  public final int read(float[] paramArrayOfFloat) throws IOException { return read(paramArrayOfFloat, 0, paramArrayOfFloat.length); }
  
  public final float read() throws IOException {
    float[] arrayOfFloat = new float[1];
    int i = read(arrayOfFloat, 0, 1);
    return (i == -1 || i == 0) ? 0.0F : arrayOfFloat[0];
  }
  
  public abstract long skip(long paramLong) throws IOException;
  
  public abstract int available() throws IOException;
  
  public abstract void close();
  
  public abstract void mark(int paramInt);
  
  public abstract boolean markSupported();
  
  public abstract void reset();
  
  private static class BytaArrayAudioFloatInputStream extends AudioFloatInputStream {
    private int pos = 0;
    
    private int markpos = 0;
    
    private final AudioFloatConverter converter;
    
    private final AudioFormat format;
    
    private final byte[] buffer;
    
    private final int buffer_offset;
    
    private final int buffer_len;
    
    private final int framesize_pc;
    
    BytaArrayAudioFloatInputStream(AudioFloatConverter param1AudioFloatConverter, byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      this.converter = param1AudioFloatConverter;
      this.format = param1AudioFloatConverter.getFormat();
      this.buffer = param1ArrayOfByte;
      this.buffer_offset = param1Int1;
      this.framesize_pc = this.format.getFrameSize() / this.format.getChannels();
      this.buffer_len = param1Int2 / this.framesize_pc;
    }
    
    public AudioFormat getFormat() { return this.format; }
    
    public long getFrameLength() { return this.buffer_len; }
    
    public int read(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      if (param1ArrayOfFloat == null)
        throw new NullPointerException(); 
      if (param1Int1 < 0 || param1Int2 < 0 || param1Int2 > param1ArrayOfFloat.length - param1Int1)
        throw new IndexOutOfBoundsException(); 
      if (this.pos >= this.buffer_len)
        return -1; 
      if (param1Int2 == 0)
        return 0; 
      if (this.pos + param1Int2 > this.buffer_len)
        param1Int2 = this.buffer_len - this.pos; 
      this.converter.toFloatArray(this.buffer, this.buffer_offset + this.pos * this.framesize_pc, param1ArrayOfFloat, param1Int1, param1Int2);
      this.pos += param1Int2;
      return param1Int2;
    }
    
    public long skip(long param1Long) throws IOException {
      if (this.pos >= this.buffer_len)
        return -1L; 
      if (param1Long <= 0L)
        return 0L; 
      if (this.pos + param1Long > this.buffer_len)
        param1Long = (this.buffer_len - this.pos); 
      this.pos = (int)(this.pos + param1Long);
      return param1Long;
    }
    
    public int available() throws IOException { return this.buffer_len - this.pos; }
    
    public void close() {}
    
    public void mark(int param1Int) { this.markpos = this.pos; }
    
    public boolean markSupported() { return true; }
    
    public void reset() { this.pos = this.markpos; }
  }
  
  private static class DirectAudioFloatInputStream extends AudioFloatInputStream {
    private final AudioInputStream stream;
    
    private AudioFloatConverter converter;
    
    private final int framesize_pc;
    
    private byte[] buffer;
    
    DirectAudioFloatInputStream(AudioInputStream param1AudioInputStream) {
      this.converter = AudioFloatConverter.getConverter(param1AudioInputStream.getFormat());
      if (this.converter == null) {
        AudioFormat audioFormat2;
        AudioFormat audioFormat1 = param1AudioInputStream.getFormat();
        AudioFormat[] arrayOfAudioFormat = AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, audioFormat1);
        if (arrayOfAudioFormat.length != 0) {
          audioFormat2 = arrayOfAudioFormat[0];
        } else {
          float f1 = audioFormat1.getSampleRate();
          int i = audioFormat1.getSampleSizeInBits();
          int j = audioFormat1.getFrameSize();
          float f2 = audioFormat1.getFrameRate();
          i = 16;
          j = audioFormat1.getChannels() * i / 8;
          f2 = f1;
          audioFormat2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, f1, i, audioFormat1.getChannels(), j, f2, false);
        } 
        param1AudioInputStream = AudioSystem.getAudioInputStream(audioFormat2, param1AudioInputStream);
        this.converter = AudioFloatConverter.getConverter(param1AudioInputStream.getFormat());
      } 
      this.framesize_pc = param1AudioInputStream.getFormat().getFrameSize() / param1AudioInputStream.getFormat().getChannels();
      this.stream = param1AudioInputStream;
    }
    
    public AudioFormat getFormat() { return this.stream.getFormat(); }
    
    public long getFrameLength() { return this.stream.getFrameLength(); }
    
    public int read(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int2 * this.framesize_pc;
      if (this.buffer == null || this.buffer.length < i)
        this.buffer = new byte[i]; 
      int j = this.stream.read(this.buffer, 0, i);
      if (j == -1)
        return -1; 
      this.converter.toFloatArray(this.buffer, param1ArrayOfFloat, param1Int1, j / this.framesize_pc);
      return j / this.framesize_pc;
    }
    
    public long skip(long param1Long) throws IOException {
      long l1 = param1Long * this.framesize_pc;
      long l2 = this.stream.skip(l1);
      return (l2 == -1L) ? -1L : (l2 / this.framesize_pc);
    }
    
    public int available() throws IOException { return this.stream.available() / this.framesize_pc; }
    
    public void close() { this.stream.close(); }
    
    public void mark(int param1Int) { this.stream.mark(param1Int * this.framesize_pc); }
    
    public boolean markSupported() { return this.stream.markSupported(); }
    
    public void reset() { this.stream.reset(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AudioFloatInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */