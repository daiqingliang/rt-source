package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AiffFileReader extends SunFileReader {
  private static final int MAX_READ_LENGTH = 8;
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = getCOMM(paramInputStream, true);
    paramInputStream.reset();
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = null;
    inputStream = paramURL.openStream();
    try {
      audioFileFormat = getCOMM(inputStream, false);
    } finally {
      inputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = null;
    fileInputStream = new FileInputStream(paramFile);
    try {
      audioFileFormat = getCOMM(fileInputStream, false);
    } finally {
      fileInputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = getCOMM(paramInputStream, true);
    return new AudioInputStream(paramInputStream, audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException {
    inputStream = paramURL.openStream();
    audioFileFormat = null;
    try {
      audioFileFormat = getCOMM(inputStream, false);
    } finally {
      if (audioFileFormat == null)
        inputStream.close(); 
    } 
    return new AudioInputStream(inputStream, audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException {
    fileInputStream = new FileInputStream(paramFile);
    audioFileFormat = null;
    try {
      audioFileFormat = getCOMM(fileInputStream, false);
    } finally {
      if (audioFileFormat == null)
        fileInputStream.close(); 
    } 
    return new AudioInputStream(fileInputStream, audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
  }
  
  private AudioFileFormat getCOMM(InputStream paramInputStream, boolean paramBoolean) throws UnsupportedAudioFileException, IOException {
    int i1;
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    if (paramBoolean)
      dataInputStream.mark(8); 
    int i = 0;
    int j = 0;
    AudioFormat audioFormat = null;
    int k = dataInputStream.readInt();
    if (k != 1179603533) {
      if (paramBoolean)
        dataInputStream.reset(); 
      throw new UnsupportedAudioFileException("not an AIFF file");
    } 
    int m = dataInputStream.readInt();
    int n = dataInputStream.readInt();
    i += true;
    if (m <= 0) {
      m = -1;
      i1 = -1;
    } else {
      i1 = m + 8;
    } 
    boolean bool1 = false;
    if (n == 1095321155)
      bool1 = true; 
    boolean bool2 = false;
    while (!bool2) {
      int i9;
      int i8;
      int i7;
      AudioFormat.Encoding encoding;
      float f;
      int i6;
      int i5;
      int i2 = dataInputStream.readInt();
      int i3 = dataInputStream.readInt();
      i += true;
      int i4 = 0;
      switch (i2) {
        case 1129270605:
          if ((!bool1 && i3 < 18) || (bool1 && i3 < 22))
            throw new UnsupportedAudioFileException("Invalid AIFF/COMM chunksize"); 
          i5 = dataInputStream.readUnsignedShort();
          if (i5 <= 0)
            throw new UnsupportedAudioFileException("Invalid number of channels"); 
          dataInputStream.readInt();
          i6 = dataInputStream.readUnsignedShort();
          if (i6 < 1 || i6 > 32)
            throw new UnsupportedAudioFileException("Invalid AIFF/COMM sampleSize"); 
          f = (float)read_ieee_extended(dataInputStream);
          i4 += true;
          encoding = AudioFormat.Encoding.PCM_SIGNED;
          if (bool1) {
            int i10 = dataInputStream.readInt();
            i4 += true;
            switch (i10) {
              case 1313820229:
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                break;
              case 1970037111:
                encoding = AudioFormat.Encoding.ULAW;
                i6 = 8;
                break;
              default:
                throw new UnsupportedAudioFileException("Invalid AIFF encoding");
            } 
          } 
          i7 = calculatePCMFrameSize(i6, i5);
          audioFormat = new AudioFormat(encoding, f, i6, i5, i7, f, true);
          break;
        case 1397968452:
          i8 = dataInputStream.readInt();
          i9 = dataInputStream.readInt();
          i4 += true;
          if (i3 < m) {
            j = i3 - i4;
          } else {
            j = m - i + i4;
          } 
          bool2 = true;
          break;
      } 
      i += i4;
      if (!bool2) {
        i5 = i3 - i4;
        if (i5 > 0)
          i += dataInputStream.skipBytes(i5); 
      } 
    } 
    if (audioFormat == null)
      throw new UnsupportedAudioFileException("missing COMM chunk"); 
    AudioFileFormat.Type type = bool1 ? AudioFileFormat.Type.AIFC : AudioFileFormat.Type.AIFF;
    return new AiffFileFormat(type, i1, audioFormat, j / audioFormat.getFrameSize());
  }
  
  private void write_ieee_extended(DataOutputStream paramDataOutputStream, double paramDouble) throws IOException {
    char c = '䀎';
    double d = paramDouble;
    while (d < 44000.0D) {
      d *= 2.0D;
      c--;
    } 
    paramDataOutputStream.writeShort(c);
    paramDataOutputStream.writeInt((int)d << 16);
    paramDataOutputStream.writeInt(0);
  }
  
  private double read_ieee_extended(DataInputStream paramDataInputStream) throws IOException {
    double d1 = 0.0D;
    int i = 0;
    long l1 = 0L;
    long l2 = 0L;
    double d2 = 3.4028234663852886E38D;
    i = paramDataInputStream.readUnsignedShort();
    long l3 = paramDataInputStream.readUnsignedShort();
    long l4 = paramDataInputStream.readUnsignedShort();
    l1 = l3 << 16 | l4;
    l3 = paramDataInputStream.readUnsignedShort();
    l4 = paramDataInputStream.readUnsignedShort();
    l2 = l3 << 16 | l4;
    if (i == 0 && l1 == 0L && l2 == 0L) {
      d1 = 0.0D;
    } else if (i == 32767) {
      d1 = d2;
    } else {
      i -= 16383;
      i -= 31;
      d1 = l1 * Math.pow(2.0D, i);
      i -= 32;
      d1 += l2 * Math.pow(2.0D, i);
    } 
    return d1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AiffFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */