package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public abstract class AbstractFilterTubeImpl extends AbstractTubeImpl {
  protected final Tube next;
  
  protected AbstractFilterTubeImpl(Tube paramTube) { this.next = paramTube; }
  
  protected AbstractFilterTubeImpl(AbstractFilterTubeImpl paramAbstractFilterTubeImpl, TubeCloner paramTubeCloner) {
    super(paramAbstractFilterTubeImpl, paramTubeCloner);
    if (paramAbstractFilterTubeImpl.next != null) {
      this.next = paramTubeCloner.copy(paramAbstractFilterTubeImpl.next);
    } else {
      this.next = null;
    } 
  }
  
  @NotNull
  public NextAction processRequest(Packet paramPacket) { return doInvoke(this.next, paramPacket); }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket) { return doReturnWith(paramPacket); }
  
  @NotNull
  public NextAction processException(Throwable paramThrowable) { return doThrow(paramThrowable); }
  
  public void preDestroy() {
    if (this.next != null)
      this.next.preDestroy(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractFilterTubeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */