package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("union")
public interface Union extends Annotated, SimpleTypeHost, TypedXmlWriter {
  @XmlAttribute
  Union memberTypes(QName[] paramArrayOfQName);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Union.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */