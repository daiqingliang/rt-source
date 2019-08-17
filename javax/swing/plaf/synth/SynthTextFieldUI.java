package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

public class SynthTextFieldUI extends BasicTextFieldUI implements SynthUI {
  private Handler handler = new Handler(null);
  
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTextFieldUI(); }
  
  private void updateStyle(JTextComponent paramJTextComponent) {
    SynthContext synthContext = getContext(paramJTextComponent, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      updateStyle(paramJTextComponent, synthContext, getPropertyPrefix());
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  static void updateStyle(JTextComponent paramJTextComponent, SynthContext paramSynthContext, String paramString) {
    SynthStyle synthStyle = paramSynthContext.getStyle();
    Color color1 = paramJTextComponent.getCaretColor();
    if (color1 == null || color1 instanceof javax.swing.plaf.UIResource)
      paramJTextComponent.setCaretColor((Color)synthStyle.get(paramSynthContext, paramString + ".caretForeground")); 
    Color color2 = paramJTextComponent.getForeground();
    if (color2 == null || color2 instanceof javax.swing.plaf.UIResource) {
      color2 = synthStyle.getColorForState(paramSynthContext, ColorType.TEXT_FOREGROUND);
      if (color2 != null)
        paramJTextComponent.setForeground(color2); 
    } 
    Object object = synthStyle.get(paramSynthContext, paramString + ".caretAspectRatio");
    if (object instanceof Number)
      paramJTextComponent.putClientProperty("caretAspectRatio", object); 
    paramSynthContext.setComponentState(768);
    Color color3 = paramJTextComponent.getSelectionColor();
    if (color3 == null || color3 instanceof javax.swing.plaf.UIResource)
      paramJTextComponent.setSelectionColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_BACKGROUND)); 
    Color color4 = paramJTextComponent.getSelectedTextColor();
    if (color4 == null || color4 instanceof javax.swing.plaf.UIResource)
      paramJTextComponent.setSelectedTextColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND)); 
    paramSynthContext.setComponentState(8);
    Color color5 = paramJTextComponent.getDisabledTextColor();
    if (color5 == null || color5 instanceof javax.swing.plaf.UIResource)
      paramJTextComponent.setDisabledTextColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND)); 
    Insets insets = paramJTextComponent.getMargin();
    if (insets == null || insets instanceof javax.swing.plaf.UIResource) {
      insets = (Insets)synthStyle.get(paramSynthContext, paramString + ".margin");
      if (insets == null)
        insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS; 
      paramJTextComponent.setMargin(insets);
    } 
    Caret caret = paramJTextComponent.getCaret();
    if (caret instanceof javax.swing.plaf.UIResource) {
      Object object1 = synthStyle.get(paramSynthContext, paramString + ".caretBlinkRate");
      if (object1 != null && object1 instanceof Integer) {
        Integer integer = (Integer)object1;
        caret.setBlinkRate(integer.intValue());
      } 
    } 
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    paintBackground(synthContext, paramGraphics, paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) { paint(paramGraphics, getComponent()); }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) { paramSynthContext.getPainter().paintTextFieldBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight()); }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected void paintBackground(Graphics paramGraphics) {}
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JTextComponent)paramPropertyChangeEvent.getSource()); 
    super.propertyChange(paramPropertyChangeEvent);
  }
  
  protected void installDefaults() {
    super.installDefaults();
    updateStyle(getComponent());
    getComponent().addFocusListener(this.handler);
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(getComponent(), 1);
    getComponent().putClientProperty("caretAspectRatio", null);
    getComponent().removeFocusListener(this.handler);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    super.uninstallDefaults();
  }
  
  private final class Handler implements FocusListener {
    private Handler() {}
    
    public void focusGained(FocusEvent param1FocusEvent) { SynthTextFieldUI.this.getComponent().repaint(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { SynthTextFieldUI.this.getComponent().repaint(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */