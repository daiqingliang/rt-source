package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceClient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service-client")
public class XmlWebServiceClient implements WebServiceClient {
  @XmlAttribute(name = "name")
  protected String name;
  
  @XmlAttribute(name = "targetNamespace")
  protected String targetNamespace;
  
  @XmlAttribute(name = "wsdlLocation")
  protected String wsdlLocation;
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String getTargetNamespace() { return this.targetNamespace; }
  
  public void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getWsdlLocation() { return this.wsdlLocation; }
  
  public void setWsdlLocation(String paramString) { this.wsdlLocation = paramString; }
  
  public String name() { return Util.nullSafe(this.name); }
  
  public String targetNamespace() { return Util.nullSafe(this.targetNamespace); }
  
  public String wsdlLocation() { return Util.nullSafe(this.wsdlLocation); }
  
  public Class<? extends Annotation> annotationType() { return WebServiceClient.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebServiceClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */