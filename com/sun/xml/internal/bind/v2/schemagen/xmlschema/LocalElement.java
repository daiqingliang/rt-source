package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public interface LocalElement extends Element, Occurs, TypedXmlWriter {
  @XmlAttribute
  LocalElement form(String paramString);
  
  @XmlAttribute
  LocalElement name(String paramString);
  
  @XmlAttribute
  LocalElement ref(QName paramQName);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\LocalElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */