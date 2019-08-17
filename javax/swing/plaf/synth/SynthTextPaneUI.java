package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SynthTextPaneUI extends SynthEditorPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTextPaneUI(); }
  
  protected String getPropertyPrefix() { return "TextPane"; }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    updateForeground(paramJComponent.getForeground());
    updateFont(paramJComponent.getFont());
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str.equals("foreground")) {
      updateForeground((Color)paramPropertyChangeEvent.getNewValue());
    } else if (str.equals("font")) {
      updateFont((Font)paramPropertyChangeEvent.getNewValue());
    } else if (str.equals("document")) {
      JTextComponent jTextComponent = getComponent();
      updateForeground(jTextComponent.getForeground());
      updateFont(jTextComponent.getFont());
    } 
  }
  
  private void updateForeground(Color paramColor) {
    StyledDocument styledDocument = (StyledDocument)getComponent().getDocument();
    Style style = styledDocument.getStyle("default");
    if (style == null)
      return; 
    if (paramColor == null) {
      style.removeAttribute(StyleConstants.Foreground);
    } else {
      StyleConstants.setForeground(style, paramColor);
    } 
  }
  
  private void updateFont(Font paramFont) {
    StyledDocument styledDocument = (StyledDocument)getComponent().getDocument();
    Style style = styledDocument.getStyle("default");
    if (style == null)
      return; 
    if (paramFont == null) {
      style.removeAttribute(StyleConstants.FontFamily);
      style.removeAttribute(StyleConstants.FontSize);
      style.removeAttribute(StyleConstants.Bold);
      style.removeAttribute(StyleConstants.Italic);
    } else {
      StyleConstants.setFontFamily(style, paramFont.getName());
      StyleConstants.setFontSize(style, paramFont.getSize());
      StyleConstants.setBold(style, paramFont.isBold());
      StyleConstants.setItalic(style, paramFont.isItalic());
    } 
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) { paramSynthContext.getPainter().paintTextPaneBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight()); }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTextPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTextPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */