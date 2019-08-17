package javax.security.auth.login;

import java.util.Collections;
import java.util.Map;
import sun.security.util.ResourcesMgr;

public class AppConfigurationEntry {
  private String loginModuleName;
  
  private LoginModuleControlFlag controlFlag;
  
  private Map<String, ?> options;
  
  public AppConfigurationEntry(String paramString, LoginModuleControlFlag paramLoginModuleControlFlag, Map<String, ?> paramMap) {
    if (paramString == null || paramString.length() == 0 || (paramLoginModuleControlFlag != LoginModuleControlFlag.REQUIRED && paramLoginModuleControlFlag != LoginModuleControlFlag.REQUISITE && paramLoginModuleControlFlag != LoginModuleControlFlag.SUFFICIENT && paramLoginModuleControlFlag != LoginModuleControlFlag.OPTIONAL) || paramMap == null)
      throw new IllegalArgumentException(); 
    this.loginModuleName = paramString;
    this.controlFlag = paramLoginModuleControlFlag;
    this.options = Collections.unmodifiableMap(paramMap);
  }
  
  public String getLoginModuleName() { return this.loginModuleName; }
  
  public LoginModuleControlFlag getControlFlag() { return this.controlFlag; }
  
  public Map<String, ?> getOptions() { return this.options; }
  
  public static class LoginModuleControlFlag {
    private String controlFlag;
    
    public static final LoginModuleControlFlag REQUIRED = new LoginModuleControlFlag("required");
    
    public static final LoginModuleControlFlag REQUISITE = new LoginModuleControlFlag("requisite");
    
    public static final LoginModuleControlFlag SUFFICIENT = new LoginModuleControlFlag("sufficient");
    
    public static final LoginModuleControlFlag OPTIONAL = new LoginModuleControlFlag("optional");
    
    private LoginModuleControlFlag(String param1String) { this.controlFlag = param1String; }
    
    public String toString() { return ResourcesMgr.getString("LoginModuleControlFlag.") + this.controlFlag; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\login\AppConfigurationEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */