package sun.security.jgss.wrapper;

import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;

class Krb5Util {
  static String getTGSName(GSSNameElement paramGSSNameElement) throws GSSException {
    String str1 = paramGSSNameElement.getKrbName();
    int i = str1.indexOf("@");
    String str2 = str1.substring(i + 1);
    StringBuffer stringBuffer = new StringBuffer("krbtgt/");
    stringBuffer.append(str2).append('@').append(str2);
    return stringBuffer.toString();
  }
  
  static void checkServicePermission(String paramString1, String paramString2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      SunNativeProvider.debug("Checking ServicePermission(" + paramString1 + ", " + paramString2 + ")");
      ServicePermission servicePermission = new ServicePermission(paramString1, paramString2);
      securityManager.checkPermission(servicePermission);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\Krb5Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */