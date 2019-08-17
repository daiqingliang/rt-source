package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.client.dispatch.DataSourceDispatch;
import com.sun.xml.internal.ws.client.dispatch.DispatchImpl;
import com.sun.xml.internal.ws.client.dispatch.JAXBDispatch;
import com.sun.xml.internal.ws.client.dispatch.MessageDispatch;
import com.sun.xml.internal.ws.client.dispatch.PacketDispatch;
import com.sun.xml.internal.ws.client.dispatch.SOAPMessageDispatch;
import com.sun.xml.internal.ws.client.sei.SEIStub;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import java.lang.reflect.Proxy;
import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public abstract class Stubs {
  @Deprecated
  public static Dispatch<SOAPMessage> createSAAJDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, Service.Mode paramMode, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) {
    DispatchImpl.checkValidSOAPMessageDispatch(paramWSBinding, paramMode);
    return new SOAPMessageDispatch(paramQName, paramMode, (WSServiceDelegate)paramWSService, paramTube, (BindingImpl)paramWSBinding, paramWSEndpointReference);
  }
  
  public static Dispatch<SOAPMessage> createSAAJDispatch(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, Service.Mode paramMode, @Nullable WSEndpointReference paramWSEndpointReference) {
    DispatchImpl.checkValidSOAPMessageDispatch(paramWSBinding, paramMode);
    return new SOAPMessageDispatch(paramWSPortInfo, paramMode, (BindingImpl)paramWSBinding, paramWSEndpointReference);
  }
  
  @Deprecated
  public static Dispatch<DataSource> createDataSourceDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, Service.Mode paramMode, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) {
    DispatchImpl.checkValidDataSourceDispatch(paramWSBinding, paramMode);
    return new DataSourceDispatch(paramQName, paramMode, (WSServiceDelegate)paramWSService, paramTube, (BindingImpl)paramWSBinding, paramWSEndpointReference);
  }
  
  public static Dispatch<DataSource> createDataSourceDispatch(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, Service.Mode paramMode, @Nullable WSEndpointReference paramWSEndpointReference) {
    DispatchImpl.checkValidDataSourceDispatch(paramWSBinding, paramMode);
    return new DataSourceDispatch(paramWSPortInfo, paramMode, (BindingImpl)paramWSBinding, paramWSEndpointReference);
  }
  
  @Deprecated
  public static Dispatch<Source> createSourceDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, Service.Mode paramMode, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) { return DispatchImpl.createSourceDispatch(paramQName, paramMode, (WSServiceDelegate)paramWSService, paramTube, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public static Dispatch<Source> createSourceDispatch(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, Service.Mode paramMode, @Nullable WSEndpointReference paramWSEndpointReference) { return DispatchImpl.createSourceDispatch(paramWSPortInfo, paramMode, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public static <T> Dispatch<T> createDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, Class<T> paramClass, Service.Mode paramMode, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) {
    if (paramClass == SOAPMessage.class)
      return createSAAJDispatch(paramQName, paramWSService, paramWSBinding, paramMode, paramTube, paramWSEndpointReference); 
    if (paramClass == Source.class)
      return createSourceDispatch(paramQName, paramWSService, paramWSBinding, paramMode, paramTube, paramWSEndpointReference); 
    if (paramClass == DataSource.class)
      return createDataSourceDispatch(paramQName, paramWSService, paramWSBinding, paramMode, paramTube, paramWSEndpointReference); 
    if (paramClass == Message.class) {
      if (paramMode == Service.Mode.MESSAGE)
        return createMessageDispatch(paramQName, paramWSService, paramWSBinding, paramTube, paramWSEndpointReference); 
      throw new WebServiceException(paramMode + " not supported with Dispatch<Message>");
    } 
    if (paramClass == Packet.class)
      return createPacketDispatch(paramQName, paramWSService, paramWSBinding, paramTube, paramWSEndpointReference); 
    throw new WebServiceException("Unknown class type " + paramClass.getName());
  }
  
  public static <T> Dispatch<T> createDispatch(WSPortInfo paramWSPortInfo, WSService paramWSService, WSBinding paramWSBinding, Class<T> paramClass, Service.Mode paramMode, @Nullable WSEndpointReference paramWSEndpointReference) {
    if (paramClass == SOAPMessage.class)
      return createSAAJDispatch(paramWSPortInfo, paramWSBinding, paramMode, paramWSEndpointReference); 
    if (paramClass == Source.class)
      return createSourceDispatch(paramWSPortInfo, paramWSBinding, paramMode, paramWSEndpointReference); 
    if (paramClass == DataSource.class)
      return createDataSourceDispatch(paramWSPortInfo, paramWSBinding, paramMode, paramWSEndpointReference); 
    if (paramClass == Message.class) {
      if (paramMode == Service.Mode.MESSAGE)
        return createMessageDispatch(paramWSPortInfo, paramWSBinding, paramWSEndpointReference); 
      throw new WebServiceException(paramMode + " not supported with Dispatch<Message>");
    } 
    if (paramClass == Packet.class) {
      if (paramMode == Service.Mode.MESSAGE)
        return createPacketDispatch(paramWSPortInfo, paramWSBinding, paramWSEndpointReference); 
      throw new WebServiceException(paramMode + " not supported with Dispatch<Packet>");
    } 
    throw new WebServiceException("Unknown class type " + paramClass.getName());
  }
  
  @Deprecated
  public static Dispatch<Object> createJAXBDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, JAXBContext paramJAXBContext, Service.Mode paramMode, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) { return new JAXBDispatch(paramQName, paramJAXBContext, paramMode, (WSServiceDelegate)paramWSService, paramTube, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public static Dispatch<Object> createJAXBDispatch(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, JAXBContext paramJAXBContext, Service.Mode paramMode, @Nullable WSEndpointReference paramWSEndpointReference) { return new JAXBDispatch(paramWSPortInfo, paramJAXBContext, paramMode, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  @Deprecated
  public static Dispatch<Message> createMessageDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) { return new MessageDispatch(paramQName, (WSServiceDelegate)paramWSService, paramTube, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public static Dispatch<Message> createMessageDispatch(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, @Nullable WSEndpointReference paramWSEndpointReference) { return new MessageDispatch(paramWSPortInfo, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public static Dispatch<Packet> createPacketDispatch(QName paramQName, WSService paramWSService, WSBinding paramWSBinding, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) { return new PacketDispatch(paramQName, (WSServiceDelegate)paramWSService, paramTube, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public static Dispatch<Packet> createPacketDispatch(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, @Nullable WSEndpointReference paramWSEndpointReference) { return new PacketDispatch(paramWSPortInfo, (BindingImpl)paramWSBinding, paramWSEndpointReference); }
  
  public <T> T createPortProxy(WSService paramWSService, WSBinding paramWSBinding, SEIModel paramSEIModel, Class<T> paramClass, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference) {
    SEIStub sEIStub = new SEIStub((WSServiceDelegate)paramWSService, (BindingImpl)paramWSBinding, (SOAPSEIModel)paramSEIModel, paramTube, paramWSEndpointReference);
    return (T)paramClass.cast(Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass, com.sun.xml.internal.ws.developer.WSBindingProvider.class }, sEIStub));
  }
  
  public <T> T createPortProxy(WSPortInfo paramWSPortInfo, WSBinding paramWSBinding, SEIModel paramSEIModel, Class<T> paramClass, @Nullable WSEndpointReference paramWSEndpointReference) {
    SEIStub sEIStub = new SEIStub(paramWSPortInfo, (BindingImpl)paramWSBinding, (SOAPSEIModel)paramSEIModel, paramWSEndpointReference);
    return (T)paramClass.cast(Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass, com.sun.xml.internal.ws.developer.WSBindingProvider.class }, sEIStub));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\Stubs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */