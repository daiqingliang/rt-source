package java.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;

public class VetoableChangeSupport implements Serializable {
  private VetoableChangeListenerMap map = new VetoableChangeListenerMap(null);
  
  private Object source;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("vetoableChangeSupportSerializedDataVersion", int.class) };
  
  static final long serialVersionUID = -5090210921595982017L;
  
  public VetoableChangeSupport(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    this.source = paramObject;
  }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener == null)
      return; 
    if (paramVetoableChangeListener instanceof VetoableChangeListenerProxy) {
      VetoableChangeListenerProxy vetoableChangeListenerProxy = (VetoableChangeListenerProxy)paramVetoableChangeListener;
      addVetoableChangeListener(vetoableChangeListenerProxy.getPropertyName(), (VetoableChangeListener)vetoableChangeListenerProxy.getListener());
    } else {
      this.map.add(null, paramVetoableChangeListener);
    } 
  }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener == null)
      return; 
    if (paramVetoableChangeListener instanceof VetoableChangeListenerProxy) {
      VetoableChangeListenerProxy vetoableChangeListenerProxy = (VetoableChangeListenerProxy)paramVetoableChangeListener;
      removeVetoableChangeListener(vetoableChangeListenerProxy.getPropertyName(), (VetoableChangeListener)vetoableChangeListenerProxy.getListener());
    } else {
      this.map.remove(null, paramVetoableChangeListener);
    } 
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners() { return (VetoableChangeListener[])this.map.getListeners(); }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener == null || paramString == null)
      return; 
    paramVetoableChangeListener = this.map.extract(paramVetoableChangeListener);
    if (paramVetoableChangeListener != null)
      this.map.add(paramString, paramVetoableChangeListener); 
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener == null || paramString == null)
      return; 
    paramVetoableChangeListener = this.map.extract(paramVetoableChangeListener);
    if (paramVetoableChangeListener != null)
      this.map.remove(paramString, paramVetoableChangeListener); 
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners(String paramString) { return (VetoableChangeListener[])this.map.getListeners(paramString); }
  
  public void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2) throws PropertyVetoException {
    if (paramObject1 == null || paramObject2 == null || !paramObject1.equals(paramObject2))
      fireVetoableChange(new PropertyChangeEvent(this.source, paramString, paramObject1, paramObject2)); 
  }
  
  public void fireVetoableChange(String paramString, int paramInt1, int paramInt2) throws PropertyVetoException {
    if (paramInt1 != paramInt2)
      fireVetoableChange(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); 
  }
  
  public void fireVetoableChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws PropertyVetoException {
    if (paramBoolean1 != paramBoolean2)
      fireVetoableChange(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2)); 
  }
  
  public void fireVetoableChange(PropertyChangeEvent paramPropertyChangeEvent) throws PropertyVetoException {
    Object object1 = paramPropertyChangeEvent.getOldValue();
    Object object2 = paramPropertyChangeEvent.getNewValue();
    if (object1 == null || object2 == null || !object1.equals(object2)) {
      VetoableChangeListener[] arrayOfVetoableChangeListener3;
      String str = paramPropertyChangeEvent.getPropertyName();
      VetoableChangeListener[] arrayOfVetoableChangeListener1 = (VetoableChangeListener[])this.map.get(null);
      VetoableChangeListener[] arrayOfVetoableChangeListener2 = (str != null) ? (VetoableChangeListener[])this.map.get(str) : null;
      if (arrayOfVetoableChangeListener1 == null) {
        arrayOfVetoableChangeListener3 = arrayOfVetoableChangeListener2;
      } else if (arrayOfVetoableChangeListener2 == null) {
        arrayOfVetoableChangeListener3 = arrayOfVetoableChangeListener1;
      } else {
        arrayOfVetoableChangeListener3 = new VetoableChangeListener[arrayOfVetoableChangeListener1.length + arrayOfVetoableChangeListener2.length];
        System.arraycopy(arrayOfVetoableChangeListener1, 0, arrayOfVetoableChangeListener3, 0, arrayOfVetoableChangeListener1.length);
        System.arraycopy(arrayOfVetoableChangeListener2, 0, arrayOfVetoableChangeListener3, arrayOfVetoableChangeListener1.length, arrayOfVetoableChangeListener2.length);
      } 
      if (arrayOfVetoableChangeListener3 != null) {
        byte b = 0;
        try {
          while (b < arrayOfVetoableChangeListener3.length) {
            arrayOfVetoableChangeListener3[b].vetoableChange(paramPropertyChangeEvent);
            b++;
          } 
        } catch (PropertyVetoException propertyVetoException) {
          paramPropertyChangeEvent = new PropertyChangeEvent(this.source, str, object2, object1);
          for (byte b1 = 0; b1 < b; b1++) {
            try {
              arrayOfVetoableChangeListener3[b1].vetoableChange(paramPropertyChangeEvent);
            } catch (PropertyVetoException propertyVetoException1) {}
          } 
          throw propertyVetoException;
        } 
      } 
    } 
  }
  
  public boolean hasListeners(String paramString) { return this.map.hasListeners(paramString); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = null;
    VetoableChangeListener[] arrayOfVetoableChangeListener = null;
    synchronized (this.map) {
      for (Map.Entry entry : this.map.getEntries()) {
        String str = (String)entry.getKey();
        if (str == null) {
          arrayOfVetoableChangeListener = (VetoableChangeListener[])entry.getValue();
          continue;
        } 
        if (hashtable == null)
          hashtable = new Hashtable(); 
        VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this.source);
        vetoableChangeSupport.map.set(null, (EventListener[])entry.getValue());
        hashtable.put(str, vetoableChangeSupport);
      } 
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("children", hashtable);
    putField.put("source", this.source);
    putField.put("vetoableChangeSupportSerializedDataVersion", 2);
    paramObjectOutputStream.writeFields();
    if (arrayOfVetoableChangeListener != null)
      for (VetoableChangeListener vetoableChangeListener : arrayOfVetoableChangeListener) {
        if (vetoableChangeListener instanceof Serializable)
          paramObjectOutputStream.writeObject(vetoableChangeListener); 
      }  
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.map = new VetoableChangeListenerMap(null);
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Hashtable hashtable = (Hashtable)getField.get("children", null);
    this.source = getField.get("source", null);
    getField.get("vetoableChangeSupportSerializedDataVersion", 2);
    Object object;
    while (null != (object = paramObjectInputStream.readObject()))
      this.map.add(null, (VetoableChangeListener)object); 
    if (hashtable != null)
      for (Map.Entry entry : hashtable.entrySet()) {
        for (VetoableChangeListener vetoableChangeListener : ((VetoableChangeSupport)entry.getValue()).getVetoableChangeListeners())
          this.map.add((String)entry.getKey(), vetoableChangeListener); 
      }  
  }
  
  private static final class VetoableChangeListenerMap extends ChangeListenerMap<VetoableChangeListener> {
    private static final VetoableChangeListener[] EMPTY = new VetoableChangeListener[0];
    
    private VetoableChangeListenerMap() {}
    
    protected VetoableChangeListener[] newArray(int param1Int) { return (0 < param1Int) ? new VetoableChangeListener[param1Int] : EMPTY; }
    
    protected VetoableChangeListener newProxy(String param1String, VetoableChangeListener param1VetoableChangeListener) { return new VetoableChangeListenerProxy(param1String, param1VetoableChangeListener); }
    
    public final VetoableChangeListener extract(VetoableChangeListener param1VetoableChangeListener) {
      while (param1VetoableChangeListener instanceof VetoableChangeListenerProxy)
        param1VetoableChangeListener = (VetoableChangeListener)((VetoableChangeListenerProxy)param1VetoableChangeListener).getListener(); 
      return param1VetoableChangeListener;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\VetoableChangeSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */