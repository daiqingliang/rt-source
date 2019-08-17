package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.lang.ref.SoftReference;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;

final class StreamValidatorHelper implements ValidatorHelper {
  private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private SoftReference fConfiguration = new SoftReference(null);
  
  private XMLSchemaValidator fSchemaValidator;
  
  private XMLSchemaValidatorComponentManager fComponentManager;
  
  private ValidatorHandlerImpl handler = null;
  
  public StreamValidatorHelper(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager) {
    this.fComponentManager = paramXMLSchemaValidatorComponentManager;
    this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
  }
  
  public void validate(Source paramSource, Result paramResult) throws SAXException, IOException {
    if (paramResult == null || paramResult instanceof javax.xml.transform.stream.StreamResult) {
      StreamSource streamSource = (StreamSource)paramSource;
      if (paramResult != null) {
        TransformerHandler transformerHandler;
        try {
          SAXTransformerFactory sAXTransformerFactory = JdkXmlUtils.getSAXTransformFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
          transformerHandler = sAXTransformerFactory.newTransformerHandler();
        } catch (TransformerConfigurationException transformerConfigurationException) {
          throw new TransformerFactoryConfigurationError(transformerConfigurationException);
        } 
        this.handler = new ValidatorHandlerImpl(this.fComponentManager);
        this.handler.setContentHandler(transformerHandler);
        transformerHandler.setResult(paramResult);
      } 
      XMLInputSource xMLInputSource = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), null);
      xMLInputSource.setByteStream(streamSource.getInputStream());
      xMLInputSource.setCharacterStream(streamSource.getReader());
      XMLParserConfiguration xMLParserConfiguration = (XMLParserConfiguration)this.fConfiguration.get();
      if (xMLParserConfiguration == null) {
        xMLParserConfiguration = initialize();
      } else if (this.fComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
        xMLParserConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
        xMLParserConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
      } 
      this.fComponentManager.reset();
      this.fSchemaValidator.setDocumentHandler(this.handler);
      try {
        xMLParserConfiguration.parse(xMLInputSource);
      } catch (XMLParseException xMLParseException) {
        throw Util.toSAXParseException(xMLParseException);
      } catch (XNIException xNIException) {
        throw Util.toSAXException(xNIException);
      } 
      return;
    } 
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
  
  private XMLParserConfiguration initialize() {
    XML11Configuration xML11Configuration = new XML11Configuration();
    if (this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"))
      xML11Configuration.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager()); 
    xML11Configuration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
    xML11Configuration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
    XMLErrorReporter xMLErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    xML11Configuration.setProperty("http://apache.org/xml/properties/internal/error-reporter", xMLErrorReporter);
    if (xMLErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
      XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
      xMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
      xMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
    } 
    xML11Configuration.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    xML11Configuration.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
    xML11Configuration.setDocumentHandler(this.fSchemaValidator);
    xML11Configuration.setDTDHandler(null);
    xML11Configuration.setDTDContentModelHandler(null);
    xML11Configuration.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
    xML11Configuration.setProperty("http://apache.org/xml/properties/security-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
    this.fConfiguration = new SoftReference(xML11Configuration);
    return xML11Configuration;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\StreamValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */