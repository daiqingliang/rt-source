package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import java.io.IOException;

public class XMLNSDocumentScannerImpl extends XMLDocumentScannerImpl {
  protected boolean fBindNamespaces;
  
  protected boolean fPerformValidation;
  
  protected boolean fNotAddNSDeclAsAttribute = false;
  
  private XMLDTDValidatorFilter fDTDValidator;
  
  private boolean fXmlnsDeclared = false;
  
  public void reset(PropertyManager paramPropertyManager) {
    setPropertyManager(paramPropertyManager);
    super.reset(paramPropertyManager);
    this.fBindNamespaces = false;
    this.fNotAddNSDeclAsAttribute = !((Boolean)paramPropertyManager.getProperty("add-namespacedecl-as-attrbiute")).booleanValue();
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    super.reset(paramXMLComponentManager);
    this.fNotAddNSDeclAsAttribute = false;
    this.fPerformValidation = false;
    this.fBindNamespaces = false;
  }
  
  public int next() throws IOException, XNIException {
    if (this.fScannerLastState == 2 && this.fBindNamespaces) {
      this.fScannerLastState = -1;
      this.fNamespaceContext.popContext();
    } 
    return this.fScannerLastState = super.next();
  }
  
  public void setDTDValidator(XMLDTDValidatorFilter paramXMLDTDValidatorFilter) { this.fDTDValidator = paramXMLDTDValidatorFilter; }
  
  protected boolean scanStartElement() throws IOException, XNIException {
    if (this.fSkip && !this.fAdd) {
      QName qName = this.fElementStack.getNext();
      this.fSkip = this.fEntityScanner.skipString(qName.rawname);
      if (this.fSkip) {
        this.fElementStack.push();
        this.fElementQName = qName;
      } else {
        this.fElementStack.reposition();
      } 
    } 
    if (!this.fSkip || this.fAdd) {
      this.fElementQName = this.fElementStack.nextElement();
      if (this.fNamespaces) {
        this.fEntityScanner.scanQName(this.fElementQName, XMLScanner.NameType.ELEMENTSTART);
      } else {
        String str1 = this.fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
        this.fElementQName.setValues(null, str1, str1, null);
      } 
    } 
    if (this.fAdd)
      this.fElementStack.matchElement(this.fElementQName); 
    this.fCurrentElement = this.fElementQName;
    String str = this.fElementQName.rawname;
    checkDepth(str);
    if (this.fBindNamespaces) {
      this.fNamespaceContext.pushContext();
      if (this.fScannerState == 26 && this.fPerformValidation) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { str }, (short)1);
        if (this.fDoctypeName == null || !this.fDoctypeName.equals(str))
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, str }, (short)1); 
      } 
    } 
    this.fEmptyElement = false;
    this.fAttributes.removeAllAttributes();
    if (!seekCloseOfStartTag()) {
      this.fReadingAttributes = true;
      this.fAttributeCacheUsedCount = 0;
      this.fStringBufferIndex = 0;
      this.fAddDefaultAttr = true;
      this.fXmlnsDeclared = false;
      do {
        scanAttribute(this.fAttributes);
        if (this.fSecurityManager == null || this.fSecurityManager.isNoLimit(this.fElementAttributeLimit) || this.fAttributes.getLength() <= this.fElementAttributeLimit)
          continue; 
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { str, Integer.valueOf(this.fElementAttributeLimit) }, (short)2);
      } while (!seekCloseOfStartTag());
      this.fReadingAttributes = false;
    } 
    if (this.fBindNamespaces) {
      if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2); 
      String str1 = (this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
      this.fElementQName.uri = this.fNamespaceContext.getURI(str1);
      this.fCurrentElement.uri = this.fElementQName.uri;
      if (this.fElementQName.prefix == null && this.fElementQName.uri != null)
        this.fElementQName.prefix = XMLSymbols.EMPTY_STRING; 
      if (this.fElementQName.prefix != null && this.fElementQName.uri == null)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { this.fElementQName.prefix, this.fElementQName.rawname }, (short)2); 
      int i = this.fAttributes.getLength();
      for (byte b = 0; b < i; b++) {
        this.fAttributes.getName(b, this.fAttributeQName);
        String str2 = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        String str3 = this.fNamespaceContext.getURI(str2);
        if ((this.fAttributeQName.uri == null || this.fAttributeQName.uri != str3) && str2 != XMLSymbols.EMPTY_STRING) {
          this.fAttributeQName.uri = str3;
          if (str3 == null)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, str2 }, (short)2); 
          this.fAttributes.setURI(b, str3);
        } 
      } 
      if (i > 1) {
        QName qName = this.fAttributes.checkDuplicatesNS();
        if (qName != null)
          if (qName.uri != null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, qName.localpart, qName.uri }, (short)2);
          } else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, qName.rawname }, (short)2);
          }  
      } 
    } 
    if (this.fEmptyElement) {
      this.fMarkupDepth--;
      if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
        reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname }); 
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null); 
      this.fScanEndElement = true;
      this.fElementStack.popElement();
    } else {
      if (this.dtdGrammarUtil != null)
        this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes); 
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null); 
    } 
    return this.fEmptyElement;
  }
  
  protected void scanAttribute(XMLAttributesImpl paramXMLAttributesImpl) throws IOException, XNIException {
    this.fEntityScanner.scanQName(this.fAttributeQName, XMLScanner.NameType.ATTRIBUTENAME);
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(61, XMLScanner.NameType.ATTRIBUTE))
      reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    this.fEntityScanner.skipSpaces();
    int i = 0;
    boolean bool1 = (this.fHasExternalDTD && !this.fStandalone);
    XMLString xMLString = getString();
    String str1 = this.fAttributeQName.localpart;
    String str2 = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
    boolean bool2 = this.fBindNamespaces & ((str2 == XMLSymbols.PREFIX_XMLNS || (str2 == XMLSymbols.EMPTY_STRING && str1 == XMLSymbols.PREFIX_XMLNS)) ? 1 : 0);
    scanAttributeValue(xMLString, this.fTempString2, this.fAttributeQName.rawname, paramXMLAttributesImpl, i, bool1, this.fCurrentElement.rawname, bool2);
    String str3 = null;
    if (this.fBindNamespaces && bool2) {
      if (xMLString.length > this.fXMLNameLimit)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxXMLNameLimit", new Object[] { new String(xMLString.ch, xMLString.offset, xMLString.length), Integer.valueOf(xMLString.length), Integer.valueOf(this.fXMLNameLimit), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT) }(short)2); 
      String str = this.fSymbolTable.addSymbol(xMLString.ch, xMLString.offset, xMLString.length);
      str3 = str;
      if (str2 == XMLSymbols.PREFIX_XMLNS && str1 == XMLSymbols.PREFIX_XMLNS)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2); 
      if (str == NamespaceContext.XMLNS_URI)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2); 
      if (str1 == XMLSymbols.PREFIX_XML) {
        if (str != NamespaceContext.XML_URI)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2); 
      } else if (str == NamespaceContext.XML_URI) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
      } 
      str2 = (str1 != XMLSymbols.PREFIX_XMLNS) ? str1 : XMLSymbols.EMPTY_STRING;
      if (str2 == XMLSymbols.EMPTY_STRING && str1 == XMLSymbols.PREFIX_XMLNS)
        this.fAttributeQName.prefix = XMLSymbols.PREFIX_XMLNS; 
      if (str == XMLSymbols.EMPTY_STRING && str1 != XMLSymbols.PREFIX_XMLNS)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { this.fAttributeQName }, (short)2); 
      if (((NamespaceSupport)this.fNamespaceContext).containsPrefixInCurrentContext(str2))
        reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
      boolean bool = this.fNamespaceContext.declarePrefix(str2, (str.length() != 0) ? str : null);
      if (!bool) {
        if (this.fXmlnsDeclared)
          reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
        this.fXmlnsDeclared = true;
      } 
      if (this.fNotAddNSDeclAsAttribute)
        return; 
    } 
    if (this.fBindNamespaces) {
      i = paramXMLAttributesImpl.getLength();
      paramXMLAttributesImpl.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
    } else {
      int j = paramXMLAttributesImpl.getLength();
      i = paramXMLAttributesImpl.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
      if (j == paramXMLAttributesImpl.getLength())
        reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    } 
    paramXMLAttributesImpl.setValue(i, str3, xMLString);
    paramXMLAttributesImpl.setSpecified(i, true);
    if (this.fAttributeQName.prefix != null)
      paramXMLAttributesImpl.setURI(i, this.fNamespaceContext.getURI(this.fAttributeQName.prefix)); 
  }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver() { return new NSContentDriver(); }
  
  protected final class NSContentDriver extends XMLDocumentScannerImpl.ContentDriver {
    protected NSContentDriver() { super(XMLNSDocumentScannerImpl.this); }
    
    protected boolean scanRootElementHook() throws IOException, XNIException {
      reconfigurePipeline();
      if (XMLNSDocumentScannerImpl.this.scanStartElement()) {
        XMLNSDocumentScannerImpl.this.setScannerState(44);
        XMLNSDocumentScannerImpl.this.setDriver(XMLNSDocumentScannerImpl.this.fTrailingMiscDriver);
        return true;
      } 
      return false;
    }
    
    private void reconfigurePipeline() {
      if (XMLNSDocumentScannerImpl.this.fNamespaces && XMLNSDocumentScannerImpl.this.fDTDValidator == null) {
        XMLNSDocumentScannerImpl.this.fBindNamespaces = true;
      } else if (XMLNSDocumentScannerImpl.this.fNamespaces && !XMLNSDocumentScannerImpl.this.fDTDValidator.hasGrammar()) {
        XMLNSDocumentScannerImpl.this.fBindNamespaces = true;
        XMLNSDocumentScannerImpl.this.fPerformValidation = XMLNSDocumentScannerImpl.this.fDTDValidator.validate();
        XMLDocumentSource xMLDocumentSource = XMLNSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
        XMLDocumentHandler xMLDocumentHandler = XMLNSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
        xMLDocumentSource.setDocumentHandler(xMLDocumentHandler);
        if (xMLDocumentHandler != null)
          xMLDocumentHandler.setDocumentSource(xMLDocumentSource); 
        XMLNSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
        XMLNSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLNSDocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */