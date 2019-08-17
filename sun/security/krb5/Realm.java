package sun.security.krb5;

import java.io.IOException;
import java.security.AccessController;
import java.util.LinkedList;
import sun.security.action.GetBooleanAction;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Realm implements Cloneable {
  public static final boolean AUTODEDUCEREALM = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.autodeducerealm"))).booleanValue();
  
  private final String realm;
  
  public Realm(String paramString) throws RealmException { this.realm = parseRealm(paramString); }
  
  public static Realm getDefault() throws RealmException {
    try {
      return new Realm(Config.getInstance().getDefaultRealm());
    } catch (RealmException realmException) {
      throw realmException;
    } catch (KrbException krbException) {
      throw new RealmException(krbException);
    } 
  }
  
  public Object clone() { return this; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Realm))
      return false; 
    Realm realm1 = (Realm)paramObject;
    return this.realm.equals(realm1.realm);
  }
  
  public int hashCode() { return this.realm.hashCode(); }
  
  public Realm(DerValue paramDerValue) throws Asn1Exception, RealmException, IOException {
    if (paramDerValue == null)
      throw new IllegalArgumentException("encoding can not be null"); 
    this.realm = (new KerberosString(paramDerValue)).toString();
    if (this.realm == null || this.realm.length() == 0)
      throw new RealmException(601); 
    if (!isValidRealmString(this.realm))
      throw new RealmException(600); 
  }
  
  public String toString() { return this.realm; }
  
  public static String parseRealmAtSeparator(String paramString) throws RealmException {
    if (paramString == null)
      throw new IllegalArgumentException("null input name is not allowed"); 
    String str1 = new String(paramString);
    String str2 = null;
    for (byte b = 0; b < str1.length(); b++) {
      if (str1.charAt(b) == '@' && (b == 0 || str1.charAt(b - 1) != '\\')) {
        if (b + 1 < str1.length()) {
          str2 = str1.substring(b + 1, str1.length());
          break;
        } 
        throw new IllegalArgumentException("empty realm part not allowed");
      } 
    } 
    if (str2 != null) {
      if (str2.length() == 0)
        throw new RealmException(601); 
      if (!isValidRealmString(str2))
        throw new RealmException(600); 
    } 
    return str2;
  }
  
  public static String parseRealmComponent(String paramString) throws RealmException {
    if (paramString == null)
      throw new IllegalArgumentException("null input name is not allowed"); 
    String str1 = new String(paramString);
    String str2 = null;
    for (byte b = 0; b < str1.length(); b++) {
      if (str1.charAt(b) == '.' && (b == 0 || str1.charAt(b - 1) != '\\')) {
        if (b + 1 < str1.length())
          str2 = str1.substring(b + 1, str1.length()); 
        break;
      } 
    } 
    return str2;
  }
  
  protected static String parseRealm(String paramString) throws RealmException {
    String str = parseRealmAtSeparator(paramString);
    if (str == null)
      str = paramString; 
    if (str == null || str.length() == 0)
      throw new RealmException(601); 
    if (!isValidRealmString(str))
      throw new RealmException(600); 
    return str;
  }
  
  protected static boolean isValidRealmString(String paramString) {
    if (paramString == null)
      return false; 
    if (paramString.length() == 0)
      return false; 
    for (byte b = 0; b < paramString.length(); b++) {
      if (paramString.charAt(b) == '/' || paramString.charAt(b) == ':' || paramString.charAt(b) == '\000')
        return false; 
    } 
    return true;
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putDerValue((new KerberosString(this.realm)).toDerValue());
    return derOutputStream.toByteArray();
  }
  
  public static Realm parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException, RealmException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new Realm(derValue2);
  }
  
  public static String[] getRealmsList(String paramString1, String paramString2) {
    try {
      return parseCapaths(paramString1, paramString2);
    } catch (KrbException krbException) {
      return parseHierarchy(paramString1, paramString2);
    } 
  }
  
  private static String[] parseCapaths(String paramString1, String paramString2) {
    Config config = Config.getInstance();
    if (!config.exists(new String[] { "capaths", paramString1, paramString2 }))
      throw new KrbException("No conf"); 
    LinkedList linkedList = new LinkedList();
    for (String str = paramString2;; str = (String)linkedList.getFirst()) {
      String str1 = config.getAll(new String[] { "capaths", paramString1, str });
      if (str1 == null)
        break; 
      String[] arrayOfString = str1.split("\\s+");
      boolean bool = false;
      for (int i = arrayOfString.length - 1; i >= 0; i--) {
        if (!linkedList.contains(arrayOfString[i]) && !arrayOfString[i].equals(".") && !arrayOfString[i].equals(paramString1) && !arrayOfString[i].equals(paramString2) && !arrayOfString[i].equals(str)) {
          bool = true;
          linkedList.addFirst(arrayOfString[i]);
        } 
      } 
      if (!bool)
        break; 
    } 
    linkedList.addFirst(paramString1);
    return (String[])linkedList.toArray(new String[linkedList.size()]);
  }
  
  private static String[] parseHierarchy(String paramString1, String paramString2) {
    String[] arrayOfString1 = paramString1.split("\\.");
    String[] arrayOfString2 = paramString2.split("\\.");
    int i = arrayOfString1.length;
    int j = arrayOfString2.length;
    boolean bool = false;
    while (j >= 0 && --i >= 0 && arrayOfString2[--j].equals(arrayOfString1[i])) {
      bool = true;
      j--;
      i--;
    } 
    LinkedList linkedList = new LinkedList();
    int k;
    for (k = 0; k <= i; k++)
      linkedList.addLast(subStringFrom(arrayOfString1, k)); 
    if (bool)
      linkedList.addLast(subStringFrom(arrayOfString1, i + 1)); 
    for (k = j; k >= 0; k--)
      linkedList.addLast(subStringFrom(arrayOfString2, k)); 
    linkedList.removeLast();
    return (String[])linkedList.toArray(new String[linkedList.size()]);
  }
  
  private static String subStringFrom(String[] paramArrayOfString, int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = paramInt; i < paramArrayOfString.length; i++) {
      if (stringBuilder.length() != 0)
        stringBuilder.append('.'); 
      stringBuilder.append(paramArrayOfString[i]);
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\Realm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */