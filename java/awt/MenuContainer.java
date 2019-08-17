package java.awt;

public interface MenuContainer {
  Font getFont();
  
  void remove(MenuComponent paramMenuComponent);
  
  @Deprecated
  boolean postEvent(Event paramEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MenuContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */