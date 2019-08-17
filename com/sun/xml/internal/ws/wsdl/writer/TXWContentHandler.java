package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TXWContentHandler implements ContentHandler {
  Stack<TypedXmlWriter> stack = new Stack();
  
  public TXWContentHandler(TypedXmlWriter paramTypedXmlWriter) { this.stack.push(paramTypedXmlWriter); }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void startDocument() throws SAXException {}
  
  public void endDocument() throws SAXException {}
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {}
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    TypedXmlWriter typedXmlWriter = ((TypedXmlWriter)this.stack.peek())._element(paramString1, paramString2, TypedXmlWriter.class);
    this.stack.push(typedXmlWriter);
    if (paramAttributes != null)
      for (byte b = 0; b < paramAttributes.getLength(); b++) {
        String str = paramAttributes.getURI(b);
        if ("http://www.w3.org/2000/xmlns/".equals(str)) {
          if ("xmlns".equals(paramAttributes.getLocalName(b))) {
            typedXmlWriter._namespace(paramAttributes.getValue(b), "");
          } else {
            typedXmlWriter._namespace(paramAttributes.getValue(b), paramAttributes.getLocalName(b));
          } 
        } else if (!"schemaLocation".equals(paramAttributes.getLocalName(b)) || !"".equals(paramAttributes.getValue(b))) {
          typedXmlWriter._attribute(str, paramAttributes.getLocalName(b), paramAttributes.getValue(b));
        } 
      }  
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException { this.stack.pop(); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {}
  
  public void skippedEntity(String paramString) throws SAXException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\TXWContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */