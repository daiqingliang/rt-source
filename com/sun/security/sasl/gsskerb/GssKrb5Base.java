package com.sun.security.sasl.gsskerb;

import com.sun.security.sasl.util.AbstractSaslImpl;
import java.util.Map;
import java.util.logging.Level;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

abstract class GssKrb5Base extends AbstractSaslImpl {
  private static final String KRB5_OID_STR = "1.2.840.113554.1.2.2";
  
  protected static Oid KRB5_OID;
  
  protected static final byte[] EMPTY = new byte[0];
  
  protected GSSContext secCtx = null;
  
  protected static final int JGSS_QOP = 0;
  
  protected GssKrb5Base(Map<String, ?> paramMap, String paramString) throws SaslException { super(paramMap, paramString); }
  
  public String getMechanismName() { return "GSSAPI"; }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (!this.completed)
      throw new IllegalStateException("GSSAPI authentication not completed"); 
    if (!this.integrity)
      throw new IllegalStateException("No security layer negotiated"); 
    try {
      MessageProp messageProp = new MessageProp(0, this.privacy);
      byte[] arrayOfByte = this.secCtx.unwrap(paramArrayOfByte, paramInt1, paramInt2, messageProp);
      if (logger.isLoggable(Level.FINEST)) {
        traceOutput(this.myClassName, "KRB501:Unwrap", "incoming: ", paramArrayOfByte, paramInt1, paramInt2);
        traceOutput(this.myClassName, "KRB502:Unwrap", "unwrapped: ", arrayOfByte, 0, arrayOfByte.length);
      } 
      return arrayOfByte;
    } catch (GSSException gSSException) {
      throw new SaslException("Problems unwrapping SASL buffer", gSSException);
    } 
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (!this.completed)
      throw new IllegalStateException("GSSAPI authentication not completed"); 
    if (!this.integrity)
      throw new IllegalStateException("No security layer negotiated"); 
    try {
      MessageProp messageProp = new MessageProp(0, this.privacy);
      byte[] arrayOfByte = this.secCtx.wrap(paramArrayOfByte, paramInt1, paramInt2, messageProp);
      if (logger.isLoggable(Level.FINEST)) {
        traceOutput(this.myClassName, "KRB503:Wrap", "outgoing: ", paramArrayOfByte, paramInt1, paramInt2);
        traceOutput(this.myClassName, "KRB504:Wrap", "wrapped: ", arrayOfByte, 0, arrayOfByte.length);
      } 
      return arrayOfByte;
    } catch (GSSException gSSException) {
      throw new SaslException("Problem performing GSS wrap", gSSException);
    } 
  }
  
  public void dispose() throws SaslException {
    if (this.secCtx != null) {
      try {
        this.secCtx.dispose();
      } catch (GSSException gSSException) {
        throw new SaslException("Problem disposing GSS context", gSSException);
      } 
      this.secCtx = null;
    } 
  }
  
  protected void finalize() throws SaslException { dispose(); }
  
  static  {
    try {
      KRB5_OID = new Oid("1.2.840.113554.1.2.2");
    } catch (GSSException gSSException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\gsskerb\GssKrb5Base.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */