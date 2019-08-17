package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;

public abstract class AbstractFilterPipeImpl extends AbstractPipeImpl {
  protected final Pipe next;
  
  protected AbstractFilterPipeImpl(Pipe paramPipe) {
    this.next = paramPipe;
    assert paramPipe != null;
  }
  
  protected AbstractFilterPipeImpl(AbstractFilterPipeImpl paramAbstractFilterPipeImpl, PipeCloner paramPipeCloner) {
    super(paramAbstractFilterPipeImpl, paramPipeCloner);
    this.next = paramPipeCloner.copy(paramAbstractFilterPipeImpl.next);
    assert this.next != null;
  }
  
  public Packet process(Packet paramPacket) { return this.next.process(paramPacket); }
  
  public void preDestroy() { this.next.preDestroy(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractFilterPipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */