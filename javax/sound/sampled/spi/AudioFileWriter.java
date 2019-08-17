package javax.sound.sampled.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class AudioFileWriter {
  public abstract AudioFileFormat.Type[] getAudioFileTypes();
  
  public boolean isFileTypeSupported(AudioFileFormat.Type paramType) {
    AudioFileFormat.Type[] arrayOfType = getAudioFileTypes();
    for (byte b = 0; b < arrayOfType.length; b++) {
      if (paramType.equals(arrayOfType[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream);
  
  public boolean isFileTypeSupported(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream) {
    AudioFileFormat.Type[] arrayOfType = getAudioFileTypes(paramAudioInputStream);
    for (byte b = 0; b < arrayOfType.length; b++) {
      if (paramType.equals(arrayOfType[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException;
  
  public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\spi\AudioFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */