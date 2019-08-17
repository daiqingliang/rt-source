package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

class ArrayTable implements Cloneable {
  private Object table = null;
  
  private static final int ARRAY_BOUNDARY = 8;
  
  static void writeArrayTable(ObjectOutputStream paramObjectOutputStream, ArrayTable paramArrayTable) throws IOException {
    Object[] arrayOfObject;
    if (paramArrayTable == null || (arrayOfObject = paramArrayTable.getKeys(null)) == null) {
      paramObjectOutputStream.writeInt(0);
    } else {
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfObject.length; b2++) {
        Object object = arrayOfObject[b2];
        if ((object instanceof java.io.Serializable && paramArrayTable.get(object) instanceof java.io.Serializable) || (object instanceof ClientPropertyKey && ((ClientPropertyKey)object).getReportValueNotSerializable())) {
          b1++;
        } else {
          arrayOfObject[b2] = null;
        } 
      } 
      paramObjectOutputStream.writeInt(b1);
      if (b1 > 0)
        for (Object object : arrayOfObject) {
          if (object != null) {
            paramObjectOutputStream.writeObject(object);
            paramObjectOutputStream.writeObject(paramArrayTable.get(object));
            if (--b1 == 0)
              break; 
          } 
        }  
    } 
  }
  
  public void put(Object paramObject1, Object paramObject2) {
    if (this.table == null) {
      this.table = new Object[] { paramObject1, paramObject2 };
    } else {
      int i = size();
      if (i < 8) {
        if (containsKey(paramObject1)) {
          Object[] arrayOfObject = (Object[])this.table;
          for (boolean bool = false; bool < arrayOfObject.length - 1; bool += true) {
            if (arrayOfObject[bool].equals(paramObject1)) {
              arrayOfObject[bool + true] = paramObject2;
              break;
            } 
          } 
        } else {
          Object[] arrayOfObject1 = (Object[])this.table;
          int j = arrayOfObject1.length;
          Object[] arrayOfObject2 = new Object[j + 2];
          System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, j);
          arrayOfObject2[j] = paramObject1;
          arrayOfObject2[j + 1] = paramObject2;
          this.table = arrayOfObject2;
        } 
      } else {
        if (i == 8 && isArray())
          grow(); 
        ((Hashtable)this.table).put(paramObject1, paramObject2);
      } 
    } 
  }
  
  public Object get(Object paramObject) {
    Object object = null;
    if (this.table != null)
      if (isArray()) {
        Object[] arrayOfObject = (Object[])this.table;
        for (boolean bool = false; bool < arrayOfObject.length - 1; bool += true) {
          if (arrayOfObject[bool].equals(paramObject)) {
            object = arrayOfObject[bool + true];
            break;
          } 
        } 
      } else {
        object = ((Hashtable)this.table).get(paramObject);
      }  
    return object;
  }
  
  public int size() {
    int i;
    if (this.table == null)
      return 0; 
    if (isArray()) {
      i = (Object[])this.table.length / 2;
    } else {
      i = ((Hashtable)this.table).size();
    } 
    return i;
  }
  
  public boolean containsKey(Object paramObject) {
    boolean bool = false;
    if (this.table != null)
      if (isArray()) {
        Object[] arrayOfObject = (Object[])this.table;
        for (boolean bool1 = false; bool1 < arrayOfObject.length - 1; bool1 += true) {
          if (arrayOfObject[bool1].equals(paramObject)) {
            bool = true;
            break;
          } 
        } 
      } else {
        bool = ((Hashtable)this.table).containsKey(paramObject);
      }  
    return bool;
  }
  
  public Object remove(Object paramObject) {
    Object object = null;
    if (paramObject == null)
      return null; 
    if (this.table != null) {
      if (isArray()) {
        int i = -1;
        Object[] arrayOfObject = (Object[])this.table;
        for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
          if (arrayOfObject[j].equals(paramObject)) {
            i = j;
            object = arrayOfObject[j + 1];
            break;
          } 
        } 
        if (i != -1) {
          Object[] arrayOfObject1 = new Object[arrayOfObject.length - 2];
          System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, i);
          if (i < arrayOfObject1.length)
            System.arraycopy(arrayOfObject, i + 2, arrayOfObject1, i, arrayOfObject1.length - i); 
          this.table = (arrayOfObject1.length == 0) ? null : arrayOfObject1;
        } 
      } else {
        object = ((Hashtable)this.table).remove(paramObject);
      } 
      if (size() == 7 && !isArray())
        shrink(); 
    } 
    return object;
  }
  
  public void clear() { this.table = null; }
  
  public Object clone() {
    ArrayTable arrayTable = new ArrayTable();
    if (isArray()) {
      Object[] arrayOfObject = (Object[])this.table;
      for (boolean bool = false; bool < arrayOfObject.length - 1; bool += true)
        arrayTable.put(arrayOfObject[bool], arrayOfObject[bool + true]); 
    } else {
      Hashtable hashtable = (Hashtable)this.table;
      Enumeration enumeration = hashtable.keys();
      while (enumeration.hasMoreElements()) {
        Object object = enumeration.nextElement();
        arrayTable.put(object, hashtable.get(object));
      } 
    } 
    return arrayTable;
  }
  
  public Object[] getKeys(Object[] paramArrayOfObject) {
    if (this.table == null)
      return null; 
    if (isArray()) {
      Object[] arrayOfObject = (Object[])this.table;
      if (paramArrayOfObject == null)
        paramArrayOfObject = new Object[arrayOfObject.length / 2]; 
      boolean bool = false;
      for (byte b = 0; bool < arrayOfObject.length - 1; b++) {
        paramArrayOfObject[b] = arrayOfObject[bool];
        bool += true;
      } 
    } else {
      Hashtable hashtable = (Hashtable)this.table;
      Enumeration enumeration = hashtable.keys();
      int i = hashtable.size();
      if (paramArrayOfObject == null)
        paramArrayOfObject = new Object[i]; 
      while (i > 0)
        paramArrayOfObject[--i] = enumeration.nextElement(); 
    } 
    return paramArrayOfObject;
  }
  
  private boolean isArray() { return this.table instanceof Object[]; }
  
  private void grow() {
    Object[] arrayOfObject = (Object[])this.table;
    Hashtable hashtable = new Hashtable(arrayOfObject.length / 2);
    for (boolean bool = false; bool < arrayOfObject.length; bool += true)
      hashtable.put(arrayOfObject[bool], arrayOfObject[bool + true]); 
    this.table = hashtable;
  }
  
  private void shrink() {
    Hashtable hashtable = (Hashtable)this.table;
    Object[] arrayOfObject = new Object[hashtable.size() * 2];
    Enumeration enumeration = hashtable.keys();
    for (boolean bool = false; enumeration.hasMoreElements(); bool += true) {
      Object object = enumeration.nextElement();
      arrayOfObject[bool] = object;
      arrayOfObject[bool + true] = hashtable.get(object);
    } 
    this.table = arrayOfObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ArrayTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */