package com.sun.jndi.url.rmi;

import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

public class rmiURLContextFactory implements ObjectFactory {
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramObject == null)
      return new rmiURLContext(paramHashtable); 
    if (paramObject instanceof String)
      return getUsingURL((String)paramObject, paramHashtable); 
    if (paramObject instanceof String[])
      return getUsingURLs((String[])paramObject, paramHashtable); 
    throw new ConfigurationException("rmiURLContextFactory.getObjectInstance: argument must be an RMI URL String or an array of them");
  }
  
  private static Object getUsingURL(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    rmiURLContext = new rmiURLContext(paramHashtable);
    try {
      return rmiURLContext.lookup(paramString);
    } finally {
      rmiURLContext.close();
    } 
  }
  
  private static Object getUsingURLs(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramArrayOfString.length == 0)
      throw new ConfigurationException("rmiURLContextFactory: empty URL array"); 
    rmiURLContext = new rmiURLContext(paramHashtable);
    try {
      NamingException namingException = null;
      byte b = 0;
      while (b < paramArrayOfString.length) {
        try {
          return rmiURLContext.lookup(paramArrayOfString[b]);
        } catch (NamingException namingException1) {
          namingException = namingException1;
          b++;
        } 
      } 
      throw namingException;
    } finally {
      rmiURLContext.close();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\rmi\rmiURLContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */