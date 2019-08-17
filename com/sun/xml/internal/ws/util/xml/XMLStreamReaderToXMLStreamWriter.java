package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import java.io.IOException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamReaderToXMLStreamWriter {
  private static final int BUF_SIZE = 4096;
  
  protected XMLStreamReader in;
  
  protected XMLStreamWriter out;
  
  private char[] buf;
  
  boolean optimizeBase64Data = false;
  
  AttachmentMarshaller mtomAttachmentMarshaller;
  
  public void bridge(XMLStreamReader paramXMLStreamReader, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    assert paramXMLStreamReader != null && paramXMLStreamWriter != null;
    this.in = paramXMLStreamReader;
    this.out = paramXMLStreamWriter;
    this.optimizeBase64Data = paramXMLStreamReader instanceof XMLStreamReaderEx;
    if (paramXMLStreamWriter instanceof XMLStreamWriterEx && paramXMLStreamWriter instanceof MtomStreamWriter)
      this.mtomAttachmentMarshaller = ((MtomStreamWriter)paramXMLStreamWriter).getAttachmentMarshaller(); 
    byte b = 0;
    this.buf = new char[4096];
    int i = paramXMLStreamReader.getEventType();
    if (i == 7)
      while (!paramXMLStreamReader.isStartElement()) {
        i = paramXMLStreamReader.next();
        if (i == 5)
          handleComment(); 
      }  
    if (i != 1)
      throw new IllegalStateException("The current event is not START_ELEMENT\n but " + i); 
    do {
      switch (i) {
        case 1:
          b++;
          handleStartElement();
          break;
        case 2:
          handleEndElement();
          if (--b == 0)
            return; 
          break;
        case 4:
          handleCharacters();
          break;
        case 9:
          handleEntityReference();
          break;
        case 3:
          handlePI();
          break;
        case 5:
          handleComment();
          break;
        case 11:
          handleDTD();
          break;
        case 12:
          handleCDATA();
          break;
        case 6:
          handleSpace();
          break;
        case 8:
          throw new XMLStreamException("Malformed XML at depth=" + b + ", Reached EOF. Event=" + i);
        default:
          throw new XMLStreamException("Cannot process event: " + i);
      } 
      i = paramXMLStreamReader.next();
    } while (b != 0);
  }
  
  protected void handlePI() { this.out.writeProcessingInstruction(this.in.getPITarget(), this.in.getPIData()); }
  
  protected void handleCharacters() {
    CharSequence charSequence = null;
    if (this.optimizeBase64Data)
      charSequence = ((XMLStreamReaderEx)this.in).getPCDATA(); 
    if (charSequence != null && charSequence instanceof Base64Data) {
      if (this.mtomAttachmentMarshaller != null) {
        Base64Data base64Data = (Base64Data)charSequence;
        ((XMLStreamWriterEx)this.out).writeBinary(base64Data.getDataHandler());
      } else {
        try {
          ((Base64Data)charSequence).writeTo(this.out);
        } catch (IOException iOException) {
          throw new XMLStreamException(iOException);
        } 
      } 
    } else {
      int i = 0;
      int j = this.buf.length;
      while (j == this.buf.length) {
        j = this.in.getTextCharacters(i, this.buf, 0, this.buf.length);
        this.out.writeCharacters(this.buf, 0, j);
        i += this.buf.length;
      } 
    } 
  }
  
  protected void handleEndElement() { this.out.writeEndElement(); }
  
  protected void handleStartElement() {
    String str = this.in.getNamespaceURI();
    if (str == null) {
      this.out.writeStartElement(this.in.getLocalName());
    } else {
      this.out.writeStartElement(fixNull(this.in.getPrefix()), this.in.getLocalName(), str);
    } 
    int i = this.in.getNamespaceCount();
    int j;
    for (j = 0; j < i; j++)
      this.out.writeNamespace(this.in.getNamespacePrefix(j), fixNull(this.in.getNamespaceURI(j))); 
    j = this.in.getAttributeCount();
    for (byte b = 0; b < j; b++)
      handleAttribute(b); 
  }
  
  protected void handleAttribute(int paramInt) throws XMLStreamException {
    String str1 = this.in.getAttributeNamespace(paramInt);
    String str2 = this.in.getAttributePrefix(paramInt);
    if (fixNull(str1).equals("http://www.w3.org/2000/xmlns/"))
      return; 
    if (str1 == null || str2 == null || str2.equals("")) {
      this.out.writeAttribute(this.in.getAttributeLocalName(paramInt), this.in.getAttributeValue(paramInt));
    } else {
      this.out.writeAttribute(str2, str1, this.in.getAttributeLocalName(paramInt), this.in.getAttributeValue(paramInt));
    } 
  }
  
  protected void handleDTD() { this.out.writeDTD(this.in.getText()); }
  
  protected void handleComment() { this.out.writeComment(this.in.getText()); }
  
  protected void handleEntityReference() { this.out.writeEntityRef(this.in.getText()); }
  
  protected void handleSpace() { handleCharacters(); }
  
  protected void handleCDATA() { this.out.writeCData(this.in.getText()); }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\XMLStreamReaderToXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */