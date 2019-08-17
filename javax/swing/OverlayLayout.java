package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class OverlayLayout implements LayoutManager2, Serializable {
  private Container target;
  
  private SizeRequirements[] xChildren;
  
  private SizeRequirements[] yChildren;
  
  private SizeRequirements xTotal;
  
  private SizeRequirements yTotal;
  
  @ConstructorProperties({"target"})
  public OverlayLayout(Container paramContainer) { this.target = paramContainer; }
  
  public final Container getTarget() { return this.target; }
  
  public void invalidateLayout(Container paramContainer) {
    checkContainer(paramContainer);
    this.xChildren = null;
    this.yChildren = null;
    this.xTotal = null;
    this.yTotal = null;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) { invalidateLayout(paramComponent.getParent()); }
  
  public void removeLayoutComponent(Component paramComponent) { invalidateLayout(paramComponent.getParent()); }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) { invalidateLayout(paramComponent.getParent()); }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    checkContainer(paramContainer);
    checkRequests();
    Dimension dimension = new Dimension(this.xTotal.preferred, this.yTotal.preferred);
    Insets insets = paramContainer.getInsets();
    dimension.width += insets.left + insets.right;
    dimension.height += insets.top + insets.bottom;
    return dimension;
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    checkContainer(paramContainer);
    checkRequests();
    Dimension dimension = new Dimension(this.xTotal.minimum, this.yTotal.minimum);
    Insets insets = paramContainer.getInsets();
    dimension.width += insets.left + insets.right;
    dimension.height += insets.top + insets.bottom;
    return dimension;
  }
  
  public Dimension maximumLayoutSize(Container paramContainer) {
    checkContainer(paramContainer);
    checkRequests();
    Dimension dimension = new Dimension(this.xTotal.maximum, this.yTotal.maximum);
    Insets insets = paramContainer.getInsets();
    dimension.width += insets.left + insets.right;
    dimension.height += insets.top + insets.bottom;
    return dimension;
  }
  
  public float getLayoutAlignmentX(Container paramContainer) {
    checkContainer(paramContainer);
    checkRequests();
    return this.xTotal.alignment;
  }
  
  public float getLayoutAlignmentY(Container paramContainer) {
    checkContainer(paramContainer);
    checkRequests();
    return this.yTotal.alignment;
  }
  
  public void layoutContainer(Container paramContainer) {
    checkContainer(paramContainer);
    checkRequests();
    int i = paramContainer.getComponentCount();
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int[] arrayOfInt3 = new int[i];
    int[] arrayOfInt4 = new int[i];
    Dimension dimension = paramContainer.getSize();
    Insets insets = paramContainer.getInsets();
    dimension.width -= insets.left + insets.right;
    dimension.height -= insets.top + insets.bottom;
    SizeRequirements.calculateAlignedPositions(dimension.width, this.xTotal, this.xChildren, arrayOfInt1, arrayOfInt2);
    SizeRequirements.calculateAlignedPositions(dimension.height, this.yTotal, this.yChildren, arrayOfInt3, arrayOfInt4);
    for (byte b = 0; b < i; b++) {
      Component component = paramContainer.getComponent(b);
      component.setBounds(insets.left + arrayOfInt1[b], insets.top + arrayOfInt3[b], arrayOfInt2[b], arrayOfInt4[b]);
    } 
  }
  
  void checkContainer(Container paramContainer) {
    if (this.target != paramContainer)
      throw new AWTError("OverlayLayout can't be shared"); 
  }
  
  void checkRequests() {
    if (this.xChildren == null || this.yChildren == null) {
      int i = this.target.getComponentCount();
      this.xChildren = new SizeRequirements[i];
      this.yChildren = new SizeRequirements[i];
      for (byte b = 0; b < i; b++) {
        Component component = this.target.getComponent(b);
        Dimension dimension1 = component.getMinimumSize();
        Dimension dimension2 = component.getPreferredSize();
        Dimension dimension3 = component.getMaximumSize();
        this.xChildren[b] = new SizeRequirements(dimension1.width, dimension2.width, dimension3.width, component.getAlignmentX());
        this.yChildren[b] = new SizeRequirements(dimension1.height, dimension2.height, dimension3.height, component.getAlignmentY());
      } 
      this.yTotal = (this.xTotal = SizeRequirements.getAlignedSizeRequirements(this.xChildren)).getAlignedSizeRequirements(this.yChildren);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\OverlayLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */