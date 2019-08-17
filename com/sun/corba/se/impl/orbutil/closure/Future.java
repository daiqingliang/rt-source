package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Future implements Closure {
  private boolean evaluated = false;
  
  private Closure closure;
  
  private Object value;
  
  public Future(Closure paramClosure) {
    this.closure = paramClosure;
    this.value = null;
  }
  
  public Object evaluate() {
    if (!this.evaluated) {
      this.evaluated = true;
      this.value = this.closure.evaluate();
    } 
    return this.value;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\closure\Future.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */