package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;

public class MultiPopupMenuUI extends PopupMenuUI {
  protected Vector uis = new Vector();
  
  public ComponentUI[] getUIs() { return MultiLookAndFeel.uisToArray(this.uis); }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent) {
    boolean bool = ((PopupMenuUI)this.uis.elementAt(0)).isPopupTrigger(paramMouseEvent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((PopupMenuUI)this.uis.elementAt(b)).isPopupTrigger(paramMouseEvent); 
    return bool;
  }
  
  public Popup getPopup(JPopupMenu paramJPopupMenu, int paramInt1, int paramInt2) {
    Popup popup = ((PopupMenuUI)this.uis.elementAt(0)).getPopup(paramJPopupMenu, paramInt1, paramInt2);
    for (byte b = 1; b < this.uis.size(); b++)
      ((PopupMenuUI)this.uis.elementAt(b)).getPopup(paramJPopupMenu, paramInt1, paramInt2); 
    return popup;
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
    MultiPopupMenuUI multiPopupMenuUI = new MultiPopupMenuUI();
    return MultiLookAndFeel.createUIs(multiPopupMenuUI, ((MultiPopupMenuUI)multiPopupMenuUI).uis, paramJComponent);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */