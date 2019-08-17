package sun.security.jgss.krb5;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Provider;
import java.util.Locale;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;

public class Krb5NameElement implements GSSNameSpi {
  private PrincipalName krb5PrincipalName;
  
  private String gssNameStr = null;
  
  private Oid gssNameType = null;
  
  private static String CHAR_ENCODING = "UTF-8";
  
  private Krb5NameElement(PrincipalName paramPrincipalName, String paramString, Oid paramOid) {
    this.krb5PrincipalName = paramPrincipalName;
    this.gssNameStr = paramString;
    this.gssNameType = paramOid;
  }
  
  static Krb5NameElement getInstance(String paramString, Oid paramOid) throws GSSException {
    PrincipalName principalName;
    if (paramOid == null) {
      paramOid = Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL;
    } else if (!paramOid.equals(GSSName.NT_USER_NAME) && !paramOid.equals(GSSName.NT_HOSTBASED_SERVICE) && !paramOid.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL) && !paramOid.equals(GSSName.NT_EXPORT_NAME)) {
      throw new GSSException(4, -1, paramOid.toString() + " is an unsupported nametype");
    } 
    try {
      if (paramOid.equals(GSSName.NT_EXPORT_NAME) || paramOid.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL)) {
        principalName = new PrincipalName(paramString, 1);
      } else {
        String[] arrayOfString = getComponents(paramString);
        if (paramOid.equals(GSSName.NT_USER_NAME)) {
          principalName = new PrincipalName(paramString, 1);
        } else {
          String str1 = null;
          String str2 = arrayOfString[0];
          if (arrayOfString.length >= 2)
            str1 = arrayOfString[1]; 
          String str3 = getHostBasedInstance(str2, str1);
          principalName = new PrincipalName(str3, 3);
        } 
      } 
    } catch (KrbException krbException) {
      throw new GSSException(3, -1, krbException.getMessage());
    } 
    if (principalName.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        try {
          securityManager.checkPermission(new ServicePermission("@" + principalName.getRealmAsString(), "-"));
        } catch (SecurityException securityException) {
          throw new GSSException(11);
        }  
    } 
    return new Krb5NameElement(principalName, paramString, paramOid);
  }
  
  static Krb5NameElement getInstance(PrincipalName paramPrincipalName) { return new Krb5NameElement(paramPrincipalName, paramPrincipalName.getName(), Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL); }
  
  private static String[] getComponents(String paramString) throws GSSException {
    String[] arrayOfString;
    int i = paramString.lastIndexOf('@', paramString.length());
    if (i > 0 && paramString.charAt(i - 1) == '\\' && (i - 2 < 0 || paramString.charAt(i - 2) != '\\'))
      i = -1; 
    if (i > 0) {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      arrayOfString = new String[] { str1, str2 };
    } else {
      arrayOfString = new String[] { paramString };
    } 
    return arrayOfString;
  }
  
  private static String getHostBasedInstance(String paramString1, String paramString2) throws GSSException {
    StringBuffer stringBuffer = new StringBuffer(paramString1);
    try {
      if (paramString2 == null)
        paramString2 = InetAddress.getLocalHost().getHostName(); 
    } catch (UnknownHostException unknownHostException) {}
    paramString2 = paramString2.toLowerCase(Locale.ENGLISH);
    stringBuffer = stringBuffer.append('/').append(paramString2);
    return stringBuffer.toString();
  }
  
  public final PrincipalName getKrb5PrincipalName() { return this.krb5PrincipalName; }
  
  public boolean equals(GSSNameSpi paramGSSNameSpi) throws GSSException {
    if (paramGSSNameSpi == this)
      return true; 
    if (paramGSSNameSpi instanceof Krb5NameElement) {
      Krb5NameElement krb5NameElement = (Krb5NameElement)paramGSSNameSpi;
      return this.krb5PrincipalName.getName().equals(krb5NameElement.krb5PrincipalName.getName());
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    try {
      if (paramObject instanceof Krb5NameElement)
        return equals((Krb5NameElement)paramObject); 
    } catch (GSSException gSSException) {}
    return false;
  }
  
  public int hashCode() { return 629 + this.krb5PrincipalName.getName().hashCode(); }
  
  public byte[] export() throws GSSException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = this.krb5PrincipalName.getName().getBytes(CHAR_ENCODING);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    return arrayOfByte;
  }
  
  public Oid getMechanism() { return Krb5MechFactory.GSS_KRB5_MECH_OID; }
  
  public String toString() { return this.gssNameStr; }
  
  public Oid getGSSNameType() { return this.gssNameType; }
  
  public Oid getStringNameType() { return this.gssNameType; }
  
  public boolean isAnonymousName() { return this.gssNameType.equals(GSSName.NT_ANONYMOUS); }
  
  public Provider getProvider() { return Krb5MechFactory.PROVIDER; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5NameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */