package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import jdk.xml.internal.JdkXmlFeatures;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public final class XMLSchemaFactory extends SchemaFactory {
  private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  
  private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();
  
  private ErrorHandler fErrorHandler;
  
  private LSResourceResolver fLSResourceResolver;
  
  private final DOMEntityResolverWrapper fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
  
  private ErrorHandlerWrapper fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
  
  private XMLSecurityManager fSecurityManager;
  
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  private XMLGrammarPoolWrapper fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
  
  private final JdkXmlFeatures fXmlFeatures;
  
  private final boolean fOverrideDefaultParser;
  
  public XMLSchemaFactory() {
    this.fXMLSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
    this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fXMLGrammarPoolWrapper);
    this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
    this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
    this.fSecurityManager = new XMLSecurityManager(true);
    this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
    this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this.fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
    this.fXmlFeatures = new JdkXmlFeatures(this.fSecurityManager.isSecureProcessing());
    this.fOverrideDefaultParser = this.fXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
    this.fXMLSchemaLoader.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
  }
  
  public boolean isSchemaLanguageSupported(String paramString) {
    if (paramString == null)
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageNull", null)); 
    if (paramString.length() == 0)
      throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageLengthZero", null)); 
    return paramString.equals("http://www.w3.org/2001/XMLSchema");
  }
  
  public LSResourceResolver getResourceResolver() { return this.fLSResourceResolver; }
  
  public void setResourceResolver(LSResourceResolver paramLSResourceResolver) {
    this.fLSResourceResolver = paramLSResourceResolver;
    this.fDOMEntityResolverWrapper.setEntityResolver(paramLSResourceResolver);
    this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
  }
  
  public ErrorHandler getErrorHandler() { return this.fErrorHandler; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) {
    this.fErrorHandler = paramErrorHandler;
    this.fErrorHandlerWrapper.setErrorHandler((paramErrorHandler != null) ? paramErrorHandler : DraconianErrorHandler.getInstance());
    this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
  }
  
  public Schema newSchema(Source[] paramArrayOfSource) throws SAXException {
    XMLGrammarPoolImplExtension xMLGrammarPoolImplExtension = new XMLGrammarPoolImplExtension();
    this.fXMLGrammarPoolWrapper.setGrammarPool(xMLGrammarPoolImplExtension);
    XMLInputSource[] arrayOfXMLInputSource = new XMLInputSource[paramArrayOfSource.length];
    for (i = 0; i < paramArrayOfSource.length; i++) {
      Source source = paramArrayOfSource[i];
      if (source instanceof StreamSource) {
        StreamSource streamSource = (StreamSource)source;
        String str1 = streamSource.getPublicId();
        String str2 = streamSource.getSystemId();
        InputStream inputStream = streamSource.getInputStream();
        Reader reader = streamSource.getReader();
        arrayOfXMLInputSource[i] = new XMLInputSource(str1, str2, null);
        arrayOfXMLInputSource[i].setByteStream(inputStream);
        arrayOfXMLInputSource[i].setCharacterStream(reader);
      } else if (source instanceof SAXSource) {
        SAXSource sAXSource = (SAXSource)source;
        InputSource inputSource = sAXSource.getInputSource();
        if (inputSource == null)
          throw new SAXException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SAXSourceNullInputSource", null)); 
        arrayOfXMLInputSource[i] = new SAXInputSource(sAXSource.getXMLReader(), inputSource);
      } else if (source instanceof DOMSource) {
        DOMSource dOMSource = (DOMSource)source;
        Node node = dOMSource.getNode();
        String str = dOMSource.getSystemId();
        arrayOfXMLInputSource[i] = new DOMInputSource(node, str);
      } else if (source instanceof StAXSource) {
        StAXSource stAXSource = (StAXSource)source;
        XMLEventReader xMLEventReader = stAXSource.getXMLEventReader();
        if (xMLEventReader != null) {
          arrayOfXMLInputSource[i] = new StAXInputSource(xMLEventReader);
        } else {
          arrayOfXMLInputSource[i] = new StAXInputSource(stAXSource.getXMLStreamReader());
        } 
      } else {
        if (source == null)
          throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaSourceArrayMemberNull", null)); 
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaFactorySourceUnrecognized", new Object[] { source.getClass().getName() }));
      } 
    } 
    try {
      this.fXMLSchemaLoader.loadGrammar(arrayOfXMLInputSource);
    } catch (XNIException i) {
      XNIException xNIException;
      throw Util.toSAXException(xNIException);
    } catch (IOException i) {
      IOException iOException;
      SAXParseException sAXParseException = new SAXParseException(iOException.getMessage(), null, iOException);
      this.fErrorHandler.error(sAXParseException);
      throw sAXParseException;
    } 
    this.fXMLGrammarPoolWrapper.setGrammarPool(null);
    i = xMLGrammarPoolImplExtension.getGrammarCount();
    EmptyXMLSchema emptyXMLSchema = null;
    if (i > 1) {
      emptyXMLSchema = new XMLSchema(new ReadOnlyGrammarPool(xMLGrammarPoolImplExtension));
    } else if (i == 1) {
      Grammar[] arrayOfGrammar = xMLGrammarPoolImplExtension.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
      SimpleXMLSchema simpleXMLSchema = new SimpleXMLSchema(arrayOfGrammar[0]);
    } else {
      emptyXMLSchema = new EmptyXMLSchema();
    } 
    propagateFeatures(emptyXMLSchema);
    propagateProperties(emptyXMLSchema);
    return emptyXMLSchema;
  }
  
  public Schema newSchema() throws SAXException {
    WeakReferenceXMLSchema weakReferenceXMLSchema = new WeakReferenceXMLSchema();
    propagateFeatures(weakReferenceXMLSchema);
    propagateProperties(weakReferenceXMLSchema);
    return weakReferenceXMLSchema;
  }
  
  public boolean getFeature(String paramString) {
    if (paramString == null)
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null)); 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      return (this.fSecurityManager != null && this.fSecurityManager.isSecureProcessing()); 
    try {
      return this.fXMLSchemaLoader.getFeature(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { str }));
    } 
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null)); 
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
      return this.fSecurityManager; 
    if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool"))
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { paramString })); 
    int i = this.fXmlFeatures.getIndex(paramString);
    if (i > -1)
      return Boolean.valueOf(this.fXmlFeatures.getFeature(i)); 
    try {
      return this.fXMLSchemaLoader.getProperty(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { str }));
    } 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null)); 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      if (System.getSecurityManager() != null && !paramBoolean)
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null)); 
      this.fSecurityManager.setSecureProcessing(paramBoolean);
      if (paramBoolean && Constants.IS_JDK8_OR_ABOVE) {
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
      } 
      this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
      return;
    } 
    if (paramString.equals("http://www.oracle.com/feature/use-service-mechanism") && System.getSecurityManager() != null)
      return; 
    if (this.fXmlFeatures != null && this.fXmlFeatures.setFeature(paramString, JdkXmlFeatures.State.APIPROPERTY, Boolean.valueOf(paramBoolean))) {
      if (paramString.equals("jdk.xml.overrideDefaultParser") || paramString.equals("http://www.oracle.com/feature/use-service-mechanism"))
        this.fXMLSchemaLoader.setFeature(paramString, paramBoolean); 
      return;
    } 
    try {
      this.fXMLSchemaLoader.setFeature(paramString, paramBoolean);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { str }));
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null)); 
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      this.fSecurityManager = XMLSecurityManager.convert(paramObject, this.fSecurityManager);
      this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
      return;
    } 
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      if (paramObject == null) {
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
      } else {
        this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)paramObject;
      } 
      this.fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
      return;
    } 
    if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool"))
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { paramString })); 
    try {
      if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject)))
        this.fXMLSchemaLoader.setProperty(paramString, paramObject); 
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED)
        throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { str })); 
      throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { str }));
    } 
  }
  
  private void propagateFeatures(AbstractXMLSchema paramAbstractXMLSchema) {
    paramAbstractXMLSchema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", (this.fSecurityManager != null && this.fSecurityManager.isSecureProcessing()));
    paramAbstractXMLSchema.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
    String[] arrayOfString = this.fXMLSchemaLoader.getRecognizedFeatures();
    for (byte b = 0; b < arrayOfString.length; b++) {
      boolean bool = this.fXMLSchemaLoader.getFeature(arrayOfString[b]);
      paramAbstractXMLSchema.setFeature(arrayOfString[b], bool);
    } 
  }
  
  private void propagateProperties(AbstractXMLSchema paramAbstractXMLSchema) {
    String[] arrayOfString = this.fXMLSchemaLoader.getRecognizedProperties();
    for (byte b = 0; b < arrayOfString.length; b++) {
      Object object = this.fXMLSchemaLoader.getProperty(arrayOfString[b]);
      paramAbstractXMLSchema.setProperty(arrayOfString[b], object);
    } 
  }
  
  static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl {
    public XMLGrammarPoolImplExtension() {}
    
    public XMLGrammarPoolImplExtension(int param1Int) { super(param1Int); }
    
    int getGrammarCount() { return this.fGrammarCount; }
  }
  
  static class XMLGrammarPoolWrapper implements XMLGrammarPool {
    private XMLGrammarPool fGrammarPool;
    
    public Grammar[] retrieveInitialGrammarSet(String param1String) { return this.fGrammarPool.retrieveInitialGrammarSet(param1String); }
    
    public void cacheGrammars(String param1String, Grammar[] param1ArrayOfGrammar) { this.fGrammarPool.cacheGrammars(param1String, param1ArrayOfGrammar); }
    
    public Grammar retrieveGrammar(XMLGrammarDescription param1XMLGrammarDescription) { return this.fGrammarPool.retrieveGrammar(param1XMLGrammarDescription); }
    
    public void lockPool() { this.fGrammarPool.lockPool(); }
    
    public void unlockPool() { this.fGrammarPool.unlockPool(); }
    
    public void clear() { this.fGrammarPool.clear(); }
    
    void setGrammarPool(XMLGrammarPool param1XMLGrammarPool) { this.fGrammarPool = param1XMLGrammarPool; }
    
    XMLGrammarPool getGrammarPool() { return this.fGrammarPool; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XMLSchemaFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */