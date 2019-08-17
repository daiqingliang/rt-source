package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Tube;
import javax.xml.ws.WebServiceException;

public interface TubeFactory {
  Tube createTube(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) throws WebServiceException;
  
  Tube createTube(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext) throws WebServiceException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\dev\TubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */