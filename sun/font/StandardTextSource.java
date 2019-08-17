package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

final class StandardTextSource extends TextSource {
  private final char[] chars;
  
  private final int start;
  
  private final int len;
  
  private final int cstart;
  
  private final int clen;
  
  private final int level;
  
  private final int flags;
  
  private final Font font;
  
  private final FontRenderContext frc;
  
  private final CoreMetrics cm;
  
  StandardTextSource(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Font paramFont, FontRenderContext paramFontRenderContext, CoreMetrics paramCoreMetrics) {
    if (paramArrayOfChar == null)
      throw new IllegalArgumentException("bad chars: null"); 
    if (paramInt3 < 0)
      throw new IllegalArgumentException("bad cstart: " + paramInt3); 
    if (paramInt1 < paramInt3)
      throw new IllegalArgumentException("bad start: " + paramInt1 + " for cstart: " + paramInt3); 
    if (paramInt4 < 0)
      throw new IllegalArgumentException("bad clen: " + paramInt4); 
    if (paramInt3 + paramInt4 > paramArrayOfChar.length)
      throw new IllegalArgumentException("bad clen: " + paramInt4 + " cstart: " + paramInt3 + " for array len: " + paramArrayOfChar.length); 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("bad len: " + paramInt2); 
    if (paramInt1 + paramInt2 > paramInt3 + paramInt4)
      throw new IllegalArgumentException("bad len: " + paramInt2 + " start: " + paramInt1 + " for cstart: " + paramInt3 + " clen: " + paramInt4); 
    if (paramFont == null)
      throw new IllegalArgumentException("bad font: null"); 
    if (paramFontRenderContext == null)
      throw new IllegalArgumentException("bad frc: null"); 
    this.chars = paramArrayOfChar;
    this.start = paramInt1;
    this.len = paramInt2;
    this.cstart = paramInt3;
    this.clen = paramInt4;
    this.level = paramInt5;
    this.flags = paramInt6;
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    if (paramCoreMetrics != null) {
      this.cm = paramCoreMetrics;
    } else {
      LineMetrics lineMetrics = paramFont.getLineMetrics(paramArrayOfChar, paramInt3, paramInt4, paramFontRenderContext);
      this.cm = ((FontLineMetrics)lineMetrics).cm;
    } 
  }
  
  public char[] getChars() { return this.chars; }
  
  public int getStart() { return this.start; }
  
  public int getLength() { return this.len; }
  
  public int getContextStart() { return this.cstart; }
  
  public int getContextLength() { return this.clen; }
  
  public int getLayoutFlags() { return this.flags; }
  
  public int getBidiLevel() { return this.level; }
  
  public Font getFont() { return this.font; }
  
  public FontRenderContext getFRC() { return this.frc; }
  
  public CoreMetrics getCoreMetrics() { return this.cm; }
  
  public TextSource getSubSource(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > this.len)
      throw new IllegalArgumentException("bad start (" + paramInt1 + ") or length (" + paramInt2 + ")"); 
    int i = this.level;
    if (paramInt3 != 2) {
      boolean bool = ((this.flags & 0x8) == 0) ? 1 : 0;
      if ((paramInt3 != 0 || !bool) && (paramInt3 != 1 || bool))
        throw new IllegalArgumentException("direction flag is invalid"); 
      i = bool ? 0 : 1;
    } 
    return new StandardTextSource(this.chars, this.start + paramInt1, paramInt2, this.cstart, this.clen, i, this.flags, this.font, this.frc, this.cm);
  }
  
  public String toString() { return toString(true); }
  
  public String toString(boolean paramBoolean) {
    int j;
    int i;
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append("[start:");
    stringBuffer.append(this.start);
    stringBuffer.append(", len:");
    stringBuffer.append(this.len);
    stringBuffer.append(", cstart:");
    stringBuffer.append(this.cstart);
    stringBuffer.append(", clen:");
    stringBuffer.append(this.clen);
    stringBuffer.append(", chars:\"");
    if (paramBoolean == true) {
      i = this.cstart;
      j = this.cstart + this.clen;
    } else {
      i = this.start;
      j = this.start + this.len;
    } 
    for (int k = i; k < j; k++) {
      if (k > i)
        stringBuffer.append(" "); 
      stringBuffer.append(Integer.toHexString(this.chars[k]));
    } 
    stringBuffer.append("\"");
    stringBuffer.append(", level:");
    stringBuffer.append(this.level);
    stringBuffer.append(", flags:");
    stringBuffer.append(this.flags);
    stringBuffer.append(", font:");
    stringBuffer.append(this.font);
    stringBuffer.append(", frc:");
    stringBuffer.append(this.frc);
    stringBuffer.append(", cm:");
    stringBuffer.append(this.cm);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\StandardTextSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */