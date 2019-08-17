package javax.swing.text.html;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class HRuleView extends View {
  private float topMargin;
  
  private float bottomMargin;
  
  private float leftMargin;
  
  private float rightMargin;
  
  private int alignment = 1;
  
  private String noshade = null;
  
  private int size = 0;
  
  private CSS.LengthValue widthValue;
  
  private static final int SPACE_ABOVE = 3;
  
  private static final int SPACE_BELOW = 3;
  
  private AttributeSet attr;
  
  public HRuleView(Element paramElement) {
    super(paramElement);
    setPropertiesFromAttributes();
  }
  
  protected void setPropertiesFromAttributes() {
    StyleSheet styleSheet = ((HTMLDocument)getDocument()).getStyleSheet();
    AttributeSet attributeSet = getElement().getAttributes();
    this.attr = styleSheet.getViewAttributes(this);
    this.alignment = 1;
    this.size = 0;
    this.noshade = null;
    this.widthValue = null;
    if (this.attr != null) {
      if (this.attr.getAttribute(StyleConstants.Alignment) != null)
        this.alignment = StyleConstants.getAlignment(this.attr); 
      this.noshade = (String)attributeSet.getAttribute(HTML.Attribute.NOSHADE);
      Object object = attributeSet.getAttribute(HTML.Attribute.SIZE);
      if (object != null && object instanceof String)
        try {
          this.size = Integer.parseInt((String)object);
        } catch (NumberFormatException numberFormatException) {
          this.size = 1;
        }  
      object = this.attr.getAttribute(CSS.Attribute.WIDTH);
      if (object != null && object instanceof CSS.LengthValue)
        this.widthValue = (CSS.LengthValue)object; 
      this.topMargin = getLength(CSS.Attribute.MARGIN_TOP, this.attr);
      this.bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, this.attr);
      this.leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, this.attr);
      this.rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, this.attr);
    } else {
      this.topMargin = this.bottomMargin = this.leftMargin = this.rightMargin = 0.0F;
    } 
    this.size = Math.max(2, this.size);
  }
  
  private float getLength(CSS.Attribute paramAttribute, AttributeSet paramAttributeSet) {
    CSS.LengthValue lengthValue = (CSS.LengthValue)paramAttributeSet.getAttribute(paramAttribute);
    return (lengthValue != null) ? lengthValue.getValue() : 0.0F;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = 0;
    int j = rectangle.y + 3 + (int)this.topMargin;
    int k = rectangle.width - (int)(this.leftMargin + this.rightMargin);
    if (this.widthValue != null)
      k = (int)this.widthValue.getValue(k); 
    int m = rectangle.height - 6 + (int)this.topMargin + (int)this.bottomMargin;
    if (this.size > 0)
      m = this.size; 
    switch (this.alignment) {
      case 1:
        i = rectangle.x + rectangle.width / 2 - k / 2;
        break;
      case 2:
        i = rectangle.x + rectangle.width - k - (int)this.rightMargin;
        break;
      default:
        i = rectangle.x + (int)this.leftMargin;
        break;
    } 
    if (this.noshade != null) {
      paramGraphics.setColor(Color.black);
      paramGraphics.fillRect(i, j, k, m);
    } else {
      Color color3;
      Color color2;
      Color color1 = getContainer().getBackground();
      if (color1 == null || color1.equals(Color.white)) {
        color3 = Color.darkGray;
        color2 = Color.lightGray;
      } else {
        color3 = Color.darkGray;
        color2 = Color.white;
      } 
      paramGraphics.setColor(color2);
      paramGraphics.drawLine(i + k - 1, j, i + k - 1, j + m - 1);
      paramGraphics.drawLine(i, j + m - 1, i + k - 1, j + m - 1);
      paramGraphics.setColor(color3);
      paramGraphics.drawLine(i, j, i + k - 1, j);
      paramGraphics.drawLine(i, j, i, j + m - 1);
    } 
  }
  
  public float getPreferredSpan(int paramInt) {
    switch (paramInt) {
      case 0:
        return 1.0F;
      case 1:
        return (this.size > 0) ? ((this.size + 3 + 3) + this.topMargin + this.bottomMargin) : ((this.noshade != null) ? (8.0F + this.topMargin + this.bottomMargin) : (6.0F + this.topMargin + this.bottomMargin));
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public int getResizeWeight(int paramInt) { return (paramInt == 0) ? 1 : ((paramInt == 1) ? 0 : 0); }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2) { return (paramInt == 0) ? 3000 : 0; }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) { return null; }
  
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
    return null;
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
  
  public AttributeSet getAttributes() { return this.attr; }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    int i = paramDocumentEvent.getOffset();
    if (i <= getStartOffset() && i + paramDocumentEvent.getLength() >= getEndOffset())
      setPropertiesFromAttributes(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\HRuleView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */