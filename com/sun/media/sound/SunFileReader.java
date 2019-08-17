package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

abstract class SunFileReader extends AudioFileReader {
  protected static final int bisBufferSize = 4096;
  
  public abstract AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException;
  
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
  
  static final int calculatePCMFrameSize(int paramInt1, int paramInt2) { return (paramInt1 + 7) / 8 * paramInt2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SunFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */