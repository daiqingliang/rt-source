package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlType;

final class XmlTypeQuick extends Quick implements XmlType {
  private final XmlType core;
  
  public XmlTypeQuick(Locatable paramLocatable, XmlType paramXmlType) {
    super(paramLocatable);
    this.core = paramXmlType;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlTypeQuick(paramLocatable, (XmlType)paramAnnotation); }
  
  public Class<XmlType> annotationType() { return XmlType.class; }
  
  public String name() { return this.core.name(); }
  
  public String namespace() { return this.core.namespace(); }
  
  public String[] propOrder() { return this.core.propOrder(); }
  
  public Class factoryClass() { return this.core.factoryClass(); }
  
  public String factoryMethod() { return this.core.factoryMethod(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlTypeQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */