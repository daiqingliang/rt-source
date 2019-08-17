package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignerOutputStream extends ByteArrayOutputStream {
  private static Logger log = Logger.getLogger(SignerOutputStream.class.getName());
  
  final SignatureAlgorithm sa;
  
  public SignerOutputStream(SignatureAlgorithm paramSignatureAlgorithm) { this.sa = paramSignatureAlgorithm; }
  
  public void write(byte[] paramArrayOfByte) {
    try {
      this.sa.update(paramArrayOfByte);
    } catch (XMLSignatureException xMLSignatureException) {
      throw new RuntimeException("" + xMLSignatureException);
    } 
  }
  
  public void write(int paramInt) {
    try {
      this.sa.update((byte)paramInt);
    } catch (XMLSignatureException xMLSignatureException) {
      throw new RuntimeException("" + xMLSignatureException);
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Canonicalized SignedInfo:");
      StringBuilder stringBuilder = new StringBuilder(paramInt2);
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
        stringBuilder.append((char)paramArrayOfByte[i]); 
      log.log(Level.FINE, stringBuilder.toString());
    } 
    try {
      this.sa.update(paramArrayOfByte, paramInt1, paramInt2);
    } catch (XMLSignatureException xMLSignatureException) {
      throw new RuntimeException("" + xMLSignatureException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\SignerOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */