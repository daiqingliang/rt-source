package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.ArrayList;

public class ClassVector implements Serializable {
  protected ArrayList vec = new ArrayList();
  
  public void addElement(JavaClass paramJavaClass) { this.vec.add(paramJavaClass); }
  
  public JavaClass elementAt(int paramInt) { return (JavaClass)this.vec.get(paramInt); }
  
  public void removeElementAt(int paramInt) { this.vec.remove(paramInt); }
  
  public JavaClass[] toArray() {
    JavaClass[] arrayOfJavaClass = new JavaClass[this.vec.size()];
    this.vec.toArray(arrayOfJavaClass);
    return arrayOfJavaClass;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */