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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "java-wsdl-mapping-type", propOrder = {"xmlSchemaMapping", "classAnnotation", "javaMethods"})
public class JavaWsdlMappingType {
  @XmlElement(name = "xml-schema-mapping")
  protected XmlSchemaMapping xmlSchemaMapping;
  
  @XmlElementRefs({@XmlElementRef(name = "web-service-client", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebServiceClient.class, required = false), @XmlElementRef(name = "binding-type", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlBindingType.class, required = false), @XmlElementRef(name = "web-service", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebService.class, required = false), @XmlElementRef(name = "web-fault", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlWebFault.class, required = false), @XmlElementRef(name = "service-mode", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlServiceMode.class, required = false), @XmlElementRef(name = "mtom", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlMTOM.class, required = false), @XmlElementRef(name = "handler-chain", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlHandlerChain.class, required = false), @XmlElementRef(name = "soap-binding", namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", type = XmlSOAPBinding.class, required = false)})
  @XmlAnyElement
  protected List<Object> classAnnotation;
  
  @XmlElement(name = "java-methods")
  protected JavaMethods javaMethods;
  
  @XmlAttribute(name = "name")
  protected String name;
  
  @XmlAttribute(name = "java-type-name")
  protected String javaTypeName;
  
  @XmlAttribute(name = "existing-annotations")
  protected ExistingAnnotationsType existingAnnotations;
  
  @XmlAttribute(name = "databinding")
  protected String databinding;
  
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public XmlSchemaMapping getXmlSchemaMapping() { return this.xmlSchemaMapping; }
  
  public void setXmlSchemaMapping(XmlSchemaMapping paramXmlSchemaMapping) { this.xmlSchemaMapping = paramXmlSchemaMapping; }
  
  public List<Object> getClassAnnotation() {
    if (this.classAnnotation == null)
      this.classAnnotation = new ArrayList(); 
    return this.classAnnotation;
  }
  
  public JavaMethods getJavaMethods() { return this.javaMethods; }
  
  public void setJavaMethods(JavaMethods paramJavaMethods) { this.javaMethods = paramJavaMethods; }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String getJavaTypeName() { return this.javaTypeName; }
  
  public void setJavaTypeName(String paramString) { this.javaTypeName = paramString; }
  
  public ExistingAnnotationsType getExistingAnnotations() { return this.existingAnnotations; }
  
  public void setExistingAnnotations(ExistingAnnotationsType paramExistingAnnotationsType) { this.existingAnnotations = paramExistingAnnotationsType; }
  
  public String getDatabinding() { return this.databinding; }
  
  public void setDatabinding(String paramString) { this.databinding = paramString; }
  
  public Map<QName, String> getOtherAttributes() { return this.otherAttributes; }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"javaMethod"})
  public static class JavaMethods {
    @XmlElement(name = "java-method")
    protected List<JavaMethod> javaMethod;
    
    public List<JavaMethod> getJavaMethod() {
      if (this.javaMethod == null)
        this.javaMethod = new ArrayList(); 
      return this.javaMethod;
    }
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = {"any"})
  public static class XmlSchemaMapping {
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    
    public List<Object> getAny() {
      if (this.any == null)
        this.any = new ArrayList(); 
      return this.any;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\JavaWsdlMappingType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */