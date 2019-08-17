package com.sun.jndi.rmi.registry;

import com.sun.jndi.url.rmi.rmiURLContextFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public class RegistryContextFactory implements ObjectFactory, InitialContextFactory {
  public static final String ADDRESS_TYPE = "URL";
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramHashtable != null)
      paramHashtable = (Hashtable)paramHashtable.clone(); 
    return URLToContext(getInitCtxURL(paramHashtable), paramHashtable);
  }
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (!isRegistryRef(paramObject))
      return null; 
    Object object = URLsToObject(getURLs((Reference)paramObject), paramHashtable);
    if (object instanceof RegistryContext) {
      RegistryContext registryContext = (RegistryContext)object;
      registryContext.reference = (Reference)paramObject;
    } 
    return object;
  }
  
  private static Context URLToContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    rmiURLContextFactory rmiURLContextFactory = new rmiURLContextFactory();
    Object object = rmiURLContextFactory.getObjectInstance(paramString, null, null, paramHashtable);
    if (object instanceof Context)
      return (Context)object; 
    throw new NotContextException(paramString);
  }
  
  private static Object URLsToObject(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable) throws NamingException {
    rmiURLContextFactory rmiURLContextFactory = new rmiURLContextFactory();
    return rmiURLContextFactory.getObjectInstance(paramArrayOfString, null, null, paramHashtable);
  }
  
  private static String getInitCtxURL(Hashtable<?, ?> paramHashtable) {
    String str = null;
    if (paramHashtable != null)
      str = (String)paramHashtable.get("java.naming.provider.url"); 
    return (str != null) ? str : "rmi:";
  }
  
  private static boolean isRegistryRef(Object paramObject) {
    if (!(paramObject instanceof Reference))
      return false; 
    String str = RegistryContextFactory.class.getName();
    Reference reference = (Reference)paramObject;
    return str.equals(reference.getFactoryClassName());
  }
  
  private static String[] getURLs(Reference paramReference) throws NamingException {
    byte b = 0;
    String[] arrayOfString1 = new String[paramReference.size()];
    Enumeration enumeration = paramReference.getAll();
    while (enumeration.hasMoreElements()) {
      RefAddr refAddr = (RefAddr)enumeration.nextElement();
      if (refAddr instanceof javax.naming.StringRefAddr && refAddr.getType().equals("URL"))
        arrayOfString1[b++] = (String)refAddr.getContent(); 
    } 
    if (b == 0)
      throw new ConfigurationException("Reference contains no valid addresses"); 
    if (b == paramReference.size())
      return arrayOfString1; 
    String[] arrayOfString2 = new String[b];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, b);
    return arrayOfString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\rmi\registry\RegistryContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */