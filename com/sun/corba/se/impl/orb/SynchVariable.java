package com.sun.corba.se.impl.orb;

class SynchVariable {
  public boolean _flag = false;
  
  public void set() { this._flag = true; }
  
  public boolean value() { return this._flag; }
  
  public void reset() { this._flag = false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\SynchVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */