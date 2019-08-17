package com.sun.media.sound;

import java.util.TreeMap;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public final class SoftReceiver implements MidiDeviceReceiver {
  boolean open = true;
  
  private final Object control_mutex;
  
  private final SoftSynthesizer synth;
  
  TreeMap<Long, Object> midimessages;
  
  SoftMainMixer mainmixer;
  
  public SoftReceiver(SoftSynthesizer paramSoftSynthesizer) {
    this.control_mutex = paramSoftSynthesizer.control_mutex;
    this.synth = paramSoftSynthesizer;
    this.mainmixer = paramSoftSynthesizer.getMainMixer();
    if (this.mainmixer != null)
      this.midimessages = this.mainmixer.midimessages; 
  }
  
  public MidiDevice getMidiDevice() { return this.synth; }
  
  public void send(MidiMessage paramMidiMessage, long paramLong) {
    synchronized (this.control_mutex) {
      if (!this.open)
        throw new IllegalStateException("Receiver is not open"); 
    } 
    if (paramLong != -1L) {
      synchronized (this.control_mutex) {
        this.mainmixer.activity();
        while (this.midimessages.get(Long.valueOf(paramLong)) != null)
          paramLong++; 
        if (paramMidiMessage instanceof ShortMessage && ((ShortMessage)paramMidiMessage).getChannel() > 15) {
          this.midimessages.put(Long.valueOf(paramLong), paramMidiMessage.clone());
        } else {
          this.midimessages.put(Long.valueOf(paramLong), paramMidiMessage.getMessage());
        } 
      } 
    } else {
      this.mainmixer.processMessage(paramMidiMessage);
    } 
  }
  
  public void close() {
    synchronized (this.control_mutex) {
      this.open = false;
    } 
    this.synth.removeReceiver(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */