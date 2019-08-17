package com.sun.java.swing.plaf.motif;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MotifScrollPaneUI extends BasicScrollPaneUI {
  private static final Border vsbMarginBorderR = new EmptyBorder(0, 4, 0, 0);
  
  private static final Border vsbMarginBorderL = new EmptyBorder(0, 0, 0, 4);
  
  private static final Border hsbMarginBorder = new EmptyBorder(4, 0, 0, 0);
  
  private CompoundBorder vsbBorder;
  
  private CompoundBorder hsbBorder;
  
  private PropertyChangeListener propertyChangeHandler;
  
  protected void installListeners(JScrollPane paramJScrollPane) {
    super.installListeners(paramJScrollPane);
    this.propertyChangeHandler = createPropertyChangeHandler();
    paramJScrollPane.addPropertyChangeListener(this.propertyChangeHandler);
  }
  
  protected void uninstallListeners(JComponent paramJComponent) {
    super.uninstallListeners(paramJComponent);
    paramJComponent.removePropertyChangeListener(this.propertyChangeHandler);
  }
  
  private PropertyChangeListener createPropertyChangeHandler() { return new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
          String str = param1PropertyChangeEvent.getPropertyName();
          if (str.equals("componentOrientation")) {
            JScrollPane jScrollPane = (JScrollPane)param1PropertyChangeEvent.getSource();
            JScrollBar jScrollBar = jScrollPane.getVerticalScrollBar();
            if (jScrollBar != null && MotifScrollPaneUI.this.vsbBorder != null && jScrollBar.getBorder() == MotifScrollPaneUI.this.vsbBorder) {
              if (MotifGraphicsUtils.isLeftToRight(jScrollPane)) {
                MotifScrollPaneUI.this.vsbBorder = new CompoundBorder(vsbMarginBorderR, MotifScrollPaneUI.this.vsbBorder.getInsideBorder());
              } else {
                MotifScrollPaneUI.this.vsbBorder = new CompoundBorder(vsbMarginBorderL, MotifScrollPaneUI.this.vsbBorder.getInsideBorder());
              } 
              jScrollBar.setBorder(MotifScrollPaneUI.this.vsbBorder);
            } 
          } 
        }
      }; }
  
  protected void installDefaults(JScrollPane paramJScrollPane) {
    super.installDefaults(paramJScrollPane);
    JScrollBar jScrollBar1 = paramJScrollPane.getVerticalScrollBar();
    if (jScrollBar1 != null) {
      if (MotifGraphicsUtils.isLeftToRight(paramJScrollPane)) {
        this.vsbBorder = new CompoundBorder(vsbMarginBorderR, jScrollBar1.getBorder());
      } else {
        this.vsbBorder = new CompoundBorder(vsbMarginBorderL, jScrollBar1.getBorder());
      } 
      jScrollBar1.setBorder(this.vsbBorder);
    } 
    JScrollBar jScrollBar2 = paramJScrollPane.getHorizontalScrollBar();
    if (jScrollBar2 != null) {
      this.hsbBorder = new CompoundBorder(hsbMarginBorder, jScrollBar2.getBorder());
      jScrollBar2.setBorder(this.hsbBorder);
    } 
  }
  
  protected void uninstallDefaults(JScrollPane paramJScrollPane) {
    super.uninstallDefaults(paramJScrollPane);
    JScrollBar jScrollBar1 = this.scrollpane.getVerticalScrollBar();
    if (jScrollBar1 != null) {
      if (jScrollBar1.getBorder() == this.vsbBorder)
        jScrollBar1.setBorder(null); 
      this.vsbBorder = null;
    } 
    JScrollBar jScrollBar2 = this.scrollpane.getHorizontalScrollBar();
    if (jScrollBar2 != null) {
      if (jScrollBar2.getBorder() == this.hsbBorder)
        jScrollBar2.setBorder(null); 
      this.hsbBorder = null;
    } 
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifScrollPaneUI(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */