package com.sun.xml.internal.ws.protocol.soap;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Set;

public class ClientMUTube extends MUTube {
  public ClientMUTube(WSBinding paramWSBinding, Tube paramTube) { super(paramWSBinding, paramTube); }
  
  protected ClientMUTube(ClientMUTube paramClientMUTube, TubeCloner paramTubeCloner) { super(paramClientMUTube, paramTubeCloner); }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket) {
    if (paramPacket.getMessage() == null)
      return super.processResponse(paramPacket); 
    HandlerConfiguration handlerConfiguration = paramPacket.handlerConfig;
    if (handlerConfiguration == null)
      handlerConfiguration = this.binding.getHandlerConfig(); 
    Set set = getMisUnderstoodHeaders(paramPacket.getMessage().getHeaders(), handlerConfiguration.getRoles(), this.binding.getKnownHeaders());
    if (set == null || set.isEmpty())
      return super.processResponse(paramPacket); 
    throw createMUSOAPFaultException(set);
  }
  
  public ClientMUTube copy(TubeCloner paramTubeCloner) { return new ClientMUTube(this, paramTubeCloner); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\protocol\soap\ClientMUTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */