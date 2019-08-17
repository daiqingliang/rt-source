package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

class CenterLayout implements LayoutManager, Serializable {
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    Component component = paramContainer.getComponent(0);
    if (component != null) {
      Dimension dimension = component.getPreferredSize();
      Insets insets = paramContainer.getInsets();
      return new Dimension(dimension.width + insets.left + insets.right, dimension.height + insets.top + insets.bottom);
    } 
    return new Dimension(0, 0);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) { return preferredLayoutSize(paramContainer); }
  
  public void layoutContainer(Container paramContainer) {
    if (paramContainer.getComponentCount() > 0) {
      Component component = paramContainer.getComponent(0);
      Dimension dimension = component.getPreferredSize();
      int i = paramContainer.getWidth();
      int j = paramContainer.getHeight();
      Insets insets = paramContainer.getInsets();
      i -= insets.left + insets.right;
      j -= insets.top + insets.bottom;
      int k = (i - dimension.width) / 2 + insets.left;
      int m = (j - dimension.height) / 2 + insets.top;
      component.setBounds(k, m, dimension.width, dimension.height);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\CenterLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */