package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;

abstract class SunFileWriter extends AudioFileWriter {
  protected static final int bufferSize = 16384;
  
  protected static final int bisBufferSize = 4096;
  
  final AudioFileFormat.Type[] types;
  
  SunFileWriter(AudioFileFormat.Type[] paramArrayOfType) { this.types = paramArrayOfType; }
  
  public final AudioFileFormat.Type[] getAudioFileTypes() {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
    System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
    return arrayOfType;
  }
  
  public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream);
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException;
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException;
  
  final int rllong(DataInputStream paramDataInputStream) throws IOException {
    null = 0;
    null = paramDataInputStream.readInt();
    int i = (null & 0xFF) << 24;
    int j = (null & 0xFF00) << 8;
    int k = (null & 0xFF0000) >> 8;
    int m = (null & 0xFF000000) >>> 24;
    return i | j | k | m;
  }
  
  final int big2little(int paramInt) {
    int i = (paramInt & 0xFF) << 24;
    int j = (paramInt & 0xFF00) << 8;
    int k = (paramInt & 0xFF0000) >> 8;
    int m = (paramInt & 0xFF000000) >>> 24;
    return i | j | k | m;
  }
  
  final short rlshort(DataInputStream paramDataInputStream) throws IOException {
    null = 0;
    null = paramDataInputStream.readShort();
    short s1 = (short)((null & 0xFF) << 8);
    short s2 = (short)((null & 0xFF00) >>> 8);
    return (short)(s1 | s2);
  }
  
  final short big2littleShort(short paramShort) {
    short s1 = (short)((paramShort & 0xFF) << 8);
    short s2 = (short)((paramShort & 0xFF00) >>> 8);
    return (short)(s1 | s2);
  }
  
  final class NoCloseInputStream extends InputStream {
    private final InputStream in;
    
    NoCloseInputStream(InputStream param1InputStream) { this.in = param1InputStream; }
    
    public int read() throws IOException { return this.in.read(); }
    
    public int read(byte[] param1ArrayOfByte) throws IOException { return this.in.read(param1ArrayOfByte); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { return this.in.read(param1ArrayOfByte, param1Int1, param1Int2); }
    
    public long skip(long param1Long) throws IOException { return this.in.skip(param1Long); }
    
    public int available() throws IOException { return this.in.available(); }
    
    public void close() throws IOException {}
    
    public void mark(int param1Int) { this.in.mark(param1Int); }
    
    public void reset() throws IOException { this.in.reset(); }
    
    public boolean markSupported() { return this.in.markSupported(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SunFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */