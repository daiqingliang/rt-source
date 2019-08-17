package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CWsaClientTube;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionWsaClientTube;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.client.ClientPipelineHook;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.ClientSchemaValidationTube;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.handler.ClientLogicalHandlerTube;
import com.sun.xml.internal.ws.handler.ClientMessageHandlerTube;
import com.sun.xml.internal.ws.handler.ClientSOAPHandlerTube;
import com.sun.xml.internal.ws.protocol.soap.ClientMUTube;
import com.sun.xml.internal.ws.transport.DeferredTransportPipe;
import com.sun.xml.internal.ws.util.pipe.DumpTube;
import java.io.PrintStream;

public class ClientTubeAssemblerContext {
  @NotNull
  private final EndpointAddress address;
  
  @Nullable
  private final WSDLPort wsdlModel;
  
  @Nullable
  private final SEIModel seiModel;
  
  @Nullable
  private final Class sei;
  
  @NotNull
  private final WSService rootOwner;
  
  @NotNull
  private final WSBinding binding;
  
  @NotNull
  private final Container container;
  
  @NotNull
  private Codec codec;
  
  @Nullable
  private final WSBindingProvider bindingProvider;
  
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding) { this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, Container.NONE); }
  
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer) { this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, paramContainer, ((BindingImpl)paramWSBinding).createCodec()); }
  
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec) { this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, paramContainer, paramCodec, null, null); }
  
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec, SEIModel paramSEIModel, Class paramClass) { this(paramEndpointAddress, paramWSDLPort, paramWSService, null, paramWSBinding, paramContainer, paramCodec, paramSEIModel, paramClass); }
  
  public ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @NotNull WSBindingProvider paramWSBindingProvider, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec, SEIModel paramSEIModel, Class paramClass) { this(paramEndpointAddress, paramWSDLPort, (paramWSBindingProvider == null) ? null : paramWSBindingProvider.getPortInfo().getOwner(), paramWSBindingProvider, paramWSBinding, paramContainer, paramCodec, paramSEIModel, paramClass); }
  
  private ClientTubeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @Nullable WSDLPort paramWSDLPort, @Nullable WSService paramWSService, @Nullable WSBindingProvider paramWSBindingProvider, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer, Codec paramCodec, SEIModel paramSEIModel, Class paramClass) {
    this.address = paramEndpointAddress;
    this.wsdlModel = paramWSDLPort;
    this.rootOwner = paramWSService;
    this.bindingProvider = paramWSBindingProvider;
    this.binding = paramWSBinding;
    this.container = paramContainer;
    this.codec = paramCodec;
    this.seiModel = paramSEIModel;
    this.sei = paramClass;
  }
  
  @NotNull
  public EndpointAddress getAddress() { return this.address; }
  
  @Nullable
  public WSDLPort getWsdlModel() { return this.wsdlModel; }
  
  @NotNull
  public WSService getService() { return this.rootOwner; }
  
  @Nullable
  public WSPortInfo getPortInfo() { return (this.bindingProvider == null) ? null : this.bindingProvider.getPortInfo(); }
  
  @Nullable
  public WSBindingProvider getBindingProvider() { return this.bindingProvider; }
  
  @NotNull
  public WSBinding getBinding() { return this.binding; }
  
  @Nullable
  public SEIModel getSEIModel() { return this.seiModel; }
  
  @Nullable
  public Class getSEI() { return this.sei; }
  
  public Container getContainer() { return this.container; }
  
  public Tube createDumpTube(String paramString, PrintStream paramPrintStream, Tube paramTube) { return new DumpTube(paramString, paramPrintStream, paramTube); }
  
  @NotNull
  public Tube createSecurityTube(@NotNull Tube paramTube) {
    ClientPipelineHook clientPipelineHook = (ClientPipelineHook)this.container.getSPI(ClientPipelineHook.class);
    if (clientPipelineHook != null) {
      ClientPipeAssemblerContext clientPipeAssemblerContext = new ClientPipeAssemblerContext(this.address, this.wsdlModel, this.rootOwner, this.binding, this.container);
      return PipeAdapter.adapt(clientPipelineHook.createSecurityPipe(clientPipeAssemblerContext, PipeAdapter.adapt(paramTube)));
    } 
    return paramTube;
  }
  
  public Tube createWsaTube(Tube paramTube) { return (this.binding instanceof javax.xml.ws.soap.SOAPBinding && AddressingVersion.isEnabled(this.binding) && this.wsdlModel != null) ? ((AddressingVersion.fromBinding(this.binding) == AddressingVersion.MEMBER) ? new MemberSubmissionWsaClientTube(this.wsdlModel, this.binding, paramTube) : new W3CWsaClientTube(this.wsdlModel, this.binding, paramTube)) : paramTube; }
  
  public Tube createHandlerTube(Tube paramTube) {
    ClientSOAPHandlerTube clientSOAPHandlerTube = null;
    if (this.binding instanceof javax.xml.ws.soap.SOAPBinding) {
      ClientMessageHandlerTube clientMessageHandlerTube = new ClientMessageHandlerTube(this.seiModel, this.binding, this.wsdlModel, paramTube);
      paramTube = clientSOAPHandlerTube = clientMessageHandlerTube;
      ClientSOAPHandlerTube clientSOAPHandlerTube1 = new ClientSOAPHandlerTube(this.binding, paramTube, clientSOAPHandlerTube);
      paramTube = clientSOAPHandlerTube = clientSOAPHandlerTube1;
    } 
    return new ClientLogicalHandlerTube(this.binding, this.seiModel, paramTube, clientSOAPHandlerTube);
  }
  
  public Tube createClientMUTube(Tube paramTube) { return (this.binding instanceof javax.xml.ws.soap.SOAPBinding) ? new ClientMUTube(this.binding, paramTube) : paramTube; }
  
  public Tube createValidationTube(Tube paramTube) { return (this.binding instanceof javax.xml.ws.soap.SOAPBinding && this.binding.isFeatureEnabled(com.sun.xml.internal.ws.developer.SchemaValidationFeature.class) && this.wsdlModel != null) ? new ClientSchemaValidationTube(this.binding, this.wsdlModel, paramTube) : paramTube; }
  
  public Tube createTransportTube() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    return new DeferredTransportPipe(classLoader, this);
  }
  
  @NotNull
  public Codec getCodec() { return this.codec; }
  
  public void setCodec(@NotNull Codec paramCodec) { this.codec = paramCodec; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\ClientTubeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */