package java.awt.image;

public class ByteLookupTable extends LookupTable {
  byte[][] data;
  
  public ByteLookupTable(int paramInt, byte[][] paramArrayOfByte) {
    super(paramInt, paramArrayOfByte.length);
    this.numComponents = paramArrayOfByte.length;
    this.numEntries = paramArrayOfByte[0].length;
    this.data = new byte[this.numComponents][];
    for (byte b = 0; b < this.numComponents; b++)
      this.data[b] = paramArrayOfByte[b]; 
  }
  
  public ByteLookupTable(int paramInt, byte[] paramArrayOfByte) {
    super(paramInt, paramArrayOfByte.length);
    this.numComponents = 1;
    this.numEntries = paramArrayOfByte.length;
    this.data = new byte[1][];
    this.data[0] = paramArrayOfByte;
  }
  
  public final byte[][] getTable() { return this.data; }
  
  public int[] lookupPixel(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (paramArrayOfInt2 == null)
      paramArrayOfInt2 = new int[paramArrayOfInt1.length]; 
    if (this.numComponents == 1) {
      for (byte b = 0; b < paramArrayOfInt1.length; b++) {
        int i = paramArrayOfInt1[b] - this.offset;
        if (i < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfInt2[b] = this.data[0][i];
      } 
    } else {
      for (byte b = 0; b < paramArrayOfInt1.length; b++) {
        int i = paramArrayOfInt1[b] - this.offset;
        if (i < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfInt2[b] = this.data[b][i];
      } 
    } 
    return paramArrayOfInt2;
  }
  
  public byte[] lookupPixel(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    if (paramArrayOfByte2 == null)
      paramArrayOfByte2 = new byte[paramArrayOfByte1.length]; 
    if (this.numComponents == 1) {
      for (byte b = 0; b < paramArrayOfByte1.length; b++) {
        byte b1 = (paramArrayOfByte1[b] & 0xFF) - this.offset;
        if (b1 < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfByte2[b] = this.data[0][b1];
      } 
    } else {
      for (byte b = 0; b < paramArrayOfByte1.length; b++) {
        byte b1 = (paramArrayOfByte1[b] & 0xFF) - this.offset;
        if (b1 < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfByte2[b] = this.data[b][b1];
      } 
    } 
    return paramArrayOfByte2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ByteLookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */