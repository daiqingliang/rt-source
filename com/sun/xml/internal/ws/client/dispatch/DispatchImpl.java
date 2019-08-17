package com.sun.xml.internal.ws.client.dispatch;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class DispatchImpl<T> extends Stub implements Dispatch<T> {
  private static final Logger LOGGER = Logger.getLogger(DispatchImpl.class.getName());
  
  final Service.Mode mode;
  
  final SOAPVersion soapVersion;
  
  final boolean allowFaultResponseMsg;
  
  static final long AWAIT_TERMINATION_TIME = 800L;
  
  static final String HTTP_REQUEST_METHOD_GET = "GET";
  
  static final String HTTP_REQUEST_METHOD_POST = "POST";
  
  static final String HTTP_REQUEST_METHOD_PUT = "PUT";
  
  @Deprecated
  protected DispatchImpl(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference) {
    super(paramQName, paramWSServiceDelegate, paramTube, paramBindingImpl, (paramWSServiceDelegate.getWsdlService() != null) ? paramWSServiceDelegate.getWsdlService().get(paramQName) : null, paramWSServiceDelegate.getEndpointAddress(paramQName), paramWSEndpointReference);
    this.mode = paramMode;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.allowFaultResponseMsg = false;
  }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference) { this(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference, false); }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference, boolean paramBoolean) { this(paramWSPortInfo, paramMode, paramBindingImpl, null, paramWSEndpointReference, paramBoolean); }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference, boolean paramBoolean) {
    super(paramWSPortInfo, paramBindingImpl, paramTube, paramWSPortInfo.getEndpointAddress(), paramWSEndpointReference);
    this.mode = paramMode;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.allowFaultResponseMsg = paramBoolean;
  }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, Tube paramTube, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference, boolean paramBoolean) {
    super(paramWSPortInfo, paramBindingImpl, paramTube, paramWSPortInfo.getEndpointAddress(), paramWSEndpointReference);
    this.mode = paramMode;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.allowFaultResponseMsg = paramBoolean;
  }
  
  abstract Packet createPacket(T paramT);
  
  abstract T toReturnValue(Packet paramPacket);
  
  public final Response<T> invokeAsync(T paramT) {
    container = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try {
      if (LOGGER.isLoggable(Level.FINE))
        dumpParam(paramT, "invokeAsync(T)"); 
      DispatchAsyncInvoker dispatchAsyncInvoker = new DispatchAsyncInvoker(paramT);
      AsyncResponseImpl asyncResponseImpl = new AsyncResponseImpl(dispatchAsyncInvoker, null);
      dispatchAsyncInvoker.setReceiver(asyncResponseImpl);
      asyncResponseImpl.run();
      return asyncResponseImpl;
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  private void dumpParam(T paramT, String paramString) {
    if (paramT instanceof Packet) {
      Packet packet = (Packet)paramT;
      if (LOGGER.isLoggable(Level.FINE)) {
        AddressingVersion addressingVersion = getBinding().getAddressingVersion();
        SOAPVersion sOAPVersion = getBinding().getSOAPVersion();
        String str1 = (addressingVersion != null && packet.getMessage() != null) ? AddressingUtils.getAction(packet.getMessage().getHeaders(), addressingVersion, sOAPVersion) : null;
        String str2 = (addressingVersion != null && packet.getMessage() != null) ? AddressingUtils.getMessageID(packet.getMessage().getHeaders(), addressingVersion, sOAPVersion) : null;
        LOGGER.fine("In DispatchImpl." + paramString + " for message with action: " + str1 + " and msg ID: " + str2 + " msg: " + packet.getMessage());
        if (packet.getMessage() == null)
          LOGGER.fine("Dispatching null message for action: " + str1 + " and msg ID: " + str2); 
      } 
    } 
  }
  
  public final Future<?> invokeAsync(T paramT, AsyncHandler<T> paramAsyncHandler) {
    container = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try {
      if (LOGGER.isLoggable(Level.FINE))
        dumpParam(paramT, "invokeAsync(T, AsyncHandler<T>)"); 
      DispatchAsyncInvoker dispatchAsyncInvoker = new DispatchAsyncInvoker(paramT);
      AsyncResponseImpl asyncResponseImpl = new AsyncResponseImpl(dispatchAsyncInvoker, paramAsyncHandler);
      dispatchAsyncInvoker.setReceiver(asyncResponseImpl);
      dispatchAsyncInvoker.setNonNullAsyncHandlerGiven((paramAsyncHandler != null));
      asyncResponseImpl.run();
      return asyncResponseImpl;
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  public final T doInvoke(T paramT, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver) {
    packet = null;
    try {
      try {
        checkNullAllowed(paramT, paramRequestContext, this.binding, this.mode);
        Packet packet1 = createPacket(paramT);
        packet1.setState(Packet.State.ClientRequest);
        resolveEndpointAddress(packet1, paramRequestContext);
        setProperties(packet1, true);
        packet = process(packet1, paramRequestContext, paramResponseContextReceiver);
        Message message = packet.getMessage();
        if (message != null && message.isFault() && !this.allowFaultResponseMsg) {
          SOAPFaultBuilder sOAPFaultBuilder = SOAPFaultBuilder.create(message);
          throw (SOAPFaultException)sOAPFaultBuilder.createException(null);
        } 
      } catch (JAXBException jAXBException) {
        throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { jAXBException });
      } catch (WebServiceException webServiceException) {
        throw webServiceException;
      } catch (Throwable throwable) {
        throw new WebServiceException(throwable);
      } 
      object = toReturnValue(packet);
      return (T)object;
    } finally {
      if (packet != null && packet.transportBackChannel != null)
        packet.transportBackChannel.close(); 
    } 
  }
  
  public final T invoke(T paramT) {
    container = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try {
      if (LOGGER.isLoggable(Level.FINE))
        dumpParam(paramT, "invoke(T)"); 
      object = doInvoke(paramT, this.requestContext, this);
      return (T)object;
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  public final void invokeOneWay(T paramT) {
    container = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
    try {
      if (LOGGER.isLoggable(Level.FINE))
        dumpParam(paramT, "invokeOneWay(T)"); 
      try {
        checkNullAllowed(paramT, this.requestContext, this.binding, this.mode);
        Packet packet = createPacket(paramT);
        packet.setState(Packet.State.ClientRequest);
        setProperties(packet, false);
        process(packet, this.requestContext, this);
      } catch (WebServiceException webServiceException) {
        throw webServiceException;
      } catch (Throwable throwable) {
        throw new WebServiceException(throwable);
      } 
    } finally {
      ContainerResolver.getDefault().exitContainer(container);
    } 
  }
  
  void setProperties(Packet paramPacket, boolean paramBoolean) { paramPacket.expectReply = Boolean.valueOf(paramBoolean); }
  
  static boolean isXMLHttp(@NotNull WSBinding paramWSBinding) { return paramWSBinding.getBindingId().equals(BindingID.XML_HTTP); }
  
  static boolean isPAYLOADMode(@NotNull Service.Mode paramMode) { return (paramMode == Service.Mode.PAYLOAD); }
  
  static void checkNullAllowed(@Nullable Object paramObject, RequestContext paramRequestContext, WSBinding paramWSBinding, Service.Mode paramMode) {
    if (paramObject != null)
      return; 
    if (isXMLHttp(paramWSBinding)) {
      if (methodNotOk(paramRequestContext))
        throw new WebServiceException(DispatchMessages.INVALID_NULLARG_XMLHTTP_REQUEST_METHOD("POST", "GET")); 
    } else if (paramMode == Service.Mode.MESSAGE) {
      throw new WebServiceException(DispatchMessages.INVALID_NULLARG_SOAP_MSGMODE(paramMode.name(), Service.Mode.PAYLOAD.toString()));
    } 
  }
  
  static boolean methodNotOk(@NotNull RequestContext paramRequestContext) {
    String str1 = (String)paramRequestContext.get("javax.xml.ws.http.request.method");
    String str2 = (str1 == null) ? "POST" : str1;
    return ("POST".equalsIgnoreCase(str2) || "PUT".equalsIgnoreCase(str2));
  }
  
  public static void checkValidSOAPMessageDispatch(WSBinding paramWSBinding, Service.Mode paramMode) {
    if (isXMLHttp(paramWSBinding))
      throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_BINDING("http://www.w3.org/2004/08/wsdl/http", "http://schemas.xmlsoap.org/wsdl/soap/http or http://www.w3.org/2003/05/soap/bindings/HTTP/")); 
    if (isPAYLOADMode(paramMode))
      throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_MSGMODE(paramMode.name(), Service.Mode.MESSAGE.toString())); 
  }
  
  public static void checkValidDataSourceDispatch(WSBinding paramWSBinding, Service.Mode paramMode) {
    if (!isXMLHttp(paramWSBinding))
      throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_BINDING("SOAP/HTTP", "http://www.w3.org/2004/08/wsdl/http")); 
    if (isPAYLOADMode(paramMode))
      throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_MSGMODE(paramMode.name(), Service.Mode.MESSAGE.toString())); 
  }
  
  @NotNull
  public final QName getPortName() { return this.portname; }
  
  void resolveEndpointAddress(@NotNull Packet paramPacket, @NotNull RequestContext paramRequestContext) {
    String str1;
    boolean bool = paramPacket.packetTakesPriorityOverRequestContext;
    if (bool && paramPacket.endpointAddress != null) {
      str1 = paramPacket.endpointAddress.toString();
    } else {
      str1 = (String)paramRequestContext.get("javax.xml.ws.service.endpoint.address");
    } 
    if (str1 == null) {
      if (paramPacket.endpointAddress == null)
        throw new WebServiceException(DispatchMessages.INVALID_NULLARG_URI()); 
      str1 = paramPacket.endpointAddress.toString();
    } 
    String str2 = null;
    String str3 = null;
    if (bool && paramPacket.invocationProperties.get("javax.xml.ws.http.request.pathinfo") != null) {
      str2 = (String)paramPacket.invocationProperties.get("javax.xml.ws.http.request.pathinfo");
    } else if (paramRequestContext.get("javax.xml.ws.http.request.pathinfo") != null) {
      str2 = (String)paramRequestContext.get("javax.xml.ws.http.request.pathinfo");
    } 
    if (bool && paramPacket.invocationProperties.get("javax.xml.ws.http.request.querystring") != null) {
      str3 = (String)paramPacket.invocationProperties.get("javax.xml.ws.http.request.querystring");
    } else if (paramRequestContext.get("javax.xml.ws.http.request.querystring") != null) {
      str3 = (String)paramRequestContext.get("javax.xml.ws.http.request.querystring");
    } 
    if (str2 != null || str3 != null) {
      str2 = checkPath(str2);
      str3 = checkQuery(str3);
      if (str1 != null)
        try {
          URI uRI = new URI(str1);
          str1 = resolveURI(uRI, str2, str3);
        } catch (URISyntaxException uRISyntaxException) {
          throw new WebServiceException(DispatchMessages.INVALID_URI(str1));
        }  
    } 
    paramRequestContext.put("javax.xml.ws.service.endpoint.address", str1);
  }
  
  @NotNull
  protected String resolveURI(@NotNull URI paramURI, @Nullable String paramString1, @Nullable String paramString2) {
    String str1 = null;
    String str2 = null;
    if (paramString2 != null) {
      URI uRI;
      try {
        URI uRI1 = new URI(null, null, paramURI.getPath(), paramString2, null);
        uRI = paramURI.resolve(uRI1);
      } catch (URISyntaxException uRISyntaxException) {
        throw new WebServiceException(DispatchMessages.INVALID_QUERY_STRING(paramString2));
      } 
      str1 = uRI.getQuery();
      str2 = uRI.getFragment();
    } 
    String str3 = (paramString1 != null) ? paramString1 : paramURI.getPath();
    try {
      StringBuilder stringBuilder = new StringBuilder();
      if (str3 != null)
        stringBuilder.append(str3); 
      if (str1 != null) {
        stringBuilder.append("?");
        stringBuilder.append(str1);
      } 
      if (str2 != null) {
        stringBuilder.append("#");
        stringBuilder.append(str2);
      } 
      return (new URL(paramURI.toURL(), stringBuilder.toString())).toExternalForm();
    } catch (MalformedURLException malformedURLException) {
      throw new WebServiceException(DispatchMessages.INVALID_URI_RESOLUTION(str3));
    } 
  }
  
  private static String checkPath(@Nullable String paramString) { return (paramString == null || paramString.startsWith("/")) ? paramString : ("/" + paramString); }
  
  private static String checkQuery(@Nullable String paramString) {
    if (paramString == null)
      return null; 
    if (paramString.indexOf('?') == 0)
      throw new WebServiceException(DispatchMessages.INVALID_QUERY_LEADING_CHAR(paramString)); 
    return paramString;
  }
  
  protected AttachmentSet setOutboundAttachments() {
    HashMap hashMap = (HashMap)getRequestContext().get("javax.xml.ws.binding.attachments.outbound");
    if (hashMap != null) {
      ArrayList arrayList = new ArrayList();
      for (Map.Entry entry : hashMap.entrySet()) {
        DataHandlerAttachment dataHandlerAttachment = new DataHandlerAttachment((String)entry.getKey(), (DataHandler)entry.getValue());
        arrayList.add(dataHandlerAttachment);
      } 
      return new AttachmentSetImpl(arrayList);
    } 
    return new AttachmentSetImpl();
  }
  
  public void setOutboundHeaders(Object... paramVarArgs) { throw new UnsupportedOperationException(); }
  
  @Deprecated
  public static Dispatch<Source> createSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) { return isXMLHttp(paramBindingImpl) ? new RESTSourceDispatch(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference) : new SOAPSourceDispatch(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference); }
  
  public static Dispatch<Source> createSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) { return isXMLHttp(paramBindingImpl) ? new RESTSourceDispatch(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference) : new SOAPSourceDispatch(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference); }
  
  private class DispatchAsyncInvoker extends AsyncInvoker {
    private final T param;
    
    private final RequestContext rc = DispatchImpl.this.requestContext.copy();
    
    DispatchAsyncInvoker(T param1T) { this.param = param1T; }
    
    public void do_run() {
      DispatchImpl.checkNullAllowed(this.param, this.rc, DispatchImpl.this.binding, DispatchImpl.this.mode);
      Packet packet = DispatchImpl.this.createPacket(this.param);
      packet.setState(Packet.State.ClientRequest);
      packet.nonNullAsyncHandlerGiven = Boolean.valueOf(this.nonNullAsyncHandlerGiven);
      DispatchImpl.this.resolveEndpointAddress(packet, this.rc);
      DispatchImpl.this.setProperties(packet, true);
      String str1 = null;
      String str2 = null;
      if (LOGGER.isLoggable(Level.FINE)) {
        AddressingVersion addressingVersion = DispatchImpl.this.getBinding().getAddressingVersion();
        SOAPVersion sOAPVersion = DispatchImpl.this.getBinding().getSOAPVersion();
        str1 = (addressingVersion != null && packet.getMessage() != null) ? AddressingUtils.getAction(packet.getMessage().getHeaders(), addressingVersion, sOAPVersion) : null;
        str2 = (addressingVersion != null && packet.getMessage() != null) ? AddressingUtils.getMessageID(packet.getMessage().getHeaders(), addressingVersion, sOAPVersion) : null;
        LOGGER.fine("In DispatchAsyncInvoker.do_run for async message with action: " + str1 + " and msg ID: " + str2);
      } 
      final String actionUse = str1;
      final String msgIdUse = str2;
      Fiber.CompletionCallback completionCallback = new Fiber.CompletionCallback() {
          public void onCompletion(@NotNull Packet param2Packet) {
            if (LOGGER.isLoggable(Level.FINE))
              LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse); 
            Message message = param2Packet.getMessage();
            if (LOGGER.isLoggable(Level.FINE))
              LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse + " msg: " + message); 
            try {
              if (message != null && message.isFault() && !DispatchImpl.this.allowFaultResponseMsg) {
                SOAPFaultBuilder sOAPFaultBuilder = SOAPFaultBuilder.create(message);
                throw (SOAPFaultException)sOAPFaultBuilder.createException(null);
              } 
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(param2Packet));
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.set(DispatchImpl.DispatchAsyncInvoker.this.this$0.toReturnValue(param2Packet), null);
            } catch (JAXBException jAXBException) {
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.set(null, new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { jAXBException }));
            } catch (WebServiceException webServiceException) {
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.set(null, webServiceException);
            } catch (Throwable throwable) {
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.set(null, new WebServiceException(throwable));
            } 
          }
          
          public void onCompletion(@NotNull Throwable param2Throwable) {
            if (LOGGER.isLoggable(Level.FINE))
              LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse + " Throwable: " + param2Throwable.toString()); 
            if (param2Throwable instanceof WebServiceException) {
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.set(null, param2Throwable);
            } else {
              DispatchImpl.DispatchAsyncInvoker.this.responseImpl.set(null, new WebServiceException(param2Throwable));
            } 
          }
        };
      DispatchImpl.this.processAsync(this.responseImpl, packet, this.rc, completionCallback);
    }
  }
  
  private class Invoker implements Callable {
    private final T param;
    
    private final RequestContext rc = DispatchImpl.this.requestContext.copy();
    
    private ResponseContextReceiver receiver;
    
    Invoker(T param1T) { this.param = param1T; }
    
    public T call() throws Exception {
      if (LOGGER.isLoggable(Level.FINE))
        DispatchImpl.this.dumpParam(this.param, "call()"); 
      return (T)DispatchImpl.this.doInvoke(this.param, this.rc, this.receiver);
    }
    
    void setReceiver(ResponseContextReceiver param1ResponseContextReceiver) { this.receiver = param1ResponseContextReceiver; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\dispatch\DispatchImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */