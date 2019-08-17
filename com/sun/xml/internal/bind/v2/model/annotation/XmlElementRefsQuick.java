package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

final class XmlElementRefsQuick extends Quick implements XmlElementRefs {
  private final XmlElementRefs core;
  
  public XmlElementRefsQuick(Locatable paramLocatable, XmlElementRefs paramXmlElementRefs) {
    super(paramLocatable);
    this.core = paramXmlElementRefs;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlElementRefsQuick(paramLocatable, (XmlElementRefs)paramAnnotation); }
  
  public Class<XmlElementRefs> annotationType() { return XmlElementRefs.class; }
  
  public XmlElementRef[] value() { return this.core.value(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementRefsQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */