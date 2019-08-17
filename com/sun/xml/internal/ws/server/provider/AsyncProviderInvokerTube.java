package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.server.AbstractWebServiceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncProviderInvokerTube<T> extends ProviderInvokerTube<T> {
  private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.AsyncProviderInvokerTube");
  
  public AsyncProviderInvokerTube(Invoker paramInvoker, ProviderArgumentsBuilder<T> paramProviderArgumentsBuilder) { super(paramInvoker, paramProviderArgumentsBuilder); }
  
  @NotNull
  public NextAction processRequest(@NotNull Packet paramPacket) {
    Object object = this.argsBuilder.getParameter(paramPacket);
    NoSuspendResumer noSuspendResumer = new NoSuspendResumer(null);
    AsyncProviderCallbackImpl asyncProviderCallbackImpl = new AsyncProviderCallbackImpl(paramPacket, noSuspendResumer);
    AsyncWebServiceContext asyncWebServiceContext = new AsyncWebServiceContext(getEndpoint(), paramPacket);
    LOGGER.fine("Invoking AsyncProvider Endpoint");
    try {
      getInvoker(paramPacket).invokeAsyncProvider(paramPacket, object, asyncProviderCallbackImpl, asyncWebServiceContext);
    } catch (Throwable throwable) {
      LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable);
      return doThrow(throwable);
    } 
    synchronized (asyncProviderCallbackImpl) {
      if (noSuspendResumer.response != null) {
        ThrowableContainerPropertySet throwableContainerPropertySet = (ThrowableContainerPropertySet)noSuspendResumer.response.getSatellite(ThrowableContainerPropertySet.class);
        Throwable throwable = (throwableContainerPropertySet != null) ? throwableContainerPropertySet.getThrowable() : null;
        return (throwable != null) ? doThrow(noSuspendResumer.response, throwable) : doReturnWith(noSuspendResumer.response);
      } 
      asyncProviderCallbackImpl.resumer = new FiberResumer();
      return doSuspend();
    } 
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket) { return doReturnWith(paramPacket); }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable) { return doThrow(paramThrowable); }
  
  public class AsyncProviderCallbackImpl extends Object implements AsyncProviderCallback<T> {
    private final Packet request;
    
    private AsyncProviderInvokerTube.Resumer resumer;
    
    public AsyncProviderCallbackImpl(Packet param1Packet, AsyncProviderInvokerTube.Resumer param1Resumer) {
      this.request = param1Packet;
      this.resumer = param1Resumer;
    }
    
    public void send(@Nullable T param1T) {
      if (param1T == null && this.request.transportBackChannel != null)
        this.request.transportBackChannel.close(); 
      Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, param1T, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
      synchronized (this) {
        this.resumer.onResume(packet);
      } 
    }
    
    public void sendError(@NotNull Throwable param1Throwable) {
      RuntimeException runtimeException;
      if (param1Throwable instanceof Exception) {
        runtimeException = (Exception)param1Throwable;
      } else {
        runtimeException = new RuntimeException(param1Throwable);
      } 
      Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, runtimeException, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
      synchronized (this) {
        this.resumer.onResume(packet);
      } 
    }
  }
  
  public class AsyncWebServiceContext extends AbstractWebServiceContext {
    final Packet packet;
    
    public AsyncWebServiceContext(WSEndpoint param1WSEndpoint, Packet param1Packet) {
      super(param1WSEndpoint);
      this.packet = param1Packet;
    }
    
    @NotNull
    public Packet getRequestPacket() { return this.packet; }
  }
  
  public class FiberResumer implements Resumer {
    private final Fiber fiber = Fiber.current();
    
    public void onResume(Packet param1Packet) {
      ThrowableContainerPropertySet throwableContainerPropertySet = (ThrowableContainerPropertySet)param1Packet.getSatellite(ThrowableContainerPropertySet.class);
      Throwable throwable = (throwableContainerPropertySet != null) ? throwableContainerPropertySet.getThrowable() : null;
      this.fiber.resume(throwable, param1Packet);
    }
  }
  
  private class NoSuspendResumer implements Resumer {
    protected Packet response = null;
    
    private NoSuspendResumer() {}
    
    public void onResume(Packet param1Packet) { this.response = param1Packet; }
  }
  
  private static interface Resumer {
    void onResume(Packet param1Packet);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\AsyncProviderInvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */