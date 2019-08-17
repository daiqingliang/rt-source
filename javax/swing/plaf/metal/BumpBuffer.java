package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

class BumpBuffer {
  static final int IMAGE_SIZE = 64;
  
  Image image;
  
  Color topColor;
  
  Color shadowColor;
  
  Color backColor;
  
  private GraphicsConfiguration gc;
  
  public BumpBuffer(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3) {
    this.gc = paramGraphicsConfiguration;
    this.topColor = paramColor1;
    this.shadowColor = paramColor2;
    this.backColor = paramColor3;
    createImage();
    fillBumpBuffer();
  }
  
  public boolean hasSameConfiguration(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3) {
    if (this.gc != null) {
      if (!this.gc.equals(paramGraphicsConfiguration))
        return false; 
    } else if (paramGraphicsConfiguration != null) {
      return false;
    } 
    return (this.topColor.equals(paramColor1) && this.shadowColor.equals(paramColor2) && this.backColor.equals(paramColor3));
  }
  
  public Image getImage() { return this.image; }
  
  private void fillBumpBuffer() {
    Graphics graphics = this.image.getGraphics();
    graphics.setColor(this.backColor);
    graphics.fillRect(0, 0, 64, 64);
    graphics.setColor(this.topColor);
    byte b;
    for (b = 0; b < 64; b += 4) {
      for (byte b1 = 0; b1 < 64; b1 += 4) {
        graphics.drawLine(b, b1, b, b1);
        graphics.drawLine(b + 2, b1 + 2, b + 2, b1 + 2);
      } 
    } 
    graphics.setColor(this.shadowColor);
    for (b = 0; b < 64; b += 4) {
      for (boolean bool = false; bool < 64; bool += true) {
        graphics.drawLine(b + 1, bool + true, b + 1, bool + true);
        graphics.drawLine(b + 3, bool + 3, b + 3, bool + 3);
      } 
    } 
    graphics.dispose();
  }
  
  private void createImage() {
    if (this.gc != null) {
      this.image = this.gc.createCompatibleImage(64, 64, (this.backColor != MetalBumps.ALPHA) ? 1 : 2);
    } else {
      int[] arrayOfInt = { this.backColor.getRGB(), this.topColor.getRGB(), this.shadowColor.getRGB() };
      IndexColorModel indexColorModel = new IndexColorModel(8, 3, arrayOfInt, 0, false, (this.backColor == MetalBumps.ALPHA) ? 0 : -1, 0);
      this.image = new BufferedImage(64, 64, 13, indexColorModel);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\BumpBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */