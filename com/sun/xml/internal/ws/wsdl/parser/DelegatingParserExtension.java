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
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import javax.xml.stream.XMLStreamReader;

class DelegatingParserExtension extends WSDLParserExtension {
  protected final WSDLParserExtension core;
  
  public DelegatingParserExtension(WSDLParserExtension paramWSDLParserExtension) { this.core = paramWSDLParserExtension; }
  
  public void start(WSDLParserExtensionContext paramWSDLParserExtensionContext) { this.core.start(paramWSDLParserExtensionContext); }
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) { this.core.serviceAttributes(paramEditableWSDLService, paramXMLStreamReader); }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) { return this.core.serviceElements(paramEditableWSDLService, paramXMLStreamReader); }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { this.core.portAttributes(paramEditableWSDLPort, paramXMLStreamReader); }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { return this.core.portElements(paramEditableWSDLPort, paramXMLStreamReader); }
  
  public boolean portTypeOperationInput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationInput(paramEditableWSDLOperation, paramXMLStreamReader); }
  
  public boolean portTypeOperationOutput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationOutput(paramEditableWSDLOperation, paramXMLStreamReader); }
  
  public boolean portTypeOperationFault(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationFault(paramEditableWSDLOperation, paramXMLStreamReader); }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader) { return this.core.definitionsElements(paramXMLStreamReader); }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { return this.core.bindingElements(paramEditableWSDLBoundPortType, paramXMLStreamReader); }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { this.core.bindingAttributes(paramEditableWSDLBoundPortType, paramXMLStreamReader); }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeElements(paramEditableWSDLPortType, paramXMLStreamReader); }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) { this.core.portTypeAttributes(paramEditableWSDLPortType, paramXMLStreamReader); }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationElements(paramEditableWSDLOperation, paramXMLStreamReader); }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) { this.core.portTypeOperationAttributes(paramEditableWSDLOperation, paramXMLStreamReader); }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return this.core.bindingOperationElements(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { this.core.bindingOperationAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) { return this.core.messageElements(paramEditableWSDLMessage, paramXMLStreamReader); }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) { this.core.messageAttributes(paramEditableWSDLMessage, paramXMLStreamReader); }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationInputElements(paramEditableWSDLInput, paramXMLStreamReader); }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) { this.core.portTypeOperationInputAttributes(paramEditableWSDLInput, paramXMLStreamReader); }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationOutputElements(paramEditableWSDLOutput, paramXMLStreamReader); }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) { this.core.portTypeOperationOutputAttributes(paramEditableWSDLOutput, paramXMLStreamReader); }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) { return this.core.portTypeOperationFaultElements(paramEditableWSDLFault, paramXMLStreamReader); }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) { this.core.portTypeOperationFaultAttributes(paramEditableWSDLFault, paramXMLStreamReader); }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return this.core.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { this.core.bindingOperationInputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return this.core.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { this.core.bindingOperationOutputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader); }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) { return this.core.bindingOperationFaultElements(paramEditableWSDLBoundFault, paramXMLStreamReader); }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) { this.core.bindingOperationFaultAttributes(paramEditableWSDLBoundFault, paramXMLStreamReader); }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext) { this.core.finished(paramWSDLParserExtensionContext); }
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext) { this.core.postFinished(paramWSDLParserExtensionContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\DelegatingParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */