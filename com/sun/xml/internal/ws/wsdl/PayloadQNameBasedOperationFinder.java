package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.util.QNameMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class PayloadQNameBasedOperationFinder extends WSDLOperationFinder {
  private static final Logger LOGGER = Logger.getLogger(PayloadQNameBasedOperationFinder.class.getName());
  
  public static final String EMPTY_PAYLOAD_LOCAL = "";
  
  public static final String EMPTY_PAYLOAD_NSURI = "";
  
  public static final QName EMPTY_PAYLOAD = new QName("", "");
  
  private final QNameMap<WSDLOperationMapping> methodHandlers = new QNameMap();
  
  private final QNameMap<List<String>> unique = new QNameMap();
  
  public PayloadQNameBasedOperationFinder(WSDLPort paramWSDLPort, WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel) {
    super(paramWSDLPort, paramWSBinding, paramSEIModel);
    if (paramSEIModel != null) {
      for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods()) {
        if ((javaMethodImpl.getMEP()).isAsync)
          continue; 
        QName qName = javaMethodImpl.getRequestPayloadName();
        if (qName == null)
          qName = EMPTY_PAYLOAD; 
        List list = (List)this.unique.get(qName);
        if (list == null) {
          list = new ArrayList();
          this.unique.put(qName, list);
        } 
        list.add(javaMethodImpl.getMethod().getName());
      } 
      for (QNameMap.Entry entry : this.unique.entrySet()) {
        if (((List)entry.getValue()).size() > 1)
          LOGGER.warning(ServerMessages.NON_UNIQUE_DISPATCH_QNAME(entry.getValue(), entry.createQName())); 
      } 
      for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods()) {
        QName qName = javaMethodImpl.getRequestPayloadName();
        if (qName == null)
          qName = EMPTY_PAYLOAD; 
        if (((List)this.unique.get(qName)).size() == 1)
          this.methodHandlers.put(qName, wsdlOperationMapping(javaMethodImpl)); 
      } 
    } else {
      for (WSDLBoundOperation wSDLBoundOperation : paramWSDLPort.getBinding().getBindingOperations()) {
        QName qName = wSDLBoundOperation.getRequestPayloadName();
        if (qName == null)
          qName = EMPTY_PAYLOAD; 
        this.methodHandlers.put(qName, wsdlOperationMapping(wSDLBoundOperation));
      } 
    } 
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket) throws DispatchException {
    String str2;
    Message message = paramPacket.getMessage();
    String str1 = message.getPayloadLocalPart();
    if (str1 == null) {
      str1 = "";
      str2 = "";
    } else {
      str2 = message.getPayloadNamespaceURI();
      if (str2 == null)
        str2 = ""; 
    } 
    WSDLOperationMapping wSDLOperationMapping = (WSDLOperationMapping)this.methodHandlers.get(str2, str1);
    if (wSDLOperationMapping == null && !this.unique.containsKey(str2, str1)) {
      String str3 = "{" + str2 + "}" + str1;
      String str4 = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(str3);
      throw new DispatchException(SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), str4, (this.binding.getSOAPVersion()).faultCodeClient));
    } 
    return wSDLOperationMapping;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\PayloadQNameBasedOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */