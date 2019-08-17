package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;

final class XmlElementRefQuick extends Quick implements XmlElementRef {
  private final XmlElementRef core;
  
  public XmlElementRefQuick(Locatable paramLocatable, XmlElementRef paramXmlElementRef) {
    super(paramLocatable);
    this.core = paramXmlElementRef;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlElementRefQuick(paramLocatable, (XmlElementRef)paramAnnotation); }
  
  public Class<XmlElementRef> annotationType() { return XmlElementRef.class; }
  
  public String name() { return this.core.name(); }
  
  public Class type() { return this.core.type(); }
  
  public String namespace() { return this.core.namespace(); }
  
  public boolean required() { return this.core.required(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementRefQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */