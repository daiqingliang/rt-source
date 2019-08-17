package sun.net.www.protocol.http;

public interface AuthCache {
  void put(String paramString, AuthCacheValue paramAuthCacheValue);
  
  AuthCacheValue get(String paramString1, String paramString2);
  
  void remove(String paramString, AuthCacheValue paramAuthCacheValue);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\AuthCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */