package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public abstract class AbstractTubeImpl implements Tube, Pipe {
  protected AbstractTubeImpl() {}
  
  protected AbstractTubeImpl(AbstractTubeImpl paramAbstractTubeImpl, TubeCloner paramTubeCloner) { paramTubeCloner.add(paramAbstractTubeImpl, this); }
  
  protected final NextAction doInvoke(Tube paramTube, Packet paramPacket) {
    NextAction nextAction = new NextAction();
    nextAction.invoke(paramTube, paramPacket);
    return nextAction;
  }
  
  protected final NextAction doInvokeAndForget(Tube paramTube, Packet paramPacket) {
    NextAction nextAction = new NextAction();
    nextAction.invokeAndForget(paramTube, paramPacket);
    return nextAction;
  }
  
  protected final NextAction doReturnWith(Packet paramPacket) {
    NextAction nextAction = new NextAction();
    nextAction.returnWith(paramPacket);
    return nextAction;
  }
  
  protected final NextAction doThrow(Packet paramPacket, Throwable paramThrowable) {
    NextAction nextAction = new NextAction();
    nextAction.throwException(paramPacket, paramThrowable);
    return nextAction;
  }
  
  @Deprecated
  protected final NextAction doSuspend() {
    NextAction nextAction = new NextAction();
    nextAction.suspend();
    return nextAction;
  }
  
  protected final NextAction doSuspend(Runnable paramRunnable) {
    NextAction nextAction = new NextAction();
    nextAction.suspend(paramRunnable);
    return nextAction;
  }
  
  @Deprecated
  protected final NextAction doSuspend(Tube paramTube) {
    NextAction nextAction = new NextAction();
    nextAction.suspend(paramTube);
    return nextAction;
  }
  
  protected final NextAction doSuspend(Tube paramTube, Runnable paramRunnable) {
    NextAction nextAction = new NextAction();
    nextAction.suspend(paramTube, paramRunnable);
    return nextAction;
  }
  
  protected final NextAction doThrow(Throwable paramThrowable) {
    NextAction nextAction = new NextAction();
    nextAction.throwException(paramThrowable);
    return nextAction;
  }
  
  public Packet process(Packet paramPacket) { return Fiber.current().runSync(this, paramPacket); }
  
  public final AbstractTubeImpl copy(PipeCloner paramPipeCloner) { return copy(paramPipeCloner); }
  
  public abstract AbstractTubeImpl copy(TubeCloner paramTubeCloner);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractTubeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */