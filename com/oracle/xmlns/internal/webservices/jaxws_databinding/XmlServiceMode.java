package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "service-mode")
public class XmlServiceMode implements ServiceMode {
  @XmlAttribute(name = "value")
  protected String value;
  
  public String getValue() { return (this.value == null) ? "PAYLOAD" : this.value; }
  
  public void setValue(String paramString) { this.value = paramString; }
  
  public Service.Mode value() { return Service.Mode.valueOf((String)Util.nullSafe(this.value, "PAYLOAD")); }
  
  public Class<? extends Annotation> annotationType() { return ServiceMode.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlServiceMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */