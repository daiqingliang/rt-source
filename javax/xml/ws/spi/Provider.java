package javax.xml.ws.spi;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public abstract class Provider {
  public static final String JAXWSPROVIDER_PROPERTY = "javax.xml.ws.spi.Provider";
  
  static final String DEFAULT_JAXWSPROVIDER = "com.sun.xml.internal.ws.spi.ProviderImpl";
  
  private static final Method loadMethod;
  
  private static final Method iteratorMethod;
  
  public static Provider provider() {
    try {
      Object object = getProviderUsingServiceLoader();
      if (object == null)
        object = FactoryFinder.find("javax.xml.ws.spi.Provider", "com.sun.xml.internal.ws.spi.ProviderImpl"); 
      if (!(object instanceof Provider)) {
        Class clazz = Provider.class;
        String str = clazz.getName().replace('.', '/') + ".class";
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null)
          classLoader = ClassLoader.getSystemClassLoader(); 
        URL uRL = classLoader.getResource(str);
        throw new LinkageError("ClassCastException: attempting to cast" + object.getClass().getClassLoader().getResource(str) + "to" + uRL.toString());
      } 
      return (Provider)object;
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (Exception exception) {
      throw new WebServiceException("Unable to createEndpointReference Provider", exception);
    } 
  }
  
  private static Provider getProviderUsingServiceLoader() {
    if (loadMethod != null) {
      Iterator iterator;
      Object object;
      try {
        object = loadMethod.invoke(null, new Object[] { Provider.class });
      } catch (Exception null) {
        throw new WebServiceException("Cannot invoke java.util.ServiceLoader#load()", iterator);
      } 
      try {
        iterator = (Iterator)iteratorMethod.invoke(object, new Object[0]);
      } catch (Exception exception) {
        throw new WebServiceException("Cannot invoke java.util.ServiceLoader#iterator()", exception);
      } 
      return iterator.hasNext() ? (Provider)iterator.next() : null;
    } 
    return null;
  }
  
  public abstract ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass);
  
  public ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
  
  public abstract Endpoint createEndpoint(String paramString, Object paramObject);
  
  public abstract Endpoint createAndPublishEndpoint(String paramString, Object paramObject);
  
  public abstract EndpointReference readEndpointReference(Source paramSource);
  
  public abstract <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, List<Element> paramList1, String paramString2, List<Element> paramList2);
  
  public W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList1, String paramString2, List<Element> paramList2, List<Element> paramList3, Map<QName, String> paramMap) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
  
  public Endpoint createAndPublishEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
  
  public Endpoint createEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
  
  public Endpoint createEndpoint(String paramString, Class<?> paramClass, Invoker paramInvoker, WebServiceFeature... paramVarArgs) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
  
  static  {
    Method method1 = null;
    Method method2 = null;
    try {
      Class clazz = Class.forName("java.util.ServiceLoader");
      method1 = clazz.getMethod("load", new Class[] { Class.class });
      method2 = clazz.getMethod("iterator", new Class[0]);
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (NoSuchMethodException noSuchMethodException) {}
    loadMethod = method1;
    iteratorMethod = method2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\spi\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */