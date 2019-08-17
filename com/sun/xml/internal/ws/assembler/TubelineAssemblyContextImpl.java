package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContext;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

class TubelineAssemblyContextImpl implements TubelineAssemblyContext {
  private static final Logger LOGGER = Logger.getLogger(TubelineAssemblyContextImpl.class);
  
  private Tube head;
  
  private Pipe adaptedHead;
  
  private List<Tube> tubes = new LinkedList();
  
  public Tube getTubelineHead() { return this.head; }
  
  public Pipe getAdaptedTubelineHead() {
    if (this.adaptedHead == null)
      this.adaptedHead = PipeAdapter.adapt(this.head); 
    return this.adaptedHead;
  }
  
  boolean setTubelineHead(Tube paramTube) {
    if (paramTube == this.head || paramTube == this.adaptedHead)
      return false; 
    this.head = paramTube;
    this.tubes.add(this.head);
    this.adaptedHead = null;
    if (LOGGER.isLoggable(Level.FINER))
      LOGGER.finer(MessageFormat.format("Added '{0}' tube instance to the tubeline.", new Object[] { (paramTube == null) ? null : paramTube.getClass().getName() })); 
    return true;
  }
  
  public <T> T getImplementation(Class<T> paramClass) {
    for (Tube tube : this.tubes) {
      if (paramClass.isInstance(tube))
        return (T)paramClass.cast(tube); 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\TubelineAssemblyContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */