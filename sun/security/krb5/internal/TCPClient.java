package sun.security.krb5.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import sun.misc.IOUtils;

class TCPClient extends NetClient {
  private Socket tcpSocket = new Socket();
  
  private BufferedOutputStream out;
  
  private BufferedInputStream in;
  
  TCPClient(String paramString, int paramInt1, int paramInt2) throws IOException {
    this.tcpSocket.connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
    this.out = new BufferedOutputStream(this.tcpSocket.getOutputStream());
    this.in = new BufferedInputStream(this.tcpSocket.getInputStream());
    this.tcpSocket.setSoTimeout(paramInt2);
  }
  
  public void send(byte[] paramArrayOfByte) throws IOException {
    byte[] arrayOfByte = new byte[4];
    intToNetworkByteOrder(paramArrayOfByte.length, arrayOfByte, 0, 4);
    this.out.write(arrayOfByte);
    this.out.write(paramArrayOfByte);
    this.out.flush();
  }
  
  public byte[] receive() throws IOException {
    byte[] arrayOfByte = new byte[4];
    int i = readFully(arrayOfByte, 4);
    if (i != 4) {
      if (Krb5.DEBUG)
        System.out.println(">>>DEBUG: TCPClient could not read length field"); 
      return null;
    } 
    int j = networkByteOrderToInt(arrayOfByte, 0, 4);
    if (Krb5.DEBUG)
      System.out.println(">>>DEBUG: TCPClient reading " + j + " bytes"); 
    if (j <= 0) {
      if (Krb5.DEBUG)
        System.out.println(">>>DEBUG: TCPClient zero or negative length field: " + j); 
      return null;
    } 
    try {
      return IOUtils.readFully(this.in, j, true);
    } catch (IOException iOException) {
      if (Krb5.DEBUG)
        System.out.println(">>>DEBUG: TCPClient could not read complete packet (" + j + "/" + i + ")"); 
      return null;
    } 
  }
  
  public void close() throws IOException { this.tcpSocket.close(); }
  
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
  
  private static int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 > 4)
      throw new IllegalArgumentException("Cannot handle more than 4 bytes"); 
    byte b = 0;
    for (int i = 0; i < paramInt2; i++) {
      b <<= 8;
      b |= paramArrayOfByte[paramInt1 + i] & 0xFF;
    } 
    return b;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\TCPClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */