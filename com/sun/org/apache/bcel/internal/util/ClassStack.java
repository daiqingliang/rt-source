package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Stack;

public class ClassStack implements Serializable {
  private Stack stack = new Stack();
  
  public void push(JavaClass paramJavaClass) { this.stack.push(paramJavaClass); }
  
  public JavaClass pop() { return (JavaClass)this.stack.pop(); }
  
  public JavaClass top() { return (JavaClass)this.stack.peek(); }
  
  public boolean empty() { return this.stack.empty(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */