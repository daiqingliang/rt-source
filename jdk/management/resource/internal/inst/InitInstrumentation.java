package jdk.management.resource.internal.inst;

import java.security.AccessController;
import java.security.Permission;
import java.util.Arrays;
import java.util.PropertyPermission;
import jdk.internal.instrumentation.Logger;
import jdk.internal.instrumentation.Tracer;
import sun.security.action.GetPropertyAction;

public final class InitInstrumentation implements Runnable {
  static final Class<?>[] hooks;
  
  public void run() {
    if (!this.initialized) {
      try {
        Tracer tracer = Tracer.getInstance();
        tracer.addInstrumentations(Arrays.asList(hooks), TestLogger.tlogger);
      } catch (ClassNotFoundException classNotFoundException) {
        TestLogger.tlogger.error("Unable to load class: " + classNotFoundException.getMessage(), classNotFoundException);
      } catch (Exception exception) {
        TestLogger.tlogger.error("Unable to load class: " + exception.getMessage(), exception);
      } 
      this.initialized = true;
    } 
  }
  
  static  {
    Class[] arrayOfClass2;
    Class[] arrayOfClass1 = { 
        AbstractInterruptibleChannelRMHooks.class, AbstractPlainDatagramSocketImplRMHooks.class, AbstractPlainSocketImplRMHooks.class, AsynchronousServerSocketChannelImplRMHooks.class, AsynchronousSocketChannelImplRMHooks.class, BaseSSLSocketImplRMHooks.class, DatagramChannelImplRMHooks.class, DatagramDispatcherRMHooks.class, DatagramSocketRMHooks.class, FileChannelImplRMHooks.class, 
        FileInputStreamRMHooks.class, FileOutputStreamRMHooks.class, NetRMHooks.class, RandomAccessFileRMHooks.class, ServerSocketRMHooks.class, ServerSocketChannelImplRMHooks.class, SocketChannelImplRMHooks.class, SocketDispatcherRMHooks.class, SocketInputStreamRMHooks.class, SocketOutputStreamRMHooks.class, 
        SocketRMHooks.class, SSLSocketImplRMHooks.class, SSLServerSocketImplRMHooks.class, ThreadRMHooks.class, WrapInstrumentationRMHooks.class };
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"), null, new Permission[] { new PropertyPermission("os.name", "read") });
    if (str.startsWith("Windows")) {
      arrayOfClass2 = new Class[] { WindowsAsynchronousFileChannelImplRMHooks.class, WindowsAsynchronousServerSocketChannelImplRMHooks.class, WindowsAsynchronousSocketChannelImplRMHooks.class };
    } else {
      arrayOfClass2 = new Class[] { SimpleAsynchronousFileChannelImplRMHooks.class, UnixAsynchronousServerSocketChannelImplRMHooks.class, UnixAsynchronousSocketChannelImplRMHooks.class };
    } 
    hooks = new Class[arrayOfClass1.length + arrayOfClass2.length];
    System.arraycopy(arrayOfClass1, 0, hooks, 0, arrayOfClass1.length);
    System.arraycopy(arrayOfClass2, 0, hooks, arrayOfClass1.length, arrayOfClass2.length);
  }
  
  static class TestLogger implements Logger {
    static final TestLogger tlogger = new TestLogger();
    
    public void debug(String param1String) { System.out.printf("TestLogger debug: %s%n", new Object[] { param1String }); }
    
    public void error(String param1String) { System.out.printf("TestLogger error: %s%n", new Object[] { param1String }); }
    
    public void error(String param1String, Throwable param1Throwable) {
      System.out.printf("TestLogger error: %s, ex: %s%n", new Object[] { param1String, param1Throwable });
      param1Throwable.printStackTrace();
    }
    
    public void info(String param1String) { System.out.printf("TestLogger info: %s%n", new Object[] { param1String }); }
    
    public void trace(String param1String) {}
    
    public void warn(String param1String) { System.out.printf("TestLogger warning: %s%n", new Object[] { param1String }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\InitInstrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */