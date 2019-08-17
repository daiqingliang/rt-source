package sun.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioData {
  private static final AudioFormat DEFAULT_FORMAT = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
  
  AudioFormat format;
  
  byte[] buffer;
  
  public AudioData(byte[] paramArrayOfByte) {
    this(DEFAULT_FORMAT, paramArrayOfByte);
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(paramArrayOfByte));
      this.format = audioInputStream.getFormat();
      audioInputStream.close();
    } catch (IOException iOException) {
    
    } catch (UnsupportedAudioFileException unsupportedAudioFileException) {}
  }
  
  AudioData(AudioFormat paramAudioFormat, byte[] paramArrayOfByte) {
    this.format = paramAudioFormat;
    if (paramArrayOfByte != null)
      this.buffer = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */