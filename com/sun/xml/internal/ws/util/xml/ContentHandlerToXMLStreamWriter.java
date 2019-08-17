package com.sun.xml.internal.ws.util.xml;

import java.util.Stack;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerToXMLStreamWriter extends DefaultHandler {
  private final XMLStreamWriter staxWriter;
  
  private final Stack prefixBindings;
  
  public ContentHandlerToXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter) {
    this.staxWriter = paramXMLStreamWriter;
    this.prefixBindings = new Stack();
  }
  
  public void endDocument() throws SAXException {
    try {
      this.staxWriter.writeEndDocument();
      this.staxWriter.flush();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void startDocument() throws SAXException {
    try {
      this.staxWriter.writeStartDocument();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this.staxWriter.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void skippedEntity(String paramString) throws SAXException {
    try {
      this.staxWriter.writeEntityRef(paramString);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      this.staxWriter.writeProcessingInstruction(paramString1, paramString2);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (paramString1 == null)
      paramString1 = ""; 
    if (paramString1.equals("xml"))
      return; 
    this.prefixBindings.add(paramString1);
    this.prefixBindings.add(paramString2);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      this.staxWriter.writeEndElement();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    try {
      this.staxWriter.writeStartElement(getPrefix(paramString3), paramString2, paramString1);
      while (this.prefixBindings.size() != 0) {
        String str1 = (String)this.prefixBindings.pop();
        String str2 = (String)this.prefixBindings.pop();
        if (str2.length() == 0) {
          this.staxWriter.setDefaultNamespace(str1);
        } else {
          this.staxWriter.setPrefix(str2, str1);
        } 
        this.staxWriter.writeNamespace(str2, str1);
      } 
      writeAttributes(paramAttributes);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  private void writeAttributes(Attributes paramAttributes) throws XMLStreamException {
    for (byte b = 0; b < paramAttributes.getLength(); b++) {
      String str = getPrefix(paramAttributes.getQName(b));
      if (!str.equals("xmlns"))
        this.staxWriter.writeAttribute(str, paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getValue(b)); 
    } 
  }
  
  private String getPrefix(String paramString) {
    int i = paramString.indexOf(':');
    return (i == -1) ? "" : paramString.substring(0, i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\ContentHandlerToXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */