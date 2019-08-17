package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAX2StAXWriter extends DefaultHandler implements LexicalHandler {
  private static final Logger logger = Logger.getLogger(SAX2StAXWriter.class.getName());
  
  XMLStreamWriter _writer;
  
  ArrayList _namespaces = new ArrayList();
  
  public SAX2StAXWriter(XMLStreamWriter paramXMLStreamWriter) { this._writer = paramXMLStreamWriter; }
  
  public XMLStreamWriter getWriter() { return this._writer; }
  
  public void startDocument() throws SAXException {
    try {
      this._writer.writeStartDocument();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void endDocument() throws SAXException {
    try {
      this._writer.writeEndDocument();
      this._writer.flush();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this._writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    try {
      int i = paramString3.indexOf(':');
      String str = (i > 0) ? paramString3.substring(0, i) : "";
      this._writer.writeStartElement(str, paramString2, paramString1);
      int j = this._namespaces.size();
      byte b;
      for (b = 0; b < j; b++) {
        QualifiedName qualifiedName = (QualifiedName)this._namespaces.get(b);
        this._writer.writeNamespace(qualifiedName.prefix, qualifiedName.namespaceName);
      } 
      this._namespaces.clear();
      j = paramAttributes.getLength();
      for (b = 0; b < j; b++)
        this._writer.writeAttribute(paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getValue(b)); 
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      this._writer.writeEndElement();
    } catch (XMLStreamException xMLStreamException) {
      logger.log(Level.FINE, "Exception on endElement", xMLStreamException);
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { this._namespaces.add(new QualifiedName(paramString1, paramString2)); }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      this._writer.writeProcessingInstruction(paramString1, paramString2);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this._writer.writeComment(new String(paramArrayOfChar, paramInt1, paramInt2));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void endCDATA() throws SAXException {}
  
  public void endDTD() throws SAXException {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startCDATA() throws SAXException {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void startEntity(String paramString) throws SAXException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\SAX2StAXWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */