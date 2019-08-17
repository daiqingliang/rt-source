package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import sun.net.dns.ResolverConfiguration;

public class DnsContextFactory implements InitialContextFactory {
  private static final String DEFAULT_URL = "dns:";
  
  private static final int DEFAULT_PORT = 53;
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramHashtable == null)
      paramHashtable = new Hashtable<?, ?>(5); 
    return urlToContext(getInitCtxUrl(paramHashtable), paramHashtable);
  }
  
  public static DnsContext getContext(String paramString, String[] paramArrayOfString, Hashtable<?, ?> paramHashtable) throws NamingException { return new DnsContext(paramString, paramArrayOfString, paramHashtable); }
  
  public static DnsContext getContext(String paramString, DnsUrl[] paramArrayOfDnsUrl, Hashtable<?, ?> paramHashtable) throws NamingException {
    String[] arrayOfString = serversForUrls(paramArrayOfDnsUrl);
    DnsContext dnsContext = getContext(paramString, arrayOfString, paramHashtable);
    if (platformServersUsed(paramArrayOfDnsUrl))
      dnsContext.setProviderUrl(constructProviderUrl(paramString, arrayOfString)); 
    return dnsContext;
  }
  
  public static boolean platformServersAvailable() { return !filterNameServers(ResolverConfiguration.open().nameservers(), true).isEmpty(); }
  
  private static Context urlToContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    DnsUrl[] arrayOfDnsUrl;
    try {
      arrayOfDnsUrl = DnsUrl.fromList(paramString);
    } catch (MalformedURLException malformedURLException) {
      throw new ConfigurationException(malformedURLException.getMessage());
    } 
    if (arrayOfDnsUrl.length == 0)
      throw new ConfigurationException("Invalid DNS pseudo-URL(s): " + paramString); 
    String str = arrayOfDnsUrl[0].getDomain();
    for (byte b = 1; b < arrayOfDnsUrl.length; b++) {
      if (!str.equalsIgnoreCase(arrayOfDnsUrl[b].getDomain()))
        throw new ConfigurationException("Conflicting domains: " + paramString); 
    } 
    return getContext(str, arrayOfDnsUrl, paramHashtable);
  }
  
  private static String[] serversForUrls(DnsUrl[] paramArrayOfDnsUrl) throws NamingException {
    if (paramArrayOfDnsUrl.length == 0)
      throw new ConfigurationException("DNS pseudo-URL required"); 
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < paramArrayOfDnsUrl.length; b++) {
      String str = paramArrayOfDnsUrl[b].getHost();
      int i = paramArrayOfDnsUrl[b].getPort();
      if (str == null && i < 0) {
        List list = filterNameServers(ResolverConfiguration.open().nameservers(), false);
        if (!list.isEmpty()) {
          arrayList.addAll(list);
          continue;
        } 
      } 
      if (str == null)
        str = "localhost"; 
      arrayList.add((i < 0) ? str : (str + ":" + i));
      continue;
    } 
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  private static boolean platformServersUsed(DnsUrl[] paramArrayOfDnsUrl) {
    if (!platformServersAvailable())
      return false; 
    for (byte b = 0; b < paramArrayOfDnsUrl.length; b++) {
      if (paramArrayOfDnsUrl[b].getHost() == null && paramArrayOfDnsUrl[b].getPort() < 0)
        return true; 
    } 
    return false;
  }
  
  private static String constructProviderUrl(String paramString, String[] paramArrayOfString) {
    String str = "";
    if (!paramString.equals("."))
      try {
        str = "/" + UrlUtil.encode(paramString, "ISO-8859-1");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {} 
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (b)
        stringBuffer.append(' '); 
      stringBuffer.append("dns://").append(paramArrayOfString[b]).append(str);
    } 
    return stringBuffer.toString();
  }
  
  private static String getInitCtxUrl(Hashtable<?, ?> paramHashtable) {
    String str = (String)paramHashtable.get("java.naming.provider.url");
    return (str != null) ? str : "dns:";
  }
  
  private static List<String> filterNameServers(List<String> paramList, boolean paramBoolean) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null || paramList == null || paramList.isEmpty())
      return paramList; 
    ArrayList arrayList = new ArrayList();
    for (String str1 : paramList) {
      int i = str1.indexOf(':', str1.indexOf(']') + 1);
      byte b = (i < 0) ? 53 : Integer.parseInt(str1.substring(i + 1));
      String str2 = (i < 0) ? str1 : str1.substring(0, i);
      try {
        securityManager.checkConnect(str2, b);
        arrayList.add(str1);
        if (paramBoolean)
          return arrayList; 
      } catch (SecurityException securityException) {}
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\DnsContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */