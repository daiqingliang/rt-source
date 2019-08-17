package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;

public class GuardedAction {
  private static Guard trueGuard = new GuardBase("true") {
      public Guard.Result evaluate(FSM param1FSM, Input param1Input) { return Guard.Result.ENABLED; }
    };
  
  private Guard guard = trueGuard;
  
  private Action action;
  
  private State nextState;
  
  public GuardedAction(Action paramAction, State paramState) {
    this.action = paramAction;
    this.nextState = paramState;
  }
  
  public GuardedAction(Guard paramGuard, Action paramAction, State paramState) {
    this.action = paramAction;
    this.nextState = paramState;
  }
  
  public String toString() { return "GuardedAction[action=" + this.action + " guard=" + this.guard + " nextState=" + this.nextState + "]"; }
  
  public Action getAction() { return this.action; }
  
  public Guard getGuard() { return this.guard; }
  
  public State getNextState() { return this.nextState; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\fsm\GuardedAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */