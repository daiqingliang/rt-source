package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.text.Bidi;

public final class TextLabelFactory {
  private final FontRenderContext frc;
  
  private final char[] text;
  
  private final Bidi bidi;
  
  private Bidi lineBidi;
  
  private final int flags;
  
  private int lineStart;
  
  private int lineLimit;
  
  public TextLabelFactory(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, Bidi paramBidi, int paramInt) {
    this.frc = paramFontRenderContext;
    this.text = (char[])paramArrayOfChar.clone();
    this.bidi = paramBidi;
    this.flags = paramInt;
    this.lineBidi = paramBidi;
    this.lineStart = 0;
    this.lineLimit = paramArrayOfChar.length;
  }
  
  public FontRenderContext getFontRenderContext() { return this.frc; }
  
  public Bidi getLineBidi() { return this.lineBidi; }
  
  public void setLineContext(int paramInt1, int paramInt2) {
    this.lineStart = paramInt1;
    this.lineLimit = paramInt2;
    if (this.bidi != null)
      this.lineBidi = this.bidi.createLineBidi(paramInt1, paramInt2); 
  }
  
  public ExtendedTextLabel createExtended(Font paramFont, CoreMetrics paramCoreMetrics, Decoration paramDecoration, int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2 || paramInt1 < this.lineStart || paramInt2 > this.lineLimit)
      throw new IllegalArgumentException("bad start: " + paramInt1 + " or limit: " + paramInt2); 
    byte b = (this.lineBidi == null) ? 0 : this.lineBidi.getLevelAt(paramInt1 - this.lineStart);
    boolean bool = (this.lineBidi == null || this.lineBidi.baseIsLeftToRight()) ? 0 : 1;
    int i = this.flags & 0xFFFFFFF6;
    if (b & true)
      i |= 0x1; 
    if (bool & true)
      i |= 0x8; 
    StandardTextSource standardTextSource = new StandardTextSource(this.text, paramInt1, paramInt2 - paramInt1, this.lineStart, this.lineLimit - this.lineStart, b, i, paramFont, this.frc, paramCoreMetrics);
    return new ExtendedTextSourceLabel(standardTextSource, paramDecoration);
  }
  
  public TextLabel createSimple(Font paramFont, CoreMetrics paramCoreMetrics, int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2 || paramInt1 < this.lineStart || paramInt2 > this.lineLimit)
      throw new IllegalArgumentException("bad start: " + paramInt1 + " or limit: " + paramInt2); 
    byte b = (this.lineBidi == null) ? 0 : this.lineBidi.getLevelAt(paramInt1 - this.lineStart);
    boolean bool = (this.lineBidi == null || this.lineBidi.baseIsLeftToRight()) ? 0 : 1;
    int i = this.flags & 0xFFFFFFF6;
    if (b & true)
      i |= 0x1; 
    if (bool & true)
      i |= 0x8; 
    StandardTextSource standardTextSource = new StandardTextSource(this.text, paramInt1, paramInt2 - paramInt1, this.lineStart, this.lineLimit - this.lineStart, b, i, paramFont, this.frc, paramCoreMetrics);
    return new TextSourceLabel(standardTextSource);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\TextLabelFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */