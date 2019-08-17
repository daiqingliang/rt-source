package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import sun.swing.MenuItemLayoutHelper;

public class SynthMenuUI extends BasicMenuUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private SynthStyle accStyle;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthMenuUI(); }
  
  protected void installDefaults() { updateStyle(this.menuItem); }
  
  protected void installListeners() {
    super.installListeners();
    this.menuItem.addPropertyChangeListener(this);
  }
  
  private void updateStyle(JMenuItem paramJMenuItem) {
    SynthStyle synthStyle = this.style;
    SynthContext synthContext1 = getContext(paramJMenuItem, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext1, this);
    if (synthStyle != this.style) {
      String str = getPropertyPrefix();
      this.defaultTextIconGap = this.style.getInt(synthContext1, str + ".textIconGap", 4);
      if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof javax.swing.plaf.UIResource) {
        Insets insets = (Insets)this.style.get(synthContext1, str + ".margin");
        if (insets == null)
          insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS; 
        this.menuItem.setMargin(insets);
      } 
      this.acceleratorDelimiter = this.style.getString(synthContext1, str + ".acceleratorDelimiter", "+");
      if (MenuItemLayoutHelper.useCheckAndArrow(this.menuItem)) {
        this.checkIcon = this.style.getIcon(synthContext1, str + ".checkIcon");
        this.arrowIcon = this.style.getIcon(synthContext1, str + ".arrowIcon");
      } else {
        this.checkIcon = null;
        this.arrowIcon = null;
      } 
      ((JMenu)this.menuItem).setDelay(this.style.getInt(synthContext1, str + ".delay", 200));
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext1.dispose();
    SynthContext synthContext2 = getContext(paramJMenuItem, Region.MENU_ITEM_ACCELERATOR, 1);
    this.accStyle = SynthLookAndFeel.updateStyle(synthContext2, this);
    synthContext2.dispose();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    JComponent jComponent = MenuItemLayoutHelper.getMenuItemParent((JMenuItem)paramJComponent);
    if (jComponent != null)
      jComponent.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, null); 
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext1 = getContext(this.menuItem, 1);
    this.style.uninstallDefaults(synthContext1);
    synthContext1.dispose();
    this.style = null;
    SynthContext synthContext2 = getContext(this.menuItem, Region.MENU_ITEM_ACCELERATOR, 1);
    this.accStyle.uninstallDefaults(synthContext2);
    synthContext2.dispose();
    this.accStyle = null;
    super.uninstallDefaults();
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.menuItem.removePropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  SynthContext getContext(JComponent paramJComponent, Region paramRegion) { return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion)); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) { return SynthContext.getContext(paramJComponent, paramRegion, this.accStyle, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) {
    int i;
    if (!paramJComponent.isEnabled())
      return 8; 
    if (this.menuItem.isArmed()) {
      i = 2;
    } else {
      i = SynthLookAndFeel.getComponentState(paramJComponent);
    } 
    if (this.menuItem.isSelected())
      i |= 0x200; 
    return i;
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion) { return getComponentState(paramJComponent); }
  
  protected Dimension getPreferredMenuItemSize(JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt) {
    SynthContext synthContext1 = getContext(paramJComponent);
    SynthContext synthContext2 = getContext(paramJComponent, Region.MENU_ITEM_ACCELERATOR);
    Dimension dimension = SynthGraphicsUtils.getPreferredMenuItemSize(synthContext1, synthContext2, paramJComponent, paramIcon1, paramIcon2, paramInt, this.acceleratorDelimiter, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), getPropertyPrefix());
    synthContext1.dispose();
    synthContext2.dispose();
    return dimension;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintMenuBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    SynthContext synthContext = getContext(this.menuItem, Region.MENU_ITEM_ACCELERATOR);
    String str = getPropertyPrefix();
    Icon icon1 = this.style.getIcon(paramSynthContext, str + ".checkIcon");
    Icon icon2 = this.style.getIcon(paramSynthContext, str + ".arrowIcon");
    SynthGraphicsUtils.paint(paramSynthContext, synthContext, paramGraphics, icon1, icon2, this.acceleratorDelimiter, this.defaultTextIconGap, getPropertyPrefix());
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintMenuBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent) || (paramPropertyChangeEvent.getPropertyName().equals("ancestor") && UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus")))
      updateStyle((JMenu)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */