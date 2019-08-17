package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
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
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class NamingContextImpl extends NamingContextExtPOA implements NamingContextDataStore {
  protected POA nsPOA;
  
  private Logger readLogger;
  
  private Logger updateLogger;
  
  private Logger lifecycleLogger;
  
  private NamingSystemException wrapper;
  
  private static NamingSystemException staticWrapper = NamingSystemException.get("naming.update");
  
  private InterOperableNamingImpl insImpl;
  
  protected ORB orb;
  
  public static final boolean debug = false;
  
  public NamingContextImpl(ORB paramORB, POA paramPOA) throws Exception {
    this.orb = paramORB;
    this.wrapper = NamingSystemException.get(paramORB, "naming.update");
    this.insImpl = new InterOperableNamingImpl();
    this.nsPOA = paramPOA;
    this.readLogger = paramORB.getLogger("naming.read");
    this.updateLogger = paramORB.getLogger("naming.update");
    this.lifecycleLogger = paramORB.getLogger("naming.lifecycle");
  }
  
  public POA getNSPOA() { return this.nsPOA; }
  
  public void bind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramObject == null) {
      this.updateLogger.warning("<<NAMING BIND>> unsuccessful because NULL Object cannot be Bound ");
      throw this.wrapper.objectIsNull();
    } 
    NamingContextImpl namingContextImpl;
    (namingContextImpl = this).doBind(namingContextImpl, paramArrayOfNameComponent, paramObject, false, BindingType.nobject);
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
  }
  
  public void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramNamingContext == null) {
      this.updateLogger.warning("<<NAMING BIND>><<FAILURE>> NULL Context cannot be Bound ");
      throw new BAD_PARAM("Naming Context should not be null ");
    } 
    NamingContextImpl namingContextImpl;
    (namingContextImpl = this).doBind(namingContextImpl, paramArrayOfNameComponent, paramNamingContext, false, BindingType.ncontext);
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
  }
  
  public void rebind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramObject == null) {
      this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Object cannot be Bound ");
      throw this.wrapper.objectIsNull();
    } 
    try {
      NamingContextImpl namingContextImpl;
      (namingContextImpl = this).doBind(namingContextImpl, paramArrayOfNameComponent, paramObject, true, BindingType.nobject);
    } catch (AlreadyBound alreadyBound) {
      this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent) + " is already bound to a Naming Context");
      throw this.wrapper.namingCtxRebindAlreadyBound(alreadyBound);
    } 
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
  }
  
  public void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    if (paramNamingContext == null) {
      this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Context cannot be Bound ");
      throw this.wrapper.objectIsNull();
    } 
    try {
      NamingContextImpl namingContextImpl;
      (namingContextImpl = this).doBind(namingContextImpl, paramArrayOfNameComponent, paramNamingContext, true, BindingType.ncontext);
    } catch (AlreadyBound alreadyBound) {
      this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent) + " is already bound to a CORBA Object");
      throw this.wrapper.namingCtxRebindctxAlreadyBound(alreadyBound);
    } 
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
  }
  
  public Object resolve(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    NamingContextImpl namingContextImpl;
    Object object = (namingContextImpl = this).doResolve(namingContextImpl, paramArrayOfNameComponent);
    if (object != null) {
      if (this.readLogger.isLoggable(Level.FINE))
        this.readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
    } else {
      this.readLogger.warning("<<NAMING RESOLVE>><<FAILURE>> Name: " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    } 
    return object;
  }
  
  public void unbind(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    NamingContextImpl namingContextImpl;
    (namingContextImpl = this).doUnbind(namingContextImpl, paramArrayOfNameComponent);
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
  }
  
  public void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder) {
    NamingContextImpl namingContextImpl = this;
    synchronized (namingContextImpl) {
      namingContextImpl.List(paramInt, paramBindingListHolder, paramBindingIteratorHolder);
    } 
    if (this.readLogger.isLoggable(Level.FINE) && paramBindingListHolder.value != null)
      this.readLogger.fine("<<NAMING LIST>><<SUCCESS>>list(" + paramInt + ") -> bindings[" + paramBindingListHolder.value.length + "] + iterator: " + paramBindingIteratorHolder.value); 
  }
  
  public NamingContext new_context() {
    this.lifecycleLogger.fine("Creating New Naming Context ");
    NamingContextImpl namingContextImpl = this;
    synchronized (namingContextImpl) {
      NamingContext namingContext = namingContextImpl.NewContext();
      if (namingContext != null) {
        this.lifecycleLogger.fine("<<LIFECYCLE CREATE>><<SUCCESS>>");
      } else {
        this.lifecycleLogger.severe("<<LIFECYCLE CREATE>><<FAILURE>>");
      } 
      return namingContext;
    } 
  }
  
  public NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
    namingContext1 = null;
    NamingContext namingContext2 = null;
    try {
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
    if (this.updateLogger.isLoggable(Level.FINE))
      this.updateLogger.fine("<<NAMING BIND>>New Context Bound To " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent)); 
    return namingContext2;
  }
  
  public void destroy() throws NotEmpty {
    this.lifecycleLogger.fine("Destroying Naming Context ");
    NamingContextImpl namingContextImpl = this;
    synchronized (namingContextImpl) {
      if (namingContextImpl.IsEmpty() == true) {
        namingContextImpl.Destroy();
        this.lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
      } else {
        this.lifecycleLogger.warning("<<LIFECYCLE DESTROY>><<FAILURE>> NamingContext children are not destroyed still..");
        throw new NotEmpty();
      } 
    } 
  }
  
  public static void doBind(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent, Object paramObject, boolean paramBoolean, BindingType paramBindingType) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
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
      throw staticWrapper.namingCtxBadBindingtype();
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
    try {
      Servant servant = paramNamingContextDataStore.getNSPOA().reference_to_servant(namingContext);
      return doResolve((NamingContextDataStore)servant, arrayOfNameComponent);
    } catch (Exception exception) {
      return namingContext.resolve(arrayOfNameComponent);
    } 
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
  
  public String to_string(NameComponent[] paramArrayOfNameComponent) throws InvalidName {
    if (paramArrayOfNameComponent == null || paramArrayOfNameComponent.length == 0)
      throw new InvalidName(); 
    NamingContextImpl namingContextImpl = this;
    String str = this.insImpl.convertToString(paramArrayOfNameComponent);
    if (str == null)
      throw new InvalidName(); 
    return str;
  }
  
  public NameComponent[] to_name(String paramString) throws InvalidName {
    if (paramString == null || paramString.length() == 0)
      throw new InvalidName(); 
    NamingContextImpl namingContextImpl = this;
    NameComponent[] arrayOfNameComponent = this.insImpl.convertToNameComponent(paramString);
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
    NamingContextImpl namingContextImpl = this;
    String str = null;
    str = this.insImpl.createURLBasedAddress(paramString1, paramString2);
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
    NamingContextImpl namingContextImpl = this;
    NameComponent[] arrayOfNameComponent = this.insImpl.convertToNameComponent(paramString);
    if (arrayOfNameComponent == null || arrayOfNameComponent.length == 0)
      throw new InvalidName(); 
    return resolve(arrayOfNameComponent);
  }
  
  public static String nameToString(NameComponent[] paramArrayOfNameComponent) throws InvalidName {
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\NamingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */