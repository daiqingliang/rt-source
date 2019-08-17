package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import java.util.Set;
import org.omg.CORBA.INTERNAL;

public class StateEngineImpl implements StateEngine {
  private static Action emptyAction = new ActionBase("Empty") {
      public void doIt(FSM param1FSM, Input param1Input) {}
    };
  
  private boolean initializing = true;
  
  private Action defaultAction = new ActionBase("Invalid Transition") {
      public void doIt(FSM param1FSM, Input param1Input) { throw new INTERNAL("Invalid transition attempted from " + param1FSM.getState() + " under " + param1Input); }
    };
  
  public StateEngine add(State paramState1, Input paramInput, Guard paramGuard, Action paramAction, State paramState2) throws IllegalArgumentException, IllegalStateException {
    mustBeInitializing();
    StateImpl stateImpl = (StateImpl)paramState1;
    GuardedAction guardedAction = new GuardedAction(paramGuard, paramAction, paramState2);
    stateImpl.addGuardedAction(paramInput, guardedAction);
    return this;
  }
  
  public StateEngine add(State paramState1, Input paramInput, Action paramAction, State paramState2) throws IllegalArgumentException, IllegalStateException {
    mustBeInitializing();
    StateImpl stateImpl = (StateImpl)paramState1;
    GuardedAction guardedAction = new GuardedAction(paramAction, paramState2);
    stateImpl.addGuardedAction(paramInput, guardedAction);
    return this;
  }
  
  public StateEngine setDefault(State paramState1, Action paramAction, State paramState2) throws IllegalArgumentException, IllegalStateException {
    mustBeInitializing();
    StateImpl stateImpl = (StateImpl)paramState1;
    stateImpl.setDefaultAction(paramAction);
    stateImpl.setDefaultNextState(paramState2);
    return this;
  }
  
  public StateEngine setDefault(State paramState1, State paramState2) throws IllegalArgumentException, IllegalStateException { return setDefault(paramState1, emptyAction, paramState2); }
  
  public StateEngine setDefault(State paramState) throws IllegalArgumentException, IllegalStateException { return setDefault(paramState, paramState); }
  
  public void done() {
    mustBeInitializing();
    this.initializing = false;
  }
  
  public void setDefaultAction(Action paramAction) throws IllegalStateException {
    mustBeInitializing();
    this.defaultAction = paramAction;
  }
  
  public void doIt(FSM paramFSM, Input paramInput, boolean paramBoolean) {
    if (paramBoolean)
      ORBUtility.dprint(this, "doIt enter: currentState = " + paramFSM.getState() + " in = " + paramInput); 
    try {
      innerDoIt(paramFSM, paramInput, paramBoolean);
    } finally {
      if (paramBoolean)
        ORBUtility.dprint(this, "doIt exit"); 
    } 
  }
  
  private StateImpl getDefaultNextState(StateImpl paramStateImpl) {
    StateImpl stateImpl = (StateImpl)paramStateImpl.getDefaultNextState();
    if (stateImpl == null)
      stateImpl = paramStateImpl; 
    return stateImpl;
  }
  
  private Action getDefaultAction(StateImpl paramStateImpl) {
    Action action = paramStateImpl.getDefaultAction();
    if (action == null)
      action = this.defaultAction; 
    return action;
  }
  
  private void innerDoIt(FSM paramFSM, Input paramInput, boolean paramBoolean) {
    if (paramBoolean)
      ORBUtility.dprint(this, "Calling innerDoIt with input " + paramInput); 
    StateImpl stateImpl1 = null;
    StateImpl stateImpl2 = null;
    Action action = null;
    boolean bool = false;
    do {
      bool = false;
      stateImpl1 = (StateImpl)paramFSM.getState();
      stateImpl2 = getDefaultNextState(stateImpl1);
      action = getDefaultAction(stateImpl1);
      if (paramBoolean) {
        ORBUtility.dprint(this, "currentState      = " + stateImpl1);
        ORBUtility.dprint(this, "in                = " + paramInput);
        ORBUtility.dprint(this, "default nextState = " + stateImpl2);
        ORBUtility.dprint(this, "default action    = " + action);
      } 
      Set set = stateImpl1.getGuardedActions(paramInput);
      if (set == null)
        continue; 
      for (GuardedAction guardedAction : set) {
        Guard.Result result = guardedAction.getGuard().evaluate(paramFSM, paramInput);
        if (paramBoolean)
          ORBUtility.dprint(this, "doIt: evaluated " + guardedAction + " with result " + result); 
        if (result == Guard.Result.ENABLED) {
          stateImpl2 = (StateImpl)guardedAction.getNextState();
          action = guardedAction.getAction();
          if (paramBoolean) {
            ORBUtility.dprint(this, "nextState = " + stateImpl2);
            ORBUtility.dprint(this, "action    = " + action);
          } 
          break;
        } 
        if (result == Guard.Result.DEFERED) {
          bool = true;
          break;
        } 
      } 
    } while (bool);
    performStateTransition(paramFSM, paramInput, stateImpl2, action, paramBoolean);
  }
  
  private void performStateTransition(FSM paramFSM, Input paramInput, StateImpl paramStateImpl, Action paramAction, boolean paramBoolean) {
    StateImpl stateImpl = (StateImpl)paramFSM.getState();
    bool = !stateImpl.equals(paramStateImpl) ? 1 : 0;
    if (bool) {
      if (paramBoolean)
        ORBUtility.dprint(this, "doIt: executing postAction for state " + stateImpl); 
      try {
        stateImpl.postAction(paramFSM);
      } catch (Throwable throwable) {
        if (paramBoolean)
          ORBUtility.dprint(this, "doIt: postAction threw " + throwable); 
        if (throwable instanceof ThreadDeath)
          throw (ThreadDeath)throwable; 
      } 
    } 
    try {
      if (paramAction != null)
        paramAction.doIt(paramFSM, paramInput); 
    } finally {
      if (bool) {
        if (paramBoolean)
          ORBUtility.dprint(this, "doIt: executing preAction for state " + paramStateImpl); 
        try {
          paramStateImpl.preAction(paramFSM);
        } catch (Throwable throwable) {
          if (paramBoolean)
            ORBUtility.dprint(this, "doIt: preAction threw " + throwable); 
          if (throwable instanceof ThreadDeath)
            throw (ThreadDeath)throwable; 
        } 
        ((FSMImpl)paramFSM).internalSetState(paramStateImpl);
      } 
      if (paramBoolean)
        ORBUtility.dprint(this, "doIt: state is now " + paramStateImpl); 
    } 
  }
  
  public FSM makeFSM(State paramState) throws IllegalStateException {
    mustNotBeInitializing();
    return new FSMImpl(this, paramState);
  }
  
  private void mustBeInitializing() {
    if (!this.initializing)
      throw new IllegalStateException("Invalid method call after initialization completed"); 
  }
  
  private void mustNotBeInitializing() {
    if (this.initializing)
      throw new IllegalStateException("Invalid method call before initialization completed"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\fsm\StateEngineImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */