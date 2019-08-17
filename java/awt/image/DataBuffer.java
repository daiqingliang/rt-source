package java.awt.image;

import sun.awt.image.SunWritableRaster;
import sun.java2d.StateTrackable;
import sun.java2d.StateTrackableDelegate;

public abstract class DataBuffer {
  public static final int TYPE_BYTE = 0;
  
  public static final int TYPE_USHORT = 1;
  
  public static final int TYPE_SHORT = 2;
  
  public static final int TYPE_INT = 3;
  
  public static final int TYPE_FLOAT = 4;
  
  public static final int TYPE_DOUBLE = 5;
  
  public static final int TYPE_UNDEFINED = 32;
  
  protected int dataType;
  
  protected int banks;
  
  protected int offset;
  
  protected int size;
  
  protected int[] offsets;
  
  StateTrackableDelegate theTrackable;
  
  private static final int[] dataTypeSize = { 8, 16, 16, 32, 32, 64 };
  
  public static int getDataTypeSize(int paramInt) {
    if (paramInt < 0 || paramInt > 5)
      throw new IllegalArgumentException("Unknown data type " + paramInt); 
    return dataTypeSize[paramInt];
  }
  
  protected DataBuffer(int paramInt1, int paramInt2) { this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2); }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2) {
    this.theTrackable = StateTrackableDelegate.createInstance(paramState);
    this.dataType = paramInt1;
    this.banks = 1;
    this.size = paramInt2;
    this.offset = 0;
    this.offsets = new int[1];
  }
  
  protected DataBuffer(int paramInt1, int paramInt2, int paramInt3) { this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2, paramInt3); }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2, int paramInt3) {
    this.theTrackable = StateTrackableDelegate.createInstance(paramState);
    this.dataType = paramInt1;
    this.banks = paramInt3;
    this.size = paramInt2;
    this.offset = 0;
    this.offsets = new int[this.banks];
  }
  
  protected DataBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.theTrackable = StateTrackableDelegate.createInstance(paramState);
    this.dataType = paramInt1;
    this.banks = paramInt3;
    this.size = paramInt2;
    this.offset = paramInt4;
    this.offsets = new int[paramInt3];
    for (byte b = 0; b < paramInt3; b++)
      this.offsets[b] = paramInt4; 
  }
  
  protected DataBuffer(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt) { this(StateTrackable.State.UNTRACKABLE, paramInt1, paramInt2, paramInt3, paramArrayOfInt); }
  
  DataBuffer(StateTrackable.State paramState, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt) {
    if (paramInt3 != paramArrayOfInt.length)
      throw new ArrayIndexOutOfBoundsException("Number of banks does not match number of bank offsets"); 
    this.theTrackable = StateTrackableDelegate.createInstance(paramState);
    this.dataType = paramInt1;
    this.banks = paramInt3;
    this.size = paramInt2;
    this.offset = paramArrayOfInt[0];
    this.offsets = (int[])paramArrayOfInt.clone();
  }
  
  public int getDataType() { return this.dataType; }
  
  public int getSize() { return this.size; }
  
  public int getOffset() { return this.offset; }
  
  public int[] getOffsets() { return (int[])this.offsets.clone(); }
  
  public int getNumBanks() { return this.banks; }
  
  public int getElem(int paramInt) { return getElem(0, paramInt); }
  
  public abstract int getElem(int paramInt1, int paramInt2);
  
  public void setElem(int paramInt1, int paramInt2) { setElem(0, paramInt1, paramInt2); }
  
  public abstract void setElem(int paramInt1, int paramInt2, int paramInt3);
  
  public float getElemFloat(int paramInt) { return getElem(paramInt); }
  
  public float getElemFloat(int paramInt1, int paramInt2) { return getElem(paramInt1, paramInt2); }
  
  public void setElemFloat(int paramInt, float paramFloat) { setElem(paramInt, (int)paramFloat); }
  
  public void setElemFloat(int paramInt1, int paramInt2, float paramFloat) { setElem(paramInt1, paramInt2, (int)paramFloat); }
  
  public double getElemDouble(int paramInt) { return getElem(paramInt); }
  
  public double getElemDouble(int paramInt1, int paramInt2) { return getElem(paramInt1, paramInt2); }
  
  public void setElemDouble(int paramInt, double paramDouble) { setElem(paramInt, (int)paramDouble); }
  
  public void setElemDouble(int paramInt1, int paramInt2, double paramDouble) { setElem(paramInt1, paramInt2, (int)paramDouble); }
  
  static int[] toIntArray(Object paramObject) {
    if (paramObject instanceof int[])
      return (int[])paramObject; 
    if (paramObject == null)
      return null; 
    if (paramObject instanceof short[]) {
      short[] arrayOfShort = (short[])paramObject;
      int[] arrayOfInt = new int[arrayOfShort.length];
      for (byte b = 0; b < arrayOfShort.length; b++)
        arrayOfInt[b] = arrayOfShort[b] & 0xFFFF; 
      return arrayOfInt;
    } 
    if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      int[] arrayOfInt = new int[arrayOfByte.length];
      for (byte b = 0; b < arrayOfByte.length; b++)
        arrayOfInt[b] = 0xFF & arrayOfByte[b]; 
      return arrayOfInt;
    } 
    return null;
  }
  
  static  {
    SunWritableRaster.setDataStealer(new SunWritableRaster.DataStealer() {
          public byte[] getData(DataBufferByte param1DataBufferByte, int param1Int) { return param1DataBufferByte.bankdata[param1Int]; }
          
          public short[] getData(DataBufferUShort param1DataBufferUShort, int param1Int) { return param1DataBufferUShort.bankdata[param1Int]; }
          
          public int[] getData(DataBufferInt param1DataBufferInt, int param1Int) { return param1DataBufferInt.bankdata[param1Int]; }
          
          public StateTrackableDelegate getTrackable(DataBuffer param1DataBuffer) { return param1DataBuffer.theTrackable; }
          
          public void setTrackable(DataBuffer param1DataBuffer, StateTrackableDelegate param1StateTrackableDelegate) { param1DataBuffer.theTrackable = param1StateTrackableDelegate; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\DataBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */