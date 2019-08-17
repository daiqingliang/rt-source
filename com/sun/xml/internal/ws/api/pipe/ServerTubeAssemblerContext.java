package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CWsaServerTube;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionWsaServerTube;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.ServerPipelineHook;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.handler.ServerLogicalHandlerTube;
import com.sun.xml.internal.ws.handler.ServerMessageHandlerTube;
import com.sun.xml.internal.ws.handler.ServerSOAPHandlerTube;
import com.sun.xml.internal.ws.protocol.soap.ServerMUTube;
import com.sun.xml.internal.ws.server.ServerSchemaValidationTube;
import com.sun.xml.internal.ws.util.pipe.DumpTube;
import java.io.PrintStream;

public class ServerTubeAssemblerContext {
  private final SEIModel seiModel;
  
  private final WSDLPort wsdlModel;
  
  private final WSEndpoint endpoint;
  
  private final BindingImpl binding;
  
  private final Tube terminal;
  
  private final boolean isSynchronous;
  
  @NotNull
  private Codec codec;
  
  public ServerTubeAssemblerContext(@Nullable SEIModel paramSEIModel, @Nullable WSDLPort paramWSDLPort, @NotNull WSEndpoint paramWSEndpoint, @NotNull Tube paramTube, boolean paramBoolean) {
    this.seiModel = paramSEIModel;
    this.wsdlModel = paramWSDLPort;
    this.endpoint = paramWSEndpoint;
    this.terminal = paramTube;
    this.binding = (BindingImpl)paramWSEndpoint.getBinding();
    this.isSynchronous = paramBoolean;
    this.codec = this.binding.createCodec();
  }
  
  @Nullable
  public SEIModel getSEIModel() { return this.seiModel; }
  
  @Nullable
  public WSDLPort getWsdlModel() { return this.wsdlModel; }
  
  @NotNull
  public WSEndpoint<?> getEndpoint() { return this.endpoint; }
  
  @NotNull
  public Tube getTerminalTube() { return this.terminal; }
  
  public boolean isSynchronous() { return this.isSynchronous; }
  
  @NotNull
  public Tube createServerMUTube(@NotNull Tube paramTube) { return (this.binding instanceof javax.xml.ws.soap.SOAPBinding) ? new ServerMUTube(this, paramTube) : paramTube; }
  
  @NotNull
  public Tube createHandlerTube(@NotNull Tube paramTube) {
    if (!this.binding.getHandlerChain().isEmpty()) {
      ServerLogicalHandlerTube serverLogicalHandlerTube = new ServerLogicalHandlerTube(this.binding, this.seiModel, this.wsdlModel, paramTube);
      paramTube = serverLogicalHandlerTube;
      if (this.binding instanceof javax.xml.ws.soap.SOAPBinding) {
        ServerSOAPHandlerTube serverSOAPHandlerTube = new ServerSOAPHandlerTube(this.binding, paramTube, serverLogicalHandlerTube);
        paramTube = serverSOAPHandlerTube;
        paramTube = new ServerMessageHandlerTube(this.seiModel, this.binding, paramTube, serverSOAPHandlerTube);
      } 
    } 
    return paramTube;
  }
  
  @NotNull
  public Tube createMonitoringTube(@NotNull Tube paramTube) {
    ServerPipelineHook serverPipelineHook = (ServerPipelineHook)this.endpoint.getContainer().getSPI(ServerPipelineHook.class);
    if (serverPipelineHook != null) {
      ServerPipeAssemblerContext serverPipeAssemblerContext = new ServerPipeAssemblerContext(this.seiModel, this.wsdlModel, this.endpoint, this.terminal, this.isSynchronous);
      return PipeAdapter.adapt(serverPipelineHook.createMonitoringPipe(serverPipeAssemblerContext, PipeAdapter.adapt(paramTube)));
    } 
    return paramTube;
  }
  
  @NotNull
  public Tube createSecurityTube(@NotNull Tube paramTube) {
    ServerPipelineHook serverPipelineHook = (ServerPipelineHook)this.endpoint.getContainer().getSPI(ServerPipelineHook.class);
    if (serverPipelineHook != null) {
      ServerPipeAssemblerContext serverPipeAssemblerContext = new ServerPipeAssemblerContext(this.seiModel, this.wsdlModel, this.endpoint, this.terminal, this.isSynchronous);
      return PipeAdapter.adapt(serverPipelineHook.createSecurityPipe(serverPipeAssemblerContext, PipeAdapter.adapt(paramTube)));
    } 
    return paramTube;
  }
  
  public Tube createDumpTube(String paramString, PrintStream paramPrintStream, Tube paramTube) { return new DumpTube(paramString, paramPrintStream, paramTube); }
  
  public Tube createValidationTube(Tube paramTube) { return (this.binding instanceof javax.xml.ws.soap.SOAPBinding && this.binding.isFeatureEnabled(com.sun.xml.internal.ws.developer.SchemaValidationFeature.class) && this.wsdlModel != null) ? new ServerSchemaValidationTube(this.endpoint, this.binding, this.seiModel, this.wsdlModel, paramTube) : paramTube; }
  
  public Tube createWsaTube(Tube paramTube) { return (this.binding instanceof javax.xml.ws.soap.SOAPBinding && AddressingVersion.isEnabled(this.binding)) ? ((AddressingVersion.fromBinding(this.binding) == AddressingVersion.MEMBER) ? new MemberSubmissionWsaServerTube(this.endpoint, this.wsdlModel, this.binding, paramTube) : new W3CWsaServerTube(this.endpoint, this.wsdlModel, this.binding, paramTube)) : paramTube; }
  
  @NotNull
  public Codec getCodec() { return this.codec; }
  
  public void setCodec(@NotNull Codec paramCodec) { this.codec = paramCodec; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\ServerTubeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */