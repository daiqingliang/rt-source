package sun.nio.ch;

import java.net.ProtocolFamily;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.util.HashMap;
import java.util.Map;

class SocketOptionRegistry {
  public static OptionKey findOption(SocketOption<?> paramSocketOption, ProtocolFamily paramProtocolFamily) {
    RegistryKey registryKey = new RegistryKey(paramSocketOption, paramProtocolFamily);
    return (OptionKey)LazyInitialization.options.get(registryKey);
  }
  
  private static class LazyInitialization {
    static final Map<SocketOptionRegistry.RegistryKey, OptionKey> options = options();
    
    private static Map<SocketOptionRegistry.RegistryKey, OptionKey> options() {
      HashMap hashMap = new HashMap();
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_BROADCAST, Net.UNSPEC), new OptionKey(65535, 32));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_KEEPALIVE, Net.UNSPEC), new OptionKey(65535, 8));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_LINGER, Net.UNSPEC), new OptionKey(65535, 128));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_SNDBUF, Net.UNSPEC), new OptionKey(65535, 4097));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_RCVBUF, Net.UNSPEC), new OptionKey(65535, 4098));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_REUSEADDR, Net.UNSPEC), new OptionKey(65535, 4));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.TCP_NODELAY, Net.UNSPEC), new OptionKey(6, 1));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET), new OptionKey(0, 3));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET), new OptionKey(0, 9));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET), new OptionKey(0, 10));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET), new OptionKey(0, 11));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET6), new OptionKey(41, 39));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET6), new OptionKey(41, 9));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET6), new OptionKey(41, 10));
      hashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET6), new OptionKey(41, 11));
      hashMap.put(new SocketOptionRegistry.RegistryKey(ExtendedSocketOption.SO_OOBINLINE, Net.UNSPEC), new OptionKey(65535, 256));
      return hashMap;
    }
  }
  
  private static class RegistryKey {
    private final SocketOption<?> name;
    
    private final ProtocolFamily family;
    
    RegistryKey(SocketOption<?> param1SocketOption, ProtocolFamily param1ProtocolFamily) {
      this.name = param1SocketOption;
      this.family = param1ProtocolFamily;
    }
    
    public int hashCode() { return this.name.hashCode() + this.family.hashCode(); }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null)
        return false; 
      if (!(param1Object instanceof RegistryKey))
        return false; 
      RegistryKey registryKey = (RegistryKey)param1Object;
      return (this.name != registryKey.name) ? false : (!(this.family != registryKey.family));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SocketOptionRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */