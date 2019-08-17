package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferShort extends DataBuffer {
  short[] data;
  
  short[][] bankdata;
  
  public DataBufferShort(int paramInt) {
    super(StateTrackable.State.STABLE, 2, paramInt);
    this.data = new short[paramInt];
    this.bankdata = new short[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferShort(int paramInt1, int paramInt2) {
    super(StateTrackable.State.STABLE, 2, paramInt1, paramInt2);
    this.bankdata = new short[paramInt2][];
    for (byte b = 0; b < paramInt2; b++)
      this.bankdata[b] = new short[paramInt1]; 
    this.data = this.bankdata[0];
  }
  
  public DataBufferShort(short[] paramArrayOfShort, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt);
    this.data = paramArrayOfShort;
    this.bankdata = new short[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferShort(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt1, 1, paramInt2);
    this.data = paramArrayOfShort;
    this.bankdata = new short[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferShort(short[][] paramArrayOfShort, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt, paramArrayOfShort.length);
    this.bankdata = (short[][])paramArrayOfShort.clone();
    this.data = this.bankdata[0];
  }
  
  public DataBufferShort(short[][] paramArrayOfShort, int paramInt, int[] paramArrayOfInt) {
    super(StateTrackable.State.UNTRACKABLE, 2, paramInt, paramArrayOfShort.length, paramArrayOfInt);
    this.bankdata = (short[][])paramArrayOfShort.clone();
    this.data = this.bankdata[0];
  }
  
  public short[] getData() {
    this.theTrackable.setUntrackable();
    return this.data;
  }
  
  public short[] getData(int paramInt) {
    this.theTrackable.setUntrackable();
    return this.bankdata[paramInt];
  }
  
  public short[][] getBankData() {
    this.theTrackable.setUntrackable();
    return (short[][])this.bankdata.clone();
  }
  
  public int getElem(int paramInt) { return this.data[paramInt + this.offset]; }
  
  public int getElem(int paramInt1, int paramInt2) { return this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]]; }
  
  public void setElem(int paramInt1, int paramInt2) {
    this.data[paramInt1 + this.offset] = (short)paramInt2;
    this.theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = (short)paramInt3;
    this.theTrackable.markDirty();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\DataBufferShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */