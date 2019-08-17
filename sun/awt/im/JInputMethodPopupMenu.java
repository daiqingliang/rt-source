package sun.awt.im;

import java.awt.Component;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class JInputMethodPopupMenu extends InputMethodPopupMenu {
  static JPopupMenu delegate = null;
  
  JInputMethodPopupMenu(String paramString) {
    synchronized (this) {
      if (delegate == null)
        delegate = new JPopupMenu(paramString); 
    } 
  }
  
  void show(Component paramComponent, int paramInt1, int paramInt2) { delegate.show(paramComponent, paramInt1, paramInt2); }
  
  void removeAll() { delegate.removeAll(); }
  
  void addSeparator() { delegate.addSeparator(); }
  
  void addToComponent(Component paramComponent) {}
  
  Object createSubmenu(String paramString) { return new JMenu(paramString); }
  
  void add(Object paramObject) { delegate.add((JMenuItem)paramObject); }
  
  void addMenuItem(String paramString1, String paramString2, String paramString3) { addMenuItem(delegate, paramString1, paramString2, paramString3); }
  
  void addMenuItem(Object paramObject, String paramString1, String paramString2, String paramString3) {
    JMenuItem jMenuItem;
    if (isSelected(paramString2, paramString3)) {
      jMenuItem = new JCheckBoxMenuItem(paramString1, true);
    } else {
      jMenuItem = new JMenuItem(paramString1);
    } 
    jMenuItem.setActionCommand(paramString2);
    jMenuItem.addActionListener(this);
    jMenuItem.setEnabled((paramString2 != null));
    if (paramObject instanceof JMenu) {
      ((JMenu)paramObject).add(jMenuItem);
    } else {
      ((JPopupMenu)paramObject).add(jMenuItem);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\JInputMethodPopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */