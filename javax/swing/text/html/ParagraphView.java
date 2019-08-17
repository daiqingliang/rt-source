package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;

public class ParagraphView extends ParagraphView {
  private AttributeSet attr;
  
  private StyleSheet.BoxPainter painter;
  
  private CSS.LengthValue cssWidth;
  
  private CSS.LengthValue cssHeight;
  
  public ParagraphView(Element paramElement) { super(paramElement); }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (paramView != null)
      setPropertiesFromAttributes(); 
  }
  
  public AttributeSet getAttributes() {
    if (this.attr == null) {
      StyleSheet styleSheet = getStyleSheet();
      this.attr = styleSheet.getViewAttributes(this);
    } 
    return this.attr;
  }
  
  protected void setPropertiesFromAttributes() {
    StyleSheet styleSheet = getStyleSheet();
    this.attr = styleSheet.getViewAttributes(this);
    this.painter = styleSheet.getBoxPainter(this.attr);
    if (this.attr != null) {
      super.setPropertiesFromAttributes();
      setInsets((short)(int)this.painter.getInset(1, this), (short)(int)this.painter.getInset(2, this), (short)(int)this.painter.getInset(3, this), (short)(int)this.painter.getInset(4, this));
      Object object = this.attr.getAttribute(CSS.Attribute.TEXT_ALIGN);
      if (object != null) {
        String str = object.toString();
        if (str.equals("left")) {
          setJustification(0);
        } else if (str.equals("center")) {
          setJustification(1);
        } else if (str.equals("right")) {
          setJustification(2);
        } else if (str.equals("justify")) {
          setJustification(3);
        } 
      } 
      this.cssWidth = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.WIDTH);
      this.cssHeight = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.HEIGHT);
    } 
  }
  
  protected StyleSheet getStyleSheet() {
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    return hTMLDocument.getStyleSheet();
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    paramSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
    if (BlockView.spanSetFromAttributes(paramInt, paramSizeRequirements, this.cssWidth, this.cssHeight)) {
      short s = (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset());
      paramSizeRequirements.minimum -= s;
      paramSizeRequirements.preferred -= s;
      paramSizeRequirements.maximum -= s;
    } 
    return paramSizeRequirements;
  }
  
  public boolean isVisible() {
    int i = getLayoutViewCount() - 1;
    byte b;
    for (b = 0; b < i; b++) {
      View view = getLayoutView(b);
      if (view.isVisible())
        return true; 
    } 
    if (i > 0) {
      View view = getLayoutView(i);
      if (view.getEndOffset() - view.getStartOffset() == 1)
        return false; 
    } 
    if (getStartOffset() == getDocument().getLength()) {
      boolean bool;
      b = 0;
      Container container = getContainer();
      if (container instanceof JTextComponent)
        bool = ((JTextComponent)container).isEditable(); 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle;
    if (paramShape == null)
      return; 
    if (paramShape instanceof Rectangle) {
      rectangle = (Rectangle)paramShape;
    } else {
      rectangle = paramShape.getBounds();
    } 
    this.painter.paint(paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this);
    super.paint(paramGraphics, paramShape);
  }
  
  public float getPreferredSpan(int paramInt) { return !isVisible() ? 0.0F : super.getPreferredSpan(paramInt); }
  
  public float getMinimumSpan(int paramInt) { return !isVisible() ? 0.0F : super.getMinimumSpan(paramInt); }
  
  public float getMaximumSpan(int paramInt) { return !isVisible() ? 0.0F : super.getMaximumSpan(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\ParagraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */