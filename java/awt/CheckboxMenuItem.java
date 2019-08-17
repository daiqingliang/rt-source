package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxMenuItemPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import sun.awt.AWTAccessor;

public class CheckboxMenuItem extends MenuItem implements ItemSelectable, Accessible {
  boolean state = false;
  
  ItemListener itemListener;
  
  private static final String base = "chkmenuitem";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = 6190621106981774043L;
  
  private int checkboxMenuItemSerializedDataVersion = 1;
  
  public CheckboxMenuItem() throws HeadlessException { this("", false); }
  
  public CheckboxMenuItem(String paramString) throws HeadlessException { this(paramString, false); }
  
  public CheckboxMenuItem(String paramString, boolean paramBoolean) throws HeadlessException {
    super(paramString);
    this.state = paramBoolean;
  }
  
  String constructComponentName() {
    synchronized (CheckboxMenuItem.class) {
      return "chkmenuitem" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this); 
      super.addNotify();
    } 
  }
  
  public boolean getState() { return this.state; }
  
  public void setState(boolean paramBoolean) {
    this.state = paramBoolean;
    CheckboxMenuItemPeer checkboxMenuItemPeer = (CheckboxMenuItemPeer)this.peer;
    if (checkboxMenuItemPeer != null)
      checkboxMenuItemPeer.setState(paramBoolean); 
  }
  
  public Object[] getSelectedObjects() {
    if (this.state) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.label;
      return arrayOfObject;
    } 
    return null;
  }
  
  public void addItemListener(ItemListener paramItemListener) {
    if (paramItemListener == null)
      return; 
    this.itemListener = AWTEventMulticaster.add(this.itemListener, paramItemListener);
    this.newEventsOnly = true;
  }
  
  public void removeItemListener(ItemListener paramItemListener) {
    if (paramItemListener == null)
      return; 
    this.itemListener = AWTEventMulticaster.remove(this.itemListener, paramItemListener);
  }
  
  public ItemListener[] getItemListeners() { return (ItemListener[])getListeners(ItemListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    ItemListener itemListener1 = null;
    if (paramClass == ItemListener.class) {
      itemListener1 = this.itemListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(itemListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return (paramAWTEvent.id == 701) ? (((this.eventMask & 0x200L) != 0L || this.itemListener != null)) : super.eventEnabled(paramAWTEvent); }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ItemEvent) {
      processItemEvent((ItemEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processItemEvent(ItemEvent paramItemEvent) {
    ItemListener itemListener1 = this.itemListener;
    if (itemListener1 != null)
      itemListener1.itemStateChanged(paramItemEvent); 
  }
  
  void doMenuEvent(long paramLong, int paramInt) {
    setState(!this.state);
    Toolkit.getEventQueue().postEvent(new ItemEvent(this, 701, getLabel(), this.state ? 1 : 2));
  }
  
  public String paramString() { return super.paramString() + ",state=" + this.state; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", this.itemListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("itemL" == str) {
        addItemListener((ItemListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTCheckboxMenuItem(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setCheckboxMenuItemAccessor(new AWTAccessor.CheckboxMenuItemAccessor() {
          public boolean getState(CheckboxMenuItem param1CheckboxMenuItem) { return param1CheckboxMenuItem.state; }
        });
    nameCounter = 0;
  }
  
  protected class AccessibleAWTCheckboxMenuItem extends MenuItem.AccessibleAWTMenuItem implements AccessibleAction, AccessibleValue {
    private static final long serialVersionUID = -1122642964303476L;
    
    protected AccessibleAWTCheckboxMenuItem() { super(CheckboxMenuItem.this); }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public int getAccessibleActionCount() { return 0; }
    
    public String getAccessibleActionDescription(int param1Int) { return null; }
    
    public boolean doAccessibleAction(int param1Int) { return false; }
    
    public Number getCurrentAccessibleValue() { return null; }
    
    public boolean setCurrentAccessibleValue(Number param1Number) { return false; }
    
    public Number getMinimumAccessibleValue() { return null; }
    
    public Number getMaximumAccessibleValue() { return null; }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.CHECK_BOX; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\CheckboxMenuItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */