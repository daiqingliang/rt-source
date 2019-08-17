package java.net;

public interface SocketOptions {
  public static final int TCP_NODELAY = 1;
  
  public static final int SO_BINDADDR = 15;
  
  public static final int SO_REUSEADDR = 4;
  
  public static final int SO_BROADCAST = 32;
  
  public static final int IP_MULTICAST_IF = 16;
  
  public static final int IP_MULTICAST_IF2 = 31;
  
  public static final int IP_MULTICAST_LOOP = 18;
  
  public static final int IP_TOS = 3;
  
  public static final int SO_LINGER = 128;
  
  public static final int SO_TIMEOUT = 4102;
  
  public static final int SO_SNDBUF = 4097;
  
  public static final int SO_RCVBUF = 4098;
  
  public static final int SO_KEEPALIVE = 8;
  
  public static final int SO_OOBINLINE = 4099;
  
  void setOption(int paramInt, Object paramObject) throws SocketException;
  
  Object getOption(int paramInt) throws SocketException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocketOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */