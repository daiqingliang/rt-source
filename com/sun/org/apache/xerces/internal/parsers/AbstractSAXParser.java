package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.Locale;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.LocatorImpl;

public abstract class AbstractSAXParser extends AbstractXMLDocumentParser implements PSVIProvider, Parser, XMLReader {
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  
  protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  
  protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/namespace-prefixes", "http://xml.org/sax/features/string-interning" };
  
  protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
  
  protected static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
  
  protected static final String DOM_NODE = "http://xml.org/sax/properties/dom-node";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/properties/declaration-handler", "http://xml.org/sax/properties/dom-node" };
  
  protected boolean fNamespaces;
  
  protected boolean fNamespacePrefixes = false;
  
  protected boolean fLexicalHandlerParameterEntities = true;
  
  protected boolean fStandalone;
  
  protected boolean fResolveDTDURIs = true;
  
  protected boolean fUseEntityResolver2 = true;
  
  protected boolean fXMLNSURIs = false;
  
  protected ContentHandler fContentHandler;
  
  protected DocumentHandler fDocumentHandler;
  
  protected NamespaceContext fNamespaceContext;
  
  protected DTDHandler fDTDHandler;
  
  protected DeclHandler fDeclHandler;
  
  protected LexicalHandler fLexicalHandler;
  
  protected QName fQName = new QName();
  
  protected boolean fParseInProgress = false;
  
  protected String fVersion;
  
  private final AttributesProxy fAttributesProxy = new AttributesProxy();
  
  private Augmentations fAugmentations = null;
  
  private static final int BUFFER_SIZE = 20;
  
  private char[] fCharBuffer = new char[20];
  
  protected SymbolHash fDeclaredAttrs = null;
  
  protected AbstractSAXParser(XMLParserConfiguration paramXMLParserConfiguration) {
    super(paramXMLParserConfiguration);
    paramXMLParserConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
    paramXMLParserConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    try {
      paramXMLParserConfiguration.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", false);
    } catch (XMLConfigurationException xMLConfigurationException) {}
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    this.fNamespaceContext = paramNamespaceContext;
    try {
      if (this.fDocumentHandler != null) {
        if (paramXMLLocator != null)
          this.fDocumentHandler.setDocumentLocator(new LocatorProxy(paramXMLLocator)); 
        this.fDocumentHandler.startDocument();
      } 
      if (this.fContentHandler != null) {
        if (paramXMLLocator != null)
          this.fContentHandler.setDocumentLocator(new LocatorProxy(paramXMLLocator)); 
        this.fContentHandler.startDocument();
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    this.fVersion = paramString1;
    this.fStandalone = "yes".equals(paramString3);
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    this.fInDTD = true;
    try {
      if (this.fLexicalHandler != null)
        this.fLexicalHandler.startDTD(paramString1, paramString2, paramString3); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
    if (this.fDeclHandler != null)
      this.fDeclaredAttrs = new SymbolHash(); 
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    try {
      if (paramAugmentations != null && Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED"))) {
        if (this.fContentHandler != null)
          this.fContentHandler.skippedEntity(paramString1); 
      } else if (this.fLexicalHandler != null) {
        this.fLexicalHandler.startEntity(paramString1);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    try {
      if ((paramAugmentations == null || !Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null)
        this.fLexicalHandler.endEntity(paramString); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDocumentHandler != null) {
        this.fAttributesProxy.setAttributes(paramXMLAttributes);
        this.fDocumentHandler.startElement(paramQName.rawname, this.fAttributesProxy);
      } 
      if (this.fContentHandler != null) {
        if (this.fNamespaces) {
          startNamespaceMapping();
          int i = paramXMLAttributes.getLength();
          if (!this.fNamespacePrefixes) {
            for (int j = i - 1; j >= 0; j--) {
              paramXMLAttributes.getName(j, this.fQName);
              if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS)
                paramXMLAttributes.removeAttributeAt(j); 
            } 
          } else if (!this.fXMLNSURIs) {
            for (int j = i - 1; j >= 0; j--) {
              paramXMLAttributes.getName(j, this.fQName);
              if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                this.fQName.prefix = "";
                this.fQName.uri = "";
                this.fQName.localpart = "";
                paramXMLAttributes.setName(j, this.fQName);
              } 
            } 
          } 
        } 
        this.fAugmentations = paramAugmentations;
        String str1 = (paramQName.uri != null) ? paramQName.uri : "";
        String str2 = this.fNamespaces ? paramQName.localpart : "";
        this.fAttributesProxy.setAttributes(paramXMLAttributes);
        this.fContentHandler.startElement(str1, str2, paramQName.rawname, this.fAttributesProxy);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (paramXMLString.length == 0)
      return; 
    try {
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.characters(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
      if (this.fContentHandler != null)
        this.fContentHandler.characters(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.ignorableWhitespace(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
      if (this.fContentHandler != null)
        this.fContentHandler.ignorableWhitespace(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.endElement(paramQName.rawname); 
      if (this.fContentHandler != null) {
        this.fAugmentations = paramAugmentations;
        String str1 = (paramQName.uri != null) ? paramQName.uri : "";
        String str2 = this.fNamespaces ? paramQName.localpart : "";
        this.fContentHandler.endElement(str1, str2, paramQName.rawname);
        if (this.fNamespaces)
          endNamespaceMapping(); 
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fLexicalHandler != null)
        this.fLexicalHandler.startCDATA(); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fLexicalHandler != null)
        this.fLexicalHandler.endCDATA(); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fLexicalHandler != null)
        this.fLexicalHandler.comment(paramXMLString.ch, 0, paramXMLString.length); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.processingInstruction(paramString, paramXMLString.toString()); 
      if (this.fContentHandler != null)
        this.fContentHandler.processingInstruction(paramString, paramXMLString.toString()); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.endDocument(); 
      if (this.fContentHandler != null)
        this.fContentHandler.endDocument(); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException { startParameterEntity("[dtd]", null, null, paramAugmentations); }
  
  public void endExternalSubset(Augmentations paramAugmentations) throws XNIException { endParameterEntity("[dtd]", paramAugmentations); }
  
  public void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    try {
      if (paramAugmentations != null && Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED"))) {
        if (this.fContentHandler != null)
          this.fContentHandler.skippedEntity(paramString1); 
      } else if (this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
        this.fLexicalHandler.startEntity(paramString1);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void endParameterEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    try {
      if ((paramAugmentations == null || !Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities)
        this.fLexicalHandler.endEntity(paramString); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDeclHandler != null)
        this.fDeclHandler.elementDecl(paramString1, paramString2); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDeclHandler != null) {
        String str1 = paramString1 + "<" + paramString2;
        if (this.fDeclaredAttrs.get(str1) != null)
          return; 
        this.fDeclaredAttrs.put(str1, Boolean.TRUE);
        if (paramString3.equals("NOTATION") || paramString3.equals("ENUMERATION")) {
          StringBuffer stringBuffer = new StringBuffer();
          if (paramString3.equals("NOTATION")) {
            stringBuffer.append(paramString3);
            stringBuffer.append(" (");
          } else {
            stringBuffer.append("(");
          } 
          for (byte b = 0; b < paramArrayOfString.length; b++) {
            stringBuffer.append(paramArrayOfString[b]);
            if (b < paramArrayOfString.length - 1)
              stringBuffer.append('|'); 
          } 
          stringBuffer.append(')');
          paramString3 = stringBuffer.toString();
        } 
        String str2 = (paramXMLString1 == null) ? null : paramXMLString1.toString();
        this.fDeclHandler.attributeDecl(paramString1, paramString2, paramString3, paramString4, str2);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDeclHandler != null)
        this.fDeclHandler.internalEntityDecl(paramString, paramXMLString1.toString()); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDeclHandler != null) {
        String str1 = paramXMLResourceIdentifier.getPublicId();
        String str2 = this.fResolveDTDURIs ? paramXMLResourceIdentifier.getExpandedSystemId() : paramXMLResourceIdentifier.getLiteralSystemId();
        this.fDeclHandler.externalEntityDecl(paramString, str1, str2);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDTDHandler != null) {
        String str1 = paramXMLResourceIdentifier.getPublicId();
        String str2 = this.fResolveDTDURIs ? paramXMLResourceIdentifier.getExpandedSystemId() : paramXMLResourceIdentifier.getLiteralSystemId();
        this.fDTDHandler.unparsedEntityDecl(paramString1, str1, str2, paramString2);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    try {
      if (this.fDTDHandler != null) {
        String str1 = paramXMLResourceIdentifier.getPublicId();
        String str2 = this.fResolveDTDURIs ? paramXMLResourceIdentifier.getExpandedSystemId() : paramXMLResourceIdentifier.getLiteralSystemId();
        this.fDTDHandler.notationDecl(paramString, str1, str2);
      } 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
  }
  
  public void endDTD(Augmentations paramAugmentations) throws XNIException {
    this.fInDTD = false;
    try {
      if (this.fLexicalHandler != null)
        this.fLexicalHandler.endDTD(); 
    } catch (SAXException sAXException) {
      throw new XNIException(sAXException);
    } 
    if (this.fDeclaredAttrs != null)
      this.fDeclaredAttrs.clear(); 
  }
  
  public void parse(String paramString) throws SAXException, IOException {
    XMLInputSource xMLInputSource = new XMLInputSource(null, paramString, null);
    try {
      parse(xMLInputSource);
    } catch (XMLParseException xMLParseException) {
      Exception exception = xMLParseException.getException();
      if (exception == null) {
        LocatorImpl locatorImpl = new LocatorImpl() {
            public String getXMLVersion() { return AbstractSAXParser.this.fVersion; }
            
            public String getEncoding() { return null; }
          };
        locatorImpl.setPublicId(xMLParseException.getPublicId());
        locatorImpl.setSystemId(xMLParseException.getExpandedSystemId());
        locatorImpl.setLineNumber(xMLParseException.getLineNumber());
        locatorImpl.setColumnNumber(xMLParseException.getColumnNumber());
        throw new SAXParseException(xMLParseException.getMessage(), locatorImpl);
      } 
      if (exception instanceof SAXException)
        throw (SAXException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw new SAXException(exception);
    } catch (XNIException xNIException) {
      Exception exception = xNIException.getException();
      if (exception == null)
        throw new SAXException(xNIException.getMessage()); 
      if (exception instanceof SAXException)
        throw (SAXException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw new SAXException(exception);
    } 
  }
  
  public void parse(InputSource paramInputSource) throws SAXException, IOException {
    try {
      XMLInputSource xMLInputSource = new XMLInputSource(paramInputSource.getPublicId(), paramInputSource.getSystemId(), null);
      xMLInputSource.setByteStream(paramInputSource.getByteStream());
      xMLInputSource.setCharacterStream(paramInputSource.getCharacterStream());
      xMLInputSource.setEncoding(paramInputSource.getEncoding());
      parse(xMLInputSource);
    } catch (XMLParseException xMLParseException) {
      Exception exception = xMLParseException.getException();
      if (exception == null) {
        LocatorImpl locatorImpl = new LocatorImpl() {
            public String getXMLVersion() { return AbstractSAXParser.this.fVersion; }
            
            public String getEncoding() { return null; }
          };
        locatorImpl.setPublicId(xMLParseException.getPublicId());
        locatorImpl.setSystemId(xMLParseException.getExpandedSystemId());
        locatorImpl.setLineNumber(xMLParseException.getLineNumber());
        locatorImpl.setColumnNumber(xMLParseException.getColumnNumber());
        throw new SAXParseException(xMLParseException.getMessage(), locatorImpl);
      } 
      if (exception instanceof SAXException)
        throw (SAXException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw new SAXException(exception);
    } catch (XNIException xNIException) {
      Exception exception = xNIException.getException();
      if (exception == null)
        throw new SAXException(xNIException.getMessage()); 
      if (exception instanceof SAXException)
        throw (SAXException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw new SAXException(exception);
    } 
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) {
    try {
      XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
      if (this.fUseEntityResolver2 && paramEntityResolver instanceof EntityResolver2) {
        if (xMLEntityResolver instanceof EntityResolver2Wrapper) {
          EntityResolver2Wrapper entityResolver2Wrapper = (EntityResolver2Wrapper)xMLEntityResolver;
          entityResolver2Wrapper.setEntityResolver((EntityResolver2)paramEntityResolver);
        } else {
          this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)paramEntityResolver));
        } 
      } else if (xMLEntityResolver instanceof EntityResolverWrapper) {
        EntityResolverWrapper entityResolverWrapper = (EntityResolverWrapper)xMLEntityResolver;
        entityResolverWrapper.setEntityResolver(paramEntityResolver);
      } else {
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(paramEntityResolver));
      } 
    } catch (XMLConfigurationException xMLConfigurationException) {}
  }
  
  public EntityResolver getEntityResolver() {
    EntityResolver entityResolver = null;
    try {
      XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
      if (xMLEntityResolver != null)
        if (xMLEntityResolver instanceof EntityResolverWrapper) {
          entityResolver = ((EntityResolverWrapper)xMLEntityResolver).getEntityResolver();
        } else if (xMLEntityResolver instanceof EntityResolver2Wrapper) {
          entityResolver = ((EntityResolver2Wrapper)xMLEntityResolver).getEntityResolver();
        }  
    } catch (XMLConfigurationException xMLConfigurationException) {}
    return entityResolver;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) {
    try {
      XMLErrorHandler xMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
      if (xMLErrorHandler instanceof ErrorHandlerWrapper) {
        ErrorHandlerWrapper errorHandlerWrapper = (ErrorHandlerWrapper)xMLErrorHandler;
        errorHandlerWrapper.setErrorHandler(paramErrorHandler);
      } else {
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(paramErrorHandler));
      } 
    } catch (XMLConfigurationException xMLConfigurationException) {}
  }
  
  public ErrorHandler getErrorHandler() {
    ErrorHandler errorHandler = null;
    try {
      XMLErrorHandler xMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
      if (xMLErrorHandler != null && xMLErrorHandler instanceof ErrorHandlerWrapper)
        errorHandler = ((ErrorHandlerWrapper)xMLErrorHandler).getErrorHandler(); 
    } catch (XMLConfigurationException xMLConfigurationException) {}
    return errorHandler;
  }
  
  public void setLocale(Locale paramLocale) throws SAXException { this.fConfiguration.setLocale(paramLocale); }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) { this.fDTDHandler = paramDTDHandler; }
  
  public void setDocumentHandler(DocumentHandler paramDocumentHandler) { this.fDocumentHandler = paramDocumentHandler; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.fContentHandler = paramContentHandler; }
  
  public ContentHandler getContentHandler() { return this.fContentHandler; }
  
  public DTDHandler getDTDHandler() { return this.fDTDHandler; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    try {
      if (paramString.startsWith("http://xml.org/sax/features/")) {
        int i = paramString.length() - "http://xml.org/sax/features/".length();
        if (i == "namespaces".length() && paramString.endsWith("namespaces")) {
          this.fConfiguration.setFeature(paramString, paramBoolean);
          this.fNamespaces = paramBoolean;
          return;
        } 
        if (i == "namespace-prefixes".length() && paramString.endsWith("namespace-prefixes")) {
          this.fConfiguration.setFeature(paramString, paramBoolean);
          this.fNamespacePrefixes = paramBoolean;
          return;
        } 
        if (i == "string-interning".length() && paramString.endsWith("string-interning")) {
          if (!paramBoolean)
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "false-not-supported", new Object[] { paramString })); 
          return;
        } 
        if (i == "lexical-handler/parameter-entities".length() && paramString.endsWith("lexical-handler/parameter-entities")) {
          this.fLexicalHandlerParameterEntities = paramBoolean;
          return;
        } 
        if (i == "resolve-dtd-uris".length() && paramString.endsWith("resolve-dtd-uris")) {
          this.fResolveDTDURIs = paramBoolean;
          return;
        } 
        if (i == "unicode-normalization-checking".length() && paramString.endsWith("unicode-normalization-checking")) {
          if (paramBoolean)
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "true-not-supported", new Object[] { paramString })); 
          return;
        } 
        if (i == "xmlns-uris".length() && paramString.endsWith("xmlns-uris")) {
          this.fXMLNSURIs = paramBoolean;
          return;
        } 
        if (i == "use-entity-resolver2".length() && paramString.endsWith("use-entity-resolver2")) {
          if (paramBoolean != this.fUseEntityResolver2) {
            this.fUseEntityResolver2 = paramBoolean;
            setEntityResolver(getEntityResolver());
          } 
          return;
        } 
        if ((i == "is-standalone".length() && paramString.endsWith("is-standalone")) || (i == "use-attributes2".length() && paramString.endsWith("use-attributes2")) || (i == "use-locator2".length() && paramString.endsWith("use-locator2")) || (i == "xml-1.1".length() && paramString.endsWith("xml-1.1")))
          throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-read-only", new Object[] { paramString })); 
      } else if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing") && paramBoolean && this.fConfiguration.getProperty("http://apache.org/xml/properties/security-manager") == null) {
        this.fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager());
      } 
      this.fConfiguration.setFeature(paramString, paramBoolean);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { str }));
    } 
  }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    try {
      if (paramString.startsWith("http://xml.org/sax/features/")) {
        int i = paramString.length() - "http://xml.org/sax/features/".length();
        if (i == "namespace-prefixes".length() && paramString.endsWith("namespace-prefixes"))
          return this.fConfiguration.getFeature(paramString); 
        if (i == "string-interning".length() && paramString.endsWith("string-interning"))
          return true; 
        if (i == "is-standalone".length() && paramString.endsWith("is-standalone"))
          return this.fStandalone; 
        if (i == "xml-1.1".length() && paramString.endsWith("xml-1.1"))
          return this.fConfiguration instanceof XML11Configurable; 
        if (i == "lexical-handler/parameter-entities".length() && paramString.endsWith("lexical-handler/parameter-entities"))
          return this.fLexicalHandlerParameterEntities; 
        if (i == "resolve-dtd-uris".length() && paramString.endsWith("resolve-dtd-uris"))
          return this.fResolveDTDURIs; 
        if (i == "xmlns-uris".length() && paramString.endsWith("xmlns-uris"))
          return this.fXMLNSURIs; 
        if (i == "unicode-normalization-checking".length() && paramString.endsWith("unicode-normalization-checking"))
          return false; 
        if (i == "use-entity-resolver2".length() && paramString.endsWith("use-entity-resolver2"))
          return this.fUseEntityResolver2; 
        if ((i == "use-attributes2".length() && paramString.endsWith("use-attributes2")) || (i == "use-locator2".length() && paramString.endsWith("use-locator2")))
          return true; 
      } 
      return this.fConfiguration.getFeature(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { str }));
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    try {
      if (paramString.startsWith("http://xml.org/sax/properties/")) {
        int i = paramString.length() - "http://xml.org/sax/properties/".length();
        if (i == "lexical-handler".length() && paramString.endsWith("lexical-handler")) {
          try {
            setLexicalHandler((LexicalHandler)paramObject);
          } catch (ClassCastException classCastException) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { paramString, "org.xml.sax.ext.LexicalHandler" }));
          } 
          return;
        } 
        if (i == "declaration-handler".length() && paramString.endsWith("declaration-handler")) {
          try {
            setDeclHandler((DeclHandler)paramObject);
          } catch (ClassCastException classCastException) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { paramString, "org.xml.sax.ext.DeclHandler" }));
          } 
          return;
        } 
        if ((i == "dom-node".length() && paramString.endsWith("dom-node")) || (i == "document-xml-version".length() && paramString.endsWith("document-xml-version")))
          throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-read-only", new Object[] { paramString })); 
      } 
      this.fConfiguration.setProperty(paramString, paramObject);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { str }));
    } 
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    try {
      if (paramString.startsWith("http://xml.org/sax/properties/")) {
        int i = paramString.length() - "http://xml.org/sax/properties/".length();
        if (i == "document-xml-version".length() && paramString.endsWith("document-xml-version"))
          return this.fVersion; 
        if (i == "lexical-handler".length() && paramString.endsWith("lexical-handler"))
          return getLexicalHandler(); 
        if (i == "declaration-handler".length() && paramString.endsWith("declaration-handler"))
          return getDeclHandler(); 
        if (i == "dom-node".length() && paramString.endsWith("dom-node"))
          throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "dom-node-read-not-supported", null)); 
      } 
      return this.fConfiguration.getProperty(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { str }));
    } 
  }
  
  protected void setDeclHandler(DeclHandler paramDeclHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (this.fParseInProgress)
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/declaration-handler" })); 
    this.fDeclHandler = paramDeclHandler;
  }
  
  protected DeclHandler getDeclHandler() throws SAXNotRecognizedException, SAXNotSupportedException { return this.fDeclHandler; }
  
  protected void setLexicalHandler(LexicalHandler paramLexicalHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (this.fParseInProgress)
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/lexical-handler" })); 
    this.fLexicalHandler = paramLexicalHandler;
  }
  
  protected LexicalHandler getLexicalHandler() throws SAXNotRecognizedException, SAXNotSupportedException { return this.fLexicalHandler; }
  
  protected final void startNamespaceMapping() throws SAXException {
    int i = this.fNamespaceContext.getDeclaredPrefixCount();
    if (i > 0) {
      String str1 = null;
      String str2 = null;
      for (byte b = 0; b < i; b++) {
        str1 = this.fNamespaceContext.getDeclaredPrefixAt(b);
        str2 = this.fNamespaceContext.getURI(str1);
        this.fContentHandler.startPrefixMapping(str1, (str2 == null) ? "" : str2);
      } 
    } 
  }
  
  protected final void endNamespaceMapping() throws SAXException {
    int i = this.fNamespaceContext.getDeclaredPrefixCount();
    if (i > 0)
      for (byte b = 0; b < i; b++)
        this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(b));  
  }
  
  public void reset() throws SAXException {
    super.reset();
    this.fInDTD = false;
    this.fVersion = "1.0";
    this.fStandalone = false;
    this.fNamespaces = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
    this.fNamespacePrefixes = this.fConfiguration.getFeature("http://xml.org/sax/features/namespace-prefixes");
    this.fAugmentations = null;
    this.fDeclaredAttrs = null;
  }
  
  public ElementPSVI getElementPSVI() { return (this.fAugmentations != null) ? (ElementPSVI)this.fAugmentations.getItem("ELEMENT_PSVI") : null; }
  
  public AttributePSVI getAttributePSVI(int paramInt) { return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(paramInt).getItem("ATTRIBUTE_PSVI"); }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2) { return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(paramString1, paramString2).getItem("ATTRIBUTE_PSVI"); }
  
  protected static final class AttributesProxy implements AttributeList, Attributes2 {
    protected XMLAttributes fAttributes;
    
    public void setAttributes(XMLAttributes param1XMLAttributes) { this.fAttributes = param1XMLAttributes; }
    
    public int getLength() { return this.fAttributes.getLength(); }
    
    public String getName(int param1Int) { return this.fAttributes.getQName(param1Int); }
    
    public String getQName(int param1Int) { return this.fAttributes.getQName(param1Int); }
    
    public String getURI(int param1Int) {
      String str = this.fAttributes.getURI(param1Int);
      return (str != null) ? str : "";
    }
    
    public String getLocalName(int param1Int) { return this.fAttributes.getLocalName(param1Int); }
    
    public String getType(int param1Int) { return this.fAttributes.getType(param1Int); }
    
    public String getType(String param1String) { return this.fAttributes.getType(param1String); }
    
    public String getType(String param1String1, String param1String2) { return param1String1.equals("") ? this.fAttributes.getType(null, param1String2) : this.fAttributes.getType(param1String1, param1String2); }
    
    public String getValue(int param1Int) { return this.fAttributes.getValue(param1Int); }
    
    public String getValue(String param1String) { return this.fAttributes.getValue(param1String); }
    
    public String getValue(String param1String1, String param1String2) { return param1String1.equals("") ? this.fAttributes.getValue(null, param1String2) : this.fAttributes.getValue(param1String1, param1String2); }
    
    public int getIndex(String param1String) { return this.fAttributes.getIndex(param1String); }
    
    public int getIndex(String param1String1, String param1String2) { return param1String1.equals("") ? this.fAttributes.getIndex(null, param1String2) : this.fAttributes.getIndex(param1String1, param1String2); }
    
    public boolean isDeclared(int param1Int) {
      if (param1Int < 0 || param1Int >= this.fAttributes.getLength())
        throw new ArrayIndexOutOfBoundsException(param1Int); 
      return Boolean.TRUE.equals(this.fAttributes.getAugmentations(param1Int).getItem("ATTRIBUTE_DECLARED"));
    }
    
    public boolean isDeclared(String param1String) throws SAXNotRecognizedException, SAXNotSupportedException {
      int i = getIndex(param1String);
      if (i == -1)
        throw new IllegalArgumentException(param1String); 
      return Boolean.TRUE.equals(this.fAttributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"));
    }
    
    public boolean isDeclared(String param1String1, String param1String2) {
      int i = getIndex(param1String1, param1String2);
      if (i == -1)
        throw new IllegalArgumentException(param1String2); 
      return Boolean.TRUE.equals(this.fAttributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"));
    }
    
    public boolean isSpecified(int param1Int) {
      if (param1Int < 0 || param1Int >= this.fAttributes.getLength())
        throw new ArrayIndexOutOfBoundsException(param1Int); 
      return this.fAttributes.isSpecified(param1Int);
    }
    
    public boolean isSpecified(String param1String) throws SAXNotRecognizedException, SAXNotSupportedException {
      int i = getIndex(param1String);
      if (i == -1)
        throw new IllegalArgumentException(param1String); 
      return this.fAttributes.isSpecified(i);
    }
    
    public boolean isSpecified(String param1String1, String param1String2) {
      int i = getIndex(param1String1, param1String2);
      if (i == -1)
        throw new IllegalArgumentException(param1String2); 
      return this.fAttributes.isSpecified(i);
    }
  }
  
  protected class LocatorProxy implements Locator2 {
    protected XMLLocator fLocator;
    
    public LocatorProxy(XMLLocator param1XMLLocator) { this.fLocator = param1XMLLocator; }
    
    public String getPublicId() { return this.fLocator.getPublicId(); }
    
    public String getSystemId() { return this.fLocator.getExpandedSystemId(); }
    
    public int getLineNumber() { return this.fLocator.getLineNumber(); }
    
    public int getColumnNumber() { return this.fLocator.getColumnNumber(); }
    
    public String getXMLVersion() { return this.fLocator.getXMLVersion(); }
    
    public String getEncoding() { return this.fLocator.getEncoding(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\AbstractSAXParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */