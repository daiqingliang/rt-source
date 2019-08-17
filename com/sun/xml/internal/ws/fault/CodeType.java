package com.sun.xml.internal.ws.fault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodeType", namespace = "http://www.w3.org/2003/05/soap-envelope", propOrder = {"Value", "Subcode"})
class CodeType {
  @XmlTransient
  private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope")
  private QName Value;
  
  @XmlElement(namespace = "http://www.w3.org/2003/05/soap-envelope")
  private SubcodeType Subcode;
  
  CodeType(QName paramQName) { this.Value = paramQName; }
  
  CodeType() {}
  
  QName getValue() { return this.Value; }
  
  SubcodeType getSubcode() { return this.Subcode; }
  
  void setSubcode(SubcodeType paramSubcodeType) { this.Subcode = paramSubcodeType; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\CodeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */