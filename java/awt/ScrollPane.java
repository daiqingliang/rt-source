package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.peer.ScrollPanePeer;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.ScrollPaneWheelScroller;
import sun.awt.SunToolkit;

public class ScrollPane extends Container implements Accessible {
  public static final int SCROLLBARS_AS_NEEDED = 0;
  
  public static final int SCROLLBARS_ALWAYS = 1;
  
  public static final int SCROLLBARS_NEVER = 2;
  
  private int scrollbarDisplayPolicy;
  
  private ScrollPaneAdjustable vAdjustable;
  
  private ScrollPaneAdjustable hAdjustable;
  
  private static final String base = "scrollpane";
  
  private static int nameCounter;
  
  private static final boolean defaultWheelScroll = true;
  
  private boolean wheelScrollingEnabled = true;
  
  private static final long serialVersionUID = 7956609840827222915L;
  
  private static native void initIDs();
  
  public ScrollPane() { this(0); }
  
  @ConstructorProperties({"scrollbarDisplayPolicy"})
  public ScrollPane(int paramInt) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.layoutMgr = null;
    this.width = 100;
    this.height = 100;
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
        this.scrollbarDisplayPolicy = paramInt;
        break;
      default:
        throw new IllegalArgumentException("illegal scrollbar display policy");
    } 
    this.vAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this, this), 1);
    this.hAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this, this), 0);
    setWheelScrollingEnabled(true);
  }
  
  String constructComponentName() {
    synchronized (ScrollPane.class) {
      return "scrollpane" + nameCounter++;
    } 
  }
  
  private void addToPanel(Component paramComponent, Object paramObject, int paramInt) {
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout());
    panel.add(paramComponent);
    super.addImpl(panel, paramObject, paramInt);
    validate();
  }
  
  protected final void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    synchronized (getTreeLock()) {
      if (getComponentCount() > 0)
        remove(0); 
      if (paramInt > 0)
        throw new IllegalArgumentException("position greater than 0"); 
      if (!SunToolkit.isLightweightOrUnknown(paramComponent)) {
        super.addImpl(paramComponent, paramObject, paramInt);
      } else {
        addToPanel(paramComponent, paramObject, paramInt);
      } 
    } 
  }
  
  public int getScrollbarDisplayPolicy() { return this.scrollbarDisplayPolicy; }
  
  public Dimension getViewportSize() {
    Insets insets = getInsets();
    return new Dimension(this.width - insets.right - insets.left, this.height - insets.top - insets.bottom);
  }
  
  public int getHScrollbarHeight() {
    int i = 0;
    if (this.scrollbarDisplayPolicy != 2) {
      ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.peer;
      if (scrollPanePeer != null)
        i = scrollPanePeer.getHScrollbarHeight(); 
    } 
    return i;
  }
  
  public int getVScrollbarWidth() {
    int i = 0;
    if (this.scrollbarDisplayPolicy != 2) {
      ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.peer;
      if (scrollPanePeer != null)
        i = scrollPanePeer.getVScrollbarWidth(); 
    } 
    return i;
  }
  
  public Adjustable getVAdjustable() { return this.vAdjustable; }
  
  public Adjustable getHAdjustable() { return this.hAdjustable; }
  
  public void setScrollPosition(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      if (getComponentCount() == 0)
        throw new NullPointerException("child is null"); 
      this.hAdjustable.setValue(paramInt1);
      this.vAdjustable.setValue(paramInt2);
    } 
  }
  
  public void setScrollPosition(Point paramPoint) { setScrollPosition(paramPoint.x, paramPoint.y); }
  
  @Transient
  public Point getScrollPosition() {
    synchronized (getTreeLock()) {
      if (getComponentCount() == 0)
        throw new NullPointerException("child is null"); 
      return new Point(this.hAdjustable.getValue(), this.vAdjustable.getValue());
    } 
  }
  
  public final void setLayout(LayoutManager paramLayoutManager) { throw new AWTError("ScrollPane controls layout"); }
  
  public void doLayout() { layout(); }
  
  Dimension calculateChildSize() {
    boolean bool2;
    boolean bool1;
    Dimension dimension1 = getSize();
    Insets insets = getInsets();
    int i = dimension1.width - insets.left * 2;
    int j = dimension1.height - insets.top * 2;
    Component component = getComponent(0);
    Dimension dimension2 = new Dimension(component.getPreferredSize());
    if (this.scrollbarDisplayPolicy == 0) {
      bool1 = (dimension2.height > j) ? 1 : 0;
      bool2 = (dimension2.width > i) ? 1 : 0;
    } else if (this.scrollbarDisplayPolicy == 1) {
      bool1 = bool2 = true;
    } else {
      bool1 = bool2 = false;
    } 
    int k = getVScrollbarWidth();
    int m = getHScrollbarHeight();
    if (bool1)
      i -= k; 
    if (bool2)
      j -= m; 
    if (dimension2.width < i)
      dimension2.width = i; 
    if (dimension2.height < j)
      dimension2.height = j; 
    return dimension2;
  }
  
  @Deprecated
  public void layout() {
    if (getComponentCount() == 0)
      return; 
    Component component = getComponent(0);
    Point point = getScrollPosition();
    Dimension dimension1 = calculateChildSize();
    Dimension dimension2 = getViewportSize();
    component.reshape(-point.x, -point.y, dimension1.width, dimension1.height);
    ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.peer;
    if (scrollPanePeer != null)
      scrollPanePeer.childResized(dimension1.width, dimension1.height); 
    dimension2 = getViewportSize();
    this.hAdjustable.setSpan(0, dimension1.width, dimension2.width);
    this.vAdjustable.setSpan(0, dimension1.height, dimension2.height);
  }
  
  public void printComponents(Graphics paramGraphics) {
    if (getComponentCount() == 0)
      return; 
    Component component = getComponent(0);
    Point point = component.getLocation();
    Dimension dimension = getViewportSize();
    Insets insets = getInsets();
    graphics = paramGraphics.create();
    try {
      graphics.clipRect(insets.left, insets.top, dimension.width, dimension.height);
      graphics.translate(point.x, point.y);
      component.printAll(graphics);
    } finally {
      graphics.dispose();
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      int i = 0;
      int j = 0;
      if (getComponentCount() > 0) {
        i = this.vAdjustable.getValue();
        j = this.hAdjustable.getValue();
        this.vAdjustable.setValue(0);
        this.hAdjustable.setValue(0);
      } 
      if (this.peer == null)
        this.peer = getToolkit().createScrollPane(this); 
      super.addNotify();
      if (getComponentCount() > 0) {
        this.vAdjustable.setValue(i);
        this.hAdjustable.setValue(j);
      } 
    } 
  }
  
  public String paramString() {
    String str;
    switch (this.scrollbarDisplayPolicy) {
      case 0:
        str = "as-needed";
        break;
      case 1:
        str = "always";
        break;
      case 2:
        str = "never";
        break;
      default:
        str = "invalid display policy";
        break;
    } 
    Point point = (getComponentCount() > 0) ? getScrollPosition() : new Point(0, 0);
    Insets insets = getInsets();
    return super.paramString() + ",ScrollPosition=(" + point.x + "," + point.y + "),Insets=(" + insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right + "),ScrollbarDisplayPolicy=" + str + ",wheelScrollingEnabled=" + isWheelScrollingEnabled();
  }
  
  void autoProcessMouseWheel(MouseWheelEvent paramMouseWheelEvent) { processMouseWheelEvent(paramMouseWheelEvent); }
  
  protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent) {
    if (isWheelScrollingEnabled()) {
      ScrollPaneWheelScroller.handleWheelScrolling(this, paramMouseWheelEvent);
      paramMouseWheelEvent.consume();
    } 
    super.processMouseWheelEvent(paramMouseWheelEvent);
  }
  
  protected boolean eventTypeEnabled(int paramInt) { return (paramInt == 507 && isWheelScrollingEnabled()) ? true : super.eventTypeEnabled(paramInt); }
  
  public void setWheelScrollingEnabled(boolean paramBoolean) { this.wheelScrollingEnabled = paramBoolean; }
  
  public boolean isWheelScrollingEnabled() { return this.wheelScrollingEnabled; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.defaultWriteObject(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.scrollbarDisplayPolicy = getField.get("scrollbarDisplayPolicy", 0);
    this.hAdjustable = (ScrollPaneAdjustable)getField.get("hAdjustable", null);
    this.vAdjustable = (ScrollPaneAdjustable)getField.get("vAdjustable", null);
    this.wheelScrollingEnabled = getField.get("wheelScrollingEnabled", true);
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTScrollPane(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    nameCounter = 0;
  }
  
  protected class AccessibleAWTScrollPane extends Container.AccessibleAWTContainer {
    private static final long serialVersionUID = 6100703663886637L;
    
    protected AccessibleAWTScrollPane() { super(ScrollPane.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SCROLL_PANE; }
  }
  
  class PeerFixer implements AdjustmentListener, Serializable {
    private static final long serialVersionUID = 1043664721353696630L;
    
    private ScrollPane scroller;
    
    PeerFixer(ScrollPane param1ScrollPane1) { this.scroller = param1ScrollPane1; }
    
    public void adjustmentValueChanged(AdjustmentEvent param1AdjustmentEvent) {
      Adjustable adjustable = param1AdjustmentEvent.getAdjustable();
      int i = param1AdjustmentEvent.getValue();
      ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.scroller.peer;
      if (scrollPanePeer != null)
        scrollPanePeer.setValue(adjustable, i); 
      Component component = this.scroller.getComponent(0);
      switch (adjustable.getOrientation()) {
        case 1:
          component.move((component.getLocation()).x, -i);
          return;
        case 0:
          component.move(-i, (component.getLocation()).y);
          return;
      } 
      throw new IllegalArgumentException("Illegal adjustable orientation");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ScrollPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */