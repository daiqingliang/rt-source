package com.sun.media.sound;

public abstract class ModelAbstractChannelMixer implements ModelChannelMixer {
  public abstract boolean process(float[][] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  public abstract void stop();
  
  public void allNotesOff() {}
  
  public void allSoundOff() {}
  
  public void controlChange(int paramInt1, int paramInt2) {}
  
  public int getChannelPressure() { return 0; }
  
  public int getController(int paramInt) { return 0; }
  
  public boolean getMono() { return false; }
  
  public boolean getMute() { return false; }
  
  public boolean getOmni() { return false; }
  
  public int getPitchBend() { return 0; }
  
  public int getPolyPressure(int paramInt) { return 0; }
  
  public int getProgram() { return 0; }
  
  public boolean getSolo() { return false; }
  
  public boolean localControl(boolean paramBoolean) { return false; }
  
  public void noteOff(int paramInt) {}
  
  public void noteOff(int paramInt1, int paramInt2) {}
  
  public void noteOn(int paramInt1, int paramInt2) {}
  
  public void programChange(int paramInt) {}
  
  public void programChange(int paramInt1, int paramInt2) {}
  
  public void resetAllControllers() {}
  
  public void setChannelPressure(int paramInt) {}
  
  public void setMono(boolean paramBoolean) {}
  
  public void setMute(boolean paramBoolean) {}
  
  public void setOmni(boolean paramBoolean) {}
  
  public void setPitchBend(int paramInt) {}
  
  public void setPolyPressure(int paramInt1, int paramInt2) {}
  
  public void setSolo(boolean paramBoolean) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelAbstractChannelMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */