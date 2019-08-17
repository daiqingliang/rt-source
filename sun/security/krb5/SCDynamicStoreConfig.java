package sun.security.krb5;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import sun.security.krb5.internal.Krb5;

public class SCDynamicStoreConfig {
  private static boolean DEBUG = Krb5.DEBUG;
  
  private static native void installNotificationCallback();
  
  private static native Hashtable<String, Object> getKerberosConfig();
  
  private static Vector<String> unwrapHost(Collection<Hashtable<String, String>> paramCollection) {
    Vector vector = new Vector();
    for (Hashtable hashtable : paramCollection)
      vector.add(hashtable.get("host")); 
    return vector;
  }
  
  private static Hashtable<String, Object> convertRealmConfigs(Hashtable<String, ?> paramHashtable) {
    Hashtable hashtable = new Hashtable();
    for (String str : paramHashtable.keySet()) {
      Hashtable hashtable1 = (Hashtable)paramHashtable.get(str);
      Hashtable hashtable2 = new Hashtable();
      Collection collection1 = (Collection)hashtable1.get("kdc");
      if (collection1 != null)
        hashtable2.put("kdc", unwrapHost(collection1)); 
      Collection collection2 = (Collection)hashtable1.get("kadmin");
      if (collection2 != null)
        hashtable2.put("admin_server", unwrapHost(collection2)); 
      hashtable.put(str, hashtable2);
    } 
    return hashtable;
  }
  
  public static Hashtable<String, Object> getConfig() {
    Hashtable hashtable = getKerberosConfig();
    if (hashtable == null)
      throw new IOException("Could not load configuration from SCDynamicStore"); 
    if (DEBUG)
      System.out.println("Raw map from JNI: " + hashtable); 
    return convertNativeConfig(hashtable);
  }
  
  private static Hashtable<String, Object> convertNativeConfig(Hashtable<String, Object> paramHashtable) {
    Hashtable hashtable = (Hashtable)paramHashtable.get("realms");
    if (hashtable != null) {
      paramHashtable.remove("realms");
      Hashtable hashtable1 = convertRealmConfigs(hashtable);
      paramHashtable.put("realms", hashtable1);
    } 
    WrapAllStringInVector(paramHashtable);
    if (DEBUG)
      System.out.println("stanzaTable : " + paramHashtable); 
    return paramHashtable;
  }
  
  private static void WrapAllStringInVector(Hashtable<String, Object> paramHashtable) {
    for (String str : paramHashtable.keySet()) {
      Object object = paramHashtable.get(str);
      if (object instanceof Hashtable) {
        WrapAllStringInVector((Hashtable)object);
        continue;
      } 
      if (object instanceof String) {
        Vector vector = new Vector();
        vector.add((String)object);
        paramHashtable.put(str, vector);
      } 
    } 
  }
  
  static  {
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            String str = System.getProperty("os.name");
            if (str.contains("OS X")) {
              System.loadLibrary("osx");
              return Boolean.valueOf(true);
            } 
            return Boolean.valueOf(false);
          }
        })).booleanValue();
    if (bool)
      installNotificationCallback(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\SCDynamicStoreConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */