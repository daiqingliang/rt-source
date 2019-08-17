package javax.xml.ws;

import java.util.Map;

public interface BindingProvider {
  public static final String USERNAME_PROPERTY = "javax.xml.ws.security.auth.username";
  
  public static final String PASSWORD_PROPERTY = "javax.xml.ws.security.auth.password";
  
  public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.ws.service.endpoint.address";
  
  public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.ws.session.maintain";
  
  public static final String SOAPACTION_USE_PROPERTY = "javax.xml.ws.soap.http.soapaction.use";
  
  public static final String SOAPACTION_URI_PROPERTY = "javax.xml.ws.soap.http.soapaction.uri";
  
  Map<String, Object> getRequestContext();
  
  Map<String, Object> getResponseContext();
  
  Binding getBinding();
  
  EndpointReference getEndpointReference();
  
  <T extends EndpointReference> T getEndpointReference(Class<T> paramClass);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\BindingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */