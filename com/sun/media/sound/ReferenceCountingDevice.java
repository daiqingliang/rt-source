package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public interface ReferenceCountingDevice {
  Receiver getReceiverReferenceCounting() throws MidiUnavailableException;
  
  Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ReferenceCountingDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */