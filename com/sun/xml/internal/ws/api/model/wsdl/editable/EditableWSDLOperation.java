package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import javax.xml.namespace.QName;

public interface EditableWSDLOperation extends WSDLOperation {
  @NotNull
  EditableWSDLInput getInput();
  
  void setInput(EditableWSDLInput paramEditableWSDLInput);
  
  @Nullable
  EditableWSDLOutput getOutput();
  
  void setOutput(EditableWSDLOutput paramEditableWSDLOutput);
  
  Iterable<? extends EditableWSDLFault> getFaults();
  
  void addFault(EditableWSDLFault paramEditableWSDLFault);
  
  @Nullable
  EditableWSDLFault getFault(QName paramQName);
  
  void setParameterOrder(String paramString);
  
  void freeze(EditableWSDLModel paramEditableWSDLModel);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */