package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import com.sun.corba.se.impl.naming.cosnaming.NamingContextDataStore;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtPOA;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class NamingContextImpl extends NamingContextExtPOA implements NamingContextDataStore, Serializable {
  private ORB orb;
  
  private final String objKey;
  
  private final Hashtable theHashtable = new Hashtable();
  
  private NameService theNameServiceHandle;
  
  private ServantManagerImpl theServantManagerImplHandle;
  
  private InterOperableNamingImpl insImpl;
  
  private NamingSystemException readWrapper;
  
  private NamingSystemException updateWrapper;
  
  private static POA biPOA = null;
  
  private static boolean debug;
  
  public NamingContextImpl(ORB paramORB, String paramString, NameService paramNameService, ServantManagerImpl paramServantManagerImpl) throws Exception {
    this.orb = paramORB;
    this.updateWrapper = (this.readWrapper = NamingSystemException.get(paramORB, "naming.read")).get(paramORB, "naming.update");
    debug = true;
    this.objKey = paramString;
    this.theNameServiceHandle = paramNameService;
    this.theServantManagerImplHandle = paramServantManagerImpl;
    this.insImpl = new InterOperableNamingImpl();
  }
  
  InterOperableNamingImpl getINSImpl() {
    if (this.insImpl == null)
      this.insImpl = new InterOperableNamingImpl(); 
    return this.insImpl;
  }
  
  public void setRootNameService(NameService paramNameService) { this.theNameServiceHandle = paramNameService; }
  
  public void setORB(ORB paramORB) { this.orb = paramORB; }
  
  public void setServantManagerImpl(ServantManagerImpl paramServantManagerImpl) { this.theServantManagerImplHandle = paramServantManagerImpl; }
  
  public POA getNSPOA() { return this.theNameServiceHandle.getNSPOA(); }
  
  public void bind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramObject == null)
      throw this.updateWrapper.objectIsNull(); 
    if (debug)
      dprint("bind " + nameToString(paramArrayOfNameComponent) + " to " + paramObject); 
    NamingContextImpl namingContextImpl = this;
    doBind(namingContextImpl, paramArrayOfNameComponent, paramObject, false, BindingType.nobject);
  }
  
  public void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramNamingContext == null)
      throw this.updateWrapper.objectIsNull(); 
    NamingContextImpl namingContextImpl = this;
    doBind(namingContextImpl, paramArrayOfNameComponent, paramNamingContext, false, BindingType.ncontext);
  }
  
  public void rebind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramObject == null)
      throw this.updateWrapper.objectIsNull(); 
    try {
      if (debug)
        dprint("rebind " + nameToString(paramArrayOfNameComponent) + " to " + paramObject); 
      NamingContextImpl namingContextImpl = this;
      doBind(namingContextImpl, paramArrayOfNameComponent, paramObject, true, BindingType.nobject);
    } catch (AlreadyBound alreadyBound) {
      throw this.updateWrapper.namingCtxRebindAlreadyBound(alreadyBound);
    } 
  }
  
  public void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    try {
      if (debug)
        dprint("rebind_context " + nameToString(paramArrayOfNameComponent) + " to " + paramNamingContext); 
      NamingContextImpl namingContextImpl = this;
      doBind(namingContextImpl, paramArrayOfNameComponent, paramNamingContext, true, BindingType.ncontext);
    } catch (AlreadyBound alreadyBound) {
      throw this.updateWrapper.namingCtxRebindAlreadyBound(alreadyBound);
    } 
  }
  
  public Object resolve(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    if (debug)
      dprint("resolve " + nameToString(paramArrayOfNameComponent)); 
    NamingContextImpl namingContextImpl;
    return (namingContextImpl = this).doResolve(namingContextImpl, paramArrayOfNameComponent);
  }
  
  public void unbind(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    if (debug)
      dprint("unbind " + nameToString(paramArrayOfNameComponent)); 
    NamingContextImpl namingContextImpl;
    (namingContextImpl = this).doUnbind(namingContextImpl, paramArrayOfNameComponent);
  }
  
  public void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder) {
    if (debug)
      dprint("list(" + paramInt + ")"); 
    NamingContextImpl namingContextImpl = this;
    synchronized (namingContextImpl) {
      namingContextImpl.List(paramInt, paramBindingListHolder, paramBindingIteratorHolder);
    } 
    if (debug && paramBindingListHolder.value != null)
      dprint("list(" + paramInt + ") -> bindings[" + paramBindingListHolder.value.length + "] + iterator: " + paramBindingIteratorHolder.value); 
  }
  
  public NamingContext new_context() {
    if (debug)
      dprint("new_context()"); 
    NamingContextImpl namingContextImpl = this;
    synchronized (namingContextImpl) {
      return namingContextImpl.NewContext();
    } 
  }
  
  public NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
    namingContext1 = null;
    NamingContext namingContext2 = null;
    try {
      if (debug)
        dprint("bind_new_context " + nameToString(paramArrayOfNameComponent)); 
      namingContext1 = new_context();
      bind_context(paramArrayOfNameComponent, namingContext1);
      namingContext2 = namingContext1;
      namingContext1 = null;
    } finally {
      try {
        if (namingContext1 != null)
          namingContext1.destroy(); 
      } catch (NotEmpty notEmpty) {}
    } 
    return namingContext2;
  }
  
  public void destroy() throws NotEmpty {
    if (debug)
      dprint("destroy "); 
    NamingContextImpl namingContextImpl = this;
    synchronized (namingContextImpl) {
      if (namingContextImpl.IsEmpty() == true) {
        namingContextImpl.Destroy();
      } else {
        throw new NotEmpty();
      } 
    } 
  }
  
  private void doBind(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent, Object paramObject, boolean paramBoolean, BindingType paramBindingType) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramArrayOfNameComponent.length < 1)
      throw new InvalidName(); 
    if (paramArrayOfNameComponent.length == 1) {
      if ((paramArrayOfNameComponent[0]).id.length() == 0 && (paramArrayOfNameComponent[0]).kind.length() == 0)
        throw new InvalidName(); 
      synchronized (paramNamingContextDataStore) {
        BindingTypeHolder bindingTypeHolder = new BindingTypeHolder();
        if (paramBoolean) {
          Object object = paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], bindingTypeHolder);
          if (object != null) {
            if (bindingTypeHolder.value.value() == BindingType.nobject.value()) {
              if (paramBindingType.value() == BindingType.ncontext.value())
                throw new NotFound(NotFoundReason.not_context, paramArrayOfNameComponent); 
            } else if (paramBindingType.value() == BindingType.nobject.value()) {
              throw new NotFound(NotFoundReason.not_object, paramArrayOfNameComponent);
            } 
            paramNamingContextDataStore.Unbind(paramArrayOfNameComponent[0]);
          } 
        } else if (paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[false], bindingTypeHolder) != null) {
          throw new AlreadyBound();
        } 
        paramNamingContextDataStore.Bind(paramArrayOfNameComponent[0], paramObject, paramBindingType);
      } 
    } else {
      NamingContext namingContext2;
      NamingContext namingContext1 = resolveFirstAsContext(paramNamingContextDataStore, paramArrayOfNameComponent);
      NameComponent[] arrayOfNameComponent = new NameComponent[paramArrayOfNameComponent.length - 1];
      System.arraycopy(paramArrayOfNameComponent, 1, arrayOfNameComponent, 0, paramArrayOfNameComponent.length - 1);
      switch (paramBindingType.value()) {
        case 0:
          if (paramBoolean) {
            namingContext1.rebind(arrayOfNameComponent, paramObject);
          } else {
            namingContext1.bind(arrayOfNameComponent, paramObject);
          } 
          return;
        case 1:
          namingContext2 = (NamingContext)paramObject;
          if (paramBoolean) {
            namingContext1.rebind_context(arrayOfNameComponent, namingContext2);
          } else {
            namingContext1.bind_context(arrayOfNameComponent, namingContext2);
          } 
          return;
      } 
      throw this.updateWrapper.namingCtxBadBindingtype();
    } 
  }
  
  public static Object doResolve(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    Object object = null;
    BindingTypeHolder bindingTypeHolder = new BindingTypeHolder();
    if (paramArrayOfNameComponent.length < 1)
      throw new InvalidName(); 
    if (paramArrayOfNameComponent.length == 1) {
      synchronized (paramNamingContextDataStore) {
        object = paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], bindingTypeHolder);
      } 
      if (object == null)
        throw new NotFound(NotFoundReason.missing_node, paramArrayOfNameComponent); 
      return object;
    } 
    if ((paramArrayOfNameComponent[1]).id.length() == 0 && (paramArrayOfNameComponent[1]).kind.length() == 0)
      throw new InvalidName(); 
    NamingContext namingContext = resolveFirstAsContext(paramNamingContextDataStore, paramArrayOfNameComponent);
    NameComponent[] arrayOfNameComponent = new NameComponent[paramArrayOfNameComponent.length - 1];
    System.arraycopy(paramArrayOfNameComponent, 1, arrayOfNameComponent, 0, paramArrayOfNameComponent.length - 1);
    return namingContext.resolve(arrayOfNameComponent);
  }
  
  public static void doUnbind(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    if (paramArrayOfNameComponent.length < 1)
      throw new InvalidName(); 
    if (paramArrayOfNameComponent.length == 1) {
      if ((paramArrayOfNameComponent[0]).id.length() == 0 && (paramArrayOfNameComponent[0]).kind.length() == 0)
        throw new InvalidName(); 
      Object object = null;
      synchronized (paramNamingContextDataStore) {
        object = paramNamingContextDataStore.Unbind(paramArrayOfNameComponent[0]);
      } 
      if (object == null)
        throw new NotFound(NotFoundReason.missing_node, paramArrayOfNameComponent); 
      return;
    } 
    NamingContext namingContext = resolveFirstAsContext(paramNamingContextDataStore, paramArrayOfNameComponent);
    NameComponent[] arrayOfNameComponent = new NameComponent[paramArrayOfNameComponent.length - 1];
    System.arraycopy(paramArrayOfNameComponent, 1, arrayOfNameComponent, 0, paramArrayOfNameComponent.length - 1);
    namingContext.unbind(arrayOfNameComponent);
  }
  
  protected static NamingContext resolveFirstAsContext(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent) throws NotFound {
    Object object = null;
    BindingTypeHolder bindingTypeHolder = new BindingTypeHolder();
    NamingContext namingContext = null;
    synchronized (paramNamingContextDataStore) {
      object = paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], bindingTypeHolder);
      if (object == null)
        throw new NotFound(NotFoundReason.missing_node, paramArrayOfNameComponent); 
    } 
    if (bindingTypeHolder.value != BindingType.ncontext)
      throw new NotFound(NotFoundReason.not_context, paramArrayOfNameComponent); 
    try {
      namingContext = NamingContextHelper.narrow(object);
    } catch (BAD_PARAM bAD_PARAM) {
      throw new NotFound(NotFoundReason.not_context, paramArrayOfNameComponent);
    } 
    return namingContext;
  }
  
  public static String nameToString(NameComponent[] paramArrayOfNameComponent) {
    StringBuffer stringBuffer = new StringBuffer("{");
    if (paramArrayOfNameComponent != null || paramArrayOfNameComponent.length > 0)
      for (byte b = 0; b < paramArrayOfNameComponent.length; b++) {
        if (b)
          stringBuffer.append(","); 
        stringBuffer.append("[").append((paramArrayOfNameComponent[b]).id).append(",").append((paramArrayOfNameComponent[b]).kind).append("]");
      }  
    stringBuffer.append("}");
    return stringBuffer.toString();
  }
  
  private static void dprint(String paramString) { NamingUtils.dprint("NamingContextImpl(" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " ems): " + paramString); }
  
  public void Bind(NameComponent paramNameComponent, Object paramObject, BindingType paramBindingType) {
    if (paramObject == null)
      return; 
    InternalBindingKey internalBindingKey = new InternalBindingKey(paramNameComponent);
    try {
      InternalBindingValue internalBindingValue1;
      if (paramBindingType.value() == 0) {
        internalBindingValue1 = new InternalBindingValue(paramBindingType, this.orb.object_to_string(paramObject));
        internalBindingValue1.setObjectRef(paramObject);
      } else {
        String str = this.theNameServiceHandle.getObjectKey(paramObject);
        internalBindingValue1 = new InternalBindingValue(paramBindingType, str);
        internalBindingValue1.setObjectRef(paramObject);
      } 
      InternalBindingValue internalBindingValue2 = (InternalBindingValue)this.theHashtable.put(internalBindingKey, internalBindingValue1);
      if (internalBindingValue2 != null)
        throw this.updateWrapper.namingCtxRebindAlreadyBound(); 
      try {
        this.theServantManagerImplHandle.updateContext(this.objKey, this);
      } catch (Exception exception) {
        throw this.updateWrapper.bindUpdateContextFailed(exception);
      } 
    } catch (Exception exception) {
      throw this.updateWrapper.bindFailure(exception);
    } 
  }
  
  public Object Resolve(NameComponent paramNameComponent, BindingTypeHolder paramBindingTypeHolder) throws SystemException {
    if (paramNameComponent.id.length() == 0 && paramNameComponent.kind.length() == 0) {
      paramBindingTypeHolder.value = BindingType.ncontext;
      return this.theNameServiceHandle.getObjectReferenceFromKey(this.objKey);
    } 
    InternalBindingKey internalBindingKey = new InternalBindingKey(paramNameComponent);
    InternalBindingValue internalBindingValue = (InternalBindingValue)this.theHashtable.get(internalBindingKey);
    if (internalBindingValue == null)
      return null; 
    Object object = null;
    paramBindingTypeHolder.value = internalBindingValue.theBindingType;
    try {
      if (internalBindingValue.strObjectRef.startsWith("NC")) {
        paramBindingTypeHolder.value = BindingType.ncontext;
        return this.theNameServiceHandle.getObjectReferenceFromKey(internalBindingValue.strObjectRef);
      } 
      object = internalBindingValue.getObjectRef();
      if (object == null)
        try {
          object = this.orb.string_to_object(internalBindingValue.strObjectRef);
          internalBindingValue.setObjectRef(object);
        } catch (Exception exception) {
          throw this.readWrapper.resolveConversionFailure(CompletionStatus.COMPLETED_MAYBE, exception);
        }  
    } catch (Exception exception) {
      throw this.readWrapper.resolveFailure(CompletionStatus.COMPLETED_MAYBE, exception);
    } 
    return object;
  }
  
  public Object Unbind(NameComponent paramNameComponent) throws SystemException {
    try {
      InternalBindingKey internalBindingKey = new InternalBindingKey(paramNameComponent);
      InternalBindingValue internalBindingValue = null;
      try {
        internalBindingValue = (InternalBindingValue)this.theHashtable.remove(internalBindingKey);
      } catch (Exception exception) {}
      this.theServantManagerImplHandle.updateContext(this.objKey, this);
      if (internalBindingValue == null)
        return null; 
      if (internalBindingValue.strObjectRef.startsWith("NC")) {
        this.theServantManagerImplHandle.readInContext(internalBindingValue.strObjectRef);
        return this.theNameServiceHandle.getObjectReferenceFromKey(internalBindingValue.strObjectRef);
      } 
      Object object = internalBindingValue.getObjectRef();
      if (object == null)
        object = this.orb.string_to_object(internalBindingValue.strObjectRef); 
      return object;
    } catch (Exception exception) {
      throw this.updateWrapper.unbindFailure(CompletionStatus.COMPLETED_MAYBE, exception);
    } 
  }
  
  public void List(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder) {
    if (biPOA == null)
      createbiPOA(); 
    try {
      PersistentBindingIterator persistentBindingIterator = new PersistentBindingIterator(this.orb, (Hashtable)this.theHashtable.clone(), biPOA);
      persistentBindingIterator.list(paramInt, paramBindingListHolder);
      byte[] arrayOfByte = biPOA.activate_object(persistentBindingIterator);
      Object object = biPOA.id_to_reference(arrayOfByte);
      BindingIterator bindingIterator = BindingIteratorHelper.narrow(object);
      paramBindingIteratorHolder.value = bindingIterator;
    } catch (SystemException systemException) {
      throw systemException;
    } catch (Exception exception) {
      throw this.readWrapper.transNcListGotExc(exception);
    } 
  }
  
  private void createbiPOA() throws NotEmpty {
    if (biPOA != null)
      return; 
    try {
      POA pOA = (POA)this.orb.resolve_initial_references("RootPOA");
      pOA.the_POAManager().activate();
      byte b = 0;
      Policy[] arrayOfPolicy = new Policy[3];
      arrayOfPolicy[b++] = pOA.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
      arrayOfPolicy[b++] = pOA.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
      arrayOfPolicy[b++] = pOA.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
      biPOA = pOA.create_POA("BindingIteratorPOA", null, arrayOfPolicy);
      biPOA.the_POAManager().activate();
    } catch (Exception exception) {
      throw this.readWrapper.namingCtxBindingIteratorCreate(exception);
    } 
  }
  
  public NamingContext NewContext() {
    try {
      return this.theNameServiceHandle.NewContext();
    } catch (SystemException systemException) {
      throw systemException;
    } catch (Exception exception) {
      throw this.updateWrapper.transNcNewctxGotExc(exception);
    } 
  }
  
  public void Destroy() throws NotEmpty {}
  
  public String to_string(NameComponent[] paramArrayOfNameComponent) {
    if (paramArrayOfNameComponent == null || paramArrayOfNameComponent.length == 0)
      throw new InvalidName(); 
    String str = getINSImpl().convertToString(paramArrayOfNameComponent);
    if (str == null)
      throw new InvalidName(); 
    return str;
  }
  
  public NameComponent[] to_name(String paramString) throws InvalidName {
    if (paramString == null || paramString.length() == 0)
      throw new InvalidName(); 
    NameComponent[] arrayOfNameComponent = getINSImpl().convertToNameComponent(paramString);
    if (arrayOfNameComponent == null || arrayOfNameComponent.length == 0)
      throw new InvalidName(); 
    for (byte b = 0; b < arrayOfNameComponent.length; b++) {
      if (((arrayOfNameComponent[b]).id == null || (arrayOfNameComponent[b]).id.length() == 0) && ((arrayOfNameComponent[b]).kind == null || (arrayOfNameComponent[b]).kind.length() == 0))
        throw new InvalidName(); 
    } 
    return arrayOfNameComponent;
  }
  
  public String to_url(String paramString1, String paramString2) throws InvalidAddress, InvalidName {
    if (paramString2 == null || paramString2.length() == 0)
      throw new InvalidName(); 
    if (paramString1 == null)
      throw new InvalidAddress(); 
    String str = null;
    try {
      str = getINSImpl().createURLBasedAddress(paramString1, paramString2);
    } catch (Exception exception) {
      str = null;
    } 
    try {
      INSURLHandler.getINSURLHandler().parseURL(str);
    } catch (BAD_PARAM bAD_PARAM) {
      throw new InvalidAddress();
    } 
    return str;
  }
  
  public Object resolve_str(String paramString) throws NotFound, CannotProceed, InvalidName {
    null = null;
    if (paramString == null || paramString.length() == 0)
      throw new InvalidName(); 
    NameComponent[] arrayOfNameComponent = getINSImpl().convertToNameComponent(paramString);
    if (arrayOfNameComponent == null || arrayOfNameComponent.length == 0)
      throw new InvalidName(); 
    return resolve(arrayOfNameComponent);
  }
  
  public boolean IsEmpty() { return this.theHashtable.isEmpty(); }
  
  public void printSize() throws NotEmpty {
    System.out.println("Hashtable Size = " + this.theHashtable.size());
    Enumeration enumeration = this.theHashtable.keys();
    while (enumeration.hasMoreElements()) {
      InternalBindingValue internalBindingValue = (InternalBindingValue)this.theHashtable.get(enumeration.nextElement());
      if (internalBindingValue != null)
        System.out.println("value = " + internalBindingValue.strObjectRef); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\NamingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */