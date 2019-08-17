package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class XIncludeParserConfiguration extends XML11Configuration {
  private XIncludeHandler fXIncludeHandler = new XIncludeHandler();
  
  protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
  
  protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  
  protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
  
  protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
  
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  public XIncludeParserConfiguration() { this(null, null, null); }
  
  public XIncludeParserConfiguration(SymbolTable paramSymbolTable) { this(paramSymbolTable, null, null); }
  
  public XIncludeParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) { this(paramSymbolTable, paramXMLGrammarPool, null); }
  
  public XIncludeParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager) {
    super(paramSymbolTable, paramXMLGrammarPool, paramXMLComponentManager);
    addCommonComponent(this.fXIncludeHandler);
    String[] arrayOfString1 = { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/namespace-context" };
    addRecognizedProperties(arrayOfString2);
    setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
    setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
    setProperty("http://apache.org/xml/properties/internal/namespace-context", new XIncludeNamespaceSupport());
  }
  
  protected void configurePipeline() {
    super.configurePipeline();
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
    if (xMLDocumentSource != null) {
      XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
      xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
      if (xMLDocumentHandler != null) {
        this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
        xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
      } 
    } else {
      setDocumentHandler(this.fXIncludeHandler);
    } 
  }
  
  protected void configureXML11Pipeline() {
    super.configureXML11Pipeline();
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
  }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    if (paramString.equals("http://apache.org/xml/properties/internal/xinclude-handler"));
    super.setProperty(paramString, paramObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XIncludeParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */