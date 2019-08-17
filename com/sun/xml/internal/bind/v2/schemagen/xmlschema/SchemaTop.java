package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface SchemaTop extends Redefinable, TypedXmlWriter {
  @XmlElement
  TopLevelAttribute attribute();
  
  @XmlElement
  TopLevelElement element();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\SchemaTop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */