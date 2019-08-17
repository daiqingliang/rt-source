package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.APRep;
import sun.security.krb5.internal.EncAPRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.SeqNumber;
import sun.security.util.DerValue;

public class KrbApRep {
  private byte[] obuf;
  
  private byte[] ibuf;
  
  private EncAPRepPart encPart;
  
  private APRep apRepMessg;
  
  public KrbApRep(KrbApReq paramKrbApReq, boolean paramBoolean, EncryptionKey paramEncryptionKey) throws KrbException, IOException {
    LocalSeqNumber localSeqNumber = new LocalSeqNumber();
    init(paramKrbApReq, paramEncryptionKey, localSeqNumber);
  }
  
  public KrbApRep(byte[] paramArrayOfByte, Credentials paramCredentials, KrbApReq paramKrbApReq) throws KrbException, IOException {
    this(paramArrayOfByte, paramCredentials);
    authenticate(paramKrbApReq);
  }
  
  private void init(KrbApReq paramKrbApReq, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber) throws KrbException, IOException {
    createMessage((paramKrbApReq.getCreds()).key, paramKrbApReq.getCtime(), paramKrbApReq.cusec(), paramEncryptionKey, paramSeqNumber);
    this.obuf = this.apRepMessg.asn1Encode();
  }
  
  private KrbApRep(byte[] paramArrayOfByte, Credentials paramCredentials) throws KrbException, IOException { this(new DerValue(paramArrayOfByte), paramCredentials); }
  
  private KrbApRep(DerValue paramDerValue, Credentials paramCredentials) throws KrbException, IOException {
    APRep aPRep = null;
    try {
      aPRep = new APRep(paramDerValue);
    } catch (Asn1Exception asn1Exception) {
      String str2;
      aPRep = null;
      KRBError kRBError = new KRBError(paramDerValue);
      String str1 = kRBError.getErrorString();
      if (str1.charAt(str1.length() - 1) == '\000') {
        str2 = str1.substring(0, str1.length() - 1);
      } else {
        str2 = str1;
      } 
      KrbException krbException = new KrbException(kRBError.getErrorCode(), str2);
      krbException.initCause(asn1Exception);
      throw krbException;
    } 
    byte[] arrayOfByte1 = aPRep.encPart.decrypt(paramCredentials.key, 12);
    byte[] arrayOfByte2 = aPRep.encPart.reset(arrayOfByte1);
    paramDerValue = new DerValue(arrayOfByte2);
    this.encPart = new EncAPRepPart(paramDerValue);
  }
  
  private void authenticate(KrbApReq paramKrbApReq) throws KrbException, IOException {
    if (this.encPart.ctime.getSeconds() != paramKrbApReq.getCtime().getSeconds() || this.encPart.cusec != paramKrbApReq.getCtime().getMicroSeconds())
      throw new KrbApErrException(46); 
  }
  
  public EncryptionKey getSubKey() { return this.encPart.getSubKey(); }
  
  public Integer getSeqNumber() { return this.encPart.getSeqNumber(); }
  
  public byte[] getMessage() { return this.obuf; }
  
  private void createMessage(EncryptionKey paramEncryptionKey1, KerberosTime paramKerberosTime, int paramInt, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
    Integer integer = null;
    if (paramSeqNumber != null)
      integer = new Integer(paramSeqNumber.current()); 
    this.encPart = new EncAPRepPart(paramKerberosTime, paramInt, paramEncryptionKey2, integer);
    byte[] arrayOfByte = this.encPart.asn1Encode();
    EncryptedData encryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, 12);
    this.apRepMessg = new APRep(encryptedData);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbApRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */