package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlEnum;

final class XmlEnumQuick extends Quick implements XmlEnum {
  private final XmlEnum core;
  
  public XmlEnumQuick(Locatable paramLocatable, XmlEnum paramXmlEnum) {
    super(paramLocatable);
    this.core = paramXmlEnum;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlEnumQuick(paramLocatable, (XmlEnum)paramAnnotation); }
  
  public Class<XmlEnum> annotationType() { return XmlEnum.class; }
  
  public Class value() { return this.core.value(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlEnumQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */