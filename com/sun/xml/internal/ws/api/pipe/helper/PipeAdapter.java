package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public class PipeAdapter extends AbstractTubeImpl {
  private final Pipe next;
  
  public static Tube adapt(Pipe paramPipe) { return (paramPipe instanceof Tube) ? (Tube)paramPipe : new PipeAdapter(paramPipe); }
  
  public static Pipe adapt(Tube paramTube) {
    class TubeAdapter extends AbstractPipeImpl {
      private final Tube t;
      
      public TubeAdapter(PipeAdapter this$0) { this.t = this$0; }
      
      private TubeAdapter(PipeAdapter this$0, PipeCloner param1PipeCloner) {
        super(this$0, param1PipeCloner);
        this.t = param1PipeCloner.copy(this$0.t);
      }
      
      public Packet process(Packet param1Packet) { return Fiber.current().runSync(this.t, param1Packet); }
      
      public Pipe copy(PipeCloner param1PipeCloner) { return new TubeAdapter(this, param1PipeCloner); }
    };
    return (paramTube instanceof Pipe) ? (Pipe)paramTube : new TubeAdapter(paramTube);
  }
  
  private PipeAdapter(Pipe paramPipe) { this.next = paramPipe; }
  
  private PipeAdapter(PipeAdapter paramPipeAdapter, TubeCloner paramTubeCloner) {
    super(paramPipeAdapter, paramTubeCloner);
    this.next = ((PipeCloner)paramTubeCloner).copy(paramPipeAdapter.next);
  }
  
  @NotNull
  public NextAction processRequest(@NotNull Packet paramPacket) { return doReturnWith(this.next.process(paramPacket)); }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket) { throw new IllegalStateException(); }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable) { throw new IllegalStateException(); }
  
  public void preDestroy() { this.next.preDestroy(); }
  
  public PipeAdapter copy(TubeCloner paramTubeCloner) { return new PipeAdapter(this, paramTubeCloner); }
  
  public String toString() { return super.toString() + "[" + this.next.toString() + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\PipeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */