package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.ws.WebServiceException;

public class WsaClientTube extends WsaTube {
  protected boolean expectReply = true;
  
  public WsaClientTube(WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube) { super(paramWSDLPort, paramWSBinding, paramTube); }
  
  public WsaClientTube(WsaClientTube paramWsaClientTube, TubeCloner paramTubeCloner) { super(paramWsaClientTube, paramTubeCloner); }
  
  public WsaClientTube copy(TubeCloner paramTubeCloner) { return new WsaClientTube(this, paramTubeCloner); }
  
  @NotNull
  public NextAction processRequest(Packet paramPacket) {
    this.expectReply = paramPacket.expectReply.booleanValue();
    return doInvoke(this.next, paramPacket);
  }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket) {
    if (paramPacket.getMessage() != null) {
      paramPacket = validateInboundHeaders(paramPacket);
      paramPacket.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, paramPacket));
      String str = AddressingUtils.getMessageID(paramPacket.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
      paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", str);
    } 
    return doReturnWith(paramPacket);
  }
  
  protected void validateAction(Packet paramPacket) {
    WSDLBoundOperation wSDLBoundOperation = getWSDLBoundOperation(paramPacket);
    if (wSDLBoundOperation == null)
      return; 
    String str1 = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
    if (str1 == null)
      throw new WebServiceException(AddressingMessages.VALIDATION_CLIENT_NULL_ACTION()); 
    String str2 = this.helper.getOutputAction(paramPacket);
    if (str2 != null && !str1.equals(str2))
      throw new ActionNotSupportedException(str1); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\WsaClientTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */