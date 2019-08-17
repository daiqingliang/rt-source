package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class InputMap implements Serializable {
  private ArrayTable arrayTable;
  
  private InputMap parent;
  
  public void setParent(InputMap paramInputMap) { this.parent = paramInputMap; }
  
  public InputMap getParent() { return this.parent; }
  
  public void put(KeyStroke paramKeyStroke, Object paramObject) {
    if (paramKeyStroke == null)
      return; 
    if (paramObject == null) {
      remove(paramKeyStroke);
    } else {
      if (this.arrayTable == null)
        this.arrayTable = new ArrayTable(); 
      this.arrayTable.put(paramKeyStroke, paramObject);
    } 
  }
  
  public Object get(KeyStroke paramKeyStroke) {
    if (this.arrayTable == null) {
      InputMap inputMap = getParent();
      return (inputMap != null) ? inputMap.get(paramKeyStroke) : null;
    } 
    Object object = this.arrayTable.get(paramKeyStroke);
    if (object == null) {
      InputMap inputMap = getParent();
      if (inputMap != null)
        return inputMap.get(paramKeyStroke); 
    } 
    return object;
  }
  
  public void remove(KeyStroke paramKeyStroke) {
    if (this.arrayTable != null)
      this.arrayTable.remove(paramKeyStroke); 
  }
  
  public void clear() {
    if (this.arrayTable != null)
      this.arrayTable.clear(); 
  }
  
  public KeyStroke[] keys() {
    if (this.arrayTable == null)
      return null; 
    KeyStroke[] arrayOfKeyStroke = new KeyStroke[this.arrayTable.size()];
    this.arrayTable.getKeys(arrayOfKeyStroke);
    return arrayOfKeyStroke;
  }
  
  public int size() { return (this.arrayTable == null) ? 0 : this.arrayTable.size(); }
  
  public KeyStroke[] allKeys() {
    int i = size();
    InputMap inputMap = getParent();
    if (i == 0)
      return (inputMap != null) ? inputMap.allKeys() : keys(); 
    if (inputMap == null)
      return keys(); 
    KeyStroke[] arrayOfKeyStroke1 = keys();
    KeyStroke[] arrayOfKeyStroke2 = inputMap.allKeys();
    if (arrayOfKeyStroke2 == null)
      return arrayOfKeyStroke1; 
    if (arrayOfKeyStroke1 == null)
      return arrayOfKeyStroke2; 
    HashMap hashMap = new HashMap();
    int j;
    for (j = arrayOfKeyStroke1.length - 1; j >= 0; j--)
      hashMap.put(arrayOfKeyStroke1[j], arrayOfKeyStroke1[j]); 
    for (j = arrayOfKeyStroke2.length - 1; j >= 0; j--)
      hashMap.put(arrayOfKeyStroke2[j], arrayOfKeyStroke2[j]); 
    KeyStroke[] arrayOfKeyStroke3 = new KeyStroke[hashMap.size()];
    return (KeyStroke[])hashMap.keySet().toArray(arrayOfKeyStroke3);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    ArrayTable.writeArrayTable(paramObjectOutputStream, this.arrayTable);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    for (int i = paramObjectInputStream.readInt() - 1; i >= 0; i--)
      put((KeyStroke)paramObjectInputStream.readObject(), paramObjectInputStream.readObject()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\InputMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */