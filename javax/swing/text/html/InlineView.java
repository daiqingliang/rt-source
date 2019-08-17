package javax.swing.text.html;

import java.awt.Color;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class InlineView extends LabelView {
  private boolean nowrap;
  
  private AttributeSet attr;
  
  public InlineView(Element paramElement) {
    super(paramElement);
    StyleSheet styleSheet = getStyleSheet();
    this.attr = styleSheet.getViewAttributes(this);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { super.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory); }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { super.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory); }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    StyleSheet styleSheet = getStyleSheet();
    this.attr = styleSheet.getViewAttributes(this);
    preferenceChanged(null, true, true);
  }
  
  public AttributeSet getAttributes() { return this.attr; }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2) { return this.nowrap ? 0 : super.getBreakWeight(paramInt, paramFloat1, paramFloat2); }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) { return super.breakView(paramInt1, paramInt2, paramFloat1, paramFloat2); }
  
  protected void setPropertiesFromAttributes() {
    super.setPropertiesFromAttributes();
    AttributeSet attributeSet = getAttributes();
    Object object1 = attributeSet.getAttribute(CSS.Attribute.TEXT_DECORATION);
    boolean bool1 = (object1 != null) ? ((object1.toString().indexOf("underline") >= 0)) : false;
    setUnderline(bool1);
    boolean bool2 = (object1 != null) ? ((object1.toString().indexOf("line-through") >= 0)) : false;
    setStrikeThrough(bool2);
    Object object2 = attributeSet.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
    bool2 = (object2 != null) ? ((object2.toString().indexOf("sup") >= 0)) : false;
    setSuperscript(bool2);
    bool2 = (object2 != null) ? ((object2.toString().indexOf("sub") >= 0)) : false;
    setSubscript(bool2);
    Object object3 = attributeSet.getAttribute(CSS.Attribute.WHITE_SPACE);
    if (object3 != null && object3.equals("nowrap")) {
      this.nowrap = true;
    } else {
      this.nowrap = false;
    } 
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    Color color = hTMLDocument.getBackground(attributeSet);
    if (color != null)
      setBackground(color); 
  }
  
  protected StyleSheet getStyleSheet() {
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    return hTMLDocument.getStyleSheet();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\InlineView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */