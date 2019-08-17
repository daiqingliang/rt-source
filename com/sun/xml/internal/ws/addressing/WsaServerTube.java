package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;

public class WsaServerTube extends WsaTube {
  private WSEndpoint endpoint;
  
  private WSEndpointReference replyTo;
  
  private WSEndpointReference faultTo;
  
  private boolean isAnonymousRequired = false;
  
  protected boolean isEarlyBackchannelCloseAllowed = true;
  
  private WSDLBoundOperation wbo;
  
  public static final String REQUEST_MESSAGE_ID = "com.sun.xml.internal.ws.addressing.request.messageID";
  
  private static final Logger LOGGER = Logger.getLogger(WsaServerTube.class.getName());
  
  public WsaServerTube(WSEndpoint paramWSEndpoint, @NotNull WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube) {
    super(paramWSDLPort, paramWSBinding, paramTube);
    this.endpoint = paramWSEndpoint;
  }
  
  public WsaServerTube(WsaServerTube paramWsaServerTube, TubeCloner paramTubeCloner) {
    super(paramWsaServerTube, paramTubeCloner);
    this.endpoint = paramWsaServerTube.endpoint;
  }
  
  public WsaServerTube copy(TubeCloner paramTubeCloner) { return new WsaServerTube(this, paramTubeCloner); }
  
  @NotNull
  public NextAction processRequest(Packet paramPacket) {
    String str;
    Message message = paramPacket.getMessage();
    if (message == null)
      return doInvoke(this.next, paramPacket); 
    paramPacket.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, paramPacket));
    MessageHeaders messageHeaders = paramPacket.getMessage().getHeaders();
    try {
      this.replyTo = AddressingUtils.getReplyTo(messageHeaders, this.addressingVersion, this.soapVersion);
      this.faultTo = AddressingUtils.getFaultTo(messageHeaders, this.addressingVersion, this.soapVersion);
      str = AddressingUtils.getMessageID(messageHeaders, this.addressingVersion, this.soapVersion);
    } catch (InvalidAddressingHeaderException invalidAddressingHeaderException) {
      LOGGER.log(Level.WARNING, this.addressingVersion.getInvalidMapText() + ", Problem header:" + invalidAddressingHeaderException.getProblemHeader() + ", Reason: " + invalidAddressingHeaderException.getSubsubcode(), invalidAddressingHeaderException);
      messageHeaders.remove(invalidAddressingHeaderException.getProblemHeader());
      SOAPFault sOAPFault = this.helper.createInvalidAddressingHeaderFault(invalidAddressingHeaderException, this.addressingVersion);
      if (this.wsdlPort != null && paramPacket.getMessage().isOneWay(this.wsdlPort)) {
        Packet packet2 = paramPacket.createServerResponse(null, this.wsdlPort, null, this.binding);
        return doReturnWith(packet2);
      } 
      Message message1 = Messages.create(sOAPFault);
      if (this.soapVersion == SOAPVersion.SOAP_11) {
        FaultDetailHeader faultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), invalidAddressingHeaderException.getProblemHeader());
        message1.getHeaders().add(faultDetailHeader);
      } 
      Packet packet1 = paramPacket.createServerResponse(message1, this.wsdlPort, null, this.binding);
      return doReturnWith(packet1);
    } 
    if (this.replyTo == null)
      this.replyTo = this.addressingVersion.anonymousEpr; 
    if (this.faultTo == null)
      this.faultTo = this.replyTo; 
    paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest", this.replyTo);
    paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest", this.faultTo);
    paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", str);
    this.wbo = getWSDLBoundOperation(paramPacket);
    this.isAnonymousRequired = isAnonymousRequired(this.wbo);
    Packet packet = validateInboundHeaders(paramPacket);
    if (packet.getMessage() == null)
      return doReturnWith(packet); 
    if (packet.getMessage().isFault()) {
      if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.faultTo.isAnonymous() && paramPacket.transportBackChannel != null)
        paramPacket.transportBackChannel.close(); 
      return processResponse(packet);
    } 
    if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.replyTo.isAnonymous() && !this.faultTo.isAnonymous() && paramPacket.transportBackChannel != null)
      paramPacket.transportBackChannel.close(); 
    return doInvoke(this.next, packet);
  }
  
  protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation paramWSDLBoundOperation) { return false; }
  
  protected void checkAnonymousSemantics(WSDLBoundOperation paramWSDLBoundOperation, WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2) {}
  
  @NotNull
  public NextAction processException(Throwable paramThrowable) {
    Packet packet = Fiber.current().getPacket();
    ThrowableContainerPropertySet throwableContainerPropertySet = (ThrowableContainerPropertySet)packet.getSatellite(ThrowableContainerPropertySet.class);
    if (throwableContainerPropertySet == null) {
      throwableContainerPropertySet = new ThrowableContainerPropertySet(paramThrowable);
      packet.addSatellite(throwableContainerPropertySet);
    } else if (paramThrowable != throwableContainerPropertySet.getThrowable()) {
      throwableContainerPropertySet.setThrowable(paramThrowable);
    } 
    return processResponse(packet.endpoint.createServiceResponseForException(throwableContainerPropertySet, packet, this.soapVersion, this.wsdlPort, packet.endpoint.getSEIModel(), this.binding));
  }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket) {
    EndpointAddress endpointAddress;
    Message message = paramPacket.getMessage();
    if (message == null)
      return doReturnWith(paramPacket); 
    String str = AddressingUtils.getTo(message.getHeaders(), this.addressingVersion, this.soapVersion);
    if (str != null)
      this.replyTo = this.faultTo = new WSEndpointReference(str, this.addressingVersion); 
    if (this.replyTo == null)
      this.replyTo = (WSEndpointReference)paramPacket.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest"); 
    if (this.faultTo == null)
      this.faultTo = (WSEndpointReference)paramPacket.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest"); 
    WSEndpointReference wSEndpointReference = message.isFault() ? this.faultTo : this.replyTo;
    if (wSEndpointReference == null && paramPacket.proxy instanceof Stub)
      wSEndpointReference = ((Stub)paramPacket.proxy).getWSEndpointReference(); 
    if (wSEndpointReference == null || wSEndpointReference.isAnonymous() || this.isAnonymousRequired)
      return doReturnWith(paramPacket); 
    if (wSEndpointReference.isNone()) {
      paramPacket.setMessage(null);
      return doReturnWith(paramPacket);
    } 
    if (this.wsdlPort != null && paramPacket.getMessage().isOneWay(this.wsdlPort)) {
      LOGGER.fine(AddressingMessages.NON_ANONYMOUS_RESPONSE_ONEWAY());
      return doReturnWith(paramPacket);
    } 
    if (this.wbo != null || paramPacket.soapAction == null) {
      endpointAddress = paramPacket.getMessage().isFault() ? this.helper.getFaultAction(this.wbo, paramPacket) : this.helper.getOutputAction(this.wbo);
      if (paramPacket.soapAction == null || (endpointAddress != null && !endpointAddress.equals("http://jax-ws.dev.java.net/addressing/output-action-not-set")))
        paramPacket.soapAction = endpointAddress; 
    } 
    paramPacket.expectReply = Boolean.valueOf(false);
    try {
      endpointAddress = new EndpointAddress(URI.create(wSEndpointReference.getAddress()));
    } catch (NullPointerException nullPointerException) {
      throw new WebServiceException(nullPointerException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new WebServiceException(illegalArgumentException);
    } 
    paramPacket.endpointAddress = endpointAddress;
    return paramPacket.isAdapterDeliversNonAnonymousResponse ? doReturnWith(paramPacket) : doReturnWith(NonAnonymousResponseProcessor.getDefault().process(paramPacket));
  }
  
  protected void validateAction(Packet paramPacket) {
    WSDLBoundOperation wSDLBoundOperation = getWSDLBoundOperation(paramPacket);
    if (wSDLBoundOperation == null)
      return; 
    String str1 = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
    if (str1 == null)
      throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION()); 
    String str2 = this.helper.getInputAction(paramPacket);
    String str3 = this.helper.getSOAPAction(paramPacket);
    if (this.helper.isInputActionDefault(paramPacket) && str3 != null && !str3.equals(""))
      str2 = str3; 
    if (str2 != null && !str1.equals(str2))
      throw new ActionNotSupportedException(str1); 
  }
  
  protected void checkMessageAddressingProperties(Packet paramPacket) {
    super.checkMessageAddressingProperties(paramPacket);
    WSDLBoundOperation wSDLBoundOperation = getWSDLBoundOperation(paramPacket);
    checkAnonymousSemantics(wSDLBoundOperation, this.replyTo, this.faultTo);
    checkNonAnonymousAddresses(this.replyTo, this.faultTo);
  }
  
  private void checkNonAnonymousAddresses(WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2) {
    if (!paramWSEndpointReference1.isAnonymous())
      try {
        new EndpointAddress(URI.create(paramWSEndpointReference1.getAddress()));
      } catch (Exception exception) {
        throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, this.addressingVersion.invalidAddressTag);
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\WsaServerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */