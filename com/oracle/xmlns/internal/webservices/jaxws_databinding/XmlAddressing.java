package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "addressing")
public class XmlAddressing implements Addressing {
  @XmlAttribute(name = "enabled")
  protected Boolean enabled;
  
  @XmlAttribute(name = "required")
  protected Boolean required;
  
  public Boolean getEnabled() { return Boolean.valueOf(enabled()); }
  
  public void setEnabled(Boolean paramBoolean) { this.enabled = paramBoolean; }
  
  public Boolean getRequired() { return Boolean.valueOf(required()); }
  
  public void setRequired(Boolean paramBoolean) { this.required = paramBoolean; }
  
  public boolean enabled() { return ((Boolean)Util.nullSafe(this.enabled, Boolean.valueOf(true))).booleanValue(); }
  
  public boolean required() { return ((Boolean)Util.nullSafe(this.required, Boolean.valueOf(false))).booleanValue(); }
  
  public AddressingFeature.Responses responses() { return AddressingFeature.Responses.ALL; }
  
  public Class<? extends Annotation> annotationType() { return Addressing.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlAddressing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */