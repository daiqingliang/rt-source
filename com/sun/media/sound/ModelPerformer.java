package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class ModelPerformer {
  private final List<ModelOscillator> oscillators = new ArrayList();
  
  private List<ModelConnectionBlock> connectionBlocks = new ArrayList();
  
  private int keyFrom = 0;
  
  private int keyTo = 127;
  
  private int velFrom = 0;
  
  private int velTo = 127;
  
  private int exclusiveClass = 0;
  
  private boolean releaseTrigger = false;
  
  private boolean selfNonExclusive = false;
  
  private Object userObject = null;
  
  private boolean addDefaultConnections = true;
  
  private String name = null;
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public List<ModelConnectionBlock> getConnectionBlocks() { return this.connectionBlocks; }
  
  public void setConnectionBlocks(List<ModelConnectionBlock> paramList) { this.connectionBlocks = paramList; }
  
  public List<ModelOscillator> getOscillators() { return this.oscillators; }
  
  public int getExclusiveClass() { return this.exclusiveClass; }
  
  public void setExclusiveClass(int paramInt) { this.exclusiveClass = paramInt; }
  
  public boolean isSelfNonExclusive() { return this.selfNonExclusive; }
  
  public void setSelfNonExclusive(boolean paramBoolean) { this.selfNonExclusive = paramBoolean; }
  
  public int getKeyFrom() { return this.keyFrom; }
  
  public void setKeyFrom(int paramInt) { this.keyFrom = paramInt; }
  
  public int getKeyTo() { return this.keyTo; }
  
  public void setKeyTo(int paramInt) { this.keyTo = paramInt; }
  
  public int getVelFrom() { return this.velFrom; }
  
  public void setVelFrom(int paramInt) { this.velFrom = paramInt; }
  
  public int getVelTo() { return this.velTo; }
  
  public void setVelTo(int paramInt) { this.velTo = paramInt; }
  
  public boolean isReleaseTriggered() { return this.releaseTrigger; }
  
  public void setReleaseTriggered(boolean paramBoolean) { this.releaseTrigger = paramBoolean; }
  
  public Object getUserObject() { return this.userObject; }
  
  public void setUserObject(Object paramObject) { this.userObject = paramObject; }
  
  public boolean isDefaultConnectionsEnabled() { return this.addDefaultConnections; }
  
  public void setDefaultConnectionsEnabled(boolean paramBoolean) { this.addDefaultConnections = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelPerformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */