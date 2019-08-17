package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;

final class XmlValueQuick extends Quick implements XmlValue {
  private final XmlValue core;
  
  public XmlValueQuick(Locatable paramLocatable, XmlValue paramXmlValue) {
    super(paramLocatable);
    this.core = paramXmlValue;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlValueQuick(paramLocatable, (XmlValue)paramAnnotation); }
  
  public Class<XmlValue> annotationType() { return XmlValue.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlValueQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */