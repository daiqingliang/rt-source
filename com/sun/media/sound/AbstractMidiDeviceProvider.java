package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

public abstract class AbstractMidiDeviceProvider extends MidiDeviceProvider {
  private static final boolean enabled;
  
  final void readDeviceInfos() {
    Info[] arrayOfInfo = getInfoCache();
    MidiDevice[] arrayOfMidiDevice = getDeviceCache();
    if (!enabled) {
      if (arrayOfInfo == null || arrayOfInfo.length != 0)
        setInfoCache(new Info[0]); 
      if (arrayOfMidiDevice == null || arrayOfMidiDevice.length != 0)
        setDeviceCache(new MidiDevice[0]); 
      return;
    } 
    byte b = (arrayOfInfo == null) ? -1 : arrayOfInfo.length;
    int i = getNumDevices();
    if (b != i) {
      Info[] arrayOfInfo1 = new Info[i];
      MidiDevice[] arrayOfMidiDevice1 = new MidiDevice[i];
      byte b1;
      for (b1 = 0; b1 < i; b1++) {
        Info info = createInfo(b1);
        if (arrayOfInfo != null)
          for (byte b2 = 0; b2 < arrayOfInfo.length; b2++) {
            Info info1 = arrayOfInfo[b2];
            if (info1 != null && info1.equalStrings(info)) {
              arrayOfInfo1[b1] = info1;
              info1.setIndex(b1);
              arrayOfInfo[b2] = null;
              arrayOfMidiDevice1[b1] = arrayOfMidiDevice[b2];
              arrayOfMidiDevice[b2] = null;
              break;
            } 
          }  
        if (arrayOfInfo1[b1] == null)
          arrayOfInfo1[b1] = info; 
      } 
      if (arrayOfInfo != null)
        for (b1 = 0; b1 < arrayOfInfo.length; b1++) {
          if (arrayOfInfo[b1] != null)
            arrayOfInfo[b1].setIndex(-1); 
        }  
      setInfoCache(arrayOfInfo1);
      setDeviceCache(arrayOfMidiDevice1);
    } 
  }
  
  public final MidiDevice.Info[] getDeviceInfo() {
    readDeviceInfos();
    Info[] arrayOfInfo = getInfoCache();
    MidiDevice.Info[] arrayOfInfo1 = new MidiDevice.Info[arrayOfInfo.length];
    System.arraycopy(arrayOfInfo, 0, arrayOfInfo1, 0, arrayOfInfo.length);
    return arrayOfInfo1;
  }
  
  public final MidiDevice getDevice(MidiDevice.Info paramInfo) {
    if (paramInfo instanceof Info) {
      readDeviceInfos();
      MidiDevice[] arrayOfMidiDevice = getDeviceCache();
      Info[] arrayOfInfo = getInfoCache();
      Info info = (Info)paramInfo;
      int i = info.getIndex();
      if (i >= 0 && i < arrayOfMidiDevice.length && arrayOfInfo[i] == paramInfo) {
        if (arrayOfMidiDevice[i] == null)
          arrayOfMidiDevice[i] = createDevice(info); 
        if (arrayOfMidiDevice[i] != null)
          return arrayOfMidiDevice[i]; 
      } 
    } 
    throw new IllegalArgumentException("MidiDevice " + paramInfo.toString() + " not supported by this provider.");
  }
  
  abstract int getNumDevices();
  
  abstract MidiDevice[] getDeviceCache();
  
  abstract void setDeviceCache(MidiDevice[] paramArrayOfMidiDevice);
  
  abstract Info[] getInfoCache();
  
  abstract void setInfoCache(Info[] paramArrayOfInfo);
  
  abstract Info createInfo(int paramInt);
  
  abstract MidiDevice createDevice(Info paramInfo);
  
  static  {
    Platform.initialize();
    enabled = Platform.isMidiIOEnabled();
  }
  
  static class Info extends MidiDevice.Info {
    private int index;
    
    Info(String param1String1, String param1String2, String param1String3, String param1String4, int param1Int) {
      super(param1String1, param1String2, param1String3, param1String4);
      this.index = param1Int;
    }
    
    final boolean equalStrings(Info param1Info) { return (param1Info != null && getName().equals(param1Info.getName()) && getVendor().equals(param1Info.getVendor()) && getDescription().equals(param1Info.getDescription()) && getVersion().equals(param1Info.getVersion())); }
    
    final int getIndex() { return this.index; }
    
    final void setIndex(int param1Int) { this.index = param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AbstractMidiDeviceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */