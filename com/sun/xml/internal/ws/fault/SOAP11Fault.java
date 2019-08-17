package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"faultcode", "faultstring", "faultactor", "detail"})
@XmlRootElement(name = "Fault", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
class SOAP11Fault extends SOAPFaultBuilder {
  @XmlElement(namespace = "")
  private QName faultcode;
  
  @XmlElement(namespace = "")
  private String faultstring;
  
  @XmlElement(namespace = "")
  private String faultactor;
  
  @XmlElement(namespace = "")
  private DetailType detail;
  
  SOAP11Fault() {}
  
  SOAP11Fault(QName paramQName, String paramString1, String paramString2, Element paramElement) {
    this.faultcode = paramQName;
    this.faultstring = paramString1;
    this.faultactor = paramString2;
    if (paramElement != null)
      if ((paramElement.getNamespaceURI() == null || "".equals(paramElement.getNamespaceURI())) && "detail".equals(paramElement.getLocalName())) {
        this.detail = new DetailType();
        for (Element element : DOMUtil.getChildElements(paramElement))
          this.detail.getDetails().add(element); 
      } else {
        this.detail = new DetailType(paramElement);
      }  
  }
  
  SOAP11Fault(SOAPFault paramSOAPFault) {
    this.faultcode = paramSOAPFault.getFaultCodeAsQName();
    this.faultstring = paramSOAPFault.getFaultString();
    this.faultactor = paramSOAPFault.getFaultActor();
    if (paramSOAPFault.getDetail() != null) {
      this.detail = new DetailType();
      Iterator iterator = paramSOAPFault.getDetail().getDetailEntries();
      while (iterator.hasNext()) {
        Element element = (Element)iterator.next();
        this.detail.getDetails().add(element);
      } 
    } 
  }
  
  QName getFaultcode() { return this.faultcode; }
  
  void setFaultcode(QName paramQName) { this.faultcode = paramQName; }
  
  String getFaultString() { return this.faultstring; }
  
  void setFaultstring(String paramString) { this.faultstring = paramString; }
  
  String getFaultactor() { return this.faultactor; }
  
  void setFaultactor(String paramString) { this.faultactor = paramString; }
  
  DetailType getDetail() { return this.detail; }
  
  void setDetail(DetailType paramDetailType) { this.detail = paramDetailType; }
  
  protected Throwable getProtocolException() {
    try {
      SOAPFault sOAPFault = SOAPVersion.SOAP_11.getSOAPFactory().createFault(this.faultstring, this.faultcode);
      sOAPFault.setFaultActor(this.faultactor);
      if (this.detail != null) {
        Detail detail1 = sOAPFault.addDetail();
        for (Element element : this.detail.getDetails()) {
          Node node = sOAPFault.getOwnerDocument().importNode(element, true);
          detail1.appendChild(node);
        } 
      } 
      return new ServerSOAPFaultException(sOAPFault);
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\SOAP11Fault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */