package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StaxSerializer implements XmlSerializer {
  private final XMLStreamWriter out;
  
  public StaxSerializer(XMLStreamWriter paramXMLStreamWriter) { this(paramXMLStreamWriter, true); }
  
  public StaxSerializer(XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean) {
    if (paramBoolean)
      paramXMLStreamWriter = new IndentingXMLStreamWriter(paramXMLStreamWriter); 
    this.out = paramXMLStreamWriter;
  }
  
  public void startDocument() {
    try {
      this.out.writeStartDocument();
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3) {
    try {
      this.out.writeStartElement(paramString3, paramString2, paramString1);
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder) {
    try {
      this.out.writeAttribute(paramString3, paramString1, paramString2, paramStringBuilder.toString());
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void writeXmlns(String paramString1, String paramString2) {
    try {
      if (paramString1.length() == 0) {
        this.out.setDefaultNamespace(paramString2);
      } else {
        this.out.setPrefix(paramString1, paramString2);
      } 
      this.out.writeNamespace(paramString1, paramString2);
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3) {}
  
  public void endTag() {
    try {
      this.out.writeEndElement();
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void text(StringBuilder paramStringBuilder) {
    try {
      this.out.writeCharacters(paramStringBuilder.toString());
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void cdata(StringBuilder paramStringBuilder) {
    try {
      this.out.writeCData(paramStringBuilder.toString());
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void comment(StringBuilder paramStringBuilder) {
    try {
      this.out.writeComment(paramStringBuilder.toString());
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void endDocument() {
    try {
      this.out.writeEndDocument();
      this.out.flush();
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
  
  public void flush() {
    try {
      this.out.flush();
    } catch (XMLStreamException xMLStreamException) {
      throw new TxwException(xMLStreamException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\StaxSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */