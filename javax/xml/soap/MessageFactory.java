package javax.xml.soap;

import java.io.IOException;
import java.io.InputStream;

public abstract class MessageFactory {
  static final String DEFAULT_MESSAGE_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl";
  
  private static final String MESSAGE_FACTORY_PROPERTY = "javax.xml.soap.MessageFactory";
  
  public static MessageFactory newInstance() throws SOAPException {
    try {
      MessageFactory messageFactory = (MessageFactory)FactoryFinder.find("javax.xml.soap.MessageFactory", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl", false);
      return (messageFactory != null) ? messageFactory : newInstance("SOAP 1.1 Protocol");
    } catch (Exception exception) {
      throw new SOAPException("Unable to create message factory for SOAP: " + exception.getMessage());
    } 
  }
  
  public static MessageFactory newInstance(String paramString) throws SOAPException { return SAAJMetaFactory.getInstance().newMessageFactory(paramString); }
  
  public abstract SOAPMessage createMessage() throws SOAPException;
  
  public abstract SOAPMessage createMessage(MimeHeaders paramMimeHeaders, InputStream paramInputStream) throws IOException, SOAPException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\MessageFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */