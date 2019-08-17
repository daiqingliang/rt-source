package sun.security.jgss.wrapper;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.HashMap;
import org.ietf.jgss.Oid;
import sun.security.action.PutAllAction;

public final class SunNativeProvider extends Provider {
  private static final long serialVersionUID = -238911724858694204L;
  
  private static final String NAME = "SunNativeGSS";
  
  private static final String INFO = "Sun Native GSS provider";
  
  private static final String MF_CLASS = "sun.security.jgss.wrapper.NativeGSSFactory";
  
  private static final String LIB_PROP = "sun.security.jgss.lib";
  
  private static final String DEBUG_PROP = "sun.security.nativegss.debug";
  
  private static HashMap<String, String> MECH_MAP;
  
  static final Provider INSTANCE = new SunNativeProvider();
  
  static boolean DEBUG;
  
  static void debug(String paramString) {
    if (DEBUG) {
      if (paramString == null)
        throw new NullPointerException(); 
      System.out.println("SunNativeGSS: " + paramString);
    } 
  }
  
  public SunNativeProvider() {
    super("SunNativeGSS", 1.8D, "Sun Native GSS provider");
    if (MECH_MAP != null)
      AccessController.doPrivileged(new PutAllAction(this, MECH_MAP)); 
  }
  
  static  {
    MECH_MAP = (HashMap)AccessController.doPrivileged(new PrivilegedAction<HashMap<String, String>>() {
          public HashMap<String, String> run() {
            SunNativeProvider.DEBUG = Boolean.parseBoolean(System.getProperty("sun.security.nativegss.debug"));
            try {
              System.loadLibrary("j2gss");
            } catch (Error error) {
              SunNativeProvider.debug("No j2gss library found!");
              if (SunNativeProvider.DEBUG)
                error.printStackTrace(); 
              return null;
            } 
            String[] arrayOfString = new String[0];
            String str = System.getProperty("sun.security.jgss.lib");
            if (str == null || str.trim().equals("")) {
              String str1 = System.getProperty("os.name");
              if (str1.startsWith("SunOS")) {
                arrayOfString = new String[] { "libgss.so" };
              } else if (str1.startsWith("Linux")) {
                arrayOfString = new String[] { "libgssapi.so", "libgssapi_krb5.so", "libgssapi_krb5.so.2" };
              } else if (str1.contains("OS X")) {
                arrayOfString = new String[] { "libgssapi_krb5.dylib", "/usr/lib/sasl2/libgssapiv2.2.so" };
              } 
            } else {
              arrayOfString = new String[] { str };
            } 
            for (String str1 : arrayOfString) {
              if (GSSLibStub.init(str1, SunNativeProvider.DEBUG)) {
                SunNativeProvider.debug("Loaded GSS library: " + str1);
                Oid[] arrayOfOid = GSSLibStub.indicateMechs();
                HashMap hashMap = new HashMap();
                for (byte b = 0; b < arrayOfOid.length; b++) {
                  SunNativeProvider.debug("Native MF for " + arrayOfOid[b]);
                  hashMap.put("GssApiMechanism." + arrayOfOid[b], "sun.security.jgss.wrapper.NativeGSSFactory");
                } 
                return hashMap;
              } 
            } 
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\SunNativeProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */