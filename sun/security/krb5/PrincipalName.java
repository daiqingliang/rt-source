package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import sun.misc.Unsafe;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PrincipalName implements Cloneable {
  public static final int KRB_NT_UNKNOWN = 0;
  
  public static final int KRB_NT_PRINCIPAL = 1;
  
  public static final int KRB_NT_SRV_INST = 2;
  
  public static final int KRB_NT_SRV_HST = 3;
  
  public static final int KRB_NT_SRV_XHST = 4;
  
  public static final int KRB_NT_UID = 5;
  
  public static final String TGS_DEFAULT_SRV_NAME = "krbtgt";
  
  public static final int TGS_DEFAULT_NT = 2;
  
  public static final char NAME_COMPONENT_SEPARATOR = '/';
  
  public static final char NAME_REALM_SEPARATOR = '@';
  
  public static final char REALM_COMPONENT_SEPARATOR = '.';
  
  public static final String NAME_COMPONENT_SEPARATOR_STR = "/";
  
  public static final String NAME_REALM_SEPARATOR_STR = "@";
  
  public static final String REALM_COMPONENT_SEPARATOR_STR = ".";
  
  private final int nameType;
  
  private final String[] nameStrings;
  
  private final Realm nameRealm;
  
  private final boolean realmDeduced;
  
  private String salt = null;
  
  private static final long NAME_STRINGS_OFFSET;
  
  private static final Unsafe UNSAFE;
  
  public PrincipalName(int paramInt, String[] paramArrayOfString, Realm paramRealm) {
    if (paramRealm == null)
      throw new IllegalArgumentException("Null realm not allowed"); 
    validateNameStrings(paramArrayOfString);
    this.nameType = paramInt;
    this.nameStrings = (String[])paramArrayOfString.clone();
    this.nameRealm = paramRealm;
    this.realmDeduced = false;
  }
  
  public PrincipalName(String[] paramArrayOfString, String paramString) throws RealmException { this(0, paramArrayOfString, new Realm(paramString)); }
  
  private static void validateNameStrings(String[] paramArrayOfString) {
    if (paramArrayOfString == null)
      throw new IllegalArgumentException("Null nameStrings not allowed"); 
    if (paramArrayOfString.length == 0)
      throw new IllegalArgumentException("Empty nameStrings not allowed"); 
    for (String str : paramArrayOfString) {
      if (str == null)
        throw new IllegalArgumentException("Null nameString not allowed"); 
      if (str.isEmpty())
        throw new IllegalArgumentException("Empty nameString not allowed"); 
    } 
  }
  
  public Object clone() {
    try {
      PrincipalName principalName = (PrincipalName)super.clone();
      UNSAFE.putObject(this, NAME_STRINGS_OFFSET, this.nameStrings.clone());
      return principalName;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new AssertionError("Should never happen");
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof PrincipalName) {
      PrincipalName principalName = (PrincipalName)paramObject;
      return (this.nameRealm.equals(principalName.nameRealm) && Arrays.equals(this.nameStrings, principalName.nameStrings));
    } 
    return false;
  }
  
  public PrincipalName(DerValue paramDerValue, Realm paramRealm) throws Asn1Exception, IOException {
    if (paramRealm == null)
      throw new IllegalArgumentException("Null realm not allowed"); 
    this.realmDeduced = false;
    this.nameRealm = paramRealm;
    if (paramDerValue == null)
      throw new IllegalArgumentException("Null encoding not allowed"); 
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      BigInteger bigInteger = derValue.getData().getBigInteger();
      this.nameType = bigInteger.intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      DerValue derValue1 = derValue.getData().getDerValue();
      if (derValue1.getTag() != 48)
        throw new Asn1Exception(906); 
      Vector vector = new Vector();
      while (derValue1.getData().available() > 0) {
        DerValue derValue2 = derValue1.getData().getDerValue();
        String str = (new KerberosString(derValue2)).toString();
        vector.addElement(str);
      } 
      this.nameStrings = new String[vector.size()];
      vector.copyInto(this.nameStrings);
      validateNameStrings(this.nameStrings);
    } else {
      throw new Asn1Exception(906);
    } 
  }
  
  public static PrincipalName parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean, Realm paramRealm) throws Asn1Exception, IOException, RealmException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    if (paramRealm == null)
      paramRealm = Realm.getDefault(); 
    return new PrincipalName(derValue2, paramRealm);
  }
  
  private static String[] parseName(String paramString) {
    Vector vector = new Vector();
    String str = paramString;
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < str.length()) {
      if (str.charAt(b1) == '/') {
        if (b1 > 0 && str.charAt(b1 - 1) == '\\') {
          str = str.substring(0, b1 - 1) + str.substring(b1, str.length());
          continue;
        } 
        if (b2 <= b1) {
          String str1 = str.substring(b2, b1);
          vector.addElement(str1);
        } 
        b2 = b1 + 1;
      } else if (str.charAt(b1) == '@') {
        if (b1 > 0 && str.charAt(b1 - 1) == '\\') {
          str = str.substring(0, b1 - 1) + str.substring(b1, str.length());
          continue;
        } 
        if (b2 < b1) {
          String str1 = str.substring(b2, b1);
          vector.addElement(str1);
        } 
        b2 = b1 + 1;
        break;
      } 
      b1++;
    } 
    if (b1 == str.length()) {
      String str1 = str.substring(b2, b1);
      vector.addElement(str1);
    } 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public PrincipalName(String paramString1, int paramInt, String paramString2) throws RealmException {
    if (paramString1 == null)
      throw new IllegalArgumentException("Null name not allowed"); 
    String[] arrayOfString = parseName(paramString1);
    validateNameStrings(arrayOfString);
    if (paramString2 == null)
      paramString2 = Realm.parseRealmAtSeparator(paramString1); 
    this.realmDeduced = (paramString2 == null);
    switch (paramInt) {
      case 3:
        if (arrayOfString.length >= 2) {
          String str = arrayOfString[1];
          try {
            String str1 = InetAddress.getByName(str).getCanonicalHostName();
            if (str1.toLowerCase(Locale.ENGLISH).startsWith(str.toLowerCase(Locale.ENGLISH) + "."))
              str = str1; 
          } catch (UnknownHostException|SecurityException unknownHostException) {}
          if (str.endsWith("."))
            str = str.substring(0, str.length() - 1); 
          arrayOfString[1] = str.toLowerCase(Locale.ENGLISH);
        } 
        this.nameStrings = arrayOfString;
        this.nameType = paramInt;
        if (paramString2 != null) {
          this.nameRealm = new Realm(paramString2);
        } else {
          String str = mapHostToRealm(arrayOfString[1]);
          if (str != null) {
            this.nameRealm = new Realm(str);
          } else {
            this.nameRealm = Realm.getDefault();
          } 
        } 
        return;
      case 0:
      case 1:
      case 2:
      case 4:
      case 5:
        this.nameStrings = arrayOfString;
        this.nameType = paramInt;
        if (paramString2 != null) {
          this.nameRealm = new Realm(paramString2);
        } else {
          this.nameRealm = Realm.getDefault();
        } 
        return;
    } 
    throw new IllegalArgumentException("Illegal name type");
  }
  
  public PrincipalName(String paramString, int paramInt) throws RealmException { this(paramString, paramInt, (String)null); }
  
  public PrincipalName(String paramString) throws RealmException { this(paramString, 0); }
  
  public PrincipalName(String paramString1, String paramString2) throws RealmException { this(paramString1, 0, paramString2); }
  
  public static PrincipalName tgsService(String paramString1, String paramString2) throws KrbException { return new PrincipalName(2, new String[] { "krbtgt", paramString1 }, new Realm(paramString2)); }
  
  public String getRealmAsString() { return getRealmString(); }
  
  public String getPrincipalNameAsString() {
    StringBuffer stringBuffer = new StringBuffer(this.nameStrings[0]);
    for (byte b = 1; b < this.nameStrings.length; b++)
      stringBuffer.append(this.nameStrings[b]); 
    return stringBuffer.toString();
  }
  
  public int hashCode() { return toString().hashCode(); }
  
  public String getName() { return toString(); }
  
  public int getNameType() { return this.nameType; }
  
  public String[] getNameStrings() { return (String[])this.nameStrings.clone(); }
  
  public byte[][] toByteArray() {
    byte[][] arrayOfByte = new byte[this.nameStrings.length][];
    for (byte b = 0; b < this.nameStrings.length; b++) {
      arrayOfByte[b] = new byte[this.nameStrings[b].length()];
      arrayOfByte[b] = this.nameStrings[b].getBytes();
    } 
    return arrayOfByte;
  }
  
  public String getRealmString() { return this.nameRealm.toString(); }
  
  public Realm getRealm() { return this.nameRealm; }
  
  public String getSalt() {
    if (this.salt == null) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(this.nameRealm.toString());
      for (byte b = 0; b < this.nameStrings.length; b++)
        stringBuffer.append(this.nameStrings[b]); 
      return stringBuffer.toString();
    } 
    return this.salt;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.nameStrings.length; b++) {
      if (b)
        stringBuffer.append("/"); 
      stringBuffer.append(this.nameStrings[b]);
    } 
    stringBuffer.append("@");
    stringBuffer.append(this.nameRealm.toString());
    return stringBuffer.toString();
  }
  
  public String getNameString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.nameStrings.length; b++) {
      if (b)
        stringBuffer.append("/"); 
      stringBuffer.append(this.nameStrings[b]);
    } 
    return stringBuffer.toString();
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    BigInteger bigInteger = BigInteger.valueOf(this.nameType);
    derOutputStream2.putInteger(bigInteger);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[this.nameStrings.length];
    for (byte b = 0; b < this.nameStrings.length; b++)
      arrayOfDerValue[b] = (new KerberosString(this.nameStrings[b])).toDerValue(); 
    derOutputStream2.putSequence(arrayOfDerValue);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public boolean match(PrincipalName paramPrincipalName) {
    boolean bool = true;
    if (this.nameRealm != null && paramPrincipalName.nameRealm != null && !this.nameRealm.toString().equalsIgnoreCase(paramPrincipalName.nameRealm.toString()))
      bool = false; 
    if (this.nameStrings.length != paramPrincipalName.nameStrings.length) {
      bool = false;
    } else {
      for (byte b = 0; b < this.nameStrings.length; b++) {
        if (!this.nameStrings[b].equalsIgnoreCase(paramPrincipalName.nameStrings[b]))
          bool = false; 
      } 
    } 
    return bool;
  }
  
  public void writePrincipal(CCacheOutputStream paramCCacheOutputStream) throws IOException {
    paramCCacheOutputStream.write32(this.nameType);
    paramCCacheOutputStream.write32(this.nameStrings.length);
    byte[] arrayOfByte1 = null;
    arrayOfByte1 = this.nameRealm.toString().getBytes();
    paramCCacheOutputStream.write32(arrayOfByte1.length);
    paramCCacheOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte2 = null;
    for (byte b = 0; b < this.nameStrings.length; b++) {
      arrayOfByte2 = this.nameStrings[b].getBytes();
      paramCCacheOutputStream.write32(arrayOfByte2.length);
      paramCCacheOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
    } 
  }
  
  public String getInstanceComponent() { return (this.nameStrings != null && this.nameStrings.length >= 2) ? new String(this.nameStrings[1]) : null; }
  
  static String mapHostToRealm(String paramString) {
    String str = null;
    try {
      String str1 = null;
      Config config = Config.getInstance();
      if ((str = config.get(new String[] { "domain_realm", paramString })) != null)
        return str; 
      for (byte b = 1; b < paramString.length(); b++) {
        if (paramString.charAt(b) == '.' && b != paramString.length() - 1) {
          str1 = paramString.substring(b);
          str = config.get(new String[] { "domain_realm", str1 });
          if (str != null)
            break; 
          str1 = paramString.substring(b + 1);
          str = config.get(new String[] { "domain_realm", str1 });
          if (str != null)
            break; 
        } 
      } 
    } catch (KrbException krbException) {}
    return str;
  }
  
  public boolean isRealmDeduced() { return this.realmDeduced; }
  
  static  {
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      NAME_STRINGS_OFFSET = unsafe.objectFieldOffset(PrincipalName.class.getDeclaredField("nameStrings"));
      UNSAFE = unsafe;
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new Error(reflectiveOperationException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\PrincipalName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */