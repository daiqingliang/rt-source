package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class CramMD5Client extends CramMD5Base implements SaslClient {
  private String username;
  
  CramMD5Client(String paramString, byte[] paramArrayOfByte) throws SaslException {
    if (paramString == null || paramArrayOfByte == null)
      throw new SaslException("CRAM-MD5: authentication ID and password must be specified"); 
    this.username = paramString;
    this.pw = paramArrayOfByte;
  }
  
  public boolean hasInitialResponse() { return false; }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("CRAM-MD5 authentication already completed"); 
    if (this.aborted)
      throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error"); 
    try {
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "CRAMCLNT01:Received challenge: {0}", new String(paramArrayOfByte, "UTF8")); 
      String str1 = HMAC_MD5(this.pw, paramArrayOfByte);
      clearPassword();
      String str2 = this.username + " " + str1;
      logger.log(Level.FINE, "CRAMCLNT02:Sending response: {0}", str2);
      this.completed = true;
      return str2.getBytes("UTF8");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      this.aborted = true;
      throw new SaslException("MD5 algorithm not available on platform", noSuchAlgorithmException);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      this.aborted = true;
      throw new SaslException("UTF8 not available on platform", unsupportedEncodingException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\CramMD5Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */