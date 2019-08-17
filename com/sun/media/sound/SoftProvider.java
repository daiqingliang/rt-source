package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class SoftProvider extends MidiDeviceProvider {
  static final MidiDevice.Info softinfo = SoftSynthesizer.info;
  
  private static final MidiDevice.Info[] softinfos = { softinfo };
  
  public MidiDevice.Info[] getDeviceInfo() { return (Info[])Arrays.copyOf(softinfos, softinfos.length); }
  
  public MidiDevice getDevice(MidiDevice.Info paramInfo) { return (paramInfo == softinfo) ? new SoftSynthesizer() : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */