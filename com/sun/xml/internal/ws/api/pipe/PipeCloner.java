package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

public abstract class PipeCloner extends TubeCloner {
  public static Pipe clone(Pipe paramPipe) { return (new PipeClonerImpl()).copy(paramPipe); }
  
  PipeCloner(Map<Object, Object> paramMap) { super(paramMap); }
  
  public abstract <T extends Pipe> T copy(T paramT);
  
  public abstract void add(Pipe paramPipe1, Pipe paramPipe2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\PipeCloner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */