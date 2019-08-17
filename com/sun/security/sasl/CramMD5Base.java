package com.sun.security.sasl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;

abstract class CramMD5Base {
  protected boolean completed = false;
  
  protected boolean aborted = false;
  
  protected byte[] pw;
  
  private static final int MD5_BLOCKSIZE = 64;
  
  private static final String SASL_LOGGER_NAME = "javax.security.sasl";
  
  protected static Logger logger;
  
  protected CramMD5Base() { initLogger(); }
  
  public String getMechanismName() { return "CRAM-MD5"; }
  
  public boolean isComplete() { return this.completed; }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy"); 
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (this.completed)
      throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy"); 
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
  
  public Object getNegotiatedProperty(String paramString) {
    if (this.completed)
      return paramString.equals("javax.security.sasl.qop") ? "auth" : null; 
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
  
  public void dispose() { clearPassword(); }
  
  protected void clearPassword() {
    if (this.pw != null) {
      for (byte b = 0; b < this.pw.length; b++)
        this.pw[b] = 0; 
      this.pw = null;
    } 
  }
  
  protected void finalize() { clearPassword(); }
  
  static final String HMAC_MD5(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    if (paramArrayOfByte1.length > 64)
      paramArrayOfByte1 = messageDigest.digest(paramArrayOfByte1); 
    byte[] arrayOfByte1 = new byte[64];
    byte[] arrayOfByte2 = new byte[64];
    byte b;
    for (b = 0; b < paramArrayOfByte1.length; b++) {
      arrayOfByte1[b] = paramArrayOfByte1[b];
      arrayOfByte2[b] = paramArrayOfByte1[b];
    } 
    for (b = 0; b < 64; b++) {
      arrayOfByte1[b] = (byte)(arrayOfByte1[b] ^ 0x36);
      arrayOfByte2[b] = (byte)(arrayOfByte2[b] ^ 0x5C);
    } 
    messageDigest.update(arrayOfByte1);
    messageDigest.update(paramArrayOfByte2);
    byte[] arrayOfByte3 = messageDigest.digest();
    messageDigest.update(arrayOfByte2);
    messageDigest.update(arrayOfByte3);
    arrayOfByte3 = messageDigest.digest();
    StringBuffer stringBuffer = new StringBuffer();
    for (b = 0; b < arrayOfByte3.length; b++) {
      if ((arrayOfByte3[b] & 0xFF) < 16) {
        stringBuffer.append("0" + Integer.toHexString(arrayOfByte3[b] & 0xFF));
      } else {
        stringBuffer.append(Integer.toHexString(arrayOfByte3[b] & 0xFF));
      } 
    } 
    Arrays.fill(arrayOfByte1, (byte)0);
    Arrays.fill(arrayOfByte2, (byte)0);
    arrayOfByte1 = null;
    arrayOfByte2 = null;
    return stringBuffer.toString();
  }
  
  private static void initLogger() {
    if (logger == null)
      logger = Logger.getLogger("javax.security.sasl"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\CramMD5Base.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */