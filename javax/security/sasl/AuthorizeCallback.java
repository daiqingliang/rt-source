package javax.security.sasl;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class AuthorizeCallback implements Callback, Serializable {
  private String authenticationID;
  
  private String authorizationID;
  
  private String authorizedID;
  
  private boolean authorized;
  
  private static final long serialVersionUID = -2353344186490470805L;
  
  public AuthorizeCallback(String paramString1, String paramString2) {
    this.authenticationID = paramString1;
    this.authorizationID = paramString2;
  }
  
  public String getAuthenticationID() { return this.authenticationID; }
  
  public String getAuthorizationID() { return this.authorizationID; }
  
  public boolean isAuthorized() { return this.authorized; }
  
  public void setAuthorized(boolean paramBoolean) { this.authorized = paramBoolean; }
  
  public String getAuthorizedID() { return !this.authorized ? null : ((this.authorizedID == null) ? this.authorizationID : this.authorizedID); }
  
  public void setAuthorizedID(String paramString) { this.authorizedID = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\sasl\AuthorizeCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */