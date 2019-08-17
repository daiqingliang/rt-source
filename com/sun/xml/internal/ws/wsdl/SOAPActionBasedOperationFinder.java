package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.util.HashMap;
import java.util.Map;

final class SOAPActionBasedOperationFinder extends WSDLOperationFinder {
  private final Map<String, WSDLOperationMapping> methodHandlers = new HashMap();
  
  public SOAPActionBasedOperationFinder(WSDLPort paramWSDLPort, WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel) {
    super(paramWSDLPort, paramWSBinding, paramSEIModel);
    HashMap hashMap = new HashMap();
    if (paramSEIModel != null) {
      for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods()) {
        String str = javaMethodImpl.getSOAPAction();
        Integer integer = (Integer)hashMap.get(str);
        if (integer == null) {
          hashMap.put(str, Integer.valueOf(1));
          continue;
        } 
        hashMap.put(str, integer = Integer.valueOf(integer.intValue() + 1));
      } 
      for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods()) {
        String str = javaMethodImpl.getSOAPAction();
        if (((Integer)hashMap.get(str)).intValue() == 1)
          this.methodHandlers.put('"' + str + '"', wsdlOperationMapping(javaMethodImpl)); 
      } 
    } else {
      for (WSDLBoundOperation wSDLBoundOperation : paramWSDLPort.getBinding().getBindingOperations())
        this.methodHandlers.put(wSDLBoundOperation.getSOAPAction(), wsdlOperationMapping(wSDLBoundOperation)); 
    } 
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket) throws DispatchException { return (paramPacket.soapAction == null) ? null : (WSDLOperationMapping)this.methodHandlers.get(paramPacket.soapAction); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\SOAPActionBasedOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */