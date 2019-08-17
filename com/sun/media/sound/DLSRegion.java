package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSRegion {
  public static final int OPTION_SELFNONEXCLUSIVE = 1;
  
  List<DLSModulator> modulators = new ArrayList();
  
  int keyfrom;
  
  int keyto;
  
  int velfrom;
  
  int velto;
  
  int options;
  
  int exclusiveClass;
  
  int fusoptions;
  
  int phasegroup;
  
  long channel;
  
  DLSSample sample = null;
  
  DLSSampleOptions sampleoptions;
  
  public List<DLSModulator> getModulators() { return this.modulators; }
  
  public long getChannel() { return this.channel; }
  
  public void setChannel(long paramLong) { this.channel = paramLong; }
  
  public int getExclusiveClass() { return this.exclusiveClass; }
  
  public void setExclusiveClass(int paramInt) { this.exclusiveClass = paramInt; }
  
  public int getFusoptions() { return this.fusoptions; }
  
  public void setFusoptions(int paramInt) { this.fusoptions = paramInt; }
  
  public int getKeyfrom() { return this.keyfrom; }
  
  public void setKeyfrom(int paramInt) { this.keyfrom = paramInt; }
  
  public int getKeyto() { return this.keyto; }
  
  public void setKeyto(int paramInt) { this.keyto = paramInt; }
  
  public int getOptions() { return this.options; }
  
  public void setOptions(int paramInt) { this.options = paramInt; }
  
  public int getPhasegroup() { return this.phasegroup; }
  
  public void setPhasegroup(int paramInt) { this.phasegroup = paramInt; }
  
  public DLSSample getSample() { return this.sample; }
  
  public void setSample(DLSSample paramDLSSample) { this.sample = paramDLSSample; }
  
  public int getVelfrom() { return this.velfrom; }
  
  public void setVelfrom(int paramInt) { this.velfrom = paramInt; }
  
  public int getVelto() { return this.velto; }
  
  public void setVelto(int paramInt) { this.velto = paramInt; }
  
  public void setModulators(List<DLSModulator> paramList) { this.modulators = paramList; }
  
  public DLSSampleOptions getSampleoptions() { return this.sampleoptions; }
  
  public void setSampleoptions(DLSSampleOptions paramDLSSampleOptions) { this.sampleoptions = paramDLSSampleOptions; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSRegion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */