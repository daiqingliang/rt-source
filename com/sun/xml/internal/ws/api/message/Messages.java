package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.ProblemActionHeader;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.message.source.ProtocolSourceMessage;
import com.sun.xml.internal.ws.message.stream.PayloadStreamReaderMessage;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Messages {
  public static Message create(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion) { return JAXBMessage.create(paramJAXBContext, paramObject, paramSOAPVersion); }
  
  public static Message createRaw(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion) { return JAXBMessage.createRaw(paramJAXBContext, paramObject, paramSOAPVersion); }
  
  public static Message create(Marshaller paramMarshaller, Object paramObject, SOAPVersion paramSOAPVersion) { return create(BindingContextFactory.getBindingContext(paramMarshaller).getJAXBContext(), paramObject, paramSOAPVersion); }
  
  public static Message create(SOAPMessage paramSOAPMessage) { return SAAJFactory.create(paramSOAPMessage); }
  
  public static Message createUsingPayload(Source paramSource, SOAPVersion paramSOAPVersion) {
    if (paramSource instanceof DOMSource) {
      if (((DOMSource)paramSource).getNode() == null)
        return new EmptyMessageImpl(paramSOAPVersion); 
    } else if (paramSource instanceof StreamSource) {
      StreamSource streamSource = (StreamSource)paramSource;
      if (streamSource.getInputStream() == null && streamSource.getReader() == null && streamSource.getSystemId() == null)
        return new EmptyMessageImpl(paramSOAPVersion); 
    } else if (paramSource instanceof SAXSource) {
      SAXSource sAXSource = (SAXSource)paramSource;
      if (sAXSource.getInputSource() == null && sAXSource.getXMLReader() == null)
        return new EmptyMessageImpl(paramSOAPVersion); 
    } 
    return new PayloadSourceMessage(paramSource, paramSOAPVersion);
  }
  
  public static Message createUsingPayload(XMLStreamReader paramXMLStreamReader, SOAPVersion paramSOAPVersion) { return new PayloadStreamReaderMessage(paramXMLStreamReader, paramSOAPVersion); }
  
  public static Message createUsingPayload(Element paramElement, SOAPVersion paramSOAPVersion) { return new DOMMessage(paramSOAPVersion, paramElement); }
  
  public static Message create(Element paramElement) {
    SOAPVersion sOAPVersion = SOAPVersion.fromNsUri(paramElement.getNamespaceURI());
    Element element1 = DOMUtil.getFirstChild(paramElement, sOAPVersion.nsUri, "Header");
    HeaderList headerList = null;
    if (element1 != null)
      for (Node node = element1.getFirstChild(); node != null; node = node.getNextSibling()) {
        if (node.getNodeType() == 1) {
          if (headerList == null)
            headerList = new HeaderList(sOAPVersion); 
          headerList.add(Headers.create((Element)node));
        } 
      }  
    Element element2 = DOMUtil.getFirstChild(paramElement, sOAPVersion.nsUri, "Body");
    if (element2 == null)
      throw new WebServiceException("Message doesn't have <S:Body> " + paramElement); 
    Element element3 = DOMUtil.getFirstChild(paramElement, sOAPVersion.nsUri, "Body");
    return (element3 == null) ? new EmptyMessageImpl(headerList, new AttachmentSetImpl(), sOAPVersion) : new DOMMessage(sOAPVersion, headerList, element3);
  }
  
  public static Message create(Source paramSource, SOAPVersion paramSOAPVersion) { return new ProtocolSourceMessage(paramSource, paramSOAPVersion); }
  
  public static Message createEmpty(SOAPVersion paramSOAPVersion) { return new EmptyMessageImpl(paramSOAPVersion); }
  
  @NotNull
  public static Message create(@NotNull XMLStreamReader paramXMLStreamReader) {
    if (paramXMLStreamReader.getEventType() != 1)
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader); 
    assert paramXMLStreamReader.getEventType() == 1 : paramXMLStreamReader.getEventType();
    SOAPVersion sOAPVersion = SOAPVersion.fromNsUri(paramXMLStreamReader.getNamespaceURI());
    return Codecs.createSOAPEnvelopeXmlCodec(sOAPVersion).decode(paramXMLStreamReader);
  }
  
  @NotNull
  public static Message create(@NotNull XMLStreamBuffer paramXMLStreamBuffer) {
    try {
      return create(paramXMLStreamBuffer.readAsXMLStreamReader());
    } catch (XMLStreamException xMLStreamException) {
      throw new XMLStreamReaderException(xMLStreamException);
    } 
  }
  
  public static Message create(Throwable paramThrowable, SOAPVersion paramSOAPVersion) { return SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, null, paramThrowable); }
  
  public static Message create(SOAPFault paramSOAPFault) {
    SOAPVersion sOAPVersion = SOAPVersion.fromNsUri(paramSOAPFault.getNamespaceURI());
    return new DOMMessage(sOAPVersion, paramSOAPFault);
  }
  
  public static Message createAddressingFaultMessage(WSBinding paramWSBinding, QName paramQName) { return createAddressingFaultMessage(paramWSBinding, null, paramQName); }
  
  public static Message createAddressingFaultMessage(WSBinding paramWSBinding, Packet paramPacket, QName paramQName) {
    AddressingVersion addressingVersion = paramWSBinding.getAddressingVersion();
    if (addressingVersion == null)
      throw new WebServiceException(AddressingMessages.ADDRESSING_SHOULD_BE_ENABLED()); 
    WsaTubeHelper wsaTubeHelper = addressingVersion.getWsaHelper(null, null, paramWSBinding);
    return create(wsaTubeHelper.newMapRequiredFault(new MissingAddressingHeaderException(paramQName, paramPacket)));
  }
  
  public static Message create(@NotNull String paramString, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) {
    Message message;
    QName qName = paramAddressingVersion.actionNotSupportedTag;
    String str = String.format(paramAddressingVersion.actionNotSupportedText, new Object[] { paramString });
    try {
      SOAPFault sOAPFault;
      if (paramSOAPVersion == SOAPVersion.SOAP_12) {
        sOAPFault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
        sOAPFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        sOAPFault.appendFaultSubcode(qName);
        Detail detail = sOAPFault.addDetail();
        SOAPElement sOAPElement = detail.addChildElement(paramAddressingVersion.problemActionTag);
        sOAPElement = sOAPElement.addChildElement(paramAddressingVersion.actionTag);
        sOAPElement.addTextNode(paramString);
      } else {
        sOAPFault = SOAPVersion.SOAP_11.getSOAPFactory().createFault();
        sOAPFault.setFaultCode(qName);
      } 
      sOAPFault.setFaultString(str);
      message = SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, sOAPFault);
      if (paramSOAPVersion == SOAPVersion.SOAP_11)
        message.getHeaders().add(new ProblemActionHeader(paramString, paramAddressingVersion)); 
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
    return message;
  }
  
  @NotNull
  public static Message create(@NotNull SOAPVersion paramSOAPVersion, @NotNull ProtocolException paramProtocolException, @Nullable QName paramQName) { return SOAPFaultBuilder.createSOAPFaultMessage(paramSOAPVersion, paramProtocolException, paramQName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */