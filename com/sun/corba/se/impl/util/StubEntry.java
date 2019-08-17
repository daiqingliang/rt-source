package com.sun.corba.se.impl.util;

import org.omg.CORBA.Object;

class StubEntry {
  Object stub;
  
  boolean mostDerived;
  
  StubEntry(Object paramObject, boolean paramBoolean) {
    this.stub = paramObject;
    this.mostDerived = paramBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\StubEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */