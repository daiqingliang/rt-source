package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXStreamWriter extends SAX2StAXBaseWriter {
  private XMLStreamWriter writer;
  
  private boolean needToCallStartDocument = false;
  
  public SAX2StAXStreamWriter() {}
  
  public SAX2StAXStreamWriter(XMLStreamWriter paramXMLStreamWriter) { this.writer = paramXMLStreamWriter; }
  
  public XMLStreamWriter getStreamWriter() { return this.writer; }
  
  public void setStreamWriter(XMLStreamWriter paramXMLStreamWriter) { this.writer = paramXMLStreamWriter; }
  
  public void startDocument() {
    super.startDocument();
    this.needToCallStartDocument = true;
  }
  
  public void endDocument() {
    try {
      this.writer.writeEndDocument();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
    super.endDocument();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.needToCallStartDocument) {
      try {
        if (this.docLocator == null) {
          this.writer.writeStartDocument();
        } else {
          try {
            this.writer.writeStartDocument(((Locator2)this.docLocator).getXMLVersion());
          } catch (ClassCastException classCastException) {
            this.writer.writeStartDocument();
          } 
        } 
      } catch (XMLStreamException xMLStreamException) {
        throw new SAXException(xMLStreamException);
      } 
      this.needToCallStartDocument = false;
    } 
    try {
      String[] arrayOfString = { null, null };
      parseQName(paramString3, arrayOfString);
      this.writer.writeStartElement(paramString3);
      byte b = 0;
      int i = paramAttributes.getLength();
      while (b < i) {
        parseQName(paramAttributes.getQName(b), arrayOfString);
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[1];
        String str3 = paramAttributes.getQName(b);
        String str4 = paramAttributes.getValue(b);
        String str5 = paramAttributes.getURI(b);
        if ("xmlns".equals(str1) || "xmlns".equals(str3)) {
          if (str2.length() == 0) {
            this.writer.setDefaultNamespace(str4);
          } else {
            this.writer.setPrefix(str2, str4);
          } 
          this.writer.writeNamespace(str2, str4);
        } else if (str1.length() > 0) {
          this.writer.writeAttribute(str1, str5, str2, str4);
        } else {
          this.writer.writeAttribute(str3, str4);
        } 
        b++;
      } 
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } finally {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      this.writer.writeEndElement();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } finally {
      super.endElement(paramString1, paramString2, paramString3);
    } 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    try {
      this.writer.writeComment(new String(paramArrayOfChar, paramInt1, paramInt2));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
    try {
      if (!this.isCDATA)
        this.writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2); 
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void endCDATA() {
    try {
      this.writer.writeCData(this.CDATABuffer.toString());
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
    super.endCDATA();
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    try {
      this.writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    super.processingInstruction(paramString1, paramString2);
    try {
      this.writer.writeProcessingInstruction(paramString1, paramString2);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2StAXStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */