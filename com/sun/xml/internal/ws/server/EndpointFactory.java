package com.sun.xml.internal.ws.server;

import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EndpointFactory {
  private static final EndpointFactory instance = new EndpointFactory();
  
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");
  
  public static EndpointFactory getInstance() { return instance; }
  
  public static <T> WSEndpoint<T> createEndpoint(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2) { return createEndpoint(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, true); }
  
  public static <T> WSEndpoint<T> createEndpoint(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2, boolean paramBoolean3) {
    EndpointFactory endpointFactory = (paramContainer != null) ? (EndpointFactory)paramContainer.getSPI(EndpointFactory.class) : null;
    if (endpointFactory == null)
      endpointFactory = getInstance(); 
    return endpointFactory.create(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, paramBoolean3);
  }
  
  public <T> WSEndpoint<T> create(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2) { return create(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, true); }
  
  public <T> WSEndpoint<T> create(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2, boolean paramBoolean3) {
    EndpointAwareTube endpointAwareTube;
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    MetadataReader metadataReader = getExternalMetadatReader(paramClass, paramWSBinding);
    if (paramBoolean3)
      verifyImplementorClass(paramClass, metadataReader); 
    if (paramInvoker == null)
      paramInvoker = InstanceResolver.createDefault(paramClass).createInvoker(); 
    ArrayList arrayList = new ArrayList();
    if (paramCollection != null)
      arrayList.addAll(paramCollection); 
    if (paramSDDocumentSource != null && !arrayList.contains(paramSDDocumentSource))
      arrayList.add(paramSDDocumentSource); 
    if (paramContainer == null)
      paramContainer = ContainerResolver.getInstance().getContainer(); 
    if (paramQName1 == null)
      paramQName1 = getDefaultServiceName(paramClass, metadataReader); 
    if (paramQName2 == null)
      paramQName2 = getDefaultPortName(paramQName1, paramClass, metadataReader); 
    QName qName = paramQName1.getNamespaceURI();
    String str = paramQName2.getNamespaceURI();
    if (!qName.equals(str))
      throw new ServerRtException("wrong.tns.for.port", new Object[] { str, qName }); 
    if (paramWSBinding == null)
      paramWSBinding = BindingImpl.create(BindingID.parse(paramClass)); 
    if (paramBoolean3 && paramSDDocumentSource != null)
      verifyPrimaryWSDL(paramSDDocumentSource, paramQName1); 
    qName = null;
    if (paramBoolean3 && paramClass.getAnnotation(WebServiceProvider.class) == null)
      qName = RuntimeModeler.getPortTypeName(paramClass, metadataReader); 
    List list = categoriseMetadata(arrayList, paramQName1, qName);
    SDDocumentImpl sDDocumentImpl = (paramSDDocumentSource != null) ? SDDocumentImpl.create(paramSDDocumentSource, paramQName1, qName) : findPrimary(list);
    WSDLPort wSDLPort = null;
    AbstractSEIModelImpl abstractSEIModelImpl = null;
    if (sDDocumentImpl != null)
      wSDLPort = getWSDLPort(sDDocumentImpl, list, paramQName1, paramQName2, paramContainer, paramEntityResolver); 
    WebServiceFeatureList webServiceFeatureList = ((BindingImpl)paramWSBinding).getFeatures();
    if (paramBoolean3)
      webServiceFeatureList.parseAnnotations(paramClass); 
    PolicyMap policyMap = null;
    if (isUseProviderTube(paramClass, paramBoolean3)) {
      Collection collection;
      if (wSDLPort != null) {
        policyMap = wSDLPort.getOwner().getParent().getPolicyMap();
        collection = wSDLPort.getFeatures();
      } else {
        policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(null, paramContainer, paramClass, false, new com.sun.xml.internal.ws.policy.PolicyMapMutator[0]));
        collection = PolicyUtil.getPortScopedFeatures(policyMap, paramQName1, paramQName2);
      } 
      webServiceFeatureList.mergeFeatures(collection, true);
      endpointAwareTube = createProviderInvokerTube(paramClass, paramWSBinding, paramInvoker, paramContainer);
    } else {
      abstractSEIModelImpl = createSEIModel(wSDLPort, paramClass, paramQName1, paramQName2, paramWSBinding, sDDocumentImpl);
      if (paramWSBinding instanceof SOAPBindingImpl)
        ((SOAPBindingImpl)paramWSBinding).setPortKnownHeaders(((SOAPSEIModel)abstractSEIModelImpl).getKnownHeaders()); 
      if (sDDocumentImpl == null) {
        sDDocumentImpl = generateWSDL(paramWSBinding, abstractSEIModelImpl, list, paramContainer, paramClass);
        wSDLPort = getWSDLPort(sDDocumentImpl, list, paramQName1, paramQName2, paramContainer, paramEntityResolver);
        abstractSEIModelImpl.freeze(wSDLPort);
      } 
      policyMap = wSDLPort.getOwner().getParent().getPolicyMap();
      webServiceFeatureList.mergeFeatures(wSDLPort.getFeatures(), true);
      endpointAwareTube = createSEIInvokerTube(abstractSEIModelImpl, paramInvoker, paramWSBinding);
    } 
    if (paramBoolean1)
      processHandlerAnnotation(paramWSBinding, paramClass, paramQName1, paramQName2); 
    if (sDDocumentImpl != null)
      list = findMetadataClosure(sDDocumentImpl, list, paramEntityResolver); 
    ServiceDefinitionImpl serviceDefinitionImpl = (sDDocumentImpl != null) ? new ServiceDefinitionImpl(list, sDDocumentImpl) : null;
    return create(paramQName1, paramQName2, paramWSBinding, paramContainer, abstractSEIModelImpl, wSDLPort, paramClass, serviceDefinitionImpl, endpointAwareTube, paramBoolean2, policyMap);
  }
  
  protected <T> WSEndpoint<T> create(QName paramQName1, QName paramQName2, WSBinding paramWSBinding, Container paramContainer, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Class<T> paramClass, ServiceDefinitionImpl paramServiceDefinitionImpl, EndpointAwareTube paramEndpointAwareTube, boolean paramBoolean, PolicyMap paramPolicyMap) { return new WSEndpointImpl(paramQName1, paramQName2, paramWSBinding, paramContainer, paramSEIModel, paramWSDLPort, paramClass, paramServiceDefinitionImpl, paramEndpointAwareTube, paramBoolean, paramPolicyMap); }
  
  protected boolean isUseProviderTube(Class<?> paramClass, boolean paramBoolean) { return (!paramBoolean || paramClass.getAnnotation(WebServiceProvider.class) != null); }
  
  protected EndpointAwareTube createSEIInvokerTube(AbstractSEIModelImpl paramAbstractSEIModelImpl, Invoker paramInvoker, WSBinding paramWSBinding) { return new SEIInvokerTube(paramAbstractSEIModelImpl, paramInvoker, paramWSBinding); }
  
  protected <T> EndpointAwareTube createProviderInvokerTube(Class<T> paramClass, WSBinding paramWSBinding, Invoker paramInvoker, Container paramContainer) { return ProviderInvokerTube.create(paramClass, paramWSBinding, paramInvoker, paramContainer); }
  
  private static List<SDDocumentImpl> findMetadataClosure(SDDocumentImpl paramSDDocumentImpl, List<SDDocumentImpl> paramList, EntityResolver paramEntityResolver) {
    HashMap hashMap1 = new HashMap();
    for (SDDocumentImpl sDDocumentImpl : paramList)
      hashMap1.put(sDDocumentImpl.getSystemId().toString(), sDDocumentImpl); 
    HashMap hashMap2 = new HashMap();
    hashMap2.put(paramSDDocumentImpl.getSystemId().toString(), paramSDDocumentImpl);
    ArrayList arrayList1 = new ArrayList();
    arrayList1.addAll(paramSDDocumentImpl.getImports());
    while (!arrayList1.isEmpty()) {
      String str = (String)arrayList1.remove(0);
      SDDocumentImpl sDDocumentImpl = (SDDocumentImpl)hashMap1.get(str);
      if (sDDocumentImpl == null && paramEntityResolver != null)
        try {
          InputSource inputSource = paramEntityResolver.resolveEntity(null, str);
          if (inputSource != null) {
            MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
            XMLStreamReader xMLStreamReader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(inputSource.getByteStream());
            mutableXMLStreamBuffer.createFromXMLStreamReader(xMLStreamReader);
            SDDocumentSource sDDocumentSource = SDDocumentImpl.create(new URL(str), mutableXMLStreamBuffer);
            sDDocumentImpl = SDDocumentImpl.create(sDDocumentSource, null, null);
          } 
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
      if (sDDocumentImpl != null && !hashMap2.containsKey(str)) {
        hashMap2.put(str, sDDocumentImpl);
        arrayList1.addAll(sDDocumentImpl.getImports());
      } 
    } 
    ArrayList arrayList2 = new ArrayList();
    arrayList2.addAll(hashMap2.values());
    return arrayList2;
  }
  
  private static <T> void processHandlerAnnotation(WSBinding paramWSBinding, Class<T> paramClass, QName paramQName1, QName paramQName2) {
    HandlerAnnotationInfo handlerAnnotationInfo = HandlerAnnotationProcessor.buildHandlerInfo(paramClass, paramQName1, paramQName2, paramWSBinding);
    if (handlerAnnotationInfo != null) {
      paramWSBinding.setHandlerChain(handlerAnnotationInfo.getHandlers());
      if (paramWSBinding instanceof SOAPBinding)
        ((SOAPBinding)paramWSBinding).setRoles(handlerAnnotationInfo.getRoles()); 
    } 
  }
  
  public static boolean verifyImplementorClass(Class<?> paramClass) { return verifyImplementorClass(paramClass, null); }
  
  public static boolean verifyImplementorClass(Class<?> paramClass, MetadataReader paramMetadataReader) {
    if (paramMetadataReader == null)
      paramMetadataReader = new ReflectAnnotationReader(); 
    WebServiceProvider webServiceProvider = (WebServiceProvider)paramMetadataReader.getAnnotation(WebServiceProvider.class, paramClass);
    WebService webService = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
    if (webServiceProvider == null && webService == null)
      throw new IllegalArgumentException(paramClass + " has neither @WebService nor @WebServiceProvider annotation"); 
    if (webServiceProvider != null && webService != null)
      throw new IllegalArgumentException(paramClass + " has both @WebService and @WebServiceProvider annotations"); 
    if (webServiceProvider != null) {
      if (javax.xml.ws.Provider.class.isAssignableFrom(paramClass) || com.sun.xml.internal.ws.api.server.AsyncProvider.class.isAssignableFrom(paramClass))
        return true; 
      throw new IllegalArgumentException(paramClass + " doesn't implement Provider or AsyncProvider interface");
    } 
    return false;
  }
  
  private static AbstractSEIModelImpl createSEIModel(WSDLPort paramWSDLPort, Class<?> paramClass, @NotNull QName paramQName1, @NotNull QName paramQName2, WSBinding paramWSBinding, SDDocumentSource paramSDDocumentSource) {
    DatabindingFactory databindingFactory = DatabindingFactory.newInstance();
    DatabindingConfig databindingConfig = new DatabindingConfig();
    databindingConfig.setEndpointClass(paramClass);
    databindingConfig.getMappingInfo().setServiceName(paramQName1);
    databindingConfig.setWsdlPort(paramWSDLPort);
    databindingConfig.setWSBinding(paramWSBinding);
    databindingConfig.setClassLoader(paramClass.getClassLoader());
    databindingConfig.getMappingInfo().setPortName(paramQName2);
    if (paramSDDocumentSource != null)
      databindingConfig.setWsdlURL(paramSDDocumentSource.getSystemId()); 
    databindingConfig.setMetadataReader(getExternalMetadatReader(paramClass, paramWSBinding));
    DatabindingImpl databindingImpl = (DatabindingImpl)databindingFactory.createRuntime(databindingConfig);
    return (AbstractSEIModelImpl)databindingImpl.getModel();
  }
  
  public static MetadataReader getExternalMetadatReader(Class<?> paramClass, WSBinding paramWSBinding) {
    ExternalMetadataFeature externalMetadataFeature = (ExternalMetadataFeature)paramWSBinding.getFeature(ExternalMetadataFeature.class);
    return (externalMetadataFeature != null) ? externalMetadataFeature.getMetadataReader(paramClass.getClassLoader(), false) : null;
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass) { return getDefaultServiceName(paramClass, null); }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass, MetadataReader paramMetadataReader) { return getDefaultServiceName(paramClass, true, paramMetadataReader); }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass, boolean paramBoolean) { return getDefaultServiceName(paramClass, paramBoolean, null); }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass, boolean paramBoolean, MetadataReader paramMetadataReader) {
    QName qName;
    if (paramMetadataReader == null)
      paramMetadataReader = new ReflectAnnotationReader(); 
    WebServiceProvider webServiceProvider = (WebServiceProvider)paramMetadataReader.getAnnotation(WebServiceProvider.class, paramClass);
    if (webServiceProvider != null) {
      String str1 = webServiceProvider.targetNamespace();
      String str2 = webServiceProvider.serviceName();
      qName = new QName(str1, str2);
    } else {
      qName = RuntimeModeler.getServiceName(paramClass, paramMetadataReader, paramBoolean);
    } 
    assert qName != null;
    return qName;
  }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass) { return getDefaultPortName(paramQName, paramClass, null); }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass, MetadataReader paramMetadataReader) { return getDefaultPortName(paramQName, paramClass, true, paramMetadataReader); }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass, boolean paramBoolean) { return getDefaultPortName(paramQName, paramClass, paramBoolean, null); }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass, boolean paramBoolean, MetadataReader paramMetadataReader) {
    QName qName;
    if (paramMetadataReader == null)
      paramMetadataReader = new ReflectAnnotationReader(); 
    WebServiceProvider webServiceProvider = (WebServiceProvider)paramMetadataReader.getAnnotation(WebServiceProvider.class, paramClass);
    if (webServiceProvider != null) {
      String str1 = webServiceProvider.targetNamespace();
      String str2 = webServiceProvider.portName();
      qName = new QName(str1, str2);
    } else {
      qName = RuntimeModeler.getPortName(paramClass, paramMetadataReader, paramQName.getNamespaceURI(), paramBoolean);
    } 
    assert qName != null;
    return qName;
  }
  
  @Nullable
  public static String getWsdlLocation(Class<?> paramClass) { return getWsdlLocation(paramClass, new ReflectAnnotationReader()); }
  
  @Nullable
  public static String getWsdlLocation(Class<?> paramClass, MetadataReader paramMetadataReader) {
    if (paramMetadataReader == null)
      paramMetadataReader = new ReflectAnnotationReader(); 
    WebService webService = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
    if (webService != null)
      return nullIfEmpty(webService.wsdlLocation()); 
    WebServiceProvider webServiceProvider = (WebServiceProvider)paramClass.getAnnotation(WebServiceProvider.class);
    assert webServiceProvider != null;
    return nullIfEmpty(webServiceProvider.wsdlLocation());
  }
  
  private static String nullIfEmpty(String paramString) {
    if (paramString.length() < 1)
      paramString = null; 
    return paramString;
  }
  
  private static SDDocumentImpl generateWSDL(WSBinding paramWSBinding, AbstractSEIModelImpl paramAbstractSEIModelImpl, List<SDDocumentImpl> paramList, Container paramContainer, Class paramClass) {
    BindingID bindingID = paramWSBinding.getBindingId();
    if (!bindingID.canGenerateWSDL())
      throw new ServerRtException("can.not.generate.wsdl", new Object[] { bindingID }); 
    if (bindingID.toString().equals("http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
      String str = ServerMessages.GENERATE_NON_STANDARD_WSDL();
      logger.warning(str);
    } 
    WSDLGenResolver wSDLGenResolver = new WSDLGenResolver(paramList, paramAbstractSEIModelImpl.getServiceQName(), paramAbstractSEIModelImpl.getPortTypeName());
    WSDLGenInfo wSDLGenInfo = new WSDLGenInfo();
    wSDLGenInfo.setWsdlResolver(wSDLGenResolver);
    wSDLGenInfo.setContainer(paramContainer);
    wSDLGenInfo.setExtensions((WSDLGeneratorExtension[])ServiceFinder.find(WSDLGeneratorExtension.class).toArray());
    wSDLGenInfo.setInlineSchemas(false);
    wSDLGenInfo.setSecureXmlProcessingDisabled(isSecureXmlProcessingDisabled(paramWSBinding.getFeatures()));
    paramAbstractSEIModelImpl.getDatabinding().generateWSDL(wSDLGenInfo);
    return wSDLGenResolver.updateDocs();
  }
  
  private static boolean isSecureXmlProcessingDisabled(WSFeatureList paramWSFeatureList) { return false; }
  
  private static List<SDDocumentImpl> categoriseMetadata(List<SDDocumentSource> paramList, QName paramQName1, QName paramQName2) {
    ArrayList arrayList = new ArrayList(paramList.size());
    for (SDDocumentSource sDDocumentSource : paramList)
      arrayList.add(SDDocumentImpl.create(sDDocumentSource, paramQName1, paramQName2)); 
    return arrayList;
  }
  
  private static void verifyPrimaryWSDL(@NotNull SDDocumentSource paramSDDocumentSource, @NotNull QName paramQName) {
    SDDocumentImpl sDDocumentImpl = SDDocumentImpl.create(paramSDDocumentSource, paramQName, null);
    if (!(sDDocumentImpl instanceof SDDocument.WSDL))
      throw new WebServiceException(paramSDDocumentSource.getSystemId() + " is not a WSDL. But it is passed as a primary WSDL"); 
    SDDocument.WSDL wSDL = (SDDocument.WSDL)sDDocumentImpl;
    if (!wSDL.hasService()) {
      if (wSDL.getAllServices().isEmpty())
        throw new WebServiceException("Not a primary WSDL=" + paramSDDocumentSource.getSystemId() + " since it doesn't have Service " + paramQName); 
      throw new WebServiceException("WSDL " + sDDocumentImpl.getSystemId() + " has the following services " + wSDL.getAllServices() + " but not " + paramQName + ". Maybe you forgot to specify a serviceName and/or targetNamespace in @WebService/@WebServiceProvider?");
    } 
  }
  
  @Nullable
  private static SDDocumentImpl findPrimary(@NotNull List<SDDocumentImpl> paramList) {
    SDDocumentImpl sDDocumentImpl = null;
    boolean bool1 = false;
    boolean bool2 = false;
    for (SDDocumentImpl sDDocumentImpl1 : paramList) {
      if (sDDocumentImpl1 instanceof SDDocument.WSDL) {
        SDDocument.WSDL wSDL = (SDDocument.WSDL)sDDocumentImpl1;
        if (wSDL.hasService()) {
          sDDocumentImpl = sDDocumentImpl1;
          if (bool1)
            throw new ServerRtException("duplicate.primary.wsdl", new Object[] { sDDocumentImpl1.getSystemId() }); 
          bool1 = true;
        } 
        if (wSDL.hasPortType()) {
          if (bool2)
            throw new ServerRtException("duplicate.abstract.wsdl", new Object[] { sDDocumentImpl1.getSystemId() }); 
          bool2 = true;
        } 
      } 
    } 
    return sDDocumentImpl;
  }
  
  @NotNull
  private static WSDLPort getWSDLPort(SDDocumentSource paramSDDocumentSource, List<? extends SDDocumentSource> paramList, @NotNull QName paramQName1, @NotNull QName paramQName2, Container paramContainer, EntityResolver paramEntityResolver) {
    URL uRL = paramSDDocumentSource.getSystemId();
    try {
      WSDLModel wSDLModel = RuntimeWSDLParser.parse(new XMLEntityResolver.Parser(paramSDDocumentSource), new EntityResolverImpl(paramList, paramEntityResolver), false, paramContainer, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
      if (wSDLModel.getServices().size() == 0)
        throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(uRL)); 
      WSDLService wSDLService = wSDLModel.getService(paramQName1);
      if (wSDLService == null)
        throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(paramQName1, uRL)); 
      WSDLPort wSDLPort = wSDLService.get(paramQName2);
      if (wSDLPort == null)
        throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(paramQName1, paramQName2, uRL)); 
      return wSDLPort;
    } catch (IOException iOException) {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { uRL, iOException });
    } catch (XMLStreamException xMLStreamException) {
      throw new ServerRtException("runtime.saxparser.exception", new Object[] { xMLStreamException.getMessage(), xMLStreamException.getLocation(), xMLStreamException });
    } catch (SAXException sAXException) {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { uRL, sAXException });
    } catch (ServiceConfigurationError serviceConfigurationError) {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { uRL, serviceConfigurationError });
    } 
  }
  
  private static final class EntityResolverImpl implements XMLEntityResolver {
    private Map<String, SDDocumentSource> metadata = new HashMap();
    
    private EntityResolver resolver;
    
    public EntityResolverImpl(List<? extends SDDocumentSource> param1List, EntityResolver param1EntityResolver) {
      for (SDDocumentSource sDDocumentSource : param1List)
        this.metadata.put(sDDocumentSource.getSystemId().toExternalForm(), sDDocumentSource); 
      this.resolver = param1EntityResolver;
    }
    
    public XMLEntityResolver.Parser resolveEntity(String param1String1, String param1String2) throws IOException, XMLStreamException {
      if (param1String2 != null) {
        SDDocumentSource sDDocumentSource = (SDDocumentSource)this.metadata.get(param1String2);
        if (sDDocumentSource != null)
          return new XMLEntityResolver.Parser(sDDocumentSource); 
      } 
      if (this.resolver != null)
        try {
          InputSource inputSource = this.resolver.resolveEntity(param1String1, param1String2);
          if (inputSource != null)
            return new XMLEntityResolver.Parser(null, XMLStreamReaderFactory.create(inputSource, true)); 
        } catch (SAXException sAXException) {
          throw new XMLStreamException(sAXException);
        }  
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\EndpointFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */