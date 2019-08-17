package com.sun.xml.internal.ws.api.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.server.Container;

public abstract class WSDLGeneratorExtension {
  public void start(@NotNull TypedXmlWriter paramTypedXmlWriter, @NotNull SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer) {}
  
  public void end(@NotNull WSDLGenExtnContext paramWSDLGenExtnContext) {}
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext) {}
  
  public void addDefinitionsExtension(TypedXmlWriter paramTypedXmlWriter) {}
  
  public void addServiceExtension(TypedXmlWriter paramTypedXmlWriter) {}
  
  public void addPortExtension(TypedXmlWriter paramTypedXmlWriter) {}
  
  public void addPortTypeExtension(TypedXmlWriter paramTypedXmlWriter) {}
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter) {}
  
  public void addOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addBindingOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addInputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addOutputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addBindingOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addBindingOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {}
  
  public void addBindingOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {}
  
  public void addFaultMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {}
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\wsdl\writer\WSDLGeneratorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */