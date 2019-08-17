package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KRBSafe;
import sun.security.krb5.internal.KRBSafeBody;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;

class KrbSafe extends KrbAppMessage {
  private byte[] obuf;
  
  private byte[] userData;
  
  public KrbSafe(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2) throws KrbException, IOException {
    EncryptionKey encryptionKey = null;
    if (paramEncryptionKey != null) {
      encryptionKey = paramEncryptionKey;
    } else {
      encryptionKey = paramCredentials.key;
    } 
    this.obuf = mk_safe(paramArrayOfByte, encryptionKey, paramKerberosTime, paramSeqNumber, paramHostAddress1, paramHostAddress2);
  }
  
  public KrbSafe(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2) throws KrbException, IOException {
    KRBSafe kRBSafe = new KRBSafe(paramArrayOfByte);
    EncryptionKey encryptionKey = null;
    if (paramEncryptionKey != null) {
      encryptionKey = paramEncryptionKey;
    } else {
      encryptionKey = paramCredentials.key;
    } 
    this.userData = rd_safe(kRBSafe, encryptionKey, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramCredentials.client);
  }
  
  public byte[] getMessage() { return this.obuf; }
  
  public byte[] getData() { return this.userData; }
  
  private byte[] mk_safe(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2) throws Asn1Exception, IOException, KdcErrException, KrbApErrException, KrbCryptoException {
    Integer integer1 = null;
    Integer integer2 = null;
    if (paramKerberosTime != null)
      integer1 = new Integer(paramKerberosTime.getMicroSeconds()); 
    if (paramSeqNumber != null) {
      integer2 = new Integer(paramSeqNumber.current());
      paramSeqNumber.step();
    } 
    KRBSafeBody kRBSafeBody = new KRBSafeBody(paramArrayOfByte, paramKerberosTime, integer1, integer2, paramHostAddress1, paramHostAddress2);
    byte[] arrayOfByte = kRBSafeBody.asn1Encode();
    Checksum checksum = new Checksum(Checksum.SAFECKSUMTYPE_DEFAULT, arrayOfByte, paramEncryptionKey, 15);
    KRBSafe kRBSafe = new KRBSafe(kRBSafeBody, checksum);
    arrayOfByte = kRBSafe.asn1Encode();
    return kRBSafe.asn1Encode();
  }
  
  private byte[] rd_safe(KRBSafe paramKRBSafe, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2, PrincipalName paramPrincipalName) throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException {
    byte[] arrayOfByte = paramKRBSafe.safeBody.asn1Encode();
    if (!paramKRBSafe.cksum.verifyKeyedChecksum(arrayOfByte, paramEncryptionKey, 15))
      throw new KrbApErrException(41); 
    check(paramKRBSafe.safeBody.timestamp, paramKRBSafe.safeBody.usec, paramKRBSafe.safeBody.seqNumber, paramKRBSafe.safeBody.sAddress, paramKRBSafe.safeBody.rAddress, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramPrincipalName);
    return paramKRBSafe.safeBody.userData;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbSafe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */