package java.awt.image;

public abstract class LookupTable {
  int numComponents;
  
  int offset;
  
  int numEntries;
  
  protected LookupTable(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Offset must be greater than 0"); 
    if (paramInt2 < 1)
      throw new IllegalArgumentException("Number of components must  be at least 1"); 
    this.numComponents = paramInt2;
    this.offset = paramInt1;
  }
  
  public int getNumComponents() { return this.numComponents; }
  
  public int getOffset() { return this.offset; }
  
  public abstract int[] lookupPixel(int[] paramArrayOfInt1, int[] paramArrayOfInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\LookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */