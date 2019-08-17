package java.net;

import java.util.List;

public interface CookieStore {
  void add(URI paramURI, HttpCookie paramHttpCookie);
  
  List<HttpCookie> get(URI paramURI);
  
  List<HttpCookie> getCookies();
  
  List<URI> getURIs();
  
  boolean remove(URI paramURI, HttpCookie paramHttpCookie);
  
  boolean removeAll();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\CookieStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */