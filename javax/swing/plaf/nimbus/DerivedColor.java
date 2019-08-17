package javax.swing.plaf.nimbus;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

class DerivedColor extends Color {
  private final String uiDefaultParentName;
  
  private final float hOffset;
  
  private final float sOffset;
  
  private final float bOffset;
  
  private final int aOffset;
  
  private int argbValue;
  
  DerivedColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt) {
    super(0);
    this.uiDefaultParentName = paramString;
    this.hOffset = paramFloat1;
    this.sOffset = paramFloat2;
    this.bOffset = paramFloat3;
    this.aOffset = paramInt;
  }
  
  public String getUiDefaultParentName() { return this.uiDefaultParentName; }
  
  public float getHueOffset() { return this.hOffset; }
  
  public float getSaturationOffset() { return this.sOffset; }
  
  public float getBrightnessOffset() { return this.bOffset; }
  
  public int getAlphaOffset() { return this.aOffset; }
  
  public void rederiveColor() {
    Color color = UIManager.getColor(this.uiDefaultParentName);
    if (color != null) {
      float[] arrayOfFloat = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
      arrayOfFloat[0] = clamp(arrayOfFloat[0] + this.hOffset);
      arrayOfFloat[1] = clamp(arrayOfFloat[1] + this.sOffset);
      arrayOfFloat[2] = clamp(arrayOfFloat[2] + this.bOffset);
      int i = clamp(color.getAlpha() + this.aOffset);
      this.argbValue = Color.HSBtoRGB(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2]) & 0xFFFFFF | i << 24;
    } else {
      float[] arrayOfFloat = new float[3];
      arrayOfFloat[0] = clamp(this.hOffset);
      arrayOfFloat[1] = clamp(this.sOffset);
      arrayOfFloat[2] = clamp(this.bOffset);
      int i = clamp(this.aOffset);
      this.argbValue = Color.HSBtoRGB(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2]) & 0xFFFFFF | i << 24;
    } 
  }
  
  public int getRGB() { return this.argbValue; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DerivedColor))
      return false; 
    DerivedColor derivedColor = (DerivedColor)paramObject;
    return (this.aOffset != derivedColor.aOffset) ? false : ((Float.compare(derivedColor.bOffset, this.bOffset) != 0) ? false : ((Float.compare(derivedColor.hOffset, this.hOffset) != 0) ? false : ((Float.compare(derivedColor.sOffset, this.sOffset) != 0) ? false : (!!this.uiDefaultParentName.equals(derivedColor.uiDefaultParentName)))));
  }
  
  public int hashCode() {
    null = this.uiDefaultParentName.hashCode();
    null = ((31 * null) + this.hOffset != 0.0F) ? Float.floatToIntBits(this.hOffset) : 0;
    null = ((31 * null) + this.sOffset != 0.0F) ? Float.floatToIntBits(this.sOffset) : 0;
    null = ((31 * null) + this.bOffset != 0.0F) ? Float.floatToIntBits(this.bOffset) : 0;
    return 31 * null + this.aOffset;
  }
  
  private float clamp(float paramFloat) {
    if (paramFloat < 0.0F) {
      paramFloat = 0.0F;
    } else if (paramFloat > 1.0F) {
      paramFloat = 1.0F;
    } 
    return paramFloat;
  }
  
  private int clamp(int paramInt) {
    if (paramInt < 0) {
      paramInt = 0;
    } else if (paramInt > 255) {
      paramInt = 255;
    } 
    return paramInt;
  }
  
  public String toString() {
    Color color = UIManager.getColor(this.uiDefaultParentName);
    String str = "DerivedColor(color=" + getRed() + "," + getGreen() + "," + getBlue() + " parent=" + this.uiDefaultParentName + " offsets=" + getHueOffset() + "," + getSaturationOffset() + "," + getBrightnessOffset() + "," + getAlphaOffset();
    return (color == null) ? str : (str + " pColor=" + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
  }
  
  static class UIResource extends DerivedColor implements UIResource {
    UIResource(String param1String, float param1Float1, float param1Float2, float param1Float3, int param1Int) { super(param1String, param1Float1, param1Float2, param1Float3, param1Int); }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof UIResource && super.equals(param1Object)); }
    
    public int hashCode() { return super.hashCode() + 7; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\DerivedColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */