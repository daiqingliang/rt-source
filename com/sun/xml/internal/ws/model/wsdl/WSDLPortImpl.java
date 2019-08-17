package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.Locator;

public final class WSDLPortImpl extends AbstractFeaturedObjectImpl implements EditableWSDLPort {
  private final QName name;
  
  private EndpointAddress address;
  
  private final QName bindingName;
  
  private final EditableWSDLService owner;
  
  private WSEndpointReference epr;
  
  private EditableWSDLBoundPortType boundPortType;
  
  public WSDLPortImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLService paramEditableWSDLService, QName paramQName1, QName paramQName2) {
    super(paramXMLStreamReader);
    this.owner = paramEditableWSDLService;
    this.name = paramQName1;
    this.bindingName = paramQName2;
  }
  
  public QName getName() { return this.name; }
  
  public QName getBindingName() { return this.bindingName; }
  
  public EndpointAddress getAddress() { return this.address; }
  
  public EditableWSDLService getOwner() { return this.owner; }
  
  public void setAddress(EndpointAddress paramEndpointAddress) {
    assert paramEndpointAddress != null;
    this.address = paramEndpointAddress;
  }
  
  public void setEPR(@NotNull WSEndpointReference paramWSEndpointReference) {
    assert paramWSEndpointReference != null;
    addExtension(paramWSEndpointReference);
    this.epr = paramWSEndpointReference;
  }
  
  @Nullable
  public WSEndpointReference getEPR() { return this.epr; }
  
  public EditableWSDLBoundPortType getBinding() { return this.boundPortType; }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel) {
    this.boundPortType = paramEditableWSDLModel.getBinding(this.bindingName);
    if (this.boundPortType == null)
      throw new LocatableWebServiceException(ClientMessages.UNDEFINED_BINDING(this.bindingName), new Locator[] { getLocation() }); 
    if (this.features == null)
      this.features = new WebServiceFeatureList(); 
    this.features.setParentFeaturedObject(this.boundPortType);
    this.notUnderstoodExtensions.addAll(this.boundPortType.getNotUnderstoodExtensions());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPortImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */