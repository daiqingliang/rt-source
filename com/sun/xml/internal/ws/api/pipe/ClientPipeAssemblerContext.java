package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import java.io.PrintStream;

public final class ClientPipeAssemblerContext extends ClientTubeAssemblerContext {
  public ClientPipeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @NotNull WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding) { this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, Container.NONE); }
  
  public ClientPipeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @NotNull WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer) { super(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, paramContainer); }
  
  public Pipe createDumpPipe(String paramString, PrintStream paramPrintStream, Pipe paramPipe) { return PipeAdapter.adapt(createDumpTube(paramString, paramPrintStream, PipeAdapter.adapt(paramPipe))); }
  
  public Pipe createWsaPipe(Pipe paramPipe) { return PipeAdapter.adapt(createWsaTube(PipeAdapter.adapt(paramPipe))); }
  
  public Pipe createClientMUPipe(Pipe paramPipe) { return PipeAdapter.adapt(createClientMUTube(PipeAdapter.adapt(paramPipe))); }
  
  public Pipe createValidationPipe(Pipe paramPipe) { return PipeAdapter.adapt(createValidationTube(PipeAdapter.adapt(paramPipe))); }
  
  public Pipe createHandlerPipe(Pipe paramPipe) { return PipeAdapter.adapt(createHandlerTube(PipeAdapter.adapt(paramPipe))); }
  
  @NotNull
  public Pipe createSecurityPipe(@NotNull Pipe paramPipe) { return PipeAdapter.adapt(createSecurityTube(PipeAdapter.adapt(paramPipe))); }
  
  public Pipe createTransportPipe() { return PipeAdapter.adapt(createTransportTube()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\ClientPipeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */