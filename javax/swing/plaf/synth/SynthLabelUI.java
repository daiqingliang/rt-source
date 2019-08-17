package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;

public class SynthLabelUI extends BasicLabelUI implements SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthLabelUI(); }
  
  protected void installDefaults(JLabel paramJLabel) { updateStyle(paramJLabel); }
  
  void updateStyle(JLabel paramJLabel) {
    SynthContext synthContext = getContext(paramJLabel, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected void uninstallDefaults(JLabel paramJLabel) {
    SynthContext synthContext = getContext(paramJLabel, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) {
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if (SynthLookAndFeel.getSelectedUI() == this && i == 1)
      i = SynthLookAndFeel.getSelectedUIState() | true; 
    return i;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    int i;
    if (paramJComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    JLabel jLabel = (JLabel)paramJComponent;
    String str = jLabel.getText();
    if (str == null || "".equals(str))
      return -1; 
    Insets insets = jLabel.getInsets();
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    rectangle1.x = insets.left;
    rectangle1.y = insets.top;
    rectangle1.width = paramInt1 - insets.right + rectangle1.x;
    rectangle1.height = paramInt2 - insets.bottom + rectangle1.y;
    SynthContext synthContext = getContext(jLabel);
    FontMetrics fontMetrics = synthContext.getComponent().getFontMetrics(synthContext.getStyle().getFont(synthContext));
    synthContext.getStyle().getGraphicsUtils(synthContext).layoutText(synthContext, fontMetrics, jLabel.getText(), jLabel.getIcon(), jLabel.getHorizontalAlignment(), jLabel.getVerticalAlignment(), jLabel.getHorizontalTextPosition(), jLabel.getVerticalTextPosition(), rectangle1, rectangle3, rectangle2, jLabel.getIconTextGap());
    View view = (View)jLabel.getClientProperty("html");
    if (view != null) {
      i = BasicHTML.getHTMLBaseline(view, rectangle2.width, rectangle2.height);
      if (i >= 0)
        i += rectangle2.y; 
    } else {
      i = rectangle2.y + fontMetrics.getAscent();
    } 
    synthContext.dispose();
    return i;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintLabelBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    JLabel jLabel = (JLabel)paramSynthContext.getComponent();
    Icon icon = jLabel.isEnabled() ? jLabel.getIcon() : jLabel.getDisabledIcon();
    paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
    paramGraphics.setFont(this.style.getFont(paramSynthContext));
    paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, jLabel.getText(), icon, jLabel.getHorizontalAlignment(), jLabel.getVerticalAlignment(), jLabel.getHorizontalTextPosition(), jLabel.getVerticalTextPosition(), jLabel.getIconTextGap(), jLabel.getDisplayedMnemonicIndex(), 0);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintLabelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    JLabel jLabel = (JLabel)paramJComponent;
    Icon icon = jLabel.isEnabled() ? jLabel.getIcon() : jLabel.getDisabledIcon();
    SynthContext synthContext = getContext(paramJComponent);
    Dimension dimension = synthContext.getStyle().getGraphicsUtils(synthContext).getPreferredSize(synthContext, synthContext.getStyle().getFont(synthContext), jLabel.getText(), icon, jLabel.getHorizontalAlignment(), jLabel.getVerticalAlignment(), jLabel.getHorizontalTextPosition(), jLabel.getVerticalTextPosition(), jLabel.getIconTextGap(), jLabel.getDisplayedMnemonicIndex());
    synthContext.dispose();
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    JLabel jLabel = (JLabel)paramJComponent;
    Icon icon = jLabel.isEnabled() ? jLabel.getIcon() : jLabel.getDisabledIcon();
    SynthContext synthContext = getContext(paramJComponent);
    Dimension dimension = synthContext.getStyle().getGraphicsUtils(synthContext).getMinimumSize(synthContext, synthContext.getStyle().getFont(synthContext), jLabel.getText(), icon, jLabel.getHorizontalAlignment(), jLabel.getVerticalAlignment(), jLabel.getHorizontalTextPosition(), jLabel.getVerticalTextPosition(), jLabel.getIconTextGap(), jLabel.getDisplayedMnemonicIndex());
    synthContext.dispose();
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    JLabel jLabel = (JLabel)paramJComponent;
    Icon icon = jLabel.isEnabled() ? jLabel.getIcon() : jLabel.getDisabledIcon();
    SynthContext synthContext = getContext(paramJComponent);
    Dimension dimension = synthContext.getStyle().getGraphicsUtils(synthContext).getMaximumSize(synthContext, synthContext.getStyle().getFont(synthContext), jLabel.getText(), icon, jLabel.getHorizontalAlignment(), jLabel.getVerticalAlignment(), jLabel.getHorizontalTextPosition(), jLabel.getVerticalTextPosition(), jLabel.getIconTextGap(), jLabel.getDisplayedMnemonicIndex());
    synthContext.dispose();
    return dimension;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    super.propertyChange(paramPropertyChangeEvent);
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JLabel)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */