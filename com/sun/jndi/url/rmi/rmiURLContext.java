package com.sun.jndi.url.rmi;

import com.sun.jndi.rmi.registry.RegistryContext;
import com.sun.jndi.toolkit.url.GenericURLContext;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class rmiURLContext extends GenericURLContext {
  public rmiURLContext(Hashtable<?, ?> paramHashtable) { super(paramHashtable); }
  
  protected ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (!paramString.startsWith("rmi:"))
      throw new IllegalArgumentException("rmiURLContext: name is not an RMI URL: " + paramString); 
    String str1 = null;
    int i = -1;
    String str2 = null;
    int j = 4;
    if (paramString.startsWith("//", j)) {
      j += 2;
      int k = paramString.indexOf('/', j);
      if (k < 0)
        k = paramString.length(); 
      if (paramString.startsWith("[", j)) {
        int m = paramString.indexOf(']', j + 1);
        if (m < 0 || m > k)
          throw new IllegalArgumentException("rmiURLContext: name is an Invalid URL: " + paramString); 
        str1 = paramString.substring(j, m + 1);
        j = m + 1;
      } else {
        int m = paramString.indexOf(':', j);
        int n = (m < 0 || m > k) ? k : m;
        if (j < n)
          str1 = paramString.substring(j, n); 
        j = n;
      } 
      if (j + 1 < k)
        if (paramString.startsWith(":", j)) {
          i = Integer.parseInt(paramString.substring(++j, k));
        } else {
          throw new IllegalArgumentException("rmiURLContext: name is an Invalid URL: " + paramString);
        }  
      j = k;
    } 
    if ("".equals(str1))
      str1 = null; 
    if (paramString.startsWith("/", j))
      j++; 
    if (j < paramString.length())
      str2 = paramString.substring(j); 
    CompositeName compositeName = new CompositeName();
    if (str2 != null)
      compositeName.add(str2); 
    RegistryContext registryContext = new RegistryContext(str1, i, paramHashtable);
    return new ResolveResult(registryContext, compositeName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\rmi\rmiURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */