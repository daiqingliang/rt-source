package com.sun.corba.se.spi.orbutil.fsm;

public interface StateEngine {
  StateEngine add(State paramState1, Input paramInput, Guard paramGuard, Action paramAction, State paramState2) throws IllegalStateException;
  
  StateEngine add(State paramState1, Input paramInput, Action paramAction, State paramState2) throws IllegalStateException;
  
  StateEngine setDefault(State paramState1, Action paramAction, State paramState2) throws IllegalStateException;
  
  StateEngine setDefault(State paramState1, State paramState2) throws IllegalStateException;
  
  StateEngine setDefault(State paramState) throws IllegalStateException;
  
  void setDefaultAction(Action paramAction) throws IllegalStateException;
  
  void done() throws IllegalStateException;
  
  FSM makeFSM(State paramState) throws IllegalStateException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\StateEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */