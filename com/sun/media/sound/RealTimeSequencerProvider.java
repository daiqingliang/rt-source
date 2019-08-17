package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class RealTimeSequencerProvider extends MidiDeviceProvider {
  public MidiDevice.Info[] getDeviceInfo() { return new MidiDevice.Info[] { RealTimeSequencer.info }; }
  
  public MidiDevice getDevice(MidiDevice.Info paramInfo) {
    if (paramInfo != null && !paramInfo.equals(RealTimeSequencer.info))
      return null; 
    try {
      return new RealTimeSequencer();
    } catch (MidiUnavailableException midiUnavailableException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\RealTimeSequencerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */