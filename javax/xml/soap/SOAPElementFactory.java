package javax.xml.soap;

public class SOAPElementFactory {
  private SOAPFactory soapFactory;
  
  private SOAPElementFactory(SOAPFactory paramSOAPFactory) { this.soapFactory = paramSOAPFactory; }
  
  public SOAPElement create(Name paramName) throws SOAPException { return this.soapFactory.createElement(paramName); }
  
  public SOAPElement create(String paramString) throws SOAPException { return this.soapFactory.createElement(paramString); }
  
  public SOAPElement create(String paramString1, String paramString2, String paramString3) throws SOAPException { return this.soapFactory.createElement(paramString1, paramString2, paramString3); }
  
  public static SOAPElementFactory newInstance() throws SOAPException {
    try {
      return new SOAPElementFactory(SOAPFactory.newInstance());
    } catch (Exception exception) {
      throw new SOAPException("Unable to create SOAP Element Factory: " + exception.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPElementFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */