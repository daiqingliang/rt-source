package javax.swing.plaf.nimbus;

import java.awt.Color;

abstract class ShadowEffect extends Effect {
  protected Color color = Color.BLACK;
  
  protected float opacity = 0.75F;
  
  protected int angle = 135;
  
  protected int distance = 5;
  
  protected int spread = 0;
  
  protected int size = 5;
  
  Color getColor() { return this.color; }
  
  void setColor(Color paramColor) {
    Color color1 = getColor();
    this.color = paramColor;
  }
  
  float getOpacity() { return this.opacity; }
  
  void setOpacity(float paramFloat) {
    float f = getOpacity();
    this.opacity = paramFloat;
  }
  
  int getAngle() { return this.angle; }
  
  void setAngle(int paramInt) {
    int i = getAngle();
    this.angle = paramInt;
  }
  
  int getDistance() { return this.distance; }
  
  void setDistance(int paramInt) {
    int i = getDistance();
    this.distance = paramInt;
  }
  
  int getSpread() { return this.spread; }
  
  void setSpread(int paramInt) {
    int i = getSpread();
    this.spread = paramInt;
  }
  
  int getSize() { return this.size; }
  
  void setSize(int paramInt) {
    int i = getSize();
    this.size = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ShadowEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */