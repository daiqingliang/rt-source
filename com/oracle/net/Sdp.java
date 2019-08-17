package com.oracle.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.sdp.SdpSupport;
import sun.nio.ch.Secrets;

public final class Sdp {
  private static final Constructor<ServerSocket> serverSocketCtor;
  
  private static final Constructor<SocketImpl> socketImplCtor;
  
  private static void setAccessible(final AccessibleObject o) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            o.setAccessible(true);
            return null;
          }
        }); }
  
  private static SocketImpl createSocketImpl() {
    try {
      return (SocketImpl)socketImplCtor.newInstance(new Object[0]);
    } catch (InstantiationException instantiationException) {
      throw new AssertionError(instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new AssertionError(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new AssertionError(invocationTargetException);
    } 
  }
  
  public static Socket openSocket() throws IOException {
    SocketImpl socketImpl = createSocketImpl();
    return new SdpSocket(socketImpl);
  }
  
  public static ServerSocket openServerSocket() throws IOException {
    SocketImpl socketImpl = createSocketImpl();
    try {
      return (ServerSocket)serverSocketCtor.newInstance(new Object[] { socketImpl });
    } catch (IllegalAccessException illegalAccessException) {
      throw new AssertionError(illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw new AssertionError(instantiationException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof IOException)
        throw (IOException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      throw new RuntimeException(invocationTargetException);
    } 
  }
  
  public static SocketChannel openSocketChannel() throws IOException {
    FileDescriptor fileDescriptor = SdpSupport.createSocket();
    return Secrets.newSocketChannel(fileDescriptor);
  }
  
  public static ServerSocketChannel openServerSocketChannel() throws IOException {
    FileDescriptor fileDescriptor = SdpSupport.createSocket();
    return Secrets.newServerSocketChannel(fileDescriptor);
  }
  
  static  {
    try {
      serverSocketCtor = ServerSocket.class.getDeclaredConstructor(new Class[] { SocketImpl.class });
      setAccessible(serverSocketCtor);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new AssertionError(noSuchMethodException);
    } 
    try {
      Class clazz = Class.forName("java.net.SdpSocketImpl", true, null);
      socketImplCtor = clazz.getDeclaredConstructor(new Class[0]);
      setAccessible(socketImplCtor);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new AssertionError(classNotFoundException);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new AssertionError(noSuchMethodException);
    } 
  }
  
  private static class SdpSocket extends Socket {
    SdpSocket(SocketImpl param1SocketImpl) throws SocketException { super(param1SocketImpl); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\net\Sdp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */