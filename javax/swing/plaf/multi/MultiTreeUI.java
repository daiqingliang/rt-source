package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreePath;

public class MultiTreeUI extends TreeUI {
  protected Vector uis = new Vector();
  
  public ComponentUI[] getUIs() { return MultiLookAndFeel.uisToArray(this.uis); }
  
  public Rectangle getPathBounds(JTree paramJTree, TreePath paramTreePath) {
    Rectangle rectangle = ((TreeUI)this.uis.elementAt(0)).getPathBounds(paramJTree, paramTreePath);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).getPathBounds(paramJTree, paramTreePath); 
    return rectangle;
  }
  
  public TreePath getPathForRow(JTree paramJTree, int paramInt) {
    TreePath treePath = ((TreeUI)this.uis.elementAt(0)).getPathForRow(paramJTree, paramInt);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).getPathForRow(paramJTree, paramInt); 
    return treePath;
  }
  
  public int getRowForPath(JTree paramJTree, TreePath paramTreePath) {
    int i = ((TreeUI)this.uis.elementAt(0)).getRowForPath(paramJTree, paramTreePath);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).getRowForPath(paramJTree, paramTreePath); 
    return i;
  }
  
  public int getRowCount(JTree paramJTree) {
    int i = ((TreeUI)this.uis.elementAt(0)).getRowCount(paramJTree);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).getRowCount(paramJTree); 
    return i;
  }
  
  public TreePath getClosestPathForLocation(JTree paramJTree, int paramInt1, int paramInt2) {
    TreePath treePath = ((TreeUI)this.uis.elementAt(0)).getClosestPathForLocation(paramJTree, paramInt1, paramInt2);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).getClosestPathForLocation(paramJTree, paramInt1, paramInt2); 
    return treePath;
  }
  
  public boolean isEditing(JTree paramJTree) {
    boolean bool = ((TreeUI)this.uis.elementAt(0)).isEditing(paramJTree);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).isEditing(paramJTree); 
    return bool;
  }
  
  public boolean stopEditing(JTree paramJTree) {
    boolean bool = ((TreeUI)this.uis.elementAt(0)).stopEditing(paramJTree);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).stopEditing(paramJTree); 
    return bool;
  }
  
  public void cancelEditing(JTree paramJTree) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).cancelEditing(paramJTree); 
  }
  
  public void startEditingAtPath(JTree paramJTree, TreePath paramTreePath) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).startEditingAtPath(paramJTree, paramTreePath); 
  }
  
  public TreePath getEditingPath(JTree paramJTree) {
    TreePath treePath = ((TreeUI)this.uis.elementAt(0)).getEditingPath(paramJTree);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TreeUI)this.uis.elementAt(b)).getEditingPath(paramJTree); 
    return treePath;
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
    MultiTreeUI multiTreeUI = new MultiTreeUI();
    return MultiLookAndFeel.createUIs(multiTreeUI, ((MultiTreeUI)multiTreeUI).uis, paramJComponent);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */