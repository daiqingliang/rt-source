package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.WsaServerTube;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;

public class MemberSubmissionWsaServerTube extends WsaServerTube {
  private final MemberSubmissionAddressing.Validation validation;
  
  public MemberSubmissionWsaServerTube(WSEndpoint paramWSEndpoint, @NotNull WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube) {
    super(paramWSEndpoint, paramWSDLPort, paramWSBinding, paramTube);
    this.validation = ((MemberSubmissionAddressingFeature)paramWSBinding.getFeature(MemberSubmissionAddressingFeature.class)).getValidation();
  }
  
  public MemberSubmissionWsaServerTube(MemberSubmissionWsaServerTube paramMemberSubmissionWsaServerTube, TubeCloner paramTubeCloner) {
    super(paramMemberSubmissionWsaServerTube, paramTubeCloner);
    this.validation = paramMemberSubmissionWsaServerTube.validation;
  }
  
  public MemberSubmissionWsaServerTube copy(TubeCloner paramTubeCloner) { return new MemberSubmissionWsaServerTube(this, paramTubeCloner); }
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6) {
    super.checkMandatoryHeaders(paramPacket, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
    if (!paramBoolean2)
      throw new MissingAddressingHeaderException(this.addressingVersion.toTag, paramPacket); 
    if (this.wsdlPort != null) {
      WSDLBoundOperation wSDLBoundOperation = getWSDLBoundOperation(paramPacket);
      if (wSDLBoundOperation != null && !wSDLBoundOperation.getOperation().isOneWay() && !paramBoolean3)
        throw new MissingAddressingHeaderException(this.addressingVersion.replyToTag, paramPacket); 
    } 
    if (!this.validation.equals(MemberSubmissionAddressing.Validation.LAX) && (paramBoolean3 || paramBoolean4) && !paramBoolean5)
      throw new MissingAddressingHeaderException(this.addressingVersion.messageIDTag, paramPacket); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\v200408\MemberSubmissionWsaServerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */