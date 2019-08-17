package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import javax.xml.stream.XMLStreamException;

public final class StAXExStreamWriterOutput extends XMLStreamWriterOutput {
  private final XMLStreamWriterEx out;
  
  public StAXExStreamWriterOutput(XMLStreamWriterEx paramXMLStreamWriterEx) {
    super(paramXMLStreamWriterEx, NoEscapeHandler.theInstance);
    this.out = paramXMLStreamWriterEx;
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws XMLStreamException {
    if (paramBoolean)
      this.out.writeCharacters(" "); 
    if (!(paramPcdata instanceof Base64Data)) {
      this.out.writeCharacters(paramPcdata.toString());
    } else {
      Base64Data base64Data = (Base64Data)paramPcdata;
      this.out.writeBinary(base64Data.getDataHandler());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\StAXExStreamWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */