package sun.font;

public class CompositeFontDescriptor {
  private String faceName;
  
  private int coreComponentCount;
  
  private String[] componentFaceNames;
  
  private String[] componentFileNames;
  
  private int[] exclusionRanges;
  
  private int[] exclusionRangeLimits;
  
  public CompositeFontDescriptor(String paramString, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    this.faceName = paramString;
    this.coreComponentCount = paramInt;
    this.componentFaceNames = paramArrayOfString1;
    this.componentFileNames = paramArrayOfString2;
    this.exclusionRanges = paramArrayOfInt1;
    this.exclusionRangeLimits = paramArrayOfInt2;
  }
  
  public String getFaceName() { return this.faceName; }
  
  public int getCoreComponentCount() { return this.coreComponentCount; }
  
  public String[] getComponentFaceNames() { return this.componentFaceNames; }
  
  public String[] getComponentFileNames() { return this.componentFileNames; }
  
  public int[] getExclusionRanges() { return this.exclusionRanges; }
  
  public int[] getExclusionRangeLimits() { return this.exclusionRangeLimits; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CompositeFontDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */