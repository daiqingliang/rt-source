package sun.security.krb5;

import java.net.SocketPermission;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.spi.NamingManager;
import sun.security.krb5.internal.Krb5;

class KrbServiceLocator {
  private static final String SRV_RR = "SRV";
  
  private static final String[] SRV_RR_ATTR = { "SRV" };
  
  private static final String SRV_TXT = "TXT";
  
  private static final String[] SRV_TXT_ATTR = { "TXT" };
  
  private static final Random random = new Random();
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  static String[] getKerberosService(String paramString) {
    String str = "dns:///_kerberos." + paramString;
    String[] arrayOfString = null;
    try {
      Context context = NamingManager.getURLContext("dns", new Hashtable(0));
      if (!(context instanceof DirContext))
        return null; 
      Attributes attributes = null;
      try {
        attributes = (Attributes)AccessController.doPrivileged(() -> ((DirContext)paramContext).getAttributes(paramString, SRV_TXT_ATTR), null, new Permission[] { new SocketPermission("*", "connect,accept") });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (NamingException)privilegedActionException.getCause();
      } 
      Attribute attribute;
      if (attributes != null && (attribute = attributes.get("TXT")) != null) {
        int i = attribute.size();
        byte b1 = 0;
        String[] arrayOfString1 = new String[i];
        byte b2 = 0;
        byte b3 = 0;
        while (b2 < i) {
          try {
            arrayOfString1[b3] = (String)attribute.get(b2);
            b3++;
          } catch (Exception exception) {}
          b2++;
        } 
        b1 = b3;
        if (b1 < i) {
          String[] arrayOfString2 = new String[b1];
          System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, b1);
          arrayOfString = arrayOfString2;
        } else {
          arrayOfString = arrayOfString1;
        } 
      } 
    } catch (NamingException namingException) {}
    return arrayOfString;
  }
  
  static String[] getKerberosService(String paramString1, String paramString2) {
    String str = "dns:///_kerberos." + paramString2 + "." + paramString1;
    String[] arrayOfString = null;
    try {
      Context context = NamingManager.getURLContext("dns", new Hashtable(0));
      if (!(context instanceof DirContext))
        return null; 
      Attributes attributes = null;
      try {
        attributes = (Attributes)AccessController.doPrivileged(() -> ((DirContext)paramContext).getAttributes(paramString, SRV_RR_ATTR), null, new Permission[] { new SocketPermission("*", "connect,accept") });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (NamingException)privilegedActionException.getCause();
      } 
      Attribute attribute;
      if (attributes != null && (attribute = attributes.get("SRV")) != null) {
        int i = attribute.size();
        byte b1 = 0;
        SrvRecord[] arrayOfSrvRecord = new SrvRecord[i];
        byte b2 = 0;
        byte b3 = 0;
        while (b2 < i) {
          try {
            arrayOfSrvRecord[b3] = new SrvRecord((String)attribute.get(b2));
            b3++;
          } catch (Exception exception) {}
          b2++;
        } 
        b1 = b3;
        if (b1 < i) {
          SrvRecord[] arrayOfSrvRecord1 = new SrvRecord[b1];
          System.arraycopy(arrayOfSrvRecord, 0, arrayOfSrvRecord1, 0, b1);
          arrayOfSrvRecord = arrayOfSrvRecord1;
        } 
        if (b1 > 1)
          Arrays.sort(arrayOfSrvRecord); 
        arrayOfString = extractHostports(arrayOfSrvRecord);
      } 
    } catch (NamingException namingException) {}
    return arrayOfString;
  }
  
  private static String[] extractHostports(SrvRecord[] paramArrayOfSrvRecord) {
    String[] arrayOfString = null;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    for (byte b5 = 0; b5 < paramArrayOfSrvRecord.length; b5++) {
      if (arrayOfString == null)
        arrayOfString = new String[paramArrayOfSrvRecord.length]; 
      b1 = b5;
      while (b5 < paramArrayOfSrvRecord.length - 1 && (paramArrayOfSrvRecord[b5]).priority == (paramArrayOfSrvRecord[b5 + 1]).priority)
        b5++; 
      b2 = b5;
      b3 = b2 - b1 + 1;
      for (byte b = 0; b < b3; b++)
        arrayOfString[b4++] = selectHostport(paramArrayOfSrvRecord, b1, b2); 
    } 
    return arrayOfString;
  }
  
  private static String selectHostport(SrvRecord[] paramArrayOfSrvRecord, int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return (paramArrayOfSrvRecord[paramInt1]).hostport; 
    int i = 0;
    for (int j = paramInt1; j <= paramInt2; j++) {
      if (paramArrayOfSrvRecord[j] != null) {
        i += (paramArrayOfSrvRecord[j]).weight;
        (paramArrayOfSrvRecord[j]).sum = i;
      } 
    } 
    String str = null;
    boolean bool = (i == 0) ? 0 : random.nextInt(i + 1);
    for (int k = paramInt1; k <= paramInt2; k++) {
      if (paramArrayOfSrvRecord[k] != null && (paramArrayOfSrvRecord[k]).sum >= bool) {
        str = (paramArrayOfSrvRecord[k]).hostport;
        paramArrayOfSrvRecord[k] = null;
        break;
      } 
    } 
    return str;
  }
  
  static class SrvRecord extends Object implements Comparable<SrvRecord> {
    int priority;
    
    int weight;
    
    int sum;
    
    String hostport;
    
    SrvRecord(String param1String) throws Exception {
      StringTokenizer stringTokenizer = new StringTokenizer(param1String, " ");
      if (stringTokenizer.countTokens() == 4) {
        this.priority = Integer.parseInt(stringTokenizer.nextToken());
        this.weight = Integer.parseInt(stringTokenizer.nextToken());
        String str = stringTokenizer.nextToken();
        this.hostport = stringTokenizer.nextToken() + ":" + str;
      } else {
        throw new IllegalArgumentException();
      } 
    }
    
    public int compareTo(SrvRecord param1SrvRecord) { return (this.priority > param1SrvRecord.priority) ? 1 : ((this.priority < param1SrvRecord.priority) ? -1 : ((this.weight == 0 && param1SrvRecord.weight != 0) ? -1 : ((this.weight != 0 && param1SrvRecord.weight == 0) ? 1 : 0))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbServiceLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */