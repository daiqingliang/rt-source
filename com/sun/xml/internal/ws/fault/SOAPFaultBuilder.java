package com.sun.xml.internal.ws.fault;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import com.sun.xml.internal.ws.message.FaultMessage;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class SOAPFaultBuilder {
  private static final JAXBContext JAXB_CONTEXT;
  
  private static final Logger logger = Logger.getLogger(SOAPFaultBuilder.class.getName());
  
  public static final boolean captureStackTrace;
  
  static final String CAPTURE_STACK_TRACE_PROPERTY = SOAPFaultBuilder.class.getName() + ".captureStackTrace";
  
  abstract DetailType getDetail();
  
  abstract void setDetail(DetailType paramDetailType);
  
  @XmlTransient
  @Nullable
  public QName getFirstDetailEntryName() {
    DetailType detailType = getDetail();
    if (detailType != null) {
      Node node = detailType.getDetail(0);
      if (node != null)
        return new QName(node.getNamespaceURI(), node.getLocalName()); 
    } 
    return null;
  }
  
  abstract String getFaultString();
  
  public Throwable createException(Map<QName, CheckedExceptionImpl> paramMap) throws JAXBException {
    DetailType detailType = getDetail();
    Node node = null;
    if (detailType != null)
      node = detailType.getDetail(0); 
    if (node == null || paramMap == null)
      return attachServerException(getProtocolException()); 
    QName qName = new QName(node.getNamespaceURI(), node.getLocalName());
    CheckedExceptionImpl checkedExceptionImpl = (CheckedExceptionImpl)paramMap.get(qName);
    if (checkedExceptionImpl == null)
      return attachServerException(getProtocolException()); 
    if (checkedExceptionImpl.getExceptionType().equals(ExceptionType.UserDefined))
      return attachServerException(createUserDefinedException(checkedExceptionImpl)); 
    Class clazz = checkedExceptionImpl.getExceptionClass();
    try {
      Constructor constructor = clazz.getConstructor(new Class[] { String.class, (Class)(checkedExceptionImpl.getDetailType()).type });
      Exception exception = (Exception)constructor.newInstance(new Object[] { getFaultString(), getJAXBObject(node, checkedExceptionImpl) });
      return attachServerException(exception);
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  @NotNull
  public static Message createSOAPFaultMessage(@NotNull SOAPVersion paramSOAPVersion, @NotNull ProtocolException paramProtocolException, @Nullable QName paramQName) {
    Object object = getFaultDetail(null, paramProtocolException);
    return (paramSOAPVersion == SOAPVersion.SOAP_12) ? createSOAP12Fault(paramSOAPVersion, paramProtocolException, object, null, paramQName) : createSOAP11Fault(paramSOAPVersion, paramProtocolException, object, null, paramQName);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, CheckedExceptionImpl paramCheckedExceptionImpl, Throwable paramThrowable) {
    Throwable throwable = (paramThrowable instanceof InvocationTargetException) ? ((InvocationTargetException)paramThrowable).getTargetException() : paramThrowable;
    return createSOAPFaultMessage(paramSOAPVersion, paramCheckedExceptionImpl, throwable, null);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, CheckedExceptionImpl paramCheckedExceptionImpl, Throwable paramThrowable, QName paramQName) {
    Object object = getFaultDetail(paramCheckedExceptionImpl, paramThrowable);
    return (paramSOAPVersion == SOAPVersion.SOAP_12) ? createSOAP12Fault(paramSOAPVersion, paramThrowable, object, paramCheckedExceptionImpl, paramQName) : createSOAP11Fault(paramSOAPVersion, paramThrowable, object, paramCheckedExceptionImpl, paramQName);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, String paramString, QName paramQName) {
    if (paramQName == null)
      paramQName = getDefaultFaultCode(paramSOAPVersion); 
    return createSOAPFaultMessage(paramSOAPVersion, paramString, paramQName, null);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, SOAPFault paramSOAPFault) {
    switch (paramSOAPVersion) {
      case SOAP_11:
        return JAXBMessage.create(JAXB_CONTEXT, new SOAP11Fault(paramSOAPFault), paramSOAPVersion);
      case SOAP_12:
        return JAXBMessage.create(JAXB_CONTEXT, new SOAP12Fault(paramSOAPFault), paramSOAPVersion);
    } 
    throw new AssertionError();
  }
  
  private static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, String paramString, QName paramQName, Element paramElement) {
    switch (paramSOAPVersion) {
      case SOAP_11:
        return JAXBMessage.create(JAXB_CONTEXT, new SOAP11Fault(paramQName, paramString, null, paramElement), paramSOAPVersion);
      case SOAP_12:
        return JAXBMessage.create(JAXB_CONTEXT, new SOAP12Fault(paramQName, paramString, paramElement), paramSOAPVersion);
    } 
    throw new AssertionError();
  }
  
  final void captureStackTrace(@Nullable Throwable paramThrowable) {
    if (paramThrowable == null)
      return; 
    if (!captureStackTrace)
      return; 
    try {
      Document document = DOMUtil.createDom();
      ExceptionBean.marshal(paramThrowable, document);
      DetailType detailType = getDetail();
      if (detailType == null)
        setDetail(detailType = new DetailType()); 
      detailType.getDetails().add(document.getDocumentElement());
    } catch (JAXBException jAXBException) {
      logger.log(Level.WARNING, "Unable to capture the stack trace into XML", jAXBException);
    } 
  }
  
  private <T extends Throwable> T attachServerException(T paramT) {
    DetailType detailType = getDetail();
    if (detailType == null)
      return paramT; 
    for (Element element : detailType.getDetails()) {
      if (ExceptionBean.isStackTraceXml(element)) {
        try {
          paramT.initCause(ExceptionBean.unmarshal(element));
        } catch (JAXBException jAXBException) {
          logger.log(Level.WARNING, "Unable to read the capture stack trace in the fault", jAXBException);
        } 
        return paramT;
      } 
    } 
    return paramT;
  }
  
  protected abstract Throwable getProtocolException();
  
  private Object getJAXBObject(Node paramNode, CheckedExceptionImpl paramCheckedExceptionImpl) throws JAXBException {
    XMLBridge xMLBridge = paramCheckedExceptionImpl.getBond();
    return xMLBridge.unmarshal(paramNode, null);
  }
  
  private Exception createUserDefinedException(CheckedExceptionImpl paramCheckedExceptionImpl) {
    Class clazz1 = paramCheckedExceptionImpl.getExceptionClass();
    Class clazz2 = paramCheckedExceptionImpl.getDetailBean();
    try {
      Node node = (Node)getDetail().getDetails().get(0);
      Object object = getJAXBObject(node, paramCheckedExceptionImpl);
      try {
        Constructor constructor = clazz1.getConstructor(new Class[] { String.class, clazz2 });
        return (Exception)constructor.newInstance(new Object[] { getFaultString(), object });
      } catch (NoSuchMethodException noSuchMethodException) {
        Constructor constructor = clazz1.getConstructor(new Class[] { String.class });
        return (Exception)constructor.newInstance(new Object[] { getFaultString() });
      } 
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  private static String getWriteMethod(Field paramField) { return "set" + StringUtils.capitalize(paramField.getName()); }
  
  private static Object getFaultDetail(CheckedExceptionImpl paramCheckedExceptionImpl, Throwable paramThrowable) {
    if (paramCheckedExceptionImpl == null)
      return null; 
    if (paramCheckedExceptionImpl.getExceptionType().equals(ExceptionType.UserDefined))
      return createDetailFromUserDefinedException(paramCheckedExceptionImpl, paramThrowable); 
    try {
      Method method = paramThrowable.getClass().getMethod("getFaultInfo", new Class[0]);
      return method.invoke(paramThrowable, new Object[0]);
    } catch (Exception exception) {
      throw new SerializationException(exception);
    } 
  }
  
  private static Object createDetailFromUserDefinedException(CheckedExceptionImpl paramCheckedExceptionImpl, Object paramObject) {
    Class clazz = paramCheckedExceptionImpl.getDetailBean();
    Field[] arrayOfField = clazz.getDeclaredFields();
    try {
      Object object = clazz.newInstance();
      for (Field field : arrayOfField) {
        Method method = paramObject.getClass().getMethod(getReadMethod(field), new Class[0]);
        try {
          Method method1 = clazz.getMethod(getWriteMethod(field), new Class[] { method.getReturnType() });
          method1.invoke(object, new Object[] { method.invoke(paramObject, new Object[0]) });
        } catch (NoSuchMethodException noSuchMethodException) {
          Field field1 = clazz.getField(field.getName());
          field1.set(object, method.invoke(paramObject, new Object[0]));
        } 
      } 
      return object;
    } catch (Exception exception) {
      throw new SerializationException(exception);
    } 
  }
  
  private static String getReadMethod(Field paramField) { return paramField.getType().isAssignableFrom(boolean.class) ? ("is" + StringUtils.capitalize(paramField.getName())) : ("get" + StringUtils.capitalize(paramField.getName())); }
  
  private static Message createSOAP11Fault(SOAPVersion paramSOAPVersion, Throwable paramThrowable, Object paramObject, CheckedExceptionImpl paramCheckedExceptionImpl, QName paramQName) {
    SOAPFaultException sOAPFaultException = null;
    String str1 = null;
    String str2 = null;
    Throwable throwable = paramThrowable.getCause();
    if (paramThrowable instanceof SOAPFaultException) {
      sOAPFaultException = (SOAPFaultException)paramThrowable;
    } else if (throwable != null && throwable instanceof SOAPFaultException) {
      sOAPFaultException = (SOAPFaultException)paramThrowable.getCause();
    } 
    if (sOAPFaultException != null) {
      QName qName1 = sOAPFaultException.getFault().getFaultCodeAsQName();
      if (qName1 != null)
        paramQName = qName1; 
      str1 = sOAPFaultException.getFault().getFaultString();
      str2 = sOAPFaultException.getFault().getFaultActor();
    } 
    if (paramQName == null)
      paramQName = getDefaultFaultCode(paramSOAPVersion); 
    if (str1 == null) {
      str1 = paramThrowable.getMessage();
      if (str1 == null)
        str1 = paramThrowable.toString(); 
    } 
    Element element = null;
    QName qName = null;
    if (paramObject == null && sOAPFaultException != null) {
      element = sOAPFaultException.getFault().getDetail();
      qName = getFirstDetailEntryName((Detail)element);
    } else if (paramCheckedExceptionImpl != null) {
      try {
        DOMResult dOMResult = new DOMResult();
        paramCheckedExceptionImpl.getBond().marshal(paramObject, dOMResult);
        element = (Element)dOMResult.getNode().getFirstChild();
        qName = getFirstDetailEntryName(element);
      } catch (JAXBException jAXBException) {
        str1 = paramThrowable.getMessage();
        paramQName = getDefaultFaultCode(paramSOAPVersion);
      } 
    } 
    SOAP11Fault sOAP11Fault = new SOAP11Fault(paramQName, str1, str2, element);
    if (paramCheckedExceptionImpl == null)
      sOAP11Fault.captureStackTrace(paramThrowable); 
    Message message = JAXBMessage.create(JAXB_CONTEXT, sOAP11Fault, paramSOAPVersion);
    return new FaultMessage(message, qName);
  }
  
  @Nullable
  private static QName getFirstDetailEntryName(@Nullable Detail paramDetail) {
    if (paramDetail != null) {
      Iterator iterator = paramDetail.getDetailEntries();
      if (iterator.hasNext()) {
        DetailEntry detailEntry = (DetailEntry)iterator.next();
        return getFirstDetailEntryName(detailEntry);
      } 
    } 
    return null;
  }
  
  @NotNull
  private static QName getFirstDetailEntryName(@NotNull Element paramElement) { return new QName(paramElement.getNamespaceURI(), paramElement.getLocalName()); }
  
  private static Message createSOAP12Fault(SOAPVersion paramSOAPVersion, Throwable paramThrowable, Object paramObject, CheckedExceptionImpl paramCheckedExceptionImpl, QName paramQName) {
    SOAPFaultException sOAPFaultException = null;
    CodeType codeType = null;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    Throwable throwable = paramThrowable.getCause();
    if (paramThrowable instanceof SOAPFaultException) {
      sOAPFaultException = (SOAPFaultException)paramThrowable;
    } else if (throwable != null && throwable instanceof SOAPFaultException) {
      sOAPFaultException = (SOAPFaultException)paramThrowable.getCause();
    } 
    if (sOAPFaultException != null) {
      SOAPFault sOAPFault = sOAPFaultException.getFault();
      QName qName1 = sOAPFault.getFaultCodeAsQName();
      if (qName1 != null) {
        paramQName = qName1;
        codeType = new CodeType(paramQName);
        Iterator iterator = sOAPFault.getFaultSubcodes();
        boolean bool = true;
        for (SubcodeType subcodeType = null; iterator.hasNext(); subcodeType = fillSubcodes(subcodeType, qName2)) {
          QName qName2 = (QName)iterator.next();
          if (bool) {
            SubcodeType subcodeType1 = new SubcodeType(qName2);
            codeType.setSubcode(subcodeType1);
            subcodeType = subcodeType1;
            bool = false;
            continue;
          } 
        } 
      } 
      str1 = sOAPFaultException.getFault().getFaultString();
      str2 = sOAPFaultException.getFault().getFaultActor();
      str3 = sOAPFaultException.getFault().getFaultNode();
    } 
    if (paramQName == null) {
      paramQName = getDefaultFaultCode(paramSOAPVersion);
      codeType = new CodeType(paramQName);
    } else if (codeType == null) {
      codeType = new CodeType(paramQName);
    } 
    if (str1 == null) {
      str1 = paramThrowable.getMessage();
      if (str1 == null)
        str1 = paramThrowable.toString(); 
    } 
    ReasonType reasonType = new ReasonType(str1);
    Element element = null;
    QName qName = null;
    if (paramObject == null && sOAPFaultException != null) {
      element = sOAPFaultException.getFault().getDetail();
      qName = getFirstDetailEntryName((Detail)element);
    } else if (paramObject != null) {
      try {
        DOMResult dOMResult = new DOMResult();
        paramCheckedExceptionImpl.getBond().marshal(paramObject, dOMResult);
        element = (Element)dOMResult.getNode().getFirstChild();
        qName = getFirstDetailEntryName(element);
      } catch (JAXBException jAXBException) {
        str1 = paramThrowable.getMessage();
      } 
    } 
    SOAP12Fault sOAP12Fault = new SOAP12Fault(codeType, reasonType, str3, str2, element);
    if (paramCheckedExceptionImpl == null)
      sOAP12Fault.captureStackTrace(paramThrowable); 
    Message message = JAXBMessage.create(JAXB_CONTEXT, sOAP12Fault, paramSOAPVersion);
    return new FaultMessage(message, qName);
  }
  
  private static SubcodeType fillSubcodes(SubcodeType paramSubcodeType, QName paramQName) {
    SubcodeType subcodeType = new SubcodeType(paramQName);
    paramSubcodeType.setSubcode(subcodeType);
    return subcodeType;
  }
  
  private static QName getDefaultFaultCode(SOAPVersion paramSOAPVersion) { return paramSOAPVersion.faultCodeServer; }
  
  public static SOAPFaultBuilder create(Message paramMessage) throws JAXBException { return (SOAPFaultBuilder)paramMessage.readPayloadAsJAXB(JAXB_CONTEXT.createUnmarshaller()); }
  
  private static JAXBContext createJAXBContext() {
    if (isJDKRuntime()) {
      Permissions permissions = new Permissions();
      permissions.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.fault"));
      permissions.add(new ReflectPermission("suppressAccessChecks"));
      return (JAXBContext)AccessController.doPrivileged(new PrivilegedAction<JAXBContext>() {
            public JAXBContext run() {
              try {
                return JAXBContext.newInstance(new Class[] { SOAP11Fault.class, SOAP12Fault.class }, );
              } catch (JAXBException jAXBException) {
                throw new Error(jAXBException);
              } 
            }
          },  new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) }));
    } 
    try {
      return JAXBContext.newInstance(new Class[] { SOAP11Fault.class, SOAP12Fault.class });
    } catch (JAXBException jAXBException) {
      throw new Error(jAXBException);
    } 
  }
  
  private static boolean isJDKRuntime() { return SOAPFaultBuilder.class.getName().contains("internal"); }
  
  static  {
    boolean bool = false;
    try {
      bool = Boolean.getBoolean(CAPTURE_STACK_TRACE_PROPERTY);
    } catch (SecurityException securityException) {}
    captureStackTrace = bool;
    JAXB_CONTEXT = createJAXBContext();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\SOAPFaultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */