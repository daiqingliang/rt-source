package sun.font;

public abstract class CharToGlyphMapper {
  public static final int HI_SURROGATE_START = 55296;
  
  public static final int HI_SURROGATE_END = 56319;
  
  public static final int LO_SURROGATE_START = 56320;
  
  public static final int LO_SURROGATE_END = 57343;
  
  public static final int UNINITIALIZED_GLYPH = -1;
  
  public static final int INVISIBLE_GLYPH_ID = 65535;
  
  public static final int INVISIBLE_GLYPHS = 65534;
  
  protected int missingGlyph = -1;
  
  public int getMissingGlyphCode() { return this.missingGlyph; }
  
  public boolean canDisplay(char paramChar) {
    int i = charToGlyph(paramChar);
    return (i != this.missingGlyph);
  }
  
  public boolean canDisplay(int paramInt) {
    int i = charToGlyph(paramInt);
    return (i != this.missingGlyph);
  }
  
  public int charToGlyph(char paramChar) {
    char[] arrayOfChar = new char[1];
    int[] arrayOfInt = new int[1];
    arrayOfChar[0] = paramChar;
    charsToGlyphs(1, arrayOfChar, arrayOfInt);
    return arrayOfInt[0];
  }
  
  public int charToGlyph(int paramInt) {
    int[] arrayOfInt1 = new int[1];
    int[] arrayOfInt2 = new int[1];
    arrayOfInt1[0] = paramInt;
    charsToGlyphs(1, arrayOfInt1, arrayOfInt2);
    return arrayOfInt2[0];
  }
  
  public abstract int getNumGlyphs();
  
  public abstract void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt);
  
  public abstract boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt);
  
  public abstract void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CharToGlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */