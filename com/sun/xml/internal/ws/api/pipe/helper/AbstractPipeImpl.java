package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;

public abstract class AbstractPipeImpl implements Pipe {
  protected AbstractPipeImpl() {}
  
  protected AbstractPipeImpl(Pipe paramPipe, PipeCloner paramPipeCloner) { paramPipeCloner.add(paramPipe, this); }
  
  public void preDestroy() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractPipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */