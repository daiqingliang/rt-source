package sun.net.spi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import sun.net.NetProperties;
import sun.net.SocksProxy;

public class DefaultProxySelector extends ProxySelector {
  static final String[][] props = { { "http", "http.proxy", "proxy", "socksProxy" }, { "https", "https.proxy", "proxy", "socksProxy" }, { "ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy" }, { "gopher", "gopherProxy", "socksProxy" }, { "socket", "socksProxy" } };
  
  private static final String SOCKS_PROXY_VERSION = "socksProxyVersion";
  
  private static boolean hasSystemProxies = false;
  
  public List<Proxy> select(URI paramURI) {
    if (paramURI == null)
      throw new IllegalArgumentException("URI can't be null."); 
    String str1 = paramURI.getScheme();
    String str2 = paramURI.getHost();
    if (str2 == null) {
      String str = paramURI.getAuthority();
      if (str != null) {
        int i = str.indexOf('@');
        if (i >= 0)
          str = str.substring(i + 1); 
        i = str.lastIndexOf(':');
        if (i >= 0)
          str = str.substring(0, i); 
        str2 = str;
      } 
    } 
    if (str1 == null || str2 == null)
      throw new IllegalArgumentException("protocol = " + str1 + " host = " + str2); 
    ArrayList arrayList = new ArrayList(1);
    NonProxyInfo nonProxyInfo1 = null;
    if ("http".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
    } else if ("https".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
    } else if ("ftp".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.ftpNonProxyInfo;
    } else if ("socket".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.socksNonProxyInfo;
    } 
    final String proto = str1;
    final NonProxyInfo nprop = nonProxyInfo1;
    final String urlhost = str2.toLowerCase();
    Proxy proxy = (Proxy)AccessController.doPrivileged(new PrivilegedAction<Proxy>() {
          public Proxy run() {
            String str1 = null;
            int i = 0;
            String str2 = null;
            InetSocketAddress inetSocketAddress = null;
            for (byte b = 0; b < DefaultProxySelector.props.length; b++) {
              if (DefaultProxySelector.props[b][0].equalsIgnoreCase(proto)) {
                byte b1;
                for (b1 = 1; b1 < DefaultProxySelector.props[b].length; b1++) {
                  str1 = NetProperties.get(DefaultProxySelector.props[b][b1] + "Host");
                  if (str1 != null && str1.length() != 0)
                    break; 
                } 
                if (str1 == null || str1.length() == 0) {
                  if (hasSystemProxies) {
                    String str;
                    if (proto.equalsIgnoreCase("socket")) {
                      str = "socks";
                    } else {
                      str = proto;
                    } 
                    Proxy proxy = DefaultProxySelector.this.getSystemProxy(str, urlhost);
                    if (proxy != null)
                      return proxy; 
                  } 
                  return Proxy.NO_PROXY;
                } 
                if (nprop != null) {
                  str2 = NetProperties.get(this.val$nprop.property);
                  synchronized (nprop) {
                    if (str2 == null) {
                      if (this.val$nprop.defaultVal != null) {
                        str2 = this.val$nprop.defaultVal;
                      } else {
                        this.val$nprop.hostsSource = null;
                        this.val$nprop.pattern = null;
                      } 
                    } else if (str2.length() != 0) {
                      str2 = str2 + "|localhost|127.*|[::1]|0.0.0.0|[::0]";
                    } 
                    if (str2 != null && !str2.equals(this.val$nprop.hostsSource)) {
                      this.val$nprop.pattern = DefaultProxySelector.toPattern(str2);
                      this.val$nprop.hostsSource = str2;
                    } 
                    if (DefaultProxySelector.shouldNotUseProxyFor(this.val$nprop.pattern, urlhost))
                      return Proxy.NO_PROXY; 
                  } 
                } 
                i = NetProperties.getInteger(DefaultProxySelector.props[b][b1] + "Port", 0).intValue();
                if (i == 0 && b1 < DefaultProxySelector.props[b].length - 1)
                  for (byte b2 = 1; b2 < DefaultProxySelector.props[b].length - 1; b2++) {
                    if (b2 != b1 && i == 0)
                      i = NetProperties.getInteger(DefaultProxySelector.props[b][b2] + "Port", 0).intValue(); 
                  }  
                if (i == 0)
                  if (b1 == DefaultProxySelector.props[b].length - 1) {
                    i = DefaultProxySelector.this.defaultPort("socket");
                  } else {
                    i = DefaultProxySelector.this.defaultPort(proto);
                  }  
                inetSocketAddress = InetSocketAddress.createUnresolved(str1, i);
                if (b1 == DefaultProxySelector.props[b].length - 1) {
                  int j = NetProperties.getInteger("socksProxyVersion", 5).intValue();
                  return SocksProxy.create(inetSocketAddress, j);
                } 
                return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
              } 
            } 
            return Proxy.NO_PROXY;
          }
        });
    arrayList.add(proxy);
    return arrayList;
  }
  
  public void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException) {
    if (paramURI == null || paramSocketAddress == null || paramIOException == null)
      throw new IllegalArgumentException("Arguments can't be null."); 
  }
  
  private int defaultPort(String paramString) { return "http".equalsIgnoreCase(paramString) ? 80 : ("https".equalsIgnoreCase(paramString) ? 443 : ("ftp".equalsIgnoreCase(paramString) ? 80 : ("socket".equalsIgnoreCase(paramString) ? 1080 : ("gopher".equalsIgnoreCase(paramString) ? 80 : -1)))); }
  
  private static native boolean init();
  
  private native Proxy getSystemProxy(String paramString1, String paramString2);
  
  static boolean shouldNotUseProxyFor(Pattern paramPattern, String paramString) { return (paramPattern == null || paramString.isEmpty()) ? false : paramPattern.matcher(paramString).matches(); }
  
  static Pattern toPattern(String paramString) {
    boolean bool = true;
    StringJoiner stringJoiner = new StringJoiner("|");
    for (String str : paramString.split("\\|")) {
      if (!str.isEmpty()) {
        bool = false;
        String str1 = disjunctToRegex(str.toLowerCase());
        stringJoiner.add(str1);
      } 
    } 
    return bool ? null : Pattern.compile(stringJoiner.toString());
  }
  
  static String disjunctToRegex(String paramString) {
    String str;
    if (paramString.startsWith("*")) {
      str = ".*" + Pattern.quote(paramString.substring(1));
    } else if (paramString.endsWith("*")) {
      str = Pattern.quote(paramString.substring(0, paramString.length() - 1)) + ".*";
    } else {
      str = Pattern.quote(paramString);
    } 
    return str;
  }
  
  static  {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return NetProperties.getBoolean("java.net.useSystemProxies"); }
        });
    if (bool != null && bool.booleanValue()) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              System.loadLibrary("net");
              return null;
            }
          });
      hasSystemProxies = init();
    } 
  }
  
  static class NonProxyInfo {
    static final String defStringVal = "localhost|127.*|[::1]|0.0.0.0|[::0]";
    
    String hostsSource;
    
    Pattern pattern;
    
    final String property;
    
    final String defaultVal;
    
    static NonProxyInfo ftpNonProxyInfo = new NonProxyInfo("ftp.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    static NonProxyInfo httpNonProxyInfo = new NonProxyInfo("http.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    static NonProxyInfo socksNonProxyInfo = new NonProxyInfo("socksNonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    NonProxyInfo(String param1String1, String param1String2, Pattern param1Pattern, String param1String3) {
      this.property = param1String1;
      this.hostsSource = param1String2;
      this.pattern = param1Pattern;
      this.defaultVal = param1String3;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\spi\DefaultProxySelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */