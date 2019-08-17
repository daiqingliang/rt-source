package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;

final class WSDLParserExtensionContextImpl implements WSDLParserExtensionContext {
  private final boolean isClientSide;
  
  private final EditableWSDLModel wsdlModel;
  
  private final Container container;
  
  private final PolicyResolver policyResolver;
  
  protected WSDLParserExtensionContextImpl(EditableWSDLModel paramEditableWSDLModel, boolean paramBoolean, Container paramContainer, PolicyResolver paramPolicyResolver) {
    this.wsdlModel = paramEditableWSDLModel;
    this.isClientSide = paramBoolean;
    this.container = paramContainer;
    this.policyResolver = paramPolicyResolver;
  }
  
  public boolean isClientSide() { return this.isClientSide; }
  
  public EditableWSDLModel getWSDLModel() { return this.wsdlModel; }
  
  public Container getContainer() { return this.container; }
  
  public PolicyResolver getPolicyResolver() { return this.policyResolver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\WSDLParserExtensionContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */