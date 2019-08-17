package sun.font;

public class CompositeGlyphMapper extends CharToGlyphMapper {
  public static final int SLOTMASK = -16777216;
  
  public static final int GLYPHMASK = 16777215;
  
  public static final int NBLOCKS = 216;
  
  public static final int BLOCKSZ = 256;
  
  public static final int MAXUNICODE = 55296;
  
  CompositeFont font;
  
  CharToGlyphMapper[] slotMappers;
  
  int[][] glyphMaps;
  
  private boolean hasExcludes;
  
  public CompositeGlyphMapper(CompositeFont paramCompositeFont) {
    this.font = paramCompositeFont;
    initMapper();
    this.hasExcludes = (paramCompositeFont.exclusionRanges != null && paramCompositeFont.maxIndices != null);
  }
  
  public final int compositeGlyphCode(int paramInt1, int paramInt2) { return paramInt1 << 24 | paramInt2 & 0xFFFFFF; }
  
  private final void initMapper() {
    if (this.missingGlyph == -1) {
      if (this.glyphMaps == null)
        this.glyphMaps = new int[216][]; 
      this.slotMappers = new CharToGlyphMapper[this.font.numSlots];
      this.missingGlyph = this.font.getSlotFont(0).getMissingGlyphCode();
      this.missingGlyph = compositeGlyphCode(0, this.missingGlyph);
    } 
  }
  
  private int getCachedGlyphCode(int paramInt) {
    int[] arrayOfInt;
    return (paramInt >= 55296) ? -1 : (((arrayOfInt = this.glyphMaps[paramInt >> 8]) == null) ? -1 : arrayOfInt[paramInt & 0xFF]);
  }
  
  private void setCachedGlyphCode(int paramInt1, int paramInt2) {
    if (paramInt1 >= 55296)
      return; 
    int i = paramInt1 >> 8;
    if (this.glyphMaps[i] == null) {
      this.glyphMaps[i] = new int[256];
      for (byte b = 0; b < 'Ā'; b++)
        this.glyphMaps[i][b] = -1; 
    } 
    this.glyphMaps[i][paramInt1 & 0xFF] = paramInt2;
  }
  
  private final CharToGlyphMapper getSlotMapper(int paramInt) {
    CharToGlyphMapper charToGlyphMapper = this.slotMappers[paramInt];
    if (charToGlyphMapper == null) {
      charToGlyphMapper = this.font.getSlotFont(paramInt).getMapper();
      this.slotMappers[paramInt] = charToGlyphMapper;
    } 
    return charToGlyphMapper;
  }
  
  private final int convertToGlyph(int paramInt) {
    for (byte b = 0; b < this.font.numSlots; b++) {
      if (!this.hasExcludes || !this.font.isExcludedChar(b, paramInt)) {
        CharToGlyphMapper charToGlyphMapper = getSlotMapper(b);
        int i = charToGlyphMapper.charToGlyph(paramInt);
        if (i != charToGlyphMapper.getMissingGlyphCode()) {
          i = compositeGlyphCode(b, i);
          setCachedGlyphCode(paramInt, i);
          return i;
        } 
      } 
    } 
    return this.missingGlyph;
  }
  
  public int getNumGlyphs() {
    int i = 0;
    for (byte b = 0; b < 1; b++) {
      CharToGlyphMapper charToGlyphMapper = this.slotMappers[b];
      if (charToGlyphMapper == null) {
        charToGlyphMapper = this.font.getSlotFont(b).getMapper();
        this.slotMappers[b] = charToGlyphMapper;
      } 
      i += charToGlyphMapper.getNumGlyphs();
    } 
    return i;
  }
  
  public int charToGlyph(int paramInt) {
    int i = getCachedGlyphCode(paramInt);
    if (i == -1)
      i = convertToGlyph(paramInt); 
    return i;
  }
  
  public int charToGlyph(int paramInt1, int paramInt2) {
    if (paramInt2 >= 0) {
      CharToGlyphMapper charToGlyphMapper = getSlotMapper(paramInt2);
      int i = charToGlyphMapper.charToGlyph(paramInt1);
      if (i != charToGlyphMapper.getMissingGlyphCode())
        return compositeGlyphCode(paramInt2, i); 
    } 
    return charToGlyph(paramInt1);
  }
  
  public int charToGlyph(char paramChar) {
    int i = getCachedGlyphCode(paramChar);
    if (i == -1)
      i = convertToGlyph(paramChar); 
    return i;
  }
  
  public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt) {
    for (byte b = 0; b < paramInt; b++) {
      char c = paramArrayOfChar[b];
      if (c >= '?' && c <= '?' && b < paramInt - 1) {
        char c1 = paramArrayOfChar[b + true];
        if (c1 >= '?' && c1 <= '?') {
          c = (c - '?') * 'Ѐ' + c1 - '?' + 65536;
          paramArrayOfInt[b + true] = 65535;
        } 
      } 
      int i = paramArrayOfInt[b] = getCachedGlyphCode(c);
      if (i == -1)
        paramArrayOfInt[b] = convertToGlyph(c); 
      if (c >= '̀') {
        if (FontUtilities.isComplexCharCode(c))
          return true; 
        if (c >= 65536)
          b++; 
      } 
    } 
    return false;
  }
  
  public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt) {
    for (byte b = 0; b < paramInt; b++) {
      char c = paramArrayOfChar[b];
      if (c >= '?' && c <= '?' && b < paramInt - 1) {
        char c1 = paramArrayOfChar[b + 1];
        if (c1 >= '?' && c1 <= '?') {
          c = (c - '?') * 'Ѐ' + c1 - '?' + 65536;
          int j = paramArrayOfInt[b] = getCachedGlyphCode(c);
          if (j == -1)
            paramArrayOfInt[b] = convertToGlyph(c); 
          paramArrayOfInt[++b] = 65535;
          continue;
        } 
      } 
      int i = paramArrayOfInt[b] = getCachedGlyphCode(c);
      if (i == -1)
        paramArrayOfInt[b] = convertToGlyph(c); 
      continue;
    } 
  }
  
  public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    for (byte b = 0; b < paramInt; b++) {
      int i = paramArrayOfInt1[b];
      paramArrayOfInt2[b] = getCachedGlyphCode(i);
      if (paramArrayOfInt2[b] == -1)
        paramArrayOfInt2[b] = convertToGlyph(i); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CompositeGlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */