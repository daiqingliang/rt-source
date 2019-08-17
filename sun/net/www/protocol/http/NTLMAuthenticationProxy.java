package sun.net.www.protocol.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.PasswordAuthentication;
import java.net.URL;
import sun.util.logging.PlatformLogger;

class NTLMAuthenticationProxy {
  private static Method supportsTA;
  
  private static Method isTrustedSite;
  
  private static final String clazzStr = "sun.net.www.protocol.http.ntlm.NTLMAuthentication";
  
  private static final String supportsTAStr = "supportsTransparentAuth";
  
  private static final String isTrustedSiteStr = "isTrustedSite";
  
  static final NTLMAuthenticationProxy proxy = tryLoadNTLMAuthentication();
  
  static final boolean supported = (proxy != null);
  
  static final boolean supportsTransparentAuth = supported ? supportsTransparentAuth() : 0;
  
  private final Constructor<? extends AuthenticationInfo> threeArgCtr;
  
  private final Constructor<? extends AuthenticationInfo> fiveArgCtr;
  
  private NTLMAuthenticationProxy(Constructor<? extends AuthenticationInfo> paramConstructor1, Constructor<? extends AuthenticationInfo> paramConstructor2) {
    this.threeArgCtr = paramConstructor1;
    this.fiveArgCtr = paramConstructor2;
  }
  
  AuthenticationInfo create(boolean paramBoolean, URL paramURL, PasswordAuthentication paramPasswordAuthentication) {
    try {
      return (AuthenticationInfo)this.threeArgCtr.newInstance(new Object[] { Boolean.valueOf(paramBoolean), paramURL, paramPasswordAuthentication });
    } catch (ReflectiveOperationException reflectiveOperationException) {
      finest(reflectiveOperationException);
      return null;
    } 
  }
  
  AuthenticationInfo create(boolean paramBoolean, String paramString, int paramInt, PasswordAuthentication paramPasswordAuthentication) {
    try {
      return (AuthenticationInfo)this.fiveArgCtr.newInstance(new Object[] { Boolean.valueOf(paramBoolean), paramString, Integer.valueOf(paramInt), paramPasswordAuthentication });
    } catch (ReflectiveOperationException reflectiveOperationException) {
      finest(reflectiveOperationException);
      return null;
    } 
  }
  
  private static boolean supportsTransparentAuth() {
    try {
      return ((Boolean)supportsTA.invoke(null, new Object[0])).booleanValue();
    } catch (ReflectiveOperationException reflectiveOperationException) {
      finest(reflectiveOperationException);
      return false;
    } 
  }
  
  public static boolean isTrustedSite(URL paramURL) {
    try {
      return ((Boolean)isTrustedSite.invoke(null, new Object[] { paramURL })).booleanValue();
    } catch (ReflectiveOperationException reflectiveOperationException) {
      finest(reflectiveOperationException);
      return false;
    } 
  }
  
  private static NTLMAuthenticationProxy tryLoadNTLMAuthentication() {
    try {
      Class clazz = Class.forName("sun.net.www.protocol.http.ntlm.NTLMAuthentication", true, null);
      if (clazz != null) {
        Constructor constructor1 = clazz.getConstructor(new Class[] { boolean.class, URL.class, PasswordAuthentication.class });
        Constructor constructor2 = clazz.getConstructor(new Class[] { boolean.class, String.class, int.class, PasswordAuthentication.class });
        supportsTA = clazz.getDeclaredMethod("supportsTransparentAuth", new Class[0]);
        isTrustedSite = clazz.getDeclaredMethod("isTrustedSite", new Class[] { URL.class });
        return new NTLMAuthenticationProxy(constructor1, constructor2);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      finest(classNotFoundException);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new AssertionError(reflectiveOperationException);
    } 
    return null;
  }
  
  static void finest(Exception paramException) {
    PlatformLogger platformLogger = HttpURLConnection.getHttpLogger();
    if (platformLogger.isLoggable(PlatformLogger.Level.FINEST))
      platformLogger.finest("NTLMAuthenticationProxy: " + paramException); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\NTLMAuthenticationProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */