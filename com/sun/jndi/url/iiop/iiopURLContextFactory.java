package com.sun.jndi.url.iiop;

import com.sun.jndi.cosnaming.CNCtx;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ResolveResult;

public class iiopURLContextFactory implements ObjectFactory {
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception {
    if (paramObject == null)
      return new iiopURLContext(paramHashtable); 
    if (paramObject instanceof String)
      return getUsingURL((String)paramObject, paramHashtable); 
    if (paramObject instanceof String[])
      return getUsingURLs((String[])paramObject, paramHashtable); 
    throw new IllegalArgumentException("iiopURLContextFactory.getObjectInstance: argument must be a URL String or array of URLs");
  }
  
  static ResolveResult getUsingURLIgnoreRest(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException { return CNCtx.createUsingURL(paramString, paramHashtable); }
  
  private static Object getUsingURL(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    ResolveResult resolveResult = getUsingURLIgnoreRest(paramString, paramHashtable);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.lookup(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  private static Object getUsingURLs(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      try {
        Object object = getUsingURL(str, paramHashtable);
        if (object != null)
          return object; 
      } catch (NamingException namingException) {}
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\iiop\iiopURLContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */