package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Tube;
import java.util.ArrayList;
import java.util.Collection;

public class TubelineAssemblyDecorator {
  public static TubelineAssemblyDecorator composite(Iterable<TubelineAssemblyDecorator> paramIterable) { return new CompositeTubelineAssemblyDecorator(paramIterable); }
  
  public Tube decorateClient(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) { return paramTube; }
  
  public Tube decorateClientHead(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) { return paramTube; }
  
  public Tube decorateClientTail(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) { return paramTube; }
  
  public Tube decorateServer(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext) { return paramTube; }
  
  public Tube decorateServerTail(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext) { return paramTube; }
  
  public Tube decorateServerHead(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext) { return paramTube; }
  
  private static class CompositeTubelineAssemblyDecorator extends TubelineAssemblyDecorator {
    private Collection<TubelineAssemblyDecorator> decorators = new ArrayList();
    
    public CompositeTubelineAssemblyDecorator(Iterable<TubelineAssemblyDecorator> param1Iterable) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : param1Iterable)
        this.decorators.add(tubelineAssemblyDecorator); 
    }
    
    public Tube decorateClient(Tube param1Tube, ClientTubelineAssemblyContext param1ClientTubelineAssemblyContext) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : this.decorators)
        param1Tube = tubelineAssemblyDecorator.decorateClient(param1Tube, param1ClientTubelineAssemblyContext); 
      return param1Tube;
    }
    
    public Tube decorateClientHead(Tube param1Tube, ClientTubelineAssemblyContext param1ClientTubelineAssemblyContext) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : this.decorators)
        param1Tube = tubelineAssemblyDecorator.decorateClientHead(param1Tube, param1ClientTubelineAssemblyContext); 
      return param1Tube;
    }
    
    public Tube decorateClientTail(Tube param1Tube, ClientTubelineAssemblyContext param1ClientTubelineAssemblyContext) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : this.decorators)
        param1Tube = tubelineAssemblyDecorator.decorateClientTail(param1Tube, param1ClientTubelineAssemblyContext); 
      return param1Tube;
    }
    
    public Tube decorateServer(Tube param1Tube, ServerTubelineAssemblyContext param1ServerTubelineAssemblyContext) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : this.decorators)
        param1Tube = tubelineAssemblyDecorator.decorateServer(param1Tube, param1ServerTubelineAssemblyContext); 
      return param1Tube;
    }
    
    public Tube decorateServerTail(Tube param1Tube, ServerTubelineAssemblyContext param1ServerTubelineAssemblyContext) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : this.decorators)
        param1Tube = tubelineAssemblyDecorator.decorateServerTail(param1Tube, param1ServerTubelineAssemblyContext); 
      return param1Tube;
    }
    
    public Tube decorateServerHead(Tube param1Tube, ServerTubelineAssemblyContext param1ServerTubelineAssemblyContext) {
      for (TubelineAssemblyDecorator tubelineAssemblyDecorator : this.decorators)
        param1Tube = tubelineAssemblyDecorator.decorateServerHead(param1Tube, param1ServerTubelineAssemblyContext); 
      return param1Tube;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\dev\TubelineAssemblyDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */