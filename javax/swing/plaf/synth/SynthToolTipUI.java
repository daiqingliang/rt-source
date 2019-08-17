package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;

public class SynthToolTipUI extends BasicToolTipUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthToolTipUI(); }
  
  protected void installDefaults(JComponent paramJComponent) { updateStyle(paramJComponent); }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected void uninstallDefaults(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected void installListeners(JComponent paramJComponent) { paramJComponent.addPropertyChangeListener(this); }
  
  protected void uninstallListeners(JComponent paramJComponent) { paramJComponent.removePropertyChangeListener(this); }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) {
    JComponent jComponent = ((JToolTip)paramJComponent).getComponent();
    return (jComponent != null && !jComponent.isEnabled()) ? 8 : SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintToolTipBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintToolTipBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    JToolTip jToolTip = (JToolTip)paramSynthContext.getComponent();
    Insets insets = jToolTip.getInsets();
    View view = (View)jToolTip.getClientProperty("html");
    if (view != null) {
      Rectangle rectangle = new Rectangle(insets.left, insets.top, jToolTip.getWidth() - insets.left + insets.right, jToolTip.getHeight() - insets.top + insets.bottom);
      view.paint(paramGraphics, rectangle);
    } else {
      paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramGraphics.setFont(this.style.getFont(paramSynthContext));
      paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, jToolTip.getTipText(), insets.left, insets.top, -1);
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    Insets insets = paramJComponent.getInsets();
    Dimension dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
    String str = ((JToolTip)paramJComponent).getTipText();
    if (str != null) {
      View view = (paramJComponent != null) ? (View)paramJComponent.getClientProperty("html") : null;
      if (view != null) {
        dimension.width += (int)view.getPreferredSpan(0);
        dimension.height += (int)view.getPreferredSpan(1);
      } else {
        Font font = synthContext.getStyle().getFont(synthContext);
        FontMetrics fontMetrics = paramJComponent.getFontMetrics(font);
        dimension.width += synthContext.getStyle().getGraphicsUtils(synthContext).computeStringWidth(synthContext, font, fontMetrics, str);
        dimension.height += fontMetrics.getHeight();
      } 
    } 
    synthContext.dispose();
    return dimension;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JToolTip)paramPropertyChangeEvent.getSource()); 
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str.equals("tiptext") || "font".equals(str) || "foreground".equals(str)) {
      JToolTip jToolTip = (JToolTip)paramPropertyChangeEvent.getSource();
      String str1 = jToolTip.getTipText();
      BasicHTML.updateRenderer(jToolTip, str1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthToolTipUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */