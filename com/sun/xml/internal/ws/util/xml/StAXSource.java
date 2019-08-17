package com.sun.xml.internal.ws.util.xml;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.SAXParseException2;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class StAXSource extends SAXSource {
  private final XMLStreamReaderToContentHandler reader;
  
  private final XMLStreamReader staxReader;
  
  private final XMLFilterImpl repeater = new XMLFilterImpl();
  
  private final XMLReader pseudoParser = new XMLReader() {
      private LexicalHandler lexicalHandler;
      
      private EntityResolver entityResolver;
      
      private DTDHandler dtdHandler;
      
      private ErrorHandler errorHandler;
      
      public boolean getFeature(String param1String) throws SAXNotRecognizedException { throw new SAXNotRecognizedException(param1String); }
      
      public void setFeature(String param1String, boolean param1Boolean) throws SAXNotRecognizedException {
        if ((param1String.equals("http://xml.org/sax/features/namespaces") && param1Boolean) || (param1String.equals("http://xml.org/sax/features/namespace-prefixes") && !param1Boolean))
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
      
      public void setContentHandler(ContentHandler param1ContentHandler) { StAXSource.this.repeater.setContentHandler(param1ContentHandler); }
      
      public ContentHandler getContentHandler() { return StAXSource.this.repeater.getContentHandler(); }
      
      public void setErrorHandler(ErrorHandler param1ErrorHandler) { this.errorHandler = param1ErrorHandler; }
      
      public ErrorHandler getErrorHandler() { return this.errorHandler; }
      
      public void parse(InputSource param1InputSource) throws SAXException { parse(); }
      
      public void parse(String param1String) throws SAXException { parse(); }
      
      public void parse() throws SAXException {
        try {
          StAXSource.this.reader.bridge();
        } catch (XMLStreamException xMLStreamException) {
          SAXParseException2 sAXParseException2 = new SAXParseException2(xMLStreamException.getMessage(), null, null, (xMLStreamException.getLocation() == null) ? -1 : xMLStreamException.getLocation().getLineNumber(), (xMLStreamException.getLocation() == null) ? -1 : xMLStreamException.getLocation().getColumnNumber(), xMLStreamException);
          if (this.errorHandler != null)
            this.errorHandler.fatalError(sAXParseException2); 
          throw sAXParseException2;
        } finally {
          try {
            StAXSource.this.staxReader.close();
          } catch (XMLStreamException xMLStreamException) {}
        } 
      }
    };
  
  public StAXSource(XMLStreamReader paramXMLStreamReader, boolean paramBoolean) { this(paramXMLStreamReader, paramBoolean, new String[0]); }
  
  public StAXSource(XMLStreamReader paramXMLStreamReader, boolean paramBoolean, @NotNull String[] paramArrayOfString) {
    if (paramXMLStreamReader == null)
      throw new IllegalArgumentException(); 
    this.staxReader = paramXMLStreamReader;
    int i = paramXMLStreamReader.getEventType();
    if (i != 7 && i != 1)
      throw new IllegalStateException(); 
    this.reader = new XMLStreamReaderToContentHandler(paramXMLStreamReader, this.repeater, paramBoolean, false, paramArrayOfString);
    setXMLReader(this.pseudoParser);
    setInputSource(new InputSource());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\StAXSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */