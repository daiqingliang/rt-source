package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public abstract class BasicParserConfiguration extends ParserConfigurationSettings implements XMLParserConfiguration {
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  
  protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected SymbolTable fSymbolTable;
  
  protected Locale fLocale;
  
  protected ArrayList fComponents = new ArrayList();
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDTDHandler fDTDHandler;
  
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  
  protected XMLDocumentSource fLastComponent;
  
  protected BasicParserConfiguration() { this(null, null); }
  
  protected BasicParserConfiguration(SymbolTable paramSymbolTable) { this(paramSymbolTable, null); }
  
  protected BasicParserConfiguration(SymbolTable paramSymbolTable, XMLComponentManager paramXMLComponentManager) {
    super(paramXMLComponentManager);
    String[] arrayOfString1 = { "http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities" };
    addRecognizedFeatures(arrayOfString1);
    this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
    String[] arrayOfString2 = { "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver" };
    addRecognizedProperties(arrayOfString2);
    if (paramSymbolTable == null)
      paramSymbolTable = new SymbolTable(); 
    this.fSymbolTable = paramSymbolTable;
    this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
  }
  
  protected void addComponent(XMLComponent paramXMLComponent) {
    if (this.fComponents.contains(paramXMLComponent))
      return; 
    this.fComponents.add(paramXMLComponent);
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
    if (arrayOfString1 != null)
      for (byte b = 0; b < arrayOfString1.length; b++) {
        String str = arrayOfString1[b];
        Boolean bool = paramXMLComponent.getFeatureDefault(str);
        if (bool != null)
          super.setFeature(str, bool.booleanValue()); 
      }  
    if (arrayOfString2 != null)
      for (byte b = 0; b < arrayOfString2.length; b++) {
        String str = arrayOfString2[b];
        Object object = paramXMLComponent.getPropertyDefault(str);
        if (object != null)
          super.setProperty(str, object); 
      }  
  }
  
  public abstract void parse(XMLInputSource paramXMLInputSource) throws XNIException, IOException;
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) {
    this.fDocumentHandler = paramXMLDocumentHandler;
    if (this.fLastComponent != null) {
      this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fLastComponent); 
    } 
  }
  
  public XMLDocumentHandler getDocumentHandler() { return this.fDocumentHandler; }
  
  public void setDTDHandler(XMLDTDHandler paramXMLDTDHandler) { this.fDTDHandler = paramXMLDTDHandler; }
  
  public XMLDTDHandler getDTDHandler() { return this.fDTDHandler; }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler paramXMLDTDContentModelHandler) { this.fDTDContentModelHandler = paramXMLDTDContentModelHandler; }
  
  public XMLDTDContentModelHandler getDTDContentModelHandler() { return this.fDTDContentModelHandler; }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver) { this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver); }
  
  public XMLEntityResolver getEntityResolver() { return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver"); }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler) { this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler); }
  
  public XMLErrorHandler getErrorHandler() { return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler"); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    int i = this.fComponents.size();
    for (byte b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(b);
      xMLComponent.setFeature(paramString, paramBoolean);
    } 
    super.setFeature(paramString, paramBoolean);
  }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    int i = this.fComponents.size();
    for (byte b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(b);
      xMLComponent.setProperty(paramString, paramObject);
    } 
    super.setProperty(paramString, paramObject);
  }
  
  public void setLocale(Locale paramLocale) throws XNIException { this.fLocale = paramLocale; }
  
  public Locale getLocale() { return this.fLocale; }
  
  protected void reset() {
    int i = this.fComponents.size();
    for (byte b = 0; b < i; b++) {
      XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(b);
      xMLComponent.reset(this);
    } 
  }
  
  protected PropertyState checkProperty(String paramString) throws XMLConfigurationException {
    if (paramString.startsWith("http://xml.org/sax/properties/")) {
      int i = paramString.length() - "http://xml.org/sax/properties/".length();
      if (i == "xml-string".length() && paramString.endsWith("xml-string"))
        return PropertyState.NOT_SUPPORTED; 
    } 
    return super.checkProperty(paramString);
  }
  
  protected FeatureState checkFeature(String paramString) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/features/")) {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if (i == "internal/parser-settings".length() && paramString.endsWith("internal/parser-settings"))
        return FeatureState.NOT_SUPPORTED; 
    } 
    return super.checkFeature(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\BasicParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */