package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubelineMappingCType", propOrder = {"endpointRef", "tubelineRef", "any"})
public class TubelineMapping {
  @XmlElement(name = "endpoint-ref", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String endpointRef;
  
  @XmlElement(name = "tubeline-ref", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String tubelineRef;
  
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public String getEndpointRef() { return this.endpointRef; }
  
  public void setEndpointRef(String paramString) { this.endpointRef = paramString; }
  
  public String getTubelineRef() { return this.tubelineRef; }
  
  public void setTubelineRef(String paramString) { this.tubelineRef = paramString; }
  
  public List<Object> getAny() {
    if (this.any == null)
      this.any = new ArrayList(); 
    return this.any;
  }
  
  public Map<QName, String> getOtherAttributes() { return this.otherAttributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubelineMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */