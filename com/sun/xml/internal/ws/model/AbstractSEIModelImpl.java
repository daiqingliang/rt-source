package com.sun.xml.internal.ws.model;

import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.Pool;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class AbstractSEIModelImpl implements SEIModel {
  private List<Class> additionalClasses = new ArrayList();
  
  private Pool.Marshaller marshallers;
  
  protected JAXBRIContext jaxbContext;
  
  protected BindingContext bindingContext;
  
  private String wsdlLocation;
  
  private QName serviceName;
  
  private QName portName;
  
  private QName portTypeName;
  
  private Map<Method, JavaMethodImpl> methodToJM = new HashMap();
  
  private Map<QName, JavaMethodImpl> nameToJM = new HashMap();
  
  private Map<QName, JavaMethodImpl> wsdlOpToJM = new HashMap();
  
  private List<JavaMethodImpl> javaMethods = new ArrayList();
  
  private final Map<TypeReference, Bridge> bridgeMap = new HashMap();
  
  private final Map<TypeInfo, XMLBridge> xmlBridgeMap = new HashMap();
  
  protected final QName emptyBodyName = new QName("");
  
  private String targetNamespace = "";
  
  private List<String> knownNamespaceURIs = null;
  
  private WSDLPort port;
  
  private final WebServiceFeatureList features;
  
  private Databinding databinding;
  
  BindingID bindingId;
  
  protected Class contractClass;
  
  protected Class endpointClass;
  
  protected ClassLoader classLoader = null;
  
  protected WSBinding wsBinding;
  
  protected BindingInfo databindingInfo;
  
  protected String defaultSchemaNamespaceSuffix;
  
  private static final Logger LOGGER = Logger.getLogger(AbstractSEIModelImpl.class.getName());
  
  protected AbstractSEIModelImpl(WebServiceFeatureList paramWebServiceFeatureList) {
    this.features = paramWebServiceFeatureList;
    this.databindingInfo = new BindingInfo();
    this.databindingInfo.setSEIModel(this);
  }
  
  void postProcess() {
    if (this.jaxbContext != null)
      return; 
    populateMaps();
    createJAXBContext();
  }
  
  public void freeze(WSDLPort paramWSDLPort) {
    this.port = paramWSDLPort;
    for (JavaMethodImpl javaMethodImpl : this.javaMethods) {
      javaMethodImpl.freeze(paramWSDLPort);
      putOp(javaMethodImpl.getOperationQName(), javaMethodImpl);
    } 
    if (this.databinding != null)
      ((DatabindingImpl)this.databinding).freeze(paramWSDLPort); 
  }
  
  protected abstract void populateMaps();
  
  public Pool.Marshaller getMarshallerPool() { return this.marshallers; }
  
  public JAXBContext getJAXBContext() {
    JAXBContext jAXBContext = this.bindingContext.getJAXBContext();
    return (jAXBContext != null) ? jAXBContext : this.jaxbContext;
  }
  
  public BindingContext getBindingContext() { return this.bindingContext; }
  
  public List<String> getKnownNamespaceURIs() { return this.knownNamespaceURIs; }
  
  public final Bridge getBridge(TypeReference paramTypeReference) {
    Bridge bridge = (Bridge)this.bridgeMap.get(paramTypeReference);
    assert bridge != null;
    return bridge;
  }
  
  public final XMLBridge getXMLBridge(TypeInfo paramTypeInfo) {
    XMLBridge xMLBridge = (XMLBridge)this.xmlBridgeMap.get(paramTypeInfo);
    assert xMLBridge != null;
    return xMLBridge;
  }
  
  private void createJAXBContext() {
    final List types = getAllTypeInfos();
    final ArrayList cls = new ArrayList(list.size() + this.additionalClasses.size());
    arrayList.addAll(this.additionalClasses);
    for (TypeInfo typeInfo : list)
      arrayList.add((Class)typeInfo.type); 
    try {
      this.bindingContext = (BindingContext)AccessController.doPrivileged(new PrivilegedExceptionAction<BindingContext>() {
            public BindingContext run() {
              if (LOGGER.isLoggable(Level.FINEST))
                LOGGER.log(Level.FINEST, "Creating JAXBContext with classes={0} and types={1}", new Object[] { cls, types }); 
              UsesJAXBContextFeature usesJAXBContextFeature = (UsesJAXBContextFeature)AbstractSEIModelImpl.this.features.get(UsesJAXBContextFeature.class);
              DatabindingModeFeature databindingModeFeature = (DatabindingModeFeature)AbstractSEIModelImpl.this.features.get(DatabindingModeFeature.class);
              JAXBContextFactory jAXBContextFactory = (usesJAXBContextFeature != null) ? usesJAXBContextFeature.getFactory() : null;
              if (jAXBContextFactory == null)
                jAXBContextFactory = JAXBContextFactory.DEFAULT; 
              AbstractSEIModelImpl.this.databindingInfo.properties().put(JAXBContextFactory.class.getName(), jAXBContextFactory);
              if (databindingModeFeature != null) {
                if (LOGGER.isLoggable(Level.FINE))
                  LOGGER.log(Level.FINE, "DatabindingModeFeature in SEI specifies mode: {0}", databindingModeFeature.getMode()); 
                AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode(databindingModeFeature.getMode());
              } 
              if (usesJAXBContextFeature != null)
                AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode("glassfish.jaxb"); 
              AbstractSEIModelImpl.this.databindingInfo.setClassLoader(AbstractSEIModelImpl.this.classLoader);
              AbstractSEIModelImpl.this.databindingInfo.contentClasses().addAll(cls);
              AbstractSEIModelImpl.this.databindingInfo.typeInfos().addAll(types);
              AbstractSEIModelImpl.this.databindingInfo.properties().put("c14nSupport", Boolean.FALSE);
              AbstractSEIModelImpl.this.databindingInfo.setDefaultNamespace(AbstractSEIModelImpl.this.getDefaultSchemaNamespace());
              BindingContext bindingContext = BindingContextFactory.create(AbstractSEIModelImpl.this.databindingInfo);
              if (LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Created binding context: " + bindingContext.getClass().getName()); 
              return bindingContext;
            }
          });
      createBondMap(list);
    } catch (PrivilegedActionException privilegedActionException) {
      throw new WebServiceException(ModelerMessages.UNABLE_TO_CREATE_JAXB_CONTEXT(), privilegedActionException);
    } 
    this.knownNamespaceURIs = new ArrayList();
    for (String str : this.bindingContext.getKnownNamespaceURIs()) {
      if (str.length() > 0 && !str.equals("http://www.w3.org/2001/XMLSchema") && !str.equals("http://www.w3.org/XML/1998/namespace"))
        this.knownNamespaceURIs.add(str); 
    } 
    this.marshallers = new Pool.Marshaller(this.jaxbContext);
  }
  
  private List<TypeInfo> getAllTypeInfos() {
    ArrayList arrayList = new ArrayList();
    Collection collection = this.methodToJM.values();
    for (JavaMethodImpl javaMethodImpl : collection)
      javaMethodImpl.fillTypes(arrayList); 
    return arrayList;
  }
  
  private void createBridgeMap(List<TypeReference> paramList) {
    for (TypeReference typeReference : paramList) {
      Bridge bridge = this.jaxbContext.createBridge(typeReference);
      this.bridgeMap.put(typeReference, bridge);
    } 
  }
  
  private void createBondMap(List<TypeInfo> paramList) {
    for (TypeInfo typeInfo : paramList) {
      XMLBridge xMLBridge = this.bindingContext.createBridge(typeInfo);
      this.xmlBridgeMap.put(typeInfo, xMLBridge);
    } 
  }
  
  public boolean isKnownFault(QName paramQName, Method paramMethod) {
    JavaMethodImpl javaMethodImpl = getJavaMethod(paramMethod);
    for (CheckedExceptionImpl checkedExceptionImpl : javaMethodImpl.getCheckedExceptions()) {
      if ((checkedExceptionImpl.getDetailType()).tagName.equals(paramQName))
        return true; 
    } 
    return false;
  }
  
  public boolean isCheckedException(Method paramMethod, Class paramClass) {
    JavaMethodImpl javaMethodImpl = getJavaMethod(paramMethod);
    for (CheckedExceptionImpl checkedExceptionImpl : javaMethodImpl.getCheckedExceptions()) {
      if (checkedExceptionImpl.getExceptionClass().equals(paramClass))
        return true; 
    } 
    return false;
  }
  
  public JavaMethodImpl getJavaMethod(Method paramMethod) { return (JavaMethodImpl)this.methodToJM.get(paramMethod); }
  
  public JavaMethodImpl getJavaMethod(QName paramQName) { return (JavaMethodImpl)this.nameToJM.get(paramQName); }
  
  public JavaMethod getJavaMethodForWsdlOperation(QName paramQName) { return (JavaMethod)this.wsdlOpToJM.get(paramQName); }
  
  public QName getQNameForJM(JavaMethodImpl paramJavaMethodImpl) {
    for (QName qName : this.nameToJM.keySet()) {
      JavaMethodImpl javaMethodImpl = (JavaMethodImpl)this.nameToJM.get(qName);
      if (javaMethodImpl.getOperationName().equals(paramJavaMethodImpl.getOperationName()))
        return qName; 
    } 
    return null;
  }
  
  public final Collection<JavaMethodImpl> getJavaMethods() { return Collections.unmodifiableList(this.javaMethods); }
  
  void addJavaMethod(JavaMethodImpl paramJavaMethodImpl) {
    if (paramJavaMethodImpl != null)
      this.javaMethods.add(paramJavaMethodImpl); 
  }
  
  private List<ParameterImpl> applyRpcLitParamBinding(JavaMethodImpl paramJavaMethodImpl, WrapperParameter paramWrapperParameter, WSDLBoundPortType paramWSDLBoundPortType, WebParam.Mode paramMode) {
    QName qName = new QName(paramWSDLBoundPortType.getPortTypeName().getNamespaceURI(), paramJavaMethodImpl.getOperationName());
    WSDLBoundOperation wSDLBoundOperation = paramWSDLBoundPortType.get(qName);
    HashMap hashMap = new HashMap();
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    for (ParameterImpl parameterImpl : paramWrapperParameter.wrapperChildren) {
      String str = parameterImpl.getPartName();
      if (str == null)
        continue; 
      ParameterBinding parameterBinding = paramWSDLBoundPortType.getBinding(qName, str, paramMode);
      if (parameterBinding != null) {
        if (paramMode == WebParam.Mode.IN) {
          parameterImpl.setInBinding(parameterBinding);
        } else if (paramMode == WebParam.Mode.OUT || paramMode == WebParam.Mode.INOUT) {
          parameterImpl.setOutBinding(parameterBinding);
        } 
        if (parameterBinding.isUnbound()) {
          arrayList1.add(parameterImpl);
          continue;
        } 
        if (parameterBinding.isAttachment()) {
          arrayList2.add(parameterImpl);
          continue;
        } 
        if (parameterBinding.isBody()) {
          if (wSDLBoundOperation != null) {
            WSDLPart wSDLPart = wSDLBoundOperation.getPart(parameterImpl.getPartName(), paramMode);
            if (wSDLPart != null) {
              hashMap.put(Integer.valueOf(wSDLPart.getIndex()), parameterImpl);
              continue;
            } 
            hashMap.put(Integer.valueOf(hashMap.size()), parameterImpl);
            continue;
          } 
          hashMap.put(Integer.valueOf(hashMap.size()), parameterImpl);
        } 
      } 
    } 
    paramWrapperParameter.clear();
    for (byte b = 0; b < hashMap.size(); b++) {
      ParameterImpl parameterImpl = (ParameterImpl)hashMap.get(Integer.valueOf(b));
      paramWrapperParameter.addWrapperChild(parameterImpl);
    } 
    for (ParameterImpl parameterImpl : arrayList1)
      paramWrapperParameter.addWrapperChild(parameterImpl); 
    return arrayList2;
  }
  
  void put(QName paramQName, JavaMethodImpl paramJavaMethodImpl) { this.nameToJM.put(paramQName, paramJavaMethodImpl); }
  
  void put(Method paramMethod, JavaMethodImpl paramJavaMethodImpl) { this.methodToJM.put(paramMethod, paramJavaMethodImpl); }
  
  void putOp(QName paramQName, JavaMethodImpl paramJavaMethodImpl) { this.wsdlOpToJM.put(paramQName, paramJavaMethodImpl); }
  
  public String getWSDLLocation() { return this.wsdlLocation; }
  
  void setWSDLLocation(String paramString) { this.wsdlLocation = paramString; }
  
  public QName getServiceQName() { return this.serviceName; }
  
  public WSDLPort getPort() { return this.port; }
  
  public QName getPortName() { return this.portName; }
  
  public QName getPortTypeName() { return this.portTypeName; }
  
  void setServiceQName(QName paramQName) { this.serviceName = paramQName; }
  
  void setPortName(QName paramQName) { this.portName = paramQName; }
  
  void setPortTypeName(QName paramQName) { this.portTypeName = paramQName; }
  
  void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getTargetNamespace() { return this.targetNamespace; }
  
  String getDefaultSchemaNamespace() {
    String str = getTargetNamespace();
    if (this.defaultSchemaNamespaceSuffix == null)
      return str; 
    if (!str.endsWith("/"))
      str = str + "/"; 
    return str + this.defaultSchemaNamespaceSuffix;
  }
  
  @NotNull
  public QName getBoundPortTypeName() {
    assert this.portName != null;
    return new QName(this.portName.getNamespaceURI(), this.portName.getLocalPart() + "Binding");
  }
  
  public void addAdditionalClasses(Class... paramVarArgs) {
    for (Class clazz : paramVarArgs)
      this.additionalClasses.add(clazz); 
  }
  
  public Databinding getDatabinding() { return this.databinding; }
  
  public void setDatabinding(Databinding paramDatabinding) { this.databinding = paramDatabinding; }
  
  public WSBinding getWSBinding() { return this.wsBinding; }
  
  public Class getContractClass() { return this.contractClass; }
  
  public Class getEndpointClass() { return this.endpointClass; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\AbstractSEIModelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */