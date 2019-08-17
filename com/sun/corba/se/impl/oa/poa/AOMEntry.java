package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.InputImpl;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.StateEngineFactory;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;

public class AOMEntry extends FSMImpl {
  private final Thread[] etherealizer;
  
  private final int[] counter;
  
  private final CondVar wait;
  
  final POAImpl poa;
  
  public static final State INVALID = new StateImpl("Invalid");
  
  public static final State INCARN = new StateImpl("Incarnating") {
      public void postAction(FSM param1FSM) {
        AOMEntry aOMEntry;
        aOMEntry.wait.broadcast();
      }
    };
  
  public static final State VALID = new StateImpl("Valid");
  
  public static final State ETHP = new StateImpl("EtherealizePending");
  
  public static final State ETH = new StateImpl("Etherealizing") {
      public void preAction(FSM param1FSM) {
        AOMEntry aOMEntry;
        Thread thread = aOMEntry.etherealizer[0];
        if (thread != null)
          thread.start(); 
      }
      
      public void postAction(FSM param1FSM) {
        AOMEntry aOMEntry;
        aOMEntry.wait.broadcast();
      }
    };
  
  public static final State DESTROYED = new StateImpl("Destroyed");
  
  static final Input START_ETH = new InputImpl("startEtherealize");
  
  static final Input ETH_DONE = new InputImpl("etherealizeDone");
  
  static final Input INC_DONE = new InputImpl("incarnateDone");
  
  static final Input INC_FAIL = new InputImpl("incarnateFailure");
  
  static final Input ACTIVATE = new InputImpl("activateObject");
  
  static final Input ENTER = new InputImpl("enter");
  
  static final Input EXIT = new InputImpl("exit");
  
  private static Action incrementAction = new ActionBase("increment") {
      public void doIt(FSM param1FSM, Input param1Input) {
        AOMEntry aOMEntry;
        aOMEntry.counter[0] = aOMEntry.counter[0] + 1;
      }
    };
  
  private static Action decrementAction = new ActionBase("decrement") {
      public void doIt(FSM param1FSM, Input param1Input) {
        AOMEntry aOMEntry;
        if (aOMEntry.counter[0] > 0) {
          aOMEntry.counter[0] = aOMEntry.counter[0] - 1;
        } else {
          throw aOMEntry.poa.lifecycleWrapper().aomEntryDecZero();
        } 
      }
    };
  
  private static Action throwIllegalStateExceptionAction = new ActionBase("throwIllegalStateException") {
      public void doIt(FSM param1FSM, Input param1Input) { throw new IllegalStateException("No transitions allowed from the DESTROYED state"); }
    };
  
  private static Action oaaAction = new ActionBase("throwObjectAlreadyActive") {
      public void doIt(FSM param1FSM, Input param1Input) { throw new RuntimeException(new ObjectAlreadyActive()); }
    };
  
  private static Guard waitGuard = new GuardBase("wait") {
      public Guard.Result evaluate(FSM param1FSM, Input param1Input) {
        AOMEntry aOMEntry = (AOMEntry)param1FSM;
        try {
          aOMEntry.wait.await();
        } catch (InterruptedException interruptedException) {}
        return Guard.Result.DEFERED;
      }
    };
  
  private static GuardBase greaterZeroGuard = new CounterGuard(0);
  
  private static Guard zeroGuard = new Guard.Complement(greaterZeroGuard);
  
  private static GuardBase greaterOneGuard = new CounterGuard(1);
  
  private static Guard oneGuard = new Guard.Complement(greaterOneGuard);
  
  private static StateEngine engine = StateEngineFactory.create();
  
  public AOMEntry(POAImpl paramPOAImpl) {
    super(engine, INVALID, (paramPOAImpl.getORB()).poaFSMDebugFlag);
    this.poa = paramPOAImpl;
    this.etherealizer = new Thread[1];
    this.etherealizer[0] = null;
    this.counter = new int[1];
    this.counter[0] = 0;
    this.wait = new CondVar(paramPOAImpl.poaMutex, (paramPOAImpl.getORB()).poaConcurrencyDebugFlag);
  }
  
  public void startEtherealize(Thread paramThread) {
    this.etherealizer[0] = paramThread;
    doIt(START_ETH);
  }
  
  public void etherealizeComplete() { doIt(ETH_DONE); }
  
  public void incarnateComplete() { doIt(INC_DONE); }
  
  public void incarnateFailure() { doIt(INC_FAIL); }
  
  public void activateObject() {
    try {
      doIt(ACTIVATE);
    } catch (RuntimeException runtimeException) {
      Throwable throwable = runtimeException.getCause();
      if (throwable instanceof ObjectAlreadyActive)
        throw (ObjectAlreadyActive)throwable; 
      throw runtimeException;
    } 
  }
  
  public void enter() { doIt(ENTER); }
  
  public void exit() { doIt(EXIT); }
  
  static  {
    engine.add(INVALID, ENTER, incrementAction, INCARN);
    engine.add(INVALID, ACTIVATE, null, VALID);
    engine.setDefault(INVALID);
    engine.add(INCARN, ENTER, waitGuard, null, INCARN);
    engine.add(INCARN, EXIT, null, INCARN);
    engine.add(INCARN, START_ETH, waitGuard, null, INCARN);
    engine.add(INCARN, INC_DONE, null, VALID);
    engine.add(INCARN, INC_FAIL, decrementAction, INVALID);
    engine.add(INCARN, ACTIVATE, oaaAction, INCARN);
    engine.add(VALID, ENTER, incrementAction, VALID);
    engine.add(VALID, EXIT, decrementAction, VALID);
    engine.add(VALID, START_ETH, greaterZeroGuard, null, ETHP);
    engine.add(VALID, START_ETH, zeroGuard, null, ETH);
    engine.add(VALID, ACTIVATE, oaaAction, VALID);
    engine.add(ETHP, ENTER, waitGuard, null, ETHP);
    engine.add(ETHP, START_ETH, null, ETHP);
    engine.add(ETHP, EXIT, greaterOneGuard, decrementAction, ETHP);
    engine.add(ETHP, EXIT, oneGuard, decrementAction, ETH);
    engine.add(ETHP, ACTIVATE, oaaAction, ETHP);
    engine.add(ETH, START_ETH, null, ETH);
    engine.add(ETH, ETH_DONE, null, DESTROYED);
    engine.add(ETH, ACTIVATE, oaaAction, ETH);
    engine.add(ETH, ENTER, waitGuard, null, ETH);
    engine.setDefault(DESTROYED, throwIllegalStateExceptionAction, DESTROYED);
    engine.done();
  }
  
  private static class CounterGuard extends GuardBase {
    private int value;
    
    public CounterGuard(int param1Int) {
      super("counter>" + param1Int);
      this.value = param1Int;
    }
    
    public Guard.Result evaluate(FSM param1FSM, Input param1Input) {
      AOMEntry aOMEntry;
      return Guard.Result.convert((aOMEntry.counter[0] > this.value));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\AOMEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */