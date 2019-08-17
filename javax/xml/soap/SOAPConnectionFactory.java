package javax.xml.soap;

public abstract class SOAPConnectionFactory {
  static final String DEFAULT_SOAP_CONNECTION_FACTORY = "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory";
  
  private static final String SF_PROPERTY = "javax.xml.soap.SOAPConnectionFactory";
  
  public static SOAPConnectionFactory newInstance() throws SOAPException, UnsupportedOperationException {
    try {
      return (SOAPConnectionFactory)FactoryFinder.find("javax.xml.soap.SOAPConnectionFactory", "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory");
    } catch (Exception exception) {
      throw new SOAPException("Unable to create SOAP connection factory: " + exception.getMessage());
    } 
  }
  
  public abstract SOAPConnection createConnection() throws SOAPException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */