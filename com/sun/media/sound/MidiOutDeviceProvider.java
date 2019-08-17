package com.sun.media.sound;

import javax.sound.midi.MidiDevice;

public final class MidiOutDeviceProvider extends AbstractMidiDeviceProvider {
  private static AbstractMidiDeviceProvider.Info[] infos = null;
  
  private static MidiDevice[] devices = null;
  
  private static final boolean enabled;
  
  AbstractMidiDeviceProvider.Info createInfo(int paramInt) { return !enabled ? null : new MidiOutDeviceInfo(paramInt, MidiOutDeviceProvider.class, null); }
  
  MidiDevice createDevice(AbstractMidiDeviceProvider.Info paramInfo) { return (enabled && paramInfo instanceof MidiOutDeviceInfo) ? new MidiOutDevice(paramInfo) : null; }
  
  int getNumDevices() { return !enabled ? 0 : nGetNumDevices(); }
  
  MidiDevice[] getDeviceCache() { return devices; }
  
  void setDeviceCache(MidiDevice[] paramArrayOfMidiDevice) {
    this;
    devices = paramArrayOfMidiDevice;
  }
  
  AbstractMidiDeviceProvider.Info[] getInfoCache() { return infos; }
  
  void setInfoCache(AbstractMidiDeviceProvider.Info[] paramArrayOfInfo) {
    this;
    infos = paramArrayOfInfo;
  }
  
  private static native int nGetNumDevices();
  
  private static native String nGetName(int paramInt);
  
  private static native String nGetVendor(int paramInt);
  
  private static native String nGetDescription(int paramInt);
  
  private static native String nGetVersion(int paramInt);
  
  static  {
    Platform.initialize();
    enabled = Platform.isMidiIOEnabled();
  }
  
  static final class MidiOutDeviceInfo extends AbstractMidiDeviceProvider.Info {
    private final Class providerClass;
    
    private MidiOutDeviceInfo(int param1Int, Class param1Class) {
      super(MidiOutDeviceProvider.nGetName(param1Int), MidiOutDeviceProvider.nGetVendor(param1Int), MidiOutDeviceProvider.nGetDescription(param1Int), MidiOutDeviceProvider.nGetVersion(param1Int), param1Int);
      this.providerClass = param1Class;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\MidiOutDeviceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */