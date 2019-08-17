package com.sun.corba.se.spi.orbutil.closure;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.sun.corba.se.impl.orbutil.closure.Future;

public abstract class ClosureFactory {
  public static Closure makeConstant(Object paramObject) { return new Constant(paramObject); }
  
  public static Closure makeFuture(Closure paramClosure) { return new Future(paramClosure); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\closure\ClosureFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */