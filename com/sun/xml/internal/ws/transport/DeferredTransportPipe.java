package com.sun.xml.internal.ws.transport;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import javax.xml.ws.WebServiceFeature;

public final class DeferredTransportPipe extends AbstractTubeImpl {
  private Tube transport;
  
  private EndpointAddress address;
  
  private final ClassLoader classLoader;
  
  private final ClientTubeAssemblerContext context;
  
  public DeferredTransportPipe(ClassLoader paramClassLoader, ClientTubeAssemblerContext paramClientTubeAssemblerContext) {
    this.classLoader = paramClassLoader;
    this.context = paramClientTubeAssemblerContext;
    if (paramClientTubeAssemblerContext.getBinding().getFeature(HttpConfigFeature.class) == null)
      paramClientTubeAssemblerContext.getBinding().getFeatures().mergeFeatures(new WebServiceFeature[] { new HttpConfigFeature() }, false); 
    try {
      this.transport = TransportTubeFactory.create(paramClassLoader, paramClientTubeAssemblerContext);
      this.address = paramClientTubeAssemblerContext.getAddress();
    } catch (Exception exception) {}
  }
  
  public DeferredTransportPipe(DeferredTransportPipe paramDeferredTransportPipe, TubeCloner paramTubeCloner) {
    super(paramDeferredTransportPipe, paramTubeCloner);
    this.classLoader = paramDeferredTransportPipe.classLoader;
    this.context = paramDeferredTransportPipe.context;
    if (paramDeferredTransportPipe.transport != null) {
      this.transport = paramTubeCloner.copy(paramDeferredTransportPipe.transport);
      this.address = paramDeferredTransportPipe.address;
    } 
  }
  
  public NextAction processException(@NotNull Throwable paramThrowable) { return this.transport.processException(paramThrowable); }
  
  public NextAction processRequest(@NotNull Packet paramPacket) {
    if (paramPacket.endpointAddress == this.address)
      return this.transport.processRequest(paramPacket); 
    if (this.transport != null) {
      this.transport.preDestroy();
      this.transport = null;
      this.address = null;
    } 
    ClientTubeAssemblerContext clientTubeAssemblerContext = new ClientTubeAssemblerContext(paramPacket.endpointAddress, this.context.getWsdlModel(), this.context.getBindingProvider(), this.context.getBinding(), this.context.getContainer(), this.context.getCodec().copy(), this.context.getSEIModel(), this.context.getSEI());
    this.address = paramPacket.endpointAddress;
    this.transport = TransportTubeFactory.create(this.classLoader, clientTubeAssemblerContext);
    assert this.transport != null;
    return this.transport.processRequest(paramPacket);
  }
  
  public NextAction processResponse(@NotNull Packet paramPacket) { return (this.transport != null) ? this.transport.processResponse(paramPacket) : doReturnWith(paramPacket); }
  
  public void preDestroy() {
    if (this.transport != null) {
      this.transport.preDestroy();
      this.transport = null;
      this.address = null;
    } 
  }
  
  public DeferredTransportPipe copy(TubeCloner paramTubeCloner) { return new DeferredTransportPipe(this, paramTubeCloner); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\DeferredTransportPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */