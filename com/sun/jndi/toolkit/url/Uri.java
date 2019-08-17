package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;

public class Uri {
  protected String uri;
  
  protected String scheme;
  
  protected String host = null;
  
  protected int port = -1;
  
  protected boolean hasAuthority;
  
  protected String path;
  
  protected String query = null;
  
  public Uri(String paramString) throws MalformedURLException { init(paramString); }
  
  protected Uri() {}
  
  protected void init(String paramString) throws MalformedURLException {
    this.uri = paramString;
    parse(paramString);
  }
  
  public String getScheme() { return this.scheme; }
  
  public String getHost() { return this.host; }
  
  public int getPort() { return this.port; }
  
  public String getPath() { return this.path; }
  
  public String getQuery() { return this.query; }
  
  public String toString() { return this.uri; }
  
  private void parse(String paramString) throws MalformedURLException {
    int i = paramString.indexOf(':');
    if (i < 0)
      throw new MalformedURLException("Invalid URI: " + paramString); 
    this.scheme = paramString.substring(0, i);
    this.hasAuthority = paramString.startsWith("//", ++i);
    if (this.hasAuthority) {
      i += 2;
      int k = paramString.indexOf('/', i);
      if (k < 0)
        k = paramString.length(); 
      if (paramString.startsWith("[", i)) {
        int m = paramString.indexOf(']', i + 1);
        if (m < 0 || m > k)
          throw new MalformedURLException("Invalid URI: " + paramString); 
        this.host = paramString.substring(i, m + 1);
        i = m + 1;
      } else {
        int m = paramString.indexOf(':', i);
        int n = (m < 0 || m > k) ? k : m;
        if (i < n)
          this.host = paramString.substring(i, n); 
        i = n;
      } 
      if (i + 1 < k && paramString.startsWith(":", i))
        this.port = Integer.parseInt(paramString.substring(++i, k)); 
      i = k;
    } 
    int j = paramString.indexOf('?', i);
    if (j < 0) {
      this.path = paramString.substring(i);
    } else {
      this.path = paramString.substring(i, j);
      this.query = paramString.substring(j);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolki\\url\Uri.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */