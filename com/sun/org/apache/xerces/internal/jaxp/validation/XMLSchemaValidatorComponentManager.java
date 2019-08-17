package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

final class XMLSchemaValidatorComponentManager extends ParserConfigurationSettings implements XMLComponentManager {
  private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  
  private static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  private static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  
  private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  
  private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  private static final String LOCALE = "http://apache.org/xml/properties/locale";
  
  private boolean _isSecureMode = false;
  
  private boolean fConfigUpdated = true;
  
  private boolean fUseGrammarPoolOnly;
  
  private final HashMap fComponents = new HashMap();
  
  private XMLEntityManager fEntityManager = new XMLEntityManager();
  
  private XMLErrorReporter fErrorReporter;
  
  private NamespaceContext fNamespaceContext;
  
  private XMLSchemaValidator fSchemaValidator;
  
  private ValidationManager fValidationManager;
  
  private final HashMap fInitFeatures = new HashMap();
  
  private final HashMap fInitProperties = new HashMap();
  
  private XMLSecurityManager fInitSecurityManager;
  
  private final XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  private ErrorHandler fErrorHandler = null;
  
  private LSResourceResolver fResourceResolver = null;
  
  private Locale fLocale = null;
  
  public XMLSchemaValidatorComponentManager(XSGrammarPoolContainer paramXSGrammarPoolContainer) {
    this.fComponents.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
    this.fErrorReporter = new XMLErrorReporter();
    this.fComponents.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    this.fNamespaceContext = new NamespaceSupport();
    this.fComponents.put("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
    this.fSchemaValidator = new XMLSchemaValidator();
    this.fComponents.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
    this.fValidationManager = new ValidationManager();
    this.fComponents.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
    this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
    this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
    this.fComponents.put("http://apache.org/xml/properties/internal/symbol-table", new SymbolTable());
    this.fComponents.put("http://apache.org/xml/properties/internal/grammar-pool", paramXSGrammarPoolContainer.getGrammarPool());
    this.fUseGrammarPoolOnly = paramXSGrammarPoolContainer.isFullyComposed();
    this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
    addRecognizedParamsAndSetDefaults(this.fEntityManager, paramXSGrammarPoolContainer);
    addRecognizedParamsAndSetDefaults(this.fErrorReporter, paramXSGrammarPoolContainer);
    addRecognizedParamsAndSetDefaults(this.fSchemaValidator, paramXSGrammarPoolContainer);
    boolean bool = paramXSGrammarPoolContainer.getFeature("http://javax.xml.XMLConstants/feature/secure-processing").booleanValue();
    if (System.getSecurityManager() != null) {
      this._isSecureMode = true;
      bool = true;
    } 
    this.fInitSecurityManager = (XMLSecurityManager)paramXSGrammarPoolContainer.getProperty("http://apache.org/xml/properties/security-manager");
    if (this.fInitSecurityManager != null) {
      this.fInitSecurityManager.setSecureProcessing(bool);
    } else {
      this.fInitSecurityManager = new XMLSecurityManager(bool);
    } 
    setProperty("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
    this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)paramXSGrammarPoolContainer.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
  }
  
  public FeatureState getFeatureState(String paramString) throws XMLConfigurationException { return "http://apache.org/xml/features/internal/parser-settings".equals(paramString) ? FeatureState.is(this.fConfigUpdated) : (("http://xml.org/sax/features/validation".equals(paramString) || "http://apache.org/xml/features/validation/schema".equals(paramString)) ? FeatureState.is(true) : ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(paramString) ? FeatureState.is(this.fUseGrammarPoolOnly) : ("http://javax.xml.XMLConstants/feature/secure-processing".equals(paramString) ? FeatureState.is(this.fInitSecurityManager.isSecureProcessing()) : ("http://apache.org/xml/features/validation/schema/element-default".equals(paramString) ? FeatureState.is(true) : super.getFeatureState(paramString))))); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    if ("http://apache.org/xml/features/internal/parser-settings".equals(paramString))
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString); 
    if (!paramBoolean && ("http://xml.org/sax/features/validation".equals(paramString) || "http://apache.org/xml/features/validation/schema".equals(paramString)))
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString); 
    if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(paramString) && paramBoolean != this.fUseGrammarPoolOnly)
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString); 
    if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(paramString)) {
      if (this._isSecureMode && !paramBoolean)
        throw new XMLConfigurationException(Status.NOT_ALLOWED, "http://javax.xml.XMLConstants/feature/secure-processing"); 
      this.fInitSecurityManager.setSecureProcessing(paramBoolean);
      setProperty("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
      if (paramBoolean && Constants.IS_JDK8_OR_ABOVE) {
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
        setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
      } 
      return;
    } 
    this.fConfigUpdated = true;
    this.fEntityManager.setFeature(paramString, paramBoolean);
    this.fErrorReporter.setFeature(paramString, paramBoolean);
    this.fSchemaValidator.setFeature(paramString, paramBoolean);
    if (!this.fInitFeatures.containsKey(paramString)) {
      boolean bool = getFeature(paramString);
      this.fInitFeatures.put(paramString, bool ? Boolean.TRUE : Boolean.FALSE);
    } 
    super.setFeature(paramString, paramBoolean);
  }
  
  public PropertyState getPropertyState(String paramString) throws XMLConfigurationException {
    if ("http://apache.org/xml/properties/locale".equals(paramString))
      return PropertyState.is(getLocale()); 
    Object object = this.fComponents.get(paramString);
    return (object != null) ? PropertyState.is(object) : (this.fComponents.containsKey(paramString) ? PropertyState.is(null) : super.getPropertyState(paramString));
  }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    if ("http://apache.org/xml/properties/internal/entity-manager".equals(paramString) || "http://apache.org/xml/properties/internal/error-reporter".equals(paramString) || "http://apache.org/xml/properties/internal/namespace-context".equals(paramString) || "http://apache.org/xml/properties/internal/validator/schema".equals(paramString) || "http://apache.org/xml/properties/internal/symbol-table".equals(paramString) || "http://apache.org/xml/properties/internal/validation-manager".equals(paramString) || "http://apache.org/xml/properties/internal/grammar-pool".equals(paramString))
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString); 
    this.fConfigUpdated = true;
    this.fEntityManager.setProperty(paramString, paramObject);
    this.fErrorReporter.setProperty(paramString, paramObject);
    this.fSchemaValidator.setProperty(paramString, paramObject);
    if ("http://apache.org/xml/properties/internal/entity-resolver".equals(paramString) || "http://apache.org/xml/properties/internal/error-handler".equals(paramString) || "http://apache.org/xml/properties/security-manager".equals(paramString)) {
      this.fComponents.put(paramString, paramObject);
      return;
    } 
    if ("http://apache.org/xml/properties/locale".equals(paramString)) {
      setLocale((Locale)paramObject);
      this.fComponents.put(paramString, paramObject);
      return;
    } 
    if ((this.fInitSecurityManager == null || !this.fInitSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject))) {
      if (!this.fInitProperties.containsKey(paramString))
        this.fInitProperties.put(paramString, getProperty(paramString)); 
      super.setProperty(paramString, paramObject);
    } 
  }
  
  public void addRecognizedParamsAndSetDefaults(XMLComponent paramXMLComponent, XSGrammarPoolContainer paramXSGrammarPoolContainer) {
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
    setFeatureDefaults(paramXMLComponent, arrayOfString1, paramXSGrammarPoolContainer);
    setPropertyDefaults(paramXMLComponent, arrayOfString2);
  }
  
  public void reset() throws XNIException {
    this.fNamespaceContext.reset();
    this.fValidationManager.reset();
    this.fEntityManager.reset(this);
    this.fErrorReporter.reset(this);
    this.fSchemaValidator.reset(this);
    this.fConfigUpdated = false;
  }
  
  void setErrorHandler(ErrorHandler paramErrorHandler) {
    this.fErrorHandler = paramErrorHandler;
    setProperty("http://apache.org/xml/properties/internal/error-handler", (paramErrorHandler != null) ? new ErrorHandlerWrapper(paramErrorHandler) : new ErrorHandlerWrapper(DraconianErrorHandler.getInstance()));
  }
  
  ErrorHandler getErrorHandler() { return this.fErrorHandler; }
  
  void setResourceResolver(LSResourceResolver paramLSResourceResolver) {
    this.fResourceResolver = paramLSResourceResolver;
    setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper(paramLSResourceResolver));
  }
  
  LSResourceResolver getResourceResolver() { return this.fResourceResolver; }
  
  void setLocale(Locale paramLocale) {
    this.fLocale = paramLocale;
    this.fErrorReporter.setLocale(paramLocale);
  }
  
  Locale getLocale() { return this.fLocale; }
  
  void restoreInitialState() throws XNIException {
    this.fConfigUpdated = true;
    this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
    this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
    setLocale(null);
    this.fComponents.put("http://apache.org/xml/properties/locale", null);
    this.fComponents.put("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
    setLocale(null);
    this.fComponents.put("http://apache.org/xml/properties/locale", null);
    if (!this.fInitFeatures.isEmpty()) {
      for (Map.Entry entry : this.fInitFeatures.entrySet()) {
        String str = (String)entry.getKey();
        boolean bool = ((Boolean)entry.getValue()).booleanValue();
        super.setFeature(str, bool);
      } 
      this.fInitFeatures.clear();
    } 
    if (!this.fInitProperties.isEmpty()) {
      for (Map.Entry entry : this.fInitProperties.entrySet()) {
        String str = (String)entry.getKey();
        Object object = entry.getValue();
        super.setProperty(str, object);
      } 
      this.fInitProperties.clear();
    } 
  }
  
  private void setFeatureDefaults(XMLComponent paramXMLComponent, String[] paramArrayOfString, XSGrammarPoolContainer paramXSGrammarPoolContainer) {
    if (paramArrayOfString != null)
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        String str = paramArrayOfString[b];
        Boolean bool = paramXSGrammarPoolContainer.getFeature(str);
        if (bool == null)
          bool = paramXMLComponent.getFeatureDefault(str); 
        if (bool != null && !this.fFeatures.containsKey(str)) {
          this.fFeatures.put(str, bool);
          this.fConfigUpdated = true;
        } 
      }  
  }
  
  private void setPropertyDefaults(XMLComponent paramXMLComponent, String[] paramArrayOfString) {
    if (paramArrayOfString != null)
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        String str = paramArrayOfString[b];
        Object object = paramXMLComponent.getPropertyDefault(str);
        if (object != null && !this.fProperties.containsKey(str)) {
          this.fProperties.put(str, object);
          this.fConfigUpdated = true;
        } 
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XMLSchemaValidatorComponentManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */