package javax.sound.midi;

public interface MidiChannel {
  void noteOn(int paramInt1, int paramInt2);
  
  void noteOff(int paramInt1, int paramInt2);
  
  void noteOff(int paramInt);
  
  void setPolyPressure(int paramInt1, int paramInt2);
  
  int getPolyPressure(int paramInt);
  
  void setChannelPressure(int paramInt);
  
  int getChannelPressure();
  
  void controlChange(int paramInt1, int paramInt2);
  
  int getController(int paramInt);
  
  void programChange(int paramInt);
  
  void programChange(int paramInt1, int paramInt2);
  
  int getProgram();
  
  void setPitchBend(int paramInt);
  
  int getPitchBend();
  
  void resetAllControllers();
  
  void allNotesOff();
  
  void allSoundOff();
  
  boolean localControl(boolean paramBoolean);
  
  void setMono(boolean paramBoolean);
  
  boolean getMono();
  
  void setOmni(boolean paramBoolean);
  
  boolean getOmni();
  
  void setMute(boolean paramBoolean);
  
  boolean getMute();
  
  void setSolo(boolean paramBoolean);
  
  boolean getSolo();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MidiChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */