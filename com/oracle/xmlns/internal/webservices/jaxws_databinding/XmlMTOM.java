package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.MTOM;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "mtom")
public class XmlMTOM implements MTOM {
  @XmlAttribute(name = "enabled")
  protected Boolean enabled;
  
  @XmlAttribute(name = "threshold")
  protected Integer threshold;
  
  public boolean isEnabled() { return (this.enabled == null) ? true : this.enabled.booleanValue(); }
  
  public void setEnabled(Boolean paramBoolean) { this.enabled = paramBoolean; }
  
  public int getThreshold() { return (this.threshold == null) ? 0 : this.threshold.intValue(); }
  
  public void setThreshold(Integer paramInteger) { this.threshold = paramInteger; }
  
  public boolean enabled() { return ((Boolean)Util.nullSafe(this.enabled, Boolean.TRUE)).booleanValue(); }
  
  public int threshold() { return ((Integer)Util.nullSafe(this.threshold, Integer.valueOf(0))).intValue(); }
  
  public Class<? extends Annotation> annotationType() { return MTOM.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlMTOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */