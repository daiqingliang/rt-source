package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.FaultAction;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "fault-action")
public class XmlFaultAction implements FaultAction {
  @XmlAttribute(name = "className", required = true)
  protected String className;
  
  @XmlAttribute(name = "value")
  protected String value;
  
  public String getClassName() { return this.className; }
  
  public void setClassName(String paramString) { this.className = paramString; }
  
  public String getValue() { return Util.nullSafe(this.value); }
  
  public void setValue(String paramString) { this.value = paramString; }
  
  public Class<? extends Exception> className() { return Util.findClass(this.className); }
  
  public String value() { return Util.nullSafe(this.value); }
  
  public Class<? extends Annotation> annotationType() { return FaultAction.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlFaultAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */