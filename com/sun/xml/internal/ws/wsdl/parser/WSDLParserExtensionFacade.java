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
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

final class WSDLParserExtensionFacade extends WSDLParserExtension {
  private final WSDLParserExtension[] extensions;
  
  WSDLParserExtensionFacade(WSDLParserExtension... paramVarArgs) {
    assert paramVarArgs != null;
    this.extensions = paramVarArgs;
  }
  
  public void start(WSDLParserExtensionContext paramWSDLParserExtensionContext) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.start(paramWSDLParserExtensionContext); 
  }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.serviceElements(paramEditableWSDLService, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.serviceAttributes(paramEditableWSDLService, paramXMLStreamReader); 
  }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.portElements(paramEditableWSDLPort, paramXMLStreamReader))
        return true; 
    } 
    if (isRequiredExtension(paramXMLStreamReader))
      paramEditableWSDLPort.addNotUnderstoodExtension(paramXMLStreamReader.getName(), getLocator(paramXMLStreamReader)); 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public boolean portTypeOperationInput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationInput(paramEditableWSDLOperation, paramXMLStreamReader); 
    return false;
  }
  
  public boolean portTypeOperationOutput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationOutput(paramEditableWSDLOperation, paramXMLStreamReader); 
    return false;
  }
  
  public boolean portTypeOperationFault(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationFault(paramEditableWSDLOperation, paramXMLStreamReader); 
    return false;
  }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portAttributes(paramEditableWSDLPort, paramXMLStreamReader); 
  }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.definitionsElements(paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.bindingElements(paramEditableWSDLBoundPortType, paramXMLStreamReader))
        return true; 
    } 
    if (isRequiredExtension(paramXMLStreamReader))
      paramEditableWSDLBoundPortType.addNotUnderstoodExtension(paramXMLStreamReader.getName(), getLocator(paramXMLStreamReader)); 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.bindingAttributes(paramEditableWSDLBoundPortType, paramXMLStreamReader); 
  }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.portTypeElements(paramEditableWSDLPortType, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeAttributes(paramEditableWSDLPortType, paramXMLStreamReader); 
  }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.portTypeOperationElements(paramEditableWSDLOperation, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationAttributes(paramEditableWSDLOperation, paramXMLStreamReader); 
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.bindingOperationElements(paramEditableWSDLBoundOperation, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.bindingOperationAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader); 
  }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.messageElements(paramEditableWSDLMessage, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.messageAttributes(paramEditableWSDLMessage, paramXMLStreamReader); 
  }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.portTypeOperationInputElements(paramEditableWSDLInput, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationInputAttributes(paramEditableWSDLInput, paramXMLStreamReader); 
  }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.portTypeOperationOutputElements(paramEditableWSDLOutput, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationOutputAttributes(paramEditableWSDLOutput, paramXMLStreamReader); 
  }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.portTypeOperationFaultElements(paramEditableWSDLFault, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.portTypeOperationFaultAttributes(paramEditableWSDLFault, paramXMLStreamReader); 
  }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.bindingOperationInputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader); 
  }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.bindingOperationOutputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader); 
  }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions) {
      if (wSDLParserExtension.bindingOperationFaultElements(paramEditableWSDLBoundFault, paramXMLStreamReader))
        return true; 
    } 
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.bindingOperationFaultAttributes(paramEditableWSDLBoundFault, paramXMLStreamReader); 
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.finished(paramWSDLParserExtensionContext); 
  }
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {
    for (WSDLParserExtension wSDLParserExtension : this.extensions)
      wSDLParserExtension.postFinished(paramWSDLParserExtensionContext); 
  }
  
  private boolean isRequiredExtension(XMLStreamReader paramXMLStreamReader) {
    String str = paramXMLStreamReader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
    return (str != null) ? Boolean.parseBoolean(str) : 0;
  }
  
  private Locator getLocator(XMLStreamReader paramXMLStreamReader) {
    Location location = paramXMLStreamReader.getLocation();
    LocatorImpl locatorImpl = new LocatorImpl();
    locatorImpl.setSystemId(location.getSystemId());
    locatorImpl.setLineNumber(location.getLineNumber());
    return locatorImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\WSDLParserExtensionFacade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */