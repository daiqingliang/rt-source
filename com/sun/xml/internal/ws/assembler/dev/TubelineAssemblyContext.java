package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;

public interface TubelineAssemblyContext {
  Pipe getAdaptedTubelineHead();
  
  <T> T getImplementation(Class<T> paramClass);
  
  Tube getTubelineHead();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\dev\TubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */