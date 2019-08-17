package javax.sound.sampled.spi;

import javax.sound.sampled.Mixer;

public abstract class MixerProvider {
  public boolean isMixerSupported(Mixer.Info paramInfo) {
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      if (paramInfo.equals(arrayOfInfo[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract Mixer.Info[] getMixerInfo();
  
  public abstract Mixer getMixer(Mixer.Info paramInfo);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\spi\MixerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */