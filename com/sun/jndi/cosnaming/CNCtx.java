package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class CNCtx implements Context {
  private static final boolean debug = false;
  
  private static ORB _defaultOrb;
  
  ORB _orb;
  
  public NamingContext _nc;
  
  private NameComponent[] _name = null;
  
  Hashtable<String, Object> _env;
  
  static final CNNameParser parser = new CNNameParser();
  
  private static final String FED_PROP = "com.sun.jndi.cosnaming.federation";
  
  boolean federation = false;
  
  public static final boolean trustURLCodebase;
  
  OrbReuseTracker orbTracker = null;
  
  int enumCount;
  
  boolean isCloseCalled = false;
  
  private static ORB getDefaultOrb() {
    if (_defaultOrb == null)
      _defaultOrb = CorbaUtils.getOrb(null, -1, new Hashtable()); 
    return _defaultOrb;
  }
  
  CNCtx(Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramHashtable != null)
      paramHashtable = (Hashtable)paramHashtable.clone(); 
    this._env = paramHashtable;
    this.federation = "true".equals((paramHashtable != null) ? paramHashtable.get("com.sun.jndi.cosnaming.federation") : null);
    initOrbAndRootContext(paramHashtable);
  }
  
  private CNCtx() {}
  
  public static ResolveResult createUsingURL(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    CNCtx cNCtx = new CNCtx();
    if (paramHashtable != null)
      paramHashtable = (Hashtable)paramHashtable.clone(); 
    cNCtx._env = paramHashtable;
    String str = cNCtx.initUsingUrl((paramHashtable != null) ? (ORB)paramHashtable.get("java.naming.corba.orb") : null, paramString, paramHashtable);
    return new ResolveResult(cNCtx, parser.parse(str));
  }
  
  CNCtx(ORB paramORB, OrbReuseTracker paramOrbReuseTracker, NamingContext paramNamingContext, Hashtable<String, Object> paramHashtable, NameComponent[] paramArrayOfNameComponent) throws NamingException {
    if (paramORB == null || paramNamingContext == null)
      throw new ConfigurationException("Must supply ORB or NamingContext"); 
    if (paramORB != null) {
      this._orb = paramORB;
    } else {
      this._orb = getDefaultOrb();
    } 
    this._nc = paramNamingContext;
    this._env = paramHashtable;
    this._name = paramArrayOfNameComponent;
    this.federation = "true".equals((paramHashtable != null) ? paramHashtable.get("com.sun.jndi.cosnaming.federation") : null);
  }
  
  NameComponent[] makeFullName(NameComponent[] paramArrayOfNameComponent) {
    if (this._name == null || this._name.length == 0)
      return paramArrayOfNameComponent; 
    NameComponent[] arrayOfNameComponent = new NameComponent[this._name.length + paramArrayOfNameComponent.length];
    System.arraycopy(this._name, 0, arrayOfNameComponent, 0, this._name.length);
    System.arraycopy(paramArrayOfNameComponent, 0, arrayOfNameComponent, this._name.length, paramArrayOfNameComponent.length);
    return arrayOfNameComponent;
  }
  
  public String getNameInNamespace() throws NamingException { return (this._name == null || this._name.length == 0) ? "" : CNNameParser.cosNameToInsString(this._name); }
  
  private static boolean isCorbaUrl(String paramString) { return (paramString.startsWith("iiop://") || paramString.startsWith("iiopname://") || paramString.startsWith("corbaname:")); }
  
  private void initOrbAndRootContext(Hashtable<?, ?> paramHashtable) throws NamingException {
    ORB oRB = null;
    String str1 = null;
    if (oRB == null && paramHashtable != null)
      oRB = (ORB)paramHashtable.get("java.naming.corba.orb"); 
    if (oRB == null)
      oRB = getDefaultOrb(); 
    String str2 = null;
    if (paramHashtable != null)
      str2 = (String)paramHashtable.get("java.naming.provider.url"); 
    if (str2 != null && !isCorbaUrl(str2)) {
      str1 = getStringifiedIor(str2);
      setOrbAndRootContext(oRB, str1);
    } else if (str2 != null) {
      String str = initUsingUrl(oRB, str2, paramHashtable);
      if (str.length() > 0) {
        this._name = CNNameParser.nameToCosName(parser.parse(str));
        try {
          Object object = this._nc.resolve(this._name);
          this._nc = NamingContextHelper.narrow(object);
          if (this._nc == null)
            throw new ConfigurationException(str + " does not name a NamingContext"); 
        } catch (BAD_PARAM bAD_PARAM) {
          throw new ConfigurationException(str + " does not name a NamingContext");
        } catch (Exception exception) {
          throw ExceptionMapper.mapException(exception, this, this._name);
        } 
      } 
    } else {
      setOrbAndRootContext(oRB, (String)null);
    } 
  }
  
  private String initUsingUrl(ORB paramORB, String paramString, Hashtable<?, ?> paramHashtable) throws NamingException { return (paramString.startsWith("iiop://") || paramString.startsWith("iiopname://")) ? initUsingIiopUrl(paramORB, paramString, paramHashtable) : initUsingCorbanameUrl(paramORB, paramString, paramHashtable); }
  
  private String initUsingIiopUrl(ORB paramORB, String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramORB == null)
      paramORB = getDefaultOrb(); 
    try {
      IiopUrl iiopUrl = new IiopUrl(paramString);
      NamingException namingException = null;
      for (IiopUrl.Address address : iiopUrl.getAddresses()) {
        try {
          String str = "corbaloc:iiop:" + address.host + ":" + address.port + "/NameService";
          Object object = paramORB.string_to_object(str);
          setOrbAndRootContext(paramORB, object);
          return iiopUrl.getStringName();
        } catch (Exception exception) {
          setOrbAndRootContext(paramORB, (String)null);
          return iiopUrl.getStringName();
        } catch (NamingException namingException1) {
          namingException = namingException1;
        } 
      } 
      if (namingException != null)
        throw namingException; 
      throw new ConfigurationException("Problem with URL: " + paramString);
    } catch (MalformedURLException malformedURLException) {
      throw new ConfigurationException(malformedURLException.getMessage());
    } 
  }
  
  private String initUsingCorbanameUrl(ORB paramORB, String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramORB == null)
      paramORB = getDefaultOrb(); 
    try {
      CorbanameUrl corbanameUrl = new CorbanameUrl(paramString);
      String str1 = corbanameUrl.getLocation();
      String str2 = corbanameUrl.getStringName();
      setOrbAndRootContext(paramORB, str1);
      return corbanameUrl.getStringName();
    } catch (MalformedURLException malformedURLException) {
      throw new ConfigurationException(malformedURLException.getMessage());
    } 
  }
  
  private void setOrbAndRootContext(ORB paramORB, String paramString) throws NamingException {
    this._orb = paramORB;
    try {
      Object object;
      if (paramString != null) {
        object = this._orb.string_to_object(paramString);
      } else {
        object = this._orb.resolve_initial_references("NameService");
      } 
      this._nc = NamingContextHelper.narrow(object);
      if (this._nc == null) {
        if (paramString != null)
          throw new ConfigurationException("Cannot convert IOR to a NamingContext: " + paramString); 
        throw new ConfigurationException("ORB.resolve_initial_references(\"NameService\") does not return a NamingContext");
      } 
    } catch (InvalidName invalidName) {
      ConfigurationException configurationException = new ConfigurationException("COS Name Service not registered with ORB under the name 'NameService'");
      configurationException.setRootCause(invalidName);
      throw configurationException;
    } catch (COMM_FAILURE cOMM_FAILURE) {
      CommunicationException communicationException = new CommunicationException("Cannot connect to ORB");
      communicationException.setRootCause(cOMM_FAILURE);
      throw communicationException;
    } catch (BAD_PARAM bAD_PARAM) {
      ConfigurationException configurationException = new ConfigurationException("Invalid URL or IOR: " + paramString);
      configurationException.setRootCause(bAD_PARAM);
      throw configurationException;
    } catch (INV_OBJREF iNV_OBJREF) {
      ConfigurationException configurationException = new ConfigurationException("Invalid object reference: " + paramString);
      configurationException.setRootCause(iNV_OBJREF);
      throw configurationException;
    } 
  }
  
  private void setOrbAndRootContext(ORB paramORB, Object paramObject) throws NamingException {
    this._orb = paramORB;
    try {
      this._nc = NamingContextHelper.narrow(paramObject);
      if (this._nc == null)
        throw new ConfigurationException("Cannot convert object reference to NamingContext: " + paramObject); 
    } catch (COMM_FAILURE cOMM_FAILURE) {
      CommunicationException communicationException = new CommunicationException("Cannot connect to ORB");
      communicationException.setRootCause(cOMM_FAILURE);
      throw communicationException;
    } 
  }
  
  private String getStringifiedIor(String paramString) throws NamingException {
    if (paramString.startsWith("IOR:") || paramString.startsWith("corbaloc:"))
      return paramString; 
    inputStream = null;
    try {
      uRL = new URL(paramString);
      inputStream = uRL.openStream();
      if (inputStream != null) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "8859_1"));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
          if (str.startsWith("IOR:"))
            return str; 
        } 
      } 
    } catch (IOException iOException) {
      ConfigurationException configurationException = new ConfigurationException("Invalid URL: " + paramString);
      configurationException.setRootCause(iOException);
      throw configurationException;
    } finally {
      try {
        if (inputStream != null)
          inputStream.close(); 
      } catch (IOException iOException) {
        ConfigurationException configurationException = new ConfigurationException("Invalid URL: " + paramString);
        configurationException.setRootCause(iOException);
        throw configurationException;
      } 
    } 
    throw new ConfigurationException(paramString + " does not contain an IOR");
  }
  
  Object callResolve(NameComponent[] paramArrayOfNameComponent) throws NamingException {
    try {
      Object object = this._nc.resolve(paramArrayOfNameComponent);
      try {
        NamingContext namingContext = NamingContextHelper.narrow(object);
        return (namingContext != null) ? new CNCtx(this._orb, this.orbTracker, namingContext, this._env, makeFullName(paramArrayOfNameComponent)) : object;
      } catch (SystemException systemException) {
        return object;
      } 
    } catch (Exception exception) {
      throw ExceptionMapper.mapException(exception, this, paramArrayOfNameComponent);
    } 
  }
  
  public Object lookup(String paramString) throws NamingException { return lookup(new CompositeName(paramString)); }
  
  public Object lookup(Name paramName) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    if (paramName.size() == 0)
      return this; 
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    Object object = null;
    try {
      object = callResolve(arrayOfNameComponent);
      try {
        if (CorbaUtils.isObjectFactoryTrusted(object))
          object = NamingManager.getObjectInstance(object, paramName, this, this._env); 
      } catch (NamingException namingException) {
        throw namingException;
      } catch (Exception exception) {
        NamingException namingException = new NamingException("problem generating object using object factory");
        namingException.setRootCause(exception);
        throw namingException;
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = getContinuationContext(cannotProceedException);
      return context.lookup(cannotProceedException.getRemainingName());
    } 
    return object;
  }
  
  private void callBindOrRebind(NameComponent[] paramArrayOfNameComponent, Name paramName, Object paramObject, boolean paramBoolean) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    try {
      paramObject = NamingManager.getStateToBind(paramObject, paramName, this, this._env);
      if (paramObject instanceof CNCtx)
        paramObject = ((CNCtx)paramObject)._nc; 
      if (paramObject instanceof NamingContext) {
        NamingContext namingContext = NamingContextHelper.narrow((Object)paramObject);
        if (paramBoolean) {
          this._nc.rebind_context(paramArrayOfNameComponent, namingContext);
        } else {
          this._nc.bind_context(paramArrayOfNameComponent, namingContext);
        } 
      } else if (paramObject instanceof Object) {
        if (paramBoolean) {
          this._nc.rebind(paramArrayOfNameComponent, (Object)paramObject);
        } else {
          this._nc.bind(paramArrayOfNameComponent, (Object)paramObject);
        } 
      } else {
        throw new IllegalArgumentException("Only instances of org.omg.CORBA.Object can be bound");
      } 
    } catch (BAD_PARAM bAD_PARAM) {
      NotContextException notContextException = new NotContextException(paramName.toString());
      notContextException.setRootCause(bAD_PARAM);
      throw notContextException;
    } catch (Exception exception) {
      throw ExceptionMapper.mapException(exception, this, paramArrayOfNameComponent);
    } 
  }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    if (paramName.size() == 0)
      throw new InvalidNameException("Name is empty"); 
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try {
      callBindOrRebind(arrayOfNameComponent, paramName, paramObject, false);
    } catch (CannotProceedException cannotProceedException) {
      Context context = getContinuationContext(cannotProceedException);
      context.bind(cannotProceedException.getRemainingName(), paramObject);
    } 
  }
  
  private static Context getContinuationContext(CannotProceedException paramCannotProceedException) throws NamingException {
    try {
      return NamingManager.getContinuationContext(paramCannotProceedException);
    } catch (CannotProceedException cannotProceedException) {
      Object object = cannotProceedException.getResolvedObj();
      if (object instanceof Reference) {
        Reference reference = (Reference)object;
        RefAddr refAddr = reference.get("nns");
        if (refAddr.getContent() instanceof Context) {
          NameNotFoundException nameNotFoundException = new NameNotFoundException("No object reference bound for specified name");
          nameNotFoundException.setRootCause(paramCannotProceedException.getRootCause());
          nameNotFoundException.setRemainingName(paramCannotProceedException.getRemainingName());
          throw nameNotFoundException;
        } 
      } 
      throw cannotProceedException;
    } 
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException { bind(new CompositeName(paramString), paramObject); }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    if (paramName.size() == 0)
      throw new InvalidNameException("Name is empty"); 
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try {
      callBindOrRebind(arrayOfNameComponent, paramName, paramObject, true);
    } catch (CannotProceedException cannotProceedException) {
      Context context = getContinuationContext(cannotProceedException);
      context.rebind(cannotProceedException.getRemainingName(), paramObject);
    } 
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException { rebind(new CompositeName(paramString), paramObject); }
  
  private void callUnbind(NameComponent[] paramArrayOfNameComponent) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    try {
      this._nc.unbind(paramArrayOfNameComponent);
    } catch (NotFound notFound) {
      if (!leafNotFound(notFound, paramArrayOfNameComponent[paramArrayOfNameComponent.length - 1]))
        throw ExceptionMapper.mapException(notFound, this, paramArrayOfNameComponent); 
    } catch (Exception exception) {
      throw ExceptionMapper.mapException(exception, this, paramArrayOfNameComponent);
    } 
  }
  
  private boolean leafNotFound(NotFound paramNotFound, NameComponent paramNameComponent) {
    NameComponent nameComponent;
    return (paramNotFound.why.value() == 0 && paramNotFound.rest_of_name.length == 1 && (nameComponent = paramNotFound.rest_of_name[0]).id.equals(paramNameComponent.id) && (nameComponent.kind == paramNameComponent.kind || (nameComponent.kind != null && nameComponent.kind.equals(paramNameComponent.kind))));
  }
  
  public void unbind(String paramString) throws NamingException { unbind(new CompositeName(paramString)); }
  
  public void unbind(Name paramName) throws NamingException {
    if (paramName.size() == 0)
      throw new InvalidNameException("Name is empty"); 
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try {
      callUnbind(arrayOfNameComponent);
    } catch (CannotProceedException cannotProceedException) {
      Context context = getContinuationContext(cannotProceedException);
      context.unbind(cannotProceedException.getRemainingName());
    } 
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException { rename(new CompositeName(paramString1), new CompositeName(paramString2)); }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    if (paramName1.size() == 0 || paramName2.size() == 0)
      throw new InvalidNameException("One or both names empty"); 
    Object object = lookup(paramName1);
    bind(paramName2, object);
    unbind(paramName1);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException { return list(new CompositeName(paramString)); }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException { return listBindings(paramName); }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException { return listBindings(new CompositeName(paramString)); }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    if (paramName.size() > 0)
      try {
        Object object = lookup(paramName);
        if (object instanceof CNCtx)
          return new CNBindingEnumeration((CNCtx)object, true, this._env); 
        throw new NotContextException(paramName.toString());
      } catch (NamingException namingException) {
        throw namingException;
      } catch (BAD_PARAM bAD_PARAM) {
        NotContextException notContextException = new NotContextException(paramName.toString());
        notContextException.setRootCause(bAD_PARAM);
        throw notContextException;
      }  
    return new CNBindingEnumeration(this, false, this._env);
  }
  
  private void callDestroy(NamingContext paramNamingContext) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    try {
      paramNamingContext.destroy();
    } catch (Exception exception) {
      throw ExceptionMapper.mapException(exception, this, null);
    } 
  }
  
  public void destroySubcontext(String paramString) throws NamingException { destroySubcontext(new CompositeName(paramString)); }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    NamingContext namingContext = this._nc;
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    if (paramName.size() > 0)
      try {
        Context context = (Context)callResolve(arrayOfNameComponent);
        CNCtx cNCtx = (CNCtx)context;
        namingContext = cNCtx._nc;
        cNCtx.close();
      } catch (ClassCastException classCastException) {
        throw new NotContextException(paramName.toString());
      } catch (CannotProceedException cannotProceedException) {
        Context context = getContinuationContext(cannotProceedException);
        context.destroySubcontext(cannotProceedException.getRemainingName());
        return;
      } catch (NameNotFoundException nameNotFoundException) {
        if (nameNotFoundException.getRootCause() instanceof NotFound && leafNotFound((NotFound)nameNotFoundException.getRootCause(), arrayOfNameComponent[arrayOfNameComponent.length - 1]))
          return; 
        throw nameNotFoundException;
      } catch (NamingException namingException) {
        throw namingException;
      }  
    callDestroy(namingContext);
    callUnbind(arrayOfNameComponent);
  }
  
  private Context callBindNewContext(NameComponent[] paramArrayOfNameComponent) throws NamingException {
    if (this._nc == null)
      throw new ConfigurationException("Context does not have a corresponding NamingContext"); 
    try {
      NamingContext namingContext = this._nc.bind_new_context(paramArrayOfNameComponent);
      return new CNCtx(this._orb, this.orbTracker, namingContext, this._env, makeFullName(paramArrayOfNameComponent));
    } catch (Exception exception) {
      throw ExceptionMapper.mapException(exception, this, paramArrayOfNameComponent);
    } 
  }
  
  public Context createSubcontext(String paramString) throws NamingException { return createSubcontext(new CompositeName(paramString)); }
  
  public Context createSubcontext(Name paramName) throws NamingException {
    if (paramName.size() == 0)
      throw new InvalidNameException("Name is empty"); 
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try {
      return callBindNewContext(arrayOfNameComponent);
    } catch (CannotProceedException cannotProceedException) {
      Context context = getContinuationContext(cannotProceedException);
      return context.createSubcontext(cannotProceedException.getRemainingName());
    } 
  }
  
  public Object lookupLink(String paramString) throws NamingException { return lookupLink(new CompositeName(paramString)); }
  
  public Object lookupLink(Name paramName) throws NamingException { return lookup(paramName); }
  
  public NameParser getNameParser(String paramString) throws NamingException { return parser; }
  
  public NameParser getNameParser(Name paramName) throws NamingException { return parser; }
  
  public Hashtable<String, Object> getEnvironment() throws NamingException { return (this._env == null) ? new Hashtable(5, 0.75F) : (Hashtable)this._env.clone(); }
  
  public String composeName(String paramString1, String paramString2) throws NamingException { return composeName(new CompositeName(paramString1), new CompositeName(paramString2)).toString(); }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    Name name = (Name)paramName2.clone();
    return name.addAll(paramName1);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    if (this._env == null) {
      this._env = new Hashtable(7, 0.75F);
    } else {
      this._env = (Hashtable)this._env.clone();
    } 
    return this._env.put(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    if (this._env != null && this._env.get(paramString) != null) {
      this._env = (Hashtable)this._env.clone();
      return this._env.remove(paramString);
    } 
    return null;
  }
  
  public void incEnumCount() { this.enumCount++; }
  
  public void decEnumCount() {
    this.enumCount--;
    if (this.enumCount == 0 && this.isCloseCalled)
      close(); 
  }
  
  public void close() {
    if (this.enumCount > 0) {
      this.isCloseCalled = true;
      return;
    } 
  }
  
  protected void finalize() {
    try {
      close();
    } catch (NamingException namingException) {}
  }
  
  static  {
    PrivilegedAction privilegedAction = () -> System.getProperty("com.sun.jndi.cosnaming.object.trustURLCodebase", "false");
    String str = (String)AccessController.doPrivileged(privilegedAction);
    trustURLCodebase = "true".equalsIgnoreCase(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\CNCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */