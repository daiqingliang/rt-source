package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.util.List;
import javax.jws.soap.SOAPBinding;

public class StubAsyncHandler extends StubHandler {
  private final Class asyncBeanClass;
  
  public StubAsyncHandler(JavaMethodImpl paramJavaMethodImpl1, JavaMethodImpl paramJavaMethodImpl2, MessageContextFactory paramMessageContextFactory) {
    super(paramJavaMethodImpl2, paramMessageContextFactory);
    List list = paramJavaMethodImpl2.getResponseParameters();
    int i = 0;
    for (ParameterImpl parameterImpl : list) {
      if (parameterImpl.isWrapperStyle()) {
        WrapperParameter wrapperParameter = (WrapperParameter)parameterImpl;
        i += wrapperParameter.getWrapperChildren().size();
        if (paramJavaMethodImpl2.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT)
          i += 2; 
        continue;
      } 
      i++;
    } 
    Class clazz = null;
    if (i > 1) {
      list = paramJavaMethodImpl1.getResponseParameters();
      for (ParameterImpl parameterImpl : list) {
        if (parameterImpl.isWrapperStyle()) {
          WrapperParameter wrapperParameter = (WrapperParameter)parameterImpl;
          if (paramJavaMethodImpl2.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT) {
            clazz = (Class)(wrapperParameter.getTypeInfo()).type;
            break;
          } 
          for (ParameterImpl parameterImpl1 : wrapperParameter.getWrapperChildren()) {
            if (parameterImpl1.getIndex() == -1) {
              clazz = (Class)(parameterImpl1.getTypeInfo()).type;
              break;
            } 
          } 
          if (clazz != null)
            break; 
          continue;
        } 
        if (parameterImpl.getIndex() == -1) {
          clazz = (Class)(parameterImpl.getTypeInfo()).type;
          break;
        } 
      } 
    } 
    this.asyncBeanClass = clazz;
    switch (i) {
      case 0:
        this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl2, ValueSetterFactory.NONE);
        return;
      case 1:
        this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl2, ValueSetterFactory.SINGLE);
        return;
    } 
    this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl2, new ValueSetterFactory.AsyncBeanValueSetterFactory(this.asyncBeanClass));
  }
  
  protected void initArgs(Object[] paramArrayOfObject) throws Exception {
    if (this.asyncBeanClass != null)
      paramArrayOfObject[0] = this.asyncBeanClass.newInstance(); 
  }
  
  ValueGetterFactory getValueGetterFactory() { return ValueGetterFactory.ASYNC; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\StubAsyncHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */