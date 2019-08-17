package com.sun.security.sasl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class CramMD5Server extends CramMD5Base implements SaslServer {
  private String fqdn;
  
  private byte[] challengeData = null;
  
  private String authzid;
  
  private CallbackHandler cbh;
  
  CramMD5Server(String paramString1, String paramString2, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    if (paramString2 == null)
      throw new SaslException("CRAM-MD5: fully qualified server name must be specified"); 
    this.fqdn = paramString2;
    this.cbh = paramCallbackHandler;
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("CRAM-MD5 authentication already completed"); 
    if (this.aborted)
      throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error"); 
    try {
      if (this.challengeData == null) {
        if (paramArrayOfByte.length != 0) {
          this.aborted = true;
          throw new SaslException("CRAM-MD5 does not expect any initial response");
        } 
        Random random = new Random();
        long l1 = random.nextLong();
        long l2 = System.currentTimeMillis();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('<');
        stringBuffer.append(l1);
        stringBuffer.append('.');
        stringBuffer.append(l2);
        stringBuffer.append('@');
        stringBuffer.append(this.fqdn);
        stringBuffer.append('>');
        String str = stringBuffer.toString();
        logger.log(Level.FINE, "CRAMSRV01:Generated challenge: {0}", str);
        this.challengeData = str.getBytes("UTF8");
        return (byte[])this.challengeData.clone();
      } 
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "CRAMSRV02:Received response: {0}", new String(paramArrayOfByte, "UTF8")); 
      int i = 0;
      for (byte b1 = 0; b1 < paramArrayOfByte.length; b1++) {
        if (paramArrayOfByte[b1] == 32) {
          i = b1;
          break;
        } 
      } 
      if (i == 0) {
        this.aborted = true;
        throw new SaslException("CRAM-MD5: Invalid response; space missing");
      } 
      String str1 = new String(paramArrayOfByte, 0, i, "UTF8");
      logger.log(Level.FINE, "CRAMSRV03:Extracted username: {0}", str1);
      NameCallback nameCallback = new NameCallback("CRAM-MD5 authentication ID: ", str1);
      PasswordCallback passwordCallback = new PasswordCallback("CRAM-MD5 password: ", false);
      this.cbh.handle(new Callback[] { nameCallback, passwordCallback });
      char[] arrayOfChar = passwordCallback.getPassword();
      if (arrayOfChar == null || arrayOfChar.length == 0) {
        this.aborted = true;
        throw new SaslException("CRAM-MD5: username not found: " + str1);
      } 
      passwordCallback.clearPassword();
      String str2 = new String(arrayOfChar);
      for (byte b2 = 0; b2 < arrayOfChar.length; b2++)
        arrayOfChar[b2] = Character.MIN_VALUE; 
      this.pw = str2.getBytes("UTF8");
      String str3 = HMAC_MD5(this.pw, this.challengeData);
      logger.log(Level.FINE, "CRAMSRV04:Expecting digest: {0}", str3);
      clearPassword();
      byte[] arrayOfByte = str3.getBytes("UTF8");
      int j = paramArrayOfByte.length - i - 1;
      if (arrayOfByte.length != j) {
        this.aborted = true;
        throw new SaslException("Invalid response");
      } 
      byte b3 = 0;
      for (int k = i + 1; k < paramArrayOfByte.length; k++) {
        if (arrayOfByte[b3++] != paramArrayOfByte[k]) {
          this.aborted = true;
          throw new SaslException("Invalid response");
        } 
      } 
      AuthorizeCallback authorizeCallback = new AuthorizeCallback(str1, str1);
      this.cbh.handle(new Callback[] { authorizeCallback });
      if (authorizeCallback.isAuthorized()) {
        this.authzid = authorizeCallback.getAuthorizedID();
      } else {
        this.aborted = true;
        throw new SaslException("CRAM-MD5: user not authorized: " + str1);
      } 
      logger.log(Level.FINE, "CRAMSRV05:Authorization id: {0}", this.authzid);
      this.completed = true;
      return null;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      this.aborted = true;
      throw new SaslException("UTF8 not available on platform", unsupportedEncodingException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      this.aborted = true;
      throw new SaslException("MD5 algorithm not available on platform", noSuchAlgorithmException);
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      this.aborted = true;
      throw new SaslException("CRAM-MD5 authentication failed", unsupportedCallbackException);
    } catch (SaslException saslException) {
      throw saslException;
    } catch (IOException iOException) {
      this.aborted = true;
      throw new SaslException("CRAM-MD5 authentication failed", iOException);
    } 
  }
  
  public String getAuthorizationID() {
    if (this.completed)
      return this.authzid; 
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\CramMD5Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */