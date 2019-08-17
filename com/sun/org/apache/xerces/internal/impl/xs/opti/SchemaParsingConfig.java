package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.parsers.BasicParserConfiguration;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.Locale;
import jdk.xml.internal.JdkXmlUtils;

public class SchemaParsingConfig extends BasicParserConfiguration implements XMLPullParserConfiguration {
  protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  
  protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  
  protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  
  protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
  
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  
  private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
  
  protected final DTDDVFactory fDatatypeValidatorFactory;
  
  protected final XMLNSDocumentScannerImpl fNamespaceScanner;
  
  protected final XMLDTDScannerImpl fDTDScanner;
  
  protected DTDDVFactory fXML11DatatypeFactory = null;
  
  protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
  
  protected XML11DTDScannerImpl fXML11DTDScanner = null;
  
  protected DTDDVFactory fCurrentDVFactory;
  
  protected XMLDocumentScanner fCurrentScanner;
  
  protected XMLDTDScanner fCurrentDTDScanner;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected final XMLVersionDetector fVersionDetector;
  
  protected final XMLErrorReporter fErrorReporter;
  
  protected final XMLEntityManager fEntityManager;
  
  protected XMLInputSource fInputSource;
  
  protected final ValidationManager fValidationManager;
  
  protected XMLLocator fLocator;
  
  protected boolean fParseInProgress = false;
  
  protected boolean fConfigUpdated = false;
  
  private boolean f11Initialized = false;
  
  public SchemaParsingConfig() { this(null, null, null); }
  
  public SchemaParsingConfig(SymbolTable paramSymbolTable) { this(paramSymbolTable, null, null); }
  
  public SchemaParsingConfig(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) { this(paramSymbolTable, paramXMLGrammarPool, null); }
  
  public SchemaParsingConfig(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager) {
    super(paramSymbolTable, paramXMLComponentManager);
    String[] arrayOfString1 = { "http://apache.org/xml/features/internal/parser-settings", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/generate-synthetic-annotations", "jdk.xml.overrideDefaultParser" };
    addRecognizedFeatures(arrayOfString1);
    this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/allow-java-encodings", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/scanner/notify-builtin-refs", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/scanner/notify-char-refs", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
    this.fFeatures.put("jdk.xml.overrideDefaultParser", Boolean.valueOf(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT));
    String[] arrayOfString2 = { 
        "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/features/generate-synthetic-annotations", 
        "http://apache.org/xml/properties/locale" };
    addRecognizedProperties(arrayOfString2);
    this.fGrammarPool = paramXMLGrammarPool;
    if (this.fGrammarPool != null)
      setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool); 
    this.fEntityManager = new XMLEntityManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
    addComponent(this.fEntityManager);
    this.fErrorReporter = new XMLErrorReporter();
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    addComponent(this.fErrorReporter);
    this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
    addRecognizedParamsAndSetDefaults(this.fNamespaceScanner);
    this.fDTDScanner = new XMLDTDScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
    addRecognizedParamsAndSetDefaults(this.fDTDScanner);
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
    if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
      XSMessageFormatter xSMessageFormatter = new XSMessageFormatter();
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xSMessageFormatter);
    } 
    try {
      setLocale(Locale.getDefault());
    } catch (XNIException xNIException) {}
  }
  
  public FeatureState getFeatureState(String paramString) throws XMLConfigurationException { return paramString.equals("http://apache.org/xml/features/internal/parser-settings") ? FeatureState.is(this.fConfigUpdated) : super.getFeatureState(paramString); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    this.fConfigUpdated = true;
    this.fNamespaceScanner.setFeature(paramString, paramBoolean);
    this.fDTDScanner.setFeature(paramString, paramBoolean);
    if (this.f11Initialized) {
      try {
        this.fXML11DTDScanner.setFeature(paramString, paramBoolean);
      } catch (Exception exception) {}
      try {
        this.fXML11NSDocScanner.setFeature(paramString, paramBoolean);
      } catch (Exception exception) {}
    } 
    super.setFeature(paramString, paramBoolean);
  }
  
  public PropertyState getPropertyState(String paramString) throws XMLConfigurationException { return "http://apache.org/xml/properties/locale".equals(paramString) ? PropertyState.is(getLocale()) : super.getPropertyState(paramString); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    this.fConfigUpdated = true;
    if ("http://apache.org/xml/properties/locale".equals(paramString))
      setLocale((Locale)paramObject); 
    this.fNamespaceScanner.setProperty(paramString, paramObject);
    this.fDTDScanner.setProperty(paramString, paramObject);
    if (this.f11Initialized) {
      try {
        this.fXML11DTDScanner.setProperty(paramString, paramObject);
      } catch (Exception exception) {}
      try {
        this.fXML11NSDocScanner.setProperty(paramString, paramObject);
      } catch (Exception exception) {}
    } 
    super.setProperty(paramString, paramObject);
  }
  
  public void setLocale(Locale paramLocale) throws XNIException {
    super.setLocale(paramLocale);
    this.fErrorReporter.setLocale(paramLocale);
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource) throws XMLConfigurationException, IOException { this.fInputSource = paramXMLInputSource; }
  
  public boolean parse(boolean paramBoolean) throws XNIException, IOException {
    if (this.fInputSource != null)
      try {
        this.fValidationManager.reset();
        this.fVersionDetector.reset(this);
        reset();
        short s = this.fVersionDetector.determineDocVersion(this.fInputSource);
        if (s == 1) {
          configurePipeline();
          resetXML10();
        } else if (s == 2) {
          initXML11Components();
          configureXML11Pipeline();
          resetXML11();
        } else {
          return false;
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
  
  public void reset() { super.reset(); }
  
  protected void configurePipeline() {
    if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
      this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    } 
    if (this.fCurrentScanner != this.fNamespaceScanner) {
      this.fCurrentScanner = this.fNamespaceScanner;
      setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fCurrentScanner);
    } 
    this.fNamespaceScanner.setDocumentHandler(this.fDocumentHandler);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.setDocumentSource(this.fNamespaceScanner); 
    this.fLastComponent = this.fNamespaceScanner;
    if (this.fCurrentDTDScanner != this.fDTDScanner) {
      this.fCurrentDTDScanner = this.fDTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
    } 
    this.fDTDScanner.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null)
      this.fDTDHandler.setDTDSource(this.fDTDScanner); 
    this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDScanner); 
  }
  
  protected void configureXML11Pipeline() {
    if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
      this.fCurrentDVFactory = this.fXML11DatatypeFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    } 
    if (this.fCurrentScanner != this.fXML11NSDocScanner) {
      this.fCurrentScanner = this.fXML11NSDocScanner;
      setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fCurrentScanner);
    } 
    this.fXML11NSDocScanner.setDocumentHandler(this.fDocumentHandler);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.setDocumentSource(this.fXML11NSDocScanner); 
    this.fLastComponent = this.fXML11NSDocScanner;
    if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
      this.fCurrentDTDScanner = this.fXML11DTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
    } 
    this.fXML11DTDScanner.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null)
      this.fDTDHandler.setDTDSource(this.fXML11DTDScanner); 
    this.fXML11DTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDScanner); 
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
    } 
    return super.checkFeature(paramString);
  }
  
  protected PropertyState checkProperty(String paramString) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "internal/dtd-scanner".length() && paramString.endsWith("internal/dtd-scanner"))
        return PropertyState.RECOGNIZED; 
    } 
    if (paramString.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
      int i = paramString.length() - "http://java.sun.com/xml/jaxp/properties/".length();
      if (i == "schemaSource".length() && paramString.endsWith("schemaSource"))
        return PropertyState.RECOGNIZED; 
    } 
    return super.checkProperty(paramString);
  }
  
  private void addRecognizedParamsAndSetDefaults(XMLComponent paramXMLComponent) {
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
  
  protected final void resetXML10() {
    this.fNamespaceScanner.reset(this);
    this.fDTDScanner.reset(this);
  }
  
  protected final void resetXML11() {
    this.fXML11NSDocScanner.reset(this);
    this.fXML11DTDScanner.reset(this);
  }
  
  public void resetNodePool() {}
  
  private void initXML11Components() {
    if (!this.f11Initialized) {
      this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
      this.fXML11DTDScanner = new XML11DTDScannerImpl();
      addRecognizedParamsAndSetDefaults(this.fXML11DTDScanner);
      this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
      addRecognizedParamsAndSetDefaults(this.fXML11NSDocScanner);
      this.f11Initialized = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\SchemaParsingConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */