package sun.font;

public final class Type1GlyphMapper extends CharToGlyphMapper {
  Type1Font font;
  
  FontScaler scaler;
  
  public Type1GlyphMapper(Type1Font paramType1Font) {
    this.font = paramType1Font;
    initMapper();
  }
  
  private void initMapper() {
    this.scaler = this.font.getScaler();
    try {
      this.missingGlyph = this.scaler.getMissingGlyphCode();
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      try {
        this.missingGlyph = this.scaler.getMissingGlyphCode();
      } catch (FontScalerException fontScalerException1) {
        this.missingGlyph = 0;
      } 
    } 
  }
  
  public int getNumGlyphs() {
    try {
      return this.scaler.getNumGlyphs();
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getNumGlyphs();
    } 
  }
  
  public int getMissingGlyphCode() { return this.missingGlyph; }
  
  public boolean canDisplay(char paramChar) {
    try {
      return (this.scaler.getGlyphCode(paramChar) != this.missingGlyph);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return canDisplay(paramChar);
    } 
  }
  
  public int charToGlyph(char paramChar) {
    try {
      return this.scaler.getGlyphCode(paramChar);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return charToGlyph(paramChar);
    } 
  }
  
  public int charToGlyph(int paramInt) {
    if (paramInt < 0 || paramInt > 65535)
      return this.missingGlyph; 
    try {
      return this.scaler.getGlyphCode((char)paramInt);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return charToGlyph(paramInt);
    } 
  }
  
  public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt) {
    for (byte b = 0; b < paramInt; b++) {
      char c = paramArrayOfChar[b];
      if (c >= '?' && c <= '?' && b < paramInt - 1) {
        char c1 = paramArrayOfChar[b + true];
        if (c1 >= '?' && c1 <= '?') {
          c = (c - '?') * 'Ѐ' + c1 - '?' + 65536;
          paramArrayOfInt[b + true] = 65535;
        } 
      } 
      paramArrayOfInt[b] = charToGlyph(c);
      if (c >= 65536)
        b++; 
    } 
  }
  
  public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfInt2[b] = charToGlyph(paramArrayOfInt1[b]); 
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
      paramArrayOfInt[b] = charToGlyph(c);
      if (c >= '̀') {
        if (FontUtilities.isComplexCharCode(c))
          return true; 
        if (c >= 65536)
          b++; 
      } 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\Type1GlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */