package com.sun.xml.internal.ws.server.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

public final class TieHandler implements EndpointCallBridge {
  private final SOAPVersion soapVersion;
  
  private final Method method;
  
  private final int noOfArgs;
  
  private final JavaMethodImpl javaMethodModel;
  
  private final Boolean isOneWay;
  
  private final EndpointArgumentsBuilder argumentsBuilder;
  
  private final EndpointResponseMessageBuilder bodyBuilder;
  
  private final MessageFiller[] outFillers;
  
  protected MessageContextFactory packetFactory;
  
  private static final Logger LOGGER = Logger.getLogger(TieHandler.class.getName());
  
  public TieHandler(JavaMethodImpl paramJavaMethodImpl, WSBinding paramWSBinding, MessageContextFactory paramMessageContextFactory) {
    this.soapVersion = paramWSBinding.getSOAPVersion();
    this.method = paramJavaMethodImpl.getMethod();
    this.javaMethodModel = paramJavaMethodImpl;
    this.argumentsBuilder = createArgumentsBuilder();
    ArrayList arrayList = new ArrayList();
    this.bodyBuilder = createResponseMessageBuilder(arrayList);
    this.outFillers = (MessageFiller[])arrayList.toArray(new MessageFiller[arrayList.size()]);
    this.isOneWay = Boolean.valueOf(paramJavaMethodImpl.getMEP().isOneWay());
    this.noOfArgs = this.method.getParameterTypes().length;
    this.packetFactory = paramMessageContextFactory;
  }
  
  private EndpointArgumentsBuilder createArgumentsBuilder() {
    List list1 = this.javaMethodModel.getRequestParameters();
    ArrayList arrayList = new ArrayList();
    for (ParameterImpl parameterImpl : list1) {
      EndpointValueSetter endpointValueSetter = EndpointValueSetter.get(parameterImpl);
      switch ((parameterImpl.getInBinding()).kind) {
        case SOAP_11:
          if (parameterImpl.isWrapperStyle()) {
            if (parameterImpl.getParent().getBinding().isRpcLit()) {
              arrayList.add(new EndpointArgumentsBuilder.RpcLit((WrapperParameter)parameterImpl));
              continue;
            } 
            arrayList.add(new EndpointArgumentsBuilder.DocLit((WrapperParameter)parameterImpl, WebParam.Mode.OUT));
            continue;
          } 
          arrayList.add(new EndpointArgumentsBuilder.Body(parameterImpl.getXMLBridge(), endpointValueSetter));
          continue;
        case SOAP_12:
          arrayList.add(new EndpointArgumentsBuilder.Header(this.soapVersion, parameterImpl, endpointValueSetter));
          continue;
        case null:
          arrayList.add(EndpointArgumentsBuilder.AttachmentBuilder.createAttachmentBuilder(parameterImpl, endpointValueSetter));
          continue;
        case null:
          arrayList.add(new EndpointArgumentsBuilder.NullSetter(endpointValueSetter, EndpointArgumentsBuilder.getVMUninitializedValue((parameterImpl.getTypeInfo()).type)));
          continue;
      } 
      throw new AssertionError();
    } 
    List list2 = this.javaMethodModel.getResponseParameters();
    for (ParameterImpl parameterImpl : list2) {
      if (parameterImpl.isWrapperStyle()) {
        WrapperParameter wrapperParameter = (WrapperParameter)parameterImpl;
        List list = wrapperParameter.getWrapperChildren();
        for (ParameterImpl parameterImpl1 : list) {
          if (parameterImpl1.isOUT() && parameterImpl1.getIndex() != -1) {
            EndpointValueSetter endpointValueSetter = EndpointValueSetter.get(parameterImpl1);
            arrayList.add(new EndpointArgumentsBuilder.NullSetter(endpointValueSetter, null));
          } 
        } 
        continue;
      } 
      if (parameterImpl.isOUT() && parameterImpl.getIndex() != -1) {
        EndpointValueSetter endpointValueSetter = EndpointValueSetter.get(parameterImpl);
        arrayList.add(new EndpointArgumentsBuilder.NullSetter(endpointValueSetter, null));
      } 
    } 
    switch (arrayList.size()) {
      case 0:
        return EndpointArgumentsBuilder.NONE;
      case 1:
        return (EndpointArgumentsBuilder)arrayList.get(0);
    } 
    return new EndpointArgumentsBuilder.Composite(arrayList);
  }
  
  private EndpointResponseMessageBuilder createResponseMessageBuilder(List<MessageFiller> paramList) {
    EndpointResponseMessageBuilder.Bare bare = null;
    List list = this.javaMethodModel.getResponseParameters();
    for (ParameterImpl parameterImpl : list) {
      ValueGetter valueGetter = ValueGetter.get(parameterImpl);
      switch ((parameterImpl.getOutBinding()).kind) {
        case SOAP_11:
          if (parameterImpl.isWrapperStyle()) {
            if (parameterImpl.getParent().getBinding().isRpcLit()) {
              bare = new EndpointResponseMessageBuilder.RpcLit((WrapperParameter)parameterImpl, this.soapVersion);
              continue;
            } 
            EndpointResponseMessageBuilder.DocLit docLit = new EndpointResponseMessageBuilder.DocLit((WrapperParameter)parameterImpl, this.soapVersion);
            continue;
          } 
          bare = new EndpointResponseMessageBuilder.Bare(parameterImpl, this.soapVersion);
          continue;
        case SOAP_12:
          paramList.add(new MessageFiller.Header(parameterImpl.getIndex(), parameterImpl.getXMLBridge(), valueGetter));
          continue;
        case null:
          paramList.add(MessageFiller.AttachmentFiller.createAttachmentFiller(parameterImpl, valueGetter));
          continue;
        case null:
          continue;
      } 
      throw new AssertionError();
    } 
    if (bare == null) {
      switch (this.soapVersion) {
        case SOAP_11:
          return EndpointResponseMessageBuilder.EMPTY_SOAP11;
        case SOAP_12:
          return EndpointResponseMessageBuilder.EMPTY_SOAP12;
      } 
      throw new AssertionError();
    } 
    return bare;
  }
  
  public Object[] readRequest(Message paramMessage) {
    Object[] arrayOfObject = new Object[this.noOfArgs];
    try {
      this.argumentsBuilder.readRequest(paramMessage, arrayOfObject);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
    return arrayOfObject;
  }
  
  public Message createResponse(JavaCallInfo paramJavaCallInfo) {
    Message message;
    if (paramJavaCallInfo.getException() == null) {
      message = this.isOneWay.booleanValue() ? null : createResponseMessage(paramJavaCallInfo.getParameters(), paramJavaCallInfo.getReturnValue());
    } else {
      Throwable throwable1 = paramJavaCallInfo.getException();
      Throwable throwable2 = getServiceException(throwable1);
      if (throwable1 instanceof java.lang.reflect.InvocationTargetException || throwable2 != null) {
        if (throwable2 != null) {
          LOGGER.log(Level.FINE, throwable2.getMessage(), throwable2);
          message = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.javaMethodModel.getCheckedException(throwable2.getClass()), throwable2);
        } else {
          Throwable throwable = throwable1.getCause();
          if (throwable instanceof javax.xml.ws.ProtocolException) {
            LOGGER.log(Level.FINE, throwable.getMessage(), throwable);
          } else {
            LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable);
          } 
          message = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, throwable);
        } 
      } else if (throwable1 instanceof DispatchException) {
        message = ((DispatchException)throwable1).fault;
      } else {
        LOGGER.log(Level.SEVERE, throwable1.getMessage(), throwable1);
        message = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, throwable1);
      } 
    } 
    return message;
  }
  
  Throwable getServiceException(Throwable paramThrowable) {
    if (this.javaMethodModel.getCheckedException(paramThrowable.getClass()) != null)
      return paramThrowable; 
    if (paramThrowable.getCause() != null) {
      Throwable throwable = paramThrowable.getCause();
      if (this.javaMethodModel.getCheckedException(throwable.getClass()) != null)
        return throwable; 
    } 
    return null;
  }
  
  private Message createResponseMessage(Object[] paramArrayOfObject, Object paramObject) {
    Message message = this.bodyBuilder.createMessage(paramArrayOfObject, paramObject);
    for (MessageFiller messageFiller : this.outFillers)
      messageFiller.fillIn(paramArrayOfObject, paramObject, message); 
    return message;
  }
  
  public Method getMethod() { return this.method; }
  
  public JavaCallInfo deserializeRequest(Packet paramPacket) {
    JavaCallInfo javaCallInfo = new JavaCallInfo();
    javaCallInfo.setMethod(getMethod());
    Object[] arrayOfObject = readRequest(paramPacket.getMessage());
    javaCallInfo.setParameters(arrayOfObject);
    return javaCallInfo;
  }
  
  public Packet serializeResponse(JavaCallInfo paramJavaCallInfo) {
    Message message = createResponse(paramJavaCallInfo);
    Packet packet = (message == null) ? (Packet)this.packetFactory.createContext() : (Packet)this.packetFactory.createContext(message);
    packet.setState(Packet.State.ServerResponse);
    return packet;
  }
  
  public JavaMethod getOperationModel() { return this.javaMethodModel; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\TieHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */