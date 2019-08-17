package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.Element;

public class ListView extends BlockView {
  private StyleSheet.ListPainter listPainter;
  
  public ListView(Element paramElement) { super(paramElement, 1); }
  
  public float getAlignment(int paramInt) {
    switch (paramInt) {
      case 0:
        return 0.5F;
      case 1:
        return 0.5F;
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    super.paint(paramGraphics, paramShape);
    Rectangle rectangle1 = paramShape.getBounds();
    Rectangle rectangle2 = paramGraphics.getClipBounds();
    if (rectangle2.x + rectangle2.width < rectangle1.x + getLeftInset()) {
      Rectangle rectangle = rectangle1;
      rectangle1 = getInsideAllocation(paramShape);
      int i = getViewCount();
      int j = rectangle2.y + rectangle2.height;
      byte b = 0;
      while (b < i) {
        rectangle.setBounds(rectangle1);
        childAllocation(b, rectangle);
        if (rectangle.y < j) {
          if (rectangle.y + rectangle.height >= rectangle2.y)
            this.listPainter.paint(paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this, b); 
          b++;
        } 
      } 
    } 
  }
  
  protected void paintChild(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    this.listPainter.paint(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this, paramInt);
    super.paintChild(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void setPropertiesFromAttributes() {
    super.setPropertiesFromAttributes();
    this.listPainter = getStyleSheet().getListPainter(getAttributes());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\ListView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */