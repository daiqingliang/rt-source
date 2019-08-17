package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTabbedPane;

public abstract class TabbedPaneUI extends ComponentUI {
  public abstract int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2);
  
  public abstract Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt);
  
  public abstract int getTabRunCount(JTabbedPane paramJTabbedPane);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\TabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */