package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;

public class StandaloneTubeAssembler implements TubelineAssembler {
  public static final boolean dump;
  
  @NotNull
  public Tube createClient(ClientTubeAssemblerContext paramClientTubeAssemblerContext) {
    Tube tube = paramClientTubeAssemblerContext.createTransportTube();
    tube = paramClientTubeAssemblerContext.createSecurityTube(tube);
    if (dump)
      tube = paramClientTubeAssemblerContext.createDumpTube("client", System.out, tube); 
    tube = paramClientTubeAssemblerContext.createWsaTube(tube);
    tube = paramClientTubeAssemblerContext.createClientMUTube(tube);
    tube = paramClientTubeAssemblerContext.createValidationTube(tube);
    return paramClientTubeAssemblerContext.createHandlerTube(tube);
  }
  
  public Tube createServer(ServerTubeAssemblerContext paramServerTubeAssemblerContext) {
    null = paramServerTubeAssemblerContext.getTerminalTube();
    null = paramServerTubeAssemblerContext.createValidationTube(null);
    null = paramServerTubeAssemblerContext.createHandlerTube(null);
    null = paramServerTubeAssemblerContext.createMonitoringTube(null);
    null = paramServerTubeAssemblerContext.createServerMUTube(null);
    null = paramServerTubeAssemblerContext.createWsaTube(null);
    if (dump)
      null = paramServerTubeAssemblerContext.createDumpTube("server", System.out, null); 
    return paramServerTubeAssemblerContext.createSecurityTube(null);
  }
  
  static  {
    boolean bool = false;
    try {
      bool = Boolean.getBoolean(StandaloneTubeAssembler.class.getName() + ".dump");
    } catch (Throwable throwable) {}
    dump = bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\pipe\StandaloneTubeAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */