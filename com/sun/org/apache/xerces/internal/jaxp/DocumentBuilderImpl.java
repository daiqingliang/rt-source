package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderImpl extends DocumentBuilder implements JAXPConstants {
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  
  private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
  
  private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
  
  private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
  
  private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
  
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  
  private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
  
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  
  private final DOMParser domParser = new DOMParser();
  
  private final Schema grammar;
  
  private final XMLComponent fSchemaValidator;
  
  private final XMLComponentManager fSchemaValidatorComponentManager;
  
  private final ValidationManager fSchemaValidationManager;
  
  private final UnparsedEntityHandler fUnparsedEntityHandler;
  
  private final ErrorHandler fInitErrorHandler;
  
  private final EntityResolver fInitEntityResolver;
  
  private XMLSecurityManager fSecurityManager;
  
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  DocumentBuilderImpl(DocumentBuilderFactoryImpl paramDocumentBuilderFactoryImpl, Map<String, Object> paramMap1, Map<String, Boolean> paramMap2) throws SAXNotRecognizedException, SAXNotSupportedException { this(paramDocumentBuilderFactoryImpl, paramMap1, paramMap2, false); }
  
  DocumentBuilderImpl(DocumentBuilderFactoryImpl paramDocumentBuilderFactoryImpl, Map<String, Object> paramMap1, Map<String, Boolean> paramMap2, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramDocumentBuilderFactoryImpl.isValidating()) {
      this.fInitErrorHandler = new DefaultValidationErrorHandler(this.domParser.getXMLParserConfiguration().getLocale());
      setErrorHandler(this.fInitErrorHandler);
    } else {
      this.fInitErrorHandler = this.domParser.getErrorHandler();
    } 
    this.domParser.setFeature("http://xml.org/sax/features/validation", paramDocumentBuilderFactoryImpl.isValidating());
    this.domParser.setFeature("http://xml.org/sax/features/namespaces", paramDocumentBuilderFactoryImpl.isNamespaceAware());
    this.domParser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", !paramDocumentBuilderFactoryImpl.isIgnoringElementContentWhitespace());
    this.domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", !paramDocumentBuilderFactoryImpl.isExpandEntityReferences());
    this.domParser.setFeature("http://apache.org/xml/features/include-comments", !paramDocumentBuilderFactoryImpl.isIgnoringComments());
    this.domParser.setFeature("http://apache.org/xml/features/create-cdata-nodes", !paramDocumentBuilderFactoryImpl.isCoalescing());
    if (paramDocumentBuilderFactoryImpl.isXIncludeAware())
      this.domParser.setFeature("http://apache.org/xml/features/xinclude", true); 
    this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this.domParser.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
    this.fSecurityManager = new XMLSecurityManager(paramBoolean);
    this.domParser.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
    if (paramBoolean && paramMap2 != null) {
      Boolean bool = (Boolean)paramMap2.get("http://javax.xml.XMLConstants/feature/secure-processing");
      if (bool != null && bool.booleanValue() && Constants.IS_JDK8_OR_ABOVE) {
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
      } 
    } 
    this.grammar = paramDocumentBuilderFactoryImpl.getSchema();
    if (this.grammar != null) {
      XMLParserConfiguration xMLParserConfiguration = this.domParser.getXMLParserConfiguration();
      JAXPValidatorComponent jAXPValidatorComponent = null;
      if (this.grammar instanceof XSGrammarPoolContainer) {
        jAXPValidatorComponent = new XMLSchemaValidator();
        this.fSchemaValidationManager = new ValidationManager();
        this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
        xMLParserConfiguration.setDTDHandler(this.fUnparsedEntityHandler);
        this.fUnparsedEntityHandler.setDTDHandler(this.domParser);
        this.domParser.setDTDSource(this.fUnparsedEntityHandler);
        this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(xMLParserConfiguration, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
      } else {
        jAXPValidatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
        this.fSchemaValidationManager = null;
        this.fUnparsedEntityHandler = null;
        this.fSchemaValidatorComponentManager = xMLParserConfiguration;
      } 
      xMLParserConfiguration.addRecognizedFeatures(jAXPValidatorComponent.getRecognizedFeatures());
      xMLParserConfiguration.addRecognizedProperties(jAXPValidatorComponent.getRecognizedProperties());
      setFeatures(paramMap2);
      xMLParserConfiguration.setDocumentHandler((XMLDocumentHandler)jAXPValidatorComponent);
      ((XMLDocumentSource)jAXPValidatorComponent).setDocumentHandler(this.domParser);
      this.domParser.setDocumentSource((XMLDocumentSource)jAXPValidatorComponent);
      this.fSchemaValidator = jAXPValidatorComponent;
    } else {
      this.fSchemaValidationManager = null;
      this.fUnparsedEntityHandler = null;
      this.fSchemaValidatorComponentManager = null;
      this.fSchemaValidator = null;
      setFeatures(paramMap2);
    } 
    setDocumentBuilderFactoryAttributes(paramMap1);
    this.fInitEntityResolver = this.domParser.getEntityResolver();
  }
  
  private void setFeatures(Map<String, Boolean> paramMap) throws SAXNotSupportedException, SAXNotRecognizedException {
    if (paramMap != null)
      for (Map.Entry entry : paramMap.entrySet())
        this.domParser.setFeature((String)entry.getKey(), ((Boolean)entry.getValue()).booleanValue());  
  }
  
  private void setDocumentBuilderFactoryAttributes(Map<String, Object> paramMap) throws SAXNotSupportedException, SAXNotRecognizedException {
    if (paramMap == null)
      return; 
    for (Map.Entry entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      Object object = entry.getValue();
      if (object instanceof Boolean) {
        this.domParser.setFeature(str, ((Boolean)object).booleanValue());
        continue;
      } 
      if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(str)) {
        if ("http://www.w3.org/2001/XMLSchema".equals(object) && isValidating()) {
          this.domParser.setFeature("http://apache.org/xml/features/validation/schema", true);
          this.domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        } 
        continue;
      } 
      if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(str)) {
        if (isValidating()) {
          String str1 = (String)paramMap.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
          if (str1 != null && "http://www.w3.org/2001/XMLSchema".equals(str1)) {
            this.domParser.setProperty(str, object);
            continue;
          } 
          throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
        } 
        continue;
      } 
      if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(str, XMLSecurityManager.State.APIPROPERTY, object)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(str, XMLSecurityPropertyManager.State.APIPROPERTY, object)))
        this.domParser.setProperty(str, object); 
    } 
  }
  
  public Document newDocument() { return new DocumentImpl(); }
  
  public DOMImplementation getDOMImplementation() { return DOMImplementationImpl.getDOMImplementation(); }
  
  public Document parse(InputSource paramInputSource) throws SAXException, IOException {
    if (paramInputSource == null)
      throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null)); 
    if (this.fSchemaValidator != null) {
      if (this.fSchemaValidationManager != null) {
        this.fSchemaValidationManager.reset();
        this.fUnparsedEntityHandler.reset();
      } 
      resetSchemaValidator();
    } 
    this.domParser.parse(paramInputSource);
    Document document = this.domParser.getDocument();
    this.domParser.dropDocumentReferences();
    return document;
  }
  
  public boolean isNamespaceAware() {
    try {
      return this.domParser.getFeature("http://xml.org/sax/features/namespaces");
    } catch (SAXException sAXException) {
      throw new IllegalStateException(sAXException.getMessage());
    } 
  }
  
  public boolean isValidating() {
    try {
      return this.domParser.getFeature("http://xml.org/sax/features/validation");
    } catch (SAXException sAXException) {
      throw new IllegalStateException(sAXException.getMessage());
    } 
  }
  
  public boolean isXIncludeAware() {
    try {
      return this.domParser.getFeature("http://apache.org/xml/features/xinclude");
    } catch (SAXException sAXException) {
      return false;
    } 
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) { this.domParser.setEntityResolver(paramEntityResolver); }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this.domParser.setErrorHandler(paramErrorHandler); }
  
  public Schema getSchema() { return this.grammar; }
  
  public void reset() {
    if (this.domParser.getErrorHandler() != this.fInitErrorHandler)
      this.domParser.setErrorHandler(this.fInitErrorHandler); 
    if (this.domParser.getEntityResolver() != this.fInitEntityResolver)
      this.domParser.setEntityResolver(this.fInitEntityResolver); 
  }
  
  DOMParser getDOMParser() { return this.domParser; }
  
  private void resetSchemaValidator() {
    try {
      this.fSchemaValidator.reset(this.fSchemaValidatorComponentManager);
    } catch (XMLConfigurationException xMLConfigurationException) {
      throw new SAXException(xMLConfigurationException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\DocumentBuilderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */