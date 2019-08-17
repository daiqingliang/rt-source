package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileWriter;

public final class WaveFloatFileWriter extends AudioFileWriter {
  public AudioFileFormat.Type[] getAudioFileTypes() { return new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE }; }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream) { return !paramAudioInputStream.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT) ? new AudioFileFormat.Type[0] : new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE }; }
  
  private void checkFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream) {
    if (!AudioFileFormat.Type.WAVE.equals(paramType))
      throw new IllegalArgumentException("File type " + paramType + " not supported."); 
    if (!paramAudioInputStream.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT))
      throw new IllegalArgumentException("File format " + paramAudioInputStream.getFormat() + " not supported."); 
  }
  
  public void write(AudioInputStream paramAudioInputStream, RIFFWriter paramRIFFWriter) throws IOException {
    RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("fmt ");
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    rIFFWriter1.writeUnsignedShort(3);
    rIFFWriter1.writeUnsignedShort(audioFormat.getChannels());
    rIFFWriter1.writeUnsignedInt((int)audioFormat.getSampleRate());
    rIFFWriter1.writeUnsignedInt(((int)audioFormat.getFrameRate() * audioFormat.getFrameSize()));
    rIFFWriter1.writeUnsignedShort(audioFormat.getFrameSize());
    rIFFWriter1.writeUnsignedShort(audioFormat.getSampleSizeInBits());
    rIFFWriter1.close();
    RIFFWriter rIFFWriter2 = paramRIFFWriter.writeChunk("data");
    byte[] arrayOfByte = new byte[1024];
    int i;
    while ((i = paramAudioInputStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1)
      rIFFWriter2.write(arrayOfByte, 0, i); 
    rIFFWriter2.close();
  }
  
  private AudioInputStream toLittleEndian(AudioInputStream paramAudioInputStream) {
    AudioFormat audioFormat1 = paramAudioInputStream.getFormat();
    AudioFormat audioFormat2 = new AudioFormat(audioFormat1.getEncoding(), audioFormat1.getSampleRate(), audioFormat1.getSampleSizeInBits(), audioFormat1.getChannels(), audioFormat1.getFrameSize(), audioFormat1.getFrameRate(), false);
    return AudioSystem.getAudioInputStream(audioFormat2, paramAudioInputStream);
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException {
    checkFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFormat().isBigEndian())
      paramAudioInputStream = toLittleEndian(paramAudioInputStream); 
    RIFFWriter rIFFWriter = new RIFFWriter(new NoCloseOutputStream(paramOutputStream), "WAVE");
    write(paramAudioInputStream, rIFFWriter);
    int i = (int)rIFFWriter.getFilePointer();
    rIFFWriter.close();
    return i;
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException {
    checkFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFormat().isBigEndian())
      paramAudioInputStream = toLittleEndian(paramAudioInputStream); 
    RIFFWriter rIFFWriter = new RIFFWriter(paramFile, "WAVE");
    write(paramAudioInputStream, rIFFWriter);
    int i = (int)rIFFWriter.getFilePointer();
    rIFFWriter.close();
    return i;
  }
  
  private static class NoCloseOutputStream extends OutputStream {
    final OutputStream out;
    
    NoCloseOutputStream(OutputStream param1OutputStream) { this.out = param1OutputStream; }
    
    public void write(int param1Int) throws IOException { this.out.write(param1Int); }
    
    public void flush() { this.out.flush(); }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { this.out.write(param1ArrayOfByte, param1Int1, param1Int2); }
    
    public void write(byte[] param1ArrayOfByte) throws IOException { this.out.write(param1ArrayOfByte); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\WaveFloatFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */