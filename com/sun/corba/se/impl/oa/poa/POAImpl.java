package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.ior.ObjectAdapterIdArray;
import com.sun.corba.se.impl.ior.POAObjectKeyTemplate;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.impl.orbutil.concurrent.ReentrantMutex;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import com.sun.corba.se.impl.orbutil.concurrent.SyncUtil;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

public class POAImpl extends ObjectAdapterBase implements POA {
  private boolean debug;
  
  private static final int STATE_START = 0;
  
  private static final int STATE_INIT = 1;
  
  private static final int STATE_INIT_DONE = 2;
  
  private static final int STATE_RUN = 3;
  
  private static final int STATE_DESTROYING = 4;
  
  private static final int STATE_DESTROYED = 5;
  
  private int state;
  
  private POAPolicyMediator mediator;
  
  private int numLevels;
  
  private ObjectAdapterId poaId;
  
  private String name;
  
  private POAManagerImpl manager;
  
  private int uniquePOAId;
  
  private POAImpl parent;
  
  private Map children;
  
  private AdapterActivator activator;
  
  private int invocationCount;
  
  Sync poaMutex;
  
  private CondVar adapterActivatorCV;
  
  private CondVar invokeCV;
  
  private CondVar beingDestroyedCV;
  
  protected ThreadLocal isDestroying;
  
  private String stateToString() {
    switch (this.state) {
      case 0:
        return "START";
      case 1:
        return "INIT";
      case 2:
        return "INIT_DONE";
      case 3:
        return "RUN";
      case 4:
        return "DESTROYING";
      case 5:
        return "DESTROYED";
    } 
    return "UNKNOWN(" + this.state + ")";
  }
  
  public String toString() { return "POA[" + this.poaId.toString() + ", uniquePOAId=" + this.uniquePOAId + ", state=" + stateToString() + ", invocationCount=" + this.invocationCount + "]"; }
  
  boolean getDebug() { return this.debug; }
  
  static POAFactory getPOAFactory(ORB paramORB) { return (POAFactory)paramORB.getRequestDispatcherRegistry().getObjectAdapterFactory(32); }
  
  static POAImpl makeRootPOA(ORB paramORB) {
    POAManagerImpl pOAManagerImpl = new POAManagerImpl(getPOAFactory(paramORB), paramORB.getPIHandler());
    POAImpl pOAImpl = new POAImpl("RootPOA", null, paramORB, 0);
    pOAImpl.initialize(pOAManagerImpl, Policies.rootPOAPolicies);
    return pOAImpl;
  }
  
  int getPOAId() { return this.uniquePOAId; }
  
  void lock() {
    SyncUtil.acquire(this.poaMutex);
    if (this.debug)
      ORBUtility.dprint(this, "LOCKED poa " + this); 
  }
  
  void unlock() {
    if (this.debug)
      ORBUtility.dprint(this, "UNLOCKED poa " + this); 
    this.poaMutex.release();
  }
  
  Policies getPolicies() { return this.mediator.getPolicies(); }
  
  private POAImpl(String paramString, POAImpl paramPOAImpl, ORB paramORB, int paramInt) {
    super(paramORB);
    this.debug = paramORB.poaDebugFlag;
    if (this.debug)
      ORBUtility.dprint(this, "Creating POA with name=" + paramString + " parent=" + paramPOAImpl); 
    this.state = paramInt;
    this.name = paramString;
    this.parent = paramPOAImpl;
    this.children = new HashMap();
    this.activator = null;
    this.uniquePOAId = getPOAFactory(paramORB).newPOAId();
    if (paramPOAImpl == null) {
      this.numLevels = 1;
    } else {
      paramPOAImpl.numLevels++;
      paramPOAImpl.children.put(paramString, this);
    } 
    String[] arrayOfString = new String[this.numLevels];
    POAImpl pOAImpl = this;
    int i = this.numLevels - 1;
    while (pOAImpl != null) {
      arrayOfString[i--] = pOAImpl.name;
      pOAImpl = pOAImpl.parent;
    } 
    this.poaId = new ObjectAdapterIdArray(arrayOfString);
    this.invocationCount = 0;
    this.poaMutex = new ReentrantMutex(paramORB.poaConcurrencyDebugFlag);
    this.adapterActivatorCV = new CondVar(this.poaMutex, paramORB.poaConcurrencyDebugFlag);
    this.invokeCV = new CondVar(this.poaMutex, paramORB.poaConcurrencyDebugFlag);
    this.beingDestroyedCV = new CondVar(this.poaMutex, paramORB.poaConcurrencyDebugFlag);
    this.isDestroying = new ThreadLocal() {
        protected Object initialValue() { return Boolean.FALSE; }
      };
  }
  
  private void initialize(POAManagerImpl paramPOAManagerImpl, Policies paramPolicies) {
    if (this.debug)
      ORBUtility.dprint(this, "Initializing poa " + this + " with POAManager=" + paramPOAManagerImpl + " policies=" + paramPolicies); 
    this.manager = paramPOAManagerImpl;
    paramPOAManagerImpl.addPOA(this);
    this.mediator = POAPolicyMediatorFactory.create(paramPolicies, this);
    int i = this.mediator.getServerId();
    int j = this.mediator.getScid();
    String str = getORB().getORBData().getORBId();
    POAObjectKeyTemplate pOAObjectKeyTemplate = new POAObjectKeyTemplate(getORB(), j, i, str, this.poaId);
    if (this.debug)
      ORBUtility.dprint(this, "Initializing poa: oktemp=" + pOAObjectKeyTemplate); 
    boolean bool = true;
    initializeTemplate(pOAObjectKeyTemplate, bool, paramPolicies, null, null, pOAObjectKeyTemplate.getObjectAdapterId());
    if (this.state == 0) {
      this.state = 3;
    } else if (this.state == 1) {
      this.state = 2;
    } else {
      throw lifecycleWrapper().illegalPoaStateTrans();
    } 
  }
  
  private boolean waitUntilRunning() {
    if (this.debug)
      ORBUtility.dprint(this, "Calling waitUntilRunning on poa " + this); 
    while (this.state < 3) {
      try {
        this.adapterActivatorCV.await();
      } catch (InterruptedException interruptedException) {}
    } 
    if (this.debug)
      ORBUtility.dprint(this, "Exiting waitUntilRunning on poa " + this); 
    return (this.state == 3);
  }
  
  private boolean destroyIfNotInitDone() {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling destroyIfNotInitDone on poa " + this); 
      boolean bool = (this.state == 2) ? 1 : 0;
      if (bool) {
        this.state = 3;
      } else {
        DestroyThread destroyThread = new DestroyThread(false, this.debug);
        destroyThread.doIt(this, true);
      } 
      return bool;
    } finally {
      this.adapterActivatorCV.broadcast();
      if (this.debug)
        ORBUtility.dprint(this, "Exiting destroyIfNotInitDone on poa " + this); 
      unlock();
    } 
  }
  
  private byte[] internalReferenceToId(Object paramObject) throws WrongAdapter {
    IOR iOR = ORBUtility.getIOR(paramObject);
    IORTemplateList iORTemplateList1 = iOR.getIORTemplates();
    ObjectReferenceFactory objectReferenceFactory = getCurrentFactory();
    IORTemplateList iORTemplateList2 = IORFactories.getIORTemplateList(objectReferenceFactory);
    if (!iORTemplateList2.isEquivalent(iORTemplateList1))
      throw new WrongAdapter(); 
    Iterator iterator = iOR.iterator();
    if (!iterator.hasNext())
      throw iorWrapper().noProfilesInIor(); 
    TaggedProfile taggedProfile = (TaggedProfile)iterator.next();
    ObjectId objectId = taggedProfile.getObjectId();
    return objectId.getId();
  }
  
  void etherealizeAll() {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling etheralizeAll on poa " + this); 
      this.mediator.etherealizeAll();
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting etheralizeAll on poa " + this); 
      unlock();
    } 
  }
  
  public POA create_POA(String paramString, POAManager paramPOAManager, Policy[] paramArrayOfPolicy) throws AdapterAlreadyExists, InvalidPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling create_POA(name=" + paramString + " theManager=" + paramPOAManager + " policies=" + paramArrayOfPolicy + ") on poa " + this); 
      if (this.state > 3)
        throw omgLifecycleWrapper().createPoaDestroy(); 
      pOAImpl = (POAImpl)this.children.get(paramString);
      if (pOAImpl == null)
        pOAImpl = new POAImpl(paramString, this, getORB(), 0); 
    } finally {
      unlock();
    } 
  }
  
  public POA find_POA(String paramString, boolean paramBoolean) throws AdapterNonExistent {
    pOAImpl = null;
    AdapterActivator adapterActivator = null;
    lock();
    if (this.debug)
      ORBUtility.dprint(this, "Calling find_POA(name=" + paramString + " activate=" + paramBoolean + ") on poa " + this); 
    pOAImpl = (POAImpl)this.children.get(paramString);
    if (pOAImpl != null) {
      if (this.debug)
        ORBUtility.dprint(this, "Calling find_POA: found poa " + pOAImpl); 
      try {
        pOAImpl.lock();
        unlock();
        if (!pOAImpl.waitUntilRunning())
          throw omgLifecycleWrapper().poaDestroyed(); 
      } finally {
        pOAImpl.unlock();
      } 
    } else {
      try {
        if (this.debug)
          ORBUtility.dprint(this, "Calling find_POA: no poa found"); 
        if (paramBoolean && this.activator != null) {
          pOAImpl = new POAImpl(paramString, this, getORB(), 1);
          if (this.debug)
            ORBUtility.dprint(this, "Calling find_POA: created poa " + pOAImpl); 
          adapterActivator = this.activator;
        } else {
          throw new AdapterNonExistent();
        } 
      } finally {
        unlock();
      } 
    } 
    if (adapterActivator != null) {
      boolean bool1 = false;
      bool2 = false;
      if (this.debug)
        ORBUtility.dprint(this, "Calling find_POA: calling AdapterActivator"); 
      try {
        synchronized (adapterActivator) {
          bool1 = adapterActivator.unknown_adapter(this, paramString);
        } 
      } catch (SystemException systemException) {
        throw omgLifecycleWrapper().adapterActivatorException(systemException, paramString, this.poaId.toString());
      } catch (Throwable throwable) {
        lifecycleWrapper().unexpectedException(throwable, toString());
        if (throwable instanceof ThreadDeath)
          throw (ThreadDeath)throwable; 
      } finally {
        bool2 = pOAImpl.destroyIfNotInitDone();
      } 
      if (bool1) {
        if (!bool2)
          throw omgLifecycleWrapper().adapterActivatorException(paramString, this.poaId.toString()); 
      } else {
        if (this.debug)
          ORBUtility.dprint(this, "Calling find_POA: AdapterActivator returned false"); 
        throw new AdapterNonExistent();
      } 
    } 
    return pOAImpl;
  }
  
  public void destroy(boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean2 && getORB().isDuringDispatch())
      throw lifecycleWrapper().destroyDeadlock(); 
    DestroyThread destroyThread = new DestroyThread(paramBoolean1, this.debug);
    destroyThread.doIt(this, paramBoolean2);
  }
  
  public ThreadPolicy create_thread_policy(ThreadPolicyValue paramThreadPolicyValue) { return new ThreadPolicyImpl(paramThreadPolicyValue); }
  
  public LifespanPolicy create_lifespan_policy(LifespanPolicyValue paramLifespanPolicyValue) { return new LifespanPolicyImpl(paramLifespanPolicyValue); }
  
  public IdUniquenessPolicy create_id_uniqueness_policy(IdUniquenessPolicyValue paramIdUniquenessPolicyValue) { return new IdUniquenessPolicyImpl(paramIdUniquenessPolicyValue); }
  
  public IdAssignmentPolicy create_id_assignment_policy(IdAssignmentPolicyValue paramIdAssignmentPolicyValue) { return new IdAssignmentPolicyImpl(paramIdAssignmentPolicyValue); }
  
  public ImplicitActivationPolicy create_implicit_activation_policy(ImplicitActivationPolicyValue paramImplicitActivationPolicyValue) { return new ImplicitActivationPolicyImpl(paramImplicitActivationPolicyValue); }
  
  public ServantRetentionPolicy create_servant_retention_policy(ServantRetentionPolicyValue paramServantRetentionPolicyValue) { return new ServantRetentionPolicyImpl(paramServantRetentionPolicyValue); }
  
  public RequestProcessingPolicy create_request_processing_policy(RequestProcessingPolicyValue paramRequestProcessingPolicyValue) { return new RequestProcessingPolicyImpl(paramRequestProcessingPolicyValue); }
  
  public String the_name() {
    try {
      lock();
      return this.name;
    } finally {
      unlock();
    } 
  }
  
  public POA the_parent() {
    try {
      lock();
      return this.parent;
    } finally {
      unlock();
    } 
  }
  
  public POA[] the_children() {
    try {
      lock();
      Collection collection = this.children.values();
      int i = collection.size();
      POA[] arrayOfPOA = new POA[i];
      byte b = 0;
      for (POA pOA : collection)
        arrayOfPOA[b++] = pOA; 
      return arrayOfPOA;
    } finally {
      unlock();
    } 
  }
  
  public POAManager the_POAManager() {
    try {
      lock();
      return this.manager;
    } finally {
      unlock();
    } 
  }
  
  public AdapterActivator the_activator() {
    try {
      lock();
      return this.activator;
    } finally {
      unlock();
    } 
  }
  
  public void the_activator(AdapterActivator paramAdapterActivator) {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling the_activator on poa " + this + " activator=" + paramAdapterActivator); 
      this.activator = paramAdapterActivator;
    } finally {
      unlock();
    } 
  }
  
  public ServantManager get_servant_manager() throws WrongPolicy {
    try {
      lock();
      return this.mediator.getServantManager();
    } finally {
      unlock();
    } 
  }
  
  public void set_servant_manager(ServantManager paramServantManager) throws WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling set_servant_manager on poa " + this + " servantManager=" + paramServantManager); 
      this.mediator.setServantManager(paramServantManager);
    } finally {
      unlock();
    } 
  }
  
  public Servant get_servant() throws NoServant, WrongPolicy {
    try {
      lock();
      return this.mediator.getDefaultServant();
    } finally {
      unlock();
    } 
  }
  
  public void set_servant(Servant paramServant) throws WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling set_servant on poa " + this + " defaultServant=" + paramServant); 
      this.mediator.setDefaultServant(paramServant);
    } finally {
      unlock();
    } 
  }
  
  public byte[] activate_object(Servant paramServant) throws ServantAlreadyActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling activate_object on poa " + this + " (servant=" + paramServant + ")"); 
      byte[] arrayOfByte = this.mediator.newSystemId();
      try {
        this.mediator.activateObject(arrayOfByte, paramServant);
      } catch (ObjectAlreadyActive objectAlreadyActive) {}
      return arrayOfByte;
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting activate_object on poa " + this); 
      unlock();
    } 
  }
  
  public void activate_object_with_id(byte[] paramArrayOfByte, Servant paramServant) throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling activate_object_with_id on poa " + this + " (servant=" + paramServant + " id=" + paramArrayOfByte + ")"); 
      byte[] arrayOfByte = (byte[])paramArrayOfByte.clone();
      this.mediator.activateObject(arrayOfByte, paramServant);
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting activate_object_with_id on poa " + this); 
      unlock();
    } 
  }
  
  public void deactivate_object(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling deactivate_object on poa " + this + " (id=" + paramArrayOfByte + ")"); 
      this.mediator.deactivateObject(paramArrayOfByte);
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting deactivate_object on poa " + this); 
      unlock();
    } 
  }
  
  public Object create_reference(String paramString) throws WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling create_reference(repId=" + paramString + ") on poa " + this); 
      return makeObject(paramString, this.mediator.newSystemId());
    } finally {
      unlock();
    } 
  }
  
  public Object create_reference_with_id(byte[] paramArrayOfByte, String paramString) {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling create_reference_with_id(oid=" + paramArrayOfByte + " repId=" + paramString + ") on poa " + this); 
      byte[] arrayOfByte = (byte[])paramArrayOfByte.clone();
      return makeObject(paramString, arrayOfByte);
    } finally {
      unlock();
    } 
  }
  
  public byte[] servant_to_id(Servant paramServant) throws ServantAlreadyActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling servant_to_id(servant=" + paramServant + ") on poa " + this); 
      return this.mediator.servantToId(paramServant);
    } finally {
      unlock();
    } 
  }
  
  public Object servant_to_reference(Servant paramServant) throws ServantNotActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling servant_to_reference(servant=" + paramServant + ") on poa " + this); 
      byte[] arrayOfByte = this.mediator.servantToId(paramServant);
      String str = paramServant._all_interfaces(this, arrayOfByte)[0];
      return create_reference_with_id(arrayOfByte, str);
    } finally {
      unlock();
    } 
  }
  
  public Servant reference_to_servant(Object paramObject) throws ObjectNotActive, WrongPolicy, WrongAdapter {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling reference_to_servant(reference=" + paramObject + ") on poa " + this); 
      if (this.state >= 4)
        throw lifecycleWrapper().adapterDestroyed(); 
      byte[] arrayOfByte = internalReferenceToId(paramObject);
      return this.mediator.idToServant(arrayOfByte);
    } finally {
      unlock();
    } 
  }
  
  public byte[] reference_to_id(Object paramObject) throws WrongAdapter {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling reference_to_id(reference=" + paramObject + ") on poa " + this); 
      if (this.state >= 4)
        throw lifecycleWrapper().adapterDestroyed(); 
      return internalReferenceToId(paramObject);
    } finally {
      unlock();
    } 
  }
  
  public Servant id_to_servant(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling id_to_servant(id=" + paramArrayOfByte + ") on poa " + this); 
      if (this.state >= 4)
        throw lifecycleWrapper().adapterDestroyed(); 
      return this.mediator.idToServant(paramArrayOfByte);
    } finally {
      unlock();
    } 
  }
  
  public Object id_to_reference(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling id_to_reference(id=" + paramArrayOfByte + ") on poa " + this); 
      if (this.state >= 4)
        throw lifecycleWrapper().adapterDestroyed(); 
      Servant servant = this.mediator.idToServant(paramArrayOfByte);
      String str = servant._all_interfaces(this, paramArrayOfByte)[0];
      return makeObject(str, paramArrayOfByte);
    } finally {
      unlock();
    } 
  }
  
  public byte[] id() {
    try {
      lock();
      return getAdapterId();
    } finally {
      unlock();
    } 
  }
  
  public Policy getEffectivePolicy(int paramInt) { return this.mediator.getPolicies().get_effective_policy(paramInt); }
  
  public int getManagerId() { return this.manager.getManagerId(); }
  
  public short getState() { return this.manager.getORTState(); }
  
  public String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte) {
    Servant servant = (Servant)paramObject;
    return servant._all_interfaces(this, paramArrayOfByte);
  }
  
  protected ObjectCopierFactory getObjectCopierFactory() {
    int i = this.mediator.getPolicies().getCopierId();
    CopierManager copierManager = getORB().getCopierManager();
    return copierManager.getObjectCopierFactory(i);
  }
  
  public void enter() {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling enter on poa " + this); 
      while (this.state == 4 && this.isDestroying.get() == Boolean.FALSE) {
        try {
          this.beingDestroyedCV.await();
        } catch (InterruptedException interruptedException) {}
      } 
      if (!waitUntilRunning())
        throw new OADestroyed(); 
      this.invocationCount++;
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting enter on poa " + this); 
      unlock();
    } 
    this.manager.enter();
  }
  
  public void exit() {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling exit on poa " + this); 
      this.invocationCount--;
      if (this.invocationCount == 0 && this.state == 4)
        this.invokeCV.broadcast(); 
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting exit on poa " + this); 
      unlock();
    } 
    this.manager.exit();
  }
  
  public void getInvocationServant(OAInvocationInfo paramOAInvocationInfo) {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling getInvocationServant on poa " + this); 
      Object object = null;
      try {
        object = this.mediator.getInvocationServant(paramOAInvocationInfo.id(), paramOAInvocationInfo.getOperation());
      } catch (ForwardRequest forwardRequest) {
        throw new ForwardException(getORB(), forwardRequest.forward_reference);
      } 
      paramOAInvocationInfo.setServant(object);
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting getInvocationServant on poa " + this); 
      unlock();
    } 
  }
  
  public Object getLocalServant(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy { return null; }
  
  public void returnServant() {
    try {
      lock();
      if (this.debug)
        ORBUtility.dprint(this, "Calling returnServant on poa " + this); 
      this.mediator.returnServant();
    } catch (Throwable throwable) {
      if (this.debug)
        ORBUtility.dprint(this, "Exception " + throwable + " in returnServant on poa " + this); 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
    } finally {
      if (this.debug)
        ORBUtility.dprint(this, "Exiting returnServant on poa " + this); 
      unlock();
    } 
  }
  
  static class DestroyThread extends Thread {
    private boolean wait;
    
    private boolean etherealize;
    
    private boolean debug;
    
    private POAImpl thePoa;
    
    public DestroyThread(boolean param1Boolean1, boolean param1Boolean2) {
      this.etherealize = param1Boolean1;
      this.debug = param1Boolean2;
    }
    
    public void doIt(POAImpl param1POAImpl, boolean param1Boolean) {
      if (this.debug)
        ORBUtility.dprint(this, "Calling DestroyThread.doIt(thePOA=" + param1POAImpl + " wait=" + param1Boolean + " etherealize=" + this.etherealize); 
      this.thePoa = param1POAImpl;
      this.wait = param1Boolean;
      if (param1Boolean) {
        run();
      } else {
        try {
          setDaemon(true);
        } catch (Exception exception) {}
        start();
      } 
    }
    
    public void run() {
      HashSet hashSet = new HashSet();
      performDestroy(this.thePoa, hashSet);
      Iterator iterator = hashSet.iterator();
      ObjectReferenceTemplate[] arrayOfObjectReferenceTemplate = new ObjectReferenceTemplate[hashSet.size()];
      byte b = 0;
      while (iterator.hasNext())
        arrayOfObjectReferenceTemplate[b++] = (ObjectReferenceTemplate)iterator.next(); 
      this.thePoa.getORB().getPIHandler().adapterStateChanged(arrayOfObjectReferenceTemplate, (short)4);
    }
    
    private boolean prepareForDestruction(POAImpl param1POAImpl, Set param1Set) {
      POAImpl[] arrayOfPOAImpl = null;
      try {
        param1POAImpl.lock();
        if (this.debug)
          ORBUtility.dprint(this, "Calling performDestroy on poa " + param1POAImpl); 
        if (param1POAImpl.state <= 3) {
          param1POAImpl.state = 4;
        } else {
          if (this.wait)
            while (param1POAImpl.state != 5) {
              try {
                param1POAImpl.beingDestroyedCV.await();
              } catch (InterruptedException interruptedException) {}
            }  
          return false;
        } 
        param1POAImpl.isDestroying.set(Boolean.TRUE);
        arrayOfPOAImpl = (POAImpl[])param1POAImpl.children.values().toArray(new POAImpl[0]);
      } finally {
        param1POAImpl.unlock();
      } 
      for (byte b = 0; b < arrayOfPOAImpl.length; b++)
        performDestroy(arrayOfPOAImpl[b], param1Set); 
      return true;
    }
    
    public void performDestroy(POAImpl param1POAImpl, Set param1Set) {
      if (!prepareForDestruction(param1POAImpl, param1Set))
        return; 
      pOAImpl = param1POAImpl.parent;
      bool = (pOAImpl == null) ? 1 : 0;
      try {
        if (!bool)
          pOAImpl.lock(); 
        try {
          param1POAImpl.lock();
          completeDestruction(param1POAImpl, pOAImpl, param1Set);
        } finally {
          param1POAImpl.unlock();
          if (bool)
            param1POAImpl.manager.getFactory().registerRootPOA(); 
        } 
      } finally {
        if (!bool) {
          pOAImpl.unlock();
          param1POAImpl.parent = null;
        } 
      } 
    }
    
    private void completeDestruction(POAImpl param1POAImpl1, POAImpl param1POAImpl2, Set param1Set) { // Byte code:
      //   0: aload_0
      //   1: getfield debug : Z
      //   4: ifeq -> 30
      //   7: aload_0
      //   8: new java/lang/StringBuilder
      //   11: dup
      //   12: invokespecial <init> : ()V
      //   15: ldc 'Calling completeDestruction on poa '
      //   17: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   20: aload_1
      //   21: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   24: invokevirtual toString : ()Ljava/lang/String;
      //   27: invokestatic dprint : (Ljava/lang/Object;Ljava/lang/String;)V
      //   30: aload_1
      //   31: invokestatic access$500 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)I
      //   34: ifeq -> 52
      //   37: aload_1
      //   38: invokestatic access$600 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/orbutil/concurrent/CondVar;
      //   41: invokevirtual await : ()V
      //   44: goto -> 30
      //   47: astore #4
      //   49: goto -> 30
      //   52: aload_1
      //   53: invokestatic access$700 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
      //   56: ifnull -> 84
      //   59: aload_0
      //   60: getfield etherealize : Z
      //   63: ifeq -> 75
      //   66: aload_1
      //   67: invokestatic access$700 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
      //   70: invokeinterface etherealizeAll : ()V
      //   75: aload_1
      //   76: invokestatic access$700 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
      //   79: invokeinterface clearAOM : ()V
      //   84: aload_1
      //   85: invokestatic access$400 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;
      //   88: ifnull -> 99
      //   91: aload_1
      //   92: invokestatic access$400 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;
      //   95: aload_1
      //   96: invokevirtual removePOA : (Lorg/omg/PortableServer/POA;)V
      //   99: aload_2
      //   100: ifnull -> 117
      //   103: aload_2
      //   104: invokestatic access$200 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Ljava/util/Map;
      //   107: aload_1
      //   108: invokestatic access$800 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Ljava/lang/String;
      //   111: invokeinterface remove : (Ljava/lang/Object;)Ljava/lang/Object;
      //   116: pop
      //   117: aload_3
      //   118: aload_1
      //   119: invokevirtual getAdapterTemplate : ()Lorg/omg/PortableInterceptor/ObjectReferenceTemplate;
      //   122: invokeinterface add : (Ljava/lang/Object;)Z
      //   127: pop
      //   128: aload_1
      //   129: iconst_5
      //   130: invokestatic access$002 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;I)I
      //   133: pop
      //   134: aload_1
      //   135: invokestatic access$100 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/orbutil/concurrent/CondVar;
      //   138: invokevirtual broadcast : ()V
      //   141: aload_1
      //   142: getfield isDestroying : Ljava/lang/ThreadLocal;
      //   145: getstatic java/lang/Boolean.FALSE : Ljava/lang/Boolean;
      //   148: invokevirtual set : (Ljava/lang/Object;)V
      //   151: aload_0
      //   152: getfield debug : Z
      //   155: ifeq -> 328
      //   158: aload_0
      //   159: new java/lang/StringBuilder
      //   162: dup
      //   163: invokespecial <init> : ()V
      //   166: ldc 'Exiting completeDestruction on poa '
      //   168: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   171: aload_1
      //   172: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   175: invokevirtual toString : ()Ljava/lang/String;
      //   178: invokestatic dprint : (Ljava/lang/Object;Ljava/lang/String;)V
      //   181: goto -> 328
      //   184: astore #4
      //   186: aload #4
      //   188: instanceof java/lang/ThreadDeath
      //   191: ifeq -> 200
      //   194: aload #4
      //   196: checkcast java/lang/ThreadDeath
      //   199: athrow
      //   200: aload_1
      //   201: invokevirtual lifecycleWrapper : ()Lcom/sun/corba/se/impl/logging/POASystemException;
      //   204: aload #4
      //   206: aload_1
      //   207: invokevirtual toString : ()Ljava/lang/String;
      //   210: invokevirtual unexpectedException : (Ljava/lang/Throwable;Ljava/lang/Object;)Lorg/omg/CORBA/INTERNAL;
      //   213: pop
      //   214: aload_1
      //   215: iconst_5
      //   216: invokestatic access$002 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;I)I
      //   219: pop
      //   220: aload_1
      //   221: invokestatic access$100 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/orbutil/concurrent/CondVar;
      //   224: invokevirtual broadcast : ()V
      //   227: aload_1
      //   228: getfield isDestroying : Ljava/lang/ThreadLocal;
      //   231: getstatic java/lang/Boolean.FALSE : Ljava/lang/Boolean;
      //   234: invokevirtual set : (Ljava/lang/Object;)V
      //   237: aload_0
      //   238: getfield debug : Z
      //   241: ifeq -> 328
      //   244: aload_0
      //   245: new java/lang/StringBuilder
      //   248: dup
      //   249: invokespecial <init> : ()V
      //   252: ldc 'Exiting completeDestruction on poa '
      //   254: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   257: aload_1
      //   258: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   261: invokevirtual toString : ()Ljava/lang/String;
      //   264: invokestatic dprint : (Ljava/lang/Object;Ljava/lang/String;)V
      //   267: goto -> 328
      //   270: astore #5
      //   272: aload_1
      //   273: iconst_5
      //   274: invokestatic access$002 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;I)I
      //   277: pop
      //   278: aload_1
      //   279: invokestatic access$100 : (Lcom/sun/corba/se/impl/oa/poa/POAImpl;)Lcom/sun/corba/se/impl/orbutil/concurrent/CondVar;
      //   282: invokevirtual broadcast : ()V
      //   285: aload_1
      //   286: getfield isDestroying : Ljava/lang/ThreadLocal;
      //   289: getstatic java/lang/Boolean.FALSE : Ljava/lang/Boolean;
      //   292: invokevirtual set : (Ljava/lang/Object;)V
      //   295: aload_0
      //   296: getfield debug : Z
      //   299: ifeq -> 325
      //   302: aload_0
      //   303: new java/lang/StringBuilder
      //   306: dup
      //   307: invokespecial <init> : ()V
      //   310: ldc 'Exiting completeDestruction on poa '
      //   312: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   315: aload_1
      //   316: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   319: invokevirtual toString : ()Ljava/lang/String;
      //   322: invokestatic dprint : (Ljava/lang/Object;Ljava/lang/String;)V
      //   325: aload #5
      //   327: athrow
      //   328: return
      // Exception table:
      //   from	to	target	type
      //   30	128	184	java/lang/Throwable
      //   30	128	270	finally
      //   37	44	47	java/lang/InterruptedException
      //   184	214	270	finally
      //   270	272	270	finally }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */