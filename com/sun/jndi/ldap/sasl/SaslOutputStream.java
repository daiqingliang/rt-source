package com.sun.jndi.ldap.sasl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

class SaslOutputStream extends FilterOutputStream {
  private static final boolean debug = false;
  
  private byte[] lenBuf = new byte[4];
  
  private int rawSendSize = 65536;
  
  private SaslClient sc;
  
  SaslOutputStream(SaslClient paramSaslClient, OutputStream paramOutputStream) throws SaslException {
    super(paramOutputStream);
    this.sc = paramSaslClient;
    String str = (String)paramSaslClient.getNegotiatedProperty("javax.security.sasl.rawsendsize");
    if (str != null)
      try {
        this.rawSendSize = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        throw new SaslException("javax.security.sasl.rawsendsize property must be numeric string: " + str);
      }  
  }
  
  public void write(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = (byte)paramInt;
    write(arrayOfByte, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i;
    for (i = 0; i < paramInt2; i += this.rawSendSize) {
      int j = (paramInt2 - i < this.rawSendSize) ? (paramInt2 - i) : this.rawSendSize;
      byte[] arrayOfByte = this.sc.wrap(paramArrayOfByte, paramInt1 + i, j);
      intToNetworkByteOrder(arrayOfByte.length, this.lenBuf, 0, 4);
      this.out.write(this.lenBuf, 0, 4);
      this.out.write(arrayOfByte, 0, arrayOfByte.length);
    } 
  }
  
  public void close() throws IOException {
    SaslException saslException = null;
    try {
      this.sc.dispose();
    } catch (SaslException saslException1) {
      saslException = saslException1;
    } 
    super.close();
    if (saslException != null)
      throw saslException; 
  }
  
  private static void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    if (paramInt3 > 4)
      throw new IllegalArgumentException("Cannot handle more than 4 bytes"); 
    for (int i = paramInt3 - 1; i >= 0; i--) {
      paramArrayOfByte[paramInt2 + i] = (byte)(paramInt1 & 0xFF);
      paramInt1 >>>= 8;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\sasl\SaslOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */