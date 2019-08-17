package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptorFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.xml.ws.Binding;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public abstract class Stub implements WSBindingProvider, ResponseContextReceiver, ComponentRegistry {
  public static final String PREVENT_SYNC_START_FOR_ASYNC_INVOKE = "com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke";
  
  private Pool<Tube> tubes;
  
  private final Engine engine;
  
  protected final WSServiceDelegate owner;
  
  @Nullable
  protected WSEndpointReference endpointReference;
  
  protected final BindingImpl binding;
  
  protected final WSPortInfo portInfo;
  
  protected AddressingVersion addrVersion;
  
  public RequestContext requestContext = new RequestContext();
  
  private final RequestContext cleanRequestContext;
  
  private ResponseContext responseContext;
  
  @Nullable
  protected final WSDLPort wsdlPort;
  
  protected QName portname;
  
  @NotNull
  private final WSDLProperties wsdlProperties;
  
  protected OperationDispatcher operationDispatcher = null;
  
  @NotNull
  private final ManagedObjectManager managedObjectManager;
  
  private boolean managedObjectManagerClosed = false;
  
  private final Set<Component> components = new CopyOnWriteArraySet();
  
  private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
  
  @Deprecated
  protected Stub(WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSDLPort paramWSDLPort, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference) { this(paramWSServiceDelegate, paramTube, null, null, paramBindingImpl, paramWSDLPort, paramEndpointAddress, paramWSEndpointReference); }
  
  @Deprecated
  protected Stub(QName paramQName, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSDLPort paramWSDLPort, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference) { this(paramWSServiceDelegate, paramTube, null, paramQName, paramBindingImpl, paramWSDLPort, paramEndpointAddress, paramWSEndpointReference); }
  
  protected Stub(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, Tube paramTube, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference) { this((WSServiceDelegate)paramWSPortInfo.getOwner(), paramTube, paramWSPortInfo, null, paramBindingImpl, paramWSPortInfo.getPort(), paramEndpointAddress, paramWSEndpointReference); }
  
  protected Stub(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference) { this(paramWSPortInfo, paramBindingImpl, null, paramEndpointAddress, paramWSEndpointReference); }
  
  private Stub(WSServiceDelegate paramWSServiceDelegate, @Nullable Tube paramTube, @Nullable WSPortInfo paramWSPortInfo, QName paramQName, BindingImpl paramBindingImpl, @Nullable WSDLPort paramWSDLPort, EndpointAddress paramEndpointAddress, @Nullable WSEndpointReference paramWSEndpointReference) {
    container = ContainerResolver.getDefault().enterContainer(paramWSServiceDelegate.getContainer());
    try {
      this.owner = paramWSServiceDelegate;
      this.portInfo = paramWSPortInfo;
      this.wsdlPort = (paramWSDLPort != null) ? paramWSDLPort : ((paramWSPortInfo != null) ? paramWSPortInfo.getPort() : null);
      this.portname = paramQName;
      if (paramQName == null)
        if (paramWSPortInfo != null) {
          this.portname = paramWSPortInfo.getPortName();
        } else if (paramWSDLPort != null) {
          this.portname = paramWSDLPort.getName();
        }  
      this.binding = paramBindingImpl;
      ComponentFeature componentFeature = (ComponentFeature)paramBindingImpl.getFeature(ComponentFeature.class);
      if (componentFeature != null && ComponentFeature.Target.STUB.equals(componentFeature.getTarget()))
        this.components.add(componentFeature.getComponent()); 
      ComponentsFeature componentsFeature = (ComponentsFeature)paramBindingImpl.getFeature(ComponentsFeature.class);
      if (componentsFeature != null)
        for (ComponentFeature componentFeature1 : componentsFeature.getComponentFeatures()) {
          if (ComponentFeature.Target.STUB.equals(componentFeature1.getTarget()))
            this.components.add(componentFeature1.getComponent()); 
        }  
      if (paramWSEndpointReference != null) {
        this.requestContext.setEndPointAddressString(paramWSEndpointReference.getAddress());
      } else {
        this.requestContext.setEndpointAddress(paramEndpointAddress);
      } 
      this.engine = new Engine(getStringId(), paramWSServiceDelegate.getContainer(), paramWSServiceDelegate.getExecutor());
      this.endpointReference = paramWSEndpointReference;
      this.wsdlProperties = (paramWSDLPort == null) ? new WSDLDirectProperties(paramWSServiceDelegate.getServiceName(), paramQName) : new WSDLPortProperties(paramWSDLPort);
      this.cleanRequestContext = this.requestContext.copy();
      this.managedObjectManager = (new MonitorRootClient(this)).createManagedObjectManager(this);
      if (paramTube != null) {
        this.tubes = new Pool.TubePool(paramTube);
      } else {
        this.tubes = new Pool.TubePool(createPipeline(paramWSPortInfo, paramBindingImpl));
      } 
      this.addrVersion = paramBindingImpl.getAddressingVersion();
      this.managedObjectManager.resumeJMXRegistration();
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  private Tube createPipeline(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding) {
    checkAllWSDLExtensionsUnderstood(paramWSPortInfo, paramWSBinding);
    SOAPSEIModel sOAPSEIModel = null;
    Class clazz = null;
    if (paramWSPortInfo instanceof SEIPortInfo) {
      SEIPortInfo sEIPortInfo = (SEIPortInfo)paramWSPortInfo;
      sOAPSEIModel = sEIPortInfo.model;
      clazz = sEIPortInfo.sei;
    } 
    BindingID bindingID = paramWSPortInfo.getBindingId();
    TubelineAssembler tubelineAssembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), bindingID, this.owner.getContainer());
    if (tubelineAssembler == null)
      throw new WebServiceException("Unable to process bindingID=" + bindingID); 
    return tubelineAssembler.createClient(new ClientTubeAssemblerContext(paramWSPortInfo.getEndpointAddress(), paramWSPortInfo.getPort(), this, paramWSBinding, this.owner.getContainer(), ((BindingImpl)paramWSBinding).createCodec(), sOAPSEIModel, clazz));
  }
  
  public WSDLPort getWSDLPort() { return this.wsdlPort; }
  
  public WSService getService() { return this.owner; }
  
  public Pool<Tube> getTubes() { return this.tubes; }
  
  private static void checkAllWSDLExtensionsUnderstood(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding) {
    if (paramWSPortInfo.getPort() != null && paramWSBinding.isFeatureEnabled(javax.xml.ws.RespectBindingFeature.class))
      paramWSPortInfo.getPort().areRequiredExtensionsUnderstood(); 
  }
  
  public WSPortInfo getPortInfo() { return this.portInfo; }
  
  @Nullable
  public OperationDispatcher getOperationDispatcher() {
    if (this.operationDispatcher == null && this.wsdlPort != null)
      this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, null); 
    return this.operationDispatcher;
  }
  
  @NotNull
  protected abstract QName getPortName();
  
  @NotNull
  protected final QName getServiceName() { return this.owner.getServiceName(); }
  
  public final Executor getExecutor() { return this.owner.getExecutor(); }
  
  protected final Packet process(Packet paramPacket, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver) {
    paramPacket.isSynchronousMEP = Boolean.valueOf(true);
    paramPacket.component = this;
    configureRequestPacket(paramPacket, paramRequestContext);
    pool = this.tubes;
    if (pool == null)
      throw new WebServiceException("close method has already been invoked"); 
    fiber = this.engine.createFiber();
    configureFiber(fiber);
    tube = (Tube)pool.take();
    try {
      return fiber.runSync(tube, paramPacket);
    } finally {
      Packet packet = (fiber.getPacket() == null) ? paramPacket : fiber.getPacket();
      paramResponseContextReceiver.setResponseContext(new ResponseContext(packet));
      pool.recycle(tube);
    } 
  }
  
  private void configureRequestPacket(Packet paramPacket, RequestContext paramRequestContext) {
    paramPacket.proxy = this;
    paramPacket.handlerConfig = this.binding.getHandlerConfig();
    Header[] arrayOfHeader = this.userOutboundHeaders;
    if (arrayOfHeader != null) {
      MessageHeaders messageHeaders = paramPacket.getMessage().getHeaders();
      for (Header header : arrayOfHeader)
        messageHeaders.add(header); 
    } 
    paramRequestContext.fill(paramPacket, (this.binding.getAddressingVersion() != null));
    paramPacket.addSatellite(this.wsdlProperties);
    if (this.addrVersion != null) {
      MessageHeaders messageHeaders = paramPacket.getMessage().getHeaders();
      AddressingUtils.fillRequestAddressingHeaders(messageHeaders, this.wsdlPort, this.binding, paramPacket);
      if (this.endpointReference != null)
        this.endpointReference.addReferenceParametersToList(paramPacket.getMessage().getHeaders()); 
    } 
  }
  
  protected final void processAsync(AsyncResponseImpl<?> paramAsyncResponseImpl, Packet paramPacket, RequestContext paramRequestContext, final Fiber.CompletionCallback completionCallback) {
    paramPacket.component = this;
    configureRequestPacket(paramPacket, paramRequestContext);
    final Pool pool = this.tubes;
    if (pool == null)
      throw new WebServiceException("close method has already been invoked"); 
    Fiber fiber = this.engine.createFiber();
    configureFiber(fiber);
    paramAsyncResponseImpl.setCancelable(fiber);
    if (paramAsyncResponseImpl.isCancelled())
      return; 
    FiberContextSwitchInterceptorFactory fiberContextSwitchInterceptorFactory = (FiberContextSwitchInterceptorFactory)this.owner.getSPI(FiberContextSwitchInterceptorFactory.class);
    if (fiberContextSwitchInterceptorFactory != null)
      fiber.addInterceptor(fiberContextSwitchInterceptorFactory.create()); 
    final Tube tube = (Tube)pool.take();
    Fiber.CompletionCallback completionCallback = new Fiber.CompletionCallback() {
        public void onCompletion(@NotNull Packet param1Packet) {
          pool.recycle(tube);
          completionCallback.onCompletion(param1Packet);
        }
        
        public void onCompletion(@NotNull Throwable param1Throwable) { completionCallback.onCompletion(param1Throwable); }
      };
    fiber.start(tube, paramPacket, completionCallback, (getBinding().isFeatureEnabled(com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature.class) && !paramRequestContext.containsKey("com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke")));
  }
  
  protected void configureFiber(Fiber paramFiber) {}
  
  public void close() {
    Pool.TubePool tubePool = (Pool.TubePool)this.tubes;
    if (tubePool != null) {
      Tube tube = tubePool.takeMaster();
      tube.preDestroy();
      this.tubes = null;
    } 
    if (!this.managedObjectManagerClosed) {
      try {
        ObjectName objectName = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
        if (objectName != null)
          monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", objectName); 
        this.managedObjectManager.close();
      } catch (IOException iOException) {
        monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", iOException);
      } 
      this.managedObjectManagerClosed = true;
    } 
  }
  
  public final WSBinding getBinding() { return this.binding; }
  
  public final Map<String, Object> getRequestContext() { return this.requestContext.asMap(); }
  
  public void resetRequestContext() { this.requestContext = this.cleanRequestContext.copy(); }
  
  public final ResponseContext getResponseContext() { return this.responseContext; }
  
  public void setResponseContext(ResponseContext paramResponseContext) { this.responseContext = paramResponseContext; }
  
  private String getStringId() { return RuntimeVersion.VERSION + ": Stub for " + getRequestContext().get("javax.xml.ws.service.endpoint.address"); }
  
  public String toString() { return getStringId(); }
  
  public final WSEndpointReference getWSEndpointReference() {
    if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http"))
      throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference(Class<T> class)", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding")); 
    if (this.endpointReference != null)
      return this.endpointReference; 
    String str1 = this.requestContext.getEndpointAddress().toString();
    QName qName = null;
    String str2 = null;
    ArrayList arrayList = new ArrayList();
    if (this.wsdlPort != null) {
      qName = this.wsdlPort.getBinding().getPortTypeName();
      str2 = str1 + "?wsdl";
      try {
        WSEndpointReference wSEndpointReference = this.wsdlPort.getEPR();
        if (wSEndpointReference != null)
          for (WSEndpointReference.EPRExtension ePRExtension : wSEndpointReference.getEPRExtensions())
            arrayList.add(new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(ePRExtension.readAsXMLStreamReader()), ePRExtension.getQName()));  
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      } 
    } 
    AddressingVersion addressingVersion = AddressingVersion.W3C;
    this.endpointReference = new WSEndpointReference(addressingVersion, str1, getServiceName(), getPortName(), qName, null, str2, null, arrayList, null);
    return this.endpointReference;
  }
  
  public final W3CEndpointReference getEndpointReference() {
    if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http"))
      throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference()", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding")); 
    return (W3CEndpointReference)getEndpointReference(W3CEndpointReference.class);
  }
  
  public final <T extends EndpointReference> T getEndpointReference(Class<T> paramClass) { return (T)getWSEndpointReference().toSpec(paramClass); }
  
  @NotNull
  public ManagedObjectManager getManagedObjectManager() { return this.managedObjectManager; }
  
  public final void setOutboundHeaders(List<Header> paramList) {
    if (paramList == null) {
      this.userOutboundHeaders = null;
    } else {
      for (Header header : paramList) {
        if (header == null)
          throw new IllegalArgumentException(); 
      } 
      this.userOutboundHeaders = (Header[])paramList.toArray(new Header[paramList.size()]);
    } 
  }
  
  public final void setOutboundHeaders(Header... paramVarArgs) {
    if (paramVarArgs == null) {
      this.userOutboundHeaders = null;
    } else {
      for (Header header : paramVarArgs) {
        if (header == null)
          throw new IllegalArgumentException(); 
      } 
      Header[] arrayOfHeader = new Header[paramVarArgs.length];
      System.arraycopy(paramVarArgs, 0, arrayOfHeader, 0, paramVarArgs.length);
      this.userOutboundHeaders = arrayOfHeader;
    } 
  }
  
  public final List<Header> getInboundHeaders() { return Collections.unmodifiableList(((MessageHeaders)this.responseContext.get("com.sun.xml.internal.ws.api.message.HeaderList")).asList()); }
  
  public final void setAddress(String paramString) { this.requestContext.put("javax.xml.ws.service.endpoint.address", paramString); }
  
  public <S> S getSPI(Class<S> paramClass) {
    for (Component component : this.components) {
      Object object = component.getSPI(paramClass);
      if (object != null)
        return (S)object; 
    } 
    return (S)this.owner.getSPI(paramClass);
  }
  
  public Set<Component> getComponents() { return this.components; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */