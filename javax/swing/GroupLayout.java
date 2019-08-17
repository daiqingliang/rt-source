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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupLayout implements LayoutManager2 {
  private static final int MIN_SIZE = 0;
  
  private static final int PREF_SIZE = 1;
  
  private static final int MAX_SIZE = 2;
  
  private static final int SPECIFIC_SIZE = 3;
  
  private static final int UNSET = -2147483648;
  
  public static final int DEFAULT_SIZE = -1;
  
  public static final int PREFERRED_SIZE = -2;
  
  private boolean autocreatePadding;
  
  private boolean autocreateContainerPadding;
  
  private Group horizontalGroup;
  
  private Group verticalGroup;
  
  private Map<Component, ComponentInfo> componentInfos;
  
  private Container host;
  
  private Set<Spring> tmpParallelSet;
  
  private boolean springsChanged;
  
  private boolean isValid;
  
  private boolean hasPreferredPaddingSprings;
  
  private LayoutStyle layoutStyle;
  
  private boolean honorsVisibility;
  
  private static void checkSize(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    checkResizeType(paramInt1, paramBoolean);
    if (!paramBoolean && paramInt2 < 0)
      throw new IllegalArgumentException("Pref must be >= 0"); 
    if (paramBoolean)
      checkResizeType(paramInt2, true); 
    checkResizeType(paramInt3, paramBoolean);
    checkLessThan(paramInt1, paramInt2);
    checkLessThan(paramInt2, paramInt3);
  }
  
  private static void checkResizeType(int paramInt, boolean paramBoolean) {
    if (paramInt < 0 && ((paramBoolean && paramInt != -1 && paramInt != -2) || (!paramBoolean && paramInt != -2)))
      throw new IllegalArgumentException("Invalid size"); 
  }
  
  private static void checkLessThan(int paramInt1, int paramInt2) {
    if (paramInt1 >= 0 && paramInt2 >= 0 && paramInt1 > paramInt2)
      throw new IllegalArgumentException("Following is not met: min<=pref<=max"); 
  }
  
  public GroupLayout(Container paramContainer) {
    if (paramContainer == null)
      throw new IllegalArgumentException("Container must be non-null"); 
    this.honorsVisibility = true;
    this.host = paramContainer;
    setHorizontalGroup(createParallelGroup(Alignment.LEADING, true));
    setVerticalGroup(createParallelGroup(Alignment.LEADING, true));
    this.componentInfos = new HashMap();
    this.tmpParallelSet = new HashSet();
  }
  
  public void setHonorsVisibility(boolean paramBoolean) {
    if (this.honorsVisibility != paramBoolean) {
      this.honorsVisibility = paramBoolean;
      this.springsChanged = true;
      this.isValid = false;
      invalidateHost();
    } 
  }
  
  public boolean getHonorsVisibility() { return this.honorsVisibility; }
  
  public void setHonorsVisibility(Component paramComponent, Boolean paramBoolean) {
    if (paramComponent == null)
      throw new IllegalArgumentException("Component must be non-null"); 
    getComponentInfo(paramComponent).setHonorsVisibility(paramBoolean);
    this.springsChanged = true;
    this.isValid = false;
    invalidateHost();
  }
  
  public void setAutoCreateGaps(boolean paramBoolean) {
    if (this.autocreatePadding != paramBoolean) {
      this.autocreatePadding = paramBoolean;
      invalidateHost();
    } 
  }
  
  public boolean getAutoCreateGaps() { return this.autocreatePadding; }
  
  public void setAutoCreateContainerGaps(boolean paramBoolean) {
    if (this.autocreateContainerPadding != paramBoolean) {
      this.autocreateContainerPadding = paramBoolean;
      this.horizontalGroup = createTopLevelGroup(getHorizontalGroup());
      this.verticalGroup = createTopLevelGroup(getVerticalGroup());
      invalidateHost();
    } 
  }
  
  public boolean getAutoCreateContainerGaps() { return this.autocreateContainerPadding; }
  
  public void setHorizontalGroup(Group paramGroup) {
    if (paramGroup == null)
      throw new IllegalArgumentException("Group must be non-null"); 
    this.horizontalGroup = createTopLevelGroup(paramGroup);
    invalidateHost();
  }
  
  private Group getHorizontalGroup() {
    byte b = 0;
    if (this.horizontalGroup.springs.size() > 1)
      b = 1; 
    return (Group)this.horizontalGroup.springs.get(b);
  }
  
  public void setVerticalGroup(Group paramGroup) {
    if (paramGroup == null)
      throw new IllegalArgumentException("Group must be non-null"); 
    this.verticalGroup = createTopLevelGroup(paramGroup);
    invalidateHost();
  }
  
  private Group getVerticalGroup() {
    byte b = 0;
    if (this.verticalGroup.springs.size() > 1)
      b = 1; 
    return (Group)this.verticalGroup.springs.get(b);
  }
  
  private Group createTopLevelGroup(Group paramGroup) {
    SequentialGroup sequentialGroup = createSequentialGroup();
    if (getAutoCreateContainerGaps()) {
      sequentialGroup.addSpring(new ContainerAutoPreferredGapSpring());
      sequentialGroup.addGroup(paramGroup);
      sequentialGroup.addSpring(new ContainerAutoPreferredGapSpring());
    } else {
      sequentialGroup.addGroup(paramGroup);
    } 
    return sequentialGroup;
  }
  
  public SequentialGroup createSequentialGroup() { return new SequentialGroup(); }
  
  public ParallelGroup createParallelGroup() { return createParallelGroup(Alignment.LEADING); }
  
  public ParallelGroup createParallelGroup(Alignment paramAlignment) { return createParallelGroup(paramAlignment, true); }
  
  public ParallelGroup createParallelGroup(Alignment paramAlignment, boolean paramBoolean) {
    if (paramAlignment == null)
      throw new IllegalArgumentException("alignment must be non null"); 
    return (paramAlignment == Alignment.BASELINE) ? new BaselineGroup(paramBoolean) : new ParallelGroup(paramAlignment, paramBoolean);
  }
  
  public ParallelGroup createBaselineGroup(boolean paramBoolean1, boolean paramBoolean2) { return new BaselineGroup(paramBoolean1, paramBoolean2); }
  
  public void linkSize(Component... paramVarArgs) {
    linkSize(0, paramVarArgs);
    linkSize(1, paramVarArgs);
  }
  
  public void linkSize(int paramInt, Component... paramVarArgs) {
    if (paramVarArgs == null)
      throw new IllegalArgumentException("Components must be non-null"); 
    int i;
    for (i = paramVarArgs.length - 1; i >= 0; i--) {
      Component component = paramVarArgs[i];
      if (paramVarArgs[i] == null)
        throw new IllegalArgumentException("Components must be non-null"); 
      getComponentInfo(component);
    } 
    if (paramInt == 0) {
      i = 0;
    } else if (paramInt == 1) {
      i = 1;
    } else {
      throw new IllegalArgumentException("Axis must be one of SwingConstants.HORIZONTAL or SwingConstants.VERTICAL");
    } 
    LinkInfo linkInfo = getComponentInfo(paramVarArgs[paramVarArgs.length - 1]).getLinkInfo(i);
    for (int j = paramVarArgs.length - 2; j >= 0; j--)
      linkInfo.add(getComponentInfo(paramVarArgs[j])); 
    invalidateHost();
  }
  
  public void replace(Component paramComponent1, Component paramComponent2) {
    if (paramComponent1 == null || paramComponent2 == null)
      throw new IllegalArgumentException("Components must be non-null"); 
    if (this.springsChanged) {
      registerComponents(this.horizontalGroup, 0);
      registerComponents(this.verticalGroup, 1);
    } 
    ComponentInfo componentInfo = (ComponentInfo)this.componentInfos.remove(paramComponent1);
    if (componentInfo == null)
      throw new IllegalArgumentException("Component must already exist"); 
    this.host.remove(paramComponent1);
    if (paramComponent2.getParent() != this.host)
      this.host.add(paramComponent2); 
    componentInfo.setComponent(paramComponent2);
    this.componentInfos.put(paramComponent2, componentInfo);
    invalidateHost();
  }
  
  public void setLayoutStyle(LayoutStyle paramLayoutStyle) {
    this.layoutStyle = paramLayoutStyle;
    invalidateHost();
  }
  
  public LayoutStyle getLayoutStyle() { return this.layoutStyle; }
  
  private LayoutStyle getLayoutStyle0() {
    LayoutStyle layoutStyle1 = getLayoutStyle();
    if (layoutStyle1 == null)
      layoutStyle1 = LayoutStyle.getInstance(); 
    return layoutStyle1;
  }
  
  private void invalidateHost() {
    if (this.host instanceof JComponent) {
      ((JComponent)this.host).revalidate();
    } else {
      this.host.invalidate();
    } 
    this.host.repaint();
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {
    ComponentInfo componentInfo = (ComponentInfo)this.componentInfos.remove(paramComponent);
    if (componentInfo != null) {
      componentInfo.dispose();
      this.springsChanged = true;
      this.isValid = false;
    } 
  }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    checkParent(paramContainer);
    prepare(1);
    return adjustSize(this.horizontalGroup.getPreferredSize(0), this.verticalGroup.getPreferredSize(1));
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    checkParent(paramContainer);
    prepare(0);
    return adjustSize(this.horizontalGroup.getMinimumSize(0), this.verticalGroup.getMinimumSize(1));
  }
  
  public void layoutContainer(Container paramContainer) {
    prepare(3);
    Insets insets = paramContainer.getInsets();
    int i = paramContainer.getWidth() - insets.left - insets.right;
    int j = paramContainer.getHeight() - insets.top - insets.bottom;
    boolean bool = isLeftToRight();
    if (getAutoCreateGaps() || getAutoCreateContainerGaps() || this.hasPreferredPaddingSprings) {
      calculateAutopadding(this.horizontalGroup, 0, 3, 0, i);
      calculateAutopadding(this.verticalGroup, 1, 3, 0, j);
    } 
    this.horizontalGroup.setSize(0, 0, i);
    this.verticalGroup.setSize(1, 0, j);
    for (ComponentInfo componentInfo : this.componentInfos.values())
      componentInfo.setBounds(insets, i, bool); 
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) {}
  
  public Dimension maximumLayoutSize(Container paramContainer) {
    checkParent(paramContainer);
    prepare(2);
    return adjustSize(this.horizontalGroup.getMaximumSize(0), this.verticalGroup.getMaximumSize(1));
  }
  
  public float getLayoutAlignmentX(Container paramContainer) {
    checkParent(paramContainer);
    return 0.5F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer) {
    checkParent(paramContainer);
    return 0.5F;
  }
  
  public void invalidateLayout(Container paramContainer) {
    checkParent(paramContainer);
    synchronized (paramContainer.getTreeLock()) {
      this.isValid = false;
    } 
  }
  
  private void prepare(int paramInt) {
    boolean bool = false;
    if (!this.isValid) {
      this.isValid = true;
      this.horizontalGroup.setSize(0, -2147483648, -2147483648);
      this.verticalGroup.setSize(1, -2147483648, -2147483648);
      for (ComponentInfo componentInfo : this.componentInfos.values()) {
        if (componentInfo.updateVisibility())
          bool = true; 
        componentInfo.clearCachedSize();
      } 
    } 
    if (this.springsChanged) {
      registerComponents(this.horizontalGroup, 0);
      registerComponents(this.verticalGroup, 1);
    } 
    if (this.springsChanged || bool) {
      checkComponents();
      this.horizontalGroup.removeAutopadding();
      this.verticalGroup.removeAutopadding();
      if (getAutoCreateGaps()) {
        insertAutopadding(true);
      } else if (this.hasPreferredPaddingSprings || getAutoCreateContainerGaps()) {
        insertAutopadding(false);
      } 
      this.springsChanged = false;
    } 
    if (paramInt != 3 && (getAutoCreateGaps() || getAutoCreateContainerGaps() || this.hasPreferredPaddingSprings)) {
      calculateAutopadding(this.horizontalGroup, 0, paramInt, 0, 0);
      calculateAutopadding(this.verticalGroup, 1, paramInt, 0, 0);
    } 
  }
  
  private void calculateAutopadding(Group paramGroup, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGroup.unsetAutopadding();
    switch (paramInt2) {
      case 0:
        paramInt4 = paramGroup.getMinimumSize(paramInt1);
        break;
      case 1:
        paramInt4 = paramGroup.getPreferredSize(paramInt1);
        break;
      case 2:
        paramInt4 = paramGroup.getMaximumSize(paramInt1);
        break;
    } 
    paramGroup.setSize(paramInt1, paramInt3, paramInt4);
    paramGroup.calculateAutopadding(paramInt1);
  }
  
  private void checkComponents() {
    for (ComponentInfo componentInfo : this.componentInfos.values()) {
      if (componentInfo.horizontalSpring == null)
        throw new IllegalStateException(componentInfo.component + " is not attached to a horizontal group"); 
      if (componentInfo.verticalSpring == null)
        throw new IllegalStateException(componentInfo.component + " is not attached to a vertical group"); 
    } 
  }
  
  private void registerComponents(Group paramGroup, int paramInt) {
    List list = paramGroup.springs;
    for (int i = list.size() - 1; i >= 0; i--) {
      Spring spring = (Spring)list.get(i);
      if (spring instanceof ComponentSpring) {
        ((ComponentSpring)spring).installIfNecessary(paramInt);
      } else if (spring instanceof Group) {
        registerComponents((Group)spring, paramInt);
      } 
    } 
  }
  
  private Dimension adjustSize(int paramInt1, int paramInt2) {
    Insets insets = this.host.getInsets();
    return new Dimension(paramInt1 + insets.left + insets.right, paramInt2 + insets.top + insets.bottom);
  }
  
  private void checkParent(Container paramContainer) {
    if (paramContainer != this.host)
      throw new IllegalArgumentException("GroupLayout can only be used with one Container at a time"); 
  }
  
  private ComponentInfo getComponentInfo(Component paramComponent) {
    ComponentInfo componentInfo = (ComponentInfo)this.componentInfos.get(paramComponent);
    if (componentInfo == null) {
      componentInfo = new ComponentInfo(paramComponent);
      this.componentInfos.put(paramComponent, componentInfo);
      if (paramComponent.getParent() != this.host)
        this.host.add(paramComponent); 
    } 
    return componentInfo;
  }
  
  private void insertAutopadding(boolean paramBoolean) {
    this.horizontalGroup.insertAutopadding(0, new ArrayList(1), new ArrayList(1), new ArrayList(1), new ArrayList(1), paramBoolean);
    this.verticalGroup.insertAutopadding(1, new ArrayList(1), new ArrayList(1), new ArrayList(1), new ArrayList(1), paramBoolean);
  }
  
  private boolean areParallelSiblings(Component paramComponent1, Component paramComponent2, int paramInt) {
    ComponentSpring componentSpring2;
    ComponentSpring componentSpring1;
    ComponentInfo componentInfo1 = getComponentInfo(paramComponent1);
    ComponentInfo componentInfo2 = getComponentInfo(paramComponent2);
    if (paramInt == 0) {
      componentSpring1 = componentInfo1.horizontalSpring;
      componentSpring2 = componentInfo2.horizontalSpring;
    } else {
      componentSpring1 = componentInfo1.verticalSpring;
      componentSpring2 = componentInfo2.verticalSpring;
    } 
    Set set = this.tmpParallelSet;
    set.clear();
    Spring spring;
    for (spring = componentSpring1.getParent(); spring != null; spring = spring.getParent())
      set.add(spring); 
    for (spring = componentSpring2.getParent(); spring != null; spring = spring.getParent()) {
      if (set.contains(spring)) {
        set.clear();
        while (spring != null) {
          if (spring instanceof ParallelGroup)
            return true; 
          spring = spring.getParent();
        } 
        return false;
      } 
    } 
    set.clear();
    return false;
  }
  
  private boolean isLeftToRight() { return this.host.getComponentOrientation().isLeftToRight(); }
  
  public String toString() {
    if (this.springsChanged) {
      registerComponents(this.horizontalGroup, 0);
      registerComponents(this.verticalGroup, 1);
    } 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("HORIZONTAL\n");
    createSpringDescription(stringBuffer, this.horizontalGroup, "  ", 0);
    stringBuffer.append("\nVERTICAL\n");
    createSpringDescription(stringBuffer, this.verticalGroup, "  ", 1);
    return stringBuffer.toString();
  }
  
  private void createSpringDescription(StringBuffer paramStringBuffer, Spring paramSpring, String paramString, int paramInt) {
    String str1 = "";
    String str2 = "";
    if (paramSpring instanceof ComponentSpring) {
      ComponentSpring componentSpring = (ComponentSpring)paramSpring;
      str1 = Integer.toString(componentSpring.getOrigin()) + " ";
      String str = componentSpring.getComponent().getName();
      if (str != null)
        str1 = "name=" + str + ", "; 
    } 
    if (paramSpring instanceof AutoPreferredGapSpring) {
      AutoPreferredGapSpring autoPreferredGapSpring = (AutoPreferredGapSpring)paramSpring;
      str2 = ", userCreated=" + autoPreferredGapSpring.getUserCreated() + ", matches=" + autoPreferredGapSpring.getMatchDescription();
    } 
    paramStringBuffer.append(paramString + paramSpring.getClass().getName() + " " + Integer.toHexString(paramSpring.hashCode()) + " " + str1 + ", size=" + paramSpring.getSize() + ", alignment=" + paramSpring.getAlignment() + " prefs=[" + paramSpring.getMinimumSize(paramInt) + " " + paramSpring.getPreferredSize(paramInt) + " " + paramSpring.getMaximumSize(paramInt) + str2 + "]\n");
    if (paramSpring instanceof Group) {
      List list = ((Group)paramSpring).springs;
      paramString = paramString + "  ";
      for (byte b = 0; b < list.size(); b++)
        createSpringDescription(paramStringBuffer, (Spring)list.get(b), paramString, paramInt); 
    } 
  }
  
  public enum Alignment {
    LEADING, TRAILING, CENTER, BASELINE;
  }
  
  private static final class AutoPreferredGapMatch {
    public final GroupLayout.ComponentSpring source;
    
    public final GroupLayout.ComponentSpring target;
    
    AutoPreferredGapMatch(GroupLayout.ComponentSpring param1ComponentSpring1, GroupLayout.ComponentSpring param1ComponentSpring2) {
      this.source = param1ComponentSpring1;
      this.target = param1ComponentSpring2;
    }
    
    private String toString(GroupLayout.ComponentSpring param1ComponentSpring) { return param1ComponentSpring.getComponent().getName(); }
    
    public String toString() { return "[" + toString(this.source) + "-" + toString(this.target) + "]"; }
  }
  
  private class AutoPreferredGapSpring extends Spring {
    List<GroupLayout.ComponentSpring> sources;
    
    GroupLayout.ComponentSpring source;
    
    private List<GroupLayout.AutoPreferredGapMatch> matches;
    
    int size;
    
    int lastSize;
    
    private final int pref;
    
    private final int max;
    
    private LayoutStyle.ComponentPlacement type;
    
    private boolean userCreated;
    
    private AutoPreferredGapSpring() {
      super(GroupLayout.this);
      this.pref = -2;
      this.max = -2;
      this.type = LayoutStyle.ComponentPlacement.RELATED;
    }
    
    AutoPreferredGapSpring(int param1Int1, int param1Int2) {
      super(GroupLayout.this);
      this.pref = param1Int1;
      this.max = param1Int2;
    }
    
    AutoPreferredGapSpring(LayoutStyle.ComponentPlacement param1ComponentPlacement, int param1Int1, int param1Int2) {
      super(GroupLayout.this);
      this.type = param1ComponentPlacement;
      this.pref = param1Int1;
      this.max = param1Int2;
      this.userCreated = true;
    }
    
    public void setSource(GroupLayout.ComponentSpring param1ComponentSpring) { this.source = param1ComponentSpring; }
    
    public void setSources(List<GroupLayout.ComponentSpring> param1List) { this.sources = new ArrayList(param1List); }
    
    public void setUserCreated(boolean param1Boolean) { this.userCreated = param1Boolean; }
    
    public boolean getUserCreated() { return this.userCreated; }
    
    void unset() {
      this.lastSize = getSize();
      super.unset();
      this.size = 0;
    }
    
    public void reset() {
      this.size = 0;
      this.sources = null;
      this.source = null;
      this.matches = null;
    }
    
    public void calculatePadding(int param1Int) {
      this.size = Integer.MIN_VALUE;
      int i = Integer.MIN_VALUE;
      if (this.matches != null) {
        byte b;
        LayoutStyle layoutStyle = GroupLayout.this.getLayoutStyle0();
        if (param1Int == 0) {
          if (GroupLayout.this.isLeftToRight()) {
            b = 3;
          } else {
            b = 7;
          } 
        } else {
          b = 5;
        } 
        for (int j = this.matches.size() - 1; j >= 0; j--) {
          GroupLayout.AutoPreferredGapMatch autoPreferredGapMatch = (GroupLayout.AutoPreferredGapMatch)this.matches.get(j);
          i = Math.max(i, calculatePadding(layoutStyle, b, autoPreferredGapMatch.source, autoPreferredGapMatch.target));
        } 
      } 
      if (this.size == Integer.MIN_VALUE)
        this.size = 0; 
      if (i == Integer.MIN_VALUE)
        i = 0; 
      if (this.lastSize != Integer.MIN_VALUE)
        this.size += Math.min(i, this.lastSize); 
    }
    
    private int calculatePadding(LayoutStyle param1LayoutStyle, int param1Int, GroupLayout.ComponentSpring param1ComponentSpring1, GroupLayout.ComponentSpring param1ComponentSpring2) {
      int i = param1ComponentSpring2.getOrigin() - param1ComponentSpring1.getOrigin() + param1ComponentSpring1.getSize();
      if (i >= 0) {
        int j;
        if (param1ComponentSpring1.getComponent() instanceof JComponent && param1ComponentSpring2.getComponent() instanceof JComponent) {
          j = param1LayoutStyle.getPreferredGap((JComponent)param1ComponentSpring1.getComponent(), (JComponent)param1ComponentSpring2.getComponent(), this.type, param1Int, GroupLayout.this.host);
        } else {
          j = 10;
        } 
        if (j > i)
          this.size = Math.max(this.size, j - i); 
        return j;
      } 
      return 0;
    }
    
    public void addTarget(GroupLayout.ComponentSpring param1ComponentSpring, int param1Int) {
      byte b = (param1Int == 0) ? 1 : 0;
      if (this.source != null) {
        if (GroupLayout.this.areParallelSiblings(this.source.getComponent(), param1ComponentSpring.getComponent(), b))
          addValidTarget(this.source, param1ComponentSpring); 
      } else {
        Component component = param1ComponentSpring.getComponent();
        for (int i = this.sources.size() - 1; i >= 0; i--) {
          GroupLayout.ComponentSpring componentSpring = (GroupLayout.ComponentSpring)this.sources.get(i);
          if (GroupLayout.this.areParallelSiblings(componentSpring.getComponent(), component, b))
            addValidTarget(componentSpring, param1ComponentSpring); 
        } 
      } 
    }
    
    private void addValidTarget(GroupLayout.ComponentSpring param1ComponentSpring1, GroupLayout.ComponentSpring param1ComponentSpring2) {
      if (this.matches == null)
        this.matches = new ArrayList(1); 
      this.matches.add(new GroupLayout.AutoPreferredGapMatch(param1ComponentSpring1, param1ComponentSpring2));
    }
    
    int calculateMinimumSize(int param1Int) { return this.size; }
    
    int calculatePreferredSize(int param1Int) { return (this.pref == -2 || this.pref == -1) ? this.size : Math.max(this.size, this.pref); }
    
    int calculateMaximumSize(int param1Int) { return (this.max >= 0) ? Math.max(getPreferredSize(param1Int), this.max) : this.size; }
    
    String getMatchDescription() { return (this.matches == null) ? "" : this.matches.toString(); }
    
    public String toString() { return super.toString() + getMatchDescription(); }
    
    boolean willHaveZeroSize(boolean param1Boolean) { return param1Boolean; }
  }
  
  private class BaselineGroup extends ParallelGroup {
    private boolean allSpringsHaveBaseline;
    
    private int prefAscent = this.prefDescent = -1;
    
    private int prefDescent;
    
    private boolean baselineAnchorSet;
    
    private boolean baselineAnchoredToTop;
    
    private boolean calcedBaseline = false;
    
    BaselineGroup(boolean param1Boolean) { super(GroupLayout.this, GroupLayout.Alignment.LEADING, param1Boolean); }
    
    BaselineGroup(boolean param1Boolean1, boolean param1Boolean2) {
      this(param1Boolean1);
      this.baselineAnchoredToTop = param1Boolean2;
      this.baselineAnchorSet = true;
    }
    
    void unset() {
      super.unset();
      this.prefAscent = this.prefDescent = -1;
      this.calcedBaseline = false;
    }
    
    void setValidSize(int param1Int1, int param1Int2, int param1Int3) {
      checkAxis(param1Int1);
      if (this.prefAscent == -1) {
        super.setValidSize(param1Int1, param1Int2, param1Int3);
      } else {
        baselineLayout(param1Int2, param1Int3);
      } 
    }
    
    int calculateSize(int param1Int1, int param1Int2) {
      checkAxis(param1Int1);
      if (!this.calcedBaseline)
        calculateBaselineAndResizeBehavior(); 
      return (param1Int2 == 0) ? calculateMinSize() : ((param1Int2 == 2) ? calculateMaxSize() : (this.allSpringsHaveBaseline ? (this.prefAscent + this.prefDescent) : Math.max(this.prefAscent + this.prefDescent, super.calculateSize(param1Int1, param1Int2))));
    }
    
    private void calculateBaselineAndResizeBehavior() {
      this.prefAscent = 0;
      this.prefDescent = 0;
      byte b = 0;
      Component.BaselineResizeBehavior baselineResizeBehavior = null;
      for (GroupLayout.Spring spring : this.springs) {
        if (spring.getAlignment() == null || spring.getAlignment() == GroupLayout.Alignment.BASELINE) {
          int i = spring.getBaseline();
          if (i >= 0) {
            if (spring.isResizable(1)) {
              Component.BaselineResizeBehavior baselineResizeBehavior1 = spring.getBaselineResizeBehavior();
              if (baselineResizeBehavior == null) {
                baselineResizeBehavior = baselineResizeBehavior1;
              } else if (baselineResizeBehavior1 != baselineResizeBehavior) {
                baselineResizeBehavior = Component.BaselineResizeBehavior.CONSTANT_ASCENT;
              } 
            } 
            this.prefAscent = Math.max(this.prefAscent, i);
            this.prefDescent = Math.max(this.prefDescent, spring.getPreferredSize(1) - i);
            b++;
          } 
        } 
      } 
      if (!this.baselineAnchorSet)
        if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
          this.baselineAnchoredToTop = false;
        } else {
          this.baselineAnchoredToTop = true;
        }  
      this.allSpringsHaveBaseline = (b == this.springs.size());
      this.calcedBaseline = true;
    }
    
    private int calculateMaxSize() {
      int i = this.prefAscent;
      int j = this.prefDescent;
      int k = 0;
      for (GroupLayout.Spring spring : this.springs) {
        int n = spring.getMaximumSize(1);
        int m;
        if ((spring.getAlignment() == null || spring.getAlignment() == GroupLayout.Alignment.BASELINE) && (m = spring.getBaseline()) >= 0) {
          int i1 = spring.getPreferredSize(1);
          if (i1 != n)
            switch (GroupLayout.null.$SwitchMap$java$awt$Component$BaselineResizeBehavior[spring.getBaselineResizeBehavior().ordinal()]) {
              case 1:
                if (this.baselineAnchoredToTop)
                  j = Math.max(j, n - m); 
                continue;
              case 2:
                if (!this.baselineAnchoredToTop)
                  i = Math.max(i, n - i1 + m); 
                continue;
            }  
          continue;
        } 
        k = Math.max(k, n);
      } 
      return Math.max(k, i + j);
    }
    
    private int calculateMinSize() {
      int i = 0;
      int j = 0;
      int k = 0;
      if (this.baselineAnchoredToTop) {
        i = this.prefAscent;
      } else {
        j = this.prefDescent;
      } 
      for (GroupLayout.Spring spring : this.springs) {
        int m = spring.getMinimumSize(1);
        int n;
        if ((spring.getAlignment() == null || spring.getAlignment() == GroupLayout.Alignment.BASELINE) && (n = spring.getBaseline()) >= 0) {
          int i1 = spring.getPreferredSize(1);
          Component.BaselineResizeBehavior baselineResizeBehavior = spring.getBaselineResizeBehavior();
          switch (GroupLayout.null.$SwitchMap$java$awt$Component$BaselineResizeBehavior[baselineResizeBehavior.ordinal()]) {
            case 1:
              if (this.baselineAnchoredToTop) {
                j = Math.max(m - n, j);
                continue;
              } 
              i = Math.max(n, i);
              continue;
            case 2:
              if (!this.baselineAnchoredToTop) {
                i = Math.max(n - i1 - m, i);
                continue;
              } 
              j = Math.max(i1 - n, j);
              continue;
          } 
          i = Math.max(n, i);
          j = Math.max(i1 - n, j);
          continue;
        } 
        k = Math.max(k, m);
      } 
      return Math.max(k, i + j);
    }
    
    private void baselineLayout(int param1Int1, int param1Int2) {
      int j;
      int i;
      if (this.baselineAnchoredToTop) {
        i = this.prefAscent;
        j = param1Int2 - i;
      } else {
        i = param1Int2 - this.prefDescent;
        j = this.prefDescent;
      } 
      for (GroupLayout.Spring spring : this.springs) {
        GroupLayout.Alignment alignment = spring.getAlignment();
        if (alignment == null || alignment == GroupLayout.Alignment.BASELINE) {
          int k = spring.getBaseline();
          if (k >= 0) {
            int i2;
            int m = spring.getMaximumSize(1);
            int n = spring.getPreferredSize(1);
            int i1 = n;
            switch (GroupLayout.null.$SwitchMap$java$awt$Component$BaselineResizeBehavior[spring.getBaselineResizeBehavior().ordinal()]) {
              case 1:
                i2 = param1Int1 + i - k;
                i1 = Math.min(j, m - k) + k;
                break;
              case 2:
                i1 = Math.min(i, m - n + k) + n - k;
                i2 = param1Int1 + i + n - k - i1;
                break;
              default:
                i2 = param1Int1 + i - k;
                break;
            } 
            spring.setSize(1, i2, i1);
            continue;
          } 
          setChildSize(spring, 1, param1Int1, param1Int2);
          continue;
        } 
        setChildSize(spring, 1, param1Int1, param1Int2);
      } 
    }
    
    int getBaseline() {
      if (this.springs.size() > 1) {
        getPreferredSize(1);
        return this.prefAscent;
      } 
      return (this.springs.size() == 1) ? ((GroupLayout.Spring)this.springs.get(0)).getBaseline() : -1;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior() { return (this.springs.size() == 1) ? ((GroupLayout.Spring)this.springs.get(0)).getBaselineResizeBehavior() : (this.baselineAnchoredToTop ? Component.BaselineResizeBehavior.CONSTANT_ASCENT : Component.BaselineResizeBehavior.CONSTANT_DESCENT); }
    
    private void checkAxis(int param1Int) {
      if (param1Int == 0)
        throw new IllegalStateException("Baseline must be used along vertical axis"); 
    }
  }
  
  private class ComponentInfo {
    private Component component;
    
    GroupLayout.ComponentSpring horizontalSpring;
    
    GroupLayout.ComponentSpring verticalSpring;
    
    private GroupLayout.LinkInfo horizontalMaster;
    
    private GroupLayout.LinkInfo verticalMaster;
    
    private boolean visible;
    
    private Boolean honorsVisibility;
    
    ComponentInfo(Component param1Component) {
      this.component = param1Component;
      updateVisibility();
    }
    
    public void dispose() {
      removeSpring(this.horizontalSpring);
      this.horizontalSpring = null;
      removeSpring(this.verticalSpring);
      this.verticalSpring = null;
      if (this.horizontalMaster != null)
        this.horizontalMaster.remove(this); 
      if (this.verticalMaster != null)
        this.verticalMaster.remove(this); 
    }
    
    void setHonorsVisibility(Boolean param1Boolean) { this.honorsVisibility = param1Boolean; }
    
    private void removeSpring(GroupLayout.Spring param1Spring) {
      if (param1Spring != null)
        ((GroupLayout.Group)param1Spring.getParent()).springs.remove(param1Spring); 
    }
    
    public boolean isVisible() { return this.visible; }
    
    boolean updateVisibility() {
      boolean bool1;
      if (this.honorsVisibility == null) {
        bool1 = GroupLayout.this.getHonorsVisibility();
      } else {
        bool1 = this.honorsVisibility.booleanValue();
      } 
      boolean bool2 = bool1 ? this.component.isVisible() : 1;
      if (this.visible != bool2) {
        this.visible = bool2;
        return true;
      } 
      return false;
    }
    
    public void setBounds(Insets param1Insets, int param1Int, boolean param1Boolean) {
      int i = this.horizontalSpring.getOrigin();
      int j = this.horizontalSpring.getSize();
      int k = this.verticalSpring.getOrigin();
      int m = this.verticalSpring.getSize();
      if (!param1Boolean)
        i = param1Int - i - j; 
      this.component.setBounds(i + param1Insets.left, k + param1Insets.top, j, m);
    }
    
    public void setComponent(Component param1Component) {
      this.component = param1Component;
      if (this.horizontalSpring != null)
        this.horizontalSpring.setComponent(param1Component); 
      if (this.verticalSpring != null)
        this.verticalSpring.setComponent(param1Component); 
    }
    
    public Component getComponent() { return this.component; }
    
    public boolean isLinked(int param1Int) {
      if (param1Int == 0)
        return (this.horizontalMaster != null); 
      assert param1Int == 1;
      return (this.verticalMaster != null);
    }
    
    private void setLinkInfo(int param1Int, GroupLayout.LinkInfo param1LinkInfo) {
      if (param1Int == 0) {
        this.horizontalMaster = param1LinkInfo;
      } else {
        assert param1Int == 1;
        this.verticalMaster = param1LinkInfo;
      } 
    }
    
    public GroupLayout.LinkInfo getLinkInfo(int param1Int) { return getLinkInfo(param1Int, true); }
    
    private GroupLayout.LinkInfo getLinkInfo(int param1Int, boolean param1Boolean) {
      if (param1Int == 0) {
        if (this.horizontalMaster == null && param1Boolean)
          (new GroupLayout.LinkInfo(0)).add(this); 
        return this.horizontalMaster;
      } 
      assert param1Int == 1;
      if (this.verticalMaster == null && param1Boolean)
        (new GroupLayout.LinkInfo(1)).add(this); 
      return this.verticalMaster;
    }
    
    public void clearCachedSize() {
      if (this.horizontalMaster != null)
        this.horizontalMaster.clearCachedSize(); 
      if (this.verticalMaster != null)
        this.verticalMaster.clearCachedSize(); 
    }
    
    int getLinkSize(int param1Int1, int param1Int2) {
      if (param1Int1 == 0)
        return this.horizontalMaster.getSize(param1Int1); 
      assert param1Int1 == 1;
      return this.verticalMaster.getSize(param1Int1);
    }
  }
  
  private final class ComponentSpring extends Spring {
    private Component component;
    
    private int origin;
    
    private final int min;
    
    private final int pref;
    
    private final int max;
    
    private int baseline = -1;
    
    private boolean installed;
    
    private ComponentSpring(Component param1Component, int param1Int1, int param1Int2, int param1Int3) {
      super(GroupLayout.this);
      this.component = param1Component;
      if (param1Component == null)
        throw new IllegalArgumentException("Component must be non-null"); 
      GroupLayout.checkSize(param1Int1, param1Int2, param1Int3, true);
      this.min = param1Int1;
      this.max = param1Int3;
      this.pref = param1Int2;
      this$0.getComponentInfo(param1Component);
    }
    
    int calculateMinimumSize(int param1Int) { return isLinked(param1Int) ? getLinkSize(param1Int, 0) : calculateNonlinkedMinimumSize(param1Int); }
    
    int calculatePreferredSize(int param1Int) {
      if (isLinked(param1Int))
        return getLinkSize(param1Int, 1); 
      int i = getMinimumSize(param1Int);
      int j = calculateNonlinkedPreferredSize(param1Int);
      int k = getMaximumSize(param1Int);
      return Math.min(k, Math.max(i, j));
    }
    
    int calculateMaximumSize(int param1Int) { return isLinked(param1Int) ? getLinkSize(param1Int, 2) : Math.max(getMinimumSize(param1Int), calculateNonlinkedMaximumSize(param1Int)); }
    
    boolean isVisible() { return GroupLayout.this.getComponentInfo(getComponent()).isVisible(); }
    
    int calculateNonlinkedMinimumSize(int param1Int) {
      if (!isVisible())
        return 0; 
      if (this.min >= 0)
        return this.min; 
      if (this.min == -2)
        return calculateNonlinkedPreferredSize(param1Int); 
      assert this.min == -1;
      return getSizeAlongAxis(param1Int, this.component.getMinimumSize());
    }
    
    int calculateNonlinkedPreferredSize(int param1Int) {
      if (!isVisible())
        return 0; 
      if (this.pref >= 0)
        return this.pref; 
      assert this.pref == -1 || this.pref == -2;
      return getSizeAlongAxis(param1Int, this.component.getPreferredSize());
    }
    
    int calculateNonlinkedMaximumSize(int param1Int) {
      if (!isVisible())
        return 0; 
      if (this.max >= 0)
        return this.max; 
      if (this.max == -2)
        return calculateNonlinkedPreferredSize(param1Int); 
      assert this.max == -1;
      return getSizeAlongAxis(param1Int, this.component.getMaximumSize());
    }
    
    private int getSizeAlongAxis(int param1Int, Dimension param1Dimension) { return (param1Int == 0) ? param1Dimension.width : param1Dimension.height; }
    
    private int getLinkSize(int param1Int1, int param1Int2) {
      if (!isVisible())
        return 0; 
      GroupLayout.ComponentInfo componentInfo = GroupLayout.this.getComponentInfo(this.component);
      return componentInfo.getLinkSize(param1Int1, param1Int2);
    }
    
    void setSize(int param1Int1, int param1Int2, int param1Int3) {
      super.setSize(param1Int1, param1Int2, param1Int3);
      this.origin = param1Int2;
      if (param1Int3 == Integer.MIN_VALUE)
        this.baseline = -1; 
    }
    
    int getOrigin() { return this.origin; }
    
    void setComponent(Component param1Component) { this.component = param1Component; }
    
    Component getComponent() { return this.component; }
    
    int getBaseline() {
      if (this.baseline == -1) {
        ComponentSpring componentSpring = (this.this$0.getComponentInfo(this.component)).horizontalSpring;
        int i = componentSpring.getPreferredSize(0);
        int j = getPreferredSize(1);
        if (i > 0 && j > 0)
          this.baseline = this.component.getBaseline(i, j); 
      } 
      return this.baseline;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior() { return getComponent().getBaselineResizeBehavior(); }
    
    private boolean isLinked(int param1Int) { return GroupLayout.this.getComponentInfo(this.component).isLinked(param1Int); }
    
    void installIfNecessary(int param1Int) {
      if (!this.installed) {
        this.installed = true;
        if (param1Int == 0) {
          (this.this$0.getComponentInfo(this.component)).horizontalSpring = this;
        } else {
          (this.this$0.getComponentInfo(this.component)).verticalSpring = this;
        } 
      } 
    }
    
    boolean willHaveZeroSize(boolean param1Boolean) { return !isVisible(); }
  }
  
  private class ContainerAutoPreferredGapSpring extends AutoPreferredGapSpring {
    private List<GroupLayout.ComponentSpring> targets;
    
    ContainerAutoPreferredGapSpring() {
      super(GroupLayout.this, null);
      setUserCreated(true);
    }
    
    ContainerAutoPreferredGapSpring(int param1Int1, int param1Int2) {
      super(GroupLayout.this, param1Int1, param1Int2);
      setUserCreated(true);
    }
    
    public void addTarget(GroupLayout.ComponentSpring param1ComponentSpring, int param1Int) {
      if (this.targets == null)
        this.targets = new ArrayList(1); 
      this.targets.add(param1ComponentSpring);
    }
    
    public void calculatePadding(int param1Int) {
      LayoutStyle layoutStyle = GroupLayout.this.getLayoutStyle0();
      int i = 0;
      this.size = 0;
      if (this.targets != null) {
        byte b;
        if (param1Int == 0) {
          if (GroupLayout.this.isLeftToRight()) {
            b = 7;
          } else {
            b = 3;
          } 
        } else {
          b = 5;
        } 
        for (int j = this.targets.size() - 1; j >= 0; j--) {
          GroupLayout.ComponentSpring componentSpring = (GroupLayout.ComponentSpring)this.targets.get(j);
          int k = 10;
          if (componentSpring.getComponent() instanceof JComponent) {
            k = layoutStyle.getContainerGap((JComponent)componentSpring.getComponent(), b, GroupLayout.this.host);
            i = Math.max(k, i);
            k -= componentSpring.getOrigin();
          } else {
            i = Math.max(k, i);
          } 
          this.size = Math.max(this.size, k);
        } 
      } else {
        byte b;
        if (param1Int == 0) {
          if (GroupLayout.this.isLeftToRight()) {
            b = 3;
          } else {
            b = 7;
          } 
        } else {
          b = 5;
        } 
        if (this.sources != null) {
          for (int j = this.sources.size() - 1; j >= 0; j--) {
            GroupLayout.ComponentSpring componentSpring = (GroupLayout.ComponentSpring)this.sources.get(j);
            i = Math.max(i, updateSize(layoutStyle, componentSpring, b));
          } 
        } else if (this.source != null) {
          i = updateSize(layoutStyle, this.source, b);
        } 
      } 
      if (this.lastSize != Integer.MIN_VALUE)
        this.size += Math.min(i, this.lastSize); 
    }
    
    private int updateSize(LayoutStyle param1LayoutStyle, GroupLayout.ComponentSpring param1ComponentSpring, int param1Int) {
      int i = 10;
      if (param1ComponentSpring.getComponent() instanceof JComponent)
        i = param1LayoutStyle.getContainerGap((JComponent)param1ComponentSpring.getComponent(), param1Int, GroupLayout.this.host); 
      int j = Math.max(0, getParent().getSize() - param1ComponentSpring.getSize() - param1ComponentSpring.getOrigin());
      this.size = Math.max(this.size, i - j);
      return i;
    }
    
    String getMatchDescription() { return (this.targets != null) ? ("leading: " + this.targets.toString()) : ((this.sources != null) ? ("trailing: " + this.sources.toString()) : "--"); }
  }
  
  private class GapSpring extends Spring {
    private final int min;
    
    private final int pref;
    
    private final int max;
    
    GapSpring(int param1Int1, int param1Int2, int param1Int3) {
      super(GroupLayout.this);
      GroupLayout.checkSize(param1Int1, param1Int2, param1Int3, false);
      this.min = param1Int1;
      this.pref = param1Int2;
      this.max = param1Int3;
    }
    
    int calculateMinimumSize(int param1Int) { return (this.min == -2) ? getPreferredSize(param1Int) : this.min; }
    
    int calculatePreferredSize(int param1Int) { return this.pref; }
    
    int calculateMaximumSize(int param1Int) { return (this.max == -2) ? getPreferredSize(param1Int) : this.max; }
    
    boolean willHaveZeroSize(boolean param1Boolean) { return false; }
  }
  
  public abstract class Group extends Spring {
    List<GroupLayout.Spring> springs = new ArrayList();
    
    Group() { super(GroupLayout.this); }
    
    public Group addGroup(Group param1Group) { return addSpring(param1Group); }
    
    public Group addComponent(Component param1Component) { return addComponent(param1Component, -1, -1, -1); }
    
    public Group addComponent(Component param1Component, int param1Int1, int param1Int2, int param1Int3) { return addSpring(new GroupLayout.ComponentSpring(GroupLayout.this, param1Component, param1Int1, param1Int2, param1Int3, null)); }
    
    public Group addGap(int param1Int) { return addGap(param1Int, param1Int, param1Int); }
    
    public Group addGap(int param1Int1, int param1Int2, int param1Int3) { return addSpring(new GroupLayout.GapSpring(GroupLayout.this, param1Int1, param1Int2, param1Int3)); }
    
    GroupLayout.Spring getSpring(int param1Int) { return (GroupLayout.Spring)this.springs.get(param1Int); }
    
    int indexOf(GroupLayout.Spring param1Spring) { return this.springs.indexOf(param1Spring); }
    
    Group addSpring(GroupLayout.Spring param1Spring) {
      this.springs.add(param1Spring);
      param1Spring.setParent(this);
      if (!(param1Spring instanceof GroupLayout.AutoPreferredGapSpring) || !((GroupLayout.AutoPreferredGapSpring)param1Spring).getUserCreated())
        GroupLayout.this.springsChanged = true; 
      return this;
    }
    
    void setSize(int param1Int1, int param1Int2, int param1Int3) {
      super.setSize(param1Int1, param1Int2, param1Int3);
      if (param1Int3 == Integer.MIN_VALUE) {
        for (int i = this.springs.size() - 1; i >= 0; i--)
          getSpring(i).setSize(param1Int1, param1Int2, param1Int3); 
      } else {
        setValidSize(param1Int1, param1Int2, param1Int3);
      } 
    }
    
    abstract void setValidSize(int param1Int1, int param1Int2, int param1Int3);
    
    int calculateMinimumSize(int param1Int) { return calculateSize(param1Int, 0); }
    
    int calculatePreferredSize(int param1Int) { return calculateSize(param1Int, 1); }
    
    int calculateMaximumSize(int param1Int) { return calculateSize(param1Int, 2); }
    
    int calculateSize(int param1Int1, int param1Int2) {
      int i = this.springs.size();
      if (i == 0)
        return 0; 
      if (i == 1)
        return getSpringSize(getSpring(0), param1Int1, param1Int2); 
      int j = constrain(operator(getSpringSize(getSpring(0), param1Int1, param1Int2), getSpringSize(getSpring(1), param1Int1, param1Int2)));
      for (byte b = 2; b < i; b++)
        j = constrain(operator(j, getSpringSize(getSpring(b), param1Int1, param1Int2))); 
      return j;
    }
    
    int getSpringSize(GroupLayout.Spring param1Spring, int param1Int1, int param1Int2) {
      switch (param1Int2) {
        case 0:
          return param1Spring.getMinimumSize(param1Int1);
        case 1:
          return param1Spring.getPreferredSize(param1Int1);
        case 2:
          return param1Spring.getMaximumSize(param1Int1);
      } 
      assert false;
      return 0;
    }
    
    abstract int operator(int param1Int1, int param1Int2);
    
    abstract void insertAutopadding(int param1Int, List<GroupLayout.AutoPreferredGapSpring> param1List1, List<GroupLayout.AutoPreferredGapSpring> param1List2, List<GroupLayout.ComponentSpring> param1List3, List<GroupLayout.ComponentSpring> param1List4, boolean param1Boolean);
    
    void removeAutopadding() {
      unset();
      for (int i = this.springs.size() - 1; i >= 0; i--) {
        GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(i);
        if (spring instanceof GroupLayout.AutoPreferredGapSpring) {
          if (((GroupLayout.AutoPreferredGapSpring)spring).getUserCreated()) {
            ((GroupLayout.AutoPreferredGapSpring)spring).reset();
          } else {
            this.springs.remove(i);
          } 
        } else if (spring instanceof Group) {
          ((Group)spring).removeAutopadding();
        } 
      } 
    }
    
    void unsetAutopadding() {
      unset();
      for (int i = this.springs.size() - 1; i >= 0; i--) {
        GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(i);
        if (spring instanceof GroupLayout.AutoPreferredGapSpring) {
          spring.unset();
        } else if (spring instanceof Group) {
          ((Group)spring).unsetAutopadding();
        } 
      } 
    }
    
    void calculateAutopadding(int param1Int) {
      for (int i = this.springs.size() - 1; i >= 0; i--) {
        GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(i);
        if (spring instanceof GroupLayout.AutoPreferredGapSpring) {
          spring.unset();
          ((GroupLayout.AutoPreferredGapSpring)spring).calculatePadding(param1Int);
        } else if (spring instanceof Group) {
          ((Group)spring).calculateAutopadding(param1Int);
        } 
      } 
      unset();
    }
    
    boolean willHaveZeroSize(boolean param1Boolean) {
      for (int i = this.springs.size() - 1; i >= 0; i--) {
        GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(i);
        if (!spring.willHaveZeroSize(param1Boolean))
          return false; 
      } 
      return true;
    }
  }
  
  private static class LinkInfo {
    private final int axis;
    
    private final List<GroupLayout.ComponentInfo> linked = new ArrayList();
    
    private int size = Integer.MIN_VALUE;
    
    LinkInfo(int param1Int) { this.axis = param1Int; }
    
    public void add(GroupLayout.ComponentInfo param1ComponentInfo) {
      LinkInfo linkInfo = param1ComponentInfo.getLinkInfo(this.axis, false);
      if (linkInfo == null) {
        this.linked.add(param1ComponentInfo);
        param1ComponentInfo.setLinkInfo(this.axis, this);
      } else if (linkInfo != this) {
        this.linked.addAll(linkInfo.linked);
        Iterator iterator = linkInfo.linked.iterator();
        while (iterator.hasNext()) {
          GroupLayout.ComponentInfo componentInfo;
          componentInfo.setLinkInfo(this.axis, this);
        } 
      } 
      clearCachedSize();
    }
    
    public void remove(GroupLayout.ComponentInfo param1ComponentInfo) {
      this.linked.remove(param1ComponentInfo);
      param1ComponentInfo.setLinkInfo(this.axis, null);
      if (this.linked.size() == 1)
        ((GroupLayout.ComponentInfo)this.linked.get(0)).setLinkInfo(this.axis, null); 
      clearCachedSize();
    }
    
    public void clearCachedSize() { this.size = Integer.MIN_VALUE; }
    
    public int getSize(int param1Int) {
      if (this.size == Integer.MIN_VALUE)
        this.size = calculateLinkedSize(param1Int); 
      return this.size;
    }
    
    private int calculateLinkedSize(int param1Int) {
      int i = 0;
      for (GroupLayout.ComponentInfo componentInfo : this.linked) {
        GroupLayout.ComponentSpring componentSpring;
        if (param1Int == 0) {
          componentSpring = componentInfo.horizontalSpring;
        } else {
          assert param1Int == 1;
          componentSpring = componentInfo.verticalSpring;
        } 
        i = Math.max(i, componentSpring.calculateNonlinkedPreferredSize(param1Int));
      } 
      return i;
    }
  }
  
  public class ParallelGroup extends Group {
    private final GroupLayout.Alignment childAlignment;
    
    private final boolean resizable;
    
    ParallelGroup(GroupLayout.Alignment param1Alignment, boolean param1Boolean) {
      super(GroupLayout.this);
      this.childAlignment = param1Alignment;
      this.resizable = param1Boolean;
    }
    
    public ParallelGroup addGroup(GroupLayout.Group param1Group) { return (ParallelGroup)super.addGroup(param1Group); }
    
    public ParallelGroup addComponent(Component param1Component) { return (ParallelGroup)super.addComponent(param1Component); }
    
    public ParallelGroup addComponent(Component param1Component, int param1Int1, int param1Int2, int param1Int3) { return (ParallelGroup)super.addComponent(param1Component, param1Int1, param1Int2, param1Int3); }
    
    public ParallelGroup addGap(int param1Int) { return (ParallelGroup)super.addGap(param1Int); }
    
    public ParallelGroup addGap(int param1Int1, int param1Int2, int param1Int3) { return (ParallelGroup)super.addGap(param1Int1, param1Int2, param1Int3); }
    
    public ParallelGroup addGroup(GroupLayout.Alignment param1Alignment, GroupLayout.Group param1Group) {
      checkChildAlignment(param1Alignment);
      param1Group.setAlignment(param1Alignment);
      return (ParallelGroup)addSpring(param1Group);
    }
    
    public ParallelGroup addComponent(Component param1Component, GroupLayout.Alignment param1Alignment) { return addComponent(param1Component, param1Alignment, -1, -1, -1); }
    
    public ParallelGroup addComponent(Component param1Component, GroupLayout.Alignment param1Alignment, int param1Int1, int param1Int2, int param1Int3) {
      checkChildAlignment(param1Alignment);
      GroupLayout.ComponentSpring componentSpring = new GroupLayout.ComponentSpring(GroupLayout.this, param1Component, param1Int1, param1Int2, param1Int3, null);
      componentSpring.setAlignment(param1Alignment);
      return (ParallelGroup)addSpring(componentSpring);
    }
    
    boolean isResizable() { return this.resizable; }
    
    int operator(int param1Int1, int param1Int2) { return Math.max(param1Int1, param1Int2); }
    
    int calculateMinimumSize(int param1Int) { return !isResizable() ? getPreferredSize(param1Int) : super.calculateMinimumSize(param1Int); }
    
    int calculateMaximumSize(int param1Int) { return !isResizable() ? getPreferredSize(param1Int) : super.calculateMaximumSize(param1Int); }
    
    void setValidSize(int param1Int1, int param1Int2, int param1Int3) {
      for (GroupLayout.Spring spring : this.springs)
        setChildSize(spring, param1Int1, param1Int2, param1Int3); 
    }
    
    void setChildSize(GroupLayout.Spring param1Spring, int param1Int1, int param1Int2, int param1Int3) {
      GroupLayout.Alignment alignment = param1Spring.getAlignment();
      int i = Math.min(Math.max(param1Spring.getMinimumSize(param1Int1), param1Int3), param1Spring.getMaximumSize(param1Int1));
      if (alignment == null)
        alignment = this.childAlignment; 
      switch (GroupLayout.null.$SwitchMap$javax$swing$GroupLayout$Alignment[alignment.ordinal()]) {
        case 1:
          param1Spring.setSize(param1Int1, param1Int2 + param1Int3 - i, i);
          return;
        case 2:
          param1Spring.setSize(param1Int1, param1Int2 + (param1Int3 - i) / 2, i);
          return;
      } 
      param1Spring.setSize(param1Int1, param1Int2, i);
    }
    
    void insertAutopadding(int param1Int, List<GroupLayout.AutoPreferredGapSpring> param1List1, List<GroupLayout.AutoPreferredGapSpring> param1List2, List<GroupLayout.ComponentSpring> param1List3, List<GroupLayout.ComponentSpring> param1List4, boolean param1Boolean) {
      for (GroupLayout.Spring spring : this.springs) {
        if (spring instanceof GroupLayout.ComponentSpring) {
          if (((GroupLayout.ComponentSpring)spring).isVisible()) {
            for (GroupLayout.AutoPreferredGapSpring autoPreferredGapSpring : param1List1)
              autoPreferredGapSpring.addTarget((GroupLayout.ComponentSpring)spring, param1Int); 
            param1List4.add((GroupLayout.ComponentSpring)spring);
          } 
          continue;
        } 
        if (spring instanceof GroupLayout.Group) {
          ((GroupLayout.Group)spring).insertAutopadding(param1Int, param1List1, param1List2, param1List3, param1List4, param1Boolean);
          continue;
        } 
        if (spring instanceof GroupLayout.AutoPreferredGapSpring) {
          ((GroupLayout.AutoPreferredGapSpring)spring).setSources(param1List3);
          param1List2.add((GroupLayout.AutoPreferredGapSpring)spring);
        } 
      } 
    }
    
    private void checkChildAlignment(GroupLayout.Alignment param1Alignment) { checkChildAlignment(param1Alignment, this instanceof GroupLayout.BaselineGroup); }
    
    private void checkChildAlignment(GroupLayout.Alignment param1Alignment, boolean param1Boolean) {
      if (param1Alignment == null)
        throw new IllegalArgumentException("Alignment must be non-null"); 
      if (!param1Boolean && param1Alignment == GroupLayout.Alignment.BASELINE)
        throw new IllegalArgumentException("Alignment must be one of:LEADING, TRAILING or CENTER"); 
    }
  }
  
  private class PreferredGapSpring extends Spring {
    private final JComponent source;
    
    private final JComponent target;
    
    private final LayoutStyle.ComponentPlacement type;
    
    private final int pref;
    
    private final int max;
    
    PreferredGapSpring(JComponent param1JComponent1, JComponent param1JComponent2, LayoutStyle.ComponentPlacement param1ComponentPlacement, int param1Int1, int param1Int2) {
      super(GroupLayout.this);
      this.source = param1JComponent1;
      this.target = param1JComponent2;
      this.type = param1ComponentPlacement;
      this.pref = param1Int1;
      this.max = param1Int2;
    }
    
    int calculateMinimumSize(int param1Int) { return getPadding(param1Int); }
    
    int calculatePreferredSize(int param1Int) {
      if (this.pref == -1 || this.pref == -2)
        return getMinimumSize(param1Int); 
      int i = getMinimumSize(param1Int);
      int j = getMaximumSize(param1Int);
      return Math.min(j, Math.max(i, this.pref));
    }
    
    int calculateMaximumSize(int param1Int) { return (this.max == -2 || this.max == -1) ? getPadding(param1Int) : Math.max(getMinimumSize(param1Int), this.max); }
    
    private int getPadding(int param1Int) {
      byte b;
      if (param1Int == 0) {
        b = 3;
      } else {
        b = 5;
      } 
      return GroupLayout.this.getLayoutStyle0().getPreferredGap(this.source, this.target, this.type, b, GroupLayout.this.host);
    }
    
    boolean willHaveZeroSize(boolean param1Boolean) { return false; }
  }
  
  public class SequentialGroup extends Group {
    private GroupLayout.Spring baselineSpring;
    
    SequentialGroup() { super(GroupLayout.this); }
    
    public SequentialGroup addGroup(GroupLayout.Group param1Group) { return (SequentialGroup)super.addGroup(param1Group); }
    
    public SequentialGroup addGroup(boolean param1Boolean, GroupLayout.Group param1Group) {
      super.addGroup(param1Group);
      if (param1Boolean)
        this.baselineSpring = param1Group; 
      return this;
    }
    
    public SequentialGroup addComponent(Component param1Component) { return (SequentialGroup)super.addComponent(param1Component); }
    
    public SequentialGroup addComponent(boolean param1Boolean, Component param1Component) {
      super.addComponent(param1Component);
      if (param1Boolean)
        this.baselineSpring = (GroupLayout.Spring)this.springs.get(this.springs.size() - 1); 
      return this;
    }
    
    public SequentialGroup addComponent(Component param1Component, int param1Int1, int param1Int2, int param1Int3) { return (SequentialGroup)super.addComponent(param1Component, param1Int1, param1Int2, param1Int3); }
    
    public SequentialGroup addComponent(boolean param1Boolean, Component param1Component, int param1Int1, int param1Int2, int param1Int3) {
      super.addComponent(param1Component, param1Int1, param1Int2, param1Int3);
      if (param1Boolean)
        this.baselineSpring = (GroupLayout.Spring)this.springs.get(this.springs.size() - 1); 
      return this;
    }
    
    public SequentialGroup addGap(int param1Int) { return (SequentialGroup)super.addGap(param1Int); }
    
    public SequentialGroup addGap(int param1Int1, int param1Int2, int param1Int3) { return (SequentialGroup)super.addGap(param1Int1, param1Int2, param1Int3); }
    
    public SequentialGroup addPreferredGap(JComponent param1JComponent1, JComponent param1JComponent2, LayoutStyle.ComponentPlacement param1ComponentPlacement) { return addPreferredGap(param1JComponent1, param1JComponent2, param1ComponentPlacement, -1, -2); }
    
    public SequentialGroup addPreferredGap(JComponent param1JComponent1, JComponent param1JComponent2, LayoutStyle.ComponentPlacement param1ComponentPlacement, int param1Int1, int param1Int2) {
      if (param1ComponentPlacement == null)
        throw new IllegalArgumentException("Type must be non-null"); 
      if (param1JComponent1 == null || param1JComponent2 == null)
        throw new IllegalArgumentException("Components must be non-null"); 
      checkPreferredGapValues(param1Int1, param1Int2);
      return (SequentialGroup)addSpring(new GroupLayout.PreferredGapSpring(GroupLayout.this, param1JComponent1, param1JComponent2, param1ComponentPlacement, param1Int1, param1Int2));
    }
    
    public SequentialGroup addPreferredGap(LayoutStyle.ComponentPlacement param1ComponentPlacement) { return addPreferredGap(param1ComponentPlacement, -1, -1); }
    
    public SequentialGroup addPreferredGap(LayoutStyle.ComponentPlacement param1ComponentPlacement, int param1Int1, int param1Int2) {
      if (param1ComponentPlacement != LayoutStyle.ComponentPlacement.RELATED && param1ComponentPlacement != LayoutStyle.ComponentPlacement.UNRELATED)
        throw new IllegalArgumentException("Type must be one of LayoutStyle.ComponentPlacement.RELATED or LayoutStyle.ComponentPlacement.UNRELATED"); 
      checkPreferredGapValues(param1Int1, param1Int2);
      GroupLayout.this.hasPreferredPaddingSprings = true;
      return (SequentialGroup)addSpring(new GroupLayout.AutoPreferredGapSpring(GroupLayout.this, param1ComponentPlacement, param1Int1, param1Int2));
    }
    
    public SequentialGroup addContainerGap() { return addContainerGap(-1, -1); }
    
    public SequentialGroup addContainerGap(int param1Int1, int param1Int2) {
      if ((param1Int1 < 0 && param1Int1 != -1) || (param1Int2 < 0 && param1Int2 != -1 && param1Int2 != -2) || (param1Int1 >= 0 && param1Int2 >= 0 && param1Int1 > param1Int2))
        throw new IllegalArgumentException("Pref and max must be either DEFAULT_VALUE or >= 0 and pref <= max"); 
      GroupLayout.this.hasPreferredPaddingSprings = true;
      return (SequentialGroup)addSpring(new GroupLayout.ContainerAutoPreferredGapSpring(GroupLayout.this, param1Int1, param1Int2));
    }
    
    int operator(int param1Int1, int param1Int2) { return constrain(param1Int1) + constrain(param1Int2); }
    
    void setValidSize(int param1Int1, int param1Int2, int param1Int3) {
      int i = getPreferredSize(param1Int1);
      if (param1Int3 == i) {
        for (GroupLayout.Spring spring : this.springs) {
          int j = spring.getPreferredSize(param1Int1);
          spring.setSize(param1Int1, param1Int2, j);
          param1Int2 += j;
        } 
      } else if (this.springs.size() == 1) {
        GroupLayout.Spring spring = getSpring(0);
        spring.setSize(param1Int1, param1Int2, Math.min(Math.max(param1Int3, spring.getMinimumSize(param1Int1)), spring.getMaximumSize(param1Int1)));
      } else if (this.springs.size() > 1) {
        setValidSizeNotPreferred(param1Int1, param1Int2, param1Int3);
      } 
    }
    
    private void setValidSizeNotPreferred(int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int3 - getPreferredSize(param1Int1);
      assert i != 0;
      boolean bool = (i < 0);
      int j = this.springs.size();
      if (bool)
        i *= -1; 
      List list = buildResizableList(param1Int1, bool);
      int k = list.size();
      if (k > 0) {
        int m = i / k;
        int n = i - m * k;
        int[] arrayOfInt = new int[j];
        int i1 = bool ? -1 : 1;
        int i2;
        for (i2 = 0; i2 < k; i2++) {
          GroupLayout.SpringDelta springDelta = (GroupLayout.SpringDelta)list.get(i2);
          if (i2 + 1 == k)
            m += n; 
          springDelta.delta = Math.min(m, springDelta.delta);
          i -= springDelta.delta;
          if (springDelta.delta != m && i2 + 1 < k) {
            m = i / (k - i2 - 1);
            n = i - m * (k - i2 - 1);
          } 
          arrayOfInt[springDelta.index] = i1 * springDelta.delta;
        } 
        for (i2 = 0; i2 < j; i2++) {
          GroupLayout.Spring spring = getSpring(i2);
          int i3 = spring.getPreferredSize(param1Int1) + arrayOfInt[i2];
          spring.setSize(param1Int1, param1Int2, i3);
          param1Int2 += i3;
        } 
      } else {
        for (byte b = 0; b < j; b++) {
          int m;
          GroupLayout.Spring spring = getSpring(b);
          if (bool) {
            m = spring.getMinimumSize(param1Int1);
          } else {
            m = spring.getMaximumSize(param1Int1);
          } 
          spring.setSize(param1Int1, param1Int2, m);
          param1Int2 += m;
        } 
      } 
    }
    
    private List<GroupLayout.SpringDelta> buildResizableList(int param1Int, boolean param1Boolean) {
      int i = this.springs.size();
      ArrayList arrayList = new ArrayList(i);
      for (byte b = 0; b < i; b++) {
        int j;
        GroupLayout.Spring spring = getSpring(b);
        if (param1Boolean) {
          j = spring.getPreferredSize(param1Int) - spring.getMinimumSize(param1Int);
        } else {
          j = spring.getMaximumSize(param1Int) - spring.getPreferredSize(param1Int);
        } 
        if (j > 0)
          arrayList.add(new GroupLayout.SpringDelta(b, j)); 
      } 
      Collections.sort(arrayList);
      return arrayList;
    }
    
    private int indexOfNextNonZeroSpring(int param1Int, boolean param1Boolean) {
      while (param1Int < this.springs.size()) {
        GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(param1Int);
        if (!spring.willHaveZeroSize(param1Boolean))
          return param1Int; 
        param1Int++;
      } 
      return param1Int;
    }
    
    void insertAutopadding(int param1Int, List<GroupLayout.AutoPreferredGapSpring> param1List1, List<GroupLayout.AutoPreferredGapSpring> param1List2, List<GroupLayout.ComponentSpring> param1List3, List<GroupLayout.ComponentSpring> param1List4, boolean param1Boolean) {
      ArrayList arrayList1 = new ArrayList(param1List1);
      ArrayList arrayList2 = new ArrayList(1);
      ArrayList arrayList3 = new ArrayList(param1List3);
      ArrayList arrayList4 = null;
      for (int i = 0; i < this.springs.size(); i++) {
        GroupLayout.Spring spring = getSpring(i);
        if (spring instanceof GroupLayout.AutoPreferredGapSpring) {
          if (arrayList1.size() == 0) {
            GroupLayout.AutoPreferredGapSpring autoPreferredGapSpring = (GroupLayout.AutoPreferredGapSpring)spring;
            autoPreferredGapSpring.setSources(arrayList3);
            arrayList3.clear();
            i = indexOfNextNonZeroSpring(i + 1, true);
            if (i == this.springs.size()) {
              if (!(autoPreferredGapSpring instanceof GroupLayout.ContainerAutoPreferredGapSpring))
                param1List2.add(autoPreferredGapSpring); 
              continue;
            } 
            arrayList1.clear();
            arrayList1.add(autoPreferredGapSpring);
            continue;
          } 
          i = indexOfNextNonZeroSpring(i + 1, true);
          continue;
        } 
        if (arrayList3.size() > 0 && param1Boolean) {
          GroupLayout.AutoPreferredGapSpring autoPreferredGapSpring = new GroupLayout.AutoPreferredGapSpring(GroupLayout.this, null);
          this.springs.add(i, autoPreferredGapSpring);
          continue;
        } 
        if (spring instanceof GroupLayout.ComponentSpring) {
          GroupLayout.ComponentSpring componentSpring = (GroupLayout.ComponentSpring)spring;
          if (!componentSpring.isVisible()) {
            i++;
            continue;
          } 
          for (GroupLayout.AutoPreferredGapSpring autoPreferredGapSpring : arrayList1)
            autoPreferredGapSpring.addTarget(componentSpring, param1Int); 
          arrayList3.clear();
          arrayList1.clear();
          i = indexOfNextNonZeroSpring(i + 1, false);
          if (i == this.springs.size()) {
            param1List4.add(componentSpring);
            continue;
          } 
          arrayList3.add(componentSpring);
          continue;
        } 
        if (spring instanceof GroupLayout.Group) {
          if (arrayList4 == null) {
            arrayList4 = new ArrayList(1);
          } else {
            arrayList4.clear();
          } 
          arrayList2.clear();
          ((GroupLayout.Group)spring).insertAutopadding(param1Int, arrayList1, arrayList2, arrayList3, arrayList4, param1Boolean);
          arrayList3.clear();
          arrayList1.clear();
          i = indexOfNextNonZeroSpring(i + 1, (arrayList4.size() == 0));
          if (i == this.springs.size()) {
            param1List4.addAll(arrayList4);
            param1List2.addAll(arrayList2);
            continue;
          } 
          arrayList3.addAll(arrayList4);
          arrayList1.addAll(arrayList2);
          continue;
        } 
        arrayList1.clear();
        arrayList3.clear();
      } 
    }
    
    int getBaseline() {
      if (this.baselineSpring != null) {
        int i = this.baselineSpring.getBaseline();
        if (i >= 0) {
          int j = 0;
          for (GroupLayout.Spring spring : this.springs) {
            if (spring == this.baselineSpring)
              return j + i; 
            j += spring.getPreferredSize(1);
          } 
        } 
      } 
      return -1;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior() {
      if (isResizable(1)) {
        if (!this.baselineSpring.isResizable(1)) {
          boolean bool1 = false;
          for (GroupLayout.Spring spring : this.springs) {
            if (spring == this.baselineSpring)
              break; 
            if (spring.isResizable(1)) {
              bool1 = true;
              break;
            } 
          } 
          boolean bool2 = false;
          for (int i = this.springs.size() - 1; i >= 0; i--) {
            GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(i);
            if (spring == this.baselineSpring)
              break; 
            if (spring.isResizable(1)) {
              bool2 = true;
              break;
            } 
          } 
          if (bool1 && !bool2)
            return Component.BaselineResizeBehavior.CONSTANT_DESCENT; 
          if (!bool1 && bool2)
            return Component.BaselineResizeBehavior.CONSTANT_ASCENT; 
        } else {
          Component.BaselineResizeBehavior baselineResizeBehavior = this.baselineSpring.getBaselineResizeBehavior();
          if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_ASCENT) {
            for (GroupLayout.Spring spring : this.springs) {
              if (spring == this.baselineSpring)
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT; 
              if (spring.isResizable(1))
                return Component.BaselineResizeBehavior.OTHER; 
            } 
          } else if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
            for (int i = this.springs.size() - 1; i >= 0; i--) {
              GroupLayout.Spring spring = (GroupLayout.Spring)this.springs.get(i);
              if (spring == this.baselineSpring)
                return Component.BaselineResizeBehavior.CONSTANT_DESCENT; 
              if (spring.isResizable(1))
                return Component.BaselineResizeBehavior.OTHER; 
            } 
          } 
        } 
        return Component.BaselineResizeBehavior.OTHER;
      } 
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    private void checkPreferredGapValues(int param1Int1, int param1Int2) {
      if ((param1Int1 < 0 && param1Int1 != -1 && param1Int1 != -2) || (param1Int2 < 0 && param1Int2 != -1 && param1Int2 != -2) || (param1Int1 >= 0 && param1Int2 >= 0 && param1Int1 > param1Int2))
        throw new IllegalArgumentException("Pref and max must be either DEFAULT_SIZE, PREFERRED_SIZE, or >= 0 and pref <= max"); 
    }
  }
  
  private abstract class Spring {
    private int size;
    
    private int min = this.pref = this.max = Integer.MIN_VALUE;
    
    private int max;
    
    private int pref;
    
    private Spring parent;
    
    private GroupLayout.Alignment alignment;
    
    abstract int calculateMinimumSize(int param1Int);
    
    abstract int calculatePreferredSize(int param1Int);
    
    abstract int calculateMaximumSize(int param1Int);
    
    void setParent(Spring param1Spring) { this.parent = param1Spring; }
    
    Spring getParent() { return this.parent; }
    
    void setAlignment(GroupLayout.Alignment param1Alignment) { this.alignment = param1Alignment; }
    
    GroupLayout.Alignment getAlignment() { return this.alignment; }
    
    final int getMinimumSize(int param1Int) {
      if (this.min == Integer.MIN_VALUE)
        this.min = constrain(calculateMinimumSize(param1Int)); 
      return this.min;
    }
    
    final int getPreferredSize(int param1Int) {
      if (this.pref == Integer.MIN_VALUE)
        this.pref = constrain(calculatePreferredSize(param1Int)); 
      return this.pref;
    }
    
    final int getMaximumSize(int param1Int) {
      if (this.max == Integer.MIN_VALUE)
        this.max = constrain(calculateMaximumSize(param1Int)); 
      return this.max;
    }
    
    void setSize(int param1Int1, int param1Int2, int param1Int3) {
      this.size = param1Int3;
      if (param1Int3 == Integer.MIN_VALUE)
        unset(); 
    }
    
    void unset() { this.size = this.min = this.pref = this.max = Integer.MIN_VALUE; }
    
    int getSize() { return this.size; }
    
    int constrain(int param1Int) { return Math.min(param1Int, 32767); }
    
    int getBaseline() { return -1; }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior() { return Component.BaselineResizeBehavior.OTHER; }
    
    final boolean isResizable(int param1Int) {
      int i = getMinimumSize(param1Int);
      int j = getPreferredSize(param1Int);
      return (i != j || j != getMaximumSize(param1Int));
    }
    
    abstract boolean willHaveZeroSize(boolean param1Boolean);
  }
  
  private static final class SpringDelta extends Object implements Comparable<SpringDelta> {
    public final int index;
    
    public int delta;
    
    public SpringDelta(int param1Int1, int param1Int2) {
      this.index = param1Int1;
      this.delta = param1Int2;
    }
    
    public int compareTo(SpringDelta param1SpringDelta) { return this.delta - param1SpringDelta.delta; }
    
    public String toString() { return super.toString() + "[index=" + this.index + ", delta=" + this.delta + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\GroupLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */