package java.awt.image;

public class ShortLookupTable extends LookupTable {
  short[][] data;
  
  public ShortLookupTable(int paramInt, short[][] paramArrayOfShort) {
    super(paramInt, paramArrayOfShort.length);
    this.numComponents = paramArrayOfShort.length;
    this.numEntries = paramArrayOfShort[0].length;
    this.data = new short[this.numComponents][];
    for (byte b = 0; b < this.numComponents; b++)
      this.data[b] = paramArrayOfShort[b]; 
  }
  
  public ShortLookupTable(int paramInt, short[] paramArrayOfShort) {
    super(paramInt, paramArrayOfShort.length);
    this.numComponents = 1;
    this.numEntries = paramArrayOfShort.length;
    this.data = new short[1][];
    this.data[0] = paramArrayOfShort;
  }
  
  public final short[][] getTable() { return this.data; }
  
  public int[] lookupPixel(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (paramArrayOfInt2 == null)
      paramArrayOfInt2 = new int[paramArrayOfInt1.length]; 
    if (this.numComponents == 1) {
      for (byte b = 0; b < paramArrayOfInt1.length; b++) {
        int i = (paramArrayOfInt1[b] & 0xFFFF) - this.offset;
        if (i < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfInt2[b] = this.data[0][i];
      } 
    } else {
      for (byte b = 0; b < paramArrayOfInt1.length; b++) {
        int i = (paramArrayOfInt1[b] & 0xFFFF) - this.offset;
        if (i < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfInt2[b] = this.data[b][i];
      } 
    } 
    return paramArrayOfInt2;
  }
  
  public short[] lookupPixel(short[] paramArrayOfShort1, short[] paramArrayOfShort2) {
    if (paramArrayOfShort2 == null)
      paramArrayOfShort2 = new short[paramArrayOfShort1.length]; 
    if (this.numComponents == 1) {
      for (byte b = 0; b < paramArrayOfShort1.length; b++) {
        short s = (paramArrayOfShort1[b] & 0xFFFF) - this.offset;
        if (s < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfShort2[b] = this.data[0][s];
      } 
    } else {
      for (byte b = 0; b < paramArrayOfShort1.length; b++) {
        short s = (paramArrayOfShort1[b] & 0xFFFF) - this.offset;
        if (s < 0)
          throw new ArrayIndexOutOfBoundsException("src[" + b + "]-offset is less than zero"); 
        paramArrayOfShort2[b] = this.data[b][s];
      } 
    } 
    return paramArrayOfShort2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ShortLookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */