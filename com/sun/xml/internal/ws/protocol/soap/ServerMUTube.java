package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Set;
import javax.xml.namespace.QName;

public class ServerMUTube extends MUTube {
  private ServerTubeAssemblerContext tubeContext;
  
  private final Set<String> roles;
  
  private final Set<QName> handlerKnownHeaders;
  
  public ServerMUTube(ServerTubeAssemblerContext paramServerTubeAssemblerContext, Tube paramTube) {
    super(paramServerTubeAssemblerContext.getEndpoint().getBinding(), paramTube);
    this.tubeContext = paramServerTubeAssemblerContext;
    HandlerConfiguration handlerConfiguration = this.binding.getHandlerConfig();
    this.roles = handlerConfiguration.getRoles();
    this.handlerKnownHeaders = this.binding.getKnownHeaders();
  }
  
  protected ServerMUTube(ServerMUTube paramServerMUTube, TubeCloner paramTubeCloner) {
    super(paramServerMUTube, paramTubeCloner);
    this.tubeContext = paramServerMUTube.tubeContext;
    this.roles = paramServerMUTube.roles;
    this.handlerKnownHeaders = paramServerMUTube.handlerKnownHeaders;
  }
  
  public NextAction processRequest(Packet paramPacket) {
    Set set = getMisUnderstoodHeaders(paramPacket.getMessage().getHeaders(), this.roles, this.handlerKnownHeaders);
    return (set == null || set.isEmpty()) ? doInvoke(this.next, paramPacket) : doReturnWith(paramPacket.createServerResponse(createMUSOAPFaultMessage(set), this.tubeContext.getWsdlModel(), this.tubeContext.getSEIModel(), this.tubeContext.getEndpoint().getBinding()));
  }
  
  public ServerMUTube copy(TubeCloner paramTubeCloner) { return new ServerMUTube(this, paramTubeCloner); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\protocol\soap\ServerMUTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */