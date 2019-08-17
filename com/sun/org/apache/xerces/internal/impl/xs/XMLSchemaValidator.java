package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xs.identity.FieldActivator;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.impl.xs.identity.ValueStore;
import com.sun.org.apache.xerces.internal.impl.xs.identity.XPathMatcher;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import jdk.xml.internal.JdkXmlUtils;

public class XMLSchemaValidator implements XMLComponent, XMLDocumentFilter, FieldActivator, RevalidationHandler {
  private static final boolean DEBUG = false;
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  
  protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  
  protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
  
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  
  protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  
  protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
  
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  
  protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  
  protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
  
  public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  protected static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
  
  private static final String[] RECOGNIZED_FEATURES = { 
      "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", 
      "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser" };
  
  private static final Boolean[] FEATURE_DEFAULTS = { 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, (new Boolean[14][12] = null).valueOf(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT) };
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  
  private static final Object[] PROPERTY_DEFAULTS = { 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null };
  
  protected static final int ID_CONSTRAINT_NUM = 1;
  
  protected ElementPSVImpl fCurrentPSVI = new ElementPSVImpl();
  
  protected final AugmentationsImpl fAugmentations = new AugmentationsImpl();
  
  protected final HashMap fMayMatchFieldMap = new HashMap();
  
  protected XMLString fDefaultValue;
  
  protected boolean fDynamicValidation = false;
  
  protected boolean fSchemaDynamicValidation = false;
  
  protected boolean fDoValidation = false;
  
  protected boolean fFullChecking = false;
  
  protected boolean fNormalizeData = true;
  
  protected boolean fSchemaElementDefault = true;
  
  protected boolean fAugPSVI = true;
  
  protected boolean fIdConstraint = false;
  
  protected boolean fUseGrammarPoolOnly = false;
  
  protected boolean fNamespaceGrowth = false;
  
  private String fSchemaType = null;
  
  protected boolean fEntityRef = false;
  
  protected boolean fInCDATA = false;
  
  protected boolean fSawOnlyWhitespaceInElementContent = false;
  
  protected SymbolTable fSymbolTable;
  
  private XMLLocator fLocator;
  
  protected final XSIErrorReporter fXSIErrorReporter = new XSIErrorReporter();
  
  protected XMLEntityResolver fEntityResolver;
  
  protected ValidationManager fValidationManager = null;
  
  protected ValidationState fValidationState = new ValidationState();
  
  protected XMLGrammarPool fGrammarPool;
  
  protected String fExternalSchemas = null;
  
  protected String fExternalNoNamespaceSchema = null;
  
  protected Object fJaxpSchemaSource = null;
  
  protected final XSDDescription fXSDDescription = new XSDDescription();
  
  protected final Map<String, XMLSchemaLoader.LocationArray> fLocationPairs = new HashMap();
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDocumentSource fDocumentSource;
  
  boolean reportWhitespace = false;
  
  static final int INITIAL_STACK_SIZE = 8;
  
  static final int INC_STACK_SIZE = 8;
  
  private static final boolean DEBUG_NORMALIZATION = false;
  
  private final XMLString fEmptyXMLStr = new XMLString(null, 0, -1);
  
  private static final int BUFFER_SIZE = 20;
  
  private final XMLString fNormalizedStr = new XMLString();
  
  private boolean fFirstChunk = true;
  
  private boolean fTrailing = false;
  
  private short fWhiteSpace = -1;
  
  private boolean fUnionType = false;
  
  private final XSGrammarBucket fGrammarBucket = new XSGrammarBucket();
  
  private final SubstitutionGroupHandler fSubGroupHandler = new SubstitutionGroupHandler(this.fGrammarBucket);
  
  private final XSSimpleType fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
  
  private final CMNodeFactory nodeFactory = new CMNodeFactory();
  
  private final CMBuilder fCMBuilder = new CMBuilder(this.nodeFactory);
  
  private final XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader(this.fXSIErrorReporter.fErrorReporter, this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder);
  
  private String fValidationRoot;
  
  private int fSkipValidationDepth;
  
  private int fNFullValidationDepth;
  
  private int fNNoneValidationDepth;
  
  private int fElementDepth;
  
  private boolean fSubElement;
  
  private boolean[] fSubElementStack = new boolean[8];
  
  private XSElementDecl fCurrentElemDecl;
  
  private XSElementDecl[] fElemDeclStack = new XSElementDecl[8];
  
  private boolean fNil;
  
  private boolean[] fNilStack = new boolean[8];
  
  private XSNotationDecl fNotation;
  
  private XSNotationDecl[] fNotationStack = new XSNotationDecl[8];
  
  private XSTypeDefinition fCurrentType;
  
  private XSTypeDefinition[] fTypeStack = new XSTypeDefinition[8];
  
  private XSCMValidator fCurrentCM;
  
  private XSCMValidator[] fCMStack = new XSCMValidator[8];
  
  private int[] fCurrCMState;
  
  private int[][] fCMStateStack = new int[8][];
  
  private boolean fStrictAssess = true;
  
  private boolean[] fStrictAssessStack = new boolean[8];
  
  private final StringBuffer fBuffer = new StringBuffer();
  
  private boolean fAppendBuffer = true;
  
  private boolean fSawText = false;
  
  private boolean[] fSawTextStack = new boolean[8];
  
  private boolean fSawCharacters = false;
  
  private boolean[] fStringContent = new boolean[8];
  
  private final QName fTempQName = new QName();
  
  private ValidatedInfo fValidatedInfo = new ValidatedInfo();
  
  private ValidationState fState4XsiType = new ValidationState();
  
  private ValidationState fState4ApplyDefault = new ValidationState();
  
  protected XPathMatcherStack fMatcherStack = new XPathMatcherStack();
  
  protected ValueStoreCache fValueStoreCache = new ValueStoreCache();
  
  public String[] getRecognizedFeatures() { return (String[])RECOGNIZED_FEATURES.clone(); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {}
  
  public String[] getRecognizedProperties() { return (String[])RECOGNIZED_PROPERTIES.clone(); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {}
  
  public Boolean getFeatureDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_FEATURES.length; b++) {
      if (RECOGNIZED_FEATURES[b].equals(paramString))
        return FEATURE_DEFAULTS[b]; 
    } 
    return null;
  }
  
  public Object getPropertyDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_PROPERTIES.length; b++) {
      if (RECOGNIZED_PROPERTIES[b].equals(paramString))
        return PROPERTY_DEFAULTS[b]; 
    } 
    return null;
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) {
    this.fDocumentHandler = paramXMLDocumentHandler;
    if (paramXMLDocumentHandler instanceof XMLParser)
      try {
        this.reportWhitespace = ((XMLParser)paramXMLDocumentHandler).getFeature("http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace");
      } catch (Exception exception) {
        this.reportWhitespace = false;
      }  
  }
  
  public XMLDocumentHandler getDocumentHandler() { return this.fDocumentHandler; }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.fDocumentSource = paramXMLDocumentSource; }
  
  public XMLDocumentSource getDocumentSource() { return this.fDocumentSource; }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    this.fValidationState.setNamespaceSupport(paramNamespaceContext);
    this.fState4XsiType.setNamespaceSupport(paramNamespaceContext);
    this.fState4ApplyDefault.setNamespaceSupport(paramNamespaceContext);
    this.fLocator = paramXMLLocator;
    handleStartDocument(paramXMLLocator, paramString);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations); 
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations); 
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations); 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    Augmentations augmentations = handleStartElement(paramQName, paramXMLAttributes, paramAugmentations);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startElement(paramQName, paramXMLAttributes, augmentations); 
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    Augmentations augmentations = handleStartElement(paramQName, paramXMLAttributes, paramAugmentations);
    this.fDefaultValue = null;
    if (this.fElementDepth != -2)
      augmentations = handleEndElement(paramQName, augmentations); 
    if (this.fDocumentHandler != null)
      if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
        this.fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, augmentations);
      } else {
        this.fDocumentHandler.startElement(paramQName, paramXMLAttributes, augmentations);
        this.fDocumentHandler.characters(this.fDefaultValue, null);
        this.fDocumentHandler.endElement(paramQName, augmentations);
      }  
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    paramXMLString = handleCharacters(paramXMLString);
    if (this.fSawOnlyWhitespaceInElementContent) {
      this.fSawOnlyWhitespaceInElementContent = false;
      if (!this.reportWhitespace) {
        ignorableWhitespace(paramXMLString, paramAugmentations);
        return;
      } 
    } 
    if (this.fDocumentHandler != null)
      if (this.fNormalizeData && this.fUnionType) {
        if (paramAugmentations != null)
          this.fDocumentHandler.characters(this.fEmptyXMLStr, paramAugmentations); 
      } else {
        this.fDocumentHandler.characters(paramXMLString, paramAugmentations);
      }  
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    handleIgnorableWhitespace(paramXMLString);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations); 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    this.fDefaultValue = null;
    Augmentations augmentations = handleEndElement(paramQName, paramAugmentations);
    if (this.fDocumentHandler != null)
      if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
        this.fDocumentHandler.endElement(paramQName, augmentations);
      } else {
        this.fDocumentHandler.characters(this.fDefaultValue, null);
        this.fDocumentHandler.endElement(paramQName, augmentations);
      }  
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    this.fInCDATA = true;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startCDATA(paramAugmentations); 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    this.fInCDATA = false;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endCDATA(paramAugmentations); 
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    handleEndDocument();
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endDocument(paramAugmentations); 
    this.fLocator = null;
  }
  
  public boolean characterData(String paramString, Augmentations paramAugmentations) {
    this.fSawText = (this.fSawText || paramString.length() > 0);
    if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
      normalizeWhitespace(paramString, (this.fWhiteSpace == 2));
      this.fBuffer.append(this.fNormalizedStr.ch, this.fNormalizedStr.offset, this.fNormalizedStr.length);
    } else if (this.fAppendBuffer) {
      this.fBuffer.append(paramString);
    } 
    boolean bool = true;
    if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
      XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
      if (xSComplexTypeDecl.fContentType == 2)
        for (byte b = 0; b < paramString.length(); b++) {
          if (!XMLChar.isSpace(paramString.charAt(b))) {
            bool = false;
            this.fSawCharacters = true;
            break;
          } 
        }  
    } 
    return bool;
  }
  
  public void elementDefault(String paramString) {}
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    this.fEntityRef = true;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations); 
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations); 
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.comment(paramXMLString, paramAugmentations); 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations); 
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    this.fEntityRef = false;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endGeneralEntity(paramString, paramAugmentations); 
  }
  
  public XMLSchemaValidator() {
    this.fState4XsiType.setExtraChecking(false);
    this.fState4ApplyDefault.setFacetChecking(false);
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fIdConstraint = false;
    this.fLocationPairs.clear();
    this.fValidationState.resetIDTables();
    this.nodeFactory.reset(paramXMLComponentManager);
    this.fSchemaLoader.reset(paramXMLComponentManager);
    this.fCurrentElemDecl = null;
    this.fCurrentCM = null;
    this.fCurrCMState = null;
    this.fSkipValidationDepth = -1;
    this.fNFullValidationDepth = -1;
    this.fNNoneValidationDepth = -1;
    this.fElementDepth = -1;
    this.fSubElement = false;
    this.fSchemaDynamicValidation = false;
    this.fEntityRef = false;
    this.fInCDATA = false;
    this.fMatcherStack.clear();
    if (!this.fMayMatchFieldMap.isEmpty())
      this.fMayMatchFieldMap.clear(); 
    this.fXSIErrorReporter.reset((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    boolean bool = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!bool) {
      this.fValidationManager.addValidationState(this.fValidationState);
      XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
      return;
    } 
    SymbolTable symbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    if (symbolTable != this.fSymbolTable)
      this.fSymbolTable = symbolTable; 
    this.fNamespaceGrowth = paramXMLComponentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
    this.fDynamicValidation = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/dynamic", false);
    if (this.fDynamicValidation) {
      this.fDoValidation = true;
    } else {
      this.fDoValidation = paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation", false);
    } 
    if (this.fDoValidation)
      this.fDoValidation |= paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema", false); 
    this.fFullChecking = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
    this.fNormalizeData = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
    this.fSchemaElementDefault = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema/element-default", false);
    this.fAugPSVI = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
    this.fSchemaType = (String)paramXMLComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
    this.fUseGrammarPoolOnly = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", false);
    this.fEntityResolver = (XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    this.fValidationManager = (ValidationManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
    this.fValidationManager.addValidationState(this.fValidationState);
    this.fValidationState.setSymbolTable(this.fSymbolTable);
    try {
      this.fExternalSchemas = (String)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
      this.fExternalNoNamespaceSchema = (String)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fExternalSchemas = null;
      this.fExternalNoNamespaceSchema = null;
    } 
    XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
    this.fJaxpSchemaSource = paramXMLComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
    this.fGrammarPool = (XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
    this.fState4XsiType.setSymbolTable(symbolTable);
    this.fState4ApplyDefault.setSymbolTable(symbolTable);
  }
  
  public void startValueScopeFor(IdentityConstraint paramIdentityConstraint, int paramInt) {
    ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(paramIdentityConstraint, paramInt);
    valueStoreBase.startValueScope();
  }
  
  public XPathMatcher activateField(Field paramField, int paramInt) {
    ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(paramField.getIdentityConstraint(), paramInt);
    setMayMatch(paramField, Boolean.TRUE);
    XPathMatcher xPathMatcher = paramField.createMatcher(this, valueStoreBase);
    this.fMatcherStack.addMatcher(xPathMatcher);
    xPathMatcher.startDocumentFragment();
    return xPathMatcher;
  }
  
  public void endValueScopeFor(IdentityConstraint paramIdentityConstraint, int paramInt) {
    ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(paramIdentityConstraint, paramInt);
    valueStoreBase.endValueScope();
  }
  
  public void setMayMatch(Field paramField, Boolean paramBoolean) { this.fMayMatchFieldMap.put(paramField, paramBoolean); }
  
  public Boolean mayMatch(Field paramField) { return (Boolean)this.fMayMatchFieldMap.get(paramField); }
  
  private void activateSelectorFor(IdentityConstraint paramIdentityConstraint) {
    Selector selector = paramIdentityConstraint.getSelector();
    XMLSchemaValidator xMLSchemaValidator = this;
    if (selector == null)
      return; 
    XPathMatcher xPathMatcher = selector.createMatcher(xMLSchemaValidator, this.fElementDepth);
    this.fMatcherStack.addMatcher(xPathMatcher);
    xPathMatcher.startDocumentFragment();
  }
  
  void ensureStackCapacity() {
    if (this.fElementDepth == this.fElemDeclStack.length) {
      int i = this.fElementDepth + 8;
      boolean[] arrayOfBoolean = new boolean[i];
      System.arraycopy(this.fSubElementStack, 0, arrayOfBoolean, 0, this.fElementDepth);
      this.fSubElementStack = arrayOfBoolean;
      XSElementDecl[] arrayOfXSElementDecl = new XSElementDecl[i];
      System.arraycopy(this.fElemDeclStack, 0, arrayOfXSElementDecl, 0, this.fElementDepth);
      this.fElemDeclStack = arrayOfXSElementDecl;
      arrayOfBoolean = new boolean[i];
      System.arraycopy(this.fNilStack, 0, arrayOfBoolean, 0, this.fElementDepth);
      this.fNilStack = arrayOfBoolean;
      XSNotationDecl[] arrayOfXSNotationDecl = new XSNotationDecl[i];
      System.arraycopy(this.fNotationStack, 0, arrayOfXSNotationDecl, 0, this.fElementDepth);
      this.fNotationStack = arrayOfXSNotationDecl;
      XSTypeDefinition[] arrayOfXSTypeDefinition = new XSTypeDefinition[i];
      System.arraycopy(this.fTypeStack, 0, arrayOfXSTypeDefinition, 0, this.fElementDepth);
      this.fTypeStack = arrayOfXSTypeDefinition;
      XSCMValidator[] arrayOfXSCMValidator = new XSCMValidator[i];
      System.arraycopy(this.fCMStack, 0, arrayOfXSCMValidator, 0, this.fElementDepth);
      this.fCMStack = arrayOfXSCMValidator;
      arrayOfBoolean = new boolean[i];
      System.arraycopy(this.fSawTextStack, 0, arrayOfBoolean, 0, this.fElementDepth);
      this.fSawTextStack = arrayOfBoolean;
      arrayOfBoolean = new boolean[i];
      System.arraycopy(this.fStringContent, 0, arrayOfBoolean, 0, this.fElementDepth);
      this.fStringContent = arrayOfBoolean;
      arrayOfBoolean = new boolean[i];
      System.arraycopy(this.fStrictAssessStack, 0, arrayOfBoolean, 0, this.fElementDepth);
      this.fStrictAssessStack = arrayOfBoolean;
      int[][] arrayOfInt = new int[i][];
      System.arraycopy(this.fCMStateStack, 0, arrayOfInt, 0, this.fElementDepth);
      this.fCMStateStack = arrayOfInt;
    } 
  }
  
  void handleStartDocument(XMLLocator paramXMLLocator, String paramString) {
    this.fValueStoreCache.startDocument();
    if (this.fAugPSVI) {
      this.fCurrentPSVI.fGrammars = null;
      this.fCurrentPSVI.fSchemaInformation = null;
    } 
  }
  
  void handleEndDocument() { this.fValueStoreCache.endDocument(); }
  
  XMLString handleCharacters(XMLString paramXMLString) {
    if (this.fSkipValidationDepth >= 0)
      return paramXMLString; 
    this.fSawText = (this.fSawText || paramXMLString.length > 0);
    if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
      normalizeWhitespace(paramXMLString, (this.fWhiteSpace == 2));
      paramXMLString = this.fNormalizedStr;
    } 
    if (this.fAppendBuffer)
      this.fBuffer.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
    this.fSawOnlyWhitespaceInElementContent = false;
    if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
      XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
      if (xSComplexTypeDecl.fContentType == 2)
        for (int i = paramXMLString.offset; i < paramXMLString.offset + paramXMLString.length; i++) {
          if (!XMLChar.isSpace(paramXMLString.ch[i])) {
            this.fSawCharacters = true;
            break;
          } 
          this.fSawOnlyWhitespaceInElementContent = !this.fSawCharacters;
        }  
    } 
    return paramXMLString;
  }
  
  private void normalizeWhitespace(XMLString paramXMLString, boolean paramBoolean) {
    boolean bool = paramBoolean;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    int i = paramXMLString.offset + paramXMLString.length;
    if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < paramXMLString.length + 1)
      this.fNormalizedStr.ch = new char[paramXMLString.length + 1]; 
    this.fNormalizedStr.offset = 1;
    this.fNormalizedStr.length = 1;
    for (int j = paramXMLString.offset; j < i; j++) {
      char c = paramXMLString.ch[j];
      if (XMLChar.isSpace(c)) {
        if (!bool) {
          this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
          bool = paramBoolean;
        } 
        if (!bool1)
          bool2 = true; 
      } else {
        this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
        bool = false;
        bool1 = true;
      } 
    } 
    if (bool)
      if (this.fNormalizedStr.length > 1) {
        this.fNormalizedStr.length--;
        bool3 = true;
      } else if (bool2 && !this.fFirstChunk) {
        bool3 = true;
      }  
    if (this.fNormalizedStr.length > 1 && !this.fFirstChunk && this.fWhiteSpace == 2)
      if (this.fTrailing) {
        this.fNormalizedStr.offset = 0;
        this.fNormalizedStr.ch[0] = ' ';
      } else if (bool2) {
        this.fNormalizedStr.offset = 0;
        this.fNormalizedStr.ch[0] = ' ';
      }  
    this.fNormalizedStr.length -= this.fNormalizedStr.offset;
    this.fTrailing = bool3;
    if (bool3 || bool1)
      this.fFirstChunk = false; 
  }
  
  private void normalizeWhitespace(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    boolean bool = paramBoolean;
    int i = paramString.length();
    if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < i)
      this.fNormalizedStr.ch = new char[i]; 
    this.fNormalizedStr.offset = 0;
    this.fNormalizedStr.length = 0;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (XMLChar.isSpace(c)) {
        if (!bool) {
          this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
          bool = paramBoolean;
        } 
      } else {
        this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
        bool = false;
      } 
    } 
    if (bool && this.fNormalizedStr.length != 0)
      this.fNormalizedStr.length--; 
  }
  
  void handleIgnorableWhitespace(XMLString paramXMLString) {
    if (this.fSkipValidationDepth >= 0)
      return; 
  }
  
  Augmentations handleStartElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) {
    if (this.fElementDepth == -1 && this.fValidationManager.isGrammarFound() && this.fSchemaType == null)
      this.fSchemaDynamicValidation = true; 
    String str1 = paramXMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION);
    String str2 = paramXMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
    storeLocations(str1, str2);
    if (this.fSkipValidationDepth >= 0) {
      this.fElementDepth++;
      if (this.fAugPSVI)
        paramAugmentations = getEmptyAugs(paramAugmentations); 
      return paramAugmentations;
    } 
    SchemaGrammar schemaGrammar = findSchemaGrammar((short)5, paramQName.uri, null, paramQName, paramXMLAttributes);
    Object object = null;
    if (this.fCurrentCM != null) {
      object = this.fCurrentCM.oneTransition(paramQName, this.fCurrCMState, this.fSubGroupHandler);
      if (this.fCurrCMState[0] == -1) {
        XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
        Vector vector;
        if (xSComplexTypeDecl.fParticle != null && (vector = this.fCurrentCM.whatCanGoHere(this.fCurrCMState)).size() > 0) {
          String str = expectedStr(vector);
          reportSchemaError("cvc-complex-type.2.4.a", new Object[] { paramQName.rawname, str });
        } else {
          reportSchemaError("cvc-complex-type.2.4.d", new Object[] { paramQName.rawname });
        } 
      } 
    } 
    if (this.fElementDepth != -1) {
      ensureStackCapacity();
      this.fSubElementStack[this.fElementDepth] = true;
      this.fSubElement = false;
      this.fElemDeclStack[this.fElementDepth] = this.fCurrentElemDecl;
      this.fNilStack[this.fElementDepth] = this.fNil;
      this.fNotationStack[this.fElementDepth] = this.fNotation;
      this.fTypeStack[this.fElementDepth] = this.fCurrentType;
      this.fStrictAssessStack[this.fElementDepth] = this.fStrictAssess;
      this.fCMStack[this.fElementDepth] = this.fCurrentCM;
      this.fCMStateStack[this.fElementDepth] = this.fCurrCMState;
      this.fSawTextStack[this.fElementDepth] = this.fSawText;
      this.fStringContent[this.fElementDepth] = this.fSawCharacters;
    } 
    this.fElementDepth++;
    this.fCurrentElemDecl = null;
    XSWildcardDecl xSWildcardDecl = null;
    this.fCurrentType = null;
    this.fStrictAssess = true;
    this.fNil = false;
    this.fNotation = null;
    this.fBuffer.setLength(0);
    this.fSawText = false;
    this.fSawCharacters = false;
    if (object != null)
      if (object instanceof XSElementDecl) {
        this.fCurrentElemDecl = (XSElementDecl)object;
      } else {
        xSWildcardDecl = (XSWildcardDecl)object;
      }  
    if (xSWildcardDecl != null && xSWildcardDecl.fProcessContents == 2) {
      this.fSkipValidationDepth = this.fElementDepth;
      if (this.fAugPSVI)
        paramAugmentations = getEmptyAugs(paramAugmentations); 
      return paramAugmentations;
    } 
    if (this.fCurrentElemDecl == null && schemaGrammar != null)
      this.fCurrentElemDecl = schemaGrammar.getGlobalElementDecl(paramQName.localpart); 
    if (this.fCurrentElemDecl != null)
      this.fCurrentType = this.fCurrentElemDecl.fType; 
    String str3 = paramXMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);
    if (this.fCurrentType == null && str3 == null) {
      if (this.fElementDepth == 0) {
        if (this.fDynamicValidation || this.fSchemaDynamicValidation) {
          if (this.fDocumentSource != null) {
            this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null)
              this.fDocumentHandler.setDocumentSource(this.fDocumentSource); 
            this.fElementDepth = -2;
            return paramAugmentations;
          } 
          this.fSkipValidationDepth = this.fElementDepth;
          if (this.fAugPSVI)
            paramAugmentations = getEmptyAugs(paramAugmentations); 
          return paramAugmentations;
        } 
        this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-elt.1", new Object[] { paramQName.rawname }, (short)1);
      } else if (xSWildcardDecl != null && xSWildcardDecl.fProcessContents == 1) {
        reportSchemaError("cvc-complex-type.2.4.c", new Object[] { paramQName.rawname });
      } 
      this.fCurrentType = SchemaGrammar.fAnyType;
      this.fStrictAssess = false;
      this.fNFullValidationDepth = this.fElementDepth;
      this.fAppendBuffer = false;
      this.fXSIErrorReporter.pushContext();
    } else {
      this.fXSIErrorReporter.pushContext();
      if (str3 != null) {
        XSTypeDefinition xSTypeDefinition = this.fCurrentType;
        this.fCurrentType = getAndCheckXsiType(paramQName, str3, paramXMLAttributes);
        if (this.fCurrentType == null)
          if (xSTypeDefinition == null) {
            this.fCurrentType = SchemaGrammar.fAnyType;
          } else {
            this.fCurrentType = xSTypeDefinition;
          }  
      } 
      this.fNNoneValidationDepth = this.fElementDepth;
      if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
        this.fAppendBuffer = true;
      } else if (this.fCurrentType.getTypeCategory() == 16) {
        this.fAppendBuffer = true;
      } else {
        XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
        this.fAppendBuffer = (xSComplexTypeDecl.fContentType == 1);
      } 
    } 
    if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getAbstract())
      reportSchemaError("cvc-elt.2", new Object[] { paramQName.rawname }); 
    if (this.fElementDepth == 0)
      this.fValidationRoot = paramQName.rawname; 
    if (this.fNormalizeData) {
      this.fFirstChunk = true;
      this.fTrailing = false;
      this.fUnionType = false;
      this.fWhiteSpace = -1;
    } 
    if (this.fCurrentType.getTypeCategory() == 15) {
      XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
      if (xSComplexTypeDecl.getAbstract())
        reportSchemaError("cvc-type.2", new Object[] { paramQName.rawname }); 
      if (this.fNormalizeData && xSComplexTypeDecl.fContentType == 1)
        if (xSComplexTypeDecl.fXSSimpleType.getVariety() == 3) {
          this.fUnionType = true;
        } else {
          try {
            this.fWhiteSpace = xSComplexTypeDecl.fXSSimpleType.getWhitespace();
          } catch (DatatypeException datatypeException) {}
        }  
    } else if (this.fNormalizeData) {
      XSSimpleType xSSimpleType = (XSSimpleType)this.fCurrentType;
      if (xSSimpleType.getVariety() == 3) {
        this.fUnionType = true;
      } else {
        try {
          this.fWhiteSpace = xSSimpleType.getWhitespace();
        } catch (DatatypeException datatypeException) {}
      } 
    } 
    this.fCurrentCM = null;
    if (this.fCurrentType.getTypeCategory() == 15)
      this.fCurrentCM = ((XSComplexTypeDecl)this.fCurrentType).getContentModel(this.fCMBuilder); 
    this.fCurrCMState = null;
    if (this.fCurrentCM != null)
      this.fCurrCMState = this.fCurrentCM.startContentModel(); 
    String str4 = paramXMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL);
    if (str4 != null && this.fCurrentElemDecl != null)
      this.fNil = getXsiNil(paramQName, str4); 
    XSAttributeGroupDecl xSAttributeGroupDecl = null;
    if (this.fCurrentType.getTypeCategory() == 15) {
      XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
      xSAttributeGroupDecl = xSComplexTypeDecl.getAttrGrp();
    } 
    this.fValueStoreCache.startElement();
    this.fMatcherStack.pushContext();
    if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fIDCPos > 0) {
      this.fIdConstraint = true;
      this.fValueStoreCache.initValueStoresFor(this.fCurrentElemDecl, this);
    } 
    processAttributes(paramQName, paramXMLAttributes, xSAttributeGroupDecl);
    if (xSAttributeGroupDecl != null)
      addDefaultAttributes(paramQName, paramXMLAttributes, xSAttributeGroupDecl); 
    int i = this.fMatcherStack.getMatcherCount();
    for (byte b = 0; b < i; b++) {
      XPathMatcher xPathMatcher = this.fMatcherStack.getMatcherAt(b);
      xPathMatcher.startElement(paramQName, paramXMLAttributes);
    } 
    if (this.fAugPSVI) {
      paramAugmentations = getEmptyAugs(paramAugmentations);
      this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
      this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
      this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
      this.fCurrentPSVI.fNotation = this.fNotation;
    } 
    return paramAugmentations;
  }
  
  Augmentations handleEndElement(QName paramQName, Augmentations paramAugmentations) {
    if (this.fSkipValidationDepth >= 0) {
      if (this.fSkipValidationDepth == this.fElementDepth && this.fSkipValidationDepth > 0) {
        this.fNFullValidationDepth = this.fSkipValidationDepth - 1;
        this.fSkipValidationDepth = -1;
        this.fElementDepth--;
        this.fSubElement = this.fSubElementStack[this.fElementDepth];
        this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
        this.fNil = this.fNilStack[this.fElementDepth];
        this.fNotation = this.fNotationStack[this.fElementDepth];
        this.fCurrentType = this.fTypeStack[this.fElementDepth];
        this.fCurrentCM = this.fCMStack[this.fElementDepth];
        this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
        this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
        this.fSawText = this.fSawTextStack[this.fElementDepth];
        this.fSawCharacters = this.fStringContent[this.fElementDepth];
      } else {
        this.fElementDepth--;
      } 
      if (this.fElementDepth == -1 && this.fFullChecking)
        XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter); 
      if (this.fAugPSVI)
        paramAugmentations = getEmptyAugs(paramAugmentations); 
      return paramAugmentations;
    } 
    processElementContent(paramQName);
    int i = this.fMatcherStack.getMatcherCount();
    int j;
    for (j = i - 1; j >= 0; j--) {
      XPathMatcher xPathMatcher = this.fMatcherStack.getMatcherAt(j);
      if (this.fCurrentElemDecl == null) {
        xPathMatcher.endElement(paramQName, null, false, this.fValidatedInfo.actualValue, this.fValidatedInfo.actualValueType, this.fValidatedInfo.itemValueTypes);
      } else {
        xPathMatcher.endElement(paramQName, this.fCurrentType, this.fCurrentElemDecl.getNillable(), (this.fDefaultValue == null) ? this.fValidatedInfo.actualValue : this.fCurrentElemDecl.fDefault.actualValue, (this.fDefaultValue == null) ? this.fValidatedInfo.actualValueType : this.fCurrentElemDecl.fDefault.actualValueType, (this.fDefaultValue == null) ? this.fValidatedInfo.itemValueTypes : this.fCurrentElemDecl.fDefault.itemValueTypes);
      } 
    } 
    if (this.fMatcherStack.size() > 0)
      this.fMatcherStack.popContext(); 
    j = this.fMatcherStack.getMatcherCount();
    int k;
    for (k = i - 1; k >= j; k--) {
      XPathMatcher xPathMatcher = this.fMatcherStack.getMatcherAt(k);
      Selector.Matcher matcher = (Selector.Matcher)xPathMatcher;
      IdentityConstraint identityConstraint;
      if (xPathMatcher instanceof Selector.Matcher && (identityConstraint = matcher.getIdentityConstraint()) != null && identityConstraint.getCategory() != 2)
        this.fValueStoreCache.transplant(identityConstraint, matcher.getInitialDepth()); 
    } 
    for (k = i - 1; k >= j; k--) {
      XPathMatcher xPathMatcher = this.fMatcherStack.getMatcherAt(k);
      Selector.Matcher matcher = (Selector.Matcher)xPathMatcher;
      IdentityConstraint identityConstraint;
      if (xPathMatcher instanceof Selector.Matcher && (identityConstraint = matcher.getIdentityConstraint()) != null && identityConstraint.getCategory() == 2) {
        ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(identityConstraint, matcher.getInitialDepth());
        if (valueStoreBase != null)
          valueStoreBase.endDocumentFragment(); 
      } 
    } 
    this.fValueStoreCache.endElement();
    SchemaGrammar[] arrayOfSchemaGrammar = null;
    if (this.fElementDepth == 0) {
      String str = this.fValidationState.checkIDRefID();
      this.fValidationState.resetIDTables();
      if (str != null)
        reportSchemaError("cvc-id.1", new Object[] { str }); 
      if (this.fFullChecking)
        XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter); 
      arrayOfSchemaGrammar = this.fGrammarBucket.getGrammars();
      if (this.fGrammarPool != null) {
        for (byte b = 0; b < arrayOfSchemaGrammar.length; b++)
          arrayOfSchemaGrammar[b].setImmutable(true); 
        this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", arrayOfSchemaGrammar);
      } 
      paramAugmentations = endElementPSVI(true, arrayOfSchemaGrammar, paramAugmentations);
    } else {
      paramAugmentations = endElementPSVI(false, arrayOfSchemaGrammar, paramAugmentations);
      this.fElementDepth--;
      this.fSubElement = this.fSubElementStack[this.fElementDepth];
      this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
      this.fNil = this.fNilStack[this.fElementDepth];
      this.fNotation = this.fNotationStack[this.fElementDepth];
      this.fCurrentType = this.fTypeStack[this.fElementDepth];
      this.fCurrentCM = this.fCMStack[this.fElementDepth];
      this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
      this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
      this.fSawText = this.fSawTextStack[this.fElementDepth];
      this.fSawCharacters = this.fStringContent[this.fElementDepth];
      this.fWhiteSpace = -1;
      this.fAppendBuffer = false;
      this.fUnionType = false;
    } 
    return paramAugmentations;
  }
  
  final Augmentations endElementPSVI(boolean paramBoolean, SchemaGrammar[] paramArrayOfSchemaGrammar, Augmentations paramAugmentations) {
    if (this.fAugPSVI) {
      paramAugmentations = getEmptyAugs(paramAugmentations);
      this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
      this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
      this.fCurrentPSVI.fNotation = this.fNotation;
      this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
      if (this.fElementDepth > this.fNFullValidationDepth) {
        this.fCurrentPSVI.fValidationAttempted = 2;
      } else if (this.fElementDepth > this.fNNoneValidationDepth) {
        this.fCurrentPSVI.fValidationAttempted = 0;
      } else {
        this.fCurrentPSVI.fValidationAttempted = 1;
        this.fNFullValidationDepth = this.fNNoneValidationDepth = this.fElementDepth - 1;
      } 
      if (this.fDefaultValue != null)
        this.fCurrentPSVI.fSpecified = true; 
      this.fCurrentPSVI.fNil = this.fNil;
      this.fCurrentPSVI.fMemberType = this.fValidatedInfo.memberType;
      this.fCurrentPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
      this.fCurrentPSVI.fActualValue = this.fValidatedInfo.actualValue;
      this.fCurrentPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
      this.fCurrentPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
      if (this.fStrictAssess) {
        String[] arrayOfString = this.fXSIErrorReporter.mergeContext();
        this.fCurrentPSVI.fErrorCodes = arrayOfString;
        this.fCurrentPSVI.fValidity = (arrayOfString == null) ? 2 : 1;
      } else {
        this.fCurrentPSVI.fValidity = 0;
        this.fXSIErrorReporter.popContext();
      } 
      if (paramBoolean) {
        this.fCurrentPSVI.fGrammars = paramArrayOfSchemaGrammar;
        this.fCurrentPSVI.fSchemaInformation = null;
      } 
    } 
    return paramAugmentations;
  }
  
  Augmentations getEmptyAugs(Augmentations paramAugmentations) {
    if (paramAugmentations == null) {
      paramAugmentations = this.fAugmentations;
      paramAugmentations.removeAllItems();
    } 
    paramAugmentations.putItem("ELEMENT_PSVI", this.fCurrentPSVI);
    this.fCurrentPSVI.reset();
    return paramAugmentations;
  }
  
  void storeLocations(String paramString1, String paramString2) {
    if (paramString1 != null && !XMLSchemaLoader.tokenizeSchemaLocationStr(paramString1, this.fLocationPairs))
      this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { paramString1 }, (short)0); 
    if (paramString2 != null) {
      XMLSchemaLoader.LocationArray locationArray = (XMLSchemaLoader.LocationArray)this.fLocationPairs.get(XMLSymbols.EMPTY_STRING);
      if (locationArray == null) {
        locationArray = new XMLSchemaLoader.LocationArray();
        this.fLocationPairs.put(XMLSymbols.EMPTY_STRING, locationArray);
      } 
      locationArray.addLocation(paramString2);
    } 
  }
  
  SchemaGrammar findSchemaGrammar(short paramShort, String paramString, QName paramQName1, QName paramQName2, XMLAttributes paramXMLAttributes) {
    SchemaGrammar schemaGrammar = null;
    schemaGrammar = this.fGrammarBucket.getGrammar(paramString);
    if (schemaGrammar == null) {
      this.fXSDDescription.setNamespace(paramString);
      if (this.fGrammarPool != null) {
        schemaGrammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(this.fXSDDescription);
        if (schemaGrammar != null && !this.fGrammarBucket.putGrammar(schemaGrammar, true, this.fNamespaceGrowth)) {
          this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
          schemaGrammar = null;
        } 
      } 
    } 
    if ((schemaGrammar == null && !this.fUseGrammarPoolOnly) || this.fNamespaceGrowth) {
      this.fXSDDescription.reset();
      this.fXSDDescription.fContextType = paramShort;
      this.fXSDDescription.setNamespace(paramString);
      this.fXSDDescription.fEnclosedElementName = paramQName1;
      this.fXSDDescription.fTriggeringComponent = paramQName2;
      this.fXSDDescription.fAttributes = paramXMLAttributes;
      if (this.fLocator != null)
        this.fXSDDescription.setBaseSystemId(this.fLocator.getExpandedSystemId()); 
      Map map = this.fLocationPairs;
      XMLSchemaLoader.LocationArray locationArray = (XMLSchemaLoader.LocationArray)map.get((paramString == null) ? XMLSymbols.EMPTY_STRING : paramString);
      if (locationArray != null) {
        String[] arrayOfString = locationArray.getLocationArray();
        if (arrayOfString.length != 0)
          setLocationHints(this.fXSDDescription, arrayOfString, schemaGrammar); 
      } 
      if (schemaGrammar == null || this.fXSDDescription.fLocationHints != null) {
        boolean bool = true;
        if (schemaGrammar != null)
          map = Collections.emptyMap(); 
        try {
          XMLInputSource xMLInputSource = XMLSchemaLoader.resolveDocument(this.fXSDDescription, map, this.fEntityResolver);
          if (schemaGrammar != null && this.fNamespaceGrowth)
            try {
              if (schemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), false)))
                bool = false; 
            } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {} 
          if (bool)
            schemaGrammar = this.fSchemaLoader.loadSchema(this.fXSDDescription, xMLInputSource, this.fLocationPairs); 
        } catch (IOException iOException) {
          String[] arrayOfString = this.fXSDDescription.getLocationHints();
          this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { (arrayOfString != null) ? arrayOfString[0] : XMLSymbols.EMPTY_STRING }, (short)0);
        } 
      } 
    } 
    return schemaGrammar;
  }
  
  private void setLocationHints(XSDDescription paramXSDDescription, String[] paramArrayOfString, SchemaGrammar paramSchemaGrammar) {
    int i = paramArrayOfString.length;
    if (paramSchemaGrammar == null) {
      this.fXSDDescription.fLocationHints = new String[i];
      System.arraycopy(paramArrayOfString, 0, this.fXSDDescription.fLocationHints, 0, i);
    } else {
      setLocationHints(paramXSDDescription, paramArrayOfString, paramSchemaGrammar.getDocumentLocations());
    } 
  }
  
  private void setLocationHints(XSDDescription paramXSDDescription, String[] paramArrayOfString, StringList paramStringList) {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i];
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      try {
        String str = XMLEntityManager.expandSystemId(paramArrayOfString[b2], paramXSDDescription.getBaseSystemId(), false);
        if (!paramStringList.contains(str))
          arrayOfString[b1++] = paramArrayOfString[b2]; 
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {}
    } 
    if (b1 > 0)
      if (b1 == i) {
        this.fXSDDescription.fLocationHints = arrayOfString;
      } else {
        this.fXSDDescription.fLocationHints = new String[b1];
        System.arraycopy(arrayOfString, 0, this.fXSDDescription.fLocationHints, 0, b1);
      }  
  }
  
  XSTypeDefinition getAndCheckXsiType(QName paramQName, String paramString, XMLAttributes paramXMLAttributes) {
    QName qName = null;
    try {
      qName = (QName)this.fQNameDV.validate(paramString, this.fValidationState, null);
    } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
      reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
      reportSchemaError("cvc-elt.4.1", new Object[] { paramQName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, paramString });
      return null;
    } 
    XSTypeDefinition xSTypeDefinition = null;
    if (qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA)
      xSTypeDefinition = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(qName.localpart); 
    if (xSTypeDefinition == null) {
      SchemaGrammar schemaGrammar = findSchemaGrammar((short)7, qName.uri, paramQName, qName, paramXMLAttributes);
      if (schemaGrammar != null)
        xSTypeDefinition = schemaGrammar.getGlobalTypeDecl(qName.localpart); 
    } 
    if (xSTypeDefinition == null) {
      reportSchemaError("cvc-elt.4.2", new Object[] { paramQName.rawname, paramString });
      return null;
    } 
    if (this.fCurrentType != null) {
      short s = this.fCurrentElemDecl.fBlock;
      if (this.fCurrentType.getTypeCategory() == 15)
        s = (short)(s | ((XSComplexTypeDecl)this.fCurrentType).fBlock); 
      if (!XSConstraints.checkTypeDerivationOk(xSTypeDefinition, this.fCurrentType, s))
        reportSchemaError("cvc-elt.4.3", new Object[] { paramQName.rawname, paramString, this.fCurrentType.getName() }); 
    } 
    return xSTypeDefinition;
  }
  
  boolean getXsiNil(QName paramQName, String paramString) {
    if (this.fCurrentElemDecl != null && !this.fCurrentElemDecl.getNillable()) {
      reportSchemaError("cvc-elt.3.1", new Object[] { paramQName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
    } else {
      String str = XMLChar.trim(paramString);
      if (str.equals("true") || str.equals("1")) {
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2)
          reportSchemaError("cvc-elt.3.2.2", new Object[] { paramQName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL }); 
        return true;
      } 
    } 
    return false;
  }
  
  void processAttributes(QName paramQName, XMLAttributes paramXMLAttributes, XSAttributeGroupDecl paramXSAttributeGroupDecl) { // Byte code:
    //   0: aconst_null
    //   1: astore #4
    //   3: aload_2
    //   4: invokeinterface getLength : ()I
    //   9: istore #5
    //   11: aconst_null
    //   12: astore #6
    //   14: aconst_null
    //   15: astore #7
    //   17: aload_0
    //   18: getfield fCurrentType : Lcom/sun/org/apache/xerces/internal/xs/XSTypeDefinition;
    //   21: ifnull -> 38
    //   24: aload_0
    //   25: getfield fCurrentType : Lcom/sun/org/apache/xerces/internal/xs/XSTypeDefinition;
    //   28: invokeinterface getTypeCategory : ()S
    //   33: bipush #16
    //   35: if_icmpne -> 42
    //   38: iconst_1
    //   39: goto -> 43
    //   42: iconst_0
    //   43: istore #8
    //   45: aconst_null
    //   46: astore #9
    //   48: iconst_0
    //   49: istore #10
    //   51: aconst_null
    //   52: astore #11
    //   54: iload #8
    //   56: ifne -> 80
    //   59: aload_3
    //   60: invokevirtual getAttributeUses : ()Lcom/sun/org/apache/xerces/internal/xs/XSObjectList;
    //   63: astore #9
    //   65: aload #9
    //   67: invokeinterface getLength : ()I
    //   72: istore #10
    //   74: aload_3
    //   75: getfield fAttributeWC : Lcom/sun/org/apache/xerces/internal/impl/xs/XSWildcardDecl;
    //   78: astore #11
    //   80: iconst_0
    //   81: istore #12
    //   83: iload #12
    //   85: iload #5
    //   87: if_icmpge -> 736
    //   90: aload_2
    //   91: iload #12
    //   93: aload_0
    //   94: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   97: invokeinterface getName : (ILcom/sun/org/apache/xerces/internal/xni/QName;)V
    //   102: aload_0
    //   103: getfield fAugPSVI : Z
    //   106: ifne -> 116
    //   109: aload_0
    //   110: getfield fIdConstraint : Z
    //   113: ifeq -> 185
    //   116: aload_2
    //   117: iload #12
    //   119: invokeinterface getAugmentations : (I)Lcom/sun/org/apache/xerces/internal/xni/Augmentations;
    //   124: astore #6
    //   126: aload #6
    //   128: ldc_w 'ATTRIBUTE_PSVI'
    //   131: invokeinterface getItem : (Ljava/lang/String;)Ljava/lang/Object;
    //   136: checkcast com/sun/org/apache/xerces/internal/impl/xs/AttributePSVImpl
    //   139: astore #7
    //   141: aload #7
    //   143: ifnull -> 154
    //   146: aload #7
    //   148: invokevirtual reset : ()V
    //   151: goto -> 176
    //   154: new com/sun/org/apache/xerces/internal/impl/xs/AttributePSVImpl
    //   157: dup
    //   158: invokespecial <init> : ()V
    //   161: astore #7
    //   163: aload #6
    //   165: ldc_w 'ATTRIBUTE_PSVI'
    //   168: aload #7
    //   170: invokeinterface putItem : (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    //   175: pop
    //   176: aload #7
    //   178: aload_0
    //   179: getfield fValidationRoot : Ljava/lang/String;
    //   182: putfield fValidationContext : Ljava/lang/String;
    //   185: aload_0
    //   186: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   189: getfield uri : Ljava/lang/String;
    //   192: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.URI_XSI : Ljava/lang/String;
    //   195: if_acmpne -> 327
    //   198: aconst_null
    //   199: astore #13
    //   201: aload_0
    //   202: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   205: getfield localpart : Ljava/lang/String;
    //   208: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_SCHEMALOCATION : Ljava/lang/String;
    //   211: if_acmpne -> 228
    //   214: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar.SG_XSI : Lcom/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar$BuiltinSchemaGrammar;
    //   217: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_SCHEMALOCATION : Ljava/lang/String;
    //   220: invokevirtual getGlobalAttributeDecl : (Ljava/lang/String;)Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   223: astore #13
    //   225: goto -> 306
    //   228: aload_0
    //   229: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   232: getfield localpart : Ljava/lang/String;
    //   235: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION : Ljava/lang/String;
    //   238: if_acmpne -> 255
    //   241: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar.SG_XSI : Lcom/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar$BuiltinSchemaGrammar;
    //   244: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION : Ljava/lang/String;
    //   247: invokevirtual getGlobalAttributeDecl : (Ljava/lang/String;)Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   250: astore #13
    //   252: goto -> 306
    //   255: aload_0
    //   256: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   259: getfield localpart : Ljava/lang/String;
    //   262: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_NIL : Ljava/lang/String;
    //   265: if_acmpne -> 282
    //   268: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar.SG_XSI : Lcom/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar$BuiltinSchemaGrammar;
    //   271: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_NIL : Ljava/lang/String;
    //   274: invokevirtual getGlobalAttributeDecl : (Ljava/lang/String;)Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   277: astore #13
    //   279: goto -> 306
    //   282: aload_0
    //   283: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   286: getfield localpart : Ljava/lang/String;
    //   289: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_TYPE : Ljava/lang/String;
    //   292: if_acmpne -> 306
    //   295: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar.SG_XSI : Lcom/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar$BuiltinSchemaGrammar;
    //   298: getstatic com/sun/org/apache/xerces/internal/impl/xs/SchemaSymbols.XSI_TYPE : Ljava/lang/String;
    //   301: invokevirtual getGlobalAttributeDecl : (Ljava/lang/String;)Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   304: astore #13
    //   306: aload #13
    //   308: ifnull -> 327
    //   311: aload_0
    //   312: aload_1
    //   313: aload_2
    //   314: iload #12
    //   316: aload #13
    //   318: aconst_null
    //   319: aload #7
    //   321: invokevirtual processOneAttribute : (Lcom/sun/org/apache/xerces/internal/xni/QName;Lcom/sun/org/apache/xerces/internal/xni/XMLAttributes;ILcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeUseImpl;Lcom/sun/org/apache/xerces/internal/impl/xs/AttributePSVImpl;)V
    //   324: goto -> 730
    //   327: aload_0
    //   328: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   331: getfield rawname : Ljava/lang/String;
    //   334: getstatic com/sun/org/apache/xerces/internal/util/XMLSymbols.PREFIX_XMLNS : Ljava/lang/String;
    //   337: if_acmpeq -> 730
    //   340: aload_0
    //   341: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   344: getfield rawname : Ljava/lang/String;
    //   347: ldc_w 'xmlns:'
    //   350: invokevirtual startsWith : (Ljava/lang/String;)Z
    //   353: ifeq -> 359
    //   356: goto -> 730
    //   359: iload #8
    //   361: ifeq -> 395
    //   364: aload_0
    //   365: ldc_w 'cvc-type.3.1.1'
    //   368: iconst_2
    //   369: anewarray java/lang/Object
    //   372: dup
    //   373: iconst_0
    //   374: aload_1
    //   375: getfield rawname : Ljava/lang/String;
    //   378: aastore
    //   379: dup
    //   380: iconst_1
    //   381: aload_0
    //   382: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   385: getfield rawname : Ljava/lang/String;
    //   388: aastore
    //   389: invokevirtual reportSchemaError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   392: goto -> 730
    //   395: aconst_null
    //   396: astore #13
    //   398: iconst_0
    //   399: istore #15
    //   401: iload #15
    //   403: iload #10
    //   405: if_icmpge -> 471
    //   408: aload #9
    //   410: iload #15
    //   412: invokeinterface item : (I)Lcom/sun/org/apache/xerces/internal/xs/XSObject;
    //   417: checkcast com/sun/org/apache/xerces/internal/impl/xs/XSAttributeUseImpl
    //   420: astore #14
    //   422: aload #14
    //   424: getfield fAttrDecl : Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   427: getfield fName : Ljava/lang/String;
    //   430: aload_0
    //   431: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   434: getfield localpart : Ljava/lang/String;
    //   437: if_acmpne -> 465
    //   440: aload #14
    //   442: getfield fAttrDecl : Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   445: getfield fTargetNamespace : Ljava/lang/String;
    //   448: aload_0
    //   449: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   452: getfield uri : Ljava/lang/String;
    //   455: if_acmpne -> 465
    //   458: aload #14
    //   460: astore #13
    //   462: goto -> 471
    //   465: iinc #15, 1
    //   468: goto -> 401
    //   471: aload #13
    //   473: ifnonnull -> 527
    //   476: aload #11
    //   478: ifnull -> 496
    //   481: aload #11
    //   483: aload_0
    //   484: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   487: getfield uri : Ljava/lang/String;
    //   490: invokevirtual allowNamespace : (Ljava/lang/String;)Z
    //   493: ifne -> 527
    //   496: aload_0
    //   497: ldc_w 'cvc-complex-type.3.2.2'
    //   500: iconst_2
    //   501: anewarray java/lang/Object
    //   504: dup
    //   505: iconst_0
    //   506: aload_1
    //   507: getfield rawname : Ljava/lang/String;
    //   510: aastore
    //   511: dup
    //   512: iconst_1
    //   513: aload_0
    //   514: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   517: getfield rawname : Ljava/lang/String;
    //   520: aastore
    //   521: invokevirtual reportSchemaError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   524: goto -> 730
    //   527: aconst_null
    //   528: astore #15
    //   530: aload #13
    //   532: ifnull -> 545
    //   535: aload #13
    //   537: getfield fAttrDecl : Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   540: astore #15
    //   542: goto -> 716
    //   545: aload #11
    //   547: getfield fProcessContents : S
    //   550: iconst_2
    //   551: if_icmpne -> 557
    //   554: goto -> 730
    //   557: aload_0
    //   558: bipush #6
    //   560: aload_0
    //   561: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   564: getfield uri : Ljava/lang/String;
    //   567: aload_1
    //   568: aload_0
    //   569: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   572: aload_2
    //   573: invokevirtual findSchemaGrammar : (SLjava/lang/String;Lcom/sun/org/apache/xerces/internal/xni/QName;Lcom/sun/org/apache/xerces/internal/xni/QName;Lcom/sun/org/apache/xerces/internal/xni/XMLAttributes;)Lcom/sun/org/apache/xerces/internal/impl/xs/SchemaGrammar;
    //   576: astore #16
    //   578: aload #16
    //   580: ifnull -> 597
    //   583: aload #16
    //   585: aload_0
    //   586: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   589: getfield localpart : Ljava/lang/String;
    //   592: invokevirtual getGlobalAttributeDecl : (Ljava/lang/String;)Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;
    //   595: astore #15
    //   597: aload #15
    //   599: ifnonnull -> 642
    //   602: aload #11
    //   604: getfield fProcessContents : S
    //   607: iconst_1
    //   608: if_icmpne -> 730
    //   611: aload_0
    //   612: ldc_w 'cvc-complex-type.3.2.2'
    //   615: iconst_2
    //   616: anewarray java/lang/Object
    //   619: dup
    //   620: iconst_0
    //   621: aload_1
    //   622: getfield rawname : Ljava/lang/String;
    //   625: aastore
    //   626: dup
    //   627: iconst_1
    //   628: aload_0
    //   629: getfield fTempQName : Lcom/sun/org/apache/xerces/internal/xni/QName;
    //   632: getfield rawname : Ljava/lang/String;
    //   635: aastore
    //   636: invokevirtual reportSchemaError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   639: goto -> 730
    //   642: aload #15
    //   644: getfield fType : Lcom/sun/org/apache/xerces/internal/impl/dv/XSSimpleType;
    //   647: invokeinterface getTypeCategory : ()S
    //   652: bipush #16
    //   654: if_icmpne -> 716
    //   657: aload #15
    //   659: getfield fType : Lcom/sun/org/apache/xerces/internal/impl/dv/XSSimpleType;
    //   662: invokeinterface isIDType : ()Z
    //   667: ifeq -> 716
    //   670: aload #4
    //   672: ifnull -> 709
    //   675: aload_0
    //   676: ldc_w 'cvc-complex-type.5.1'
    //   679: iconst_3
    //   680: anewarray java/lang/Object
    //   683: dup
    //   684: iconst_0
    //   685: aload_1
    //   686: getfield rawname : Ljava/lang/String;
    //   689: aastore
    //   690: dup
    //   691: iconst_1
    //   692: aload #15
    //   694: getfield fName : Ljava/lang/String;
    //   697: aastore
    //   698: dup
    //   699: iconst_2
    //   700: aload #4
    //   702: aastore
    //   703: invokevirtual reportSchemaError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   706: goto -> 716
    //   709: aload #15
    //   711: getfield fName : Ljava/lang/String;
    //   714: astore #4
    //   716: aload_0
    //   717: aload_1
    //   718: aload_2
    //   719: iload #12
    //   721: aload #15
    //   723: aload #13
    //   725: aload #7
    //   727: invokevirtual processOneAttribute : (Lcom/sun/org/apache/xerces/internal/xni/QName;Lcom/sun/org/apache/xerces/internal/xni/XMLAttributes;ILcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeDecl;Lcom/sun/org/apache/xerces/internal/impl/xs/XSAttributeUseImpl;Lcom/sun/org/apache/xerces/internal/impl/xs/AttributePSVImpl;)V
    //   730: iinc #12, 1
    //   733: goto -> 83
    //   736: iload #8
    //   738: ifne -> 783
    //   741: aload_3
    //   742: getfield fIDAttrName : Ljava/lang/String;
    //   745: ifnull -> 783
    //   748: aload #4
    //   750: ifnull -> 783
    //   753: aload_0
    //   754: ldc_w 'cvc-complex-type.5.2'
    //   757: iconst_3
    //   758: anewarray java/lang/Object
    //   761: dup
    //   762: iconst_0
    //   763: aload_1
    //   764: getfield rawname : Ljava/lang/String;
    //   767: aastore
    //   768: dup
    //   769: iconst_1
    //   770: aload #4
    //   772: aastore
    //   773: dup
    //   774: iconst_2
    //   775: aload_3
    //   776: getfield fIDAttrName : Ljava/lang/String;
    //   779: aastore
    //   780: invokevirtual reportSchemaError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   783: return }
  
  void processOneAttribute(QName paramQName, XMLAttributes paramXMLAttributes, int paramInt, XSAttributeDecl paramXSAttributeDecl, XSAttributeUseImpl paramXSAttributeUseImpl, AttributePSVImpl paramAttributePSVImpl) {
    String str = paramXMLAttributes.getValue(paramInt);
    this.fXSIErrorReporter.pushContext();
    XSSimpleType xSSimpleType = paramXSAttributeDecl.fType;
    Object object = null;
    try {
      object = xSSimpleType.validate(str, this.fValidationState, this.fValidatedInfo);
      if (this.fNormalizeData)
        paramXMLAttributes.setValue(paramInt, this.fValidatedInfo.normalizedValue); 
      if (paramXMLAttributes instanceof XMLAttributesImpl) {
        XMLAttributesImpl xMLAttributesImpl = (XMLAttributesImpl)paramXMLAttributes;
        boolean bool = (this.fValidatedInfo.memberType != null) ? this.fValidatedInfo.memberType.isIDType() : xSSimpleType.isIDType();
        xMLAttributesImpl.setSchemaId(paramInt, bool);
      } 
      if (xSSimpleType.getVariety() == 1 && xSSimpleType.getPrimitiveKind() == 20) {
        QName qName = (QName)object;
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(qName.uri);
        if (schemaGrammar != null)
          this.fNotation = schemaGrammar.getGlobalNotationDecl(qName.localpart); 
      } 
    } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
      reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
      reportSchemaError("cvc-attribute.3", new Object[] { paramQName.rawname, this.fTempQName.rawname, str, xSSimpleType.getName() });
    } 
    if (object != null && paramXSAttributeDecl.getConstraintType() == 2 && (!isComparable(this.fValidatedInfo, paramXSAttributeDecl.fDefault) || !object.equals(paramXSAttributeDecl.fDefault.actualValue)))
      reportSchemaError("cvc-attribute.4", new Object[] { paramQName.rawname, this.fTempQName.rawname, str, paramXSAttributeDecl.fDefault.stringValue() }); 
    if (object != null && paramXSAttributeUseImpl != null && paramXSAttributeUseImpl.fConstraintType == 2 && (!isComparable(this.fValidatedInfo, paramXSAttributeUseImpl.fDefault) || !object.equals(paramXSAttributeUseImpl.fDefault.actualValue)))
      reportSchemaError("cvc-complex-type.3.1", new Object[] { paramQName.rawname, this.fTempQName.rawname, str, paramXSAttributeUseImpl.fDefault.stringValue() }); 
    if (this.fIdConstraint)
      paramAttributePSVImpl.fActualValue = object; 
    if (this.fAugPSVI) {
      paramAttributePSVImpl.fDeclaration = paramXSAttributeDecl;
      paramAttributePSVImpl.fTypeDecl = xSSimpleType;
      paramAttributePSVImpl.fMemberType = this.fValidatedInfo.memberType;
      paramAttributePSVImpl.fNormalizedValue = this.fValidatedInfo.normalizedValue;
      paramAttributePSVImpl.fActualValue = this.fValidatedInfo.actualValue;
      paramAttributePSVImpl.fActualValueType = this.fValidatedInfo.actualValueType;
      paramAttributePSVImpl.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
      paramAttributePSVImpl.fValidationAttempted = 2;
      String[] arrayOfString = this.fXSIErrorReporter.mergeContext();
      paramAttributePSVImpl.fErrorCodes = arrayOfString;
      paramAttributePSVImpl.fValidity = (arrayOfString == null) ? 2 : 1;
    } 
  }
  
  void addDefaultAttributes(QName paramQName, XMLAttributes paramXMLAttributes, XSAttributeGroupDecl paramXSAttributeGroupDecl) {
    XSObjectList xSObjectList = paramXSAttributeGroupDecl.getAttributeUses();
    int i = xSObjectList.getLength();
    for (byte b = 0; b < i; b++) {
      XSAttributeUseImpl xSAttributeUseImpl = (XSAttributeUseImpl)xSObjectList.item(b);
      XSAttributeDecl xSAttributeDecl = xSAttributeUseImpl.fAttrDecl;
      short s = xSAttributeUseImpl.fConstraintType;
      ValidatedInfo validatedInfo = xSAttributeUseImpl.fDefault;
      if (s == 0) {
        s = xSAttributeDecl.getConstraintType();
        validatedInfo = xSAttributeDecl.fDefault;
      } 
      boolean bool = (paramXMLAttributes.getValue(xSAttributeDecl.fTargetNamespace, xSAttributeDecl.fName) != null) ? 1 : 0;
      if (xSAttributeUseImpl.fUse == 1 && !bool)
        reportSchemaError("cvc-complex-type.4", new Object[] { paramQName.rawname, xSAttributeDecl.fName }); 
      if (!bool && s != 0) {
        QName qName = new QName(null, xSAttributeDecl.fName, xSAttributeDecl.fName, xSAttributeDecl.fTargetNamespace);
        String str = (validatedInfo != null) ? validatedInfo.stringValue() : "";
        int j = paramXMLAttributes.addAttribute(qName, "CDATA", str);
        if (paramXMLAttributes instanceof XMLAttributesImpl) {
          XMLAttributesImpl xMLAttributesImpl = (XMLAttributesImpl)paramXMLAttributes;
          boolean bool1 = (validatedInfo != null && validatedInfo.memberType != null) ? validatedInfo.memberType.isIDType() : xSAttributeDecl.fType.isIDType();
          xMLAttributesImpl.setSchemaId(j, bool1);
        } 
        if (this.fAugPSVI) {
          Augmentations augmentations = paramXMLAttributes.getAugmentations(j);
          AttributePSVImpl attributePSVImpl = new AttributePSVImpl();
          augmentations.putItem("ATTRIBUTE_PSVI", attributePSVImpl);
          attributePSVImpl.fDeclaration = xSAttributeDecl;
          attributePSVImpl.fTypeDecl = xSAttributeDecl.fType;
          attributePSVImpl.fMemberType = validatedInfo.memberType;
          attributePSVImpl.fNormalizedValue = str;
          attributePSVImpl.fActualValue = validatedInfo.actualValue;
          attributePSVImpl.fActualValueType = validatedInfo.actualValueType;
          attributePSVImpl.fItemValueTypes = validatedInfo.itemValueTypes;
          attributePSVImpl.fValidationContext = this.fValidationRoot;
          attributePSVImpl.fValidity = 2;
          attributePSVImpl.fValidationAttempted = 2;
          attributePSVImpl.fSpecified = true;
        } 
      } 
    } 
  }
  
  void processElementContent(QName paramQName) {
    if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fDefault != null && !this.fSawText && !this.fSubElement && !this.fNil) {
      String str = this.fCurrentElemDecl.fDefault.stringValue();
      int i = str.length();
      if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < i)
        this.fNormalizedStr.ch = new char[i]; 
      str.getChars(0, i, this.fNormalizedStr.ch, 0);
      this.fNormalizedStr.offset = 0;
      this.fNormalizedStr.length = i;
      this.fDefaultValue = this.fNormalizedStr;
    } 
    this.fValidatedInfo.normalizedValue = null;
    if (this.fNil && (this.fSubElement || this.fSawText))
      reportSchemaError("cvc-elt.3.2.1", new Object[] { paramQName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL }); 
    this.fValidatedInfo.reset();
    if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() != 0 && !this.fSubElement && !this.fSawText && !this.fNil) {
      if (this.fCurrentType != this.fCurrentElemDecl.fType && XSConstraints.ElementDefaultValidImmediate(this.fCurrentType, this.fCurrentElemDecl.fDefault.stringValue(), this.fState4XsiType, null) == null)
        reportSchemaError("cvc-elt.5.1.1", new Object[] { paramQName.rawname, this.fCurrentType.getName(), this.fCurrentElemDecl.fDefault.stringValue() }); 
      elementLocallyValidType(paramQName, this.fCurrentElemDecl.fDefault.stringValue());
    } else {
      Object object = elementLocallyValidType(paramQName, this.fBuffer);
      if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2 && !this.fNil) {
        String str = this.fBuffer.toString();
        if (this.fSubElement)
          reportSchemaError("cvc-elt.5.2.2.1", new Object[] { paramQName.rawname }); 
        if (this.fCurrentType.getTypeCategory() == 15) {
          XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
          if (xSComplexTypeDecl.fContentType == 3) {
            if (!this.fCurrentElemDecl.fDefault.normalizedValue.equals(str))
              reportSchemaError("cvc-elt.5.2.2.2.1", new Object[] { paramQName.rawname, str, this.fCurrentElemDecl.fDefault.normalizedValue }); 
          } else if (xSComplexTypeDecl.fContentType == 1 && object != null && (!isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) || !object.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
            reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { paramQName.rawname, str, this.fCurrentElemDecl.fDefault.stringValue() });
          } 
        } else if (this.fCurrentType.getTypeCategory() == 16 && object != null && (!isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) || !object.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
          reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { paramQName.rawname, str, this.fCurrentElemDecl.fDefault.stringValue() });
        } 
      } 
    } 
    if (this.fDefaultValue == null && this.fNormalizeData && this.fDocumentHandler != null && this.fUnionType) {
      String str = this.fValidatedInfo.normalizedValue;
      if (str == null)
        str = this.fBuffer.toString(); 
      int i = str.length();
      if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < i)
        this.fNormalizedStr.ch = new char[i]; 
      str.getChars(0, i, this.fNormalizedStr.ch, 0);
      this.fNormalizedStr.offset = 0;
      this.fNormalizedStr.length = i;
      this.fDocumentHandler.characters(this.fNormalizedStr, null);
    } 
  }
  
  Object elementLocallyValidType(QName paramQName, Object paramObject) {
    if (this.fCurrentType == null)
      return null; 
    Object object = null;
    if (this.fCurrentType.getTypeCategory() == 16) {
      if (this.fSubElement)
        reportSchemaError("cvc-type.3.1.2", new Object[] { paramQName.rawname }); 
      if (!this.fNil) {
        XSSimpleType xSSimpleType = (XSSimpleType)this.fCurrentType;
        try {
          if (!this.fNormalizeData || this.fUnionType)
            this.fValidationState.setNormalizationRequired(true); 
          object = xSSimpleType.validate(paramObject, this.fValidationState, this.fValidatedInfo);
        } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
          reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
          reportSchemaError("cvc-type.3.1.3", new Object[] { paramQName.rawname, paramObject });
        } 
      } 
    } else {
      object = elementLocallyValidComplexType(paramQName, paramObject);
    } 
    return object;
  }
  
  Object elementLocallyValidComplexType(QName paramQName, Object paramObject) {
    Object object = null;
    XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
    if (!this.fNil) {
      if (xSComplexTypeDecl.fContentType == 0 && (this.fSubElement || this.fSawText)) {
        reportSchemaError("cvc-complex-type.2.1", new Object[] { paramQName.rawname });
      } else if (xSComplexTypeDecl.fContentType == 1) {
        if (this.fSubElement)
          reportSchemaError("cvc-complex-type.2.2", new Object[] { paramQName.rawname }); 
        XSSimpleType xSSimpleType = xSComplexTypeDecl.fXSSimpleType;
        try {
          if (!this.fNormalizeData || this.fUnionType)
            this.fValidationState.setNormalizationRequired(true); 
          object = xSSimpleType.validate(paramObject, this.fValidationState, this.fValidatedInfo);
        } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
          reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
          reportSchemaError("cvc-complex-type.2.2", new Object[] { paramQName.rawname });
        } 
      } else if (xSComplexTypeDecl.fContentType == 2 && this.fSawCharacters) {
        reportSchemaError("cvc-complex-type.2.3", new Object[] { paramQName.rawname });
      } 
      if (xSComplexTypeDecl.fContentType == 2 || xSComplexTypeDecl.fContentType == 3)
        if (this.fCurrCMState[0] >= 0 && !this.fCurrentCM.endContentModel(this.fCurrCMState)) {
          String str = expectedStr(this.fCurrentCM.whatCanGoHere(this.fCurrCMState));
          reportSchemaError("cvc-complex-type.2.4.b", new Object[] { paramQName.rawname, str });
        } else {
          ArrayList arrayList = this.fCurrentCM.checkMinMaxBounds();
          if (arrayList != null)
            for (byte b = 0; b < arrayList.size(); b += 2) {
              reportSchemaError((String)arrayList.get(b), new Object[] { paramQName.rawname, arrayList.get(b + 1) });
            }  
        }  
    } 
    return object;
  }
  
  void reportSchemaError(String paramString, Object[] paramArrayOfObject) {
    if (this.fDoValidation)
      this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", paramString, paramArrayOfObject, (short)1); 
  }
  
  private boolean isComparable(ValidatedInfo paramValidatedInfo1, ValidatedInfo paramValidatedInfo2) {
    short s1 = convertToPrimitiveKind(paramValidatedInfo1.actualValueType);
    short s2 = convertToPrimitiveKind(paramValidatedInfo2.actualValueType);
    if (s1 != s2)
      return ((s1 == 1 && s2 == 2) || (s1 == 2 && s2 == 1)); 
    if (s1 == 44 || s1 == 43) {
      ShortList shortList1 = paramValidatedInfo1.itemValueTypes;
      ShortList shortList2 = paramValidatedInfo2.itemValueTypes;
      int i = (shortList1 != null) ? shortList1.getLength() : 0;
      int j = (shortList2 != null) ? shortList2.getLength() : 0;
      if (i != j)
        return false; 
      byte b = 0;
      while (b < i) {
        short s3 = convertToPrimitiveKind(shortList1.item(b));
        short s4 = convertToPrimitiveKind(shortList2.item(b));
        if (s3 == s4 || (s3 == 1 && s4 == 2) || (s3 == 2 && s4 == 1)) {
          b++;
          continue;
        } 
        return false;
      } 
    } 
    return true;
  }
  
  private short convertToPrimitiveKind(short paramShort) { return (paramShort <= 20) ? paramShort : ((paramShort <= 29) ? 2 : ((paramShort <= 42) ? 4 : paramShort)); }
  
  private String expectedStr(Vector paramVector) {
    StringBuffer stringBuffer = new StringBuffer("{");
    int i = paramVector.size();
    for (byte b = 0; b < i; b++) {
      if (b)
        stringBuffer.append(", "); 
      stringBuffer.append(paramVector.elementAt(b).toString());
    } 
    stringBuffer.append('}');
    return stringBuffer.toString();
  }
  
  protected class KeyRefValueStore extends ValueStoreBase {
    protected XMLSchemaValidator.ValueStoreBase fKeyValueStore;
    
    public KeyRefValueStore(KeyRef param1KeyRef, XMLSchemaValidator.KeyValueStore param1KeyValueStore) {
      super(XMLSchemaValidator.this, param1KeyRef);
      this.fKeyValueStore = param1KeyValueStore;
    }
    
    public void endDocumentFragment() {
      super.endDocumentFragment();
      this.fKeyValueStore = (XMLSchemaValidator.ValueStoreBase)this.this$0.fValueStoreCache.fGlobalIDConstraintMap.get(((KeyRef)this.fIdentityConstraint).getKey());
      if (this.fKeyValueStore == null) {
        String str1 = "KeyRefOutOfScope";
        String str2 = this.fIdentityConstraint.toString();
        XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { str2 });
        return;
      } 
      int i = this.fKeyValueStore.contains(this);
      if (i != -1) {
        String str1 = "KeyNotFound";
        String str2 = toString(this.fValues, i, this.fFieldCount);
        String str3 = this.fIdentityConstraint.getElementName();
        String str4 = this.fIdentityConstraint.getName();
        XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { str4, str2, str3 });
      } 
    }
    
    public void endDocument() { super.endDocument(); }
  }
  
  protected class KeyValueStore extends ValueStoreBase {
    public KeyValueStore(UniqueOrKey param1UniqueOrKey) { super(XMLSchemaValidator.this, param1UniqueOrKey); }
    
    protected void checkDuplicateValues() {
      if (contains()) {
        String str1 = "DuplicateKey";
        String str2 = toString(this.fLocalValues);
        String str3 = this.fIdentityConstraint.getElementName();
        String str4 = this.fIdentityConstraint.getIdentityConstraintName();
        XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { str2, str3, str4 });
      } 
    }
  }
  
  protected class LocalIDKey {
    public IdentityConstraint fId;
    
    public int fDepth;
    
    public LocalIDKey() {}
    
    public LocalIDKey(IdentityConstraint param1IdentityConstraint, int param1Int) {
      this.fId = param1IdentityConstraint;
      this.fDepth = param1Int;
    }
    
    public int hashCode() { return this.fId.hashCode() + this.fDepth; }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof LocalIDKey) {
        LocalIDKey localIDKey = (LocalIDKey)param1Object;
        return (localIDKey.fId == this.fId && localIDKey.fDepth == this.fDepth);
      } 
      return false;
    }
  }
  
  protected static final class ShortVector {
    private int fLength;
    
    private short[] fData;
    
    public ShortVector() {}
    
    public ShortVector(int param1Int) { this.fData = new short[param1Int]; }
    
    public int length() { return this.fLength; }
    
    public void add(short param1Short) {
      ensureCapacity(this.fLength + 1);
      this.fData[this.fLength++] = param1Short;
    }
    
    public short valueAt(int param1Int) { return this.fData[param1Int]; }
    
    public void clear() { this.fLength = 0; }
    
    public boolean contains(short param1Short) {
      for (byte b = 0; b < this.fLength; b++) {
        if (this.fData[b] == param1Short)
          return true; 
      } 
      return false;
    }
    
    private void ensureCapacity(int param1Int) {
      if (this.fData == null) {
        this.fData = new short[8];
      } else if (this.fData.length <= param1Int) {
        short[] arrayOfShort = new short[this.fData.length * 2];
        System.arraycopy(this.fData, 0, arrayOfShort, 0, this.fData.length);
        this.fData = arrayOfShort;
      } 
    }
  }
  
  protected class UniqueValueStore extends ValueStoreBase {
    public UniqueValueStore(UniqueOrKey param1UniqueOrKey) { super(XMLSchemaValidator.this, param1UniqueOrKey); }
    
    protected void checkDuplicateValues() {
      if (contains()) {
        String str1 = "DuplicateUnique";
        String str2 = toString(this.fLocalValues);
        String str3 = this.fIdentityConstraint.getElementName();
        String str4 = this.fIdentityConstraint.getIdentityConstraintName();
        XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { str2, str3, str4 });
      } 
    }
  }
  
  protected abstract class ValueStoreBase implements ValueStore {
    protected IdentityConstraint fIdentityConstraint;
    
    protected int fFieldCount = 0;
    
    protected Field[] fFields = null;
    
    protected Object[] fLocalValues = null;
    
    protected short[] fLocalValueTypes = null;
    
    protected ShortList[] fLocalItemValueTypes = null;
    
    protected int fValuesCount;
    
    public final Vector fValues = new Vector();
    
    public XMLSchemaValidator.ShortVector fValueTypes = null;
    
    public Vector fItemValueTypes = null;
    
    private boolean fUseValueTypeVector = false;
    
    private int fValueTypesLength = 0;
    
    private short fValueType = 0;
    
    private boolean fUseItemValueTypeVector = false;
    
    private int fItemValueTypesLength = 0;
    
    private ShortList fItemValueType = null;
    
    final StringBuffer fTempBuffer = new StringBuffer();
    
    protected ValueStoreBase(IdentityConstraint param1IdentityConstraint) {
      this.fIdentityConstraint = param1IdentityConstraint;
      this.fFieldCount = this.fIdentityConstraint.getFieldCount();
      this.fFields = new Field[this.fFieldCount];
      this.fLocalValues = new Object[this.fFieldCount];
      this.fLocalValueTypes = new short[this.fFieldCount];
      this.fLocalItemValueTypes = new ShortList[this.fFieldCount];
      for (byte b = 0; b < this.fFieldCount; b++)
        this.fFields[b] = this.fIdentityConstraint.getFieldAt(b); 
    }
    
    public void clear() {
      this.fValuesCount = 0;
      this.fUseValueTypeVector = false;
      this.fValueTypesLength = 0;
      this.fValueType = 0;
      this.fUseItemValueTypeVector = false;
      this.fItemValueTypesLength = 0;
      this.fItemValueType = null;
      this.fValues.setSize(0);
      if (this.fValueTypes != null)
        this.fValueTypes.clear(); 
      if (this.fItemValueTypes != null)
        this.fItemValueTypes.setSize(0); 
    }
    
    public void append(ValueStoreBase param1ValueStoreBase) {
      for (byte b = 0; b < param1ValueStoreBase.fValues.size(); b++)
        this.fValues.addElement(param1ValueStoreBase.fValues.elementAt(b)); 
    }
    
    public void startValueScope() {
      this.fValuesCount = 0;
      for (byte b = 0; b < this.fFieldCount; b++) {
        this.fLocalValues[b] = null;
        this.fLocalValueTypes[b] = 0;
        this.fLocalItemValueTypes[b] = null;
      } 
    }
    
    public void endValueScope() {
      if (this.fValuesCount == 0) {
        if (this.fIdentityConstraint.getCategory() == 1) {
          String str1 = "AbsentKeyValue";
          String str2 = this.fIdentityConstraint.getElementName();
          String str3 = this.fIdentityConstraint.getIdentityConstraintName();
          XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { str2, str3 });
        } 
        return;
      } 
      if (this.fValuesCount != this.fFieldCount) {
        if (this.fIdentityConstraint.getCategory() == 1) {
          String str1 = "KeyNotEnoughValues";
          UniqueOrKey uniqueOrKey = (UniqueOrKey)this.fIdentityConstraint;
          String str2 = this.fIdentityConstraint.getElementName();
          String str3 = uniqueOrKey.getIdentityConstraintName();
          XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { str2, str3 });
        } 
        return;
      } 
    }
    
    public void endDocumentFragment() {}
    
    public void endDocument() {}
    
    public void reportError(String param1String, Object[] param1ArrayOfObject) { XMLSchemaValidator.this.reportSchemaError(param1String, param1ArrayOfObject); }
    
    public void addValue(Field param1Field, Object param1Object, short param1Short, ShortList param1ShortList) {
      int i;
      for (i = this.fFieldCount - 1; i > -1 && this.fFields[i] != param1Field; i--);
      if (i == -1) {
        String str1 = "UnknownField";
        String str2 = this.fIdentityConstraint.getElementName();
        String str3 = this.fIdentityConstraint.getIdentityConstraintName();
        XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { param1Field.toString(), str2, str3 });
        return;
      } 
      if (Boolean.TRUE != XMLSchemaValidator.this.mayMatch(param1Field)) {
        String str1 = "FieldMultipleMatch";
        String str2 = this.fIdentityConstraint.getIdentityConstraintName();
        XMLSchemaValidator.this.reportSchemaError(str1, new Object[] { param1Field.toString(), str2 });
      } else {
        this.fValuesCount++;
      } 
      this.fLocalValues[i] = param1Object;
      this.fLocalValueTypes[i] = param1Short;
      this.fLocalItemValueTypes[i] = param1ShortList;
      if (this.fValuesCount == this.fFieldCount) {
        checkDuplicateValues();
        for (i = 0; i < this.fFieldCount; i++) {
          this.fValues.addElement(this.fLocalValues[i]);
          addValueType(this.fLocalValueTypes[i]);
          addItemValueType(this.fLocalItemValueTypes[i]);
        } 
      } 
    }
    
    public boolean contains() {
      int i = 0;
      int j = this.fValues.size();
      for (int k = 0; k < j; k = i) {
        i = k + this.fFieldCount;
        byte b = 0;
        while (true) {
          if (b < this.fFieldCount) {
            Object object1 = this.fLocalValues[b];
            Object object2 = this.fValues.elementAt(k);
            short s1 = this.fLocalValueTypes[b];
            short s2 = getValueTypeAt(k);
            if (object1 == null || object2 == null || s1 != s2 || !object1.equals(object2))
              break; 
            if (s1 == 44 || s1 == 43) {
              ShortList shortList1 = this.fLocalItemValueTypes[b];
              ShortList shortList2 = getItemValueTypeAt(k);
              if (shortList1 == null || shortList2 == null || !shortList1.equals(shortList2))
                break; 
            } 
            k++;
            b++;
            continue;
          } 
          return true;
        } 
      } 
      return false;
    }
    
    public int contains(ValueStoreBase param1ValueStoreBase) {
      Vector vector = param1ValueStoreBase.fValues;
      int i = vector.size();
      if (this.fFieldCount <= 1) {
        for (byte b = 0; b < i; b++) {
          short s = param1ValueStoreBase.getValueTypeAt(b);
          if (!valueTypeContains(s) || !this.fValues.contains(vector.elementAt(b)))
            return b; 
          if (s == 44 || s == 43) {
            ShortList shortList = param1ValueStoreBase.getItemValueTypeAt(b);
            if (!itemValueTypeContains(shortList))
              return b; 
          } 
        } 
      } else {
        int j = this.fValues.size();
        int k;
        for (k = 0; k < i; k += this.fFieldCount) {
          int m = 0;
          label52: while (true) {
            if (m < j) {
              for (int n = 0; n < this.fFieldCount; n++) {
                Object object1 = vector.elementAt(k + n);
                Object object2 = this.fValues.elementAt(m + n);
                short s1 = param1ValueStoreBase.getValueTypeAt(k + n);
                short s2 = getValueTypeAt(m + n);
                if (object1 != object2)
                  if (s1 == s2) {
                    if (object1 != null) {
                      if (!object1.equals(object2)) {
                        m += this.fFieldCount;
                        continue;
                      } 
                    } else {
                      continue label52;
                    } 
                  } else {
                    continue label52;
                  }  
                if (s1 == 44 || s1 == 43) {
                  ShortList shortList1 = param1ValueStoreBase.getItemValueTypeAt(k + n);
                  ShortList shortList2 = getItemValueTypeAt(m + n);
                  if (shortList1 != null) {
                    if (shortList2 != null) {
                      if (!shortList1.equals(shortList2))
                        continue label52; 
                    } else {
                      continue label52;
                    } 
                  } else {
                    continue label52;
                  } 
                } 
              } 
              break;
            } 
            return k;
          } 
        } 
      } 
      return -1;
    }
    
    protected void checkDuplicateValues() {}
    
    protected String toString(Object[] param1ArrayOfObject) {
      int i = param1ArrayOfObject.length;
      if (i == 0)
        return ""; 
      this.fTempBuffer.setLength(0);
      for (byte b = 0; b < i; b++) {
        if (b)
          this.fTempBuffer.append(','); 
        this.fTempBuffer.append(param1ArrayOfObject[b]);
      } 
      return this.fTempBuffer.toString();
    }
    
    protected String toString(Vector param1Vector, int param1Int1, int param1Int2) {
      if (param1Int2 == 0)
        return ""; 
      if (param1Int2 == 1)
        return String.valueOf(param1Vector.elementAt(param1Int1)); 
      StringBuffer stringBuffer = new StringBuffer();
      for (int i = 0; i < param1Int2; i++) {
        if (i)
          stringBuffer.append(','); 
        stringBuffer.append(param1Vector.elementAt(param1Int1 + i));
      } 
      return stringBuffer.toString();
    }
    
    public String toString() {
      String str = super.toString();
      int i = str.lastIndexOf('$');
      if (i != -1)
        str = str.substring(i + 1); 
      int j = str.lastIndexOf('.');
      if (j != -1)
        str = str.substring(j + 1); 
      return str + '[' + this.fIdentityConstraint + ']';
    }
    
    private void addValueType(short param1Short) {
      if (this.fUseValueTypeVector) {
        this.fValueTypes.add(param1Short);
      } else if (this.fValueTypesLength++ == 0) {
        this.fValueType = param1Short;
      } else if (this.fValueType != param1Short) {
        this.fUseValueTypeVector = true;
        if (this.fValueTypes == null)
          this.fValueTypes = new XMLSchemaValidator.ShortVector(this.fValueTypesLength * 2); 
        for (byte b = 1; b < this.fValueTypesLength; b++)
          this.fValueTypes.add(this.fValueType); 
        this.fValueTypes.add(param1Short);
      } 
    }
    
    private short getValueTypeAt(int param1Int) { return this.fUseValueTypeVector ? this.fValueTypes.valueAt(param1Int) : this.fValueType; }
    
    private boolean valueTypeContains(short param1Short) { return this.fUseValueTypeVector ? this.fValueTypes.contains(param1Short) : ((this.fValueType == param1Short) ? 1 : 0); }
    
    private void addItemValueType(ShortList param1ShortList) {
      if (this.fUseItemValueTypeVector) {
        this.fItemValueTypes.add(param1ShortList);
      } else if (this.fItemValueTypesLength++ == 0) {
        this.fItemValueType = param1ShortList;
      } else if (this.fItemValueType != param1ShortList && (this.fItemValueType == null || !this.fItemValueType.equals(param1ShortList))) {
        this.fUseItemValueTypeVector = true;
        if (this.fItemValueTypes == null)
          this.fItemValueTypes = new Vector(this.fItemValueTypesLength * 2); 
        for (byte b = 1; b < this.fItemValueTypesLength; b++)
          this.fItemValueTypes.add(this.fItemValueType); 
        this.fItemValueTypes.add(param1ShortList);
      } 
    }
    
    private ShortList getItemValueTypeAt(int param1Int) { return this.fUseItemValueTypeVector ? (ShortList)this.fItemValueTypes.elementAt(param1Int) : this.fItemValueType; }
    
    private boolean itemValueTypeContains(ShortList param1ShortList) { return this.fUseItemValueTypeVector ? this.fItemValueTypes.contains(param1ShortList) : ((this.fItemValueType == param1ShortList || (this.fItemValueType != null && this.fItemValueType.equals(param1ShortList))) ? 1 : 0); }
  }
  
  protected class ValueStoreCache {
    final XMLSchemaValidator.LocalIDKey fLocalId = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this);
    
    protected final Vector fValueStores = new Vector();
    
    protected final Map<XMLSchemaValidator.LocalIDKey, XMLSchemaValidator.ValueStoreBase> fIdentityConstraint2ValueStoreMap = new HashMap();
    
    protected final Stack<Map<IdentityConstraint, XMLSchemaValidator.ValueStoreBase>> fGlobalMapStack = new Stack();
    
    protected final Map<IdentityConstraint, XMLSchemaValidator.ValueStoreBase> fGlobalIDConstraintMap = new HashMap();
    
    public void startDocument() {
      this.fValueStores.removeAllElements();
      this.fIdentityConstraint2ValueStoreMap.clear();
      this.fGlobalIDConstraintMap.clear();
      this.fGlobalMapStack.removeAllElements();
    }
    
    public void startElement() {
      if (this.fGlobalIDConstraintMap.size() > 0) {
        this.fGlobalMapStack.push((Map)((HashMap)this.fGlobalIDConstraintMap).clone());
      } else {
        this.fGlobalMapStack.push(null);
      } 
      this.fGlobalIDConstraintMap.clear();
    }
    
    public void endElement() {
      if (this.fGlobalMapStack.isEmpty())
        return; 
      Map map = (Map)this.fGlobalMapStack.pop();
      if (map == null)
        return; 
      for (Map.Entry entry : map.entrySet()) {
        IdentityConstraint identityConstraint = (IdentityConstraint)entry.getKey();
        XMLSchemaValidator.ValueStoreBase valueStoreBase = (XMLSchemaValidator.ValueStoreBase)entry.getValue();
        if (valueStoreBase != null) {
          XMLSchemaValidator.ValueStoreBase valueStoreBase1 = (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(identityConstraint);
          if (valueStoreBase1 == null) {
            this.fGlobalIDConstraintMap.put(identityConstraint, valueStoreBase);
            continue;
          } 
          if (valueStoreBase1 != valueStoreBase)
            valueStoreBase1.append(valueStoreBase); 
        } 
      } 
    }
    
    public void initValueStoresFor(XSElementDecl param1XSElementDecl, FieldActivator param1FieldActivator) {
      IdentityConstraint[] arrayOfIdentityConstraint = param1XSElementDecl.fIDConstraints;
      int i = param1XSElementDecl.fIDCPos;
      for (byte b = 0; b < i; b++) {
        XMLSchemaValidator.KeyRefValueStore keyRefValueStore;
        KeyRef keyRef;
        XMLSchemaValidator.KeyValueStore keyValueStore;
        UniqueOrKey uniqueOrKey2;
        XMLSchemaValidator.UniqueValueStore uniqueValueStore;
        XMLSchemaValidator.LocalIDKey localIDKey;
        UniqueOrKey uniqueOrKey1;
        switch (arrayOfIdentityConstraint[b].getCategory()) {
          case 3:
            uniqueOrKey1 = (UniqueOrKey)arrayOfIdentityConstraint[b];
            localIDKey = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this, uniqueOrKey1, XMLSchemaValidator.this.fElementDepth);
            uniqueValueStore = (XMLSchemaValidator.UniqueValueStore)this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
            if (uniqueValueStore == null) {
              uniqueValueStore = new XMLSchemaValidator.UniqueValueStore(XMLSchemaValidator.this, uniqueOrKey1);
              this.fIdentityConstraint2ValueStoreMap.put(localIDKey, uniqueValueStore);
            } else {
              uniqueValueStore.clear();
            } 
            this.fValueStores.addElement(uniqueValueStore);
            XMLSchemaValidator.this.activateSelectorFor(arrayOfIdentityConstraint[b]);
            break;
          case 1:
            uniqueOrKey2 = (UniqueOrKey)arrayOfIdentityConstraint[b];
            localIDKey = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this, uniqueOrKey2, XMLSchemaValidator.this.fElementDepth);
            keyValueStore = (XMLSchemaValidator.KeyValueStore)this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
            if (keyValueStore == null) {
              keyValueStore = new XMLSchemaValidator.KeyValueStore(XMLSchemaValidator.this, uniqueOrKey2);
              this.fIdentityConstraint2ValueStoreMap.put(localIDKey, keyValueStore);
            } else {
              keyValueStore.clear();
            } 
            this.fValueStores.addElement(keyValueStore);
            XMLSchemaValidator.this.activateSelectorFor(arrayOfIdentityConstraint[b]);
            break;
          case 2:
            keyRef = (KeyRef)arrayOfIdentityConstraint[b];
            localIDKey = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this, keyRef, XMLSchemaValidator.this.fElementDepth);
            keyRefValueStore = (XMLSchemaValidator.KeyRefValueStore)this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
            if (keyRefValueStore == null) {
              keyRefValueStore = new XMLSchemaValidator.KeyRefValueStore(XMLSchemaValidator.this, keyRef, null);
              this.fIdentityConstraint2ValueStoreMap.put(localIDKey, keyRefValueStore);
            } else {
              keyRefValueStore.clear();
            } 
            this.fValueStores.addElement(keyRefValueStore);
            XMLSchemaValidator.this.activateSelectorFor(arrayOfIdentityConstraint[b]);
            break;
        } 
      } 
    }
    
    public XMLSchemaValidator.ValueStoreBase getValueStoreFor(IdentityConstraint param1IdentityConstraint, int param1Int) {
      this.fLocalId.fDepth = param1Int;
      this.fLocalId.fId = param1IdentityConstraint;
      return (XMLSchemaValidator.ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
    }
    
    public XMLSchemaValidator.ValueStoreBase getGlobalValueStoreFor(IdentityConstraint param1IdentityConstraint) { return (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(param1IdentityConstraint); }
    
    public void transplant(IdentityConstraint param1IdentityConstraint, int param1Int) {
      this.fLocalId.fDepth = param1Int;
      this.fLocalId.fId = param1IdentityConstraint;
      XMLSchemaValidator.ValueStoreBase valueStoreBase1 = (XMLSchemaValidator.ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
      if (param1IdentityConstraint.getCategory() == 2)
        return; 
      XMLSchemaValidator.ValueStoreBase valueStoreBase2 = (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(param1IdentityConstraint);
      if (valueStoreBase2 != null) {
        valueStoreBase2.append(valueStoreBase1);
        this.fGlobalIDConstraintMap.put(param1IdentityConstraint, valueStoreBase2);
      } else {
        this.fGlobalIDConstraintMap.put(param1IdentityConstraint, valueStoreBase1);
      } 
    }
    
    public void endDocument() {
      int i = this.fValueStores.size();
      for (byte b = 0; b < i; b++) {
        XMLSchemaValidator.ValueStoreBase valueStoreBase = (XMLSchemaValidator.ValueStoreBase)this.fValueStores.elementAt(b);
        valueStoreBase.endDocument();
      } 
    }
    
    public String toString() {
      String str = super.toString();
      int i = str.lastIndexOf('$');
      if (i != -1)
        return str.substring(i + 1); 
      int j = str.lastIndexOf('.');
      return (j != -1) ? str.substring(j + 1) : str;
    }
  }
  
  protected static class XPathMatcherStack {
    protected XPathMatcher[] fMatchers = new XPathMatcher[4];
    
    protected int fMatchersCount;
    
    protected IntStack fContextStack = new IntStack();
    
    public void clear() {
      for (byte b = 0; b < this.fMatchersCount; b++)
        this.fMatchers[b] = null; 
      this.fMatchersCount = 0;
      this.fContextStack.clear();
    }
    
    public int size() { return this.fContextStack.size(); }
    
    public int getMatcherCount() { return this.fMatchersCount; }
    
    public void addMatcher(XPathMatcher param1XPathMatcher) {
      ensureMatcherCapacity();
      this.fMatchers[this.fMatchersCount++] = param1XPathMatcher;
    }
    
    public XPathMatcher getMatcherAt(int param1Int) { return this.fMatchers[param1Int]; }
    
    public void pushContext() { this.fContextStack.push(this.fMatchersCount); }
    
    public void popContext() { this.fMatchersCount = this.fContextStack.pop(); }
    
    private void ensureMatcherCapacity() {
      if (this.fMatchersCount == this.fMatchers.length) {
        XPathMatcher[] arrayOfXPathMatcher = new XPathMatcher[this.fMatchers.length * 2];
        System.arraycopy(this.fMatchers, 0, arrayOfXPathMatcher, 0, this.fMatchers.length);
        this.fMatchers = arrayOfXPathMatcher;
      } 
    }
  }
  
  protected final class XSIErrorReporter {
    XMLErrorReporter fErrorReporter;
    
    Vector fErrors = new Vector();
    
    int[] fContext = new int[8];
    
    int fContextCount;
    
    public void reset(XMLErrorReporter param1XMLErrorReporter) {
      this.fErrorReporter = param1XMLErrorReporter;
      this.fErrors.removeAllElements();
      this.fContextCount = 0;
    }
    
    public void pushContext() {
      if (!XMLSchemaValidator.this.fAugPSVI)
        return; 
      if (this.fContextCount == this.fContext.length) {
        int i = this.fContextCount + 8;
        int[] arrayOfInt = new int[i];
        System.arraycopy(this.fContext, 0, arrayOfInt, 0, this.fContextCount);
        this.fContext = arrayOfInt;
      } 
      this.fContext[this.fContextCount++] = this.fErrors.size();
    }
    
    public String[] popContext() {
      if (!XMLSchemaValidator.this.fAugPSVI)
        return null; 
      int i = this.fContext[--this.fContextCount];
      int j = this.fErrors.size() - i;
      if (j == 0)
        return null; 
      String[] arrayOfString = new String[j];
      for (int k = 0; k < j; k++)
        arrayOfString[k] = (String)this.fErrors.elementAt(i + k); 
      this.fErrors.setSize(i);
      return arrayOfString;
    }
    
    public String[] mergeContext() {
      if (!XMLSchemaValidator.this.fAugPSVI)
        return null; 
      int i = this.fContext[--this.fContextCount];
      int j = this.fErrors.size() - i;
      if (j == 0)
        return null; 
      String[] arrayOfString = new String[j];
      for (int k = 0; k < j; k++)
        arrayOfString[k] = (String)this.fErrors.elementAt(i + k); 
      return arrayOfString;
    }
    
    public void reportError(String param1String1, String param1String2, Object[] param1ArrayOfObject, short param1Short) throws XNIException {
      this.fErrorReporter.reportError(param1String1, param1String2, param1ArrayOfObject, param1Short);
      if (XMLSchemaValidator.this.fAugPSVI)
        this.fErrors.addElement(param1String2); 
    }
    
    public void reportError(XMLLocator param1XMLLocator, String param1String1, String param1String2, Object[] param1ArrayOfObject, short param1Short) throws XNIException {
      this.fErrorReporter.reportError(param1XMLLocator, param1String1, param1String2, param1ArrayOfObject, param1Short);
      if (XMLSchemaValidator.this.fAugPSVI)
        this.fErrors.addElement(param1String2); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XMLSchemaValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */