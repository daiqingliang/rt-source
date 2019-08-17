package com.sun.net.httpserver;

import java.util.Base64;
import jdk.Exported;

@Exported
public abstract class BasicAuthenticator extends Authenticator {
  protected String realm;
  
  public BasicAuthenticator(String paramString) { this.realm = paramString; }
  
  public String getRealm() { return this.realm; }
  
  public Authenticator.Result authenticate(HttpExchange paramHttpExchange) {
    Headers headers1 = paramHttpExchange.getRequestHeaders();
    String str1 = headers1.getFirst("Authorization");
    if (str1 == null) {
      Headers headers = paramHttpExchange.getResponseHeaders();
      headers.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
      return new Authenticator.Retry(401);
    } 
    int i = str1.indexOf(' ');
    if (i == -1 || !str1.substring(0, i).equals("Basic"))
      return new Authenticator.Failure(401); 
    byte[] arrayOfByte = Base64.getDecoder().decode(str1.substring(i + 1));
    String str2 = new String(arrayOfByte);
    int j = str2.indexOf(':');
    String str3 = str2.substring(0, j);
    String str4 = str2.substring(j + 1);
    if (checkCredentials(str3, str4))
      return new Authenticator.Success(new HttpPrincipal(str3, this.realm)); 
    Headers headers2 = paramHttpExchange.getResponseHeaders();
    headers2.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
    return new Authenticator.Failure(401);
  }
  
  public abstract boolean checkCredentials(String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\BasicAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */