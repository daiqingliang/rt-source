package javax.xml.ws.soap;

import java.util.Set;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Binding;

public interface SOAPBinding extends Binding {
  public static final String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";
  
  public static final String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
  
  public static final String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
  
  public static final String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
  
  Set<String> getRoles();
  
  void setRoles(Set<String> paramSet);
  
  boolean isMTOMEnabled();
  
  void setMTOMEnabled(boolean paramBoolean);
  
  SOAPFactory getSOAPFactory();
  
  MessageFactory getMessageFactory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\soap\SOAPBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */