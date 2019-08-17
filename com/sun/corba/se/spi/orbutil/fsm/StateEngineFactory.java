package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;

public class StateEngineFactory {
  public static StateEngine create() { return new StateEngineImpl(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\StateEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */