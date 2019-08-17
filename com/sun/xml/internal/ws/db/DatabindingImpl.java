package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.sei.StubAsyncHandler;
import com.sun.xml.internal.ws.client.sei.StubHandler;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.server.sei.TieHandler;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.xml.internal.ws.wsdl.writer.WSDLGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public final class DatabindingImpl implements Databinding {
  AbstractSEIModelImpl seiModel;
  
  Map<Method, StubHandler> stubHandlers;
  
  Map<JavaMethodImpl, TieHandler> wsdlOpMap = new HashMap();
  
  Map<Method, TieHandler> tieHandlers = new HashMap();
  
  OperationDispatcher operationDispatcher;
  
  OperationDispatcher operationDispatcherNoWsdl;
  
  boolean clientConfig = false;
  
  Codec codec;
  
  MessageContextFactory packetFactory = null;
  
  public DatabindingImpl(DatabindingProviderImpl paramDatabindingProviderImpl, DatabindingConfig paramDatabindingConfig) {
    RuntimeModeler runtimeModeler = new RuntimeModeler(paramDatabindingConfig);
    runtimeModeler.setClassLoader(paramDatabindingConfig.getClassLoader());
    this.seiModel = runtimeModeler.buildRuntimeModel();
    WSDLPort wSDLPort = paramDatabindingConfig.getWsdlPort();
    this.packetFactory = new MessageContextFactory(this.seiModel.getWSBinding().getFeatures());
    this.clientConfig = isClientConfig(paramDatabindingConfig);
    if (this.clientConfig)
      initStubHandlers(); 
    this.seiModel.setDatabinding(this);
    if (wSDLPort != null)
      freeze(wSDLPort); 
    if (this.operationDispatcher == null)
      this.operationDispatcherNoWsdl = new OperationDispatcher(null, this.seiModel.getWSBinding(), this.seiModel); 
    for (JavaMethodImpl javaMethodImpl : this.seiModel.getJavaMethods()) {
      if (!javaMethodImpl.isAsync()) {
        TieHandler tieHandler = new TieHandler(javaMethodImpl, this.seiModel.getWSBinding(), this.packetFactory);
        this.wsdlOpMap.put(javaMethodImpl, tieHandler);
        this.tieHandlers.put(tieHandler.getMethod(), tieHandler);
      } 
    } 
  }
  
  private boolean isClientConfig(DatabindingConfig paramDatabindingConfig) { return (paramDatabindingConfig.getContractClass() == null) ? false : (!paramDatabindingConfig.getContractClass().isInterface() ? false : ((paramDatabindingConfig.getEndpointClass() == null || paramDatabindingConfig.getEndpointClass().isInterface()))); }
  
  public void freeze(WSDLPort paramWSDLPort) {
    if (this.clientConfig)
      return; 
    synchronized (this) {
      if (this.operationDispatcher == null)
        this.operationDispatcher = (paramWSDLPort == null) ? null : new OperationDispatcher(paramWSDLPort, this.seiModel.getWSBinding(), this.seiModel); 
    } 
  }
  
  public SEIModel getModel() { return this.seiModel; }
  
  private void initStubHandlers() {
    this.stubHandlers = new HashMap();
    HashMap hashMap = new HashMap();
    for (JavaMethodImpl javaMethodImpl : this.seiModel.getJavaMethods()) {
      if (!(javaMethodImpl.getMEP()).isAsync) {
        StubHandler stubHandler = new StubHandler(javaMethodImpl, this.packetFactory);
        hashMap.put(javaMethodImpl.getOperationSignature(), javaMethodImpl);
        this.stubHandlers.put(javaMethodImpl.getMethod(), stubHandler);
      } 
    } 
    for (JavaMethodImpl javaMethodImpl1 : this.seiModel.getJavaMethods()) {
      JavaMethodImpl javaMethodImpl2 = (JavaMethodImpl)hashMap.get(javaMethodImpl1.getOperationSignature());
      if (javaMethodImpl1.getMEP() == MEP.ASYNC_CALLBACK || javaMethodImpl1.getMEP() == MEP.ASYNC_POLL) {
        Method method = javaMethodImpl1.getMethod();
        StubAsyncHandler stubAsyncHandler = new StubAsyncHandler(javaMethodImpl1, javaMethodImpl2, this.packetFactory);
        this.stubHandlers.put(method, stubAsyncHandler);
      } 
    } 
  }
  
  JavaMethodImpl resolveJavaMethod(Packet paramPacket) throws DispatchException {
    WSDLOperationMapping wSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (wSDLOperationMapping == null)
      synchronized (this) {
        wSDLOperationMapping = (this.operationDispatcher != null) ? this.operationDispatcher.getWSDLOperationMapping(paramPacket) : this.operationDispatcherNoWsdl.getWSDLOperationMapping(paramPacket);
      }  
    return (JavaMethodImpl)wSDLOperationMapping.getJavaMethod();
  }
  
  public JavaCallInfo deserializeRequest(Packet paramPacket) {
    JavaCallInfo javaCallInfo = new JavaCallInfo();
    try {
      JavaMethodImpl javaMethodImpl = resolveJavaMethod(paramPacket);
      TieHandler tieHandler = (TieHandler)this.wsdlOpMap.get(javaMethodImpl);
      javaCallInfo.setMethod(tieHandler.getMethod());
      Object[] arrayOfObject = tieHandler.readRequest(paramPacket.getMessage());
      javaCallInfo.setParameters(arrayOfObject);
    } catch (DispatchException dispatchException) {
      javaCallInfo.setException(dispatchException);
    } 
    return javaCallInfo;
  }
  
  public JavaCallInfo deserializeResponse(Packet paramPacket, JavaCallInfo paramJavaCallInfo) {
    StubHandler stubHandler = (StubHandler)this.stubHandlers.get(paramJavaCallInfo.getMethod());
    try {
      return stubHandler.readResponse(paramPacket, paramJavaCallInfo);
    } catch (Throwable throwable) {
      paramJavaCallInfo.setException(throwable);
      return paramJavaCallInfo;
    } 
  }
  
  public WebServiceFeature[] getFeatures() { return null; }
  
  public Packet serializeRequest(JavaCallInfo paramJavaCallInfo) {
    StubHandler stubHandler = (StubHandler)this.stubHandlers.get(paramJavaCallInfo.getMethod());
    Packet packet = stubHandler.createRequestPacket(paramJavaCallInfo);
    packet.setState(Packet.State.ClientRequest);
    return packet;
  }
  
  public Packet serializeResponse(JavaCallInfo paramJavaCallInfo) {
    Method method = paramJavaCallInfo.getMethod();
    Message message = null;
    if (method != null) {
      TieHandler tieHandler = (TieHandler)this.tieHandlers.get(method);
      if (tieHandler != null)
        return tieHandler.serializeResponse(paramJavaCallInfo); 
    } 
    if (paramJavaCallInfo.getException() instanceof DispatchException)
      message = ((DispatchException)paramJavaCallInfo.getException()).fault; 
    Packet packet = (Packet)this.packetFactory.createContext(message);
    packet.setState(Packet.State.ServerResponse);
    return packet;
  }
  
  public ClientCallBridge getClientBridge(Method paramMethod) { return (ClientCallBridge)this.stubHandlers.get(paramMethod); }
  
  public void generateWSDL(WSDLGenInfo paramWSDLGenInfo) {
    WSDLGenerator wSDLGenerator = new WSDLGenerator(this.seiModel, paramWSDLGenInfo.getWsdlResolver(), this.seiModel.getWSBinding(), paramWSDLGenInfo.getContainer(), this.seiModel.getEndpointClass(), paramWSDLGenInfo.isInlineSchemas(), paramWSDLGenInfo.isSecureXmlProcessingDisabled(), paramWSDLGenInfo.getExtensions());
    wSDLGenerator.doGeneration();
  }
  
  public EndpointCallBridge getEndpointBridge(Packet paramPacket) throws DispatchException {
    JavaMethodImpl javaMethodImpl = resolveJavaMethod(paramPacket);
    return (EndpointCallBridge)this.wsdlOpMap.get(javaMethodImpl);
  }
  
  Codec getCodec() {
    if (this.codec == null)
      this.codec = ((BindingImpl)this.seiModel.getWSBinding()).createCodec(); 
    return this.codec;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException { return getCodec().encode(paramPacket, paramOutputStream); }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException { getCodec().decode(paramInputStream, paramString, paramPacket); }
  
  public JavaCallInfo createJavaCallInfo(Method paramMethod, Object[] paramArrayOfObject) { return new JavaCallInfo(paramMethod, paramArrayOfObject); }
  
  public JavaCallInfo deserializeResponse(MessageContext paramMessageContext, JavaCallInfo paramJavaCallInfo) { return deserializeResponse((Packet)paramMessageContext, paramJavaCallInfo); }
  
  public JavaCallInfo deserializeRequest(MessageContext paramMessageContext) { return deserializeRequest((Packet)paramMessageContext); }
  
  public MessageContextFactory getMessageContextFactory() { return this.packetFactory; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\db\DatabindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */