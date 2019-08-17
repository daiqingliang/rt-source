package sun.security.krb5.internal.ccache;

import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.LoginOptions;

public abstract class CredentialsCache {
  static CredentialsCache singleton = null;
  
  static String cacheName;
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static CredentialsCache getInstance(PrincipalName paramPrincipalName) { return FileCredentialsCache.acquireInstance(paramPrincipalName, null); }
  
  public static CredentialsCache getInstance(String paramString) { return (paramString.length() >= 5 && paramString.substring(0, 5).equalsIgnoreCase("FILE:")) ? FileCredentialsCache.acquireInstance(null, paramString.substring(5)) : FileCredentialsCache.acquireInstance(null, paramString); }
  
  public static CredentialsCache getInstance(PrincipalName paramPrincipalName, String paramString) { return (paramString != null && paramString.length() >= 5 && paramString.regionMatches(true, 0, "FILE:", 0, 5)) ? FileCredentialsCache.acquireInstance(paramPrincipalName, paramString.substring(5)) : FileCredentialsCache.acquireInstance(paramPrincipalName, paramString); }
  
  public static CredentialsCache getInstance() { return FileCredentialsCache.acquireInstance(); }
  
  public static CredentialsCache create(PrincipalName paramPrincipalName, String paramString) {
    if (paramString == null)
      throw new RuntimeException("cache name error"); 
    if (paramString.length() >= 5 && paramString.regionMatches(true, 0, "FILE:", 0, 5)) {
      paramString = paramString.substring(5);
      return FileCredentialsCache.New(paramPrincipalName, paramString);
    } 
    return FileCredentialsCache.New(paramPrincipalName, paramString);
  }
  
  public static CredentialsCache create(PrincipalName paramPrincipalName) { return FileCredentialsCache.New(paramPrincipalName); }
  
  public static String cacheName() { return cacheName; }
  
  public abstract PrincipalName getPrimaryPrincipal();
  
  public abstract void update(Credentials paramCredentials);
  
  public abstract void save();
  
  public abstract Credentials[] getCredsList();
  
  public abstract Credentials getDefaultCreds();
  
  public abstract Credentials getCreds(PrincipalName paramPrincipalName);
  
  public abstract Credentials getCreds(LoginOptions paramLoginOptions, PrincipalName paramPrincipalName);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ccache\CredentialsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */