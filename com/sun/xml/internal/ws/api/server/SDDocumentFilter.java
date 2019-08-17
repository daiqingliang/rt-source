package com.sun.xml.internal.ws.api.server;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface SDDocumentFilter {
  XMLStreamWriter filter(SDDocument paramSDDocument, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\SDDocumentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */