package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import sun.swing.DefaultLookup;

class SynthSplitPaneDivider extends BasicSplitPaneDivider {
  public SynthSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI) { super(paramBasicSplitPaneUI); }
  
  protected void setMouseOver(boolean paramBoolean) {
    if (isMouseOver() != paramBoolean)
      repaint(); 
    super.setMouseOver(paramBoolean);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    super.propertyChange(paramPropertyChangeEvent);
    if (paramPropertyChangeEvent.getSource() == this.splitPane && paramPropertyChangeEvent.getPropertyName() == "orientation") {
      if (this.leftButton instanceof SynthArrowButton)
        ((SynthArrowButton)this.leftButton).setDirection(mapDirection(true)); 
      if (this.rightButton instanceof SynthArrowButton)
        ((SynthArrowButton)this.rightButton).setDirection(mapDirection(false)); 
    } 
  }
  
  public void paint(Graphics paramGraphics) {
    Graphics graphics = paramGraphics.create();
    SynthContext synthContext = ((SynthSplitPaneUI)this.splitPaneUI).getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER);
    Rectangle rectangle = getBounds();
    rectangle.x = rectangle.y = 0;
    SynthLookAndFeel.updateSubregion(synthContext, paramGraphics, rectangle);
    synthContext.getPainter().paintSplitPaneDividerBackground(synthContext, paramGraphics, 0, 0, rectangle.width, rectangle.height, this.splitPane.getOrientation());
    Object object = null;
    synthContext.getPainter().paintSplitPaneDividerForeground(synthContext, paramGraphics, 0, 0, getWidth(), getHeight(), this.splitPane.getOrientation());
    synthContext.dispose();
    for (byte b = 0; b < getComponentCount(); b++) {
      Component component = getComponent(b);
      Rectangle rectangle1 = component.getBounds();
      Graphics graphics1 = paramGraphics.create(rectangle1.x, rectangle1.y, rectangle1.width, rectangle1.height);
      component.paint(graphics1);
      graphics1.dispose();
    } 
    graphics.dispose();
  }
  
  private int mapDirection(boolean paramBoolean) { return paramBoolean ? ((this.splitPane.getOrientation() == 1) ? 7 : 1) : ((this.splitPane.getOrientation() == 1) ? 3 : 5); }
  
  protected JButton createLeftOneTouchButton() {
    SynthArrowButton synthArrowButton = new SynthArrowButton(1);
    int i = lookupOneTouchSize();
    synthArrowButton.setName("SplitPaneDivider.leftOneTouchButton");
    synthArrowButton.setMinimumSize(new Dimension(i, i));
    synthArrowButton.setCursor(Cursor.getPredefinedCursor(0));
    synthArrowButton.setFocusPainted(false);
    synthArrowButton.setBorderPainted(false);
    synthArrowButton.setRequestFocusEnabled(false);
    synthArrowButton.setDirection(mapDirection(true));
    return synthArrowButton;
  }
  
  private int lookupOneTouchSize() { return DefaultLookup.getInt(this.splitPaneUI.getSplitPane(), this.splitPaneUI, "SplitPaneDivider.oneTouchButtonSize", 6); }
  
  protected JButton createRightOneTouchButton() {
    SynthArrowButton synthArrowButton = new SynthArrowButton(1);
    int i = lookupOneTouchSize();
    synthArrowButton.setName("SplitPaneDivider.rightOneTouchButton");
    synthArrowButton.setMinimumSize(new Dimension(i, i));
    synthArrowButton.setCursor(Cursor.getPredefinedCursor(0));
    synthArrowButton.setFocusPainted(false);
    synthArrowButton.setBorderPainted(false);
    synthArrowButton.setRequestFocusEnabled(false);
    synthArrowButton.setDirection(mapDirection(false));
    return synthArrowButton;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */