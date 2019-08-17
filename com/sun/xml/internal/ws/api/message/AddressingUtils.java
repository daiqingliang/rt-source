package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingPropertySet;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.OneWayFeature;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

public class AddressingUtils {
  public static void fillRequestAddressingHeaders(MessageHeaders paramMessageHeaders, Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean, String paramString) { fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean, paramString, false); }
  
  public static void fillRequestAddressingHeaders(MessageHeaders paramMessageHeaders, Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean1, String paramString, boolean paramBoolean2) {
    fillCommonAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramString, paramBoolean2);
    if (!paramBoolean1) {
      WSEndpointReference wSEndpointReference = paramAddressingVersion.anonymousEpr;
      if (paramMessageHeaders.get(paramAddressingVersion.replyToTag, false) == null)
        paramMessageHeaders.add(wSEndpointReference.createHeader(paramAddressingVersion.replyToTag)); 
      if (paramMessageHeaders.get(paramAddressingVersion.faultToTag, false) == null)
        paramMessageHeaders.add(wSEndpointReference.createHeader(paramAddressingVersion.faultToTag)); 
      if (paramPacket.getMessage().getHeaders().get(paramAddressingVersion.messageIDTag, false) == null && paramMessageHeaders.get(paramAddressingVersion.messageIDTag, false) == null) {
        StringHeader stringHeader = new StringHeader(paramAddressingVersion.messageIDTag, Message.generateMessageID());
        paramMessageHeaders.add(stringHeader);
      } 
    } 
  }
  
  public static void fillRequestAddressingHeaders(MessageHeaders paramMessageHeaders, WSDLPort paramWSDLPort, WSBinding paramWSBinding, Packet paramPacket) {
    if (paramWSBinding == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_BINDING()); 
    if (paramWSBinding.isFeatureEnabled(SuppressAutomaticWSARequestHeadersFeature.class))
      return; 
    MessageHeaders messageHeaders = paramPacket.getMessage().getHeaders();
    String str1 = getAction(messageHeaders, paramWSBinding.getAddressingVersion(), paramWSBinding.getSOAPVersion());
    if (str1 != null)
      return; 
    AddressingVersion addressingVersion = paramWSBinding.getAddressingVersion();
    WsaTubeHelper wsaTubeHelper = addressingVersion.getWsaHelper(paramWSDLPort, null, paramWSBinding);
    String str2 = wsaTubeHelper.getEffectiveInputAction(paramPacket);
    if (str2 == null || (str2.equals("") && paramWSBinding.getSOAPVersion() == SOAPVersion.SOAP_11))
      throw new WebServiceException(ClientMessages.INVALID_SOAP_ACTION()); 
    boolean bool = !paramPacket.expectReply.booleanValue();
    if (paramWSDLPort != null && !bool && paramPacket.getMessage() != null && paramPacket.getWSDLOperation() != null) {
      WSDLBoundOperation wSDLBoundOperation = paramWSDLPort.getBinding().get(paramPacket.getWSDLOperation());
      if (wSDLBoundOperation != null && wSDLBoundOperation.getAnonymous() == WSDLBoundOperation.ANONYMOUS.prohibited)
        throw new WebServiceException(AddressingMessages.WSAW_ANONYMOUS_PROHIBITED()); 
    } 
    OneWayFeature oneWayFeature = (OneWayFeature)paramWSBinding.getFeature(OneWayFeature.class);
    AddressingPropertySet addressingPropertySet = (AddressingPropertySet)paramPacket.getSatellite(AddressingPropertySet.class);
    oneWayFeature = (addressingPropertySet == null) ? oneWayFeature : new OneWayFeature(addressingPropertySet, addressingVersion);
    if (oneWayFeature == null || !oneWayFeature.isEnabled()) {
      fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, addressingVersion, paramWSBinding.getSOAPVersion(), bool, str2, AddressingVersion.isRequired(paramWSBinding));
    } else {
      fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, addressingVersion, paramWSBinding.getSOAPVersion(), oneWayFeature, bool, str2);
    } 
  }
  
  public static String getAction(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) {
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    String str = null;
    Header header = getFirstHeader(paramMessageHeaders, paramAddressingVersion.actionTag, true, paramSOAPVersion);
    if (header != null)
      str = header.getStringContent(); 
    return str;
  }
  
  public static WSEndpointReference getFaultTo(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) {
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    Header header = getFirstHeader(paramMessageHeaders, paramAddressingVersion.faultToTag, true, paramSOAPVersion);
    WSEndpointReference wSEndpointReference = null;
    if (header != null)
      try {
        wSEndpointReference = header.readAsEPR(paramAddressingVersion);
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), xMLStreamException);
      }  
    return wSEndpointReference;
  }
  
  public static String getMessageID(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) {
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    Header header = getFirstHeader(paramMessageHeaders, paramAddressingVersion.messageIDTag, true, paramSOAPVersion);
    String str = null;
    if (header != null)
      str = header.getStringContent(); 
    return str;
  }
  
  public static String getRelatesTo(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) {
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    Header header = getFirstHeader(paramMessageHeaders, paramAddressingVersion.relatesToTag, true, paramSOAPVersion);
    String str = null;
    if (header != null)
      str = header.getStringContent(); 
    return str;
  }
  
  public static WSEndpointReference getReplyTo(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) {
    WSEndpointReference wSEndpointReference;
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    Header header = getFirstHeader(paramMessageHeaders, paramAddressingVersion.replyToTag, true, paramSOAPVersion);
    if (header != null) {
      try {
        wSEndpointReference = header.readAsEPR(paramAddressingVersion);
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), xMLStreamException);
      } 
    } else {
      wSEndpointReference = paramAddressingVersion.anonymousEpr;
    } 
    return wSEndpointReference;
  }
  
  public static String getTo(MessageHeaders paramMessageHeaders, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion) {
    String str;
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    Header header = getFirstHeader(paramMessageHeaders, paramAddressingVersion.toTag, true, paramSOAPVersion);
    if (header != null) {
      str = header.getStringContent();
    } else {
      str = paramAddressingVersion.anonymousUri;
    } 
    return str;
  }
  
  public static Header getFirstHeader(MessageHeaders paramMessageHeaders, QName paramQName, boolean paramBoolean, SOAPVersion paramSOAPVersion) {
    if (paramSOAPVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION()); 
    Iterator iterator = paramMessageHeaders.getHeaders(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean);
    while (iterator.hasNext()) {
      Header header = (Header)iterator.next();
      if (header.getRole(paramSOAPVersion).equals(paramSOAPVersion.implicitRole))
        return header; 
    } 
    return null;
  }
  
  private static void fillRequestAddressingHeaders(@NotNull MessageHeaders paramMessageHeaders, @NotNull Packet paramPacket, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull OneWayFeature paramOneWayFeature, boolean paramBoolean, @NotNull String paramString) {
    if (!paramBoolean && !paramOneWayFeature.isUseAsyncWithSyncInvoke() && Boolean.TRUE.equals(paramPacket.isSynchronousMEP)) {
      fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean, paramString);
    } else {
      fillCommonAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramString, false);
      boolean bool = false;
      if (paramMessageHeaders.get(paramAddressingVersion.replyToTag, false) == null) {
        WSEndpointReference wSEndpointReference = paramOneWayFeature.getReplyTo();
        if (wSEndpointReference != null) {
          paramMessageHeaders.add(wSEndpointReference.createHeader(paramAddressingVersion.replyToTag));
          if (paramPacket.getMessage().getHeaders().get(paramAddressingVersion.messageIDTag, false) == null) {
            String str1 = (paramOneWayFeature.getMessageId() == null) ? Message.generateMessageID() : paramOneWayFeature.getMessageId();
            paramMessageHeaders.add(new StringHeader(paramAddressingVersion.messageIDTag, str1));
            bool = true;
          } 
        } 
      } 
      String str = paramOneWayFeature.getMessageId();
      if (!bool && str != null)
        paramMessageHeaders.add(new StringHeader(paramAddressingVersion.messageIDTag, str)); 
      if (paramMessageHeaders.get(paramAddressingVersion.faultToTag, false) == null) {
        WSEndpointReference wSEndpointReference = paramOneWayFeature.getFaultTo();
        if (wSEndpointReference != null) {
          paramMessageHeaders.add(wSEndpointReference.createHeader(paramAddressingVersion.faultToTag));
          if (paramMessageHeaders.get(paramAddressingVersion.messageIDTag, false) == null)
            paramMessageHeaders.add(new StringHeader(paramAddressingVersion.messageIDTag, Message.generateMessageID())); 
        } 
      } 
      if (paramOneWayFeature.getFrom() != null)
        paramMessageHeaders.addOrReplace(paramOneWayFeature.getFrom().createHeader(paramAddressingVersion.fromTag)); 
      if (paramOneWayFeature.getRelatesToID() != null)
        paramMessageHeaders.addOrReplace(new RelatesToHeader(paramAddressingVersion.relatesToTag, paramOneWayFeature.getRelatesToID())); 
    } 
  }
  
  private static void fillCommonAddressingHeaders(MessageHeaders paramMessageHeaders, Packet paramPacket, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull String paramString, boolean paramBoolean) {
    if (paramPacket == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_PACKET()); 
    if (paramAddressingVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION()); 
    if (paramSOAPVersion == null)
      throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION()); 
    if (paramString == null && !paramSOAPVersion.httpBindingId.equals("http://www.w3.org/2003/05/soap/bindings/HTTP/"))
      throw new IllegalArgumentException(AddressingMessages.NULL_ACTION()); 
    if (paramMessageHeaders.get(paramAddressingVersion.toTag, false) == null) {
      StringHeader stringHeader = new StringHeader(paramAddressingVersion.toTag, paramPacket.endpointAddress.toString());
      paramMessageHeaders.add(stringHeader);
    } 
    if (paramString != null) {
      paramPacket.soapAction = paramString;
      if (paramMessageHeaders.get(paramAddressingVersion.actionTag, false) == null) {
        StringHeader stringHeader = new StringHeader(paramAddressingVersion.actionTag, paramString, paramSOAPVersion, paramBoolean);
        paramMessageHeaders.add(stringHeader);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\AddressingUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */