package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferDouble extends DataBuffer {
  double[][] bankdata;
  
  double[] data;
  
  public DataBufferDouble(int paramInt) {
    super(StateTrackable.State.STABLE, 5, paramInt);
    this.data = new double[paramInt];
    this.bankdata = new double[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferDouble(int paramInt1, int paramInt2) {
    super(StateTrackable.State.STABLE, 5, paramInt1, paramInt2);
    this.bankdata = new double[paramInt2][];
    for (byte b = 0; b < paramInt2; b++)
      this.bankdata[b] = new double[paramInt1]; 
    this.data = this.bankdata[0];
  }
  
  public DataBufferDouble(double[] paramArrayOfDouble, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt);
    this.data = paramArrayOfDouble;
    this.bankdata = new double[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferDouble(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt1, 1, paramInt2);
    this.data = paramArrayOfDouble;
    this.bankdata = new double[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferDouble(double[][] paramArrayOfDouble, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt, paramArrayOfDouble.length);
    this.bankdata = (double[][])paramArrayOfDouble.clone();
    this.data = this.bankdata[0];
  }
  
  public DataBufferDouble(double[][] paramArrayOfDouble, int paramInt, int[] paramArrayOfInt) {
    super(StateTrackable.State.UNTRACKABLE, 5, paramInt, paramArrayOfDouble.length, paramArrayOfInt);
    this.bankdata = (double[][])paramArrayOfDouble.clone();
    this.data = this.bankdata[0];
  }
  
  public double[] getData() {
    this.theTrackable.setUntrackable();
    return this.data;
  }
  
  public double[] getData(int paramInt) {
    this.theTrackable.setUntrackable();
    return this.bankdata[paramInt];
  }
  
  public double[][] getBankData() {
    this.theTrackable.setUntrackable();
    return (double[][])this.bankdata.clone();
  }
  
  public int getElem(int paramInt) { return (int)this.data[paramInt + this.offset]; }
  
  public int getElem(int paramInt1, int paramInt2) { return (int)this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]]; }
  
  public void setElem(int paramInt1, int paramInt2) {
    this.data[paramInt1 + this.offset] = paramInt2;
    this.theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = paramInt3;
    this.theTrackable.markDirty();
  }
  
  public float getElemFloat(int paramInt) { return (float)this.data[paramInt + this.offset]; }
  
  public float getElemFloat(int paramInt1, int paramInt2) { return (float)this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]]; }
  
  public void setElemFloat(int paramInt, float paramFloat) {
    this.data[paramInt + this.offset] = paramFloat;
    this.theTrackable.markDirty();
  }
  
  public void setElemFloat(int paramInt1, int paramInt2, float paramFloat) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = paramFloat;
    this.theTrackable.markDirty();
  }
  
  public double getElemDouble(int paramInt) { return this.data[paramInt + this.offset]; }
  
  public double getElemDouble(int paramInt1, int paramInt2) { return this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]]; }
  
  public void setElemDouble(int paramInt, double paramDouble) {
    this.data[paramInt + this.offset] = paramDouble;
    this.theTrackable.markDirty();
  }
  
  public void setElemDouble(int paramInt1, int paramInt2, double paramDouble) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = paramDouble;
    this.theTrackable.markDirty();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\DataBufferDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */