package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;

final class XmlTransientQuick extends Quick implements XmlTransient {
  private final XmlTransient core;
  
  public XmlTransientQuick(Locatable paramLocatable, XmlTransient paramXmlTransient) {
    super(paramLocatable);
    this.core = paramXmlTransient;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlTransientQuick(paramLocatable, (XmlTransient)paramAnnotation); }
  
  public Class<XmlTransient> annotationType() { return XmlTransient.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlTransientQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */