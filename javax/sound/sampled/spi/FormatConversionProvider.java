package javax.sound.sampled.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class FormatConversionProvider {
  public abstract AudioFormat.Encoding[] getSourceEncodings();
  
  public abstract AudioFormat.Encoding[] getTargetEncodings();
  
  public boolean isSourceEncodingSupported(AudioFormat.Encoding paramEncoding) {
    AudioFormat.Encoding[] arrayOfEncoding = getSourceEncodings();
    for (byte b = 0; b < arrayOfEncoding.length; b++) {
      if (paramEncoding.equals(arrayOfEncoding[b]))
        return true; 
    } 
    return false;
  }
  
  public boolean isTargetEncodingSupported(AudioFormat.Encoding paramEncoding) {
    AudioFormat.Encoding[] arrayOfEncoding = getTargetEncodings();
    for (byte b = 0; b < arrayOfEncoding.length; b++) {
      if (paramEncoding.equals(arrayOfEncoding[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat);
  
  public boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) {
    AudioFormat.Encoding[] arrayOfEncoding = getTargetEncodings(paramAudioFormat);
    for (byte b = 0; b < arrayOfEncoding.length; b++) {
      if (paramEncoding.equals(arrayOfEncoding[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat);
  
  public boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2) {
    AudioFormat[] arrayOfAudioFormat = getTargetFormats(paramAudioFormat1.getEncoding(), paramAudioFormat2);
    for (byte b = 0; b < arrayOfAudioFormat.length; b++) {
      if (paramAudioFormat1.matches(arrayOfAudioFormat[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream);
  
  public abstract AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\spi\FormatConversionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */