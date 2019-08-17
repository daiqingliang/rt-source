package java.awt;

import java.io.Serializable;

public class BorderLayout implements LayoutManager2, Serializable {
  int hgap;
  
  int vgap;
  
  Component north;
  
  Component west;
  
  Component east;
  
  Component south;
  
  Component center;
  
  Component firstLine;
  
  Component lastLine;
  
  Component firstItem;
  
  Component lastItem;
  
  public static final String NORTH = "North";
  
  public static final String SOUTH = "South";
  
  public static final String EAST = "East";
  
  public static final String WEST = "West";
  
  public static final String CENTER = "Center";
  
  public static final String BEFORE_FIRST_LINE = "First";
  
  public static final String AFTER_LAST_LINE = "Last";
  
  public static final String BEFORE_LINE_BEGINS = "Before";
  
  public static final String AFTER_LINE_ENDS = "After";
  
  public static final String PAGE_START = "First";
  
  public static final String PAGE_END = "Last";
  
  public static final String LINE_START = "Before";
  
  public static final String LINE_END = "After";
  
  private static final long serialVersionUID = -8658291919501921765L;
  
  public BorderLayout() { this(0, 0); }
  
  public BorderLayout(int paramInt1, int paramInt2) {
    this.hgap = paramInt1;
    this.vgap = paramInt2;
  }
  
  public int getHgap() { return this.hgap; }
  
  public void setHgap(int paramInt) { this.hgap = paramInt; }
  
  public int getVgap() { return this.vgap; }
  
  public void setVgap(int paramInt) { this.vgap = paramInt; }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) {
    synchronized (paramComponent.getTreeLock()) {
      if (paramObject == null || paramObject instanceof String) {
        addLayoutComponent((String)paramObject, paramComponent);
      } else {
        throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
      } 
    } 
  }
  
  @Deprecated
  public void addLayoutComponent(String paramString, Component paramComponent) {
    synchronized (paramComponent.getTreeLock()) {
      if (paramString == null)
        paramString = "Center"; 
      if ("Center".equals(paramString)) {
        this.center = paramComponent;
      } else if ("North".equals(paramString)) {
        this.north = paramComponent;
      } else if ("South".equals(paramString)) {
        this.south = paramComponent;
      } else if ("East".equals(paramString)) {
        this.east = paramComponent;
      } else if ("West".equals(paramString)) {
        this.west = paramComponent;
      } else if ("First".equals(paramString)) {
        this.firstLine = paramComponent;
      } else if ("Last".equals(paramString)) {
        this.lastLine = paramComponent;
      } else if ("Before".equals(paramString)) {
        this.firstItem = paramComponent;
      } else if ("After".equals(paramString)) {
        this.lastItem = paramComponent;
      } else {
        throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + paramString);
      } 
    } 
  }
  
  public void removeLayoutComponent(Component paramComponent) {
    synchronized (paramComponent.getTreeLock()) {
      if (paramComponent == this.center) {
        this.center = null;
      } else if (paramComponent == this.north) {
        this.north = null;
      } else if (paramComponent == this.south) {
        this.south = null;
      } else if (paramComponent == this.east) {
        this.east = null;
      } else if (paramComponent == this.west) {
        this.west = null;
      } 
      if (paramComponent == this.firstLine) {
        this.firstLine = null;
      } else if (paramComponent == this.lastLine) {
        this.lastLine = null;
      } else if (paramComponent == this.firstItem) {
        this.firstItem = null;
      } else if (paramComponent == this.lastItem) {
        this.lastItem = null;
      } 
    } 
  }
  
  public Component getLayoutComponent(Object paramObject) {
    if ("Center".equals(paramObject))
      return this.center; 
    if ("North".equals(paramObject))
      return this.north; 
    if ("South".equals(paramObject))
      return this.south; 
    if ("West".equals(paramObject))
      return this.west; 
    if ("East".equals(paramObject))
      return this.east; 
    if ("First".equals(paramObject))
      return this.firstLine; 
    if ("Last".equals(paramObject))
      return this.lastLine; 
    if ("Before".equals(paramObject))
      return this.firstItem; 
    if ("After".equals(paramObject))
      return this.lastItem; 
    throw new IllegalArgumentException("cannot get component: unknown constraint: " + paramObject);
  }
  
  public Component getLayoutComponent(Container paramContainer, Object paramObject) {
    boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
    Component component = null;
    if ("North".equals(paramObject)) {
      component = (this.firstLine != null) ? this.firstLine : this.north;
    } else if ("South".equals(paramObject)) {
      component = (this.lastLine != null) ? this.lastLine : this.south;
    } else if ("West".equals(paramObject)) {
      component = bool ? this.firstItem : this.lastItem;
      if (component == null)
        component = this.west; 
    } else if ("East".equals(paramObject)) {
      component = bool ? this.lastItem : this.firstItem;
      if (component == null)
        component = this.east; 
    } else if ("Center".equals(paramObject)) {
      component = this.center;
    } else {
      throw new IllegalArgumentException("cannot get component: invalid constraint: " + paramObject);
    } 
    return component;
  }
  
  public Object getConstraints(Component paramComponent) { return (paramComponent == null) ? null : ((paramComponent == this.center) ? "Center" : ((paramComponent == this.north) ? "North" : ((paramComponent == this.south) ? "South" : ((paramComponent == this.west) ? "West" : ((paramComponent == this.east) ? "East" : ((paramComponent == this.firstLine) ? "First" : ((paramComponent == this.lastLine) ? "Last" : ((paramComponent == this.firstItem) ? "Before" : ((paramComponent == this.lastItem) ? "After" : null))))))))); }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Dimension dimension = new Dimension(0, 0);
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      Component component = null;
      if ((component = getChild("East", bool)) != null) {
        Dimension dimension1 = component.getMinimumSize();
        dimension.width += dimension1.width + this.hgap;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if ((component = getChild("West", bool)) != null) {
        Dimension dimension1 = component.getMinimumSize();
        dimension.width += dimension1.width + this.hgap;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if ((component = getChild("Center", bool)) != null) {
        Dimension dimension1 = component.getMinimumSize();
        dimension.width += dimension1.width;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if ((component = getChild("North", bool)) != null) {
        Dimension dimension1 = component.getMinimumSize();
        dimension.width = Math.max(dimension1.width, dimension.width);
        dimension.height += dimension1.height + this.vgap;
      } 
      if ((component = getChild("South", bool)) != null) {
        Dimension dimension1 = component.getMinimumSize();
        dimension.width = Math.max(dimension1.width, dimension.width);
        dimension.height += dimension1.height + this.vgap;
      } 
      Insets insets = paramContainer.getInsets();
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      return dimension;
    } 
  }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Dimension dimension = new Dimension(0, 0);
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      Component component = null;
      if ((component = getChild("East", bool)) != null) {
        Dimension dimension1 = component.getPreferredSize();
        dimension.width += dimension1.width + this.hgap;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if ((component = getChild("West", bool)) != null) {
        Dimension dimension1 = component.getPreferredSize();
        dimension.width += dimension1.width + this.hgap;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if ((component = getChild("Center", bool)) != null) {
        Dimension dimension1 = component.getPreferredSize();
        dimension.width += dimension1.width;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if ((component = getChild("North", bool)) != null) {
        Dimension dimension1 = component.getPreferredSize();
        dimension.width = Math.max(dimension1.width, dimension.width);
        dimension.height += dimension1.height + this.vgap;
      } 
      if ((component = getChild("South", bool)) != null) {
        Dimension dimension1 = component.getPreferredSize();
        dimension.width = Math.max(dimension1.width, dimension.width);
        dimension.height += dimension1.height + this.vgap;
      } 
      Insets insets = paramContainer.getInsets();
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      return dimension;
    } 
  }
  
  public Dimension maximumLayoutSize(Container paramContainer) { return new Dimension(2147483647, 2147483647); }
  
  public float getLayoutAlignmentX(Container paramContainer) { return 0.5F; }
  
  public float getLayoutAlignmentY(Container paramContainer) { return 0.5F; }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void layoutContainer(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = insets.top;
      int j = paramContainer.height - insets.bottom;
      int k = insets.left;
      int m = paramContainer.width - insets.right;
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      Component component = null;
      if ((component = getChild("North", bool)) != null) {
        component.setSize(m - k, component.height);
        Dimension dimension = component.getPreferredSize();
        component.setBounds(k, i, m - k, dimension.height);
        i += dimension.height + this.vgap;
      } 
      if ((component = getChild("South", bool)) != null) {
        component.setSize(m - k, component.height);
        Dimension dimension = component.getPreferredSize();
        component.setBounds(k, j - dimension.height, m - k, dimension.height);
        j -= dimension.height + this.vgap;
      } 
      if ((component = getChild("East", bool)) != null) {
        component.setSize(component.width, j - i);
        Dimension dimension = component.getPreferredSize();
        component.setBounds(m - dimension.width, i, dimension.width, j - i);
        m -= dimension.width + this.hgap;
      } 
      if ((component = getChild("West", bool)) != null) {
        component.setSize(component.width, j - i);
        Dimension dimension = component.getPreferredSize();
        component.setBounds(k, i, dimension.width, j - i);
        k += dimension.width + this.hgap;
      } 
      if ((component = getChild("Center", bool)) != null)
        component.setBounds(k, i, m - k, j - i); 
    } 
  }
  
  private Component getChild(String paramString, boolean paramBoolean) {
    Component component = null;
    if (paramString == "North") {
      component = (this.firstLine != null) ? this.firstLine : this.north;
    } else if (paramString == "South") {
      component = (this.lastLine != null) ? this.lastLine : this.south;
    } else if (paramString == "West") {
      component = paramBoolean ? this.firstItem : this.lastItem;
      if (component == null)
        component = this.west; 
    } else if (paramString == "East") {
      component = paramBoolean ? this.lastItem : this.firstItem;
      if (component == null)
        component = this.east; 
    } else if (paramString == "Center") {
      component = this.center;
    } 
    if (component != null && !component.visible)
      component = null; 
    return component;
  }
  
  public String toString() { return getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\BorderLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */