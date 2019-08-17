package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardDirector implements ModelDirector {
  private final ModelPerformer[] performers;
  
  private final ModelDirectedPlayer player;
  
  private boolean noteOnUsed = false;
  
  private boolean noteOffUsed = false;
  
  public ModelStandardDirector(ModelPerformer[] paramArrayOfModelPerformer, ModelDirectedPlayer paramModelDirectedPlayer) {
    this.performers = (ModelPerformer[])Arrays.copyOf(paramArrayOfModelPerformer, paramArrayOfModelPerformer.length);
    this.player = paramModelDirectedPlayer;
    for (ModelPerformer modelPerformer : this.performers) {
      if (modelPerformer.isReleaseTriggered()) {
        this.noteOffUsed = true;
      } else {
        this.noteOnUsed = true;
      } 
    } 
  }
  
  public void close() {}
  
  public void noteOff(int paramInt1, int paramInt2) {
    if (!this.noteOffUsed)
      return; 
    for (byte b = 0; b < this.performers.length; b++) {
      ModelPerformer modelPerformer = this.performers[b];
      if (modelPerformer.getKeyFrom() <= paramInt1 && modelPerformer.getKeyTo() >= paramInt1 && modelPerformer.getVelFrom() <= paramInt2 && modelPerformer.getVelTo() >= paramInt2 && modelPerformer.isReleaseTriggered())
        this.player.play(b, null); 
    } 
  }
  
  public void noteOn(int paramInt1, int paramInt2) {
    if (!this.noteOnUsed)
      return; 
    for (byte b = 0; b < this.performers.length; b++) {
      ModelPerformer modelPerformer = this.performers[b];
      if (modelPerformer.getKeyFrom() <= paramInt1 && modelPerformer.getKeyTo() >= paramInt1 && modelPerformer.getVelFrom() <= paramInt2 && modelPerformer.getVelTo() >= paramInt2 && !modelPerformer.isReleaseTriggered())
        this.player.play(b, null); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelStandardDirector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */