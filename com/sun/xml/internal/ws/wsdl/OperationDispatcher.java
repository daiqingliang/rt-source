package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class OperationDispatcher {
  private List<WSDLOperationFinder> opFinders;
  
  private WSBinding binding;
  
  public OperationDispatcher(@NotNull WSDLPort paramWSDLPort, @NotNull WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel) {
    this.binding = paramWSBinding;
    this.opFinders = new ArrayList();
    if (paramWSBinding.getAddressingVersion() != null)
      this.opFinders.add(new ActionBasedOperationFinder(paramWSDLPort, paramWSBinding, paramSEIModel)); 
    this.opFinders.add(new PayloadQNameBasedOperationFinder(paramWSDLPort, paramWSBinding, paramSEIModel));
    this.opFinders.add(new SOAPActionBasedOperationFinder(paramWSDLPort, paramWSBinding, paramSEIModel));
  }
  
  @NotNull
  public QName getWSDLOperationQName(Packet paramPacket) throws DispatchException {
    WSDLOperationMapping wSDLOperationMapping = getWSDLOperationMapping(paramPacket);
    return (wSDLOperationMapping != null) ? wSDLOperationMapping.getOperationName() : null;
  }
  
  @NotNull
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket) throws DispatchException {
    for (WSDLOperationFinder wSDLOperationFinder : this.opFinders) {
      WSDLOperationMapping wSDLOperationMapping = wSDLOperationFinder.getWSDLOperationMapping(paramPacket);
      if (wSDLOperationMapping != null)
        return wSDLOperationMapping; 
    } 
    String str1 = MessageFormat.format("Request=[SOAPAction={0},Payload='{'{1}'}'{2}]", new Object[] { paramPacket.soapAction, paramPacket.getMessage().getPayloadNamespaceURI(), paramPacket.getMessage().getPayloadLocalPart() });
    String str2 = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(str1);
    Message message = SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), str2, (this.binding.getSOAPVersion()).faultCodeClient);
    throw new DispatchException(message);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\OperationDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */