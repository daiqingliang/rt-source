package com.sun.xml.internal.ws.server;

import com.sun.org.glassfish.gmbal.AMXClient;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class RewritingMOM implements ManagedObjectManager {
  private final ManagedObjectManager mom;
  
  private static final String gmbalQuotingCharsRegex = "\n|\\|\"|\\*|\\?|:|=|,";
  
  private static final String replacementChar = "-";
  
  RewritingMOM(ManagedObjectManager paramManagedObjectManager) { this.mom = paramManagedObjectManager; }
  
  private String rewrite(String paramString) { return paramString.replaceAll("\n|\\|\"|\\*|\\?|:|=|,", "-"); }
  
  public void suspendJMXRegistration() { this.mom.suspendJMXRegistration(); }
  
  public void resumeJMXRegistration() { this.mom.resumeJMXRegistration(); }
  
  public GmbalMBean createRoot() { return this.mom.createRoot(); }
  
  public GmbalMBean createRoot(Object paramObject) { return this.mom.createRoot(paramObject); }
  
  public GmbalMBean createRoot(Object paramObject, String paramString) { return this.mom.createRoot(paramObject, rewrite(paramString)); }
  
  public Object getRoot() { return this.mom.getRoot(); }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2, String paramString) { return this.mom.register(paramObject1, paramObject2, rewrite(paramString)); }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2) { return this.mom.register(paramObject1, paramObject2); }
  
  public GmbalMBean registerAtRoot(Object paramObject, String paramString) { return this.mom.registerAtRoot(paramObject, rewrite(paramString)); }
  
  public GmbalMBean registerAtRoot(Object paramObject) { return this.mom.registerAtRoot(paramObject); }
  
  public void unregister(Object paramObject) { this.mom.unregister(paramObject); }
  
  public ObjectName getObjectName(Object paramObject) { return this.mom.getObjectName(paramObject); }
  
  public AMXClient getAMXClient(Object paramObject) { return this.mom.getAMXClient(paramObject); }
  
  public Object getObject(ObjectName paramObjectName) { return this.mom.getObject(paramObjectName); }
  
  public void stripPrefix(String... paramVarArgs) { this.mom.stripPrefix(paramVarArgs); }
  
  public void stripPackagePrefix() { this.mom.stripPackagePrefix(); }
  
  public String getDomain() { return this.mom.getDomain(); }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) { this.mom.setMBeanServer(paramMBeanServer); }
  
  public MBeanServer getMBeanServer() { return this.mom.getMBeanServer(); }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle) { this.mom.setResourceBundle(paramResourceBundle); }
  
  public ResourceBundle getResourceBundle() { return this.mom.getResourceBundle(); }
  
  public void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation) { this.mom.addAnnotation(paramAnnotatedElement, paramAnnotation); }
  
  public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel paramRegistrationDebugLevel) { this.mom.setRegistrationDebug(paramRegistrationDebugLevel); }
  
  public void setRuntimeDebug(boolean paramBoolean) { this.mom.setRuntimeDebug(paramBoolean); }
  
  public void setTypelibDebug(int paramInt) { this.mom.setTypelibDebug(paramInt); }
  
  public String dumpSkeleton(Object paramObject) { return this.mom.dumpSkeleton(paramObject); }
  
  public void suppressDuplicateRootReport(boolean paramBoolean) { this.mom.suppressDuplicateRootReport(paramBoolean); }
  
  public void close() { this.mom.close(); }
  
  public void setJMXRegistrationDebug(boolean paramBoolean) { this.mom.setJMXRegistrationDebug(paramBoolean); }
  
  public boolean isManagedObject(Object paramObject) { return this.mom.isManagedObject(paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\RewritingMOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */