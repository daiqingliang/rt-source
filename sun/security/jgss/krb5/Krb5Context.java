package sun.security.jgss.krb5;

import com.sun.security.jgss.AuthorizationDataEntry;
import com.sun.security.jgss.InquireType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Key;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.misc.HexDumpEncoder;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.TokenTracker;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Ticket;

class Krb5Context implements GSSContextSpi {
  private static final int STATE_NEW = 1;
  
  private static final int STATE_IN_PROCESS = 2;
  
  private static final int STATE_DONE = 3;
  
  private static final int STATE_DELETED = 4;
  
  private int state = 1;
  
  public static final int SESSION_KEY = 0;
  
  public static final int INITIATOR_SUBKEY = 1;
  
  public static final int ACCEPTOR_SUBKEY = 2;
  
  private boolean credDelegState = false;
  
  private boolean mutualAuthState = true;
  
  private boolean replayDetState = true;
  
  private boolean sequenceDetState = true;
  
  private boolean confState = true;
  
  private boolean integState = true;
  
  private boolean delegPolicyState = false;
  
  private boolean isConstrainedDelegationTried = false;
  
  private int mySeqNumber;
  
  private int peerSeqNumber;
  
  private int keySrc;
  
  private TokenTracker peerTokenTracker;
  
  private CipherHelper cipherHelper = null;
  
  private Object mySeqNumberLock = new Object();
  
  private Object peerSeqNumberLock = new Object();
  
  private EncryptionKey key;
  
  private Krb5NameElement myName;
  
  private Krb5NameElement peerName;
  
  private int lifetime;
  
  private boolean initiator;
  
  private ChannelBinding channelBinding;
  
  private Krb5CredElement myCred;
  
  private Krb5CredElement delegatedCred;
  
  private Credentials serviceCreds;
  
  private KrbApReq apReq;
  
  Ticket serviceTicket;
  
  private final GSSCaller caller;
  
  private static final boolean DEBUG = Krb5Util.DEBUG;
  
  private boolean[] tktFlags;
  
  private String authTime;
  
  private AuthorizationDataEntry[] authzData;
  
  Krb5Context(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, Krb5CredElement paramKrb5CredElement, int paramInt) throws GSSException {
    if (paramKrb5NameElement == null)
      throw new IllegalArgumentException("Cannot have null peer name"); 
    this.caller = paramGSSCaller;
    this.peerName = paramKrb5NameElement;
    this.myCred = paramKrb5CredElement;
    this.lifetime = paramInt;
    this.initiator = true;
  }
  
  Krb5Context(GSSCaller paramGSSCaller, Krb5CredElement paramKrb5CredElement) throws GSSException {
    this.caller = paramGSSCaller;
    this.myCred = paramKrb5CredElement;
    this.initiator = false;
  }
  
  public Krb5Context(GSSCaller paramGSSCaller, byte[] paramArrayOfByte) throws GSSException { throw new GSSException(16, -1, "GSS Import Context not available"); }
  
  public final boolean isTransferable() throws GSSException { return false; }
  
  public final int getLifetime() { return Integer.MAX_VALUE; }
  
  public void requestLifetime(int paramInt) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.lifetime = paramInt; 
  }
  
  public final void requestConf(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.confState = paramBoolean; 
  }
  
  public final boolean getConfState() throws GSSException { return this.confState; }
  
  public final void requestInteg(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.integState = paramBoolean; 
  }
  
  public final boolean getIntegState() throws GSSException { return this.integState; }
  
  public final void requestCredDeleg(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator() && (this.myCred == null || !(this.myCred instanceof Krb5ProxyCredential)))
      this.credDelegState = paramBoolean; 
  }
  
  public final boolean getCredDelegState() throws GSSException {
    if (isInitiator())
      return this.credDelegState; 
    tryConstrainedDelegation();
    return (this.delegatedCred != null);
  }
  
  public final void requestMutualAuth(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.mutualAuthState = paramBoolean; 
  }
  
  public final boolean getMutualAuthState() throws GSSException { return this.mutualAuthState; }
  
  public final void requestReplayDet(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.replayDetState = paramBoolean; 
  }
  
  public final boolean getReplayDetState() throws GSSException { return (this.replayDetState || this.sequenceDetState); }
  
  public final void requestSequenceDet(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.sequenceDetState = paramBoolean; 
  }
  
  public final boolean getSequenceDetState() throws GSSException { return (this.sequenceDetState || this.replayDetState); }
  
  public final void requestDelegPolicy(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.delegPolicyState = paramBoolean; 
  }
  
  public final boolean getDelegPolicyState() throws GSSException { return this.delegPolicyState; }
  
  public final void requestAnonymity(boolean paramBoolean) throws GSSException {}
  
  public final boolean getAnonymityState() throws GSSException { return false; }
  
  final CipherHelper getCipherHelper(EncryptionKey paramEncryptionKey) throws GSSException {
    EncryptionKey encryptionKey = null;
    if (this.cipherHelper == null) {
      encryptionKey = (getKey() == null) ? paramEncryptionKey : getKey();
      this.cipherHelper = new CipherHelper(encryptionKey);
    } 
    return this.cipherHelper;
  }
  
  final int incrementMySequenceNumber() {
    int i;
    synchronized (this.mySeqNumberLock) {
      i = this.mySeqNumber;
      this.mySeqNumber = i + 1;
    } 
    return i;
  }
  
  final void resetMySequenceNumber(int paramInt) throws GSSException {
    if (DEBUG)
      System.out.println("Krb5Context setting mySeqNumber to: " + paramInt); 
    synchronized (this.mySeqNumberLock) {
      this.mySeqNumber = paramInt;
    } 
  }
  
  final void resetPeerSequenceNumber(int paramInt) throws GSSException {
    if (DEBUG)
      System.out.println("Krb5Context setting peerSeqNumber to: " + paramInt); 
    synchronized (this.peerSeqNumberLock) {
      this.peerSeqNumber = paramInt;
      this.peerTokenTracker = new TokenTracker(this.peerSeqNumber);
    } 
  }
  
  final void setKey(int paramInt, EncryptionKey paramEncryptionKey) throws GSSException {
    this.key = paramEncryptionKey;
    this.keySrc = paramInt;
    this.cipherHelper = new CipherHelper(paramEncryptionKey);
  }
  
  public final int getKeySrc() { return this.keySrc; }
  
  private final EncryptionKey getKey() { return this.key; }
  
  final void setDelegCred(Krb5CredElement paramKrb5CredElement) { this.delegatedCred = paramKrb5CredElement; }
  
  final void setCredDelegState(boolean paramBoolean) throws GSSException { this.credDelegState = paramBoolean; }
  
  final void setMutualAuthState(boolean paramBoolean) throws GSSException { this.mutualAuthState = paramBoolean; }
  
  final void setReplayDetState(boolean paramBoolean) throws GSSException { this.replayDetState = paramBoolean; }
  
  final void setSequenceDetState(boolean paramBoolean) throws GSSException { this.sequenceDetState = paramBoolean; }
  
  final void setConfState(boolean paramBoolean) throws GSSException { this.confState = paramBoolean; }
  
  final void setIntegState(boolean paramBoolean) throws GSSException { this.integState = paramBoolean; }
  
  final void setDelegPolicyState(boolean paramBoolean) throws GSSException { this.delegPolicyState = paramBoolean; }
  
  public final void setChannelBinding(ChannelBinding paramChannelBinding) throws GSSException { this.channelBinding = paramChannelBinding; }
  
  final ChannelBinding getChannelBinding() { return this.channelBinding; }
  
  public final Oid getMech() { return Krb5MechFactory.GSS_KRB5_MECH_OID; }
  
  public final GSSNameSpi getSrcName() throws GSSException { return isInitiator() ? this.myName : this.peerName; }
  
  public final GSSNameSpi getTargName() throws GSSException { return !isInitiator() ? this.myName : this.peerName; }
  
  public final GSSCredentialSpi getDelegCred() throws GSSException {
    if (this.state != 2 && this.state != 3)
      throw new GSSException(12); 
    if (isInitiator())
      throw new GSSException(13); 
    tryConstrainedDelegation();
    if (this.delegatedCred == null)
      throw new GSSException(13); 
    return this.delegatedCred;
  }
  
  private void tryConstrainedDelegation() {
    if (this.state != 2 && this.state != 3)
      return; 
    if (!this.isConstrainedDelegationTried) {
      if (this.delegatedCred == null) {
        if (DEBUG)
          System.out.println(">>> Constrained deleg from " + this.caller); 
        try {
          this.delegatedCred = new Krb5ProxyCredential(Krb5InitCredential.getInstance(GSSCaller.CALLER_ACCEPT, this.myName, this.lifetime), this.peerName, this.serviceTicket);
        } catch (GSSException gSSException) {}
      } 
      this.isConstrainedDelegationTried = true;
    } 
  }
  
  public final boolean isInitiator() throws GSSException { return this.initiator; }
  
  public final boolean isProtReady() throws GSSException { return (this.state == 3); }
  
  public final byte[] initSecContext(InputStream paramInputStream, int paramInt) throws GSSException {
    byte[] arrayOfByte = null;
    InitSecContextToken initSecContextToken = null;
    byte b = 11;
    if (DEBUG)
      System.out.println("Entered Krb5Context.initSecContext with state=" + printState(this.state)); 
    if (!isInitiator())
      throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext"); 
    try {
      if (this.state == 1) {
        final Krb5ProxyCredential second;
        Credentials credentials;
        this.state = 2;
        b = 13;
        if (this.myCred == null) {
          this.myCred = Krb5InitCredential.getInstance(this.caller, this.myName, 0);
        } else if (!this.myCred.isInitiatorCredential()) {
          throw new GSSException(b, -1, "No TGT available");
        } 
        this.myName = (Krb5NameElement)this.myCred.getName();
        if (this.myCred instanceof Krb5InitCredential) {
          krb5ProxyCredential = null;
          credentials = ((Krb5InitCredential)this.myCred).getKrb5Credentials();
        } else {
          krb5ProxyCredential = (Krb5ProxyCredential)this.myCred;
          credentials = krb5ProxyCredential.self.getKrb5Credentials();
        } 
        checkPermission(this.peerName.getKrb5PrincipalName().getName(), "initiate");
        final AccessControlContext acc = AccessController.getContext();
        if (GSSUtil.useSubjectCredsOnly(this.caller)) {
          final KerberosTicket kt = null;
          try {
            kerberosTicket = (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction<KerberosTicket>() {
                  public KerberosTicket run() throws Exception { return Krb5Util.getTicket(GSSCaller.CALLER_UNKNOWN, (second == null) ? Krb5Context.this.myName.getKrb5PrincipalName().getName() : second.getName().getKrb5PrincipalName().getName(), Krb5Context.this.peerName.getKrb5PrincipalName().getName(), acc); }
                });
          } catch (PrivilegedActionException privilegedActionException) {
            if (DEBUG)
              System.out.println("Attempt to obtain service ticket from the subject failed!"); 
          } 
          if (kerberosTicket != null) {
            if (DEBUG)
              System.out.println("Found service ticket in the subject" + kerberosTicket); 
            this.serviceCreds = Krb5Util.ticketToCreds(kerberosTicket);
          } 
        } 
        if (this.serviceCreds == null) {
          if (DEBUG)
            System.out.println("Service ticket not found in the subject"); 
          if (krb5ProxyCredential == null) {
            this.serviceCreds = Credentials.acquireServiceCreds(this.peerName.getKrb5PrincipalName().getName(), credentials);
          } else {
            this.serviceCreds = Credentials.acquireS4U2proxyCreds(this.peerName.getKrb5PrincipalName().getName(), krb5ProxyCredential.tkt, krb5ProxyCredential.getName().getKrb5PrincipalName(), credentials);
          } 
          if (GSSUtil.useSubjectCredsOnly(this.caller)) {
            final Subject subject = (Subject)AccessController.doPrivileged(new PrivilegedAction<Subject>() {
                  public Subject run() { return Subject.getSubject(acc); }
                });
            if (subject != null && !subject.isReadOnly()) {
              final KerberosTicket kt = Krb5Util.credsToTicket(this.serviceCreds);
              AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                      subject.getPrivateCredentials().add(kt);
                      return null;
                    }
                  });
            } else if (DEBUG) {
              System.out.println("Subject is readOnly;Kerberos Service ticket not stored");
            } 
          } 
        } 
        b = 11;
        initSecContextToken = new InitSecContextToken(this, credentials, this.serviceCreds);
        this.apReq = ((InitSecContextToken)initSecContextToken).getKrbApReq();
        arrayOfByte = initSecContextToken.encode();
        this.myCred = null;
        if (!getMutualAuthState())
          this.state = 3; 
        if (DEBUG)
          System.out.println("Created InitSecContextToken:\n" + (new HexDumpEncoder()).encodeBuffer(arrayOfByte)); 
      } else if (this.state == 2) {
        new AcceptSecContextToken(this, this.serviceCreds, this.apReq, paramInputStream);
        this.serviceCreds = null;
        this.apReq = null;
        this.state = 3;
      } else if (DEBUG) {
        System.out.println(this.state);
      } 
    } catch (KrbException krbException) {
      if (DEBUG)
        krbException.printStackTrace(); 
      GSSException gSSException = new GSSException(b, -1, krbException.getMessage());
      gSSException.initCause(krbException);
      throw gSSException;
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(b, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    return arrayOfByte;
  }
  
  public final boolean isEstablished() throws GSSException { return (this.state == 3); }
  
  public final byte[] acceptSecContext(InputStream paramInputStream, int paramInt) throws GSSException {
    byte[] arrayOfByte = null;
    if (DEBUG)
      System.out.println("Entered Krb5Context.acceptSecContext with state=" + printState(this.state)); 
    if (isInitiator())
      throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext"); 
    try {
      if (this.state == 1) {
        this.state = 2;
        if (this.myCred == null) {
          this.myCred = Krb5AcceptCredential.getInstance(this.caller, this.myName);
        } else if (!this.myCred.isAcceptorCredential()) {
          throw new GSSException(13, -1, "No Secret Key available");
        } 
        this.myName = (Krb5NameElement)this.myCred.getName();
        if (this.myName != null)
          Krb5MechFactory.checkAcceptCredPermission(this.myName, this.myName); 
        InitSecContextToken initSecContextToken = new InitSecContextToken(this, (Krb5AcceptCredential)this.myCred, paramInputStream);
        PrincipalName principalName = initSecContextToken.getKrbApReq().getClient();
        this.peerName = Krb5NameElement.getInstance(principalName);
        if (this.myName == null) {
          this.myName = Krb5NameElement.getInstance(initSecContextToken.getKrbApReq().getCreds().getServer());
          Krb5MechFactory.checkAcceptCredPermission(this.myName, this.myName);
        } 
        if (getMutualAuthState())
          arrayOfByte = (new AcceptSecContextToken(this, initSecContextToken.getKrbApReq())).encode(); 
        this.serviceTicket = initSecContextToken.getKrbApReq().getCreds().getTicket();
        this.myCred = null;
        this.state = 3;
      } else if (DEBUG) {
        System.out.println(this.state);
      } 
    } catch (KrbException krbException) {
      GSSException gSSException = new GSSException(11, -1, krbException.getMessage());
      gSSException.initCause(krbException);
      throw gSSException;
    } catch (IOException iOException) {
      if (DEBUG)
        iOException.printStackTrace(); 
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    return arrayOfByte;
  }
  
  public final int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2) throws GSSException {
    int i = 0;
    if (this.cipherHelper.getProto() == 0) {
      i = WrapToken.getSizeLimit(paramInt1, paramBoolean, paramInt2, getCipherHelper(null));
    } else if (this.cipherHelper.getProto() == 1) {
      i = WrapToken_v2.getSizeLimit(paramInt1, paramBoolean, paramInt2, getCipherHelper(null));
    } 
    return i;
  }
  
  public final byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (DEBUG)
      System.out.println("Krb5Context.wrap: data=[" + getHexBytes(paramArrayOfByte, paramInt1, paramInt2) + "]"); 
    if (this.state != 3)
      throw new GSSException(12, -1, "Wrap called in invalid state!"); 
    byte[] arrayOfByte = null;
    try {
      if (this.cipherHelper.getProto() == 0) {
        WrapToken wrapToken = new WrapToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = wrapToken.encode();
      } else if (this.cipherHelper.getProto() == 1) {
        WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = wrapToken_v2.encode();
      } 
      if (DEBUG)
        System.out.println("Krb5Context.wrap: token=[" + getHexBytes(arrayOfByte, 0, arrayOfByte.length) + "]"); 
      return arrayOfByte;
    } catch (IOException iOException) {
      arrayOfByte = null;
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
  
  public final int wrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp) throws GSSException {
    if (this.state != 3)
      throw new GSSException(12, -1, "Wrap called in invalid state!"); 
    int i = 0;
    try {
      if (this.cipherHelper.getProto() == 0) {
        WrapToken wrapToken = new WrapToken(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = wrapToken.encode(paramArrayOfByte2, paramInt3);
      } else if (this.cipherHelper.getProto() == 1) {
        WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = wrapToken_v2.encode(paramArrayOfByte2, paramInt3);
      } 
      if (DEBUG)
        System.out.println("Krb5Context.wrap: token=[" + getHexBytes(paramArrayOfByte2, paramInt3, i) + "]"); 
      return i;
    } catch (IOException iOException) {
      i = 0;
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
  
  public final void wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.state != 3)
      throw new GSSException(12, -1, "Wrap called in invalid state!"); 
    byte[] arrayOfByte = null;
    try {
      if (this.cipherHelper.getProto() == 0) {
        WrapToken wrapToken = new WrapToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        wrapToken.encode(paramOutputStream);
        if (DEBUG)
          arrayOfByte = wrapToken.encode(); 
      } else if (this.cipherHelper.getProto() == 1) {
        WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        wrapToken_v2.encode(paramOutputStream);
        if (DEBUG)
          arrayOfByte = wrapToken_v2.encode(); 
      } 
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    if (DEBUG)
      System.out.println("Krb5Context.wrap: token=[" + getHexBytes(arrayOfByte, 0, arrayOfByte.length) + "]"); 
  }
  
  public final void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = new byte[paramInputStream.available()];
      paramInputStream.read(arrayOfByte);
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    wrap(arrayOfByte, 0, arrayOfByte.length, paramOutputStream, paramMessageProp);
  }
  
  public final byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (DEBUG)
      System.out.println("Krb5Context.unwrap: token=[" + getHexBytes(paramArrayOfByte, paramInt1, paramInt2) + "]"); 
    if (this.state != 3)
      throw new GSSException(12, -1, " Unwrap called in invalid state!"); 
    byte[] arrayOfByte = null;
    if (this.cipherHelper.getProto() == 0) {
      WrapToken wrapToken = new WrapToken(this, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
      arrayOfByte = wrapToken.getData();
      setSequencingAndReplayProps(wrapToken, paramMessageProp);
    } else if (this.cipherHelper.getProto() == 1) {
      WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
      arrayOfByte = wrapToken_v2.getData();
      setSequencingAndReplayProps(wrapToken_v2, paramMessageProp);
    } 
    if (DEBUG)
      System.out.println("Krb5Context.unwrap: data=[" + getHexBytes(arrayOfByte, 0, arrayOfByte.length) + "]"); 
    return arrayOfByte;
  }
  
  public final int unwrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp) throws GSSException {
    if (this.state != 3)
      throw new GSSException(12, -1, "Unwrap called in invalid state!"); 
    if (this.cipherHelper.getProto() == 0) {
      WrapToken wrapToken = new WrapToken(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      paramInt2 = wrapToken.getData(paramArrayOfByte2, paramInt3);
      setSequencingAndReplayProps(wrapToken, paramMessageProp);
    } else if (this.cipherHelper.getProto() == 1) {
      WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      paramInt2 = wrapToken_v2.getData(paramArrayOfByte2, paramInt3);
      setSequencingAndReplayProps(wrapToken_v2, paramMessageProp);
    } 
    return paramInt2;
  }
  
  public final int unwrap(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt, MessageProp paramMessageProp) throws GSSException {
    if (this.state != 3)
      throw new GSSException(12, -1, "Unwrap called in invalid state!"); 
    int i = 0;
    if (this.cipherHelper.getProto() == 0) {
      WrapToken wrapToken = new WrapToken(this, paramInputStream, paramMessageProp);
      i = wrapToken.getData(paramArrayOfByte, paramInt);
      setSequencingAndReplayProps(wrapToken, paramMessageProp);
    } else if (this.cipherHelper.getProto() == 1) {
      WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramInputStream, paramMessageProp);
      i = wrapToken_v2.getData(paramArrayOfByte, paramInt);
      setSequencingAndReplayProps(wrapToken_v2, paramMessageProp);
    } 
    return i;
  }
  
  public final void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.state != 3)
      throw new GSSException(12, -1, "Unwrap called in invalid state!"); 
    byte[] arrayOfByte = null;
    if (this.cipherHelper.getProto() == 0) {
      WrapToken wrapToken = new WrapToken(this, paramInputStream, paramMessageProp);
      arrayOfByte = wrapToken.getData();
      setSequencingAndReplayProps(wrapToken, paramMessageProp);
    } else if (this.cipherHelper.getProto() == 1) {
      WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, paramInputStream, paramMessageProp);
      arrayOfByte = wrapToken_v2.getData();
      setSequencingAndReplayProps(wrapToken_v2, paramMessageProp);
    } 
    try {
      paramOutputStream.write(arrayOfByte);
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
  
  public final byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte = null;
    try {
      if (this.cipherHelper.getProto() == 0) {
        MicToken micToken = new MicToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = micToken.encode();
      } else if (this.cipherHelper.getProto() == 1) {
        MicToken_v2 micToken_v2 = new MicToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        arrayOfByte = micToken_v2.encode();
      } 
      return arrayOfByte;
    } catch (IOException iOException) {
      arrayOfByte = null;
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
  
  private int getMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp) throws GSSException {
    int i = 0;
    try {
      if (this.cipherHelper.getProto() == 0) {
        MicToken micToken = new MicToken(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = micToken.encode(paramArrayOfByte2, paramInt3);
      } else if (this.cipherHelper.getProto() == 1) {
        MicToken_v2 micToken_v2 = new MicToken_v2(this, paramMessageProp, paramArrayOfByte1, paramInt1, paramInt2);
        i = micToken_v2.encode(paramArrayOfByte2, paramInt3);
      } 
      return i;
    } catch (IOException iOException) {
      i = 0;
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
  
  private void getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    try {
      if (this.cipherHelper.getProto() == 0) {
        MicToken micToken = new MicToken(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        micToken.encode(paramOutputStream);
      } else if (this.cipherHelper.getProto() == 1) {
        MicToken_v2 micToken_v2 = new MicToken_v2(this, paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
        micToken_v2.encode(paramOutputStream);
      } 
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
  }
  
  public final void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = new byte[paramInputStream.available()];
      paramInputStream.read(arrayOfByte);
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    getMIC(arrayOfByte, 0, arrayOfByte.length, paramOutputStream, paramMessageProp);
  }
  
  public final void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp) throws GSSException {
    if (this.cipherHelper.getProto() == 0) {
      MicToken micToken = new MicToken(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      micToken.verify(paramArrayOfByte2, paramInt3, paramInt4);
      setSequencingAndReplayProps(micToken, paramMessageProp);
    } else if (this.cipherHelper.getProto() == 1) {
      MicToken_v2 micToken_v2 = new MicToken_v2(this, paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
      micToken_v2.verify(paramArrayOfByte2, paramInt3, paramInt4);
      setSequencingAndReplayProps(micToken_v2, paramMessageProp);
    } 
  }
  
  private void verifyMIC(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.cipherHelper.getProto() == 0) {
      MicToken micToken = new MicToken(this, paramInputStream, paramMessageProp);
      micToken.verify(paramArrayOfByte, paramInt1, paramInt2);
      setSequencingAndReplayProps(micToken, paramMessageProp);
    } else if (this.cipherHelper.getProto() == 1) {
      MicToken_v2 micToken_v2 = new MicToken_v2(this, paramInputStream, paramMessageProp);
      micToken_v2.verify(paramArrayOfByte, paramInt1, paramInt2);
      setSequencingAndReplayProps(micToken_v2, paramMessageProp);
    } 
  }
  
  public final void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = new byte[paramInputStream2.available()];
      paramInputStream2.read(arrayOfByte);
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    verifyMIC(paramInputStream1, arrayOfByte, 0, arrayOfByte.length, paramMessageProp);
  }
  
  public final byte[] export() throws GSSException { throw new GSSException(16, -1, "GSS Export Context not available"); }
  
  public final void dispose() {
    this.state = 4;
    this.delegatedCred = null;
  }
  
  public final Provider getProvider() { return Krb5MechFactory.PROVIDER; }
  
  private void setSequencingAndReplayProps(MessageToken paramMessageToken, MessageProp paramMessageProp) {
    if (this.replayDetState || this.sequenceDetState) {
      int i = paramMessageToken.getSequenceNumber();
      this.peerTokenTracker.getProps(i, paramMessageProp);
    } 
  }
  
  private void setSequencingAndReplayProps(MessageToken_v2 paramMessageToken_v2, MessageProp paramMessageProp) {
    if (this.replayDetState || this.sequenceDetState) {
      int i = paramMessageToken_v2.getSequenceNumber();
      this.peerTokenTracker.getProps(i, paramMessageProp);
    } 
  }
  
  private void checkPermission(String paramString1, String paramString2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ServicePermission servicePermission = new ServicePermission(paramString1, paramString2);
      securityManager.checkPermission(servicePermission);
    } 
  }
  
  private static String getHexBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramInt2; b++) {
      byte b1 = paramArrayOfByte[b] >> 4 & 0xF;
      byte b2 = paramArrayOfByte[b] & 0xF;
      stringBuffer.append(Integer.toHexString(b1));
      stringBuffer.append(Integer.toHexString(b2));
      stringBuffer.append(' ');
    } 
    return stringBuffer.toString();
  }
  
  private static String printState(int paramInt) {
    switch (paramInt) {
      case 1:
        return "STATE_NEW";
      case 2:
        return "STATE_IN_PROCESS";
      case 3:
        return "STATE_DONE";
      case 4:
        return "STATE_DELETED";
    } 
    return "Unknown state " + paramInt;
  }
  
  GSSCaller getCaller() { return this.caller; }
  
  public Object inquireSecContext(InquireType paramInquireType) throws GSSException {
    if (!isEstablished())
      throw new GSSException(12, -1, "Security context not established."); 
    switch (paramInquireType) {
      case KRB5_GET_SESSION_KEY:
        return new KerberosSessionKey(this.key);
      case KRB5_GET_TKT_FLAGS:
        return this.tktFlags.clone();
      case KRB5_GET_AUTHZ_DATA:
        if (isInitiator())
          throw new GSSException(16, -1, "AuthzData not available on initiator side."); 
        return (this.authzData == null) ? null : this.authzData.clone();
      case KRB5_GET_AUTHTIME:
        return this.authTime;
    } 
    throw new GSSException(16, -1, "Inquire type not supported.");
  }
  
  public void setTktFlags(boolean[] paramArrayOfBoolean) { this.tktFlags = paramArrayOfBoolean; }
  
  public void setAuthTime(String paramString) { this.authTime = paramString; }
  
  public void setAuthzData(AuthorizationDataEntry[] paramArrayOfAuthorizationDataEntry) { this.authzData = paramArrayOfAuthorizationDataEntry; }
  
  static class KerberosSessionKey implements Key {
    private static final long serialVersionUID = 699307378954123869L;
    
    private final EncryptionKey key;
    
    KerberosSessionKey(EncryptionKey param1EncryptionKey) { this.key = param1EncryptionKey; }
    
    public String getAlgorithm() { return Integer.toString(this.key.getEType()); }
    
    public String getFormat() { return "RAW"; }
    
    public byte[] getEncoded() throws GSSException { return (byte[])this.key.getBytes().clone(); }
    
    public String toString() { return "Kerberos session key: etype: " + this.key.getEType() + "\n" + (new HexDumpEncoder()).encodeBuffer(this.key.getBytes()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */