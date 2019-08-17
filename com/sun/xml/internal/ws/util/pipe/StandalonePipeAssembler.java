package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipelineAssembler;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;

public class StandalonePipeAssembler implements PipelineAssembler {
  private static final boolean dump;
  
  @NotNull
  public Pipe createClient(ClientPipeAssemblerContext paramClientPipeAssemblerContext) {
    Pipe pipe = paramClientPipeAssemblerContext.createTransportPipe();
    pipe = paramClientPipeAssemblerContext.createSecurityPipe(pipe);
    if (dump)
      pipe = paramClientPipeAssemblerContext.createDumpPipe("client", System.out, pipe); 
    pipe = paramClientPipeAssemblerContext.createWsaPipe(pipe);
    pipe = paramClientPipeAssemblerContext.createClientMUPipe(pipe);
    return paramClientPipeAssemblerContext.createHandlerPipe(pipe);
  }
  
  public Pipe createServer(ServerPipeAssemblerContext paramServerPipeAssemblerContext) {
    null = paramServerPipeAssemblerContext.getTerminalPipe();
    null = paramServerPipeAssemblerContext.createHandlerPipe(null);
    null = paramServerPipeAssemblerContext.createMonitoringPipe(null);
    null = paramServerPipeAssemblerContext.createServerMUPipe(null);
    null = paramServerPipeAssemblerContext.createWsaPipe(null);
    return paramServerPipeAssemblerContext.createSecurityPipe(null);
  }
  
  static  {
    boolean bool = false;
    try {
      bool = Boolean.getBoolean(StandalonePipeAssembler.class.getName() + ".dump");
    } catch (Throwable throwable) {}
    dump = bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\pipe\StandalonePipeAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */