package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.AMXClient;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public class WSEndpointMOMProxy extends WSEndpoint implements ManagedObjectManager {
  @NotNull
  private final WSEndpointImpl wsEndpoint;
  
  private ManagedObjectManager managedObjectManager;
  
  WSEndpointMOMProxy(@NotNull WSEndpointImpl paramWSEndpointImpl) { this.wsEndpoint = paramWSEndpointImpl; }
  
  public ManagedObjectManager getManagedObjectManager() {
    if (this.managedObjectManager == null)
      this.managedObjectManager = this.wsEndpoint.obtainManagedObjectManager(); 
    return this.managedObjectManager;
  }
  
  void setManagedObjectManager(ManagedObjectManager paramManagedObjectManager) { this.managedObjectManager = paramManagedObjectManager; }
  
  public boolean isInitialized() { return (this.managedObjectManager != null); }
  
  public WSEndpointImpl getWsEndpoint() { return this.wsEndpoint; }
  
  public void suspendJMXRegistration() { getManagedObjectManager().suspendJMXRegistration(); }
  
  public void resumeJMXRegistration() { getManagedObjectManager().resumeJMXRegistration(); }
  
  public boolean isManagedObject(Object paramObject) { return getManagedObjectManager().isManagedObject(paramObject); }
  
  public GmbalMBean createRoot() { return getManagedObjectManager().createRoot(); }
  
  public GmbalMBean createRoot(Object paramObject) { return getManagedObjectManager().createRoot(paramObject); }
  
  public GmbalMBean createRoot(Object paramObject, String paramString) { return getManagedObjectManager().createRoot(paramObject, paramString); }
  
  public Object getRoot() { return getManagedObjectManager().getRoot(); }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2, String paramString) { return getManagedObjectManager().register(paramObject1, paramObject2, paramString); }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2) { return getManagedObjectManager().register(paramObject1, paramObject2); }
  
  public GmbalMBean registerAtRoot(Object paramObject, String paramString) { return getManagedObjectManager().registerAtRoot(paramObject, paramString); }
  
  public GmbalMBean registerAtRoot(Object paramObject) { return getManagedObjectManager().registerAtRoot(paramObject); }
  
  public void unregister(Object paramObject) { getManagedObjectManager().unregister(paramObject); }
  
  public ObjectName getObjectName(Object paramObject) { return getManagedObjectManager().getObjectName(paramObject); }
  
  public AMXClient getAMXClient(Object paramObject) { return getManagedObjectManager().getAMXClient(paramObject); }
  
  public Object getObject(ObjectName paramObjectName) { return getManagedObjectManager().getObject(paramObjectName); }
  
  public void stripPrefix(String... paramVarArgs) { getManagedObjectManager().stripPrefix(paramVarArgs); }
  
  public void stripPackagePrefix() { getManagedObjectManager().stripPackagePrefix(); }
  
  public String getDomain() { return getManagedObjectManager().getDomain(); }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) { getManagedObjectManager().setMBeanServer(paramMBeanServer); }
  
  public MBeanServer getMBeanServer() { return getManagedObjectManager().getMBeanServer(); }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle) { getManagedObjectManager().setResourceBundle(paramResourceBundle); }
  
  public ResourceBundle getResourceBundle() { return getManagedObjectManager().getResourceBundle(); }
  
  public void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation) { getManagedObjectManager().addAnnotation(paramAnnotatedElement, paramAnnotation); }
  
  public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel paramRegistrationDebugLevel) { getManagedObjectManager().setRegistrationDebug(paramRegistrationDebugLevel); }
  
  public void setRuntimeDebug(boolean paramBoolean) { getManagedObjectManager().setRuntimeDebug(paramBoolean); }
  
  public void setTypelibDebug(int paramInt) { getManagedObjectManager().setTypelibDebug(paramInt); }
  
  public void setJMXRegistrationDebug(boolean paramBoolean) { getManagedObjectManager().setJMXRegistrationDebug(paramBoolean); }
  
  public String dumpSkeleton(Object paramObject) { return getManagedObjectManager().dumpSkeleton(paramObject); }
  
  public void suppressDuplicateRootReport(boolean paramBoolean) { getManagedObjectManager().suppressDuplicateRootReport(paramBoolean); }
  
  public void close() { getManagedObjectManager().close(); }
  
  public boolean equalsProxiedInstance(WSEndpoint paramWSEndpoint) { return (this.wsEndpoint == null) ? ((paramWSEndpoint == null)) : this.wsEndpoint.equals(paramWSEndpoint); }
  
  public Codec createCodec() { return this.wsEndpoint.createCodec(); }
  
  public QName getServiceName() { return this.wsEndpoint.getServiceName(); }
  
  public QName getPortName() { return this.wsEndpoint.getPortName(); }
  
  public Class getImplementationClass() { return this.wsEndpoint.getImplementationClass(); }
  
  public WSBinding getBinding() { return this.wsEndpoint.getBinding(); }
  
  public Container getContainer() { return this.wsEndpoint.getContainer(); }
  
  public WSDLPort getPort() { return this.wsEndpoint.getPort(); }
  
  public void setExecutor(Executor paramExecutor) { this.wsEndpoint.setExecutor(paramExecutor); }
  
  public void schedule(Packet paramPacket, WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor) { this.wsEndpoint.schedule(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor); }
  
  public WSEndpoint.PipeHead createPipeHead() { return this.wsEndpoint.createPipeHead(); }
  
  public void dispose() {
    if (this.wsEndpoint != null)
      this.wsEndpoint.dispose(); 
  }
  
  public ServiceDefinition getServiceDefinition() { return this.wsEndpoint.getServiceDefinition(); }
  
  public Set getComponentRegistry() { return this.wsEndpoint.getComponentRegistry(); }
  
  public SEIModel getSEIModel() { return this.wsEndpoint.getSEIModel(); }
  
  public PolicyMap getPolicyMap() { return this.wsEndpoint.getPolicyMap(); }
  
  public void closeManagedObjectManager() { this.wsEndpoint.closeManagedObjectManager(); }
  
  public ServerTubeAssemblerContext getAssemblerContext() { return this.wsEndpoint.getAssemblerContext(); }
  
  public EndpointReference getEndpointReference(Class paramClass, String paramString1, String paramString2, Element... paramVarArgs) { return this.wsEndpoint.getEndpointReference(paramClass, paramString1, paramString2, paramVarArgs); }
  
  public EndpointReference getEndpointReference(Class paramClass, String paramString1, String paramString2, List paramList1, List paramList2) { return this.wsEndpoint.getEndpointReference(paramClass, paramString1, paramString2, paramList1, paramList2); }
  
  public OperationDispatcher getOperationDispatcher() { return this.wsEndpoint.getOperationDispatcher(); }
  
  public Packet createServiceResponseForException(ThrowableContainerPropertySet paramThrowableContainerPropertySet, Packet paramPacket, SOAPVersion paramSOAPVersion, WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding) { return this.wsEndpoint.createServiceResponseForException(paramThrowableContainerPropertySet, paramPacket, paramSOAPVersion, paramWSDLPort, paramSEIModel, paramWSBinding); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\WSEndpointMOMProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */