package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;

@XmlElement("types")
public interface Types extends TypedXmlWriter, Documented {
  @XmlElement(value = "schema", ns = "http://www.w3.org/2001/XMLSchema")
  Schema schema();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */