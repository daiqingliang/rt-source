package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(name = "Fault", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"code", "reason", "node", "role", "detail"})
class SOAP12Fault extends SOAPFaultBuilder {
  @XmlTransient
  private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Code")
  private CodeType code;
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Reason")
  private ReasonType reason;
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Node")
  private String node;
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Role")
  private String role;
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope", name = "Detail")
  private DetailType detail;
  
  SOAP12Fault() {}
  
  SOAP12Fault(CodeType paramCodeType, ReasonType paramReasonType, String paramString1, String paramString2, DetailType paramDetailType) {
    this.code = paramCodeType;
    this.reason = paramReasonType;
    this.node = paramString1;
    this.role = paramString2;
    this.detail = paramDetailType;
  }
  
  SOAP12Fault(CodeType paramCodeType, ReasonType paramReasonType, String paramString1, String paramString2, Element paramElement) {
    this.code = paramCodeType;
    this.reason = paramReasonType;
    this.node = paramString1;
    this.role = paramString2;
    if (paramElement != null)
      if (paramElement.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope") && paramElement.getLocalName().equals("Detail")) {
        this.detail = new DetailType();
        for (Element element : DOMUtil.getChildElements(paramElement))
          this.detail.getDetails().add(element); 
      } else {
        this.detail = new DetailType(paramElement);
      }  
  }
  
  SOAP12Fault(SOAPFault paramSOAPFault) {
    this.code = new CodeType(paramSOAPFault.getFaultCodeAsQName());
    try {
      fillFaultSubCodes(paramSOAPFault);
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
    this.reason = new ReasonType(paramSOAPFault.getFaultString());
    this.role = paramSOAPFault.getFaultRole();
    this.node = paramSOAPFault.getFaultNode();
    if (paramSOAPFault.getDetail() != null) {
      this.detail = new DetailType();
      Iterator iterator = paramSOAPFault.getDetail().getDetailEntries();
      while (iterator.hasNext()) {
        Element element = (Element)iterator.next();
        this.detail.getDetails().add(element);
      } 
    } 
  }
  
  SOAP12Fault(QName paramQName, String paramString, Element paramElement) { this(new CodeType(paramQName), new ReasonType(paramString), null, null, paramElement); }
  
  CodeType getCode() { return this.code; }
  
  ReasonType getReason() { return this.reason; }
  
  String getNode() { return this.node; }
  
  String getRole() { return this.role; }
  
  DetailType getDetail() { return this.detail; }
  
  void setDetail(DetailType paramDetailType) { this.detail = paramDetailType; }
  
  String getFaultString() { return ((TextType)this.reason.texts().get(0)).getText(); }
  
  protected Throwable getProtocolException() {
    try {
      SOAPFault sOAPFault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
      if (this.reason != null)
        for (TextType textType : this.reason.texts())
          sOAPFault.setFaultString(textType.getText());  
      if (this.code != null) {
        sOAPFault.setFaultCode(this.code.getValue());
        fillFaultSubCodes(sOAPFault, this.code.getSubcode());
      } 
      if (this.detail != null && this.detail.getDetail(false) != null) {
        Detail detail1 = sOAPFault.addDetail();
        for (Node node1 : this.detail.getDetails()) {
          Node node2 = sOAPFault.getOwnerDocument().importNode(node1, true);
          detail1.appendChild(node2);
        } 
      } 
      if (this.node != null)
        sOAPFault.setFaultNode(this.node); 
      return new ServerSOAPFaultException(sOAPFault);
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  private void fillFaultSubCodes(SOAPFault paramSOAPFault, SubcodeType paramSubcodeType) throws SOAPException {
    if (paramSubcodeType != null) {
      paramSOAPFault.appendFaultSubcode(paramSubcodeType.getValue());
      fillFaultSubCodes(paramSOAPFault, paramSubcodeType.getSubcode());
    } 
  }
  
  private void fillFaultSubCodes(SOAPFault paramSOAPFault) {
    Iterator iterator = paramSOAPFault.getFaultSubcodes();
    for (SubcodeType subcodeType = null; iterator.hasNext(); subcodeType = subcodeType1) {
      QName qName = (QName)iterator.next();
      if (subcodeType == null) {
        subcodeType = new SubcodeType(qName);
        this.code.setSubcode(subcodeType);
        continue;
      } 
      SubcodeType subcodeType1 = new SubcodeType(qName);
      subcodeType.setSubcode(subcodeType1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\SOAP12Fault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */