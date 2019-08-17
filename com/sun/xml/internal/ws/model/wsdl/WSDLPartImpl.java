package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartImpl extends AbstractObjectImpl implements EditableWSDLPart {
  private final String name;
  
  private ParameterBinding binding;
  
  private int index;
  
  private final WSDLPartDescriptor descriptor;
  
  public WSDLPartImpl(XMLStreamReader paramXMLStreamReader, String paramString, int paramInt, WSDLPartDescriptor paramWSDLPartDescriptor) {
    super(paramXMLStreamReader);
    this.name = paramString;
    this.binding = ParameterBinding.UNBOUND;
    this.index = paramInt;
    this.descriptor = paramWSDLPartDescriptor;
  }
  
  public String getName() { return this.name; }
  
  public ParameterBinding getBinding() { return this.binding; }
  
  public void setBinding(ParameterBinding paramParameterBinding) { this.binding = paramParameterBinding; }
  
  public int getIndex() { return this.index; }
  
  public void setIndex(int paramInt) { this.index = paramInt; }
  
  public WSDLPartDescriptor getDescriptor() { return this.descriptor; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */