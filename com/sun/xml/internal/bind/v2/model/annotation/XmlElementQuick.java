package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElement;

final class XmlElementQuick extends Quick implements XmlElement {
  private final XmlElement core;
  
  public XmlElementQuick(Locatable paramLocatable, XmlElement paramXmlElement) {
    super(paramLocatable);
    this.core = paramXmlElement;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlElementQuick(paramLocatable, (XmlElement)paramAnnotation); }
  
  public Class<XmlElement> annotationType() { return XmlElement.class; }
  
  public String name() { return this.core.name(); }
  
  public Class type() { return this.core.type(); }
  
  public String namespace() { return this.core.namespace(); }
  
  public String defaultValue() { return this.core.defaultValue(); }
  
  public boolean required() { return this.core.required(); }
  
  public boolean nillable() { return this.core.nillable(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */