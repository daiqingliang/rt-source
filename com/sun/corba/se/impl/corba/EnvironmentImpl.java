package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Environment;

public class EnvironmentImpl extends Environment {
  private Exception _exc;
  
  public Exception exception() { return this._exc; }
  
  public void exception(Exception paramException) { this._exc = paramException; }
  
  public void clear() { this._exc = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\EnvironmentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */