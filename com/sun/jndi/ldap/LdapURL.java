package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import javax.naming.NamingException;

public final class LdapURL extends Uri {
  private boolean useSsl = false;
  
  private String DN = null;
  
  private String attributes = null;
  
  private String scope = null;
  
  private String filter = null;
  
  private String extensions = null;
  
  public LdapURL(String paramString) throws NamingException {
    try {
      init(paramString);
      this.useSsl = this.scheme.equalsIgnoreCase("ldaps");
      if (!this.scheme.equalsIgnoreCase("ldap") && !this.useSsl)
        throw new MalformedURLException("Not an LDAP URL: " + paramString); 
      parsePathAndQuery();
    } catch (MalformedURLException malformedURLException) {
      NamingException namingException = new NamingException("Cannot parse url: " + paramString);
      namingException.setRootCause(malformedURLException);
      throw namingException;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      NamingException namingException = new NamingException("Cannot parse url: " + paramString);
      namingException.setRootCause(unsupportedEncodingException);
      throw namingException;
    } 
  }
  
  public boolean useSsl() { return this.useSsl; }
  
  public String getDN() { return this.DN; }
  
  public String getAttributes() { return this.attributes; }
  
  public String getScope() { return this.scope; }
  
  public String getFilter() { return this.filter; }
  
  public String getExtensions() { return this.extensions; }
  
  public static String[] fromList(String paramString) throws NamingException {
    String[] arrayOfString1 = new String[(paramString.length() + 1) / 2];
    byte b = 0;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, " ");
    while (stringTokenizer.hasMoreTokens())
      arrayOfString1[b++] = stringTokenizer.nextToken(); 
    String[] arrayOfString2 = new String[b];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, b);
    return arrayOfString2;
  }
  
  public static boolean hasQueryComponents(String paramString) { return (paramString.lastIndexOf('?') != -1); }
  
  static String toUrlString(String paramString1, int paramInt, String paramString2, boolean paramBoolean) {
    try {
      String str1 = (paramString1 != null) ? paramString1 : "";
      if (str1.indexOf(':') != -1 && str1.charAt(0) != '[')
        str1 = "[" + str1 + "]"; 
      String str2 = (paramInt != -1) ? (":" + paramInt) : "";
      String str3 = (paramString2 != null) ? ("/" + UrlUtil.encode(paramString2, "UTF8")) : "";
      return paramBoolean ? ("ldaps://" + str1 + str2 + str3) : ("ldap://" + str1 + str2 + str3);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new IllegalStateException("UTF-8 encoding unavailable");
    } 
  }
  
  private void parsePathAndQuery() throws MalformedURLException, UnsupportedEncodingException {
    if (this.path.equals(""))
      return; 
    this.DN = this.path.startsWith("/") ? this.path.substring(1) : this.path;
    if (this.DN.length() > 0)
      this.DN = UrlUtil.decode(this.DN, "UTF8"); 
    if (this.query == null || this.query.length() < 2)
      return; 
    int i = 1;
    int j = this.query.indexOf('?', i);
    int k = (j == -1) ? this.query.length() : j;
    if (k - i > 0)
      this.attributes = this.query.substring(i, k); 
    i = k + 1;
    if (i >= this.query.length())
      return; 
    j = this.query.indexOf('?', i);
    k = (j == -1) ? this.query.length() : j;
    if (k - i > 0)
      this.scope = this.query.substring(i, k); 
    i = k + 1;
    if (i >= this.query.length())
      return; 
    j = this.query.indexOf('?', i);
    k = (j == -1) ? this.query.length() : j;
    if (k - i > 0) {
      this.filter = this.query.substring(i, k);
      this.filter = UrlUtil.decode(this.filter, "UTF8");
    } 
    i = k + 1;
    if (i >= this.query.length())
      return; 
    if (this.query.length() - i > 0) {
      this.extensions = this.query.substring(i);
      this.extensions = UrlUtil.decode(this.extensions, "UTF8");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */