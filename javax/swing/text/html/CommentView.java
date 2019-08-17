package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

class CommentView extends HiddenTagView {
  static final Border CBorder = new CommentBorder();
  
  static final int commentPadding = 3;
  
  static final int commentPaddingD = 9;
  
  CommentView(Element paramElement) { super(paramElement); }
  
  protected Component createComponent() {
    Font font;
    Container container = getContainer();
    if (container != null && !((JTextComponent)container).isEditable())
      return null; 
    JTextArea jTextArea = new JTextArea(getRepresentedText());
    Document document = getDocument();
    if (document instanceof StyledDocument) {
      font = ((StyledDocument)document).getFont(getAttributes());
      jTextArea.setFont(font);
    } else {
      font = jTextArea.getFont();
    } 
    updateYAlign(font);
    jTextArea.setBorder(CBorder);
    jTextArea.getDocument().addDocumentListener(this);
    jTextArea.setFocusable(isVisible());
    return jTextArea;
  }
  
  void resetBorder() {}
  
  void _updateModelFromText() {
    JTextComponent jTextComponent = getTextComponent();
    Document document = getDocument();
    if (jTextComponent != null && document != null) {
      String str = jTextComponent.getText();
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
      this.isSettingAttributes = true;
      try {
        simpleAttributeSet.addAttribute(HTML.Attribute.COMMENT, str);
        ((StyledDocument)document).setCharacterAttributes(getStartOffset(), getEndOffset() - getStartOffset(), simpleAttributeSet, false);
      } finally {
        this.isSettingAttributes = false;
      } 
    } 
  }
  
  JTextComponent getTextComponent() { return (JTextComponent)getComponent(); }
  
  String getRepresentedText() {
    AttributeSet attributeSet = getElement().getAttributes();
    if (attributeSet != null) {
      Object object = attributeSet.getAttribute(HTML.Attribute.COMMENT);
      if (object instanceof String)
        return (String)object; 
    } 
    return "";
  }
  
  static class CommentBorder extends LineBorder {
    CommentBorder() { super(Color.black, 1); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super.paintBorder(param1Component, param1Graphics, param1Int1 + 3, param1Int2, param1Int3 - 9, param1Int4); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      Insets insets = super.getBorderInsets(param1Component, param1Insets);
      insets.left += 3;
      insets.right += 3;
      return insets;
    }
    
    public boolean isBorderOpaque() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\CommentView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */