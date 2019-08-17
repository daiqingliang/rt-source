package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import sun.awt.SunHints;

public class FontStrikeDesc {
  static final int AA_ON = 16;
  
  static final int AA_LCD_H = 32;
  
  static final int AA_LCD_V = 64;
  
  static final int FRAC_METRICS_ON = 256;
  
  static final int FRAC_METRICS_SP = 512;
  
  AffineTransform devTx;
  
  AffineTransform glyphTx;
  
  int style;
  
  int aaHint;
  
  int fmHint;
  
  private int hashCode;
  
  private int valuemask;
  
  public int hashCode() {
    if (this.hashCode == 0)
      this.hashCode = this.glyphTx.hashCode() + this.devTx.hashCode() + this.valuemask; 
    return this.hashCode;
  }
  
  public boolean equals(Object paramObject) {
    try {
      FontStrikeDesc fontStrikeDesc = (FontStrikeDesc)paramObject;
      return (fontStrikeDesc.valuemask == this.valuemask && fontStrikeDesc.glyphTx.equals(this.glyphTx) && fontStrikeDesc.devTx.equals(this.devTx));
    } catch (Exception exception) {
      return false;
    } 
  }
  
  FontStrikeDesc() {}
  
  public static int getAAHintIntVal(Object paramObject, Font2D paramFont2D, int paramInt) { return (paramObject == SunHints.VALUE_TEXT_ANTIALIAS_OFF || paramObject == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT) ? 1 : ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_ON) ? 2 : ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_GASP) ? (paramFont2D.useAAForPtSize(paramInt) ? 2 : 1) : ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) ? 4 : ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB || paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR) ? 6 : 1)))); }
  
  public static int getAAHintIntVal(Font2D paramFont2D, Font paramFont, FontRenderContext paramFontRenderContext) {
    Object object = paramFontRenderContext.getAntiAliasingHint();
    if (object == SunHints.VALUE_TEXT_ANTIALIAS_OFF || object == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT)
      return 1; 
    if (object == SunHints.VALUE_TEXT_ANTIALIAS_ON)
      return 2; 
    if (object == SunHints.VALUE_TEXT_ANTIALIAS_GASP) {
      int i;
      AffineTransform affineTransform = paramFontRenderContext.getTransform();
      if (affineTransform.isIdentity() && !paramFont.isTransformed()) {
        i = paramFont.getSize();
      } else {
        float f = paramFont.getSize2D();
        if (affineTransform.isIdentity()) {
          affineTransform = paramFont.getTransform();
          affineTransform.scale(f, f);
        } else {
          affineTransform.scale(f, f);
          if (paramFont.isTransformed())
            affineTransform.concatenate(paramFont.getTransform()); 
        } 
        double d1 = affineTransform.getShearX();
        double d2 = affineTransform.getScaleY();
        if (d1 != 0.0D)
          d2 = Math.sqrt(d1 * d1 + d2 * d2); 
        i = (int)(Math.abs(d2) + 0.5D);
      } 
      return paramFont2D.useAAForPtSize(i) ? 2 : 1;
    } 
    return (object == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || object == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) ? 4 : ((object == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB || object == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR) ? 6 : 1);
  }
  
  public static int getFMHintIntVal(Object paramObject) { return (paramObject == SunHints.VALUE_FRACTIONALMETRICS_OFF || paramObject == SunHints.VALUE_FRACTIONALMETRICS_DEFAULT) ? 1 : 2; }
  
  public FontStrikeDesc(AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2, int paramInt3) {
    this.devTx = paramAffineTransform1;
    this.glyphTx = paramAffineTransform2;
    this.style = paramInt1;
    this.aaHint = paramInt2;
    this.fmHint = paramInt3;
    this.valuemask = paramInt1;
    switch (paramInt2) {
      case 2:
        this.valuemask |= 0x10;
        break;
      case 4:
      case 5:
        this.valuemask |= 0x20;
        break;
      case 6:
      case 7:
        this.valuemask |= 0x40;
        break;
    } 
    if (paramInt3 == 2)
      this.valuemask |= 0x100; 
  }
  
  FontStrikeDesc(FontStrikeDesc paramFontStrikeDesc) {
    this.devTx = paramFontStrikeDesc.devTx;
    this.glyphTx = (AffineTransform)paramFontStrikeDesc.glyphTx.clone();
    this.style = paramFontStrikeDesc.style;
    this.aaHint = paramFontStrikeDesc.aaHint;
    this.fmHint = paramFontStrikeDesc.fmHint;
    this.hashCode = paramFontStrikeDesc.hashCode;
    this.valuemask = paramFontStrikeDesc.valuemask;
  }
  
  public String toString() { return "FontStrikeDesc: Style=" + this.style + " AA=" + this.aaHint + " FM=" + this.fmHint + " devTx=" + this.devTx + " devTx.FontTx.ptSize=" + this.glyphTx; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontStrikeDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */