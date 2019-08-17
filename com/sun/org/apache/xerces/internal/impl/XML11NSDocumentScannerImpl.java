package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import java.io.IOException;

public class XML11NSDocumentScannerImpl extends XML11DocumentScannerImpl {
  protected boolean fBindNamespaces;
  
  protected boolean fPerformValidation;
  
  private XMLDTDValidatorFilter fDTDValidator;
  
  private boolean fSawSpace;
  
  public void setDTDValidator(XMLDTDValidatorFilter paramXMLDTDValidatorFilter) { this.fDTDValidator = paramXMLDTDValidatorFilter; }
  
  protected boolean scanStartElement() throws IOException, XNIException {
    this.fEntityScanner.scanQName(this.fElementQName, XMLScanner.NameType.ELEMENTSTART);
    String str = this.fElementQName.rawname;
    if (this.fBindNamespaces) {
      this.fNamespaceContext.pushContext();
      if (this.fScannerState == 26 && this.fPerformValidation) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { str }, (short)1);
        if (this.fDoctypeName == null || !this.fDoctypeName.equals(str))
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, str }, (short)1); 
      } 
    } 
    this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
    boolean bool = false;
    this.fAttributes.removeAllAttributes();
    while (true) {
      boolean bool1 = this.fEntityScanner.skipSpaces();
      int i = this.fEntityScanner.peekChar();
      if (i == 62) {
        this.fEntityScanner.scanChar(null);
        break;
      } 
      if (i == 47) {
        this.fEntityScanner.scanChar(null);
        if (!this.fEntityScanner.skipChar(62, null))
          reportFatalError("ElementUnterminated", new Object[] { str }); 
        bool = true;
        break;
      } 
      if ((!isValidNameStartChar(i) || !bool1) && (!isValidNameStartHighSurrogate(i) || !bool1))
        reportFatalError("ElementUnterminated", new Object[] { str }); 
      scanAttribute(this.fAttributes);
      if (this.fSecurityManager != null && !this.fSecurityManager.isNoLimit(this.fElementAttributeLimit) && this.fAttributes.getLength() > this.fElementAttributeLimit)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { str, new Integer(this.fElementAttributeLimit) }, (short)2); 
    } 
    if (this.fBindNamespaces) {
      if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2); 
      String str1 = (this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
      this.fElementQName.uri = this.fNamespaceContext.getURI(str1);
      this.fCurrentElement.uri = this.fElementQName.uri;
      if (this.fElementQName.prefix == null && this.fElementQName.uri != null) {
        this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
        this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
      } 
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
    if (bool) {
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
    return bool;
  }
  
  protected void scanStartElementName() {
    this.fEntityScanner.scanQName(this.fElementQName, XMLScanner.NameType.ELEMENTSTART);
    this.fSawSpace = this.fEntityScanner.skipSpaces();
  }
  
  protected boolean scanStartElementAfterName() throws IOException, XNIException {
    String str = this.fElementQName.rawname;
    if (this.fBindNamespaces) {
      this.fNamespaceContext.pushContext();
      if (this.fScannerState == 26 && this.fPerformValidation) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { str }, (short)1);
        if (this.fDoctypeName == null || !this.fDoctypeName.equals(str))
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, str }, (short)1); 
      } 
    } 
    this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
    boolean bool = false;
    this.fAttributes.removeAllAttributes();
    while (true) {
      int i = this.fEntityScanner.peekChar();
      if (i == 62) {
        this.fEntityScanner.scanChar(null);
        break;
      } 
      if (i == 47) {
        this.fEntityScanner.scanChar(null);
        if (!this.fEntityScanner.skipChar(62, null))
          reportFatalError("ElementUnterminated", new Object[] { str }); 
        bool = true;
        break;
      } 
      if ((!isValidNameStartChar(i) || !this.fSawSpace) && (!isValidNameStartHighSurrogate(i) || !this.fSawSpace))
        reportFatalError("ElementUnterminated", new Object[] { str }); 
      scanAttribute(this.fAttributes);
      this.fSawSpace = this.fEntityScanner.skipSpaces();
    } 
    if (this.fBindNamespaces) {
      if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2); 
      String str1 = (this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
      this.fElementQName.uri = this.fNamespaceContext.getURI(str1);
      this.fCurrentElement.uri = this.fElementQName.uri;
      if (this.fElementQName.prefix == null && this.fElementQName.uri != null) {
        this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
        this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
      } 
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
    if (this.fDocumentHandler != null)
      if (bool) {
        this.fMarkupDepth--;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
          reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname }); 
        this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
        if (this.fBindNamespaces)
          this.fNamespaceContext.popContext(); 
        this.fElementStack.popElement();
      } else {
        this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
      }  
    return bool;
  }
  
  protected void scanAttribute(XMLAttributesImpl paramXMLAttributesImpl) throws IOException, XNIException {
    int i;
    this.fEntityScanner.scanQName(this.fAttributeQName, XMLScanner.NameType.ATTRIBUTENAME);
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(61, XMLScanner.NameType.ATTRIBUTE))
      reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    this.fEntityScanner.skipSpaces();
    if (this.fBindNamespaces) {
      i = paramXMLAttributesImpl.getLength();
      paramXMLAttributesImpl.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
    } else {
      int j = paramXMLAttributesImpl.getLength();
      i = paramXMLAttributesImpl.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
      if (j == paramXMLAttributesImpl.getLength())
        reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    } 
    boolean bool1 = (this.fHasExternalDTD && !this.fStandalone);
    String str1 = this.fAttributeQName.localpart;
    String str2 = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
    boolean bool2 = this.fBindNamespaces & ((str2 == XMLSymbols.PREFIX_XMLNS || (str2 == XMLSymbols.EMPTY_STRING && str1 == XMLSymbols.PREFIX_XMLNS)) ? 1 : 0);
    scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, bool1, this.fCurrentElement.rawname, bool2);
    String str3 = this.fTempString.toString();
    paramXMLAttributesImpl.setValue(i, str3);
    paramXMLAttributesImpl.setNonNormalizedValue(i, this.fTempString2.toString());
    paramXMLAttributesImpl.setSpecified(i, true);
    if (this.fBindNamespaces)
      if (bool2) {
        if (str3.length() > this.fXMLNameLimit)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxXMLNameLimit", new Object[] { str3, Integer.valueOf(str3.length()), Integer.valueOf(this.fXMLNameLimit), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT) }, (short)2); 
        String str = this.fSymbolTable.addSymbol(str3);
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
        this.fNamespaceContext.declarePrefix(str2, (str.length() != 0) ? str : null);
        paramXMLAttributesImpl.setURI(i, this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
      } else if (this.fAttributeQName.prefix != null) {
        paramXMLAttributesImpl.setURI(i, this.fNamespaceContext.getURI(this.fAttributeQName.prefix));
      }  
  }
  
  protected int scanEndElement() throws IOException, XNIException {
    QName qName = this.fElementStack.popElement();
    if (!this.fEntityScanner.skipString(qName.rawname))
      reportFatalError("ETagRequired", new Object[] { qName.rawname }); 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(62, XMLScanner.NameType.ELEMENTEND))
      reportFatalError("ETagUnterminated", new Object[] { qName.rawname }); 
    this.fMarkupDepth--;
    this.fMarkupDepth--;
    if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
      reportFatalError("ElementEntityMismatch", new Object[] { qName.rawname }); 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endElement(qName, null); 
    if (this.dtdGrammarUtil != null)
      this.dtdGrammarUtil.endElement(qName); 
    return this.fMarkupDepth;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    super.reset(paramXMLComponentManager);
    this.fPerformValidation = false;
    this.fBindNamespaces = false;
  }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver() { return new NS11ContentDriver(); }
  
  public int next() throws IOException, XNIException {
    if (this.fScannerLastState == 2 && this.fBindNamespaces) {
      this.fScannerLastState = -1;
      this.fNamespaceContext.popContext();
    } 
    return this.fScannerLastState = super.next();
  }
  
  protected final class NS11ContentDriver extends XMLDocumentScannerImpl.ContentDriver {
    protected NS11ContentDriver() { super(XML11NSDocumentScannerImpl.this); }
    
    protected boolean scanRootElementHook() throws IOException, XNIException {
      if (XML11NSDocumentScannerImpl.this.fExternalSubsetResolver != null && !XML11NSDocumentScannerImpl.this.fSeenDoctypeDecl && !XML11NSDocumentScannerImpl.this.fDisallowDoctype && (XML11NSDocumentScannerImpl.this.fValidation || XML11NSDocumentScannerImpl.this.fLoadExternalDTD)) {
        XML11NSDocumentScannerImpl.this.scanStartElementName();
        resolveExternalSubsetAndRead();
        reconfigurePipeline();
        if (XML11NSDocumentScannerImpl.this.scanStartElementAfterName()) {
          XML11NSDocumentScannerImpl.this.setScannerState(44);
          XML11NSDocumentScannerImpl.this.setDriver(XML11NSDocumentScannerImpl.this.fTrailingMiscDriver);
          return true;
        } 
      } else {
        reconfigurePipeline();
        if (XML11NSDocumentScannerImpl.this.scanStartElement()) {
          XML11NSDocumentScannerImpl.this.setScannerState(44);
          XML11NSDocumentScannerImpl.this.setDriver(XML11NSDocumentScannerImpl.this.fTrailingMiscDriver);
          return true;
        } 
      } 
      return false;
    }
    
    private void reconfigurePipeline() {
      if (XML11NSDocumentScannerImpl.this.fDTDValidator == null) {
        XML11NSDocumentScannerImpl.this.fBindNamespaces = true;
      } else if (!XML11NSDocumentScannerImpl.this.fDTDValidator.hasGrammar()) {
        XML11NSDocumentScannerImpl.this.fBindNamespaces = true;
        XML11NSDocumentScannerImpl.this.fPerformValidation = XML11NSDocumentScannerImpl.this.fDTDValidator.validate();
        XMLDocumentSource xMLDocumentSource = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
        XMLDocumentHandler xMLDocumentHandler = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
        xMLDocumentSource.setDocumentHandler(xMLDocumentHandler);
        if (xMLDocumentHandler != null)
          xMLDocumentHandler.setDocumentSource(xMLDocumentSource); 
        XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
        XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11NSDocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */