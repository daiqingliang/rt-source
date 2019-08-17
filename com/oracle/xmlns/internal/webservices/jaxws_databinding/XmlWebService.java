package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service")
public class XmlWebService implements WebService {
  @XmlAttribute(name = "endpoint-interface")
  protected String endpointInterface;
  
  @XmlAttribute(name = "name")
  protected String name;
  
  @XmlAttribute(name = "port-name")
  protected String portName;
  
  @XmlAttribute(name = "service-name")
  protected String serviceName;
  
  @XmlAttribute(name = "target-namespace")
  protected String targetNamespace;
  
  @XmlAttribute(name = "wsdl-location")
  protected String wsdlLocation;
  
  public String getEndpointInterface() { return (this.endpointInterface == null) ? "" : this.endpointInterface; }
  
  public void setEndpointInterface(String paramString) { this.endpointInterface = paramString; }
  
  public String getName() { return (this.name == null) ? "" : this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String getPortName() { return (this.portName == null) ? "" : this.portName; }
  
  public void setPortName(String paramString) { this.portName = paramString; }
  
  public String getServiceName() { return (this.serviceName == null) ? "" : this.serviceName; }
  
  public void setServiceName(String paramString) { this.serviceName = paramString; }
  
  public String getTargetNamespace() { return (this.targetNamespace == null) ? "" : this.targetNamespace; }
  
  public void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getWsdlLocation() { return (this.wsdlLocation == null) ? "" : this.wsdlLocation; }
  
  public void setWsdlLocation(String paramString) { this.wsdlLocation = paramString; }
  
  public String name() { return Util.nullSafe(this.name); }
  
  public String targetNamespace() { return Util.nullSafe(this.targetNamespace); }
  
  public String serviceName() { return Util.nullSafe(this.serviceName); }
  
  public String portName() { return Util.nullSafe(this.portName); }
  
  public String wsdlLocation() { return Util.nullSafe(this.wsdlLocation); }
  
  public String endpointInterface() { return Util.nullSafe(this.endpointInterface); }
  
  public Class<? extends Annotation> annotationType() { return WebService.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */