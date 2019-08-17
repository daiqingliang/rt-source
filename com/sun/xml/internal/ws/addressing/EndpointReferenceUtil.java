package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EndpointReferenceUtil {
  private static boolean w3cMetadataWritten = false;
  
  public static <T extends EndpointReference> T transform(Class<T> paramClass, @NotNull EndpointReference paramEndpointReference) {
    assert paramEndpointReference != null;
    if (paramClass.isAssignableFrom(W3CEndpointReference.class)) {
      if (paramEndpointReference instanceof W3CEndpointReference)
        return (T)paramEndpointReference; 
      if (paramEndpointReference instanceof MemberSubmissionEndpointReference)
        return (T)toW3CEpr((MemberSubmissionEndpointReference)paramEndpointReference); 
    } else if (paramClass.isAssignableFrom(MemberSubmissionEndpointReference.class)) {
      if (paramEndpointReference instanceof W3CEndpointReference)
        return (T)toMSEpr((W3CEndpointReference)paramEndpointReference); 
      if (paramEndpointReference instanceof MemberSubmissionEndpointReference)
        return (T)paramEndpointReference; 
    } 
    throw new WebServiceException("Unknwon EndpointReference: " + paramEndpointReference.getClass());
  }
  
  private static W3CEndpointReference toW3CEpr(MemberSubmissionEndpointReference paramMemberSubmissionEndpointReference) {
    StreamWriterBufferCreator streamWriterBufferCreator = new StreamWriterBufferCreator();
    w3cMetadataWritten = false;
    try {
      streamWriterBufferCreator.writeStartDocument();
      streamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
      streamWriterBufferCreator.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
      streamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
      streamWriterBufferCreator.writeCharacters(paramMemberSubmissionEndpointReference.addr.uri);
      streamWriterBufferCreator.writeEndElement();
      if ((paramMemberSubmissionEndpointReference.referenceProperties != null && paramMemberSubmissionEndpointReference.referenceProperties.elements.size() > 0) || (paramMemberSubmissionEndpointReference.referenceParameters != null && paramMemberSubmissionEndpointReference.referenceParameters.elements.size() > 0)) {
        streamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), "ReferenceParameters", AddressingVersion.W3C.nsUri);
        if (paramMemberSubmissionEndpointReference.referenceProperties != null)
          for (Element element1 : paramMemberSubmissionEndpointReference.referenceProperties.elements)
            DOMUtil.serializeNode(element1, streamWriterBufferCreator);  
        if (paramMemberSubmissionEndpointReference.referenceParameters != null)
          for (Element element1 : paramMemberSubmissionEndpointReference.referenceParameters.elements)
            DOMUtil.serializeNode(element1, streamWriterBufferCreator);  
        streamWriterBufferCreator.writeEndElement();
      } 
      Element element = null;
      if (paramMemberSubmissionEndpointReference.elements != null && paramMemberSubmissionEndpointReference.elements.size() > 0)
        for (Element element1 : paramMemberSubmissionEndpointReference.elements) {
          if (element1.getNamespaceURI().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI()) && element1.getLocalName().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart())) {
            NodeList nodeList = element1.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
            if (nodeList != null)
              element = (Element)nodeList.item(0); 
          } 
        }  
      if (element != null)
        DOMUtil.serializeNode(element, streamWriterBufferCreator); 
      if (w3cMetadataWritten)
        streamWriterBufferCreator.writeEndElement(); 
      if (paramMemberSubmissionEndpointReference.elements != null && paramMemberSubmissionEndpointReference.elements.size() > 0)
        for (Element element1 : paramMemberSubmissionEndpointReference.elements) {
          if (!element1.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") || element1.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart()));
          DOMUtil.serializeNode(element1, streamWriterBufferCreator);
        }  
      streamWriterBufferCreator.writeEndElement();
      streamWriterBufferCreator.writeEndDocument();
      streamWriterBufferCreator.flush();
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
    return new W3CEndpointReference(new XMLStreamBufferSource(streamWriterBufferCreator.getXMLStreamBuffer()));
  }
  
  private static MemberSubmissionEndpointReference toMSEpr(W3CEndpointReference paramW3CEndpointReference) {
    DOMResult dOMResult = new DOMResult();
    paramW3CEndpointReference.writeTo(dOMResult);
    Node node = dOMResult.getNode();
    Element element = DOMUtil.getFirstElementChild(node);
    if (element == null)
      return null; 
    MemberSubmissionEndpointReference memberSubmissionEndpointReference = new MemberSubmissionEndpointReference();
    NodeList nodeList = element.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      if (nodeList.item(b).getNodeType() == 1) {
        Element element1 = (Element)nodeList.item(b);
        if (element1.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && element1.getLocalName().equals(AddressingVersion.W3C.eprType.address)) {
          if (memberSubmissionEndpointReference.addr == null)
            memberSubmissionEndpointReference.addr = new MemberSubmissionEndpointReference.Address(); 
          memberSubmissionEndpointReference.addr.uri = XmlUtil.getTextForNode(element1);
        } else if (element1.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && element1.getLocalName().equals("ReferenceParameters")) {
          NodeList nodeList1 = element1.getChildNodes();
          for (byte b1 = 0; b1 < nodeList1.getLength(); b1++) {
            if (nodeList1.item(b1).getNodeType() == 1) {
              if (memberSubmissionEndpointReference.referenceParameters == null) {
                memberSubmissionEndpointReference.referenceParameters = new MemberSubmissionEndpointReference.Elements();
                memberSubmissionEndpointReference.referenceParameters.elements = new ArrayList();
              } 
              memberSubmissionEndpointReference.referenceParameters.elements.add((Element)nodeList1.item(b1));
            } 
          } 
        } else if (element1.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && element1.getLocalName().equals(AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart())) {
          NodeList nodeList1 = element1.getChildNodes();
          String str = element1.getAttributeNS("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
          Element element2 = null;
          for (byte b1 = 0; b1 < nodeList1.getLength(); b1++) {
            Node node1 = nodeList1.item(b1);
            if (node1.getNodeType() == 1) {
              Element element5 = (Element)node1;
              if ((element5.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || element5.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && element5.getLocalName().equals(AddressingVersion.W3C.eprType.serviceName)) {
                memberSubmissionEndpointReference.serviceName = new MemberSubmissionEndpointReference.ServiceNameType();
                memberSubmissionEndpointReference.serviceName.portName = element5.getAttribute(AddressingVersion.W3C.eprType.portName);
                String str1 = element5.getTextContent();
                String str2 = XmlUtil.getPrefix(str1);
                String str3 = XmlUtil.getLocalPart(str1);
                if (str3 != null) {
                  if (str2 != null) {
                    String str4 = element5.lookupNamespaceURI(str2);
                    if (str4 != null)
                      memberSubmissionEndpointReference.serviceName.name = new QName(str4, str3, str2); 
                  } else {
                    memberSubmissionEndpointReference.serviceName.name = new QName(null, str3);
                  } 
                  memberSubmissionEndpointReference.serviceName.attributes = getAttributes(element5);
                } 
              } else if ((element5.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || element5.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && element5.getLocalName().equals(AddressingVersion.W3C.eprType.portTypeName)) {
                memberSubmissionEndpointReference.portTypeName = new MemberSubmissionEndpointReference.AttributedQName();
                String str1 = element5.getTextContent();
                String str2 = XmlUtil.getPrefix(str1);
                String str3 = XmlUtil.getLocalPart(str1);
                if (str3 != null) {
                  if (str2 != null) {
                    String str4 = element5.lookupNamespaceURI(str2);
                    if (str4 != null)
                      memberSubmissionEndpointReference.portTypeName.name = new QName(str4, str3, str2); 
                  } else {
                    memberSubmissionEndpointReference.portTypeName.name = new QName(null, str3);
                  } 
                  memberSubmissionEndpointReference.portTypeName.attributes = getAttributes(element5);
                } 
              } else if (element5.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && element5.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                element2 = element5;
              } else {
                if (memberSubmissionEndpointReference.elements == null)
                  memberSubmissionEndpointReference.elements = new ArrayList(); 
                memberSubmissionEndpointReference.elements.add(element5);
              } 
            } 
          } 
          Document document = DOMUtil.createDom();
          Element element3 = document.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart());
          Element element4 = document.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart());
          element4.setAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
          if (element2 == null && str != null && !str.equals("")) {
            str = str.trim();
            String str1 = str.substring(0, str.indexOf(' '));
            str = str.substring(str.indexOf(' ') + 1);
            Element element5 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
            Element element6 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_IMPORT.getLocalPart());
            element6.setAttribute("namespace", str1);
            element6.setAttribute("location", str);
            element5.appendChild(element6);
            element4.appendChild(element5);
          } else if (element2 != null) {
            element4.appendChild(element2);
          } 
          element3.appendChild(element4);
          if (memberSubmissionEndpointReference.elements == null)
            memberSubmissionEndpointReference.elements = new ArrayList(); 
          memberSubmissionEndpointReference.elements.add(element3);
        } else {
          if (memberSubmissionEndpointReference.elements == null)
            memberSubmissionEndpointReference.elements = new ArrayList(); 
          memberSubmissionEndpointReference.elements.add(element1);
        } 
      } else if (nodeList.item(b).getNodeType() == 2) {
        Node node1 = nodeList.item(b);
        if (memberSubmissionEndpointReference.attributes == null) {
          memberSubmissionEndpointReference.attributes = new HashMap();
          String str1 = fixNull(node1.getPrefix());
          String str2 = fixNull(node1.getNamespaceURI());
          String str3 = node1.getLocalName();
          memberSubmissionEndpointReference.attributes.put(new QName(str2, str3, str1), node1.getNodeValue());
        } 
      } 
    } 
    return memberSubmissionEndpointReference;
  }
  
  private static Map<QName, String> getAttributes(Node paramNode) {
    HashMap hashMap = null;
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    for (byte b = 0; b < namedNodeMap.getLength(); b++) {
      if (hashMap == null)
        hashMap = new HashMap(); 
      Node node = namedNodeMap.item(b);
      String str1 = fixNull(node.getPrefix());
      String str2 = fixNull(node.getNamespaceURI());
      String str3 = node.getLocalName();
      if (!str1.equals("xmlns") && (str1.length() != 0 || !str3.equals("xmlns")) && !str3.equals(AddressingVersion.W3C.eprType.portName))
        hashMap.put(new QName(str2, str3, str1), node.getNodeValue()); 
    } 
    return hashMap;
  }
  
  @NotNull
  private static String fixNull(@Nullable String paramString) { return (paramString == null) ? "" : paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\EndpointReferenceUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */