package sun.security.jgss;

import com.sun.security.jgss.ExtendedGSSContext;
import com.sun.security.jgss.InquireSecContextPermission;
import com.sun.security.jgss.InquireType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.ObjectIdentifier;

class GSSContextImpl implements ExtendedGSSContext {
  private final GSSManagerImpl gssManager;
  
  private final boolean initiator;
  
  private static final int PRE_INIT = 1;
  
  private static final int IN_PROGRESS = 2;
  
  private static final int READY = 3;
  
  private static final int DELETED = 4;
  
  private int currentState = 1;
  
  private GSSContextSpi mechCtxt = null;
  
  private Oid mechOid = null;
  
  private ObjectIdentifier objId = null;
  
  private GSSCredentialImpl myCred = null;
  
  private GSSNameImpl srcName = null;
  
  private GSSNameImpl targName = null;
  
  private int reqLifetime = Integer.MAX_VALUE;
  
  private ChannelBinding channelBindings = null;
  
  private boolean reqConfState = true;
  
  private boolean reqIntegState = true;
  
  private boolean reqMutualAuthState = true;
  
  private boolean reqReplayDetState = true;
  
  private boolean reqSequenceDetState = true;
  
  private boolean reqCredDelegState = false;
  
  private boolean reqAnonState = false;
  
  private boolean reqDelegPolicyState = false;
  
  public GSSContextImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, Oid paramOid, GSSCredential paramGSSCredential, int paramInt) throws GSSException {
    if (paramGSSName == null || !(paramGSSName instanceof GSSNameImpl))
      throw new GSSException(3); 
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    this.gssManager = paramGSSManagerImpl;
    this.myCred = (GSSCredentialImpl)paramGSSCredential;
    this.reqLifetime = paramInt;
    this.targName = (GSSNameImpl)paramGSSName;
    this.mechOid = paramOid;
    this.initiator = true;
  }
  
  public GSSContextImpl(GSSManagerImpl paramGSSManagerImpl, GSSCredential paramGSSCredential) throws GSSException {
    this.gssManager = paramGSSManagerImpl;
    this.myCred = (GSSCredentialImpl)paramGSSCredential;
    this.initiator = false;
  }
  
  public GSSContextImpl(GSSManagerImpl paramGSSManagerImpl, byte[] paramArrayOfByte) throws GSSException {
    this.gssManager = paramGSSManagerImpl;
    this.mechCtxt = paramGSSManagerImpl.getMechanismContext(paramArrayOfByte);
    this.initiator = this.mechCtxt.isInitiator();
    this.mechOid = this.mechCtxt.getMech();
  }
  
  public byte[] initSecContext(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(600);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2);
    int i = initSecContext(byteArrayInputStream, byteArrayOutputStream);
    return (i == 0) ? null : byteArrayOutputStream.toByteArray();
  }
  
  public int initSecContext(InputStream paramInputStream, OutputStream paramOutputStream) throws GSSException {
    if (this.mechCtxt != null && this.currentState != 2)
      throw new GSSExceptionImpl(11, "Illegal call to initSecContext"); 
    GSSHeader gSSHeader = null;
    int i = -1;
    GSSCredentialSpi gSSCredentialSpi = null;
    boolean bool = false;
    try {
      if (this.mechCtxt == null) {
        if (this.myCred != null)
          try {
            gSSCredentialSpi = this.myCred.getElement(this.mechOid, true);
          } catch (GSSException gSSException) {
            if (GSSUtil.isSpNegoMech(this.mechOid) && gSSException.getMajor() == 13) {
              gSSCredentialSpi = this.myCred.getElement(this.myCred.getMechs()[0], true);
            } else {
              throw gSSException;
            } 
          }  
        GSSNameSpi gSSNameSpi = this.targName.getElement(this.mechOid);
        this.mechCtxt = this.gssManager.getMechanismContext(gSSNameSpi, gSSCredentialSpi, this.reqLifetime, this.mechOid);
        this.mechCtxt.requestConf(this.reqConfState);
        this.mechCtxt.requestInteg(this.reqIntegState);
        this.mechCtxt.requestCredDeleg(this.reqCredDelegState);
        this.mechCtxt.requestMutualAuth(this.reqMutualAuthState);
        this.mechCtxt.requestReplayDet(this.reqReplayDetState);
        this.mechCtxt.requestSequenceDet(this.reqSequenceDetState);
        this.mechCtxt.requestAnonymity(this.reqAnonState);
        this.mechCtxt.setChannelBinding(this.channelBindings);
        this.mechCtxt.requestDelegPolicy(this.reqDelegPolicyState);
        this.objId = new ObjectIdentifier(this.mechOid.toString());
        this.currentState = 2;
        bool = true;
      } else if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && !GSSUtil.isSpNegoMech(this.mechOid)) {
        gSSHeader = new GSSHeader(paramInputStream);
        if (!gSSHeader.getOid().equals(this.objId))
          throw new GSSExceptionImpl(10, "Mechanism not equal to " + this.mechOid.toString() + " in initSecContext token"); 
        i = gSSHeader.getMechTokenLength();
      } 
      byte[] arrayOfByte = this.mechCtxt.initSecContext(paramInputStream, i);
      int j = 0;
      if (arrayOfByte != null) {
        j = arrayOfByte.length;
        if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && (bool || !GSSUtil.isSpNegoMech(this.mechOid))) {
          gSSHeader = new GSSHeader(this.objId, arrayOfByte.length);
          j += gSSHeader.encode(paramOutputStream);
        } 
        paramOutputStream.write(arrayOfByte);
      } 
      if (this.mechCtxt.isEstablished())
        this.currentState = 3; 
      return j;
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(10, iOException.getMessage());
    } 
  }
  
  public byte[] acceptSecContext(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
    acceptSecContext(new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), byteArrayOutputStream);
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    return (arrayOfByte.length == 0) ? null : arrayOfByte;
  }
  
  public void acceptSecContext(InputStream paramInputStream, OutputStream paramOutputStream) throws GSSException {
    if (this.mechCtxt != null && this.currentState != 2)
      throw new GSSExceptionImpl(11, "Illegal call to acceptSecContext"); 
    GSSHeader gSSHeader = null;
    int i = -1;
    GSSCredentialSpi gSSCredentialSpi = null;
    try {
      if (this.mechCtxt == null) {
        gSSHeader = new GSSHeader(paramInputStream);
        i = gSSHeader.getMechTokenLength();
        this.objId = gSSHeader.getOid();
        this.mechOid = new Oid(this.objId.toString());
        if (this.myCred != null)
          gSSCredentialSpi = this.myCred.getElement(this.mechOid, false); 
        this.mechCtxt = this.gssManager.getMechanismContext(gSSCredentialSpi, this.mechOid);
        this.mechCtxt.setChannelBinding(this.channelBindings);
        this.currentState = 2;
      } else if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && !GSSUtil.isSpNegoMech(this.mechOid)) {
        gSSHeader = new GSSHeader(paramInputStream);
        if (!gSSHeader.getOid().equals(this.objId))
          throw new GSSExceptionImpl(10, "Mechanism not equal to " + this.mechOid.toString() + " in acceptSecContext token"); 
        i = gSSHeader.getMechTokenLength();
      } 
      byte[] arrayOfByte = this.mechCtxt.acceptSecContext(paramInputStream, i);
      if (arrayOfByte != null) {
        int j = arrayOfByte.length;
        if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS") && !GSSUtil.isSpNegoMech(this.mechOid)) {
          gSSHeader = new GSSHeader(this.objId, arrayOfByte.length);
          j += gSSHeader.encode(paramOutputStream);
        } 
        paramOutputStream.write(arrayOfByte);
      } 
      if (this.mechCtxt.isEstablished())
        this.currentState = 3; 
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(10, iOException.getMessage());
    } 
  }
  
  public boolean isEstablished() { return (this.mechCtxt == null) ? false : ((this.currentState == 3)); }
  
  public int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2) throws GSSException {
    if (this.mechCtxt != null)
      return this.mechCtxt.getWrapSizeLimit(paramInt1, paramBoolean, paramInt2); 
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null)
      return this.mechCtxt.wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); 
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null) {
      this.mechCtxt.wrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    } 
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null)
      return this.mechCtxt.unwrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); 
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null) {
      this.mechCtxt.unwrap(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    } 
  }
  
  public byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null)
      return this.mechCtxt.getMIC(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); 
    throw new GSSExceptionImpl(12, "No mechanism context yet!");
  }
  
  public void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null) {
      this.mechCtxt.getMIC(paramInputStream, paramOutputStream, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    } 
  }
  
  public void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null) {
      this.mechCtxt.verifyMIC(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    } 
  }
  
  public void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp) throws GSSException {
    if (this.mechCtxt != null) {
      this.mechCtxt.verifyMIC(paramInputStream1, paramInputStream2, paramMessageProp);
    } else {
      throw new GSSExceptionImpl(12, "No mechanism context yet!");
    } 
  }
  
  public byte[] export() throws GSSException {
    byte[] arrayOfByte = null;
    if (this.mechCtxt.isTransferable() && this.mechCtxt.getProvider().getName().equals("SunNativeGSS"))
      arrayOfByte = this.mechCtxt.export(); 
    return arrayOfByte;
  }
  
  public void requestMutualAuth(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqMutualAuthState = paramBoolean; 
  }
  
  public void requestReplayDet(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqReplayDetState = paramBoolean; 
  }
  
  public void requestSequenceDet(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqSequenceDetState = paramBoolean; 
  }
  
  public void requestCredDeleg(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqCredDelegState = paramBoolean; 
  }
  
  public void requestAnonymity(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqAnonState = paramBoolean; 
  }
  
  public void requestConf(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqConfState = paramBoolean; 
  }
  
  public void requestInteg(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqIntegState = paramBoolean; 
  }
  
  public void requestLifetime(int paramInt) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqLifetime = paramInt; 
  }
  
  public void setChannelBinding(ChannelBinding paramChannelBinding) throws GSSException {
    if (this.mechCtxt == null)
      this.channelBindings = paramChannelBinding; 
  }
  
  public boolean getCredDelegState() { return (this.mechCtxt != null) ? this.mechCtxt.getCredDelegState() : this.reqCredDelegState; }
  
  public boolean getMutualAuthState() { return (this.mechCtxt != null) ? this.mechCtxt.getMutualAuthState() : this.reqMutualAuthState; }
  
  public boolean getReplayDetState() { return (this.mechCtxt != null) ? this.mechCtxt.getReplayDetState() : this.reqReplayDetState; }
  
  public boolean getSequenceDetState() { return (this.mechCtxt != null) ? this.mechCtxt.getSequenceDetState() : this.reqSequenceDetState; }
  
  public boolean getAnonymityState() { return (this.mechCtxt != null) ? this.mechCtxt.getAnonymityState() : this.reqAnonState; }
  
  public boolean isTransferable() { return (this.mechCtxt != null) ? this.mechCtxt.isTransferable() : 0; }
  
  public boolean isProtReady() { return (this.mechCtxt != null) ? this.mechCtxt.isProtReady() : 0; }
  
  public boolean getConfState() { return (this.mechCtxt != null) ? this.mechCtxt.getConfState() : this.reqConfState; }
  
  public boolean getIntegState() { return (this.mechCtxt != null) ? this.mechCtxt.getIntegState() : this.reqIntegState; }
  
  public int getLifetime() { return (this.mechCtxt != null) ? this.mechCtxt.getLifetime() : this.reqLifetime; }
  
  public GSSName getSrcName() throws GSSException {
    if (this.srcName == null)
      this.srcName = GSSNameImpl.wrapElement(this.gssManager, this.mechCtxt.getSrcName()); 
    return this.srcName;
  }
  
  public GSSName getTargName() throws GSSException {
    if (this.targName == null)
      this.targName = GSSNameImpl.wrapElement(this.gssManager, this.mechCtxt.getTargName()); 
    return this.targName;
  }
  
  public Oid getMech() throws GSSException { return (this.mechCtxt != null) ? this.mechCtxt.getMech() : this.mechOid; }
  
  public GSSCredential getDelegCred() throws GSSException {
    if (this.mechCtxt == null)
      throw new GSSExceptionImpl(12, "No mechanism context yet!"); 
    GSSCredentialSpi gSSCredentialSpi = this.mechCtxt.getDelegCred();
    return (gSSCredentialSpi == null) ? null : new GSSCredentialImpl(this.gssManager, gSSCredentialSpi);
  }
  
  public boolean isInitiator() { return this.initiator; }
  
  public void dispose() throws GSSException {
    this.currentState = 4;
    if (this.mechCtxt != null) {
      this.mechCtxt.dispose();
      this.mechCtxt = null;
    } 
    this.myCred = null;
    this.srcName = null;
    this.targName = null;
  }
  
  public Object inquireSecContext(InquireType paramInquireType) throws GSSException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new InquireSecContextPermission(paramInquireType.toString())); 
    if (this.mechCtxt == null)
      throw new GSSException(12); 
    return this.mechCtxt.inquireSecContext(paramInquireType);
  }
  
  public void requestDelegPolicy(boolean paramBoolean) throws GSSException {
    if (this.mechCtxt == null && this.initiator)
      this.reqDelegPolicyState = paramBoolean; 
  }
  
  public boolean getDelegPolicyState() { return (this.mechCtxt != null) ? this.mechCtxt.getDelegPolicyState() : this.reqDelegPolicyState; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */