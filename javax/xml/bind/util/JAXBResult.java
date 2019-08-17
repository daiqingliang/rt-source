package javax.xml.bind.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.transform.sax.SAXResult;

public class JAXBResult extends SAXResult {
  private final UnmarshallerHandler unmarshallerHandler;
  
  public JAXBResult(JAXBContext paramJAXBContext) throws JAXBException { this((paramJAXBContext == null) ? assertionFailed() : paramJAXBContext.createUnmarshaller()); }
  
  public JAXBResult(Unmarshaller paramUnmarshaller) throws JAXBException {
    if (paramUnmarshaller == null)
      throw new JAXBException(Messages.format("JAXBResult.NullUnmarshaller")); 
    this.unmarshallerHandler = paramUnmarshaller.getUnmarshallerHandler();
    setHandler(this.unmarshallerHandler);
  }
  
  public Object getResult() throws JAXBException { return this.unmarshallerHandler.getResult(); }
  
  private static Unmarshaller assertionFailed() throws JAXBException { throw new JAXBException(Messages.format("JAXBResult.NullContext")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bin\\util\JAXBResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */