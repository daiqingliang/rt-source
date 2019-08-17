package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.SignatureException;

public class SignerOutputStream extends ByteArrayOutputStream {
  private final Signature sig;
  
  public SignerOutputStream(Signature paramSignature) { this.sig = paramSignature; }
  
  public void write(int paramInt) {
    super.write(paramInt);
    try {
      this.sig.update((byte)paramInt);
    } catch (SignatureException signatureException) {
      throw new RuntimeException(signatureException);
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    try {
      this.sig.update(paramArrayOfByte, paramInt1, paramInt2);
    } catch (SignatureException signatureException) {
      throw new RuntimeException(signatureException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\SignerOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */