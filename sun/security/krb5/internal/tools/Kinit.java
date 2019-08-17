package sun.security.krb5.internal.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.Config;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.util.Password;

public class Kinit {
  private KinitOptions options;
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  public static void main(String[] paramArrayOfString) {
    try {
      Kinit kinit = new Kinit(paramArrayOfString);
    } catch (Exception exception) {
      String str = null;
      if (exception instanceof KrbException) {
        str = ((KrbException)exception).krbErrorMessage() + " " + ((KrbException)exception).returnCodeMessage();
      } else {
        str = exception.getMessage();
      } 
      if (str != null) {
        System.err.println("Exception: " + str);
      } else {
        System.out.println("Exception: " + exception);
      } 
      exception.printStackTrace();
      System.exit(-1);
    } 
  }
  
  private Kinit(String[] paramArrayOfString) {
    if (paramArrayOfString == null || paramArrayOfString.length == 0) {
      this.options = new KinitOptions();
    } else {
      this.options = new KinitOptions(paramArrayOfString);
    } 
    String str1 = null;
    PrincipalName principalName1 = this.options.getPrincipal();
    if (principalName1 != null)
      str1 = principalName1.toString(); 
    if (DEBUG)
      System.out.println("Principal is " + principalName1); 
    char[] arrayOfChar = this.options.password;
    boolean bool = this.options.useKeytabFile();
    if (!bool) {
      if (str1 == null)
        throw new IllegalArgumentException(" Can not obtain principal name"); 
      if (arrayOfChar == null) {
        System.out.print("Password for " + str1 + ":");
        System.out.flush();
        arrayOfChar = Password.readPassword(System.in);
        if (DEBUG)
          System.out.println(">>> Kinit console input " + new String(arrayOfChar)); 
      } 
      krbAsReqBuilder = new KrbAsReqBuilder(principalName1, arrayOfChar);
    } else {
      if (DEBUG)
        System.out.println(">>> Kinit using keytab"); 
      if (str1 == null)
        throw new IllegalArgumentException("Principal name must be specified."); 
      String str = this.options.keytabFileName();
      if (str != null && DEBUG)
        System.out.println(">>> Kinit keytab file name: " + str); 
      krbAsReqBuilder = new KrbAsReqBuilder(principalName1, (str == null) ? KeyTab.getInstance() : KeyTab.getInstance(new File(str)));
    } 
    KDCOptions kDCOptions = new KDCOptions();
    setOptions(1, this.options.forwardable, kDCOptions);
    setOptions(3, this.options.proxiable, kDCOptions);
    krbAsReqBuilder.setOptions(kDCOptions);
    String str2 = this.options.getKDCRealm();
    if (str2 == null)
      str2 = Config.getInstance().getDefaultRealm(); 
    if (DEBUG)
      System.out.println(">>> Kinit realm name is " + str2); 
    PrincipalName principalName2 = PrincipalName.tgsService(str2, str2);
    krbAsReqBuilder.setTarget(principalName2);
    if (DEBUG)
      System.out.println(">>> Creating KrbAsReq"); 
    if (this.options.getAddressOption())
      krbAsReqBuilder.setAddresses(HostAddresses.getLocalAddresses()); 
    krbAsReqBuilder.action();
    Credentials credentials = krbAsReqBuilder.getCCreds();
    krbAsReqBuilder.destroy();
    CredentialsCache credentialsCache = CredentialsCache.create(principalName1, this.options.cachename);
    if (credentialsCache == null)
      throw new IOException("Unable to create the cache file " + this.options.cachename); 
    credentialsCache.update(credentials);
    credentialsCache.save();
    if (this.options.password == null) {
      System.out.println("New ticket is stored in cache file " + this.options.cachename);
    } else {
      Arrays.fill(this.options.password, '0');
    } 
    if (arrayOfChar != null)
      Arrays.fill(arrayOfChar, '0'); 
    this.options = null;
  }
  
  private static void setOptions(int paramInt1, int paramInt2, KDCOptions paramKDCOptions) {
    switch (paramInt2) {
      case -1:
        paramKDCOptions.set(paramInt1, false);
        break;
      case 1:
        paramKDCOptions.set(paramInt1, true);
        break;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\tools\Kinit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */