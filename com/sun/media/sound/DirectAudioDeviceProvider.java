package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class DirectAudioDeviceProvider extends MixerProvider {
  private static DirectAudioDeviceInfo[] infos;
  
  private static DirectAudioDevice[] devices;
  
  public DirectAudioDeviceProvider() {
    synchronized (DirectAudioDeviceProvider.class) {
      if (Platform.isDirectAudioEnabled()) {
        init();
      } else {
        infos = new DirectAudioDeviceInfo[0];
        devices = new DirectAudioDevice[0];
      } 
    } 
  }
  
  private static void init() {
    int i = nGetNumDevices();
    if (infos == null || infos.length != i) {
      infos = new DirectAudioDeviceInfo[i];
      devices = new DirectAudioDevice[i];
      for (byte b = 0; b < infos.length; b++)
        infos[b] = nNewDirectAudioDeviceInfo(b); 
    } 
  }
  
  public Mixer.Info[] getMixerInfo() {
    synchronized (DirectAudioDeviceProvider.class) {
      Mixer.Info[] arrayOfInfo = new Mixer.Info[infos.length];
      System.arraycopy(infos, 0, arrayOfInfo, 0, infos.length);
      return arrayOfInfo;
    } 
  }
  
  public Mixer getMixer(Mixer.Info paramInfo) {
    synchronized (DirectAudioDeviceProvider.class) {
      if (paramInfo == null)
        for (byte b1 = 0; b1 < infos.length; b1++) {
          Mixer mixer = getDevice(infos[b1]);
          if (mixer.getSourceLineInfo().length > 0)
            return mixer; 
        }  
      for (byte b = 0; b < infos.length; b++) {
        if (infos[b].equals(paramInfo))
          return getDevice(infos[b]); 
      } 
    } 
    throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider.");
  }
  
  private static Mixer getDevice(DirectAudioDeviceInfo paramDirectAudioDeviceInfo) {
    int i = paramDirectAudioDeviceInfo.getIndex();
    if (devices[i] == null)
      devices[i] = new DirectAudioDevice(paramDirectAudioDeviceInfo); 
    return devices[i];
  }
  
  private static native int nGetNumDevices();
  
  private static native DirectAudioDeviceInfo nNewDirectAudioDeviceInfo(int paramInt);
  
  static  {
    Platform.initialize();
  }
  
  static final class DirectAudioDeviceInfo extends Mixer.Info {
    private final int index;
    
    private final int maxSimulLines;
    
    private final int deviceID;
    
    private DirectAudioDeviceInfo(int param1Int1, int param1Int2, int param1Int3, String param1String1, String param1String2, String param1String3, String param1String4) {
      super(param1String1, param1String2, "Direct Audio Device: " + param1String3, param1String4);
      this.index = param1Int1;
      this.maxSimulLines = param1Int3;
      this.deviceID = param1Int2;
    }
    
    int getIndex() { return this.index; }
    
    int getMaxSimulLines() { return this.maxSimulLines; }
    
    int getDeviceID() { return this.deviceID; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DirectAudioDeviceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */