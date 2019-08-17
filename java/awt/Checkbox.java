package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Checkbox extends Component implements ItemSelectable, Accessible {
  String label;
  
  boolean state;
  
  CheckboxGroup group;
  
  ItemListener itemListener;
  
  private static final String base = "checkbox";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = 7270714317450821763L;
  
  private int checkboxSerializedDataVersion = 1;
  
  void setStateInternal(boolean paramBoolean) {
    this.state = paramBoolean;
    CheckboxPeer checkboxPeer = (CheckboxPeer)this.peer;
    if (checkboxPeer != null)
      checkboxPeer.setState(paramBoolean); 
  }
  
  public Checkbox() throws HeadlessException { this("", false, null); }
  
  public Checkbox(String paramString) throws HeadlessException { this(paramString, false, null); }
  
  public Checkbox(String paramString, boolean paramBoolean) throws HeadlessException { this(paramString, paramBoolean, null); }
  
  public Checkbox(String paramString, boolean paramBoolean, CheckboxGroup paramCheckboxGroup) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.label = paramString;
    this.state = paramBoolean;
    this.group = paramCheckboxGroup;
    if (paramBoolean && paramCheckboxGroup != null)
      paramCheckboxGroup.setSelectedCheckbox(this); 
  }
  
  public Checkbox(String paramString, CheckboxGroup paramCheckboxGroup, boolean paramBoolean) throws HeadlessException { this(paramString, paramBoolean, paramCheckboxGroup); }
  
  String constructComponentName() {
    synchronized (Checkbox.class) {
      return "checkbox" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createCheckbox(this); 
      super.addNotify();
    } 
  }
  
  public String getLabel() { return this.label; }
  
  public void setLabel(String paramString) throws HeadlessException {
    boolean bool = false;
    synchronized (this) {
      if (paramString != this.label && (this.label == null || !this.label.equals(paramString))) {
        this.label = paramString;
        CheckboxPeer checkboxPeer = (CheckboxPeer)this.peer;
        if (checkboxPeer != null)
          checkboxPeer.setLabel(paramString); 
        bool = true;
      } 
    } 
    if (bool)
      invalidateIfValid(); 
  }
  
  public boolean getState() { return this.state; }
  
  public void setState(boolean paramBoolean) {
    CheckboxGroup checkboxGroup = this.group;
    if (checkboxGroup != null)
      if (paramBoolean) {
        checkboxGroup.setSelectedCheckbox(this);
      } else if (checkboxGroup.getSelectedCheckbox() == this) {
        paramBoolean = true;
      }  
    setStateInternal(paramBoolean);
  }
  
  public Object[] getSelectedObjects() {
    if (this.state) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.label;
      return arrayOfObject;
    } 
    return null;
  }
  
  public CheckboxGroup getCheckboxGroup() { return this.group; }
  
  public void setCheckboxGroup(CheckboxGroup paramCheckboxGroup) {
    boolean bool;
    CheckboxGroup checkboxGroup;
    if (this.group == paramCheckboxGroup)
      return; 
    synchronized (this) {
      checkboxGroup = this.group;
      bool = getState();
      this.group = paramCheckboxGroup;
      CheckboxPeer checkboxPeer = (CheckboxPeer)this.peer;
      if (checkboxPeer != null)
        checkboxPeer.setCheckboxGroup(paramCheckboxGroup); 
      if (this.group != null && getState())
        if (this.group.getSelectedCheckbox() != null) {
          setState(false);
        } else {
          this.group.setSelectedCheckbox(this);
        }  
    } 
    if (checkboxGroup != null && bool)
      checkboxGroup.setSelectedCheckbox(null); 
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
  
  protected String paramString() {
    String str1 = super.paramString();
    String str2 = this.label;
    if (str2 != null)
      str1 = str1 + ",label=" + str2; 
    return str1 + ",state=" + this.state;
  }
  
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
      this.accessibleContext = new AccessibleAWTCheckbox(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    nameCounter = 0;
  }
  
  protected class AccessibleAWTCheckbox extends Component.AccessibleAWTComponent implements ItemListener, AccessibleAction, AccessibleValue {
    private static final long serialVersionUID = 7881579233144754107L;
    
    public AccessibleAWTCheckbox() {
      super(Checkbox.this);
      this$0.addItemListener(this);
    }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {
      Checkbox checkbox = (Checkbox)param1ItemEvent.getSource();
      if (Checkbox.this.accessibleContext != null)
        if (checkbox.getState()) {
          Checkbox.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
        } else {
          Checkbox.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
        }  
    }
    
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
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (Checkbox.this.getState())
        accessibleStateSet.add(AccessibleState.CHECKED); 
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Checkbox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */