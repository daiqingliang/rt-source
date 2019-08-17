package com.sun.xml.internal.stream.buffer.sax;

import com.sun.xml.internal.stream.buffer.AbstractCreator;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class SAXBufferCreator extends AbstractCreator implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler {
  protected String[] _namespaceAttributes = new String[32];
  
  protected int _namespaceAttributesPtr;
  
  private int depth = 0;
  
  public SAXBufferCreator() {}
  
  public SAXBufferCreator(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) {
    this();
    setBuffer(paramMutableXMLStreamBuffer);
  }
  
  public MutableXMLStreamBuffer create(XMLReader paramXMLReader, InputStream paramInputStream) throws IOException, SAXException { return create(paramXMLReader, paramInputStream, null); }
  
  public MutableXMLStreamBuffer create(XMLReader paramXMLReader, InputStream paramInputStream, String paramString) throws IOException, SAXException {
    if (this._buffer == null)
      createBuffer(); 
    this._buffer.setSystemId(paramString);
    paramXMLReader.setContentHandler(this);
    paramXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
    try {
      setHasInternedStrings(paramXMLReader.getFeature("http://xml.org/sax/features/string-interning"));
    } catch (SAXException sAXException) {}
    if (paramString != null) {
      InputSource inputSource = new InputSource(paramString);
      inputSource.setByteStream(paramInputStream);
      paramXMLReader.parse(inputSource);
    } else {
      paramXMLReader.parse(new InputSource(paramInputStream));
    } 
    return getXMLStreamBuffer();
  }
  
  public void reset() {
    this._buffer = null;
    this._namespaceAttributesPtr = 0;
    this.depth = 0;
  }
  
  public void startDocument() { storeStructure(16); }
  
  public void endDocument() { storeStructure(144); }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { cacheNamespaceAttribute(paramString1, paramString2); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    storeQualifiedName(32, paramString1, paramString2, paramString3);
    if (this._namespaceAttributesPtr > 0)
      storeNamespaceAttributes(); 
    if (paramAttributes.getLength() > 0)
      storeAttributes(paramAttributes); 
    this.depth++;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    storeStructure(144);
    if (--this.depth == 0)
      increaseTreeCount(); 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { storeContentCharacters(80, paramArrayOfChar, paramInt1, paramInt2); }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    storeStructure(112);
    storeStructureString(paramString1);
    storeStructureString(paramString2);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { storeContentCharacters(96, paramArrayOfChar, paramInt1, paramInt2); }
  
  private void cacheNamespaceAttribute(String paramString1, String paramString2) throws SAXException {
    this._namespaceAttributes[this._namespaceAttributesPtr++] = paramString1;
    this._namespaceAttributes[this._namespaceAttributesPtr++] = paramString2;
    if (this._namespaceAttributesPtr == this._namespaceAttributes.length) {
      String[] arrayOfString = new String[this._namespaceAttributesPtr * 2];
      System.arraycopy(this._namespaceAttributes, 0, arrayOfString, 0, this._namespaceAttributesPtr);
      this._namespaceAttributes = arrayOfString;
    } 
  }
  
  private void storeNamespaceAttributes() {
    for (boolean bool = false; bool < this._namespaceAttributesPtr; bool += true) {
      byte b = 64;
      if (this._namespaceAttributes[bool].length() > 0) {
        b |= 0x1;
        storeStructureString(this._namespaceAttributes[bool]);
      } 
      if (this._namespaceAttributes[bool + true].length() > 0) {
        b |= 0x2;
        storeStructureString(this._namespaceAttributes[bool + true]);
      } 
      storeStructure(b);
    } 
    this._namespaceAttributesPtr = 0;
  }
  
  private void storeAttributes(Attributes paramAttributes) {
    for (byte b = 0; b < paramAttributes.getLength(); b++) {
      if (!paramAttributes.getQName(b).startsWith("xmlns")) {
        storeQualifiedName(48, paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getQName(b));
        storeStructureString(paramAttributes.getType(b));
        storeContentString(paramAttributes.getValue(b));
      } 
    } 
  }
  
  private void storeQualifiedName(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (paramString1.length() > 0) {
      paramInt |= 0x2;
      storeStructureString(paramString1);
    } 
    storeStructureString(paramString2);
    if (paramString3.indexOf(':') >= 0) {
      paramInt |= 0x4;
      storeStructureString(paramString3);
    } 
    storeStructure(paramInt);
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2) throws IOException, SAXException { return null; }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void endDTD() {}
  
  public void startEntity(String paramString) throws SAXException {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startCDATA() {}
  
  public void endCDATA() {}
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\sax\SAXBufferCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */