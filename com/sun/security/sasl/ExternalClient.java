package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class ExternalClient implements SaslClient {
  private byte[] username;
  
  private boolean completed = false;
  
  ExternalClient(String paramString) throws SaslException {
    if (paramString != null) {
      try {
        this.username = paramString.getBytes("UTF8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new SaslException("Cannot convert " + paramString + " into UTF-8", unsupportedEncodingException);
      } 
    } else {
      this.username = new byte[0];
    } 
  }
  
  public String getMechanismName() { return "EXTERNAL"; }
  
  public boolean hasInitialResponse() { return true; }
  
  public void dispose() throws SaslException {}
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("EXTERNAL authentication already completed"); 
    this.completed = true;
    return this.username;
  }
  
  public boolean isComplete() { return this.completed; }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (this.completed)
      throw new SaslException("EXTERNAL has no supported QOP"); 
    throw new IllegalStateException("EXTERNAL authentication Not completed");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (this.completed)
      throw new SaslException("EXTERNAL has no supported QOP"); 
    throw new IllegalStateException("EXTERNAL authentication not completed");
  }
  
  public Object getNegotiatedProperty(String paramString) {
    if (this.completed)
      return null; 
    throw new IllegalStateException("EXTERNAL authentication not completed");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\ExternalClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */