package sun.net.dns;

import java.util.List;

public abstract class ResolverConfiguration {
  private static final Object lock = new Object();
  
  private static ResolverConfiguration provider;
  
  public static ResolverConfiguration open() {
    synchronized (lock) {
      if (provider == null)
        provider = new ResolverConfigurationImpl(); 
      return provider;
    } 
  }
  
  public abstract List<String> searchlist();
  
  public abstract List<String> nameservers();
  
  public abstract Options options();
  
  public static abstract class Options {
    public int attempts() { return -1; }
    
    public int retrans() { return -1; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\dns\ResolverConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */