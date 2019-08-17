package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

public class SynthListUI extends BasicListUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private boolean useListColors;
  
  private boolean useUIBorder;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthListUI(); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintListBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    synthContext.dispose();
    paint(paramGraphics, paramJComponent);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintListBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected void installListeners() {
    super.installListeners();
    this.list.addPropertyChangeListener(this);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JList)paramPropertyChangeEvent.getSource()); 
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.list.removePropertyChangeListener(this);
  }
  
  protected void installDefaults() {
    if (this.list.getCellRenderer() == null || this.list.getCellRenderer() instanceof javax.swing.plaf.UIResource)
      this.list.setCellRenderer(new SynthListCellRenderer(null)); 
    updateStyle(this.list);
  }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(this.list, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      synthContext.setComponentState(512);
      Color color1 = this.list.getSelectionBackground();
      if (color1 == null || color1 instanceof javax.swing.plaf.UIResource)
        this.list.setSelectionBackground(this.style.getColor(synthContext, ColorType.TEXT_BACKGROUND)); 
      Color color2 = this.list.getSelectionForeground();
      if (color2 == null || color2 instanceof javax.swing.plaf.UIResource)
        this.list.setSelectionForeground(this.style.getColor(synthContext, ColorType.TEXT_FOREGROUND)); 
      this.useListColors = this.style.getBoolean(synthContext, "List.rendererUseListColors", true);
      this.useUIBorder = this.style.getBoolean(synthContext, "List.rendererUseUIBorder", true);
      int i = this.style.getInt(synthContext, "List.cellHeight", -1);
      if (i != -1)
        this.list.setFixedCellHeight(i); 
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    SynthContext synthContext = getContext(this.list, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  private class SynthListCellRenderer extends DefaultListCellRenderer.UIResource {
    private SynthListCellRenderer() {}
    
    public String getName() { return "List.cellRenderer"; }
    
    public void setBorder(Border param1Border) {
      if (SynthListUI.this.useUIBorder || param1Border instanceof SynthBorder)
        super.setBorder(param1Border); 
    }
    
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      if (!SynthListUI.this.useListColors && (param1Boolean1 || param1Boolean2)) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), param1Boolean1, param1Boolean2, param1JList.isEnabled(), false);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      } 
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      return this;
    }
    
    public void paint(Graphics param1Graphics) {
      super.paint(param1Graphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthListUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */