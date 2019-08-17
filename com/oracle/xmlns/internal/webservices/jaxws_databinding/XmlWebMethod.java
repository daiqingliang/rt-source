package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-method")
public class XmlWebMethod implements WebMethod {
  @XmlAttribute(name = "action")
  protected String action;
  
  @XmlAttribute(name = "exclude")
  protected Boolean exclude;
  
  @XmlAttribute(name = "operation-name")
  protected String operationName;
  
  public String getAction() { return (this.action == null) ? "" : this.action; }
  
  public void setAction(String paramString) { this.action = paramString; }
  
  public boolean isExclude() { return (this.exclude == null) ? false : this.exclude.booleanValue(); }
  
  public void setExclude(Boolean paramBoolean) { this.exclude = paramBoolean; }
  
  public String getOperationName() { return (this.operationName == null) ? "" : this.operationName; }
  
  public void setOperationName(String paramString) { this.operationName = paramString; }
  
  public String operationName() { return Util.nullSafe(this.operationName); }
  
  public String action() { return Util.nullSafe(this.action); }
  
  public boolean exclude() { return ((Boolean)Util.nullSafe(this.exclude, Boolean.valueOf(false))).booleanValue(); }
  
  public Class<? extends Annotation> annotationType() { return WebMethod.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */