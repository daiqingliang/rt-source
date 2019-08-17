package com.sun.jndi.url.iiop;

import com.sun.jndi.cosnaming.CorbanameUrl;
import com.sun.jndi.cosnaming.IiopUrl;
import com.sun.jndi.toolkit.url.GenericURLContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class iiopURLContext extends GenericURLContext {
  iiopURLContext(Hashtable<?, ?> paramHashtable) { super(paramHashtable); }
  
  protected ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException { return iiopURLContextFactory.getUsingURLIgnoreRest(paramString, paramHashtable); }
  
  protected Name getURLSuffix(String paramString1, String paramString2) throws NamingException {
    try {
      if (paramString2.startsWith("iiop://") || paramString2.startsWith("iiopname://")) {
        IiopUrl iiopUrl = new IiopUrl(paramString2);
        return iiopUrl.getCosName();
      } 
      if (paramString2.startsWith("corbaname:")) {
        CorbanameUrl corbanameUrl = new CorbanameUrl(paramString2);
        return corbanameUrl.getCosName();
      } 
      throw new MalformedURLException("Not a valid URL: " + paramString2);
    } catch (MalformedURLException malformedURLException) {
      throw new InvalidNameException(malformedURLException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\iiop\iiopURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */