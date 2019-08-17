package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.Map;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public interface WSDLBoundOperation extends WSDLObject, WSDLExtensible {
  @NotNull
  QName getName();
  
  @NotNull
  String getSOAPAction();
  
  @NotNull
  WSDLOperation getOperation();
  
  @NotNull
  WSDLBoundPortType getBoundPortType();
  
  ANONYMOUS getAnonymous();
  
  @Nullable
  WSDLPart getPart(@NotNull String paramString, @NotNull WebParam.Mode paramMode);
  
  ParameterBinding getInputBinding(String paramString);
  
  ParameterBinding getOutputBinding(String paramString);
  
  ParameterBinding getFaultBinding(String paramString);
  
  String getMimeTypeForInputPart(String paramString);
  
  String getMimeTypeForOutputPart(String paramString);
  
  String getMimeTypeForFaultPart(String paramString);
  
  @NotNull
  Map<String, ? extends WSDLPart> getInParts();
  
  @NotNull
  Map<String, ? extends WSDLPart> getOutParts();
  
  @NotNull
  Iterable<? extends WSDLBoundFault> getFaults();
  
  Map<String, ParameterBinding> getInputParts();
  
  Map<String, ParameterBinding> getOutputParts();
  
  Map<String, ParameterBinding> getFaultParts();
  
  @Nullable
  QName getRequestPayloadName();
  
  @Nullable
  QName getResponsePayloadName();
  
  String getRequestNamespace();
  
  String getResponseNamespace();
  
  public enum ANONYMOUS {
    optional, required, prohibited;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLBoundOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */