package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.Pool;
import com.sun.jndi.ldap.pool.PoolCleaner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;

public final class LdapPoolManager {
  private static final String DEBUG = "com.sun.jndi.ldap.connect.pool.debug";
  
  public static final boolean debug = "all".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", null));
  
  public static final boolean trace = (debug || "fine".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", null)));
  
  private static final String POOL_AUTH = "com.sun.jndi.ldap.connect.pool.authentication";
  
  private static final String POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
  
  private static final String MAX_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
  
  private static final String PREF_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
  
  private static final String INIT_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.initsize";
  
  private static final String POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
  
  private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
  
  private static final int DEFAULT_MAX_POOL_SIZE = 0;
  
  private static final int DEFAULT_PREF_POOL_SIZE = 0;
  
  private static final int DEFAULT_INIT_POOL_SIZE = 1;
  
  private static final int DEFAULT_TIMEOUT = 0;
  
  private static final String DEFAULT_AUTH_MECHS = "none simple";
  
  private static final String DEFAULT_PROTOCOLS = "plain";
  
  private static final int NONE = 0;
  
  private static final int SIMPLE = 1;
  
  private static final int DIGEST = 2;
  
  private static final long idleTimeout;
  
  private static final int maxSize;
  
  private static final int prefSize;
  
  private static final int initSize;
  
  private static boolean supportPlainProtocol = false;
  
  private static boolean supportSslProtocol = false;
  
  private static final Pool[] pools = new Pool[3];
  
  private static int findPool(String paramString) { return "none".equalsIgnoreCase(paramString) ? 0 : ("simple".equalsIgnoreCase(paramString) ? 1 : ("digest-md5".equalsIgnoreCase(paramString) ? 2 : -1)); }
  
  static boolean isPoolingAllowed(String paramString1, OutputStream paramOutputStream, String paramString2, String paramString3, Hashtable<?, ?> paramHashtable) throws NamingException {
    if ((paramOutputStream != null && !debug) || (paramString3 == null && !supportPlainProtocol) || ("ssl".equalsIgnoreCase(paramString3) && !supportSslProtocol)) {
      d("Pooling disallowed due to tracing or unsupported pooling of protocol");
      return false;
    } 
    String str = "java.util.Comparator";
    boolean bool = false;
    if (paramString1 != null && !paramString1.equals("javax.net.ssl.SSLSocketFactory")) {
      try {
        Class clazz = Obj.helper.loadClass(paramString1);
        Class[] arrayOfClass = clazz.getInterfaces();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (arrayOfClass[b].getCanonicalName().equals(str))
            bool = true; 
        } 
      } catch (Exception exception) {
        CommunicationException communicationException = new CommunicationException("Loading the socket factory");
        communicationException.setRootCause(exception);
        throw communicationException;
      } 
      if (!bool)
        return false; 
    } 
    int i = findPool(paramString2);
    if (i < 0 || pools[i] == null) {
      d("authmech not found: ", paramString2);
      return false;
    } 
    d("using authmech: ", paramString2);
    switch (i) {
      case 0:
      case 1:
        return true;
      case 2:
        return (paramHashtable == null || paramHashtable.get("java.naming.security.sasl.callback") == null);
    } 
    return false;
  }
  
  static LdapClient getLdapClient(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, int paramInt4, String paramString3, Control[] paramArrayOfControl, String paramString4, String paramString5, Object paramObject, Hashtable<?, ?> paramHashtable) throws NamingException {
    ClientId clientId = null;
    int i = findPool(paramString3);
    Pool pool;
    if (i < 0 || (pool = pools[i]) == null)
      throw new IllegalArgumentException("Attempting to use pooling for an unsupported mechanism: " + paramString3); 
    switch (i) {
      case 0:
        clientId = new ClientId(paramInt4, paramString1, paramInt1, paramString4, paramArrayOfControl, paramOutputStream, paramString2);
        break;
      case 1:
        clientId = new SimpleClientId(paramInt4, paramString1, paramInt1, paramString4, paramArrayOfControl, paramOutputStream, paramString2, paramString5, paramObject);
        break;
      case 2:
        clientId = new DigestClientId(paramInt4, paramString1, paramInt1, paramString4, paramArrayOfControl, paramOutputStream, paramString2, paramString5, paramObject, paramHashtable);
        break;
    } 
    return (LdapClient)pool.getPooledConnection(clientId, paramInt2, new LdapClientFactory(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream));
  }
  
  public static void showStats(PrintStream paramPrintStream) {
    paramPrintStream.println("***** start *****");
    paramPrintStream.println("idle timeout: " + idleTimeout);
    paramPrintStream.println("maximum pool size: " + maxSize);
    paramPrintStream.println("preferred pool size: " + prefSize);
    paramPrintStream.println("initial pool size: " + initSize);
    paramPrintStream.println("protocol types: " + (supportPlainProtocol ? "plain " : "") + (supportSslProtocol ? "ssl" : ""));
    paramPrintStream.println("authentication types: " + ((pools[false] != null) ? "none " : "") + ((pools[true] != null) ? "simple " : "") + ((pools[2] != null) ? "DIGEST-MD5 " : ""));
    for (byte b = 0; b < pools.length; b++) {
      if (pools[b] != null) {
        paramPrintStream.println((!b ? "anonymous pools" : ((b == 1) ? "simple auth pools" : ((b == 2) ? "digest pools" : ""))) + ":");
        pools[b].showStats(paramPrintStream);
      } 
    } 
    paramPrintStream.println("***** end *****");
  }
  
  public static void expire(long paramLong) {
    for (byte b = 0; b < pools.length; b++) {
      if (pools[b] != null)
        pools[b].expire(paramLong); 
    } 
  }
  
  private static void d(String paramString) {
    if (debug)
      System.err.println("LdapPoolManager: " + paramString); 
  }
  
  private static void d(String paramString1, String paramString2) {
    if (debug)
      System.err.println("LdapPoolManager: " + paramString1 + paramString2); 
  }
  
  private static final String getProperty(final String propName, final String defVal) { return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            try {
              return System.getProperty(propName, defVal);
            } catch (SecurityException securityException) {
              return defVal;
            } 
          }
        }); }
  
  private static final int getInteger(final String propName, final int defVal) {
    Integer integer = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
          public Integer run() {
            try {
              return Integer.getInteger(propName, defVal);
            } catch (SecurityException securityException) {
              return new Integer(defVal);
            } 
          }
        });
    return integer.intValue();
  }
  
  private static final long getLong(final String propName, final long defVal) {
    Long long = (Long)AccessController.doPrivileged(new PrivilegedAction<Long>() {
          public Long run() {
            try {
              return Long.getLong(propName, defVal);
            } catch (SecurityException securityException) {
              return new Long(defVal);
            } 
          }
        });
    return long.longValue();
  }
  
  static  {
    maxSize = getInteger("com.sun.jndi.ldap.connect.pool.maxsize", 0);
    prefSize = getInteger("com.sun.jndi.ldap.connect.pool.prefsize", 0);
    initSize = getInteger("com.sun.jndi.ldap.connect.pool.initsize", 1);
    idleTimeout = getLong("com.sun.jndi.ldap.connect.pool.timeout", 0L);
    String str = getProperty("com.sun.jndi.ldap.connect.pool.authentication", "none simple");
    StringTokenizer stringTokenizer = new StringTokenizer(str);
    int i = stringTokenizer.countTokens();
    for (byte b1 = 0; b1 < i; b1++) {
      String str1 = stringTokenizer.nextToken().toLowerCase(Locale.ENGLISH);
      if (str1.equals("anonymous"))
        str1 = "none"; 
      int j = findPool(str1);
      if (j >= 0 && pools[j] == null)
        pools[j] = new Pool(initSize, prefSize, maxSize); 
    } 
    str = getProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain");
    stringTokenizer = new StringTokenizer(str);
    i = stringTokenizer.countTokens();
    for (byte b2 = 0; b2 < i; b2++) {
      String str1 = stringTokenizer.nextToken();
      if ("plain".equalsIgnoreCase(str1)) {
        supportPlainProtocol = true;
      } else if ("ssl".equalsIgnoreCase(str1)) {
        supportSslProtocol = true;
      } 
    } 
    if (idleTimeout > 0L)
      (new PoolCleaner(idleTimeout, pools)).start(); 
    if (debug)
      showStats(System.err); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapPoolManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */