package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class WSEPRExtension extends WSEndpointReference.EPRExtension {
  XMLStreamBuffer xsb;
  
  final QName qname;
  
  public WSEPRExtension(XMLStreamBuffer paramXMLStreamBuffer, QName paramQName) {
    this.xsb = paramXMLStreamBuffer;
    this.qname = paramQName;
  }
  
  public XMLStreamReader readAsXMLStreamReader() throws XMLStreamException { return this.xsb.readAsXMLStreamReader(); }
  
  public QName getQName() { return this.qname; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\WSEPRExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */