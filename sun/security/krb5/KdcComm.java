package sun.security.krb5;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.NetClient;

public final class KdcComm {
  private static int defaultKdcRetryLimit;
  
  private static int defaultKdcTimeout;
  
  private static int defaultUdpPrefLimit;
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  private static final String BAD_POLICY_KEY = "krb5.kdc.bad.policy";
  
  private static int tryLessMaxRetries = 1;
  
  private static int tryLessTimeout = 5000;
  
  private static BpType badPolicy;
  
  private String realm;
  
  public static void initStatic() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("krb5.kdc.bad.policy"); }
        });
    if (str != null) {
      str = str.toLowerCase(Locale.ENGLISH);
      String[] arrayOfString = str.split(":");
      if ("tryless".equals(arrayOfString[0])) {
        if (arrayOfString.length > 1) {
          String[] arrayOfString1 = arrayOfString[1].split(",");
          try {
            int m = Integer.parseInt(arrayOfString1[0]);
            if (arrayOfString1.length > 1)
              tryLessTimeout = Integer.parseInt(arrayOfString1[1]); 
            tryLessMaxRetries = m;
          } catch (NumberFormatException numberFormatException) {
            if (DEBUG)
              System.out.println("Invalid krb5.kdc.bad.policy parameter for tryLess: " + str + ", use default"); 
          } 
        } 
        badPolicy = BpType.TRY_LESS;
      } else if ("trylast".equals(arrayOfString[0])) {
        badPolicy = BpType.TRY_LAST;
      } else {
        badPolicy = BpType.NONE;
      } 
    } else {
      badPolicy = BpType.NONE;
    } 
    int i = -1;
    int j = -1;
    int k = -1;
    try {
      Config config = Config.getInstance();
      String str1 = config.get(new String[] { "libdefaults", "kdc_timeout" });
      i = parseTimeString(str1);
      str1 = config.get(new String[] { "libdefaults", "max_retries" });
      j = parsePositiveIntString(str1);
      str1 = config.get(new String[] { "libdefaults", "udp_preference_limit" });
      k = parsePositiveIntString(str1);
    } catch (Exception exception) {
      if (DEBUG)
        System.out.println("Exception in getting KDC communication settings, using default value " + exception.getMessage()); 
    } 
    defaultKdcTimeout = (i > 0) ? i : 30000;
    defaultKdcRetryLimit = (j > 0) ? j : 3;
    if (k < 0) {
      defaultUdpPrefLimit = 1465;
    } else if (k > 32700) {
      defaultUdpPrefLimit = 32700;
    } else {
      defaultUdpPrefLimit = k;
    } 
    KdcAccessibility.reset();
  }
  
  public KdcComm(String paramString) throws KrbException {
    if (paramString == null) {
      paramString = Config.getInstance().getDefaultRealm();
      if (paramString == null)
        throw new KrbException(60, "Cannot find default realm"); 
    } 
    this.realm = paramString;
  }
  
  public byte[] send(byte[] paramArrayOfByte) throws IOException, KrbException {
    int i = getRealmSpecificValue(this.realm, "udp_preference_limit", defaultUdpPrefLimit);
    boolean bool = (i > 0 && paramArrayOfByte != null && paramArrayOfByte.length > i);
    return send(paramArrayOfByte, bool);
  }
  
  private byte[] send(byte[] paramArrayOfByte, boolean paramBoolean) throws IOException, KrbException {
    if (paramArrayOfByte == null)
      return null; 
    Config config = Config.getInstance();
    if (this.realm == null) {
      this.realm = config.getDefaultRealm();
      if (this.realm == null)
        throw new KrbException(60, "Cannot find default realm"); 
    } 
    String str = config.getKDCList(this.realm);
    if (str == null)
      throw new KrbException("Cannot get kdc for realm " + this.realm); 
    Iterator iterator = KdcAccessibility.list(str).iterator();
    if (!iterator.hasNext())
      throw new KrbException("Cannot get kdc for realm " + this.realm); 
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = sendIfPossible(paramArrayOfByte, (String)iterator.next(), paramBoolean);
    } catch (Exception exception) {
      boolean bool = false;
      while (iterator.hasNext()) {
        try {
          arrayOfByte = sendIfPossible(paramArrayOfByte, (String)iterator.next(), paramBoolean);
          bool = true;
          break;
        } catch (Exception exception1) {}
      } 
      if (!bool)
        throw exception; 
    } 
    if (arrayOfByte == null)
      throw new IOException("Cannot get a KDC reply"); 
    return arrayOfByte;
  }
  
  private byte[] sendIfPossible(byte[] paramArrayOfByte, String paramString, boolean paramBoolean) throws IOException, KrbException {
    try {
      byte[] arrayOfByte = send(paramArrayOfByte, paramString, paramBoolean);
      KRBError kRBError = null;
      try {
        kRBError = new KRBError(arrayOfByte);
      } catch (Exception exception) {}
      if (kRBError != null && kRBError.getErrorCode() == 52)
        arrayOfByte = send(paramArrayOfByte, paramString, true); 
      KdcAccessibility.removeBad(paramString);
      return arrayOfByte;
    } catch (Exception exception) {
      if (DEBUG) {
        System.out.println(">>> KrbKdcReq send: error trying " + paramString);
        exception.printStackTrace(System.out);
      } 
      KdcAccessibility.addBad(paramString);
      throw exception;
    } 
  }
  
  private byte[] send(byte[] paramArrayOfByte, String paramString, boolean paramBoolean) throws IOException, KrbException {
    if (paramArrayOfByte == null)
      return null; 
    int i = 88;
    int j = getRealmSpecificValue(this.realm, "max_retries", defaultKdcRetryLimit);
    int k = getRealmSpecificValue(this.realm, "kdc_timeout", defaultKdcTimeout);
    if (badPolicy == BpType.TRY_LESS && KdcAccessibility.isBad(paramString)) {
      if (j > tryLessMaxRetries)
        j = tryLessMaxRetries; 
      if (k > tryLessTimeout)
        k = tryLessTimeout; 
    } 
    String str1 = null;
    String str2 = null;
    if (paramString.charAt(0) == '[') {
      int m = paramString.indexOf(']', 1);
      if (m == -1)
        throw new IOException("Illegal KDC: " + paramString); 
      str1 = paramString.substring(1, m);
      if (m != paramString.length() - 1) {
        if (paramString.charAt(m + 1) != ':')
          throw new IOException("Illegal KDC: " + paramString); 
        str2 = paramString.substring(m + 2);
      } 
    } else {
      int m = paramString.indexOf(':');
      if (m == -1) {
        str1 = paramString;
      } else {
        int n = paramString.indexOf(':', m + 1);
        if (n > 0) {
          str1 = paramString;
        } else {
          str1 = paramString.substring(0, m);
          str2 = paramString.substring(m + 1);
        } 
      } 
    } 
    if (str2 != null) {
      int m = parsePositiveIntString(str2);
      if (m > 0)
        i = m; 
    } 
    if (DEBUG)
      System.out.println(">>> KrbKdcReq send: kdc=" + str1 + (paramBoolean ? " TCP:" : " UDP:") + i + ", timeout=" + k + ", number of retries =" + j + ", #bytes=" + paramArrayOfByte.length); 
    KdcCommunication kdcCommunication = new KdcCommunication(str1, i, paramBoolean, k, j, paramArrayOfByte);
    try {
      byte[] arrayOfByte = (byte[])AccessController.doPrivileged(kdcCommunication);
      if (DEBUG)
        System.out.println(">>> KrbKdcReq send: #bytes read=" + ((arrayOfByte != null) ? arrayOfByte.length : 0)); 
      return arrayOfByte;
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = privilegedActionException.getException();
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw (KrbException)exception;
    } 
  }
  
  private static int parseTimeString(String paramString) {
    if (paramString == null)
      return -1; 
    if (paramString.endsWith("s")) {
      int i = parsePositiveIntString(paramString.substring(0, paramString.length() - 1));
      return (i < 0) ? -1 : (i * 1000);
    } 
    return parsePositiveIntString(paramString);
  }
  
  private int getRealmSpecificValue(String paramString1, String paramString2, int paramInt) {
    int i = paramInt;
    if (paramString1 == null)
      return i; 
    int j = -1;
    try {
      String str = Config.getInstance().get(new String[] { "realms", paramString1, paramString2 });
      if (paramString2.equals("kdc_timeout")) {
        j = parseTimeString(str);
      } else {
        j = parsePositiveIntString(str);
      } 
    } catch (Exception exception) {}
    if (j > 0)
      i = j; 
    return i;
  }
  
  private static int parsePositiveIntString(String paramString) {
    if (paramString == null)
      return -1; 
    int i = -1;
    try {
      i = Integer.parseInt(paramString);
    } catch (Exception exception) {
      return -1;
    } 
    return (i >= 0) ? i : -1;
  }
  
  static  {
    initStatic();
  }
  
  private enum BpType {
    NONE, TRY_LAST, TRY_LESS;
  }
  
  static class KdcAccessibility {
    private static Set<String> bads = new HashSet();
    
    private static void addBad(String param1String) throws KrbException {
      if (DEBUG)
        System.out.println(">>> KdcAccessibility: add " + param1String); 
      bads.add(param1String);
    }
    
    private static void removeBad(String param1String) throws KrbException {
      if (DEBUG)
        System.out.println(">>> KdcAccessibility: remove " + param1String); 
      bads.remove(param1String);
    }
    
    private static boolean isBad(String param1String) { return bads.contains(param1String); }
    
    private static void reset() {
      if (DEBUG)
        System.out.println(">>> KdcAccessibility: reset"); 
      bads.clear();
    }
    
    private static List<String> list(String param1String) {
      StringTokenizer stringTokenizer = new StringTokenizer(param1String);
      ArrayList arrayList = new ArrayList();
      if (badPolicy == KdcComm.BpType.TRY_LAST) {
        ArrayList arrayList1 = new ArrayList();
        while (stringTokenizer.hasMoreTokens()) {
          String str = stringTokenizer.nextToken();
          if (bads.contains(str)) {
            arrayList1.add(str);
            continue;
          } 
          arrayList.add(str);
        } 
        arrayList.addAll(arrayList1);
      } else {
        while (stringTokenizer.hasMoreTokens())
          arrayList.add(stringTokenizer.nextToken()); 
      } 
      return arrayList;
    }
  }
  
  private static class KdcCommunication extends Object implements PrivilegedExceptionAction<byte[]> {
    private String kdc;
    
    private int port;
    
    private boolean useTCP;
    
    private int timeout;
    
    private int retries;
    
    private byte[] obuf;
    
    public KdcCommunication(String param1String, int param1Int1, boolean param1Boolean, int param1Int2, int param1Int3, byte[] param1ArrayOfByte) {
      this.kdc = param1String;
      this.port = param1Int1;
      this.useTCP = param1Boolean;
      this.timeout = param1Int2;
      this.retries = param1Int3;
      this.obuf = param1ArrayOfByte;
    }
    
    public byte[] run() throws IOException, KrbException {
      arrayOfByte = null;
      b = 1;
      while (b <= this.retries) {
        String str = this.useTCP ? "TCP" : "UDP";
        try (NetClient null = NetClient.getInstance(str, this.kdc, this.port, this.timeout)) {
          if (DEBUG)
            System.out.println(">>> KDCCommunication: kdc=" + this.kdc + " " + str + ":" + this.port + ", timeout=" + this.timeout + ",Attempt =" + b + ", #bytes=" + this.obuf.length); 
        } 
      } 
      return arrayOfByte;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KdcComm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */