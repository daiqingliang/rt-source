package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlSchemaType;

final class XmlSchemaTypeQuick extends Quick implements XmlSchemaType {
  private final XmlSchemaType core;
  
  public XmlSchemaTypeQuick(Locatable paramLocatable, XmlSchemaType paramXmlSchemaType) {
    super(paramLocatable);
    this.core = paramXmlSchemaType;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlSchemaTypeQuick(paramLocatable, (XmlSchemaType)paramAnnotation); }
  
  public Class<XmlSchemaType> annotationType() { return XmlSchemaType.class; }
  
  public String name() { return this.core.name(); }
  
  public Class type() { return this.core.type(); }
  
  public String namespace() { return this.core.namespace(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlSchemaTypeQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */