package javax.net.ssl;

import java.util.Enumeration;

public interface SSLSessionContext {
  SSLSession getSession(byte[] paramArrayOfByte);
  
  Enumeration<byte[]> getIds();
  
  void setSessionTimeout(int paramInt) throws IllegalArgumentException;
  
  int getSessionTimeout();
  
  void setSessionCacheSize(int paramInt) throws IllegalArgumentException;
  
  int getSessionCacheSize();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLSessionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */