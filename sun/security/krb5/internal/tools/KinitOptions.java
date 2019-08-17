package sun.security.krb5.internal.tools;

import java.io.FileInputStream;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ccache.CCacheInputStream;
import sun.security.krb5.internal.ccache.FileCredentialsCache;

class KinitOptions {
  public boolean validate = false;
  
  public short forwardable = -1;
  
  public short proxiable = -1;
  
  public boolean renew = false;
  
  public KerberosTime lifetime;
  
  public KerberosTime renewable_lifetime;
  
  public String target_service;
  
  public String keytab_file;
  
  public String cachename;
  
  private PrincipalName principal;
  
  public String realm;
  
  char[] password = null;
  
  public boolean keytab;
  
  private boolean DEBUG = Krb5.DEBUG;
  
  private boolean includeAddresses = true;
  
  private boolean useKeytab = false;
  
  private String ktabName;
  
  public KinitOptions() throws RuntimeException, RealmException {
    this.cachename = FileCredentialsCache.getDefaultCacheName();
    if (this.cachename == null)
      throw new RuntimeException("default cache name error"); 
    this.principal = getDefaultPrincipal();
  }
  
  public void setKDCRealm(String paramString) throws RealmException { this.realm = paramString; }
  
  public String getKDCRealm() { return (this.realm == null && this.principal != null) ? this.principal.getRealmString() : null; }
  
  public KinitOptions(String[] paramArrayOfString) throws KrbException, RuntimeException, IOException {
    String str = null;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].equals("-f")) {
        this.forwardable = 1;
      } else if (paramArrayOfString[b].equals("-p")) {
        this.proxiable = 1;
      } else if (paramArrayOfString[b].equals("-c")) {
        if (paramArrayOfString[b + true].startsWith("-"))
          throw new IllegalArgumentException("input format  not correct:  -c  option must be followed by the cache name"); 
        this.cachename = paramArrayOfString[++b];
        if (this.cachename.length() >= 5 && this.cachename.substring(0, 5).equalsIgnoreCase("FILE:"))
          this.cachename = this.cachename.substring(5); 
      } else if (paramArrayOfString[b].equals("-A")) {
        this.includeAddresses = false;
      } else if (paramArrayOfString[b].equals("-k")) {
        this.useKeytab = true;
      } else if (paramArrayOfString[b].equals("-t")) {
        if (this.ktabName != null)
          throw new IllegalArgumentException("-t option/keytab file name repeated"); 
        if (b + 1 < paramArrayOfString.length) {
          this.ktabName = paramArrayOfString[++b];
        } else {
          throw new IllegalArgumentException("-t option requires keytab file name");
        } 
        this.useKeytab = true;
      } else if (paramArrayOfString[b].equalsIgnoreCase("-help")) {
        printHelp();
        System.exit(0);
      } else if (str == null) {
        str = paramArrayOfString[b];
        try {
          this.principal = new PrincipalName(str);
        } catch (Exception exception) {
          throw new IllegalArgumentException("invalid Principal name: " + str + exception.getMessage());
        } 
      } else if (this.password == null) {
        this.password = paramArrayOfString[b].toCharArray();
      } else {
        throw new IllegalArgumentException("too many parameters");
      } 
    } 
    if (this.cachename == null) {
      this.cachename = FileCredentialsCache.getDefaultCacheName();
      if (this.cachename == null)
        throw new RuntimeException("default cache name error"); 
    } 
    if (this.principal == null)
      this.principal = getDefaultPrincipal(); 
  }
  
  PrincipalName getDefaultPrincipal() {
    try {
      CCacheInputStream cCacheInputStream = new CCacheInputStream(new FileInputStream(this.cachename));
      int i;
      if ((i = cCacheInputStream.readVersion()) == 1284) {
        cCacheInputStream.readTag();
      } else if (i == 1281 || i == 1282) {
        cCacheInputStream.setNativeByteOrder();
      } 
      PrincipalName principalName = cCacheInputStream.readPrincipal(i);
      cCacheInputStream.close();
      if (this.DEBUG)
        System.out.println(">>>KinitOptions principal name from the cache is :" + principalName); 
      return principalName;
    } catch (IOException iOException) {
      if (this.DEBUG)
        iOException.printStackTrace(); 
    } catch (RealmException realmException) {
      if (this.DEBUG)
        realmException.printStackTrace(); 
    } 
    String str = System.getProperty("user.name");
    if (this.DEBUG)
      System.out.println(">>>KinitOptions default username is :" + str); 
    try {
      return new PrincipalName(str);
    } catch (RealmException realmException) {
      if (this.DEBUG) {
        System.out.println("Exception in getting principal name " + realmException.getMessage());
        realmException.printStackTrace();
      } 
      return null;
    } 
  }
  
  void printHelp() throws RuntimeException, RealmException {
    System.out.println("Usage: kinit [-A] [-f] [-p] [-c cachename] [[-k [-t keytab_file_name]] [principal] [password]");
    System.out.println("\tavailable options to Kerberos 5 ticket request:");
    System.out.println("\t    -A   do not include addresses");
    System.out.println("\t    -f   forwardable");
    System.out.println("\t    -p   proxiable");
    System.out.println("\t    -c   cache name (i.e., FILE:\\d:\\myProfiles\\mykrb5cache)");
    System.out.println("\t    -k   use keytab");
    System.out.println("\t    -t   keytab file name");
    System.out.println("\t    principal   the principal name (i.e., qweadf@ATHENA.MIT.EDU qweadf)");
    System.out.println("\t    password   the principal's Kerberos password");
  }
  
  public boolean getAddressOption() { return this.includeAddresses; }
  
  public boolean useKeytabFile() { return this.useKeytab; }
  
  public String keytabFileName() { return this.ktabName; }
  
  public PrincipalName getPrincipal() { return this.principal; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\tools\KinitOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */