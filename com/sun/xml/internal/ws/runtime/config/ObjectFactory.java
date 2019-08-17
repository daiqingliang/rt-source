package com.sun.xml.internal.ws.runtime.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
  private static final QName _Tubelines_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubelines");
  
  private static final QName _TubelineMapping_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubeline-mapping");
  
  private static final QName _Tubeline_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubeline");
  
  public TubeFactoryConfig createTubeFactoryConfig() { return new TubeFactoryConfig(); }
  
  public TubeFactoryList createTubeFactoryList() { return new TubeFactoryList(); }
  
  public TubelineDefinition createTubelineDefinition() { return new TubelineDefinition(); }
  
  public Tubelines createTubelines() { return new Tubelines(); }
  
  public MetroConfig createMetroConfig() { return new MetroConfig(); }
  
  public TubelineMapping createTubelineMapping() { return new TubelineMapping(); }
  
  @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/metro/config", name = "tubelines")
  public JAXBElement<Tubelines> createTubelines(Tubelines paramTubelines) { return new JAXBElement(_Tubelines_QNAME, Tubelines.class, null, paramTubelines); }
  
  @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/metro/config", name = "tubeline-mapping")
  public JAXBElement<TubelineMapping> createTubelineMapping(TubelineMapping paramTubelineMapping) { return new JAXBElement(_TubelineMapping_QNAME, TubelineMapping.class, null, paramTubelineMapping); }
  
  @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/metro/config", name = "tubeline")
  public JAXBElement<TubelineDefinition> createTubeline(TubelineDefinition paramTubelineDefinition) { return new JAXBElement(_Tubeline_QNAME, TubelineDefinition.class, null, paramTubelineDefinition); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\runtime\config\ObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */