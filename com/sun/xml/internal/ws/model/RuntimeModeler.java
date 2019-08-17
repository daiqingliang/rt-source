package com.sun.xml.internal.ws.model;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;
import javax.xml.ws.soap.MTOM;

public class RuntimeModeler {
  private final WebServiceFeatureList features;
  
  private BindingID bindingId;
  
  private WSBinding wsBinding;
  
  private final Class portClass;
  
  private AbstractSEIModelImpl model;
  
  private SOAPBindingImpl defaultBinding;
  
  private String packageName;
  
  private String targetNamespace;
  
  private boolean isWrapped = true;
  
  private ClassLoader classLoader;
  
  private final WSDLPort binding;
  
  private QName serviceName;
  
  private QName portName;
  
  private Set<Class> classUsesWebMethod;
  
  private DatabindingConfig config;
  
  private MetadataReader metadataReader;
  
  public static final String PD_JAXWS_PACKAGE_PD = ".jaxws.";
  
  public static final String JAXWS_PACKAGE_PD = "jaxws.";
  
  public static final String RESPONSE = "Response";
  
  public static final String RETURN = "return";
  
  public static final String BEAN = "Bean";
  
  public static final String SERVICE = "Service";
  
  public static final String PORT = "Port";
  
  public static final Class HOLDER_CLASS = javax.xml.ws.Holder.class;
  
  public static final Class<RemoteException> REMOTE_EXCEPTION_CLASS = RemoteException.class;
  
  public static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
  
  public static final Class<Exception> EXCEPTION_CLASS = Exception.class;
  
  public static final String DecapitalizeExceptionBeanProperties = "com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties";
  
  public static final String SuppressDocLitWrapperGeneration = "com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration";
  
  public static final String DocWrappeeNamespapceQualified = "com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified";
  
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");
  
  public RuntimeModeler(@NotNull DatabindingConfig paramDatabindingConfig) {
    this.portClass = (paramDatabindingConfig.getEndpointClass() != null) ? paramDatabindingConfig.getEndpointClass() : paramDatabindingConfig.getContractClass();
    this.serviceName = paramDatabindingConfig.getMappingInfo().getServiceName();
    this.binding = paramDatabindingConfig.getWsdlPort();
    this.classLoader = paramDatabindingConfig.getClassLoader();
    this.portName = paramDatabindingConfig.getMappingInfo().getPortName();
    this.config = paramDatabindingConfig;
    this.wsBinding = paramDatabindingConfig.getWSBinding();
    this.metadataReader = paramDatabindingConfig.getMetadataReader();
    this.targetNamespace = paramDatabindingConfig.getMappingInfo().getTargetNamespace();
    if (this.metadataReader == null)
      this.metadataReader = new ReflectAnnotationReader(); 
    if (this.wsBinding != null) {
      this.bindingId = this.wsBinding.getBindingId();
      if (paramDatabindingConfig.getFeatures() != null)
        this.wsBinding.getFeatures().mergeFeatures(paramDatabindingConfig.getFeatures(), false); 
      if (this.binding != null)
        this.wsBinding.getFeatures().mergeFeatures(this.binding.getFeatures(), false); 
      this.features = WebServiceFeatureList.toList(this.wsBinding.getFeatures());
    } else {
      this.bindingId = paramDatabindingConfig.getMappingInfo().getBindingID();
      this.features = WebServiceFeatureList.toList(paramDatabindingConfig.getFeatures());
      if (this.binding != null)
        this.bindingId = this.binding.getBinding().getBindingId(); 
      if (this.bindingId == null)
        this.bindingId = getDefaultBindingID(); 
      if (!this.features.contains(javax.xml.ws.soap.MTOMFeature.class)) {
        MTOM mTOM = (MTOM)getAnnotation(this.portClass, MTOM.class);
        if (mTOM != null)
          this.features.add(WebServiceFeatureList.getFeature(mTOM)); 
      } 
      if (!this.features.contains(com.oracle.webservices.internal.api.EnvelopeStyleFeature.class)) {
        EnvelopeStyle envelopeStyle = (EnvelopeStyle)getAnnotation(this.portClass, EnvelopeStyle.class);
        if (envelopeStyle != null)
          this.features.add(WebServiceFeatureList.getFeature(envelopeStyle)); 
      } 
      this.wsBinding = this.bindingId.createBinding(this.features);
    } 
  }
  
  private BindingID getDefaultBindingID() {
    BindingType bindingType = (BindingType)getAnnotation(this.portClass, BindingType.class);
    if (bindingType != null)
      return BindingID.parse(bindingType.value()); 
    SOAPVersion sOAPVersion = WebServiceFeatureList.getSoapVersion(this.features);
    boolean bool = this.features.isEnabled(javax.xml.ws.soap.MTOMFeature.class);
    return SOAPVersion.SOAP_12.equals(sOAPVersion) ? (bool ? BindingID.SOAP12_HTTP_MTOM : BindingID.SOAP12_HTTP) : (bool ? BindingID.SOAP11_HTTP_MTOM : BindingID.SOAP11_HTTP);
  }
  
  public void setClassLoader(ClassLoader paramClassLoader) { this.classLoader = paramClassLoader; }
  
  public void setPortName(QName paramQName) { this.portName = paramQName; }
  
  private <T extends Annotation> T getAnnotation(Class<?> paramClass1, Class<T> paramClass2) { return (T)this.metadataReader.getAnnotation(paramClass2, paramClass1); }
  
  private <T extends Annotation> T getAnnotation(Method paramMethod, Class<T> paramClass) { return (T)this.metadataReader.getAnnotation(paramClass, paramMethod); }
  
  private Annotation[] getAnnotations(Method paramMethod) { return this.metadataReader.getAnnotations(paramMethod); }
  
  private Annotation[] getAnnotations(Class<?> paramClass) { return this.metadataReader.getAnnotations(paramClass); }
  
  private Annotation[][] getParamAnnotations(Method paramMethod) { return this.metadataReader.getParameterAnnotations(paramMethod); }
  
  public AbstractSEIModelImpl buildRuntimeModel() {
    this.model = new SOAPSEIModel(this.features);
    this.model.contractClass = this.config.getContractClass();
    this.model.endpointClass = this.config.getEndpointClass();
    this.model.classLoader = this.classLoader;
    this.model.wsBinding = this.wsBinding;
    this.model.databindingInfo.setWsdlURL(this.config.getWsdlURL());
    this.model.databindingInfo.properties().putAll(this.config.properties());
    if (this.model.contractClass == null)
      this.model.contractClass = this.portClass; 
    if (this.model.endpointClass == null && !this.portClass.isInterface())
      this.model.endpointClass = this.portClass; 
    Class clazz1 = this.portClass;
    this.metadataReader.getProperties(this.model.databindingInfo.properties(), this.portClass);
    WebService webService = (WebService)getAnnotation(this.portClass, WebService.class);
    if (webService == null)
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { this.portClass.getCanonicalName() }); 
    Class clazz2 = configEndpointInterface();
    if (webService.endpointInterface().length() > 0 || clazz2 != null) {
      if (clazz2 != null) {
        clazz1 = clazz2;
      } else {
        clazz1 = getClass(webService.endpointInterface(), ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(webService.endpointInterface()));
      } 
      this.model.contractClass = clazz1;
      this.model.endpointClass = this.portClass;
      WebService webService1 = (WebService)getAnnotation(clazz1, WebService.class);
      if (webService1 == null)
        throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() }); 
      SOAPBinding sOAPBinding1 = (SOAPBinding)getAnnotation(this.portClass, SOAPBinding.class);
      SOAPBinding sOAPBinding2 = (SOAPBinding)getAnnotation(clazz1, SOAPBinding.class);
      if (sOAPBinding1 != null && (sOAPBinding2 == null || sOAPBinding2.style() != sOAPBinding1.style() || sOAPBinding2.use() != sOAPBinding1.use()))
        logger.warning(ServerMessages.RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL("@SOAPBinding", this.portClass.getName(), clazz1.getName())); 
    } 
    if (this.serviceName == null)
      this.serviceName = getServiceName(this.portClass, this.metadataReader); 
    this.model.setServiceQName(this.serviceName);
    if (this.portName == null)
      this.portName = getPortName(this.portClass, this.metadataReader, this.serviceName.getNamespaceURI()); 
    this.model.setPortName(this.portName);
    DatabindingMode databindingMode = (DatabindingMode)getAnnotation(this.portClass, DatabindingMode.class);
    if (databindingMode != null)
      this.model.databindingInfo.setDatabindingMode(databindingMode.value()); 
    processClass(clazz1);
    if (this.model.getJavaMethods().size() == 0)
      throw new RuntimeModelerException("runtime.modeler.no.operations", new Object[] { this.portClass.getName() }); 
    this.model.postProcess();
    this.config.properties().put(com.sun.xml.internal.ws.spi.db.BindingContext.class.getName(), this.model.bindingContext);
    if (this.binding != null)
      this.model.freeze(this.binding); 
    return this.model;
  }
  
  private Class configEndpointInterface() { return (this.config.getEndpointClass() == null || this.config.getEndpointClass().isInterface()) ? null : this.config.getContractClass(); }
  
  private Class getClass(String paramString, Localizable paramLocalizable) {
    try {
      return (this.classLoader == null) ? Thread.currentThread().getContextClassLoader().loadClass(paramString) : this.classLoader.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeModelerException(paramLocalizable);
    } 
  }
  
  private boolean noWrapperGen() {
    Object object = this.config.properties().get("com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration");
    return (object != null && object instanceof Boolean) ? ((Boolean)object).booleanValue() : 0;
  }
  
  private Class getRequestWrapperClass(String paramString, Method paramMethod, QName paramQName) {
    ClassLoader classLoader1 = (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
    try {
      return classLoader1.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      if (noWrapperGen())
        return com.sun.xml.internal.ws.spi.db.WrapperComposite.class; 
      logger.fine("Dynamically creating request wrapper Class " + paramString);
      return WrapperBeanGenerator.createRequestWrapperBean(paramString, paramMethod, paramQName, classLoader1);
    } 
  }
  
  private Class getResponseWrapperClass(String paramString, Method paramMethod, QName paramQName) {
    ClassLoader classLoader1 = (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
    try {
      return classLoader1.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      if (noWrapperGen())
        return com.sun.xml.internal.ws.spi.db.WrapperComposite.class; 
      logger.fine("Dynamically creating response wrapper bean Class " + paramString);
      return WrapperBeanGenerator.createResponseWrapperBean(paramString, paramMethod, paramQName, classLoader1);
    } 
  }
  
  private Class getExceptionBeanClass(String paramString1, Class paramClass, String paramString2, String paramString3) {
    boolean bool = true;
    Object object = this.config.properties().get("com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties");
    if (object != null && object instanceof Boolean)
      bool = ((Boolean)object).booleanValue(); 
    ClassLoader classLoader1 = (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
    try {
      return classLoader1.loadClass(paramString1);
    } catch (ClassNotFoundException classNotFoundException) {
      logger.fine("Dynamically creating exception bean Class " + paramString1);
      return WrapperBeanGenerator.createExceptionBean(paramString1, paramClass, this.targetNamespace, paramString2, paramString3, classLoader1, bool);
    } 
  }
  
  protected void determineWebMethodUse(Class paramClass) {
    if (paramClass == null)
      return; 
    if (!paramClass.isInterface()) {
      if (paramClass == Object.class)
        return; 
      for (Method method : paramClass.getMethods()) {
        if (method.getDeclaringClass() == paramClass) {
          WebMethod webMethod = (WebMethod)getAnnotation(method, WebMethod.class);
          if (webMethod != null && !webMethod.exclude()) {
            this.classUsesWebMethod.add(paramClass);
            break;
          } 
        } 
      } 
    } 
    determineWebMethodUse(paramClass.getSuperclass());
  }
  
  void processClass(Class paramClass) {
    this.classUsesWebMethod = new HashSet();
    determineWebMethodUse(paramClass);
    WebService webService = (WebService)getAnnotation(paramClass, WebService.class);
    QName qName = getPortTypeName(paramClass, this.targetNamespace, this.metadataReader);
    this.packageName = "";
    if (paramClass.getPackage() != null)
      this.packageName = paramClass.getPackage().getName(); 
    this.targetNamespace = qName.getNamespaceURI();
    this.model.setPortTypeName(qName);
    this.model.setTargetNamespace(this.targetNamespace);
    this.model.defaultSchemaNamespaceSuffix = this.config.getMappingInfo().getDefaultSchemaNamespaceSuffix();
    this.model.setWSDLLocation(webService.wsdlLocation());
    SOAPBinding sOAPBinding = (SOAPBinding)getAnnotation(paramClass, SOAPBinding.class);
    if (sOAPBinding != null) {
      if (sOAPBinding.style() == SOAPBinding.Style.RPC && sOAPBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE)
        throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { sOAPBinding, paramClass }); 
      this.isWrapped = (sOAPBinding.parameterStyle() == SOAPBinding.ParameterStyle.WRAPPED);
    } 
    this.defaultBinding = createBinding(sOAPBinding);
    for (Method method : paramClass.getMethods()) {
      if (paramClass.isInterface() || (method.getDeclaringClass() != Object.class && (!getBooleanSystemProperty("com.sun.xml.internal.ws.legacyWebMethod").booleanValue() ? !isWebMethodBySpec(method, paramClass) : !isWebMethod(method))))
        processMethod(method); 
    } 
    XmlSeeAlso xmlSeeAlso = (XmlSeeAlso)getAnnotation(paramClass, XmlSeeAlso.class);
    if (xmlSeeAlso != null)
      this.model.addAdditionalClasses(xmlSeeAlso.value()); 
  }
  
  private boolean isWebMethodBySpec(Method paramMethod, Class paramClass) {
    int i = paramMethod.getModifiers();
    boolean bool = (Modifier.isStatic(i) || Modifier.isFinal(i)) ? 1 : 0;
    assert Modifier.isPublic(i);
    assert !paramClass.isInterface();
    WebMethod webMethod = (WebMethod)getAnnotation(paramMethod, WebMethod.class);
    if (webMethod != null) {
      if (webMethod.exclude())
        return false; 
      if (bool)
        throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(paramMethod)); 
      return true;
    } 
    if (bool)
      return false; 
    Class clazz = paramMethod.getDeclaringClass();
    return (getAnnotation(clazz, WebService.class) != null);
  }
  
  private boolean isWebMethod(Method paramMethod) {
    int i = paramMethod.getModifiers();
    if (Modifier.isStatic(i) || Modifier.isFinal(i))
      return false; 
    Class clazz = paramMethod.getDeclaringClass();
    boolean bool = (getAnnotation(clazz, WebService.class) != null) ? 1 : 0;
    WebMethod webMethod = (WebMethod)getAnnotation(paramMethod, WebMethod.class);
    return (webMethod != null && !webMethod.exclude() && bool) ? true : ((bool && !this.classUsesWebMethod.contains(clazz)));
  }
  
  protected SOAPBindingImpl createBinding(SOAPBinding paramSOAPBinding) {
    SOAPBindingImpl sOAPBindingImpl = new SOAPBindingImpl();
    SOAPBinding.Style style = (paramSOAPBinding != null) ? paramSOAPBinding.style() : SOAPBinding.Style.DOCUMENT;
    sOAPBindingImpl.setStyle(style);
    assert this.bindingId != null;
    this.model.bindingId = this.bindingId;
    SOAPVersion sOAPVersion = this.bindingId.getSOAPVersion();
    sOAPBindingImpl.setSOAPVersion(sOAPVersion);
    return sOAPBindingImpl;
  }
  
  public static String getNamespace(@NotNull String paramString) {
    String[] arrayOfString;
    if (paramString.length() == 0)
      return null; 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ".");
    if (stringTokenizer.countTokens() == 0) {
      arrayOfString = new String[0];
    } else {
      arrayOfString = new String[stringTokenizer.countTokens()];
      for (int i = stringTokenizer.countTokens() - 1; i >= 0; i--)
        arrayOfString[i] = stringTokenizer.nextToken(); 
    } 
    StringBuilder stringBuilder = new StringBuilder("http://");
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (b)
        stringBuilder.append('.'); 
      stringBuilder.append(arrayOfString[b]);
    } 
    stringBuilder.append('/');
    return stringBuilder.toString();
  }
  
  private boolean isServiceException(Class<?> paramClass) { return (EXCEPTION_CLASS.isAssignableFrom(paramClass) && !RUNTIME_EXCEPTION_CLASS.isAssignableFrom(paramClass) && !REMOTE_EXCEPTION_CLASS.isAssignableFrom(paramClass)); }
  
  private void processMethod(Method paramMethod) {
    JavaMethodImpl javaMethodImpl;
    WebMethod webMethod = (WebMethod)getAnnotation(paramMethod, WebMethod.class);
    if (webMethod != null && webMethod.exclude())
      return; 
    String str1 = paramMethod.getName();
    boolean bool = (getAnnotation(paramMethod, javax.jws.Oneway.class) != null) ? 1 : 0;
    if (bool)
      for (Class clazz : paramMethod.getExceptionTypes()) {
        if (isServiceException(clazz))
          throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.checked.exceptions", new Object[] { this.portClass.getCanonicalName(), str1, clazz.getName() }); 
      }  
    if (paramMethod.getDeclaringClass() == this.portClass) {
      javaMethodImpl = new JavaMethodImpl(this.model, paramMethod, paramMethod, this.metadataReader);
    } else {
      try {
        Method method = this.portClass.getMethod(paramMethod.getName(), paramMethod.getParameterTypes());
        javaMethodImpl = new JavaMethodImpl(this.model, method, paramMethod, this.metadataReader);
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new RuntimeModelerException("runtime.modeler.method.not.found", new Object[] { paramMethod.getName(), this.portClass.getName() });
      } 
    } 
    MEP mEP = getMEP(paramMethod);
    javaMethodImpl.setMEP(mEP);
    String str2 = null;
    String str3 = paramMethod.getName();
    if (webMethod != null) {
      str2 = webMethod.action();
      str3 = (webMethod.operationName().length() > 0) ? webMethod.operationName() : str3;
    } 
    if (this.binding != null) {
      WSDLBoundOperation wSDLBoundOperation = this.binding.getBinding().get(new QName(this.targetNamespace, str3));
      if (wSDLBoundOperation != null) {
        WSDLInput wSDLInput = wSDLBoundOperation.getOperation().getInput();
        String str = wSDLInput.getAction();
        if (str != null && !wSDLInput.isDefaultAction()) {
          str2 = str;
        } else {
          str2 = wSDLBoundOperation.getSOAPAction();
        } 
      } 
    } 
    javaMethodImpl.setOperationQName(new QName(this.targetNamespace, str3));
    SOAPBinding sOAPBinding = (SOAPBinding)getAnnotation(paramMethod, SOAPBinding.class);
    if (sOAPBinding != null && sOAPBinding.style() == SOAPBinding.Style.RPC) {
      logger.warning(ModelerMessages.RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(sOAPBinding, paramMethod.getName(), paramMethod.getDeclaringClass().getName()));
    } else if (sOAPBinding == null && !paramMethod.getDeclaringClass().equals(this.portClass)) {
      sOAPBinding = (SOAPBinding)getAnnotation(paramMethod.getDeclaringClass(), SOAPBinding.class);
      if (sOAPBinding != null && sOAPBinding.style() == SOAPBinding.Style.RPC && sOAPBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE)
        throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { sOAPBinding, paramMethod.getDeclaringClass() }); 
    } 
    if (sOAPBinding != null && this.defaultBinding.getStyle() != sOAPBinding.style())
      throw new RuntimeModelerException("runtime.modeler.soapbinding.conflict", new Object[] { sOAPBinding.style(), paramMethod.getName(), this.defaultBinding.getStyle() }); 
    boolean bool1 = this.isWrapped;
    SOAPBinding.Style style = this.defaultBinding.getStyle();
    if (sOAPBinding != null) {
      SOAPBindingImpl sOAPBindingImpl = createBinding(sOAPBinding);
      style = sOAPBindingImpl.getStyle();
      if (str2 != null)
        sOAPBindingImpl.setSOAPAction(str2); 
      bool1 = sOAPBinding.parameterStyle().equals(SOAPBinding.ParameterStyle.WRAPPED);
      javaMethodImpl.setBinding(sOAPBindingImpl);
    } else {
      SOAPBindingImpl sOAPBindingImpl = new SOAPBindingImpl(this.defaultBinding);
      if (str2 != null) {
        sOAPBindingImpl.setSOAPAction(str2);
      } else {
        String str = (SOAPVersion.SOAP_11 == sOAPBindingImpl.getSOAPVersion()) ? "" : null;
        sOAPBindingImpl.setSOAPAction(str);
      } 
      javaMethodImpl.setBinding(sOAPBindingImpl);
    } 
    if (!bool1) {
      processDocBareMethod(javaMethodImpl, str3, paramMethod);
    } else if (style.equals(SOAPBinding.Style.DOCUMENT)) {
      processDocWrappedMethod(javaMethodImpl, str1, str3, paramMethod);
    } else {
      processRpcMethod(javaMethodImpl, str1, str3, paramMethod);
    } 
    this.model.addJavaMethod(javaMethodImpl);
  }
  
  private MEP getMEP(Method paramMethod) { return (getAnnotation(paramMethod, javax.jws.Oneway.class) != null) ? MEP.ONE_WAY : (javax.xml.ws.Response.class.isAssignableFrom(paramMethod.getReturnType()) ? MEP.ASYNC_POLL : (java.util.concurrent.Future.class.isAssignableFrom(paramMethod.getReturnType()) ? MEP.ASYNC_CALLBACK : MEP.REQUEST_RESPONSE)); }
  
  protected void processDocWrappedMethod(JavaMethodImpl paramJavaMethodImpl, String paramString1, String paramString2, Method paramMethod) {
    String str3;
    String str2;
    boolean bool1 = false;
    boolean bool2 = (getAnnotation(paramMethod, javax.jws.Oneway.class) != null) ? 1 : 0;
    RequestWrapper requestWrapper = (RequestWrapper)getAnnotation(paramMethod, RequestWrapper.class);
    ResponseWrapper responseWrapper = (ResponseWrapper)getAnnotation(paramMethod, ResponseWrapper.class);
    String str1 = this.packageName + ".jaxws.";
    if (this.packageName == null || this.packageName.length() == 0)
      str1 = "jaxws."; 
    if (requestWrapper != null && requestWrapper.className().length() > 0) {
      str2 = requestWrapper.className();
    } else {
      str2 = str1 + capitalize(paramMethod.getName());
    } 
    if (responseWrapper != null && responseWrapper.className().length() > 0) {
      str3 = responseWrapper.className();
    } else {
      str3 = str1 + capitalize(paramMethod.getName()) + "Response";
    } 
    String str4 = paramString2;
    String str5 = this.targetNamespace;
    String str6 = "parameters";
    if (requestWrapper != null) {
      if (requestWrapper.targetNamespace().length() > 0)
        str5 = requestWrapper.targetNamespace(); 
      if (requestWrapper.localName().length() > 0)
        str4 = requestWrapper.localName(); 
      try {
        if (requestWrapper.partName().length() > 0)
          str6 = requestWrapper.partName(); 
      } catch (LinkageError linkageError) {}
    } 
    QName qName1 = new QName(str5, str4);
    paramJavaMethodImpl.setRequestPayloadName(qName1);
    Class clazz1 = getRequestWrapperClass(str2, paramMethod, qName1);
    Class clazz2 = null;
    String str7 = paramString2 + "Response";
    String str8 = this.targetNamespace;
    QName qName2 = null;
    String str9 = "parameters";
    if (!bool2) {
      if (responseWrapper != null) {
        if (responseWrapper.targetNamespace().length() > 0)
          str8 = responseWrapper.targetNamespace(); 
        if (responseWrapper.localName().length() > 0)
          str7 = responseWrapper.localName(); 
        try {
          if (responseWrapper.partName().length() > 0)
            str9 = responseWrapper.partName(); 
        } catch (LinkageError linkageError) {}
      } 
      qName2 = new QName(str8, str7);
      clazz2 = getResponseWrapperClass(str3, paramMethod, qName2);
    } 
    TypeInfo typeInfo = new TypeInfo(qName1, clazz1, new Annotation[0]);
    typeInfo.setNillable(false);
    WrapperParameter wrapperParameter1 = new WrapperParameter(paramJavaMethodImpl, typeInfo, WebParam.Mode.IN, 0);
    wrapperParameter1.setPartName(str6);
    wrapperParameter1.setBinding(ParameterBinding.BODY);
    paramJavaMethodImpl.addParameter(wrapperParameter1);
    WrapperParameter wrapperParameter2 = null;
    if (!bool2) {
      typeInfo = new TypeInfo(qName2, clazz2, new Annotation[0]);
      typeInfo.setNillable(false);
      wrapperParameter2 = new WrapperParameter(paramJavaMethodImpl, typeInfo, WebParam.Mode.OUT, -1);
      paramJavaMethodImpl.addParameter(wrapperParameter2);
      wrapperParameter2.setBinding(ParameterBinding.BODY);
    } 
    WebResult webResult = (WebResult)getAnnotation(paramMethod, WebResult.class);
    XmlElement xmlElement = (XmlElement)getAnnotation(paramMethod, XmlElement.class);
    QName qName3 = getReturnQName(paramMethod, webResult, xmlElement);
    Class clazz3 = paramMethod.getReturnType();
    boolean bool = false;
    if (webResult != null) {
      bool = webResult.header();
      bool1 = (bool || bool1) ? 1 : 0;
      if (bool && xmlElement != null)
        throw new RuntimeModelerException("@XmlElement cannot be specified on method " + paramMethod + " as the return value is bound to header", new Object[0]); 
      if (qName3.getNamespaceURI().length() == 0 && webResult.header())
        qName3 = new QName(this.targetNamespace, qName3.getLocalPart()); 
    } 
    if (paramJavaMethodImpl.isAsync()) {
      clazz3 = getAsyncReturnType(paramMethod, clazz3);
      qName3 = new QName("return");
    } 
    qName3 = qualifyWrappeeIfNeeded(qName3, str8);
    if (!bool2 && clazz3 != null && !clazz3.getName().equals("void")) {
      Annotation[] arrayOfAnnotation1 = getAnnotations(paramMethod);
      if (qName3.getLocalPart() != null) {
        TypeInfo typeInfo1 = new TypeInfo(qName3, clazz3, arrayOfAnnotation1);
        this.metadataReader.getProperties(typeInfo1.properties(), paramMethod);
        typeInfo1.setGenericType(paramMethod.getGenericReturnType());
        ParameterImpl parameterImpl = new ParameterImpl(paramJavaMethodImpl, typeInfo1, WebParam.Mode.OUT, -1);
        if (bool) {
          parameterImpl.setBinding(ParameterBinding.HEADER);
          paramJavaMethodImpl.addParameter(parameterImpl);
        } else {
          parameterImpl.setBinding(ParameterBinding.BODY);
          wrapperParameter2.addWrapperChild(parameterImpl);
        } 
      } 
    } 
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    Annotation[][] arrayOfAnnotation = getParamAnnotations(paramMethod);
    byte b = 0;
    for (Class clazz : arrayOfClass) {
      String str10 = null;
      String str11 = "arg" + b;
      boolean bool3 = false;
      if (!paramJavaMethodImpl.isAsync() || !javax.xml.ws.AsyncHandler.class.isAssignableFrom(clazz)) {
        boolean bool4 = HOLDER_CLASS.isAssignableFrom(clazz);
        if (bool4 && clazz == javax.xml.ws.Holder.class)
          clazz = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)arrayOfType[b]).getActualTypeArguments()[0]); 
        WebParam.Mode mode = bool4 ? WebParam.Mode.INOUT : WebParam.Mode.IN;
        WebParam webParam = null;
        xmlElement = null;
        for (Annotation annotation : arrayOfAnnotation[b]) {
          if (annotation.annotationType() == WebParam.class) {
            webParam = (WebParam)annotation;
          } else if (annotation.annotationType() == XmlElement.class) {
            xmlElement = (XmlElement)annotation;
          } 
        } 
        QName qName = getParameterQName(paramMethod, webParam, xmlElement, str11);
        if (webParam != null) {
          bool3 = webParam.header();
          bool1 = (bool3 || bool1) ? 1 : 0;
          if (bool3 && xmlElement != null)
            throw new RuntimeModelerException("@XmlElement cannot be specified on method " + paramMethod + " parameter that is bound to header", new Object[0]); 
          if (webParam.partName().length() > 0) {
            str10 = webParam.partName();
          } else {
            str10 = qName.getLocalPart();
          } 
          if (bool3 && qName.getNamespaceURI().equals(""))
            qName = new QName(this.targetNamespace, qName.getLocalPart()); 
          mode = webParam.mode();
          if (bool4 && mode == WebParam.Mode.IN)
            mode = WebParam.Mode.INOUT; 
        } 
        qName = qualifyWrappeeIfNeeded(qName, str5);
        typeInfo = new TypeInfo(qName, clazz, arrayOfAnnotation[b]);
        this.metadataReader.getProperties(typeInfo.properties(), paramMethod, b);
        typeInfo.setGenericType(arrayOfType[b]);
        ParameterImpl parameterImpl = new ParameterImpl(paramJavaMethodImpl, typeInfo, mode, b++);
        if (bool3) {
          parameterImpl.setBinding(ParameterBinding.HEADER);
          paramJavaMethodImpl.addParameter(parameterImpl);
          parameterImpl.setPartName(str10);
        } else {
          parameterImpl.setBinding(ParameterBinding.BODY);
          if (mode != WebParam.Mode.OUT)
            wrapperParameter1.addWrapperChild(parameterImpl); 
          if (mode != WebParam.Mode.IN) {
            if (bool2)
              throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { this.portClass.getCanonicalName(), paramString1 }); 
            wrapperParameter2.addWrapperChild(parameterImpl);
          } 
        } 
      } 
    } 
    if (bool1)
      str9 = "result"; 
    if (wrapperParameter2 != null)
      wrapperParameter2.setPartName(str9); 
    processExceptions(paramJavaMethodImpl, paramMethod);
  }
  
  private QName qualifyWrappeeIfNeeded(QName paramQName, String paramString) {
    Object object = this.config.properties().get("com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified");
    boolean bool = (object != null && object instanceof Boolean) ? ((Boolean)object).booleanValue() : 0;
    return (bool && (paramQName.getNamespaceURI() == null || "".equals(paramQName.getNamespaceURI()))) ? new QName(paramString, paramQName.getLocalPart()) : paramQName;
  }
  
  protected void processRpcMethod(JavaMethodImpl paramJavaMethodImpl, String paramString1, String paramString2, Method paramMethod) {
    QName qName3;
    boolean bool = (getAnnotation(paramMethod, javax.jws.Oneway.class) != null) ? 1 : 0;
    TreeMap treeMap1 = new TreeMap();
    TreeMap treeMap2 = new TreeMap();
    String str1 = this.targetNamespace;
    String str2 = this.targetNamespace;
    if (this.binding != null && SOAPBinding.Style.RPC.equals(this.binding.getBinding().getStyle())) {
      QName qName = new QName(this.binding.getBinding().getPortTypeName().getNamespaceURI(), paramString2);
      WSDLBoundOperation wSDLBoundOperation = this.binding.getBinding().get(qName);
      if (wSDLBoundOperation != null) {
        if (wSDLBoundOperation.getRequestNamespace() != null)
          str1 = wSDLBoundOperation.getRequestNamespace(); 
        if (wSDLBoundOperation.getResponseNamespace() != null)
          str2 = wSDLBoundOperation.getResponseNamespace(); 
      } 
    } 
    QName qName1 = new QName(str1, paramString2);
    paramJavaMethodImpl.setRequestPayloadName(qName1);
    QName qName2 = null;
    if (!bool)
      qName2 = new QName(str2, paramString2 + "Response"); 
    Class clazz1 = com.sun.xml.internal.ws.spi.db.WrapperComposite.class;
    TypeInfo typeInfo = new TypeInfo(qName1, clazz1, new Annotation[0]);
    WrapperParameter wrapperParameter1 = new WrapperParameter(paramJavaMethodImpl, typeInfo, WebParam.Mode.IN, 0);
    wrapperParameter1.setInBinding(ParameterBinding.BODY);
    paramJavaMethodImpl.addParameter(wrapperParameter1);
    WrapperParameter wrapperParameter2 = null;
    if (!bool) {
      typeInfo = new TypeInfo(qName2, clazz1, new Annotation[0]);
      wrapperParameter2 = new WrapperParameter(paramJavaMethodImpl, typeInfo, WebParam.Mode.OUT, -1);
      wrapperParameter2.setOutBinding(ParameterBinding.BODY);
      paramJavaMethodImpl.addParameter(wrapperParameter2);
    } 
    Class clazz2 = paramMethod.getReturnType();
    String str3 = "return";
    String str4 = this.targetNamespace;
    String str5 = str3;
    boolean bool1 = false;
    WebResult webResult = (WebResult)getAnnotation(paramMethod, WebResult.class);
    if (webResult != null) {
      bool1 = webResult.header();
      if (webResult.name().length() > 0)
        str3 = webResult.name(); 
      if (webResult.partName().length() > 0) {
        str5 = webResult.partName();
        if (!bool1)
          str3 = str5; 
      } else {
        str5 = str3;
      } 
      if (webResult.targetNamespace().length() > 0)
        str4 = webResult.targetNamespace(); 
      bool1 = webResult.header();
    } 
    if (bool1) {
      qName3 = new QName(str4, str3);
    } else {
      qName3 = new QName(str3);
    } 
    if (paramJavaMethodImpl.isAsync())
      clazz2 = getAsyncReturnType(paramMethod, clazz2); 
    if (!bool && clazz2 != null && clazz2 != void.class) {
      Annotation[] arrayOfAnnotation1 = getAnnotations(paramMethod);
      TypeInfo typeInfo1 = new TypeInfo(qName3, clazz2, arrayOfAnnotation1);
      this.metadataReader.getProperties(typeInfo1.properties(), paramMethod);
      typeInfo1.setGenericType(paramMethod.getGenericReturnType());
      ParameterImpl parameterImpl = new ParameterImpl(paramJavaMethodImpl, typeInfo1, WebParam.Mode.OUT, -1);
      parameterImpl.setPartName(str5);
      if (bool1) {
        parameterImpl.setBinding(ParameterBinding.HEADER);
        paramJavaMethodImpl.addParameter(parameterImpl);
        typeInfo1.setGlobalElement(true);
      } else {
        ParameterBinding parameterBinding = getBinding(paramString2, str5, false, WebParam.Mode.OUT);
        parameterImpl.setBinding(parameterBinding);
        if (parameterBinding.isBody()) {
          typeInfo1.setGlobalElement(false);
          WSDLPart wSDLPart = getPart(new QName(this.targetNamespace, paramString2), str5, WebParam.Mode.OUT);
          if (wSDLPart == null) {
            treeMap1.put(Integer.valueOf(treeMap1.size() + 10000), parameterImpl);
          } else {
            treeMap1.put(Integer.valueOf(wSDLPart.getIndex()), parameterImpl);
          } 
        } else {
          paramJavaMethodImpl.addParameter(parameterImpl);
        } 
      } 
    } 
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    Annotation[][] arrayOfAnnotation = getParamAnnotations(paramMethod);
    byte b = 0;
    for (Class clazz : arrayOfClass) {
      String str6 = "";
      String str7 = "";
      String str8 = "";
      boolean bool2 = false;
      if (!paramJavaMethodImpl.isAsync() || !javax.xml.ws.AsyncHandler.class.isAssignableFrom(clazz)) {
        QName qName;
        boolean bool3 = HOLDER_CLASS.isAssignableFrom(clazz);
        if (bool3 && clazz == javax.xml.ws.Holder.class)
          clazz = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)arrayOfType[b]).getActualTypeArguments()[0]); 
        WebParam.Mode mode = bool3 ? WebParam.Mode.INOUT : WebParam.Mode.IN;
        for (Annotation annotation : arrayOfAnnotation[b]) {
          if (annotation.annotationType() == WebParam.class) {
            WebParam webParam = (WebParam)annotation;
            str6 = webParam.name();
            str8 = webParam.partName();
            bool2 = webParam.header();
            WebParam.Mode mode1 = webParam.mode();
            str7 = webParam.targetNamespace();
            if (bool3 && mode1 == WebParam.Mode.IN)
              mode1 = WebParam.Mode.INOUT; 
            mode = mode1;
            break;
          } 
        } 
        if (str6.length() == 0)
          str6 = "arg" + b; 
        if (str8.length() == 0) {
          str8 = str6;
        } else if (!bool2) {
          str6 = str8;
        } 
        if (str8.length() == 0)
          str8 = str6; 
        if (!bool2) {
          qName = new QName("", str6);
        } else {
          if (str7.length() == 0)
            str7 = this.targetNamespace; 
          qName = new QName(str7, str6);
        } 
        typeInfo = new TypeInfo(qName, clazz, arrayOfAnnotation[b]);
        this.metadataReader.getProperties(typeInfo.properties(), paramMethod, b);
        typeInfo.setGenericType(arrayOfType[b]);
        ParameterImpl parameterImpl = new ParameterImpl(paramJavaMethodImpl, typeInfo, mode, b++);
        parameterImpl.setPartName(str8);
        if (mode == WebParam.Mode.INOUT) {
          ParameterBinding parameterBinding = getBinding(paramString2, str8, bool2, WebParam.Mode.IN);
          parameterImpl.setInBinding(parameterBinding);
          parameterBinding = getBinding(paramString2, str8, bool2, WebParam.Mode.OUT);
          parameterImpl.setOutBinding(parameterBinding);
        } else if (bool2) {
          typeInfo.setGlobalElement(true);
          parameterImpl.setBinding(ParameterBinding.HEADER);
        } else {
          ParameterBinding parameterBinding = getBinding(paramString2, str8, false, mode);
          parameterImpl.setBinding(parameterBinding);
        } 
        if (parameterImpl.getInBinding().isBody()) {
          typeInfo.setGlobalElement(false);
          if (!parameterImpl.isOUT()) {
            WSDLPart wSDLPart = getPart(new QName(this.targetNamespace, paramString2), str8, WebParam.Mode.IN);
            if (wSDLPart == null) {
              treeMap2.put(Integer.valueOf(treeMap2.size() + 10000), parameterImpl);
            } else {
              treeMap2.put(Integer.valueOf(wSDLPart.getIndex()), parameterImpl);
            } 
          } 
          if (!parameterImpl.isIN()) {
            if (bool)
              throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { this.portClass.getCanonicalName(), paramString1 }); 
            WSDLPart wSDLPart = getPart(new QName(this.targetNamespace, paramString2), str8, WebParam.Mode.OUT);
            if (wSDLPart == null) {
              treeMap1.put(Integer.valueOf(treeMap1.size() + 10000), parameterImpl);
            } else {
              treeMap1.put(Integer.valueOf(wSDLPart.getIndex()), parameterImpl);
            } 
          } 
        } else {
          paramJavaMethodImpl.addParameter(parameterImpl);
        } 
      } 
    } 
    for (ParameterImpl parameterImpl : treeMap2.values())
      wrapperParameter1.addWrapperChild(parameterImpl); 
    for (ParameterImpl parameterImpl : treeMap1.values())
      wrapperParameter2.addWrapperChild(parameterImpl); 
    processExceptions(paramJavaMethodImpl, paramMethod);
  }
  
  protected void processExceptions(JavaMethodImpl paramJavaMethodImpl, Method paramMethod) {
    Action action = (Action)getAnnotation(paramMethod, Action.class);
    FaultAction[] arrayOfFaultAction = new FaultAction[0];
    if (action != null)
      arrayOfFaultAction = action.fault(); 
    for (Class clazz : paramMethod.getExceptionTypes()) {
      if (EXCEPTION_CLASS.isAssignableFrom(clazz) && !RUNTIME_EXCEPTION_CLASS.isAssignableFrom(clazz) && !REMOTE_EXCEPTION_CLASS.isAssignableFrom(clazz)) {
        Annotation[] arrayOfAnnotation;
        Class clazz1;
        WebFault webFault = (WebFault)getAnnotation(clazz, WebFault.class);
        Method method = getWSDLExceptionFaultInfo(clazz);
        ExceptionType exceptionType = ExceptionType.WSDLException;
        String str1 = this.targetNamespace;
        String str2 = clazz.getSimpleName();
        String str3 = this.packageName + ".jaxws.";
        if (this.packageName.length() == 0)
          str3 = "jaxws."; 
        String str4 = str3 + str2 + "Bean";
        String str5 = clazz.getSimpleName();
        if (webFault != null) {
          if (webFault.faultBean().length() > 0)
            str4 = webFault.faultBean(); 
          if (webFault.name().length() > 0)
            str2 = webFault.name(); 
          if (webFault.targetNamespace().length() > 0)
            str1 = webFault.targetNamespace(); 
          if (webFault.messageName().length() > 0)
            str5 = webFault.messageName(); 
        } 
        if (method == null) {
          clazz1 = getExceptionBeanClass(str4, clazz, str2, str1);
          exceptionType = ExceptionType.UserDefined;
          arrayOfAnnotation = getAnnotations(clazz1);
        } else {
          clazz1 = method.getReturnType();
          arrayOfAnnotation = getAnnotations(method);
        } 
        QName qName = new QName(str1, str2);
        TypeInfo typeInfo = new TypeInfo(qName, clazz1, arrayOfAnnotation);
        CheckedExceptionImpl checkedExceptionImpl = new CheckedExceptionImpl(paramJavaMethodImpl, clazz, typeInfo, exceptionType);
        checkedExceptionImpl.setMessageName(str5);
        for (FaultAction faultAction : arrayOfFaultAction) {
          if (faultAction.className().equals(clazz) && !faultAction.value().equals("")) {
            checkedExceptionImpl.setFaultAction(faultAction.value());
            break;
          } 
        } 
        paramJavaMethodImpl.addException(checkedExceptionImpl);
      } 
    } 
  }
  
  protected Method getWSDLExceptionFaultInfo(Class paramClass) {
    if (getAnnotation(paramClass, WebFault.class) == null)
      return null; 
    try {
      return paramClass.getMethod("getFaultInfo", new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  protected void processDocBareMethod(JavaMethodImpl paramJavaMethodImpl, String paramString, Method paramMethod) {
    String str1 = paramString + "Response";
    String str2 = this.targetNamespace;
    String str3 = null;
    boolean bool = false;
    WebResult webResult = (WebResult)getAnnotation(paramMethod, WebResult.class);
    if (webResult != null) {
      if (webResult.name().length() > 0)
        str1 = webResult.name(); 
      if (webResult.targetNamespace().length() > 0)
        str2 = webResult.targetNamespace(); 
      str3 = webResult.partName();
      bool = webResult.header();
    } 
    Class clazz = paramMethod.getReturnType();
    Type type = paramMethod.getGenericReturnType();
    if (paramJavaMethodImpl.isAsync())
      clazz = getAsyncReturnType(paramMethod, clazz); 
    if (clazz != null && !clazz.getName().equals("void")) {
      Annotation[] arrayOfAnnotation1 = getAnnotations(paramMethod);
      if (str1 != null) {
        QName qName = new QName(str2, str1);
        TypeInfo typeInfo = new TypeInfo(qName, clazz, arrayOfAnnotation1);
        typeInfo.setGenericType(type);
        this.metadataReader.getProperties(typeInfo.properties(), paramMethod);
        ParameterImpl parameterImpl = new ParameterImpl(paramJavaMethodImpl, typeInfo, WebParam.Mode.OUT, -1);
        if (str3 == null || str3.length() == 0)
          str3 = str1; 
        parameterImpl.setPartName(str3);
        if (bool) {
          parameterImpl.setBinding(ParameterBinding.HEADER);
        } else {
          ParameterBinding parameterBinding = getBinding(paramString, str3, false, WebParam.Mode.OUT);
          parameterImpl.setBinding(parameterBinding);
        } 
        paramJavaMethodImpl.addParameter(parameterImpl);
      } 
    } 
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    Annotation[][] arrayOfAnnotation = getParamAnnotations(paramMethod);
    byte b = 0;
    for (Class clazz1 : arrayOfClass) {
      String str4 = paramString;
      String str5 = null;
      String str6 = this.targetNamespace;
      boolean bool1 = false;
      if (!paramJavaMethodImpl.isAsync() || !javax.xml.ws.AsyncHandler.class.isAssignableFrom(clazz1)) {
        boolean bool2 = HOLDER_CLASS.isAssignableFrom(clazz1);
        if (bool2 && clazz1 == javax.xml.ws.Holder.class)
          clazz1 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)arrayOfType[b]).getActualTypeArguments()[0]); 
        WebParam.Mode mode = bool2 ? WebParam.Mode.INOUT : WebParam.Mode.IN;
        for (Annotation annotation : arrayOfAnnotation[b]) {
          if (annotation.annotationType() == WebParam.class) {
            WebParam webParam = (WebParam)annotation;
            mode = webParam.mode();
            if (bool2 && mode == WebParam.Mode.IN)
              mode = WebParam.Mode.INOUT; 
            bool1 = webParam.header();
            if (bool1)
              str4 = "arg" + b; 
            if (mode == WebParam.Mode.OUT && !bool1)
              str4 = paramString + "Response"; 
            if (webParam.name().length() > 0)
              str4 = webParam.name(); 
            str5 = webParam.partName();
            if (!webParam.targetNamespace().equals(""))
              str6 = webParam.targetNamespace(); 
            break;
          } 
        } 
        QName qName = new QName(str6, str4);
        if (!bool1 && mode != WebParam.Mode.OUT)
          paramJavaMethodImpl.setRequestPayloadName(qName); 
        TypeInfo typeInfo = new TypeInfo(qName, clazz1, arrayOfAnnotation[b]);
        this.metadataReader.getProperties(typeInfo.properties(), paramMethod, b);
        typeInfo.setGenericType(arrayOfType[b]);
        ParameterImpl parameterImpl = new ParameterImpl(paramJavaMethodImpl, typeInfo, mode, b++);
        if (str5 == null || str5.length() == 0)
          str5 = str4; 
        parameterImpl.setPartName(str5);
        if (mode == WebParam.Mode.INOUT) {
          ParameterBinding parameterBinding = getBinding(paramString, str5, bool1, WebParam.Mode.IN);
          parameterImpl.setInBinding(parameterBinding);
          parameterBinding = getBinding(paramString, str5, bool1, WebParam.Mode.OUT);
          parameterImpl.setOutBinding(parameterBinding);
        } else if (bool1) {
          parameterImpl.setBinding(ParameterBinding.HEADER);
        } else {
          ParameterBinding parameterBinding = getBinding(paramString, str5, false, mode);
          parameterImpl.setBinding(parameterBinding);
        } 
        paramJavaMethodImpl.addParameter(parameterImpl);
      } 
    } 
    validateDocBare(paramJavaMethodImpl);
    processExceptions(paramJavaMethodImpl, paramMethod);
  }
  
  private void validateDocBare(JavaMethodImpl paramJavaMethodImpl) {
    byte b1 = 0;
    for (Parameter parameter : paramJavaMethodImpl.getRequestParameters()) {
      if (parameter.getBinding().equals(ParameterBinding.BODY) && parameter.isIN())
        b1++; 
      if (b1 > 1)
        throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), paramJavaMethodImpl.getMethod().getName())); 
    } 
    byte b2 = 0;
    for (Parameter parameter : paramJavaMethodImpl.getResponseParameters()) {
      if (parameter.getBinding().equals(ParameterBinding.BODY) && parameter.isOUT())
        b2++; 
      if (b2 > 1)
        throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), paramJavaMethodImpl.getMethod().getName())); 
    } 
  }
  
  private Class getAsyncReturnType(Method paramMethod, Class paramClass) {
    if (javax.xml.ws.Response.class.isAssignableFrom(paramClass)) {
      Type type = paramMethod.getGenericReturnType();
      return (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)type).getActualTypeArguments()[0]);
    } 
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    byte b = 0;
    for (Class clazz : arrayOfClass) {
      if (javax.xml.ws.AsyncHandler.class.isAssignableFrom(clazz))
        return (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)arrayOfType[b]).getActualTypeArguments()[0]); 
      b++;
    } 
    return paramClass;
  }
  
  public static String capitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toUpperCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  public static QName getServiceName(Class<?> paramClass) { return getServiceName(paramClass, null); }
  
  public static QName getServiceName(Class<?> paramClass, boolean paramBoolean) { return getServiceName(paramClass, null, paramBoolean); }
  
  public static QName getServiceName(Class<?> paramClass, MetadataReader paramMetadataReader) { return getServiceName(paramClass, paramMetadataReader, true); }
  
  public static QName getServiceName(Class<?> paramClass, MetadataReader paramMetadataReader, boolean paramBoolean) {
    if (paramClass.isInterface())
      throw new RuntimeModelerException("runtime.modeler.cannot.get.serviceName.from.interface", new Object[] { paramClass.getCanonicalName() }); 
    String str1 = paramClass.getSimpleName() + "Service";
    String str2 = "";
    if (paramClass.getPackage() != null)
      str2 = paramClass.getPackage().getName(); 
    WebService webService = (WebService)getAnnotation(WebService.class, paramClass, paramMetadataReader);
    if (paramBoolean && webService == null)
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() }); 
    if (webService != null && webService.serviceName().length() > 0)
      str1 = webService.serviceName(); 
    String str3 = getNamespace(str2);
    if (webService != null && webService.targetNamespace().length() > 0) {
      str3 = webService.targetNamespace();
    } else if (str3 == null) {
      throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { paramClass.getName() });
    } 
    return new QName(str3, str1);
  }
  
  public static QName getPortName(Class<?> paramClass, String paramString) { return getPortName(paramClass, null, paramString); }
  
  public static QName getPortName(Class<?> paramClass, String paramString, boolean paramBoolean) { return getPortName(paramClass, null, paramString, paramBoolean); }
  
  public static QName getPortName(Class<?> paramClass, MetadataReader paramMetadataReader, String paramString) { return getPortName(paramClass, paramMetadataReader, paramString, true); }
  
  public static QName getPortName(Class<?> paramClass, MetadataReader paramMetadataReader, String paramString, boolean paramBoolean) {
    String str;
    WebService webService = (WebService)getAnnotation(WebService.class, paramClass, paramMetadataReader);
    if (paramBoolean && webService == null)
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() }); 
    if (webService != null && webService.portName().length() > 0) {
      str = webService.portName();
    } else if (webService != null && webService.name().length() > 0) {
      str = webService.name() + "Port";
    } else {
      str = paramClass.getSimpleName() + "Port";
    } 
    if (paramString == null)
      if (webService != null && webService.targetNamespace().length() > 0) {
        paramString = webService.targetNamespace();
      } else {
        String str1 = null;
        if (paramClass.getPackage() != null)
          str1 = paramClass.getPackage().getName(); 
        if (str1 != null)
          paramString = getNamespace(str1); 
        if (paramString == null)
          throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { paramClass.getName() }); 
      }  
    return new QName(paramString, str);
  }
  
  static <A extends Annotation> A getAnnotation(Class<A> paramClass1, Class<?> paramClass2, MetadataReader paramMetadataReader) { return (A)((paramMetadataReader == null) ? paramClass2.getAnnotation(paramClass1) : paramMetadataReader.getAnnotation(paramClass1, paramClass2)); }
  
  public static QName getPortTypeName(Class<?> paramClass) { return getPortTypeName(paramClass, null, null); }
  
  public static QName getPortTypeName(Class<?> paramClass, MetadataReader paramMetadataReader) { return getPortTypeName(paramClass, null, paramMetadataReader); }
  
  public static QName getPortTypeName(Class<?> paramClass, String paramString, MetadataReader paramMetadataReader) {
    assert paramClass != null;
    WebService webService = (WebService)getAnnotation(WebService.class, paramClass, paramMetadataReader);
    Class<?> clazz = paramClass;
    if (webService == null)
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() }); 
    if (!paramClass.isInterface()) {
      String str1 = webService.endpointInterface();
      if (str1.length() > 0) {
        try {
          clazz = Thread.currentThread().getContextClassLoader().loadClass(str1);
        } catch (ClassNotFoundException classNotFoundException) {
          throw new RuntimeModelerException("runtime.modeler.class.not.found", new Object[] { str1 });
        } 
        WebService webService1 = (WebService)getAnnotation(WebService.class, clazz, paramMetadataReader);
        if (webService1 == null)
          throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() }); 
      } 
    } 
    webService = (WebService)getAnnotation(WebService.class, clazz, paramMetadataReader);
    String str = webService.name();
    if (str.length() == 0)
      str = clazz.getSimpleName(); 
    if (paramString == null || "".equals(paramString.trim()))
      paramString = webService.targetNamespace(); 
    if (paramString.length() == 0)
      paramString = getNamespace(clazz.getPackage().getName()); 
    if (paramString == null)
      throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { clazz.getName() }); 
    return new QName(paramString, str);
  }
  
  private ParameterBinding getBinding(String paramString1, String paramString2, boolean paramBoolean, WebParam.Mode paramMode) {
    if (this.binding == null)
      return paramBoolean ? ParameterBinding.HEADER : ParameterBinding.BODY; 
    QName qName = new QName(this.binding.getBinding().getPortType().getName().getNamespaceURI(), paramString1);
    return this.binding.getBinding().getBinding(qName, paramString2, paramMode);
  }
  
  private WSDLPart getPart(QName paramQName, String paramString, WebParam.Mode paramMode) {
    if (this.binding != null) {
      WSDLBoundOperation wSDLBoundOperation = this.binding.getBinding().get(paramQName);
      if (wSDLBoundOperation != null)
        return wSDLBoundOperation.getPart(paramString, paramMode); 
    } 
    return null;
  }
  
  private static Boolean getBooleanSystemProperty(final String prop) { return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            String str = System.getProperty(prop);
            return (str != null) ? Boolean.valueOf(str) : Boolean.FALSE;
          }
        }); }
  
  private static QName getReturnQName(Method paramMethod, WebResult paramWebResult, XmlElement paramXmlElement) {
    String str1 = null;
    if (paramWebResult != null && paramWebResult.name().length() > 0)
      str1 = paramWebResult.name(); 
    String str2 = null;
    if (paramXmlElement != null && !paramXmlElement.name().equals("##default"))
      str2 = paramXmlElement.name(); 
    if (str2 != null && str1 != null && !str2.equals(str1))
      throw new RuntimeModelerException("@XmlElement(name)=" + str2 + " and @WebResult(name)=" + str1 + " are different for method " + paramMethod, new Object[0]); 
    String str3 = "return";
    if (str1 != null) {
      str3 = str1;
    } else if (str2 != null) {
      str3 = str2;
    } 
    String str4 = null;
    if (paramWebResult != null && paramWebResult.targetNamespace().length() > 0)
      str4 = paramWebResult.targetNamespace(); 
    String str5 = null;
    if (paramXmlElement != null && !paramXmlElement.namespace().equals("##default"))
      str5 = paramXmlElement.namespace(); 
    if (str5 != null && str4 != null && !str5.equals(str4))
      throw new RuntimeModelerException("@XmlElement(namespace)=" + str5 + " and @WebResult(targetNamespace)=" + str4 + " are different for method " + paramMethod, new Object[0]); 
    String str6 = "";
    if (str4 != null) {
      str6 = str4;
    } else if (str5 != null) {
      str6 = str5;
    } 
    return new QName(str6, str3);
  }
  
  private static QName getParameterQName(Method paramMethod, WebParam paramWebParam, XmlElement paramXmlElement, String paramString) {
    String str1 = null;
    if (paramWebParam != null && paramWebParam.name().length() > 0)
      str1 = paramWebParam.name(); 
    String str2 = null;
    if (paramXmlElement != null && !paramXmlElement.name().equals("##default"))
      str2 = paramXmlElement.name(); 
    if (str2 != null && str1 != null && !str2.equals(str1))
      throw new RuntimeModelerException("@XmlElement(name)=" + str2 + " and @WebParam(name)=" + str1 + " are different for method " + paramMethod, new Object[0]); 
    String str3 = paramString;
    if (str1 != null) {
      str3 = str1;
    } else if (str2 != null) {
      str3 = str2;
    } 
    String str4 = null;
    if (paramWebParam != null && paramWebParam.targetNamespace().length() > 0)
      str4 = paramWebParam.targetNamespace(); 
    String str5 = null;
    if (paramXmlElement != null && !paramXmlElement.namespace().equals("##default"))
      str5 = paramXmlElement.namespace(); 
    if (str5 != null && str4 != null && !str5.equals(str4))
      throw new RuntimeModelerException("@XmlElement(namespace)=" + str5 + " and @WebParam(targetNamespace)=" + str4 + " are different for method " + paramMethod, new Object[0]); 
    String str6 = "";
    if (str4 != null) {
      str6 = str4;
    } else if (str5 != null) {
      str6 = str5;
    } 
    return new QName(str6, str3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\RuntimeModeler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */