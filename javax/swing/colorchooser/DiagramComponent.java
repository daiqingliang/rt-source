package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

final class DiagramComponent extends JComponent implements MouseListener, MouseMotionListener {
  private final ColorPanel panel;
  
  private final boolean diagram;
  
  private final Insets insets = new Insets(0, 0, 0, 0);
  
  private int width;
  
  private int height;
  
  private int[] array;
  
  private BufferedImage image;
  
  DiagramComponent(ColorPanel paramColorPanel, boolean paramBoolean) {
    this.panel = paramColorPanel;
    this.diagram = paramBoolean;
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  protected void paintComponent(Graphics paramGraphics) {
    getInsets(this.insets);
    this.width = getWidth() - this.insets.left - this.insets.right;
    this.height = getHeight() - this.insets.top - this.insets.bottom;
    boolean bool = (this.image == null || this.width != this.image.getWidth() || this.height != this.image.getHeight()) ? 1 : 0;
    if (bool) {
      int i = this.width * this.height;
      if (this.array == null || this.array.length < i)
        this.array = new int[i]; 
      this.image = new BufferedImage(this.width, this.height, 1);
    } 
    float f1 = 1.0F / (this.width - 1);
    float f2 = 1.0F / (this.height - 1);
    byte b1 = 0;
    float f3 = 0.0F;
    byte b2 = 0;
    while (b2 < this.height) {
      if (this.diagram) {
        float f = 0.0F;
        byte b = 0;
        while (b < this.width) {
          this.array[b1] = this.panel.getColor(f, f3);
          b++;
          f += f1;
          b1++;
        } 
      } else {
        int i = this.panel.getColor(f3);
        byte b = 0;
        while (b < this.width) {
          this.array[b1] = i;
          b++;
          b1++;
        } 
      } 
      b2++;
      f3 += f2;
    } 
    this.image.setRGB(0, 0, this.width, this.height, this.array, 0, this.width);
    paramGraphics.drawImage(this.image, this.insets.left, this.insets.top, this.width, this.height, this);
    if (isEnabled()) {
      this.width--;
      this.height--;
      paramGraphics.setXORMode(Color.WHITE);
      paramGraphics.setColor(Color.BLACK);
      if (this.diagram) {
        int i = getValue(this.panel.getValueX(), this.insets.left, this.width);
        int j = getValue(this.panel.getValueY(), this.insets.top, this.height);
        paramGraphics.drawLine(i - 8, j, i + 8, j);
        paramGraphics.drawLine(i, j - 8, i, j + 8);
      } else {
        int i = getValue(this.panel.getValueZ(), this.insets.top, this.height);
        paramGraphics.drawLine(this.insets.left, i, this.insets.left + this.width, i);
      } 
      paramGraphics.setPaintMode();
    } 
  }
  
  public void mousePressed(MouseEvent paramMouseEvent) { mouseDragged(paramMouseEvent); }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {}
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    if (isEnabled()) {
      float f = getValue(paramMouseEvent.getY(), this.insets.top, this.height);
      if (this.diagram) {
        float f1 = getValue(paramMouseEvent.getX(), this.insets.left, this.width);
        this.panel.setValue(f1, f);
      } else {
        this.panel.setValue(f);
      } 
    } 
  }
  
  private static int getValue(float paramFloat, int paramInt1, int paramInt2) { return paramInt1 + (int)(paramFloat * paramInt2); }
  
  private static float getValue(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 < paramInt1) {
      paramInt1 -= paramInt2;
      return (paramInt1 < paramInt3) ? (paramInt1 / paramInt3) : 1.0F;
    } 
    return 0.0F;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\DiagramComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */