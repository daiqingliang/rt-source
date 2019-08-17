package javax.security.sasl;

import java.security.Provider;
import java.security.Security;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.CallbackHandler;

public class Sasl {
  public static final String QOP = "javax.security.sasl.qop";
  
  public static final String STRENGTH = "javax.security.sasl.strength";
  
  public static final String SERVER_AUTH = "javax.security.sasl.server.authentication";
  
  public static final String BOUND_SERVER_NAME = "javax.security.sasl.bound.server.name";
  
  public static final String MAX_BUFFER = "javax.security.sasl.maxbuffer";
  
  public static final String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";
  
  public static final String REUSE = "javax.security.sasl.reuse";
  
  public static final String POLICY_NOPLAINTEXT = "javax.security.sasl.policy.noplaintext";
  
  public static final String POLICY_NOACTIVE = "javax.security.sasl.policy.noactive";
  
  public static final String POLICY_NODICTIONARY = "javax.security.sasl.policy.nodictionary";
  
  public static final String POLICY_NOANONYMOUS = "javax.security.sasl.policy.noanonymous";
  
  public static final String POLICY_FORWARD_SECRECY = "javax.security.sasl.policy.forward";
  
  public static final String POLICY_PASS_CREDENTIALS = "javax.security.sasl.policy.credentials";
  
  public static final String CREDENTIALS = "javax.security.sasl.credentials";
  
  public static SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    SaslClient saslClient = null;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str;
      if ((str = paramArrayOfString[b]) == null)
        throw new NullPointerException("Mechanism name cannot be null"); 
      if (str.length() != 0) {
        String str1 = "SaslClientFactory." + str;
        Provider[] arrayOfProvider = Security.getProviders(str1);
        for (byte b1 = 0; arrayOfProvider != null && b1 < arrayOfProvider.length; b1++) {
          String str2 = arrayOfProvider[b1].getProperty(str1);
          if (str2 != null) {
            SaslClientFactory saslClientFactory = (SaslClientFactory)loadFactory(arrayOfProvider[b1], str2);
            if (saslClientFactory != null) {
              saslClient = saslClientFactory.createSaslClient(new String[] { paramArrayOfString[b] }, paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
              if (saslClient != null)
                return saslClient; 
            } 
          } 
        } 
      } 
    } 
    return null;
  }
  
  private static Object loadFactory(Provider paramProvider, String paramString) throws SaslException {
    try {
      ClassLoader classLoader = paramProvider.getClass().getClassLoader();
      Class clazz = Class.forName(paramString, true, classLoader);
      return clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SaslException("Cannot load class " + paramString, classNotFoundException);
    } catch (InstantiationException instantiationException) {
      throw new SaslException("Cannot instantiate class " + paramString, instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new SaslException("Cannot access class " + paramString, illegalAccessException);
    } catch (SecurityException securityException) {
      throw new SaslException("Cannot access class " + paramString, securityException);
    } 
  }
  
  public static SaslServer createSaslServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    SaslServer saslServer = null;
    if (paramString1 == null)
      throw new NullPointerException("Mechanism name cannot be null"); 
    if (paramString1.length() == 0)
      return null; 
    String str = "SaslServerFactory." + paramString1;
    Provider[] arrayOfProvider = Security.getProviders(str);
    for (byte b = 0; arrayOfProvider != null && b < arrayOfProvider.length; b++) {
      String str1 = arrayOfProvider[b].getProperty(str);
      if (str1 == null)
        throw new SaslException("Provider does not support " + str); 
      SaslServerFactory saslServerFactory = (SaslServerFactory)loadFactory(arrayOfProvider[b], str1);
      if (saslServerFactory != null) {
        saslServer = saslServerFactory.createSaslServer(paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
        if (saslServer != null)
          return saslServer; 
      } 
    } 
    return null;
  }
  
  public static Enumeration<SaslClientFactory> getSaslClientFactories() {
    Set set = getFactories("SaslClientFactory");
    final Iterator iter = set.iterator();
    return new Enumeration<SaslClientFactory>() {
        public boolean hasMoreElements() { return iter.hasNext(); }
        
        public SaslClientFactory nextElement() { return (SaslClientFactory)iter.next(); }
      };
  }
  
  public static Enumeration<SaslServerFactory> getSaslServerFactories() {
    Set set = getFactories("SaslServerFactory");
    final Iterator iter = set.iterator();
    return new Enumeration<SaslServerFactory>() {
        public boolean hasMoreElements() { return iter.hasNext(); }
        
        public SaslServerFactory nextElement() { return (SaslServerFactory)iter.next(); }
      };
  }
  
  private static Set<Object> getFactories(String paramString) {
    HashSet hashSet1 = new HashSet();
    if (paramString == null || paramString.length() == 0 || paramString.endsWith("."))
      return hashSet1; 
    Provider[] arrayOfProvider = Security.getProviders();
    HashSet hashSet2 = new HashSet();
    for (byte b = 0; b < arrayOfProvider.length; b++) {
      hashSet2.clear();
      Enumeration enumeration = arrayOfProvider[b].keys();
      while (enumeration.hasMoreElements()) {
        String str = (String)enumeration.nextElement();
        if (str.startsWith(paramString) && str.indexOf(" ") < 0) {
          String str1 = arrayOfProvider[b].getProperty(str);
          if (!hashSet2.contains(str1)) {
            hashSet2.add(str1);
            try {
              Object object = loadFactory(arrayOfProvider[b], str1);
              if (object != null)
                hashSet1.add(object); 
            } catch (Exception exception) {}
          } 
        } 
      } 
    } 
    return Collections.unmodifiableSet(hashSet1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\sasl\Sasl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */