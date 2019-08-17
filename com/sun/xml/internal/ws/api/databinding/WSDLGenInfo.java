package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

public class WSDLGenInfo {
  WSDLResolver wsdlResolver;
  
  Container container;
  
  boolean inlineSchemas;
  
  boolean secureXmlProcessingDisabled;
  
  WSDLGeneratorExtension[] extensions;
  
  public WSDLResolver getWsdlResolver() { return this.wsdlResolver; }
  
  public void setWsdlResolver(WSDLResolver paramWSDLResolver) { this.wsdlResolver = paramWSDLResolver; }
  
  public Container getContainer() { return this.container; }
  
  public void setContainer(Container paramContainer) { this.container = paramContainer; }
  
  public boolean isInlineSchemas() { return this.inlineSchemas; }
  
  public void setInlineSchemas(boolean paramBoolean) { this.inlineSchemas = paramBoolean; }
  
  public WSDLGeneratorExtension[] getExtensions() { return (this.extensions == null) ? new WSDLGeneratorExtension[0] : this.extensions; }
  
  public void setExtensions(WSDLGeneratorExtension[] paramArrayOfWSDLGeneratorExtension) { this.extensions = paramArrayOfWSDLGeneratorExtension; }
  
  public void setSecureXmlProcessingDisabled(boolean paramBoolean) { this.secureXmlProcessingDisabled = paramBoolean; }
  
  public boolean isSecureXmlProcessingDisabled() { return this.secureXmlProcessingDisabled; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\WSDLGenInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */