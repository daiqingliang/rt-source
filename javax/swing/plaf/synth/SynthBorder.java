package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

class SynthBorder extends AbstractBorder implements UIResource {
  private SynthUI ui;
  
  private Insets insets;
  
  SynthBorder(SynthUI paramSynthUI, Insets paramInsets) {
    this.ui = paramSynthUI;
    this.insets = paramInsets;
  }
  
  SynthBorder(SynthUI paramSynthUI) { this(paramSynthUI, null); }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    JComponent jComponent = (JComponent)paramComponent;
    SynthContext synthContext = this.ui.getContext(jComponent);
    SynthStyle synthStyle = synthContext.getStyle();
    if (synthStyle == null) {
      assert false : "SynthBorder is being used outside after the UI has been uninstalled";
      return;
    } 
    this.ui.paintBorder(synthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    synthContext.dispose();
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    if (this.insets != null) {
      if (paramInsets == null) {
        paramInsets = new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
      } else {
        paramInsets.top = this.insets.top;
        paramInsets.bottom = this.insets.bottom;
        paramInsets.left = this.insets.left;
        paramInsets.right = this.insets.right;
      } 
    } else if (paramInsets == null) {
      paramInsets = new Insets(0, 0, 0, 0);
    } else {
      paramInsets.top = paramInsets.bottom = paramInsets.left = paramInsets.right = 0;
    } 
    if (paramComponent instanceof JComponent) {
      Region region = Region.getRegion((JComponent)paramComponent);
      Insets insets1 = null;
      if ((region == Region.ARROW_BUTTON || region == Region.BUTTON || region == Region.CHECK_BOX || region == Region.CHECK_BOX_MENU_ITEM || region == Region.MENU || region == Region.MENU_ITEM || region == Region.RADIO_BUTTON || region == Region.RADIO_BUTTON_MENU_ITEM || region == Region.TOGGLE_BUTTON) && paramComponent instanceof AbstractButton) {
        insets1 = ((AbstractButton)paramComponent).getMargin();
      } else if ((region == Region.EDITOR_PANE || region == Region.FORMATTED_TEXT_FIELD || region == Region.PASSWORD_FIELD || region == Region.TEXT_AREA || region == Region.TEXT_FIELD || region == Region.TEXT_PANE) && paramComponent instanceof JTextComponent) {
        insets1 = ((JTextComponent)paramComponent).getMargin();
      } else if (region == Region.TOOL_BAR && paramComponent instanceof JToolBar) {
        insets1 = ((JToolBar)paramComponent).getMargin();
      } else if (region == Region.MENU_BAR && paramComponent instanceof JMenuBar) {
        insets1 = ((JMenuBar)paramComponent).getMargin();
      } 
      if (insets1 != null) {
        paramInsets.top += insets1.top;
        paramInsets.bottom += insets1.bottom;
        paramInsets.left += insets1.left;
        paramInsets.right += insets1.right;
      } 
    } 
    return paramInsets;
  }
  
  public boolean isBorderOpaque() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */