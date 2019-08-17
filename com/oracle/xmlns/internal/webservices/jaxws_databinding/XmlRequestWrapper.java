package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.RequestWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "request-wrapper")
public class XmlRequestWrapper implements RequestWrapper {
  @XmlAttribute(name = "local-name")
  protected String localName;
  
  @XmlAttribute(name = "target-namespace")
  protected String targetNamespace;
  
  @XmlAttribute(name = "class-name")
  protected String className;
  
  @XmlAttribute(name = "part-name")
  protected String partName;
  
  public String getLocalName() { return (this.localName == null) ? "" : this.localName; }
  
  public void setLocalName(String paramString) { this.localName = paramString; }
  
  public String getTargetNamespace() { return (this.targetNamespace == null) ? "" : this.targetNamespace; }
  
  public void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getClassName() { return (this.className == null) ? "" : this.className; }
  
  public void setClassName(String paramString) { this.className = paramString; }
  
  public String getPartName() { return this.partName; }
  
  public void setPartName(String paramString) { this.partName = paramString; }
  
  public String localName() { return Util.nullSafe(this.localName); }
  
  public String targetNamespace() { return Util.nullSafe(this.targetNamespace); }
  
  public String className() { return Util.nullSafe(this.className); }
  
  public String partName() { return Util.nullSafe(this.partName); }
  
  public Class<? extends Annotation> annotationType() { return RequestWrapper.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlRequestWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */