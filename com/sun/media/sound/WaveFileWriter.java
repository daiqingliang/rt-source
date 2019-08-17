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

public final class WaveFileWriter extends SunFileWriter {
  static final int RIFF_MAGIC = 1380533830;
  
  static final int WAVE_MAGIC = 1463899717;
  
  static final int FMT_MAGIC = 1718449184;
  
  static final int DATA_MAGIC = 1684108385;
  
  static final int WAVE_FORMAT_UNKNOWN = 0;
  
  static final int WAVE_FORMAT_PCM = 1;
  
  static final int WAVE_FORMAT_ADPCM = 2;
  
  static final int WAVE_FORMAT_ALAW = 6;
  
  static final int WAVE_FORMAT_MULAW = 7;
  
  static final int WAVE_FORMAT_OKI_ADPCM = 16;
  
  static final int WAVE_FORMAT_DIGISTD = 21;
  
  static final int WAVE_FORMAT_DIGIFIX = 22;
  
  static final int WAVE_IBM_FORMAT_MULAW = 257;
  
  static final int WAVE_IBM_FORMAT_ALAW = 258;
  
  static final int WAVE_IBM_FORMAT_ADPCM = 259;
  
  static final int WAVE_FORMAT_DVI_ADPCM = 17;
  
  static final int WAVE_FORMAT_SX7383 = 7175;
  
  public WaveFileWriter() { super(new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE }); }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream) {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
    System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    AudioFormat.Encoding encoding = audioFormat.getEncoding();
    return (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding) || AudioFormat.Encoding.PCM_SIGNED.equals(encoding) || AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding)) ? arrayOfType : new AudioFileFormat.Type[0];
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException {
    WaveFileFormat waveFileFormat = (WaveFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFrameLength() == -1L)
      throw new IOException("stream length not specified"); 
    return writeWaveFile(paramAudioInputStream, waveFileFormat, paramOutputStream);
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException {
    WaveFileFormat waveFileFormat = (WaveFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 4096);
    int i = writeWaveFile(paramAudioInputStream, waveFileFormat, bufferedOutputStream);
    bufferedOutputStream.close();
    if (waveFileFormat.getByteLength() == -1) {
      int j = i - waveFileFormat.getHeaderSize();
      int k = j + waveFileFormat.getHeaderSize() - 8;
      RandomAccessFile randomAccessFile = new RandomAccessFile(paramFile, "rw");
      randomAccessFile.skipBytes(4);
      randomAccessFile.writeInt(big2little(k));
      randomAccessFile.skipBytes(12 + WaveFileFormat.getFmtChunkSize(waveFileFormat.getWaveType()) + 4);
      randomAccessFile.writeInt(big2little(j));
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
    byte b1 = 1;
    if (AudioFormat.Encoding.ALAW.equals(encoding2) || AudioFormat.Encoding.ULAW.equals(encoding2)) {
      encoding1 = encoding2;
      i = audioFormat2.getSampleSizeInBits();
      if (encoding2.equals(AudioFormat.Encoding.ALAW)) {
        b1 = 6;
      } else {
        b1 = 7;
      } 
    } else if (audioFormat2.getSampleSizeInBits() == 8) {
      encoding1 = AudioFormat.Encoding.PCM_UNSIGNED;
      i = 8;
    } else {
      encoding1 = AudioFormat.Encoding.PCM_SIGNED;
      i = audioFormat2.getSampleSizeInBits();
    } 
    audioFormat1 = new AudioFormat(encoding1, audioFormat2.getSampleRate(), i, audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), false);
    if (paramAudioInputStream.getFrameLength() != -1L) {
      b = (int)paramAudioInputStream.getFrameLength() * audioFormat2.getFrameSize() + WaveFileFormat.getHeaderSize(b1);
    } else {
      b = -1;
    } 
    return new WaveFileFormat(AudioFileFormat.Type.WAVE, b, audioFormat1, (int)paramAudioInputStream.getFrameLength());
  }
  
  private int writeWaveFile(InputStream paramInputStream, WaveFileFormat paramWaveFileFormat, OutputStream paramOutputStream) throws IOException {
    int i = 0;
    int j = 0;
    InputStream inputStream = getFileStream(paramWaveFileFormat, paramInputStream);
    byte[] arrayOfByte = new byte[4096];
    int k = paramWaveFileFormat.getByteLength();
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
  
  private InputStream getFileStream(WaveFileFormat paramWaveFileFormat, InputStream paramInputStream) throws IOException {
    AudioFormat audioFormat1 = paramWaveFileFormat.getFormat();
    int i = paramWaveFileFormat.getHeaderSize();
    int j = 1380533830;
    int k = 1463899717;
    int m = 1718449184;
    int n = WaveFileFormat.getFmtChunkSize(paramWaveFileFormat.getWaveType());
    short s1 = (short)paramWaveFileFormat.getWaveType();
    short s2 = (short)audioFormat1.getChannels();
    short s3 = (short)audioFormat1.getSampleSizeInBits();
    int i1 = (int)audioFormat1.getSampleRate();
    int i2 = audioFormat1.getFrameSize();
    int i3 = (int)audioFormat1.getFrameRate();
    short s4 = s2 * s3 * i1 / 8;
    short s5 = (short)(s3 / 8 * s2);
    int i4 = 1684108385;
    int i5 = paramWaveFileFormat.getFrameLength() * i2;
    int i6 = paramWaveFileFormat.getByteLength();
    int i7 = i5 + i - 8;
    byte[] arrayOfByte = null;
    ByteArrayInputStream byteArrayInputStream = null;
    ByteArrayOutputStream byteArrayOutputStream = null;
    DataOutputStream dataOutputStream = null;
    null = null;
    AudioFormat audioFormat2 = null;
    AudioFormat.Encoding encoding = null;
    InputStream inputStream = paramInputStream;
    if (paramInputStream instanceof AudioInputStream) {
      audioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
      encoding = audioFormat2.getEncoding();
      if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && s3 == 8) {
        s1 = 1;
        inputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat2.getSampleRate(), audioFormat2.getSampleSizeInBits(), audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), false), (AudioInputStream)paramInputStream);
      } 
      if (((AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && audioFormat2.isBigEndian()) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && !audioFormat2.isBigEndian()) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && audioFormat2.isBigEndian())) && s3 != 8) {
        s1 = 1;
        inputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat2.getSampleRate(), audioFormat2.getSampleSizeInBits(), audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getFrameRate(), false), (AudioInputStream)paramInputStream);
      } 
    } 
    byteArrayOutputStream = new ByteArrayOutputStream();
    dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    dataOutputStream.writeInt(j);
    dataOutputStream.writeInt(big2little(i7));
    dataOutputStream.writeInt(k);
    dataOutputStream.writeInt(m);
    dataOutputStream.writeInt(big2little(n));
    dataOutputStream.writeShort(big2littleShort(s1));
    dataOutputStream.writeShort(big2littleShort(s2));
    dataOutputStream.writeInt(big2little(i1));
    dataOutputStream.writeInt(big2little(s4));
    dataOutputStream.writeShort(big2littleShort(s5));
    dataOutputStream.writeShort(big2littleShort(s3));
    if (s1 != 1)
      dataOutputStream.writeShort(0); 
    dataOutputStream.writeInt(i4);
    dataOutputStream.writeInt(big2little(i5));
    dataOutputStream.close();
    arrayOfByte = byteArrayOutputStream.toByteArray();
    byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    return new SequenceInputStream(byteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, inputStream));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\WaveFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */