package sun.security.krb5;

import java.io.IOException;
import java.util.Objects;
import javax.security.auth.kerberos.KeyTab;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.internal.ASRep;
import sun.security.krb5.internal.ASReq;
import sun.security.krb5.internal.EncASRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.internal.crypto.EType;
import sun.security.util.DerValue;

class KrbAsRep extends KrbKdcRep {
  private ASRep rep;
  
  private Credentials creds;
  
  private boolean DEBUG = Krb5.DEBUG;
  
  KrbAsRep(byte[] paramArrayOfByte) throws KrbException, Asn1Exception, IOException {
    DerValue derValue = new DerValue(paramArrayOfByte);
    try {
      this.rep = new ASRep(derValue);
    } catch (Asn1Exception asn1Exception) {
      KrbException krbException;
      this.rep = null;
      KRBError kRBError = new KRBError(derValue);
      String str1 = kRBError.getErrorString();
      String str2 = null;
      if (str1 != null && str1.length() > 0)
        if (str1.charAt(str1.length() - 1) == '\000') {
          str2 = str1.substring(0, str1.length() - 1);
        } else {
          str2 = str1;
        }  
      if (str2 == null) {
        krbException = new KrbException(kRBError);
      } else {
        if (this.DEBUG)
          System.out.println("KRBError received: " + str2); 
        krbException = new KrbException(kRBError, str2);
      } 
      krbException.initCause(asn1Exception);
      throw krbException;
    } 
  }
  
  PAData[] getPA() { return this.rep.pAData; }
  
  void decryptUsingKeyTab(KeyTab paramKeyTab, KrbAsReq paramKrbAsReq, PrincipalName paramPrincipalName) throws KrbException, Asn1Exception, IOException {
    EncryptionKey encryptionKey = null;
    int i = this.rep.encPart.getEType();
    Integer integer = this.rep.encPart.kvno;
    try {
      encryptionKey = EncryptionKey.findKey(i, integer, Krb5Util.keysFromJavaxKeyTab(paramKeyTab, paramPrincipalName));
    } catch (KrbException krbException) {
      if (krbException.returnCode() == 44)
        encryptionKey = EncryptionKey.findKey(i, Krb5Util.keysFromJavaxKeyTab(paramKeyTab, paramPrincipalName)); 
    } 
    if (encryptionKey == null)
      throw new KrbException(400, "Cannot find key for type/kvno to decrypt AS REP - " + EType.toString(i) + "/" + integer); 
    decrypt(encryptionKey, paramKrbAsReq);
  }
  
  void decryptUsingPassword(char[] paramArrayOfChar, KrbAsReq paramKrbAsReq, PrincipalName paramPrincipalName) throws KrbException, Asn1Exception, IOException {
    int i = this.rep.encPart.getEType();
    EncryptionKey encryptionKey = EncryptionKey.acquireSecretKey(paramPrincipalName, paramArrayOfChar, i, PAData.getSaltAndParams(i, this.rep.pAData));
    decrypt(encryptionKey, paramKrbAsReq);
  }
  
  private void decrypt(EncryptionKey paramEncryptionKey, KrbAsReq paramKrbAsReq) throws KrbException, Asn1Exception, IOException {
    byte[] arrayOfByte1 = this.rep.encPart.decrypt(paramEncryptionKey, 3);
    byte[] arrayOfByte2 = this.rep.encPart.reset(arrayOfByte1);
    DerValue derValue = new DerValue(arrayOfByte2);
    EncASRepPart encASRepPart = new EncASRepPart(derValue);
    this.rep.encKDCRepPart = encASRepPart;
    ASReq aSReq = paramKrbAsReq.getMessage();
    check(true, aSReq, this.rep);
    this.creds = new Credentials(this.rep.ticket, aSReq.reqBody.cname, encASRepPart.sname, encASRepPart.key, encASRepPart.flags, encASRepPart.authtime, encASRepPart.starttime, encASRepPart.endtime, encASRepPart.renewTill, encASRepPart.caddr);
    if (this.DEBUG)
      System.out.println(">>> KrbAsRep cons in KrbAsReq.getReply " + aSReq.reqBody.cname.getNameString()); 
  }
  
  Credentials getCreds() { return (Credentials)Objects.requireNonNull(this.creds, "Creds not available yet."); }
  
  Credentials getCCreds() { return new Credentials(this.rep); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbAsRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */