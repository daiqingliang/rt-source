package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class ActionBasedOperationFinder extends WSDLOperationFinder {
  private static final Logger LOGGER = Logger.getLogger(ActionBasedOperationFinder.class.getName());
  
  private final Map<ActionBasedOperationSignature, WSDLOperationMapping> uniqueOpSignatureMap;
  
  private final Map<String, WSDLOperationMapping> actionMap;
  
  @NotNull
  private final AddressingVersion av;
  
  public ActionBasedOperationFinder(WSDLPort paramWSDLPort, WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel) {
    super(paramWSDLPort, paramWSBinding, paramSEIModel);
    assert paramWSBinding.getAddressingVersion() != null;
    this.av = paramWSBinding.getAddressingVersion();
    this.uniqueOpSignatureMap = new HashMap();
    this.actionMap = new HashMap();
    if (paramSEIModel != null) {
      for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods()) {
        if ((javaMethodImpl.getMEP()).isAsync)
          continue; 
        String str = javaMethodImpl.getInputAction();
        QName qName = javaMethodImpl.getRequestPayloadName();
        if (qName == null)
          qName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD; 
        if ((str == null || str.equals("")) && javaMethodImpl.getOperation() != null)
          str = javaMethodImpl.getOperation().getOperation().getInput().getAction(); 
        if (str != null) {
          ActionBasedOperationSignature actionBasedOperationSignature = new ActionBasedOperationSignature(str, qName);
          if (this.uniqueOpSignatureMap.get(actionBasedOperationSignature) != null)
            LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(actionBasedOperationSignature), javaMethodImpl.getOperationQName(), str, qName)); 
          this.uniqueOpSignatureMap.put(actionBasedOperationSignature, wsdlOperationMapping(javaMethodImpl));
          this.actionMap.put(str, wsdlOperationMapping(javaMethodImpl));
        } 
      } 
    } else {
      for (WSDLBoundOperation wSDLBoundOperation : paramWSDLPort.getBinding().getBindingOperations()) {
        QName qName = wSDLBoundOperation.getRequestPayloadName();
        if (qName == null)
          qName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD; 
        String str = wSDLBoundOperation.getOperation().getInput().getAction();
        ActionBasedOperationSignature actionBasedOperationSignature = new ActionBasedOperationSignature(str, qName);
        if (this.uniqueOpSignatureMap.get(actionBasedOperationSignature) != null)
          LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(actionBasedOperationSignature), wSDLBoundOperation.getName(), str, qName)); 
        this.uniqueOpSignatureMap.put(actionBasedOperationSignature, wsdlOperationMapping(wSDLBoundOperation));
        this.actionMap.put(str, wsdlOperationMapping(wSDLBoundOperation));
      } 
    } 
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket) throws DispatchException {
    QName qName;
    MessageHeaders messageHeaders = paramPacket.getMessage().getHeaders();
    String str1 = AddressingUtils.getAction(messageHeaders, this.av, this.binding.getSOAPVersion());
    if (str1 == null)
      return null; 
    Message message1 = paramPacket.getMessage();
    String str2 = message1.getPayloadLocalPart();
    if (str2 == null) {
      qName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
    } else {
      String str = message1.getPayloadNamespaceURI();
      if (str == null)
        str = ""; 
      qName = new QName(str, str2);
    } 
    WSDLOperationMapping wSDLOperationMapping = (WSDLOperationMapping)this.uniqueOpSignatureMap.get(new ActionBasedOperationSignature(str1, qName));
    if (wSDLOperationMapping != null)
      return wSDLOperationMapping; 
    wSDLOperationMapping = (WSDLOperationMapping)this.actionMap.get(str1);
    if (wSDLOperationMapping != null)
      return wSDLOperationMapping; 
    Message message2 = Messages.create(str1, this.av, this.binding.getSOAPVersion());
    throw new DispatchException(message2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\ActionBasedOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */