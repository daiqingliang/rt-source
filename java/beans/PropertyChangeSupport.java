package java.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;

public class PropertyChangeSupport implements Serializable {
  private PropertyChangeListenerMap map = new PropertyChangeListenerMap(null);
  
  private Object source;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("propertyChangeSupportSerializedDataVersion", int.class) };
  
  static final long serialVersionUID = 6401253773779951803L;
  
  public PropertyChangeSupport(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    this.source = paramObject;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null)
      return; 
    if (paramPropertyChangeListener instanceof PropertyChangeListenerProxy) {
      PropertyChangeListenerProxy propertyChangeListenerProxy = (PropertyChangeListenerProxy)paramPropertyChangeListener;
      addPropertyChangeListener(propertyChangeListenerProxy.getPropertyName(), (PropertyChangeListener)propertyChangeListenerProxy.getListener());
    } else {
      this.map.add(null, paramPropertyChangeListener);
    } 
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null)
      return; 
    if (paramPropertyChangeListener instanceof PropertyChangeListenerProxy) {
      PropertyChangeListenerProxy propertyChangeListenerProxy = (PropertyChangeListenerProxy)paramPropertyChangeListener;
      removePropertyChangeListener(propertyChangeListenerProxy.getPropertyName(), (PropertyChangeListener)propertyChangeListenerProxy.getListener());
    } else {
      this.map.remove(null, paramPropertyChangeListener);
    } 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (PropertyChangeListener[])this.map.getListeners(); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null || paramString == null)
      return; 
    paramPropertyChangeListener = this.map.extract(paramPropertyChangeListener);
    if (paramPropertyChangeListener != null)
      this.map.add(paramString, paramPropertyChangeListener); 
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null || paramString == null)
      return; 
    paramPropertyChangeListener = this.map.extract(paramPropertyChangeListener);
    if (paramPropertyChangeListener != null)
      this.map.remove(paramString, paramPropertyChangeListener); 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) { return (PropertyChangeListener[])this.map.getListeners(paramString); }
  
  public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject1 == null || paramObject2 == null || !paramObject1.equals(paramObject2))
      firePropertyChange(new PropertyChangeEvent(this.source, paramString, paramObject1, paramObject2)); 
  }
  
  public void firePropertyChange(String paramString, int paramInt1, int paramInt2) {
    if (paramInt1 != paramInt2)
      firePropertyChange(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); 
  }
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1 != paramBoolean2)
      firePropertyChange(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2)); 
  }
  
  public void firePropertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    Object object1 = paramPropertyChangeEvent.getOldValue();
    Object object2 = paramPropertyChangeEvent.getNewValue();
    if (object1 == null || object2 == null || !object1.equals(object2)) {
      String str = paramPropertyChangeEvent.getPropertyName();
      PropertyChangeListener[] arrayOfPropertyChangeListener1 = (PropertyChangeListener[])this.map.get(null);
      PropertyChangeListener[] arrayOfPropertyChangeListener2 = (str != null) ? (PropertyChangeListener[])this.map.get(str) : null;
      fire(arrayOfPropertyChangeListener1, paramPropertyChangeEvent);
      fire(arrayOfPropertyChangeListener2, paramPropertyChangeEvent);
    } 
  }
  
  private static void fire(PropertyChangeListener[] paramArrayOfPropertyChangeListener, PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramArrayOfPropertyChangeListener != null)
      for (PropertyChangeListener propertyChangeListener : paramArrayOfPropertyChangeListener)
        propertyChangeListener.propertyChange(paramPropertyChangeEvent);  
  }
  
  public void fireIndexedPropertyChange(String paramString, int paramInt, Object paramObject1, Object paramObject2) {
    if (paramObject1 == null || paramObject2 == null || !paramObject1.equals(paramObject2))
      firePropertyChange(new IndexedPropertyChangeEvent(this.source, paramString, paramObject1, paramObject2, paramInt)); 
  }
  
  public void fireIndexedPropertyChange(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 != paramInt3)
      fireIndexedPropertyChange(paramString, paramInt1, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3)); 
  }
  
  public void fireIndexedPropertyChange(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1 != paramBoolean2)
      fireIndexedPropertyChange(paramString, paramInt, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2)); 
  }
  
  public boolean hasListeners(String paramString) { return this.map.hasListeners(paramString); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = null;
    PropertyChangeListener[] arrayOfPropertyChangeListener = null;
    synchronized (this.map) {
      for (Map.Entry entry : this.map.getEntries()) {
        String str = (String)entry.getKey();
        if (str == null) {
          arrayOfPropertyChangeListener = (PropertyChangeListener[])entry.getValue();
          continue;
        } 
        if (hashtable == null)
          hashtable = new Hashtable(); 
        PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this.source);
        propertyChangeSupport.map.set(null, (EventListener[])entry.getValue());
        hashtable.put(str, propertyChangeSupport);
      } 
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("children", hashtable);
    putField.put("source", this.source);
    putField.put("propertyChangeSupportSerializedDataVersion", 2);
    paramObjectOutputStream.writeFields();
    if (arrayOfPropertyChangeListener != null)
      for (PropertyChangeListener propertyChangeListener : arrayOfPropertyChangeListener) {
        if (propertyChangeListener instanceof Serializable)
          paramObjectOutputStream.writeObject(propertyChangeListener); 
      }  
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.map = new PropertyChangeListenerMap(null);
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Hashtable hashtable = (Hashtable)getField.get("children", null);
    this.source = getField.get("source", null);
    getField.get("propertyChangeSupportSerializedDataVersion", 2);
    Object object;
    while (null != (object = paramObjectInputStream.readObject()))
      this.map.add(null, (PropertyChangeListener)object); 
    if (hashtable != null)
      for (Map.Entry entry : hashtable.entrySet()) {
        for (PropertyChangeListener propertyChangeListener : ((PropertyChangeSupport)entry.getValue()).getPropertyChangeListeners())
          this.map.add((String)entry.getKey(), propertyChangeListener); 
      }  
  }
  
  private static final class PropertyChangeListenerMap extends ChangeListenerMap<PropertyChangeListener> {
    private static final PropertyChangeListener[] EMPTY = new PropertyChangeListener[0];
    
    private PropertyChangeListenerMap() {}
    
    protected PropertyChangeListener[] newArray(int param1Int) { return (0 < param1Int) ? new PropertyChangeListener[param1Int] : EMPTY; }
    
    protected PropertyChangeListener newProxy(String param1String, PropertyChangeListener param1PropertyChangeListener) { return new PropertyChangeListenerProxy(param1String, param1PropertyChangeListener); }
    
    public final PropertyChangeListener extract(PropertyChangeListener param1PropertyChangeListener) {
      while (param1PropertyChangeListener instanceof PropertyChangeListenerProxy)
        param1PropertyChangeListener = (PropertyChangeListener)((PropertyChangeListenerProxy)param1PropertyChangeListener).getListener(); 
      return param1PropertyChangeListener;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PropertyChangeSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */