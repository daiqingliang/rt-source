package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class PlainClient implements SaslClient {
  private boolean completed = false;
  
  private byte[] pw;
  
  private String authorizationID;
  
  private String authenticationID;
  
  private static byte SEP = 0;
  
  PlainClient(String paramString1, String paramString2, byte[] paramArrayOfByte) throws SaslException {
    if (paramString2 == null || paramArrayOfByte == null)
      throw new SaslException("PLAIN: authorization ID and password must be specified"); 
    this.authorizationID = paramString1;
    this.authenticationID = paramString2;
    this.pw = paramArrayOfByte;
  }
  
  public String getMechanismName() { return "PLAIN"; }
  
  public boolean hasInitialResponse() { return true; }
  
  public void dispose() throws SaslException { clearPassword(); }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("PLAIN authentication already completed"); 
    this.completed = true;
    try {
      byte[] arrayOfByte1 = (this.authorizationID != null) ? this.authorizationID.getBytes("UTF8") : null;
      byte[] arrayOfByte2 = this.authenticationID.getBytes("UTF8");
      byte[] arrayOfByte3 = new byte[this.pw.length + arrayOfByte2.length + 2 + ((arrayOfByte1 == null) ? 0 : arrayOfByte1.length)];
      int i = 0;
      if (arrayOfByte1 != null) {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
        i = arrayOfByte1.length;
      } 
      arrayOfByte3[i++] = SEP;
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, i, arrayOfByte2.length);
      i += arrayOfByte2.length;
      arrayOfByte3[i++] = SEP;
      System.arraycopy(this.pw, 0, arrayOfByte3, i, this.pw.length);
      clearPassword();
      return arrayOfByte3;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new SaslException("Cannot get UTF-8 encoding of ids", unsupportedEncodingException);
    } 
  }
  
  public boolean isComplete() { return this.completed; }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (this.completed)
      throw new SaslException("PLAIN supports neither integrity nor privacy"); 
    throw new IllegalStateException("PLAIN authentication not completed");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (this.completed)
      throw new SaslException("PLAIN supports neither integrity nor privacy"); 
    throw new IllegalStateException("PLAIN authentication not completed");
  }
  
  public Object getNegotiatedProperty(String paramString) {
    if (this.completed)
      return paramString.equals("javax.security.sasl.qop") ? "auth" : null; 
    throw new IllegalStateException("PLAIN authentication not completed");
  }
  
  private void clearPassword() throws SaslException {
    if (this.pw != null) {
      for (byte b = 0; b < this.pw.length; b++)
        this.pw[b] = 0; 
      this.pw = null;
    } 
  }
  
  protected void finalize() throws SaslException { clearPassword(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\PlainClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */