package sun.security.krb5.internal.ccache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import sun.misc.IOUtils;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.AuthorizationDataEntry;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class CCacheInputStream extends KrbDataInputStream implements FileCCacheConstants {
  private static boolean DEBUG = Krb5.DEBUG;
  
  public CCacheInputStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public Tag readTag() throws IOException {
    char[] arrayOfChar = new char[1024];
    int j = -1;
    Integer integer1 = null;
    Integer integer2 = null;
    int i = read(2);
    if (i < 0)
      throw new IOException("stop."); 
    if (i > arrayOfChar.length)
      throw new IOException("Invalid tag length."); 
    while (i > 0) {
      j = read(2);
      int k = read(2);
      switch (j) {
        case 1:
          integer1 = new Integer(read(4));
          integer2 = new Integer(read(4));
          break;
      } 
      i -= 4 + k;
    } 
    return new Tag(i, j, integer1, integer2);
  }
  
  public PrincipalName readPrincipal(int paramInt) throws IOException, RealmException {
    int i;
    Object object = null;
    if (paramInt == 1281) {
      i = 0;
    } else {
      i = read(4);
    } 
    int j = readLength4();
    ArrayList arrayList = new ArrayList();
    if (paramInt == 1281)
      j--; 
    for (b = 0; b <= j; b++) {
      int k = readLength4();
      byte[] arrayOfByte = IOUtils.readFully(this, k, true);
      arrayList.add(new String(arrayOfByte));
    } 
    if (arrayList.isEmpty())
      throw new IOException("No realm or principal"); 
    if (isRealm((String)arrayList.get(0))) {
      String str = (String)arrayList.remove(0);
      if (arrayList.isEmpty())
        throw new IOException("No principal name components"); 
      return new PrincipalName(i, (String[])arrayList.toArray(new String[arrayList.size()]), new Realm(str));
    } 
    try {
      return new PrincipalName(i, (String[])arrayList.toArray(new String[arrayList.size()]), Realm.getDefault());
    } catch (RealmException b) {
      RealmException realmException;
      return null;
    } 
  }
  
  boolean isRealm(String paramString) {
    try {
      Realm realm = new Realm(paramString);
    } catch (Exception exception) {
      return false;
    } 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ".");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      for (byte b = 0; b < str.length(); b++) {
        if (str.charAt(b) >= '')
          return false; 
      } 
    } 
    return true;
  }
  
  EncryptionKey readKey(int paramInt) throws IOException {
    int i = read(2);
    if (paramInt == 1283)
      read(2); 
    int j = readLength4();
    byte[] arrayOfByte = IOUtils.readFully(this, j, true);
    return new EncryptionKey(arrayOfByte, i, new Integer(paramInt));
  }
  
  long[] readTimes() throws IOException {
    long[] arrayOfLong = new long[4];
    arrayOfLong[0] = read(4) * 1000L;
    arrayOfLong[1] = read(4) * 1000L;
    arrayOfLong[2] = read(4) * 1000L;
    arrayOfLong[3] = read(4) * 1000L;
    return arrayOfLong;
  }
  
  boolean readskey() throws IOException { return !(read() == 0); }
  
  HostAddress[] readAddr() throws IOException, KrbApErrException {
    int i = readLength4();
    if (i > 0) {
      ArrayList arrayList = new ArrayList();
      for (byte b = 0; b < i; b++) {
        int j = read(2);
        int k = readLength4();
        if (k != 4 && k != 16) {
          if (DEBUG)
            System.out.println("Incorrect address format."); 
          return null;
        } 
        byte[] arrayOfByte = new byte[k];
        for (byte b1 = 0; b1 < k; b1++)
          arrayOfByte[b1] = (byte)read(1); 
        arrayList.add(new HostAddress(j, arrayOfByte));
      } 
      return (HostAddress[])arrayList.toArray(new HostAddress[arrayList.size()]);
    } 
    return null;
  }
  
  AuthorizationDataEntry[] readAuth() throws IOException {
    int i = readLength4();
    if (i > 0) {
      ArrayList arrayList = new ArrayList();
      byte[] arrayOfByte = null;
      for (byte b = 0; b < i; b++) {
        int j = read(2);
        int k = readLength4();
        arrayOfByte = IOUtils.readFully(this, k, true);
        arrayList.add(new AuthorizationDataEntry(j, arrayOfByte));
      } 
      return (AuthorizationDataEntry[])arrayList.toArray(new AuthorizationDataEntry[arrayList.size()]);
    } 
    return null;
  }
  
  byte[] readData() throws IOException {
    int i = readLength4();
    return (i == 0) ? null : IOUtils.readFully(this, i, true);
  }
  
  boolean[] readFlags() throws IOException {
    boolean[] arrayOfBoolean = new boolean[32];
    int i = read(4);
    if ((i & 0x40000000) == 1073741824)
      arrayOfBoolean[1] = true; 
    if ((i & 0x20000000) == 536870912)
      arrayOfBoolean[2] = true; 
    if ((i & 0x10000000) == 268435456)
      arrayOfBoolean[3] = true; 
    if ((i & 0x8000000) == 134217728)
      arrayOfBoolean[4] = true; 
    if ((i & 0x4000000) == 67108864)
      arrayOfBoolean[5] = true; 
    if ((i & 0x2000000) == 33554432)
      arrayOfBoolean[6] = true; 
    if ((i & 0x1000000) == 16777216)
      arrayOfBoolean[7] = true; 
    if ((i & 0x800000) == 8388608)
      arrayOfBoolean[8] = true; 
    if ((i & 0x400000) == 4194304)
      arrayOfBoolean[9] = true; 
    if ((i & 0x200000) == 2097152)
      arrayOfBoolean[10] = true; 
    if ((i & 0x100000) == 1048576)
      arrayOfBoolean[11] = true; 
    if (DEBUG) {
      String str = ">>> CCacheInputStream: readFlags() ";
      if (arrayOfBoolean[1] == true)
        str = str + " FORWARDABLE;"; 
      if (arrayOfBoolean[2] == true)
        str = str + " FORWARDED;"; 
      if (arrayOfBoolean[3] == true)
        str = str + " PROXIABLE;"; 
      if (arrayOfBoolean[4] == true)
        str = str + " PROXY;"; 
      if (arrayOfBoolean[5] == true)
        str = str + " MAY_POSTDATE;"; 
      if (arrayOfBoolean[6] == true)
        str = str + " POSTDATED;"; 
      if (arrayOfBoolean[7] == true)
        str = str + " INVALID;"; 
      if (arrayOfBoolean[8] == true)
        str = str + " RENEWABLE;"; 
      if (arrayOfBoolean[9] == true)
        str = str + " INITIAL;"; 
      if (arrayOfBoolean[10] == true)
        str = str + " PRE_AUTH;"; 
      if (arrayOfBoolean[11] == true)
        str = str + " HW_AUTH;"; 
      System.out.println(str);
    } 
    return arrayOfBoolean;
  }
  
  Credentials readCred(int paramInt) throws IOException, RealmException, KrbApErrException, Asn1Exception {
    PrincipalName principalName1 = null;
    try {
      principalName1 = readPrincipal(paramInt);
    } catch (Exception exception) {}
    if (DEBUG)
      System.out.println(">>>DEBUG <CCacheInputStream>  client principal is " + principalName1); 
    PrincipalName principalName2 = null;
    try {
      principalName2 = readPrincipal(paramInt);
    } catch (Exception exception) {}
    if (DEBUG)
      System.out.println(">>>DEBUG <CCacheInputStream> server principal is " + principalName2); 
    EncryptionKey encryptionKey = readKey(paramInt);
    if (DEBUG)
      System.out.println(">>>DEBUG <CCacheInputStream> key type: " + encryptionKey.getEType()); 
    long[] arrayOfLong = readTimes();
    KerberosTime kerberosTime1 = new KerberosTime(arrayOfLong[0]);
    KerberosTime kerberosTime2 = (arrayOfLong[1] == 0L) ? null : new KerberosTime(arrayOfLong[1]);
    KerberosTime kerberosTime3 = new KerberosTime(arrayOfLong[2]);
    KerberosTime kerberosTime4 = (arrayOfLong[3] == 0L) ? null : new KerberosTime(arrayOfLong[3]);
    if (DEBUG) {
      System.out.println(">>>DEBUG <CCacheInputStream> auth time: " + kerberosTime1.toDate().toString());
      System.out.println(">>>DEBUG <CCacheInputStream> start time: " + ((kerberosTime2 == null) ? "null" : kerberosTime2.toDate().toString()));
      System.out.println(">>>DEBUG <CCacheInputStream> end time: " + kerberosTime3.toDate().toString());
      System.out.println(">>>DEBUG <CCacheInputStream> renew_till time: " + ((kerberosTime4 == null) ? "null" : kerberosTime4.toDate().toString()));
    } 
    boolean bool = readskey();
    boolean[] arrayOfBoolean = readFlags();
    TicketFlags ticketFlags = new TicketFlags(arrayOfBoolean);
    HostAddress[] arrayOfHostAddress = readAddr();
    HostAddresses hostAddresses = null;
    if (arrayOfHostAddress != null)
      hostAddresses = new HostAddresses(arrayOfHostAddress); 
    AuthorizationDataEntry[] arrayOfAuthorizationDataEntry = readAuth();
    AuthorizationData authorizationData = null;
    if (arrayOfAuthorizationDataEntry != null)
      authorizationData = new AuthorizationData(arrayOfAuthorizationDataEntry); 
    byte[] arrayOfByte1 = readData();
    byte[] arrayOfByte2 = readData();
    if (principalName1 == null || principalName2 == null)
      return null; 
    try {
      return new Credentials(principalName1, principalName2, encryptionKey, kerberosTime1, kerberosTime2, kerberosTime3, kerberosTime4, bool, ticketFlags, hostAddresses, authorizationData, (arrayOfByte1 != null) ? new Ticket(arrayOfByte1) : null, (arrayOfByte2 != null) ? new Ticket(arrayOfByte2) : null);
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ccache\CCacheInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */