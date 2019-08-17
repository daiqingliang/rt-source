package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.util.DerValue;

public final class KerberosPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = -7374788026156829911L;
  
  public static final int KRB_NT_UNKNOWN = 0;
  
  public static final int KRB_NT_PRINCIPAL = 1;
  
  public static final int KRB_NT_SRV_INST = 2;
  
  public static final int KRB_NT_SRV_HST = 3;
  
  public static final int KRB_NT_SRV_XHST = 4;
  
  public static final int KRB_NT_UID = 5;
  
  private String fullName;
  
  private String realm;
  
  private int nameType;
  
  public KerberosPrincipal(String paramString) { this(paramString, 1); }
  
  public KerberosPrincipal(String paramString, int paramInt) {
    PrincipalName principalName = null;
    try {
      principalName = new PrincipalName(paramString, paramInt);
    } catch (KrbException krbException) {
      throw new IllegalArgumentException(krbException.getMessage());
    } 
    if (principalName.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        try {
          securityManager.checkPermission(new ServicePermission("@" + principalName.getRealmAsString(), "-"));
        } catch (SecurityException securityException) {
          throw new SecurityException("Cannot read realm info");
        }  
    } 
    this.nameType = paramInt;
    this.fullName = principalName.toString();
    this.realm = principalName.getRealmString();
  }
  
  public String getRealm() { return this.realm; }
  
  public int hashCode() { return getName().hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof KerberosPrincipal))
      return false; 
    String str1 = getName();
    String str2 = ((KerberosPrincipal)paramObject).getName();
    return str1.equals(str2);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    try {
      PrincipalName principalName = new PrincipalName(this.fullName, this.nameType);
      paramObjectOutputStream.writeObject(principalName.asn1Encode());
      paramObjectOutputStream.writeObject(principalName.getRealm().asn1Encode());
    } catch (Exception exception) {
      throw new IOException(exception);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    byte[] arrayOfByte1 = (byte[])paramObjectInputStream.readObject();
    byte[] arrayOfByte2 = (byte[])paramObjectInputStream.readObject();
    try {
      Realm realm1 = new Realm(new DerValue(arrayOfByte2));
      PrincipalName principalName = new PrincipalName(new DerValue(arrayOfByte1), realm1);
      this.realm = realm1.toString();
      this.fullName = principalName.toString();
      this.nameType = principalName.getNameType();
    } catch (Exception exception) {
      throw new IOException(exception);
    } 
  }
  
  public String getName() { return this.fullName; }
  
  public int getNameType() { return this.nameType; }
  
  public String toString() { return getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\KerberosPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */