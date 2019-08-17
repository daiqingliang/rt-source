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

public final class AuFileWriter extends SunFileWriter {
  public static final int UNKNOWN_SIZE = -1;
  
  public AuFileWriter() { super(new AudioFileFormat.Type[] { AudioFileFormat.Type.AU }); }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream) {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
    System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    AudioFormat.Encoding encoding = audioFormat.getEncoding();
    return (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding) || AudioFormat.Encoding.PCM_SIGNED.equals(encoding) || AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding)) ? arrayOfType : new AudioFileFormat.Type[0];
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException {
    AuFileFormat auFileFormat = (AuFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    return writeAuFile(paramAudioInputStream, auFileFormat, paramOutputStream);
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException {
    AuFileFormat auFileFormat = (AuFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 4096);
    int i = writeAuFile(paramAudioInputStream, auFileFormat, bufferedOutputStream);
    bufferedOutputStream.close();
    if (auFileFormat.getByteLength() == -1) {
      RandomAccessFile randomAccessFile = new RandomAccessFile(paramFile, "rw");
      if (randomAccessFile.length() <= 2147483647L) {
        randomAccessFile.skipBytes(8);
        randomAccessFile.writeInt(i - 24);
      } 
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
    if (!this.types[0].equals(paramType))
      throw new IllegalArgumentException("File type " + paramType + " not supported."); 
    if (AudioFormat.Encoding.ALAW.equals(encoding2) || AudioFormat.Encoding.ULAW.equals(encoding2)) {
      encoding1 = encoding2;
      i = audioFormat2.getSampleSizeInBits();
    } else if (audioFormat2.getSampleSizeInBits() == 8) {
      encoding1 = AudioFormat.Encoding.PCM_SIGNED;
      i = 8;
    } else {
      encoding1 = AudioFormat.Encoding.PCM_SIGNED;
      i = audioFormat2.getSampleSizeInBits();
    } 
    audioFormat1 = new AudioFormat(encoding1, audioFormat2.getSampleRate(), i, audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), true);
    if (paramAudioInputStream.getFrameLength() != -1L) {
      b = (int)paramAudioInputStream.getFrameLength() * audioFormat2.getFrameSize() + 24;
    } else {
      b = -1;
    } 
    return new AuFileFormat(AudioFileFormat.Type.AU, b, audioFormat1, (int)paramAudioInputStream.getFrameLength());
  }
  
  private InputStream getFileStream(AuFileFormat paramAuFileFormat, InputStream paramInputStream) throws IOException {
    AudioFormat audioFormat1 = paramAuFileFormat.getFormat();
    int i = 779316836;
    byte b = 24;
    long l1 = paramAuFileFormat.getFrameLength();
    long l2 = (l1 == -1L) ? -1L : (l1 * audioFormat1.getFrameSize());
    if (l2 > 2147483647L)
      l2 = -1L; 
    int j = paramAuFileFormat.getAuType();
    int k = (int)audioFormat1.getSampleRate();
    int m = audioFormat1.getChannels();
    boolean bool = true;
    byte[] arrayOfByte = null;
    ByteArrayInputStream byteArrayInputStream = null;
    ByteArrayOutputStream byteArrayOutputStream = null;
    DataOutputStream dataOutputStream = null;
    null = null;
    AudioFormat audioFormat2 = null;
    AudioFormat.Encoding encoding = null;
    InputStream inputStream = paramInputStream;
    inputStream = paramInputStream;
    if (paramInputStream instanceof AudioInputStream) {
      audioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
      encoding = audioFormat2.getEncoding();
      if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) || (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && bool != audioFormat2.isBigEndian()))
        inputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat2.getSampleRate(), audioFormat2.getSampleSizeInBits(), audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), bool), (AudioInputStream)paramInputStream); 
    } 
    byteArrayOutputStream = new ByteArrayOutputStream();
    dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    if (bool) {
      dataOutputStream.writeInt(779316836);
      dataOutputStream.writeInt(b);
      dataOutputStream.writeInt((int)l2);
      dataOutputStream.writeInt(j);
      dataOutputStream.writeInt(k);
      dataOutputStream.writeInt(m);
    } else {
      dataOutputStream.writeInt(1684960046);
      dataOutputStream.writeInt(big2little(b));
      dataOutputStream.writeInt(big2little((int)l2));
      dataOutputStream.writeInt(big2little(j));
      dataOutputStream.writeInt(big2little(k));
      dataOutputStream.writeInt(big2little(m));
    } 
    dataOutputStream.close();
    arrayOfByte = byteArrayOutputStream.toByteArray();
    byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    return new SequenceInputStream(byteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, inputStream));
  }
  
  private int writeAuFile(InputStream paramInputStream, AuFileFormat paramAuFileFormat, OutputStream paramOutputStream) throws IOException {
    int i = 0;
    int j = 0;
    InputStream inputStream = getFileStream(paramAuFileFormat, paramInputStream);
    byte[] arrayOfByte = new byte[4096];
    int k = paramAuFileFormat.getByteLength();
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AuFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */