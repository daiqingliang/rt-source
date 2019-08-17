package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingConfig {
  protected Class contractClass;
  
  protected Class endpointClass;
  
  protected Set<Class> additionalValueTypes = new HashSet();
  
  protected MappingInfo mappingInfo = new MappingInfo();
  
  protected URL wsdlURL;
  
  protected ClassLoader classLoader;
  
  protected Iterable<WebServiceFeature> features;
  
  protected WSBinding wsBinding;
  
  protected WSDLPort wsdlPort;
  
  protected MetadataReader metadataReader;
  
  protected Map<String, Object> properties = new HashMap();
  
  protected Source wsdlSource;
  
  protected EntityResolver entityResolver;
  
  public Class getContractClass() { return this.contractClass; }
  
  public void setContractClass(Class paramClass) { this.contractClass = paramClass; }
  
  public Class getEndpointClass() { return this.endpointClass; }
  
  public void setEndpointClass(Class paramClass) { this.endpointClass = paramClass; }
  
  public MappingInfo getMappingInfo() { return this.mappingInfo; }
  
  public void setMappingInfo(MappingInfo paramMappingInfo) { this.mappingInfo = paramMappingInfo; }
  
  public URL getWsdlURL() { return this.wsdlURL; }
  
  public void setWsdlURL(URL paramURL) { this.wsdlURL = paramURL; }
  
  public ClassLoader getClassLoader() { return this.classLoader; }
  
  public void setClassLoader(ClassLoader paramClassLoader) { this.classLoader = paramClassLoader; }
  
  public Iterable<WebServiceFeature> getFeatures() { return (this.features == null && this.wsBinding != null) ? this.wsBinding.getFeatures() : this.features; }
  
  public void setFeatures(WebServiceFeature[] paramArrayOfWebServiceFeature) { setFeatures(new WebServiceFeatureList(paramArrayOfWebServiceFeature)); }
  
  public void setFeatures(Iterable<WebServiceFeature> paramIterable) { this.features = WebServiceFeatureList.toList(paramIterable); }
  
  public WSDLPort getWsdlPort() { return this.wsdlPort; }
  
  public void setWsdlPort(WSDLPort paramWSDLPort) { this.wsdlPort = paramWSDLPort; }
  
  public Set<Class> additionalValueTypes() { return this.additionalValueTypes; }
  
  public Map<String, Object> properties() { return this.properties; }
  
  public WSBinding getWSBinding() { return this.wsBinding; }
  
  public void setWSBinding(WSBinding paramWSBinding) { this.wsBinding = paramWSBinding; }
  
  public MetadataReader getMetadataReader() { return this.metadataReader; }
  
  public void setMetadataReader(MetadataReader paramMetadataReader) { this.metadataReader = paramMetadataReader; }
  
  public Source getWsdlSource() { return this.wsdlSource; }
  
  public void setWsdlSource(Source paramSource) { this.wsdlSource = paramSource; }
  
  public EntityResolver getEntityResolver() { return this.entityResolver; }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) { this.entityResolver = paramEntityResolver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\DatabindingConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */