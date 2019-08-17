package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("definitions")
public interface Definitions extends TypedXmlWriter, Documented {
  @XmlAttribute
  Definitions name(String paramString);
  
  @XmlAttribute
  Definitions targetNamespace(String paramString);
  
  @XmlElement
  Service service();
  
  @XmlElement
  Binding binding();
  
  @XmlElement
  PortType portType();
  
  @XmlElement
  Message message();
  
  @XmlElement
  Types types();
  
  @XmlElement("import")
  Import _import();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Definitions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */