package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebEndpoint;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-endpoint")
public class XmlWebEndpoint implements WebEndpoint {
  @XmlAttribute(name = "name")
  protected String name;
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String name() { return Util.nullSafe(this.name); }
  
  public Class<? extends Annotation> annotationType() { return WebEndpoint.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */