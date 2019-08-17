package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class ClassSet implements Serializable {
  private HashMap _map = new HashMap();
  
  public boolean add(JavaClass paramJavaClass) {
    boolean bool = false;
    if (!this._map.containsKey(paramJavaClass.getClassName())) {
      bool = true;
      this._map.put(paramJavaClass.getClassName(), paramJavaClass);
    } 
    return bool;
  }
  
  public void remove(JavaClass paramJavaClass) { this._map.remove(paramJavaClass.getClassName()); }
  
  public boolean empty() { return this._map.isEmpty(); }
  
  public JavaClass[] toArray() {
    Collection collection = this._map.values();
    JavaClass[] arrayOfJavaClass = new JavaClass[collection.size()];
    collection.toArray(arrayOfJavaClass);
    return arrayOfJavaClass;
  }
  
  public String[] getClassNames() { return (String[])this._map.keySet().toArray(new String[this._map.keySet().size()]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */