package sun.security.krb5.internal.ktab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.Config;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.crypto.EType;

public class KeyTab implements KeyTabConstants {
  private static final boolean DEBUG = Krb5.DEBUG;
  
  private static String defaultTabName = null;
  
  private static Map<String, KeyTab> map = new HashMap();
  
  private boolean isMissing = false;
  
  private boolean isValid = true;
  
  private final String tabName;
  
  private long lastModified;
  
  private int kt_vno = 1282;
  
  private Vector<KeyTabEntry> entries = new Vector();
  
  private KeyTab(String paramString) {
    this.tabName = paramString;
    try {
      this.lastModified = (new File(this.tabName)).lastModified();
      try (KeyTabInputStream null = new KeyTabInputStream(new FileInputStream(paramString))) {
        load(keyTabInputStream);
      } 
    } catch (FileNotFoundException fileNotFoundException) {
      this.entries.clear();
      this.isMissing = true;
    } catch (Exception exception) {
      this.entries.clear();
      this.isValid = false;
    } 
  }
  
  private static KeyTab getInstance0(String paramString) {
    long l = (new File(paramString)).lastModified();
    KeyTab keyTab1 = (KeyTab)map.get(paramString);
    if (keyTab1 != null && keyTab1.isValid() && keyTab1.lastModified == l)
      return keyTab1; 
    KeyTab keyTab2 = new KeyTab(paramString);
    if (keyTab2.isValid()) {
      map.put(paramString, keyTab2);
      return keyTab2;
    } 
    return (keyTab1 != null) ? keyTab1 : keyTab2;
  }
  
  public static KeyTab getInstance(String paramString) { return (paramString == null) ? getInstance() : getInstance0(normalize(paramString)); }
  
  public static KeyTab getInstance(File paramFile) { return (paramFile == null) ? getInstance() : getInstance0(paramFile.getPath()); }
  
  public static KeyTab getInstance() { return getInstance(getDefaultTabName()); }
  
  public boolean isMissing() { return this.isMissing; }
  
  public boolean isValid() { return this.isValid; }
  
  private static String getDefaultTabName() {
    if (defaultTabName != null)
      return defaultTabName; 
    String str = null;
    try {
      String str1 = Config.getInstance().get(new String[] { "libdefaults", "default_keytab_name" });
      if (str1 != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(str1, " ");
        do {
          str = normalize(stringTokenizer.nextToken());
        } while (stringTokenizer.hasMoreTokens() && !(new File(str)).exists());
      } 
    } catch (KrbException krbException) {
      str = null;
    } 
    if (str == null) {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
      if (str1 == null)
        str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir")); 
      str = str1 + File.separator + "krb5.keytab";
    } 
    defaultTabName = str;
    return str;
  }
  
  public static String normalize(String paramString) {
    String str;
    if (paramString.length() >= 5 && paramString.substring(0, 5).equalsIgnoreCase("FILE:")) {
      str = paramString.substring(5);
    } else if (paramString.length() >= 9 && paramString.substring(0, 9).equalsIgnoreCase("ANY:FILE:")) {
      str = paramString.substring(9);
    } else if (paramString.length() >= 7 && paramString.substring(0, 7).equalsIgnoreCase("SRVTAB:")) {
      str = paramString.substring(7);
    } else {
      str = paramString;
    } 
    return str;
  }
  
  private void load(KeyTabInputStream paramKeyTabInputStream) throws IOException, RealmException {
    this.entries.clear();
    this.kt_vno = paramKeyTabInputStream.readVersion();
    if (this.kt_vno == 1281)
      paramKeyTabInputStream.setNativeByteOrder(); 
    int i = 0;
    while (paramKeyTabInputStream.available() > 0) {
      i = paramKeyTabInputStream.readEntryLength();
      KeyTabEntry keyTabEntry = paramKeyTabInputStream.readEntry(i, this.kt_vno);
      if (DEBUG)
        System.out.println(">>> KeyTab: load() entry length: " + i + "; type: " + ((keyTabEntry != null) ? keyTabEntry.keyType : 0)); 
      if (keyTabEntry != null)
        this.entries.addElement(keyTabEntry); 
    } 
  }
  
  public PrincipalName getOneName() {
    int i = this.entries.size();
    return (i > 0) ? ((KeyTabEntry)this.entries.elementAt(i - 1)).service : null;
  }
  
  public EncryptionKey[] readServiceKeys(PrincipalName paramPrincipalName) {
    int i = this.entries.size();
    ArrayList arrayList = new ArrayList(i);
    if (DEBUG)
      System.out.println("Looking for keys for: " + paramPrincipalName); 
    for (int j = i - 1; j >= 0; j--) {
      KeyTabEntry keyTabEntry = (KeyTabEntry)this.entries.elementAt(j);
      if (keyTabEntry.service.match(paramPrincipalName))
        if (EType.isSupported(keyTabEntry.keyType)) {
          EncryptionKey encryptionKey = new EncryptionKey(keyTabEntry.keyblock, keyTabEntry.keyType, new Integer(keyTabEntry.keyVersion));
          arrayList.add(encryptionKey);
          if (DEBUG)
            System.out.println("Added key: " + keyTabEntry.keyType + "version: " + keyTabEntry.keyVersion); 
        } else if (DEBUG) {
          System.out.println("Found unsupported keytype (" + keyTabEntry.keyType + ") for " + paramPrincipalName);
        }  
    } 
    i = arrayList.size();
    EncryptionKey[] arrayOfEncryptionKey = (EncryptionKey[])arrayList.toArray(new EncryptionKey[i]);
    Arrays.sort(arrayOfEncryptionKey, new Comparator<EncryptionKey>() {
          public int compare(EncryptionKey param1EncryptionKey1, EncryptionKey param1EncryptionKey2) { return param1EncryptionKey2.getKeyVersionNumber().intValue() - param1EncryptionKey1.getKeyVersionNumber().intValue(); }
        });
    return arrayOfEncryptionKey;
  }
  
  public boolean findServiceEntry(PrincipalName paramPrincipalName) {
    for (byte b = 0; b < this.entries.size(); b++) {
      KeyTabEntry keyTabEntry = (KeyTabEntry)this.entries.elementAt(b);
      if (keyTabEntry.service.match(paramPrincipalName)) {
        if (EType.isSupported(keyTabEntry.keyType))
          return true; 
        if (DEBUG)
          System.out.println("Found unsupported keytype (" + keyTabEntry.keyType + ") for " + paramPrincipalName); 
      } 
    } 
    return false;
  }
  
  public String tabName() { return this.tabName; }
  
  public void addEntry(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, boolean paramBoolean) throws KrbException { addEntry(paramPrincipalName, paramPrincipalName.getSalt(), paramArrayOfChar, paramInt, paramBoolean); }
  
  public void addEntry(PrincipalName paramPrincipalName, String paramString, char[] paramArrayOfChar, int paramInt, boolean paramBoolean) throws KrbException {
    EncryptionKey[] arrayOfEncryptionKey = EncryptionKey.acquireSecretKeys(paramArrayOfChar, paramString);
    int i = 0;
    int j;
    for (j = this.entries.size() - 1; j >= 0; j--) {
      KeyTabEntry keyTabEntry = (KeyTabEntry)this.entries.get(j);
      if (keyTabEntry.service.match(paramPrincipalName)) {
        if (keyTabEntry.keyVersion > i)
          i = keyTabEntry.keyVersion; 
        if (!paramBoolean || keyTabEntry.keyVersion == paramInt)
          this.entries.removeElementAt(j); 
      } 
    } 
    if (paramInt == -1)
      paramInt = i + 1; 
    for (j = 0; arrayOfEncryptionKey != null && j < arrayOfEncryptionKey.length; j++) {
      int k = arrayOfEncryptionKey[j].getEType();
      byte[] arrayOfByte = arrayOfEncryptionKey[j].getBytes();
      KeyTabEntry keyTabEntry = new KeyTabEntry(paramPrincipalName, paramPrincipalName.getRealm(), new KerberosTime(System.currentTimeMillis()), paramInt, k, arrayOfByte);
      this.entries.addElement(keyTabEntry);
    } 
  }
  
  public KeyTabEntry[] getEntries() {
    KeyTabEntry[] arrayOfKeyTabEntry = new KeyTabEntry[this.entries.size()];
    for (byte b = 0; b < arrayOfKeyTabEntry.length; b++)
      arrayOfKeyTabEntry[b] = (KeyTabEntry)this.entries.elementAt(b); 
    return arrayOfKeyTabEntry;
  }
  
  public static KeyTab create() {
    String str = getDefaultTabName();
    return create(str);
  }
  
  public static KeyTab create(String paramString) {
    try (KeyTabOutputStream null = new KeyTabOutputStream(new FileOutputStream(paramString))) {
      keyTabOutputStream.writeVersion(1282);
    } 
    return new KeyTab(paramString);
  }
  
  public void save() throws IOException {
    try (KeyTabOutputStream null = new KeyTabOutputStream(new FileOutputStream(this.tabName))) {
      keyTabOutputStream.writeVersion(this.kt_vno);
      for (b = 0; b < this.entries.size(); b++)
        keyTabOutputStream.writeEntry((KeyTabEntry)this.entries.elementAt(b)); 
    } 
  }
  
  public int deleteEntries(PrincipalName paramPrincipalName, int paramInt1, int paramInt2) {
    byte b = 0;
    HashMap hashMap = new HashMap();
    int i;
    for (i = this.entries.size() - 1; i >= 0; i--) {
      KeyTabEntry keyTabEntry = (KeyTabEntry)this.entries.get(i);
      if (paramPrincipalName.match(keyTabEntry.getService()) && (paramInt1 == -1 || keyTabEntry.keyType == paramInt1))
        if (paramInt2 == -2) {
          if (hashMap.containsKey(Integer.valueOf(keyTabEntry.keyType))) {
            int j = ((Integer)hashMap.get(Integer.valueOf(keyTabEntry.keyType))).intValue();
            if (keyTabEntry.keyVersion > j)
              hashMap.put(Integer.valueOf(keyTabEntry.keyType), Integer.valueOf(keyTabEntry.keyVersion)); 
          } else {
            hashMap.put(Integer.valueOf(keyTabEntry.keyType), Integer.valueOf(keyTabEntry.keyVersion));
          } 
        } else if (paramInt2 == -1 || keyTabEntry.keyVersion == paramInt2) {
          this.entries.removeElementAt(i);
          b++;
        }  
    } 
    if (paramInt2 == -2)
      for (i = this.entries.size() - 1; i >= 0; i--) {
        KeyTabEntry keyTabEntry = (KeyTabEntry)this.entries.get(i);
        if (paramPrincipalName.match(keyTabEntry.getService()) && (paramInt1 == -1 || keyTabEntry.keyType == paramInt1)) {
          int j = ((Integer)hashMap.get(Integer.valueOf(keyTabEntry.keyType))).intValue();
          if (keyTabEntry.keyVersion != j) {
            this.entries.removeElementAt(i);
            b++;
          } 
        } 
      }  
    return b;
  }
  
  public void createVersion(File paramFile) throws IOException {
    try (KeyTabOutputStream null = new KeyTabOutputStream(new FileOutputStream(paramFile))) {
      keyTabOutputStream.write16(1282);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ktab\KeyTab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */