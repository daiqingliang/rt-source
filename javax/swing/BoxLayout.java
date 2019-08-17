package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.beans.ConstructorProperties;
import java.io.PrintStream;
import java.io.Serializable;

public class BoxLayout implements LayoutManager2, Serializable {
  public static final int X_AXIS = 0;
  
  public static final int Y_AXIS = 1;
  
  public static final int LINE_AXIS = 2;
  
  public static final int PAGE_AXIS = 3;
  
  private int axis;
  
  private Container target;
  
  private SizeRequirements[] xChildren;
  
  private SizeRequirements[] yChildren;
  
  private SizeRequirements xTotal;
  
  private SizeRequirements yTotal;
  
  private PrintStream dbg;
  
  @ConstructorProperties({"target", "axis"})
  public BoxLayout(Container paramContainer, int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2 && paramInt != 3)
      throw new AWTError("Invalid axis"); 
    this.axis = paramInt;
    this.target = paramContainer;
  }
  
  BoxLayout(Container paramContainer, int paramInt, PrintStream paramPrintStream) {
    this(paramContainer, paramInt);
    this.dbg = paramPrintStream;
  }
  
  public final Container getTarget() { return this.target; }
  
  public final int getAxis() { return this.axis; }
  
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
    Dimension dimension;
    synchronized (this) {
      checkContainer(paramContainer);
      checkRequests();
      dimension = new Dimension(this.xTotal.preferred, this.yTotal.preferred);
    } 
    Insets insets = paramContainer.getInsets();
    dimension.width = (int)Math.min(dimension.width + insets.left + insets.right, 2147483647L);
    dimension.height = (int)Math.min(dimension.height + insets.top + insets.bottom, 2147483647L);
    return dimension;
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    Dimension dimension;
    synchronized (this) {
      checkContainer(paramContainer);
      checkRequests();
      dimension = new Dimension(this.xTotal.minimum, this.yTotal.minimum);
    } 
    Insets insets = paramContainer.getInsets();
    dimension.width = (int)Math.min(dimension.width + insets.left + insets.right, 2147483647L);
    dimension.height = (int)Math.min(dimension.height + insets.top + insets.bottom, 2147483647L);
    return dimension;
  }
  
  public Dimension maximumLayoutSize(Container paramContainer) {
    Dimension dimension;
    synchronized (this) {
      checkContainer(paramContainer);
      checkRequests();
      dimension = new Dimension(this.xTotal.maximum, this.yTotal.maximum);
    } 
    Insets insets = paramContainer.getInsets();
    dimension.width = (int)Math.min(dimension.width + insets.left + insets.right, 2147483647L);
    dimension.height = (int)Math.min(dimension.height + insets.top + insets.bottom, 2147483647L);
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
    int i = paramContainer.getComponentCount();
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int[] arrayOfInt3 = new int[i];
    int[] arrayOfInt4 = new int[i];
    Dimension dimension = paramContainer.getSize();
    Insets insets = paramContainer.getInsets();
    dimension.width -= insets.left + insets.right;
    dimension.height -= insets.top + insets.bottom;
    ComponentOrientation componentOrientation = paramContainer.getComponentOrientation();
    int j = resolveAxis(this.axis, componentOrientation);
    boolean bool = (j != this.axis) ? componentOrientation.isLeftToRight() : 1;
    synchronized (this) {
      checkRequests();
      if (j == 0) {
        SizeRequirements.calculateTiledPositions(dimension.width, this.xTotal, this.xChildren, arrayOfInt1, arrayOfInt2, bool);
        SizeRequirements.calculateAlignedPositions(dimension.height, this.yTotal, this.yChildren, arrayOfInt3, arrayOfInt4);
      } else {
        SizeRequirements.calculateAlignedPositions(dimension.width, this.xTotal, this.xChildren, arrayOfInt1, arrayOfInt2, bool);
        SizeRequirements.calculateTiledPositions(dimension.height, this.yTotal, this.yChildren, arrayOfInt3, arrayOfInt4);
      } 
    } 
    byte b;
    for (b = 0; b < i; b++) {
      Component component = paramContainer.getComponent(b);
      component.setBounds((int)Math.min(insets.left + arrayOfInt1[b], 2147483647L), (int)Math.min(insets.top + arrayOfInt3[b], 2147483647L), arrayOfInt2[b], arrayOfInt4[b]);
    } 
    if (this.dbg != null)
      for (b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        this.dbg.println(component.toString());
        this.dbg.println("X: " + this.xChildren[b]);
        this.dbg.println("Y: " + this.yChildren[b]);
      }  
  }
  
  void checkContainer(Container paramContainer) {
    if (this.target != paramContainer)
      throw new AWTError("BoxLayout can't be shared"); 
  }
  
  void checkRequests() {
    if (this.xChildren == null || this.yChildren == null) {
      int i = this.target.getComponentCount();
      this.xChildren = new SizeRequirements[i];
      this.yChildren = new SizeRequirements[i];
      int j;
      for (j = 0; j < i; j++) {
        Component component = this.target.getComponent(j);
        if (!component.isVisible()) {
          this.xChildren[j] = new SizeRequirements(0, 0, 0, component.getAlignmentX());
          this.yChildren[j] = new SizeRequirements(0, 0, 0, component.getAlignmentY());
        } else {
          Dimension dimension1 = component.getMinimumSize();
          Dimension dimension2 = component.getPreferredSize();
          Dimension dimension3 = component.getMaximumSize();
          this.xChildren[j] = new SizeRequirements(dimension1.width, dimension2.width, dimension3.width, component.getAlignmentX());
          this.yChildren[j] = new SizeRequirements(dimension1.height, dimension2.height, dimension3.height, component.getAlignmentY());
        } 
      } 
      j = resolveAxis(this.axis, this.target.getComponentOrientation());
      if (j == 0) {
        this.yTotal = (this.xTotal = SizeRequirements.getTiledSizeRequirements(this.xChildren)).getAlignedSizeRequirements(this.yChildren);
      } else {
        this.yTotal = (this.xTotal = SizeRequirements.getAlignedSizeRequirements(this.xChildren)).getTiledSizeRequirements(this.yChildren);
      } 
    } 
  }
  
  private int resolveAxis(int paramInt, ComponentOrientation paramComponentOrientation) {
    int i;
    if (paramInt == 2) {
      i = paramComponentOrientation.isHorizontal() ? 0 : 1;
    } else if (paramInt == 3) {
      i = paramComponentOrientation.isHorizontal() ? 1 : 0;
    } else {
      i = paramInt;
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\BoxLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */