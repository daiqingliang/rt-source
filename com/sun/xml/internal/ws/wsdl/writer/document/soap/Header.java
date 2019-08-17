package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("header")
public interface Header extends TypedXmlWriter, BodyType {
  @XmlAttribute
  Header message(QName paramQName);
  
  @XmlElement
  HeaderFault headerFault();
  
  @XmlAttribute
  BodyType part(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\soap\Header.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */