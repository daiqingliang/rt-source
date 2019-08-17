package sun.awt.im;

import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;

class AWTInputMethodPopupMenu extends InputMethodPopupMenu {
  static PopupMenu delegate = null;
  
  AWTInputMethodPopupMenu(String paramString) {
    synchronized (this) {
      if (delegate == null)
        delegate = new PopupMenu(paramString); 
    } 
  }
  
  void show(Component paramComponent, int paramInt1, int paramInt2) { delegate.show(paramComponent, paramInt1, paramInt2); }
  
  void removeAll() { delegate.removeAll(); }
  
  void addSeparator() { delegate.addSeparator(); }
  
  void addToComponent(Component paramComponent) { paramComponent.add(delegate); }
  
  Object createSubmenu(String paramString) { return new Menu(paramString); }
  
  void add(Object paramObject) { delegate.add((MenuItem)paramObject); }
  
  void addMenuItem(String paramString1, String paramString2, String paramString3) { addMenuItem(delegate, paramString1, paramString2, paramString3); }
  
  void addMenuItem(Object paramObject, String paramString1, String paramString2, String paramString3) {
    MenuItem menuItem;
    if (isSelected(paramString2, paramString3)) {
      menuItem = new CheckboxMenuItem(paramString1, true);
    } else {
      menuItem = new MenuItem(paramString1);
    } 
    menuItem.setActionCommand(paramString2);
    menuItem.addActionListener(this);
    menuItem.setEnabled((paramString2 != null));
    ((Menu)paramObject).add(menuItem);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\AWTInputMethodPopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */