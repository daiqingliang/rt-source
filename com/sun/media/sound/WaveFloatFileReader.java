package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveFloatFileReader extends AudioFileReader {
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat;
    paramInputStream.mark(200);
    try {
      audioFileFormat = internal_getAudioFileFormat(paramInputStream);
    } finally {
      paramInputStream.reset();
    } 
    return audioFileFormat;
  }
  
  private AudioFileFormat internal_getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    RIFFReader rIFFReader = new RIFFReader(paramInputStream);
    if (!rIFFReader.getFormat().equals("RIFF"))
      throw new UnsupportedAudioFileException(); 
    if (!rIFFReader.getType().equals("WAVE"))
      throw new UnsupportedAudioFileException(); 
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 1;
    long l = 1L;
    int j = 1;
    int k = 1;
    while (rIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader1 = rIFFReader.nextChunk();
      if (rIFFReader1.getFormat().equals("fmt ")) {
        bool1 = true;
        int m = rIFFReader1.readUnsignedShort();
        if (m != 3)
          throw new UnsupportedAudioFileException(); 
        i = rIFFReader1.readUnsignedShort();
        l = rIFFReader1.readUnsignedInt();
        rIFFReader1.readUnsignedInt();
        j = rIFFReader1.readUnsignedShort();
        k = rIFFReader1.readUnsignedShort();
      } 
      if (rIFFReader1.getFormat().equals("data")) {
        bool2 = true;
        break;
      } 
    } 
    if (!bool1)
      throw new UnsupportedAudioFileException(); 
    if (!bool2)
      throw new UnsupportedAudioFileException(); 
    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l, k, i, j, (float)l, false);
    return new AudioFileFormat(AudioFileFormat.Type.WAVE, audioFormat, -1);
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = getAudioFileFormat(paramInputStream);
    RIFFReader rIFFReader = new RIFFReader(paramInputStream);
    if (!rIFFReader.getFormat().equals("RIFF"))
      throw new UnsupportedAudioFileException(); 
    if (!rIFFReader.getType().equals("WAVE"))
      throw new UnsupportedAudioFileException(); 
    while (rIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader1 = rIFFReader.nextChunk();
      if (rIFFReader1.getFormat().equals("data"))
        return new AudioInputStream(rIFFReader1, audioFileFormat.getFormat(), rIFFReader1.getSize()); 
    } 
    throw new UnsupportedAudioFileException();
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat;
    inputStream = paramURL.openStream();
    try {
      audioFileFormat = getAudioFileFormat(new BufferedInputStream(inputStream));
    } finally {
      inputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat;
    fileInputStream = new FileInputStream(paramFile);
    try {
      audioFileFormat = getAudioFileFormat(new BufferedInputStream(fileInputStream));
    } finally {
      fileInputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException { return getAudioInputStream(new BufferedInputStream(paramURL.openStream())); }
  
  public AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException { return getAudioInputStream(new BufferedInputStream(new FileInputStream(paramFile))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\WaveFloatFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */