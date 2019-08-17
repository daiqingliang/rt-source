package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class DatagramPacket {
  byte[] buf;
  
  int offset;
  
  int length;
  
  int bufLength;
  
  InetAddress address;
  
  int port;
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    setData(paramArrayOfByte, paramInt1, paramInt2);
    this.address = null;
    this.port = -1;
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt) { this(paramArrayOfByte, 0, paramInt); }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2, InetAddress paramInetAddress, int paramInt3) {
    setData(paramArrayOfByte, paramInt1, paramInt2);
    setAddress(paramInetAddress);
    setPort(paramInt3);
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2, SocketAddress paramSocketAddress) {
    setData(paramArrayOfByte, paramInt1, paramInt2);
    setSocketAddress(paramSocketAddress);
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, InetAddress paramInetAddress, int paramInt2) { this(paramArrayOfByte, 0, paramInt1, paramInetAddress, paramInt2); }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt, SocketAddress paramSocketAddress) { this(paramArrayOfByte, 0, paramInt, paramSocketAddress); }
  
  public InetAddress getAddress() { return this.address; }
  
  public int getPort() { return this.port; }
  
  public byte[] getData() { return this.buf; }
  
  public int getOffset() { return this.offset; }
  
  public int getLength() { return this.length; }
  
  public void setData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 < 0 || paramInt1 < 0 || paramInt2 + paramInt1 < 0 || paramInt2 + paramInt1 > paramArrayOfByte.length)
      throw new IllegalArgumentException("illegal length or offset"); 
    this.buf = paramArrayOfByte;
    this.length = paramInt2;
    this.bufLength = paramInt2;
    this.offset = paramInt1;
  }
  
  public void setAddress(InetAddress paramInetAddress) { this.address = paramInetAddress; }
  
  public void setPort(int paramInt) {
    if (paramInt < 0 || paramInt > 65535)
      throw new IllegalArgumentException("Port out of range:" + paramInt); 
    this.port = paramInt;
  }
  
  public void setSocketAddress(SocketAddress paramSocketAddress) {
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress.isUnresolved())
      throw new IllegalArgumentException("unresolved address"); 
    setAddress(inetSocketAddress.getAddress());
    setPort(inetSocketAddress.getPort());
  }
  
  public SocketAddress getSocketAddress() { return new InetSocketAddress(getAddress(), getPort()); }
  
  public void setData(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      throw new NullPointerException("null packet buffer"); 
    this.buf = paramArrayOfByte;
    this.offset = 0;
    this.length = paramArrayOfByte.length;
    this.bufLength = paramArrayOfByte.length;
  }
  
  public void setLength(int paramInt) {
    if (paramInt + this.offset > this.buf.length || paramInt < 0 || paramInt + this.offset < 0)
      throw new IllegalArgumentException("illegal length"); 
    this.length = paramInt;
    this.bufLength = this.length;
  }
  
  private static native void init();
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
    init();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\DatagramPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */