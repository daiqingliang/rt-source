package com.sun.jndi.ldap.sasl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class SaslInputStream extends InputStream {
  private static final boolean debug = false;
  
  private byte[] saslBuffer;
  
  private byte[] lenBuf = new byte[4];
  
  private byte[] buf = new byte[0];
  
  private int bufPos = 0;
  
  private InputStream in;
  
  private SaslClient sc;
  
  private int recvMaxBufSize = 65536;
  
  SaslInputStream(SaslClient paramSaslClient, InputStream paramInputStream) throws SaslException {
    this.in = paramInputStream;
    this.sc = paramSaslClient;
    String str = (String)paramSaslClient.getNegotiatedProperty("javax.security.sasl.maxbuffer");
    if (str != null)
      try {
        this.recvMaxBufSize = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        throw new SaslException("javax.security.sasl.maxbuffer property must be numeric string: " + str);
      }  
    this.saslBuffer = new byte[this.recvMaxBufSize];
  }
  
  public int read() throws IOException {
    byte[] arrayOfByte = new byte[1];
    int i = read(arrayOfByte, 0, 1);
    return (i > 0) ? arrayOfByte[0] : -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.bufPos >= this.buf.length) {
      int j;
      for (j = fill(); j == 0; j = fill());
      if (j == -1)
        return -1; 
    } 
    int i = this.buf.length - this.bufPos;
    if (paramInt2 > i) {
      System.arraycopy(this.buf, this.bufPos, paramArrayOfByte, paramInt1, i);
      this.bufPos = this.buf.length;
      return i;
    } 
    System.arraycopy(this.buf, this.bufPos, paramArrayOfByte, paramInt1, paramInt2);
    this.bufPos += paramInt2;
    return paramInt2;
  }
  
  private int fill() throws IOException {
    int i = readFully(this.lenBuf, 4);
    if (i != 4)
      return -1; 
    int j = networkByteOrderToInt(this.lenBuf, 0, 4);
    if (j > this.recvMaxBufSize)
      throw new IOException(j + "exceeds the negotiated receive buffer size limit:" + this.recvMaxBufSize); 
    i = readFully(this.saslBuffer, j);
    if (i != j)
      throw new EOFException("Expecting to read " + j + " bytes but got " + i + " bytes before EOF"); 
    this.buf = this.sc.unwrap(this.saslBuffer, 0, j);
    this.bufPos = 0;
    return this.buf.length;
  }
  
  private int readFully(byte[] paramArrayOfByte, int paramInt) throws IOException {
    int i = 0;
    while (paramInt > 0) {
      int j = this.in.read(paramArrayOfByte, i, paramInt);
      if (j == -1)
        return (i == 0) ? -1 : i; 
      i += j;
      paramInt -= j;
    } 
    return i;
  }
  
  public int available() throws IOException { return this.buf.length - this.bufPos; }
  
  public void close() throws IOException {
    SaslException saslException = null;
    try {
      this.sc.dispose();
    } catch (SaslException saslException1) {
      saslException = saslException1;
    } 
    this.in.close();
    if (saslException != null)
      throw saslException; 
  }
  
  private static int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 > 4)
      throw new IllegalArgumentException("Cannot handle more than 4 bytes"); 
    byte b = 0;
    for (int i = 0; i < paramInt2; i++) {
      b <<= 8;
      b |= paramArrayOfByte[paramInt1 + i] & 0xFF;
    } 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\sasl\SaslInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */