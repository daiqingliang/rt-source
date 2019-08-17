package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class StAX2SAXReader {
  ContentHandler _handler;
  
  LexicalHandler _lexicalHandler;
  
  XMLStreamReader _reader;
  
  public StAX2SAXReader(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler) {
    this._handler = paramContentHandler;
    this._reader = paramXMLStreamReader;
  }
  
  public StAX2SAXReader(XMLStreamReader paramXMLStreamReader) { this._reader = paramXMLStreamReader; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this._handler = paramContentHandler; }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) { this._lexicalHandler = paramLexicalHandler; }
  
  public void adapt() throws XMLStreamException, SAXException {
    AttributesImpl attributesImpl = new AttributesImpl();
    this._handler.startDocument();
    try {
      while (this._reader.hasNext()) {
        byte b;
        int j;
        int i;
        String str2;
        String str1;
        QName qName;
        int k = this._reader.next();
        switch (k) {
          case 1:
            i = this._reader.getNamespaceCount();
            for (b = 0; b < i; b++)
              this._handler.startPrefixMapping(this._reader.getNamespacePrefix(b), this._reader.getNamespaceURI(b)); 
            attributesImpl.clear();
            j = this._reader.getAttributeCount();
            for (b = 0; b < j; b++) {
              QName qName1 = this._reader.getAttributeName(b);
              String str = this._reader.getAttributePrefix(b);
              if (str == null || str == "") {
                str = qName1.getLocalPart();
              } else {
                str = str + ":" + qName1.getLocalPart();
              } 
              attributesImpl.addAttribute(this._reader.getAttributeNamespace(b), qName1.getLocalPart(), str, this._reader.getAttributeType(b), this._reader.getAttributeValue(b));
            } 
            qName = this._reader.getName();
            str1 = qName.getPrefix();
            str2 = qName.getLocalPart();
            this._handler.startElement(this._reader.getNamespaceURI(), str2, (str1.length() > 0) ? (str1 + ":" + str2) : str2, attributesImpl);
            continue;
          case 2:
            qName = this._reader.getName();
            str1 = qName.getPrefix();
            str2 = qName.getLocalPart();
            this._handler.endElement(this._reader.getNamespaceURI(), str2, (str1.length() > 0) ? (str1 + ":" + str2) : str2);
            i = this._reader.getNamespaceCount();
            for (b = 0; b < i; b++)
              this._handler.endPrefixMapping(this._reader.getNamespacePrefix(b)); 
            continue;
          case 4:
            this._handler.characters(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
            continue;
          case 5:
            this._lexicalHandler.comment(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
            continue;
          case 3:
            this._handler.processingInstruction(this._reader.getPITarget(), this._reader.getPIData());
            continue;
          case 8:
            continue;
        } 
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[] { Integer.valueOf(k) }));
      } 
    } catch (XMLStreamException xMLStreamException) {
      this._handler.endDocument();
      throw xMLStreamException;
    } 
    this._handler.endDocument();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\StAX2SAXReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */