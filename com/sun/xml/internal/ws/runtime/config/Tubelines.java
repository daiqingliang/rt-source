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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tubelinesConfigCType", propOrder = {"tubelineMappings", "tubelineDefinitions", "any"})
public class Tubelines {
  @XmlElement(name = "tubeline-mapping")
  protected List<TubelineMapping> tubelineMappings;
  
  @XmlElement(name = "tubeline")
  protected List<TubelineDefinition> tubelineDefinitions;
  
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  @XmlAttribute(name = "default")
  @XmlSchemaType(name = "anyURI")
  protected String _default;
  
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public List<TubelineMapping> getTubelineMappings() {
    if (this.tubelineMappings == null)
      this.tubelineMappings = new ArrayList(); 
    return this.tubelineMappings;
  }
  
  public List<TubelineDefinition> getTubelineDefinitions() {
    if (this.tubelineDefinitions == null)
      this.tubelineDefinitions = new ArrayList(); 
    return this.tubelineDefinitions;
  }
  
  public List<Object> getAny() {
    if (this.any == null)
      this.any = new ArrayList(); 
    return this.any;
  }
  
  public String getDefault() { return this._default; }
  
  public void setDefault(String paramString) { this._default = paramString; }
  
  public Map<QName, String> getOtherAttributes() { return this.otherAttributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\runtime\config\Tubelines.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */