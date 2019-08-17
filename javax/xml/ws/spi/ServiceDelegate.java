package javax.xml.ws.spi;

import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;

public abstract class ServiceDelegate {
  public abstract <T> T getPort(QName paramQName, Class<T> paramClass);
  
  public abstract <T> T getPort(QName paramQName, Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract <T> T getPort(Class<T> paramClass);
  
  public abstract <T> T getPort(Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract void addPort(QName paramQName, String paramString1, String paramString2);
  
  public abstract <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode);
  
  public abstract <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  public abstract <T> Dispatch<T> createDispatch(EndpointReference paramEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  public abstract Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode);
  
  public abstract Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  public abstract Dispatch<Object> createDispatch(EndpointReference paramEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  public abstract QName getServiceName();
  
  public abstract Iterator<QName> getPorts();
  
  public abstract URL getWSDLDocumentLocation();
  
  public abstract HandlerResolver getHandlerResolver();
  
  public abstract void setHandlerResolver(HandlerResolver paramHandlerResolver);
  
  public abstract Executor getExecutor();
  
  public abstract void setExecutor(Executor paramExecutor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\spi\ServiceDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */