package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class SoftMixingMixerProvider extends MixerProvider {
  static SoftMixingMixer globalmixer = null;
  
  static Thread lockthread = null;
  
  static final Object mutex = new Object();
  
  public Mixer getMixer(Mixer.Info paramInfo) {
    if (paramInfo != null && paramInfo != SoftMixingMixer.info)
      throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider."); 
    synchronized (mutex) {
      if (lockthread != null && Thread.currentThread() == lockthread)
        throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider."); 
      if (globalmixer == null)
        globalmixer = new SoftMixingMixer(); 
      return globalmixer;
    } 
  }
  
  public Mixer.Info[] getMixerInfo() { return new Mixer.Info[] { SoftMixingMixer.info }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMixingMixerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */