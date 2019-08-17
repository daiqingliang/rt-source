package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.fsm.GuardedAction;
import com.sun.corba.se.impl.orbutil.fsm.NameBase;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateImpl extends NameBase implements State {
  private Action defaultAction = null;
  
  private State defaultNextState;
  
  private Map inputToGuardedActions = new HashMap();
  
  public StateImpl(String paramString) { super(paramString); }
  
  public void preAction(FSM paramFSM) {}
  
  public void postAction(FSM paramFSM) {}
  
  public State getDefaultNextState() { return this.defaultNextState; }
  
  public void setDefaultNextState(State paramState) { this.defaultNextState = paramState; }
  
  public Action getDefaultAction() { return this.defaultAction; }
  
  public void setDefaultAction(Action paramAction) { this.defaultAction = paramAction; }
  
  public void addGuardedAction(Input paramInput, GuardedAction paramGuardedAction) {
    Set set = (Set)this.inputToGuardedActions.get(paramInput);
    if (set == null) {
      set = new HashSet();
      this.inputToGuardedActions.put(paramInput, set);
    } 
    set.add(paramGuardedAction);
  }
  
  public Set getGuardedActions(Input paramInput) { return (Set)this.inputToGuardedActions.get(paramInput); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\StateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */