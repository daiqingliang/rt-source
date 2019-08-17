package sun.security.jgss.krb5;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import org.ietf.jgss.GSSException;
import sun.security.action.GetBooleanAction;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApRep;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.util.DerValue;

class AcceptSecContextToken extends InitialToken {
  private KrbApRep apRep = null;
  
  public AcceptSecContextToken(Krb5Context paramKrb5Context, KrbApReq paramKrbApReq) throws KrbException, IOException, GSSException {
    boolean bool1 = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.acceptor.subkey"))).booleanValue();
    boolean bool2 = true;
    EncryptionKey encryptionKey = null;
    if (bool1) {
      encryptionKey = new EncryptionKey(paramKrbApReq.getCreds().getSessionKey());
      paramKrb5Context.setKey(2, encryptionKey);
    } 
    this.apRep = new KrbApRep(paramKrbApReq, bool2, encryptionKey);
    paramKrb5Context.resetMySequenceNumber(this.apRep.getSeqNumber().intValue());
  }
  
  public AcceptSecContextToken(Krb5Context paramKrb5Context, Credentials paramCredentials, KrbApReq paramKrbApReq, InputStream paramInputStream) throws IOException, GSSException, KrbException {
    int i = paramInputStream.read() << 8 | paramInputStream.read();
    if (i != 512)
      throw new GSSException(10, -1, "AP_REP token id does not match!"); 
    byte[] arrayOfByte = (new DerValue(paramInputStream)).toByteArray();
    KrbApRep krbApRep = new KrbApRep(arrayOfByte, paramCredentials, paramKrbApReq);
    EncryptionKey encryptionKey = krbApRep.getSubKey();
    if (encryptionKey != null)
      paramKrb5Context.setKey(2, encryptionKey); 
    Integer integer = krbApRep.getSeqNumber();
    int j = (integer != null) ? integer.intValue() : 0;
    paramKrb5Context.resetPeerSequenceNumber(j);
  }
  
  public final byte[] encode() throws IOException {
    byte[] arrayOfByte1 = this.apRep.getMessage();
    byte[] arrayOfByte2 = new byte[2 + arrayOfByte1.length];
    writeInt(512, arrayOfByte2, 0);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 2, arrayOfByte1.length);
    return arrayOfByte2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\AcceptSecContextToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */