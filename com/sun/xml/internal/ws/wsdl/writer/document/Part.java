package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("part")
public interface Part extends TypedXmlWriter, OpenAtts {
  @XmlAttribute
  Part element(QName paramQName);
  
  @XmlAttribute
  Part type(QName paramQName);
  
  @XmlAttribute
  Part name(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */