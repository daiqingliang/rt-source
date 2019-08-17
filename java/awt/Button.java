package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ButtonPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;

public class Button extends Component implements Accessible {
  String label;
  
  String actionCommand;
  
  ActionListener actionListener;
  
  private static final String base = "button";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = -8774683716313001058L;
  
  private int buttonSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public Button() { this(""); }
  
  public Button(String paramString) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.label = paramString;
  }
  
  String constructComponentName() {
    synchronized (Button.class) {
      return "button" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createButton(this); 
      super.addNotify();
    } 
  }
  
  public String getLabel() { return this.label; }
  
  public void setLabel(String paramString) throws HeadlessException {
    boolean bool = false;
    synchronized (this) {
      if (paramString != this.label && (this.label == null || !this.label.equals(paramString))) {
        this.label = paramString;
        ButtonPeer buttonPeer = (ButtonPeer)this.peer;
        if (buttonPeer != null)
          buttonPeer.setLabel(paramString); 
        bool = true;
      } 
    } 
    if (bool)
      invalidateIfValid(); 
  }
  
  public void setActionCommand(String paramString) throws HeadlessException { this.actionCommand = paramString; }
  
  public String getActionCommand() { return (this.actionCommand == null) ? this.label : this.actionCommand; }
  
  public void addActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.add(this.actionListener, paramActionListener);
    this.newEventsOnly = true;
  }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, paramActionListener);
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])getListeners(ActionListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    ActionListener actionListener1 = null;
    if (paramClass == ActionListener.class) {
      actionListener1 = this.actionListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(actionListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return (paramAWTEvent.id == 1001) ? (((this.eventMask & 0x80L) != 0L || this.actionListener != null)) : super.eventEnabled(paramAWTEvent); }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ActionEvent) {
      processActionEvent((ActionEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processActionEvent(ActionEvent paramActionEvent) {
    ActionListener actionListener1 = this.actionListener;
    if (actionListener1 != null)
      actionListener1.actionPerformed(paramActionEvent); 
  }
  
  protected String paramString() { return super.paramString() + ",label=" + this.label; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", this.actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTButton(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
  
  protected class AccessibleAWTButton extends Component.AccessibleAWTComponent implements AccessibleAction, AccessibleValue {
    private static final long serialVersionUID = -5932203980244017102L;
    
    protected AccessibleAWTButton() { super(Button.this); }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((Button.this.getLabel() == null) ? super.getAccessibleName() : Button.this.getLabel()); }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public int getAccessibleActionCount() { return 1; }
    
    public String getAccessibleActionDescription(int param1Int) { return (param1Int == 0) ? "click" : null; }
    
    public boolean doAccessibleAction(int param1Int) {
      if (param1Int == 0) {
        Toolkit.getEventQueue().postEvent(new ActionEvent(Button.this, 1001, Button.this.getActionCommand()));
        return true;
      } 
      return false;
    }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(0); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) { return false; }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(0); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(0); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PUSH_BUTTON; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Button.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */