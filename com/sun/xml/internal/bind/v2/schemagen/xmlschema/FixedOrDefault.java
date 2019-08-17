package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface FixedOrDefault extends TypedXmlWriter {
  @XmlAttribute("default")
  FixedOrDefault _default(String paramString);
  
  @XmlAttribute
  FixedOrDefault fixed(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\FixedOrDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */