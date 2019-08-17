package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public interface Element extends Annotated, ComplexTypeHost, FixedOrDefault, SimpleTypeHost, TypedXmlWriter {
  @XmlAttribute
  Element type(QName paramQName);
  
  @XmlAttribute
  Element block(String[] paramArrayOfString);
  
  @XmlAttribute
  Element block(String paramString);
  
  @XmlAttribute
  Element nillable(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */