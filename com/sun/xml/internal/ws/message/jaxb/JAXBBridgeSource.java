package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class JAXBBridgeSource extends SAXSource {
  private final XMLBridge bridge;
  
  private final Object contentObject;
  
  private final XMLReader pseudoParser = new XMLFilterImpl() {
      private LexicalHandler lexicalHandler;
      
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
      
      public void parse(InputSource param1InputSource) throws SAXException { parse(); }
      
      public void parse(String param1String) throws SAXException { parse(); }
      
      public void parse() throws SAXException {
        try {
          startDocument();
          JAXBBridgeSource.this.bridge.marshal(JAXBBridgeSource.this.contentObject, this, null);
          endDocument();
        } catch (JAXBException jAXBException) {
          SAXParseException sAXParseException = new SAXParseException(jAXBException.getMessage(), null, null, -1, -1, jAXBException);
          fatalError(sAXParseException);
          throw sAXParseException;
        } 
      }
    };
  
  public JAXBBridgeSource(XMLBridge paramXMLBridge, Object paramObject) {
    this.bridge = paramXMLBridge;
    this.contentObject = paramObject;
    setXMLReader(this.pseudoParser);
    setInputSource(new InputSource());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBBridgeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */