package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class PortMixerProvider extends MixerProvider {
  private static PortMixerInfo[] infos;
  
  private static PortMixer[] devices;
  
  public PortMixerProvider() {
    synchronized (PortMixerProvider.class) {
      if (Platform.isPortsEnabled()) {
        init();
      } else {
        infos = new PortMixerInfo[0];
        devices = new PortMixer[0];
      } 
    } 
  }
  
  private static void init() {
    int i = nGetNumDevices();
    if (infos == null || infos.length != i) {
      infos = new PortMixerInfo[i];
      devices = new PortMixer[i];
      for (byte b = 0; b < infos.length; b++)
        infos[b] = nNewPortMixerInfo(b); 
    } 
  }
  
  public Mixer.Info[] getMixerInfo() {
    synchronized (PortMixerProvider.class) {
      Mixer.Info[] arrayOfInfo = new Mixer.Info[infos.length];
      System.arraycopy(infos, 0, arrayOfInfo, 0, infos.length);
      return arrayOfInfo;
    } 
  }
  
  public Mixer getMixer(Mixer.Info paramInfo) {
    synchronized (PortMixerProvider.class) {
      for (byte b = 0; b < infos.length; b++) {
        if (infos[b].equals(paramInfo))
          return getDevice(infos[b]); 
      } 
    } 
    throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider.");
  }
  
  private static Mixer getDevice(PortMixerInfo paramPortMixerInfo) {
    int i = paramPortMixerInfo.getIndex();
    if (devices[i] == null)
      devices[i] = new PortMixer(paramPortMixerInfo); 
    return devices[i];
  }
  
  private static native int nGetNumDevices();
  
  private static native PortMixerInfo nNewPortMixerInfo(int paramInt);
  
  static  {
    Platform.initialize();
  }
  
  static final class PortMixerInfo extends Mixer.Info {
    private final int index;
    
    private PortMixerInfo(int param1Int, String param1String1, String param1String2, String param1String3, String param1String4) {
      super("Port " + param1String1, param1String2, param1String3, param1String4);
      this.index = param1Int;
    }
    
    int getIndex() { return this.index; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\PortMixerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */