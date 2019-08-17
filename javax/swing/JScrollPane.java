package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.UIResource;

public class JScrollPane extends JComponent implements ScrollPaneConstants, Accessible {
  private Border viewportBorder;
  
  private static final String uiClassID = "ScrollPaneUI";
  
  protected int verticalScrollBarPolicy = 20;
  
  protected int horizontalScrollBarPolicy = 30;
  
  protected JViewport viewport;
  
  protected JScrollBar verticalScrollBar;
  
  protected JScrollBar horizontalScrollBar;
  
  protected JViewport rowHeader;
  
  protected JViewport columnHeader;
  
  protected Component lowerLeft;
  
  protected Component lowerRight;
  
  protected Component upperLeft;
  
  protected Component upperRight;
  
  private boolean wheelScrollState = true;
  
  public JScrollPane(Component paramComponent, int paramInt1, int paramInt2) {
    setLayout(new ScrollPaneLayout.UIResource());
    setVerticalScrollBarPolicy(paramInt1);
    setHorizontalScrollBarPolicy(paramInt2);
    setViewport(createViewport());
    setVerticalScrollBar(createVerticalScrollBar());
    setHorizontalScrollBar(createHorizontalScrollBar());
    if (paramComponent != null)
      setViewportView(paramComponent); 
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
    if (!getComponentOrientation().isLeftToRight())
      this.viewport.setViewPosition(new Point(2147483647, 0)); 
  }
  
  public JScrollPane(Component paramComponent) { this(paramComponent, 20, 30); }
  
  public JScrollPane(int paramInt1, int paramInt2) { this(null, paramInt1, paramInt2); }
  
  public JScrollPane() { this(null, 20, 30); }
  
  public ScrollPaneUI getUI() { return (ScrollPaneUI)this.ui; }
  
  public void setUI(ScrollPaneUI paramScrollPaneUI) { setUI(paramScrollPaneUI); }
  
  public void updateUI() { setUI((ScrollPaneUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ScrollPaneUI"; }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    if (paramLayoutManager instanceof ScrollPaneLayout) {
      super.setLayout(paramLayoutManager);
      ((ScrollPaneLayout)paramLayoutManager).syncWithScrollPane(this);
    } else if (paramLayoutManager == null) {
      super.setLayout(paramLayoutManager);
    } else {
      String str = "layout of JScrollPane must be a ScrollPaneLayout";
      throw new ClassCastException(str);
    } 
  }
  
  public boolean isValidateRoot() { return true; }
  
  public int getVerticalScrollBarPolicy() { return this.verticalScrollBarPolicy; }
  
  public void setVerticalScrollBarPolicy(int paramInt) {
    switch (paramInt) {
      case 20:
      case 21:
      case 22:
        break;
      default:
        throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
    } 
    int i = this.verticalScrollBarPolicy;
    this.verticalScrollBarPolicy = paramInt;
    firePropertyChange("verticalScrollBarPolicy", i, paramInt);
    revalidate();
    repaint();
  }
  
  public int getHorizontalScrollBarPolicy() { return this.horizontalScrollBarPolicy; }
  
  public void setHorizontalScrollBarPolicy(int paramInt) {
    switch (paramInt) {
      case 30:
      case 31:
      case 32:
        break;
      default:
        throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
    } 
    int i = this.horizontalScrollBarPolicy;
    this.horizontalScrollBarPolicy = paramInt;
    firePropertyChange("horizontalScrollBarPolicy", i, paramInt);
    revalidate();
    repaint();
  }
  
  public Border getViewportBorder() { return this.viewportBorder; }
  
  public void setViewportBorder(Border paramBorder) {
    Border border = this.viewportBorder;
    this.viewportBorder = paramBorder;
    firePropertyChange("viewportBorder", border, paramBorder);
  }
  
  public Rectangle getViewportBorderBounds() {
    Rectangle rectangle = new Rectangle(getSize());
    Insets insets = getInsets();
    rectangle.x = insets.left;
    rectangle.y = insets.top;
    rectangle.width -= insets.left + insets.right;
    rectangle.height -= insets.top + insets.bottom;
    boolean bool = SwingUtilities.isLeftToRight(this);
    JViewport jViewport1 = getColumnHeader();
    if (jViewport1 != null && jViewport1.isVisible()) {
      int i = jViewport1.getHeight();
      rectangle.y += i;
      rectangle.height -= i;
    } 
    JViewport jViewport2 = getRowHeader();
    if (jViewport2 != null && jViewport2.isVisible()) {
      int i = jViewport2.getWidth();
      if (bool)
        rectangle.x += i; 
      rectangle.width -= i;
    } 
    JScrollBar jScrollBar1 = getVerticalScrollBar();
    if (jScrollBar1 != null && jScrollBar1.isVisible()) {
      int i = jScrollBar1.getWidth();
      if (!bool)
        rectangle.x += i; 
      rectangle.width -= i;
    } 
    JScrollBar jScrollBar2 = getHorizontalScrollBar();
    if (jScrollBar2 != null && jScrollBar2.isVisible())
      rectangle.height -= jScrollBar2.getHeight(); 
    return rectangle;
  }
  
  public JScrollBar createHorizontalScrollBar() { return new ScrollBar(0); }
  
  @Transient
  public JScrollBar getHorizontalScrollBar() { return this.horizontalScrollBar; }
  
  public void setHorizontalScrollBar(JScrollBar paramJScrollBar) {
    JScrollBar jScrollBar = getHorizontalScrollBar();
    this.horizontalScrollBar = paramJScrollBar;
    if (paramJScrollBar != null) {
      add(paramJScrollBar, "HORIZONTAL_SCROLLBAR");
    } else if (jScrollBar != null) {
      remove(jScrollBar);
    } 
    firePropertyChange("horizontalScrollBar", jScrollBar, paramJScrollBar);
    revalidate();
    repaint();
  }
  
  public JScrollBar createVerticalScrollBar() { return new ScrollBar(1); }
  
  @Transient
  public JScrollBar getVerticalScrollBar() { return this.verticalScrollBar; }
  
  public void setVerticalScrollBar(JScrollBar paramJScrollBar) {
    JScrollBar jScrollBar = getVerticalScrollBar();
    this.verticalScrollBar = paramJScrollBar;
    add(paramJScrollBar, "VERTICAL_SCROLLBAR");
    firePropertyChange("verticalScrollBar", jScrollBar, paramJScrollBar);
    revalidate();
    repaint();
  }
  
  protected JViewport createViewport() { return new JViewport(); }
  
  public JViewport getViewport() { return this.viewport; }
  
  public void setViewport(JViewport paramJViewport) {
    JViewport jViewport = getViewport();
    this.viewport = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "VIEWPORT");
    } else if (jViewport != null) {
      remove(jViewport);
    } 
    firePropertyChange("viewport", jViewport, paramJViewport);
    if (this.accessibleContext != null)
      ((AccessibleJScrollPane)this.accessibleContext).resetViewPort(); 
    revalidate();
    repaint();
  }
  
  public void setViewportView(Component paramComponent) {
    if (getViewport() == null)
      setViewport(createViewport()); 
    getViewport().setView(paramComponent);
  }
  
  @Transient
  public JViewport getRowHeader() { return this.rowHeader; }
  
  public void setRowHeader(JViewport paramJViewport) {
    JViewport jViewport = getRowHeader();
    this.rowHeader = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "ROW_HEADER");
    } else if (jViewport != null) {
      remove(jViewport);
    } 
    firePropertyChange("rowHeader", jViewport, paramJViewport);
    revalidate();
    repaint();
  }
  
  public void setRowHeaderView(Component paramComponent) {
    if (getRowHeader() == null)
      setRowHeader(createViewport()); 
    getRowHeader().setView(paramComponent);
  }
  
  @Transient
  public JViewport getColumnHeader() { return this.columnHeader; }
  
  public void setColumnHeader(JViewport paramJViewport) {
    JViewport jViewport = getColumnHeader();
    this.columnHeader = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "COLUMN_HEADER");
    } else if (jViewport != null) {
      remove(jViewport);
    } 
    firePropertyChange("columnHeader", jViewport, paramJViewport);
    revalidate();
    repaint();
  }
  
  public void setColumnHeaderView(Component paramComponent) {
    if (getColumnHeader() == null)
      setColumnHeader(createViewport()); 
    getColumnHeader().setView(paramComponent);
  }
  
  public Component getCorner(String paramString) {
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramString.equals("LOWER_LEADING_CORNER")) {
      paramString = bool ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
    } else if (paramString.equals("LOWER_TRAILING_CORNER")) {
      paramString = bool ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    } else if (paramString.equals("UPPER_LEADING_CORNER")) {
      paramString = bool ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
    } else if (paramString.equals("UPPER_TRAILING_CORNER")) {
      paramString = bool ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
    } 
    return paramString.equals("LOWER_LEFT_CORNER") ? this.lowerLeft : (paramString.equals("LOWER_RIGHT_CORNER") ? this.lowerRight : (paramString.equals("UPPER_LEFT_CORNER") ? this.upperLeft : (paramString.equals("UPPER_RIGHT_CORNER") ? this.upperRight : null)));
  }
  
  public void setCorner(String paramString, Component paramComponent) {
    Component component;
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramString.equals("LOWER_LEADING_CORNER")) {
      paramString = bool ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
    } else if (paramString.equals("LOWER_TRAILING_CORNER")) {
      paramString = bool ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    } else if (paramString.equals("UPPER_LEADING_CORNER")) {
      paramString = bool ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
    } else if (paramString.equals("UPPER_TRAILING_CORNER")) {
      paramString = bool ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
    } 
    if (paramString.equals("LOWER_LEFT_CORNER")) {
      component = this.lowerLeft;
      this.lowerLeft = paramComponent;
    } else if (paramString.equals("LOWER_RIGHT_CORNER")) {
      component = this.lowerRight;
      this.lowerRight = paramComponent;
    } else if (paramString.equals("UPPER_LEFT_CORNER")) {
      component = this.upperLeft;
      this.upperLeft = paramComponent;
    } else if (paramString.equals("UPPER_RIGHT_CORNER")) {
      component = this.upperRight;
      this.upperRight = paramComponent;
    } else {
      throw new IllegalArgumentException("invalid corner key");
    } 
    if (component != null)
      remove(component); 
    if (paramComponent != null)
      add(paramComponent, paramString); 
    firePropertyChange(paramString, component, paramComponent);
    revalidate();
    repaint();
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation) {
    super.setComponentOrientation(paramComponentOrientation);
    if (this.verticalScrollBar != null)
      this.verticalScrollBar.setComponentOrientation(paramComponentOrientation); 
    if (this.horizontalScrollBar != null)
      this.horizontalScrollBar.setComponentOrientation(paramComponentOrientation); 
  }
  
  public boolean isWheelScrollingEnabled() { return this.wheelScrollState; }
  
  public void setWheelScrollingEnabled(boolean paramBoolean) {
    boolean bool = this.wheelScrollState;
    this.wheelScrollState = paramBoolean;
    firePropertyChange("wheelScrollingEnabled", bool, paramBoolean);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ScrollPaneUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str4;
    String str3;
    String str1 = (this.viewportBorder != null) ? this.viewportBorder.toString() : "";
    String str2 = (this.viewport != null) ? this.viewport.toString() : "";
    if (this.verticalScrollBarPolicy == 20) {
      str3 = "VERTICAL_SCROLLBAR_AS_NEEDED";
    } else if (this.verticalScrollBarPolicy == 21) {
      str3 = "VERTICAL_SCROLLBAR_NEVER";
    } else if (this.verticalScrollBarPolicy == 22) {
      str3 = "VERTICAL_SCROLLBAR_ALWAYS";
    } else {
      str3 = "";
    } 
    if (this.horizontalScrollBarPolicy == 30) {
      str4 = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
    } else if (this.horizontalScrollBarPolicy == 31) {
      str4 = "HORIZONTAL_SCROLLBAR_NEVER";
    } else if (this.horizontalScrollBarPolicy == 32) {
      str4 = "HORIZONTAL_SCROLLBAR_ALWAYS";
    } else {
      str4 = "";
    } 
    String str5 = (this.horizontalScrollBar != null) ? this.horizontalScrollBar.toString() : "";
    String str6 = (this.verticalScrollBar != null) ? this.verticalScrollBar.toString() : "";
    String str7 = (this.columnHeader != null) ? this.columnHeader.toString() : "";
    String str8 = (this.rowHeader != null) ? this.rowHeader.toString() : "";
    String str9 = (this.lowerLeft != null) ? this.lowerLeft.toString() : "";
    String str10 = (this.lowerRight != null) ? this.lowerRight.toString() : "";
    String str11 = (this.upperLeft != null) ? this.upperLeft.toString() : "";
    String str12 = (this.upperRight != null) ? this.upperRight.toString() : "";
    return super.paramString() + ",columnHeader=" + str7 + ",horizontalScrollBar=" + str5 + ",horizontalScrollBarPolicy=" + str4 + ",lowerLeft=" + str9 + ",lowerRight=" + str10 + ",rowHeader=" + str8 + ",upperLeft=" + str11 + ",upperRight=" + str12 + ",verticalScrollBar=" + str6 + ",verticalScrollBarPolicy=" + str3 + ",viewport=" + str2 + ",viewportBorder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJScrollPane(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJScrollPane extends JComponent.AccessibleJComponent implements ChangeListener, PropertyChangeListener {
    protected JViewport viewPort = null;
    
    public void resetViewPort() {
      if (this.viewPort != null) {
        this.viewPort.removeChangeListener(this);
        this.viewPort.removePropertyChangeListener(this);
      } 
      this.viewPort = JScrollPane.this.getViewport();
      if (this.viewPort != null) {
        this.viewPort.addChangeListener(this);
        this.viewPort.addPropertyChangeListener(this);
      } 
    }
    
    public AccessibleJScrollPane() {
      super(JScrollPane.this);
      resetViewPort();
      JScrollBar jScrollBar = this$0.getHorizontalScrollBar();
      if (jScrollBar != null)
        setScrollBarRelations(jScrollBar); 
      jScrollBar = this$0.getVerticalScrollBar();
      if (jScrollBar != null)
        setScrollBarRelations(jScrollBar); 
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SCROLL_PANE; }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (param1ChangeEvent == null)
        throw new NullPointerException(); 
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ((str == "horizontalScrollBar" || str == "verticalScrollBar") && param1PropertyChangeEvent.getNewValue() instanceof JScrollBar)
        setScrollBarRelations((JScrollBar)param1PropertyChangeEvent.getNewValue()); 
    }
    
    void setScrollBarRelations(JScrollBar param1JScrollBar) {
      AccessibleRelation accessibleRelation1 = new AccessibleRelation(AccessibleRelation.CONTROLLED_BY, param1JScrollBar);
      AccessibleRelation accessibleRelation2 = new AccessibleRelation(AccessibleRelation.CONTROLLER_FOR, JScrollPane.this);
      AccessibleContext accessibleContext = param1JScrollBar.getAccessibleContext();
      accessibleContext.getAccessibleRelationSet().add(accessibleRelation2);
      getAccessibleRelationSet().add(accessibleRelation1);
    }
  }
  
  protected class ScrollBar extends JScrollBar implements UIResource {
    private boolean unitIncrementSet;
    
    private boolean blockIncrementSet;
    
    public ScrollBar(int param1Int) {
      super(param1Int);
      putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
    }
    
    public void setUnitIncrement(int param1Int) {
      this.unitIncrementSet = true;
      putClientProperty("JScrollBar.fastWheelScrolling", null);
      super.setUnitIncrement(param1Int);
    }
    
    public int getUnitIncrement(int param1Int) {
      JViewport jViewport = JScrollPane.this.getViewport();
      if (!this.unitIncrementSet && jViewport != null && jViewport.getView() instanceof Scrollable) {
        Scrollable scrollable = (Scrollable)jViewport.getView();
        Rectangle rectangle = jViewport.getViewRect();
        return scrollable.getScrollableUnitIncrement(rectangle, getOrientation(), param1Int);
      } 
      return super.getUnitIncrement(param1Int);
    }
    
    public void setBlockIncrement(int param1Int) {
      this.blockIncrementSet = true;
      putClientProperty("JScrollBar.fastWheelScrolling", null);
      super.setBlockIncrement(param1Int);
    }
    
    public int getBlockIncrement(int param1Int) {
      JViewport jViewport = JScrollPane.this.getViewport();
      if (this.blockIncrementSet || jViewport == null)
        return super.getBlockIncrement(param1Int); 
      if (jViewport.getView() instanceof Scrollable) {
        Scrollable scrollable = (Scrollable)jViewport.getView();
        Rectangle rectangle = jViewport.getViewRect();
        return scrollable.getScrollableBlockIncrement(rectangle, getOrientation(), param1Int);
      } 
      return (getOrientation() == 1) ? (jViewport.getExtentSize()).height : (jViewport.getExtentSize()).width;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JScrollPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */