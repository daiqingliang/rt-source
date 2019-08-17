package com.sun.xml.internal.ws.api.wsdl.parser;

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
import javax.xml.stream.XMLStreamReader;

public abstract class WSDLParserExtension {
  public void start(WSDLParserExtensionContext paramWSDLParserExtensionContext) {}
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean portTypeOperationInput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean portTypeOperationOutput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean portTypeOperationFault(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {}
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) {}
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {}
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\WSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */