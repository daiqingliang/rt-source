package sun.security.jgss.krb5;

import com.sun.security.jgss.AuthorizationDataEntry;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import org.ietf.jgss.GSSException;
import sun.security.krb5.Checksum;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.KerberosTime;
import sun.security.util.DerValue;

class InitSecContextToken extends InitialToken {
  private KrbApReq apReq = null;
  
  InitSecContextToken(Krb5Context paramKrb5Context, Credentials paramCredentials1, Credentials paramCredentials2) throws KrbException, IOException, GSSException {
    boolean bool1 = paramKrb5Context.getMutualAuthState();
    boolean bool2 = true;
    boolean bool3 = true;
    InitialToken.OverloadedChecksum overloadedChecksum = new InitialToken.OverloadedChecksum(this, paramKrb5Context, paramCredentials1, paramCredentials2);
    Checksum checksum = overloadedChecksum.getChecksum();
    paramKrb5Context.setTktFlags(paramCredentials2.getFlags());
    paramKrb5Context.setAuthTime((new KerberosTime(paramCredentials2.getAuthTime())).toString());
    this.apReq = new KrbApReq(paramCredentials2, bool1, bool2, bool3, checksum);
    paramKrb5Context.resetMySequenceNumber(this.apReq.getSeqNumber().intValue());
    EncryptionKey encryptionKey = this.apReq.getSubKey();
    if (encryptionKey != null) {
      paramKrb5Context.setKey(1, encryptionKey);
    } else {
      paramKrb5Context.setKey(0, paramCredentials2.getSessionKey());
    } 
    if (!bool1)
      paramKrb5Context.resetPeerSequenceNumber(0); 
  }
  
  InitSecContextToken(Krb5Context paramKrb5Context, Krb5AcceptCredential paramKrb5AcceptCredential, InputStream paramInputStream) throws IOException, GSSException, KrbException {
    int i = paramInputStream.read() << 8 | paramInputStream.read();
    if (i != 256)
      throw new GSSException(10, -1, "AP_REQ token id does not match!"); 
    byte[] arrayOfByte = (new DerValue(paramInputStream)).toByteArray();
    InetAddress inetAddress = null;
    if (paramKrb5Context.getChannelBinding() != null)
      inetAddress = paramKrb5Context.getChannelBinding().getInitiatorAddress(); 
    this.apReq = new KrbApReq(arrayOfByte, paramKrb5AcceptCredential, inetAddress);
    EncryptionKey encryptionKey1 = this.apReq.getCreds().getSessionKey();
    EncryptionKey encryptionKey2 = this.apReq.getSubKey();
    if (encryptionKey2 != null) {
      paramKrb5Context.setKey(1, encryptionKey2);
    } else {
      paramKrb5Context.setKey(0, encryptionKey1);
    } 
    InitialToken.OverloadedChecksum overloadedChecksum = new InitialToken.OverloadedChecksum(this, paramKrb5Context, this.apReq.getChecksum(), encryptionKey1, encryptionKey2);
    overloadedChecksum.setContextFlags(paramKrb5Context);
    Credentials credentials = overloadedChecksum.getDelegatedCreds();
    if (credentials != null) {
      Krb5InitCredential krb5InitCredential = Krb5InitCredential.getInstance((Krb5NameElement)paramKrb5Context.getSrcName(), credentials);
      paramKrb5Context.setDelegCred(krb5InitCredential);
    } 
    Integer integer = this.apReq.getSeqNumber();
    int j = (integer != null) ? integer.intValue() : 0;
    paramKrb5Context.resetPeerSequenceNumber(j);
    if (!paramKrb5Context.getMutualAuthState())
      paramKrb5Context.resetMySequenceNumber(j); 
    paramKrb5Context.setAuthTime((new KerberosTime(this.apReq.getCreds().getAuthTime())).toString());
    paramKrb5Context.setTktFlags(this.apReq.getCreds().getFlags());
    AuthorizationData authorizationData = this.apReq.getCreds().getAuthzData();
    if (authorizationData == null) {
      paramKrb5Context.setAuthzData(null);
    } else {
      AuthorizationDataEntry[] arrayOfAuthorizationDataEntry = new AuthorizationDataEntry[authorizationData.count()];
      for (byte b = 0; b < authorizationData.count(); b++)
        arrayOfAuthorizationDataEntry[b] = new AuthorizationDataEntry((authorizationData.item(b)).adType, (authorizationData.item(b)).adData); 
      paramKrb5Context.setAuthzData(arrayOfAuthorizationDataEntry);
    } 
  }
  
  public final KrbApReq getKrbApReq() { return this.apReq; }
  
  public final byte[] encode() throws IOException {
    byte[] arrayOfByte1 = this.apReq.getMessage();
    byte[] arrayOfByte2 = new byte[2 + arrayOfByte1.length];
    writeInt(256, arrayOfByte2, 0);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 2, arrayOfByte1.length);
    return arrayOfByte2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\InitSecContextToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */