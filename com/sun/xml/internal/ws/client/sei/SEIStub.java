package com.sun.xml.internal.ws.client.sei;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public final class SEIStub extends Stub implements InvocationHandler {
  Databinding databinding;
  
  public final SOAPSEIModel seiModel;
  
  public final SOAPVersion soapVersion;
  
  private final Map<Method, MethodHandler> methodHandlers = new HashMap();
  
  @Deprecated
  public SEIStub(WSServiceDelegate paramWSServiceDelegate, BindingImpl paramBindingImpl, SOAPSEIModel paramSOAPSEIModel, Tube paramTube, WSEndpointReference paramWSEndpointReference) {
    super(paramWSServiceDelegate, paramTube, paramBindingImpl, paramSOAPSEIModel.getPort(), paramSOAPSEIModel.getPort().getAddress(), paramWSEndpointReference);
    this.seiModel = paramSOAPSEIModel;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.databinding = paramSOAPSEIModel.getDatabinding();
    initMethodHandlers();
  }
  
  public SEIStub(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, SOAPSEIModel paramSOAPSEIModel, WSEndpointReference paramWSEndpointReference) {
    super(paramWSPortInfo, paramBindingImpl, paramSOAPSEIModel.getPort().getAddress(), paramWSEndpointReference);
    this.seiModel = paramSOAPSEIModel;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.databinding = paramSOAPSEIModel.getDatabinding();
    initMethodHandlers();
  }
  
  private void initMethodHandlers() {
    HashMap hashMap = new HashMap();
    for (JavaMethodImpl javaMethodImpl : this.seiModel.getJavaMethods()) {
      if (!(javaMethodImpl.getMEP()).isAsync) {
        SyncMethodHandler syncMethodHandler = new SyncMethodHandler(this, javaMethodImpl);
        hashMap.put(javaMethodImpl.getOperation(), javaMethodImpl);
        this.methodHandlers.put(javaMethodImpl.getMethod(), syncMethodHandler);
      } 
    } 
    for (JavaMethodImpl javaMethodImpl1 : this.seiModel.getJavaMethods()) {
      JavaMethodImpl javaMethodImpl2 = (JavaMethodImpl)hashMap.get(javaMethodImpl1.getOperation());
      if (javaMethodImpl1.getMEP() == MEP.ASYNC_CALLBACK) {
        Method method = javaMethodImpl1.getMethod();
        CallbackMethodHandler callbackMethodHandler = new CallbackMethodHandler(this, method, method.getParameterTypes().length - 1);
        this.methodHandlers.put(method, callbackMethodHandler);
      } 
      if (javaMethodImpl1.getMEP() == MEP.ASYNC_POLL) {
        Method method = javaMethodImpl1.getMethod();
        PollingMethodHandler pollingMethodHandler = new PollingMethodHandler(this, method);
        this.methodHandlers.put(method, pollingMethodHandler);
      } 
    } 
  }
  
  @Nullable
  public OperationDispatcher getOperationDispatcher() {
    if (this.operationDispatcher == null && this.wsdlPort != null)
      this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, this.seiModel); 
    return this.operationDispatcher;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    validateInputs(paramObject, paramMethod);
    container = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try {
      MethodHandler methodHandler = (MethodHandler)this.methodHandlers.get(paramMethod);
      if (methodHandler != null)
        return methodHandler.invoke(paramObject, paramArrayOfObject); 
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  private void validateInputs(Object paramObject, Method paramMethod) {
    if (paramObject == null || !Proxy.isProxyClass(paramObject.getClass()))
      throw new IllegalStateException("Passed object is not proxy!"); 
    Class clazz = paramMethod.getDeclaringClass();
    if (paramMethod == null || clazz == null || Modifier.isStatic(paramMethod.getModifiers()))
      throw new IllegalStateException("Invoking static method is not allowed!"); 
  }
  
  public final Packet doProcess(Packet paramPacket, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver) { return process(paramPacket, paramRequestContext, paramResponseContextReceiver); }
  
  public final void doProcessAsync(AsyncResponseImpl<?> paramAsyncResponseImpl, Packet paramPacket, RequestContext paramRequestContext, Fiber.CompletionCallback paramCompletionCallback) { processAsync(paramAsyncResponseImpl, paramPacket, paramRequestContext, paramCompletionCallback); }
  
  @NotNull
  protected final QName getPortName() { return this.wsdlPort.getName(); }
  
  public void setOutboundHeaders(Object... paramVarArgs) {
    if (paramVarArgs == null)
      throw new IllegalArgumentException(); 
    Header[] arrayOfHeader = new Header[paramVarArgs.length];
    for (byte b = 0; b < arrayOfHeader.length; b++) {
      if (paramVarArgs[b] == null)
        throw new IllegalArgumentException(); 
      arrayOfHeader[b] = Headers.create(this.seiModel.getBindingContext(), paramVarArgs[b]);
    } 
    setOutboundHeaders(arrayOfHeader);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\SEIStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */