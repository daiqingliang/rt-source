package com.sun.media.sound;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class AiffFileWriter extends SunFileWriter {
  private static final int DOUBLE_MANTISSA_LENGTH = 52;
  
  private static final int DOUBLE_EXPONENT_LENGTH = 11;
  
  private static final long DOUBLE_SIGN_MASK = -9223372036854775808L;
  
  private static final long DOUBLE_EXPONENT_MASK = 9218868437227405312L;
  
  private static final long DOUBLE_MANTISSA_MASK = 4503599627370495L;
  
  private static final int DOUBLE_EXPONENT_OFFSET = 1023;
  
  private static final int EXTENDED_EXPONENT_OFFSET = 16383;
  
  private static final int EXTENDED_MANTISSA_LENGTH = 63;
  
  private static final int EXTENDED_EXPONENT_LENGTH = 15;
  
  private static final long EXTENDED_INTEGER_MASK = -9223372036854775808L;
  
  public AiffFileWriter() { super(new AudioFileFormat.Type[] { AudioFileFormat.Type.AIFF }); }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream) {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
    System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    AudioFormat.Encoding encoding = audioFormat.getEncoding();
    return (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding) || AudioFormat.Encoding.PCM_SIGNED.equals(encoding) || AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding)) ? arrayOfType : new AudioFileFormat.Type[0];
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException {
    AiffFileFormat aiffFileFormat = (AiffFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFrameLength() == -1L)
      throw new IOException("stream length not specified"); 
    return writeAiffFile(paramAudioInputStream, aiffFileFormat, paramOutputStream);
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException {
    AiffFileFormat aiffFileFormat = (AiffFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 4096);
    int i = writeAiffFile(paramAudioInputStream, aiffFileFormat, bufferedOutputStream);
    bufferedOutputStream.close();
    if (aiffFileFormat.getByteLength() == -1) {
      int j = aiffFileFormat.getFormat().getChannels() * aiffFileFormat.getFormat().getSampleSizeInBits();
      int k = i;
      int m = k - aiffFileFormat.getHeaderSize() + 16;
      long l = (m - 16);
      int n = (int)(l * 8L / j);
      RandomAccessFile randomAccessFile = new RandomAccessFile(paramFile, "rw");
      randomAccessFile.skipBytes(4);
      randomAccessFile.writeInt(k - 8);
      randomAccessFile.skipBytes(4 + aiffFileFormat.getFverChunkSize() + 4 + 4 + 2);
      randomAccessFile.writeInt(n);
      randomAccessFile.skipBytes(16);
      randomAccessFile.writeInt(m - 8);
      randomAccessFile.close();
    } 
    return i;
  }
  
  private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream) {
    byte b;
    int i;
    AudioFormat audioFormat1 = null;
    null = null;
    AudioFormat.Encoding encoding1 = AudioFormat.Encoding.PCM_SIGNED;
    AudioFormat audioFormat2 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding encoding2 = audioFormat2.getEncoding();
    boolean bool = false;
    if (!this.types[0].equals(paramType))
      throw new IllegalArgumentException("File type " + paramType + " not supported."); 
    if (AudioFormat.Encoding.ALAW.equals(encoding2) || AudioFormat.Encoding.ULAW.equals(encoding2)) {
      if (audioFormat2.getSampleSizeInBits() == 8) {
        encoding1 = AudioFormat.Encoding.PCM_SIGNED;
        i = 16;
        bool = true;
      } else {
        throw new IllegalArgumentException("Encoding " + encoding2 + " supported only for 8-bit data.");
      } 
    } else if (audioFormat2.getSampleSizeInBits() == 8) {
      encoding1 = AudioFormat.Encoding.PCM_UNSIGNED;
      i = 8;
    } else {
      encoding1 = AudioFormat.Encoding.PCM_SIGNED;
      i = audioFormat2.getSampleSizeInBits();
    } 
    audioFormat1 = new AudioFormat(encoding1, audioFormat2.getSampleRate(), i, audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), true);
    if (paramAudioInputStream.getFrameLength() != -1L) {
      if (bool) {
        b = (int)paramAudioInputStream.getFrameLength() * audioFormat2.getFrameSize() * 2 + 54;
      } else {
        b = (int)paramAudioInputStream.getFrameLength() * audioFormat2.getFrameSize() + 54;
      } 
    } else {
      b = -1;
    } 
    return new AiffFileFormat(AudioFileFormat.Type.AIFF, b, audioFormat1, (int)paramAudioInputStream.getFrameLength());
  }
  
  private int writeAiffFile(InputStream paramInputStream, AiffFileFormat paramAiffFileFormat, OutputStream paramOutputStream) throws IOException {
    int i = 0;
    int j = 0;
    InputStream inputStream = getFileStream(paramAiffFileFormat, paramInputStream);
    byte[] arrayOfByte = new byte[4096];
    int k = paramAiffFileFormat.getByteLength();
    while ((i = inputStream.read(arrayOfByte)) >= 0) {
      if (k > 0) {
        if (i < k) {
          paramOutputStream.write(arrayOfByte, 0, i);
          j += i;
          k -= i;
          continue;
        } 
        paramOutputStream.write(arrayOfByte, 0, k);
        j += k;
        k = 0;
        break;
      } 
      paramOutputStream.write(arrayOfByte, 0, i);
      j += i;
    } 
    return j;
  }
  
  private InputStream getFileStream(AiffFileFormat paramAiffFileFormat, InputStream paramInputStream) throws IOException {
    AudioFormat audioFormat1 = paramAiffFileFormat.getFormat();
    AudioFormat audioFormat2 = null;
    AudioFormat.Encoding encoding = null;
    int i = paramAiffFileFormat.getHeaderSize();
    int j = paramAiffFileFormat.getFverChunkSize();
    int k = paramAiffFileFormat.getCommChunkSize();
    int m = -1;
    int n = -1;
    int i1 = paramAiffFileFormat.getSsndChunkOffset();
    short s1 = (short)audioFormat1.getChannels();
    short s2 = (short)audioFormat1.getSampleSizeInBits();
    short s3 = s1 * s2;
    int i2 = paramAiffFileFormat.getFrameLength();
    long l = -1L;
    if (i2 != -1) {
      l = i2 * s3 / 8L;
      n = (int)l + 16;
      m = (int)l + i;
    } 
    float f = audioFormat1.getSampleRate();
    int i3 = 1313820229;
    byte[] arrayOfByte = null;
    ByteArrayInputStream byteArrayInputStream = null;
    ByteArrayOutputStream byteArrayOutputStream = null;
    DataOutputStream dataOutputStream = null;
    null = null;
    InputStream inputStream = paramInputStream;
    if (paramInputStream instanceof AudioInputStream) {
      audioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
      encoding = audioFormat2.getEncoding();
      if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) || (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && !audioFormat2.isBigEndian())) {
        inputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat2.getSampleRate(), audioFormat2.getSampleSizeInBits(), audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), true), (AudioInputStream)paramInputStream);
      } else if (AudioFormat.Encoding.ULAW.equals(encoding) || AudioFormat.Encoding.ALAW.equals(encoding)) {
        if (audioFormat2.getSampleSizeInBits() != 8)
          throw new IllegalArgumentException("unsupported encoding"); 
        inputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat2.getSampleRate(), audioFormat2.getSampleSizeInBits() * 2, audioFormat2.getChannels(), audioFormat2.getFrameSize() * 2, audioFormat2.getFrameRate(), true), (AudioInputStream)paramInputStream);
      } 
    } 
    byteArrayOutputStream = new ByteArrayOutputStream();
    dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    dataOutputStream.writeInt(1179603533);
    dataOutputStream.writeInt(m - 8);
    dataOutputStream.writeInt(1095321158);
    dataOutputStream.writeInt(1129270605);
    dataOutputStream.writeInt(k - 8);
    dataOutputStream.writeShort(s1);
    dataOutputStream.writeInt(i2);
    dataOutputStream.writeShort(s2);
    write_ieee_extended(dataOutputStream, f);
    dataOutputStream.writeInt(1397968452);
    dataOutputStream.writeInt(n - 8);
    dataOutputStream.writeInt(0);
    dataOutputStream.writeInt(0);
    dataOutputStream.close();
    arrayOfByte = byteArrayOutputStream.toByteArray();
    byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    return new SequenceInputStream(byteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, inputStream));
  }
  
  private void write_ieee_extended(DataOutputStream paramDataOutputStream, float paramFloat) throws IOException {
    long l1 = Double.doubleToLongBits(paramFloat);
    long l2 = (l1 & Float.MIN_VALUE) >> 63;
    long l3 = (l1 & 0x7FF0000000000000L) >> 52;
    long l4 = l1 & 0xFFFFFFFFFFFFFL;
    long l5 = l3 - 1023L + 16383L;
    long l6 = l4 << 11;
    long l7 = l2 << 15;
    short s = (short)(int)(l7 | l5);
    long l8 = Float.MIN_VALUE | l6;
    paramDataOutputStream.writeShort(s);
    paramDataOutputStream.writeLong(l8);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AiffFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */