package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public final class MidiDeviceReceiverEnvelope implements MidiDeviceReceiver {
  private final MidiDevice device;
  
  private final Receiver receiver;
  
  public MidiDeviceReceiverEnvelope(MidiDevice paramMidiDevice, Receiver paramReceiver) {
    if (paramMidiDevice == null || paramReceiver == null)
      throw new NullPointerException(); 
    this.device = paramMidiDevice;
    this.receiver = paramReceiver;
  }
  
  public void close() { this.receiver.close(); }
  
  public void send(MidiMessage paramMidiMessage, long paramLong) { this.receiver.send(paramMidiMessage, paramLong); }
  
  public MidiDevice getMidiDevice() { return this.device; }
  
  public Receiver getReceiver() { return this.receiver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\MidiDeviceReceiverEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */