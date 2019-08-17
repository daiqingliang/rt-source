package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.jaxb.JAXBDispatchMessage;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class JAXBDispatch extends DispatchImpl<Object> {
  private final JAXBContext jaxbcontext;
  
  private final boolean isContextSupported;
  
  @Deprecated
  public JAXBDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    this.jaxbcontext = paramJAXBContext;
    this.isContextSupported = BindingContextFactory.isContextSupported(paramJAXBContext);
  }
  
  public JAXBDispatch(WSPortInfo paramWSPortInfo, JAXBContext paramJAXBContext, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    this.jaxbcontext = paramJAXBContext;
    this.isContextSupported = BindingContextFactory.isContextSupported(paramJAXBContext);
  }
  
  Object toReturnValue(Packet paramPacket) {
    try {
      Source source;
      Unmarshaller unmarshaller = this.jaxbcontext.createUnmarshaller();
      Message message = paramPacket.getMessage();
      switch (this.mode) {
        case PAYLOAD:
          return message.readPayloadAsJAXB(unmarshaller);
        case MESSAGE:
          source = message.readEnvelopeAsSource();
          return unmarshaller.unmarshal(source);
      } 
      throw new WebServiceException("Unrecognized dispatch mode");
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
  
  Packet createPacket(Object paramObject) {
    Message message;
    assert this.jaxbcontext != null;
    if (this.mode == Service.Mode.MESSAGE) {
      message = this.isContextSupported ? new JAXBDispatchMessage(BindingContextFactory.create(this.jaxbcontext), paramObject, this.soapVersion) : new JAXBDispatchMessage(this.jaxbcontext, paramObject, this.soapVersion);
    } else if (paramObject == null) {
      message = Messages.createEmpty(this.soapVersion);
    } else {
      message = this.isContextSupported ? Messages.create(this.jaxbcontext, paramObject, this.soapVersion) : Messages.createRaw(this.jaxbcontext, paramObject, this.soapVersion);
    } 
    return new Packet(message);
  }
  
  public void setOutboundHeaders(Object... paramVarArgs) {
    if (paramVarArgs == null)
      throw new IllegalArgumentException(); 
    Header[] arrayOfHeader = new Header[paramVarArgs.length];
    for (byte b = 0; b < arrayOfHeader.length; b++) {
      if (paramVarArgs[b] == null)
        throw new IllegalArgumentException(); 
      arrayOfHeader[b] = Headers.create((JAXBRIContext)this.jaxbcontext, paramVarArgs[b]);
    } 
    setOutboundHeaders(arrayOfHeader);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\dispatch\JAXBDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */