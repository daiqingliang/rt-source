package com.sun.org.glassfish.gmbal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class ManagedObjectManagerNOPImpl implements ManagedObjectManager {
  static final ManagedObjectManager self = new ManagedObjectManagerNOPImpl();
  
  private static final GmbalMBean gmb = new GmbalMBeanNOPImpl();
  
  public void suspendJMXRegistration() {}
  
  public void resumeJMXRegistration() {}
  
  public boolean isManagedObject(Object paramObject) { return false; }
  
  public GmbalMBean createRoot() { return gmb; }
  
  public GmbalMBean createRoot(Object paramObject) { return gmb; }
  
  public GmbalMBean createRoot(Object paramObject, String paramString) { return gmb; }
  
  public Object getRoot() { return null; }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2, String paramString) { return gmb; }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2) { return gmb; }
  
  public GmbalMBean registerAtRoot(Object paramObject, String paramString) { return gmb; }
  
  public GmbalMBean registerAtRoot(Object paramObject) { return gmb; }
  
  public void unregister(Object paramObject) {}
  
  public ObjectName getObjectName(Object paramObject) { return null; }
  
  public Object getObject(ObjectName paramObjectName) { return null; }
  
  public void stripPrefix(String... paramVarArgs) {}
  
  public String getDomain() { return null; }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  public MBeanServer getMBeanServer() { return null; }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle) {}
  
  public ResourceBundle getResourceBundle() { return null; }
  
  public void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation) {}
  
  public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel paramRegistrationDebugLevel) {}
  
  public void setRuntimeDebug(boolean paramBoolean) {}
  
  public String dumpSkeleton(Object paramObject) { return ""; }
  
  public void close() {}
  
  public void setTypelibDebug(int paramInt) {}
  
  public void stripPackagePrefix() {}
  
  public void suppressDuplicateRootReport(boolean paramBoolean) {}
  
  public AMXClient getAMXClient(Object paramObject) { return null; }
  
  public void setJMXRegistrationDebug(boolean paramBoolean) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmbal\ManagedObjectManagerNOPImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */