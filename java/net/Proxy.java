package java.net;

public class Proxy {
  private Type type;
  
  private SocketAddress sa;
  
  public static final Proxy NO_PROXY = new Proxy();
  
  private Proxy() {
    this.type = Type.DIRECT;
    this.sa = null;
  }
  
  public Proxy(Type paramType, SocketAddress paramSocketAddress) {
    if (paramType == Type.DIRECT || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("type " + paramType + " is not compatible with address " + paramSocketAddress); 
    this.type = paramType;
    this.sa = paramSocketAddress;
  }
  
  public Type type() { return this.type; }
  
  public SocketAddress address() { return this.sa; }
  
  public String toString() { return (type() == Type.DIRECT) ? "DIRECT" : (type() + " @ " + address()); }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Proxy))
      return false; 
    Proxy proxy = (Proxy)paramObject;
    return (proxy.type() == type()) ? ((address() == null) ? ((proxy.address() == null)) : address().equals(proxy.address())) : false;
  }
  
  public final int hashCode() { return (address() == null) ? type().hashCode() : (type().hashCode() + address().hashCode()); }
  
  public enum Type {
    DIRECT, HTTP, SOCKS;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\Proxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */