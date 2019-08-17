package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class XIncludeAwareParserConfiguration extends XML11Configuration {
  protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
  
  protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  
  protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
  
  protected static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  
  protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
  
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  protected XIncludeHandler fXIncludeHandler;
  
  protected NamespaceSupport fNonXIncludeNSContext;
  
  protected XIncludeNamespaceSupport fXIncludeNSContext;
  
  protected NamespaceContext fCurrentNSContext;
  
  protected boolean fXIncludeEnabled = false;
  
  public XIncludeAwareParserConfiguration() { this(null, null, null); }
  
  public XIncludeAwareParserConfiguration(SymbolTable paramSymbolTable) { this(paramSymbolTable, null, null); }
  
  public XIncludeAwareParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) { this(paramSymbolTable, paramXMLGrammarPool, null); }
  
  public XIncludeAwareParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager) {
    super(paramSymbolTable, paramXMLGrammarPool, paramXMLComponentManager);
    String[] arrayOfString1 = { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/namespace-context" };
    addRecognizedProperties(arrayOfString2);
    setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
    this.fNonXIncludeNSContext = new NamespaceSupport();
    this.fCurrentNSContext = this.fNonXIncludeNSContext;
    setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
  }
  
  protected void configurePipeline() {
    super.configurePipeline();
    if (this.fXIncludeEnabled) {
      if (this.fXIncludeHandler == null) {
        this.fXIncludeHandler = new XIncludeHandler();
        setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
        addCommonComponent(this.fXIncludeHandler);
        this.fXIncludeHandler.reset(this);
      } 
      if (this.fCurrentNSContext != this.fXIncludeNSContext) {
        if (this.fXIncludeNSContext == null)
          this.fXIncludeNSContext = new XIncludeNamespaceSupport(); 
        this.fCurrentNSContext = this.fXIncludeNSContext;
        setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fXIncludeNSContext);
      } 
      this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
      this.fDTDProcessor.setDTDSource(this.fDTDScanner);
      this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDTDSource(this.fDTDProcessor);
      this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
      if (this.fDTDHandler != null)
        this.fDTDHandler.setDTDSource(this.fXIncludeHandler); 
      XMLDocumentSource xMLDocumentSource = null;
      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
        xMLDocumentSource = this.fSchemaValidator.getDocumentSource();
      } else {
        xMLDocumentSource = this.fLastComponent;
        this.fLastComponent = this.fXIncludeHandler;
      } 
      XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
      xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
      if (xMLDocumentHandler != null) {
        this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
        xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
      } 
    } else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
      this.fCurrentNSContext = this.fNonXIncludeNSContext;
      setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
    } 
  }
  
  protected void configureXML11Pipeline() {
    super.configureXML11Pipeline();
    if (this.fXIncludeEnabled) {
      if (this.fXIncludeHandler == null) {
        this.fXIncludeHandler = new XIncludeHandler();
        setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
        addCommonComponent(this.fXIncludeHandler);
        this.fXIncludeHandler.reset(this);
      } 
      if (this.fCurrentNSContext != this.fXIncludeNSContext) {
        if (this.fXIncludeNSContext == null)
          this.fXIncludeNSContext = new XIncludeNamespaceSupport(); 
        this.fCurrentNSContext = this.fXIncludeNSContext;
        setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fXIncludeNSContext);
      } 
      this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
      this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
      this.fXML11DTDProcessor.setDTDHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDTDSource(this.fXML11DTDProcessor);
      this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
      if (this.fDTDHandler != null)
        this.fDTDHandler.setDTDSource(this.fXIncludeHandler); 
      XMLDocumentSource xMLDocumentSource = null;
      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
        xMLDocumentSource = this.fSchemaValidator.getDocumentSource();
      } else {
        xMLDocumentSource = this.fLastComponent;
        this.fLastComponent = this.fXIncludeHandler;
      } 
      XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
      xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
      if (xMLDocumentHandler != null) {
        this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
        xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
      } 
    } else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
      this.fCurrentNSContext = this.fNonXIncludeNSContext;
      setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
    } 
  }
  
  public FeatureState getFeatureState(String paramString) throws XMLConfigurationException { return paramString.equals("http://apache.org/xml/features/internal/parser-settings") ? FeatureState.is(this.fConfigUpdated) : (paramString.equals("http://apache.org/xml/features/xinclude") ? FeatureState.is(this.fXIncludeEnabled) : getFeatureState0(paramString)); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    if (paramString.equals("http://apache.org/xml/features/xinclude")) {
      this.fXIncludeEnabled = paramBoolean;
      this.fConfigUpdated = true;
      return;
    } 
    super.setFeature(paramString, paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XIncludeAwareParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */