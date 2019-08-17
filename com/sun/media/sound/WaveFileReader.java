package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class WaveFileReader extends SunFileReader {
  private static final int MAX_READ_LENGTH = 12;
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = getFMT(paramInputStream, true);
    paramInputStream.reset();
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    inputStream = paramURL.openStream();
    AudioFileFormat audioFileFormat = null;
    try {
      audioFileFormat = getFMT(inputStream, false);
    } finally {
      inputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = null;
    fileInputStream = new FileInputStream(paramFile);
    try {
      audioFileFormat = getFMT(fileInputStream, false);
    } finally {
      fileInputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = getFMT(paramInputStream, true);
    return new AudioInputStream(paramInputStream, audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException {
    inputStream = paramURL.openStream();
    audioFileFormat = null;
    try {
      audioFileFormat = getFMT(inputStream, false);
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
      audioFileFormat = getFMT(fileInputStream, false);
    } finally {
      if (audioFileFormat == null)
        fileInputStream.close(); 
    } 
    return new AudioInputStream(fileInputStream, audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
  }
  
  private AudioFileFormat getFMT(InputStream paramInputStream, boolean paramBoolean) throws UnsupportedAudioFileException, IOException {
    int i1;
    int i = 0;
    int j = 0;
    short s = 0;
    AudioFormat.Encoding encoding = null;
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    if (paramBoolean)
      dataInputStream.mark(12); 
    int k = dataInputStream.readInt();
    int m = rllong(dataInputStream);
    int n = dataInputStream.readInt();
    if (m <= 0) {
      m = -1;
      i1 = -1;
    } else {
      i1 = m + 8;
    } 
    if (k != 1380533830 || n != 1463899717) {
      if (paramBoolean)
        dataInputStream.reset(); 
      throw new UnsupportedAudioFileException("not a WAVE file");
    } 
    try {
      while (true) {
        int i4 = dataInputStream.readInt();
        i += true;
        if (i4 == 1718449184)
          break; 
        j = rllong(dataInputStream);
        i += true;
        if (j % 2 > 0)
          j++; 
        i += dataInputStream.skipBytes(j);
      } 
    } catch (EOFException eOFException) {
      throw new UnsupportedAudioFileException("Not a valid WAV file");
    } 
    j = rllong(dataInputStream);
    i += 4;
    int i2 = i + j;
    s = rlshort(dataInputStream);
    i += 2;
    if (s == 1) {
      encoding = AudioFormat.Encoding.PCM_SIGNED;
    } else if (s == 6) {
      encoding = AudioFormat.Encoding.ALAW;
    } else if (s == 7) {
      encoding = AudioFormat.Encoding.ULAW;
    } else {
      throw new UnsupportedAudioFileException("Not a supported WAV file");
    } 
    short s1 = rlshort(dataInputStream);
    i += 2;
    if (s1 <= 0)
      throw new UnsupportedAudioFileException("Invalid number of channels"); 
    long l1 = rllong(dataInputStream);
    i += 4;
    long l2 = rllong(dataInputStream);
    i += 4;
    short s2 = rlshort(dataInputStream);
    i += 2;
    short s3 = rlshort(dataInputStream);
    i += 2;
    if (s3 <= 0)
      throw new UnsupportedAudioFileException("Invalid bitsPerSample"); 
    if (s3 == 8 && encoding.equals(AudioFormat.Encoding.PCM_SIGNED))
      encoding = AudioFormat.Encoding.PCM_UNSIGNED; 
    if (j % 2 != 0)
      j++; 
    if (i2 > i)
      i += dataInputStream.skipBytes(i2 - i); 
    i = 0;
    try {
      while (true) {
        int i4 = dataInputStream.readInt();
        i += 4;
        if (i4 == 1684108385)
          break; 
        int i5 = rllong(dataInputStream);
        i += 4;
        if (i5 % 2 > 0)
          i5++; 
        i += dataInputStream.skipBytes(i5);
      } 
    } catch (EOFException eOFException) {
      throw new UnsupportedAudioFileException("Not a valid WAV file");
    } 
    int i3 = rllong(dataInputStream);
    i += 4;
    AudioFormat audioFormat = new AudioFormat(encoding, (float)l1, s3, s1, calculatePCMFrameSize(s3, s1), (float)l1, false);
    return new WaveFileFormat(AudioFileFormat.Type.WAVE, i1, audioFormat, i3 / audioFormat.getFrameSize());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\WaveFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */