package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.Invoker;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncProviderInvokerTube<T> extends ProviderInvokerTube<T> {
  private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.SyncProviderInvokerTube");
  
  public SyncProviderInvokerTube(Invoker paramInvoker, ProviderArgumentsBuilder<T> paramProviderArgumentsBuilder) { super(paramInvoker, paramProviderArgumentsBuilder); }
  
  public NextAction processRequest(Packet paramPacket) {
    Object object2;
    WSDLPort wSDLPort = getEndpoint().getPort();
    WSBinding wSBinding = getEndpoint().getBinding();
    Object object1 = this.argsBuilder.getParameter(paramPacket);
    LOGGER.fine("Invoking Provider Endpoint");
    try {
      object2 = getInvoker(paramPacket).invokeProvider(paramPacket, object1);
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
      Packet packet1 = this.argsBuilder.getResponse(paramPacket, exception, wSDLPort, wSBinding);
      return doReturnWith(packet1);
    } 
    if (object2 == null && paramPacket.transportBackChannel != null)
      paramPacket.transportBackChannel.close(); 
    Packet packet = this.argsBuilder.getResponse(paramPacket, object2, wSDLPort, wSBinding);
    ThrowableContainerPropertySet throwableContainerPropertySet = (ThrowableContainerPropertySet)packet.getSatellite(ThrowableContainerPropertySet.class);
    Throwable throwable = (throwableContainerPropertySet != null) ? throwableContainerPropertySet.getThrowable() : null;
    return (throwable != null) ? doThrow(packet, throwable) : doReturnWith(packet);
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket) { return doReturnWith(paramPacket); }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable) { return doThrow(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\SyncProviderInvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */