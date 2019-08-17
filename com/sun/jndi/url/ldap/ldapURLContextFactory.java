package com.sun.jndi.url.ldap;

import com.sun.jndi.ldap.LdapCtx;
import com.sun.jndi.ldap.LdapCtxFactory;
import com.sun.jndi.ldap.LdapURL;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ResolveResult;

public class ldapURLContextFactory implements ObjectFactory {
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception { return (paramObject == null) ? new ldapURLContext(paramHashtable) : LdapCtxFactory.getLdapCtxInstance(paramObject, paramHashtable); }
  
  static ResolveResult getUsingURLIgnoreRootDN(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    LdapURL ldapURL = new LdapURL(paramString);
    LdapCtx ldapCtx = new LdapCtx("", ldapURL.getHost(), ldapURL.getPort(), paramHashtable, ldapURL.useSsl());
    String str = (ldapURL.getDN() != null) ? ldapURL.getDN() : "";
    CompositeName compositeName = new CompositeName();
    if (!"".equals(str))
      compositeName.add(str); 
    return new ResolveResult(ldapCtx, compositeName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\ldap\ldapURLContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */