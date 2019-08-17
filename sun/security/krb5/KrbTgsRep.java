package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncTGSRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.TGSRep;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.util.DerValue;

public class KrbTgsRep extends KrbKdcRep {
  private TGSRep rep;
  
  private Credentials creds;
  
  private Ticket secondTicket;
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  KrbTgsRep(byte[] paramArrayOfByte, KrbTgsReq paramKrbTgsReq) throws KrbException, IOException {
    DerValue derValue = new DerValue(paramArrayOfByte);
    TGSReq tGSReq = paramKrbTgsReq.getMessage();
    TGSRep tGSRep = null;
    try {
      tGSRep = new TGSRep(derValue);
    } catch (Asn1Exception asn1Exception) {
      KrbException krbException;
      tGSRep = null;
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
        krbException = new KrbException(kRBError.getErrorCode());
      } else {
        krbException = new KrbException(kRBError.getErrorCode(), str2);
      } 
      krbException.initCause(asn1Exception);
      throw krbException;
    } 
    byte[] arrayOfByte1 = tGSRep.encPart.decrypt(paramKrbTgsReq.tgsReqKey, paramKrbTgsReq.usedSubkey() ? 9 : 8);
    byte[] arrayOfByte2 = tGSRep.encPart.reset(arrayOfByte1);
    derValue = new DerValue(arrayOfByte2);
    EncTGSRepPart encTGSRepPart = new EncTGSRepPart(derValue);
    tGSRep.encKDCRepPart = encTGSRepPart;
    check(false, tGSReq, tGSRep);
    this.creds = new Credentials(tGSRep.ticket, tGSRep.cname, encTGSRepPart.sname, encTGSRepPart.key, encTGSRepPart.flags, encTGSRepPart.authtime, encTGSRepPart.starttime, encTGSRepPart.endtime, encTGSRepPart.renewTill, encTGSRepPart.caddr);
    this.rep = tGSRep;
    this.secondTicket = paramKrbTgsReq.getSecondTicket();
  }
  
  public Credentials getCreds() { return this.creds; }
  
  Credentials setCredentials() { return new Credentials(this.rep, this.secondTicket); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbTgsRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */