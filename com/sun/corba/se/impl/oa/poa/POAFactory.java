package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CORBA.TRANSIENT;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.portable.Delegate;

public class POAFactory implements ObjectAdapterFactory {
  private Map exportedServantsToPOA = new WeakHashMap();
  
  private Set poaManagers = Collections.synchronizedSet(new HashSet(4));
  
  private int poaManagerId = 0;
  
  private int poaId = 0;
  
  private POAImpl rootPOA = null;
  
  private DelegateImpl delegateImpl = null;
  
  private ORB orb = null;
  
  private POASystemException wrapper;
  
  private OMGSystemException omgWrapper;
  
  private boolean isShuttingDown = false;
  
  public POASystemException getWrapper() { return this.wrapper; }
  
  public POA lookupPOA(Servant paramServant) { return (POA)this.exportedServantsToPOA.get(paramServant); }
  
  public void registerPOAForServant(POA paramPOA, Servant paramServant) { this.exportedServantsToPOA.put(paramServant, paramPOA); }
  
  public void unregisterPOAForServant(POA paramPOA, Servant paramServant) { this.exportedServantsToPOA.remove(paramServant); }
  
  public void init(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = POASystemException.get(paramORB, "oa.lifecycle");
    this.omgWrapper = OMGSystemException.get(paramORB, "oa.lifecycle");
    this.delegateImpl = new DelegateImpl(paramORB, this);
    registerRootPOA();
    POACurrent pOACurrent = new POACurrent(paramORB);
    paramORB.getLocalResolver().register("POACurrent", ClosureFactory.makeConstant(pOACurrent));
  }
  
  public ObjectAdapter find(ObjectAdapterId paramObjectAdapterId) {
    POA pOA = null;
    try {
      boolean bool = true;
      Iterator iterator = paramObjectAdapterId.iterator();
      for (pOA = getRootPOA(); iterator.hasNext(); pOA = pOA.find_POA(str, true)) {
        String str = (String)iterator.next();
        if (bool) {
          if (!str.equals("RootPOA"))
            throw this.wrapper.makeFactoryNotPoa(str); 
          bool = false;
          continue;
        } 
      } 
    } catch (AdapterNonExistent adapterNonExistent) {
      throw this.omgWrapper.noObjectAdaptor(adapterNonExistent);
    } catch (OBJECT_NOT_EXIST oBJECT_NOT_EXIST) {
      throw oBJECT_NOT_EXIST;
    } catch (TRANSIENT tRANSIENT) {
      throw tRANSIENT;
    } catch (Exception exception) {
      throw this.wrapper.poaLookupError(exception);
    } 
    if (pOA == null)
      throw this.wrapper.poaLookupError(); 
    return (ObjectAdapter)pOA;
  }
  
  public void shutdown(boolean paramBoolean) {
    Iterator iterator = null;
    synchronized (this) {
      this.isShuttingDown = true;
      iterator = (new HashSet(this.poaManagers)).iterator();
    } 
    while (iterator.hasNext()) {
      try {
        ((POAManager)iterator.next()).deactivate(true, paramBoolean);
      } catch (AdapterInactive adapterInactive) {}
    } 
  }
  
  public void removePoaManager(POAManager paramPOAManager) { this.poaManagers.remove(paramPOAManager); }
  
  public void addPoaManager(POAManager paramPOAManager) { this.poaManagers.add(paramPOAManager); }
  
  public int newPOAManagerId() { return this.poaManagerId++; }
  
  public void registerRootPOA() {
    Closure closure = new Closure() {
        public Object evaluate() { return POAImpl.makeRootPOA(POAFactory.this.orb); }
      };
    this.orb.getLocalResolver().register("RootPOA", ClosureFactory.makeFuture(closure));
  }
  
  public POA getRootPOA() {
    if (this.rootPOA == null) {
      if (this.isShuttingDown)
        throw this.omgWrapper.noObjectAdaptor(); 
      try {
        Object object = this.orb.resolve_initial_references("RootPOA");
        this.rootPOA = (POAImpl)object;
      } catch (InvalidName invalidName) {
        throw this.wrapper.cantResolveRootPoa(invalidName);
      } 
    } 
    return this.rootPOA;
  }
  
  public Delegate getDelegateImpl() { return this.delegateImpl; }
  
  public int newPOAId() { return this.poaId++; }
  
  public ORB getORB() { return this.orb; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */