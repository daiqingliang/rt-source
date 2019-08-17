package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public interface TopLevelElement extends Element, TypedXmlWriter {
  @XmlAttribute("final")
  TopLevelElement _final(String[] paramArrayOfString);
  
  @XmlAttribute("final")
  TopLevelElement _final(String paramString);
  
  @XmlAttribute("abstract")
  TopLevelElement _abstract(boolean paramBoolean);
  
  @XmlAttribute
  TopLevelElement substitutionGroup(QName paramQName);
  
  @XmlAttribute
  TopLevelElement name(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\TopLevelElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */