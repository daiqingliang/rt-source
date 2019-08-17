package javax.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import javax.swing.event.SwingPropertyChangeSupport;
import sun.security.action.GetPropertyAction;

public abstract class AbstractAction implements Action, Cloneable, Serializable {
  private static Boolean RECONFIGURE_ON_NULL;
  
  protected boolean enabled = true;
  
  private ArrayTable arrayTable;
  
  protected SwingPropertyChangeSupport changeSupport;
  
  static boolean shouldReconfigure(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getPropertyName() == null)
      synchronized (AbstractAction.class) {
        if (RECONFIGURE_ON_NULL == null)
          RECONFIGURE_ON_NULL = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("swing.actions.reconfigureOnNull", "false"))); 
        return RECONFIGURE_ON_NULL.booleanValue();
      }  
    return false;
  }
  
  static void setEnabledFromAction(JComponent paramJComponent, Action paramAction) { paramJComponent.setEnabled((paramAction != null) ? paramAction.isEnabled() : 1); }
  
  static void setToolTipTextFromAction(JComponent paramJComponent, Action paramAction) { paramJComponent.setToolTipText((paramAction != null) ? (String)paramAction.getValue("ShortDescription") : null); }
  
  static boolean hasSelectedKey(Action paramAction) { return (paramAction != null && paramAction.getValue("SwingSelectedKey") != null); }
  
  static boolean isSelected(Action paramAction) { return Boolean.TRUE.equals(paramAction.getValue("SwingSelectedKey")); }
  
  public AbstractAction() {}
  
  public AbstractAction(String paramString) { putValue("Name", paramString); }
  
  public AbstractAction(String paramString, Icon paramIcon) {
    this(paramString);
    putValue("SmallIcon", paramIcon);
  }
  
  public Object getValue(String paramString) { return (paramString == "enabled") ? Boolean.valueOf(this.enabled) : ((this.arrayTable == null) ? null : this.arrayTable.get(paramString)); }
  
  public void putValue(String paramString, Object paramObject) {
    Object object = null;
    if (paramString == "enabled") {
      if (paramObject == null || !(paramObject instanceof Boolean))
        paramObject = Boolean.valueOf(false); 
      object = Boolean.valueOf(this.enabled);
      this.enabled = ((Boolean)paramObject).booleanValue();
    } else {
      if (this.arrayTable == null)
        this.arrayTable = new ArrayTable(); 
      if (this.arrayTable.containsKey(paramString))
        object = this.arrayTable.get(paramString); 
      if (paramObject == null) {
        this.arrayTable.remove(paramString);
      } else {
        this.arrayTable.put(paramString, paramObject);
      } 
    } 
    firePropertyChange(paramString, object, paramObject);
  }
  
  public boolean isEnabled() { return this.enabled; }
  
  public void setEnabled(boolean paramBoolean) {
    boolean bool = this.enabled;
    if (bool != paramBoolean) {
      this.enabled = paramBoolean;
      firePropertyChange("enabled", Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
    } 
  }
  
  public Object[] getKeys() {
    if (this.arrayTable == null)
      return null; 
    Object[] arrayOfObject = new Object[this.arrayTable.size()];
    this.arrayTable.getKeys(arrayOfObject);
    return arrayOfObject;
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (this.changeSupport == null || (paramObject1 != null && paramObject2 != null && paramObject1.equals(paramObject2)))
      return; 
    this.changeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      this.changeSupport = new SwingPropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      return; 
    this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(); }
  
  protected Object clone() throws CloneNotSupportedException {
    AbstractAction abstractAction = (AbstractAction)super.clone();
    synchronized (this) {
      if (this.arrayTable != null)
        abstractAction.arrayTable = (ArrayTable)this.arrayTable.clone(); 
    } 
    return abstractAction;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    ArrayTable.writeArrayTable(paramObjectOutputStream, this.arrayTable);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    for (int i = paramObjectInputStream.readInt() - 1; i >= 0; i--)
      putValue((String)paramObjectInputStream.readObject(), paramObjectInputStream.readObject()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\AbstractAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */