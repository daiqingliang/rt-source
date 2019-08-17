package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public final class NetworkInterface {
  private String name;
  
  private String displayName;
  
  private int index;
  
  private InetAddress[] addrs;
  
  private InterfaceAddress[] bindings;
  
  private NetworkInterface[] childs;
  
  private NetworkInterface parent = null;
  
  private boolean virtual = false;
  
  private static final NetworkInterface defaultInterface;
  
  private static final int defaultIndex;
  
  NetworkInterface() {}
  
  NetworkInterface(String paramString, int paramInt, InetAddress[] paramArrayOfInetAddress) {
    this.name = paramString;
    this.index = paramInt;
    this.addrs = paramArrayOfInetAddress;
  }
  
  public String getName() { return this.name; }
  
  public Enumeration<InetAddress> getInetAddresses() {
    class checkedAddresses extends Object implements Enumeration<InetAddress> {
      private int i = 0;
      
      private int count = 0;
      
      private InetAddress[] local_addrs;
      
      checkedAddresses() {
        this.local_addrs = new InetAddress[this$0.addrs.length];
        boolean bool = true;
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          try {
            securityManager.checkPermission(new NetPermission("getNetworkInformation"));
          } catch (SecurityException securityException) {
            bool = false;
          }  
        for (byte b = 0; b < this$0.addrs.length; b++) {
          try {
            if (securityManager != null && !bool)
              securityManager.checkConnect(this$0.addrs[b].getHostAddress(), -1); 
            this.local_addrs[this.count++] = this$0.addrs[b];
          } catch (SecurityException securityException) {}
        } 
      }
      
      public InetAddress nextElement() {
        if (this.i < this.count)
          return this.local_addrs[this.i++]; 
        throw new NoSuchElementException();
      }
      
      public boolean hasMoreElements() throws SocketException { return (this.i < this.count); }
    };
    return new checkedAddresses();
  }
  
  public List<InterfaceAddress> getInterfaceAddresses() {
    ArrayList arrayList = new ArrayList(1);
    SecurityManager securityManager = System.getSecurityManager();
    for (byte b = 0; b < this.bindings.length; b++) {
      try {
        if (securityManager != null)
          securityManager.checkConnect(this.bindings[b].getAddress().getHostAddress(), -1); 
        arrayList.add(this.bindings[b]);
      } catch (SecurityException securityException) {}
    } 
    return arrayList;
  }
  
  public Enumeration<NetworkInterface> getSubInterfaces() {
    class subIFs extends Object implements Enumeration<NetworkInterface> {
      private int i = 0;
      
      public NetworkInterface nextElement() {
        if (this.i < NetworkInterface.this.childs.length)
          return NetworkInterface.this.childs[this.i++]; 
        throw new NoSuchElementException();
      }
      
      public boolean hasMoreElements() throws SocketException { return (this.i < NetworkInterface.this.childs.length); }
    };
    return new subIFs();
  }
  
  public NetworkInterface getParent() { return this.parent; }
  
  public int getIndex() { return this.index; }
  
  public String getDisplayName() { return "".equals(this.displayName) ? null : this.displayName; }
  
  public static NetworkInterface getByName(String paramString) throws SocketException {
    if (paramString == null)
      throw new NullPointerException(); 
    return getByName0(paramString);
  }
  
  public static NetworkInterface getByIndex(int paramInt) throws SocketException {
    if (paramInt < 0)
      throw new IllegalArgumentException("Interface index can't be negative"); 
    return getByIndex0(paramInt);
  }
  
  public static NetworkInterface getByInetAddress(InetAddress paramInetAddress) throws SocketException {
    if (paramInetAddress == null)
      throw new NullPointerException(); 
    if (paramInetAddress instanceof Inet4Address) {
      Inet4Address inet4Address = (Inet4Address)paramInetAddress;
      if (inet4Address.holder.family != 1)
        throw new IllegalArgumentException("invalid family type: " + inet4Address.holder.family); 
    } else if (paramInetAddress instanceof Inet6Address) {
      Inet6Address inet6Address = (Inet6Address)paramInetAddress;
      if (inet6Address.holder.family != 2)
        throw new IllegalArgumentException("invalid family type: " + inet6Address.holder.family); 
    } else {
      throw new IllegalArgumentException("invalid address type: " + paramInetAddress);
    } 
    return getByInetAddress0(paramInetAddress);
  }
  
  public static Enumeration<NetworkInterface> getNetworkInterfaces() {
    final NetworkInterface[] netifs = getAll();
    return (arrayOfNetworkInterface == null) ? null : new Enumeration<NetworkInterface>() {
        private int i = 0;
        
        public NetworkInterface nextElement() {
          if (netifs != null && this.i < netifs.length)
            return netifs[this.i++]; 
          throw new NoSuchElementException();
        }
        
        public boolean hasMoreElements() throws SocketException { return (netifs != null && this.i < netifs.length); }
      };
  }
  
  private static native NetworkInterface[] getAll() throws SocketException;
  
  private static native NetworkInterface getByName0(String paramString) throws SocketException;
  
  private static native NetworkInterface getByIndex0(int paramInt) throws SocketException;
  
  private static native NetworkInterface getByInetAddress0(InetAddress paramInetAddress) throws SocketException;
  
  public boolean isUp() throws SocketException { return isUp0(this.name, this.index); }
  
  public boolean isLoopback() throws SocketException { return isLoopback0(this.name, this.index); }
  
  public boolean isPointToPoint() throws SocketException { return isP2P0(this.name, this.index); }
  
  public boolean supportsMulticast() throws SocketException { return supportsMulticast0(this.name, this.index); }
  
  public byte[] getHardwareAddress() throws SocketException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkPermission(new NetPermission("getNetworkInformation"));
      } catch (SecurityException securityException) {
        if (!getInetAddresses().hasMoreElements())
          return null; 
      }  
    for (InetAddress inetAddress : this.addrs) {
      if (inetAddress instanceof Inet4Address)
        return getMacAddr0(((Inet4Address)inetAddress).getAddress(), this.name, this.index); 
    } 
    return getMacAddr0(null, this.name, this.index);
  }
  
  public int getMTU() { return getMTU0(this.name, this.index); }
  
  public boolean isVirtual() throws SocketException { return this.virtual; }
  
  private static native boolean isUp0(String paramString, int paramInt) throws SocketException;
  
  private static native boolean isLoopback0(String paramString, int paramInt) throws SocketException;
  
  private static native boolean supportsMulticast0(String paramString, int paramInt) throws SocketException;
  
  private static native boolean isP2P0(String paramString, int paramInt) throws SocketException;
  
  private static native byte[] getMacAddr0(byte[] paramArrayOfByte, String paramString, int paramInt) throws SocketException;
  
  private static native int getMTU0(String paramString, int paramInt) throws SocketException;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof NetworkInterface))
      return false; 
    NetworkInterface networkInterface = (NetworkInterface)paramObject;
    if (this.name != null) {
      if (!this.name.equals(networkInterface.name))
        return false; 
    } else if (networkInterface.name != null) {
      return false;
    } 
    if (this.addrs == null)
      return (networkInterface.addrs == null); 
    if (networkInterface.addrs == null)
      return false; 
    if (this.addrs.length != networkInterface.addrs.length)
      return false; 
    InetAddress[] arrayOfInetAddress = networkInterface.addrs;
    int i = arrayOfInetAddress.length;
    for (byte b = 0; b < i; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < i; b1++) {
        if (this.addrs[b].equals(arrayOfInetAddress[b1])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  public int hashCode() { return (this.name == null) ? 0 : this.name.hashCode(); }
  
  public String toString() {
    String str = "name:";
    str = str + ((this.name == null) ? "null" : this.name);
    if (this.displayName != null)
      str = str + " (" + this.displayName + ")"; 
    return str;
  }
  
  private static native void init();
  
  static NetworkInterface getDefault() { return defaultInterface; }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
    init();
    defaultInterface = DefaultInterface.getDefault();
    if (defaultInterface != null) {
      defaultIndex = defaultInterface.getIndex();
    } else {
      defaultIndex = 0;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\NetworkInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */