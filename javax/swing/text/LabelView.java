package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.Toolkit;
import javax.swing.event.DocumentEvent;

public class LabelView extends GlyphView implements TabableView {
  private Font font;
  
  private Color fg;
  
  private Color bg;
  
  private boolean underline;
  
  private boolean strike;
  
  private boolean superscript;
  
  private boolean subscript;
  
  public LabelView(Element paramElement) { super(paramElement); }
  
  final void sync() {
    if (this.font == null)
      setPropertiesFromAttributes(); 
  }
  
  protected void setUnderline(boolean paramBoolean) { this.underline = paramBoolean; }
  
  protected void setStrikeThrough(boolean paramBoolean) { this.strike = paramBoolean; }
  
  protected void setSuperscript(boolean paramBoolean) { this.superscript = paramBoolean; }
  
  protected void setSubscript(boolean paramBoolean) { this.subscript = paramBoolean; }
  
  protected void setBackground(Color paramColor) { this.bg = paramColor; }
  
  protected void setPropertiesFromAttributes() {
    AttributeSet attributeSet = getAttributes();
    if (attributeSet != null) {
      Document document = getDocument();
      if (document instanceof StyledDocument) {
        StyledDocument styledDocument = (StyledDocument)document;
        this.font = styledDocument.getFont(attributeSet);
        this.fg = styledDocument.getForeground(attributeSet);
        if (attributeSet.isDefined(StyleConstants.Background)) {
          this.bg = styledDocument.getBackground(attributeSet);
        } else {
          this.bg = null;
        } 
        setUnderline(StyleConstants.isUnderline(attributeSet));
        setStrikeThrough(StyleConstants.isStrikeThrough(attributeSet));
        setSuperscript(StyleConstants.isSuperscript(attributeSet));
        setSubscript(StyleConstants.isSubscript(attributeSet));
      } else {
        throw new StateInvariantError("LabelView needs StyledDocument");
      } 
    } 
  }
  
  @Deprecated
  protected FontMetrics getFontMetrics() {
    sync();
    Container container = getContainer();
    return (container != null) ? container.getFontMetrics(this.font) : Toolkit.getDefaultToolkit().getFontMetrics(this.font);
  }
  
  public Color getBackground() {
    sync();
    return this.bg;
  }
  
  public Color getForeground() {
    sync();
    return this.fg;
  }
  
  public Font getFont() {
    sync();
    return this.font;
  }
  
  public boolean isUnderline() {
    sync();
    return this.underline;
  }
  
  public boolean isStrikeThrough() {
    sync();
    return this.strike;
  }
  
  public boolean isSubscript() {
    sync();
    return this.subscript;
  }
  
  public boolean isSuperscript() {
    sync();
    return this.superscript;
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.font = null;
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\LabelView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */