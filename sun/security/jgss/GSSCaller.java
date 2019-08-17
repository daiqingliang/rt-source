package sun.security.jgss;

public class GSSCaller {
  public static final GSSCaller CALLER_UNKNOWN = new GSSCaller("UNKNOWN");
  
  public static final GSSCaller CALLER_INITIATE = new GSSCaller("INITIATE");
  
  public static final GSSCaller CALLER_ACCEPT = new GSSCaller("ACCEPT");
  
  public static final GSSCaller CALLER_SSL_CLIENT = new GSSCaller("SSL_CLIENT");
  
  public static final GSSCaller CALLER_SSL_SERVER = new GSSCaller("SSL_SERVER");
  
  private String name;
  
  GSSCaller(String paramString) { this.name = paramString; }
  
  public String toString() { return "GSSCaller{" + this.name + '}'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSCaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */