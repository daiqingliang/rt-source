package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferInt extends DataBuffer {
  int[] data;
  
  int[][] bankdata;
  
  public DataBufferInt(int paramInt) {
    super(StateTrackable.State.STABLE, 3, paramInt);
    this.data = new int[paramInt];
    this.bankdata = new int[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferInt(int paramInt1, int paramInt2) {
    super(StateTrackable.State.STABLE, 3, paramInt1, paramInt2);
    this.bankdata = new int[paramInt2][];
    for (byte b = 0; b < paramInt2; b++)
      this.bankdata[b] = new int[paramInt1]; 
    this.data = this.bankdata[0];
  }
  
  public DataBufferInt(int[] paramArrayOfInt, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt);
    this.data = paramArrayOfInt;
    this.bankdata = new int[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferInt(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt1, 1, paramInt2);
    this.data = paramArrayOfInt;
    this.bankdata = new int[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferInt(int[][] paramArrayOfInt, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt, paramArrayOfInt.length);
    this.bankdata = (int[][])paramArrayOfInt.clone();
    this.data = this.bankdata[0];
  }
  
  public DataBufferInt(int[][] paramArrayOfInt, int paramInt, int[] paramArrayOfInt1) {
    super(StateTrackable.State.UNTRACKABLE, 3, paramInt, paramArrayOfInt.length, paramArrayOfInt1);
    this.bankdata = (int[][])paramArrayOfInt.clone();
    this.data = this.bankdata[0];
  }
  
  public int[] getData() {
    this.theTrackable.setUntrackable();
    return this.data;
  }
  
  public int[] getData(int paramInt) {
    this.theTrackable.setUntrackable();
    return this.bankdata[paramInt];
  }
  
  public int[][] getBankData() {
    this.theTrackable.setUntrackable();
    return (int[][])this.bankdata.clone();
  }
  
  public int getElem(int paramInt) { return this.data[paramInt + this.offset]; }
  
  public int getElem(int paramInt1, int paramInt2) { return this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]]; }
  
  public void setElem(int paramInt1, int paramInt2) {
    this.data[paramInt1 + this.offset] = paramInt2;
    this.theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = paramInt3;
    this.theTrackable.markDirty();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\DataBufferInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */