package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.BindingType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "binding-type")
public class XmlBindingType implements BindingType {
  @XmlAttribute(name = "value")
  protected String value;
  
  public String getValue() { return this.value; }
  
  public void setValue(String paramString) { this.value = paramString; }
  
  public String value() { return Util.nullSafe(this.value); }
  
  public Class<? extends Annotation> annotationType() { return BindingType.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlBindingType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */