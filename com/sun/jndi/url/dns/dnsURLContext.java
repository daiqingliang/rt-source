package com.sun.jndi.url.dns;

import com.sun.jndi.dns.DnsContextFactory;
import com.sun.jndi.dns.DnsUrl;
import com.sun.jndi.toolkit.url.GenericURLDirContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class dnsURLContext extends GenericURLDirContext {
  public dnsURLContext(Hashtable<?, ?> paramHashtable) { super(paramHashtable); }
  
  protected ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    DnsUrl dnsUrl;
    try {
      dnsUrl = new DnsUrl(paramString);
    } catch (MalformedURLException malformedURLException) {
      throw new InvalidNameException(malformedURLException.getMessage());
    } 
    DnsUrl[] arrayOfDnsUrl = { dnsUrl };
    String str = dnsUrl.getDomain();
    return new ResolveResult(DnsContextFactory.getContext(".", arrayOfDnsUrl, paramHashtable), (new CompositeName()).add(str));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\dns\dnsURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */