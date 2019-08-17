package java.net;

public interface CookiePolicy {
  public static final CookiePolicy ACCEPT_ALL = new CookiePolicy() {
      public boolean shouldAccept(URI param1URI, HttpCookie param1HttpCookie) { return true; }
    };
  
  public static final CookiePolicy ACCEPT_NONE = new CookiePolicy() {
      public boolean shouldAccept(URI param1URI, HttpCookie param1HttpCookie) { return false; }
    };
  
  public static final CookiePolicy ACCEPT_ORIGINAL_SERVER = new CookiePolicy() {
      public boolean shouldAccept(URI param1URI, HttpCookie param1HttpCookie) { return (param1URI == null || param1HttpCookie == null) ? false : HttpCookie.domainMatches(param1HttpCookie.getDomain(), param1URI.getHost()); }
    };
  
  boolean shouldAccept(URI paramURI, HttpCookie paramHttpCookie);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\CookiePolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */