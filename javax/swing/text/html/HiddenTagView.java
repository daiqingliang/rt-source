package javax.swing.text.html;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.ViewFactory;

class HiddenTagView extends EditableView implements DocumentListener {
  float yAlign = 1.0F;
  
  boolean isSettingAttributes;
  
  static final int circleR = 3;
  
  static final int circleD = 6;
  
  static final int tagSize = 6;
  
  static final int padding = 3;
  
  static final Color UnknownTagBorderColor = Color.black;
  
  static final Border StartBorder = new StartTagBorder();
  
  static final Border EndBorder = new EndTagBorder();
  
  HiddenTagView(Element paramElement) { super(paramElement); }
  
  protected Component createComponent() {
    Font font;
    JTextField jTextField = new JTextField(getElement().getName());
    Document document = getDocument();
    if (document instanceof StyledDocument) {
      font = ((StyledDocument)document).getFont(getAttributes());
      jTextField.setFont(font);
    } else {
      font = jTextField.getFont();
    } 
    jTextField.getDocument().addDocumentListener(this);
    updateYAlign(font);
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.setBackground(null);
    if (isEndTag()) {
      jPanel.setBorder(EndBorder);
    } else {
      jPanel.setBorder(StartBorder);
    } 
    jPanel.add(jTextField);
    return jPanel;
  }
  
  public float getAlignment(int paramInt) { return (paramInt == 1) ? this.yAlign : 0.5F; }
  
  public float getMinimumSpan(int paramInt) { return (paramInt == 0 && isVisible()) ? Math.max(30.0F, super.getPreferredSpan(paramInt)) : super.getMinimumSpan(paramInt); }
  
  public float getPreferredSpan(int paramInt) { return (paramInt == 0 && isVisible()) ? Math.max(30.0F, super.getPreferredSpan(paramInt)) : super.getPreferredSpan(paramInt); }
  
  public float getMaximumSpan(int paramInt) { return (paramInt == 0 && isVisible()) ? Math.max(30.0F, super.getMaximumSpan(paramInt)) : super.getMaximumSpan(paramInt); }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent) { updateModelFromText(); }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent) { updateModelFromText(); }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent) { updateModelFromText(); }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    if (!this.isSettingAttributes)
      setTextFromModel(); 
  }
  
  void updateYAlign(Font paramFont) {
    Container container = getContainer();
    FontMetrics fontMetrics = (container != null) ? container.getFontMetrics(paramFont) : Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
    float f1 = fontMetrics.getHeight();
    float f2 = fontMetrics.getDescent();
    this.yAlign = (f1 > 0.0F) ? ((f1 - f2) / f1) : 0.0F;
  }
  
  void resetBorder() {
    Component component = getComponent();
    if (component != null)
      if (isEndTag()) {
        ((JPanel)component).setBorder(EndBorder);
      } else {
        ((JPanel)component).setBorder(StartBorder);
      }  
  }
  
  void setTextFromModel() {
    if (SwingUtilities.isEventDispatchThread()) {
      _setTextFromModel();
    } else {
      SwingUtilities.invokeLater(new Runnable() {
            public void run() { HiddenTagView.this._setTextFromModel(); }
          });
    } 
  }
  
  void _setTextFromModel() {
    document = getDocument();
    try {
      this.isSettingAttributes = true;
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readLock(); 
      JTextComponent jTextComponent = getTextComponent();
      if (jTextComponent != null) {
        jTextComponent.setText(getRepresentedText());
        resetBorder();
        Container container = getContainer();
        if (container != null) {
          preferenceChanged(this, true, true);
          container.repaint();
        } 
      } 
    } finally {
      this.isSettingAttributes = false;
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
  }
  
  void updateModelFromText() {
    if (!this.isSettingAttributes)
      if (SwingUtilities.isEventDispatchThread()) {
        _updateModelFromText();
      } else {
        SwingUtilities.invokeLater(new Runnable() {
              public void run() { HiddenTagView.this._updateModelFromText(); }
            });
      }  
  }
  
  void _updateModelFromText() {
    Document document = getDocument();
    Object object = getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.UnknownTag && document instanceof StyledDocument) {
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
      JTextComponent jTextComponent = getTextComponent();
      if (jTextComponent != null) {
        String str = jTextComponent.getText();
        this.isSettingAttributes = true;
        try {
          simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, new HTML.UnknownTag(str));
          ((StyledDocument)document).setCharacterAttributes(getStartOffset(), getEndOffset() - getStartOffset(), simpleAttributeSet, false);
        } finally {
          this.isSettingAttributes = false;
        } 
      } 
    } 
  }
  
  JTextComponent getTextComponent() {
    Component component = getComponent();
    return (component == null) ? null : (JTextComponent)((Container)component).getComponent(0);
  }
  
  String getRepresentedText() {
    String str = getElement().getName();
    return (str == null) ? "" : str;
  }
  
  boolean isEndTag() {
    AttributeSet attributeSet = getElement().getAttributes();
    if (attributeSet != null) {
      Object object = attributeSet.getAttribute(HTML.Attribute.ENDTAG);
      if (object != null && object instanceof String && ((String)object).equals("true"))
        return true; 
    } 
    return false;
  }
  
  static class EndTagBorder implements Border, Serializable {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.setColor(HiddenTagView.UnknownTagBorderColor);
      param1Int1 += 3;
      param1Int3 -= 6;
      param1Graphics.drawLine(param1Int1 + param1Int3 - 1, param1Int2 + 3, param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 - 3);
      param1Graphics.drawArc(param1Int1 + param1Int3 - 6 - 1, param1Int2 + param1Int4 - 6 - 1, 6, 6, 270, 90);
      param1Graphics.drawArc(param1Int1 + param1Int3 - 6 - 1, param1Int2, 6, 6, 0, 90);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2, param1Int1 + param1Int3 - 3, param1Int2);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2 + param1Int4 - 1, param1Int1 + param1Int3 - 3, param1Int2 + param1Int4 - 1);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2, param1Int1, param1Int2 + param1Int4 / 2);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2 + param1Int4, param1Int1, param1Int2 + param1Int4 / 2);
    }
    
    public Insets getBorderInsets(Component param1Component) { return new Insets(2, 11, 2, 5); }
    
    public boolean isBorderOpaque() { return false; }
  }
  
  static class StartTagBorder implements Border, Serializable {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.setColor(HiddenTagView.UnknownTagBorderColor);
      param1Int1 += 3;
      param1Int3 -= 6;
      param1Graphics.drawLine(param1Int1, param1Int2 + 3, param1Int1, param1Int2 + param1Int4 - 3);
      param1Graphics.drawArc(param1Int1, param1Int2 + param1Int4 - 6 - 1, 6, 6, 180, 90);
      param1Graphics.drawArc(param1Int1, param1Int2, 6, 6, 90, 90);
      param1Graphics.drawLine(param1Int1 + 3, param1Int2, param1Int1 + param1Int3 - 6, param1Int2);
      param1Graphics.drawLine(param1Int1 + 3, param1Int2 + param1Int4 - 1, param1Int1 + param1Int3 - 6, param1Int2 + param1Int4 - 1);
      param1Graphics.drawLine(param1Int1 + param1Int3 - 6, param1Int2, param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 / 2);
      param1Graphics.drawLine(param1Int1 + param1Int3 - 6, param1Int2 + param1Int4, param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 / 2);
    }
    
    public Insets getBorderInsets(Component param1Component) { return new Insets(2, 5, 2, 11); }
    
    public boolean isBorderOpaque() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\HiddenTagView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */