package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import javax.xml.namespace.QName;

public abstract class WSDLOperationFinder {
  protected final WSDLPort wsdlModel;
  
  protected final WSBinding binding;
  
  protected final SEIModel seiModel;
  
  public WSDLOperationFinder(@NotNull WSDLPort paramWSDLPort, @NotNull WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel) {
    this.wsdlModel = paramWSDLPort;
    this.binding = paramWSBinding;
    this.seiModel = paramSEIModel;
  }
  
  public QName getWSDLOperationQName(Packet paramPacket) throws DispatchException {
    WSDLOperationMapping wSDLOperationMapping = getWSDLOperationMapping(paramPacket);
    return (wSDLOperationMapping != null) ? wSDLOperationMapping.getOperationName() : null;
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket) throws DispatchException { return null; }
  
  protected WSDLOperationMapping wsdlOperationMapping(JavaMethodImpl paramJavaMethodImpl) { return new WSDLOperationMappingImpl(paramJavaMethodImpl.getOperation(), paramJavaMethodImpl); }
  
  protected WSDLOperationMapping wsdlOperationMapping(WSDLBoundOperation paramWSDLBoundOperation) { return new WSDLOperationMappingImpl(paramWSDLBoundOperation, null); }
  
  static class WSDLOperationMappingImpl implements WSDLOperationMapping {
    private WSDLBoundOperation wsdlOperation;
    
    private JavaMethod javaMethod;
    
    private QName operationName;
    
    WSDLOperationMappingImpl(WSDLBoundOperation param1WSDLBoundOperation, JavaMethodImpl param1JavaMethodImpl) {
      this.wsdlOperation = param1WSDLBoundOperation;
      this.javaMethod = param1JavaMethodImpl;
      this.operationName = (param1JavaMethodImpl != null) ? param1JavaMethodImpl.getOperationQName() : param1WSDLBoundOperation.getName();
    }
    
    public WSDLBoundOperation getWSDLBoundOperation() { return this.wsdlOperation; }
    
    public JavaMethod getJavaMethod() { return this.javaMethod; }
    
    public QName getOperationName() { return this.operationName; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\WSDLOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */