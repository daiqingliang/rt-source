package javax.xml.soap;

public abstract class SAAJMetaFactory {
  private static final String META_FACTORY_CLASS_PROPERTY = "javax.xml.soap.MetaFactory";
  
  static final String DEFAULT_META_FACTORY_CLASS = "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";
  
  static SAAJMetaFactory getInstance() throws SOAPException {
    try {
      return (SAAJMetaFactory)FactoryFinder.find("javax.xml.soap.MetaFactory", "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl");
    } catch (Exception exception) {
      throw new SOAPException("Unable to create SAAJ meta-factory" + exception.getMessage());
    } 
  }
  
  protected abstract MessageFactory newMessageFactory(String paramString) throws SOAPException;
  
  protected abstract SOAPFactory newSOAPFactory(String paramString) throws SOAPException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SAAJMetaFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */