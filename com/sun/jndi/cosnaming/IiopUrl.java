package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Name;
import javax.naming.NamingException;

public final class IiopUrl {
  private static final int DEFAULT_IIOPNAME_PORT = 9999;
  
  private static final int DEFAULT_IIOP_PORT = 900;
  
  private static final String DEFAULT_HOST = "localhost";
  
  private Vector<Address> addresses;
  
  private String stringName;
  
  public Vector<Address> getAddresses() { return this.addresses; }
  
  public String getStringName() { return this.stringName; }
  
  public Name getCosName() throws NamingException { return CNCtx.parser.parse(this.stringName); }
  
  public IiopUrl(String paramString) throws MalformedURLException {
    if (paramString.startsWith("iiopname://")) {
      bool = false;
      b = 11;
    } else if (paramString.startsWith("iiop://")) {
      bool = true;
      b = 7;
    } else {
      throw new MalformedURLException("Invalid iiop/iiopname URL: " + paramString);
    } 
    int i = paramString.indexOf('/', b);
    if (i < 0) {
      i = paramString.length();
      this.stringName = "";
    } else {
      this.stringName = UrlUtil.decode(paramString.substring(i + 1));
    } 
    this.addresses = new Vector(3);
    if (bool) {
      this.addresses.addElement(new Address(paramString.substring(b, i), bool));
    } else {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString.substring(b, i), ",");
      while (stringTokenizer.hasMoreTokens())
        this.addresses.addElement(new Address(stringTokenizer.nextToken(), bool)); 
      if (this.addresses.size() == 0)
        this.addresses.addElement(new Address("", bool)); 
    } 
  }
  
  public static class Address {
    public int port = -1;
    
    public int major;
    
    public int minor;
    
    public String host;
    
    public Address(String param1String, boolean param1Boolean) throws MalformedURLException {
      int j;
      if (param1Boolean || (j = param1String.indexOf('@')) < 0) {
        this.major = 1;
        this.minor = 0;
        i = 0;
      } else {
        int m = param1String.indexOf('.');
        if (m < 0)
          throw new MalformedURLException("invalid version: " + param1String); 
        try {
          this.major = Integer.parseInt(param1String.substring(0, m));
          this.minor = Integer.parseInt(param1String.substring(m + 1, j));
        } catch (NumberFormatException numberFormatException) {
          throw new MalformedURLException("Nonnumeric version: " + param1String);
        } 
        i = j + 1;
      } 
      int k = param1String.indexOf('/', i);
      if (k < 0)
        k = param1String.length(); 
      if (param1String.startsWith("[", i)) {
        int m = param1String.indexOf(']', i + 1);
        if (m < 0 || m > k)
          throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + param1String); 
        this.host = param1String.substring(i, m + 1);
        i = m + 1;
      } else {
        int m = param1String.indexOf(':', i);
        int n = (m < 0 || m > k) ? k : m;
        if (i < n)
          this.host = param1String.substring(i, n); 
        i = n;
      } 
      if (i + 1 < k)
        if (param1String.startsWith(":", i)) {
          this.port = Integer.parseInt(param1String.substring(++i, k));
        } else {
          throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + param1String);
        }  
      int i = k;
      if ("".equals(this.host) || this.host == null)
        this.host = "localhost"; 
      if (this.port == -1)
        this.port = param1Boolean ? 900 : 9999; 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\IiopUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */