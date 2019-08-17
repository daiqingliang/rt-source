package sun.security.jgss.wrapper;

import com.sun.security.jgss.InquireType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import javax.security.auth.kerberos.DelegationPermission;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSHeader;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.NegTokenInit;
import sun.security.jgss.spnego.NegTokenTarg;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

class NativeGSSContext implements GSSContextSpi {
  private static final int GSS_C_DELEG_FLAG = 1;
  
  private static final int GSS_C_MUTUAL_FLAG = 2;
  
  private static final int GSS_C_REPLAY_FLAG = 4;
  
  private static final int GSS_C_SEQUENCE_FLAG = 8;
  
  private static final int GSS_C_CONF_FLAG = 16;
  
  private static final int GSS_C_INTEG_FLAG = 32;
  
  private static final int GSS_C_ANON_FLAG = 64;
  
  private static final int GSS_C_PROT_READY_FLAG = 128;
  
  private static final int GSS_C_TRANS_FLAG = 256;
  
  private static final int NUM_OF_INQUIRE_VALUES = 6;
  
  private long pContext = 0L;
  
  private GSSNameElement srcName;
  
  private GSSNameElement targetName;
  
  private GSSCredElement cred;
  
  private boolean isInitiator;
  
  private boolean isEstablished;
  
  private Oid actualMech;
  
  private ChannelBinding cb;
  
  private GSSCredElement delegatedCred;
  
  private int flags;
  
  private int lifetime = 0;
  
  private final GSSLibStub cStub;
  
  private boolean skipDelegPermCheck;
  
  private boolean skipServicePermCheck;
  
  private static Oid getMechFromSpNegoToken(byte[] paramArrayOfByte, boolean paramBoolean) throws GSSException {
    Oid oid = null;
    if (paramBoolean) {
      GSSHeader gSSHeader = null;
      try {
        gSSHeader = new GSSHeader(new ByteArrayInputStream(paramArrayOfByte));
      } catch (IOException iOException) {
        throw new GSSExceptionImpl(11, iOException);
      } 
      int i = gSSHeader.getMechTokenLength();
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(paramArrayOfByte, paramArrayOfByte.length - i, arrayOfByte, 0, arrayOfByte.length);
      NegTokenInit negTokenInit = new NegTokenInit(arrayOfByte);
      if (negTokenInit.getMechToken() != null) {
        Oid[] arrayOfOid = negTokenInit.getMechTypeList();
        oid = arrayOfOid[0];
      } 
    } else {
      NegTokenTarg negTokenTarg = new NegTokenTarg(paramArrayOfByte);
      oid = negTokenTarg.getSupportedMech();
    } 
    return oid;
  }
  
  private void doServicePermCheck() throws GSSException {
    if (System.getSecurityManager() != null) {
      String str1 = this.isInitiator ? "initiate" : "accept";
      if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && this.isInitiator && !this.isEstablished)
        if (this.srcName == null) {
          GSSCredElement gSSCredElement = new GSSCredElement(null, this.lifetime, 1, GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID));
          gSSCredElement.dispose();
        } else {
          String str = Krb5Util.getTGSName(this.srcName);
          Krb5Util.checkServicePermission(str, str1);
        }  
      String str2 = this.targetName.getKrbName();
      Krb5Util.checkServicePermission(str2, str1);
      this.skipServicePermCheck = true;
    } 
  }
  
  private void doDelegPermCheck() throws GSSException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      String str1 = this.targetName.getKrbName();
      String str2 = Krb5Util.getTGSName(this.targetName);
      StringBuffer stringBuffer = new StringBuffer("\"");
      stringBuffer.append(str1).append("\" \"");
      stringBuffer.append(str2).append('"');
      String str3 = stringBuffer.toString();
      SunNativeProvider.debug("Checking DelegationPermission (" + str3 + ")");
      DelegationPermission delegationPermission = new DelegationPermission(str3);
      securityManager.checkPermission(delegationPermission);
      this.skipDelegPermCheck = true;
    } 
  }
  
  private byte[] retrieveToken(InputStream paramInputStream, int paramInt) throws GSSException {
    try {
      byte[] arrayOfByte = null;
      if (paramInt != -1) {
        SunNativeProvider.debug("Precomputed mechToken length: " + paramInt);
        GSSHeader gSSHeader = new GSSHeader(new ObjectIdentifier(this.cStub.getMech().toString()), paramInt);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(600);
        byte[] arrayOfByte1 = new byte[paramInt];
        int i = paramInputStream.read(arrayOfByte1);
        assert paramInt == i;
        gSSHeader.encode(byteArrayOutputStream);
        byteArrayOutputStream.write(arrayOfByte1);
        arrayOfByte = byteArrayOutputStream.toByteArray();
      } else {
        assert paramInt == -1;
        DerValue derValue = new DerValue(paramInputStream);
        arrayOfByte = derValue.toByteArray();
      } 
      SunNativeProvider.debug("Complete Token length: " + arrayOfByte.length);
      return arrayOfByte;
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
  }
  
  NativeGSSContext(GSSNameElement paramGSSNameElement, GSSCredElement paramGSSCredElement, int paramInt, GSSLibStub paramGSSLibStub) throws GSSException {
    if (paramGSSNameElement == null)
      throw new GSSException(11, 1, "null peer"); 
    this.cStub = paramGSSLibStub;
    this.cred = paramGSSCredElement;
    this.targetName = paramGSSNameElement;
    this.isInitiator = true;
    this.lifetime = paramInt;
    if (GSSUtil.isKerberosMech(this.cStub.getMech())) {
      doServicePermCheck();
      if (this.cred == null)
        this.cred = new GSSCredElement(null, this.lifetime, 1, this.cStub); 
      this.srcName = this.cred.getName();
    } 
  }
  
  NativeGSSContext(GSSCredElement paramGSSCredElement, GSSLibStub paramGSSLibStub) throws GSSException {
    this.cStub = paramGSSLibStub;
    this.cred = paramGSSCredElement;
    if (this.cred != null)
      this.targetName = this.cred.getName(); 
    this.isInitiator = false;
    if (GSSUtil.isKerberosMech(this.cStub.getMech()) && this.targetName != null)
      doServicePermCheck(); 
  }
  
  NativeGSSContext(long paramLong, GSSLibStub paramGSSLibStub) throws GSSException {
    assert this.pContext != 0L;
    this.pContext = paramLong;
    this.cStub = paramGSSLibStub;
    long[] arrayOfLong = this.cStub.inquireContext(this.pContext);
    if (arrayOfLong.length != 6)
      throw new RuntimeException("Bug w/ GSSLibStub.inquireContext()"); 
    this.srcName = new GSSNameElement(arrayOfLong[0], this.cStub);
    this.targetName = new GSSNameElement(arrayOfLong[1], this.cStub);
    this.isInitiator = (arrayOfLong[2] != 0L);
    this.isEstablished = (arrayOfLong[3] != 0L);
    this.flags = (int)arrayOfLong[4];
    this.lifetime = (int)arrayOfLong[5];
    Oid oid = this.cStub.getMech();
    if (GSSUtil.isSpNegoMech(oid) || GSSUtil.isKerberosMech(oid))
      doServicePermCheck(); 
  }
  
  public Provider getProvider() { return SunNativeProvider.INSTANCE; }
  
  public byte[] initSecContext(InputStream paramInputStream, int paramInt) throws GSSException {
    byte[] arrayOfByte = null;
    if (!this.isEstablished && this.isInitiator) {
      byte[] arrayOfByte1 = null;
      if (this.pContext != 0L) {
        arrayOfByte1 = retrieveToken(paramInputStream, paramInt);
        SunNativeProvider.debug("initSecContext=> inToken len=" + arrayOfByte1.length);
      } 
      if (!getCredDelegState())
        this.skipDelegPermCheck = true; 
      if (GSSUtil.isKerberosMech(this.cStub.getMech()) && !this.skipDelegPermCheck)
        doDelegPermCheck(); 
      long l = (this.cred == null) ? 0L : this.cred.pCred;
      arrayOfByte = this.cStub.initContext(l, this.targetName.pName, this.cb, arrayOfByte1, this);
      SunNativeProvider.debug("initSecContext=> outToken len=" + ((arrayOfByte == null) ? 0 : arrayOfByte.length));
      if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && arrayOfByte != null) {
        this.actualMech = getMechFromSpNegoToken(arrayOfByte, true);
        if (GSSUtil.isKerberosMech(this.actualMech)) {
          if (!this.skipServicePermCheck)
            doServicePermCheck(); 
          if (!this.skipDelegPermCheck)
            doDelegPermCheck(); 
        } 
      } 
      if (this.isEstablished) {
        if (this.srcName == null)
          this.srcName = new GSSNameElement(this.cStub.getContextName(this.pContext, true), this.cStub); 
        if (this.cred == null)
          this.cred = new GSSCredElement(this.srcName, this.lifetime, 1, this.cStub); 
      } 
    } 
    return arrayOfByte;
  }
  
  public byte[] acceptSecContext(InputStream paramInputStream, int paramInt) throws GSSException {
    byte[] arrayOfByte = null;
    if (!this.isEstablished && !this.isInitiator) {
      byte[] arrayOfByte1 = retrieveToken(paramInputStream, paramInt);
      SunNativeProvider.debug("acceptSecContext=> inToken len=" + arrayOfByte1.length);
      long l = (this.cred == null) ? 0L : this.cred.pCred;
      arrayOfByte = this.cStub.acceptContext(l, this.cb, arrayOfByte1, this);
      SunNativeProvider.debug("acceptSecContext=> outToken len=" + ((arrayOfByte == null) ? 0 : arrayOfByte.length));
      if (this.targetName == null) {
        this.targetName = new GSSNameElement(this.cStub.getContextName(this.pContext, false), this.cStub);
        if (this.cred != null)
          this.cred.dispose(); 
        this.cred = new GSSCredElement(this.targetName, this.lifetime, 2, this.cStub);
      } 
      if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && arrayOfByte != null && !this.skipServicePermCheck && GSSUtil.isKerberosMech(getMechFromSpNegoToken(arrayOfByte, false)))
        doServicePermCheck(); 
    } 
    return arrayOfByte;
  }
  
  public boolean isEstablished() { return this.isEstablished; }
  
  public void dispose() throws GSSException {
    this.srcName = null;
    this.targetName = null;
    this.cred = null;
    this.delegatedCred = null;
    if (this.pContext != 0L) {
      this.pContext = this.cStub.deleteContext(this.pContext);
      this.pContext = 0L;
    } 
  }
  
  public int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2) throws GSSException { return this.cStub.wrapSizeLimit(this.pContext, paramBoolean ? 1 : 0, paramInt1, paramInt2); }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramInt1 != 0 || paramInt2 != paramArrayOfByte.length) {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    } 
    return this.cStub.wrap(this.pContext, arrayOfByte, paramMessageProp);
  }
  
  public void wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    try {
      byte[] arrayOfByte = wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
      paramOutputStream.write(arrayOfByte);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
  }
  
  public int wrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte = wrap(paramArrayOfByte1, paramInt1, paramInt2, paramMessageProp);
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte2, paramInt3, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  public void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    try {
      byte[] arrayOfByte1 = new byte[paramInputStream.available()];
      int i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = wrap(arrayOfByte1, 0, i, paramMessageProp);
      paramOutputStream.write(arrayOfByte2);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    if (paramInt1 != 0 || paramInt2 != paramArrayOfByte.length) {
      byte[] arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
      return this.cStub.unwrap(this.pContext, arrayOfByte, paramMessageProp);
    } 
    return this.cStub.unwrap(this.pContext, paramArrayOfByte, paramMessageProp);
  }
  
  public int unwrap(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte = null;
    if (paramInt1 != 0 || paramInt2 != paramArrayOfByte1.length) {
      byte[] arrayOfByte1 = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte1, paramInt1, arrayOfByte1, 0, paramInt2);
      arrayOfByte = this.cStub.unwrap(this.pContext, arrayOfByte1, paramMessageProp);
    } else {
      arrayOfByte = this.cStub.unwrap(this.pContext, paramArrayOfByte1, paramMessageProp);
    } 
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte2, paramInt3, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  public void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    try {
      byte[] arrayOfByte1 = new byte[paramInputStream.available()];
      int i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = unwrap(arrayOfByte1, 0, i, paramMessageProp);
      paramOutputStream.write(arrayOfByte2);
      paramOutputStream.flush();
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
  }
  
  public int unwrap(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte1 = null;
    int i = 0;
    try {
      arrayOfByte1 = new byte[paramInputStream.available()];
      i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte = unwrap(arrayOfByte1, 0, i, paramMessageProp);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
    byte[] arrayOfByte2 = unwrap(arrayOfByte1, 0, i, paramMessageProp);
    System.arraycopy(arrayOfByte2, 0, paramArrayOfByte, paramInt, arrayOfByte2.length);
    return arrayOfByte2.length;
  }
  
  public byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    byte b = (paramMessageProp == null) ? 0 : paramMessageProp.getQOP();
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramInt1 != 0 || paramInt2 != paramArrayOfByte.length) {
      arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    } 
    return this.cStub.getMic(this.pContext, b, arrayOfByte);
  }
  
  public void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp) throws GSSException {
    try {
      int i = 0;
      byte[] arrayOfByte1 = new byte[paramInputStream.available()];
      i = paramInputStream.read(arrayOfByte1);
      byte[] arrayOfByte2 = getMIC(arrayOfByte1, 0, i, paramMessageProp);
      if (arrayOfByte2 != null && arrayOfByte2.length != 0)
        paramOutputStream.write(arrayOfByte2); 
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
  }
  
  public void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp) throws GSSException {
    byte[] arrayOfByte1 = paramArrayOfByte1;
    byte[] arrayOfByte2 = paramArrayOfByte2;
    if (paramInt1 != 0 || paramInt2 != paramArrayOfByte1.length) {
      arrayOfByte1 = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte1, paramInt1, arrayOfByte1, 0, paramInt2);
    } 
    if (paramInt3 != 0 || paramInt4 != paramArrayOfByte2.length) {
      arrayOfByte2 = new byte[paramInt4];
      System.arraycopy(paramArrayOfByte2, paramInt3, arrayOfByte2, 0, paramInt4);
    } 
    this.cStub.verifyMic(this.pContext, arrayOfByte1, arrayOfByte2, paramMessageProp);
  }
  
  public void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp) throws GSSException {
    try {
      byte[] arrayOfByte1 = new byte[paramInputStream2.available()];
      int i = paramInputStream2.read(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[paramInputStream1.available()];
      int j = paramInputStream1.read(arrayOfByte2);
      verifyMIC(arrayOfByte2, 0, j, arrayOfByte1, 0, i, paramMessageProp);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, iOException);
    } 
  }
  
  public byte[] export() throws GSSException {
    byte[] arrayOfByte = this.cStub.exportContext(this.pContext);
    this.pContext = 0L;
    return arrayOfByte;
  }
  
  private void changeFlags(int paramInt, boolean paramBoolean) {
    if (this.isInitiator && this.pContext == 0L)
      if (paramBoolean) {
        this.flags |= paramInt;
      } else {
        this.flags &= (paramInt ^ 0xFFFFFFFF);
      }  
  }
  
  public void requestMutualAuth(boolean paramBoolean) throws GSSException { changeFlags(2, paramBoolean); }
  
  public void requestReplayDet(boolean paramBoolean) throws GSSException { changeFlags(4, paramBoolean); }
  
  public void requestSequenceDet(boolean paramBoolean) throws GSSException { changeFlags(8, paramBoolean); }
  
  public void requestCredDeleg(boolean paramBoolean) throws GSSException { changeFlags(1, paramBoolean); }
  
  public void requestAnonymity(boolean paramBoolean) throws GSSException { changeFlags(64, paramBoolean); }
  
  public void requestConf(boolean paramBoolean) throws GSSException { changeFlags(16, paramBoolean); }
  
  public void requestInteg(boolean paramBoolean) throws GSSException { changeFlags(32, paramBoolean); }
  
  public void requestDelegPolicy(boolean paramBoolean) throws GSSException {}
  
  public void requestLifetime(int paramInt) throws GSSException {
    if (this.isInitiator && this.pContext == 0L)
      this.lifetime = paramInt; 
  }
  
  public void setChannelBinding(ChannelBinding paramChannelBinding) throws GSSException {
    if (this.pContext == 0L)
      this.cb = paramChannelBinding; 
  }
  
  private boolean checkFlags(int paramInt) { return ((this.flags & paramInt) != 0); }
  
  public boolean getCredDelegState() { return checkFlags(1); }
  
  public boolean getMutualAuthState() { return checkFlags(2); }
  
  public boolean getReplayDetState() { return checkFlags(4); }
  
  public boolean getSequenceDetState() { return checkFlags(8); }
  
  public boolean getAnonymityState() { return checkFlags(64); }
  
  public boolean isTransferable() { return checkFlags(256); }
  
  public boolean isProtReady() { return checkFlags(128); }
  
  public boolean getConfState() { return checkFlags(16); }
  
  public boolean getIntegState() { return checkFlags(32); }
  
  public boolean getDelegPolicyState() { return false; }
  
  public int getLifetime() { return this.cStub.getContextTime(this.pContext); }
  
  public GSSNameSpi getSrcName() throws GSSException { return this.srcName; }
  
  public GSSNameSpi getTargName() throws GSSException { return this.targetName; }
  
  public Oid getMech() throws GSSException { return (this.isEstablished && this.actualMech != null) ? this.actualMech : this.cStub.getMech(); }
  
  public GSSCredentialSpi getDelegCred() throws GSSException { return this.delegatedCred; }
  
  public boolean isInitiator() { return this.isInitiator; }
  
  protected void finalize() throws GSSException { dispose(); }
  
  public Object inquireSecContext(InquireType paramInquireType) throws GSSException { throw new GSSException(16, -1, "Inquire type not supported."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\NativeGSSContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */