package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.protocol.PIHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

public class POAManagerImpl extends LocalObject implements POAManager {
  private final POAFactory factory;
  
  private PIHandler pihandler;
  
  private State state;
  
  private Set poas = new HashSet(4);
  
  private int nInvocations = 0;
  
  private int nWaiters = 0;
  
  private int myId = 0;
  
  private boolean debug;
  
  private boolean explicitStateChange;
  
  private String stateToString(State paramState) {
    switch (paramState.value()) {
      case 0:
        return "State[HOLDING]";
      case 1:
        return "State[ACTIVE]";
      case 2:
        return "State[DISCARDING]";
      case 3:
        return "State[INACTIVE]";
    } 
    return "State[UNKNOWN]";
  }
  
  public String toString() { return "POAManagerImpl[myId=" + this.myId + " state=" + stateToString(this.state) + " nInvocations=" + this.nInvocations + " nWaiters=" + this.nWaiters + "]"; }
  
  POAFactory getFactory() { return this.factory; }
  
  PIHandler getPIHandler() { return this.pihandler; }
  
  private void countedWait() {
    try {
      if (this.debug)
        ORBUtility.dprint(this, "Calling countedWait on POAManager " + this + " nWaiters=" + this.nWaiters); 
      this.nWaiters++;
      wait();
    } catch (InterruptedException interruptedException) {
    
    } finally {
      this.nWaiters--;
      if (this.debug)
        ORBUtility.dprint(this, "Exiting countedWait on POAManager " + this + " nWaiters=" + this.nWaiters); 
    } 
  }
  
  private void notifyWaiters() {
    if (this.debug)
      ORBUtility.dprint(this, "Calling notifyWaiters on POAManager " + this + " nWaiters=" + this.nWaiters); 
    if (this.nWaiters > 0)
      notifyAll(); 
  }
  
  public int getManagerId() { return this.myId; }
  
  POAManagerImpl(POAFactory paramPOAFactory, PIHandler paramPIHandler) {
    this.factory = paramPOAFactory;
    paramPOAFactory.addPoaManager(this);
    this.pihandler = paramPIHandler;
    this.myId = paramPOAFactory.newPOAManagerId();
    this.state = State.HOLDING;
    this.debug = (paramPOAFactory.getORB()).poaDebugFlag;
    this.explicitStateChange = false;
    if (this.debug)
      ORBUtility.dprint(this, "Creating POAManagerImpl " + this); 
  }
  
  void addPOA(POA paramPOA) {
    if (this.state.value() == 3) {
      POASystemException pOASystemException = this.factory.getWrapper();
      throw pOASystemException.addPoaInactive(CompletionStatus.COMPLETED_NO);
    } 
    this.poas.add(paramPOA);
  }
  
  void removePOA(POA paramPOA) {
    this.poas.remove(paramPOA);
    if (this.poas.isEmpty())
      this.factory.removePoaManager(this); 
  }
  
  public short getORTState() {
    switch (this.state.value()) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 3:
        return 3;
      case 2:
        return 2;
    } 
    return 4;
  }
  
  public void activate() {
    this.explicitStateChange = true;
    if (this.debug)
      ORBUtility.dprint(this, "Calling activate on POAManager " + this); 
    try {
      if (this.state.value() == 3)
        throw new AdapterInactive(); 
      this.state = State.ACTIVE;
      this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
      notifyWaiters();
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting activate on POAManager " + this); 
    } 
  }
  
  public void hold_requests(boolean paramBoolean) throws AdapterInactive {
    this.explicitStateChange = true;
    if (this.debug)
      ORBUtility.dprint(this, "Calling hold_requests on POAManager " + this); 
    try {
      if (this.state.value() == 3)
        throw new AdapterInactive(); 
      this.state = State.HOLDING;
      this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
      notifyWaiters();
      if (paramBoolean)
        while (this.state.value() == 0 && this.nInvocations > 0)
          countedWait();  
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this); 
    } 
  }
  
  public void discard_requests(boolean paramBoolean) throws AdapterInactive {
    this.explicitStateChange = true;
    if (this.debug)
      ORBUtility.dprint(this, "Calling hold_requests on POAManager " + this); 
    try {
      if (this.state.value() == 3)
        throw new AdapterInactive(); 
      this.state = State.DISCARDING;
      this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
      notifyWaiters();
      if (paramBoolean)
        while (this.state.value() == 2 && this.nInvocations > 0)
          countedWait();  
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this); 
    } 
  }
  
  public void deactivate(boolean paramBoolean1, boolean paramBoolean2) throws AdapterInactive {
    this.explicitStateChange = true;
    try {
      synchronized (this) {
        if (this.debug)
          ORBUtility.dprint(this, "Calling deactivate on POAManager " + this); 
        if (this.state.value() == 3)
          throw new AdapterInactive(); 
        this.state = State.INACTIVE;
        this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
        notifyWaiters();
      } 
      POAManagerDeactivator pOAManagerDeactivator = new POAManagerDeactivator(this, paramBoolean1, this.debug);
      if (paramBoolean2) {
        pOAManagerDeactivator.run();
      } else {
        Thread thread = new Thread(pOAManagerDeactivator);
        thread.start();
      } 
    } finally {
      synchronized (this) {
        if (this.debug)
          ORBUtility.dprint(this, "Exiting deactivate on POAManager " + this); 
      } 
    } 
  }
  
  public State get_state() { return this.state; }
  
  void checkIfActive() {
    try {
      if (this.debug)
        ORBUtility.dprint(this, "Calling checkIfActive for POAManagerImpl " + this); 
      checkState();
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting checkIfActive for POAManagerImpl " + this); 
    } 
  }
  
  private void checkState() {
    while (this.state.value() != 1) {
      switch (this.state.value()) {
        case 0:
          while (this.state.value() == 0)
            countedWait(); 
        case 2:
          throw this.factory.getWrapper().poaDiscarding();
        case 3:
          throw this.factory.getWrapper().poaInactive();
      } 
    } 
  }
  
  void enter() {
    try {
      if (this.debug)
        ORBUtility.dprint(this, "Calling enter for POAManagerImpl " + this); 
      checkState();
      this.nInvocations++;
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting enter for POAManagerImpl " + this); 
    } 
  }
  
  void exit() {
    try {
      if (this.debug)
        ORBUtility.dprint(this, "Calling exit for POAManagerImpl " + this); 
      this.nInvocations--;
      if (this.nInvocations == 0)
        notifyWaiters(); 
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting exit for POAManagerImpl " + this); 
    } 
  }
  
  public void implicitActivation() {
    if (!this.explicitStateChange)
      try {
        activate();
      } catch (AdapterInactive adapterInactive) {} 
  }
  
  private class POAManagerDeactivator implements Runnable {
    private boolean etherealize_objects;
    
    private POAManagerImpl pmi;
    
    private boolean debug;
    
    POAManagerDeactivator(POAManagerImpl param1POAManagerImpl1, boolean param1Boolean1, boolean param1Boolean2) {
      this.etherealize_objects = param1Boolean1;
      this.pmi = param1POAManagerImpl1;
      this.debug = param1Boolean2;
    }
    
    public void run() {
      try {
        synchronized (this.pmi) {
          if (this.debug)
            ORBUtility.dprint(this, "Calling run with etherealize_objects=" + this.etherealize_objects + " pmi=" + this.pmi); 
          while (this.pmi.nInvocations > 0)
            POAManagerImpl.this.countedWait(); 
        } 
        if (this.etherealize_objects) {
          Iterator iterator = null;
          synchronized (this.pmi) {
            if (this.debug)
              ORBUtility.dprint(this, "run: Preparing to etherealize with pmi=" + this.pmi); 
            iterator = (new HashSet(this.pmi.poas)).iterator();
          } 
          while (iterator.hasNext())
            ((POAImpl)iterator.next()).etherealizeAll(); 
          synchronized (this.pmi) {
            if (this.debug)
              ORBUtility.dprint(this, "run: removing POAManager and clearing poas with pmi=" + this.pmi); 
            POAManagerImpl.this.factory.removePoaManager(this.pmi);
            POAManagerImpl.this.poas.clear();
          } 
        } 
      } finally {
        if (this.debug)
          synchronized (this.pmi) {
            ORBUtility.dprint(this, "Exiting run");
          }  
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */