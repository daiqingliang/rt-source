package javax.sound.midi.spi;

import javax.sound.midi.MidiDevice;

public abstract class MidiDeviceProvider {
  public boolean isDeviceSupported(MidiDevice.Info paramInfo) {
    MidiDevice.Info[] arrayOfInfo = getDeviceInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      if (paramInfo.equals(arrayOfInfo[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract MidiDevice.Info[] getDeviceInfo();
  
  public abstract MidiDevice getDevice(MidiDevice.Info paramInfo);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\spi\MidiDeviceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */