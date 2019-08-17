package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class BlockView extends BoxView {
  private AttributeSet attr;
  
  private StyleSheet.BoxPainter painter;
  
  private CSS.LengthValue cssWidth;
  
  private CSS.LengthValue cssHeight;
  
  public BlockView(Element paramElement, int paramInt) { super(paramElement, paramInt); }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (paramView != null)
      setPropertiesFromAttributes(); 
  }
  
  protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    if (!spanSetFromAttributes(paramInt, paramSizeRequirements, this.cssWidth, this.cssHeight)) {
      paramSizeRequirements = super.calculateMajorAxisRequirements(paramInt, paramSizeRequirements);
    } else {
      SizeRequirements sizeRequirements = super.calculateMajorAxisRequirements(paramInt, null);
      short s = (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset());
      paramSizeRequirements.minimum -= s;
      paramSizeRequirements.preferred -= s;
      paramSizeRequirements.maximum -= s;
      constrainSize(paramInt, paramSizeRequirements, sizeRequirements);
    } 
    return paramSizeRequirements;
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    if (!spanSetFromAttributes(paramInt, paramSizeRequirements, this.cssWidth, this.cssHeight)) {
      paramSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
    } else {
      SizeRequirements sizeRequirements = super.calculateMinorAxisRequirements(paramInt, null);
      short s = (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset());
      paramSizeRequirements.minimum -= s;
      paramSizeRequirements.preferred -= s;
      paramSizeRequirements.maximum -= s;
      constrainSize(paramInt, paramSizeRequirements, sizeRequirements);
    } 
    if (paramInt == 0) {
      Object object = getAttributes().getAttribute(CSS.Attribute.TEXT_ALIGN);
      if (object != null) {
        String str = object.toString();
        if (str.equals("center")) {
          paramSizeRequirements.alignment = 0.5F;
        } else if (str.equals("right")) {
          paramSizeRequirements.alignment = 1.0F;
        } else {
          paramSizeRequirements.alignment = 0.0F;
        } 
      } 
    } 
    return paramSizeRequirements;
  }
  
  boolean isPercentage(int paramInt, AttributeSet paramAttributeSet) {
    if (paramInt == 0) {
      if (this.cssWidth != null)
        return this.cssWidth.isPercentage(); 
    } else if (this.cssHeight != null) {
      return this.cssHeight.isPercentage();
    } 
    return false;
  }
  
  static boolean spanSetFromAttributes(int paramInt, SizeRequirements paramSizeRequirements, CSS.LengthValue paramLengthValue1, CSS.LengthValue paramLengthValue2) {
    if (paramInt == 0) {
      if (paramLengthValue1 != null && !paramLengthValue1.isPercentage()) {
        paramSizeRequirements.minimum = paramSizeRequirements.preferred = paramSizeRequirements.maximum = (int)paramLengthValue1.getValue();
        return true;
      } 
    } else if (paramLengthValue2 != null && !paramLengthValue2.isPercentage()) {
      paramSizeRequirements.minimum = paramSizeRequirements.preferred = paramSizeRequirements.maximum = (int)paramLengthValue2.getValue();
      return true;
    } 
    return false;
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    int i = getViewCount();
    CSS.Attribute attribute = (paramInt2 == 0) ? CSS.Attribute.WIDTH : CSS.Attribute.HEIGHT;
    for (byte b = 0; b < i; b++) {
      int k;
      View view = getView(b);
      int j = (int)view.getMinimumSpan(paramInt2);
      AttributeSet attributeSet = view.getAttributes();
      CSS.LengthValue lengthValue = (CSS.LengthValue)attributeSet.getAttribute(attribute);
      if (lengthValue != null && lengthValue.isPercentage()) {
        j = Math.max((int)lengthValue.getValue(paramInt1), j);
        k = j;
      } else {
        k = (int)view.getMaximumSpan(paramInt2);
      } 
      if (k < paramInt1) {
        float f = view.getAlignment(paramInt2);
        paramArrayOfInt1[b] = (int)((paramInt1 - k) * f);
        paramArrayOfInt2[b] = k;
      } else {
        paramArrayOfInt1[b] = 0;
        paramArrayOfInt2[b] = Math.max(j, paramInt1);
      } 
    } 
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = (Rectangle)paramShape;
    this.painter.paint(paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this);
    super.paint(paramGraphics, rectangle);
  }
  
  public AttributeSet getAttributes() {
    if (this.attr == null) {
      StyleSheet styleSheet = getStyleSheet();
      this.attr = styleSheet.getViewAttributes(this);
    } 
    return this.attr;
  }
  
  public int getResizeWeight(int paramInt) {
    switch (paramInt) {
      case 0:
        return 1;
      case 1:
        return 0;
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt) {
    float f2;
    View view;
    float f1;
    switch (paramInt) {
      case 0:
        return 0.0F;
      case 1:
        if (getViewCount() == 0)
          return 0.0F; 
        f1 = getPreferredSpan(1);
        view = getView(0);
        f2 = view.getPreferredSpan(1);
        return ((int)f1 != 0) ? (f2 * view.getAlignment(1) / f1) : 0.0F;
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    int i = paramDocumentEvent.getOffset();
    if (i <= getStartOffset() && i + paramDocumentEvent.getLength() >= getEndOffset())
      setPropertiesFromAttributes(); 
  }
  
  public float getPreferredSpan(int paramInt) { return super.getPreferredSpan(paramInt); }
  
  public float getMinimumSpan(int paramInt) { return super.getMinimumSpan(paramInt); }
  
  public float getMaximumSpan(int paramInt) { return super.getMaximumSpan(paramInt); }
  
  protected void setPropertiesFromAttributes() {
    StyleSheet styleSheet = getStyleSheet();
    this.attr = styleSheet.getViewAttributes(this);
    this.painter = styleSheet.getBoxPainter(this.attr);
    if (this.attr != null)
      setInsets((short)(int)this.painter.getInset(1, this), (short)(int)this.painter.getInset(2, this), (short)(int)this.painter.getInset(3, this), (short)(int)this.painter.getInset(4, this)); 
    this.cssWidth = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.WIDTH);
    this.cssHeight = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.HEIGHT);
  }
  
  protected StyleSheet getStyleSheet() {
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    return hTMLDocument.getStyleSheet();
  }
  
  private void constrainSize(int paramInt, SizeRequirements paramSizeRequirements1, SizeRequirements paramSizeRequirements2) {
    if (paramSizeRequirements2.minimum > paramSizeRequirements1.minimum) {
      paramSizeRequirements1.minimum = paramSizeRequirements1.preferred = paramSizeRequirements2.minimum;
      paramSizeRequirements1.maximum = Math.max(paramSizeRequirements1.maximum, paramSizeRequirements2.maximum);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\BlockView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */