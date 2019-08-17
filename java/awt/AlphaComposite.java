package java.awt;

import java.awt.image.ColorModel;
import sun.java2d.SunCompositeContext;

public final class AlphaComposite implements Composite {
  public static final int CLEAR = 1;
  
  public static final int SRC = 2;
  
  public static final int DST = 9;
  
  public static final int SRC_OVER = 3;
  
  public static final int DST_OVER = 4;
  
  public static final int SRC_IN = 5;
  
  public static final int DST_IN = 6;
  
  public static final int SRC_OUT = 7;
  
  public static final int DST_OUT = 8;
  
  public static final int SRC_ATOP = 10;
  
  public static final int DST_ATOP = 11;
  
  public static final int XOR = 12;
  
  public static final AlphaComposite Clear = new AlphaComposite(1);
  
  public static final AlphaComposite Src = new AlphaComposite(2);
  
  public static final AlphaComposite Dst = new AlphaComposite(9);
  
  public static final AlphaComposite SrcOver = new AlphaComposite(3);
  
  public static final AlphaComposite DstOver = new AlphaComposite(4);
  
  public static final AlphaComposite SrcIn = new AlphaComposite(5);
  
  public static final AlphaComposite DstIn = new AlphaComposite(6);
  
  public static final AlphaComposite SrcOut = new AlphaComposite(7);
  
  public static final AlphaComposite DstOut = new AlphaComposite(8);
  
  public static final AlphaComposite SrcAtop = new AlphaComposite(10);
  
  public static final AlphaComposite DstAtop = new AlphaComposite(11);
  
  public static final AlphaComposite Xor = new AlphaComposite(12);
  
  private static final int MIN_RULE = 1;
  
  private static final int MAX_RULE = 12;
  
  float extraAlpha;
  
  int rule;
  
  private AlphaComposite(int paramInt) { this(paramInt, 1.0F); }
  
  private AlphaComposite(int paramInt, float paramFloat) {
    if (paramInt < 1 || paramInt > 12)
      throw new IllegalArgumentException("unknown composite rule"); 
    if (paramFloat >= 0.0F && paramFloat <= 1.0F) {
      this.rule = paramInt;
      this.extraAlpha = paramFloat;
    } else {
      throw new IllegalArgumentException("alpha value out of range");
    } 
  }
  
  public static AlphaComposite getInstance(int paramInt) {
    switch (paramInt) {
      case 1:
        return Clear;
      case 2:
        return Src;
      case 9:
        return Dst;
      case 3:
        return SrcOver;
      case 4:
        return DstOver;
      case 5:
        return SrcIn;
      case 6:
        return DstIn;
      case 7:
        return SrcOut;
      case 8:
        return DstOut;
      case 10:
        return SrcAtop;
      case 11:
        return DstAtop;
      case 12:
        return Xor;
    } 
    throw new IllegalArgumentException("unknown composite rule");
  }
  
  public static AlphaComposite getInstance(int paramInt, float paramFloat) { return (paramFloat == 1.0F) ? getInstance(paramInt) : new AlphaComposite(paramInt, paramFloat); }
  
  public CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints) { return new SunCompositeContext(this, paramColorModel1, paramColorModel2); }
  
  public float getAlpha() { return this.extraAlpha; }
  
  public int getRule() { return this.rule; }
  
  public AlphaComposite derive(int paramInt) { return (this.rule == paramInt) ? this : getInstance(paramInt, this.extraAlpha); }
  
  public AlphaComposite derive(float paramFloat) { return (this.extraAlpha == paramFloat) ? this : getInstance(this.rule, paramFloat); }
  
  public int hashCode() { return Float.floatToIntBits(this.extraAlpha) * 31 + this.rule; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof AlphaComposite))
      return false; 
    AlphaComposite alphaComposite = (AlphaComposite)paramObject;
    return (this.rule != alphaComposite.rule) ? false : (!(this.extraAlpha != alphaComposite.extraAlpha));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\AlphaComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */