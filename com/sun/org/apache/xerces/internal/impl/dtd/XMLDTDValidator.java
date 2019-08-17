package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
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
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import java.io.IOException;

public class XMLDTDValidator implements XMLComponent, XMLDocumentFilter, XMLDTDValidatorFilter, RevalidationHandler {
  private static final int TOP_LEVEL_SCOPE = -1;
  
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
  
  protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
  
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/balance-syntax-trees" };
  
  private static final Boolean[] FEATURE_DEFAULTS = { null, null, Boolean.FALSE, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager" };
  
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null, null };
  
  private static final boolean DEBUG_ATTRIBUTES = false;
  
  private static final boolean DEBUG_ELEMENT_CHILDREN = false;
  
  protected ValidationManager fValidationManager = null;
  
  protected final ValidationState fValidationState = new ValidationState();
  
  protected boolean fNamespaces;
  
  protected boolean fValidation;
  
  protected boolean fDTDValidation;
  
  protected boolean fDynamicValidation;
  
  protected boolean fBalanceSyntaxTrees;
  
  protected boolean fWarnDuplicateAttdef;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected DTDGrammarBucket fGrammarBucket;
  
  protected XMLLocator fDocLocation;
  
  protected NamespaceContext fNamespaceContext = null;
  
  protected DTDDVFactory fDatatypeValidatorFactory;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDocumentSource fDocumentSource;
  
  protected DTDGrammar fDTDGrammar;
  
  protected boolean fSeenDoctypeDecl = false;
  
  private boolean fPerformValidation;
  
  private String fSchemaType;
  
  private final QName fCurrentElement = new QName();
  
  private int fCurrentElementIndex = -1;
  
  private int fCurrentContentSpecType = -1;
  
  private final QName fRootElement = new QName();
  
  private boolean fInCDATASection = false;
  
  private int[] fElementIndexStack = new int[8];
  
  private int[] fContentSpecTypeStack = new int[8];
  
  private QName[] fElementQNamePartsStack = new QName[8];
  
  private QName[] fElementChildren = new QName[32];
  
  private int fElementChildrenLength = 0;
  
  private int[] fElementChildrenOffsetStack = new int[32];
  
  private int fElementDepth = -1;
  
  private boolean fSeenRootElement = false;
  
  private boolean fInElementContent = false;
  
  private XMLElementDecl fTempElementDecl = new XMLElementDecl();
  
  private final XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();
  
  private final XMLEntityDecl fEntityDecl = new XMLEntityDecl();
  
  private final QName fTempQName = new QName();
  
  private final StringBuffer fBuffer = new StringBuffer();
  
  protected DatatypeValidator fValID;
  
  protected DatatypeValidator fValIDRef;
  
  protected DatatypeValidator fValIDRefs;
  
  protected DatatypeValidator fValENTITY;
  
  protected DatatypeValidator fValENTITIES;
  
  protected DatatypeValidator fValNMTOKEN;
  
  protected DatatypeValidator fValNMTOKENS;
  
  protected DatatypeValidator fValNOTATION;
  
  public XMLDTDValidator() {
    for (byte b = 0; b < this.fElementQNamePartsStack.length; b++)
      this.fElementQNamePartsStack[b] = new QName(); 
    this.fGrammarBucket = new DTDGrammarBucket();
  }
  
  DTDGrammarBucket getGrammarBucket() { return this.fGrammarBucket; }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fDTDGrammar = null;
    this.fSeenDoctypeDecl = false;
    this.fInCDATASection = false;
    this.fSeenRootElement = false;
    this.fInElementContent = false;
    this.fCurrentElementIndex = -1;
    this.fCurrentContentSpecType = -1;
    this.fRootElement.clear();
    this.fValidationState.resetIDTables();
    this.fGrammarBucket.clear();
    this.fElementDepth = -1;
    this.fElementChildrenLength = 0;
    boolean bool = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!bool) {
      this.fValidationManager.addValidationState(this.fValidationState);
      return;
    } 
    this.fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fValidation = paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation", false);
    this.fDTDValidation = !paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
    this.fDynamicValidation = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/dynamic", false);
    this.fBalanceSyntaxTrees = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/balance-syntax-trees", false);
    this.fWarnDuplicateAttdef = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", false);
    this.fSchemaType = (String)paramXMLComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
    this.fValidationManager = (ValidationManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
    this.fValidationManager.addValidationState(this.fValidationState);
    this.fValidationState.setUsingNamespaces(this.fNamespaces);
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fGrammarPool = (XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
    this.fDatatypeValidatorFactory = (DTDDVFactory)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/datatype-validator-factory");
    init();
  }
  
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
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) { this.fDocumentHandler = paramXMLDocumentHandler; }
  
  public XMLDocumentHandler getDocumentHandler() { return this.fDocumentHandler; }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.fDocumentSource = paramXMLDocumentSource; }
  
  public XMLDocumentSource getDocumentSource() { return this.fDocumentSource; }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    if (this.fGrammarPool != null) {
      Grammar[] arrayOfGrammar = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/TR/REC-xml");
      int i = (arrayOfGrammar != null) ? arrayOfGrammar.length : 0;
      for (byte b = 0; b < i; b++)
        this.fGrammarBucket.putGrammar((DTDGrammar)arrayOfGrammar[b]); 
    } 
    this.fDocLocation = paramXMLLocator;
    this.fNamespaceContext = paramNamespaceContext;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations); 
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    this.fGrammarBucket.setStandalone((paramString3 != null && paramString3.equals("yes")));
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations); 
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    this.fSeenDoctypeDecl = true;
    this.fRootElement.setValues(null, paramString1, paramString1, null);
    String str = null;
    try {
      str = XMLEntityManager.expandSystemId(paramString3, this.fDocLocation.getExpandedSystemId(), false);
    } catch (IOException iOException) {}
    XMLDTDDescription xMLDTDDescription = new XMLDTDDescription(paramString2, paramString3, this.fDocLocation.getExpandedSystemId(), str, paramString1);
    this.fDTDGrammar = this.fGrammarBucket.getGrammar(xMLDTDDescription);
    if (this.fDTDGrammar == null && this.fGrammarPool != null && (paramString3 != null || paramString2 != null))
      this.fDTDGrammar = (DTDGrammar)this.fGrammarPool.retrieveGrammar(xMLDTDDescription); 
    if (this.fDTDGrammar == null) {
      if (!this.fBalanceSyntaxTrees) {
        this.fDTDGrammar = new DTDGrammar(this.fSymbolTable, xMLDTDDescription);
      } else {
        this.fDTDGrammar = new BalancedDTDGrammar(this.fSymbolTable, xMLDTDDescription);
      } 
    } else {
      this.fValidationManager.setCachedDTD(true);
    } 
    this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations); 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    handleStartElement(paramQName, paramXMLAttributes, paramAugmentations);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations); 
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    boolean bool = handleStartElement(paramQName, paramXMLAttributes, paramAugmentations);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations); 
    if (!bool)
      handleEndElement(paramQName, paramAugmentations, true); 
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    boolean bool1 = true;
    boolean bool2 = true;
    for (int i = paramXMLString.offset; i < paramXMLString.offset + paramXMLString.length; i++) {
      if (!isSpace(paramXMLString.ch[i])) {
        bool2 = false;
        break;
      } 
    } 
    if (this.fInElementContent && bool2 && !this.fInCDATASection && this.fDocumentHandler != null) {
      this.fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations);
      bool1 = false;
    } 
    if (this.fPerformValidation) {
      if (this.fInElementContent) {
        if (this.fGrammarBucket.getStandalone() && this.fDTDGrammar.getElementDeclIsExternal(this.fCurrentElementIndex) && bool2)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_WHITE_SPACE_IN_ELEMENT_CONTENT_WHEN_STANDALONE", null, (short)1); 
        if (!bool2)
          charDataInContent(); 
        if (paramAugmentations != null && paramAugmentations.getItem("CHAR_REF_PROBABLE_WS") == Boolean.TRUE)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, this.fDTDGrammar.getContentSpecAsString(this.fElementDepth), "character reference" }, (short)1); 
      } 
      if (this.fCurrentContentSpecType == 1)
        charDataInContent(); 
    } 
    if (bool1 && this.fDocumentHandler != null)
      this.fDocumentHandler.characters(paramXMLString, paramAugmentations); 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations); 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException { handleEndElement(paramQName, paramAugmentations, false); }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fPerformValidation && this.fInElementContent)
      charDataInContent(); 
    this.fInCDATASection = true;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startCDATA(paramAugmentations); 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    this.fInCDATASection = false;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endCDATA(paramAugmentations); 
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endDocument(paramAugmentations); 
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
      this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
      if (this.fTempElementDecl.type == 1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "comment" }, (short)1); 
    } 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.comment(paramXMLString, paramAugmentations); 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
      this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
      if (this.fTempElementDecl.type == 1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "processing instruction" }, (short)1); 
    } 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations); 
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
      this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
      if (this.fTempElementDecl.type == 1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "ENTITY" }, (short)1); 
      if (this.fGrammarBucket.getStandalone())
        XMLDTDLoader.checkStandaloneEntityRef(paramString1, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter); 
    } 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations); 
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endGeneralEntity(paramString, paramAugmentations); 
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations); 
  }
  
  public final boolean hasGrammar() { return (this.fDTDGrammar != null); }
  
  public final boolean validate() { return (this.fSchemaType != Constants.NS_XMLSCHEMA && ((!this.fDynamicValidation && this.fValidation) || (this.fDynamicValidation && this.fSeenDoctypeDecl)) && (this.fDTDValidation || this.fSeenDoctypeDecl)); }
  
  protected void addDTDDefaultAttrsAndValidate(QName paramQName, int paramInt, XMLAttributes paramXMLAttributes) throws XNIException {
    if (paramInt == -1 || this.fDTDGrammar == null)
      return; 
    int i;
    for (i = this.fDTDGrammar.getFirstAttributeDeclIndex(paramInt); i != -1; i = this.fDTDGrammar.getNextAttributeDeclIndex(i)) {
      this.fDTDGrammar.getAttributeDecl(i, this.fTempAttDecl);
      String str1 = this.fTempAttDecl.name.prefix;
      String str2 = this.fTempAttDecl.name.localpart;
      String str3 = this.fTempAttDecl.name.rawname;
      String str4 = getAttributeTypeName(this.fTempAttDecl);
      short s = this.fTempAttDecl.simpleType.defaultType;
      String str5 = null;
      if (this.fTempAttDecl.simpleType.defaultValue != null)
        str5 = this.fTempAttDecl.simpleType.defaultValue; 
      boolean bool1 = false;
      boolean bool2 = (s == 2) ? 1 : 0;
      boolean bool3 = (str4 == XMLSymbols.fCDATASymbol) ? 1 : 0;
      if (!bool3 || bool2 || str5 != null) {
        int k = paramXMLAttributes.getLength();
        for (byte b1 = 0; b1 < k; b1++) {
          if (paramXMLAttributes.getQName(b1) == str3) {
            bool1 = true;
            break;
          } 
        } 
      } 
      if (!bool1)
        if (bool2) {
          if (this.fPerformValidation) {
            Object[] arrayOfObject = { paramQName.localpart, str3 };
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REQUIRED_ATTRIBUTE_NOT_SPECIFIED", arrayOfObject, (short)1);
          } 
        } else if (str5 != null) {
          if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && this.fDTDGrammar.getAttributeDeclIsExternal(i)) {
            Object[] arrayOfObject = { paramQName.localpart, str3 };
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DEFAULTED_ATTRIBUTE_NOT_SPECIFIED", arrayOfObject, (short)1);
          } 
          if (this.fNamespaces) {
            int m = str3.indexOf(':');
            if (m != -1) {
              str1 = str3.substring(0, m);
              str1 = this.fSymbolTable.addSymbol(str1);
              str2 = str3.substring(m + 1);
              str2 = this.fSymbolTable.addSymbol(str2);
            } 
          } 
          this.fTempQName.setValues(str1, str2, str3, this.fTempAttDecl.name.uri);
          int k = paramXMLAttributes.addAttribute(this.fTempQName, str4, str5);
        }  
    } 
    int j = paramXMLAttributes.getLength();
    for (byte b = 0; b < j; b++) {
      String str = paramXMLAttributes.getQName(b);
      boolean bool = false;
      if (this.fPerformValidation && this.fGrammarBucket.getStandalone()) {
        String str1 = paramXMLAttributes.getNonNormalizedValue(b);
        if (str1 != null) {
          String str2 = getExternalEntityRefInAttrValue(str1);
          if (str2 != null)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { str2 }, (short)1); 
        } 
      } 
      int k = -1;
      int m;
      for (m = this.fDTDGrammar.getFirstAttributeDeclIndex(paramInt); m != -1; m = this.fDTDGrammar.getNextAttributeDeclIndex(m)) {
        this.fDTDGrammar.getAttributeDecl(m, this.fTempAttDecl);
        if (this.fTempAttDecl.name.rawname == str) {
          k = m;
          bool = true;
          break;
        } 
      } 
      if (!bool) {
        if (this.fPerformValidation) {
          Object[] arrayOfObject = { paramQName.rawname, str };
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_NOT_DECLARED", arrayOfObject, (short)1);
        } 
      } else {
        String str1 = getAttributeTypeName(this.fTempAttDecl);
        paramXMLAttributes.setType(b, str1);
        paramXMLAttributes.getAugmentations(b).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
        boolean bool1 = false;
        String str2 = paramXMLAttributes.getValue(b);
        String str3 = str2;
        if (paramXMLAttributes.isSpecified(b) && str1 != XMLSymbols.fCDATASymbol) {
          bool1 = normalizeAttrValue(paramXMLAttributes, b);
          str3 = paramXMLAttributes.getValue(b);
          if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && bool1 && this.fDTDGrammar.getAttributeDeclIsExternal(m))
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTVALUE_CHANGED_DURING_NORMALIZATION_WHEN_STANDALONE", new Object[] { str, str2, str3 }, (short)1); 
        } 
        if (this.fPerformValidation) {
          if (this.fTempAttDecl.simpleType.defaultType == 1) {
            String str4 = this.fTempAttDecl.simpleType.defaultValue;
            if (!str3.equals(str4)) {
              Object[] arrayOfObject = { paramQName.localpart, str, str3, str4 };
              this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_FIXED_ATTVALUE_INVALID", arrayOfObject, (short)1);
            } 
          } 
          if (this.fTempAttDecl.simpleType.type == 1 || this.fTempAttDecl.simpleType.type == 2 || this.fTempAttDecl.simpleType.type == 3 || this.fTempAttDecl.simpleType.type == 4 || this.fTempAttDecl.simpleType.type == 5 || this.fTempAttDecl.simpleType.type == 6)
            validateDTDattribute(paramQName, str3, this.fTempAttDecl); 
        } 
      } 
    } 
  }
  
  protected String getExternalEntityRefInAttrValue(String paramString) {
    int i = paramString.length();
    for (int j = paramString.indexOf('&'); j != -1; j = paramString.indexOf('&', j + 1)) {
      if (j + 1 < i && paramString.charAt(j + 1) != '#') {
        int k = paramString.indexOf(';', j + 1);
        String str = paramString.substring(j + 1, k);
        str = this.fSymbolTable.addSymbol(str);
        int m = this.fDTDGrammar.getEntityDeclIndex(str);
        if (m > -1) {
          this.fDTDGrammar.getEntityDecl(m, this.fEntityDecl);
          if (this.fEntityDecl.inExternal || (str = getExternalEntityRefInAttrValue(this.fEntityDecl.value)) != null)
            return str; 
        } 
      } 
    } 
    return null;
  }
  
  protected void validateDTDattribute(QName paramQName, String paramString, XMLAttributeDecl paramXMLAttributeDecl) throws XNIException {
    switch (paramXMLAttributeDecl.simpleType.type) {
      case 1:
        bool = paramXMLAttributeDecl.simpleType.list;
        try {
          if (bool) {
            this.fValENTITIES.validate(paramString, this.fValidationState);
            break;
          } 
          this.fValENTITY.validate(paramString, this.fValidationState);
        } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)1);
        } 
        break;
      case 2:
      case 6:
        bool = false;
        arrayOfString = paramXMLAttributeDecl.simpleType.enumeration;
        if (arrayOfString == null) {
          bool = false;
        } else {
          for (byte b = 0; b < arrayOfString.length; b++) {
            if (paramString == arrayOfString[b] || paramString.equals(arrayOfString[b])) {
              bool = true;
              break;
            } 
          } 
        } 
        if (!bool) {
          StringBuffer stringBuffer = new StringBuffer();
          if (arrayOfString != null)
            for (byte b = 0; b < arrayOfString.length; b++)
              stringBuffer.append(arrayOfString[b] + " ");  
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_VALUE_NOT_IN_LIST", new Object[] { paramXMLAttributeDecl.name.rawname, paramString, stringBuffer }, (short)1);
        } 
        break;
      case 3:
        try {
          this.fValID.validate(paramString, this.fValidationState);
        } catch (InvalidDatatypeValueException bool) {
          InvalidDatatypeValueException invalidDatatypeValueException;
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)1);
        } 
        break;
      case 4:
        bool = paramXMLAttributeDecl.simpleType.list;
        try {
          if (bool) {
            this.fValIDRefs.validate(paramString, this.fValidationState);
            break;
          } 
          this.fValIDRef.validate(paramString, this.fValidationState);
        } catch (InvalidDatatypeValueException arrayOfString) {}
        break;
      case 5:
        bool = paramXMLAttributeDecl.simpleType.list;
        try {
          if (bool) {
            this.fValNMTOKENS.validate(paramString, this.fValidationState);
            break;
          } 
          this.fValNMTOKEN.validate(paramString, this.fValidationState);
        } catch (InvalidDatatypeValueException arrayOfString) {}
        break;
    } 
  }
  
  protected boolean invalidStandaloneAttDef(QName paramQName1, QName paramQName2) { return true; }
  
  private boolean normalizeAttrValue(XMLAttributes paramXMLAttributes, int paramInt) {
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = false;
    byte b1 = 0;
    byte b2 = 0;
    String str1 = paramXMLAttributes.getValue(paramInt);
    char[] arrayOfChar = new char[str1.length()];
    this.fBuffer.setLength(0);
    str1.getChars(0, str1.length(), arrayOfChar, 0);
    for (byte b3 = 0; b3 < arrayOfChar.length; b3++) {
      if (arrayOfChar[b3] == ' ') {
        if (bool3) {
          bool2 = true;
          bool3 = false;
        } 
        if (bool2 && !bool1) {
          bool2 = false;
          this.fBuffer.append(arrayOfChar[b3]);
          b1++;
        } else if (bool1 || !bool2) {
          b2++;
        } 
      } else {
        bool3 = true;
        bool2 = false;
        bool1 = false;
        this.fBuffer.append(arrayOfChar[b3]);
        b1++;
      } 
    } 
    if (b1 > 0 && this.fBuffer.charAt(b1 - 1) == ' ')
      this.fBuffer.setLength(b1 - 1); 
    String str2 = this.fBuffer.toString();
    paramXMLAttributes.setValue(paramInt, str2);
    return !str1.equals(str2);
  }
  
  private final void rootElementSpecified(QName paramQName) throws XNIException {
    if (this.fPerformValidation) {
      String str1 = this.fRootElement.rawname;
      String str2 = paramQName.rawname;
      if (str1 == null || !str1.equals(str2))
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { str1, str2 }, (short)1); 
    } 
  }
  
  private int checkContent(int paramInt1, QName[] paramArrayOfQName, int paramInt2, int paramInt3) throws XNIException {
    this.fDTDGrammar.getElementDecl(paramInt1, this.fTempElementDecl);
    String str = this.fCurrentElement.rawname;
    int i = this.fCurrentContentSpecType;
    if (i == 1) {
      if (paramInt3 != 0)
        return 0; 
    } else if (i != 0) {
      if (i == 2 || i == 3) {
        ContentModelValidator contentModelValidator = null;
        contentModelValidator = this.fTempElementDecl.contentModelValidator;
        return contentModelValidator.validate(paramArrayOfQName, paramInt2, paramInt3);
      } 
      if (i != -1 && i == 4);
    } 
    return -1;
  }
  
  private int getContentSpecType(int paramInt) {
    short s = -1;
    if (paramInt > -1 && this.fDTDGrammar.getElementDecl(paramInt, this.fTempElementDecl))
      s = this.fTempElementDecl.type; 
    return s;
  }
  
  private void charDataInContent() {
    if (this.fElementChildren.length <= this.fElementChildrenLength) {
      QName[] arrayOfQName = new QName[this.fElementChildren.length * 2];
      System.arraycopy(this.fElementChildren, 0, arrayOfQName, 0, this.fElementChildren.length);
      this.fElementChildren = arrayOfQName;
    } 
    QName qName = this.fElementChildren[this.fElementChildrenLength];
    if (qName == null) {
      for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; i++)
        this.fElementChildren[i] = new QName(); 
      qName = this.fElementChildren[this.fElementChildrenLength];
    } 
    qName.clear();
    this.fElementChildrenLength++;
  }
  
  private String getAttributeTypeName(XMLAttributeDecl paramXMLAttributeDecl) {
    byte b;
    StringBuffer stringBuffer;
    switch (paramXMLAttributeDecl.simpleType.type) {
      case 1:
        return paramXMLAttributeDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
      case 2:
        stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (b = 0; b < paramXMLAttributeDecl.simpleType.enumeration.length; b++) {
          if (b)
            stringBuffer.append('|'); 
          stringBuffer.append(paramXMLAttributeDecl.simpleType.enumeration[b]);
        } 
        stringBuffer.append(')');
        return this.fSymbolTable.addSymbol(stringBuffer.toString());
      case 3:
        return XMLSymbols.fIDSymbol;
      case 4:
        return paramXMLAttributeDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
      case 5:
        return paramXMLAttributeDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
      case 6:
        return XMLSymbols.fNOTATIONSymbol;
    } 
    return XMLSymbols.fCDATASymbol;
  }
  
  protected void init() {
    if (this.fValidation || this.fDynamicValidation)
      try {
        this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDSymbol);
        this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSymbol);
        this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSSymbol);
        this.fValENTITY = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITYSymbol);
        this.fValENTITIES = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITIESSymbol);
        this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSymbol);
        this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSSymbol);
        this.fValNOTATION = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNOTATIONSymbol);
      } catch (Exception exception) {
        exception.printStackTrace(System.err);
      }  
  }
  
  private void ensureStackCapacity(int paramInt) {
    if (paramInt == this.fElementQNamePartsStack.length) {
      QName[] arrayOfQName = new QName[paramInt * 2];
      System.arraycopy(this.fElementQNamePartsStack, 0, arrayOfQName, 0, paramInt);
      this.fElementQNamePartsStack = arrayOfQName;
      QName qName = this.fElementQNamePartsStack[paramInt];
      if (qName == null)
        for (int i = paramInt; i < this.fElementQNamePartsStack.length; i++)
          this.fElementQNamePartsStack[i] = new QName();  
      int[] arrayOfInt = new int[paramInt * 2];
      System.arraycopy(this.fElementIndexStack, 0, arrayOfInt, 0, paramInt);
      this.fElementIndexStack = arrayOfInt;
      arrayOfInt = new int[paramInt * 2];
      System.arraycopy(this.fContentSpecTypeStack, 0, arrayOfInt, 0, paramInt);
      this.fContentSpecTypeStack = arrayOfInt;
    } 
  }
  
  protected boolean handleStartElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (!this.fSeenRootElement) {
      this.fPerformValidation = validate();
      this.fSeenRootElement = true;
      this.fValidationManager.setEntityState(this.fDTDGrammar);
      this.fValidationManager.setGrammarFound(this.fSeenDoctypeDecl);
      rootElementSpecified(paramQName);
    } 
    if (this.fDTDGrammar == null) {
      if (!this.fPerformValidation) {
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fInElementContent = false;
      } 
      if (this.fPerformValidation)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { paramQName.rawname }, (short)1); 
      if (this.fDocumentSource != null) {
        this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
        if (this.fDocumentHandler != null)
          this.fDocumentHandler.setDocumentSource(this.fDocumentSource); 
        return true;
      } 
    } else {
      this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(paramQName);
      this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
      if (this.fCurrentContentSpecType == -1 && this.fPerformValidation)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_NOT_DECLARED", new Object[] { paramQName.rawname }, (short)1); 
      addDTDDefaultAttrsAndValidate(paramQName, this.fCurrentElementIndex, paramXMLAttributes);
    } 
    this.fInElementContent = (this.fCurrentContentSpecType == 3);
    this.fElementDepth++;
    if (this.fPerformValidation) {
      if (this.fElementChildrenOffsetStack.length <= this.fElementDepth) {
        int[] arrayOfInt = new int[this.fElementChildrenOffsetStack.length * 2];
        System.arraycopy(this.fElementChildrenOffsetStack, 0, arrayOfInt, 0, this.fElementChildrenOffsetStack.length);
        this.fElementChildrenOffsetStack = arrayOfInt;
      } 
      this.fElementChildrenOffsetStack[this.fElementDepth] = this.fElementChildrenLength;
      if (this.fElementChildren.length <= this.fElementChildrenLength) {
        QName[] arrayOfQName = new QName[this.fElementChildrenLength * 2];
        System.arraycopy(this.fElementChildren, 0, arrayOfQName, 0, this.fElementChildren.length);
        this.fElementChildren = arrayOfQName;
      } 
      QName qName = this.fElementChildren[this.fElementChildrenLength];
      if (qName == null) {
        for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; i++)
          this.fElementChildren[i] = new QName(); 
        qName = this.fElementChildren[this.fElementChildrenLength];
      } 
      qName.setValues(paramQName);
      this.fElementChildrenLength++;
    } 
    this.fCurrentElement.setValues(paramQName);
    ensureStackCapacity(this.fElementDepth);
    this.fElementQNamePartsStack[this.fElementDepth].setValues(this.fCurrentElement);
    this.fElementIndexStack[this.fElementDepth] = this.fCurrentElementIndex;
    this.fContentSpecTypeStack[this.fElementDepth] = this.fCurrentContentSpecType;
    startNamespaceScope(paramQName, paramXMLAttributes, paramAugmentations);
    return false;
  }
  
  protected void startNamespaceScope(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {}
  
  protected void handleEndElement(QName paramQName, Augmentations paramAugmentations, boolean paramBoolean) throws XNIException {
    this.fElementDepth--;
    if (this.fPerformValidation) {
      int i = this.fCurrentElementIndex;
      if (i != -1 && this.fCurrentContentSpecType != -1) {
        QName[] arrayOfQName = this.fElementChildren;
        int j = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
        int k = this.fElementChildrenLength - j;
        int m = checkContent(i, arrayOfQName, j, k);
        if (m != -1) {
          this.fDTDGrammar.getElementDecl(i, this.fTempElementDecl);
          if (this.fTempElementDecl.type == 1) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID", new Object[] { paramQName.rawname, "EMPTY" }, (short)1);
          } else {
            String str = (m != k) ? "MSG_CONTENT_INVALID" : "MSG_CONTENT_INCOMPLETE";
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", str, new Object[] { paramQName.rawname, this.fDTDGrammar.getContentSpecAsString(i) }, (short)1);
          } 
        } 
      } 
      this.fElementChildrenLength = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
    } 
    endNamespaceScope(this.fCurrentElement, paramAugmentations, paramBoolean);
    if (this.fElementDepth < -1)
      throw new RuntimeException("FWK008 Element stack underflow"); 
    if (this.fElementDepth < 0) {
      this.fCurrentElement.clear();
      this.fCurrentElementIndex = -1;
      this.fCurrentContentSpecType = -1;
      this.fInElementContent = false;
      if (this.fPerformValidation) {
        String str = this.fValidationState.checkIDRefID();
        if (str != null)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_WITH_ID_REQUIRED", new Object[] { str }, (short)1); 
      } 
      return;
    } 
    this.fCurrentElement.setValues(this.fElementQNamePartsStack[this.fElementDepth]);
    this.fCurrentElementIndex = this.fElementIndexStack[this.fElementDepth];
    this.fCurrentContentSpecType = this.fContentSpecTypeStack[this.fElementDepth];
    this.fInElementContent = (this.fCurrentContentSpecType == 3);
  }
  
  protected void endNamespaceScope(QName paramQName, Augmentations paramAugmentations, boolean paramBoolean) throws XNIException {
    if (this.fDocumentHandler != null && !paramBoolean)
      this.fDocumentHandler.endElement(this.fCurrentElement, paramAugmentations); 
  }
  
  protected boolean isSpace(int paramInt) { return XMLChar.isSpace(paramInt); }
  
  public boolean characterData(String paramString, Augmentations paramAugmentations) {
    characters(new XMLString(paramString.toCharArray(), 0, paramString.length()), paramAugmentations);
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLDTDValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */