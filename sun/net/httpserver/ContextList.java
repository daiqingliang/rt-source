package sun.net.httpserver;

import java.util.LinkedList;

class ContextList {
  static final int MAX_CONTEXTS = 50;
  
  LinkedList<HttpContextImpl> list = new LinkedList();
  
  public void add(HttpContextImpl paramHttpContextImpl) {
    assert paramHttpContextImpl.getPath() != null;
    this.list.add(paramHttpContextImpl);
  }
  
  public int size() { return this.list.size(); }
  
  HttpContextImpl findContext(String paramString1, String paramString2) { return findContext(paramString1, paramString2, false); }
  
  HttpContextImpl findContext(String paramString1, String paramString2, boolean paramBoolean) {
    paramString1 = paramString1.toLowerCase();
    String str = "";
    HttpContextImpl httpContextImpl = null;
    for (HttpContextImpl httpContextImpl1 : this.list) {
      if (!httpContextImpl1.getProtocol().equals(paramString1))
        continue; 
      String str1 = httpContextImpl1.getPath();
      if ((!paramBoolean || str1.equals(paramString2)) && (paramBoolean || paramString2.startsWith(str1)) && str1.length() > str.length()) {
        str = str1;
        httpContextImpl = httpContextImpl1;
      } 
    } 
    return httpContextImpl;
  }
  
  public void remove(String paramString1, String paramString2) throws IllegalArgumentException {
    HttpContextImpl httpContextImpl = findContext(paramString1, paramString2, true);
    if (httpContextImpl == null)
      throw new IllegalArgumentException("cannot remove element from list"); 
    this.list.remove(httpContextImpl);
  }
  
  public void remove(HttpContextImpl paramHttpContextImpl) {
    for (HttpContextImpl httpContextImpl : this.list) {
      if (httpContextImpl.equals(paramHttpContextImpl)) {
        this.list.remove(httpContextImpl);
        return;
      } 
    } 
    throw new IllegalArgumentException("no such context in list");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\ContextList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */