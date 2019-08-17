package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.ietf.jgss.Oid;
import sun.security.action.GetPropertyAction;
import sun.security.util.Debug;

public class LoginConfigImpl extends Configuration {
  private final Configuration config;
  
  private final GSSCaller caller;
  
  private final String mechName;
  
  private static final Debug debug = Debug.getInstance("gssloginconfig", "\t[GSS LoginConfigImpl]");
  
  public static final boolean HTTP_USE_GLOBAL_CREDS;
  
  public LoginConfigImpl(GSSCaller paramGSSCaller, Oid paramOid) {
    this.caller = paramGSSCaller;
    if (paramOid.equals(GSSUtil.GSS_KRB5_MECH_OID)) {
      this.mechName = "krb5";
    } else {
      throw new IllegalArgumentException(paramOid.toString() + " not supported");
    } 
    this.config = (Configuration)AccessController.doPrivileged(new PrivilegedAction<Configuration>() {
          public Configuration run() { return Configuration.getConfiguration(); }
        });
  }
  
  public AppConfigurationEntry[] getAppConfigurationEntry(String paramString) {
    AppConfigurationEntry[] arrayOfAppConfigurationEntry = null;
    if ("OTHER".equalsIgnoreCase(paramString))
      return null; 
    String[] arrayOfString = null;
    if ("krb5".equals(this.mechName)) {
      if (this.caller == GSSCaller.CALLER_INITIATE) {
        arrayOfString = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.security.jgss.initiate" };
      } else if (this.caller == GSSCaller.CALLER_ACCEPT) {
        arrayOfString = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.security.jgss.accept" };
      } else if (this.caller == GSSCaller.CALLER_SSL_CLIENT) {
        arrayOfString = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.net.ssl.client" };
      } else if (this.caller == GSSCaller.CALLER_SSL_SERVER) {
        arrayOfString = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.net.ssl.server" };
      } else if (this.caller instanceof HttpCaller) {
        arrayOfString = new String[] { "com.sun.security.jgss.krb5.initiate" };
      } else if (this.caller == GSSCaller.CALLER_UNKNOWN) {
        throw new AssertionError("caller not defined");
      } 
    } else {
      throw new IllegalArgumentException(this.mechName + " not supported");
    } 
    for (String str : arrayOfString) {
      arrayOfAppConfigurationEntry = this.config.getAppConfigurationEntry(str);
      if (debug != null)
        debug.println("Trying " + str + ((arrayOfAppConfigurationEntry == null) ? ": does not exist." : ": Found!")); 
      if (arrayOfAppConfigurationEntry != null)
        break; 
    } 
    if (arrayOfAppConfigurationEntry == null) {
      if (debug != null)
        debug.println("Cannot read JGSS entry, use default values instead."); 
      arrayOfAppConfigurationEntry = getDefaultConfigurationEntry();
    } 
    return arrayOfAppConfigurationEntry;
  }
  
  private AppConfigurationEntry[] getDefaultConfigurationEntry() {
    HashMap hashMap = new HashMap(2);
    if (this.mechName == null || this.mechName.equals("krb5")) {
      if (isServerSide(this.caller)) {
        hashMap.put("useKeyTab", "true");
        hashMap.put("storeKey", "true");
        hashMap.put("doNotPrompt", "true");
        hashMap.put("principal", "*");
        hashMap.put("isInitiator", "false");
      } else {
        if (this.caller instanceof HttpCaller && !HTTP_USE_GLOBAL_CREDS) {
          hashMap.put("useTicketCache", "false");
        } else {
          hashMap.put("useTicketCache", "true");
        } 
        hashMap.put("doNotPrompt", "false");
      } 
      return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, hashMap) };
    } 
    return null;
  }
  
  private static boolean isServerSide(GSSCaller paramGSSCaller) { return (GSSCaller.CALLER_ACCEPT == paramGSSCaller || GSSCaller.CALLER_SSL_SERVER == paramGSSCaller); }
  
  static  {
    String str = GetPropertyAction.privilegedGetProperty("http.use.global.creds");
    HTTP_USE_GLOBAL_CREDS = !"false".equalsIgnoreCase(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\LoginConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */