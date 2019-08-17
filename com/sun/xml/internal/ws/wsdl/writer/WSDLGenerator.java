package com.sun.xml.internal.ws.wsdl.writer;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.bind.v2.schemagen.Util;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Element;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.txw2.output.TXWResult;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLGeneratorExtension;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.writer.document.Binding;
import com.sun.xml.internal.ws.wsdl.writer.document.BindingOperationType;
import com.sun.xml.internal.ws.wsdl.writer.document.Definitions;
import com.sun.xml.internal.ws.wsdl.writer.document.Fault;
import com.sun.xml.internal.ws.wsdl.writer.document.FaultType;
import com.sun.xml.internal.ws.wsdl.writer.document.Import;
import com.sun.xml.internal.ws.wsdl.writer.document.Message;
import com.sun.xml.internal.ws.wsdl.writer.document.Operation;
import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import com.sun.xml.internal.ws.wsdl.writer.document.Port;
import com.sun.xml.internal.ws.wsdl.writer.document.PortType;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;
import com.sun.xml.internal.ws.wsdl.writer.document.StartWithExtensionsType;
import com.sun.xml.internal.ws.wsdl.writer.document.Types;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Header;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPFault;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPAddress;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Import;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;

public class WSDLGenerator {
  private JAXWSOutputSchemaResolver resolver;
  
  private WSDLResolver wsdlResolver = null;
  
  private AbstractSEIModelImpl model;
  
  private Definitions serviceDefinitions;
  
  private Definitions portDefinitions;
  
  private Types types;
  
  private static final String DOT_WSDL = ".wsdl";
  
  private static final String RESPONSE = "Response";
  
  private static final String PARAMETERS = "parameters";
  
  private static final String RESULT = "parameters";
  
  private static final String UNWRAPPABLE_RESULT = "result";
  
  private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
  
  private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
  
  private static final String XSD_PREFIX = "xsd";
  
  private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
  
  private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";
  
  private static final String SOAP_PREFIX = "soap";
  
  private static final String SOAP12_PREFIX = "soap12";
  
  private static final String TNS_PREFIX = "tns";
  
  private static final String DOCUMENT = "document";
  
  private static final String RPC = "rpc";
  
  private static final String LITERAL = "literal";
  
  private static final String REPLACE_WITH_ACTUAL_URL = "REPLACE_WITH_ACTUAL_URL";
  
  private Set<QName> processedExceptions = new HashSet();
  
  private WSBinding binding;
  
  private String wsdlLocation;
  
  private String portWSDLID;
  
  private String schemaPrefix;
  
  private WSDLGeneratorExtension extension;
  
  List<WSDLGeneratorExtension> extensionHandlers;
  
  private String endpointAddress = "REPLACE_WITH_ACTUAL_URL";
  
  private Container container;
  
  private final Class implType;
  
  private boolean inlineSchemas;
  
  private final boolean disableXmlSecurity;
  
  public WSDLGenerator(AbstractSEIModelImpl paramAbstractSEIModelImpl, WSDLResolver paramWSDLResolver, WSBinding paramWSBinding, Container paramContainer, Class paramClass, boolean paramBoolean, WSDLGeneratorExtension... paramVarArgs) { this(paramAbstractSEIModelImpl, paramWSDLResolver, paramWSBinding, paramContainer, paramClass, paramBoolean, false, paramVarArgs); }
  
  public WSDLGenerator(AbstractSEIModelImpl paramAbstractSEIModelImpl, WSDLResolver paramWSDLResolver, WSBinding paramWSBinding, Container paramContainer, Class paramClass, boolean paramBoolean1, boolean paramBoolean2, WSDLGeneratorExtension... paramVarArgs) {
    this.model = paramAbstractSEIModelImpl;
    this.resolver = new JAXWSOutputSchemaResolver();
    this.wsdlResolver = paramWSDLResolver;
    this.binding = paramWSBinding;
    this.container = paramContainer;
    this.implType = paramClass;
    this.extensionHandlers = new ArrayList();
    this.inlineSchemas = paramBoolean1;
    this.disableXmlSecurity = paramBoolean2;
    register(new W3CAddressingWSDLGeneratorExtension());
    register(new W3CAddressingMetadataWSDLGeneratorExtension());
    register(new PolicyWSDLGeneratorExtension());
    if (paramContainer != null) {
      WSDLGeneratorExtension[] arrayOfWSDLGeneratorExtension = (WSDLGeneratorExtension[])paramContainer.getSPI(WSDLGeneratorExtension[].class);
      if (arrayOfWSDLGeneratorExtension != null)
        for (WSDLGeneratorExtension wSDLGeneratorExtension : arrayOfWSDLGeneratorExtension)
          register(wSDLGeneratorExtension);  
    } 
    for (WSDLGeneratorExtension wSDLGeneratorExtension : paramVarArgs)
      register(wSDLGeneratorExtension); 
    this.extension = new WSDLGeneratorExtensionFacade((WSDLGeneratorExtension[])this.extensionHandlers.toArray(new WSDLGeneratorExtension[0]));
  }
  
  public void setEndpointAddress(String paramString) { this.endpointAddress = paramString; }
  
  protected String mangleName(String paramString) { return BindingHelper.mangleNameToClassName(paramString); }
  
  public void doGeneration() {
    CommentFilter commentFilter2 = null;
    String str = mangleName(this.model.getServiceQName().getLocalPart());
    Result result = this.wsdlResolver.getWSDL(str + ".wsdl");
    this.wsdlLocation = result.getSystemId();
    CommentFilter commentFilter1 = new CommentFilter(ResultFactory.createSerializer(result));
    if (this.model.getServiceQName().getNamespaceURI().equals(this.model.getTargetNamespace())) {
      commentFilter2 = commentFilter1;
      this.schemaPrefix = str + "_";
    } else {
      String str1 = mangleName(this.model.getPortTypeName().getLocalPart());
      if (str1.equals(str))
        str1 = str1 + "PortType"; 
      Holder holder = new Holder();
      holder.value = str1 + ".wsdl";
      result = this.wsdlResolver.getAbstractWSDL(holder);
      if (result != null) {
        this.portWSDLID = result.getSystemId();
        if (this.portWSDLID.equals(this.wsdlLocation)) {
          commentFilter2 = commentFilter1;
        } else {
          commentFilter2 = new CommentFilter(ResultFactory.createSerializer(result));
        } 
      } else {
        this.portWSDLID = (String)holder.value;
      } 
      this.schemaPrefix = (new File(this.portWSDLID)).getName();
      int i = this.schemaPrefix.lastIndexOf('.');
      if (i > 0)
        this.schemaPrefix = this.schemaPrefix.substring(0, i); 
      this.schemaPrefix = mangleName(this.schemaPrefix) + "_";
    } 
    generateDocument(commentFilter1, commentFilter2);
  }
  
  private void generateDocument(XmlSerializer paramXmlSerializer1, XmlSerializer paramXmlSerializer2) {
    this.serviceDefinitions = (Definitions)TXW.create(Definitions.class, paramXmlSerializer1);
    this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/", "");
    this.serviceDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
    this.serviceDefinitions.targetNamespace(this.model.getServiceQName().getNamespaceURI());
    this.serviceDefinitions._namespace(this.model.getServiceQName().getNamespaceURI(), "tns");
    if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
      this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap12/", "soap12");
    } else {
      this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap/", "soap");
    } 
    this.serviceDefinitions.name(this.model.getServiceQName().getLocalPart());
    WSDLGenExtnContext wSDLGenExtnContext = new WSDLGenExtnContext(this.serviceDefinitions, this.model, this.binding, this.container, this.implType);
    this.extension.start(wSDLGenExtnContext);
    if (paramXmlSerializer1 != paramXmlSerializer2 && paramXmlSerializer2 != null) {
      this.portDefinitions = (Definitions)TXW.create(Definitions.class, paramXmlSerializer2);
      this.portDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/", "");
      this.portDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
      if (this.model.getTargetNamespace() != null) {
        this.portDefinitions.targetNamespace(this.model.getTargetNamespace());
        this.portDefinitions._namespace(this.model.getTargetNamespace(), "tns");
      } 
      String str = relativize(this.portWSDLID, this.wsdlLocation);
      Import import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
      import.location(str);
    } else if (paramXmlSerializer2 != null) {
      this.portDefinitions = this.serviceDefinitions;
    } else {
      String str = relativize(this.portWSDLID, this.wsdlLocation);
      Import import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
      import.location(str);
    } 
    this.extension.addDefinitionsExtension(this.serviceDefinitions);
    if (this.portDefinitions != null) {
      generateTypes();
      generateMessages();
      generatePortType();
    } 
    generateBinding();
    generateService();
    this.extension.end(wSDLGenExtnContext);
    this.serviceDefinitions.commit();
    if (this.portDefinitions != null && this.portDefinitions != this.serviceDefinitions)
      this.portDefinitions.commit(); 
  }
  
  protected void generateTypes() {
    this.types = this.portDefinitions.types();
    if (this.model.getBindingContext() != null) {
      if (this.inlineSchemas && this.model.getBindingContext().getClass().getName().indexOf("glassfish") == -1)
        this.resolver.nonGlassfishSchemas = new ArrayList(); 
      try {
        this.model.getBindingContext().generateSchema(this.resolver);
      } catch (IOException iOException) {
        throw new WebServiceException(iOException.getMessage());
      } 
    } 
    if (this.resolver.nonGlassfishSchemas != null) {
      TransformerFactory transformerFactory = XmlUtil.newTransformerFactory(!this.disableXmlSecurity);
      try {
        Transformer transformer = transformerFactory.newTransformer();
        for (DOMResult dOMResult : this.resolver.nonGlassfishSchemas) {
          Document document = (Document)dOMResult.getNode();
          SAXResult sAXResult = new SAXResult(new TXWContentHandler(this.types));
          transformer.transform(new DOMSource(document.getDocumentElement()), sAXResult);
        } 
      } catch (TransformerConfigurationException transformerConfigurationException) {
        throw new WebServiceException(transformerConfigurationException.getMessage(), transformerConfigurationException);
      } catch (TransformerException transformerException) {
        throw new WebServiceException(transformerException.getMessage(), transformerException);
      } 
    } 
    generateWrappers();
  }
  
  void generateWrappers() {
    ArrayList arrayList = new ArrayList();
    for (JavaMethodImpl javaMethodImpl : this.model.getJavaMethods()) {
      if (javaMethodImpl.getBinding().isRpcLit())
        continue; 
      for (ParameterImpl parameterImpl : javaMethodImpl.getRequestParameters()) {
        if (parameterImpl instanceof WrapperParameter && com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals((((WrapperParameter)parameterImpl).getTypeInfo()).type))
          arrayList.add((WrapperParameter)parameterImpl); 
      } 
      for (ParameterImpl parameterImpl : javaMethodImpl.getResponseParameters()) {
        if (parameterImpl instanceof WrapperParameter && com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals((((WrapperParameter)parameterImpl).getTypeInfo()).type))
          arrayList.add((WrapperParameter)parameterImpl); 
      } 
    } 
    if (arrayList.isEmpty())
      return; 
    HashMap hashMap = new HashMap();
    for (WrapperParameter wrapperParameter : arrayList) {
      String str = wrapperParameter.getName().getNamespaceURI();
      Schema schema = (Schema)hashMap.get(str);
      if (schema == null) {
        schema = this.types.schema();
        schema.targetNamespace(str);
        hashMap.put(str, schema);
      } 
      Element element = (Element)schema._element(Element.class);
      element._attribute("name", wrapperParameter.getName().getLocalPart());
      element.type(wrapperParameter.getName());
      ComplexType complexType = (ComplexType)schema._element(ComplexType.class);
      complexType._attribute("name", wrapperParameter.getName().getLocalPart());
      ExplicitGroup explicitGroup = complexType.sequence();
      for (ParameterImpl parameterImpl : wrapperParameter.getWrapperChildren()) {
        if (parameterImpl.getBinding().isBody()) {
          LocalElement localElement = explicitGroup.element();
          localElement._attribute("name", parameterImpl.getName().getLocalPart());
          TypeInfo typeInfo = parameterImpl.getItemType();
          boolean bool = false;
          if (typeInfo == null) {
            typeInfo = parameterImpl.getTypeInfo();
          } else {
            bool = true;
          } 
          QName qName = this.model.getBindingContext().getTypeName(typeInfo);
          localElement.type(qName);
          if (bool) {
            localElement.minOccurs(0);
            localElement.maxOccurs("unbounded");
          } 
        } 
      } 
    } 
  }
  
  protected void generateMessages() {
    for (JavaMethodImpl javaMethodImpl : this.model.getJavaMethods())
      generateSOAPMessages(javaMethodImpl, javaMethodImpl.getBinding()); 
  }
  
  protected void generateSOAPMessages(JavaMethodImpl paramJavaMethodImpl, SOAPBinding paramSOAPBinding) {
    boolean bool = paramSOAPBinding.isDocLit();
    Message message = this.portDefinitions.message().name(paramJavaMethodImpl.getRequestMessageName());
    this.extension.addInputMessageExtension(message, paramJavaMethodImpl);
    BindingContext bindingContext = this.model.getBindingContext();
    boolean bool1 = true;
    for (ParameterImpl parameterImpl : paramJavaMethodImpl.getRequestParameters()) {
      if (bool) {
        if (isHeaderParameter(parameterImpl))
          bool1 = false; 
        Part part1 = message.part().name(parameterImpl.getPartName());
        part1.element(parameterImpl.getName());
        continue;
      } 
      if (parameterImpl.isWrapperStyle()) {
        for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
          Part part1 = message.part().name(parameterImpl1.getPartName());
          part1.type(bindingContext.getTypeName(parameterImpl1.getXMLBridge().getTypeInfo()));
        } 
        continue;
      } 
      Part part = message.part().name(parameterImpl.getPartName());
      part.element(parameterImpl.getName());
    } 
    if (paramJavaMethodImpl.getMEP() != MEP.ONE_WAY) {
      message = this.portDefinitions.message().name(paramJavaMethodImpl.getResponseMessageName());
      this.extension.addOutputMessageExtension(message, paramJavaMethodImpl);
      for (ParameterImpl parameterImpl : paramJavaMethodImpl.getResponseParameters()) {
        if (bool) {
          Part part1 = message.part().name(parameterImpl.getPartName());
          part1.element(parameterImpl.getName());
          continue;
        } 
        if (parameterImpl.isWrapperStyle()) {
          for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
            Part part1 = message.part().name(parameterImpl1.getPartName());
            part1.type(bindingContext.getTypeName(parameterImpl1.getXMLBridge().getTypeInfo()));
          } 
          continue;
        } 
        Part part = message.part().name(parameterImpl.getPartName());
        part.element(parameterImpl.getName());
      } 
    } 
    for (CheckedExceptionImpl checkedExceptionImpl : paramJavaMethodImpl.getCheckedExceptions()) {
      QName qName1 = (checkedExceptionImpl.getDetailType()).tagName;
      String str = checkedExceptionImpl.getMessageName();
      QName qName2 = new QName(this.model.getTargetNamespace(), str);
      if (this.processedExceptions.contains(qName2))
        continue; 
      message = this.portDefinitions.message().name(str);
      this.extension.addFaultMessageExtension(message, paramJavaMethodImpl, checkedExceptionImpl);
      Part part = message.part().name("fault");
      part.element(qName1);
      this.processedExceptions.add(qName2);
    } 
  }
  
  protected void generatePortType() {
    PortType portType = this.portDefinitions.portType().name(this.model.getPortTypeName().getLocalPart());
    this.extension.addPortTypeExtension(portType);
    for (JavaMethodImpl javaMethodImpl : this.model.getJavaMethods()) {
      Operation operation = portType.operation().name(javaMethodImpl.getOperationName());
      generateParameterOrder(operation, javaMethodImpl);
      this.extension.addOperationExtension(operation, javaMethodImpl);
      switch (javaMethodImpl.getMEP()) {
        case REQUEST_RESPONSE:
          generateInputMessage(operation, javaMethodImpl);
          generateOutputMessage(operation, javaMethodImpl);
          break;
        case ONE_WAY:
          generateInputMessage(operation, javaMethodImpl);
          break;
      } 
      for (CheckedExceptionImpl checkedExceptionImpl : javaMethodImpl.getCheckedExceptions()) {
        QName qName = new QName(this.model.getTargetNamespace(), checkedExceptionImpl.getMessageName());
        FaultType faultType = operation.fault().message(qName).name(checkedExceptionImpl.getMessageName());
        this.extension.addOperationFaultExtension(faultType, javaMethodImpl, checkedExceptionImpl);
      } 
    } 
  }
  
  protected boolean isWrapperStyle(JavaMethodImpl paramJavaMethodImpl) {
    if (paramJavaMethodImpl.getRequestParameters().size() > 0) {
      ParameterImpl parameterImpl = (ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next();
      return parameterImpl.isWrapperStyle();
    } 
    return false;
  }
  
  protected boolean isRpcLit(JavaMethodImpl paramJavaMethodImpl) { return (paramJavaMethodImpl.getBinding().getStyle() == SOAPBinding.Style.RPC); }
  
  protected void generateParameterOrder(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl) {
    if (paramJavaMethodImpl.getMEP() == MEP.ONE_WAY)
      return; 
    if (isRpcLit(paramJavaMethodImpl)) {
      generateRpcParameterOrder(paramOperation, paramJavaMethodImpl);
    } else {
      generateDocumentParameterOrder(paramOperation, paramJavaMethodImpl);
    } 
  }
  
  protected void generateRpcParameterOrder(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl) {
    StringBuilder stringBuilder = new StringBuilder();
    HashSet hashSet = new HashSet();
    List list = sortMethodParameters(paramJavaMethodImpl);
    byte b = 0;
    for (ParameterImpl parameterImpl : list) {
      if (parameterImpl.getIndex() >= 0) {
        String str = parameterImpl.getPartName();
        if (!hashSet.contains(str)) {
          if (b++ > 0)
            stringBuilder.append(' '); 
          stringBuilder.append(str);
          hashSet.add(str);
        } 
      } 
    } 
    if (b > 1)
      paramOperation.parameterOrder(stringBuilder.toString()); 
  }
  
  protected void generateDocumentParameterOrder(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl) {
    StringBuilder stringBuilder = new StringBuilder();
    HashSet hashSet = new HashSet();
    List list = sortMethodParameters(paramJavaMethodImpl);
    byte b = 0;
    for (ParameterImpl parameterImpl : list) {
      if (parameterImpl.getIndex() < 0)
        continue; 
      String str = parameterImpl.getPartName();
      if (!hashSet.contains(str)) {
        if (b++ > 0)
          stringBuilder.append(' '); 
        stringBuilder.append(str);
        hashSet.add(str);
      } 
    } 
    if (b > 1)
      paramOperation.parameterOrder(stringBuilder.toString()); 
  }
  
  protected List<ParameterImpl> sortMethodParameters(JavaMethodImpl paramJavaMethodImpl) {
    HashSet hashSet = new HashSet();
    ArrayList arrayList = new ArrayList();
    if (isRpcLit(paramJavaMethodImpl)) {
      for (ParameterImpl parameterImpl1 : paramJavaMethodImpl.getRequestParameters()) {
        if (parameterImpl1 instanceof WrapperParameter) {
          hashSet.addAll(((WrapperParameter)parameterImpl1).getWrapperChildren());
          continue;
        } 
        hashSet.add(parameterImpl1);
      } 
      for (ParameterImpl parameterImpl1 : paramJavaMethodImpl.getResponseParameters()) {
        if (parameterImpl1 instanceof WrapperParameter) {
          hashSet.addAll(((WrapperParameter)parameterImpl1).getWrapperChildren());
          continue;
        } 
        hashSet.add(parameterImpl1);
      } 
    } else {
      hashSet.addAll(paramJavaMethodImpl.getRequestParameters());
      hashSet.addAll(paramJavaMethodImpl.getResponseParameters());
    } 
    Iterator iterator = hashSet.iterator();
    if (hashSet.isEmpty())
      return arrayList; 
    ParameterImpl parameterImpl = (ParameterImpl)iterator.next();
    arrayList.add(parameterImpl);
    for (byte b = 1; b < hashSet.size(); b++) {
      parameterImpl = (ParameterImpl)iterator.next();
      byte b1;
      for (b1 = 0; b1 < b; b1++) {
        ParameterImpl parameterImpl1 = (ParameterImpl)arrayList.get(b1);
        if ((parameterImpl.getIndex() == parameterImpl1.getIndex() && parameterImpl instanceof WrapperParameter) || parameterImpl.getIndex() < parameterImpl1.getIndex())
          break; 
      } 
      arrayList.add(b1, parameterImpl);
    } 
    return arrayList;
  }
  
  protected boolean isBodyParameter(ParameterImpl paramParameterImpl) {
    ParameterBinding parameterBinding = paramParameterImpl.getBinding();
    return parameterBinding.isBody();
  }
  
  protected boolean isHeaderParameter(ParameterImpl paramParameterImpl) {
    ParameterBinding parameterBinding = paramParameterImpl.getBinding();
    return parameterBinding.isHeader();
  }
  
  protected boolean isAttachmentParameter(ParameterImpl paramParameterImpl) {
    ParameterBinding parameterBinding = paramParameterImpl.getBinding();
    return parameterBinding.isAttachment();
  }
  
  protected void generateBinding() {
    Binding binding1 = this.serviceDefinitions.binding().name(this.model.getBoundPortTypeName().getLocalPart());
    this.extension.addBindingExtension(binding1);
    binding1.type(this.model.getPortTypeName());
    boolean bool = true;
    for (JavaMethodImpl javaMethodImpl : this.model.getJavaMethods()) {
      if (bool) {
        SOAPBinding sOAPBinding = javaMethodImpl.getBinding();
        SOAPVersion sOAPVersion = sOAPBinding.getSOAPVersion();
        if (sOAPVersion == SOAPVersion.SOAP_12) {
          SOAPBinding sOAPBinding1 = binding1.soap12Binding();
          sOAPBinding1.transport(this.binding.getBindingId().getTransport());
          if (sOAPBinding.getStyle().equals(SOAPBinding.Style.DOCUMENT)) {
            sOAPBinding1.style("document");
          } else {
            sOAPBinding1.style("rpc");
          } 
        } else {
          SOAPBinding sOAPBinding1 = binding1.soapBinding();
          sOAPBinding1.transport(this.binding.getBindingId().getTransport());
          if (sOAPBinding.getStyle().equals(SOAPBinding.Style.DOCUMENT)) {
            sOAPBinding1.style("document");
          } else {
            sOAPBinding1.style("rpc");
          } 
        } 
        bool = false;
      } 
      if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
        generateSOAP12BindingOperation(javaMethodImpl, binding1);
        continue;
      } 
      generateBindingOperation(javaMethodImpl, binding1);
    } 
  }
  
  protected void generateBindingOperation(JavaMethodImpl paramJavaMethodImpl, Binding paramBinding) {
    BindingOperationType bindingOperationType = paramBinding.operation().name(paramJavaMethodImpl.getOperationName());
    this.extension.addBindingOperationExtension(bindingOperationType, paramJavaMethodImpl);
    String str = this.model.getTargetNamespace();
    QName qName = new QName(str, paramJavaMethodImpl.getOperationName());
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    splitParameters(arrayList1, arrayList2, paramJavaMethodImpl.getRequestParameters());
    SOAPBinding sOAPBinding = paramJavaMethodImpl.getBinding();
    bindingOperationType.soapOperation().soapAction(sOAPBinding.getSOAPAction());
    StartWithExtensionsType startWithExtensionsType = bindingOperationType.input();
    this.extension.addBindingOperationInputExtension(startWithExtensionsType, paramJavaMethodImpl);
    BodyType bodyType = (BodyType)startWithExtensionsType._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.Body.class);
    boolean bool = sOAPBinding.getStyle().equals(SOAPBinding.Style.RPC);
    if (sOAPBinding.getUse() == SOAPBinding.Use.LITERAL) {
      bodyType.use("literal");
      if (arrayList2.size() > 0) {
        if (arrayList1.size() > 0) {
          ParameterImpl parameterImpl = (ParameterImpl)arrayList1.iterator().next();
          if (bool) {
            StringBuilder stringBuilder = new StringBuilder();
            byte b = 0;
            for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
              if (b++ > 0)
                stringBuilder.append(' '); 
              stringBuilder.append(parameterImpl1.getPartName());
            } 
            bodyType.parts(stringBuilder.toString());
          } else {
            bodyType.parts(parameterImpl.getPartName());
          } 
        } else {
          bodyType.parts("");
        } 
        generateSOAPHeaders(startWithExtensionsType, arrayList2, qName);
      } 
      if (bool)
        bodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI()); 
    } else {
      throw new WebServiceException("encoded use is not supported");
    } 
    if (paramJavaMethodImpl.getMEP() != MEP.ONE_WAY) {
      arrayList1.clear();
      arrayList2.clear();
      splitParameters(arrayList1, arrayList2, paramJavaMethodImpl.getResponseParameters());
      StartWithExtensionsType startWithExtensionsType1 = bindingOperationType.output();
      this.extension.addBindingOperationOutputExtension(startWithExtensionsType1, paramJavaMethodImpl);
      bodyType = (BodyType)startWithExtensionsType1._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.Body.class);
      bodyType.use("literal");
      if (arrayList2.size() > 0) {
        StringBuilder stringBuilder = new StringBuilder();
        if (arrayList1.size() > 0) {
          ParameterImpl parameterImpl = arrayList1.iterator().hasNext() ? (ParameterImpl)arrayList1.iterator().next() : null;
          if (parameterImpl != null)
            if (bool) {
              byte b = 0;
              for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
                if (b++ > 0)
                  stringBuilder.append(" "); 
                stringBuilder.append(parameterImpl1.getPartName());
              } 
            } else {
              stringBuilder = new StringBuilder(parameterImpl.getPartName());
            }  
        } 
        bodyType.parts(stringBuilder.toString());
        QName qName1 = new QName(str, paramJavaMethodImpl.getResponseMessageName());
        generateSOAPHeaders(startWithExtensionsType1, arrayList2, qName1);
      } 
      if (bool)
        bodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI()); 
    } 
    for (CheckedExceptionImpl checkedExceptionImpl : paramJavaMethodImpl.getCheckedExceptions()) {
      Fault fault = bindingOperationType.fault().name(checkedExceptionImpl.getMessageName());
      this.extension.addBindingOperationFaultExtension(fault, paramJavaMethodImpl, checkedExceptionImpl);
      SOAPFault sOAPFault = ((SOAPFault)fault._element(SOAPFault.class)).name(checkedExceptionImpl.getMessageName());
      sOAPFault.use("literal");
    } 
  }
  
  protected void generateSOAP12BindingOperation(JavaMethodImpl paramJavaMethodImpl, Binding paramBinding) {
    BindingOperationType bindingOperationType = paramBinding.operation().name(paramJavaMethodImpl.getOperationName());
    this.extension.addBindingOperationExtension(bindingOperationType, paramJavaMethodImpl);
    String str1 = this.model.getTargetNamespace();
    QName qName = new QName(str1, paramJavaMethodImpl.getOperationName());
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    splitParameters(arrayList1, arrayList2, paramJavaMethodImpl.getRequestParameters());
    SOAPBinding sOAPBinding = paramJavaMethodImpl.getBinding();
    String str2 = sOAPBinding.getSOAPAction();
    if (str2 != null)
      bindingOperationType.soap12Operation().soapAction(str2); 
    StartWithExtensionsType startWithExtensionsType = bindingOperationType.input();
    this.extension.addBindingOperationInputExtension(startWithExtensionsType, paramJavaMethodImpl);
    BodyType bodyType = (BodyType)startWithExtensionsType._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
    boolean bool = sOAPBinding.getStyle().equals(SOAPBinding.Style.RPC);
    if (sOAPBinding.getUse().equals(SOAPBinding.Use.LITERAL)) {
      bodyType.use("literal");
      if (arrayList2.size() > 0) {
        if (arrayList1.size() > 0) {
          ParameterImpl parameterImpl = (ParameterImpl)arrayList1.iterator().next();
          if (bool) {
            StringBuilder stringBuilder = new StringBuilder();
            byte b = 0;
            for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
              if (b++ > 0)
                stringBuilder.append(' '); 
              stringBuilder.append(parameterImpl1.getPartName());
            } 
            bodyType.parts(stringBuilder.toString());
          } else {
            bodyType.parts(parameterImpl.getPartName());
          } 
        } else {
          bodyType.parts("");
        } 
        generateSOAP12Headers(startWithExtensionsType, arrayList2, qName);
      } 
      if (bool)
        bodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI()); 
    } else {
      throw new WebServiceException("encoded use is not supported");
    } 
    if (paramJavaMethodImpl.getMEP() != MEP.ONE_WAY) {
      arrayList1.clear();
      arrayList2.clear();
      splitParameters(arrayList1, arrayList2, paramJavaMethodImpl.getResponseParameters());
      StartWithExtensionsType startWithExtensionsType1 = bindingOperationType.output();
      this.extension.addBindingOperationOutputExtension(startWithExtensionsType1, paramJavaMethodImpl);
      bodyType = (BodyType)startWithExtensionsType1._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
      bodyType.use("literal");
      if (arrayList2.size() > 0) {
        if (arrayList1.size() > 0) {
          ParameterImpl parameterImpl = (ParameterImpl)arrayList1.iterator().next();
          if (bool) {
            StringBuilder stringBuilder = new StringBuilder();
            byte b = 0;
            for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
              if (b++ > 0)
                stringBuilder.append(" "); 
              stringBuilder.append(parameterImpl1.getPartName());
            } 
            bodyType.parts(stringBuilder.toString());
          } else {
            bodyType.parts(parameterImpl.getPartName());
          } 
        } else {
          bodyType.parts("");
        } 
        QName qName1 = new QName(str1, paramJavaMethodImpl.getResponseMessageName());
        generateSOAP12Headers(startWithExtensionsType1, arrayList2, qName1);
      } 
      if (bool)
        bodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI()); 
    } 
    for (CheckedExceptionImpl checkedExceptionImpl : paramJavaMethodImpl.getCheckedExceptions()) {
      Fault fault = bindingOperationType.fault().name(checkedExceptionImpl.getMessageName());
      this.extension.addBindingOperationFaultExtension(fault, paramJavaMethodImpl, checkedExceptionImpl);
      SOAPFault sOAPFault = ((SOAPFault)fault._element(SOAPFault.class)).name(checkedExceptionImpl.getMessageName());
      sOAPFault.use("literal");
    } 
  }
  
  protected void splitParameters(List<ParameterImpl> paramList1, List<ParameterImpl> paramList2, List<ParameterImpl> paramList3) {
    for (ParameterImpl parameterImpl : paramList3) {
      if (isBodyParameter(parameterImpl)) {
        paramList1.add(parameterImpl);
        continue;
      } 
      paramList2.add(parameterImpl);
    } 
  }
  
  protected void generateSOAPHeaders(TypedXmlWriter paramTypedXmlWriter, List<ParameterImpl> paramList, QName paramQName) {
    for (ParameterImpl parameterImpl : paramList) {
      Header header = (Header)paramTypedXmlWriter._element(Header.class);
      header.message(paramQName);
      header.part(parameterImpl.getPartName());
      header.use("literal");
    } 
  }
  
  protected void generateSOAP12Headers(TypedXmlWriter paramTypedXmlWriter, List<ParameterImpl> paramList, QName paramQName) {
    for (ParameterImpl parameterImpl : paramList) {
      Header header = (Header)paramTypedXmlWriter._element(Header.class);
      header.message(paramQName);
      header.part(parameterImpl.getPartName());
      header.use("literal");
    } 
  }
  
  protected void generateService() {
    QName qName1 = this.model.getPortName();
    QName qName2 = this.model.getServiceQName();
    Service service = this.serviceDefinitions.service().name(qName2.getLocalPart());
    this.extension.addServiceExtension(service);
    Port port = service.port().name(qName1.getLocalPart());
    port.binding(this.model.getBoundPortTypeName());
    this.extension.addPortExtension(port);
    if (this.model.getJavaMethods().isEmpty())
      return; 
    if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
      SOAPAddress sOAPAddress = (SOAPAddress)port._element(SOAPAddress.class);
      sOAPAddress.location(this.endpointAddress);
    } else {
      SOAPAddress sOAPAddress = (SOAPAddress)port._element(SOAPAddress.class);
      sOAPAddress.location(this.endpointAddress);
    } 
  }
  
  protected void generateInputMessage(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl) {
    ParamType paramType = paramOperation.input();
    this.extension.addOperationInputExtension(paramType, paramJavaMethodImpl);
    paramType.message(new QName(this.model.getTargetNamespace(), paramJavaMethodImpl.getRequestMessageName()));
  }
  
  protected void generateOutputMessage(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl) {
    ParamType paramType = paramOperation.output();
    this.extension.addOperationOutputExtension(paramType, paramJavaMethodImpl);
    paramType.message(new QName(this.model.getTargetNamespace(), paramJavaMethodImpl.getResponseMessageName()));
  }
  
  public Result createOutputFile(String paramString1, String paramString2) throws IOException {
    String str;
    if (paramString1 == null)
      return null; 
    Holder holder = new Holder();
    holder.value = this.schemaPrefix + paramString2;
    Result result = this.wsdlResolver.getSchemaOutput(paramString1, holder);
    if (result == null) {
      str = (String)holder.value;
    } else {
      str = relativize(result.getSystemId(), this.wsdlLocation);
    } 
    boolean bool = paramString1.trim().equals("");
    if (!bool) {
      Import import = this.types.schema()._import();
      import.namespace(paramString1);
      import.schemaLocation(str);
    } 
    return result;
  }
  
  private Result createInlineSchema(String paramString1, String paramString2) throws IOException {
    if (paramString1.equals(""))
      return null; 
    TXWResult tXWResult = new TXWResult(this.types);
    tXWResult.setSystemId("");
    return tXWResult;
  }
  
  protected static String relativize(String paramString1, String paramString2) {
    try {
      assert paramString1 != null;
      if (paramString2 == null)
        return paramString1; 
      URI uRI1 = new URI(Util.escapeURI(paramString1));
      URI uRI2 = new URI(Util.escapeURI(paramString2));
      if (uRI1.isOpaque() || uRI2.isOpaque())
        return paramString1; 
      if (!Util.equalsIgnoreCase(uRI1.getScheme(), uRI2.getScheme()) || !Util.equal(uRI1.getAuthority(), uRI2.getAuthority()))
        return paramString1; 
      String str1 = uRI1.getPath();
      String str2 = uRI2.getPath();
      if (!str2.endsWith("/"))
        str2 = Util.normalizeUriPath(str2); 
      if (str1.equals(str2))
        return "."; 
      String str3 = calculateRelativePath(str1, str2);
      if (str3 == null)
        return paramString1; 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(str3);
      if (uRI1.getQuery() != null)
        stringBuilder.append('?').append(uRI1.getQuery()); 
      if (uRI1.getFragment() != null)
        stringBuilder.append('#').append(uRI1.getFragment()); 
      return stringBuilder.toString();
    } catch (URISyntaxException uRISyntaxException) {
      throw new InternalError("Error escaping one of these uris:\n\t" + paramString1 + "\n\t" + paramString2);
    } 
  }
  
  private static String calculateRelativePath(String paramString1, String paramString2) { return (paramString2 == null) ? null : (paramString1.startsWith(paramString2) ? paramString1.substring(paramString2.length()) : ("../" + calculateRelativePath(paramString1, Util.getParentUriPath(paramString2)))); }
  
  private void register(WSDLGeneratorExtension paramWSDLGeneratorExtension) { this.extensionHandlers.add(paramWSDLGeneratorExtension); }
  
  private static class CommentFilter implements XmlSerializer {
    final XmlSerializer serializer;
    
    private static final String VERSION_COMMENT = " Generated by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
    
    CommentFilter(XmlSerializer param1XmlSerializer) { this.serializer = param1XmlSerializer; }
    
    public void startDocument() {
      this.serializer.startDocument();
      comment(new StringBuilder(VERSION_COMMENT));
      text(new StringBuilder("\n"));
    }
    
    public void beginStartTag(String param1String1, String param1String2, String param1String3) { this.serializer.beginStartTag(param1String1, param1String2, param1String3); }
    
    public void writeAttribute(String param1String1, String param1String2, String param1String3, StringBuilder param1StringBuilder) { this.serializer.writeAttribute(param1String1, param1String2, param1String3, param1StringBuilder); }
    
    public void writeXmlns(String param1String1, String param1String2) { this.serializer.writeXmlns(param1String1, param1String2); }
    
    public void endStartTag(String param1String1, String param1String2, String param1String3) { this.serializer.endStartTag(param1String1, param1String2, param1String3); }
    
    public void endTag() { this.serializer.endTag(); }
    
    public void text(StringBuilder param1StringBuilder) { this.serializer.text(param1StringBuilder); }
    
    public void cdata(StringBuilder param1StringBuilder) { this.serializer.cdata(param1StringBuilder); }
    
    public void comment(StringBuilder param1StringBuilder) { this.serializer.comment(param1StringBuilder); }
    
    public void endDocument() { this.serializer.endDocument(); }
    
    public void flush() { this.serializer.flush(); }
  }
  
  protected class JAXWSOutputSchemaResolver extends SchemaOutputResolver {
    ArrayList<DOMResult> nonGlassfishSchemas = null;
    
    public Result createOutput(String param1String1, String param1String2) throws IOException { return WSDLGenerator.this.inlineSchemas ? ((this.nonGlassfishSchemas != null) ? nonGlassfishSchemaResult(param1String1, param1String2) : WSDLGenerator.this.createInlineSchema(param1String1, param1String2)) : WSDLGenerator.this.createOutputFile(param1String1, param1String2); }
    
    private Result nonGlassfishSchemaResult(String param1String1, String param1String2) throws IOException {
      DOMResult dOMResult = new DOMResult();
      dOMResult.setSystemId("");
      this.nonGlassfishSchemas.add(dOMResult);
      return dOMResult;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\WSDLGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */