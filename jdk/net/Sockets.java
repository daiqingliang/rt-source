package jdk.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import jdk.Exported;
import sun.net.ExtendedOptionsImpl;

@Exported
public class Sockets {
  private static final HashMap<Class<?>, Set<SocketOption<?>>> options = new HashMap();
  
  private static Method siSetOption;
  
  private static Method siGetOption;
  
  private static Method dsiSetOption;
  
  private static Method dsiGetOption;
  
  private static void initMethods() {
    try {
      Class clazz = Class.forName("java.net.SocketSecrets");
      siSetOption = clazz.getDeclaredMethod("setOption", new Class[] { Object.class, SocketOption.class, Object.class });
      siSetOption.setAccessible(true);
      siGetOption = clazz.getDeclaredMethod("getOption", new Class[] { Object.class, SocketOption.class });
      siGetOption.setAccessible(true);
      dsiSetOption = clazz.getDeclaredMethod("setOption", new Class[] { DatagramSocket.class, SocketOption.class, Object.class });
      dsiSetOption.setAccessible(true);
      dsiGetOption = clazz.getDeclaredMethod("getOption", new Class[] { DatagramSocket.class, SocketOption.class });
      dsiGetOption.setAccessible(true);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new InternalError(reflectiveOperationException);
    } 
  }
  
  private static <T> void invokeSet(Method paramMethod, Object paramObject, SocketOption<T> paramSocketOption, T paramT) throws IOException {
    try {
      paramMethod.invoke(null, new Object[] { paramObject, paramSocketOption, paramT });
    } catch (Exception exception) {
      if (exception instanceof InvocationTargetException) {
        Throwable throwable = ((InvocationTargetException)exception).getTargetException();
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
      } 
      throw new RuntimeException(exception);
    } 
  }
  
  private static <T> T invokeGet(Method paramMethod, Object paramObject, SocketOption<T> paramSocketOption) throws IOException {
    try {
      return (T)paramMethod.invoke(null, new Object[] { paramObject, paramSocketOption });
    } catch (Exception exception) {
      if (exception instanceof InvocationTargetException) {
        Throwable throwable = ((InvocationTargetException)exception).getTargetException();
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
      } 
      throw new RuntimeException(exception);
    } 
  }
  
  public static <T> void setOption(Socket paramSocket, SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (!isSupported(Socket.class, paramSocketOption))
      throw new UnsupportedOperationException(paramSocketOption.name()); 
    invokeSet(siSetOption, paramSocket, paramSocketOption, paramT);
  }
  
  public static <T> T getOption(Socket paramSocket, SocketOption<T> paramSocketOption) throws IOException {
    if (!isSupported(Socket.class, paramSocketOption))
      throw new UnsupportedOperationException(paramSocketOption.name()); 
    return (T)invokeGet(siGetOption, paramSocket, paramSocketOption);
  }
  
  public static <T> void setOption(ServerSocket paramServerSocket, SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (!isSupported(ServerSocket.class, paramSocketOption))
      throw new UnsupportedOperationException(paramSocketOption.name()); 
    invokeSet(siSetOption, paramServerSocket, paramSocketOption, paramT);
  }
  
  public static <T> T getOption(ServerSocket paramServerSocket, SocketOption<T> paramSocketOption) throws IOException {
    if (!isSupported(ServerSocket.class, paramSocketOption))
      throw new UnsupportedOperationException(paramSocketOption.name()); 
    return (T)invokeGet(siGetOption, paramServerSocket, paramSocketOption);
  }
  
  public static <T> void setOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (!isSupported(paramDatagramSocket.getClass(), paramSocketOption))
      throw new UnsupportedOperationException(paramSocketOption.name()); 
    invokeSet(dsiSetOption, paramDatagramSocket, paramSocketOption, paramT);
  }
  
  public static <T> T getOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption) throws IOException {
    if (!isSupported(paramDatagramSocket.getClass(), paramSocketOption))
      throw new UnsupportedOperationException(paramSocketOption.name()); 
    return (T)invokeGet(dsiGetOption, paramDatagramSocket, paramSocketOption);
  }
  
  public static Set<SocketOption<?>> supportedOptions(Class<?> paramClass) {
    Set set = (Set)options.get(paramClass);
    if (set == null)
      throw new IllegalArgumentException("unknown socket type"); 
    return set;
  }
  
  private static boolean isSupported(Class<?> paramClass, SocketOption<?> paramSocketOption) {
    Set set = supportedOptions(paramClass);
    return set.contains(paramSocketOption);
  }
  
  private static void initOptionSets() {
    boolean bool = ExtendedOptionsImpl.flowSupported();
    HashSet hashSet = new HashSet();
    hashSet.add(StandardSocketOptions.SO_KEEPALIVE);
    hashSet.add(StandardSocketOptions.SO_SNDBUF);
    hashSet.add(StandardSocketOptions.SO_RCVBUF);
    hashSet.add(StandardSocketOptions.SO_REUSEADDR);
    hashSet.add(StandardSocketOptions.SO_LINGER);
    hashSet.add(StandardSocketOptions.IP_TOS);
    hashSet.add(StandardSocketOptions.TCP_NODELAY);
    if (bool)
      hashSet.add(ExtendedSocketOptions.SO_FLOW_SLA); 
    Set set = Collections.unmodifiableSet(hashSet);
    options.put(Socket.class, set);
    set = new HashSet();
    set.add(StandardSocketOptions.SO_RCVBUF);
    set.add(StandardSocketOptions.SO_REUSEADDR);
    set.add(StandardSocketOptions.IP_TOS);
    set = Collections.unmodifiableSet(set);
    options.put(ServerSocket.class, set);
    set = new HashSet();
    set.add(StandardSocketOptions.SO_SNDBUF);
    set.add(StandardSocketOptions.SO_RCVBUF);
    set.add(StandardSocketOptions.SO_REUSEADDR);
    set.add(StandardSocketOptions.IP_TOS);
    if (bool)
      set.add(ExtendedSocketOptions.SO_FLOW_SLA); 
    set = Collections.unmodifiableSet(set);
    options.put(DatagramSocket.class, set);
    set = new HashSet();
    set.add(StandardSocketOptions.SO_SNDBUF);
    set.add(StandardSocketOptions.SO_RCVBUF);
    set.add(StandardSocketOptions.SO_REUSEADDR);
    set.add(StandardSocketOptions.IP_TOS);
    set.add(StandardSocketOptions.IP_MULTICAST_IF);
    set.add(StandardSocketOptions.IP_MULTICAST_TTL);
    set.add(StandardSocketOptions.IP_MULTICAST_LOOP);
    if (bool)
      set.add(ExtendedSocketOptions.SO_FLOW_SLA); 
    set = Collections.unmodifiableSet(set);
    options.put(java.net.MulticastSocket.class, set);
  }
  
  static  {
    initOptionSets();
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            Sockets.initMethods();
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\net\Sockets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */