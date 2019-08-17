package sun.security.krb5;

import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.kerberos.KeyTab;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.internal.crypto.EType;

public final class KrbAsReqBuilder {
  private KDCOptions options;
  
  private PrincipalName cname;
  
  private PrincipalName sname;
  
  private KerberosTime from;
  
  private KerberosTime till;
  
  private KerberosTime rtime;
  
  private HostAddresses addresses;
  
  private final char[] password;
  
  private final KeyTab ktab;
  
  private PAData[] paList;
  
  private KrbAsReq req;
  
  private KrbAsRep rep;
  
  private State state;
  
  private void init(PrincipalName paramPrincipalName) throws KrbException {
    this.cname = paramPrincipalName;
    this.state = State.INIT;
  }
  
  public KrbAsReqBuilder(PrincipalName paramPrincipalName, KeyTab paramKeyTab) throws KrbException {
    init(paramPrincipalName);
    this.ktab = paramKeyTab;
    this.password = null;
  }
  
  public KrbAsReqBuilder(PrincipalName paramPrincipalName, char[] paramArrayOfChar) throws KrbException {
    init(paramPrincipalName);
    this.password = (char[])paramArrayOfChar.clone();
    this.ktab = null;
  }
  
  public EncryptionKey[] getKeys(boolean paramBoolean) throws KrbException {
    checkState(paramBoolean ? State.REQ_OK : State.INIT, "Cannot get keys");
    if (this.password != null) {
      int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
      EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfInt.length];
      String str = null;
      try {
        byte b;
        for (b = 0; b < arrayOfInt.length; b++) {
          PAData.SaltAndParams saltAndParams = PAData.getSaltAndParams(arrayOfInt[b], this.paList);
          if (saltAndParams != null) {
            if (arrayOfInt[b] != 23 && saltAndParams.salt != null)
              str = saltAndParams.salt; 
            arrayOfEncryptionKey[b] = EncryptionKey.acquireSecretKey(this.cname, this.password, arrayOfInt[b], saltAndParams);
          } 
        } 
        if (str == null)
          str = this.cname.getSalt(); 
        for (b = 0; b < arrayOfInt.length; b++) {
          if (arrayOfEncryptionKey[b] == null)
            arrayOfEncryptionKey[b] = EncryptionKey.acquireSecretKey(this.password, str, arrayOfInt[b], null); 
        } 
      } catch (IOException iOException) {
        KrbException krbException = new KrbException(909);
        krbException.initCause(iOException);
        throw krbException;
      } 
      return arrayOfEncryptionKey;
    } 
    throw new IllegalStateException("Required password not provided");
  }
  
  public void setOptions(KDCOptions paramKDCOptions) {
    checkState(State.INIT, "Cannot specify options");
    this.options = paramKDCOptions;
  }
  
  public void setTarget(PrincipalName paramPrincipalName) throws KrbException {
    checkState(State.INIT, "Cannot specify target");
    this.sname = paramPrincipalName;
  }
  
  public void setAddresses(HostAddresses paramHostAddresses) {
    checkState(State.INIT, "Cannot specify addresses");
    this.addresses = paramHostAddresses;
  }
  
  private KrbAsReq build(EncryptionKey paramEncryptionKey) throws KrbException, IOException {
    int[] arrayOfInt;
    if (this.password != null) {
      arrayOfInt = EType.getDefaults("default_tkt_enctypes");
    } else {
      EncryptionKey[] arrayOfEncryptionKey = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
      arrayOfInt = EType.getDefaults("default_tkt_enctypes", arrayOfEncryptionKey);
      for (EncryptionKey encryptionKey : arrayOfEncryptionKey)
        encryptionKey.destroy(); 
    } 
    return new KrbAsReq(paramEncryptionKey, this.options, this.cname, this.sname, this.from, this.till, this.rtime, arrayOfInt, this.addresses);
  }
  
  private KrbAsReqBuilder resolve() throws KrbException, Asn1Exception, IOException {
    if (this.ktab != null) {
      this.rep.decryptUsingKeyTab(this.ktab, this.req, this.cname);
    } else {
      this.rep.decryptUsingPassword(this.password, this.req, this.cname);
    } 
    if (this.rep.getPA() != null)
      if (this.paList == null || this.paList.length == 0) {
        this.paList = this.rep.getPA();
      } else {
        int i = this.rep.getPA().length;
        if (i > 0) {
          int j = this.paList.length;
          this.paList = (PAData[])Arrays.copyOf(this.paList, this.paList.length + i);
          System.arraycopy(this.rep.getPA(), 0, this.paList, j, i);
        } 
      }  
    return this;
  }
  
  private KrbAsReqBuilder send() throws KrbException, Asn1Exception, IOException {
    boolean bool = false;
    KdcComm kdcComm = new KdcComm(this.cname.getRealmAsString());
    EncryptionKey encryptionKey = null;
    while (true) {
      try {
        this.req = build(encryptionKey);
        this.rep = new KrbAsRep(kdcComm.send(this.req.encoding()));
        return this;
      } catch (KrbException krbException) {
        if (!bool && (krbException.returnCode() == 24 || krbException.returnCode() == 25)) {
          if (Krb5.DEBUG)
            System.out.println("KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ"); 
          bool = true;
          KRBError kRBError = krbException.getError();
          int i = PAData.getPreferredEType(kRBError.getPA(), EType.getDefaults("default_tkt_enctypes")[0]);
          if (this.password == null) {
            EncryptionKey[] arrayOfEncryptionKey = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
            encryptionKey = EncryptionKey.findKey(i, arrayOfEncryptionKey);
            if (encryptionKey != null)
              encryptionKey = (EncryptionKey)encryptionKey.clone(); 
            for (EncryptionKey encryptionKey1 : arrayOfEncryptionKey)
              encryptionKey1.destroy(); 
          } else {
            encryptionKey = EncryptionKey.acquireSecretKey(this.cname, this.password, i, PAData.getSaltAndParams(i, kRBError.getPA()));
          } 
          this.paList = kRBError.getPA();
          continue;
        } 
        break;
      } 
    } 
    throw krbException;
  }
  
  public KrbAsReqBuilder action() throws KrbException, Asn1Exception, IOException {
    checkState(State.INIT, "Cannot call action");
    this.state = State.REQ_OK;
    return send().resolve();
  }
  
  public Credentials getCreds() {
    checkState(State.REQ_OK, "Cannot retrieve creds");
    return this.rep.getCreds();
  }
  
  public Credentials getCCreds() {
    checkState(State.REQ_OK, "Cannot retrieve CCreds");
    return this.rep.getCCreds();
  }
  
  public void destroy() {
    this.state = State.DESTROYED;
    if (this.password != null)
      Arrays.fill(this.password, false); 
  }
  
  private void checkState(State paramState, String paramString) {
    if (this.state != paramState)
      throw new IllegalStateException(paramString + " at " + paramState + " state"); 
  }
  
  private enum State {
    INIT, REQ_OK, DESTROYED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\KrbAsReqBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */