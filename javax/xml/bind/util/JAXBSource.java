package javax.xml.bind.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JAXBSource extends SAXSource {
  private final Marshaller marshaller;
  
  private final Object contentObject;
  
  private final XMLReader pseudoParser = new XMLReader() {
      private LexicalHandler lexicalHandler;
      
      private EntityResolver entityResolver;
      
      private DTDHandler dtdHandler;
      
      private XMLFilter repeater = new XMLFilterImpl();
      
      private ErrorHandler errorHandler;
      
      public boolean getFeature(String param1String) throws SAXNotRecognizedException {
        if (param1String.equals("http://xml.org/sax/features/namespaces"))
          return true; 
        if (param1String.equals("http://xml.org/sax/features/namespace-prefixes"))
          return false; 
        throw new SAXNotRecognizedException(param1String);
      }
      
      public void setFeature(String param1String, boolean param1Boolean) throws SAXNotRecognizedException {
        if (param1String.equals("http://xml.org/sax/features/namespaces") && param1Boolean)
          return; 
        if (param1String.equals("http://xml.org/sax/features/namespace-prefixes") && !param1Boolean)
          return; 
        throw new SAXNotRecognizedException(param1String);
      }
      
      public Object getProperty(String param1String) throws SAXNotRecognizedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(param1String))
          return this.lexicalHandler; 
        throw new SAXNotRecognizedException(param1String);
      }
      
      public void setProperty(String param1String, Object param1Object) throws SAXNotRecognizedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(param1String)) {
          this.lexicalHandler = (LexicalHandler)param1Object;
          return;
        } 
        throw new SAXNotRecognizedException(param1String);
      }
      
      public void setEntityResolver(EntityResolver param1EntityResolver) { this.entityResolver = param1EntityResolver; }
      
      public EntityResolver getEntityResolver() { return this.entityResolver; }
      
      public void setDTDHandler(DTDHandler param1DTDHandler) { this.dtdHandler = param1DTDHandler; }
      
      public DTDHandler getDTDHandler() { return this.dtdHandler; }
      
      public void setContentHandler(ContentHandler param1ContentHandler) { this.repeater.setContentHandler(param1ContentHandler); }
      
      public ContentHandler getContentHandler() { return this.repeater.getContentHandler(); }
      
      public void setErrorHandler(ErrorHandler param1ErrorHandler) { this.errorHandler = param1ErrorHandler; }
      
      public ErrorHandler getErrorHandler() { return this.errorHandler; }
      
      public void parse(InputSource param1InputSource) throws SAXException { parse(); }
      
      public void parse(String param1String) throws SAXException { parse(); }
      
      public void parse() throws SAXException {
        try {
          JAXBSource.this.marshaller.marshal(JAXBSource.this.contentObject, (XMLFilterImpl)this.repeater);
        } catch (JAXBException jAXBException) {
          SAXParseException sAXParseException = new SAXParseException(jAXBException.getMessage(), null, null, -1, -1, jAXBException);
          if (this.errorHandler != null)
            this.errorHandler.fatalError(sAXParseException); 
          throw sAXParseException;
        } 
      }
    };
  
  public JAXBSource(JAXBContext paramJAXBContext, Object paramObject) throws JAXBException { this((paramJAXBContext == null) ? assertionFailed(Messages.format("JAXBSource.NullContext")) : paramJAXBContext.createMarshaller(), (paramObject == null) ? assertionFailed(Messages.format("JAXBSource.NullContent")) : paramObject); }
  
  public JAXBSource(Marshaller paramMarshaller, Object paramObject) throws JAXBException {
    if (paramMarshaller == null)
      throw new JAXBException(Messages.format("JAXBSource.NullMarshaller")); 
    if (paramObject == null)
      throw new JAXBException(Messages.format("JAXBSource.NullContent")); 
    this.marshaller = paramMarshaller;
    this.contentObject = paramObject;
    setXMLReader(this.pseudoParser);
    setInputSource(new InputSource());
  }
  
  private static Marshaller assertionFailed(String paramString) throws JAXBException { throw new JAXBException(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bin\\util\JAXBSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */