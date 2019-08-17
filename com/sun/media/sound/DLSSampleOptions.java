package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSSampleOptions {
  int unitynote;
  
  short finetune;
  
  int attenuation;
  
  long options;
  
  List<DLSSampleLoop> loops = new ArrayList();
  
  public int getAttenuation() { return this.attenuation; }
  
  public void setAttenuation(int paramInt) { this.attenuation = paramInt; }
  
  public short getFinetune() { return this.finetune; }
  
  public void setFinetune(short paramShort) { this.finetune = paramShort; }
  
  public List<DLSSampleLoop> getLoops() { return this.loops; }
  
  public long getOptions() { return this.options; }
  
  public void setOptions(long paramLong) { this.options = paramLong; }
  
  public int getUnitynote() { return this.unitynote; }
  
  public void setUnitynote(int paramInt) { this.unitynote = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSSampleOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */