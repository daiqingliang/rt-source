package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;

public class ViewportLayout implements LayoutManager, Serializable {
  static ViewportLayout SHARED_INSTANCE = new ViewportLayout();
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    Component component = ((JViewport)paramContainer).getView();
    return (component == null) ? new Dimension(0, 0) : ((component instanceof Scrollable) ? ((Scrollable)component).getPreferredScrollableViewportSize() : component.getPreferredSize());
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) { return new Dimension(4, 4); }
  
  public void layoutContainer(Container paramContainer) {
    JViewport jViewport = (JViewport)paramContainer;
    Component component = jViewport.getView();
    Scrollable scrollable = null;
    if (component == null)
      return; 
    if (component instanceof Scrollable)
      scrollable = (Scrollable)component; 
    Insets insets = jViewport.getInsets();
    Dimension dimension1 = component.getPreferredSize();
    Dimension dimension2 = jViewport.getSize();
    Dimension dimension3 = jViewport.toViewCoordinates(dimension2);
    Dimension dimension4 = new Dimension(dimension1);
    if (scrollable != null) {
      if (scrollable.getScrollableTracksViewportWidth())
        dimension4.width = dimension2.width; 
      if (scrollable.getScrollableTracksViewportHeight())
        dimension4.height = dimension2.height; 
    } 
    Point point = jViewport.getViewPosition();
    if (scrollable == null || jViewport.getParent() == null || jViewport.getParent().getComponentOrientation().isLeftToRight()) {
      if (point.x + dimension3.width > dimension4.width)
        point.x = Math.max(0, dimension4.width - dimension3.width); 
    } else if (dimension3.width > dimension4.width) {
      point.x = dimension4.width - dimension3.width;
    } else {
      point.x = Math.max(0, Math.min(dimension4.width - dimension3.width, point.x));
    } 
    if (point.y + dimension3.height > dimension4.height)
      point.y = Math.max(0, dimension4.height - dimension3.height); 
    if (scrollable == null) {
      if (point.x == 0 && dimension2.width > dimension1.width)
        dimension4.width = dimension2.width; 
      if (point.y == 0 && dimension2.height > dimension1.height)
        dimension4.height = dimension2.height; 
    } 
    jViewport.setViewPosition(point);
    jViewport.setViewSize(dimension4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ViewportLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */