package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("schema")
public interface Schema extends SchemaTop, TypedXmlWriter {
  @XmlElement
  Annotation annotation();
  
  @XmlElement("import")
  Import _import();
  
  @XmlAttribute
  Schema targetNamespace(String paramString);
  
  @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
  Schema lang(String paramString);
  
  @XmlAttribute
  Schema id(String paramString);
  
  @XmlAttribute
  Schema elementFormDefault(String paramString);
  
  @XmlAttribute
  Schema attributeFormDefault(String paramString);
  
  @XmlAttribute
  Schema blockDefault(String[] paramArrayOfString);
  
  @XmlAttribute
  Schema blockDefault(String paramString);
  
  @XmlAttribute
  Schema finalDefault(String[] paramArrayOfString);
  
  @XmlAttribute
  Schema finalDefault(String paramString);
  
  @XmlAttribute
  Schema version(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Schema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */