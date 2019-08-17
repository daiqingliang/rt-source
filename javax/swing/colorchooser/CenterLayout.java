package javax.swing.colorchooser;

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
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      return dimension;
    } 
    return new Dimension(0, 0);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) { return preferredLayoutSize(paramContainer); }
  
  public void layoutContainer(Container paramContainer) {
    try {
      Component component = paramContainer.getComponent(0);
      component.setSize(component.getPreferredSize());
      Dimension dimension1 = component.getSize();
      Dimension dimension2 = paramContainer.getSize();
      Insets insets = paramContainer.getInsets();
      dimension2.width -= insets.left + insets.right;
      dimension2.height -= insets.top + insets.bottom;
      int i = dimension2.width / 2 - dimension1.width / 2;
      int j = dimension2.height / 2 - dimension1.height / 2;
      i += insets.left;
      j += insets.top;
      component.setBounds(i, j, dimension1.width, dimension1.height);
    } catch (Exception exception) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\CenterLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */