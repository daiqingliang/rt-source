package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ChoicePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Choice extends Component implements ItemSelectable, Accessible {
  Vector<String> pItems;
  
  int selectedIndex = -1;
  
  ItemListener itemListener;
  
  private static final String base = "choice";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = -4075310674757313071L;
  
  private int choiceSerializedDataVersion = 1;
  
  public Choice() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.pItems = new Vector();
  }
  
  String constructComponentName() {
    synchronized (Choice.class) {
      return "choice" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createChoice(this); 
      super.addNotify();
    } 
  }
  
  public int getItemCount() { return countItems(); }
  
  @Deprecated
  public int countItems() { return this.pItems.size(); }
  
  public String getItem(int paramInt) { return getItemImpl(paramInt); }
  
  final String getItemImpl(int paramInt) { return (String)this.pItems.elementAt(paramInt); }
  
  public void add(String paramString) { addItem(paramString); }
  
  public void addItem(String paramString) {
    synchronized (this) {
      insertNoInvalidate(paramString, this.pItems.size());
    } 
    invalidateIfValid();
  }
  
  private void insertNoInvalidate(String paramString, int paramInt) {
    if (paramString == null)
      throw new NullPointerException("cannot add null item to Choice"); 
    this.pItems.insertElementAt(paramString, paramInt);
    ChoicePeer choicePeer = (ChoicePeer)this.peer;
    if (choicePeer != null)
      choicePeer.add(paramString, paramInt); 
    if (this.selectedIndex < 0 || this.selectedIndex >= paramInt)
      select(0); 
  }
  
  public void insert(String paramString, int paramInt) {
    synchronized (this) {
      if (paramInt < 0)
        throw new IllegalArgumentException("index less than zero."); 
      paramInt = Math.min(paramInt, this.pItems.size());
      insertNoInvalidate(paramString, paramInt);
    } 
    invalidateIfValid();
  }
  
  public void remove(String paramString) {
    synchronized (this) {
      int i = this.pItems.indexOf(paramString);
      if (i < 0)
        throw new IllegalArgumentException("item " + paramString + " not found in choice"); 
      removeNoInvalidate(i);
    } 
    invalidateIfValid();
  }
  
  public void remove(int paramInt) {
    synchronized (this) {
      removeNoInvalidate(paramInt);
    } 
    invalidateIfValid();
  }
  
  private void removeNoInvalidate(int paramInt) {
    this.pItems.removeElementAt(paramInt);
    ChoicePeer choicePeer = (ChoicePeer)this.peer;
    if (choicePeer != null)
      choicePeer.remove(paramInt); 
    if (this.pItems.size() == 0) {
      this.selectedIndex = -1;
    } else if (this.selectedIndex == paramInt) {
      select(0);
    } else if (this.selectedIndex > paramInt) {
      select(this.selectedIndex - 1);
    } 
  }
  
  public void removeAll() throws HeadlessException {
    synchronized (this) {
      if (this.peer != null)
        ((ChoicePeer)this.peer).removeAll(); 
      this.pItems.removeAllElements();
      this.selectedIndex = -1;
    } 
    invalidateIfValid();
  }
  
  public String getSelectedItem() { return (this.selectedIndex >= 0) ? getItem(this.selectedIndex) : null; }
  
  public Object[] getSelectedObjects() {
    if (this.selectedIndex >= 0) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = getItem(this.selectedIndex);
      return arrayOfObject;
    } 
    return null;
  }
  
  public int getSelectedIndex() { return this.selectedIndex; }
  
  public void select(int paramInt) {
    if (paramInt >= this.pItems.size() || paramInt < 0)
      throw new IllegalArgumentException("illegal Choice item position: " + paramInt); 
    if (this.pItems.size() > 0) {
      this.selectedIndex = paramInt;
      ChoicePeer choicePeer = (ChoicePeer)this.peer;
      if (choicePeer != null)
        choicePeer.select(paramInt); 
    } 
  }
  
  public void select(String paramString) {
    int i = this.pItems.indexOf(paramString);
    if (i >= 0)
      select(i); 
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
  
  protected String paramString() { return super.paramString() + ",current=" + getSelectedItem(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", this.itemListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
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
      this.accessibleContext = new AccessibleAWTChoice(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
  
  protected class AccessibleAWTChoice extends Component.AccessibleAWTComponent implements AccessibleAction {
    private static final long serialVersionUID = 7175603582428509322L;
    
    public AccessibleAWTChoice() { super(Choice.this); }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.COMBO_BOX; }
    
    public int getAccessibleActionCount() { return 0; }
    
    public String getAccessibleActionDescription(int param1Int) { return null; }
    
    public boolean doAccessibleAction(int param1Int) { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Choice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */