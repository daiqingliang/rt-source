package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementDecl;

final class XmlElementDeclQuick extends Quick implements XmlElementDecl {
  private final XmlElementDecl core;
  
  public XmlElementDeclQuick(Locatable paramLocatable, XmlElementDecl paramXmlElementDecl) {
    super(paramLocatable);
    this.core = paramXmlElementDecl;
  }
  
  protected Annotation getAnnotation() { return this.core; }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation) { return new XmlElementDeclQuick(paramLocatable, (XmlElementDecl)paramAnnotation); }
  
  public Class<XmlElementDecl> annotationType() { return XmlElementDecl.class; }
  
  public String name() { return this.core.name(); }
  
  public Class scope() { return this.core.scope(); }
  
  public String namespace() { return this.core.namespace(); }
  
  public String defaultValue() { return this.core.defaultValue(); }
  
  public String substitutionHeadNamespace() { return this.core.substitutionHeadNamespace(); }
  
  public String substitutionHeadName() { return this.core.substitutionHeadName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementDeclQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */