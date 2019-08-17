package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public final class SoftChannelProxy implements MidiChannel {
  private MidiChannel channel = null;
  
  public MidiChannel getChannel() { return this.channel; }
  
  public void setChannel(MidiChannel paramMidiChannel) { this.channel = paramMidiChannel; }
  
  public void allNotesOff() {
    if (this.channel == null)
      return; 
    this.channel.allNotesOff();
  }
  
  public void allSoundOff() {
    if (this.channel == null)
      return; 
    this.channel.allSoundOff();
  }
  
  public void controlChange(int paramInt1, int paramInt2) {
    if (this.channel == null)
      return; 
    this.channel.controlChange(paramInt1, paramInt2);
  }
  
  public int getChannelPressure() { return (this.channel == null) ? 0 : this.channel.getChannelPressure(); }
  
  public int getController(int paramInt) { return (this.channel == null) ? 0 : this.channel.getController(paramInt); }
  
  public boolean getMono() { return (this.channel == null) ? false : this.channel.getMono(); }
  
  public boolean getMute() { return (this.channel == null) ? false : this.channel.getMute(); }
  
  public boolean getOmni() { return (this.channel == null) ? false : this.channel.getOmni(); }
  
  public int getPitchBend() { return (this.channel == null) ? 8192 : this.channel.getPitchBend(); }
  
  public int getPolyPressure(int paramInt) { return (this.channel == null) ? 0 : this.channel.getPolyPressure(paramInt); }
  
  public int getProgram() { return (this.channel == null) ? 0 : this.channel.getProgram(); }
  
  public boolean getSolo() { return (this.channel == null) ? false : this.channel.getSolo(); }
  
  public boolean localControl(boolean paramBoolean) { return (this.channel == null) ? false : this.channel.localControl(paramBoolean); }
  
  public void noteOff(int paramInt) {
    if (this.channel == null)
      return; 
    this.channel.noteOff(paramInt);
  }
  
  public void noteOff(int paramInt1, int paramInt2) {
    if (this.channel == null)
      return; 
    this.channel.noteOff(paramInt1, paramInt2);
  }
  
  public void noteOn(int paramInt1, int paramInt2) {
    if (this.channel == null)
      return; 
    this.channel.noteOn(paramInt1, paramInt2);
  }
  
  public void programChange(int paramInt) {
    if (this.channel == null)
      return; 
    this.channel.programChange(paramInt);
  }
  
  public void programChange(int paramInt1, int paramInt2) {
    if (this.channel == null)
      return; 
    this.channel.programChange(paramInt1, paramInt2);
  }
  
  public void resetAllControllers() {
    if (this.channel == null)
      return; 
    this.channel.resetAllControllers();
  }
  
  public void setChannelPressure(int paramInt) {
    if (this.channel == null)
      return; 
    this.channel.setChannelPressure(paramInt);
  }
  
  public void setMono(boolean paramBoolean) {
    if (this.channel == null)
      return; 
    this.channel.setMono(paramBoolean);
  }
  
  public void setMute(boolean paramBoolean) {
    if (this.channel == null)
      return; 
    this.channel.setMute(paramBoolean);
  }
  
  public void setOmni(boolean paramBoolean) {
    if (this.channel == null)
      return; 
    this.channel.setOmni(paramBoolean);
  }
  
  public void setPitchBend(int paramInt) {
    if (this.channel == null)
      return; 
    this.channel.setPitchBend(paramInt);
  }
  
  public void setPolyPressure(int paramInt1, int paramInt2) {
    if (this.channel == null)
      return; 
    this.channel.setPolyPressure(paramInt1, paramInt2);
  }
  
  public void setSolo(boolean paramBoolean) {
    if (this.channel == null)
      return; 
    this.channel.setSolo(paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftChannelProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */