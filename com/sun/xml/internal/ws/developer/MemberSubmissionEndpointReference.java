package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

@XmlRootElement(name = "EndpointReference", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
@XmlType(name = "EndpointReferenceType", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
public final class MemberSubmissionEndpointReference extends EndpointReference implements MemberSubmissionAddressingConstants {
  private static final ContextClassloaderLocal<JAXBContext> msjc = new ContextClassloaderLocal<JAXBContext>() {
      protected JAXBContext initialValue() { return MemberSubmissionEndpointReference.getMSJaxbContext(); }
    };
  
  @XmlElement(name = "Address", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public Address addr;
  
  @XmlElement(name = "ReferenceProperties", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public Elements referenceProperties;
  
  @XmlElement(name = "ReferenceParameters", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public Elements referenceParameters;
  
  @XmlElement(name = "PortType", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public AttributedQName portTypeName;
  
  @XmlElement(name = "ServiceName", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public ServiceNameType serviceName;
  
  @XmlAnyAttribute
  public Map<QName, String> attributes;
  
  @XmlAnyElement
  public List<Element> elements;
  
  protected static final String MSNS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
  
  public MemberSubmissionEndpointReference() {}
  
  public MemberSubmissionEndpointReference(@NotNull Source paramSource) {
    if (paramSource == null)
      throw new WebServiceException("Source parameter can not be null on constructor"); 
    try {
      Unmarshaller unmarshaller = ((JAXBContext)msjc.get()).createUnmarshaller();
      MemberSubmissionEndpointReference memberSubmissionEndpointReference = (MemberSubmissionEndpointReference)unmarshaller.unmarshal(paramSource, MemberSubmissionEndpointReference.class).getValue();
      this.addr = memberSubmissionEndpointReference.addr;
      this.referenceProperties = memberSubmissionEndpointReference.referenceProperties;
      this.referenceParameters = memberSubmissionEndpointReference.referenceParameters;
      this.portTypeName = memberSubmissionEndpointReference.portTypeName;
      this.serviceName = memberSubmissionEndpointReference.serviceName;
      this.attributes = memberSubmissionEndpointReference.attributes;
      this.elements = memberSubmissionEndpointReference.elements;
    } catch (JAXBException jAXBException) {
      throw new WebServiceException("Error unmarshalling MemberSubmissionEndpointReference ", jAXBException);
    } catch (ClassCastException classCastException) {
      throw new WebServiceException("Source did not contain MemberSubmissionEndpointReference", classCastException);
    } 
  }
  
  public void writeTo(Result paramResult) {
    try {
      Marshaller marshaller = ((JAXBContext)msjc.get()).createMarshaller();
      marshaller.marshal(this, paramResult);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException("Error marshalling W3CEndpointReference. ", jAXBException);
    } 
  }
  
  public Source toWSDLSource() {
    Element element = null;
    for (Element element1 : this.elements) {
      if (element1.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && element1.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart()))
        element = element1; 
    } 
    return new DOMSource(element);
  }
  
  private static JAXBContext getMSJaxbContext() {
    try {
      return JAXBContext.newInstance(new Class[] { MemberSubmissionEndpointReference.class });
    } catch (JAXBException jAXBException) {
      throw new WebServiceException("Error creating JAXBContext for MemberSubmissionEndpointReference. ", jAXBException);
    } 
  }
  
  @XmlType(name = "address", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public static class Address {
    @XmlValue
    public String uri;
    
    @XmlAnyAttribute
    public Map<QName, String> attributes;
  }
  
  public static class AttributedQName {
    @XmlValue
    public QName name;
    
    @XmlAnyAttribute
    public Map<QName, String> attributes;
  }
  
  @XmlType(name = "elements", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public static class Elements {
    @XmlAnyElement
    public List<Element> elements;
  }
  
  public static class ServiceNameType extends AttributedQName {
    @XmlAttribute(name = "PortName")
    public String portName;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\MemberSubmissionEndpointReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */