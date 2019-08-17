package com.sun.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;

public abstract class InvokerTube<T extends Invoker> extends AbstractTubeImpl implements InvokerSource<T> {
  protected final T invoker;
  
  protected InvokerTube(T paramT) { this.invoker = paramT; }
  
  protected InvokerTube(InvokerTube<T> paramInvokerTube, TubeCloner paramTubeCloner) {
    paramTubeCloner.add(paramInvokerTube, this);
    this.invoker = paramInvokerTube.invoker;
  }
  
  @NotNull
  public T getInvoker(Packet paramPacket) { return (T)this.invoker; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\InvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */