package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.util.QNameMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLOperationImpl extends AbstractExtensibleImpl implements EditableWSDLOperation {
  private final QName name;
  
  private String parameterOrder;
  
  private EditableWSDLInput input;
  
  private EditableWSDLOutput output;
  
  private final List<EditableWSDLFault> faults;
  
  private final QNameMap<EditableWSDLFault> faultMap;
  
  protected Iterable<EditableWSDLMessage> messages;
  
  private final EditableWSDLPortType owner;
  
  public WSDLOperationImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLPortType paramEditableWSDLPortType, QName paramQName) {
    super(paramXMLStreamReader);
    this.name = paramQName;
    this.faults = new ArrayList();
    this.faultMap = new QNameMap();
    this.owner = paramEditableWSDLPortType;
  }
  
  public QName getName() { return this.name; }
  
  public String getParameterOrder() { return this.parameterOrder; }
  
  public void setParameterOrder(String paramString) { this.parameterOrder = paramString; }
  
  public EditableWSDLInput getInput() { return this.input; }
  
  public void setInput(EditableWSDLInput paramEditableWSDLInput) { this.input = paramEditableWSDLInput; }
  
  public EditableWSDLOutput getOutput() { return this.output; }
  
  public boolean isOneWay() { return (this.output == null); }
  
  public void setOutput(EditableWSDLOutput paramEditableWSDLOutput) { this.output = paramEditableWSDLOutput; }
  
  public Iterable<EditableWSDLFault> getFaults() { return this.faults; }
  
  public EditableWSDLFault getFault(QName paramQName) {
    EditableWSDLFault editableWSDLFault = (EditableWSDLFault)this.faultMap.get(paramQName);
    if (editableWSDLFault != null)
      return editableWSDLFault; 
    for (EditableWSDLFault editableWSDLFault1 : this.faults) {
      assert editableWSDLFault1.getMessage().parts().iterator().hasNext();
      EditableWSDLPart editableWSDLPart = (EditableWSDLPart)editableWSDLFault1.getMessage().parts().iterator().next();
      if (editableWSDLPart.getDescriptor().name().equals(paramQName)) {
        this.faultMap.put(paramQName, editableWSDLFault1);
        return editableWSDLFault1;
      } 
    } 
    return null;
  }
  
  @NotNull
  public QName getPortTypeName() { return this.owner.getName(); }
  
  public void addFault(EditableWSDLFault paramEditableWSDLFault) { this.faults.add(paramEditableWSDLFault); }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel) {
    assert this.input != null;
    this.input.freeze(paramEditableWSDLModel);
    if (this.output != null)
      this.output.freeze(paramEditableWSDLModel); 
    for (EditableWSDLFault editableWSDLFault : this.faults)
      editableWSDLFault.freeze(paramEditableWSDLModel); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLOperationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */