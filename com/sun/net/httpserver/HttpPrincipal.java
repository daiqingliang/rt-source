package com.sun.net.httpserver;

import java.security.Principal;
import jdk.Exported;

@Exported
public class HttpPrincipal implements Principal {
  private String username;
  
  private String realm;
  
  public HttpPrincipal(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    this.username = paramString1;
    this.realm = paramString2;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof HttpPrincipal))
      return false; 
    HttpPrincipal httpPrincipal = (HttpPrincipal)paramObject;
    return (this.username.equals(httpPrincipal.username) && this.realm.equals(httpPrincipal.realm));
  }
  
  public String getName() { return this.username; }
  
  public String getUsername() { return this.username; }
  
  public String getRealm() { return this.realm; }
  
  public int hashCode() { return (this.username + this.realm).hashCode(); }
  
  public String toString() { return getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\HttpPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */