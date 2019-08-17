package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import com.sun.naming.internal.VersionHelper;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;

public class NamingManager {
  static final VersionHelper helper = VersionHelper.getVersionHelper();
  
  private static ObjectFactoryBuilder object_factory_builder = null;
  
  private static final String defaultPkgPrefix = "com.sun.jndi.url";
  
  private static InitialContextFactoryBuilder initctx_factory_builder = null;
  
  public static final String CPE = "java.naming.spi.CannotProceedException";
  
  public static void setObjectFactoryBuilder(ObjectFactoryBuilder paramObjectFactoryBuilder) throws NamingException {
    if (object_factory_builder != null)
      throw new IllegalStateException("ObjectFactoryBuilder already set"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    object_factory_builder = paramObjectFactoryBuilder;
  }
  
  static ObjectFactoryBuilder getObjectFactoryBuilder() { return object_factory_builder; }
  
  static ObjectFactory getObjectFactoryFromReference(Reference paramReference, String paramString) throws IllegalAccessException, InstantiationException, MalformedURLException {
    Class clazz = null;
    try {
      clazz = helper.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {}
    String str;
    if (clazz == null && (str = paramReference.getFactoryClassLocation()) != null)
      try {
        clazz = helper.loadClass(paramString, str);
      } catch (ClassNotFoundException classNotFoundException) {} 
    return (clazz != null) ? (ObjectFactory)clazz.newInstance() : null;
  }
  
  private static Object createObjectFromFactories(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception {
    FactoryEnumeration factoryEnumeration = ResourceManager.getFactories("java.naming.factory.object", paramHashtable, paramContext);
    if (factoryEnumeration == null)
      return null; 
    Object object;
    for (object = null; object == null && factoryEnumeration.hasMore(); object = objectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable))
      ObjectFactory objectFactory = (ObjectFactory)factoryEnumeration.next(); 
    return object;
  }
  
  private static String getURLScheme(String paramString) {
    int i = paramString.indexOf(':');
    int j = paramString.indexOf('/');
    return (i > 0 && (j == -1 || i < j)) ? paramString.substring(0, i) : null;
  }
  
  public static Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception {
    ObjectFactoryBuilder objectFactoryBuilder = getObjectFactoryBuilder();
    if (objectFactoryBuilder != null) {
      ObjectFactory objectFactory = objectFactoryBuilder.createObjectFactory(paramObject, paramHashtable);
      return objectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    } 
    Reference reference = null;
    if (paramObject instanceof Reference) {
      reference = (Reference)paramObject;
    } else if (paramObject instanceof Referenceable) {
      reference = ((Referenceable)paramObject).getReference();
    } 
    if (reference != null) {
      String str = reference.getFactoryClassName();
      if (str != null) {
        ObjectFactory objectFactory = getObjectFactoryFromReference(reference, str);
        return (objectFactory != null) ? objectFactory.getObjectInstance(reference, paramName, paramContext, paramHashtable) : paramObject;
      } 
      Object object1 = processURLAddrs(reference, paramName, paramContext, paramHashtable);
      if (object1 != null)
        return object1; 
    } 
    Object object = createObjectFromFactories(paramObject, paramName, paramContext, paramHashtable);
    return (object != null) ? object : paramObject;
  }
  
  static Object processURLAddrs(Reference paramReference, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    for (byte b = 0; b < paramReference.size(); b++) {
      RefAddr refAddr = paramReference.get(b);
      if (refAddr instanceof javax.naming.StringRefAddr && refAddr.getType().equalsIgnoreCase("URL")) {
        String str = (String)refAddr.getContent();
        Object object = processURL(str, paramName, paramContext, paramHashtable);
        if (object != null)
          return object; 
      } 
    } 
    return null;
  }
  
  private static Object processURL(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception {
    if (paramObject instanceof String) {
      String str1 = (String)paramObject;
      String str2 = getURLScheme(str1);
      if (str2 != null) {
        Object object = getURLObject(str2, paramObject, paramName, paramContext, paramHashtable);
        if (object != null)
          return object; 
      } 
    } 
    if (paramObject instanceof String[]) {
      String[] arrayOfString = (String[])paramObject;
      for (byte b = 0; b < arrayOfString.length; b++) {
        String str = getURLScheme(arrayOfString[b]);
        if (str != null) {
          Object object = getURLObject(str, paramObject, paramName, paramContext, paramHashtable);
          if (object != null)
            return object; 
        } 
      } 
    } 
    return null;
  }
  
  static Context getContext(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    Object object;
    if (paramObject instanceof Context)
      return (Context)paramObject; 
    try {
      object = getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    } catch (NamingException namingException) {
      throw namingException;
    } catch (Exception exception) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(exception);
      throw namingException;
    } 
    return (object instanceof Context) ? (Context)object : null;
  }
  
  static Resolver getResolver(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    Object object;
    if (paramObject instanceof Resolver)
      return (Resolver)paramObject; 
    try {
      object = getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    } catch (NamingException namingException) {
      throw namingException;
    } catch (Exception exception) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(exception);
      throw namingException;
    } 
    return (object instanceof Resolver) ? (Resolver)object : null;
  }
  
  public static Context getURLContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    Object object = getURLObject(paramString, null, null, null, paramHashtable);
    return (object instanceof Context) ? (Context)object : null;
  }
  
  private static Object getURLObject(String paramString, Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    ObjectFactory objectFactory = (ObjectFactory)ResourceManager.getFactory("java.naming.factory.url.pkgs", paramHashtable, paramContext, "." + paramString + "." + paramString + "URLContextFactory", "com.sun.jndi.url");
    if (objectFactory == null)
      return null; 
    try {
      return objectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    } catch (NamingException namingException) {
      throw namingException;
    } catch (Exception exception) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(exception);
      throw namingException;
    } 
  }
  
  private static InitialContextFactoryBuilder getInitialContextFactoryBuilder() { return initctx_factory_builder; }
  
  public static Context getInitialContext(Hashtable<?, ?> paramHashtable) throws NamingException {
    InitialContextFactory initialContextFactory;
    InitialContextFactoryBuilder initialContextFactoryBuilder = getInitialContextFactoryBuilder();
    if (initialContextFactoryBuilder == null) {
      String str = (paramHashtable != null) ? (String)paramHashtable.get("java.naming.factory.initial") : null;
      if (str == null) {
        NoInitialContextException noInitialContextException = new NoInitialContextException("Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial");
        throw noInitialContextException;
      } 
      try {
        initialContextFactory = (InitialContextFactory)helper.loadClass(str).newInstance();
      } catch (Exception exception) {
        NoInitialContextException noInitialContextException = new NoInitialContextException("Cannot instantiate class: " + str);
        noInitialContextException.setRootCause(exception);
        throw noInitialContextException;
      } 
    } else {
      initialContextFactory = initialContextFactoryBuilder.createInitialContextFactory(paramHashtable);
    } 
    return initialContextFactory.getInitialContext(paramHashtable);
  }
  
  public static void setInitialContextFactoryBuilder(InitialContextFactoryBuilder paramInitialContextFactoryBuilder) throws NamingException {
    if (initctx_factory_builder != null)
      throw new IllegalStateException("InitialContextFactoryBuilder already set"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    initctx_factory_builder = paramInitialContextFactoryBuilder;
  }
  
  public static boolean hasInitialContextFactoryBuilder() { return (getInitialContextFactoryBuilder() != null); }
  
  public static Context getContinuationContext(CannotProceedException paramCannotProceedException) throws NamingException {
    Hashtable hashtable = paramCannotProceedException.getEnvironment();
    if (hashtable == null) {
      hashtable = new Hashtable(7);
    } else {
      hashtable = (Hashtable)hashtable.clone();
    } 
    hashtable.put("java.naming.spi.CannotProceedException", paramCannotProceedException);
    ContinuationContext continuationContext = new ContinuationContext(paramCannotProceedException, hashtable);
    return continuationContext.getTargetContext();
  }
  
  public static Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception {
    FactoryEnumeration factoryEnumeration = ResourceManager.getFactories("java.naming.factory.state", paramHashtable, paramContext);
    if (factoryEnumeration == null)
      return paramObject; 
    Object object;
    for (object = null; object == null && factoryEnumeration.hasMore(); object = stateFactory.getStateToBind(paramObject, paramName, paramContext, paramHashtable))
      StateFactory stateFactory = (StateFactory)factoryEnumeration.next(); 
    return (object != null) ? object : paramObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\NamingManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */