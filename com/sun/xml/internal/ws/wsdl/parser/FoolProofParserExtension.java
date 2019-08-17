package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

final class FoolProofParserExtension extends DelegatingParserExtension {
  public FoolProofParserExtension(WSDLParserExtension paramWSDLParserExtension) { super(paramWSDLParserExtension); }
  
  private QName pre(XMLStreamReader paramXMLStreamReader) { return paramXMLStreamReader.getName(); }
  
  private boolean post(QName paramQName, XMLStreamReader paramXMLStreamReader, boolean paramBoolean) {
    if (!paramQName.equals(paramXMLStreamReader.getName()))
      return foundFool(); 
    if (paramBoolean) {
      if (paramXMLStreamReader.getEventType() != 2)
        foundFool(); 
    } else if (paramXMLStreamReader.getEventType() != 1) {
      foundFool();
    } 
    return paramBoolean;
  }
  
  private boolean foundFool() { throw new AssertionError("XMLStreamReader is placed at the wrong place after invoking " + this.core); }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.serviceElements(paramEditableWSDLService, paramXMLStreamReader)); }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.portElements(paramEditableWSDLPort, paramXMLStreamReader)); }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.definitionsElements(paramXMLStreamReader)); }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.bindingElements(paramEditableWSDLBoundPortType, paramXMLStreamReader)); }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.portTypeElements(paramEditableWSDLPortType, paramXMLStreamReader)); }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.portTypeOperationElements(paramEditableWSDLOperation, paramXMLStreamReader)); }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.bindingOperationElements(paramEditableWSDLBoundOperation, paramXMLStreamReader)); }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.messageElements(paramEditableWSDLMessage, paramXMLStreamReader)); }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.portTypeOperationInputElements(paramEditableWSDLInput, paramXMLStreamReader)); }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.portTypeOperationOutputElements(paramEditableWSDLOutput, paramXMLStreamReader)); }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.portTypeOperationFaultElements(paramEditableWSDLFault, paramXMLStreamReader)); }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return super.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader)); }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) { return post(pre(paramXMLStreamReader), paramXMLStreamReader, super.bindingOperationFaultElements(paramEditableWSDLBoundFault, paramXMLStreamReader)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\FoolProofParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */