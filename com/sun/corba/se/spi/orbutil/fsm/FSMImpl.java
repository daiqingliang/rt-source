package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;

public class FSMImpl implements FSM {
  private boolean debug;
  
  private State state;
  
  private StateEngineImpl stateEngine;
  
  public FSMImpl(StateEngine paramStateEngine, State paramState) { this(paramStateEngine, paramState, false); }
  
  public FSMImpl(StateEngine paramStateEngine, State paramState, boolean paramBoolean) {
    this.state = paramState;
    this.stateEngine = (StateEngineImpl)paramStateEngine;
    this.debug = paramBoolean;
  }
  
  public State getState() { return this.state; }
  
  public void doIt(Input paramInput) { this.stateEngine.doIt(this, paramInput, this.debug); }
  
  public void internalSetState(State paramState) {
    if (this.debug)
      ORBUtility.dprint(this, "Calling internalSetState with nextState = " + paramState); 
    this.state = paramState;
    if (this.debug)
      ORBUtility.dprint(this, "Exiting internalSetState with state = " + this.state); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\FSMImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */