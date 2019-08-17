package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.net.httpserver.HttpContext;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointContext;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServicePermission;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.http.HttpContext;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class EndpointImpl extends Endpoint {
  private static final WebServicePermission ENDPOINT_PUBLISH_PERMISSION = new WebServicePermission("publishEndpoint");
  
  private Object actualEndpoint;
  
  private final WSBinding binding;
  
  @Nullable
  private final Object implementor;
  
  private List<Source> metadata;
  
  private Executor executor;
  
  private Map<String, Object> properties = Collections.emptyMap();
  
  private boolean stopped;
  
  @Nullable
  private EndpointContext endpointContext;
  
  @NotNull
  private final Class<?> implClass;
  
  private final Invoker invoker;
  
  private Container container;
  
  public EndpointImpl(@NotNull BindingID paramBindingID, @NotNull Object paramObject, WebServiceFeature... paramVarArgs) { this(paramBindingID, paramObject, paramObject.getClass(), InstanceResolver.createSingleton(paramObject).createInvoker(), paramVarArgs); }
  
  public EndpointImpl(@NotNull BindingID paramBindingID, @NotNull Class paramClass, Invoker paramInvoker, WebServiceFeature... paramVarArgs) { this(paramBindingID, null, paramClass, new InvokerImpl(paramInvoker), paramVarArgs); }
  
  private EndpointImpl(@NotNull BindingID paramBindingID, Object paramObject, @NotNull Class paramClass, Invoker paramInvoker, WebServiceFeature... paramVarArgs) {
    this.binding = BindingImpl.create(paramBindingID, paramVarArgs);
    this.implClass = paramClass;
    this.invoker = paramInvoker;
    this.implementor = paramObject;
  }
  
  public EndpointImpl(WSEndpoint paramWSEndpoint, Object paramObject) { this(paramWSEndpoint, paramObject, null); }
  
  public EndpointImpl(WSEndpoint paramWSEndpoint, Object paramObject, EndpointContext paramEndpointContext) {
    this.endpointContext = paramEndpointContext;
    this.actualEndpoint = new HttpEndpoint(null, getAdapter(paramWSEndpoint, ""));
    ((HttpEndpoint)this.actualEndpoint).publish(paramObject);
    this.binding = paramWSEndpoint.getBinding();
    this.implementor = null;
    this.implClass = null;
    this.invoker = null;
  }
  
  public EndpointImpl(WSEndpoint paramWSEndpoint, String paramString) { this(paramWSEndpoint, paramString, null); }
  
  public EndpointImpl(WSEndpoint paramWSEndpoint, String paramString, EndpointContext paramEndpointContext) {
    try {
      uRL = new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException("Cannot create URL for this address " + paramString);
    } 
    if (!uRL.getProtocol().equals("http"))
      throw new IllegalArgumentException(uRL.getProtocol() + " protocol based address is not supported"); 
    if (!uRL.getPath().startsWith("/"))
      throw new IllegalArgumentException("Incorrect WebService address=" + paramString + ". The address's path should start with /"); 
    this.endpointContext = paramEndpointContext;
    this.actualEndpoint = new HttpEndpoint(null, getAdapter(paramWSEndpoint, uRL.getPath()));
    ((HttpEndpoint)this.actualEndpoint).publish(paramString);
    this.binding = paramWSEndpoint.getBinding();
    this.implementor = null;
    this.implClass = null;
    this.invoker = null;
  }
  
  public Binding getBinding() { return this.binding; }
  
  public Object getImplementor() { return this.implementor; }
  
  public void publish(String paramString) {
    URL uRL;
    canPublish();
    try {
      uRL = new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException("Cannot create URL for this address " + paramString);
    } 
    if (!uRL.getProtocol().equals("http"))
      throw new IllegalArgumentException(uRL.getProtocol() + " protocol based address is not supported"); 
    if (!uRL.getPath().startsWith("/"))
      throw new IllegalArgumentException("Incorrect WebService address=" + paramString + ". The address's path should start with /"); 
    createEndpoint(uRL.getPath());
    ((HttpEndpoint)this.actualEndpoint).publish(paramString);
  }
  
  public void publish(Object paramObject) {
    canPublish();
    if (!HttpContext.class.isAssignableFrom(paramObject.getClass()))
      throw new IllegalArgumentException(paramObject.getClass() + " is not a supported context."); 
    createEndpoint(((HttpContext)paramObject).getPath());
    ((HttpEndpoint)this.actualEndpoint).publish(paramObject);
  }
  
  public void publish(HttpContext paramHttpContext) {
    canPublish();
    createEndpoint(paramHttpContext.getPath());
    ((HttpEndpoint)this.actualEndpoint).publish(paramHttpContext);
  }
  
  public void stop() {
    if (isPublished()) {
      ((HttpEndpoint)this.actualEndpoint).stop();
      this.actualEndpoint = null;
      this.stopped = true;
    } 
  }
  
  public boolean isPublished() { return (this.actualEndpoint != null); }
  
  public List<Source> getMetadata() { return this.metadata; }
  
  public void setMetadata(List<Source> paramList) {
    if (isPublished())
      throw new IllegalStateException("Cannot set Metadata. Endpoint is already published"); 
    this.metadata = paramList;
  }
  
  public Executor getExecutor() { return this.executor; }
  
  public void setExecutor(Executor paramExecutor) { this.executor = paramExecutor; }
  
  public Map<String, Object> getProperties() { return new HashMap(this.properties); }
  
  public void setProperties(Map<String, Object> paramMap) { this.properties = new HashMap(paramMap); }
  
  private void createEndpoint(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(ENDPOINT_PUBLISH_PERMISSION); 
    try {
      Class.forName("com.sun.net.httpserver.HttpServer");
    } catch (Exception exception) {
      throw new UnsupportedOperationException("Couldn't load light weight http server", exception);
    } 
    this.container = getContainer();
    MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(this.implClass, this.binding);
    WSEndpoint wSEndpoint = WSEndpoint.create(this.implClass, true, this.invoker, (QName)getProperty(QName.class, "javax.xml.ws.wsdl.service"), (QName)getProperty(QName.class, "javax.xml.ws.wsdl.port"), this.container, this.binding, getPrimaryWsdl(metadataReader), buildDocList(), (EntityResolver)null, false);
    this.actualEndpoint = new HttpEndpoint(this.executor, getAdapter(wSEndpoint, paramString));
  }
  
  private <T> T getProperty(Class<T> paramClass, String paramString) {
    Object object = this.properties.get(paramString);
    if (object == null)
      return null; 
    if (paramClass.isInstance(object))
      return (T)paramClass.cast(object); 
    throw new IllegalArgumentException("Property " + paramString + " has to be of type " + paramClass);
  }
  
  private List<SDDocumentSource> buildDocList() {
    ArrayList arrayList = new ArrayList();
    if (this.metadata != null)
      for (Source source : this.metadata) {
        try {
          XMLStreamBufferResult xMLStreamBufferResult = (XMLStreamBufferResult)XmlUtil.identityTransform(source, new XMLStreamBufferResult());
          String str = source.getSystemId();
          arrayList.add(SDDocumentSource.create(new URL(str), xMLStreamBufferResult.getXMLStreamBuffer()));
        } catch (TransformerException transformerException) {
          throw new ServerRtException("server.rt.err", new Object[] { transformerException });
        } catch (IOException iOException) {
          throw new ServerRtException("server.rt.err", new Object[] { iOException });
        } catch (SAXException sAXException) {
          throw new ServerRtException("server.rt.err", new Object[] { sAXException });
        } catch (ParserConfigurationException parserConfigurationException) {
          throw new ServerRtException("server.rt.err", new Object[] { parserConfigurationException });
        } 
      }  
    return arrayList;
  }
  
  @Nullable
  private SDDocumentSource getPrimaryWsdl(MetadataReader paramMetadataReader) {
    EndpointFactory.verifyImplementorClass(this.implClass, paramMetadataReader);
    String str = EndpointFactory.getWsdlLocation(this.implClass, paramMetadataReader);
    if (str != null) {
      ClassLoader classLoader = this.implClass.getClassLoader();
      URL uRL = classLoader.getResource(str);
      if (uRL != null)
        return SDDocumentSource.create(uRL); 
      throw new ServerRtException("cannot.load.wsdl", new Object[] { str });
    } 
    return null;
  }
  
  private void canPublish() {
    if (isPublished())
      throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already published."); 
    if (this.stopped)
      throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already stopped."); 
  }
  
  public EndpointReference getEndpointReference(Element... paramVarArgs) { return getEndpointReference(javax.xml.ws.wsaddressing.W3CEndpointReference.class, paramVarArgs); }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs) {
    if (!isPublished())
      throw new WebServiceException("Endpoint is not published yet"); 
    return (T)((HttpEndpoint)this.actualEndpoint).getEndpointReference(paramClass, paramVarArgs);
  }
  
  public void setEndpointContext(EndpointContext paramEndpointContext) { this.endpointContext = paramEndpointContext; }
  
  private HttpAdapter getAdapter(WSEndpoint paramWSEndpoint, String paramString) {
    HttpAdapterList httpAdapterList = null;
    if (this.endpointContext != null) {
      if (this.endpointContext instanceof Component)
        httpAdapterList = (HttpAdapterList)((Component)this.endpointContext).getSPI(HttpAdapterList.class); 
      if (httpAdapterList == null)
        for (Endpoint endpoint : this.endpointContext.getEndpoints()) {
          if (endpoint.isPublished() && endpoint != this) {
            httpAdapterList = ((HttpEndpoint)((EndpointImpl)endpoint).actualEndpoint).getAdapterOwner();
            assert httpAdapterList != null;
            break;
          } 
        }  
    } 
    if (httpAdapterList == null)
      httpAdapterList = new ServerAdapterList(); 
    return httpAdapterList.createAdapter("", paramString, paramWSEndpoint);
  }
  
  private Container getContainer() {
    if (this.endpointContext != null) {
      if (this.endpointContext instanceof Component) {
        Container container1 = (Container)((Component)this.endpointContext).getSPI(Container.class);
        if (container1 != null)
          return container1; 
      } 
      for (Endpoint endpoint : this.endpointContext.getEndpoints()) {
        if (endpoint.isPublished() && endpoint != this)
          return ((EndpointImpl)endpoint).container; 
      } 
    } 
    return new ServerContainer();
  }
  
  private static class InvokerImpl extends Invoker {
    private Invoker spiInvoker;
    
    InvokerImpl(Invoker param1Invoker) { this.spiInvoker = param1Invoker; }
    
    public void start(@NotNull WSWebServiceContext param1WSWebServiceContext, @NotNull WSEndpoint param1WSEndpoint) {
      try {
        this.spiInvoker.inject(param1WSWebServiceContext);
      } catch (IllegalAccessException illegalAccessException) {
        throw new WebServiceException(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new WebServiceException(invocationTargetException);
      } 
    }
    
    public Object invoke(@NotNull Packet param1Packet, @NotNull Method param1Method, @NotNull Object... param1VarArgs) throws InvocationTargetException, IllegalAccessException { return this.spiInvoker.invoke(param1Method, param1VarArgs); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\EndpointImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */