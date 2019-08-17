package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;

public class MultiFileChooserUI extends FileChooserUI {
  protected Vector uis = new Vector();
  
  public ComponentUI[] getUIs() { return MultiLookAndFeel.uisToArray(this.uis); }
  
  public FileFilter getAcceptAllFileFilter(JFileChooser paramJFileChooser) {
    FileFilter fileFilter = ((FileChooserUI)this.uis.elementAt(0)).getAcceptAllFileFilter(paramJFileChooser);
    for (byte b = 1; b < this.uis.size(); b++)
      ((FileChooserUI)this.uis.elementAt(b)).getAcceptAllFileFilter(paramJFileChooser); 
    return fileFilter;
  }
  
  public FileView getFileView(JFileChooser paramJFileChooser) {
    FileView fileView = ((FileChooserUI)this.uis.elementAt(0)).getFileView(paramJFileChooser);
    for (byte b = 1; b < this.uis.size(); b++)
      ((FileChooserUI)this.uis.elementAt(b)).getFileView(paramJFileChooser); 
    return fileView;
  }
  
  public String getApproveButtonText(JFileChooser paramJFileChooser) {
    String str = ((FileChooserUI)this.uis.elementAt(0)).getApproveButtonText(paramJFileChooser);
    for (byte b = 1; b < this.uis.size(); b++)
      ((FileChooserUI)this.uis.elementAt(b)).getApproveButtonText(paramJFileChooser); 
    return str;
  }
  
  public String getDialogTitle(JFileChooser paramJFileChooser) {
    String str = ((FileChooserUI)this.uis.elementAt(0)).getDialogTitle(paramJFileChooser);
    for (byte b = 1; b < this.uis.size(); b++)
      ((FileChooserUI)this.uis.elementAt(b)).getDialogTitle(paramJFileChooser); 
    return str;
  }
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((FileChooserUI)this.uis.elementAt(b)).rescanCurrentDirectory(paramJFileChooser); 
  }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((FileChooserUI)this.uis.elementAt(b)).ensureFileIsVisible(paramJFileChooser, paramFile); 
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
    MultiFileChooserUI multiFileChooserUI = new MultiFileChooserUI();
    return MultiLookAndFeel.createUIs(multiFileChooserUI, ((MultiFileChooserUI)multiFileChooserUI).uis, paramJComponent);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */