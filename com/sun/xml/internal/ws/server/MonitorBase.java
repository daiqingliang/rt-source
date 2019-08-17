package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.external.amx.AMXGlassfish;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.org.glassfish.gmbal.ManagedObjectManagerFactory;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.client.Stub;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;

public abstract class MonitorBase {
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
  
  private static ManagementAssertion.Setting clientMonitoring = ManagementAssertion.Setting.NOT_SET;
  
  private static ManagementAssertion.Setting endpointMonitoring = ManagementAssertion.Setting.NOT_SET;
  
  private static int typelibDebug = -1;
  
  private static String registrationDebug = "NONE";
  
  private static boolean runtimeDebug = false;
  
  private static int maxUniqueEndpointRootNameRetries = 100;
  
  private static final String monitorProperty = "com.sun.xml.internal.ws.monitoring.";
  
  @NotNull
  public ManagedObjectManager createManagedObjectManager(WSEndpoint paramWSEndpoint) {
    String str1 = paramWSEndpoint.getServiceName().getLocalPart() + "-" + paramWSEndpoint.getPortName().getLocalPart();
    if (str1.equals("-"))
      str1 = "provider"; 
    String str2 = getContextPath(paramWSEndpoint);
    if (str2 != null)
      str1 = str2 + "-" + str1; 
    ManagedServiceAssertion managedServiceAssertion = ManagedServiceAssertion.getAssertion(paramWSEndpoint);
    if (managedServiceAssertion != null) {
      String str = managedServiceAssertion.getId();
      if (str != null)
        str1 = str; 
      if (managedServiceAssertion.monitoringAttribute() == ManagementAssertion.Setting.OFF)
        return disabled("This endpoint", str1); 
    } 
    return endpointMonitoring.equals(ManagementAssertion.Setting.OFF) ? disabled("Global endpoint", str1) : createMOMLoop(str1, 0);
  }
  
  private String getContextPath(WSEndpoint paramWSEndpoint) {
    try {
      Container container = paramWSEndpoint.getContainer();
      Method method = container.getClass().getDeclaredMethod("getSPI", new Class[] { Class.class });
      method.setAccessible(true);
      Class clazz = Class.forName("javax.servlet.ServletContext");
      Object object = method.invoke(container, new Object[] { clazz });
      if (object != null) {
        Method method1 = clazz.getDeclaredMethod("getContextPath", new Class[0]);
        method1.setAccessible(true);
        return (String)method1.invoke(object, new Object[0]);
      } 
      return null;
    } catch (Throwable throwable) {
      logger.log(Level.FINEST, "getContextPath", throwable);
      return null;
    } 
  }
  
  @NotNull
  public ManagedObjectManager createManagedObjectManager(Stub paramStub) {
    EndpointAddress endpointAddress = paramStub.requestContext.getEndpointAddress();
    if (endpointAddress == null)
      return ManagedObjectManagerFactory.createNOOP(); 
    String str = endpointAddress.toString();
    ManagedClientAssertion managedClientAssertion = ManagedClientAssertion.getAssertion(paramStub.getPortInfo());
    if (managedClientAssertion != null) {
      String str1 = managedClientAssertion.getId();
      if (str1 != null)
        str = str1; 
      if (managedClientAssertion.monitoringAttribute() == ManagementAssertion.Setting.OFF)
        return disabled("This client", str); 
      if (managedClientAssertion.monitoringAttribute() == ManagementAssertion.Setting.ON && clientMonitoring != ManagementAssertion.Setting.OFF)
        return createMOMLoop(str, 0); 
    } 
    return (clientMonitoring == ManagementAssertion.Setting.NOT_SET || clientMonitoring == ManagementAssertion.Setting.OFF) ? disabled("Global client", str) : createMOMLoop(str, 0);
  }
  
  @NotNull
  private ManagedObjectManager disabled(String paramString1, String paramString2) {
    String str = paramString1 + " monitoring disabled. " + paramString2 + " will not be monitored";
    logger.log(Level.CONFIG, str);
    return ManagedObjectManagerFactory.createNOOP();
  }
  
  @NotNull
  private ManagedObjectManager createMOMLoop(String paramString, int paramInt) {
    boolean bool = (AMXGlassfish.getGlassfishVersion() != null);
    null = createMOM(bool);
    null = initMOM(null);
    return createRoot(null, paramString, paramInt);
  }
  
  @NotNull
  private ManagedObjectManager createMOM(boolean paramBoolean) {
    try {
      return new RewritingMOM(paramBoolean ? ManagedObjectManagerFactory.createFederated(AMXGlassfish.DEFAULT.serverMon(AMXGlassfish.DEFAULT.dasName())) : ManagedObjectManagerFactory.createStandalone("com.sun.metro"));
    } catch (Throwable throwable) {
      if (paramBoolean) {
        logger.log(Level.CONFIG, "Problem while attempting to federate with GlassFish AMX monitoring.  Trying standalone.", throwable);
        return createMOM(false);
      } 
      logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", throwable);
      return ManagedObjectManagerFactory.createNOOP();
    } 
  }
  
  @NotNull
  private ManagedObjectManager initMOM(ManagedObjectManager paramManagedObjectManager) {
    try {
      if (typelibDebug != -1)
        paramManagedObjectManager.setTypelibDebug(typelibDebug); 
      if (registrationDebug.equals("FINE")) {
        paramManagedObjectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.FINE);
      } else if (registrationDebug.equals("NORMAL")) {
        paramManagedObjectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NORMAL);
      } else {
        paramManagedObjectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
      } 
      paramManagedObjectManager.setRuntimeDebug(runtimeDebug);
      paramManagedObjectManager.suppressDuplicateRootReport(true);
      paramManagedObjectManager.stripPrefix(new String[] { "com.sun.xml.internal.ws.server", "com.sun.xml.internal.ws.rx.rm.runtime.sequence" });
      paramManagedObjectManager.addAnnotation(javax.xml.ws.WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(com.sun.org.glassfish.gmbal.ManagedData.class));
      paramManagedObjectManager.addAnnotation(javax.xml.ws.WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(com.sun.org.glassfish.gmbal.Description.class));
      paramManagedObjectManager.addAnnotation(javax.xml.ws.WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(com.sun.org.glassfish.gmbal.InheritedAttributes.class));
      paramManagedObjectManager.suspendJMXRegistration();
    } catch (Throwable throwable) {
      try {
        paramManagedObjectManager.close();
      } catch (IOException iOException) {
        logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", iOException);
      } 
      logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", throwable);
      return ManagedObjectManagerFactory.createNOOP();
    } 
    return paramManagedObjectManager;
  }
  
  private ManagedObjectManager createRoot(ManagedObjectManager paramManagedObjectManager, String paramString, int paramInt) {
    String str = paramString + ((paramInt == 0) ? "" : ("-" + String.valueOf(paramInt)));
    try {
      GmbalMBean gmbalMBean = paramManagedObjectManager.createRoot(this, str);
      if (gmbalMBean != null) {
        ObjectName objectName = paramManagedObjectManager.getObjectName(paramManagedObjectManager.getRoot());
        if (objectName != null)
          logger.log(Level.INFO, "Metro monitoring rootname successfully set to: {0}", objectName); 
        return paramManagedObjectManager;
      } 
      try {
        paramManagedObjectManager.close();
      } catch (IOException iOException) {
        logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", iOException);
      } 
      String str1 = "Duplicate Metro monitoring rootname: " + str + " : ";
      if (paramInt > maxUniqueEndpointRootNameRetries) {
        String str3 = str1 + "Giving up.";
        logger.log(Level.INFO, str3);
        return ManagedObjectManagerFactory.createNOOP();
      } 
      String str2 = str1 + "Will try to make unique";
      logger.log(Level.CONFIG, str2);
      return createMOMLoop(paramString, ++paramInt);
    } catch (Throwable throwable) {
      logger.log(Level.WARNING, "Error while creating monitoring root with name: " + paramString, throwable);
      return ManagedObjectManagerFactory.createNOOP();
    } 
  }
  
  private static ManagementAssertion.Setting propertyToSetting(String paramString) {
    String str = System.getProperty(paramString);
    if (str == null)
      return ManagementAssertion.Setting.NOT_SET; 
    str = str.toLowerCase();
    return (str.equals("false") || str.equals("off")) ? ManagementAssertion.Setting.OFF : ((str.equals("true") || str.equals("on")) ? ManagementAssertion.Setting.ON : ManagementAssertion.Setting.NOT_SET);
  }
  
  static  {
    try {
      endpointMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.endpoint");
      clientMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.client");
      Integer integer = Integer.getInteger("com.sun.xml.internal.ws.monitoring.typelibDebug");
      if (integer != null)
        typelibDebug = integer.intValue(); 
      String str = System.getProperty("com.sun.xml.internal.ws.monitoring.registrationDebug");
      if (str != null)
        registrationDebug = str.toUpperCase(); 
      str = System.getProperty("com.sun.xml.internal.ws.monitoring.runtimeDebug");
      if (str != null && str.toLowerCase().equals("true"))
        runtimeDebug = true; 
      integer = Integer.getInteger("com.sun.xml.internal.ws.monitoring.maxUniqueEndpointRootNameRetries");
      if (integer != null)
        maxUniqueEndpointRootNameRetries = integer.intValue(); 
    } catch (Exception exception) {
      logger.log(Level.WARNING, "Error while reading monitoring properties", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\MonitorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */