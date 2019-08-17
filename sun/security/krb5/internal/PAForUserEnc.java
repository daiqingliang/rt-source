package sun.security.krb5.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAForUserEnc {
  public final PrincipalName name;
  
  private final EncryptionKey key;
  
  public static final String AUTH_PACKAGE = "Kerberos";
  
  public PAForUserEnc(PrincipalName paramPrincipalName, EncryptionKey paramEncryptionKey) {
    this.name = paramPrincipalName;
    this.key = paramEncryptionKey;
  }
  
  public PAForUserEnc(DerValue paramDerValue, EncryptionKey paramEncryptionKey) throws Asn1Exception, KrbException, IOException {
    DerValue derValue = null;
    this.key = paramEncryptionKey;
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    PrincipalName principalName = null;
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      try {
        principalName = new PrincipalName(derValue.getData().getDerValue(), new Realm("PLACEHOLDER"));
      } catch (RealmException realmException) {}
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      try {
        Realm realm = new Realm(derValue.getData().getDerValue());
        this.name = new PrincipalName(principalName.getNameType(), principalName.getNameStrings(), realm);
      } catch (RealmException realmException) {
        throw new IOException(realmException);
      } 
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 2) {
      derValue = paramDerValue.getData().getDerValue();
      if ((derValue.getTag() & 0x1F) == 3) {
        String str = (new KerberosString(derValue.getData().getDerValue())).toString();
        if (!str.equalsIgnoreCase("Kerberos"))
          throw new IOException("Incorrect auth-package"); 
      } else {
        throw new Asn1Exception(906);
      } 
      if (paramDerValue.getData().available() > 0)
        throw new Asn1Exception(906); 
      return;
    } 
    throw new Asn1Exception(906);
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), this.name.asn1Encode());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), this.name.getRealm().asn1Encode());
    try {
      Checksum checksum = new Checksum(-138, getS4UByteArray(), this.key, 17);
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), checksum.asn1Encode());
    } catch (KrbException krbException) {
      throw new IOException(krbException);
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putDerValue((new KerberosString("Kerberos")).toDerValue());
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)3), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public byte[] getS4UByteArray() throws Asn1Exception, IOException {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byteArrayOutputStream.write(new byte[4]);
      for (String str : this.name.getNameStrings())
        byteArrayOutputStream.write(str.getBytes("UTF-8")); 
      byteArrayOutputStream.write(this.name.getRealm().toString().getBytes("UTF-8"));
      byteArrayOutputStream.write("Kerberos".getBytes("UTF-8"));
      byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
      int i = this.name.getNameType();
      arrayOfByte[0] = (byte)(i & 0xFF);
      arrayOfByte[1] = (byte)(i >> 8 & 0xFF);
      arrayOfByte[2] = (byte)(i >> 16 & 0xFF);
      arrayOfByte[3] = (byte)(i >> 24 & 0xFF);
      return arrayOfByte;
    } catch (IOException iOException) {
      throw new AssertionError("Cannot write ByteArrayOutputStream", iOException);
    } 
  }
  
  public String toString() { return "PA-FOR-USER: " + this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\PAForUserEnc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */