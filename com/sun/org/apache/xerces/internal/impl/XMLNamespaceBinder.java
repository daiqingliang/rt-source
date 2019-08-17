package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class XMLNamespaceBinder implements XMLComponent, XMLDocumentFilter {
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces" };
  
  private static final Boolean[] FEATURE_DEFAULTS = { null };
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter" };
  
  private static final Object[] PROPERTY_DEFAULTS = { null, null };
  
  protected boolean fNamespaces;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDocumentSource fDocumentSource;
  
  protected boolean fOnlyPassPrefixMappingEvents;
  
  private NamespaceContext fNamespaceContext;
  
  private QName fAttributeQName = new QName();
  
  public void setOnlyPassPrefixMappingEvents(boolean paramBoolean) { this.fOnlyPassPrefixMappingEvents = paramBoolean; }
  
  public boolean getOnlyPassPrefixMappingEvents() { return this.fOnlyPassPrefixMappingEvents; }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XNIException {
    this.fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
  }
  
  public String[] getRecognizedFeatures() { return (String[])RECOGNIZED_FEATURES.clone(); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {}
  
  public String[] getRecognizedProperties() { return (String[])RECOGNIZED_PROPERTIES.clone(); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "internal/symbol-table".length() && paramString.endsWith("internal/symbol-table")) {
        this.fSymbolTable = (SymbolTable)paramObject;
      } else if (i == "internal/error-reporter".length() && paramString.endsWith("internal/error-reporter")) {
        this.fErrorReporter = (XMLErrorReporter)paramObject;
      } 
      return;
    } 
  }
  
  public Boolean getFeatureDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_FEATURES.length; b++) {
      if (RECOGNIZED_FEATURES[b].equals(paramString))
        return FEATURE_DEFAULTS[b]; 
    } 
    return null;
  }
  
  public Object getPropertyDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_PROPERTIES.length; b++) {
      if (RECOGNIZED_PROPERTIES[b].equals(paramString))
        return PROPERTY_DEFAULTS[b]; 
    } 
    return null;
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) { this.fDocumentHandler = paramXMLDocumentHandler; }
  
  public XMLDocumentHandler getDocumentHandler() { return this.fDocumentHandler; }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.fDocumentSource = paramXMLDocumentSource; }
  
  public XMLDocumentSource getDocumentSource() { return this.fDocumentSource; }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations); 
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations); 
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    this.fNamespaceContext = paramNamespaceContext;
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations); 
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations); 
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations); 
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.comment(paramXMLString, paramAugmentations); 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations); 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (this.fNamespaces) {
      handleStartElement(paramQName, paramXMLAttributes, paramAugmentations, false);
    } else if (this.fDocumentHandler != null) {
      this.fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
    } 
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (this.fNamespaces) {
      handleStartElement(paramQName, paramXMLAttributes, paramAugmentations, true);
      handleEndElement(paramQName, paramAugmentations, true);
    } else if (this.fDocumentHandler != null) {
      this.fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
    } 
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.characters(paramXMLString, paramAugmentations); 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations); 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    if (this.fNamespaces) {
      handleEndElement(paramQName, paramAugmentations, false);
    } else if (this.fDocumentHandler != null) {
      this.fDocumentHandler.endElement(paramQName, paramAugmentations);
    } 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.startCDATA(paramAugmentations); 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.endCDATA(paramAugmentations); 
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.endDocument(paramAugmentations); 
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      this.fDocumentHandler.endGeneralEntity(paramString, paramAugmentations); 
  }
  
  protected void handleStartElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, boolean paramBoolean) throws XNIException {
    this.fNamespaceContext.pushContext();
    if (paramQName.prefix == XMLSymbols.PREFIX_XMLNS)
      this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { paramQName.rawname }, (short)2); 
    int i = paramXMLAttributes.getLength();
    for (byte b1 = 0; b1 < i; b1++) {
      String str1 = paramXMLAttributes.getLocalName(b1);
      String str2 = paramXMLAttributes.getPrefix(b1);
      if (str2 == XMLSymbols.PREFIX_XMLNS || (str2 == XMLSymbols.EMPTY_STRING && str1 == XMLSymbols.PREFIX_XMLNS)) {
        String str3 = this.fSymbolTable.addSymbol(paramXMLAttributes.getValue(b1));
        if (str2 == XMLSymbols.PREFIX_XMLNS && str1 == XMLSymbols.PREFIX_XMLNS)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { paramXMLAttributes.getQName(b1) }, (short)2); 
        if (str3 == NamespaceContext.XMLNS_URI)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { paramXMLAttributes.getQName(b1) }, (short)2); 
        if (str1 == XMLSymbols.PREFIX_XML) {
          if (str3 != NamespaceContext.XML_URI)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { paramXMLAttributes.getQName(b1) }, (short)2); 
        } else if (str3 == NamespaceContext.XML_URI) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { paramXMLAttributes.getQName(b1) }, (short)2);
        } 
        str2 = (str1 != XMLSymbols.PREFIX_XMLNS) ? str1 : XMLSymbols.EMPTY_STRING;
        if (prefixBoundToNullURI(str3, str1)) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { paramXMLAttributes.getQName(b1) }, (short)2);
        } else {
          this.fNamespaceContext.declarePrefix(str2, (str3.length() != 0) ? str3 : null);
        } 
      } 
    } 
    String str = (paramQName.prefix != null) ? paramQName.prefix : XMLSymbols.EMPTY_STRING;
    paramQName.uri = this.fNamespaceContext.getURI(str);
    if (paramQName.prefix == null && paramQName.uri != null)
      paramQName.prefix = XMLSymbols.EMPTY_STRING; 
    if (paramQName.prefix != null && paramQName.uri == null)
      this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { paramQName.prefix, paramQName.rawname }, (short)2); 
    int j;
    for (j = 0; j < i; j++) {
      paramXMLAttributes.getName(j, this.fAttributeQName);
      String str1 = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
      String str2 = this.fAttributeQName.rawname;
      if (str2 == XMLSymbols.PREFIX_XMLNS) {
        this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
        paramXMLAttributes.setName(j, this.fAttributeQName);
      } else if (str1 != XMLSymbols.EMPTY_STRING) {
        this.fAttributeQName.uri = this.fNamespaceContext.getURI(str1);
        if (this.fAttributeQName.uri == null)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { paramQName.rawname, str2, str1 }, (short)2); 
        paramXMLAttributes.setName(j, this.fAttributeQName);
      } 
    } 
    j = paramXMLAttributes.getLength();
    for (byte b2 = 0; b2 < j - 1; b2++) {
      String str1 = paramXMLAttributes.getURI(b2);
      if (str1 != null && str1 != NamespaceContext.XMLNS_URI) {
        String str2 = paramXMLAttributes.getLocalName(b2);
        for (byte b = b2 + 1; b < j; b++) {
          String str3 = paramXMLAttributes.getLocalName(b);
          String str4 = paramXMLAttributes.getURI(b);
          if (str2 == str3 && str1 == str4)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { paramQName.rawname, str2, str1 }, (short)2); 
        } 
      } 
    } 
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents)
      if (paramBoolean) {
        this.fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
      } else {
        this.fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
      }  
  }
  
  protected void handleEndElement(QName paramQName, Augmentations paramAugmentations, boolean paramBoolean) throws XNIException {
    String str = (paramQName.prefix != null) ? paramQName.prefix : XMLSymbols.EMPTY_STRING;
    paramQName.uri = this.fNamespaceContext.getURI(str);
    if (paramQName.uri != null)
      paramQName.prefix = str; 
    if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents && !paramBoolean)
      this.fDocumentHandler.endElement(paramQName, paramAugmentations); 
    this.fNamespaceContext.popContext();
  }
  
  protected boolean prefixBoundToNullURI(String paramString1, String paramString2) { return (paramString1 == XMLSymbols.EMPTY_STRING && paramString2 != XMLSymbols.PREFIX_XMLNS); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLNamespaceBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */