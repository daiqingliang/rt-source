package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AuFileReader extends SunFileReader {
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    byte b2;
    AudioFormat audioFormat = null;
    AuFileFormat auFileFormat = null;
    byte b1 = 28;
    boolean bool = false;
    int i = -1;
    int j = -1;
    int k = -1;
    int m = -1;
    int n = -1;
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    int i4 = 0;
    boolean bool1 = false;
    AudioFormat.Encoding encoding = null;
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    dataInputStream.mark(b1);
    i = dataInputStream.readInt();
    if (i != 779316836 || i == 779314176 || i == 1684960046 || i == 6583086) {
      dataInputStream.reset();
      throw new UnsupportedAudioFileException("not an AU file");
    } 
    if (i == 779316836 || i == 779314176)
      bool = true; 
    j = (bool == true) ? dataInputStream.readInt() : rllong(dataInputStream);
    bool1 += true;
    k = (bool == true) ? dataInputStream.readInt() : rllong(dataInputStream);
    bool1 += true;
    m = (bool == true) ? dataInputStream.readInt() : rllong(dataInputStream);
    bool1 += true;
    n = (bool == true) ? dataInputStream.readInt() : rllong(dataInputStream);
    bool1 += true;
    i3 = (bool == true) ? dataInputStream.readInt() : rllong(dataInputStream);
    bool1 += true;
    if (i3 <= 0) {
      dataInputStream.reset();
      throw new UnsupportedAudioFileException("Invalid number of channels");
    } 
    i1 = n;
    switch (m) {
      case 1:
        encoding = AudioFormat.Encoding.ULAW;
        b2 = 8;
        break;
      case 27:
        encoding = AudioFormat.Encoding.ALAW;
        b2 = 8;
        break;
      case 2:
        encoding = AudioFormat.Encoding.PCM_SIGNED;
        b2 = 8;
        break;
      case 3:
        encoding = AudioFormat.Encoding.PCM_SIGNED;
        b2 = 16;
        break;
      case 4:
        encoding = AudioFormat.Encoding.PCM_SIGNED;
        b2 = 24;
        break;
      case 5:
        encoding = AudioFormat.Encoding.PCM_SIGNED;
        b2 = 32;
        break;
      default:
        dataInputStream.reset();
        throw new UnsupportedAudioFileException("not a valid AU file");
    } 
    i2 = calculatePCMFrameSize(b2, i3);
    if (k < 0) {
      i4 = -1;
    } else {
      i4 = k / i2;
    } 
    audioFormat = new AudioFormat(encoding, n, b2, i3, i2, i1, bool);
    auFileFormat = new AuFileFormat(AudioFileFormat.Type.AU, k + j, audioFormat, i4);
    dataInputStream.reset();
    return auFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    inputStream = null;
    BufferedInputStream bufferedInputStream = null;
    AudioFileFormat audioFileFormat = null;
    Object object = null;
    inputStream = paramURL.openStream();
    try {
      bufferedInputStream = new BufferedInputStream(inputStream, 4096);
      audioFileFormat = getAudioFileFormat(bufferedInputStream);
    } finally {
      inputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    fileInputStream = null;
    BufferedInputStream bufferedInputStream = null;
    AudioFileFormat audioFileFormat = null;
    Object object = null;
    fileInputStream = new FileInputStream(paramFile);
    try {
      bufferedInputStream = new BufferedInputStream(fileInputStream, 4096);
      audioFileFormat = getAudioFileFormat(bufferedInputStream);
    } finally {
      fileInputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    DataInputStream dataInputStream = null;
    AudioFileFormat audioFileFormat = null;
    AudioFormat audioFormat = null;
    audioFileFormat = getAudioFileFormat(paramInputStream);
    audioFormat = audioFileFormat.getFormat();
    dataInputStream = new DataInputStream(paramInputStream);
    dataInputStream.readInt();
    int i = (audioFormat.isBigEndian() == true) ? dataInputStream.readInt() : rllong(dataInputStream);
    dataInputStream.skipBytes(i - 8);
    return new AudioInputStream(dataInputStream, audioFormat, audioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException {
    inputStream = null;
    BufferedInputStream bufferedInputStream = null;
    Object object = null;
    inputStream = paramURL.openStream();
    audioInputStream = null;
    try {
      bufferedInputStream = new BufferedInputStream(inputStream, 4096);
      audioInputStream = getAudioInputStream(bufferedInputStream);
    } finally {
      if (audioInputStream == null)
        inputStream.close(); 
    } 
    return audioInputStream;
  }
  
  public AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException {
    fileInputStream = null;
    BufferedInputStream bufferedInputStream = null;
    Object object = null;
    fileInputStream = new FileInputStream(paramFile);
    audioInputStream = null;
    try {
      bufferedInputStream = new BufferedInputStream(fileInputStream, 4096);
      audioInputStream = getAudioInputStream(bufferedInputStream);
    } finally {
      if (audioInputStream == null)
        fileInputStream.close(); 
    } 
    return audioInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AuFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */