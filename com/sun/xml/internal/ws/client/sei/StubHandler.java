package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class StubHandler implements ClientCallBridge {
  private final BodyBuilder bodyBuilder;
  
  private final MessageFiller[] inFillers;
  
  protected final String soapAction;
  
  protected final boolean isOneWay;
  
  protected final JavaMethodImpl javaMethod;
  
  protected final Map<QName, CheckedExceptionImpl> checkedExceptions = new HashMap();
  
  protected SOAPVersion soapVersion = SOAPVersion.SOAP_11;
  
  protected ResponseBuilder responseBuilder;
  
  protected MessageContextFactory packetFactory;
  
  public StubHandler(JavaMethodImpl paramJavaMethodImpl, MessageContextFactory paramMessageContextFactory) {
    for (CheckedExceptionImpl checkedExceptionImpl : paramJavaMethodImpl.getCheckedExceptions())
      this.checkedExceptions.put((checkedExceptionImpl.getBond().getTypeInfo()).tagName, checkedExceptionImpl); 
    String str = paramJavaMethodImpl.getBinding().getSOAPAction();
    if (paramJavaMethodImpl.getInputAction() != null && str != null && !str.equals("")) {
      this.soapAction = paramJavaMethodImpl.getInputAction();
    } else {
      this.soapAction = str;
    } 
    this.javaMethod = paramJavaMethodImpl;
    this.packetFactory = paramMessageContextFactory;
    this.soapVersion = this.javaMethod.getBinding().getSOAPVersion();
    List list = paramJavaMethodImpl.getRequestParameters();
    BodyBuilder bodyBuilder1 = null;
    ArrayList arrayList = new ArrayList();
    for (ParameterImpl parameterImpl : list) {
      ValueGetter valueGetter = getValueGetterFactory().get(parameterImpl);
      switch ((parameterImpl.getInBinding()).kind) {
        case SOAP_11:
          if (parameterImpl.isWrapperStyle()) {
            if (parameterImpl.getParent().getBinding().isRpcLit()) {
              bodyBuilder1 = new BodyBuilder.RpcLit((WrapperParameter)parameterImpl, this.soapVersion, getValueGetterFactory());
              continue;
            } 
            BodyBuilder.DocLit docLit = new BodyBuilder.DocLit((WrapperParameter)parameterImpl, this.soapVersion, getValueGetterFactory());
            continue;
          } 
          bodyBuilder1 = new BodyBuilder.Bare(parameterImpl, this.soapVersion, valueGetter);
          continue;
        case SOAP_12:
          arrayList.add(new MessageFiller.Header(parameterImpl.getIndex(), parameterImpl.getXMLBridge(), valueGetter));
          continue;
        case null:
          arrayList.add(MessageFiller.AttachmentFiller.createAttachmentFiller(parameterImpl, valueGetter));
          continue;
        case null:
          continue;
      } 
      throw new AssertionError();
    } 
    if (bodyBuilder1 == null)
      switch (this.soapVersion) {
        case SOAP_11:
          bodyBuilder1 = BodyBuilder.EMPTY_SOAP11;
          break;
        case SOAP_12:
          bodyBuilder1 = BodyBuilder.EMPTY_SOAP12;
          break;
        default:
          throw new AssertionError();
      }  
    this.bodyBuilder = bodyBuilder1;
    this.inFillers = (MessageFiller[])arrayList.toArray(new MessageFiller[arrayList.size()]);
    this.isOneWay = paramJavaMethodImpl.getMEP().isOneWay();
    this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl, ValueSetterFactory.SYNC);
  }
  
  ResponseBuilder buildResponseBuilder(JavaMethodImpl paramJavaMethodImpl, ValueSetterFactory paramValueSetterFactory) {
    List list = paramJavaMethodImpl.getResponseParameters();
    ArrayList arrayList = new ArrayList();
    for (ParameterImpl parameterImpl : list) {
      ValueSetter valueSetter;
      switch ((parameterImpl.getOutBinding()).kind) {
        case SOAP_11:
          if (parameterImpl.isWrapperStyle()) {
            if (parameterImpl.getParent().getBinding().isRpcLit()) {
              arrayList.add(new ResponseBuilder.RpcLit((WrapperParameter)parameterImpl, paramValueSetterFactory));
              continue;
            } 
            arrayList.add(new ResponseBuilder.DocLit((WrapperParameter)parameterImpl, paramValueSetterFactory));
            continue;
          } 
          valueSetter = paramValueSetterFactory.get(parameterImpl);
          arrayList.add(new ResponseBuilder.Body(parameterImpl.getXMLBridge(), valueSetter));
          continue;
        case SOAP_12:
          valueSetter = paramValueSetterFactory.get(parameterImpl);
          arrayList.add(new ResponseBuilder.Header(this.soapVersion, parameterImpl, valueSetter));
          continue;
        case null:
          valueSetter = paramValueSetterFactory.get(parameterImpl);
          arrayList.add(ResponseBuilder.AttachmentBuilder.createAttachmentBuilder(parameterImpl, valueSetter));
          continue;
        case null:
          valueSetter = paramValueSetterFactory.get(parameterImpl);
          arrayList.add(new ResponseBuilder.NullSetter(valueSetter, ResponseBuilder.getVMUninitializedValue((parameterImpl.getTypeInfo()).type)));
          continue;
      } 
      throw new AssertionError();
    } 
    switch (arrayList.size()) {
      case 0:
        return ResponseBuilder.NONE;
      case 1:
        return (ResponseBuilder)arrayList.get(0);
    } 
    return new ResponseBuilder.Composite(arrayList);
  }
  
  public Packet createRequestPacket(JavaCallInfo paramJavaCallInfo) {
    Message message = this.bodyBuilder.createMessage(paramJavaCallInfo.getParameters());
    for (MessageFiller messageFiller : this.inFillers)
      messageFiller.fillIn(paramJavaCallInfo.getParameters(), message); 
    Packet packet = (Packet)this.packetFactory.createContext(message);
    packet.setState(Packet.State.ClientRequest);
    packet.soapAction = this.soapAction;
    packet.expectReply = Boolean.valueOf(!this.isOneWay);
    packet.getMessage().assertOneWay(this.isOneWay);
    packet.setWSDLOperation(getOperationName());
    return packet;
  }
  
  ValueGetterFactory getValueGetterFactory() { return ValueGetterFactory.SYNC; }
  
  public JavaCallInfo readResponse(Packet paramPacket, JavaCallInfo paramJavaCallInfo) throws Throwable {
    Message message = paramPacket.getMessage();
    if (message.isFault()) {
      SOAPFaultBuilder sOAPFaultBuilder = SOAPFaultBuilder.create(message);
      Throwable throwable = sOAPFaultBuilder.createException(this.checkedExceptions);
      paramJavaCallInfo.setException(throwable);
      throw throwable;
    } 
    initArgs(paramJavaCallInfo.getParameters());
    Object object = this.responseBuilder.readResponse(message, paramJavaCallInfo.getParameters());
    paramJavaCallInfo.setReturnValue(object);
    return paramJavaCallInfo;
  }
  
  public QName getOperationName() { return this.javaMethod.getOperationQName(); }
  
  public String getSoapAction() { return this.soapAction; }
  
  public boolean isOneWay() { return this.isOneWay; }
  
  protected void initArgs(Object[] paramArrayOfObject) throws Exception {}
  
  public Method getMethod() { return this.javaMethod.getMethod(); }
  
  public JavaMethod getOperationModel() { return this.javaMethod; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\StubHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */