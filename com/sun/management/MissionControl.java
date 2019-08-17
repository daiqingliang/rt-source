package com.sun.management;

import com.sun.jmx.mbeanserver.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public final class MissionControl extends StandardMBean implements MissionControlMXBean {
  private static final ObjectName MBEAN_NAME = Util.newObjectName("com.sun.management:type=MissionControl");
  
  private MBeanServer server;
  
  public MissionControl() { super(MissionControlMXBean.class, true); }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    this.server = paramMBeanServer;
    return MBEAN_NAME;
  }
  
  public void unregisterMBeans() { doPrivileged(new PrivilegedExceptionAction<Void>() {
          public Void run() {
            MissionControl.FlightRecorderHelper.unregisterWithMBeanServer(MissionControl.this.server);
            return null;
          }
        }); }
  
  public void registerMBeans() { doPrivileged(new PrivilegedExceptionAction<Void>() {
          public Void run() {
            if (!MissionControl.this.server.isRegistered(new ObjectName("com.oracle.jrockit:type=FlightRecorder")))
              try {
                MissionControl.FlightRecorderHelper.registerWithMBeanServer(MissionControl.this.server);
              } catch (IllegalStateException illegalStateException) {} 
            return null;
          }
        }); }
  
  private void doPrivileged(PrivilegedExceptionAction<Void> paramPrivilegedExceptionAction) {
    try {
      AccessController.doPrivileged(paramPrivilegedExceptionAction);
    } catch (PrivilegedActionException privilegedActionException) {}
  }
  
  private static class FlightRecorderHelper {
    static final String MBEAN_NAME = "com.oracle.jrockit:type=FlightRecorder";
    
    private static final Class<?> FLIGHTRECORDER_CLASS = getClass("com.oracle.jrockit.jfr.FlightRecorder");
    
    private static final Method REGISTERWITHMBEANSERVER_METHOD = getMethod(FLIGHTRECORDER_CLASS, "registerWithMBeanServer", new Class[] { MBeanServer.class });
    
    private static final Method UNREGISTERWITHMBEANSERVER_METHOD = getMethod(FLIGHTRECORDER_CLASS, "unregisterWithMBeanServer", new Class[] { MBeanServer.class });
    
    private static Class<?> getClass(String param1String) {
      try {
        return Class.forName(param1String, true, FlightRecorderHelper.class.getClassLoader());
      } catch (ClassNotFoundException classNotFoundException) {
        throw new InternalError("jfr.jar missing?", classNotFoundException);
      } 
    }
    
    private static Method getMethod(Class<?> param1Class, String param1String, Class<?>... param1VarArgs) {
      try {
        return param1Class.getMethod(param1String, param1VarArgs);
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new InternalError(noSuchMethodException);
      } 
    }
    
    private static Object invokeStatic(Method param1Method, Object... param1VarArgs) {
      try {
        return param1Method.invoke(null, param1VarArgs);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        if (throwable instanceof Error)
          throw (Error)throwable; 
        throw new InternalError(throwable);
      } catch (IllegalAccessException illegalAccessException) {
        throw new InternalError(illegalAccessException);
      } 
    }
    
    static void registerWithMBeanServer(MBeanServer param1MBeanServer) { invokeStatic(REGISTERWITHMBEANSERVER_METHOD, new Object[] { param1MBeanServer }); }
    
    static void unregisterWithMBeanServer(MBeanServer param1MBeanServer) { invokeStatic(UNREGISTERWITHMBEANSERVER_METHOD, new Object[] { param1MBeanServer }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\MissionControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */