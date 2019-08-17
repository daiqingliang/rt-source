package com.sun.security.sasl.gsskerb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;

final class GssKrb5Server extends GssKrb5Base implements SaslServer {
  private static final String MY_CLASS_NAME = GssKrb5Server.class.getName();
  
  private int handshakeStage = 0;
  
  private String peer;
  
  private String me;
  
  private String authzid;
  
  private CallbackHandler cbh;
  
  private final String protocolSaved;
  
  GssKrb5Server(String paramString1, String paramString2, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    super(paramMap, MY_CLASS_NAME);
    this.cbh = paramCallbackHandler;
    if (paramString2 == null) {
      this.protocolSaved = paramString1;
      str = null;
    } else {
      this.protocolSaved = null;
      str = paramString1 + "@" + paramString2;
    } 
    logger.log(Level.FINE, "KRB5SRV01:Using service name: {0}", str);
    try {
      GSSManager gSSManager = GSSManager.getInstance();
      GSSName gSSName = (str == null) ? null : gSSManager.createName(str, GSSName.NT_HOSTBASED_SERVICE, KRB5_OID);
      GSSCredential gSSCredential = gSSManager.createCredential(gSSName, 2147483647, KRB5_OID, 2);
      this.secCtx = gSSManager.createContext(gSSCredential);
      if ((this.allQop & 0x2) != 0)
        this.secCtx.requestInteg(true); 
      if ((this.allQop & 0x4) != 0)
        this.secCtx.requestConf(true); 
    } catch (GSSException gSSException) {
      throw new SaslException("Failure to initialize security context", gSSException);
    } 
    logger.log(Level.FINE, "KRB5SRV02:Initialization complete");
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte) throws SaslException {
    if (this.completed)
      throw new SaslException("SASL authentication already complete"); 
    if (logger.isLoggable(Level.FINER))
      traceOutput(MY_CLASS_NAME, "evaluateResponse", "KRB5SRV03:Response [raw]:", paramArrayOfByte); 
    switch (this.handshakeStage) {
      case 1:
        return doHandshake1(paramArrayOfByte);
      case 2:
        return doHandshake2(paramArrayOfByte);
    } 
    try {
      byte[] arrayOfByte = this.secCtx.acceptSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "evaluateResponse", "KRB5SRV04:Challenge: [after acceptSecCtx]", arrayOfByte); 
      if (this.secCtx.isEstablished()) {
        this.handshakeStage = 1;
        this.peer = this.secCtx.getSrcName().toString();
        this.me = this.secCtx.getTargName().toString();
        logger.log(Level.FINE, "KRB5SRV05:Peer name is : {0}, my name is : {1}", new Object[] { this.peer, this.me });
        if (this.protocolSaved != null && !this.protocolSaved.equalsIgnoreCase(this.me.split("[/@]")[0]))
          throw new SaslException("GSS context targ name protocol error: " + this.me); 
        if (arrayOfByte == null)
          return doHandshake1(EMPTY); 
      } 
      return arrayOfByte;
    } catch (GSSException gSSException) {
      throw new SaslException("GSS initiate failed", gSSException);
    } 
  }
  
  private byte[] doHandshake1(byte[] paramArrayOfByte) throws SaslException {
    try {
      if (paramArrayOfByte != null && paramArrayOfByte.length > 0)
        throw new SaslException("Handshake expecting no response data from server"); 
      byte[] arrayOfByte1 = new byte[4];
      arrayOfByte1[0] = this.allQop;
      intToNetworkByteOrder(this.recvMaxBufSize, arrayOfByte1, 1, 3);
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "KRB5SRV06:Supported protections: {0}; recv max buf size: {1}", new Object[] { new Byte(this.allQop), new Integer(this.recvMaxBufSize) }); 
      this.handshakeStage = 2;
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "doHandshake1", "KRB5SRV07:Challenge [raw]", arrayOfByte1); 
      byte[] arrayOfByte2 = this.secCtx.wrap(arrayOfByte1, 0, arrayOfByte1.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "doHandshake1", "KRB5SRV08:Challenge [after wrap]", arrayOfByte2); 
      return arrayOfByte2;
    } catch (GSSException gSSException) {
      throw new SaslException("Problem wrapping handshake1", gSSException);
    } 
  }
  
  private byte[] doHandshake2(byte[] paramArrayOfByte) throws SaslException {
    try {
      byte[] arrayOfByte = this.secCtx.unwrap(paramArrayOfByte, 0, paramArrayOfByte.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINER))
        traceOutput(MY_CLASS_NAME, "doHandshake2", "KRB5SRV09:Response [after unwrap]", arrayOfByte); 
      byte b = arrayOfByte[0];
      if ((b & this.allQop) == 0)
        throw new SaslException("Client selected unsupported protection: " + b); 
      if ((b & 0x4) != 0) {
        this.privacy = true;
        this.integrity = true;
      } else if ((b & 0x2) != 0) {
        this.integrity = true;
      } 
      int i = networkByteOrderToInt(arrayOfByte, 1, 3);
      this.sendMaxBufSize = (this.sendMaxBufSize == 0) ? i : Math.min(this.sendMaxBufSize, i);
      this.rawSendSize = this.secCtx.getWrapSizeLimit(0, this.privacy, this.sendMaxBufSize);
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "KRB5SRV10:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[] { new Byte(b), Boolean.valueOf(this.privacy), Boolean.valueOf(this.integrity) });
        logger.log(Level.FINE, "KRB5SRV11:Client max recv size: {0}; server max send size: {1}; rawSendSize: {2}", new Object[] { new Integer(i), new Integer(this.sendMaxBufSize), new Integer(this.rawSendSize) });
      } 
      if (arrayOfByte.length > 4) {
        try {
          this.authzid = new String(arrayOfByte, 4, arrayOfByte.length - 4, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new SaslException("Cannot decode authzid", unsupportedEncodingException);
        } 
      } else {
        this.authzid = this.peer;
      } 
      logger.log(Level.FINE, "KRB5SRV12:Authzid: {0}", this.authzid);
      AuthorizeCallback authorizeCallback = new AuthorizeCallback(this.peer, this.authzid);
      this.cbh.handle(new Callback[] { authorizeCallback });
      if (authorizeCallback.isAuthorized()) {
        this.authzid = authorizeCallback.getAuthorizedID();
        this.completed = true;
      } else {
        throw new SaslException(this.peer + " is not authorized to connect as " + this.authzid);
      } 
      return null;
    } catch (GSSException gSSException) {
      throw new SaslException("Final handshake step failed", gSSException);
    } catch (IOException iOException) {
      throw new SaslException("Problem with callback handler", iOException);
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new SaslException("Problem with callback handler", unsupportedCallbackException);
    } 
  }
  
  public String getAuthorizationID() {
    if (this.completed)
      return this.authzid; 
    throw new IllegalStateException("Authentication incomplete");
  }
  
  public Object getNegotiatedProperty(String paramString) {
    if (!this.completed)
      throw new IllegalStateException("Authentication incomplete"); 
    switch (paramString) {
      case "javax.security.sasl.bound.server.name":
        try {
          null = this.me.split("[/@]")[1];
        } catch (Exception exception) {
          null = null;
        } 
        return null;
    } 
    return super.getNegotiatedProperty(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\gsskerb\GssKrb5Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */