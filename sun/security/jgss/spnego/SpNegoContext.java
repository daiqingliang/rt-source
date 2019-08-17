package sun.security.jgss.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import com.sun.security.jgss.InquireType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Provider;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSCredentialImpl;
import sun.security.jgss.GSSNameImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class SpNegoContext implements GSSContextSpi {
  private static final int STATE_NEW = 1;
  
  private static final int STATE_IN_PROCESS = 2;
  
  private static final int STATE_DONE = 3;
  
  private static final int STATE_DELETED = 4;
  
  private int state = 1;
  
  private boolean credDelegState = false;
  
  private boolean mutualAuthState = true;
  
  private boolean replayDetState = true;
  
  private boolean sequenceDetState = true;
  
  private boolean confState = true;
  
  private boolean integState = true;
  
  private boolean delegPolicyState = false;
  
  private GSSNameSpi peerName = null;
  
  private GSSNameSpi myName = null;
  
  private SpNegoCredElement myCred = null;
  
  private GSSContext mechContext = null;
  
  private byte[] DER_mechTypes = null;
  
  private int lifetime;
  
  private ChannelBinding channelBinding;
  
  private boolean initiator;
  
  private Oid internal_mech = null;
  
  private final SpNegoMechFactory factory;
  
  static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.spnego.debug"))).booleanValue();
  
  public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt) throws GSSException {
    if (paramGSSNameSpi == null)
      throw new IllegalArgumentException("Cannot have null peer name"); 
    if (paramGSSCredentialSpi != null && !(paramGSSCredentialSpi instanceof SpNegoCredElement))
      throw new IllegalArgumentException("Wrong cred element type"); 
    this.peerName = paramGSSNameSpi;
    this.myCred = (SpNegoCredElement)paramGSSCredentialSpi;
    this.lifetime = paramInt;
    this.initiator = true;
    this.factory = paramSpNegoMechFactory;
  }
  
  public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSCredentialSpi paramGSSCredentialSpi) throws GSSException {
    if (paramGSSCredentialSpi != null && !(paramGSSCredentialSpi instanceof SpNegoCredElement))
      throw new IllegalArgumentException("Wrong cred element type"); 
    this.myCred = (SpNegoCredElement)paramGSSCredentialSpi;
    this.initiator = false;
    this.factory = paramSpNegoMechFactory;
  }
  
  public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, byte[] paramArrayOfByte) throws GSSException { throw new GSSException(16, -1, "GSS Import Context not available"); }
  
  public final void requestConf(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.confState = paramBoolean; 
  }
  
  public final boolean getConfState() { return this.confState; }
  
  public final void requestInteg(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.integState = paramBoolean; 
  }
  
  public final void requestDelegPolicy(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.delegPolicyState = paramBoolean; 
  }
  
  public final boolean getIntegState() { return this.integState; }
  
  public final boolean getDelegPolicyState() { return (isInitiator() && this.mechContext != null && this.mechContext instanceof ExtendedGSSContext && (this.state == 2 || this.state == 3)) ? ((ExtendedGSSContext)this.mechContext).getDelegPolicyState() : this.delegPolicyState; }
  
  public final void requestCredDeleg(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.credDelegState = paramBoolean; 
  }
  
  public final boolean getCredDelegState() { return (isInitiator() && this.mechContext != null && (this.state == 2 || this.state == 3)) ? this.mechContext.getCredDelegState() : this.credDelegState; }
  
  public final void requestMutualAuth(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.mutualAuthState = paramBoolean; 
  }
  
  public final boolean getMutualAuthState() { return this.mutualAuthState; }
  
  public final Oid getMech() { return isEstablished() ? getNegotiatedMech() : SpNegoMechFactory.GSS_SPNEGO_MECH_OID; }
  
  public final Oid getNegotiatedMech() { return this.internal_mech; }
  
  public final Provider getProvider() { return SpNegoMechFactory.PROVIDER; }
  
  public final void dispose() throws GSSException {
    this.mechContext = null;
    this.state = 4;
  }
  
  public final boolean isInitiator() { return this.initiator; }
  
  public final boolean isProtReady() { return (this.state == 3); }
  
  public final byte[] initSecContext(InputStream paramInputStream, int paramInt) throws GSSException {
    byte[] arrayOfByte1 = null;
    NegTokenInit negTokenInit = null;
    byte[] arrayOfByte2 = null;
    byte b = 11;
    if (DEBUG)
      System.out.println("Entered SpNego.initSecContext with state=" + printState(this.state)); 
    if (!isInitiator())
      throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext"); 
    try {
      if (this.state == 1) {
        this.state = 2;
        b = 13;
        Oid[] arrayOfOid = getAvailableMechs();
        this.DER_mechTypes = getEncodedMechs(arrayOfOid);
        this.internal_mech = arrayOfOid[0];
        arrayOfByte2 = GSS_initSecContext(null);
        b = 10;
        negTokenInit = new NegTokenInit(this.DER_mechTypes, getContextFlags(), arrayOfByte2, null);
        if (DEBUG)
          System.out.println("SpNegoContext.initSecContext: sending token of type = " + SpNegoToken.getTokenName(negTokenInit.getType())); 
        arrayOfByte1 = negTokenInit.getEncoded();
      } else if (this.state == 2) {
        b = 11;
        if (paramInputStream == null)
          throw new GSSException(b, -1, "No token received from peer!"); 
        b = 10;
        byte[] arrayOfByte = new byte[paramInputStream.available()];
        SpNegoToken.readFully(paramInputStream, arrayOfByte);
        if (DEBUG)
          System.out.println("SpNegoContext.initSecContext: process received token = " + SpNegoToken.getHexBytes(arrayOfByte)); 
        NegTokenTarg negTokenTarg = new NegTokenTarg(arrayOfByte);
        if (DEBUG)
          System.out.println("SpNegoContext.initSecContext: received token of type = " + SpNegoToken.getTokenName(negTokenTarg.getType())); 
        this.internal_mech = negTokenTarg.getSupportedMech();
        if (this.internal_mech == null)
          throw new GSSException(b, -1, "supported mechanism from server is null"); 
        SpNegoToken.NegoResult negoResult = null;
        int i = negTokenTarg.getNegotiatedResult();
        switch (i) {
          case 0:
            negoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
            this.state = 3;
            break;
          case 1:
            negoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
            this.state = 2;
            break;
          case 2:
            negoResult = SpNegoToken.NegoResult.REJECT;
            this.state = 4;
            break;
          default:
            this.state = 3;
            break;
        } 
        b = 2;
        if (negoResult == SpNegoToken.NegoResult.REJECT)
          throw new GSSException(b, -1, this.internal_mech.toString()); 
        b = 10;
        if (negoResult == SpNegoToken.NegoResult.ACCEPT_COMPLETE || negoResult == SpNegoToken.NegoResult.ACCEPT_INCOMPLETE) {
          byte[] arrayOfByte3 = negTokenTarg.getResponseToken();
          if (arrayOfByte3 == null) {
            if (!isMechContextEstablished())
              throw new GSSException(b, -1, "mechanism token from server is null"); 
          } else {
            arrayOfByte2 = GSS_initSecContext(arrayOfByte3);
          } 
          if (!GSSUtil.useMSInterop()) {
            byte[] arrayOfByte4 = negTokenTarg.getMechListMIC();
            if (!verifyMechListMIC(this.DER_mechTypes, arrayOfByte4))
              throw new GSSException(b, -1, "verification of MIC on MechList Failed!"); 
          } 
          if (isMechContextEstablished()) {
            this.state = 3;
            arrayOfByte1 = arrayOfByte2;
            if (DEBUG)
              System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech)); 
          } else {
            negTokenInit = new NegTokenInit(null, null, arrayOfByte2, null);
            if (DEBUG)
              System.out.println("SpNegoContext.initSecContext: continue sending token of type = " + SpNegoToken.getTokenName(negTokenInit.getType())); 
            arrayOfByte1 = negTokenInit.getEncoded();
          } 
        } 
      } else if (DEBUG) {
        System.out.println(this.state);
      } 
      if (DEBUG && arrayOfByte1 != null)
        System.out.println("SNegoContext.initSecContext: sending token = " + SpNegoToken.getHexBytes(arrayOfByte1)); 
    } catch (GSSException gSSException1) {
      GSSException gSSException2 = new GSSException(b, -1, gSSException1.getMessage());
      gSSException2.initCause(gSSException1);
      throw gSSException2;
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    return arrayOfByte1;
  }
  
  public final byte[] acceptSecContext(InputStream paramInputStream, int paramInt) throws GSSException {
    byte[] arrayOfByte = null;
    boolean bool = true;
    if (DEBUG)
      System.out.println("Entered SpNegoContext.acceptSecContext with state=" + printState(this.state)); 
    if (isInitiator())
      throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext"); 
    try {
      if (this.state == 1) {
        byte[] arrayOfByte2;
        SpNegoToken.NegoResult negoResult;
        this.state = 2;
        byte[] arrayOfByte1 = new byte[paramInputStream.available()];
        SpNegoToken.readFully(paramInputStream, arrayOfByte1);
        if (DEBUG)
          System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(arrayOfByte1)); 
        NegTokenInit negTokenInit = new NegTokenInit(arrayOfByte1);
        if (DEBUG)
          System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(negTokenInit.getType())); 
        Oid[] arrayOfOid1 = negTokenInit.getMechTypeList();
        this.DER_mechTypes = negTokenInit.getMechTypes();
        if (this.DER_mechTypes == null)
          bool = false; 
        Oid[] arrayOfOid2 = getAvailableMechs();
        Oid oid = negotiate_mech_type(arrayOfOid2, arrayOfOid1);
        if (oid == null)
          bool = false; 
        this.internal_mech = oid;
        if (arrayOfOid1[0].equals(oid) || (GSSUtil.isKerberosMech(arrayOfOid1[0]) && GSSUtil.isKerberosMech(oid))) {
          if (DEBUG && !oid.equals(arrayOfOid1[0]))
            System.out.println("SpNegoContext.acceptSecContext: negotiated mech adjusted to " + arrayOfOid1[0]); 
          byte[] arrayOfByte3 = negTokenInit.getMechToken();
          if (arrayOfByte3 == null)
            throw new GSSException(11, -1, "mechToken is missing"); 
          arrayOfByte2 = GSS_acceptSecContext(arrayOfByte3);
          oid = arrayOfOid1[0];
        } else {
          arrayOfByte2 = null;
        } 
        if (!GSSUtil.useMSInterop() && bool)
          bool = verifyMechListMIC(this.DER_mechTypes, negTokenInit.getMechListMIC()); 
        if (bool) {
          if (isMechContextEstablished()) {
            negoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
            this.state = 3;
            setContextFlags();
            if (DEBUG)
              System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech)); 
          } else {
            negoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
            this.state = 2;
          } 
        } else {
          negoResult = SpNegoToken.NegoResult.REJECT;
          this.state = 3;
        } 
        if (DEBUG) {
          System.out.println("SpNegoContext.acceptSecContext: mechanism wanted = " + oid);
          System.out.println("SpNegoContext.acceptSecContext: negotiated result = " + negoResult);
        } 
        NegTokenTarg negTokenTarg = new NegTokenTarg(negoResult.ordinal(), oid, arrayOfByte2, null);
        if (DEBUG)
          System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(negTokenTarg.getType())); 
        arrayOfByte = negTokenTarg.getEncoded();
      } else if (this.state == 2) {
        SpNegoToken.NegoResult negoResult;
        byte[] arrayOfByte1 = new byte[paramInputStream.available()];
        SpNegoToken.readFully(paramInputStream, arrayOfByte1);
        if (DEBUG)
          System.out.println("SpNegoContext.acceptSecContext: receiving token = " + SpNegoToken.getHexBytes(arrayOfByte1)); 
        NegTokenTarg negTokenTarg1 = new NegTokenTarg(arrayOfByte1);
        if (DEBUG)
          System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(negTokenTarg1.getType())); 
        byte[] arrayOfByte2 = negTokenTarg1.getResponseToken();
        byte[] arrayOfByte3 = GSS_acceptSecContext(arrayOfByte2);
        if (arrayOfByte3 == null)
          bool = false; 
        if (bool) {
          if (isMechContextEstablished()) {
            negoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
            this.state = 3;
          } else {
            negoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
            this.state = 2;
          } 
        } else {
          negoResult = SpNegoToken.NegoResult.REJECT;
          this.state = 3;
        } 
        NegTokenTarg negTokenTarg2 = new NegTokenTarg(negoResult.ordinal(), null, arrayOfByte3, null);
        if (DEBUG)
          System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(negTokenTarg2.getType())); 
        arrayOfByte = negTokenTarg2.getEncoded();
      } else if (DEBUG) {
        System.out.println("AcceptSecContext: state = " + this.state);
      } 
      if (DEBUG)
        System.out.println("SpNegoContext.acceptSecContext: sending token = " + SpNegoToken.getHexBytes(arrayOfByte)); 
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(11, -1, iOException.getMessage());
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    if (this.state == 3)
      setContextFlags(); 
    return arrayOfByte;
  }
  
  private Oid[] getAvailableMechs() {
    if (this.myCred != null) {
      Oid[] arrayOfOid = new Oid[1];
      arrayOfOid[0] = this.myCred.getInternalMech();
      return arrayOfOid;
    } 
    return this.factory.availableMechs;
  }
  
  private byte[] getEncodedMechs(Oid[] paramArrayOfOid) throws IOException, GSSException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    for (byte b = 0; b < paramArrayOfOid.length; b++) {
      byte[] arrayOfByte = paramArrayOfOid[b].getDER();
      derOutputStream1.write(arrayOfByte);
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  private BitArray getContextFlags() {
    BitArray bitArray = new BitArray(7);
    if (getCredDelegState())
      bitArray.set(0, true); 
    if (getMutualAuthState())
      bitArray.set(1, true); 
    if (getReplayDetState())
      bitArray.set(2, true); 
    if (getSequenceDetState())
      bitArray.set(3, true); 
    if (getConfState())
      bitArray.set(5, true); 
    if (getIntegState())
      bitArray.set(6, true); 
    return bitArray;
  }
  
  private void setContextFlags() throws GSSException {
    if (this.mechContext != null) {
      if (this.mechContext.getCredDelegState())
        this.credDelegState = true; 
      if (!this.mechContext.getMutualAuthState())
        this.mutualAuthState = false; 
      if (!this.mechContext.getReplayDetState())
        this.replayDetState = false; 
      if (!this.mechContext.getSequenceDetState())
        this.sequenceDetState = false; 
      if (!this.mechContext.getIntegState())
        this.integState = false; 
      if (!this.mechContext.getConfState())
        this.confState = false; 
    } 
  }
  
  private boolean verifyMechListMIC(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GSSException {
    if (paramArrayOfByte2 == null) {
      if (DEBUG)
        System.out.println("SpNegoContext: no MIC token validation"); 
      return true;
    } 
    if (!this.mechContext.getIntegState()) {
      if (DEBUG)
        System.out.println("SpNegoContext: no MIC token validation - mechanism does not support integrity"); 
      return true;
    } 
    boolean bool = false;
    try {
      MessageProp messageProp = new MessageProp(0, true);
      verifyMIC(paramArrayOfByte2, 0, paramArrayOfByte2.length, paramArrayOfByte1, 0, paramArrayOfByte1.length, messageProp);
      bool = true;
    } catch (GSSException gSSException) {
      bool = false;
      if (DEBUG)
        System.out.println("SpNegoContext: MIC validation failed! " + gSSException.getMessage()); 
    } 
    return bool;
  }
  
  private byte[] GSS_initSecContext(byte[] paramArrayOfByte) throws GSSException {
    byte[] arrayOfByte = null;
    if (this.mechContext == null) {
      GSSName gSSName = this.factory.manager.createName(this.peerName.toString(), this.peerName.getStringNameType(), this.internal_mech);
      GSSCredentialImpl gSSCredentialImpl = null;
      if (this.myCred != null)
        gSSCredentialImpl = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred()); 
      this.mechContext = this.factory.manager.createContext(gSSName, this.internal_mech, gSSCredentialImpl, 0);
      this.mechContext.requestConf(this.confState);
      this.mechContext.requestInteg(this.integState);
      this.mechContext.requestCredDeleg(this.credDelegState);
      this.mechContext.requestMutualAuth(this.mutualAuthState);
      this.mechContext.requestReplayDet(this.replayDetState);
      this.mechContext.requestSequenceDet(this.sequenceDetState);
      if (this.mechContext instanceof ExtendedGSSContext)
        ((ExtendedGSSContext)this.mechContext).requestDelegPolicy(this.delegPolicyState); 
    } 
    if (paramArrayOfByte != null) {
      arrayOfByte = paramArrayOfByte;
    } else {
      arrayOfByte = new byte[0];
    } 
    return this.mechContext.initSecContext(arrayOfByte, 0, arrayOfByte.length);
  }
  
  private byte[] GSS_acceptSecContext(byte[] paramArrayOfByte) throws GSSException {
    if (this.mechContext == null) {
      GSSCredentialImpl gSSCredentialImpl = null;
      if (this.myCred != null)
        gSSCredentialImpl = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred()); 
      this.mechContext = this.factory.manager.createContext(gSSCredentialImpl);
    } 
    return this.mechContext.acceptSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private static Oid negotiate_mech_type(Oid[] paramArrayOfOid1, Oid[] paramArrayOfOid2) {
    for (byte b = 0; b < paramArrayOfOid1.length; b++) {
      for (byte b1 = 0; b1 < paramArrayOfOid2.length; b1++) {
        if (paramArrayOfOid2[b1].equals(paramArrayOfOid1[b])) {
          if (DEBUG)
            System.out.println("SpNegoContext: negotiated mechanism = " + paramArrayOfOid2[b1]); 
          return paramArrayOfOid2[b1];
        } 
      } 
    } 
    return null;
  }
  
  public final boolean isEstablished() { return (this.state == 3); }
  
  public final boolean isMechContextEstablished() {
    if (this.mechContext != null)
      return this.mechContext.isEstablished(); 
    if (DEBUG)
      System.out.println("The underlying mechanism context has not been initialized"); 
    return false;
  }
  
  public final byte[] export() throws GSSException { throw new GSSException(16, -1, "GSS Export Context not available"); }
  
  public final void setChannelBinding(ChannelBinding paramChannelBinding) throws GSSException { this.channelBinding = paramChannelBinding; }
  
  final ChannelBinding getChannelBinding() { return this.channelBinding; }
  
  public final void requestAnonymity(boolean paramBoolean) throws GSSException {}
  
  public final boolean getAnonymityState() { return false; }
  
  public void requestLifetime(int paramInt) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.lifetime = paramInt; 
  }
  
  public final int getLifetime() { return (this.mechContext != null) ? this.mechContext.getLifetime() : Integer.MAX_VALUE; }
  
  public final boolean isTransferable() { return false; }
  
  public final void requestSequenceDet(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.sequenceDetState = paramBoolean; 
  }
  
  public final boolean getSequenceDetState() { return (this.sequenceDetState || this.replayDetState); }
  
  public final void requestReplayDet(boolean paramBoolean) throws GSSException {
    if (this.state == 1 && isInitiator())
      this.replayDetState = paramBoolean; 
  }
  
  public final boolean getReplayDetState() { return (this.replayDetState || this.sequenceDetState); }
  
  public final GSSNameSpi getTargName() throws GSSException {
    if (this.mechContext != null) {
      GSSNameImpl gSSNameImpl = (GSSNameImpl)this.mechContext.getTargName();
      this.peerName = gSSNameImpl.getElement(this.internal_mech);
      return this.peerName;
    } 
    if (DEBUG)
      System.out.println("The underlying mechanism context has not been initialized"); 
    return null;
  }
  
  public final GSSNameSpi getSrcName() throws GSSException {
    if (this.mechContext != null) {
      GSSNameImpl gSSNameImpl = (GSSNameImpl)this.mechContext.getSrcName();
      this.myName = gSSNameImpl.getElement(this.internal_mech);
      return this.myName;
    } 
    if (DEBUG)
      System.out.println("The underlying mechanism context has not been initialized"); 
    return null;
  }
  
  public final GSSCredentialSpi getDelegCred() throws GSSException {
    if (this.state != 2 && this.state != 3)
      throw new GSSException(12); 
    if (this.mechContext != null) {
      GSSCredentialImpl gSSCredentialImpl = (GSSCredentialImpl)this.mechContext.getDelegCred();
      if (gSSCredentialImpl == null)
        return null; 
      boolean bool = false;
      if (gSSCredentialImpl.getUsage() == 1)
        bool = true; 
      GSSCredentialSpi gSSCredentialSpi = gSSCredentialImpl.getElement(this.internal_mech, bool);
      SpNegoCredElement spNegoCredElement = new SpNegoCredElement(gSSCredentialSpi);
      return spNegoCredElement.getInternalCred();
    } 
    throw new GSSException(12, -1, "getDelegCred called in invalid state!");
  }
  
  public final int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2) throws GSSException {
    if (this.mechContext != null)
      return this.mechContext.getWrapSizeLimit(paramInt1, paramBoolean, paramInt2); 
    throw new GSSException(12, -1, "getWrapSizeLimit called in invalid state!");
  }
  
  public final byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null)
      return this.mechContext.wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); 
    throw new GSSException(12, -1, "Wrap called in invalid state!");
  }
  
  public final void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null) {
      this.mechContext.wrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "Wrap called in invalid state!");
    } 
  }
  
  public final byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null)
      return this.mechContext.unwrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); 
    throw new GSSException(12, -1, "UnWrap called in invalid state!");
  }
  
  public final void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null) {
      this.mechContext.unwrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "UnWrap called in invalid state!");
    } 
  }
  
  public final byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null)
      return this.mechContext.getMIC(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); 
    throw new GSSException(12, -1, "getMIC called in invalid state!");
  }
  
  public final void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null) {
      this.mechContext.getMIC(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "getMIC called in invalid state!");
    } 
  }
  
  public final void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null) {
      this.mechContext.verifyMIC(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "verifyMIC called in invalid state!");
    } 
  }
  
  public final void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechContext != null) {
      this.mechContext.verifyMIC(paramInputStream1, paramInputStream2, paramMessageProp);
    } else {
      throw new GSSException(12, -1, "verifyMIC called in invalid state!");
    } 
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
  
  public Object inquireSecContext(InquireType paramInquireType) throws GSSException {
    if (this.mechContext == null)
      throw new GSSException(12, -1, "Underlying mech not established."); 
    if (this.mechContext instanceof ExtendedGSSContext)
      return ((ExtendedGSSContext)this.mechContext).inquireSecContext(paramInquireType); 
    throw new GSSException(2, -1, "inquireSecContext not supported by underlying mech.");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spnego\SpNegoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */