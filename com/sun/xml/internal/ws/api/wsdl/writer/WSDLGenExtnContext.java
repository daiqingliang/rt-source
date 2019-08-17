package com.sun.xml.internal.ws.api.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.server.Container;

public class WSDLGenExtnContext {
  private final TypedXmlWriter root;
  
  private final SEIModel model;
  
  private final WSBinding binding;
  
  private final Container container;
  
  private final Class endpointClass;
  
  public WSDLGenExtnContext(@NotNull TypedXmlWriter paramTypedXmlWriter, @NotNull SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding, @Nullable Container paramContainer, @NotNull Class paramClass) {
    this.root = paramTypedXmlWriter;
    this.model = paramSEIModel;
    this.binding = paramWSBinding;
    this.container = paramContainer;
    this.endpointClass = paramClass;
  }
  
  public TypedXmlWriter getRoot() { return this.root; }
  
  public SEIModel getModel() { return this.model; }
  
  public WSBinding getBinding() { return this.binding; }
  
  public Container getContainer() { return this.container; }
  
  public Class getEndpointClass() { return this.endpointClass; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\wsdl\writer\WSDLGenExtnContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */