package sun.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public class RepaintArea {
  private static final int MAX_BENEFIT_RATIO = 4;
  
  private static final int HORIZONTAL = 0;
  
  private static final int VERTICAL = 1;
  
  private static final int UPDATE = 2;
  
  private static final int RECT_COUNT = 3;
  
  private Rectangle[] paintRects = new Rectangle[3];
  
  public RepaintArea() {}
  
  private RepaintArea(RepaintArea paramRepaintArea) {
    for (byte b = 0; b < 3; b++)
      this.paintRects[b] = paramRepaintArea.paintRects[b]; 
  }
  
  public void add(Rectangle paramRectangle, int paramInt) {
    if (paramRectangle.isEmpty())
      return; 
    byte b = 2;
    if (paramInt == 800)
      b = (paramRectangle.width > paramRectangle.height) ? 0 : 1; 
    if (this.paintRects[b] != null) {
      this.paintRects[b].add(paramRectangle);
    } else {
      this.paintRects[b] = new Rectangle(paramRectangle);
    } 
  }
  
  private RepaintArea cloneAndReset() {
    RepaintArea repaintArea = new RepaintArea(this);
    for (byte b = 0; b < 3; b++)
      this.paintRects[b] = null; 
    return repaintArea;
  }
  
  public boolean isEmpty() {
    for (byte b = 0; b < 3; b++) {
      if (this.paintRects[b] != null)
        return false; 
    } 
    return true;
  }
  
  public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    for (byte b = 0; b < 3; b++) {
      Rectangle rectangle = this.paintRects[b];
      if (rectangle != null) {
        if (rectangle.x < paramInt1) {
          rectangle.width -= paramInt1 - rectangle.x;
          rectangle.x = paramInt1;
        } 
        if (rectangle.y < paramInt2) {
          rectangle.height -= paramInt2 - rectangle.y;
          rectangle.y = paramInt2;
        } 
        int i = rectangle.x + rectangle.width - paramInt1 - paramInt3;
        if (i > 0)
          rectangle.width -= i; 
        int j = rectangle.y + rectangle.height - paramInt2 - paramInt4;
        if (j > 0)
          rectangle.height -= j; 
        if (rectangle.width <= 0 || rectangle.height <= 0)
          this.paintRects[b] = null; 
      } 
    } 
  }
  
  public void subtract(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Rectangle rectangle = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4);
    for (byte b = 0; b < 3; b++) {
      if (subtract(this.paintRects[b], rectangle) && this.paintRects[b] != null && this.paintRects[b].isEmpty())
        this.paintRects[b] = null; 
    } 
  }
  
  public void paint(Object paramObject, boolean paramBoolean) {
    Component component = (Component)paramObject;
    if (isEmpty())
      return; 
    if (!component.isVisible())
      return; 
    RepaintArea repaintArea;
    if (!(repaintArea = cloneAndReset()).subtract(repaintArea.paintRects[1], repaintArea.paintRects[0]))
      subtract(repaintArea.paintRects[0], repaintArea.paintRects[1]); 
    if (repaintArea.paintRects[false] != null && repaintArea.paintRects[true] != null) {
      Rectangle rectangle = repaintArea.paintRects[0].union(repaintArea.paintRects[1]);
      int i = rectangle.width * rectangle.height;
      int j = i - (repaintArea.paintRects[0]).width * (repaintArea.paintRects[0]).height - (repaintArea.paintRects[1]).width * (repaintArea.paintRects[1]).height;
      if (4 * j < i) {
        repaintArea.paintRects[0] = rectangle;
        repaintArea.paintRects[1] = null;
      } 
    } 
    for (byte b = 0; b < this.paintRects.length; b++) {
      if (repaintArea.paintRects[b] != null && !repaintArea.paintRects[b].isEmpty()) {
        graphics = component.getGraphics();
        if (graphics != null)
          try {
            graphics.setClip(repaintArea.paintRects[b]);
            if (b == 2) {
              updateComponent(component, graphics);
            } else {
              if (paramBoolean)
                graphics.clearRect((repaintArea.paintRects[b]).x, (repaintArea.paintRects[b]).y, (repaintArea.paintRects[b]).width, (repaintArea.paintRects[b]).height); 
              paintComponent(component, graphics);
            } 
          } finally {
            graphics.dispose();
          }  
      } 
    } 
  }
  
  protected void updateComponent(Component paramComponent, Graphics paramGraphics) {
    if (paramComponent != null)
      paramComponent.update(paramGraphics); 
  }
  
  protected void paintComponent(Component paramComponent, Graphics paramGraphics) {
    if (paramComponent != null)
      paramComponent.paint(paramGraphics); 
  }
  
  static boolean subtract(Rectangle paramRectangle1, Rectangle paramRectangle2) {
    if (paramRectangle1 == null || paramRectangle2 == null)
      return true; 
    Rectangle rectangle = paramRectangle1.intersection(paramRectangle2);
    if (rectangle.isEmpty())
      return true; 
    if (paramRectangle1.x == rectangle.x && paramRectangle1.y == rectangle.y) {
      if (paramRectangle1.width == rectangle.width) {
        paramRectangle1.y += rectangle.height;
        paramRectangle1.height -= rectangle.height;
        return true;
      } 
      if (paramRectangle1.height == rectangle.height) {
        paramRectangle1.x += rectangle.width;
        paramRectangle1.width -= rectangle.width;
        return true;
      } 
    } else if (paramRectangle1.x + paramRectangle1.width == rectangle.x + rectangle.width && paramRectangle1.y + paramRectangle1.height == rectangle.y + rectangle.height) {
      if (paramRectangle1.width == rectangle.width) {
        paramRectangle1.height -= rectangle.height;
        return true;
      } 
      if (paramRectangle1.height == rectangle.height) {
        paramRectangle1.width -= rectangle.width;
        return true;
      } 
    } 
    return false;
  }
  
  public String toString() { return super.toString() + "[ horizontal=" + this.paintRects[0] + " vertical=" + this.paintRects[1] + " update=" + this.paintRects[2] + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\RepaintArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */