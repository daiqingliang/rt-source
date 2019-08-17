package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubelineDefinitionCType", propOrder = {"clientSide", "endpointSide", "any"})
public class TubelineDefinition {
  @XmlElement(name = "client-side")
  protected TubeFactoryList clientSide;
  
  @XmlElement(name = "endpoint-side")
  protected TubeFactoryList endpointSide;
  
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  @XmlAttribute
  @XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @XmlID
  @XmlSchemaType(name = "ID")
  protected String name;
  
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public TubeFactoryList getClientSide() { return this.clientSide; }
  
  public void setClientSide(TubeFactoryList paramTubeFactoryList) { this.clientSide = paramTubeFactoryList; }
  
  public TubeFactoryList getEndpointSide() { return this.endpointSide; }
  
  public void setEndpointSide(TubeFactoryList paramTubeFactoryList) { this.endpointSide = paramTubeFactoryList; }
  
  public List<Object> getAny() {
    if (this.any == null)
      this.any = new ArrayList(); 
    return this.any;
  }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public Map<QName, String> getOtherAttributes() { return this.otherAttributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubelineDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */