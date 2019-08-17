package com.sun.corba.se.spi.orbutil.fsm;

public interface FSM {
  State getState();
  
  void doIt(Input paramInput);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\FSM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */