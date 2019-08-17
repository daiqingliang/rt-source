package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class SDDocumentSource {
  public abstract XMLStreamReader read(XMLInputFactory paramXMLInputFactory) throws IOException, XMLStreamException;
  
  public abstract XMLStreamReader read() throws IOException, XMLStreamException;
  
  public abstract URL getSystemId();
  
  public static SDDocumentSource create(final URL url) { return new SDDocumentSource() {
        private final URL systemId = url;
        
        public XMLStreamReader read(XMLInputFactory param1XMLInputFactory) throws IOException, XMLStreamException {
          InputStream inputStream = url.openStream();
          return new TidyXMLStreamReader(param1XMLInputFactory.createXMLStreamReader(this.systemId.toExternalForm(), inputStream), inputStream);
        }
        
        public XMLStreamReader read() throws IOException, XMLStreamException {
          InputStream inputStream = url.openStream();
          return new TidyXMLStreamReader(XMLStreamReaderFactory.create(this.systemId.toExternalForm(), inputStream, false), inputStream);
        }
        
        public URL getSystemId() { return this.systemId; }
      }; }
  
  public static SDDocumentSource create(final URL systemId, final XMLStreamBuffer xsb) { return new SDDocumentSource() {
        public XMLStreamReader read(XMLInputFactory param1XMLInputFactory) throws IOException, XMLStreamException { return xsb.readAsXMLStreamReader(); }
        
        public XMLStreamReader read() throws IOException, XMLStreamException { return xsb.readAsXMLStreamReader(); }
        
        public URL getSystemId() { return systemId; }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\SDDocumentSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */