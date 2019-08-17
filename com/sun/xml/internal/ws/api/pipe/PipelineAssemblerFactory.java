package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.pipe.StandalonePipeAssembler;
import java.util.logging.Logger;

public abstract class PipelineAssemblerFactory {
  private static final Logger logger = Logger.getLogger(PipelineAssemblerFactory.class.getName());
  
  public abstract PipelineAssembler doCreate(BindingID paramBindingID);
  
  public static PipelineAssembler create(ClassLoader paramClassLoader, BindingID paramBindingID) {
    for (PipelineAssemblerFactory pipelineAssemblerFactory : ServiceFinder.find(PipelineAssemblerFactory.class, paramClassLoader)) {
      PipelineAssembler pipelineAssembler = pipelineAssemblerFactory.doCreate(paramBindingID);
      if (pipelineAssembler != null) {
        logger.fine(pipelineAssemblerFactory.getClass() + " successfully created " + pipelineAssembler);
        return pipelineAssembler;
      } 
    } 
    return new StandalonePipeAssembler();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\PipelineAssemblerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */