package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserImpl extends SAXParser implements JAXPConstants, PSVIProvider {
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  
  private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
  
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  
  private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
  
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  private final JAXPSAXParser xmlReader;
  
  private String schemaLanguage = null;
  
  private final Schema grammar;
  
  private final XMLComponent fSchemaValidator;
  
  private final XMLComponentManager fSchemaValidatorComponentManager;
  
  private final ValidationManager fSchemaValidationManager;
  
  private final UnparsedEntityHandler fUnparsedEntityHandler;
  
  private final ErrorHandler fInitErrorHandler;
  
  private final EntityResolver fInitEntityResolver;
  
  private final XMLSecurityManager fSecurityManager;
  
  private final XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  SAXParserImpl(SAXParserFactoryImpl paramSAXParserFactoryImpl, Map<String, Boolean> paramMap) throws SAXException { this(paramSAXParserFactoryImpl, paramMap, false); }
  
  SAXParserImpl(SAXParserFactoryImpl paramSAXParserFactoryImpl, Map<String, Boolean> paramMap, boolean paramBoolean) throws SAXException {
    this.fSecurityManager = new XMLSecurityManager(paramBoolean);
    this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this.xmlReader = new JAXPSAXParser(this, this.fSecurityPropertyMgr, this.fSecurityManager);
    this.xmlReader.setFeature0("http://xml.org/sax/features/namespaces", paramSAXParserFactoryImpl.isNamespaceAware());
    this.xmlReader.setFeature0("http://xml.org/sax/features/namespace-prefixes", !paramSAXParserFactoryImpl.isNamespaceAware());
    if (paramSAXParserFactoryImpl.isXIncludeAware())
      this.xmlReader.setFeature0("http://apache.org/xml/features/xinclude", true); 
    this.xmlReader.setProperty0("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
    this.xmlReader.setProperty0("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
    if (paramBoolean && paramMap != null) {
      Boolean bool = (Boolean)paramMap.get("http://javax.xml.XMLConstants/feature/secure-processing");
      if (bool != null && bool.booleanValue() && Constants.IS_JDK8_OR_ABOVE) {
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
      } 
    } 
    setFeatures(paramMap);
    if (paramSAXParserFactoryImpl.isValidating()) {
      this.fInitErrorHandler = new DefaultValidationErrorHandler(this.xmlReader.getLocale());
      this.xmlReader.setErrorHandler(this.fInitErrorHandler);
    } else {
      this.fInitErrorHandler = this.xmlReader.getErrorHandler();
    } 
    this.xmlReader.setFeature0("http://xml.org/sax/features/validation", paramSAXParserFactoryImpl.isValidating());
    this.grammar = paramSAXParserFactoryImpl.getSchema();
    if (this.grammar != null) {
      XMLParserConfiguration xMLParserConfiguration = this.xmlReader.getXMLParserConfiguration();
      JAXPValidatorComponent jAXPValidatorComponent = null;
      if (this.grammar instanceof XSGrammarPoolContainer) {
        jAXPValidatorComponent = new XMLSchemaValidator();
        this.fSchemaValidationManager = new ValidationManager();
        this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
        xMLParserConfiguration.setDTDHandler(this.fUnparsedEntityHandler);
        this.fUnparsedEntityHandler.setDTDHandler(this.xmlReader);
        this.xmlReader.setDTDSource(this.fUnparsedEntityHandler);
        this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(xMLParserConfiguration, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
      } else {
        jAXPValidatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
        this.fSchemaValidationManager = null;
        this.fUnparsedEntityHandler = null;
        this.fSchemaValidatorComponentManager = xMLParserConfiguration;
      } 
      xMLParserConfiguration.addRecognizedFeatures(jAXPValidatorComponent.getRecognizedFeatures());
      xMLParserConfiguration.addRecognizedProperties(jAXPValidatorComponent.getRecognizedProperties());
      xMLParserConfiguration.setDocumentHandler((XMLDocumentHandler)jAXPValidatorComponent);
      ((XMLDocumentSource)jAXPValidatorComponent).setDocumentHandler(this.xmlReader);
      this.xmlReader.setDocumentSource((XMLDocumentSource)jAXPValidatorComponent);
      this.fSchemaValidator = jAXPValidatorComponent;
    } else {
      this.fSchemaValidationManager = null;
      this.fUnparsedEntityHandler = null;
      this.fSchemaValidatorComponentManager = null;
      this.fSchemaValidator = null;
    } 
    this.fInitEntityResolver = this.xmlReader.getEntityResolver();
  }
  
  private void setFeatures(Map<String, Boolean> paramMap) throws SAXNotSupportedException, SAXNotRecognizedException {
    if (paramMap != null)
      for (Map.Entry entry : paramMap.entrySet())
        this.xmlReader.setFeature0((String)entry.getKey(), ((Boolean)entry.getValue()).booleanValue());  
  }
  
  public Parser getParser() throws SAXException { return this.xmlReader; }
  
  public XMLReader getXMLReader() { return this.xmlReader; }
  
  public boolean isNamespaceAware() {
    try {
      return this.xmlReader.getFeature("http://xml.org/sax/features/namespaces");
    } catch (SAXException sAXException) {
      throw new IllegalStateException(sAXException.getMessage());
    } 
  }
  
  public boolean isValidating() {
    try {
      return this.xmlReader.getFeature("http://xml.org/sax/features/validation");
    } catch (SAXException sAXException) {
      throw new IllegalStateException(sAXException.getMessage());
    } 
  }
  
  public boolean isXIncludeAware() {
    try {
      return this.xmlReader.getFeature("http://apache.org/xml/features/xinclude");
    } catch (SAXException sAXException) {
      return false;
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException { this.xmlReader.setProperty(paramString, paramObject); }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return this.xmlReader.getProperty(paramString); }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramInputSource == null)
      throw new IllegalArgumentException(); 
    if (paramDefaultHandler != null) {
      this.xmlReader.setContentHandler(paramDefaultHandler);
      this.xmlReader.setEntityResolver(paramDefaultHandler);
      this.xmlReader.setErrorHandler(paramDefaultHandler);
      this.xmlReader.setDTDHandler(paramDefaultHandler);
      this.xmlReader.setDocumentHandler(null);
    } 
    this.xmlReader.parse(paramInputSource);
  }
  
  public void parse(InputSource paramInputSource, HandlerBase paramHandlerBase) throws SAXException, IOException {
    if (paramInputSource == null)
      throw new IllegalArgumentException(); 
    if (paramHandlerBase != null) {
      this.xmlReader.setDocumentHandler(paramHandlerBase);
      this.xmlReader.setEntityResolver(paramHandlerBase);
      this.xmlReader.setErrorHandler(paramHandlerBase);
      this.xmlReader.setDTDHandler(paramHandlerBase);
      this.xmlReader.setContentHandler(null);
    } 
    this.xmlReader.parse(paramInputSource);
  }
  
  public Schema getSchema() { return this.grammar; }
  
  public void reset() {
    try {
      this.xmlReader.restoreInitState();
    } catch (SAXException sAXException) {}
    this.xmlReader.setContentHandler(null);
    this.xmlReader.setDTDHandler(null);
    if (this.xmlReader.getErrorHandler() != this.fInitErrorHandler)
      this.xmlReader.setErrorHandler(this.fInitErrorHandler); 
    if (this.xmlReader.getEntityResolver() != this.fInitEntityResolver)
      this.xmlReader.setEntityResolver(this.fInitEntityResolver); 
  }
  
  public ElementPSVI getElementPSVI() { return this.xmlReader.getElementPSVI(); }
  
  public AttributePSVI getAttributePSVI(int paramInt) { return this.xmlReader.getAttributePSVI(paramInt); }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2) { return this.xmlReader.getAttributePSVIByName(paramString1, paramString2); }
  
  public static class JAXPSAXParser extends SAXParser {
    private final HashMap fInitFeatures = new HashMap();
    
    private final HashMap fInitProperties = new HashMap();
    
    private final SAXParserImpl fSAXParser;
    
    private XMLSecurityManager fSecurityManager;
    
    private XMLSecurityPropertyManager fSecurityPropertyMgr;
    
    public JAXPSAXParser() { this(null, null, null); }
    
    JAXPSAXParser(SAXParserImpl param1SAXParserImpl, XMLSecurityPropertyManager param1XMLSecurityPropertyManager, XMLSecurityManager param1XMLSecurityManager) {
      this.fSAXParser = param1SAXParserImpl;
      this.fSecurityManager = param1XMLSecurityManager;
      this.fSecurityPropertyMgr = param1XMLSecurityPropertyManager;
      if (this.fSecurityManager == null) {
        this.fSecurityManager = new XMLSecurityManager(true);
        try {
          super.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        } catch (SAXException sAXException) {
          throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { "http://apache.org/xml/properties/security-manager" }), sAXException);
        } 
      } 
      if (this.fSecurityPropertyMgr == null) {
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        try {
          super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        } catch (SAXException sAXException) {
          throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { "http://apache.org/xml/properties/security-manager" }), sAXException);
        } 
      } 
    }
    
    public void setFeature(String param1String, boolean param1Boolean) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (param1String == null)
        throw new NullPointerException(); 
      if (param1String.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
        try {
          this.fSecurityManager.setSecureProcessing(param1Boolean);
          setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        } catch (SAXNotRecognizedException sAXNotRecognizedException) {
          if (param1Boolean)
            throw sAXNotRecognizedException; 
        } catch (SAXNotSupportedException sAXNotSupportedException) {
          if (param1Boolean)
            throw sAXNotSupportedException; 
        } 
        return;
      } 
      if (!this.fInitFeatures.containsKey(param1String)) {
        boolean bool = super.getFeature(param1String);
        this.fInitFeatures.put(param1String, bool ? Boolean.TRUE : Boolean.FALSE);
      } 
      if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null)
        setSchemaValidatorFeature(param1String, param1Boolean); 
      super.setFeature(param1String, param1Boolean);
    }
    
    public boolean getFeature(String param1String) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (param1String == null)
        throw new NullPointerException(); 
      return param1String.equals("http://javax.xml.XMLConstants/feature/secure-processing") ? this.fSecurityManager.isSecureProcessing() : super.getFeature(param1String);
    }
    
    public void setProperty(String param1String, Object param1Object) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (param1String == null)
        throw new NullPointerException(); 
      if (this.fSAXParser != null) {
        if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(param1String)) {
          if (this.fSAXParser.grammar != null)
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { param1String })); 
          if ("http://www.w3.org/2001/XMLSchema".equals(param1Object)) {
            if (this.fSAXParser.isValidating()) {
              this.fSAXParser.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
              setFeature("http://apache.org/xml/features/validation/schema", true);
              if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage"))
                this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage")); 
              super.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            } 
          } else if (param1Object == null) {
            this.fSAXParser.schemaLanguage = null;
            setFeature("http://apache.org/xml/features/validation/schema", false);
          } else {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-not-supported", null));
          } 
          return;
        } 
        if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(param1String)) {
          if (this.fSAXParser.grammar != null)
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { param1String })); 
          String str = (String)getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
          if (str != null && "http://www.w3.org/2001/XMLSchema".equals(str)) {
            if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource"))
              this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaSource", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource")); 
            super.setProperty(param1String, param1Object);
          } else {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
          } 
          return;
        } 
      } 
      if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null)
        setSchemaValidatorProperty(param1String, param1Object); 
      if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(param1String, XMLSecurityManager.State.APIPROPERTY, param1Object)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(param1String, XMLSecurityPropertyManager.State.APIPROPERTY, param1Object))) {
        if (!this.fInitProperties.containsKey(param1String))
          this.fInitProperties.put(param1String, super.getProperty(param1String)); 
        super.setProperty(param1String, param1Object);
      } 
    }
    
    public Object getProperty(String param1String) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (param1String == null)
        throw new NullPointerException(); 
      if (this.fSAXParser != null && "http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(param1String))
        return this.fSAXParser.schemaLanguage; 
      String str = (this.fSecurityManager != null) ? this.fSecurityManager.getLimitAsString(param1String) : null;
      if (str != null)
        return str; 
      str = (this.fSecurityPropertyMgr != null) ? this.fSecurityPropertyMgr.getValue(param1String) : null;
      return (str != null) ? str : super.getProperty(param1String);
    }
    
    void restoreInitState() {
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
    
    public void parse(InputSource param1InputSource) throws SAXException, IOException {
      if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
        if (this.fSAXParser.fSchemaValidationManager != null) {
          this.fSAXParser.fSchemaValidationManager.reset();
          this.fSAXParser.fUnparsedEntityHandler.reset();
        } 
        resetSchemaValidator();
      } 
      super.parse(param1InputSource);
    }
    
    public void parse(String param1String) throws SAXException, IOException {
      if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
        if (this.fSAXParser.fSchemaValidationManager != null) {
          this.fSAXParser.fSchemaValidationManager.reset();
          this.fSAXParser.fUnparsedEntityHandler.reset();
        } 
        resetSchemaValidator();
      } 
      super.parse(param1String);
    }
    
    XMLParserConfiguration getXMLParserConfiguration() { return this.fConfiguration; }
    
    void setFeature0(String param1String, boolean param1Boolean) throws SAXNotRecognizedException, SAXNotSupportedException { super.setFeature(param1String, param1Boolean); }
    
    boolean getFeature0(String param1String) throws SAXNotRecognizedException, SAXNotSupportedException { return super.getFeature(param1String); }
    
    void setProperty0(String param1String, Object param1Object) throws SAXNotRecognizedException, SAXNotSupportedException { super.setProperty(param1String, param1Object); }
    
    Object getProperty0(String param1String) throws SAXNotRecognizedException, SAXNotSupportedException { return super.getProperty(param1String); }
    
    Locale getLocale() { return this.fConfiguration.getLocale(); }
    
    private void setSchemaValidatorFeature(String param1String, boolean param1Boolean) throws SAXNotRecognizedException, SAXNotSupportedException {
      try {
        this.fSAXParser.fSchemaValidator.setFeature(param1String, param1Boolean);
      } catch (XMLConfigurationException xMLConfigurationException) {
        String str = xMLConfigurationException.getIdentifier();
        if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
          throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { str })); 
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { str }));
      } 
    }
    
    private void setSchemaValidatorProperty(String param1String, Object param1Object) throws SAXNotRecognizedException, SAXNotSupportedException {
      try {
        this.fSAXParser.fSchemaValidator.setProperty(param1String, param1Object);
      } catch (XMLConfigurationException xMLConfigurationException) {
        String str = xMLConfigurationException.getIdentifier();
        if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
          throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { str })); 
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { str }));
      } 
    }
    
    private void resetSchemaValidator() {
      try {
        this.fSAXParser.fSchemaValidator.reset(this.fSAXParser.fSchemaValidatorComponentManager);
      } catch (XMLConfigurationException xMLConfigurationException) {
        throw new SAXException(xMLConfigurationException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\SAXParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */