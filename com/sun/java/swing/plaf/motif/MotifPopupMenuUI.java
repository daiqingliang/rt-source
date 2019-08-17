package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import sun.swing.SwingUtilities2;

public class MotifPopupMenuUI extends BasicPopupMenuUI {
  private static Border border = null;
  
  private Font titleFont = null;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifPopupMenuUI(); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    LayoutManager layoutManager = paramJComponent.getLayout();
    Dimension dimension = layoutManager.preferredLayoutSize(paramJComponent);
    String str = ((JPopupMenu)paramJComponent).getLabel();
    if (this.titleFont == null) {
      UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
      this.titleFont = uIDefaults.getFont("PopupMenu.font");
    } 
    FontMetrics fontMetrics = paramJComponent.getFontMetrics(this.titleFont);
    int i = 0;
    if (str != null)
      i += SwingUtilities2.stringWidth(paramJComponent, fontMetrics, str); 
    if (dimension.width < i) {
      dimension.width = i + 8;
      Insets insets = paramJComponent.getInsets();
      if (insets != null)
        dimension.width += insets.left + insets.right; 
      if (border != null) {
        insets = border.getBorderInsets(paramJComponent);
        dimension.width += insets.left + insets.right;
      } 
      return dimension;
    } 
    return null;
  }
  
  protected ChangeListener createChangeListener(JPopupMenu paramJPopupMenu) { return new ChangeListener() {
        public void stateChanged(ChangeEvent param1ChangeEvent) {}
      }; }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent) { return (paramMouseEvent.getID() == 501 && (paramMouseEvent.getModifiers() & 0x4) != 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */