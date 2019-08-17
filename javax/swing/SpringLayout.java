package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpringLayout implements LayoutManager2 {
  private Map<Component, Constraints> componentConstraints = new HashMap();
  
  private Spring cyclicReference = Spring.constant(-2147483648);
  
  private Set<Spring> cyclicSprings;
  
  private Set<Spring> acyclicSprings;
  
  public static final String NORTH = "North";
  
  public static final String SOUTH = "South";
  
  public static final String EAST = "East";
  
  public static final String WEST = "West";
  
  public static final String HORIZONTAL_CENTER = "HorizontalCenter";
  
  public static final String VERTICAL_CENTER = "VerticalCenter";
  
  public static final String BASELINE = "Baseline";
  
  public static final String WIDTH = "Width";
  
  public static final String HEIGHT = "Height";
  
  private static String[] ALL_HORIZONTAL = { "West", "Width", "East", "HorizontalCenter" };
  
  private static String[] ALL_VERTICAL = { "North", "Height", "South", "VerticalCenter", "Baseline" };
  
  private void resetCyclicStatuses() {
    this.cyclicSprings = new HashSet();
    this.acyclicSprings = new HashSet();
  }
  
  private void setParent(Container paramContainer) {
    resetCyclicStatuses();
    Constraints constraints = getConstraints(paramContainer);
    constraints.setX(Spring.constant(0));
    constraints.setY(Spring.constant(0));
    Spring spring1 = constraints.getWidth();
    if (spring1 instanceof Spring.WidthSpring && ((Spring.WidthSpring)spring1).c == paramContainer)
      constraints.setWidth(Spring.constant(0, 0, 2147483647)); 
    Spring spring2 = constraints.getHeight();
    if (spring2 instanceof Spring.HeightSpring && ((Spring.HeightSpring)spring2).c == paramContainer)
      constraints.setHeight(Spring.constant(0, 0, 2147483647)); 
  }
  
  boolean isCyclic(Spring paramSpring) {
    if (paramSpring == null)
      return false; 
    if (this.cyclicSprings.contains(paramSpring))
      return true; 
    if (this.acyclicSprings.contains(paramSpring))
      return false; 
    this.cyclicSprings.add(paramSpring);
    boolean bool = paramSpring.isCyclic(this);
    if (!bool) {
      this.acyclicSprings.add(paramSpring);
      this.cyclicSprings.remove(paramSpring);
    } else {
      System.err.println(paramSpring + " is cyclic. ");
    } 
    return bool;
  }
  
  private Spring abandonCycles(Spring paramSpring) { return isCyclic(paramSpring) ? this.cyclicReference : paramSpring; }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) { this.componentConstraints.remove(paramComponent); }
  
  private static Dimension addInsets(int paramInt1, int paramInt2, Container paramContainer) {
    Insets insets = paramContainer.getInsets();
    return new Dimension(paramInt1 + insets.left + insets.right, paramInt2 + insets.top + insets.bottom);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    setParent(paramContainer);
    Constraints constraints = getConstraints(paramContainer);
    return addInsets(abandonCycles(constraints.getWidth()).getMinimumValue(), abandonCycles(constraints.getHeight()).getMinimumValue(), paramContainer);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    setParent(paramContainer);
    Constraints constraints = getConstraints(paramContainer);
    return addInsets(abandonCycles(constraints.getWidth()).getPreferredValue(), abandonCycles(constraints.getHeight()).getPreferredValue(), paramContainer);
  }
  
  public Dimension maximumLayoutSize(Container paramContainer) {
    setParent(paramContainer);
    Constraints constraints = getConstraints(paramContainer);
    return addInsets(abandonCycles(constraints.getWidth()).getMaximumValue(), abandonCycles(constraints.getHeight()).getMaximumValue(), paramContainer);
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) {
    if (paramObject instanceof Constraints)
      putConstraints(paramComponent, (Constraints)paramObject); 
  }
  
  public float getLayoutAlignmentX(Container paramContainer) { return 0.5F; }
  
  public float getLayoutAlignmentY(Container paramContainer) { return 0.5F; }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void putConstraint(String paramString1, Component paramComponent1, int paramInt, String paramString2, Component paramComponent2) { putConstraint(paramString1, paramComponent1, Spring.constant(paramInt), paramString2, paramComponent2); }
  
  public void putConstraint(String paramString1, Component paramComponent1, Spring paramSpring, String paramString2, Component paramComponent2) { putConstraint(paramString1, paramComponent1, Spring.sum(paramSpring, getConstraint(paramString2, paramComponent2))); }
  
  private void putConstraint(String paramString, Component paramComponent, Spring paramSpring) {
    if (paramSpring != null)
      getConstraints(paramComponent).setConstraint(paramString, paramSpring); 
  }
  
  private Constraints applyDefaults(Component paramComponent, Constraints paramConstraints) {
    if (paramConstraints == null)
      paramConstraints = new Constraints(); 
    if (paramConstraints.c == null)
      paramConstraints.c = paramComponent; 
    if (paramConstraints.horizontalHistory.size() < 2)
      applyDefaults(paramConstraints, "West", Spring.constant(0), "Width", Spring.width(paramComponent), paramConstraints.horizontalHistory); 
    if (paramConstraints.verticalHistory.size() < 2)
      applyDefaults(paramConstraints, "North", Spring.constant(0), "Height", Spring.height(paramComponent), paramConstraints.verticalHistory); 
    return paramConstraints;
  }
  
  private void applyDefaults(Constraints paramConstraints, String paramString1, Spring paramSpring1, String paramString2, Spring paramSpring2, List<String> paramList) {
    if (paramList.size() == 0) {
      paramConstraints.setConstraint(paramString1, paramSpring1);
      paramConstraints.setConstraint(paramString2, paramSpring2);
    } else {
      if (paramConstraints.getConstraint(paramString2) == null) {
        paramConstraints.setConstraint(paramString2, paramSpring2);
      } else {
        paramConstraints.setConstraint(paramString1, paramSpring1);
      } 
      Collections.rotate(paramList, 1);
    } 
  }
  
  private void putConstraints(Component paramComponent, Constraints paramConstraints) { this.componentConstraints.put(paramComponent, applyDefaults(paramComponent, paramConstraints)); }
  
  public Constraints getConstraints(Component paramComponent) {
    Constraints constraints = (Constraints)this.componentConstraints.get(paramComponent);
    if (constraints == null) {
      if (paramComponent instanceof JComponent) {
        Object object = ((JComponent)paramComponent).getClientProperty(SpringLayout.class);
        if (object instanceof Constraints)
          return applyDefaults(paramComponent, (Constraints)object); 
      } 
      constraints = new Constraints();
      putConstraints(paramComponent, constraints);
    } 
    return constraints;
  }
  
  public Spring getConstraint(String paramString, Component paramComponent) {
    paramString = paramString.intern();
    return new SpringProxy(paramString, paramComponent, this);
  }
  
  public void layoutContainer(Container paramContainer) {
    setParent(paramContainer);
    int i = paramContainer.getComponentCount();
    getConstraints(paramContainer).reset();
    for (byte b1 = 0; b1 < i; b1++)
      getConstraints(paramContainer.getComponent(b1)).reset(); 
    Insets insets = paramContainer.getInsets();
    Constraints constraints = getConstraints(paramContainer);
    abandonCycles(constraints.getX()).setValue(0);
    abandonCycles(constraints.getY()).setValue(0);
    abandonCycles(constraints.getWidth()).setValue(paramContainer.getWidth() - insets.left - insets.right);
    abandonCycles(constraints.getHeight()).setValue(paramContainer.getHeight() - insets.top - insets.bottom);
    for (byte b2 = 0; b2 < i; b2++) {
      Component component = paramContainer.getComponent(b2);
      Constraints constraints1 = getConstraints(component);
      int j = abandonCycles(constraints1.getX()).getValue();
      int k = abandonCycles(constraints1.getY()).getValue();
      int m = abandonCycles(constraints1.getWidth()).getValue();
      int n = abandonCycles(constraints1.getHeight()).getValue();
      component.setBounds(insets.left + j, insets.top + k, m, n);
    } 
  }
  
  public static class Constraints {
    private Spring x;
    
    private Spring y;
    
    private Spring width;
    
    private Spring height;
    
    private Spring east;
    
    private Spring south;
    
    private Spring horizontalCenter;
    
    private Spring verticalCenter;
    
    private Spring baseline;
    
    private List<String> horizontalHistory = new ArrayList(2);
    
    private List<String> verticalHistory = new ArrayList(2);
    
    private Component c;
    
    public Constraints() {}
    
    public Constraints(Spring param1Spring1, Spring param1Spring2) {
      setX(param1Spring1);
      setY(param1Spring2);
    }
    
    public Constraints(Spring param1Spring1, Spring param1Spring2, Spring param1Spring3, Spring param1Spring4) {
      setX(param1Spring1);
      setY(param1Spring2);
      setWidth(param1Spring3);
      setHeight(param1Spring4);
    }
    
    public Constraints(Component param1Component) {
      this.c = param1Component;
      setX(Spring.constant(param1Component.getX()));
      setY(Spring.constant(param1Component.getY()));
      setWidth(Spring.width(param1Component));
      setHeight(Spring.height(param1Component));
    }
    
    private void pushConstraint(String param1String, Spring param1Spring, boolean param1Boolean) {
      boolean bool = true;
      List list = param1Boolean ? this.horizontalHistory : this.verticalHistory;
      if (list.contains(param1String)) {
        list.remove(param1String);
        bool = false;
      } else if (list.size() == 2 && param1Spring != null) {
        list.remove(0);
        bool = false;
      } 
      if (param1Spring != null)
        list.add(param1String); 
      if (!bool) {
        String[] arrayOfString = param1Boolean ? ALL_HORIZONTAL : ALL_VERTICAL;
        for (String str : arrayOfString) {
          if (!list.contains(str))
            setConstraint(str, null); 
        } 
      } 
    }
    
    private Spring sum(Spring param1Spring1, Spring param1Spring2) { return (param1Spring1 == null || param1Spring2 == null) ? null : Spring.sum(param1Spring1, param1Spring2); }
    
    private Spring difference(Spring param1Spring1, Spring param1Spring2) { return (param1Spring1 == null || param1Spring2 == null) ? null : Spring.difference(param1Spring1, param1Spring2); }
    
    private Spring scale(Spring param1Spring, float param1Float) { return (param1Spring == null) ? null : Spring.scale(param1Spring, param1Float); }
    
    private int getBaselineFromHeight(int param1Int) { return (param1Int < 0) ? -this.c.getBaseline((this.c.getPreferredSize()).width, -param1Int) : this.c.getBaseline((this.c.getPreferredSize()).width, param1Int); }
    
    private int getHeightFromBaseLine(int param1Int) { // Byte code:
      //   0: aload_0
      //   1: getfield c : Ljava/awt/Component;
      //   4: invokevirtual getPreferredSize : ()Ljava/awt/Dimension;
      //   7: astore_2
      //   8: aload_2
      //   9: getfield height : I
      //   12: istore_3
      //   13: aload_0
      //   14: getfield c : Ljava/awt/Component;
      //   17: aload_2
      //   18: getfield width : I
      //   21: iload_3
      //   22: invokevirtual getBaseline : (II)I
      //   25: istore #4
      //   27: iload #4
      //   29: iload_1
      //   30: if_icmpne -> 35
      //   33: iload_3
      //   34: ireturn
      //   35: getstatic javax/swing/SpringLayout$1.$SwitchMap$java$awt$Component$BaselineResizeBehavior : [I
      //   38: aload_0
      //   39: getfield c : Ljava/awt/Component;
      //   42: invokevirtual getBaselineResizeBehavior : ()Ljava/awt/Component$BaselineResizeBehavior;
      //   45: invokevirtual ordinal : ()I
      //   48: iaload
      //   49: tableswitch default -> 92, 1 -> 76, 2 -> 83, 3 -> 92
      //   76: iload_3
      //   77: iload_1
      //   78: iload #4
      //   80: isub
      //   81: iadd
      //   82: ireturn
      //   83: iload_3
      //   84: iconst_2
      //   85: iload_1
      //   86: iload #4
      //   88: isub
      //   89: imul
      //   90: iadd
      //   91: ireturn
      //   92: ldc -2147483648
      //   94: ireturn }
    
    private Spring heightToRelativeBaseline(Spring param1Spring) { return new Spring.SpringMap(param1Spring) {
          protected int map(int param2Int) { return SpringLayout.Constraints.this.getBaselineFromHeight(param2Int); }
          
          protected int inv(int param2Int) { return SpringLayout.Constraints.this.getHeightFromBaseLine(param2Int); }
        }; }
    
    private Spring relativeBaselineToHeight(Spring param1Spring) { return new Spring.SpringMap(param1Spring) {
          protected int map(int param2Int) { return SpringLayout.Constraints.this.getHeightFromBaseLine(param2Int); }
          
          protected int inv(int param2Int) { return SpringLayout.Constraints.this.getBaselineFromHeight(param2Int); }
        }; }
    
    private boolean defined(List param1List, String param1String1, String param1String2) { return (param1List.contains(param1String1) && param1List.contains(param1String2)); }
    
    public void setX(Spring param1Spring) {
      this.x = param1Spring;
      pushConstraint("West", param1Spring, true);
    }
    
    public Spring getX() {
      if (this.x == null)
        if (defined(this.horizontalHistory, "East", "Width")) {
          this.x = difference(this.east, this.width);
        } else if (defined(this.horizontalHistory, "HorizontalCenter", "Width")) {
          this.x = difference(this.horizontalCenter, scale(this.width, 0.5F));
        } else if (defined(this.horizontalHistory, "HorizontalCenter", "East")) {
          this.x = difference(scale(this.horizontalCenter, 2.0F), this.east);
        }  
      return this.x;
    }
    
    public void setY(Spring param1Spring) {
      this.y = param1Spring;
      pushConstraint("North", param1Spring, false);
    }
    
    public Spring getY() {
      if (this.y == null)
        if (defined(this.verticalHistory, "South", "Height")) {
          this.y = difference(this.south, this.height);
        } else if (defined(this.verticalHistory, "VerticalCenter", "Height")) {
          this.y = difference(this.verticalCenter, scale(this.height, 0.5F));
        } else if (defined(this.verticalHistory, "VerticalCenter", "South")) {
          this.y = difference(scale(this.verticalCenter, 2.0F), this.south);
        } else if (defined(this.verticalHistory, "Baseline", "Height")) {
          this.y = difference(this.baseline, heightToRelativeBaseline(this.height));
        } else if (defined(this.verticalHistory, "Baseline", "South")) {
          this.y = scale(difference(this.baseline, heightToRelativeBaseline(this.south)), 2.0F);
        }  
      return this.y;
    }
    
    public void setWidth(Spring param1Spring) {
      this.width = param1Spring;
      pushConstraint("Width", param1Spring, true);
    }
    
    public Spring getWidth() {
      if (this.width == null)
        if (this.horizontalHistory.contains("East")) {
          this.width = difference(this.east, getX());
        } else if (this.horizontalHistory.contains("HorizontalCenter")) {
          this.width = scale(difference(this.horizontalCenter, getX()), 2.0F);
        }  
      return this.width;
    }
    
    public void setHeight(Spring param1Spring) {
      this.height = param1Spring;
      pushConstraint("Height", param1Spring, false);
    }
    
    public Spring getHeight() {
      if (this.height == null)
        if (this.verticalHistory.contains("South")) {
          this.height = difference(this.south, getY());
        } else if (this.verticalHistory.contains("VerticalCenter")) {
          this.height = scale(difference(this.verticalCenter, getY()), 2.0F);
        } else if (this.verticalHistory.contains("Baseline")) {
          this.height = relativeBaselineToHeight(difference(this.baseline, getY()));
        }  
      return this.height;
    }
    
    private void setEast(Spring param1Spring) {
      this.east = param1Spring;
      pushConstraint("East", param1Spring, true);
    }
    
    private Spring getEast() {
      if (this.east == null)
        this.east = sum(getX(), getWidth()); 
      return this.east;
    }
    
    private void setSouth(Spring param1Spring) {
      this.south = param1Spring;
      pushConstraint("South", param1Spring, false);
    }
    
    private Spring getSouth() {
      if (this.south == null)
        this.south = sum(getY(), getHeight()); 
      return this.south;
    }
    
    private Spring getHorizontalCenter() {
      if (this.horizontalCenter == null)
        this.horizontalCenter = sum(getX(), scale(getWidth(), 0.5F)); 
      return this.horizontalCenter;
    }
    
    private void setHorizontalCenter(Spring param1Spring) {
      this.horizontalCenter = param1Spring;
      pushConstraint("HorizontalCenter", param1Spring, true);
    }
    
    private Spring getVerticalCenter() {
      if (this.verticalCenter == null)
        this.verticalCenter = sum(getY(), scale(getHeight(), 0.5F)); 
      return this.verticalCenter;
    }
    
    private void setVerticalCenter(Spring param1Spring) {
      this.verticalCenter = param1Spring;
      pushConstraint("VerticalCenter", param1Spring, false);
    }
    
    private Spring getBaseline() {
      if (this.baseline == null)
        this.baseline = sum(getY(), heightToRelativeBaseline(getHeight())); 
      return this.baseline;
    }
    
    private void setBaseline(Spring param1Spring) {
      this.baseline = param1Spring;
      pushConstraint("Baseline", param1Spring, false);
    }
    
    public void setConstraint(String param1String, Spring param1Spring) {
      param1String = param1String.intern();
      if (param1String == "West") {
        setX(param1Spring);
      } else if (param1String == "North") {
        setY(param1Spring);
      } else if (param1String == "East") {
        setEast(param1Spring);
      } else if (param1String == "South") {
        setSouth(param1Spring);
      } else if (param1String == "HorizontalCenter") {
        setHorizontalCenter(param1Spring);
      } else if (param1String == "Width") {
        setWidth(param1Spring);
      } else if (param1String == "Height") {
        setHeight(param1Spring);
      } else if (param1String == "VerticalCenter") {
        setVerticalCenter(param1Spring);
      } else if (param1String == "Baseline") {
        setBaseline(param1Spring);
      } 
    }
    
    public Spring getConstraint(String param1String) {
      param1String = param1String.intern();
      return (param1String == "West") ? getX() : ((param1String == "North") ? getY() : ((param1String == "East") ? getEast() : ((param1String == "South") ? getSouth() : ((param1String == "Width") ? getWidth() : ((param1String == "Height") ? getHeight() : ((param1String == "HorizontalCenter") ? getHorizontalCenter() : ((param1String == "VerticalCenter") ? getVerticalCenter() : ((param1String == "Baseline") ? getBaseline() : null))))))));
    }
    
    void reset() {
      Spring[] arrayOfSpring = { this.x, this.y, this.width, this.height, this.east, this.south, this.horizontalCenter, this.verticalCenter, this.baseline };
      for (Spring spring : arrayOfSpring) {
        if (spring != null)
          spring.setValue(-2147483648); 
      } 
    }
  }
  
  private static class SpringProxy extends Spring {
    private String edgeName;
    
    private Component c;
    
    private SpringLayout l;
    
    public SpringProxy(String param1String, Component param1Component, SpringLayout param1SpringLayout) {
      this.edgeName = param1String;
      this.c = param1Component;
      this.l = param1SpringLayout;
    }
    
    private Spring getConstraint() { return this.l.getConstraints(this.c).getConstraint(this.edgeName); }
    
    public int getMinimumValue() { return getConstraint().getMinimumValue(); }
    
    public int getPreferredValue() { return getConstraint().getPreferredValue(); }
    
    public int getMaximumValue() { return getConstraint().getMaximumValue(); }
    
    public int getValue() { return getConstraint().getValue(); }
    
    public void setValue(int param1Int) { getConstraint().setValue(param1Int); }
    
    boolean isCyclic(SpringLayout param1SpringLayout) { return param1SpringLayout.isCyclic(getConstraint()); }
    
    public String toString() { return "SpringProxy for " + this.edgeName + " edge of " + this.c.getName() + "."; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SpringLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */