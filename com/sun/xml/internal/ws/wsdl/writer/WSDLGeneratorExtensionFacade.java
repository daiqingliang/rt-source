package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

final class WSDLGeneratorExtensionFacade extends WSDLGeneratorExtension {
  private final WSDLGeneratorExtension[] extensions;
  
  WSDLGeneratorExtensionFacade(WSDLGeneratorExtension... paramVarArgs) {
    assert paramVarArgs != null;
    this.extensions = paramVarArgs;
  }
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.start(paramWSDLGenExtnContext); 
  }
  
  public void end(@NotNull WSDLGenExtnContext paramWSDLGenExtnContext) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.end(paramWSDLGenExtnContext); 
  }
  
  public void addDefinitionsExtension(TypedXmlWriter paramTypedXmlWriter) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addDefinitionsExtension(paramTypedXmlWriter); 
  }
  
  public void addServiceExtension(TypedXmlWriter paramTypedXmlWriter) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addServiceExtension(paramTypedXmlWriter); 
  }
  
  public void addPortExtension(TypedXmlWriter paramTypedXmlWriter) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addPortExtension(paramTypedXmlWriter); 
  }
  
  public void addPortTypeExtension(TypedXmlWriter paramTypedXmlWriter) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addPortTypeExtension(paramTypedXmlWriter); 
  }
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addBindingExtension(paramTypedXmlWriter); 
  }
  
  public void addOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addOperationExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addBindingOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addBindingOperationExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addInputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addInputMessageExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addOutputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addOutputMessageExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addOperationInputExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addOperationOutputExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addBindingOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addBindingOperationInputExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addBindingOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addBindingOperationOutputExtension(paramTypedXmlWriter, paramJavaMethod); 
  }
  
  public void addBindingOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addBindingOperationFaultExtension(paramTypedXmlWriter, paramJavaMethod, paramCheckedException); 
  }
  
  public void addFaultMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addFaultMessageExtension(paramTypedXmlWriter, paramJavaMethod, paramCheckedException); 
  }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    for (WSDLGeneratorExtension wSDLGeneratorExtension : this.extensions)
      wSDLGeneratorExtension.addOperationFaultExtension(paramTypedXmlWriter, paramJavaMethod, paramCheckedException); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\WSDLGeneratorExtensionFacade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */