package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

abstract class SEIMethodHandler extends MethodHandler {
  private BodyBuilder bodyBuilder;
  
  private MessageFiller[] inFillers;
  
  protected String soapAction;
  
  protected boolean isOneWay;
  
  protected JavaMethodImpl javaMethod;
  
  protected Map<QName, CheckedExceptionImpl> checkedExceptions;
  
  SEIMethodHandler(SEIStub paramSEIStub) { super(paramSEIStub, null); }
  
  SEIMethodHandler(SEIStub paramSEIStub, JavaMethodImpl paramJavaMethodImpl) {
    super(paramSEIStub, null);
    this.checkedExceptions = new HashMap();
    for (CheckedExceptionImpl checkedExceptionImpl : paramJavaMethodImpl.getCheckedExceptions())
      this.checkedExceptions.put((checkedExceptionImpl.getBond().getTypeInfo()).tagName, checkedExceptionImpl); 
    if (paramJavaMethodImpl.getInputAction() != null && !paramJavaMethodImpl.getBinding().getSOAPAction().equals("")) {
      this.soapAction = paramJavaMethodImpl.getInputAction();
    } else {
      this.soapAction = paramJavaMethodImpl.getBinding().getSOAPAction();
    } 
    this.javaMethod = paramJavaMethodImpl;
    List list = paramJavaMethodImpl.getRequestParameters();
    BodyBuilder bodyBuilder1 = null;
    ArrayList arrayList = new ArrayList();
    for (ParameterImpl parameterImpl : list) {
      ValueGetter valueGetter = getValueGetterFactory().get(parameterImpl);
      switch ((parameterImpl.getInBinding()).kind) {
        case SOAP_11:
          if (parameterImpl.isWrapperStyle()) {
            if (parameterImpl.getParent().getBinding().isRpcLit()) {
              bodyBuilder1 = new BodyBuilder.RpcLit((WrapperParameter)parameterImpl, paramSEIStub.soapVersion, getValueGetterFactory());
              continue;
            } 
            BodyBuilder.DocLit docLit = new BodyBuilder.DocLit((WrapperParameter)parameterImpl, paramSEIStub.soapVersion, getValueGetterFactory());
            continue;
          } 
          bodyBuilder1 = new BodyBuilder.Bare(parameterImpl, paramSEIStub.soapVersion, valueGetter);
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
      switch (paramSEIStub.soapVersion) {
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
          arrayList.add(new ResponseBuilder.Header(this.owner.soapVersion, parameterImpl, valueSetter));
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
  
  Message createRequestMessage(Object[] paramArrayOfObject) {
    Message message = this.bodyBuilder.createMessage(paramArrayOfObject);
    for (MessageFiller messageFiller : this.inFillers)
      messageFiller.fillIn(paramArrayOfObject, message); 
    return message;
  }
  
  abstract ValueGetterFactory getValueGetterFactory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\SEIMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */