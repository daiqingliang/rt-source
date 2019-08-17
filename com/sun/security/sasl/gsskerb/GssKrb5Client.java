package com.sun.security.sasl.gsskerb;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;

final class GssKrb5Client extends GssKrb5Base implements SaslClient {
  private static final String MY_CLASS_NAME = GssKrb5Client.class.getName();
  
  private boolean finalHandshake = false;
  
  private boolean mutual = false;
  
  private byte[] authzID;
  
  GssKrb5Client(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    super(paramMap, MY_CLASS_NAME);
    String str = paramString2 + "@" + paramString3;
    logger.log(Level.FINE, "KRB5CLNT01:Requesting service name: {0}", str);
    try {
      GSSManager gSSManager = GSSManager.getInstance();
      GSSName gSSName = gSSManager.createName(str, GSSName.NT_HOSTBASED_SERVICE, KRB5_OID);
      GSSCredential gSSCredential = null;
      if (paramMap != null) {
        Object object = paramMap.get("javax.security.sasl.credentials");
        if (object != null && object instanceof GSSCredential) {
          gSSCredential = (GSSCredential)object;
          logger.log(Level.FINE, "KRB5CLNT01:Using the credentials supplied in javax.security.sasl.credentials");
        } 
      } 
      this.secCtx = gSSManager.createContext(gSSName, KRB5_OID, gSSCredential, 2147483647);
      if (gSSCredential != null)
        this.secCtx.requestCredDeleg(true); 
      if (paramMap != null) {
        String str1 = (String)paramMap.get("javax.security.sasl.server.authentication");
        if (str1 != null)
          this.mutual = "true".equalsIgnoreCase(str1); 
      } 
      this.secCtx.requestMutualAuth(this.mutual);
      this.secCtx.requestConf(true);
      this.secCtx.requestInteg(true);
    } catch (GSSException gSSException) {
      throw new SaslException("Failure to initialize security context", gSSException);
    } 
    if (paramString1 != null && paramString1.length() > 0)
      try {
        this.authzID = paramString1.getBytes("UTF8");
      } catch (IOException iOException) {
        throw new SaslException("Cannot encode authorization ID", iOException);
      }  
  }
  
  public boolean hasInitialResponse() { return true; }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("GSSAPI authentication already complete"); 
    if (this.finalHandshake)
      return doFinalHandshake(paramArrayOfByte); 
    try {
      byte[] arrayOfByte = this.secCtx.initSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "evaluteChallenge", "KRB5CLNT02:Challenge: [raw]", paramArrayOfByte);
        traceOutput(MY_CLASS_NAME, "evaluateChallenge", "KRB5CLNT03:Response: [after initSecCtx]", arrayOfByte);
      } 
      if (this.secCtx.isEstablished()) {
        this.finalHandshake = true;
        if (arrayOfByte == null)
          return EMPTY; 
      } 
      return arrayOfByte;
    } catch (GSSException gSSException) {
      throw new SaslException("GSS initiate failed", gSSException);
    } 
  }
  
  private byte[] doFinalHandshake(byte[] paramArrayOfByte) throws SaslException {
    try {
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT04:Challenge [raw]:", paramArrayOfByte); 
      if (paramArrayOfByte.length == 0)
        return EMPTY; 
      byte[] arrayOfByte1 = this.secCtx.unwrap(paramArrayOfByte, 0, paramArrayOfByte.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINE)) {
        if (logger.isLoggable(Level.FINER))
          traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT05:Challenge [unwrapped]:", arrayOfByte1); 
        logger.log(Level.FINE, "KRB5CLNT06:Server protections: {0}", new Byte(arrayOfByte1[0]));
      } 
      byte b = findPreferredMask(arrayOfByte1[0], this.qop);
      if (b == 0)
        throw new SaslException("No common protection layer between client and server"); 
      if ((b & 0x4) != 0) {
        this.privacy = true;
        this.integrity = true;
      } else if ((b & 0x2) != 0) {
        this.integrity = true;
      } 
      int i = networkByteOrderToInt(arrayOfByte1, 1, 3);
      this.sendMaxBufSize = (this.sendMaxBufSize == 0) ? i : Math.min(this.sendMaxBufSize, i);
      this.rawSendSize = this.secCtx.getWrapSizeLimit(0, this.privacy, this.sendMaxBufSize);
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "KRB5CLNT07:Client max recv size: {0}; server max recv size: {1}; rawSendSize: {2}", new Object[] { new Integer(this.recvMaxBufSize), new Integer(i), new Integer(this.rawSendSize) }); 
      int j = 4;
      if (this.authzID != null)
        j += this.authzID.length; 
      byte[] arrayOfByte2 = new byte[j];
      arrayOfByte2[0] = b;
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "KRB5CLNT08:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[] { new Byte(b), Boolean.valueOf(this.privacy), Boolean.valueOf(this.integrity) }); 
      intToNetworkByteOrder(this.recvMaxBufSize, arrayOfByte2, 1, 3);
      if (this.authzID != null) {
        System.arraycopy(this.authzID, 0, arrayOfByte2, 4, this.authzID.length);
        logger.log(Level.FINE, "KRB5CLNT09:Authzid: {0}", this.authzID);
      } 
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT10:Response [raw]", arrayOfByte2); 
      arrayOfByte1 = this.secCtx.wrap(arrayOfByte2, 0, arrayOfByte2.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT11:Response [after wrap]", arrayOfByte1); 
      this.completed = true;
      return arrayOfByte1;
    } catch (GSSException gSSException) {
      throw new SaslException("Final handshake failed", gSSException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\gsskerb\GssKrb5Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */