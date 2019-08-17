package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

final class SchemaValidatorConfiguration implements XMLComponentManager {
  private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  
  private static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  
  private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  private final XMLComponentManager fParentComponentManager;
  
  private final XMLGrammarPool fGrammarPool;
  
  private final boolean fUseGrammarPoolOnly;
  
  private final ValidationManager fValidationManager;
  
  public SchemaValidatorConfiguration(XMLComponentManager paramXMLComponentManager, XSGrammarPoolContainer paramXSGrammarPoolContainer, ValidationManager paramValidationManager) {
    this.fParentComponentManager = paramXMLComponentManager;
    this.fGrammarPool = paramXSGrammarPoolContainer.getGrammarPool();
    this.fUseGrammarPoolOnly = paramXSGrammarPoolContainer.isFullyComposed();
    this.fValidationManager = paramValidationManager;
    try {
      XMLErrorReporter xMLErrorReporter = (XMLErrorReporter)this.fParentComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      if (xMLErrorReporter != null)
        xMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter()); 
    } catch (XMLConfigurationException xMLConfigurationException) {}
  }
  
  public boolean getFeature(String paramString) throws XMLConfigurationException {
    FeatureState featureState = getFeatureState(paramString);
    if (featureState.isExceptional())
      throw new XMLConfigurationException(featureState.status, paramString); 
    return featureState.state;
  }
  
  public FeatureState getFeatureState(String paramString) { return "http://apache.org/xml/features/internal/parser-settings".equals(paramString) ? this.fParentComponentManager.getFeatureState(paramString) : (("http://xml.org/sax/features/validation".equals(paramString) || "http://apache.org/xml/features/validation/schema".equals(paramString)) ? FeatureState.is(true) : ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(paramString) ? FeatureState.is(this.fUseGrammarPoolOnly) : this.fParentComponentManager.getFeatureState(paramString))); }
  
  public PropertyState getPropertyState(String paramString) { return "http://apache.org/xml/properties/internal/grammar-pool".equals(paramString) ? PropertyState.is(this.fGrammarPool) : ("http://apache.org/xml/properties/internal/validation-manager".equals(paramString) ? PropertyState.is(this.fValidationManager) : this.fParentComponentManager.getPropertyState(paramString)); }
  
  public Object getProperty(String paramString) throws XMLConfigurationException {
    PropertyState propertyState = getPropertyState(paramString);
    if (propertyState.isExceptional())
      throw new XMLConfigurationException(propertyState.status, paramString); 
    return propertyState.state;
  }
  
  public boolean getFeature(String paramString, boolean paramBoolean) {
    FeatureState featureState = getFeatureState(paramString);
    return featureState.isExceptional() ? paramBoolean : featureState.state;
  }
  
  public Object getProperty(String paramString, Object paramObject) {
    PropertyState propertyState = getPropertyState(paramString);
    return propertyState.isExceptional() ? paramObject : propertyState.state;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\SchemaValidatorConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */