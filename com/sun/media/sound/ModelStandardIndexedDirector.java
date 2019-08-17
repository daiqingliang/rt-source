package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardIndexedDirector implements ModelDirector {
  private final ModelPerformer[] performers;
  
  private final ModelDirectedPlayer player;
  
  private boolean noteOnUsed = false;
  
  private boolean noteOffUsed = false;
  
  private byte[][] trantables;
  
  private int[] counters;
  
  private int[][] mat;
  
  public ModelStandardIndexedDirector(ModelPerformer[] paramArrayOfModelPerformer, ModelDirectedPlayer paramModelDirectedPlayer) {
    this.performers = (ModelPerformer[])Arrays.copyOf(paramArrayOfModelPerformer, paramArrayOfModelPerformer.length);
    this.player = paramModelDirectedPlayer;
    for (ModelPerformer modelPerformer : this.performers) {
      if (modelPerformer.isReleaseTriggered()) {
        this.noteOffUsed = true;
      } else {
        this.noteOnUsed = true;
      } 
    } 
    buildindex();
  }
  
  private int[] lookupIndex(int paramInt1, int paramInt2) {
    if (paramInt1 >= 0 && paramInt1 < 128 && paramInt2 >= 0 && paramInt2 < 128) {
      byte b1 = this.trantables[0][paramInt1];
      byte b2 = this.trantables[1][paramInt2];
      if (b1 != -1 && b2 != -1)
        return this.mat[b1 + b2 * this.counters[0]]; 
    } 
    return null;
  }
  
  private int restrict(int paramInt) { return (paramInt < 0) ? 0 : ((paramInt > 127) ? 127 : paramInt); }
  
  private void buildindex() {
    this.trantables = new byte[2][129];
    this.counters = new int[this.trantables.length];
    for (ModelPerformer modelPerformer : this.performers) {
      int i = modelPerformer.getKeyFrom();
      int j = modelPerformer.getKeyTo();
      int k = modelPerformer.getVelFrom();
      int m = modelPerformer.getVelTo();
      if (i <= j && k <= m) {
        i = restrict(i);
        j = restrict(j);
        k = restrict(k);
        m = restrict(m);
        this.trantables[0][i] = 1;
        this.trantables[0][j + 1] = 1;
        this.trantables[1][k] = 1;
        this.trantables[1][m + 1] = 1;
      } 
    } 
    byte b;
    for (b = 0; b < this.trantables.length; b++) {
      byte[] arrayOfByte = this.trantables[b];
      int i = arrayOfByte.length;
      int j;
      for (j = i - 1; j >= 0; j--) {
        if (arrayOfByte[j] == 1) {
          arrayOfByte[j] = -1;
          break;
        } 
        arrayOfByte[j] = -1;
      } 
      j = -1;
      for (byte b1 = 0; b1 < i; b1++) {
        if (arrayOfByte[b1] != 0) {
          j++;
          if (arrayOfByte[b1] == -1)
            break; 
        } 
        arrayOfByte[b1] = (byte)j;
      } 
      this.counters[b] = j;
    } 
    this.mat = new int[this.counters[0] * this.counters[1]][];
    b = 0;
    for (ModelPerformer modelPerformer : this.performers) {
      int i = modelPerformer.getKeyFrom();
      int j = modelPerformer.getKeyTo();
      int k = modelPerformer.getVelFrom();
      int m = modelPerformer.getVelTo();
      if (i <= j && k <= m) {
        i = restrict(i);
        j = restrict(j);
        k = restrict(k);
        m = restrict(m);
        byte b1 = this.trantables[0][i];
        int n = this.trantables[0][j + 1];
        byte b2 = this.trantables[1][k];
        int i1 = this.trantables[1][m + 1];
        if (n == -1)
          n = this.counters[0]; 
        if (i1 == -1)
          i1 = this.counters[1]; 
        for (int i2 = b2; i2 < i1; i2++) {
          byte b3 = b1 + i2 * this.counters[0];
          for (byte b4 = b1; b4 < n; b4++) {
            int[] arrayOfInt = this.mat[b3];
            if (arrayOfInt == null) {
              new int[1][0] = b;
              this.mat[b3] = new int[1];
            } else {
              int[] arrayOfInt1 = new int[arrayOfInt.length + 1];
              arrayOfInt1[arrayOfInt1.length - 1] = b;
              for (byte b5 = 0; b5 < arrayOfInt.length; b5++)
                arrayOfInt1[b5] = arrayOfInt[b5]; 
              this.mat[b3] = arrayOfInt1;
            } 
            b3++;
          } 
        } 
        b++;
      } 
    } 
  }
  
  public void close() {}
  
  public void noteOff(int paramInt1, int paramInt2) {
    if (!this.noteOffUsed)
      return; 
    int[] arrayOfInt = lookupIndex(paramInt1, paramInt2);
    if (arrayOfInt == null)
      return; 
    for (int i : arrayOfInt) {
      ModelPerformer modelPerformer = this.performers[i];
      if (modelPerformer.isReleaseTriggered())
        this.player.play(i, null); 
    } 
  }
  
  public void noteOn(int paramInt1, int paramInt2) {
    if (!this.noteOnUsed)
      return; 
    int[] arrayOfInt = lookupIndex(paramInt1, paramInt2);
    if (arrayOfInt == null)
      return; 
    for (int i : arrayOfInt) {
      ModelPerformer modelPerformer = this.performers[i];
      if (!modelPerformer.isReleaseTriggered())
        this.player.play(i, null); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelStandardIndexedDirector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */