package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;

final class ProviderEndpointModel<T> extends Object {
  final boolean isAsync;
  
  @NotNull
  final Service.Mode mode;
  
  @NotNull
  final Class datatype;
  
  @NotNull
  final Class implClass;
  
  ProviderEndpointModel(Class<T> paramClass, WSBinding paramWSBinding) {
    assert paramClass != null;
    assert paramWSBinding != null;
    this.implClass = paramClass;
    this.mode = getServiceMode(paramClass);
    Class clazz1 = (paramWSBinding instanceof javax.xml.ws.soap.SOAPBinding) ? javax.xml.soap.SOAPMessage.class : javax.activation.DataSource.class;
    this.isAsync = com.sun.xml.internal.ws.api.server.AsyncProvider.class.isAssignableFrom(paramClass);
    Class clazz2 = this.isAsync ? com.sun.xml.internal.ws.api.server.AsyncProvider.class : javax.xml.ws.Provider.class;
    Type type = BindingHelper.getBaseType(paramClass, clazz2);
    if (type == null)
      throw new WebServiceException(ServerMessages.NOT_IMPLEMENT_PROVIDER(paramClass.getName())); 
    if (!(type instanceof ParameterizedType))
      throw new WebServiceException(ServerMessages.PROVIDER_NOT_PARAMETERIZED(paramClass.getName())); 
    ParameterizedType parameterizedType = (ParameterizedType)type;
    Type[] arrayOfType = parameterizedType.getActualTypeArguments();
    if (!(arrayOfType[0] instanceof Class))
      throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(paramClass.getName(), arrayOfType[0])); 
    this.datatype = (Class)arrayOfType[0];
    if (this.mode == Service.Mode.PAYLOAD && this.datatype != javax.xml.transform.Source.class)
      throw new IllegalArgumentException("Illeagal combination - Mode.PAYLOAD and Provider<" + clazz1.getName() + ">"); 
  }
  
  private static Service.Mode getServiceMode(Class<?> paramClass) {
    ServiceMode serviceMode = (ServiceMode)paramClass.getAnnotation(ServiceMode.class);
    return (serviceMode == null) ? Service.Mode.PAYLOAD : serviceMode.value();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\ProviderEndpointModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */