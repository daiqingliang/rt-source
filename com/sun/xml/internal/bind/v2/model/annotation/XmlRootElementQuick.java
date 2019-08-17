package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;

final class XmlRootElementQuick extends Quick implements XmlRootElement {
  private final XmlRootElement core;
  
  public XmlRootElementQuick(Locatable paramLocatable, XmlRootElement paramXmlRootElement) {
    super(paramLocatable);
    this.core = paramXmlRootElement;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlRootElementQuick(paramLocatable, (XmlRootElement)paramAnnotation); }
  
  public Class<XmlRootElement> annotationType() { return XmlRootElement.class; }
  
  public String name() { return this.core.name(); }
  
  public String namespace() { return this.core.namespace(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlRootElementQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */