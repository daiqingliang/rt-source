package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.assembler.MetroTubelineAssembler;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TubelineAssemblerFactory {
  private static final Logger logger = Logger.getLogger(TubelineAssemblerFactory.class.getName());
  
  public abstract TubelineAssembler doCreate(BindingID paramBindingID);
  
  public static TubelineAssembler create(ClassLoader paramClassLoader, BindingID paramBindingID) { return create(paramClassLoader, paramBindingID, null); }
  
  public static TubelineAssembler create(ClassLoader paramClassLoader, BindingID paramBindingID, @Nullable Container paramContainer) {
    if (paramContainer != null) {
      TubelineAssemblerFactory tubelineAssemblerFactory = (TubelineAssemblerFactory)paramContainer.getSPI(TubelineAssemblerFactory.class);
      if (tubelineAssemblerFactory != null) {
        TubelineAssembler tubelineAssembler = tubelineAssemblerFactory.doCreate(paramBindingID);
        if (tubelineAssembler != null)
          return tubelineAssembler; 
      } 
    } 
    for (TubelineAssemblerFactory tubelineAssemblerFactory : ServiceFinder.find(TubelineAssemblerFactory.class, paramClassLoader)) {
      TubelineAssembler tubelineAssembler = tubelineAssemblerFactory.doCreate(paramBindingID);
      if (tubelineAssembler != null) {
        logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { tubelineAssemblerFactory.getClass(), tubelineAssembler });
        return tubelineAssembler;
      } 
    } 
    for (PipelineAssemblerFactory pipelineAssemblerFactory : ServiceFinder.find(PipelineAssemblerFactory.class, paramClassLoader)) {
      PipelineAssembler pipelineAssembler = pipelineAssemblerFactory.doCreate(paramBindingID);
      if (pipelineAssembler != null) {
        logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { pipelineAssemblerFactory.getClass(), pipelineAssembler });
        return new TubelineAssemblerAdapter(pipelineAssembler);
      } 
    } 
    return new MetroTubelineAssembler(paramBindingID, MetroTubelineAssembler.JAXWS_TUBES_CONFIG_NAMES);
  }
  
  private static class TubelineAssemblerAdapter implements TubelineAssembler {
    private PipelineAssembler assembler;
    
    TubelineAssemblerAdapter(PipelineAssembler param1PipelineAssembler) { this.assembler = param1PipelineAssembler; }
    
    @NotNull
    public Tube createClient(@NotNull ClientTubeAssemblerContext param1ClientTubeAssemblerContext) {
      ClientPipeAssemblerContext clientPipeAssemblerContext = new ClientPipeAssemblerContext(param1ClientTubeAssemblerContext.getAddress(), param1ClientTubeAssemblerContext.getWsdlModel(), param1ClientTubeAssemblerContext.getService(), param1ClientTubeAssemblerContext.getBinding(), param1ClientTubeAssemblerContext.getContainer());
      return PipeAdapter.adapt(this.assembler.createClient(clientPipeAssemblerContext));
    }
    
    @NotNull
    public Tube createServer(@NotNull ServerTubeAssemblerContext param1ServerTubeAssemblerContext) {
      if (!(param1ServerTubeAssemblerContext instanceof ServerPipeAssemblerContext))
        throw new IllegalArgumentException("{0} is not instance of ServerPipeAssemblerContext"); 
      return PipeAdapter.adapt(this.assembler.createServer((ServerPipeAssemblerContext)param1ServerTubeAssemblerContext));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\TubelineAssemblerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */