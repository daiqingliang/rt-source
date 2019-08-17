package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"paramAnnotation"})
@XmlRootElement(name = "java-param")
public class JavaParam {
  @XmlElementRef(name = "web-param", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebParam.class, required = false)
  @XmlAnyElement
  protected List<Object> paramAnnotation;
  
  @XmlAttribute(name = "java-type")
  protected String javaType;
  
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public List<Object> getParamAnnotation() {
    if (this.paramAnnotation == null)
      this.paramAnnotation = new ArrayList(); 
    return this.paramAnnotation;
  }
  
  public String getJavaType() { return this.javaType; }
  
  public void setJavaType(String paramString) { this.javaType = paramString; }
  
  public Map<QName, String> getOtherAttributes() { return this.otherAttributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\JavaParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */