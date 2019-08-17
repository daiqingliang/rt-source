package javax.xml.ws;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.transform.Source;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.http.HttpContext;
import org.w3c.dom.Element;

public abstract class Endpoint {
  public static final String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
  
  public static final String WSDL_PORT = "javax.xml.ws.wsdl.port";
  
  public static Endpoint create(Object paramObject) { return create(null, paramObject); }
  
  public static Endpoint create(Object paramObject, WebServiceFeature... paramVarArgs) { return create(null, paramObject, paramVarArgs); }
  
  public static Endpoint create(String paramString, Object paramObject) { return Provider.provider().createEndpoint(paramString, paramObject); }
  
  public static Endpoint create(String paramString, Object paramObject, WebServiceFeature... paramVarArgs) { return Provider.provider().createEndpoint(paramString, paramObject, paramVarArgs); }
  
  public abstract Binding getBinding();
  
  public abstract Object getImplementor();
  
  public abstract void publish(String paramString);
  
  public static Endpoint publish(String paramString, Object paramObject) { return Provider.provider().createAndPublishEndpoint(paramString, paramObject); }
  
  public static Endpoint publish(String paramString, Object paramObject, WebServiceFeature... paramVarArgs) { return Provider.provider().createAndPublishEndpoint(paramString, paramObject, paramVarArgs); }
  
  public abstract void publish(Object paramObject);
  
  public void publish(HttpContext paramHttpContext) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
  
  public abstract void stop();
  
  public abstract boolean isPublished();
  
  public abstract List<Source> getMetadata();
  
  public abstract void setMetadata(List<Source> paramList);
  
  public abstract Executor getExecutor();
  
  public abstract void setExecutor(Executor paramExecutor);
  
  public abstract Map<String, Object> getProperties();
  
  public abstract void setProperties(Map<String, Object> paramMap);
  
  public abstract EndpointReference getEndpointReference(Element... paramVarArgs);
  
  public abstract <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs);
  
  public void setEndpointContext(EndpointContext paramEndpointContext) { throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\Endpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */