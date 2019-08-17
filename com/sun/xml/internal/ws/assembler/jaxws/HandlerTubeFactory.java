package com.sun.xml.internal.ws.assembler.jaxws;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import javax.xml.ws.WebServiceException;

public final class HandlerTubeFactory implements TubeFactory {
  public Tube createTube(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) throws WebServiceException { return paramClientTubelineAssemblyContext.getWrappedContext().createHandlerTube(paramClientTubelineAssemblyContext.getTubelineHead()); }
  
  public Tube createTube(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext) throws WebServiceException { return paramServerTubelineAssemblyContext.getWrappedContext().createHandlerTube(paramServerTubelineAssemblyContext.getTubelineHead()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\jaxws\HandlerTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */