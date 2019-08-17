package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;

public interface XMLEntityResolver {
  Parser resolveEntity(String paramString1, String paramString2) throws SAXException, IOException, XMLStreamException;
  
  public static final class Parser {
    public final URL systemId;
    
    public final XMLStreamReader parser;
    
    public Parser(URL param1URL, XMLStreamReader param1XMLStreamReader) {
      assert param1XMLStreamReader != null;
      this.systemId = param1URL;
      this.parser = param1XMLStreamReader;
    }
    
    public Parser(SDDocumentSource param1SDDocumentSource) throws IOException, XMLStreamException {
      this.systemId = param1SDDocumentSource.getSystemId();
      this.parser = param1SDDocumentSource.read();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\XMLEntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */