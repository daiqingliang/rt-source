package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Constant implements Closure {
  private Object value;
  
  public Constant(Object paramObject) { this.value = paramObject; }
  
  public Object evaluate() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\closure\Constant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */