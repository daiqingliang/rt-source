package java.awt;

public interface LayoutManager {
  void addLayoutComponent(String paramString, Component paramComponent);
  
  void removeLayoutComponent(Component paramComponent);
  
  Dimension preferredLayoutSize(Container paramContainer);
  
  Dimension minimumLayoutSize(Container paramContainer);
  
  void layoutContainer(Container paramContainer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\LayoutManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */