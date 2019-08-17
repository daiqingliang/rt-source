package com.sun.corba.se.spi.orbutil.fsm;

class NegateGuard implements Guard {
  Guard guard;
  
  public NegateGuard(Guard paramGuard) { this.guard = paramGuard; }
  
  public Guard.Result evaluate(FSM paramFSM, Input paramInput) { return this.guard.evaluate(paramFSM, paramInput).complement(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\NegateGuard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */