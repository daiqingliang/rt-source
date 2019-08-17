package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MetalScrollPaneUI extends BasicScrollPaneUI {
  private PropertyChangeListener scrollBarSwapListener;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalScrollPaneUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    JScrollPane jScrollPane = (JScrollPane)paramJComponent;
    updateScrollbarsFreeStanding();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    JScrollPane jScrollPane = (JScrollPane)paramJComponent;
    JScrollBar jScrollBar1 = jScrollPane.getHorizontalScrollBar();
    JScrollBar jScrollBar2 = jScrollPane.getVerticalScrollBar();
    if (jScrollBar1 != null)
      jScrollBar1.putClientProperty("JScrollBar.isFreeStanding", null); 
    if (jScrollBar2 != null)
      jScrollBar2.putClientProperty("JScrollBar.isFreeStanding", null); 
  }
  
  public void installListeners(JScrollPane paramJScrollPane) {
    super.installListeners(paramJScrollPane);
    this.scrollBarSwapListener = createScrollBarSwapListener();
    paramJScrollPane.addPropertyChangeListener(this.scrollBarSwapListener);
  }
  
  protected void uninstallListeners(JComponent paramJComponent) {
    super.uninstallListeners(paramJComponent);
    paramJComponent.removePropertyChangeListener(this.scrollBarSwapListener);
  }
  
  @Deprecated
  public void uninstallListeners(JScrollPane paramJScrollPane) {
    super.uninstallListeners(paramJScrollPane);
    paramJScrollPane.removePropertyChangeListener(this.scrollBarSwapListener);
  }
  
  private void updateScrollbarsFreeStanding() {
    Boolean bool;
    if (this.scrollpane == null)
      return; 
    Border border = this.scrollpane.getBorder();
    if (border instanceof MetalBorders.ScrollPaneBorder) {
      bool = Boolean.FALSE;
    } else {
      bool = Boolean.TRUE;
    } 
    JScrollBar jScrollBar = this.scrollpane.getHorizontalScrollBar();
    if (jScrollBar != null)
      jScrollBar.putClientProperty("JScrollBar.isFreeStanding", bool); 
    jScrollBar = this.scrollpane.getVerticalScrollBar();
    if (jScrollBar != null)
      jScrollBar.putClientProperty("JScrollBar.isFreeStanding", bool); 
  }
  
  protected PropertyChangeListener createScrollBarSwapListener() { return new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
          String str = param1PropertyChangeEvent.getPropertyName();
          if (str.equals("verticalScrollBar") || str.equals("horizontalScrollBar")) {
            JScrollBar jScrollBar1 = (JScrollBar)param1PropertyChangeEvent.getOldValue();
            if (jScrollBar1 != null)
              jScrollBar1.putClientProperty("JScrollBar.isFreeStanding", null); 
            JScrollBar jScrollBar2 = (JScrollBar)param1PropertyChangeEvent.getNewValue();
            if (jScrollBar2 != null)
              jScrollBar2.putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE); 
          } else if ("border".equals(str)) {
            MetalScrollPaneUI.this.updateScrollbarsFreeStanding();
          } 
        }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */