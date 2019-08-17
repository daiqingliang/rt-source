package javax.swing.plaf.basic;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import javax.swing.plaf.UIResource;
import sun.swing.MenuItemLayoutHelper;

public class DefaultMenuLayout extends BoxLayout implements UIResource {
  public DefaultMenuLayout(Container paramContainer, int paramInt) { super(paramContainer, paramInt); }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    if (paramContainer instanceof JPopupMenu) {
      JPopupMenu jPopupMenu = (JPopupMenu)paramContainer;
      MenuItemLayoutHelper.clearUsedClientProperties(jPopupMenu);
      if (jPopupMenu.getComponentCount() == 0)
        return new Dimension(0, 0); 
    } 
    invalidateLayout(paramContainer);
    return super.preferredLayoutSize(paramContainer);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\DefaultMenuLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */