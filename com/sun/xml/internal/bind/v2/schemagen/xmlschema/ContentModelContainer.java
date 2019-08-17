package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface ContentModelContainer extends TypedXmlWriter {
  @XmlElement
  LocalElement element();
  
  @XmlElement
  Any any();
  
  @XmlElement
  ExplicitGroup all();
  
  @XmlElement
  ExplicitGroup sequence();
  
  @XmlElement
  ExplicitGroup choice();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\ContentModelContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */