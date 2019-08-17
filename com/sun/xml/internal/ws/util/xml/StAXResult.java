package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

public class StAXResult extends SAXResult {
  public StAXResult(XMLStreamWriter paramXMLStreamWriter) {
    if (paramXMLStreamWriter == null)
      throw new IllegalArgumentException(); 
    setHandler(new ContentHandlerToXMLStreamWriter(paramXMLStreamWriter));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\StAXResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */