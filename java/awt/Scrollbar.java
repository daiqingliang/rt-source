package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollbarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Scrollbar extends Component implements Adjustable, Accessible {
  public static final int HORIZONTAL = 0;
  
  public static final int VERTICAL = 1;
  
  int value;
  
  int maximum;
  
  int minimum;
  
  int visibleAmount;
  
  int orientation;
  
  int lineIncrement = 1;
  
  int pageIncrement = 10;
  
  boolean isAdjusting;
  
  AdjustmentListener adjustmentListener;
  
  private static final String base = "scrollbar";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = 8451667562882310543L;
  
  private int scrollbarSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public Scrollbar() { this(1, 0, 10, 0, 100); }
  
  public Scrollbar(int paramInt) throws HeadlessException { this(paramInt, 0, 10, 0, 100); }
  
  public Scrollbar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    switch (paramInt1) {
      case 0:
      case 1:
        this.orientation = paramInt1;
        break;
      default:
        throw new IllegalArgumentException("illegal scrollbar orientation");
    } 
    setValues(paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  String constructComponentName() {
    synchronized (Scrollbar.class) {
      return "scrollbar" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createScrollbar(this); 
      super.addNotify();
    } 
  }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) throws HeadlessException {
    synchronized (getTreeLock()) {
      if (paramInt == this.orientation)
        return; 
      switch (paramInt) {
        case 0:
        case 1:
          this.orientation = paramInt;
          break;
        default:
          throw new IllegalArgumentException("illegal scrollbar orientation");
      } 
      if (this.peer != null) {
        removeNotify();
        addNotify();
        invalidate();
      } 
    } 
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", (paramInt == 1) ? AccessibleState.HORIZONTAL : AccessibleState.VERTICAL, (paramInt == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL); 
  }
  
  public int getValue() { return this.value; }
  
  public void setValue(int paramInt) throws HeadlessException { setValues(paramInt, this.visibleAmount, this.minimum, this.maximum); }
  
  public int getMinimum() { return this.minimum; }
  
  public void setMinimum(int paramInt) throws HeadlessException { setValues(this.value, this.visibleAmount, paramInt, this.maximum); }
  
  public int getMaximum() { return this.maximum; }
  
  public void setMaximum(int paramInt) throws HeadlessException {
    if (paramInt == Integer.MIN_VALUE)
      paramInt = -2147483647; 
    if (this.minimum >= paramInt)
      this.minimum = paramInt - 1; 
    setValues(this.value, this.visibleAmount, this.minimum, paramInt);
  }
  
  public int getVisibleAmount() { return getVisible(); }
  
  @Deprecated
  public int getVisible() { return this.visibleAmount; }
  
  public void setVisibleAmount(int paramInt) throws HeadlessException { setValues(this.value, paramInt, this.minimum, this.maximum); }
  
  public void setUnitIncrement(int paramInt) throws HeadlessException { setLineIncrement(paramInt); }
  
  @Deprecated
  public void setLineIncrement(int paramInt) throws HeadlessException {
    boolean bool = (paramInt < 1) ? 1 : paramInt;
    if (this.lineIncrement == bool)
      return; 
    this.lineIncrement = bool;
    ScrollbarPeer scrollbarPeer = (ScrollbarPeer)this.peer;
    if (scrollbarPeer != null)
      scrollbarPeer.setLineIncrement(this.lineIncrement); 
  }
  
  public int getUnitIncrement() { return getLineIncrement(); }
  
  @Deprecated
  public int getLineIncrement() { return this.lineIncrement; }
  
  public void setBlockIncrement(int paramInt) throws HeadlessException { setPageIncrement(paramInt); }
  
  @Deprecated
  public void setPageIncrement(int paramInt) throws HeadlessException {
    boolean bool = (paramInt < 1) ? 1 : paramInt;
    if (this.pageIncrement == bool)
      return; 
    this.pageIncrement = bool;
    ScrollbarPeer scrollbarPeer = (ScrollbarPeer)this.peer;
    if (scrollbarPeer != null)
      scrollbarPeer.setPageIncrement(this.pageIncrement); 
  }
  
  public int getBlockIncrement() { return getPageIncrement(); }
  
  @Deprecated
  public int getPageIncrement() { return this.pageIncrement; }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i;
    synchronized (this) {
      if (paramInt3 == Integer.MAX_VALUE)
        paramInt3 = 2147483646; 
      if (paramInt4 <= paramInt3)
        paramInt4 = paramInt3 + 1; 
      long l = paramInt4 - paramInt3;
      if (l > 2147483647L) {
        l = 2147483647L;
        paramInt4 = paramInt3 + (int)l;
      } 
      if (paramInt2 > (int)l)
        paramInt2 = (int)l; 
      if (paramInt2 < 1)
        paramInt2 = 1; 
      if (paramInt1 < paramInt3)
        paramInt1 = paramInt3; 
      if (paramInt1 > paramInt4 - paramInt2)
        paramInt1 = paramInt4 - paramInt2; 
      i = this.value;
      this.value = paramInt1;
      this.visibleAmount = paramInt2;
      this.minimum = paramInt3;
      this.maximum = paramInt4;
      ScrollbarPeer scrollbarPeer = (ScrollbarPeer)this.peer;
      if (scrollbarPeer != null)
        scrollbarPeer.setValues(paramInt1, this.visibleAmount, paramInt3, paramInt4); 
    } 
    if (i != paramInt1 && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(paramInt1)); 
  }
  
  public boolean getValueIsAdjusting() { return this.isAdjusting; }
  
  public void setValueIsAdjusting(boolean paramBoolean) {
    boolean bool;
    synchronized (this) {
      bool = this.isAdjusting;
      this.isAdjusting = paramBoolean;
    } 
    if (bool != paramBoolean && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.BUSY : null, paramBoolean ? AccessibleState.BUSY : null); 
  }
  
  public void addAdjustmentListener(AdjustmentListener paramAdjustmentListener) {
    if (paramAdjustmentListener == null)
      return; 
    this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, paramAdjustmentListener);
    this.newEventsOnly = true;
  }
  
  public void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener) {
    if (paramAdjustmentListener == null)
      return; 
    this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, paramAdjustmentListener);
  }
  
  public AdjustmentListener[] getAdjustmentListeners() { return (AdjustmentListener[])getListeners(AdjustmentListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    AdjustmentListener adjustmentListener1 = null;
    if (paramClass == AdjustmentListener.class) {
      adjustmentListener1 = this.adjustmentListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(adjustmentListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return (paramAWTEvent.id == 601) ? (((this.eventMask & 0x100L) != 0L || this.adjustmentListener != null)) : super.eventEnabled(paramAWTEvent); }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof AdjustmentEvent) {
      processAdjustmentEvent((AdjustmentEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processAdjustmentEvent(AdjustmentEvent paramAdjustmentEvent) {
    AdjustmentListener adjustmentListener1 = this.adjustmentListener;
    if (adjustmentListener1 != null)
      adjustmentListener1.adjustmentValueChanged(paramAdjustmentEvent); 
  }
  
  protected String paramString() { return super.paramString() + ",val=" + this.value + ",vis=" + this.visibleAmount + ",min=" + this.minimum + ",max=" + this.maximum + ((this.orientation == 1) ? ",vert" : ",horz") + ",isAdjusting=" + this.isAdjusting; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "adjustmentL", this.adjustmentListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("adjustmentL" == str) {
        addAdjustmentListener((AdjustmentListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTScrollBar(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
  
  protected class AccessibleAWTScrollBar extends Component.AccessibleAWTComponent implements AccessibleValue {
    private static final long serialVersionUID = -344337268523697807L;
    
    protected AccessibleAWTScrollBar() { super(Scrollbar.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (Scrollbar.this.getValueIsAdjusting())
        accessibleStateSet.add(AccessibleState.BUSY); 
      if (Scrollbar.this.getOrientation() == 1) {
        accessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        accessibleStateSet.add(AccessibleState.HORIZONTAL);
      } 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SCROLL_BAR; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(Scrollbar.this.getValue()); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number instanceof Integer) {
        Scrollbar.this.setValue(param1Number.intValue());
        return true;
      } 
      return false;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(Scrollbar.this.getMinimum()); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(Scrollbar.this.getMaximum()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Scrollbar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */