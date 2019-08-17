package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("fault")
public interface SOAPFault extends TypedXmlWriter, BodyType {
  @XmlAttribute
  SOAPFault name(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\soap\SOAPFault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */