package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

final class SyncMethodHandler extends MethodHandler {
  final boolean isVoid;
  
  final boolean isOneway;
  
  final JavaMethodImpl javaMethod;
  
  SyncMethodHandler(SEIStub paramSEIStub, JavaMethodImpl paramJavaMethodImpl) {
    super(paramSEIStub, paramJavaMethodImpl.getMethod());
    this.javaMethod = paramJavaMethodImpl;
    this.isVoid = void.class.equals(paramJavaMethodImpl.getMethod().getReturnType());
    this.isOneway = paramJavaMethodImpl.getMEP().isOneWay();
  }
  
  Object invoke(Object paramObject, Object[] paramArrayOfObject) throws Throwable { return invoke(paramObject, paramArrayOfObject, this.owner.requestContext, this.owner); }
  
  Object invoke(Object paramObject, Object[] paramArrayOfObject, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver) throws Throwable {
    JavaCallInfo javaCallInfo = this.owner.databinding.createJavaCallInfo(this.method, paramArrayOfObject);
    Packet packet1 = (Packet)this.owner.databinding.serializeRequest(javaCallInfo);
    packet2 = this.owner.doProcess(packet1, paramRequestContext, paramResponseContextReceiver);
    Message message = packet2.getMessage();
    if (message == null) {
      if (!this.isOneway || !this.isVoid)
        throw new WebServiceException(DispatchMessages.INVALID_RESPONSE()); 
      return null;
    } 
    try {
      javaCallInfo = this.owner.databinding.deserializeResponse(packet2, javaCallInfo);
      if (javaCallInfo.getException() != null)
        throw javaCallInfo.getException(); 
      return javaCallInfo.getReturnValue();
    } catch (JAXBException jAXBException) {
      throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { jAXBException });
    } catch (XMLStreamException xMLStreamException) {
      throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { xMLStreamException });
    } finally {
      if (packet2.transportBackChannel != null)
        packet2.transportBackChannel.close(); 
    } 
  }
  
  ValueGetterFactory getValueGetterFactory() { return ValueGetterFactory.SYNC; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\SyncMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */