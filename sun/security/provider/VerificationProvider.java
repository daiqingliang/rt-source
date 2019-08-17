package sun.security.provider;

import java.security.AccessController;
import java.security.Provider;
import java.util.LinkedHashMap;
import sun.security.action.PutAllAction;
import sun.security.rsa.SunRsaSignEntries;

public final class VerificationProvider extends Provider {
  private static final long serialVersionUID = 7482667077568930381L;
  
  private static final boolean ACTIVE;
  
  public VerificationProvider() {
    super("SunJarVerification", 1.8D, "Jar Verification Provider");
    if (!ACTIVE)
      return; 
    if (System.getSecurityManager() == null) {
      SunEntries.putEntries(this);
      SunRsaSignEntries.putEntries(this);
    } else {
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      SunEntries.putEntries(linkedHashMap);
      SunRsaSignEntries.putEntries(linkedHashMap);
      AccessController.doPrivileged(new PutAllAction(this, linkedHashMap));
    } 
  }
  
  static  {
    boolean bool;
    try {
      Class.forName("sun.security.provider.Sun").forName("sun.security.rsa.SunRsaSign");
      bool = false;
    } catch (ClassNotFoundException classNotFoundException) {
      bool = true;
    } 
    ACTIVE = bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\VerificationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */