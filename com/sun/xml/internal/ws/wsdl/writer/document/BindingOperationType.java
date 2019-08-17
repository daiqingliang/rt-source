package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPOperation;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPOperation;

public interface BindingOperationType extends TypedXmlWriter, StartWithExtensionsType {
  @XmlAttribute
  BindingOperationType name(String paramString);
  
  @XmlElement(value = "operation", ns = "http://schemas.xmlsoap.org/wsdl/soap/")
  SOAPOperation soapOperation();
  
  @XmlElement(value = "operation", ns = "http://schemas.xmlsoap.org/wsdl/soap12/")
  SOAPOperation soap12Operation();
  
  @XmlElement
  Fault fault();
  
  @XmlElement
  StartWithExtensionsType output();
  
  @XmlElement
  StartWithExtensionsType input();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\BindingOperationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */