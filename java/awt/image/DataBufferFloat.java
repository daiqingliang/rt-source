package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferFloat extends DataBuffer {
  float[][] bankdata;
  
  float[] data;
  
  public DataBufferFloat(int paramInt) {
    super(StateTrackable.State.STABLE, 4, paramInt);
    this.data = new float[paramInt];
    this.bankdata = new float[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferFloat(int paramInt1, int paramInt2) {
    super(StateTrackable.State.STABLE, 4, paramInt1, paramInt2);
    this.bankdata = new float[paramInt2][];
    for (byte b = 0; b < paramInt2; b++)
      this.bankdata[b] = new float[paramInt1]; 
    this.data = this.bankdata[0];
  }
  
  public DataBufferFloat(float[] paramArrayOfFloat, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt);
    this.data = paramArrayOfFloat;
    this.bankdata = new float[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferFloat(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt1, 1, paramInt2);
    this.data = paramArrayOfFloat;
    this.bankdata = new float[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferFloat(float[][] paramArrayOfFloat, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt, paramArrayOfFloat.length);
    this.bankdata = (float[][])paramArrayOfFloat.clone();
    this.data = this.bankdata[0];
  }
  
  public DataBufferFloat(float[][] paramArrayOfFloat, int paramInt, int[] paramArrayOfInt) {
    super(StateTrackable.State.UNTRACKABLE, 4, paramInt, paramArrayOfFloat.length, paramArrayOfInt);
    this.bankdata = (float[][])paramArrayOfFloat.clone();
    this.data = this.bankdata[0];
  }
  
  public float[] getData() {
    this.theTrackable.setUntrackable();
    return this.data;
  }
  
  public float[] getData(int paramInt) {
    this.theTrackable.setUntrackable();
    return this.bankdata[paramInt];
  }
  
  public float[][] getBankData() {
    this.theTrackable.setUntrackable();
    return (float[][])this.bankdata.clone();
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
  
  public float getElemFloat(int paramInt) { return this.data[paramInt + this.offset]; }
  
  public float getElemFloat(int paramInt1, int paramInt2) { return this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]]; }
  
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
    this.data[paramInt + this.offset] = (float)paramDouble;
    this.theTrackable.markDirty();
  }
  
  public void setElemDouble(int paramInt1, int paramInt2, double paramDouble) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = (float)paramDouble;
    this.theTrackable.markDirty();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\DataBufferFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */