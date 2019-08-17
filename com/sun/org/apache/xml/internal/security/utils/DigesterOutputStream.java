package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigesterOutputStream extends ByteArrayOutputStream {
  private static final Logger log = Logger.getLogger(DigesterOutputStream.class.getName());
  
  final MessageDigestAlgorithm mda;
  
  public DigesterOutputStream(MessageDigestAlgorithm paramMessageDigestAlgorithm) { this.mda = paramMessageDigestAlgorithm; }
  
  public void write(byte[] paramArrayOfByte) { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(int paramInt) { this.mda.update((byte)paramInt); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Pre-digested input:");
      StringBuilder stringBuilder = new StringBuilder(paramInt2);
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
        stringBuilder.append((char)paramArrayOfByte[i]); 
      log.log(Level.FINE, stringBuilder.toString());
    } 
    this.mda.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] getDigestValue() { return this.mda.digest(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\DigesterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */