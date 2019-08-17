package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class Message {
  protected AttachmentSet attachmentSet;
  
  private WSDLBoundOperation operation = null;
  
  private WSDLOperationMapping wsdlOperationMapping = null;
  
  private MessageMetadata messageMetadata = null;
  
  private Boolean isOneWay;
  
  public abstract boolean hasHeaders();
  
  @NotNull
  public abstract MessageHeaders getHeaders();
  
  @NotNull
  public AttachmentSet getAttachments() {
    if (this.attachmentSet == null)
      this.attachmentSet = new AttachmentSetImpl(); 
    return this.attachmentSet;
  }
  
  protected boolean hasAttachments() { return (this.attachmentSet != null); }
  
  public void setMessageMedadata(MessageMetadata paramMessageMetadata) { this.messageMetadata = paramMessageMetadata; }
  
  @Deprecated
  @Nullable
  public final WSDLBoundOperation getOperation(@NotNull WSDLBoundPortType paramWSDLBoundPortType) {
    if (this.operation == null && this.messageMetadata != null) {
      if (this.wsdlOperationMapping == null)
        this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping(); 
      if (this.wsdlOperationMapping != null)
        this.operation = this.wsdlOperationMapping.getWSDLBoundOperation(); 
    } 
    if (this.operation == null)
      this.operation = paramWSDLBoundPortType.getOperation(getPayloadNamespaceURI(), getPayloadLocalPart()); 
    return this.operation;
  }
  
  @Deprecated
  @Nullable
  public final WSDLBoundOperation getOperation(@NotNull WSDLPort paramWSDLPort) { return getOperation(paramWSDLPort.getBinding()); }
  
  @Deprecated
  @Nullable
  public final JavaMethod getMethod(@NotNull SEIModel paramSEIModel) {
    String str2;
    if (this.wsdlOperationMapping == null && this.messageMetadata != null)
      this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping(); 
    if (this.wsdlOperationMapping != null)
      return this.wsdlOperationMapping.getJavaMethod(); 
    String str1 = getPayloadLocalPart();
    if (str1 == null) {
      str1 = "";
      str2 = "";
    } else {
      str2 = getPayloadNamespaceURI();
    } 
    QName qName = new QName(str2, str1);
    return paramSEIModel.getJavaMethod(qName);
  }
  
  public boolean isOneWay(@NotNull WSDLPort paramWSDLPort) {
    if (this.isOneWay == null) {
      WSDLBoundOperation wSDLBoundOperation = getOperation(paramWSDLPort);
      if (wSDLBoundOperation != null) {
        this.isOneWay = Boolean.valueOf(wSDLBoundOperation.getOperation().isOneWay());
      } else {
        this.isOneWay = Boolean.valueOf(false);
      } 
    } 
    return this.isOneWay.booleanValue();
  }
  
  public final void assertOneWay(boolean paramBoolean) {
    assert this.isOneWay == null || this.isOneWay.booleanValue() == paramBoolean;
    this.isOneWay = Boolean.valueOf(paramBoolean);
  }
  
  @Nullable
  public abstract String getPayloadLocalPart();
  
  public abstract String getPayloadNamespaceURI();
  
  public abstract boolean hasPayload();
  
  public boolean isFault() {
    String str1 = getPayloadLocalPart();
    if (str1 == null || !str1.equals("Fault"))
      return false; 
    String str2 = getPayloadNamespaceURI();
    return (str2.equals(SOAPVersion.SOAP_11.nsUri) || str2.equals(SOAPVersion.SOAP_12.nsUri));
  }
  
  @Nullable
  public QName getFirstDetailEntryName() {
    assert isFault();
    Message message = copy();
    try {
      SOAPFaultBuilder sOAPFaultBuilder = SOAPFaultBuilder.create(message);
      return sOAPFaultBuilder.getFirstDetailEntryName();
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
  
  public abstract Source readEnvelopeAsSource();
  
  public abstract Source readPayloadAsSource();
  
  public abstract SOAPMessage readAsSOAPMessage() throws SOAPException;
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean) throws SOAPException { return readAsSOAPMessage(); }
  
  public static Map<String, List<String>> getTransportHeaders(Packet paramPacket) { return getTransportHeaders(paramPacket, paramPacket.getState().isInbound()); }
  
  public static Map<String, List<String>> getTransportHeaders(Packet paramPacket, boolean paramBoolean) {
    Map map = null;
    String str = paramBoolean ? "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers" : "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
    if (paramPacket.supports(str))
      map = (Map)paramPacket.get(str); 
    return map;
  }
  
  public static void addSOAPMimeHeaders(MimeHeaders paramMimeHeaders, Map<String, List<String>> paramMap) {
    for (Map.Entry entry : paramMap.entrySet()) {
      if (!((String)entry.getKey()).equalsIgnoreCase("Content-Type"))
        for (String str : (List)entry.getValue())
          paramMimeHeaders.addHeader((String)entry.getKey(), str);  
    } 
  }
  
  public abstract <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException;
  
  public abstract <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException;
  
  public abstract <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException;
  
  public abstract XMLStreamReader readPayload() throws XMLStreamException;
  
  public void consume() {}
  
  public abstract void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException;
  
  public abstract void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException;
  
  public abstract void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException;
  
  public abstract Message copy();
  
  @NotNull
  public String getID(@NotNull WSBinding paramWSBinding) { return getID(paramWSBinding.getAddressingVersion(), paramWSBinding.getSOAPVersion()); }
  
  @NotNull
  public String getID(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion) {
    String str = null;
    if (paramAddressingVersion != null)
      str = AddressingUtils.getMessageID(getHeaders(), paramAddressingVersion, paramSOAPVersion); 
    if (str == null) {
      str = generateMessageID();
      getHeaders().add(new StringHeader(paramAddressingVersion.messageIDTag, str));
    } 
    return str;
  }
  
  public static String generateMessageID() { return "uuid:" + UUID.randomUUID().toString(); }
  
  public SOAPVersion getSOAPVersion() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */