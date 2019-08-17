package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
  private static final QName _JavaWsdlMapping_QNAME = new QName("http://xmlns.oracle.com/webservices/jaxws-databinding", "java-wsdl-mapping");
  
  public JavaMethod createJavaMethod() { return new JavaMethod(); }
  
  public JavaWsdlMappingType createJavaWsdlMappingType() { return new JavaWsdlMappingType(); }
  
  public XmlWebEndpoint createWebEndpoint() { return new XmlWebEndpoint(); }
  
  public XmlMTOM createMtom() { return new XmlMTOM(); }
  
  public XmlWebServiceClient createWebServiceClient() { return new XmlWebServiceClient(); }
  
  public XmlServiceMode createServiceMode() { return new XmlServiceMode(); }
  
  public XmlBindingType createBindingType() { return new XmlBindingType(); }
  
  public XmlWebServiceRef createWebServiceRef() { return new XmlWebServiceRef(); }
  
  public JavaParam createJavaParam() { return new JavaParam(); }
  
  public XmlWebParam createWebParam() { return new XmlWebParam(); }
  
  public XmlWebMethod createWebMethod() { return new XmlWebMethod(); }
  
  public XmlWebResult createWebResult() { return new XmlWebResult(); }
  
  public XmlOneway createOneway() { return new XmlOneway(); }
  
  public XmlSOAPBinding createSoapBinding() { return new XmlSOAPBinding(); }
  
  public XmlAction createAction() { return new XmlAction(); }
  
  public XmlFaultAction createFaultAction() { return new XmlFaultAction(); }
  
  public JavaMethod.JavaParams createJavaMethodJavaParams() { return new JavaMethod.JavaParams(); }
  
  public XmlHandlerChain createHandlerChain() { return new XmlHandlerChain(); }
  
  public XmlWebServiceProvider createWebServiceProvider() { return new XmlWebServiceProvider(); }
  
  public XmlWebFault createWebFault() { return new XmlWebFault(); }
  
  public XmlResponseWrapper createResponseWrapper() { return new XmlResponseWrapper(); }
  
  public XmlWebService createWebService() { return new XmlWebService(); }
  
  public XmlRequestWrapper createRequestWrapper() { return new XmlRequestWrapper(); }
  
  public JavaWsdlMappingType.XmlSchemaMapping createJavaWsdlMappingTypeXmlSchemaMapping() { return new JavaWsdlMappingType.XmlSchemaMapping(); }
  
  public JavaWsdlMappingType.JavaMethods createJavaWsdlMappingTypeJavaMethods() { return new JavaWsdlMappingType.JavaMethods(); }
  
  @XmlElementDecl(namespace = "http://xmlns.oracle.com/webservices/jaxws-databinding", name = "java-wsdl-mapping")
  public JAXBElement<JavaWsdlMappingType> createJavaWsdlMapping(JavaWsdlMappingType paramJavaWsdlMappingType) { return new JAXBElement(_JavaWsdlMapping_QNAME, JavaWsdlMappingType.class, null, paramJavaWsdlMappingType); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\ObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */