package javax.swing.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.Icon;

public class IconView extends View {
  private Icon c;
  
  public IconView(Element paramElement) {
    super(paramElement);
    AttributeSet attributeSet = paramElement.getAttributes();
    this.c = StyleConstants.getIcon(attributeSet);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = paramShape.getBounds();
    this.c.paintIcon(getContainer(), paramGraphics, rectangle.x, rectangle.y);
  }
  
  public float getPreferredSpan(int paramInt) {
    switch (paramInt) {
      case 0:
        return this.c.getIconWidth();
      case 1:
        return this.c.getIconHeight();
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt) {
    switch (paramInt) {
      case 1:
        return 1.0F;
    } 
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    int i = getStartOffset();
    int j = getEndOffset();
    if (paramInt >= i && paramInt <= j) {
      Rectangle rectangle = paramShape.getBounds();
      if (paramInt == j)
        rectangle.x += rectangle.width; 
      rectangle.width = 0;
      return rectangle;
    } 
    throw new BadLocationException(paramInt + " not in range " + i + "," + j, paramInt);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    Rectangle rectangle = (Rectangle)paramShape;
    if (paramFloat1 < (rectangle.x + rectangle.width / 2)) {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    } 
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\IconView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */