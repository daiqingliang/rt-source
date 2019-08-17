package com.sun.media.sound;

import javax.sound.midi.Patch;

public final class ModelPatch extends Patch {
  private boolean percussion = false;
  
  public ModelPatch(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  public ModelPatch(int paramInt1, int paramInt2, boolean paramBoolean) {
    super(paramInt1, paramInt2);
    this.percussion = paramBoolean;
  }
  
  public boolean isPercussion() { return this.percussion; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelPatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */