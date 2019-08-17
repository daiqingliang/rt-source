package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import sun.misc.Unsafe;
import sun.net.InetAddressCachePolicy;
import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;
import sun.net.util.IPAddressUtil;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class InetAddress implements Serializable {
  static final int IPv4 = 1;
  
  static final int IPv6 = 2;
  
  static boolean preferIPv6Address = false;
  
  final InetAddressHolder holder = new InetAddressHolder();
  
  private static List<NameService> nameServices = null;
  
  private String canonicalHostName = null;
  
  private static final long serialVersionUID = 3286316764910316507L;
  
  private static Cache addressCache;
  
  private static Cache negativeCache;
  
  private static boolean addressCacheInit;
  
  static InetAddress[] unknown_array;
  
  static InetAddressImpl impl;
  
  private static final HashMap<String, Void> lookupTable;
  
  private static InetAddress cachedLocalHost;
  
  private static long cacheTime;
  
  private static final long maxCacheTime = 5000L;
  
  private static final Object cacheLock;
  
  private static final long FIELDS_OFFSET;
  
  private static final Unsafe UNSAFE;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  InetAddressHolder holder() { return this.holder; }
  
  private Object readResolve() throws ObjectStreamException { return new Inet4Address(holder().getHostName(), holder().getAddress()); }
  
  public boolean isMulticastAddress() { return false; }
  
  public boolean isAnyLocalAddress() { return false; }
  
  public boolean isLoopbackAddress() { return false; }
  
  public boolean isLinkLocalAddress() { return false; }
  
  public boolean isSiteLocalAddress() { return false; }
  
  public boolean isMCGlobal() { return false; }
  
  public boolean isMCNodeLocal() { return false; }
  
  public boolean isMCLinkLocal() { return false; }
  
  public boolean isMCSiteLocal() { return false; }
  
  public boolean isMCOrgLocal() { return false; }
  
  public boolean isReachable(int paramInt) throws IOException { return isReachable(null, 0, paramInt); }
  
  public boolean isReachable(NetworkInterface paramNetworkInterface, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("ttl can't be negative"); 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("timeout can't be negative"); 
    return impl.isReachable(this, paramInt2, paramNetworkInterface, paramInt1);
  }
  
  public String getHostName() { return getHostName(true); }
  
  String getHostName(boolean paramBoolean) {
    if (holder().getHostName() == null)
      (holder()).hostName = getHostFromNameService(this, paramBoolean); 
    return holder().getHostName();
  }
  
  public String getCanonicalHostName() {
    if (this.canonicalHostName == null)
      this.canonicalHostName = getHostFromNameService(this, true); 
    return this.canonicalHostName;
  }
  
  private static String getHostFromNameService(InetAddress paramInetAddress, boolean paramBoolean) {
    String str = null;
    for (NameService nameService : nameServices) {
      try {
        str = nameService.getHostByAddr(paramInetAddress.getAddress());
        if (paramBoolean) {
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkConnect(str, -1); 
        } 
        InetAddress[] arrayOfInetAddress = getAllByName0(str, paramBoolean);
        boolean bool = false;
        if (arrayOfInetAddress != null)
          for (byte b = 0; !bool && b < arrayOfInetAddress.length; b++)
            bool = paramInetAddress.equals(arrayOfInetAddress[b]);  
        if (!bool)
          return paramInetAddress.getHostAddress(); 
        break;
      } catch (SecurityException securityException) {
        str = paramInetAddress.getHostAddress();
        break;
      } catch (UnknownHostException unknownHostException) {
        str = paramInetAddress.getHostAddress();
      } 
    } 
    return str;
  }
  
  public byte[] getAddress() { return null; }
  
  public String getHostAddress() { return null; }
  
  public int hashCode() { return -1; }
  
  public boolean equals(Object paramObject) { return false; }
  
  public String toString() {
    String str = holder().getHostName();
    return ((str != null) ? str : "") + "/" + getHostAddress();
  }
  
  private static void cacheInitIfNeeded() {
    assert Thread.holdsLock(addressCache);
    if (addressCacheInit)
      return; 
    unknown_array = new InetAddress[1];
    unknown_array[0] = impl.anyLocalAddress();
    addressCache.put(impl.anyLocalAddress().getHostName(), unknown_array);
    addressCacheInit = true;
  }
  
  private static void cacheAddresses(String paramString, InetAddress[] paramArrayOfInetAddress, boolean paramBoolean) {
    paramString = paramString.toLowerCase();
    synchronized (addressCache) {
      cacheInitIfNeeded();
      if (paramBoolean) {
        addressCache.put(paramString, paramArrayOfInetAddress);
      } else {
        negativeCache.put(paramString, paramArrayOfInetAddress);
      } 
    } 
  }
  
  private static InetAddress[] getCachedAddresses(String paramString) {
    paramString = paramString.toLowerCase();
    synchronized (addressCache) {
      cacheInitIfNeeded();
      CacheEntry cacheEntry = addressCache.get(paramString);
      if (cacheEntry == null)
        cacheEntry = negativeCache.get(paramString); 
      if (cacheEntry != null)
        return cacheEntry.addresses; 
    } 
    return null;
  }
  
  private static NameService createNSProvider(String paramString) {
    if (paramString == null)
      return null; 
    NameService nameService = null;
    if (paramString.equals("default")) {
      nameService = new NameService() {
          public InetAddress[] lookupAllHostAddr(String param1String) { return InetAddress.impl.lookupAllHostAddr(param1String); }
          
          public String getHostByAddr(byte[] param1ArrayOfByte) throws UnknownHostException { return InetAddress.impl.getHostByAddr(param1ArrayOfByte); }
        };
    } else {
      final String providerName = paramString;
      try {
        nameService = (NameService)AccessController.doPrivileged(new PrivilegedExceptionAction<NameService>() {
              public NameService run() {
                for (NameServiceDescriptor nameServiceDescriptor : ServiceLoader.load(NameServiceDescriptor.class)) {
                  if (providerName.equalsIgnoreCase(nameServiceDescriptor.getType() + "," + nameServiceDescriptor.getProviderName()))
                    try {
                      return nameServiceDescriptor.createNameService();
                    } catch (Exception exception) {
                      exception.printStackTrace();
                      System.err.println("Cannot create name service:" + providerName + ": " + exception);
                    }  
                } 
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {}
    } 
    return nameService;
  }
  
  public static InetAddress getByAddress(String paramString, byte[] paramArrayOfByte) throws UnknownHostException {
    if (paramString != null && paramString.length() > 0 && paramString.charAt(0) == '[' && paramString.charAt(paramString.length() - 1) == ']')
      paramString = paramString.substring(1, paramString.length() - 1); 
    if (paramArrayOfByte != null) {
      if (paramArrayOfByte.length == 4)
        return new Inet4Address(paramString, paramArrayOfByte); 
      if (paramArrayOfByte.length == 16) {
        byte[] arrayOfByte = IPAddressUtil.convertFromIPv4MappedAddress(paramArrayOfByte);
        return (arrayOfByte != null) ? new Inet4Address(paramString, arrayOfByte) : new Inet6Address(paramString, paramArrayOfByte);
      } 
    } 
    throw new UnknownHostException("addr is of illegal length");
  }
  
  public static InetAddress getByName(String paramString) throws UnknownHostException { return getAllByName(paramString)[0]; }
  
  private static InetAddress getByName(String paramString, InetAddress paramInetAddress) throws UnknownHostException { return getAllByName(paramString, paramInetAddress)[0]; }
  
  public static InetAddress[] getAllByName(String paramString) { return getAllByName(paramString, null); }
  
  private static InetAddress[] getAllByName(String paramString, InetAddress paramInetAddress) throws UnknownHostException {
    if (paramString == null || paramString.length() == 0) {
      InetAddress[] arrayOfInetAddress = new InetAddress[1];
      arrayOfInetAddress[0] = impl.loopbackAddress();
      return arrayOfInetAddress;
    } 
    boolean bool = false;
    if (paramString.charAt(0) == '[')
      if (paramString.length() > 2 && paramString.charAt(paramString.length() - 1) == ']') {
        paramString = paramString.substring(1, paramString.length() - 1);
        bool = true;
      } else {
        throw new UnknownHostException(paramString + ": invalid IPv6 address");
      }  
    if (Character.digit(paramString.charAt(0), 16) != -1 || paramString.charAt(0) == ':') {
      byte[] arrayOfByte = null;
      int i = -1;
      String str = null;
      arrayOfByte = IPAddressUtil.textToNumericFormatV4(paramString);
      if (arrayOfByte == null) {
        int j;
        if ((j = paramString.indexOf("%")) != -1) {
          i = checkNumericZone(paramString);
          if (i == -1)
            str = paramString.substring(j + 1); 
        } 
        if ((arrayOfByte = IPAddressUtil.textToNumericFormatV6(paramString)) == null && paramString.contains(":"))
          throw new UnknownHostException(paramString + ": invalid IPv6 address"); 
      } else if (bool) {
        throw new UnknownHostException("[" + paramString + "]");
      } 
      InetAddress[] arrayOfInetAddress = new InetAddress[1];
      if (arrayOfByte != null) {
        if (arrayOfByte.length == 4) {
          arrayOfInetAddress[0] = new Inet4Address(null, arrayOfByte);
        } else if (str != null) {
          arrayOfInetAddress[0] = new Inet6Address(null, arrayOfByte, str);
        } else {
          arrayOfInetAddress[0] = new Inet6Address(null, arrayOfByte, i);
        } 
        return arrayOfInetAddress;
      } 
    } else if (bool) {
      throw new UnknownHostException("[" + paramString + "]");
    } 
    return getAllByName0(paramString, paramInetAddress, true);
  }
  
  public static InetAddress getLoopbackAddress() { return impl.loopbackAddress(); }
  
  private static int checkNumericZone(String paramString) throws UnknownHostException {
    int i = paramString.indexOf('%');
    int j = paramString.length();
    int k = 0;
    if (i == -1)
      return -1; 
    for (int m = i + 1; m < j; m++) {
      char c = paramString.charAt(m);
      if (c == ']') {
        if (m == i + 1)
          return -1; 
        break;
      } 
      int n;
      if ((n = Character.digit(c, 10)) < 0)
        return -1; 
      k = k * 10 + n;
    } 
    return k;
  }
  
  private static InetAddress[] getAllByName0(String paramString) { return getAllByName0(paramString, true); }
  
  static InetAddress[] getAllByName0(String paramString, boolean paramBoolean) throws UnknownHostException { return getAllByName0(paramString, null, paramBoolean); }
  
  private static InetAddress[] getAllByName0(String paramString, InetAddress paramInetAddress, boolean paramBoolean) throws UnknownHostException {
    if (paramBoolean) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkConnect(paramString, -1); 
    } 
    InetAddress[] arrayOfInetAddress = getCachedAddresses(paramString);
    if (arrayOfInetAddress == null)
      arrayOfInetAddress = getAddressesFromNameService(paramString, paramInetAddress); 
    if (arrayOfInetAddress == unknown_array)
      throw new UnknownHostException(paramString); 
    return (InetAddress[])arrayOfInetAddress.clone();
  }
  
  private static InetAddress[] getAddressesFromNameService(String paramString, InetAddress paramInetAddress) throws UnknownHostException {
    InetAddress[] arrayOfInetAddress = null;
    boolean bool = false;
    UnknownHostException unknownHostException = null;
    if ((arrayOfInetAddress = checkLookupTable(paramString)) == null)
      try {
        for (NameService nameService : nameServices) {
          try {
            arrayOfInetAddress = nameService.lookupAllHostAddr(paramString);
            bool = true;
            break;
          } catch (UnknownHostException unknownHostException1) {
            if (paramString.equalsIgnoreCase("localhost")) {
              InetAddress[] arrayOfInetAddress1 = { impl.loopbackAddress() };
              arrayOfInetAddress = arrayOfInetAddress1;
              bool = true;
              break;
            } 
            arrayOfInetAddress = unknown_array;
            bool = false;
            unknownHostException = unknownHostException1;
          } 
        } 
        if (paramInetAddress != null && arrayOfInetAddress.length > 1 && !arrayOfInetAddress[0].equals(paramInetAddress)) {
          byte b;
          for (b = 1; b < arrayOfInetAddress.length && !arrayOfInetAddress[b].equals(paramInetAddress); b++);
          if (b < arrayOfInetAddress.length) {
            InetAddress inetAddress = paramInetAddress;
            for (byte b1 = 0; b1 < b; b1++) {
              InetAddress inetAddress1 = arrayOfInetAddress[b1];
              arrayOfInetAddress[b1] = inetAddress;
              inetAddress = inetAddress1;
            } 
            arrayOfInetAddress[b] = inetAddress;
          } 
        } 
        cacheAddresses(paramString, arrayOfInetAddress, bool);
        if (!bool && unknownHostException != null)
          throw unknownHostException; 
      } finally {
        updateLookupTable(paramString);
      }  
    return arrayOfInetAddress;
  }
  
  private static InetAddress[] checkLookupTable(String paramString) {
    synchronized (lookupTable) {
      if (!lookupTable.containsKey(paramString)) {
        lookupTable.put(paramString, null);
        return null;
      } 
      while (lookupTable.containsKey(paramString)) {
        try {
          lookupTable.wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
    InetAddress[] arrayOfInetAddress = getCachedAddresses(paramString);
    if (arrayOfInetAddress == null)
      synchronized (lookupTable) {
        lookupTable.put(paramString, null);
        return null;
      }  
    return arrayOfInetAddress;
  }
  
  private static void updateLookupTable(String paramString) {
    synchronized (lookupTable) {
      lookupTable.remove(paramString);
      lookupTable.notifyAll();
    } 
  }
  
  public static InetAddress getByAddress(byte[] paramArrayOfByte) throws UnknownHostException { return getByAddress(null, paramArrayOfByte); }
  
  public static InetAddress getLocalHost() {
    SecurityManager securityManager = System.getSecurityManager();
    try {
      String str = impl.getLocalHostName();
      if (securityManager != null)
        securityManager.checkConnect(str, -1); 
      if (str.equals("localhost"))
        return impl.loopbackAddress(); 
      InetAddress inetAddress = null;
      synchronized (cacheLock) {
        long l = System.currentTimeMillis();
        if (cachedLocalHost != null)
          if (l - cacheTime < 5000L) {
            inetAddress = cachedLocalHost;
          } else {
            cachedLocalHost = null;
          }  
        if (inetAddress == null) {
          InetAddress[] arrayOfInetAddress;
          try {
            arrayOfInetAddress = getAddressesFromNameService(str, null);
          } catch (UnknownHostException unknownHostException1) {
            UnknownHostException unknownHostException2 = new UnknownHostException(str + ": " + unknownHostException1.getMessage());
            unknownHostException2.initCause(unknownHostException1);
            throw unknownHostException2;
          } 
          cachedLocalHost = arrayOfInetAddress[0];
          cacheTime = l;
          inetAddress = arrayOfInetAddress[0];
        } 
      } 
      return inetAddress;
    } catch (SecurityException securityException) {
      return impl.loopbackAddress();
    } 
  }
  
  private static native void init();
  
  static InetAddress anyLocalAddress() { return impl.anyLocalAddress(); }
  
  static InetAddressImpl loadImpl(String paramString) {
    Object object = null;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("impl.prefix", ""));
    try {
      object = Class.forName("java.net." + str + paramString).newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("Class not found: java.net." + str + paramString + ":\ncheck impl.prefix property in your properties file.");
    } catch (InstantiationException instantiationException) {
      System.err.println("Could not instantiate: java.net." + str + paramString + ":\ncheck impl.prefix property in your properties file.");
    } catch (IllegalAccessException illegalAccessException) {
      System.err.println("Cannot access class: java.net." + str + paramString + ":\ncheck impl.prefix property in your properties file.");
    } 
    if (object == null)
      try {
        object = Class.forName(paramString).newInstance();
      } catch (Exception exception) {
        throw new Error("System property impl.prefix incorrect");
      }  
    return (InetAddressImpl)object;
  }
  
  private void readObjectNoData(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (getClass().getClassLoader() != null)
      throw new SecurityException("invalid address type"); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (getClass().getClassLoader() != null)
      throw new SecurityException("invalid address type"); 
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str = (String)getField.get("hostName", null);
    int i = getField.get("address", 0);
    int j = getField.get("family", 0);
    if (j != 1 && j != 2)
      throw new InvalidObjectException("invalid address family type: " + j); 
    InetAddressHolder inetAddressHolder = new InetAddressHolder(str, i, j);
    UNSAFE.putObject(this, FIELDS_OFFSET, inetAddressHolder);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (getClass().getClassLoader() != null)
      throw new SecurityException("invalid address type"); 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("hostName", holder().getHostName());
    putField.put("address", holder().getAddress());
    putField.put("family", holder().getFamily());
    paramObjectOutputStream.writeFields();
  }
  
  static  {
    preferIPv6Address = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.net.preferIPv6Addresses"))).booleanValue();
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
    init();
    addressCache = new Cache(Cache.Type.Positive);
    negativeCache = new Cache(Cache.Type.Negative);
    addressCacheInit = false;
    lookupTable = new HashMap();
    impl = InetAddressImplFactory.create();
    str1 = null;
    String str2 = "sun.net.spi.nameservice.provider.";
    byte b = 1;
    nameServices = new ArrayList();
    for (str1 = (String)AccessController.doPrivileged(new GetPropertyAction(str2 + b)); str1 != null; str1 = (String)AccessController.doPrivileged(new GetPropertyAction(str2 + ++b))) {
      NameService nameService = createNSProvider(str1);
      if (nameService != null)
        nameServices.add(nameService); 
    } 
    if (nameServices.size() == 0) {
      NameService nameService = createNSProvider("default");
      nameServices.add(nameService);
    } 
    cachedLocalHost = null;
    cacheTime = 0L;
    cacheLock = new Object();
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      FIELDS_OFFSET = unsafe.objectFieldOffset(InetAddress.class.getDeclaredField("holder"));
      UNSAFE = unsafe;
    } catch (ReflectiveOperationException str1) {
      throw new Error(str1);
    } 
    serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("hostName", String.class), new ObjectStreamField("address", int.class), new ObjectStreamField("family", int.class) };
  }
  
  static final class Cache {
    private LinkedHashMap<String, InetAddress.CacheEntry> cache;
    
    private Type type;
    
    public Cache(Type param1Type) {
      this.type = param1Type;
      this.cache = new LinkedHashMap();
    }
    
    private int getPolicy() { return (this.type == Type.Positive) ? InetAddressCachePolicy.get() : InetAddressCachePolicy.getNegative(); }
    
    public Cache put(String param1String, InetAddress[] param1ArrayOfInetAddress) {
      long l;
      int i = getPolicy();
      if (i == 0)
        return this; 
      if (i != -1) {
        LinkedList linkedList = new LinkedList();
        long l1 = System.currentTimeMillis();
        for (String str : this.cache.keySet()) {
          InetAddress.CacheEntry cacheEntry1 = (InetAddress.CacheEntry)this.cache.get(str);
          if (cacheEntry1.expiration >= 0L && cacheEntry1.expiration < l1)
            linkedList.add(str); 
        } 
        for (String str : linkedList)
          this.cache.remove(str); 
      } 
      if (i == -1) {
        l = -1L;
      } else {
        l = System.currentTimeMillis() + (i * 1000);
      } 
      InetAddress.CacheEntry cacheEntry = new InetAddress.CacheEntry(param1ArrayOfInetAddress, l);
      this.cache.put(param1String, cacheEntry);
      return this;
    }
    
    public InetAddress.CacheEntry get(String param1String) {
      int i = getPolicy();
      if (i == 0)
        return null; 
      InetAddress.CacheEntry cacheEntry = (InetAddress.CacheEntry)this.cache.get(param1String);
      if (cacheEntry != null && i != -1 && cacheEntry.expiration >= 0L && cacheEntry.expiration < System.currentTimeMillis()) {
        this.cache.remove(param1String);
        cacheEntry = null;
      } 
      return cacheEntry;
    }
    
    enum Type {
      Positive, Negative;
    }
  }
  
  enum Type {
    Positive, Negative;
  }
  
  static final class CacheEntry {
    InetAddress[] addresses;
    
    long expiration;
    
    CacheEntry(InetAddress[] param1ArrayOfInetAddress, long param1Long) {
      this.addresses = param1ArrayOfInetAddress;
      this.expiration = param1Long;
    }
  }
  
  static class InetAddressHolder {
    String originalHostName;
    
    String hostName;
    
    int address;
    
    int family;
    
    InetAddressHolder() {}
    
    InetAddressHolder(String param1String, int param1Int1, int param1Int2) {
      this.originalHostName = param1String;
      this.hostName = param1String;
      this.address = param1Int1;
      this.family = param1Int2;
    }
    
    void init(String param1String, int param1Int) {
      this.originalHostName = param1String;
      this.hostName = param1String;
      if (param1Int != -1)
        this.family = param1Int; 
    }
    
    String getHostName() { return this.hostName; }
    
    String getOriginalHostName() { return this.originalHostName; }
    
    int getAddress() { return this.address; }
    
    int getFamily() { return this.family; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\InetAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */