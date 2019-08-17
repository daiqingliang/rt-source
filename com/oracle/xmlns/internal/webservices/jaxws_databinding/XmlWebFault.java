package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebFault;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-fault")
public class XmlWebFault implements WebFault {
  @XmlAttribute(name = "name")
  protected String name;
  
  @XmlAttribute(name = "targetNamespace")
  protected String targetNamespace;
  
  @XmlAttribute(name = "faultBean")
  protected String faultBean;
  
  @XmlAttribute(name = "messageName")
  protected String messageName;
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String getTargetNamespace() { return this.targetNamespace; }
  
  public void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getFaultBean() { return this.faultBean; }
  
  public void setFaultBean(String paramString) { this.faultBean = paramString; }
  
  public String name() { return Util.nullSafe(this.name); }
  
  public String targetNamespace() { return Util.nullSafe(this.targetNamespace); }
  
  public String faultBean() { return Util.nullSafe(this.faultBean); }
  
  public String messageName() { return Util.nullSafe(this.messageName); }
  
  public Class<? extends Annotation> annotationType() { return WebFault.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebFault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */