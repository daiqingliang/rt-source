package com.sun.xml.internal.ws.server.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.server.InvokerTube;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;

public class SEIInvokerTube extends InvokerTube {
  private final WSBinding binding;
  
  private final AbstractSEIModelImpl model;
  
  public SEIInvokerTube(AbstractSEIModelImpl paramAbstractSEIModelImpl, Invoker paramInvoker, WSBinding paramWSBinding) {
    super(paramInvoker);
    this.binding = paramWSBinding;
    this.model = paramAbstractSEIModelImpl;
  }
  
  @NotNull
  public NextAction processRequest(@NotNull Packet paramPacket) {
    JavaCallInfo javaCallInfo = this.model.getDatabinding().deserializeRequest(paramPacket);
    if (javaCallInfo.getException() == null) {
      try {
        if (paramPacket.getMessage().isOneWay(this.model.getPort()) && paramPacket.transportBackChannel != null)
          paramPacket.transportBackChannel.close(); 
        Object object = getInvoker(paramPacket).invoke(paramPacket, javaCallInfo.getMethod(), javaCallInfo.getParameters());
        javaCallInfo.setReturnValue(object);
      } catch (InvocationTargetException invocationTargetException) {
        javaCallInfo.setException(invocationTargetException);
      } catch (Exception exception) {
        javaCallInfo.setException(exception);
      } 
    } else if (javaCallInfo.getException() instanceof DispatchException) {
      DispatchException dispatchException = (DispatchException)javaCallInfo.getException();
      return doReturnWith(paramPacket.createServerResponse(dispatchException.fault, this.model.getPort(), null, this.binding));
    } 
    Packet packet = (Packet)this.model.getDatabinding().serializeResponse(javaCallInfo);
    packet = paramPacket.relateServerResponse(packet, paramPacket.endpoint.getPort(), this.model, paramPacket.endpoint.getBinding());
    assert packet != null;
    return doReturnWith(packet);
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket) { return doReturnWith(paramPacket); }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable) { return doThrow(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\SEIInvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */