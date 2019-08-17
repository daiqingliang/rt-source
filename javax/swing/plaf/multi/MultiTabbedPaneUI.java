package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;

public class MultiTabbedPaneUI extends TabbedPaneUI {
  protected Vector uis = new Vector();
  
  public ComponentUI[] getUIs() { return MultiLookAndFeel.uisToArray(this.uis); }
  
  public int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2) {
    int i = ((TabbedPaneUI)this.uis.elementAt(0)).tabForCoordinate(paramJTabbedPane, paramInt1, paramInt2);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TabbedPaneUI)this.uis.elementAt(b)).tabForCoordinate(paramJTabbedPane, paramInt1, paramInt2); 
    return i;
  }
  
  public Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt) {
    Rectangle rectangle = ((TabbedPaneUI)this.uis.elementAt(0)).getTabBounds(paramJTabbedPane, paramInt);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TabbedPaneUI)this.uis.elementAt(b)).getTabBounds(paramJTabbedPane, paramInt); 
    return rectangle;
  }
  
  public int getTabRunCount(JTabbedPane paramJTabbedPane) {
    int i = ((TabbedPaneUI)this.uis.elementAt(0)).getTabRunCount(paramJTabbedPane);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TabbedPaneUI)this.uis.elementAt(b)).getTabRunCount(paramJTabbedPane); 
    return i;
  }
  
  public boolean contains(JComponent paramJComponent, int paramInt1, int paramInt2) {
    boolean bool = ((ComponentUI)this.uis.elementAt(0)).contains(paramJComponent, paramInt1, paramInt2);
    for (byte b = 1; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).contains(paramJComponent, paramInt1, paramInt2); 
    return bool;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).update(paramGraphics, paramJComponent); 
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    MultiTabbedPaneUI multiTabbedPaneUI = new MultiTabbedPaneUI();
    return MultiLookAndFeel.createUIs(multiTabbedPaneUI, ((MultiTabbedPaneUI)multiTabbedPaneUI).uis, paramJComponent);
  }
  
  public void installUI(JComponent paramJComponent) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).installUI(paramJComponent); 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).uninstallUI(paramJComponent); 
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).paint(paramGraphics, paramJComponent); 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension = ((ComponentUI)this.uis.elementAt(0)).getPreferredSize(paramJComponent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).getPreferredSize(paramJComponent); 
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = ((ComponentUI)this.uis.elementAt(0)).getMinimumSize(paramJComponent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).getMinimumSize(paramJComponent); 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = ((ComponentUI)this.uis.elementAt(0)).getMaximumSize(paramJComponent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).getMaximumSize(paramJComponent); 
    return dimension;
  }
  
  public int getAccessibleChildrenCount(JComponent paramJComponent) {
    int i = ((ComponentUI)this.uis.elementAt(0)).getAccessibleChildrenCount(paramJComponent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).getAccessibleChildrenCount(paramJComponent); 
    return i;
  }
  
  public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt) {
    Accessible accessible = ((ComponentUI)this.uis.elementAt(0)).getAccessibleChild(paramJComponent, paramInt);
    for (byte b = 1; b < this.uis.size(); b++)
      ((ComponentUI)this.uis.elementAt(b)).getAccessibleChild(paramJComponent, paramInt); 
    return accessible;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */