package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.EntityResolver2;

final class ValidatorHandlerImpl extends ValidatorHandler implements DTDHandler, EntityState, PSVIProvider, ValidatorHelper, XMLDocumentHandler {
  private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  
  protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  private XMLErrorReporter fErrorReporter;
  
  private NamespaceContext fNamespaceContext;
  
  private XMLSchemaValidator fSchemaValidator;
  
  private SymbolTable fSymbolTable;
  
  private ValidationManager fValidationManager;
  
  private XMLSchemaValidatorComponentManager fComponentManager;
  
  private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
  
  private boolean fNeedPushNSContext = true;
  
  private HashMap fUnparsedEntities = null;
  
  private boolean fStringsInternalized = false;
  
  private final QName fElementQName = new QName();
  
  private final QName fAttributeQName = new QName();
  
  private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  
  private final AttributesProxy fAttrAdapter = new AttributesProxy(this.fAttributes);
  
  private final XMLString fTempString = new XMLString();
  
  private ContentHandler fContentHandler = null;
  
  private final XMLSchemaTypeInfoProvider fTypeInfoProvider = new XMLSchemaTypeInfoProvider(null);
  
  private final ResolutionForwarder fResolutionForwarder = new ResolutionForwarder(null);
  
  public ValidatorHandlerImpl(XSGrammarPoolContainer paramXSGrammarPoolContainer) {
    this(new XMLSchemaValidatorComponentManager(paramXSGrammarPoolContainer));
    this.fComponentManager.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/namespace-prefixes" });
    this.fComponentManager.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    setErrorHandler(null);
    setResourceResolver(null);
  }
  
  public ValidatorHandlerImpl(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager) {
    this.fComponentManager = paramXMLSchemaValidatorComponentManager;
    this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fNamespaceContext = (NamespaceContext)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
    this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
    this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.fContentHandler = paramContentHandler; }
  
  public ContentHandler getContentHandler() { return this.fContentHandler; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this.fComponentManager.setErrorHandler(paramErrorHandler); }
  
  public ErrorHandler getErrorHandler() { return this.fComponentManager.getErrorHandler(); }
  
  public void setResourceResolver(LSResourceResolver paramLSResourceResolver) { this.fComponentManager.setResourceResolver(paramLSResourceResolver); }
  
  public LSResourceResolver getResourceResolver() { return this.fComponentManager.getResourceResolver(); }
  
  public TypeInfoProvider getTypeInfoProvider() { return this.fTypeInfoProvider; }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      return this.fComponentManager.getFeature(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str1 = xMLConfigurationException.getIdentifier();
      String str2 = (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) ? "feature-not-recognized" : "feature-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      this.fComponentManager.setFeature(paramString, paramBoolean);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str2;
      String str1 = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_ALLOWED)
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null)); 
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        str2 = "feature-not-recognized";
      } else {
        str2 = "feature-not-supported";
      } 
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      return this.fComponentManager.getProperty(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str1 = xMLConfigurationException.getIdentifier();
      String str2 = (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      this.fComponentManager.setProperty(paramString, paramObject);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str1 = xMLConfigurationException.getIdentifier();
      String str2 = (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
  }
  
  public boolean isEntityDeclared(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return false; }
  
  public boolean isEntityUnparsed(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return (this.fUnparsedEntities != null) ? this.fUnparsedEntities.containsKey(paramString) : 0; }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null)
      try {
        this.fContentHandler.startDocument();
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      }  
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null)
      try {
        this.fContentHandler.processingInstruction(paramString, paramXMLString.toString());
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      }  
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null)
      try {
        this.fTypeInfoProvider.beginStartElement(paramAugmentations, paramXMLAttributes);
        this.fContentHandler.startElement((paramQName.uri != null) ? paramQName.uri : XMLSymbols.EMPTY_STRING, paramQName.localpart, paramQName.rawname, this.fAttrAdapter);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } finally {
        this.fTypeInfoProvider.finishStartElement();
      }  
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null) {
      if (paramXMLString.length == 0)
        return; 
      try {
        this.fContentHandler.characters(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } 
    } 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null)
      try {
        this.fContentHandler.ignorableWhitespace(paramXMLString.ch, paramXMLString.offset, paramXMLString.length);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      }  
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null)
      try {
        this.fTypeInfoProvider.beginEndElement(paramAugmentations);
        this.fContentHandler.endElement((paramQName.uri != null) ? paramQName.uri : XMLSymbols.EMPTY_STRING, paramQName.localpart, paramQName.rawname);
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      } finally {
        this.fTypeInfoProvider.finishEndElement();
      }  
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    if (this.fContentHandler != null)
      try {
        this.fContentHandler.endDocument();
      } catch (SAXException sAXException) {
        throw new XNIException(sAXException);
      }  
  }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) {}
  
  public XMLDocumentSource getDocumentSource() { return this.fSchemaValidator; }
  
  public void setDocumentLocator(Locator paramLocator) {
    this.fSAXLocatorWrapper.setLocator(paramLocator);
    if (this.fContentHandler != null)
      this.fContentHandler.setDocumentLocator(paramLocator); 
  }
  
  public void startDocument() throws SAXException {
    this.fComponentManager.reset();
    this.fSchemaValidator.setDocumentHandler(this);
    this.fValidationManager.setEntityState(this);
    this.fTypeInfoProvider.finishStartElement();
    this.fNeedPushNSContext = true;
    if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty())
      this.fUnparsedEntities.clear(); 
    this.fErrorReporter.setDocumentLocator(this.fSAXLocatorWrapper);
    try {
      this.fSchemaValidator.startDocument(this.fSAXLocatorWrapper, this.fSAXLocatorWrapper.getEncoding(), this.fNamespaceContext, null);
    } catch (XMLParseException xMLParseException) {
      throw Util.toSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      throw Util.toSAXException(xNIException);
    } 
  }
  
  public void endDocument() throws SAXException {
    this.fSAXLocatorWrapper.setLocator(null);
    try {
      this.fSchemaValidator.endDocument(null);
    } catch (XMLParseException xMLParseException) {
      throw Util.toSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      throw Util.toSAXException(xNIException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    String str2;
    String str1;
    if (!this.fStringsInternalized) {
      str1 = (paramString1 != null) ? this.fSymbolTable.addSymbol(paramString1) : XMLSymbols.EMPTY_STRING;
      str2 = (paramString2 != null && paramString2.length() > 0) ? this.fSymbolTable.addSymbol(paramString2) : null;
    } else {
      str1 = (paramString1 != null) ? paramString1 : XMLSymbols.EMPTY_STRING;
      str2 = (paramString2 != null && paramString2.length() > 0) ? paramString2 : null;
    } 
    if (this.fNeedPushNSContext) {
      this.fNeedPushNSContext = false;
      this.fNamespaceContext.pushContext();
    } 
    this.fNamespaceContext.declarePrefix(str1, str2);
    if (this.fContentHandler != null)
      this.fContentHandler.startPrefixMapping(paramString1, paramString2); 
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {
    if (this.fContentHandler != null)
      this.fContentHandler.endPrefixMapping(paramString); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.fNeedPushNSContext)
      this.fNamespaceContext.pushContext(); 
    this.fNeedPushNSContext = true;
    fillQName(this.fElementQName, paramString1, paramString2, paramString3);
    if (paramAttributes instanceof Attributes2) {
      fillXMLAttributes2((Attributes2)paramAttributes);
    } else {
      fillXMLAttributes(paramAttributes);
    } 
    try {
      this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
    } catch (XMLParseException xMLParseException) {
      throw Util.toSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      throw Util.toSAXException(xNIException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    fillQName(this.fElementQName, paramString1, paramString2, paramString3);
    try {
      this.fSchemaValidator.endElement(this.fElementQName, null);
    } catch (XMLParseException xMLParseException) {
      throw Util.toSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      throw Util.toSAXException(xNIException);
    } finally {
      this.fNamespaceContext.popContext();
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this.fTempString.setValues(paramArrayOfChar, paramInt1, paramInt2);
      this.fSchemaValidator.characters(this.fTempString, null);
    } catch (XMLParseException xMLParseException) {
      throw Util.toSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      throw Util.toSAXException(xNIException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this.fTempString.setValues(paramArrayOfChar, paramInt1, paramInt2);
      this.fSchemaValidator.ignorableWhitespace(this.fTempString, null);
    } catch (XMLParseException xMLParseException) {
      throw Util.toSAXParseException(xMLParseException);
    } catch (XNIException xNIException) {
      throw Util.toSAXException(xNIException);
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (this.fContentHandler != null)
      this.fContentHandler.processingInstruction(paramString1, paramString2); 
  }
  
  public void skippedEntity(String paramString) throws SAXException {
    if (this.fContentHandler != null)
      this.fContentHandler.skippedEntity(paramString); 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    if (this.fUnparsedEntities == null)
      this.fUnparsedEntities = new HashMap(); 
    this.fUnparsedEntities.put(paramString1, paramString1);
  }
  
  public void validate(Source paramSource, Result paramResult) throws SAXException, IOException {
    if (paramResult instanceof SAXResult || paramResult == null) {
      SAXSource sAXSource = (SAXSource)paramSource;
      SAXResult sAXResult = (SAXResult)paramResult;
      if (paramResult != null)
        setContentHandler(sAXResult.getHandler()); 
      try {
        XMLReader xMLReader = sAXSource.getXMLReader();
        if (xMLReader == null) {
          xMLReader = JdkXmlUtils.getXMLReader(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"), this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"));
          try {
            if (xMLReader instanceof com.sun.org.apache.xerces.internal.parsers.SAXParser) {
              XMLSecurityManager xMLSecurityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
              if (xMLSecurityManager != null)
                try {
                  xMLReader.setProperty("http://apache.org/xml/properties/security-manager", xMLSecurityManager);
                } catch (SAXException sAXException) {} 
              try {
                XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
                xMLReader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD));
              } catch (SAXException sAXException) {
                XMLSecurityManager.printWarning(xMLReader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", sAXException);
              } 
            } 
          } catch (Exception exception) {
            throw new FactoryConfigurationError(exception);
          } 
        } 
        try {
          this.fStringsInternalized = xMLReader.getFeature("http://xml.org/sax/features/string-interning");
        } catch (SAXException sAXException) {
          this.fStringsInternalized = false;
        } 
        ErrorHandler errorHandler = this.fComponentManager.getErrorHandler();
        xMLReader.setErrorHandler((errorHandler != null) ? errorHandler : DraconianErrorHandler.getInstance());
        xMLReader.setEntityResolver(this.fResolutionForwarder);
        this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
        xMLReader.setContentHandler(this);
        xMLReader.setDTDHandler(this);
        InputSource inputSource = sAXSource.getInputSource();
        xMLReader.parse(inputSource);
      } finally {
        setContentHandler(null);
      } 
      return;
    } 
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
  
  public ElementPSVI getElementPSVI() { return this.fTypeInfoProvider.getElementPSVI(); }
  
  public AttributePSVI getAttributePSVI(int paramInt) { return this.fTypeInfoProvider.getAttributePSVI(paramInt); }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2) { return this.fTypeInfoProvider.getAttributePSVIByName(paramString1, paramString2); }
  
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
    if (i != -1)
      str = this.fSymbolTable.addSymbol(paramString3.substring(0, i)); 
    paramQName.setValues(str, paramString2, paramString3, paramString1);
  }
  
  private void fillXMLAttributes(Attributes paramAttributes) {
    this.fAttributes.removeAllAttributes();
    int i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      fillXMLAttribute(paramAttributes, b);
      this.fAttributes.setSpecified(b, true);
    } 
  }
  
  private void fillXMLAttributes2(Attributes2 paramAttributes2) {
    this.fAttributes.removeAllAttributes();
    int i = paramAttributes2.getLength();
    for (byte b = 0; b < i; b++) {
      fillXMLAttribute(paramAttributes2, b);
      this.fAttributes.setSpecified(b, paramAttributes2.isSpecified(b));
      if (paramAttributes2.isDeclared(b))
        this.fAttributes.getAugmentations(b).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE); 
    } 
  }
  
  private void fillXMLAttribute(Attributes paramAttributes, int paramInt) {
    fillQName(this.fAttributeQName, paramAttributes.getURI(paramInt), paramAttributes.getLocalName(paramInt), paramAttributes.getQName(paramInt));
    String str = paramAttributes.getType(paramInt);
    this.fAttributes.addAttributeNS(this.fAttributeQName, (str != null) ? str : XMLSymbols.fCDATASymbol, paramAttributes.getValue(paramInt));
  }
  
  static final class ResolutionForwarder implements EntityResolver2 {
    private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
    
    protected LSResourceResolver fEntityResolver;
    
    public ResolutionForwarder() throws SAXException {}
    
    public ResolutionForwarder(LSResourceResolver param1LSResourceResolver) { setEntityResolver(param1LSResourceResolver); }
    
    public void setEntityResolver(LSResourceResolver param1LSResourceResolver) { this.fEntityResolver = param1LSResourceResolver; }
    
    public LSResourceResolver getEntityResolver() { return this.fEntityResolver; }
    
    public InputSource getExternalSubset(String param1String1, String param1String2) throws SAXException, IOException { return null; }
    
    public InputSource resolveEntity(String param1String1, String param1String2, String param1String3, String param1String4) throws SAXException, IOException {
      if (this.fEntityResolver != null) {
        LSInput lSInput = this.fEntityResolver.resolveResource("http://www.w3.org/TR/REC-xml", null, param1String2, param1String4, param1String3);
        if (lSInput != null) {
          String str1 = lSInput.getPublicId();
          String str2 = lSInput.getSystemId();
          String str3 = lSInput.getBaseURI();
          Reader reader = lSInput.getCharacterStream();
          InputStream inputStream = lSInput.getByteStream();
          String str4 = lSInput.getStringData();
          String str5 = lSInput.getEncoding();
          InputSource inputSource = new InputSource();
          inputSource.setPublicId(str1);
          inputSource.setSystemId((str3 != null) ? resolveSystemId(param1String4, str3) : param1String4);
          if (reader != null) {
            inputSource.setCharacterStream(reader);
          } else if (inputStream != null) {
            inputSource.setByteStream(inputStream);
          } else if (str4 != null && str4.length() != 0) {
            inputSource.setCharacterStream(new StringReader(str4));
          } 
          inputSource.setEncoding(str5);
          return inputSource;
        } 
      } 
      return null;
    }
    
    public InputSource resolveEntity(String param1String1, String param1String2) throws SAXException, IOException { return resolveEntity(null, param1String1, null, param1String2); }
    
    private String resolveSystemId(String param1String1, String param1String2) {
      try {
        return XMLEntityManager.expandSystemId(param1String1, param1String2, false);
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        return param1String1;
      } 
    }
  }
  
  private class XMLSchemaTypeInfoProvider extends TypeInfoProvider {
    private Augmentations fElementAugs;
    
    private XMLAttributes fAttributes;
    
    private boolean fInStartElement = false;
    
    private boolean fInEndElement = false;
    
    private XMLSchemaTypeInfoProvider() {}
    
    void beginStartElement(Augmentations param1Augmentations, XMLAttributes param1XMLAttributes) {
      this.fInStartElement = true;
      this.fElementAugs = param1Augmentations;
      this.fAttributes = param1XMLAttributes;
    }
    
    void finishStartElement() throws SAXException {
      this.fInStartElement = false;
      this.fElementAugs = null;
      this.fAttributes = null;
    }
    
    void beginEndElement(Augmentations param1Augmentations) throws XNIException {
      this.fInEndElement = true;
      this.fElementAugs = param1Augmentations;
    }
    
    void finishEndElement() throws SAXException {
      this.fInEndElement = false;
      this.fElementAugs = null;
    }
    
    private void checkState(boolean param1Boolean) {
      if (!this.fInStartElement && (!this.fInEndElement || !param1Boolean))
        throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalState", null)); 
    }
    
    public TypeInfo getAttributeTypeInfo(int param1Int) {
      checkState(false);
      return getAttributeType(param1Int);
    }
    
    private TypeInfo getAttributeType(int param1Int) {
      checkState(false);
      if (param1Int < 0 || this.fAttributes.getLength() <= param1Int)
        throw new IndexOutOfBoundsException(Integer.toString(param1Int)); 
      Augmentations augmentations = this.fAttributes.getAugmentations(param1Int);
      if (augmentations == null)
        return null; 
      AttributePSVI attributePSVI = (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
      return getTypeInfoFromPSVI(attributePSVI);
    }
    
    public TypeInfo getAttributeTypeInfo(String param1String1, String param1String2) {
      checkState(false);
      return getAttributeTypeInfo(this.fAttributes.getIndex(param1String1, param1String2));
    }
    
    public TypeInfo getAttributeTypeInfo(String param1String) {
      checkState(false);
      return getAttributeTypeInfo(this.fAttributes.getIndex(param1String));
    }
    
    public TypeInfo getElementTypeInfo() {
      checkState(true);
      if (this.fElementAugs == null)
        return null; 
      ElementPSVI elementPSVI = (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI");
      return getTypeInfoFromPSVI(elementPSVI);
    }
    
    private TypeInfo getTypeInfoFromPSVI(ItemPSVI param1ItemPSVI) {
      if (param1ItemPSVI == null)
        return null; 
      if (param1ItemPSVI.getValidity() == 2) {
        XSSimpleTypeDefinition xSSimpleTypeDefinition = param1ItemPSVI.getMemberTypeDefinition();
        if (xSSimpleTypeDefinition != null)
          return (xSSimpleTypeDefinition instanceof TypeInfo) ? (TypeInfo)xSSimpleTypeDefinition : null; 
      } 
      XSTypeDefinition xSTypeDefinition = param1ItemPSVI.getTypeDefinition();
      return (xSTypeDefinition != null) ? ((xSTypeDefinition instanceof TypeInfo) ? (TypeInfo)xSTypeDefinition : null) : null;
    }
    
    public boolean isIdAttribute(int param1Int) {
      checkState(false);
      XSSimpleType xSSimpleType = (XSSimpleType)getAttributeType(param1Int);
      return (xSSimpleType == null) ? false : xSSimpleType.isIDType();
    }
    
    public boolean isSpecified(int param1Int) {
      checkState(false);
      return this.fAttributes.isSpecified(param1Int);
    }
    
    ElementPSVI getElementPSVI() { return (this.fElementAugs != null) ? (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI") : null; }
    
    AttributePSVI getAttributePSVI(int param1Int) {
      if (this.fAttributes != null) {
        Augmentations augmentations = this.fAttributes.getAugmentations(param1Int);
        if (augmentations != null)
          return (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI"); 
      } 
      return null;
    }
    
    AttributePSVI getAttributePSVIByName(String param1String1, String param1String2) {
      if (this.fAttributes != null) {
        Augmentations augmentations = this.fAttributes.getAugmentations(param1String1, param1String2);
        if (augmentations != null)
          return (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI"); 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ValidatorHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */