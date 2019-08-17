package sun.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public interface MenuItemCheckIconFactory {
  Icon getIcon(JMenuItem paramJMenuItem);
  
  boolean isCompatible(Object paramObject, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\MenuItemCheckIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */