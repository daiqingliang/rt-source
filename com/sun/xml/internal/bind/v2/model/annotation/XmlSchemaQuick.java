package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

final class XmlSchemaQuick extends Quick implements XmlSchema {
  private final XmlSchema core;
  
  public XmlSchemaQuick(Locatable paramLocatable, XmlSchema paramXmlSchema) {
    super(paramLocatable);
    this.core = paramXmlSchema;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlSchemaQuick(paramLocatable, (XmlSchema)paramAnnotation); }
  
  public Class<XmlSchema> annotationType() { return XmlSchema.class; }
  
  public String location() { return this.core.location(); }
  
  public String namespace() { return this.core.namespace(); }
  
  public XmlNs[] xmlns() { return this.core.xmlns(); }
  
  public XmlNsForm elementFormDefault() { return this.core.elementFormDefault(); }
  
  public XmlNsForm attributeFormDefault() { return this.core.attributeFormDefault(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlSchemaQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */