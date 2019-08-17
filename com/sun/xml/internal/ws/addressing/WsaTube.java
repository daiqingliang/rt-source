package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

abstract class WsaTube extends AbstractFilterTubeImpl {
  @NotNull
  protected final WSDLPort wsdlPort;
  
  protected final WSBinding binding;
  
  final WsaTubeHelper helper;
  
  @NotNull
  protected final AddressingVersion addressingVersion;
  
  protected final SOAPVersion soapVersion;
  
  private final boolean addressingRequired;
  
  private static final Logger LOGGER = Logger.getLogger(WsaTube.class.getName());
  
  public WsaTube(WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube) {
    super(paramTube);
    this.wsdlPort = paramWSDLPort;
    this.binding = paramWSBinding;
    addKnownHeadersToBinding(paramWSBinding);
    this.addressingVersion = paramWSBinding.getAddressingVersion();
    this.soapVersion = paramWSBinding.getSOAPVersion();
    this.helper = getTubeHelper();
    this.addressingRequired = AddressingVersion.isRequired(paramWSBinding);
  }
  
  public WsaTube(WsaTube paramWsaTube, TubeCloner paramTubeCloner) {
    super(paramWsaTube, paramTubeCloner);
    this.wsdlPort = paramWsaTube.wsdlPort;
    this.binding = paramWsaTube.binding;
    this.helper = paramWsaTube.helper;
    this.addressingVersion = paramWsaTube.addressingVersion;
    this.soapVersion = paramWsaTube.soapVersion;
    this.addressingRequired = paramWsaTube.addressingRequired;
  }
  
  private void addKnownHeadersToBinding(WSBinding paramWSBinding) {
    for (AddressingVersion addressingVersion1 : AddressingVersion.values()) {
      paramWSBinding.addKnownHeader(addressingVersion1.actionTag);
      paramWSBinding.addKnownHeader(addressingVersion1.faultDetailTag);
      paramWSBinding.addKnownHeader(addressingVersion1.faultToTag);
      paramWSBinding.addKnownHeader(addressingVersion1.fromTag);
      paramWSBinding.addKnownHeader(addressingVersion1.messageIDTag);
      paramWSBinding.addKnownHeader(addressingVersion1.relatesToTag);
      paramWSBinding.addKnownHeader(addressingVersion1.replyToTag);
      paramWSBinding.addKnownHeader(addressingVersion1.toTag);
    } 
  }
  
  @NotNull
  public NextAction processException(Throwable paramThrowable) { return super.processException(paramThrowable); }
  
  protected WsaTubeHelper getTubeHelper() {
    if (this.binding.isFeatureEnabled(javax.xml.ws.soap.AddressingFeature.class))
      return new WsaTubeHelperImpl(this.wsdlPort, null, this.binding); 
    if (this.binding.isFeatureEnabled(com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature.class))
      return new WsaTubeHelperImpl(this.wsdlPort, null, this.binding); 
    throw new WebServiceException(AddressingMessages.ADDRESSING_NOT_ENABLED(getClass().getSimpleName()));
  }
  
  protected Packet validateInboundHeaders(Packet paramPacket) {
    FaultDetailHeader faultDetailHeader;
    SOAPFault sOAPFault;
    try {
      checkMessageAddressingProperties(paramPacket);
      return paramPacket;
    } catch (InvalidAddressingHeaderException invalidAddressingHeaderException) {
      LOGGER.log(Level.WARNING, this.addressingVersion.getInvalidMapText() + ", Problem header:" + invalidAddressingHeaderException.getProblemHeader() + ", Reason: " + invalidAddressingHeaderException.getSubsubcode(), invalidAddressingHeaderException);
      sOAPFault = this.helper.createInvalidAddressingHeaderFault(invalidAddressingHeaderException, this.addressingVersion);
      faultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), invalidAddressingHeaderException.getProblemHeader());
    } catch (MissingAddressingHeaderException missingAddressingHeaderException) {
      LOGGER.log(Level.WARNING, this.addressingVersion.getMapRequiredText() + ", Problem header:" + missingAddressingHeaderException.getMissingHeaderQName(), missingAddressingHeaderException);
      sOAPFault = this.helper.newMapRequiredFault(missingAddressingHeaderException);
      faultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), missingAddressingHeaderException.getMissingHeaderQName());
    } 
    if (sOAPFault != null) {
      if (this.wsdlPort != null && paramPacket.getMessage().isOneWay(this.wsdlPort))
        return paramPacket.createServerResponse(null, this.wsdlPort, null, this.binding); 
      Message message = Messages.create(sOAPFault);
      if (this.soapVersion == SOAPVersion.SOAP_11)
        message.getHeaders().add(faultDetailHeader); 
      return paramPacket.createServerResponse(message, this.wsdlPort, null, this.binding);
    } 
    return paramPacket;
  }
  
  protected void checkMessageAddressingProperties(Packet paramPacket) { checkCardinality(paramPacket); }
  
  final boolean isAddressingEngagedOrRequired(Packet paramPacket, WSBinding paramWSBinding) {
    if (AddressingVersion.isRequired(paramWSBinding))
      return true; 
    if (paramPacket == null)
      return false; 
    if (paramPacket.getMessage() == null)
      return false; 
    if (paramPacket.getMessage().getHeaders() != null)
      return false; 
    String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
    return (str == null) ? true : true;
  }
  
  protected void checkCardinality(Packet paramPacket) {
    Message message = paramPacket.getMessage();
    if (message == null) {
      if (this.addressingRequired)
        throw new WebServiceException(AddressingMessages.NULL_MESSAGE()); 
      return;
    } 
    Iterator iterator = message.getHeaders().getHeaders(this.addressingVersion.nsUri, true);
    if (!iterator.hasNext()) {
      if (this.addressingRequired)
        throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, paramPacket); 
      return;
    } 
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool6 = false;
    boolean bool7 = false;
    QName qName = null;
    while (iterator.hasNext()) {
      Header header = (Header)iterator.next();
      if (!isInCurrentRole(header, this.binding))
        continue; 
      String str = header.getLocalPart();
      if (str.equals(this.addressingVersion.fromTag.getLocalPart())) {
        if (bool1) {
          qName = this.addressingVersion.fromTag;
          break;
        } 
        bool1 = true;
        continue;
      } 
      if (str.equals(this.addressingVersion.toTag.getLocalPart())) {
        if (bool2) {
          qName = this.addressingVersion.toTag;
          break;
        } 
        bool2 = true;
        continue;
      } 
      if (str.equals(this.addressingVersion.replyToTag.getLocalPart())) {
        if (bool3) {
          qName = this.addressingVersion.replyToTag;
          break;
        } 
        bool3 = true;
        try {
          header.readAsEPR(this.addressingVersion);
          continue;
        } catch (XMLStreamException xMLStreamException) {
          throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), xMLStreamException);
        } 
      } 
      if (str.equals(this.addressingVersion.faultToTag.getLocalPart())) {
        if (bool4) {
          qName = this.addressingVersion.faultToTag;
          break;
        } 
        bool4 = true;
        try {
          header.readAsEPR(this.addressingVersion);
          continue;
        } catch (XMLStreamException xMLStreamException) {
          throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), xMLStreamException);
        } 
      } 
      if (str.equals(this.addressingVersion.actionTag.getLocalPart())) {
        if (bool5) {
          qName = this.addressingVersion.actionTag;
          break;
        } 
        bool5 = true;
        continue;
      } 
      if (str.equals(this.addressingVersion.messageIDTag.getLocalPart())) {
        if (bool6) {
          qName = this.addressingVersion.messageIDTag;
          break;
        } 
        bool6 = true;
        continue;
      } 
      if (str.equals(this.addressingVersion.relatesToTag.getLocalPart())) {
        bool7 = true;
        continue;
      } 
      if (str.equals(this.addressingVersion.faultDetailTag.getLocalPart()))
        continue; 
      System.err.println(AddressingMessages.UNKNOWN_WSA_HEADER());
    } 
    if (qName != null)
      throw new InvalidAddressingHeaderException(qName, this.addressingVersion.invalidCardinalityTag); 
    boolean bool8 = bool5;
    if (bool8 || this.addressingRequired)
      checkMandatoryHeaders(paramPacket, bool5, bool2, bool3, bool4, bool6, bool7); 
  }
  
  final boolean isInCurrentRole(Header paramHeader, WSBinding paramWSBinding) { return (paramWSBinding == null) ? true : ((SOAPBinding)paramWSBinding).getRoles().contains(paramHeader.getRole(this.soapVersion)); }
  
  protected final WSDLBoundOperation getWSDLBoundOperation(Packet paramPacket) {
    if (this.wsdlPort == null)
      return null; 
    QName qName = paramPacket.getWSDLOperation();
    return (qName != null) ? this.wsdlPort.getBinding().get(qName) : null;
  }
  
  protected void validateSOAPAction(Packet paramPacket) {
    String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
    if (str == null)
      throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION()); 
    if (paramPacket.soapAction != null && !paramPacket.soapAction.equals("\"\"") && !paramPacket.soapAction.equals("\"" + str + "\""))
      throw new InvalidAddressingHeaderException(this.addressingVersion.actionTag, this.addressingVersion.actionMismatchTag); 
  }
  
  protected abstract void validateAction(Packet paramPacket);
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6) {
    if (!paramBoolean1)
      throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, paramPacket); 
    validateSOAPAction(paramPacket);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\WsaTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */