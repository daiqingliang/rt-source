package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;

public class XPointerElementHandler implements XPointerSchema {
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String XPOINTER_SCHEMA = "http://apache.org/xml/properties/xpointer-schema";
  
  private static final String[] RECOGNIZED_FEATURES = new String[0];
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[0];
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/xpointer-schema" };
  
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null };
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDocumentSource fDocumentSource;
  
  protected XIncludeHandler fParentXIncludeHandler;
  
  protected XMLLocator fDocLocation;
  
  protected XIncludeNamespaceSupport fNamespaceContext;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected XMLGrammarDescription fGrammarDesc;
  
  protected DTDGrammar fDTDGrammar;
  
  protected XMLEntityResolver fEntityResolver;
  
  protected ParserConfigurationSettings fSettings;
  
  protected StringBuffer fPointer;
  
  private int elemCount = 0;
  
  private int fDepth = 0;
  
  private int fRootDepth = 0;
  
  private static final int INITIAL_SIZE = 8;
  
  private boolean[] fSawInclude = new boolean[8];
  
  private boolean[] fSawFallback = new boolean[8];
  
  private int[] fState = new int[8];
  
  QName foundElement = null;
  
  boolean skip = false;
  
  String fSchemaName;
  
  String fSchemaPointer;
  
  boolean fSubResourceIdentified;
  
  Stack fPointerToken = new Stack();
  
  int fCurrentTokenint = 0;
  
  String fCurrentTokenString = null;
  
  int fCurrentTokenType = 0;
  
  Stack ftempCurrentElement = new Stack();
  
  int fElementCount = 0;
  
  int fCurrentToken;
  
  boolean includeElement;
  
  public XPointerElementHandler() {
    this.fSawFallback[this.fDepth] = false;
    this.fSawInclude[this.fDepth] = false;
    this.fSchemaName = "element";
  }
  
  public void reset() {
    this.elemCount = 0;
    this.fPointerToken = null;
    this.fCurrentTokenint = 0;
    this.fCurrentTokenString = null;
    this.fCurrentTokenType = 0;
    this.fElementCount = 0;
    this.fCurrentToken = 0;
    this.includeElement = false;
    this.foundElement = null;
    this.skip = false;
    this.fSubResourceIdentified = false;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XNIException {
    this.fNamespaceContext = null;
    this.elemCount = 0;
    this.fDepth = 0;
    this.fRootDepth = 0;
    this.fPointerToken = null;
    this.fCurrentTokenint = 0;
    this.fCurrentTokenString = null;
    this.fCurrentTokenType = 0;
    this.foundElement = null;
    this.includeElement = false;
    this.skip = false;
    this.fSubResourceIdentified = false;
    try {
      setErrorReporter((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fErrorReporter = null;
    } 
    try {
      this.fGrammarPool = (XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fGrammarPool = null;
    } 
    try {
      this.fEntityResolver = (XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
    } catch (XMLConfigurationException xMLConfigurationException) {
      this.fEntityResolver = null;
    } 
    this.fSettings = new ParserConfigurationSettings();
    Enumeration enumeration = Constants.getXercesFeatures();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      this.fSettings.addRecognizedFeatures(new String[] { str });
      try {
        this.fSettings.setFeature(str, paramXMLComponentManager.getFeature(str));
      } catch (XMLConfigurationException xMLConfigurationException) {}
    } 
  }
  
  public String[] getRecognizedFeatures() { return RECOGNIZED_FEATURES; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    if (this.fSettings != null)
      this.fSettings.setFeature(paramString, paramBoolean); 
  }
  
  public String[] getRecognizedProperties() { return RECOGNIZED_PROPERTIES; }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    if (paramString.equals("http://apache.org/xml/properties/internal/error-reporter"))
      setErrorReporter((XMLErrorReporter)paramObject); 
    if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool"))
      this.fGrammarPool = (XMLGrammarPool)paramObject; 
    if (paramString.equals("http://apache.org/xml/properties/internal/entity-resolver"))
      this.fEntityResolver = (XMLEntityResolver)paramObject; 
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
  
  private void setErrorReporter(XMLErrorReporter paramXMLErrorReporter) {
    this.fErrorReporter = paramXMLErrorReporter;
    if (this.fErrorReporter != null)
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", new XIncludeMessageFormatter()); 
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) { this.fDocumentHandler = paramXMLDocumentHandler; }
  
  public XMLDocumentHandler getDocumentHandler() { return this.fDocumentHandler; }
  
  public void setXPointerSchemaName(String paramString) { this.fSchemaName = paramString; }
  
  public String getXpointerSchemaName() { return this.fSchemaName; }
  
  public void setParent(Object paramObject) { this.fParentXIncludeHandler = (XIncludeHandler)paramObject; }
  
  public Object getParent() { return this.fParentXIncludeHandler; }
  
  public void setXPointerSchemaPointer(String paramString) { this.fSchemaPointer = paramString; }
  
  public String getXPointerSchemaPointer() { return this.fSchemaPointer; }
  
  public boolean isSubResourceIndentified() { return this.fSubResourceIdentified; }
  
  public void getTokens() {
    this.fSchemaPointer = this.fSchemaPointer.substring(this.fSchemaPointer.indexOf("(") + 1, this.fSchemaPointer.length());
    StringTokenizer stringTokenizer = new StringTokenizer(this.fSchemaPointer, "/");
    Integer integer = null;
    Stack stack = new Stack();
    if (this.fPointerToken == null)
      this.fPointerToken = new Stack(); 
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      try {
        integer = Integer.valueOf(str);
        stack.push(integer);
      } catch (NumberFormatException numberFormatException) {
        stack.push(str);
      } 
    } 
    while (!stack.empty())
      this.fPointerToken.push(stack.pop()); 
  }
  
  public boolean hasMoreToken() { return !this.fPointerToken.isEmpty(); }
  
  public boolean getNextToken() {
    if (!this.fPointerToken.isEmpty()) {
      Object object = this.fPointerToken.pop();
      if (object instanceof Integer) {
        this.fCurrentTokenint = ((Integer)object).intValue();
        this.fCurrentTokenType = 1;
      } else {
        this.fCurrentTokenString = ((String)object).toString();
        this.fCurrentTokenType = 2;
      } 
      return true;
    } 
    return false;
  }
  
  private boolean isIdAttribute(XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, int paramInt) {
    Object object = paramAugmentations.getItem("ID_ATTRIBUTE");
    return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : "ID".equals(paramXMLAttributes.getType(paramInt));
  }
  
  public boolean checkStringToken(QName paramQName, XMLAttributes paramXMLAttributes) {
    Object object1 = null;
    Object object2 = null;
    Object object3 = null;
    QName qName = new QName();
    String str1 = null;
    String str2 = null;
    int i = paramXMLAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      Augmentations augmentations = paramXMLAttributes.getAugmentations(b);
      paramXMLAttributes.getName(b, qName);
      str1 = paramXMLAttributes.getType(b);
      str2 = paramXMLAttributes.getValue(b);
      if (str1 != null && str2 != null && isIdAttribute(paramXMLAttributes, augmentations, b) && str2.equals(this.fCurrentTokenString)) {
        if (hasMoreToken()) {
          this.fCurrentTokenType = 0;
          this.fCurrentTokenString = null;
          return true;
        } 
        this.foundElement = paramQName;
        this.includeElement = true;
        this.fCurrentTokenType = 0;
        this.fCurrentTokenString = null;
        this.fSubResourceIdentified = true;
        return true;
      } 
    } 
    return false;
  }
  
  public boolean checkIntegerToken(QName paramQName) {
    if (!this.skip) {
      this.fElementCount++;
      if (this.fCurrentTokenint == this.fElementCount) {
        if (hasMoreToken()) {
          this.fElementCount = 0;
          this.fCurrentTokenType = 0;
          return true;
        } 
        this.foundElement = paramQName;
        this.includeElement = true;
        this.fCurrentTokenType = 0;
        this.fElementCount = 0;
        this.fSubResourceIdentified = true;
        return true;
      } 
      addQName(paramQName);
      this.skip = true;
      return false;
    } 
    return false;
  }
  
  public void addQName(QName paramQName) {
    QName qName = new QName(paramQName);
    this.ftempCurrentElement.push(qName);
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException { getTokens(); }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.comment(paramXMLString, paramAugmentations); 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations); 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    boolean bool = false;
    if (this.fCurrentTokenType == 0)
      getNextToken(); 
    if (this.fCurrentTokenType == 1) {
      bool = checkIntegerToken(paramQName);
    } else if (this.fCurrentTokenType == 2) {
      bool = checkStringToken(paramQName, paramXMLAttributes);
    } 
    if (bool && hasMoreToken())
      getNextToken(); 
    if (this.fDocumentHandler != null && this.includeElement) {
      this.elemCount++;
      this.fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
    } 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    if (this.includeElement && this.foundElement != null) {
      if (this.elemCount > 0)
        this.elemCount--; 
      this.fDocumentHandler.endElement(paramQName, paramAugmentations);
      if (this.elemCount == 0)
        this.includeElement = false; 
    } else if (!this.ftempCurrentElement.empty()) {
      QName qName = (QName)this.ftempCurrentElement.peek();
      if (qName.equals(paramQName)) {
        this.ftempCurrentElement.pop();
        this.skip = false;
      } 
    } 
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations); 
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations); 
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations); 
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endGeneralEntity(paramString, paramAugmentations); 
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.characters(paramXMLString, paramAugmentations); 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations); 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.startCDATA(paramAugmentations); 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fDocumentHandler != null && this.includeElement)
      this.fDocumentHandler.endCDATA(paramAugmentations); 
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {}
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.fDocumentSource = paramXMLDocumentSource; }
  
  public XMLDocumentSource getDocumentSource() { return this.fDocumentSource; }
  
  protected void reportFatalError(String paramString) { reportFatalError(paramString, null); }
  
  protected void reportFatalError(String paramString, Object[] paramArrayOfObject) {
    if (this.fErrorReporter != null)
      this.fErrorReporter.reportError(this.fDocLocation, "http://www.w3.org/TR/xinclude", paramString, paramArrayOfObject, (short)2); 
  }
  
  protected boolean isRootDocument() { return (this.fParentXIncludeHandler == null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XPointerElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */