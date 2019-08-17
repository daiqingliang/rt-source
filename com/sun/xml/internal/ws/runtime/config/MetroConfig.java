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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"tubelines", "any"})
@XmlRootElement(name = "metro")
public class MetroConfig {
  protected Tubelines tubelines;
  
  @XmlAnyElement(lax = true)
  protected List<Object> any;
  
  @XmlAttribute(required = true)
  protected String version;
  
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public Tubelines getTubelines() { return this.tubelines; }
  
  public void setTubelines(Tubelines paramTubelines) { this.tubelines = paramTubelines; }
  
  public List<Object> getAny() {
    if (this.any == null)
      this.any = new ArrayList(); 
    return this.any;
  }
  
  public String getVersion() { return this.version; }
  
  public void setVersion(String paramString) { this.version = paramString; }
  
  public Map<QName, String> getOtherAttributes() { return this.otherAttributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\runtime\config\MetroConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */