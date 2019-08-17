package com.sun.xml.internal.ws.client;

import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import com.sun.xml.internal.ws.api.client.ServiceInterceptorFactory;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.pipe.Stubs;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.client.sei.SEIStub;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.AddressingFeature;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class WSServiceDelegate extends WSService {
  private final Map<QName, PortInfo> ports = new HashMap();
  
  @NotNull
  private HandlerConfigurator handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(null);
  
  private final Class<? extends Service> serviceClass;
  
  private final WebServiceFeatureList features;
  
  @NotNull
  private final QName serviceName;
  
  private final Map<QName, SEIPortInfo> seiContext = new HashMap();
  
  @Nullable
  private WSDLService wsdlService;
  
  private final Container container;
  
  @NotNull
  final ServiceInterceptor serviceInterceptor;
  
  private URL wsdlURL;
  
  protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
  
  protected Map<QName, PortInfo> getQNameToPortInfoMap() { return this.ports; }
  
  public WSServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs) { this(paramURL, paramQName, paramClass, new WebServiceFeatureList(paramVarArgs)); }
  
  protected WSServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass, WebServiceFeatureList paramWebServiceFeatureList) {
    this((paramURL == null) ? null : new StreamSource(paramURL.toExternalForm()), paramQName, paramClass, paramWebServiceFeatureList);
    this.wsdlURL = paramURL;
  }
  
  public WSServiceDelegate(@Nullable Source paramSource, @NotNull QName paramQName, @NotNull Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs) { this(paramSource, paramQName, paramClass, new WebServiceFeatureList(paramVarArgs)); }
  
  protected WSServiceDelegate(@Nullable Source paramSource, @NotNull QName paramQName, @NotNull Class<? extends Service> paramClass, WebServiceFeatureList paramWebServiceFeatureList) { this(paramSource, null, paramQName, paramClass, paramWebServiceFeatureList); }
  
  public WSServiceDelegate(@Nullable Source paramSource, @Nullable WSDLService paramWSDLService, @NotNull QName paramQName, @NotNull Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs) { this(paramSource, paramWSDLService, paramQName, paramClass, new WebServiceFeatureList(paramVarArgs)); }
  
  public WSServiceDelegate(@Nullable Source paramSource, @Nullable WSDLService paramWSDLService, @NotNull QName paramQName, @NotNull final Class<? extends Service> serviceClass, WebServiceFeatureList paramWebServiceFeatureList) {
    if (paramQName == null)
      throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME_NULL(null)); 
    this.features = paramWebServiceFeatureList;
    WSService.InitParams initParams = (WSService.InitParams)INIT_PARAMS.get();
    INIT_PARAMS.set(null);
    if (initParams == null)
      initParams = EMPTY_PARAMS; 
    this.serviceName = paramQName;
    this.serviceClass = paramClass;
    Container container1 = (initParams.getContainer() != null) ? initParams.getContainer() : ContainerResolver.getInstance().getContainer();
    if (container1 == Container.NONE)
      container1 = new ClientContainer(); 
    this.container = container1;
    ComponentFeature componentFeature = (ComponentFeature)this.features.get(ComponentFeature.class);
    if (componentFeature != null)
      switch (componentFeature.getTarget()) {
        case SERVICE:
          getComponents().add(componentFeature.getComponent());
          break;
        case CONTAINER:
          this.container.getComponents().add(componentFeature.getComponent());
          break;
        default:
          throw new IllegalArgumentException();
      }  
    ComponentsFeature componentsFeature = (ComponentsFeature)this.features.get(ComponentsFeature.class);
    if (componentsFeature != null)
      for (ComponentFeature componentFeature1 : componentsFeature.getComponentFeatures()) {
        switch (componentFeature1.getTarget()) {
          case SERVICE:
            getComponents().add(componentFeature1.getComponent());
            continue;
          case CONTAINER:
            this.container.getComponents().add(componentFeature1.getComponent());
            continue;
        } 
        throw new IllegalArgumentException();
      }  
    ServiceInterceptor serviceInterceptor1 = ServiceInterceptorFactory.load(this, Thread.currentThread().getContextClassLoader());
    ServiceInterceptor serviceInterceptor2 = (ServiceInterceptor)this.container.getSPI(ServiceInterceptor.class);
    if (serviceInterceptor2 != null)
      serviceInterceptor1 = ServiceInterceptor.aggregate(new ServiceInterceptor[] { serviceInterceptor1, serviceInterceptor2 }); 
    this.serviceInterceptor = serviceInterceptor1;
    if (paramWSDLService == null) {
      if (paramSource == null && paramClass != Service.class) {
        WebServiceClient webServiceClient = (WebServiceClient)AccessController.doPrivileged(new PrivilegedAction<WebServiceClient>() {
              public WebServiceClient run() { return (WebServiceClient)serviceClass.getAnnotation(WebServiceClient.class); }
            });
        String str = webServiceClient.wsdlLocation();
        str = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(str));
        paramSource = new StreamSource(str);
      } 
      if (paramSource != null)
        try {
          URL uRL = (paramSource.getSystemId() == null) ? null : JAXWSUtils.getEncodedURL(paramSource.getSystemId());
          WSDLModel wSDLModel = parseWSDL(uRL, paramSource, paramClass);
          paramWSDLService = wSDLModel.getService(this.serviceName);
          if (paramWSDLService == null)
            throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, buildNameList(wSDLModel.getServices().keySet()))); 
          for (WSDLPort wSDLPort : paramWSDLService.getPorts())
            this.ports.put(wSDLPort.getName(), new PortInfo(this, wSDLPort)); 
        } catch (MalformedURLException malformedURLException) {
          throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(paramSource.getSystemId()));
        }  
    } else {
      for (WSDLPort wSDLPort : paramWSDLService.getPorts())
        this.ports.put(wSDLPort.getName(), new PortInfo(this, wSDLPort)); 
    } 
    this.wsdlService = paramWSDLService;
    if (paramClass != Service.class) {
      HandlerChain handlerChain = (HandlerChain)AccessController.doPrivileged(new PrivilegedAction<HandlerChain>() {
            public HandlerChain run() { return (HandlerChain)serviceClass.getAnnotation(HandlerChain.class); }
          });
      if (handlerChain != null)
        this.handlerConfigurator = new HandlerConfigurator.AnnotationConfigurator(this); 
    } 
  }
  
  private WSDLModel parseWSDL(URL paramURL, Source paramSource, Class paramClass) {
    try {
      return RuntimeWSDLParser.parse(paramURL, paramSource, createCatalogResolver(), true, getContainer(), paramClass, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } catch (SAXException sAXException) {
      throw new WebServiceException(sAXException);
    } catch (ServiceConfigurationError serviceConfigurationError) {
      throw new WebServiceException(serviceConfigurationError);
    } 
  }
  
  protected EntityResolver createCatalogResolver() { return XmlUtil.createDefaultCatalogResolver(); }
  
  public Executor getExecutor() { return this.executor; }
  
  public void setExecutor(Executor paramExecutor) { this.executor = paramExecutor; }
  
  public HandlerResolver getHandlerResolver() { return this.handlerConfigurator.getResolver(); }
  
  final HandlerConfigurator getHandlerConfigurator() { return this.handlerConfigurator; }
  
  public void setHandlerResolver(HandlerResolver paramHandlerResolver) { this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(paramHandlerResolver); }
  
  public <T> T getPort(QName paramQName, Class<T> paramClass) throws WebServiceException { return (T)getPort(paramQName, paramClass, EMPTY_FEATURES); }
  
  public <T> T getPort(QName paramQName, Class<T> paramClass, WebServiceFeature... paramVarArgs) {
    if (paramQName == null || paramClass == null)
      throw new IllegalArgumentException(); 
    WSDLService wSDLService = this.wsdlService;
    if (wSDLService == null) {
      wSDLService = getWSDLModelfromSEI(paramClass);
      if (wSDLService == null)
        throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(paramClass.getName())); 
    } 
    WSDLPort wSDLPort = getPortModel(wSDLService, paramQName);
    return (T)getPort(wSDLPort.getEPR(), paramQName, paramClass, new WebServiceFeatureList(paramVarArgs));
  }
  
  public <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs) { return (T)getPort(WSEndpointReference.create(paramEndpointReference), paramClass, paramVarArgs); }
  
  public <T> T getPort(WSEndpointReference paramWSEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs) {
    WebServiceFeatureList webServiceFeatureList = new WebServiceFeatureList(paramVarArgs);
    QName qName1 = RuntimeModeler.getPortTypeName(paramClass, getMetadadaReader(webServiceFeatureList, paramClass.getClassLoader()));
    QName qName2 = getPortNameFromEPR(paramWSEndpointReference, qName1);
    return (T)getPort(paramWSEndpointReference, qName2, paramClass, webServiceFeatureList);
  }
  
  protected <T> T getPort(WSEndpointReference paramWSEndpointReference, QName paramQName, Class<T> paramClass, WebServiceFeatureList paramWebServiceFeatureList) {
    ComponentFeature componentFeature = (ComponentFeature)paramWebServiceFeatureList.get(ComponentFeature.class);
    if (componentFeature != null && !ComponentFeature.Target.STUB.equals(componentFeature.getTarget()))
      throw new IllegalArgumentException(); 
    ComponentsFeature componentsFeature = (ComponentsFeature)paramWebServiceFeatureList.get(ComponentsFeature.class);
    if (componentsFeature != null)
      for (ComponentFeature componentFeature1 : componentsFeature.getComponentFeatures()) {
        if (!ComponentFeature.Target.STUB.equals(componentFeature1.getTarget()))
          throw new IllegalArgumentException(); 
      }  
    paramWebServiceFeatureList.addAll(this.features);
    SEIPortInfo sEIPortInfo = addSEI(paramQName, paramClass, paramWebServiceFeatureList);
    return (T)createEndpointIFBaseProxy(paramWSEndpointReference, paramQName, paramClass, paramWebServiceFeatureList, sEIPortInfo);
  }
  
  public <T> T getPort(Class<T> paramClass, WebServiceFeature... paramVarArgs) {
    QName qName1 = RuntimeModeler.getPortTypeName(paramClass, getMetadadaReader(new WebServiceFeatureList(paramVarArgs), paramClass.getClassLoader()));
    WSDLService wSDLService = this.wsdlService;
    if (wSDLService == null) {
      wSDLService = getWSDLModelfromSEI(paramClass);
      if (wSDLService == null)
        throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(paramClass.getName())); 
    } 
    WSDLPort wSDLPort = wSDLService.getMatchingPort(qName1);
    if (wSDLPort == null)
      throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(qName1)); 
    QName qName2 = wSDLPort.getName();
    return (T)getPort(qName2, paramClass, paramVarArgs);
  }
  
  public <T> T getPort(Class<T> paramClass) throws WebServiceException { return (T)getPort(paramClass, EMPTY_FEATURES); }
  
  public void addPort(QName paramQName, String paramString1, String paramString2) throws WebServiceException {
    if (!this.ports.containsKey(paramQName)) {
      BindingID.SOAPHTTPImpl sOAPHTTPImpl = (paramString1 == null) ? BindingID.SOAP11_HTTP : BindingID.parse(paramString1);
      this.ports.put(paramQName, new PortInfo(this, (paramString2 == null) ? null : EndpointAddress.create(paramString2), paramQName, sOAPHTTPImpl));
    } else {
      throw new WebServiceException(DispatchMessages.DUPLICATE_PORT(paramQName.toString()));
    } 
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode) throws WebServiceException { return createDispatch(paramQName, paramClass, paramMode, EMPTY_FEATURES); }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs) { return createDispatch(paramQName, paramWSEndpointReference, paramClass, paramMode, new WebServiceFeatureList(paramVarArgs)); }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList) {
    PortInfo portInfo = safeGetPort(paramQName);
    ComponentFeature componentFeature = (ComponentFeature)paramWebServiceFeatureList.get(ComponentFeature.class);
    if (componentFeature != null && !ComponentFeature.Target.STUB.equals(componentFeature.getTarget()))
      throw new IllegalArgumentException(); 
    ComponentsFeature componentsFeature = (ComponentsFeature)paramWebServiceFeatureList.get(ComponentsFeature.class);
    if (componentsFeature != null)
      for (ComponentFeature componentFeature1 : componentsFeature.getComponentFeatures()) {
        if (!ComponentFeature.Target.STUB.equals(componentFeature1.getTarget()))
          throw new IllegalArgumentException(); 
      }  
    paramWebServiceFeatureList.addAll(this.features);
    BindingImpl bindingImpl = portInfo.createBinding(paramWebServiceFeatureList, null, null);
    bindingImpl.setMode(paramMode);
    Dispatch dispatch = Stubs.createDispatch(portInfo, this, bindingImpl, paramClass, paramMode, paramWSEndpointReference);
    this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
    return dispatch;
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs) { return createDispatch(paramQName, paramClass, paramMode, new WebServiceFeatureList(paramVarArgs)); }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList) {
    WSEndpointReference wSEndpointReference = null;
    boolean bool = false;
    AddressingFeature addressingFeature = (AddressingFeature)paramWebServiceFeatureList.get(AddressingFeature.class);
    if (addressingFeature == null)
      addressingFeature = (AddressingFeature)this.features.get(AddressingFeature.class); 
    if (addressingFeature != null && addressingFeature.isEnabled())
      bool = true; 
    MemberSubmissionAddressingFeature memberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)paramWebServiceFeatureList.get(MemberSubmissionAddressingFeature.class);
    if (memberSubmissionAddressingFeature == null)
      memberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)this.features.get(MemberSubmissionAddressingFeature.class); 
    if (memberSubmissionAddressingFeature != null && memberSubmissionAddressingFeature.isEnabled())
      bool = true; 
    if (bool && this.wsdlService != null && this.wsdlService.get(paramQName) != null)
      wSEndpointReference = this.wsdlService.get(paramQName).getEPR(); 
    return createDispatch(paramQName, wSEndpointReference, paramClass, paramMode, paramWebServiceFeatureList);
  }
  
  public <T> Dispatch<T> createDispatch(EndpointReference paramEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs) {
    WSEndpointReference wSEndpointReference = new WSEndpointReference(paramEndpointReference);
    QName qName = addPortEpr(wSEndpointReference);
    return createDispatch(qName, wSEndpointReference, paramClass, paramMode, paramVarArgs);
  }
  
  @NotNull
  public PortInfo safeGetPort(QName paramQName) {
    PortInfo portInfo = (PortInfo)this.ports.get(paramQName);
    if (portInfo == null)
      throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(paramQName, buildNameList(this.ports.keySet()))); 
    return portInfo;
  }
  
  private StringBuilder buildNameList(Collection<QName> paramCollection) {
    StringBuilder stringBuilder = new StringBuilder();
    for (QName qName : paramCollection) {
      if (stringBuilder.length() > 0)
        stringBuilder.append(','); 
      stringBuilder.append(qName);
    } 
    return stringBuilder;
  }
  
  public EndpointAddress getEndpointAddress(QName paramQName) {
    PortInfo portInfo = (PortInfo)this.ports.get(paramQName);
    return (portInfo != null) ? portInfo.targetEndpoint : null;
  }
  
  public Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode) throws WebServiceException { return createDispatch(paramQName, paramJAXBContext, paramMode, EMPTY_FEATURES); }
  
  public Dispatch<Object> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs) { return createDispatch(paramQName, paramWSEndpointReference, paramJAXBContext, paramMode, new WebServiceFeatureList(paramVarArgs)); }
  
  protected Dispatch<Object> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList) {
    PortInfo portInfo = safeGetPort(paramQName);
    ComponentFeature componentFeature = (ComponentFeature)paramWebServiceFeatureList.get(ComponentFeature.class);
    if (componentFeature != null && !ComponentFeature.Target.STUB.equals(componentFeature.getTarget()))
      throw new IllegalArgumentException(); 
    ComponentsFeature componentsFeature = (ComponentsFeature)paramWebServiceFeatureList.get(ComponentsFeature.class);
    if (componentsFeature != null)
      for (ComponentFeature componentFeature1 : componentsFeature.getComponentFeatures()) {
        if (!ComponentFeature.Target.STUB.equals(componentFeature1.getTarget()))
          throw new IllegalArgumentException(); 
      }  
    paramWebServiceFeatureList.addAll(this.features);
    BindingImpl bindingImpl = portInfo.createBinding(paramWebServiceFeatureList, null, null);
    bindingImpl.setMode(paramMode);
    Dispatch dispatch = Stubs.createJAXBDispatch(portInfo, bindingImpl, paramJAXBContext, paramMode, paramWSEndpointReference);
    this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
    return dispatch;
  }
  
  @NotNull
  public Container getContainer() { return this.container; }
  
  public Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs) { return createDispatch(paramQName, paramJAXBContext, paramMode, new WebServiceFeatureList(paramVarArgs)); }
  
  protected Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList) {
    WSEndpointReference wSEndpointReference = null;
    boolean bool = false;
    AddressingFeature addressingFeature = (AddressingFeature)paramWebServiceFeatureList.get(AddressingFeature.class);
    if (addressingFeature == null)
      addressingFeature = (AddressingFeature)this.features.get(AddressingFeature.class); 
    if (addressingFeature != null && addressingFeature.isEnabled())
      bool = true; 
    MemberSubmissionAddressingFeature memberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)paramWebServiceFeatureList.get(MemberSubmissionAddressingFeature.class);
    if (memberSubmissionAddressingFeature == null)
      memberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)this.features.get(MemberSubmissionAddressingFeature.class); 
    if (memberSubmissionAddressingFeature != null && memberSubmissionAddressingFeature.isEnabled())
      bool = true; 
    if (bool && this.wsdlService != null && this.wsdlService.get(paramQName) != null)
      wSEndpointReference = this.wsdlService.get(paramQName).getEPR(); 
    return createDispatch(paramQName, wSEndpointReference, paramJAXBContext, paramMode, paramWebServiceFeatureList);
  }
  
  public Dispatch<Object> createDispatch(EndpointReference paramEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs) {
    WSEndpointReference wSEndpointReference = new WSEndpointReference(paramEndpointReference);
    QName qName = addPortEpr(wSEndpointReference);
    return createDispatch(qName, wSEndpointReference, paramJAXBContext, paramMode, paramVarArgs);
  }
  
  private QName addPortEpr(WSEndpointReference paramWSEndpointReference) {
    if (paramWSEndpointReference == null)
      throw new WebServiceException(ProviderApiMessages.NULL_EPR()); 
    QName qName = getPortNameFromEPR(paramWSEndpointReference, null);
    PortInfo portInfo = new PortInfo(this, (paramWSEndpointReference.getAddress() == null) ? null : EndpointAddress.create(paramWSEndpointReference.getAddress()), qName, getPortModel(this.wsdlService, qName).getBinding().getBindingId());
    if (!this.ports.containsKey(qName))
      this.ports.put(qName, portInfo); 
    return qName;
  }
  
  private QName getPortNameFromEPR(@NotNull WSEndpointReference paramWSEndpointReference, @Nullable QName paramQName) {
    WSEndpointReference.Metadata metadata = paramWSEndpointReference.getMetaData();
    QName qName2 = metadata.getServiceName();
    QName qName3 = metadata.getPortName();
    if (qName2 != null && !qName2.equals(this.serviceName))
      throw new WebServiceException("EndpointReference WSDL ServiceName differs from Service Instance WSDL Service QName.\n The two Service QNames must match"); 
    if (this.wsdlService == null) {
      Source source = metadata.getWsdlSource();
      if (source == null)
        throw new WebServiceException(ProviderApiMessages.NULL_WSDL()); 
      try {
        WSDLModel wSDLModel = parseWSDL(new URL(paramWSEndpointReference.getAddress()), source, null);
        this.wsdlService = wSDLModel.getService(this.serviceName);
        if (this.wsdlService == null)
          throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, buildNameList(wSDLModel.getServices().keySet()))); 
      } catch (MalformedURLException malformedURLException) {
        throw new WebServiceException(ClientMessages.INVALID_ADDRESS(paramWSEndpointReference.getAddress()));
      } 
    } 
    QName qName1 = qName3;
    if (qName1 == null && paramQName != null) {
      WSDLPort wSDLPort = this.wsdlService.getMatchingPort(paramQName);
      if (wSDLPort == null)
        throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(paramQName)); 
      qName1 = wSDLPort.getName();
    } 
    if (qName1 == null)
      throw new WebServiceException(ProviderApiMessages.NULL_PORTNAME()); 
    if (this.wsdlService.get(qName1) == null)
      throw new WebServiceException(ClientMessages.INVALID_EPR_PORT_NAME(qName1, buildWsdlPortNames())); 
    return qName1;
  }
  
  private <T> T createProxy(final Class<T> portInterface, final InvocationHandler pis) {
    final ClassLoader loader = getDelegatingLoader(paramClass.getClassLoader(), WSServiceDelegate.class.getClassLoader());
    RuntimePermission runtimePermission = new RuntimePermission("accessClassInPackage.com.sun.xml.internal.*");
    PermissionCollection permissionCollection = runtimePermission.newPermissionCollection();
    permissionCollection.add(runtimePermission);
    return (T)AccessController.doPrivileged(new PrivilegedAction<T>() {
          public T run() {
            Object object = Proxy.newProxyInstance(loader, new Class[] { portInterface, WSBindingProvider.class, com.sun.xml.internal.ws.Closeable.class }, pis);
            return (T)portInterface.cast(object);
          }
        }new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissionCollection) }));
  }
  
  private WSDLService getWSDLModelfromSEI(final Class sei) {
    WebService webService = (WebService)AccessController.doPrivileged(new PrivilegedAction<WebService>() {
          public WebService run() { return (WebService)sei.getAnnotation(WebService.class); }
        });
    if (webService == null || webService.wsdlLocation().equals(""))
      return null; 
    String str = webService.wsdlLocation();
    str = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(str));
    StreamSource streamSource = new StreamSource(str);
    WSDLService wSDLService = null;
    try {
      URL uRL = (streamSource.getSystemId() == null) ? null : new URL(streamSource.getSystemId());
      WSDLModel wSDLModel = parseWSDL(uRL, streamSource, paramClass);
      wSDLService = wSDLModel.getService(this.serviceName);
      if (wSDLService == null)
        throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, buildNameList(wSDLModel.getServices().keySet()))); 
    } catch (MalformedURLException malformedURLException) {
      throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(streamSource.getSystemId()));
    } 
    return wSDLService;
  }
  
  public QName getServiceName() { return this.serviceName; }
  
  public Class getServiceClass() { return this.serviceClass; }
  
  public Iterator<QName> getPorts() throws WebServiceException { return this.ports.keySet().iterator(); }
  
  public URL getWSDLDocumentLocation() {
    if (this.wsdlService == null)
      return null; 
    try {
      return new URL(this.wsdlService.getParent().getLocation().getSystemId());
    } catch (MalformedURLException malformedURLException) {
      throw new AssertionError(malformedURLException);
    } 
  }
  
  private <T> T createEndpointIFBaseProxy(@Nullable WSEndpointReference paramWSEndpointReference, QName paramQName, Class<T> paramClass, WebServiceFeatureList paramWebServiceFeatureList, SEIPortInfo paramSEIPortInfo) {
    if (this.wsdlService == null)
      throw new WebServiceException(ClientMessages.INVALID_SERVICE_NO_WSDL(this.serviceName)); 
    if (this.wsdlService.get(paramQName) == null)
      throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(paramQName, buildWsdlPortNames())); 
    BindingImpl bindingImpl = paramSEIPortInfo.createBinding(paramWebServiceFeatureList, paramClass);
    InvocationHandler invocationHandler = getStubHandler(bindingImpl, paramSEIPortInfo, paramWSEndpointReference);
    Object object = createProxy(paramClass, invocationHandler);
    if (this.serviceInterceptor != null)
      this.serviceInterceptor.postCreateProxy((WSBindingProvider)object, paramClass); 
    return (T)object;
  }
  
  protected InvocationHandler getStubHandler(BindingImpl paramBindingImpl, SEIPortInfo paramSEIPortInfo, @Nullable WSEndpointReference paramWSEndpointReference) { return new SEIStub(paramSEIPortInfo, paramBindingImpl, paramSEIPortInfo.model, paramWSEndpointReference); }
  
  private StringBuilder buildWsdlPortNames() {
    HashSet hashSet = new HashSet();
    for (WSDLPort wSDLPort : this.wsdlService.getPorts())
      hashSet.add(wSDLPort.getName()); 
    return buildNameList(hashSet);
  }
  
  @NotNull
  public WSDLPort getPortModel(WSDLService paramWSDLService, QName paramQName) {
    WSDLPort wSDLPort = paramWSDLService.get(paramQName);
    if (wSDLPort == null)
      throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(paramQName, buildWsdlPortNames())); 
    return wSDLPort;
  }
  
  private SEIPortInfo addSEI(QName paramQName, Class paramClass, WebServiceFeatureList paramWebServiceFeatureList) throws WebServiceException {
    boolean bool = useOwnSEIModel(paramWebServiceFeatureList);
    if (bool)
      return createSEIPortInfo(paramQName, paramClass, paramWebServiceFeatureList); 
    SEIPortInfo sEIPortInfo = (SEIPortInfo)this.seiContext.get(paramQName);
    if (sEIPortInfo == null) {
      sEIPortInfo = createSEIPortInfo(paramQName, paramClass, paramWebServiceFeatureList);
      this.seiContext.put(sEIPortInfo.portName, sEIPortInfo);
      this.ports.put(sEIPortInfo.portName, sEIPortInfo);
    } 
    return sEIPortInfo;
  }
  
  public SEIModel buildRuntimeModel(QName paramQName1, QName paramQName2, Class paramClass, WSDLPort paramWSDLPort, WebServiceFeatureList paramWebServiceFeatureList) {
    DatabindingFactory databindingFactory = DatabindingFactory.newInstance();
    DatabindingConfig databindingConfig = new DatabindingConfig();
    databindingConfig.setContractClass(paramClass);
    databindingConfig.getMappingInfo().setServiceName(paramQName1);
    databindingConfig.setWsdlPort(paramWSDLPort);
    databindingConfig.setFeatures(paramWebServiceFeatureList);
    databindingConfig.setClassLoader(paramClass.getClassLoader());
    databindingConfig.getMappingInfo().setPortName(paramQName2);
    databindingConfig.setWsdlURL(this.wsdlURL);
    databindingConfig.setMetadataReader(getMetadadaReader(paramWebServiceFeatureList, paramClass.getClassLoader()));
    DatabindingImpl databindingImpl = (DatabindingImpl)databindingFactory.createRuntime(databindingConfig);
    return databindingImpl.getModel();
  }
  
  private MetadataReader getMetadadaReader(WebServiceFeatureList paramWebServiceFeatureList, ClassLoader paramClassLoader) {
    if (paramWebServiceFeatureList == null)
      return null; 
    ExternalMetadataFeature externalMetadataFeature = (ExternalMetadataFeature)paramWebServiceFeatureList.get(ExternalMetadataFeature.class);
    return (externalMetadataFeature != null) ? externalMetadataFeature.getMetadataReader(paramClassLoader, false) : null;
  }
  
  private SEIPortInfo createSEIPortInfo(QName paramQName, Class paramClass, WebServiceFeatureList paramWebServiceFeatureList) throws WebServiceException {
    WSDLPort wSDLPort = getPortModel(this.wsdlService, paramQName);
    SEIModel sEIModel = buildRuntimeModel(this.serviceName, paramQName, paramClass, wSDLPort, paramWebServiceFeatureList);
    return new SEIPortInfo(this, paramClass, (SOAPSEIModel)sEIModel, wSDLPort);
  }
  
  private boolean useOwnSEIModel(WebServiceFeatureList paramWebServiceFeatureList) { return paramWebServiceFeatureList.contains(com.sun.xml.internal.ws.developer.UsesJAXBContextFeature.class); }
  
  public WSDLService getWsdlService() { return this.wsdlService; }
  
  private static ClassLoader getDelegatingLoader(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) { return (paramClassLoader1 == null) ? paramClassLoader2 : ((paramClassLoader2 == null) ? paramClassLoader1 : new DelegatingLoader(paramClassLoader1, paramClassLoader2)); }
  
  static class DaemonThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable param1Runnable) {
      Thread thread = new Thread(param1Runnable);
      thread.setDaemon(Boolean.TRUE.booleanValue());
      return thread;
    }
  }
  
  private static final class DelegatingLoader extends ClassLoader {
    private final ClassLoader loader;
    
    public int hashCode() {
      byte b = 31;
      null = 1;
      null = 31 * null + ((this.loader == null) ? 0 : this.loader.hashCode());
      return 31 * null + ((getParent() == null) ? 0 : getParent().hashCode());
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      DelegatingLoader delegatingLoader = (DelegatingLoader)param1Object;
      if (this.loader == null) {
        if (delegatingLoader.loader != null)
          return false; 
      } else if (!this.loader.equals(delegatingLoader.loader)) {
        return false;
      } 
      if (getParent() == null) {
        if (delegatingLoader.getParent() != null)
          return false; 
      } else if (!getParent().equals(delegatingLoader.getParent())) {
        return false;
      } 
      return true;
    }
    
    DelegatingLoader(ClassLoader param1ClassLoader1, ClassLoader param1ClassLoader2) {
      super(param1ClassLoader2);
      this.loader = param1ClassLoader1;
    }
    
    protected Class findClass(String param1String) throws ClassNotFoundException { return this.loader.loadClass(param1String); }
    
    protected URL findResource(String param1String) { return this.loader.getResource(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\WSServiceDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */