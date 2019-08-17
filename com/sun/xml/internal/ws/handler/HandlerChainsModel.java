package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.UtilException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.PortInfo;

public class HandlerChainsModel {
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.util");
  
  private Class annotatedClass;
  
  private List<HandlerChainType> handlerChains;
  
  private String id;
  
  public static final String PROTOCOL_SOAP11_TOKEN = "##SOAP11_HTTP";
  
  public static final String PROTOCOL_SOAP12_TOKEN = "##SOAP12_HTTP";
  
  public static final String PROTOCOL_XML_TOKEN = "##XML_HTTP";
  
  public static final String NS_109 = "http://java.sun.com/xml/ns/javaee";
  
  public static final QName QNAME_CHAIN_PORT_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "port-name-pattern");
  
  public static final QName QNAME_CHAIN_PROTOCOL_BINDING = new QName("http://java.sun.com/xml/ns/javaee", "protocol-bindings");
  
  public static final QName QNAME_CHAIN_SERVICE_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "service-name-pattern");
  
  public static final QName QNAME_HANDLER_CHAIN = new QName("http://java.sun.com/xml/ns/javaee", "handler-chain");
  
  public static final QName QNAME_HANDLER_CHAINS = new QName("http://java.sun.com/xml/ns/javaee", "handler-chains");
  
  public static final QName QNAME_HANDLER = new QName("http://java.sun.com/xml/ns/javaee", "handler");
  
  public static final QName QNAME_HANDLER_NAME = new QName("http://java.sun.com/xml/ns/javaee", "handler-name");
  
  public static final QName QNAME_HANDLER_CLASS = new QName("http://java.sun.com/xml/ns/javaee", "handler-class");
  
  public static final QName QNAME_HANDLER_PARAM = new QName("http://java.sun.com/xml/ns/javaee", "init-param");
  
  public static final QName QNAME_HANDLER_PARAM_NAME = new QName("http://java.sun.com/xml/ns/javaee", "param-name");
  
  public static final QName QNAME_HANDLER_PARAM_VALUE = new QName("http://java.sun.com/xml/ns/javaee", "param-value");
  
  public static final QName QNAME_HANDLER_HEADER = new QName("http://java.sun.com/xml/ns/javaee", "soap-header");
  
  public static final QName QNAME_HANDLER_ROLE = new QName("http://java.sun.com/xml/ns/javaee", "soap-role");
  
  private HandlerChainsModel(Class paramClass) { this.annotatedClass = paramClass; }
  
  private List<HandlerChainType> getHandlerChain() {
    if (this.handlerChains == null)
      this.handlerChains = new ArrayList(); 
    return this.handlerChains;
  }
  
  public String getId() { return this.id; }
  
  public void setId(String paramString) { this.id = paramString; }
  
  public static HandlerChainsModel parseHandlerConfigFile(Class paramClass, XMLStreamReader paramXMLStreamReader) {
    ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAINS);
    HandlerChainsModel handlerChainsModel = new HandlerChainsModel(paramClass);
    List list = handlerChainsModel.getHandlerChain();
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_CHAIN)) {
      HandlerChainType handlerChainType = new HandlerChainType();
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PORT_PATTERN)) {
        QName qName = XMLStreamReaderUtil.getElementQName(paramXMLStreamReader);
        handlerChainType.setPortNamePattern(qName);
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      } else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING)) {
        String str = XMLStreamReaderUtil.getElementText(paramXMLStreamReader);
        StringTokenizer stringTokenizer = new StringTokenizer(str);
        while (stringTokenizer.hasMoreTokens()) {
          String str1 = stringTokenizer.nextToken();
          handlerChainType.addProtocolBinding(str1);
        } 
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      } else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN)) {
        QName qName = XMLStreamReaderUtil.getElementQName(paramXMLStreamReader);
        handlerChainType.setServiceNamePattern(qName);
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      } 
      List list1 = handlerChainType.getHandlers();
      while (paramXMLStreamReader.getName().equals(QNAME_HANDLER)) {
        HandlerType handlerType = new HandlerType();
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        if (paramXMLStreamReader.getName().equals(QNAME_HANDLER_NAME)) {
          String str1 = XMLStreamReaderUtil.getElementText(paramXMLStreamReader).trim();
          handlerType.setHandlerName(str1);
          XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        } 
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CLASS);
        String str = XMLStreamReaderUtil.getElementText(paramXMLStreamReader).trim();
        handlerType.setHandlerClass(str);
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_PARAM))
          skipInitParamElement(paramXMLStreamReader); 
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_HEADER))
          skipTextElement(paramXMLStreamReader); 
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_ROLE)) {
          List list2 = handlerType.getSoapRoles();
          list2.add(XMLStreamReaderUtil.getElementText(paramXMLStreamReader));
          XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        } 
        list1.add(handlerType);
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER);
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
      } 
      ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAIN);
      list.add(handlerChainType);
      XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    } 
    return handlerChainsModel;
  }
  
  public static HandlerAnnotationInfo parseHandlerFile(XMLStreamReader paramXMLStreamReader, ClassLoader paramClassLoader, QName paramQName1, QName paramQName2, WSBinding paramWSBinding) {
    ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAINS);
    String str = paramWSBinding.getBindingId().toString();
    HandlerAnnotationInfo handlerAnnotationInfo = new HandlerAnnotationInfo();
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    ArrayList arrayList = new ArrayList();
    HashSet hashSet = new HashSet();
    while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_CHAIN)) {
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PORT_PATTERN)) {
        if (paramQName2 == null)
          logger.warning("handler chain sepcified for port but port QName passed to parser is null"); 
        boolean bool = JAXWSUtils.matchQNames(paramQName2, XMLStreamReaderUtil.getElementQName(paramXMLStreamReader));
        if (!bool) {
          skipChain(paramXMLStreamReader);
          continue;
        } 
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      } else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING)) {
        if (str == null)
          logger.warning("handler chain sepcified for bindingId but bindingId passed to parser is null"); 
        String str1 = XMLStreamReaderUtil.getElementText(paramXMLStreamReader);
        boolean bool = true;
        StringTokenizer stringTokenizer = new StringTokenizer(str1);
        ArrayList arrayList1 = new ArrayList();
        while (stringTokenizer.hasMoreTokens()) {
          String str2 = stringTokenizer.nextToken();
          str2 = DeploymentDescriptorParser.getBindingIdForToken(str2);
          String str3 = BindingID.parse(str2).toString();
          arrayList1.add(str3);
        } 
        if (arrayList1.contains(str))
          bool = false; 
        if (bool) {
          skipChain(paramXMLStreamReader);
          continue;
        } 
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      } else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN)) {
        if (paramQName1 == null)
          logger.warning("handler chain sepcified for service but service QName passed to parser is null"); 
        boolean bool = JAXWSUtils.matchQNames(paramQName1, XMLStreamReaderUtil.getElementQName(paramXMLStreamReader));
        if (!bool) {
          skipChain(paramXMLStreamReader);
          continue;
        } 
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      } 
      while (paramXMLStreamReader.getName().equals(QNAME_HANDLER)) {
        Handler handler;
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        if (paramXMLStreamReader.getName().equals(QNAME_HANDLER_NAME))
          skipTextElement(paramXMLStreamReader); 
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CLASS);
        try {
          handler = (Handler)loadClass(paramClassLoader, XMLStreamReaderUtil.getElementText(paramXMLStreamReader).trim()).newInstance();
        } catch (InstantiationException instantiationException) {
          throw new RuntimeException(instantiationException);
        } catch (IllegalAccessException illegalAccessException) {
          throw new RuntimeException(illegalAccessException);
        } 
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_PARAM))
          skipInitParamElement(paramXMLStreamReader); 
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_HEADER))
          skipTextElement(paramXMLStreamReader); 
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_ROLE)) {
          hashSet.add(XMLStreamReaderUtil.getElementText(paramXMLStreamReader));
          XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        } 
        Method[] arrayOfMethod = handler.getClass().getMethods();
        int i = arrayOfMethod.length;
        byte b = 0;
        while (b < i) {
          Method method = arrayOfMethod[b];
          if (method.getAnnotation(javax.annotation.PostConstruct.class) == null) {
            b++;
            continue;
          } 
          try {
            method.invoke(handler, new Object[0]);
            break;
          } catch (Exception exception) {
            throw new RuntimeException(exception);
          } 
        } 
        arrayList.add(handler);
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER);
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
      } 
      ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAIN);
      XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    } 
    handlerAnnotationInfo.setHandlers(arrayList);
    handlerAnnotationInfo.setRoles(hashSet);
    return handlerAnnotationInfo;
  }
  
  public HandlerAnnotationInfo getHandlersForPortInfo(PortInfo paramPortInfo) {
    HandlerAnnotationInfo handlerAnnotationInfo = new HandlerAnnotationInfo();
    ArrayList arrayList = new ArrayList();
    HashSet hashSet = new HashSet();
    for (HandlerChainType handlerChainType : this.handlerChains) {
      boolean bool = false;
      if (!handlerChainType.isConstraintSet() || JAXWSUtils.matchQNames(paramPortInfo.getServiceName(), handlerChainType.getServiceNamePattern()) || JAXWSUtils.matchQNames(paramPortInfo.getPortName(), handlerChainType.getPortNamePattern()) || handlerChainType.getProtocolBindings().contains(paramPortInfo.getBindingID()))
        bool = true; 
      if (bool)
        for (HandlerType handlerType : handlerChainType.getHandlers()) {
          try {
            Handler handler = (Handler)loadClass(this.annotatedClass.getClassLoader(), handlerType.getHandlerClass()).newInstance();
            callHandlerPostConstruct(handler);
            arrayList.add(handler);
          } catch (InstantiationException instantiationException) {
            throw new RuntimeException(instantiationException);
          } catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
          } 
          hashSet.addAll(handlerType.getSoapRoles());
        }  
    } 
    handlerAnnotationInfo.setHandlers(arrayList);
    handlerAnnotationInfo.setRoles(hashSet);
    return handlerAnnotationInfo;
  }
  
  private static Class loadClass(ClassLoader paramClassLoader, String paramString) {
    try {
      return Class.forName(paramString, true, paramClassLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new UtilException("util.handler.class.not.found", new Object[] { paramString });
    } 
  }
  
  private static void callHandlerPostConstruct(Object paramObject) {
    Method[] arrayOfMethod = paramObject.getClass().getMethods();
    int i = arrayOfMethod.length;
    byte b = 0;
    while (b < i) {
      Method method = arrayOfMethod[b];
      if (method.getAnnotation(javax.annotation.PostConstruct.class) == null) {
        b++;
        continue;
      } 
      try {
        method.invoke(paramObject, new Object[0]);
        break;
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
    } 
  }
  
  private static void skipChain(XMLStreamReader paramXMLStreamReader) {
    while (XMLStreamReaderUtil.nextContent(paramXMLStreamReader) != 2 || !paramXMLStreamReader.getName().equals(QNAME_HANDLER_CHAIN));
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
  }
  
  private static void skipTextElement(XMLStreamReader paramXMLStreamReader) {
    XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
  }
  
  private static void skipInitParamElement(XMLStreamReader paramXMLStreamReader) {
    int i;
    do {
      i = XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    } while (i != 2 || !paramXMLStreamReader.getName().equals(QNAME_HANDLER_PARAM));
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
  }
  
  private static void ensureProperName(XMLStreamReader paramXMLStreamReader, QName paramQName) {
    if (!paramXMLStreamReader.getName().equals(paramQName))
      failWithLocalName("util.parser.wrong.element", paramXMLStreamReader, paramQName.getLocalPart()); 
  }
  
  static void ensureProperName(XMLStreamReader paramXMLStreamReader, String paramString) {
    if (!paramXMLStreamReader.getLocalName().equals(paramString))
      failWithLocalName("util.parser.wrong.element", paramXMLStreamReader, paramString); 
  }
  
  private static void failWithLocalName(String paramString1, XMLStreamReader paramXMLStreamReader, String paramString2) { throw new UtilException(paramString1, new Object[] { Integer.toString(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getLocalName(), paramString2 }); }
  
  static class HandlerChainType {
    QName serviceNamePattern;
    
    QName portNamePattern;
    
    List<String> protocolBindings = new ArrayList();
    
    boolean constraintSet = false;
    
    List<HandlerChainsModel.HandlerType> handlers;
    
    String id;
    
    public void setServiceNamePattern(QName param1QName) {
      this.serviceNamePattern = param1QName;
      this.constraintSet = true;
    }
    
    public QName getServiceNamePattern() { return this.serviceNamePattern; }
    
    public void setPortNamePattern(QName param1QName) {
      this.portNamePattern = param1QName;
      this.constraintSet = true;
    }
    
    public QName getPortNamePattern() { return this.portNamePattern; }
    
    public List<String> getProtocolBindings() { return this.protocolBindings; }
    
    public void addProtocolBinding(String param1String) {
      param1String = DeploymentDescriptorParser.getBindingIdForToken(param1String);
      String str = BindingID.parse(param1String).toString();
      this.protocolBindings.add(str);
      this.constraintSet = true;
    }
    
    public boolean isConstraintSet() { return (this.constraintSet || !this.protocolBindings.isEmpty()); }
    
    public String getId() { return this.id; }
    
    public void setId(String param1String) { this.id = param1String; }
    
    public List<HandlerChainsModel.HandlerType> getHandlers() {
      if (this.handlers == null)
        this.handlers = new ArrayList(); 
      return this.handlers;
    }
  }
  
  static class HandlerType {
    String handlerName;
    
    String handlerClass;
    
    List<String> soapRoles;
    
    String id;
    
    public String getHandlerName() { return this.handlerName; }
    
    public void setHandlerName(String param1String) { this.handlerName = param1String; }
    
    public String getHandlerClass() { return this.handlerClass; }
    
    public void setHandlerClass(String param1String) { this.handlerClass = param1String; }
    
    public String getId() { return this.id; }
    
    public void setId(String param1String) { this.id = param1String; }
    
    public List<String> getSoapRoles() {
      if (this.soapRoles == null)
        this.soapRoles = new ArrayList(); 
      return this.soapRoles;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\HandlerChainsModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */