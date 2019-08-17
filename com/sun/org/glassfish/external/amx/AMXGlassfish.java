package com.sun.org.glassfish.external.amx;

import java.io.IOException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public final class AMXGlassfish {
  public static final String DEFAULT_JMX_DOMAIN = "amx";
  
  public static final AMXGlassfish DEFAULT = new AMXGlassfish("amx");
  
  private final String mJMXDomain;
  
  private final ObjectName mDomainRoot;
  
  public AMXGlassfish(String paramString) {
    this.mJMXDomain = paramString;
    this.mDomainRoot = newObjectName("", "domain-root", null);
  }
  
  public static String getGlassfishVersion() { return System.getProperty("glassfish.version"); }
  
  public String amxJMXDomain() { return this.mJMXDomain; }
  
  public String amxSupportDomain() { return amxJMXDomain() + "-support"; }
  
  public String dasName() { return "server"; }
  
  public String dasConfig() { return dasName() + "-config"; }
  
  public ObjectName domainRoot() { return this.mDomainRoot; }
  
  public ObjectName monitoringRoot() { return newObjectName("/", "mon", null); }
  
  public ObjectName serverMon(String paramString) { return newObjectName("/mon", "server-mon", paramString); }
  
  public ObjectName serverMonForDAS() { return serverMon("server"); }
  
  public ObjectName newObjectName(String paramString1, String paramString2, String paramString3) {
    String str = prop("pp", paramString1) + "," + prop("type", paramString2);
    if (paramString3 != null)
      str = str + "," + prop("name", paramString3); 
    return newObjectName(str);
  }
  
  public ObjectName newObjectName(String paramString) {
    String str = paramString;
    if (!str.startsWith(amxJMXDomain()))
      str = amxJMXDomain() + ":" + str; 
    return AMXUtil.newObjectName(str);
  }
  
  private static String prop(String paramString1, String paramString2) { return paramString1 + "=" + paramString2; }
  
  public ObjectName getBootAMXMBeanObjectName() { return AMXUtil.newObjectName(amxSupportDomain() + ":type=boot-amx"); }
  
  public void invokeBootAMX(MBeanServerConnection paramMBeanServerConnection) {
    try {
      paramMBeanServerConnection.invoke(getBootAMXMBeanObjectName(), "bootAMX", null, null);
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException(exception);
    } 
  }
  
  private static void invokeWaitAMXReady(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName) {
    try {
      paramMBeanServerConnection.invoke(paramObjectName, "waitAMXReady", null, null);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public <T extends MBeanListener.Callback> MBeanListener<T> listenForDomainRoot(MBeanServerConnection paramMBeanServerConnection, T paramT) {
    MBeanListener mBeanListener = new MBeanListener(paramMBeanServerConnection, domainRoot(), paramT);
    mBeanListener.startListening();
    return mBeanListener;
  }
  
  public ObjectName waitAMXReady(MBeanServerConnection paramMBeanServerConnection) {
    WaitForDomainRootListenerCallback waitForDomainRootListenerCallback = new WaitForDomainRootListenerCallback(paramMBeanServerConnection);
    listenForDomainRoot(paramMBeanServerConnection, waitForDomainRootListenerCallback);
    waitForDomainRootListenerCallback.await();
    return waitForDomainRootListenerCallback.getRegistered();
  }
  
  public <T extends MBeanListener.Callback> MBeanListener<T> listenForBootAMX(MBeanServerConnection paramMBeanServerConnection, T paramT) {
    MBeanListener mBeanListener = new MBeanListener(paramMBeanServerConnection, getBootAMXMBeanObjectName(), paramT);
    mBeanListener.startListening();
    return mBeanListener;
  }
  
  public ObjectName bootAMX(MBeanServerConnection paramMBeanServerConnection) {
    ObjectName objectName = domainRoot();
    if (!paramMBeanServerConnection.isRegistered(objectName)) {
      BootAMXCallback bootAMXCallback = new BootAMXCallback(paramMBeanServerConnection);
      listenForBootAMX(paramMBeanServerConnection, bootAMXCallback);
      bootAMXCallback.await();
      invokeBootAMX(paramMBeanServerConnection);
      WaitForDomainRootListenerCallback waitForDomainRootListenerCallback = new WaitForDomainRootListenerCallback(paramMBeanServerConnection);
      listenForDomainRoot(paramMBeanServerConnection, waitForDomainRootListenerCallback);
      waitForDomainRootListenerCallback.await();
      invokeWaitAMXReady(paramMBeanServerConnection, objectName);
    } else {
      invokeWaitAMXReady(paramMBeanServerConnection, objectName);
    } 
    return objectName;
  }
  
  public ObjectName bootAMX(MBeanServer paramMBeanServer) {
    try {
      return bootAMX(paramMBeanServer);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public static class BootAMXCallback extends MBeanListener.CallbackImpl {
    private final MBeanServerConnection mConn;
    
    public BootAMXCallback(MBeanServerConnection param1MBeanServerConnection) { this.mConn = param1MBeanServerConnection; }
    
    public void mbeanRegistered(ObjectName param1ObjectName, MBeanListener param1MBeanListener) {
      super.mbeanRegistered(param1ObjectName, param1MBeanListener);
      this.mLatch.countDown();
    }
  }
  
  private static final class WaitForDomainRootListenerCallback extends MBeanListener.CallbackImpl {
    private final MBeanServerConnection mConn;
    
    public WaitForDomainRootListenerCallback(MBeanServerConnection param1MBeanServerConnection) { this.mConn = param1MBeanServerConnection; }
    
    public void mbeanRegistered(ObjectName param1ObjectName, MBeanListener param1MBeanListener) {
      super.mbeanRegistered(param1ObjectName, param1MBeanListener);
      AMXGlassfish.invokeWaitAMXReady(this.mConn, param1ObjectName);
      this.mLatch.countDown();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\amx\AMXGlassfish.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */