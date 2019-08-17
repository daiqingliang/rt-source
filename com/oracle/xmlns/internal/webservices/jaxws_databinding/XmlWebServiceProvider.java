package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceProvider;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service-provider")
public class XmlWebServiceProvider implements WebServiceProvider {
  @XmlAttribute(name = "targetNamespace")
  protected String targetNamespace;
  
  @XmlAttribute(name = "serviceName")
  protected String serviceName;
  
  @XmlAttribute(name = "portName")
  protected String portName;
  
  @XmlAttribute(name = "wsdlLocation")
  protected String wsdlLocation;
  
  public String getTargetNamespace() { return this.targetNamespace; }
  
  public void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getServiceName() { return this.serviceName; }
  
  public void setServiceName(String paramString) { this.serviceName = paramString; }
  
  public String getPortName() { return this.portName; }
  
  public void setPortName(String paramString) { this.portName = paramString; }
  
  public String getWsdlLocation() { return this.wsdlLocation; }
  
  public void setWsdlLocation(String paramString) { this.wsdlLocation = paramString; }
  
  public String wsdlLocation() { return Util.nullSafe(this.wsdlLocation); }
  
  public String serviceName() { return Util.nullSafe(this.serviceName); }
  
  public String targetNamespace() { return Util.nullSafe(this.targetNamespace); }
  
  public String portName() { return Util.nullSafe(this.portName); }
  
  public Class<? extends Annotation> annotationType() { return WebServiceProvider.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */