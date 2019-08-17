package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.EPRSDDocumentFilter;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.EndpointAwareCodec;
import com.sun.xml.internal.ws.api.server.EndpointComponent;
import com.sun.xml.internal.ws.api.server.EndpointReferenceExtensionContributor;
import com.sun.xml.internal.ws.api.server.LazyMOMProvider;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.resources.HandlerMessages;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import org.w3c.dom.Element;

public class WSEndpointImpl<T> extends WSEndpoint<T> implements LazyMOMProvider.WSEndpointScopeChangeListener {
  private static final Logger logger;
  
  @NotNull
  private final QName serviceName;
  
  @NotNull
  private final QName portName;
  
  protected final WSBinding binding;
  
  private final SEIModel seiModel;
  
  @NotNull
  private final Container container;
  
  private final WSDLPort port;
  
  protected final Tube masterTubeline;
  
  private final ServiceDefinitionImpl serviceDef;
  
  private final SOAPVersion soapVersion;
  
  private final Engine engine;
  
  @NotNull
  private final Codec masterCodec;
  
  @NotNull
  private final PolicyMap endpointPolicy;
  
  private final Pool<Tube> tubePool;
  
  private final OperationDispatcher operationDispatcher;
  
  @NotNull
  private ManagedObjectManager managedObjectManager;
  
  private boolean managedObjectManagerClosed = false;
  
  private final Object managedObjectManagerLock = new Object();
  
  private LazyMOMProvider.Scope lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
  
  @NotNull
  private final ServerTubeAssemblerContext context;
  
  private Map<QName, WSEndpointReference.EPRExtension> endpointReferenceExtensions = new HashMap();
  
  private boolean disposed;
  
  private final Class<T> implementationClass;
  
  @NotNull
  private final WSDLProperties wsdlProperties;
  
  private final Set<Component> componentRegistry = new CopyOnWriteArraySet();
  
  private static final Logger monitoringLogger = (logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint")).getLogger("com.sun.xml.internal.ws.monitoring");
  
  protected WSEndpointImpl(@NotNull QName paramQName1, @NotNull QName paramQName2, WSBinding paramWSBinding, Container paramContainer, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Class<T> paramClass, @Nullable ServiceDefinitionImpl paramServiceDefinitionImpl, EndpointAwareTube paramEndpointAwareTube, boolean paramBoolean, PolicyMap paramPolicyMap) {
    this.serviceName = paramQName1;
    this.portName = paramQName2;
    this.binding = paramWSBinding;
    this.soapVersion = paramWSBinding.getSOAPVersion();
    this.container = paramContainer;
    this.port = paramWSDLPort;
    this.implementationClass = paramClass;
    this.serviceDef = paramServiceDefinitionImpl;
    this.seiModel = paramSEIModel;
    this.endpointPolicy = paramPolicyMap;
    LazyMOMProvider.INSTANCE.registerEndpoint(this);
    initManagedObjectManager();
    if (paramServiceDefinitionImpl != null)
      paramServiceDefinitionImpl.setOwner(this); 
    ComponentFeature componentFeature = (ComponentFeature)paramWSBinding.getFeature(ComponentFeature.class);
    if (componentFeature != null)
      switch (componentFeature.getTarget()) {
        case GLASSFISH_NO_JMX:
          this.componentRegistry.add(componentFeature.getComponent());
          break;
        case null:
          paramContainer.getComponents().add(componentFeature.getComponent());
          break;
        default:
          throw new IllegalArgumentException();
      }  
    ComponentsFeature componentsFeature = (ComponentsFeature)paramWSBinding.getFeature(ComponentsFeature.class);
    if (componentsFeature != null)
      for (ComponentFeature componentFeature1 : componentsFeature.getComponentFeatures()) {
        switch (componentFeature1.getTarget()) {
          case GLASSFISH_NO_JMX:
            this.componentRegistry.add(componentFeature1.getComponent());
            continue;
          case null:
            paramContainer.getComponents().add(componentFeature1.getComponent());
            continue;
        } 
        throw new IllegalArgumentException();
      }  
    TubelineAssembler tubelineAssembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), paramWSBinding.getBindingId(), paramContainer);
    assert tubelineAssembler != null;
    this.operationDispatcher = (paramWSDLPort == null) ? null : new OperationDispatcher(paramWSDLPort, paramWSBinding, paramSEIModel);
    this.context = createServerTubeAssemblerContext(paramEndpointAwareTube, paramBoolean);
    this.masterTubeline = tubelineAssembler.createServer(this.context);
    Codec codec = this.context.getCodec();
    if (codec instanceof EndpointAwareCodec) {
      codec = codec.copy();
      ((EndpointAwareCodec)codec).setEndpoint(this);
    } 
    this.masterCodec = codec;
    this.tubePool = new Pool.TubePool(this.masterTubeline);
    paramEndpointAwareTube.setEndpoint(this);
    this.engine = new Engine(toString(), paramContainer);
    this.wsdlProperties = (paramWSDLPort == null) ? new WSDLDirectProperties(paramQName1, paramQName2, paramSEIModel) : new WSDLPortProperties(paramWSDLPort, paramSEIModel);
    HashMap hashMap = new HashMap();
    try {
      if (paramWSDLPort != null) {
        WSEndpointReference wSEndpointReference = paramWSDLPort.getEPR();
        if (wSEndpointReference != null)
          for (WSEndpointReference.EPRExtension ePRExtension : wSEndpointReference.getEPRExtensions())
            hashMap.put(ePRExtension.getQName(), ePRExtension);  
      } 
      EndpointReferenceExtensionContributor[] arrayOfEndpointReferenceExtensionContributor = (EndpointReferenceExtensionContributor[])ServiceFinder.find(EndpointReferenceExtensionContributor.class).toArray();
      for (EndpointReferenceExtensionContributor endpointReferenceExtensionContributor : arrayOfEndpointReferenceExtensionContributor) {
        WSEndpointReference.EPRExtension ePRExtension1 = (WSEndpointReference.EPRExtension)hashMap.remove(endpointReferenceExtensionContributor.getQName());
        WSEndpointReference.EPRExtension ePRExtension2 = endpointReferenceExtensionContributor.getEPRExtension(this, ePRExtension1);
        if (ePRExtension2 != null)
          hashMap.put(ePRExtension2.getQName(), ePRExtension2); 
      } 
      for (WSEndpointReference.EPRExtension ePRExtension : hashMap.values())
        this.endpointReferenceExtensions.put(ePRExtension.getQName(), new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(ePRExtension.readAsXMLStreamReader()), ePRExtension.getQName())); 
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
    if (!hashMap.isEmpty())
      paramServiceDefinitionImpl.addFilter(new EPRSDDocumentFilter(this)); 
  }
  
  protected ServerTubeAssemblerContext createServerTubeAssemblerContext(EndpointAwareTube paramEndpointAwareTube, boolean paramBoolean) { return new ServerPipeAssemblerContext(this.seiModel, this.port, this, paramEndpointAwareTube, paramBoolean); }
  
  protected WSEndpointImpl(@NotNull QName paramQName1, @NotNull QName paramQName2, WSBinding paramWSBinding, Container paramContainer, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube) {
    this.serviceName = paramQName1;
    this.portName = paramQName2;
    this.binding = paramWSBinding;
    this.soapVersion = paramWSBinding.getSOAPVersion();
    this.container = paramContainer;
    this.endpointPolicy = null;
    this.port = paramWSDLPort;
    this.seiModel = paramSEIModel;
    this.serviceDef = null;
    this.implementationClass = null;
    this.masterTubeline = paramTube;
    this.masterCodec = ((BindingImpl)this.binding).createCodec();
    LazyMOMProvider.INSTANCE.registerEndpoint(this);
    initManagedObjectManager();
    this.operationDispatcher = (paramWSDLPort == null) ? null : new OperationDispatcher(paramWSDLPort, paramWSBinding, paramSEIModel);
    this.context = new ServerPipeAssemblerContext(paramSEIModel, paramWSDLPort, this, null, false);
    this.tubePool = new Pool.TubePool(paramTube);
    this.engine = new Engine(toString(), paramContainer);
    this.wsdlProperties = (paramWSDLPort == null) ? new WSDLDirectProperties(paramQName1, paramQName2, paramSEIModel) : new WSDLPortProperties(paramWSDLPort, paramSEIModel);
  }
  
  public Collection<WSEndpointReference.EPRExtension> getEndpointReferenceExtensions() { return this.endpointReferenceExtensions.values(); }
  
  @Nullable
  public OperationDispatcher getOperationDispatcher() { return this.operationDispatcher; }
  
  public PolicyMap getPolicyMap() { return this.endpointPolicy; }
  
  @NotNull
  public Class<T> getImplementationClass() { return this.implementationClass; }
  
  @NotNull
  public WSBinding getBinding() { return this.binding; }
  
  @NotNull
  public Container getContainer() { return this.container; }
  
  public WSDLPort getPort() { return this.port; }
  
  @Nullable
  public SEIModel getSEIModel() { return this.seiModel; }
  
  public void setExecutor(Executor paramExecutor) { this.engine.setExecutor(paramExecutor); }
  
  public Engine getEngine() { return this.engine; }
  
  public void schedule(Packet paramPacket, WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor) { processAsync(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor, true); }
  
  private void processAsync(final Packet request, final WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor, boolean paramBoolean) {
    container1 = ContainerResolver.getDefault().enterContainer(this.container);
    try {
      paramPacket.endpoint = this;
      paramPacket.addSatellite(this.wsdlProperties);
      Fiber fiber = this.engine.createFiber();
      fiber.setDeliverThrowableInPacket(true);
      if (paramFiberContextSwitchInterceptor != null)
        fiber.addInterceptor(paramFiberContextSwitchInterceptor); 
      final Tube tube = (Tube)this.tubePool.take();
      Fiber.CompletionCallback completionCallback = new Fiber.CompletionCallback() {
          public void onCompletion(@NotNull Packet param1Packet) {
            ThrowableContainerPropertySet throwableContainerPropertySet = (ThrowableContainerPropertySet)param1Packet.getSatellite(ThrowableContainerPropertySet.class);
            if (throwableContainerPropertySet == null)
              WSEndpointImpl.this.tubePool.recycle(tube); 
            if (callback != null) {
              if (throwableContainerPropertySet != null)
                param1Packet = WSEndpointImpl.this.createServiceResponseForException(throwableContainerPropertySet, param1Packet, WSEndpointImpl.this.soapVersion, this.val$request.endpoint.getPort(), null, this.val$request.endpoint.getBinding()); 
              callback.onCompletion(param1Packet);
            } 
          }
          
          public void onCompletion(@NotNull Throwable param1Throwable) { throw new IllegalStateException(); }
        };
      fiber.start(tube, paramPacket, completionCallback, (this.binding.isFeatureEnabled(com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature.class) || !paramBoolean));
    } finally {
      ContainerResolver.getDefault().exitContainer(container1);
    } 
  }
  
  public Packet createServiceResponseForException(ThrowableContainerPropertySet paramThrowableContainerPropertySet, Packet paramPacket, SOAPVersion paramSOAPVersion, WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding) {
    if (paramThrowableContainerPropertySet.isFaultCreated())
      return paramPacket; 
    Message message = SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, null, paramThrowableContainerPropertySet.getThrowable());
    Packet packet = paramPacket.createServerResponse(message, paramWSDLPort, paramSEIModel, paramWSBinding);
    paramThrowableContainerPropertySet.setFaultMessage(message);
    paramThrowableContainerPropertySet.setResponsePacket(paramPacket);
    paramThrowableContainerPropertySet.setFaultCreated(true);
    return packet;
  }
  
  public void process(Packet paramPacket, WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor) { processAsync(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor, false); }
  
  @NotNull
  public WSEndpoint.PipeHead createPipeHead() { return new WSEndpoint.PipeHead() {
        private final Tube tube = TubeCloner.clone(WSEndpointImpl.this.masterTubeline);
        
        @NotNull
        public Packet process(Packet param1Packet, WebServiceContextDelegate param1WebServiceContextDelegate, TransportBackChannel param1TransportBackChannel) {
          container = ContainerResolver.getDefault().enterContainer(WSEndpointImpl.this.container);
          try {
            Packet packet;
            param1Packet.webServiceContextDelegate = param1WebServiceContextDelegate;
            param1Packet.transportBackChannel = param1TransportBackChannel;
            param1Packet.endpoint = WSEndpointImpl.this;
            param1Packet.addSatellite(WSEndpointImpl.this.wsdlProperties);
            Fiber fiber = WSEndpointImpl.this.engine.createFiber();
            try {
              packet = fiber.runSync(this.tube, param1Packet);
            } catch (RuntimeException runtimeException) {
              Message message = SOAPFaultBuilder.createSOAPFaultMessage(WSEndpointImpl.this.soapVersion, null, runtimeException);
              packet = param1Packet.createServerResponse(message, param1Packet.endpoint.getPort(), null, param1Packet.endpoint.getBinding());
            } 
            return packet;
          } finally {
            ContainerResolver.getDefault().exitContainer(container);
          } 
        }
      }; }
  
  public void dispose() {
    if (this.disposed)
      return; 
    this.disposed = true;
    this.masterTubeline.preDestroy();
    for (Handler handler : this.binding.getHandlerChain()) {
      Method[] arrayOfMethod = handler.getClass().getMethods();
      int i = arrayOfMethod.length;
      byte b = 0;
      while (b < i) {
        Method method = arrayOfMethod[b];
        if (method.getAnnotation(javax.annotation.PreDestroy.class) == null) {
          b++;
          continue;
        } 
        try {
          method.invoke(handler, new Object[0]);
          break;
        } catch (Exception exception) {
          logger.log(Level.WARNING, HandlerMessages.HANDLER_PREDESTROY_IGNORE(exception.getMessage()), exception);
          break;
        } 
      } 
    } 
    closeManagedObjectManager();
    LazyMOMProvider.INSTANCE.unregisterEndpoint(this);
  }
  
  public ServiceDefinitionImpl getServiceDefinition() { return this.serviceDef; }
  
  public Set<EndpointComponent> getComponentRegistry() {
    EndpointComponentSet endpointComponentSet = new EndpointComponentSet(null);
    for (Component component : this.componentRegistry)
      endpointComponentSet.add((component instanceof EndpointComponentWrapper) ? ((EndpointComponentWrapper)component).component : new ComponentWrapper(component)); 
    return endpointComponentSet;
  }
  
  @NotNull
  public Set<Component> getComponents() { return this.componentRegistry; }
  
  public <T extends javax.xml.ws.EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, Element... paramVarArgs) {
    List list = null;
    if (paramVarArgs != null)
      list = Arrays.asList(paramVarArgs); 
    return (T)getEndpointReference(paramClass, paramString1, paramString2, null, list);
  }
  
  public <T extends javax.xml.ws.EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, List<Element> paramList1, List<Element> paramList2) {
    QName qName = null;
    if (this.port != null)
      qName = this.port.getBinding().getPortTypeName(); 
    AddressingVersion addressingVersion = AddressingVersion.fromSpecClass(paramClass);
    return (T)(new WSEndpointReference(addressingVersion, paramString1, this.serviceName, this.portName, qName, paramList1, paramString2, paramList2, this.endpointReferenceExtensions.values(), null)).toSpec(paramClass);
  }
  
  @NotNull
  public QName getPortName() { return this.portName; }
  
  @NotNull
  public Codec createCodec() { return this.masterCodec.copy(); }
  
  @NotNull
  public QName getServiceName() { return this.serviceName; }
  
  private void initManagedObjectManager() {
    synchronized (this.managedObjectManagerLock) {
      if (this.managedObjectManager == null)
        switch (this.lazyMOMProviderScope) {
          case GLASSFISH_NO_JMX:
            this.managedObjectManager = new WSEndpointMOMProxy(this);
            break;
          default:
            this.managedObjectManager = obtainManagedObjectManager();
            break;
        }  
    } 
  }
  
  @NotNull
  public ManagedObjectManager getManagedObjectManager() { return this.managedObjectManager; }
  
  @NotNull
  ManagedObjectManager obtainManagedObjectManager() {
    MonitorRootService monitorRootService = new MonitorRootService(this);
    ManagedObjectManager managedObjectManager1 = monitorRootService.createManagedObjectManager(this);
    managedObjectManager1.resumeJMXRegistration();
    return managedObjectManager1;
  }
  
  public void scopeChanged(LazyMOMProvider.Scope paramScope) {
    synchronized (this.managedObjectManagerLock) {
      if (this.managedObjectManagerClosed)
        return; 
      this.lazyMOMProviderScope = paramScope;
      if (this.managedObjectManager == null) {
        if (paramScope != LazyMOMProvider.Scope.GLASSFISH_NO_JMX) {
          this.managedObjectManager = obtainManagedObjectManager();
        } else {
          this.managedObjectManager = new WSEndpointMOMProxy(this);
        } 
      } else if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
        ((WSEndpointMOMProxy)this.managedObjectManager).setManagedObjectManager(obtainManagedObjectManager());
      } 
    } 
  }
  
  public void closeManagedObjectManager() {
    synchronized (this.managedObjectManagerLock) {
      if (this.managedObjectManagerClosed == true)
        return; 
      if (this.managedObjectManager != null) {
        boolean bool = true;
        if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized())
          bool = false; 
        if (bool)
          try {
            ObjectName objectName = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
            if (objectName != null)
              monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", objectName); 
            this.managedObjectManager.close();
          } catch (IOException iOException) {
            monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", iOException);
          }  
      } 
      this.managedObjectManagerClosed = true;
    } 
  }
  
  @NotNull
  public ServerTubeAssemblerContext getAssemblerContext() { return this.context; }
  
  private static class ComponentWrapper implements EndpointComponent {
    private final Component component;
    
    public ComponentWrapper(Component param1Component) { this.component = param1Component; }
    
    public <S> S getSPI(Class<S> param1Class) { return (S)this.component.getSPI(param1Class); }
    
    public int hashCode() { return this.component.hashCode(); }
    
    public boolean equals(Object param1Object) { return this.component.equals(param1Object); }
  }
  
  private class EndpointComponentSet extends HashSet<EndpointComponent> {
    private EndpointComponentSet() {}
    
    public Iterator<EndpointComponent> iterator() {
      final Iterator it = super.iterator();
      return new Iterator<EndpointComponent>() {
          private EndpointComponent last = null;
          
          public boolean hasNext() { return it.hasNext(); }
          
          public EndpointComponent next() {
            this.last = (EndpointComponent)it.next();
            return this.last;
          }
          
          public void remove() {
            it.remove();
            if (this.last != null)
              WSEndpointImpl.EndpointComponentSet.this.this$0.componentRegistry.remove((this.last instanceof WSEndpointImpl.ComponentWrapper) ? ((WSEndpointImpl.ComponentWrapper)this.last).component : new WSEndpointImpl.EndpointComponentWrapper(this.last)); 
            this.last = null;
          }
        };
    }
    
    public boolean add(EndpointComponent param1EndpointComponent) {
      boolean bool = super.add(param1EndpointComponent);
      if (bool)
        WSEndpointImpl.this.componentRegistry.add(new WSEndpointImpl.EndpointComponentWrapper(param1EndpointComponent)); 
      return bool;
    }
    
    public boolean remove(Object param1Object) {
      boolean bool = super.remove(param1Object);
      if (bool)
        WSEndpointImpl.this.componentRegistry.remove((param1Object instanceof WSEndpointImpl.ComponentWrapper) ? ((WSEndpointImpl.ComponentWrapper)param1Object).component : new WSEndpointImpl.EndpointComponentWrapper((EndpointComponent)param1Object)); 
      return bool;
    }
  }
  
  private static class EndpointComponentWrapper implements Component {
    private final EndpointComponent component;
    
    public EndpointComponentWrapper(EndpointComponent param1EndpointComponent) { this.component = param1EndpointComponent; }
    
    public <S> S getSPI(Class<S> param1Class) { return (S)this.component.getSPI(param1Class); }
    
    public int hashCode() { return this.component.hashCode(); }
    
    public boolean equals(Object param1Object) { return this.component.equals(param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\WSEndpointImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */