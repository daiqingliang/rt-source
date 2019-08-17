package javax.xml.ws.wsaddressing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.spi.Provider;
import org.w3c.dom.Element;

public final class W3CEndpointReferenceBuilder {
  private String address;
  
  private List<Element> referenceParameters = new ArrayList();
  
  private List<Element> metadata = new ArrayList();
  
  private QName interfaceName;
  
  private QName serviceName;
  
  private QName endpointName;
  
  private String wsdlDocumentLocation;
  
  private Map<QName, String> attributes = new HashMap();
  
  private List<Element> elements = new ArrayList();
  
  public W3CEndpointReferenceBuilder address(String paramString) {
    this.address = paramString;
    return this;
  }
  
  public W3CEndpointReferenceBuilder interfaceName(QName paramQName) {
    this.interfaceName = paramQName;
    return this;
  }
  
  public W3CEndpointReferenceBuilder serviceName(QName paramQName) {
    this.serviceName = paramQName;
    return this;
  }
  
  public W3CEndpointReferenceBuilder endpointName(QName paramQName) {
    if (this.serviceName == null)
      throw new IllegalStateException("The W3CEndpointReferenceBuilder's serviceName must be set before setting the endpointName: " + paramQName); 
    this.endpointName = paramQName;
    return this;
  }
  
  public W3CEndpointReferenceBuilder wsdlDocumentLocation(String paramString) {
    this.wsdlDocumentLocation = paramString;
    return this;
  }
  
  public W3CEndpointReferenceBuilder referenceParameter(Element paramElement) {
    if (paramElement == null)
      throw new IllegalArgumentException("The referenceParameter cannot be null."); 
    this.referenceParameters.add(paramElement);
    return this;
  }
  
  public W3CEndpointReferenceBuilder metadata(Element paramElement) {
    if (paramElement == null)
      throw new IllegalArgumentException("The metadataElement cannot be null."); 
    this.metadata.add(paramElement);
    return this;
  }
  
  public W3CEndpointReferenceBuilder element(Element paramElement) {
    if (paramElement == null)
      throw new IllegalArgumentException("The extension element cannot be null."); 
    this.elements.add(paramElement);
    return this;
  }
  
  public W3CEndpointReferenceBuilder attribute(QName paramQName, String paramString) {
    if (paramQName == null || paramString == null)
      throw new IllegalArgumentException("The extension attribute name or value cannot be null."); 
    this.attributes.put(paramQName, paramString);
    return this;
  }
  
  public W3CEndpointReference build() { return (this.elements.isEmpty() && this.attributes.isEmpty() && this.interfaceName == null) ? Provider.provider().createW3CEndpointReference(this.address, this.serviceName, this.endpointName, this.metadata, this.wsdlDocumentLocation, this.referenceParameters) : Provider.provider().createW3CEndpointReference(this.address, this.interfaceName, this.serviceName, this.endpointName, this.metadata, this.wsdlDocumentLocation, this.referenceParameters, this.elements, this.attributes); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\wsaddressing\W3CEndpointReferenceBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */