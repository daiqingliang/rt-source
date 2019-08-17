package sun.security.action;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class GetPropertyAction extends Object implements PrivilegedAction<String> {
  private String theProp;
  
  private String defaultVal;
  
  public GetPropertyAction(String paramString) { this.theProp = paramString; }
  
  public GetPropertyAction(String paramString1, String paramString2) {
    this.theProp = paramString1;
    this.defaultVal = paramString2;
  }
  
  public String run() {
    String str = System.getProperty(this.theProp);
    return (str == null) ? this.defaultVal : str;
  }
  
  public static String privilegedGetProperty(String paramString) { return (System.getSecurityManager() == null) ? System.getProperty(paramString) : (String)AccessController.doPrivileged(new GetPropertyAction(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */