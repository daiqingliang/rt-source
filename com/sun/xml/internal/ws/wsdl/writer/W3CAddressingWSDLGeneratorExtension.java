package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLGeneratorExtension extends WSDLGeneratorExtension {
  private boolean enabled;
  
  private boolean required = false;
  
  private static final Logger LOGGER = Logger.getLogger(W3CAddressingWSDLGeneratorExtension.class.getName());
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext) {
    WSBinding wSBinding = paramWSDLGenExtnContext.getBinding();
    TypedXmlWriter typedXmlWriter = paramWSDLGenExtnContext.getRoot();
    this.enabled = wSBinding.isFeatureEnabled(AddressingFeature.class);
    if (!this.enabled)
      return; 
    AddressingFeature addressingFeature = (AddressingFeature)wSBinding.getFeature(AddressingFeature.class);
    this.required = addressingFeature.isRequired();
    typedXmlWriter._namespace(AddressingVersion.W3C.wsdlNsUri, AddressingVersion.W3C.getWsdlPrefix());
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    if (!this.enabled)
      return; 
    Action action = (Action)paramJavaMethod.getSEIMethod().getAnnotation(Action.class);
    if (action != null && !action.input().equals("")) {
      addAttribute(paramTypedXmlWriter, action.input());
    } else {
      String str = paramJavaMethod.getBinding().getSOAPAction();
      if (str == null || str.equals("")) {
        String str1 = getDefaultAction(paramJavaMethod);
        addAttribute(paramTypedXmlWriter, str1);
      } 
    } 
  }
  
  protected static final String getDefaultAction(JavaMethod paramJavaMethod) {
    String str1 = paramJavaMethod.getOwner().getTargetNamespace();
    String str2 = "/";
    try {
      URI uRI = new URI(str1);
      if (uRI.getScheme().equalsIgnoreCase("urn"))
        str2 = ":"; 
    } catch (URISyntaxException uRISyntaxException) {
      LOGGER.warning("TargetNamespace of WebService is not a valid URI");
    } 
    if (str1.endsWith(str2))
      str1 = str1.substring(0, str1.length() - 1); 
    String str3 = paramJavaMethod.getMEP().isOneWay() ? paramJavaMethod.getOperationName() : (paramJavaMethod.getOperationName() + "Request");
    return str1 + str2 + paramJavaMethod.getOwner().getPortTypeName().getLocalPart() + str2 + str3;
  }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    if (!this.enabled)
      return; 
    Action action = (Action)paramJavaMethod.getSEIMethod().getAnnotation(Action.class);
    if (action != null && !action.output().equals(""))
      addAttribute(paramTypedXmlWriter, action.output()); 
  }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    if (!this.enabled)
      return; 
    Action action = (Action)paramJavaMethod.getSEIMethod().getAnnotation(Action.class);
    Class[] arrayOfClass = paramJavaMethod.getSEIMethod().getExceptionTypes();
    if (arrayOfClass == null)
      return; 
    if (action != null && action.fault() != null)
      for (FaultAction faultAction : action.fault()) {
        if (faultAction.className().getName().equals(paramCheckedException.getExceptionClass().getName())) {
          if (faultAction.value().equals(""))
            return; 
          addAttribute(paramTypedXmlWriter, faultAction.value());
          return;
        } 
      }  
  }
  
  private void addAttribute(TypedXmlWriter paramTypedXmlWriter, String paramString) { paramTypedXmlWriter._attribute(AddressingVersion.W3C.wsdlActionTag, paramString); }
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter) {
    if (!this.enabled)
      return; 
    paramTypedXmlWriter._element(AddressingVersion.W3C.wsdlExtensionTag, UsingAddressing.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\W3CAddressingWSDLGeneratorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */