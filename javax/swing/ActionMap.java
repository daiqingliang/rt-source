package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class ActionMap implements Serializable {
  private ArrayTable arrayTable;
  
  private ActionMap parent;
  
  public void setParent(ActionMap paramActionMap) { this.parent = paramActionMap; }
  
  public ActionMap getParent() { return this.parent; }
  
  public void put(Object paramObject, Action paramAction) {
    if (paramObject == null)
      return; 
    if (paramAction == null) {
      remove(paramObject);
    } else {
      if (this.arrayTable == null)
        this.arrayTable = new ArrayTable(); 
      this.arrayTable.put(paramObject, paramAction);
    } 
  }
  
  public Action get(Object paramObject) {
    Action action = (this.arrayTable == null) ? null : (Action)this.arrayTable.get(paramObject);
    if (action == null) {
      ActionMap actionMap = getParent();
      if (actionMap != null)
        return actionMap.get(paramObject); 
    } 
    return action;
  }
  
  public void remove(Object paramObject) {
    if (this.arrayTable != null)
      this.arrayTable.remove(paramObject); 
  }
  
  public void clear() {
    if (this.arrayTable != null)
      this.arrayTable.clear(); 
  }
  
  public Object[] keys() { return (this.arrayTable == null) ? null : this.arrayTable.getKeys(null); }
  
  public int size() { return (this.arrayTable == null) ? 0 : this.arrayTable.size(); }
  
  public Object[] allKeys() {
    int i = size();
    ActionMap actionMap = getParent();
    if (i == 0)
      return (actionMap != null) ? actionMap.allKeys() : keys(); 
    if (actionMap == null)
      return keys(); 
    Object[] arrayOfObject1 = keys();
    Object[] arrayOfObject2 = actionMap.allKeys();
    if (arrayOfObject2 == null)
      return arrayOfObject1; 
    if (arrayOfObject1 == null)
      return arrayOfObject2; 
    HashMap hashMap = new HashMap();
    int j;
    for (j = arrayOfObject1.length - 1; j >= 0; j--)
      hashMap.put(arrayOfObject1[j], arrayOfObject1[j]); 
    for (j = arrayOfObject2.length - 1; j >= 0; j--)
      hashMap.put(arrayOfObject2[j], arrayOfObject2[j]); 
    return hashMap.keySet().toArray();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    ArrayTable.writeArrayTable(paramObjectOutputStream, this.arrayTable);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    for (int i = paramObjectInputStream.readInt() - 1; i >= 0; i--)
      put(paramObjectInputStream.readObject(), (Action)paramObjectInputStream.readObject()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ActionMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */