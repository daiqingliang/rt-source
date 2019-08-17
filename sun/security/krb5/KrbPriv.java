package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncKrbPrivPart;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBPriv;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;
import sun.security.util.DerValue;

class KrbPriv extends KrbAppMessage {
  private byte[] obuf;
  
  private byte[] userData;
  
  private KrbPriv(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2) throws KrbException, IOException {
    EncryptionKey encryptionKey = null;
    if (paramEncryptionKey != null) {
      encryptionKey = paramEncryptionKey;
    } else {
      encryptionKey = paramCredentials.key;
    } 
    this.obuf = mk_priv(paramArrayOfByte, encryptionKey, paramKerberosTime, paramSeqNumber, paramHostAddress1, paramHostAddress2);
  }
  
  private KrbPriv(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2) throws KrbException, IOException {
    KRBPriv kRBPriv = new KRBPriv(paramArrayOfByte);
    EncryptionKey encryptionKey = null;
    if (paramEncryptionKey != null) {
      encryptionKey = paramEncryptionKey;
    } else {
      encryptionKey = paramCredentials.key;
    } 
    this.userData = rd_priv(kRBPriv, encryptionKey, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramCredentials.client);
  }
  
  public byte[] getMessage() throws KrbException { return this.obuf; }
  
  public byte[] getData() throws KrbException { return this.userData; }
  
  private byte[] mk_priv(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
    Integer integer1 = null;
    Integer integer2 = null;
    if (paramKerberosTime != null)
      integer1 = new Integer(paramKerberosTime.getMicroSeconds()); 
    if (paramSeqNumber != null) {
      integer2 = new Integer(paramSeqNumber.current());
      paramSeqNumber.step();
    } 
    EncKrbPrivPart encKrbPrivPart = new EncKrbPrivPart(paramArrayOfByte, paramKerberosTime, integer1, integer2, paramHostAddress1, paramHostAddress2);
    byte[] arrayOfByte = encKrbPrivPart.asn1Encode();
    EncryptedData encryptedData = new EncryptedData(paramEncryptionKey, arrayOfByte, 13);
    KRBPriv kRBPriv = new KRBPriv(encryptedData);
    arrayOfByte = kRBPriv.asn1Encode();
    return kRBPriv.asn1Encode();
  }
  
  private byte[] rd_priv(KRBPriv paramKRBPriv, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2, PrincipalName paramPrincipalName) throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException {
    byte[] arrayOfByte1 = paramKRBPriv.encPart.decrypt(paramEncryptionKey, 13);
    byte[] arrayOfByte2 = paramKRBPriv.encPart.reset(arrayOfByte1);
    DerValue derValue = new DerValue(arrayOfByte2);
    EncKrbPrivPart encKrbPrivPart = new EncKrbPrivPart(derValue);
    check(encKrbPrivPart.timestamp, encKrbPrivPart.usec, encKrbPrivPart.seqNumber, encKrbPrivPart.sAddress, encKrbPrivPart.rAddress, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramPrincipalName);
    return encKrbPrivPart.userData;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbPriv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */