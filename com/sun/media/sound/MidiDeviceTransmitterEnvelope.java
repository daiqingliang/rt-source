package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public final class MidiDeviceTransmitterEnvelope implements MidiDeviceTransmitter {
  private final MidiDevice device;
  
  private final Transmitter transmitter;
  
  public MidiDeviceTransmitterEnvelope(MidiDevice paramMidiDevice, Transmitter paramTransmitter) {
    if (paramMidiDevice == null || paramTransmitter == null)
      throw new NullPointerException(); 
    this.device = paramMidiDevice;
    this.transmitter = paramTransmitter;
  }
  
  public void setReceiver(Receiver paramReceiver) { this.transmitter.setReceiver(paramReceiver); }
  
  public Receiver getReceiver() { return this.transmitter.getReceiver(); }
  
  public void close() { this.transmitter.close(); }
  
  public MidiDevice getMidiDevice() { return this.device; }
  
  public Transmitter getTransmitter() { return this.transmitter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\MidiDeviceTransmitterEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */