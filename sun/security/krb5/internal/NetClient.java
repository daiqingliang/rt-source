package sun.security.krb5.internal;

import java.io.IOException;

public abstract class NetClient implements AutoCloseable {
  public static NetClient getInstance(String paramString1, String paramString2, int paramInt1, int paramInt2) throws IOException { return paramString1.equals("TCP") ? new TCPClient(paramString2, paramInt1, paramInt2) : new UDPClient(paramString2, paramInt1, paramInt2); }
  
  public abstract void send(byte[] paramArrayOfByte) throws IOException;
  
  public abstract byte[] receive() throws IOException;
  
  public abstract void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\NetClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */