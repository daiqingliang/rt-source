package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class HandlerAnnotationProcessor {
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.util");
  
  public static HandlerAnnotationInfo buildHandlerInfo(@NotNull Class<?> paramClass, QName paramQName1, QName paramQName2, WSBinding paramWSBinding) {
    MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(paramClass, paramWSBinding);
    if (metadataReader == null)
      metadataReader = new ReflectAnnotationReader(); 
    HandlerChain handlerChain = (HandlerChain)metadataReader.getAnnotation(HandlerChain.class, paramClass);
    if (handlerChain == null) {
      paramClass = getSEI(paramClass, metadataReader);
      if (paramClass != null)
        handlerChain = (HandlerChain)metadataReader.getAnnotation(HandlerChain.class, paramClass); 
      if (handlerChain == null)
        return null; 
    } 
    if (paramClass.getAnnotation(javax.jws.soap.SOAPMessageHandlers.class) != null)
      throw new UtilException("util.handler.cannot.combine.soapmessagehandlers", new Object[0]); 
    InputStream inputStream = getFileAsStream(paramClass, handlerChain);
    XMLStreamReader xMLStreamReader = XMLStreamReaderFactory.create(null, inputStream, true);
    XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
    HandlerAnnotationInfo handlerAnnotationInfo = HandlerChainsModel.parseHandlerFile(xMLStreamReader, paramClass.getClassLoader(), paramQName1, paramQName2, paramWSBinding);
    try {
      xMLStreamReader.close();
      inputStream.close();
    } catch (XMLStreamException xMLStreamException) {
      xMLStreamException.printStackTrace();
      throw new UtilException(xMLStreamException.getMessage(), new Object[0]);
    } catch (IOException iOException) {
      iOException.printStackTrace();
      throw new UtilException(iOException.getMessage(), new Object[0]);
    } 
    return handlerAnnotationInfo;
  }
  
  public static HandlerChainsModel buildHandlerChainsModel(Class<?> paramClass) {
    if (paramClass == null)
      return null; 
    HandlerChain handlerChain = (HandlerChain)paramClass.getAnnotation(HandlerChain.class);
    if (handlerChain == null)
      return null; 
    InputStream inputStream = getFileAsStream(paramClass, handlerChain);
    XMLStreamReader xMLStreamReader = XMLStreamReaderFactory.create(null, inputStream, true);
    XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
    HandlerChainsModel handlerChainsModel = HandlerChainsModel.parseHandlerConfigFile(paramClass, xMLStreamReader);
    try {
      xMLStreamReader.close();
      inputStream.close();
    } catch (XMLStreamException xMLStreamException) {
      xMLStreamException.printStackTrace();
      throw new UtilException(xMLStreamException.getMessage(), new Object[0]);
    } catch (IOException iOException) {
      iOException.printStackTrace();
      throw new UtilException(iOException.getMessage(), new Object[0]);
    } 
    return handlerChainsModel;
  }
  
  static Class getClass(String paramString) {
    try {
      return Thread.currentThread().getContextClassLoader().loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new UtilException("util.handler.class.not.found", new Object[] { paramString });
    } 
  }
  
  static Class getSEI(Class<?> paramClass, MetadataReader paramMetadataReader) {
    if (paramMetadataReader == null)
      paramMetadataReader = new ReflectAnnotationReader(); 
    if (javax.xml.ws.Provider.class.isAssignableFrom(paramClass) || com.sun.xml.internal.ws.api.server.AsyncProvider.class.isAssignableFrom(paramClass))
      return null; 
    if (javax.xml.ws.Service.class.isAssignableFrom(paramClass))
      return null; 
    WebService webService = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
    if (webService == null)
      throw new UtilException("util.handler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() }); 
    String str = webService.endpointInterface();
    if (str.length() > 0) {
      paramClass = getClass(webService.endpointInterface());
      WebService webService1 = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
      if (webService1 == null)
        throw new UtilException("util.handler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() }); 
      return paramClass;
    } 
    return null;
  }
  
  static InputStream getFileAsStream(Class paramClass, HandlerChain paramHandlerChain) {
    URL uRL = paramClass.getResource(paramHandlerChain.file());
    if (uRL == null)
      uRL = Thread.currentThread().getContextClassLoader().getResource(paramHandlerChain.file()); 
    if (uRL == null) {
      String str = paramClass.getPackage().getName();
      str = str.replace('.', '/');
      str = str + "/" + paramHandlerChain.file();
      uRL = Thread.currentThread().getContextClassLoader().getResource(str);
    } 
    if (uRL == null)
      throw new UtilException("util.failed.to.find.handlerchain.file", new Object[] { paramClass.getName(), paramHandlerChain.file() }); 
    try {
      return uRL.openStream();
    } catch (IOException iOException) {
      throw new UtilException("util.failed.to.parse.handlerchain.file", new Object[] { paramClass.getName(), paramHandlerChain.file() });
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\HandlerAnnotationProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */