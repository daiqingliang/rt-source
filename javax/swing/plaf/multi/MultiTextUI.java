package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public class MultiTextUI extends TextUI {
  protected Vector uis = new Vector();
  
  public ComponentUI[] getUIs() { return MultiLookAndFeel.uisToArray(this.uis); }
  
  public String getToolTipText(JTextComponent paramJTextComponent, Point paramPoint) {
    String str = ((TextUI)this.uis.elementAt(0)).getToolTipText(paramJTextComponent, paramPoint);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).getToolTipText(paramJTextComponent, paramPoint); 
    return str;
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Rectangle rectangle = ((TextUI)this.uis.elementAt(0)).modelToView(paramJTextComponent, paramInt);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).modelToView(paramJTextComponent, paramInt); 
    return rectangle;
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt, Position.Bias paramBias) throws BadLocationException {
    Rectangle rectangle = ((TextUI)this.uis.elementAt(0)).modelToView(paramJTextComponent, paramInt, paramBias);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).modelToView(paramJTextComponent, paramInt, paramBias); 
    return rectangle;
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint) {
    int i = ((TextUI)this.uis.elementAt(0)).viewToModel(paramJTextComponent, paramPoint);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).viewToModel(paramJTextComponent, paramPoint); 
    return i;
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint, Position.Bias[] paramArrayOfBias) {
    int i = ((TextUI)this.uis.elementAt(0)).viewToModel(paramJTextComponent, paramPoint, paramArrayOfBias);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).viewToModel(paramJTextComponent, paramPoint, paramArrayOfBias); 
    return i;
  }
  
  public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    int i = ((TextUI)this.uis.elementAt(0)).getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias); 
    return i;
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).damageRange(paramJTextComponent, paramInt1, paramInt2); 
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2, Position.Bias paramBias1, Position.Bias paramBias2) {
    for (byte b = 0; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).damageRange(paramJTextComponent, paramInt1, paramInt2, paramBias1, paramBias2); 
  }
  
  public EditorKit getEditorKit(JTextComponent paramJTextComponent) {
    EditorKit editorKit = ((TextUI)this.uis.elementAt(0)).getEditorKit(paramJTextComponent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).getEditorKit(paramJTextComponent); 
    return editorKit;
  }
  
  public View getRootView(JTextComponent paramJTextComponent) {
    View view = ((TextUI)this.uis.elementAt(0)).getRootView(paramJTextComponent);
    for (byte b = 1; b < this.uis.size(); b++)
      ((TextUI)this.uis.elementAt(b)).getRootView(paramJTextComponent); 
    return view;
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
    MultiTextUI multiTextUI = new MultiTextUI();
    return MultiLookAndFeel.createUIs(multiTextUI, ((MultiTextUI)multiTextUI).uis, paramJComponent);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */