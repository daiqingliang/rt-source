package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class StAXExConnector extends StAXStreamConnector {
  private final XMLStreamReaderEx in;
  
  public StAXExConnector(XMLStreamReaderEx paramXMLStreamReaderEx, XmlVisitor paramXmlVisitor) {
    super(paramXMLStreamReaderEx, paramXmlVisitor);
    this.in = paramXMLStreamReaderEx;
  }
  
  protected void handleCharacters() throws XMLStreamException, SAXException {
    if (this.predictor.expectText()) {
      CharSequence charSequence = this.in.getPCDATA();
      if (charSequence instanceof Base64Data) {
        Base64Data base64Data = (Base64Data)charSequence;
        Base64Data base64Data1 = new Base64Data();
        if (!base64Data.hasData()) {
          base64Data1.set(base64Data.getDataHandler());
        } else {
          base64Data1.set(base64Data.get(), base64Data.getDataLen(), base64Data.getMimeType());
        } 
        this.visitor.text(base64Data1);
        this.textReported = true;
      } else {
        this.buffer.append(charSequence);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\StAXExConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */