package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLParserExtension extends WSDLParserExtension {
  protected static final String COLON_DELIMITER = ":";
  
  protected static final String SLASH_DELIMITER = "/";
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { return addressibleElement(paramXMLStreamReader, paramEditableWSDLBoundPortType); }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { return addressibleElement(paramXMLStreamReader, paramEditableWSDLPort); }
  
  private boolean addressibleElement(XMLStreamReader paramXMLStreamReader, WSDLFeaturedObject paramWSDLFeaturedObject) {
    QName qName = paramXMLStreamReader.getName();
    if (qName.equals(AddressingVersion.W3C.wsdlExtensionTag)) {
      String str = paramXMLStreamReader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
      paramWSDLFeaturedObject.addFeature(new AddressingFeature(true, Boolean.parseBoolean(str)));
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return true;
    } 
    return false;
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    EditableWSDLBoundOperation editableWSDLBoundOperation = paramEditableWSDLBoundOperation;
    QName qName = paramXMLStreamReader.getName();
    if (qName.equals(AddressingVersion.W3C.wsdlAnonymousTag)) {
      try {
        String str = paramXMLStreamReader.getElementText();
        if (str == null || str.trim().equals(""))
          throw new WebServiceException("Null values not permitted in wsaw:Anonymous."); 
        if (str.equals("optional")) {
          editableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
        } else if (str.equals("required")) {
          editableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.required);
        } else if (str.equals("prohibited")) {
          editableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.prohibited);
        } else {
          throw new WebServiceException("wsaw:Anonymous value \"" + str + "\" not understood.");
        } 
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      } 
      return true;
    } 
    return false;
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) {
    String str = ParserUtil.getAttribute(paramXMLStreamReader, getWsdlActionTag());
    if (str != null) {
      paramEditableWSDLInput.setAction(str);
      paramEditableWSDLInput.setDefaultAction(false);
    } 
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) {
    String str = ParserUtil.getAttribute(paramXMLStreamReader, getWsdlActionTag());
    if (str != null) {
      paramEditableWSDLOutput.setAction(str);
      paramEditableWSDLOutput.setDefaultAction(false);
    } 
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) {
    String str = ParserUtil.getAttribute(paramXMLStreamReader, getWsdlActionTag());
    if (str != null) {
      paramEditableWSDLFault.setAction(str);
      paramEditableWSDLFault.setDefaultAction(false);
    } 
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {
    EditableWSDLModel editableWSDLModel = paramWSDLParserExtensionContext.getWSDLModel();
    for (EditableWSDLService editableWSDLService : editableWSDLModel.getServices().values()) {
      for (EditableWSDLPort editableWSDLPort : editableWSDLService.getPorts()) {
        EditableWSDLBoundPortType editableWSDLBoundPortType = editableWSDLPort.getBinding();
        populateActions(editableWSDLBoundPortType);
        patchAnonymousDefault(editableWSDLBoundPortType);
      } 
    } 
  }
  
  protected String getNamespaceURI() { return AddressingVersion.W3C.wsdlNsUri; }
  
  protected QName getWsdlActionTag() { return AddressingVersion.W3C.wsdlActionTag; }
  
  private void populateActions(EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {
    EditableWSDLPortType editableWSDLPortType = paramEditableWSDLBoundPortType.getPortType();
    for (EditableWSDLOperation editableWSDLOperation : editableWSDLPortType.getOperations()) {
      EditableWSDLBoundOperation editableWSDLBoundOperation = paramEditableWSDLBoundPortType.get(editableWSDLOperation.getName());
      if (editableWSDLBoundOperation == null) {
        editableWSDLOperation.getInput().setAction(defaultInputAction(editableWSDLOperation));
        continue;
      } 
      String str = editableWSDLBoundOperation.getSOAPAction();
      if (editableWSDLOperation.getInput().getAction() == null || editableWSDLOperation.getInput().getAction().equals(""))
        if (str != null && !str.equals("")) {
          editableWSDLOperation.getInput().setAction(str);
        } else {
          editableWSDLOperation.getInput().setAction(defaultInputAction(editableWSDLOperation));
        }  
      if (editableWSDLOperation.getOutput() == null)
        continue; 
      if (editableWSDLOperation.getOutput().getAction() == null || editableWSDLOperation.getOutput().getAction().equals(""))
        editableWSDLOperation.getOutput().setAction(defaultOutputAction(editableWSDLOperation)); 
      if (editableWSDLOperation.getFaults() == null || !editableWSDLOperation.getFaults().iterator().hasNext())
        continue; 
      for (EditableWSDLFault editableWSDLFault : editableWSDLOperation.getFaults()) {
        if (editableWSDLFault.getAction() == null || editableWSDLFault.getAction().equals(""))
          editableWSDLFault.setAction(defaultFaultAction(editableWSDLFault.getName(), editableWSDLOperation)); 
      } 
    } 
  }
  
  protected void patchAnonymousDefault(EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {
    for (EditableWSDLBoundOperation editableWSDLBoundOperation : paramEditableWSDLBoundPortType.getBindingOperations()) {
      if (editableWSDLBoundOperation.getAnonymous() == null)
        editableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional); 
    } 
  }
  
  private String defaultInputAction(EditableWSDLOperation paramEditableWSDLOperation) { return buildAction(paramEditableWSDLOperation.getInput().getName(), paramEditableWSDLOperation, false); }
  
  private String defaultOutputAction(EditableWSDLOperation paramEditableWSDLOperation) { return buildAction(paramEditableWSDLOperation.getOutput().getName(), paramEditableWSDLOperation, false); }
  
  private String defaultFaultAction(String paramString, EditableWSDLOperation paramEditableWSDLOperation) { return buildAction(paramString, paramEditableWSDLOperation, true); }
  
  protected static final String buildAction(String paramString, EditableWSDLOperation paramEditableWSDLOperation, boolean paramBoolean) {
    String str1 = paramEditableWSDLOperation.getName().getNamespaceURI();
    String str2 = "/";
    if (!str1.startsWith("http"))
      str2 = ":"; 
    if (str1.endsWith(str2))
      str1 = str1.substring(0, str1.length() - 1); 
    if (paramEditableWSDLOperation.getPortTypeName() == null)
      throw new WebServiceException("\"" + paramEditableWSDLOperation.getName() + "\" operation's owning portType name is null."); 
    return str1 + str2 + paramEditableWSDLOperation.getPortTypeName().getLocalPart() + str2 + (paramBoolean ? (paramEditableWSDLOperation.getName().getLocalPart() + str2 + "Fault" + str2) : "") + paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\W3CAddressingWSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */