package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.xml.internal.ws.addressing.WsaClientTube;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;

public class MemberSubmissionWsaClientTube extends WsaClientTube {
  private final MemberSubmissionAddressing.Validation validation;
  
  public MemberSubmissionWsaClientTube(WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube) {
    super(paramWSDLPort, paramWSBinding, paramTube);
    this.validation = ((MemberSubmissionAddressingFeature)paramWSBinding.getFeature(MemberSubmissionAddressingFeature.class)).getValidation();
  }
  
  public MemberSubmissionWsaClientTube(MemberSubmissionWsaClientTube paramMemberSubmissionWsaClientTube, TubeCloner paramTubeCloner) {
    super(paramMemberSubmissionWsaClientTube, paramTubeCloner);
    this.validation = paramMemberSubmissionWsaClientTube.validation;
  }
  
  public MemberSubmissionWsaClientTube copy(TubeCloner paramTubeCloner) { return new MemberSubmissionWsaClientTube(this, paramTubeCloner); }
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6) {
    super.checkMandatoryHeaders(paramPacket, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
    if (!paramBoolean2)
      throw new MissingAddressingHeaderException(this.addressingVersion.toTag, paramPacket); 
    if (!this.validation.equals(MemberSubmissionAddressing.Validation.LAX) && this.expectReply && paramPacket.getMessage() != null && !paramBoolean6) {
      String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
      if (!paramPacket.getMessage().isFault() || !str.equals(this.addressingVersion.getDefaultFaultAction()))
        throw new MissingAddressingHeaderException(this.addressingVersion.relatesToTag, paramPacket); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\v200408\MemberSubmissionWsaClientTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */