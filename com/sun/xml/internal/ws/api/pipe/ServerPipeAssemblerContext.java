package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.io.PrintStream;

public final class ServerPipeAssemblerContext extends ServerTubeAssemblerContext {
  public ServerPipeAssemblerContext(@Nullable SEIModel paramSEIModel, @Nullable WSDLPort paramWSDLPort, @NotNull WSEndpoint paramWSEndpoint, @NotNull Tube paramTube, boolean paramBoolean) { super(paramSEIModel, paramWSDLPort, paramWSEndpoint, paramTube, paramBoolean); }
  
  @NotNull
  public Pipe createServerMUPipe(@NotNull Pipe paramPipe) { return PipeAdapter.adapt(createServerMUTube(PipeAdapter.adapt(paramPipe))); }
  
  public Pipe createDumpPipe(String paramString, PrintStream paramPrintStream, Pipe paramPipe) { return PipeAdapter.adapt(createDumpTube(paramString, paramPrintStream, PipeAdapter.adapt(paramPipe))); }
  
  @NotNull
  public Pipe createMonitoringPipe(@NotNull Pipe paramPipe) { return PipeAdapter.adapt(createMonitoringTube(PipeAdapter.adapt(paramPipe))); }
  
  @NotNull
  public Pipe createSecurityPipe(@NotNull Pipe paramPipe) { return PipeAdapter.adapt(createSecurityTube(PipeAdapter.adapt(paramPipe))); }
  
  @NotNull
  public Pipe createValidationPipe(@NotNull Pipe paramPipe) { return PipeAdapter.adapt(createValidationTube(PipeAdapter.adapt(paramPipe))); }
  
  @NotNull
  public Pipe createHandlerPipe(@NotNull Pipe paramPipe) { return PipeAdapter.adapt(createHandlerTube(PipeAdapter.adapt(paramPipe))); }
  
  @NotNull
  public Pipe getTerminalPipe() { return PipeAdapter.adapt(getTerminalTube()); }
  
  public Pipe createWsaPipe(Pipe paramPipe) { return PipeAdapter.adapt(createWsaTube(PipeAdapter.adapt(paramPipe))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\ServerPipeAssemblerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */