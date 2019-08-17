package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.util.DerValue;

public class TGSRep extends KDCRep {
  public TGSRep(PAData[] paramArrayOfPAData, PrincipalName paramPrincipalName, Ticket paramTicket, EncryptedData paramEncryptedData) throws IOException { super(paramArrayOfPAData, paramPrincipalName, paramTicket, paramEncryptedData, 13); }
  
  public TGSRep(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(new DerValue(paramArrayOfByte)); }
  
  public TGSRep(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(paramDerValue); }
  
  private void init(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException { init(paramDerValue, 13); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\TGSRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */