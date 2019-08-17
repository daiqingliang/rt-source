package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

final class SchemaContentHandler implements ContentHandler {
  private SymbolTable fSymbolTable;
  
  private SchemaDOMParser fSchemaDOMParser;
  
  private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
  
  private NamespaceSupport fNamespaceContext = new NamespaceSupport();
  
  private boolean fNeedPushNSContext;
  
  private boolean fNamespacePrefixes = false;
  
  private boolean fStringsInternalized = false;
  
  private final QName fElementQName = new QName();
  
  private final QName fAttributeQName = new QName();
  
  private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  
  private final XMLString fTempString = new XMLString();
  
  private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  public Document getDocument() { return this.fSchemaDOMParser.getDocument(); }
  
  public void setDocumentLocator(Locator paramLocator) { this.fSAXLocatorWrapper.setLocator(paramLocator); }
  
  public void startDocument() {
    this.fNeedPushNSContext = true;
    this.fNamespaceContext.reset();
    try {
      this.fSchemaDOMParser.startDocument(this.fSAXLocatorWrapper, null, this.fNamespaceContext, null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } 
  }
  
  public void endDocument() {
    this.fSAXLocatorWrapper.setLocator(null);
    try {
      this.fSchemaDOMParser.endDocument(null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (this.fNeedPushNSContext) {
      this.fNeedPushNSContext = false;
      this.fNamespaceContext.pushContext();
    } 
    if (!this.fStringsInternalized) {
      paramString1 = (paramString1 != null) ? this.fSymbolTable.addSymbol(paramString1) : XMLSymbols.EMPTY_STRING;
      paramString2 = (paramString2 != null && paramString2.length() > 0) ? this.fSymbolTable.addSymbol(paramString2) : null;
    } else {
      if (paramString1 == null)
        paramString1 = XMLSymbols.EMPTY_STRING; 
      if (paramString2 != null && paramString2.length() == 0)
        paramString2 = null; 
    } 
    this.fNamespaceContext.declarePrefix(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.fNeedPushNSContext)
      this.fNamespaceContext.pushContext(); 
    this.fNeedPushNSContext = true;
    fillQName(this.fElementQName, paramString1, paramString2, paramString3);
    fillXMLAttributes(paramAttributes);
    if (!this.fNamespacePrefixes) {
      int i = this.fNamespaceContext.getDeclaredPrefixCount();
      if (i > 0)
        addNamespaceDeclarations(i); 
    } 
    try {
      this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    fillQName(this.fElementQName, paramString1, paramString2, paramString3);
    try {
      this.fSchemaDOMParser.endElement(this.fElementQName, null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } finally {
      this.fNamespaceContext.popContext();
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this.fTempString.setValues(paramArrayOfChar, paramInt1, paramInt2);
      this.fSchemaDOMParser.characters(this.fTempString, null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this.fTempString.setValues(paramArrayOfChar, paramInt1, paramInt2);
      this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      this.fTempString.setValues(paramString2.toCharArray(), 0, paramString2.length());
      this.fSchemaDOMParser.processingInstruction(paramString1, this.fTempString, null);
    } catch (XMLParseException xMLParseException) {
      convertToSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      convertToSAXException(xNIException);
    } 
  }
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  private void fillQName(QName paramQName, String paramString1, String paramString2, String paramString3) {
    if (!this.fStringsInternalized) {
      paramString1 = (paramString1 != null && paramString1.length() > 0) ? this.fSymbolTable.addSymbol(paramString1) : null;
      paramString2 = (paramString2 != null) ? this.fSymbolTable.addSymbol(paramString2) : XMLSymbols.EMPTY_STRING;
      paramString3 = (paramString3 != null) ? this.fSymbolTable.addSymbol(paramString3) : XMLSymbols.EMPTY_STRING;
    } else {
      if (paramString1 != null && paramString1.length() == 0)
        paramString1 = null; 
      if (paramString2 == null)
        paramString2 = XMLSymbols.EMPTY_STRING; 
      if (paramString3 == null)
        paramString3 = XMLSymbols.EMPTY_STRING; 
    } 
    String str = XMLSymbols.EMPTY_STRING;
    int i = paramString3.indexOf(':');
    if (i != -1) {
      str = this.fSymbolTable.addSymbol(paramString3.substring(0, i));
      if (paramString2 == XMLSymbols.EMPTY_STRING)
        paramString2 = this.fSymbolTable.addSymbol(paramString3.substring(i + 1)); 
    } else if (paramString2 == XMLSymbols.EMPTY_STRING) {
      paramString2 = paramString3;
    } 
    paramQName.setValues(str, paramString2, paramString3, paramString1);
  }
  
  private void fillXMLAttributes(Attributes paramAttributes) {
    this.fAttributes.removeAllAttributes();
    int i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      fillQName(this.fAttributeQName, paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getQName(b));
      String str = paramAttributes.getType(b);
      this.fAttributes.addAttributeNS(this.fAttributeQName, (str != null) ? str : XMLSymbols.fCDATASymbol, paramAttributes.getValue(b));
      this.fAttributes.setSpecified(b, true);
    } 
  }
  
  private void addNamespaceDeclarations(int paramInt) {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    for (byte b = 0; b < paramInt; b++) {
      str4 = this.fNamespaceContext.getDeclaredPrefixAt(b);
      str5 = this.fNamespaceContext.getURI(str4);
      if (str4.length() > 0) {
        str1 = XMLSymbols.PREFIX_XMLNS;
        str2 = str4;
        this.fStringBuffer.clear();
        this.fStringBuffer.append(str1);
        this.fStringBuffer.append(':');
        this.fStringBuffer.append(str2);
        str3 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
      } else {
        str1 = XMLSymbols.EMPTY_STRING;
        str2 = XMLSymbols.PREFIX_XMLNS;
        str3 = XMLSymbols.PREFIX_XMLNS;
      } 
      this.fAttributeQName.setValues(str1, str2, str3, NamespaceContext.XMLNS_URI);
      this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, (str5 != null) ? str5 : XMLSymbols.EMPTY_STRING);
    } 
  }
  
  public void reset(SchemaDOMParser paramSchemaDOMParser, SymbolTable paramSymbolTable, boolean paramBoolean1, boolean paramBoolean2) {
    this.fSchemaDOMParser = paramSchemaDOMParser;
    this.fSymbolTable = paramSymbolTable;
    this.fNamespacePrefixes = paramBoolean1;
    this.fStringsInternalized = paramBoolean2;
  }
  
  static void convertToSAXParseException(XMLParseException paramXMLParseException) throws SAXException {
    Exception exception = paramXMLParseException.getException();
    if (exception == null) {
      LocatorImpl locatorImpl = new LocatorImpl();
      locatorImpl.setPublicId(paramXMLParseException.getPublicId());
      locatorImpl.setSystemId(paramXMLParseException.getExpandedSystemId());
      locatorImpl.setLineNumber(paramXMLParseException.getLineNumber());
      locatorImpl.setColumnNumber(paramXMLParseException.getColumnNumber());
      throw new SAXParseException(paramXMLParseException.getMessage(), locatorImpl);
    } 
    if (exception instanceof SAXException)
      throw (SAXException)exception; 
    throw new SAXException(exception);
  }
  
  static void convertToSAXException(XNIException paramXNIException) throws SAXException {
    Exception exception = paramXNIException.getException();
    if (exception == null)
      throw new SAXException(paramXNIException.getMessage()); 
    if (exception instanceof SAXException)
      throw (SAXException)exception; 
    throw new SAXException(exception);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\SchemaContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */