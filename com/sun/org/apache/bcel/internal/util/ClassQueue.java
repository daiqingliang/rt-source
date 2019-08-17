package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.LinkedList;

public class ClassQueue implements Serializable {
  protected LinkedList vec = new LinkedList();
  
  public void enqueue(JavaClass paramJavaClass) { this.vec.addLast(paramJavaClass); }
  
  public JavaClass dequeue() { return (JavaClass)this.vec.removeFirst(); }
  
  public boolean empty() { return this.vec.isEmpty(); }
  
  public String toString() { return this.vec.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */