package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.sei.Invoker;
import com.sun.xml.internal.ws.server.sei.InvokerTube;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InvokerTube<T> extends InvokerTube<Invoker> implements EndpointAwareTube {
  private WSEndpoint endpoint;
  
  private static final ThreadLocal<Packet> packets = new ThreadLocal();
  
  private final Invoker wrapper = new Invoker() {
      public Object invoke(Packet param1Packet, Method param1Method, Object... param1VarArgs) throws InvocationTargetException, IllegalAccessException {
        packet = set(param1Packet);
        try {
          return ((Invoker)InvokerTube.this.invoker).invoke(param1Packet, param1Method, param1VarArgs);
        } finally {
          set(packet);
        } 
      }
      
      public <T> T invokeProvider(Packet param1Packet, T param1T) throws IllegalAccessException, InvocationTargetException {
        packet = set(param1Packet);
        try {
          object = ((Invoker)InvokerTube.this.invoker).invokeProvider(param1Packet, param1T);
          return (T)object;
        } finally {
          set(packet);
        } 
      }
      
      public <T> void invokeAsyncProvider(Packet param1Packet, T param1T, AsyncProviderCallback param1AsyncProviderCallback, WebServiceContext param1WebServiceContext) throws IllegalAccessException, InvocationTargetException {
        packet = set(param1Packet);
        try {
          ((Invoker)InvokerTube.this.invoker).invokeAsyncProvider(param1Packet, param1T, param1AsyncProviderCallback, param1WebServiceContext);
        } finally {
          set(packet);
        } 
      }
      
      private Packet set(Packet param1Packet) {
        Packet packet = (Packet)packets.get();
        packets.set(param1Packet);
        return packet;
      }
    };
  
  protected InvokerTube(Invoker paramInvoker) { super(paramInvoker); }
  
  public void setEndpoint(WSEndpoint paramWSEndpoint) {
    this.endpoint = paramWSEndpoint;
    AbstractWebServiceContext abstractWebServiceContext = new AbstractWebServiceContext(paramWSEndpoint) {
        @Nullable
        public Packet getRequestPacket() { return (Packet)packets.get(); }
      };
    ((Invoker)this.invoker).start(abstractWebServiceContext, paramWSEndpoint);
  }
  
  protected WSEndpoint getEndpoint() { return this.endpoint; }
  
  @NotNull
  public final Invoker getInvoker(Packet paramPacket) { return this.wrapper; }
  
  public final AbstractTubeImpl copy(TubeCloner paramTubeCloner) {
    paramTubeCloner.add(this, this);
    return this;
  }
  
  public void preDestroy() { ((Invoker)this.invoker).dispose(); }
  
  @NotNull
  public static Packet getCurrentPacket() {
    Packet packet = (Packet)packets.get();
    if (packet == null)
      throw new WebServiceException(ServerMessages.NO_CURRENT_PACKET()); 
    return packet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\InvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */