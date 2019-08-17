package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.traversers.XSDHandler;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;

public class XMLSchemaLoader implements XMLGrammarLoader, XMLComponent, XSLoader, DOMConfiguration {
  protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
  
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  
  protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
  
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  
  protected static final String AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  
  protected static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
  
  private static final String[] RECOGNIZED_FEATURES = { 
      "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", 
      "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser" };
  
  public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  
  private static final String[] RECOGNIZED_PROPERTIES = { 
      "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://apache.org/xml/properties/security-manager", 
      "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  
  private ParserConfigurationSettings fLoaderConfig = new ParserConfigurationSettings();
  
  private SymbolTable fSymbolTable = null;
  
  private XMLErrorReporter fErrorReporter = new XMLErrorReporter();
  
  private XMLEntityManager fEntityManager = null;
  
  private XMLEntityResolver fUserEntityResolver = null;
  
  private XMLGrammarPool fGrammarPool = null;
  
  private String fExternalSchemas = null;
  
  private String fExternalNoNSSchema = null;
  
  private Object fJAXPSource = null;
  
  private boolean fIsCheckedFully = false;
  
  private boolean fJAXPProcessed = false;
  
  private boolean fSettingsChanged = true;
  
  private XSDHandler fSchemaHandler;
  
  private XSGrammarBucket fGrammarBucket;
  
  private XSDeclarationPool fDeclPool = null;
  
  private SubstitutionGroupHandler fSubGroupHandler;
  
  private final CMNodeFactory fNodeFactory = new CMNodeFactory();
  
  private CMBuilder fCMBuilder;
  
  private XSDDescription fXSDDescription = new XSDDescription();
  
  private String faccessExternalSchema = "all";
  
  private Map fJAXPCache;
  
  private Locale fLocale = Locale.getDefault();
  
  private DOMStringList fRecognizedParameters = null;
  
  private DOMErrorHandlerWrapper fErrorHandler = null;
  
  private DOMEntityResolverWrapper fResourceResolver = null;
  
  public XMLSchemaLoader() { this(new SymbolTable(), null, new XMLEntityManager(), null, null, null); }
  
  public XMLSchemaLoader(SymbolTable paramSymbolTable) { this(paramSymbolTable, null, new XMLEntityManager(), null, null, null); }
  
  XMLSchemaLoader(XMLErrorReporter paramXMLErrorReporter, XSGrammarBucket paramXSGrammarBucket, SubstitutionGroupHandler paramSubstitutionGroupHandler, CMBuilder paramCMBuilder) { this(null, paramXMLErrorReporter, null, paramXSGrammarBucket, paramSubstitutionGroupHandler, paramCMBuilder); }
  
  XMLSchemaLoader(SymbolTable paramSymbolTable, XMLErrorReporter paramXMLErrorReporter, XMLEntityManager paramXMLEntityManager, XSGrammarBucket paramXSGrammarBucket, SubstitutionGroupHandler paramSubstitutionGroupHandler, CMBuilder paramCMBuilder) {
    this.fLoaderConfig.addRecognizedFeatures(RECOGNIZED_FEATURES);
    this.fLoaderConfig.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    if (paramSymbolTable != null)
      this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable); 
    if (paramXMLErrorReporter == null) {
      paramXMLErrorReporter = new XMLErrorReporter();
      paramXMLErrorReporter.setLocale(this.fLocale);
      paramXMLErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
    } 
    this.fErrorReporter = paramXMLErrorReporter;
    if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter()); 
    this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    this.fEntityManager = paramXMLEntityManager;
    if (this.fEntityManager != null)
      this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager); 
    this.fLoaderConfig.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
    if (paramXSGrammarBucket == null)
      paramXSGrammarBucket = new XSGrammarBucket(); 
    this.fGrammarBucket = paramXSGrammarBucket;
    if (paramSubstitutionGroupHandler == null)
      paramSubstitutionGroupHandler = new SubstitutionGroupHandler(this.fGrammarBucket); 
    this.fSubGroupHandler = paramSubstitutionGroupHandler;
    if (paramCMBuilder == null)
      paramCMBuilder = new CMBuilder(this.fNodeFactory); 
    this.fCMBuilder = paramCMBuilder;
    this.fSchemaHandler = new XSDHandler(this.fGrammarBucket);
    if (this.fDeclPool != null)
      this.fDeclPool.reset(); 
    this.fJAXPCache = new HashMap();
    this.fSettingsChanged = true;
  }
  
  public String[] getRecognizedFeatures() { return (String[])RECOGNIZED_FEATURES.clone(); }
  
  public boolean getFeature(String paramString) throws XMLConfigurationException { return this.fLoaderConfig.getFeature(paramString); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    this.fSettingsChanged = true;
    if (paramString.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
      this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", paramBoolean);
    } else if (paramString.equals("http://apache.org/xml/features/generate-synthetic-annotations")) {
      this.fSchemaHandler.setGenerateSyntheticAnnotations(paramBoolean);
    } 
    this.fLoaderConfig.setFeature(paramString, paramBoolean);
  }
  
  public String[] getRecognizedProperties() { return (String[])RECOGNIZED_PROPERTIES.clone(); }
  
  public Object getProperty(String paramString) throws XMLConfigurationException { return this.fLoaderConfig.getProperty(paramString); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    this.fSettingsChanged = true;
    this.fLoaderConfig.setProperty(paramString, paramObject);
    if (paramString.equals("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
      this.fJAXPSource = paramObject;
      this.fJAXPProcessed = false;
    } else if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
      this.fGrammarPool = (XMLGrammarPool)paramObject;
    } else if (paramString.equals("http://apache.org/xml/properties/schema/external-schemaLocation")) {
      this.fExternalSchemas = (String)paramObject;
    } else if (paramString.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation")) {
      this.fExternalNoNSSchema = (String)paramObject;
    } else if (paramString.equals("http://apache.org/xml/properties/locale")) {
      setLocale((Locale)paramObject);
    } else if (paramString.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
      this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", paramObject);
    } else if (paramString.equals("http://apache.org/xml/properties/internal/error-reporter")) {
      this.fErrorReporter = (XMLErrorReporter)paramObject;
      if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
        this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter()); 
    } else if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramObject;
      this.faccessExternalSchema = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
    } 
  }
  
  public void setLocale(Locale paramLocale) {
    this.fLocale = paramLocale;
    this.fErrorReporter.setLocale(paramLocale);
  }
  
  public Locale getLocale() { return this.fLocale; }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler) { this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler); }
  
  public XMLErrorHandler getErrorHandler() { return this.fErrorReporter.getErrorHandler(); }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver) {
    this.fUserEntityResolver = paramXMLEntityResolver;
    this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
    this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
  }
  
  public XMLEntityResolver getEntityResolver() { return this.fUserEntityResolver; }
  
  public void loadGrammar(XMLInputSource[] paramArrayOfXMLInputSource) throws IOException, XNIException {
    int i = paramArrayOfXMLInputSource.length;
    for (byte b = 0; b < i; b++)
      loadGrammar(paramArrayOfXMLInputSource[b]); 
  }
  
  public Grammar loadGrammar(XMLInputSource paramXMLInputSource) throws IOException, XNIException {
    reset(this.fLoaderConfig);
    this.fSettingsChanged = false;
    XSDDescription xSDDescription = new XSDDescription();
    xSDDescription.fContextType = 3;
    xSDDescription.setBaseSystemId(paramXMLInputSource.getBaseSystemId());
    xSDDescription.setLiteralSystemId(paramXMLInputSource.getSystemId());
    HashMap hashMap = new HashMap();
    processExternalHints(this.fExternalSchemas, this.fExternalNoNSSchema, hashMap, this.fErrorReporter);
    SchemaGrammar schemaGrammar = loadSchema(xSDDescription, paramXMLInputSource, hashMap);
    if (schemaGrammar != null && this.fGrammarPool != null) {
      this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", this.fGrammarBucket.getGrammars());
      if (this.fIsCheckedFully && this.fJAXPCache.get(schemaGrammar) != schemaGrammar)
        XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter); 
    } 
    return schemaGrammar;
  }
  
  SchemaGrammar loadSchema(XSDDescription paramXSDDescription, XMLInputSource paramXMLInputSource, Map<String, LocationArray> paramMap) throws IOException, XNIException {
    if (!this.fJAXPProcessed)
      processJAXPSchemaSource(paramMap); 
    if (paramXSDDescription.isExternal()) {
      String str = SecuritySupport.checkAccess(paramXSDDescription.getExpandedSystemId(), this.faccessExternalSchema, "all");
      if (str != null)
        throw new XNIException(this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.access", new Object[] { SecuritySupport.sanitizePath(paramXSDDescription.getExpandedSystemId()), str }, (short)1)); 
    } 
    return this.fSchemaHandler.parseSchema(paramXMLInputSource, paramXSDDescription, paramMap);
  }
  
  public static XMLInputSource resolveDocument(XSDDescription paramXSDDescription, Map<String, LocationArray> paramMap, XMLEntityResolver paramXMLEntityResolver) throws IOException {
    String str1 = null;
    if (paramXSDDescription.getContextType() == 2 || paramXSDDescription.fromInstance()) {
      String str3 = paramXSDDescription.getTargetNamespace();
      String str4 = (str3 == null) ? XMLSymbols.EMPTY_STRING : str3;
      LocationArray locationArray = (LocationArray)paramMap.get(str4);
      if (locationArray != null)
        str1 = locationArray.getFirstLocation(); 
    } 
    if (str1 == null) {
      String[] arrayOfString = paramXSDDescription.getLocationHints();
      if (arrayOfString != null && arrayOfString.length > 0)
        str1 = arrayOfString[0]; 
    } 
    String str2 = XMLEntityManager.expandSystemId(str1, paramXSDDescription.getBaseSystemId(), false);
    paramXSDDescription.setLiteralSystemId(str1);
    paramXSDDescription.setExpandedSystemId(str2);
    return paramXMLEntityResolver.resolveEntity(paramXSDDescription);
  }
  
  public static void processExternalHints(String paramString1, String paramString2, Map<String, LocationArray> paramMap, XMLErrorReporter paramXMLErrorReporter) {
    if (paramString1 != null)
      try {
        XSAttributeDecl xSAttributeDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
        xSAttributeDecl.fType.validate(paramString1, null, null);
        if (!tokenizeSchemaLocationStr(paramString1, paramMap))
          paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { paramString1 }, (short)0); 
      } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
        paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)0);
      }  
    if (paramString2 != null)
      try {
        XSAttributeDecl xSAttributeDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
        xSAttributeDecl.fType.validate(paramString2, null, null);
        LocationArray locationArray = (LocationArray)paramMap.get(XMLSymbols.EMPTY_STRING);
        if (locationArray == null) {
          locationArray = new LocationArray();
          paramMap.put(XMLSymbols.EMPTY_STRING, locationArray);
        } 
        locationArray.addLocation(paramString2);
      } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
        paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)0);
      }  
  }
  
  public static boolean tokenizeSchemaLocationStr(String paramString, Map<String, LocationArray> paramMap) {
    if (paramString != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString, " \n\t\r");
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        if (!stringTokenizer.hasMoreTokens())
          return false; 
        String str2 = stringTokenizer.nextToken();
        LocationArray locationArray = (LocationArray)paramMap.get(str1);
        if (locationArray == null) {
          locationArray = new LocationArray();
          paramMap.put(str1, locationArray);
        } 
        locationArray.addLocation(str2);
      } 
    } 
    return true;
  }
  
  private void processJAXPSchemaSource(Map<String, LocationArray> paramMap) throws IOException {
    this.fJAXPProcessed = true;
    if (this.fJAXPSource == null)
      return; 
    Class clazz = this.fJAXPSource.getClass().getComponentType();
    XMLInputSource xMLInputSource = null;
    String str = null;
    if (clazz == null) {
      if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
        SchemaGrammar schemaGrammar1 = (SchemaGrammar)this.fJAXPCache.get(this.fJAXPSource);
        if (schemaGrammar1 != null) {
          this.fGrammarBucket.putGrammar(schemaGrammar1);
          return;
        } 
      } 
      this.fXSDDescription.reset();
      xMLInputSource = xsdToXMLInputSource(this.fJAXPSource);
      str = xMLInputSource.getSystemId();
      this.fXSDDescription.fContextType = 3;
      if (str != null) {
        this.fXSDDescription.setBaseSystemId(xMLInputSource.getBaseSystemId());
        this.fXSDDescription.setLiteralSystemId(str);
        this.fXSDDescription.setExpandedSystemId(str);
        this.fXSDDescription.fLocationHints = new String[] { str };
      } 
      SchemaGrammar schemaGrammar = loadSchema(this.fXSDDescription, xMLInputSource, paramMap);
      if (schemaGrammar != null) {
        if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
          this.fJAXPCache.put(this.fJAXPSource, schemaGrammar);
          if (this.fIsCheckedFully)
            XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter); 
        } 
        this.fGrammarBucket.putGrammar(schemaGrammar);
      } 
      return;
    } 
    if (clazz != Object.class && clazz != String.class && clazz != File.class && clazz != InputStream.class && clazz != InputSource.class)
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have an array of type {" + clazz.getName() + "}. Possible types of the array supported are Object, String, File, InputStream, InputSource."); 
    Object[] arrayOfObject = (Object[])this.fJAXPSource;
    Vector vector = new Vector();
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (arrayOfObject[b] instanceof InputStream || arrayOfObject[b] instanceof InputSource) {
        SchemaGrammar schemaGrammar1 = (SchemaGrammar)this.fJAXPCache.get(arrayOfObject[b]);
        if (schemaGrammar1 != null) {
          this.fGrammarBucket.putGrammar(schemaGrammar1);
          continue;
        } 
      } 
      this.fXSDDescription.reset();
      xMLInputSource = xsdToXMLInputSource(arrayOfObject[b]);
      str = xMLInputSource.getSystemId();
      this.fXSDDescription.fContextType = 3;
      if (str != null) {
        this.fXSDDescription.setBaseSystemId(xMLInputSource.getBaseSystemId());
        this.fXSDDescription.setLiteralSystemId(str);
        this.fXSDDescription.setExpandedSystemId(str);
        this.fXSDDescription.fLocationHints = new String[] { str };
      } 
      String str1 = null;
      SchemaGrammar schemaGrammar = this.fSchemaHandler.parseSchema(xMLInputSource, this.fXSDDescription, paramMap);
      if (this.fIsCheckedFully)
        XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter); 
      if (schemaGrammar != null) {
        str1 = schemaGrammar.getTargetNamespace();
        if (vector.contains(str1))
          throw new IllegalArgumentException(" When using array of Objects as the value of SCHEMA_SOURCE property , no two Schemas should share the same targetNamespace. "); 
        vector.add(str1);
        if (arrayOfObject[b] instanceof InputStream || arrayOfObject[b] instanceof InputSource)
          this.fJAXPCache.put(arrayOfObject[b], schemaGrammar); 
        this.fGrammarBucket.putGrammar(schemaGrammar);
      } 
      continue;
    } 
  }
  
  private XMLInputSource xsdToXMLInputSource(Object paramObject) {
    if (paramObject instanceof String) {
      String str = (String)paramObject;
      this.fXSDDescription.reset();
      this.fXSDDescription.setValues(null, str, null, null);
      XMLInputSource xMLInputSource = null;
      try {
        xMLInputSource = this.fEntityManager.resolveEntity(this.fXSDDescription);
      } catch (IOException iOException) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { str }, (short)1);
      } 
      return (xMLInputSource == null) ? new XMLInputSource(null, str, null) : xMLInputSource;
    } 
    if (paramObject instanceof InputSource)
      return saxToXMLInputSource((InputSource)paramObject); 
    if (paramObject instanceof InputStream)
      return new XMLInputSource(null, null, null, (InputStream)paramObject, null); 
    if (paramObject instanceof File) {
      File file = (File)paramObject;
      BufferedInputStream bufferedInputStream = null;
      try {
        bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
      } catch (FileNotFoundException fileNotFoundException) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { file.toString() }, (short)1);
      } 
      return new XMLInputSource(null, null, null, bufferedInputStream, null);
    } 
    throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have a value of type {" + paramObject.getClass().getName() + "}. Possible types of the value supported are String, File, InputStream, InputSource OR an array of these types.");
  }
  
  private static XMLInputSource saxToXMLInputSource(InputSource paramInputSource) {
    String str1 = paramInputSource.getPublicId();
    String str2 = paramInputSource.getSystemId();
    Reader reader = paramInputSource.getCharacterStream();
    if (reader != null)
      return new XMLInputSource(str1, str2, null, reader, null); 
    InputStream inputStream = paramInputSource.getByteStream();
    return (inputStream != null) ? new XMLInputSource(str1, str2, null, inputStream, paramInputSource.getEncoding()) : new XMLInputSource(str1, str2, null);
  }
  
  public Boolean getFeatureDefault(String paramString) { return paramString.equals("http://apache.org/xml/features/validation/schema/augment-psvi") ? Boolean.TRUE : null; }
  
  public Object getPropertyDefault(String paramString) throws XMLConfigurationException { return null; }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    if (xMLSecurityPropertyManager == null) {
      xMLSecurityPropertyManager = new XMLSecurityPropertyManager();
      setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", xMLSecurityPropertyManager);
    } 
    XMLSecurityManager xMLSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
    if (xMLSecurityManager == null)
      setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true)); 
    this.faccessExternalSchema = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
    this.fGrammarBucket.reset();
    this.fSubGroupHandler.reset();
    boolean bool1 = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!bool1 || !this.fSettingsChanged) {
      this.fJAXPProcessed = false;
      initGrammarBucket();
      return;
    } 
    this.fNodeFactory.reset(paramXMLComponentManager);
    this.fEntityManager = (XMLEntityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    SchemaDVFactory schemaDVFactory = null;
    schemaDVFactory = this.fSchemaHandler.getDVFactory();
    if (schemaDVFactory == null) {
      schemaDVFactory = SchemaDVFactory.getInstance();
      this.fSchemaHandler.setDVFactory(schemaDVFactory);
    } 
    boolean bool2 = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", false);
    if (!bool2) {
      if (this.fDeclPool != null) {
        this.fDeclPool.reset();
      } else {
        this.fDeclPool = new XSDeclarationPool();
      } 
      this.fCMBuilder.setDeclPool(this.fDeclPool);
      this.fSchemaHandler.setDeclPool(this.fDeclPool);
      if (schemaDVFactory instanceof SchemaDVFactoryImpl) {
        this.fDeclPool.setDVFactory((SchemaDVFactoryImpl)schemaDVFactory);
        ((SchemaDVFactoryImpl)schemaDVFactory).setDeclPool(this.fDeclPool);
      } 
    } else {
      this.fCMBuilder.setDeclPool(null);
      this.fSchemaHandler.setDeclPool(null);
    } 
    try {
      this.fExternalSchemas = (String)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
      this.fExternalNoNSSchema = (String)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fExternalSchemas = null;
      this.fExternalNoNSSchema = null;
    } 
    this.fJAXPSource = paramXMLComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
    this.fJAXPProcessed = false;
    this.fGrammarPool = (XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
    initGrammarBucket();
    try {
      boolean bool = paramXMLComponentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
      if (!bool)
        this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", bool); 
    } catch (XMLConfigurationException xMLConfigurationException) {}
    this.fIsCheckedFully = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
    this.fSchemaHandler.setGenerateSyntheticAnnotations(paramXMLComponentManager.getFeature("http://apache.org/xml/features/generate-synthetic-annotations", false));
    this.fSchemaHandler.reset(paramXMLComponentManager);
  }
  
  private void initGrammarBucket() {
    if (this.fGrammarPool != null) {
      Grammar[] arrayOfGrammar = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
      for (byte b = 0; b < arrayOfGrammar.length; b++) {
        if (!this.fGrammarBucket.putGrammar((SchemaGrammar)arrayOfGrammar[b], true))
          this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0); 
      } 
    } 
  }
  
  public DOMConfiguration getConfig() { return this; }
  
  public XSModel load(LSInput paramLSInput) {
    try {
      Grammar grammar = loadGrammar(dom2xmlInputSource(paramLSInput));
      return ((XSGrammar)grammar).toXSModel();
    } catch (Exception exception) {
      reportDOMFatalError(exception);
      return null;
    } 
  }
  
  public XSModel loadInputList(LSInputList paramLSInputList) {
    int i = paramLSInputList.getLength();
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[i];
    for (byte b = 0; b < i; b++) {
      try {
        arrayOfSchemaGrammar[b] = (SchemaGrammar)loadGrammar(dom2xmlInputSource(paramLSInputList.item(b)));
      } catch (Exception exception) {
        reportDOMFatalError(exception);
        return null;
      } 
    } 
    return new XSModelImpl(arrayOfSchemaGrammar);
  }
  
  public XSModel loadURI(String paramString) {
    try {
      Grammar grammar = loadGrammar(new XMLInputSource(null, paramString, null));
      return ((XSGrammar)grammar).toXSModel();
    } catch (Exception exception) {
      reportDOMFatalError(exception);
      return null;
    } 
  }
  
  public XSModel loadURIList(StringList paramStringList) {
    int i = paramStringList.getLength();
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[i];
    for (byte b = 0; b < i; b++) {
      try {
        arrayOfSchemaGrammar[b] = (SchemaGrammar)loadGrammar(new XMLInputSource(null, paramStringList.item(b), null));
      } catch (Exception exception) {
        reportDOMFatalError(exception);
        return null;
      } 
    } 
    return new XSModelImpl(arrayOfSchemaGrammar);
  }
  
  void reportDOMFatalError(Exception paramException) {
    if (this.fErrorHandler != null) {
      DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
      dOMErrorImpl.fException = paramException;
      dOMErrorImpl.fMessage = paramException.getMessage();
      dOMErrorImpl.fSeverity = 3;
      this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
    } 
  }
  
  public boolean canSetParameter(String paramString, Object paramObject) { return (paramObject instanceof Boolean) ? ((paramString.equals("validate") || paramString.equals("http://apache.org/xml/features/validation/schema-full-checking") || paramString.equals("http://apache.org/xml/features/validate-annotations") || paramString.equals("http://apache.org/xml/features/continue-after-fatal-error") || paramString.equals("http://apache.org/xml/features/allow-java-encodings") || paramString.equals("http://apache.org/xml/features/standard-uri-conformant") || paramString.equals("http://apache.org/xml/features/generate-synthetic-annotations") || paramString.equals("http://apache.org/xml/features/honour-all-schemaLocations") || paramString.equals("http://apache.org/xml/features/namespace-growth") || paramString.equals("http://apache.org/xml/features/internal/tolerate-duplicates") || paramString.equals("jdk.xml.overrideDefaultParser"))) : ((paramString.equals("error-handler") || paramString.equals("resource-resolver") || paramString.equals("http://apache.org/xml/properties/internal/symbol-table") || paramString.equals("http://apache.org/xml/properties/internal/error-reporter") || paramString.equals("http://apache.org/xml/properties/internal/error-handler") || paramString.equals("http://apache.org/xml/properties/internal/entity-resolver") || paramString.equals("http://apache.org/xml/properties/internal/grammar-pool") || paramString.equals("http://apache.org/xml/properties/schema/external-schemaLocation") || paramString.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation") || paramString.equals("http://java.sun.com/xml/jaxp/properties/schemaSource") || paramString.equals("http://apache.org/xml/properties/internal/validation/schema/dv-factory"))); }
  
  public Object getParameter(String paramString) throws XMLConfigurationException {
    if (paramString.equals("error-handler"))
      return (this.fErrorHandler != null) ? this.fErrorHandler.getErrorHandler() : null; 
    if (paramString.equals("resource-resolver"))
      return (this.fResourceResolver != null) ? this.fResourceResolver.getEntityResolver() : null; 
    try {
      boolean bool = getFeature(paramString);
      return bool ? Boolean.TRUE : Boolean.FALSE;
    } catch (Exception exception) {
      try {
        return getProperty(paramString);
      } catch (Exception exception1) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str);
      } 
    } 
  }
  
  public DOMStringList getParameterNames() {
    if (this.fRecognizedParameters == null) {
      Vector vector = new Vector();
      vector.add("validate");
      vector.add("error-handler");
      vector.add("resource-resolver");
      vector.add("http://apache.org/xml/properties/internal/symbol-table");
      vector.add("http://apache.org/xml/properties/internal/error-reporter");
      vector.add("http://apache.org/xml/properties/internal/error-handler");
      vector.add("http://apache.org/xml/properties/internal/entity-resolver");
      vector.add("http://apache.org/xml/properties/internal/grammar-pool");
      vector.add("http://apache.org/xml/properties/schema/external-schemaLocation");
      vector.add("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
      vector.add("http://java.sun.com/xml/jaxp/properties/schemaSource");
      vector.add("http://apache.org/xml/features/validation/schema-full-checking");
      vector.add("http://apache.org/xml/features/continue-after-fatal-error");
      vector.add("http://apache.org/xml/features/allow-java-encodings");
      vector.add("http://apache.org/xml/features/standard-uri-conformant");
      vector.add("http://apache.org/xml/features/validate-annotations");
      vector.add("http://apache.org/xml/features/generate-synthetic-annotations");
      vector.add("http://apache.org/xml/features/honour-all-schemaLocations");
      vector.add("http://apache.org/xml/features/namespace-growth");
      vector.add("http://apache.org/xml/features/internal/tolerate-duplicates");
      vector.add("jdk.xml.overrideDefaultParser");
      this.fRecognizedParameters = new DOMStringListImpl(vector);
    } 
    return this.fRecognizedParameters;
  }
  
  public void setParameter(String paramString, Object paramObject) throws XMLConfigurationException {
    if (paramObject instanceof Boolean) {
      boolean bool = ((Boolean)paramObject).booleanValue();
      if (paramString.equals("validate") && bool)
        return; 
      try {
        setFeature(paramString, bool);
      } catch (Exception exception) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str);
      } 
      return;
    } 
    if (paramString.equals("error-handler")) {
      if (paramObject instanceof DOMErrorHandler) {
        try {
          this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)paramObject);
          setErrorHandler(this.fErrorHandler);
        } catch (XMLConfigurationException xMLConfigurationException) {}
      } else {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str);
      } 
      return;
    } 
    if (paramString.equals("resource-resolver")) {
      if (paramObject instanceof LSResourceResolver) {
        try {
          this.fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver)paramObject);
          setEntityResolver(this.fResourceResolver);
        } catch (XMLConfigurationException xMLConfigurationException) {}
      } else {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str);
      } 
      return;
    } 
    try {
      setProperty(paramString, paramObject);
    } catch (Exception exception) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
      throw new DOMException((short)9, str);
    } 
  }
  
  XMLInputSource dom2xmlInputSource(LSInput paramLSInput) {
    XMLInputSource xMLInputSource = null;
    if (paramLSInput.getCharacterStream() != null) {
      xMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI(), paramLSInput.getCharacterStream(), "UTF-16");
    } else if (paramLSInput.getByteStream() != null) {
      xMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI(), paramLSInput.getByteStream(), paramLSInput.getEncoding());
    } else if (paramLSInput.getStringData() != null && paramLSInput.getStringData().length() != 0) {
      xMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI(), new StringReader(paramLSInput.getStringData()), "UTF-16");
    } else {
      xMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI());
    } 
    return xMLInputSource;
  }
  
  public static class LocationArray {
    int length;
    
    String[] locations = new String[2];
    
    public void resize(int param1Int1, int param1Int2) {
      String[] arrayOfString = new String[param1Int2];
      System.arraycopy(this.locations, 0, arrayOfString, 0, Math.min(param1Int1, param1Int2));
      this.locations = arrayOfString;
      this.length = Math.min(param1Int1, param1Int2);
    }
    
    public void addLocation(String param1String) {
      if (this.length >= this.locations.length)
        resize(this.length, Math.max(1, this.length * 2)); 
      this.locations[this.length++] = param1String;
    }
    
    public String[] getLocationArray() {
      if (this.length < this.locations.length)
        resize(this.locations.length, this.length); 
      return this.locations;
    }
    
    public String getFirstLocation() { return (this.length > 0) ? this.locations[0] : null; }
    
    public int getLength() { return this.length; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XMLSchemaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */