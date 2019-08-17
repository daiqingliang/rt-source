package javax.swing.plaf;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JList;

public abstract class ListUI extends ComponentUI {
  public abstract int locationToIndex(JList paramJList, Point paramPoint);
  
  public abstract Point indexToLocation(JList paramJList, int paramInt);
  
  public abstract Rectangle getCellBounds(JList paramJList, int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\ListUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */