package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11NSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import jdk.xml.internal.JdkXmlUtils;

public class XML11Configuration extends ParserConfigurationSettings implements XMLPullParserConfiguration, XML11Configurable {
  protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  
  protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  
  protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  
  protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  
  protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  
  protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  
  protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  
  protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
  
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  
  protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
  
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLInputSource fInputSource;
  
  protected ValidationManager fValidationManager;
  
  protected XMLVersionDetector fVersionDetector;
  
  protected XMLLocator fLocator;
  
  protected Locale fLocale;
  
  protected ArrayList<XMLComponent> fComponents = new ArrayList();
  
  protected ArrayList<XMLComponent> fXML11Components = null;
  
  protected ArrayList<XMLComponent> fCommonComponents = null;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDTDHandler fDTDHandler;
  
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  
  protected XMLDocumentSource fLastComponent;
  
  protected boolean fParseInProgress = false;
  
  protected boolean fConfigUpdated = false;
  
  protected DTDDVFactory fDatatypeValidatorFactory;
  
  protected XMLNSDocumentScannerImpl fNamespaceScanner;
  
  protected XMLDocumentScannerImpl fNonNSScanner;
  
  protected XMLDTDValidator fDTDValidator;
  
  protected XMLDTDValidator fNonNSDTDValidator;
  
  protected XMLDTDScanner fDTDScanner;
  
  protected XMLDTDProcessor fDTDProcessor;
  
  protected DTDDVFactory fXML11DatatypeFactory = null;
  
  protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
  
  protected XML11DocumentScannerImpl fXML11DocScanner = null;
  
  protected XML11NSDTDValidator fXML11NSDTDValidator = null;
  
  protected XML11DTDValidator fXML11DTDValidator = null;
  
  protected XML11DTDScannerImpl fXML11DTDScanner = null;
  
  protected XML11DTDProcessor fXML11DTDProcessor = null;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityManager fEntityManager;
  
  protected XMLSchemaValidator fSchemaValidator;
  
  protected XMLDocumentScanner fCurrentScanner;
  
  protected DTDDVFactory fCurrentDVFactory;
  
  protected XMLDTDScanner fCurrentDTDScanner;
  
  private boolean f11Initialized = false;
  
  public XML11Configuration() { this(null, null, null); }
  
  public XML11Configuration(SymbolTable paramSymbolTable) { this(paramSymbolTable, null, null); }
  
  public XML11Configuration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) { this(paramSymbolTable, paramXMLGrammarPool, null); }
  
  public XML11Configuration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager) {
    super(paramXMLComponentManager);
    this.fXML11Components = new ArrayList();
    this.fCommonComponents = new ArrayList();
    this.fFeatures = new HashMap();
    this.fProperties = new HashMap();
    String[] arrayOfString1 = { 
        "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", 
        "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings", "http://javax.xml.XMLConstants/feature/secure-processing", "jdk.xml.overrideDefaultParser" };
    addRecognizedFeatures(arrayOfString1);
    this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/schema/element-default", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/schema/augment-psvi", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/validate-annotations", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/honour-all-schemaLocations", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/namespace-growth", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/internal/tolerate-duplicates", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    this.fFeatures.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
    this.fFeatures.put("jdk.xml.overrideDefaultParser", Boolean.valueOf(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT));
    String[] arrayOfString2 = { 
        "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", 
        "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/validator/schema", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", 
        "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
    addRecognizedProperties(arrayOfString2);
    if (paramSymbolTable == null)
      paramSymbolTable = new SymbolTable(); 
    this.fSymbolTable = paramSymbolTable;
    this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
    this.fGrammarPool = paramXMLGrammarPool;
    if (this.fGrammarPool != null)
      this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool); 
    this.fEntityManager = new XMLEntityManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
    addCommonComponent(this.fEntityManager);
    this.fErrorReporter = new XMLErrorReporter();
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    addCommonComponent(this.fErrorReporter);
    this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
    addComponent(this.fNamespaceScanner);
    this.fDTDScanner = new XMLDTDScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
    addComponent((XMLComponent)this.fDTDScanner);
    this.fDTDProcessor = new XMLDTDProcessor();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
    addComponent(this.fDTDProcessor);
    this.fDTDValidator = new XMLNSDTDValidator();
    this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
    addComponent(this.fDTDValidator);
    this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
    this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
    this.fValidationManager = new ValidationManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
    this.fVersionDetector = new XMLVersionDetector();
    if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
      XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
    } 
    try {
      setLocale(Locale.getDefault());
    } catch (XNIException xNIException) {}
    this.fConfigUpdated = false;
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource) throws XMLConfigurationException, IOException { this.fInputSource = paramXMLInputSource; }
  
  public void setLocale(Locale paramLocale) throws XNIException {
    this.fLocale = paramLocale;
    this.fErrorReporter.setLocale(paramLocale);
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) {
    this.fDocumentHandler = paramXMLDocumentHandler;
    if (this.fLastComponent != null) {
      this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fLastComponent); 
    } 
  }
  
  public XMLDocumentHandler getDocumentHandler() { return this.fDocumentHandler; }
  
  public void setDTDHandler(XMLDTDHandler paramXMLDTDHandler) { this.fDTDHandler = paramXMLDTDHandler; }
  
  public XMLDTDHandler getDTDHandler() { return this.fDTDHandler; }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler paramXMLDTDContentModelHandler) { this.fDTDContentModelHandler = paramXMLDTDContentModelHandler; }
  
  public XMLDTDContentModelHandler getDTDContentModelHandler() { return this.fDTDContentModelHandler; }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver) { this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver); }
  
  public XMLEntityResolver getEntityResolver() { return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver"); }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler) { this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler); }
  
  public XMLErrorHandler getErrorHandler() { return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler"); }
  
  public void cleanup() { this.fEntityManager.closeReaders(); }
  
  public void parse(XMLInputSource paramXMLInputSource) throws XMLConfigurationException, IOException {
    if (this.fParseInProgress)
      throw new XNIException("FWK005 parse may not be called while parsing."); 
    this.fParseInProgress = true;
    try {
      setInputSource(paramXMLInputSource);
      parse(true);
    } catch (XNIException xNIException) {
      throw xNIException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      throw new XNIException(exception);
    } finally {
      this.fParseInProgress = false;
      cleanup();
    } 
  }
  
  public boolean parse(boolean paramBoolean) throws XNIException, IOException {
    if (this.fInputSource != null)
      try {
        this.fValidationManager.reset();
        this.fVersionDetector.reset(this);
        this.fConfigUpdated = true;
        resetCommon();
        short s = this.fVersionDetector.determineDocVersion(this.fInputSource);
        if (s == 2) {
          initXML11Components();
          configureXML11Pipeline();
          resetXML11();
        } else {
          configurePipeline();
          reset();
        } 
        this.fConfigUpdated = false;
        this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, s);
        this.fInputSource = null;
      } catch (XNIException xNIException) {
        throw xNIException;
      } catch (IOException iOException) {
        throw iOException;
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new XNIException(exception);
      }  
    try {
      return this.fCurrentScanner.scanDocument(paramBoolean);
    } catch (XNIException xNIException) {
      throw xNIException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      throw new XNIException(exception);
    } 
  }
  
  public FeatureState getFeatureState(String paramString) throws XMLConfigurationException { return paramString.equals("http://apache.org/xml/features/internal/parser-settings") ? FeatureState.is(this.fConfigUpdated) : super.getFeatureState(paramString); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    this.fConfigUpdated = true;
    int i = this.fComponents.size();
    byte b;
    for (b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(b);
      xMLComponent.setFeature(paramString, paramBoolean);
    } 
    i = this.fCommonComponents.size();
    for (b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fCommonComponents.get(b);
      xMLComponent.setFeature(paramString, paramBoolean);
    } 
    i = this.fXML11Components.size();
    for (b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fXML11Components.get(b);
      try {
        xMLComponent.setFeature(paramString, paramBoolean);
      } catch (Exception exception) {}
    } 
    super.setFeature(paramString, paramBoolean);
  }
  
  public PropertyState getPropertyState(String paramString) throws XMLConfigurationException { return "http://apache.org/xml/properties/locale".equals(paramString) ? PropertyState.is(getLocale()) : super.getPropertyState(paramString); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    this.fConfigUpdated = true;
    if ("http://apache.org/xml/properties/locale".equals(paramString))
      setLocale((Locale)paramObject); 
    int i = this.fComponents.size();
    byte b;
    for (b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(b);
      xMLComponent.setProperty(paramString, paramObject);
    } 
    i = this.fCommonComponents.size();
    for (b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fCommonComponents.get(b);
      xMLComponent.setProperty(paramString, paramObject);
    } 
    i = this.fXML11Components.size();
    for (b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fXML11Components.get(b);
      try {
        xMLComponent.setProperty(paramString, paramObject);
      } catch (Exception exception) {}
    } 
    super.setProperty(paramString, paramObject);
  }
  
  public Locale getLocale() { return this.fLocale; }
  
  protected void reset() {
    int i = this.fComponents.size();
    for (byte b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(b);
      xMLComponent.reset(this);
    } 
  }
  
  protected void resetCommon() {
    int i = this.fCommonComponents.size();
    for (byte b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fCommonComponents.get(b);
      xMLComponent.reset(this);
    } 
  }
  
  protected void resetXML11() {
    int i = this.fXML11Components.size();
    for (byte b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fXML11Components.get(b);
      xMLComponent.reset(this);
    } 
  }
  
  protected void configureXML11Pipeline() {
    if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
      this.fCurrentDVFactory = this.fXML11DatatypeFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    } 
    if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
      this.fCurrentDTDScanner = this.fXML11DTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fXML11DTDProcessor);
    } 
    this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
    this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
    this.fXML11DTDProcessor.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null)
      this.fDTDHandler.setDTDSource(this.fXML11DTDProcessor); 
    this.fXML11DTDScanner.setDTDContentModelHandler(this.fXML11DTDProcessor);
    this.fXML11DTDProcessor.setDTDContentModelSource(this.fXML11DTDScanner);
    this.fXML11DTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDProcessor); 
    if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
      if (this.fCurrentScanner != this.fXML11NSDocScanner) {
        this.fCurrentScanner = this.fXML11NSDocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11NSDocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11NSDTDValidator);
      } 
      this.fXML11NSDocScanner.setDTDValidator(this.fXML11NSDTDValidator);
      this.fXML11NSDocScanner.setDocumentHandler(this.fXML11NSDTDValidator);
      this.fXML11NSDTDValidator.setDocumentSource(this.fXML11NSDocScanner);
      this.fXML11NSDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fXML11NSDTDValidator); 
      this.fLastComponent = this.fXML11NSDTDValidator;
    } else {
      if (this.fXML11DocScanner == null) {
        this.fXML11DocScanner = new XML11DocumentScannerImpl();
        addXML11Component(this.fXML11DocScanner);
        this.fXML11DTDValidator = new XML11DTDValidator();
        addXML11Component(this.fXML11DTDValidator);
      } 
      if (this.fCurrentScanner != this.fXML11DocScanner) {
        this.fCurrentScanner = this.fXML11DocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11DocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11DTDValidator);
      } 
      this.fXML11DocScanner.setDocumentHandler(this.fXML11DTDValidator);
      this.fXML11DTDValidator.setDocumentSource(this.fXML11DocScanner);
      this.fXML11DTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fXML11DTDValidator); 
      this.fLastComponent = this.fXML11DTDValidator;
    } 
    if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
      if (this.fSchemaValidator == null) {
        this.fSchemaValidator = new XMLSchemaValidator();
        setProperty("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
        addCommonComponent(this.fSchemaValidator);
        this.fSchemaValidator.reset(this);
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
          XSMessageFormatter xSMessageFormatter = new XSMessageFormatter();
          this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xSMessageFormatter);
        } 
      } 
      this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
      this.fSchemaValidator.setDocumentSource(this.fLastComponent);
      this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fSchemaValidator); 
      this.fLastComponent = this.fSchemaValidator;
    } 
  }
  
  protected void configurePipeline() {
    if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
      this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    } 
    if (this.fCurrentDTDScanner != this.fDTDScanner) {
      this.fCurrentDTDScanner = this.fDTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
    } 
    this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
    this.fDTDProcessor.setDTDSource(this.fDTDScanner);
    this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null)
      this.fDTDHandler.setDTDSource(this.fDTDProcessor); 
    this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
    this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
    this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor); 
    if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
      if (this.fCurrentScanner != this.fNamespaceScanner) {
        this.fCurrentScanner = this.fNamespaceScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
      } 
      this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
      this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
      this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
      this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fDTDValidator); 
      this.fLastComponent = this.fDTDValidator;
    } else {
      if (this.fNonNSScanner == null) {
        this.fNonNSScanner = new XMLDocumentScannerImpl();
        this.fNonNSDTDValidator = new XMLDTDValidator();
        addComponent(this.fNonNSScanner);
        addComponent(this.fNonNSDTDValidator);
      } 
      if (this.fCurrentScanner != this.fNonNSScanner) {
        this.fCurrentScanner = this.fNonNSScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fNonNSDTDValidator);
      } 
      this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
      this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
      this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator); 
      this.fLastComponent = this.fNonNSDTDValidator;
    } 
    if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
      if (this.fSchemaValidator == null) {
        this.fSchemaValidator = new XMLSchemaValidator();
        setProperty("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
        addCommonComponent(this.fSchemaValidator);
        this.fSchemaValidator.reset(this);
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
          XSMessageFormatter xSMessageFormatter = new XSMessageFormatter();
          this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xSMessageFormatter);
        } 
      } 
      this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
      this.fSchemaValidator.setDocumentSource(this.fLastComponent);
      this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fSchemaValidator); 
      this.fLastComponent = this.fSchemaValidator;
    } 
  }
  
  protected FeatureState checkFeature(String paramString) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/features/")) {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if (i == "validation/dynamic".length() && paramString.endsWith("validation/dynamic"))
        return FeatureState.RECOGNIZED; 
      if (i == "validation/default-attribute-values".length() && paramString.endsWith("validation/default-attribute-values"))
        return FeatureState.NOT_SUPPORTED; 
      if (i == "validation/validate-content-models".length() && paramString.endsWith("validation/validate-content-models"))
        return FeatureState.NOT_SUPPORTED; 
      if (i == "nonvalidating/load-dtd-grammar".length() && paramString.endsWith("nonvalidating/load-dtd-grammar"))
        return FeatureState.RECOGNIZED; 
      if (i == "nonvalidating/load-external-dtd".length() && paramString.endsWith("nonvalidating/load-external-dtd"))
        return FeatureState.RECOGNIZED; 
      if (i == "validation/validate-datatypes".length() && paramString.endsWith("validation/validate-datatypes"))
        return FeatureState.NOT_SUPPORTED; 
      if (i == "validation/schema".length() && paramString.endsWith("validation/schema"))
        return FeatureState.RECOGNIZED; 
      if (i == "validation/schema-full-checking".length() && paramString.endsWith("validation/schema-full-checking"))
        return FeatureState.RECOGNIZED; 
      if (i == "validation/schema/normalized-value".length() && paramString.endsWith("validation/schema/normalized-value"))
        return FeatureState.RECOGNIZED; 
      if (i == "validation/schema/element-default".length() && paramString.endsWith("validation/schema/element-default"))
        return FeatureState.RECOGNIZED; 
      if (i == "internal/parser-settings".length() && paramString.endsWith("internal/parser-settings"))
        return FeatureState.NOT_SUPPORTED; 
    } 
    return super.checkFeature(paramString);
  }
  
  protected PropertyState checkProperty(String paramString) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "internal/dtd-scanner".length() && paramString.endsWith("internal/dtd-scanner"))
        return PropertyState.RECOGNIZED; 
      if (i == "schema/external-schemaLocation".length() && paramString.endsWith("schema/external-schemaLocation"))
        return PropertyState.RECOGNIZED; 
      if (i == "schema/external-noNamespaceSchemaLocation".length() && paramString.endsWith("schema/external-noNamespaceSchemaLocation"))
        return PropertyState.RECOGNIZED; 
    } 
    if (paramString.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
      int i = paramString.length() - "http://java.sun.com/xml/jaxp/properties/".length();
      if (i == "schemaSource".length() && paramString.endsWith("schemaSource"))
        return PropertyState.RECOGNIZED; 
    } 
    if (paramString.startsWith("http://xml.org/sax/properties/")) {
      int i = paramString.length() - "http://xml.org/sax/properties/".length();
      if (i == "xml-string".length() && paramString.endsWith("xml-string"))
        return PropertyState.NOT_SUPPORTED; 
    } 
    return super.checkProperty(paramString);
  }
  
  protected void addComponent(XMLComponent paramXMLComponent) {
    if (this.fComponents.contains(paramXMLComponent))
      return; 
    this.fComponents.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addCommonComponent(XMLComponent paramXMLComponent) {
    if (this.fCommonComponents.contains(paramXMLComponent))
      return; 
    this.fCommonComponents.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addXML11Component(XMLComponent paramXMLComponent) {
    if (this.fXML11Components.contains(paramXMLComponent))
      return; 
    this.fXML11Components.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addRecognizedParamsAndSetDefaults(XMLComponent paramXMLComponent) {
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
    if (arrayOfString1 != null)
      for (byte b = 0; b < arrayOfString1.length; b++) {
        String str = arrayOfString1[b];
        Boolean bool = paramXMLComponent.getFeatureDefault(str);
        if (bool != null && !this.fFeatures.containsKey(str)) {
          this.fFeatures.put(str, bool);
          this.fConfigUpdated = true;
        } 
      }  
    if (arrayOfString2 != null)
      for (byte b = 0; b < arrayOfString2.length; b++) {
        String str = arrayOfString2[b];
        Object object = paramXMLComponent.getPropertyDefault(str);
        if (object != null && !this.fProperties.containsKey(str)) {
          this.fProperties.put(str, object);
          this.fConfigUpdated = true;
        } 
      }  
  }
  
  private void initXML11Components() {
    if (!this.f11Initialized) {
      this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
      this.fXML11DTDScanner = new XML11DTDScannerImpl();
      addXML11Component(this.fXML11DTDScanner);
      this.fXML11DTDProcessor = new XML11DTDProcessor();
      addXML11Component(this.fXML11DTDProcessor);
      this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
      addXML11Component(this.fXML11NSDocScanner);
      this.fXML11NSDTDValidator = new XML11NSDTDValidator();
      addXML11Component(this.fXML11NSDTDValidator);
      this.f11Initialized = true;
    } 
  }
  
  FeatureState getFeatureState0(String paramString) throws XMLConfigurationException { return super.getFeatureState(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XML11Configuration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */