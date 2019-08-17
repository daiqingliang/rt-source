package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;

public class XMLDocumentFragmentScannerImpl extends XMLScanner implements XMLDocumentScanner, XMLComponent, XMLEntityHandler, XMLBufferListener {
  protected int fElementAttributeLimit;
  
  protected int fXMLNameLimit;
  
  protected ExternalSubsetResolver fExternalSubsetResolver;
  
  protected static final int SCANNER_STATE_START_OF_MARKUP = 21;
  
  protected static final int SCANNER_STATE_CONTENT = 22;
  
  protected static final int SCANNER_STATE_PI = 23;
  
  protected static final int SCANNER_STATE_DOCTYPE = 24;
  
  protected static final int SCANNER_STATE_XML_DECL = 25;
  
  protected static final int SCANNER_STATE_ROOT_ELEMENT = 26;
  
  protected static final int SCANNER_STATE_COMMENT = 27;
  
  protected static final int SCANNER_STATE_REFERENCE = 28;
  
  protected static final int SCANNER_STATE_ATTRIBUTE = 29;
  
  protected static final int SCANNER_STATE_ATTRIBUTE_VALUE = 30;
  
  protected static final int SCANNER_STATE_END_OF_INPUT = 33;
  
  protected static final int SCANNER_STATE_TERMINATED = 34;
  
  protected static final int SCANNER_STATE_CDATA = 35;
  
  protected static final int SCANNER_STATE_TEXT_DECL = 36;
  
  protected static final int SCANNER_STATE_CHARACTER_DATA = 37;
  
  protected static final int SCANNER_STATE_START_ELEMENT_TAG = 38;
  
  protected static final int SCANNER_STATE_END_ELEMENT_TAG = 39;
  
  protected static final int SCANNER_STATE_CHAR_REFERENCE = 40;
  
  protected static final int SCANNER_STATE_BUILT_IN_REFS = 41;
  
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  static final String EXTERNAL_ACCESS_DEFAULT = "all";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "report-cdata-event" };
  
  private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, null, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE };
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null };
  
  private static final char[] cdata = { '[', 'C', 'D', 'A', 'T', 'A', '[' };
  
  static final char[] xmlDecl = { '<', '?', 'x', 'm', 'l' };
  
  private static final boolean DEBUG_SCANNER_STATE = false;
  
  private static final boolean DEBUG_DISPATCHER = false;
  
  protected static final boolean DEBUG_START_END_ELEMENT = false;
  
  protected static final boolean DEBUG_NEXT = false;
  
  protected static final boolean DEBUG = false;
  
  protected static final boolean DEBUG_COALESCE = false;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected int fScannerLastState;
  
  protected XMLEntityStorage fEntityStore;
  
  protected int[] fEntityStack = new int[4];
  
  protected int fMarkupDepth;
  
  protected boolean fEmptyElement;
  
  protected boolean fReadingAttributes = false;
  
  protected int fScannerState;
  
  protected boolean fInScanContent = false;
  
  protected boolean fLastSectionWasCData = false;
  
  protected boolean fLastSectionWasEntityReference = false;
  
  protected boolean fLastSectionWasCharacterData = false;
  
  protected boolean fHasExternalDTD;
  
  protected boolean fStandaloneSet;
  
  protected boolean fStandalone;
  
  protected String fVersion;
  
  protected QName fCurrentElement;
  
  protected ElementStack fElementStack = new ElementStack();
  
  protected ElementStack2 fElementStack2 = new ElementStack2();
  
  protected String fPITarget;
  
  protected XMLString fPIData = new XMLString();
  
  protected boolean fNotifyBuiltInRefs = false;
  
  protected boolean fSupportDTD = true;
  
  protected boolean fReplaceEntityReferences = true;
  
  protected boolean fSupportExternalEntities = false;
  
  protected boolean fReportCdataEvent = false;
  
  protected boolean fIsCoalesce = false;
  
  protected String fDeclaredEncoding = null;
  
  protected boolean fDisallowDoctype = false;
  
  protected String fAccessExternalDTD = "all";
  
  protected boolean fStrictURI;
  
  protected Driver fDriver;
  
  protected Driver fContentDriver = createContentDriver();
  
  protected QName fElementQName = new QName();
  
  protected QName fAttributeQName = new QName();
  
  protected XMLAttributesIteratorImpl fAttributes = new XMLAttributesIteratorImpl();
  
  protected XMLString fTempString = new XMLString();
  
  protected XMLString fTempString2 = new XMLString();
  
  private String[] fStrings = new String[3];
  
  protected XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  protected XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  
  protected XMLStringBuffer fContentBuffer = new XMLStringBuffer();
  
  private final char[] fSingleChar = new char[1];
  
  private String fCurrentEntityName = null;
  
  protected boolean fScanToEnd = false;
  
  protected DTDGrammarUtil dtdGrammarUtil = null;
  
  protected boolean fAddDefaultAttr = false;
  
  protected boolean foundBuiltInRefs = false;
  
  static final short MAX_DEPTH_LIMIT = 5;
  
  static final short ELEMENT_ARRAY_LENGTH = 200;
  
  static final short MAX_POINTER_AT_A_DEPTH = 4;
  
  static final boolean DEBUG_SKIP_ALGORITHM = false;
  
  String[] fElementArray = new String[200];
  
  short fLastPointerLocation = 0;
  
  short fElementPointer = 0;
  
  short[][] fPointerInfo = new short[5][4];
  
  protected String fElementRawname;
  
  protected boolean fShouldSkip = false;
  
  protected boolean fAdd = false;
  
  protected boolean fSkip = false;
  
  private Augmentations fTempAugmentations = null;
  
  protected boolean fUsebuffer;
  
  public void setInputSource(XMLInputSource paramXMLInputSource) throws IOException {
    this.fEntityManager.setEntityHandler(this);
    this.fEntityManager.startEntity(false, "$fragment$", paramXMLInputSource, false, true);
  }
  
  public boolean scanDocument(boolean paramBoolean) throws IOException, XNIException {
    this.fEntityManager.setEntityHandler(this);
    int i = next();
    do {
      switch (i) {
        case 7:
        case 1:
          break;
        case 4:
          this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
          this.fDocumentHandler.characters(getCharacterData(), null);
          break;
        case 6:
          break;
        case 9:
          this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
          break;
        case 3:
          this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
          this.fDocumentHandler.processingInstruction(getPITarget(), getPIData(), null);
          break;
        case 5:
          this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
          this.fDocumentHandler.comment(getCharacterData(), null);
          break;
        case 11:
          break;
        case 12:
          this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
          this.fDocumentHandler.startCDATA(null);
          this.fDocumentHandler.characters(getCharacterData(), null);
          this.fDocumentHandler.endCDATA(null);
          break;
        case 14:
        case 15:
        case 13:
        case 10:
        case 2:
          break;
        default:
          throw new InternalError("processing event: " + i);
      } 
      i = next();
    } while (i != 8 && paramBoolean);
    if (i == 8) {
      this.fDocumentHandler.endDocument(null);
      return false;
    } 
    return true;
  }
  
  public QName getElementQName() {
    if (this.fScannerLastState == 2)
      this.fElementQName.setValues(this.fElementStack.getLastPoppedElement()); 
    return this.fElementQName;
  }
  
  public int next() throws IOException, XNIException { return this.fDriver.next(); }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    super.reset(paramXMLComponentManager);
    this.fReportCdataEvent = paramXMLComponentManager.getFeature("report-cdata-event", true);
    this.fSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
    this.fNotifyBuiltInRefs = paramXMLComponentManager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
    Object object = paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
    this.fExternalSubsetResolver = (object instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)object : null;
    this.fReadingAttributes = false;
    this.fSupportExternalEntities = true;
    this.fReplaceEntityReferences = true;
    this.fIsCoalesce = false;
    setScannerState(22);
    setDriver(this.fContentDriver);
    XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
    this.fAccessExternalDTD = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this.fStrictURI = paramXMLComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
    resetCommon();
  }
  
  public void reset(PropertyManager paramPropertyManager) {
    super.reset(paramPropertyManager);
    this.fNamespaces = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue();
    this.fNotifyBuiltInRefs = false;
    Boolean bool1 = (Boolean)paramPropertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences");
    this.fReplaceEntityReferences = bool1.booleanValue();
    bool1 = (Boolean)paramPropertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities");
    this.fSupportExternalEntities = bool1.booleanValue();
    Boolean bool2 = (Boolean)paramPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/report-cdata-event");
    if (bool2 != null)
      this.fReportCdataEvent = bool2.booleanValue(); 
    Boolean bool3 = (Boolean)paramPropertyManager.getProperty("javax.xml.stream.isCoalescing");
    if (bool3 != null)
      this.fIsCoalesce = bool3.booleanValue(); 
    this.fReportCdataEvent = this.fIsCoalesce ? false : (this.fReportCdataEvent);
    this.fReplaceEntityReferences = this.fIsCoalesce ? true : this.fReplaceEntityReferences;
    XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramPropertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    this.fAccessExternalDTD = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this.fSecurityManager = (XMLSecurityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/security-manager");
    resetCommon();
  }
  
  void resetCommon() {
    this.fMarkupDepth = 0;
    this.fCurrentElement = null;
    this.fElementStack.clear();
    this.fHasExternalDTD = false;
    this.fStandaloneSet = false;
    this.fStandalone = false;
    this.fInScanContent = false;
    this.fShouldSkip = false;
    this.fAdd = false;
    this.fSkip = false;
    this.fEntityStore = this.fEntityManager.getEntityStore();
    this.dtdGrammarUtil = null;
    if (this.fSecurityManager != null) {
      this.fElementAttributeLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT);
      this.fXMLNameLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT);
    } else {
      this.fElementAttributeLimit = 0;
      this.fXMLNameLimit = XMLSecurityManager.Limit.MAX_NAME_LIMIT.defaultValue();
    } 
    this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
  }
  
  public String[] getRecognizedFeatures() { return (String[])RECOGNIZED_FEATURES.clone(); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    super.setFeature(paramString, paramBoolean);
    if (paramString.startsWith("http://apache.org/xml/features/")) {
      String str = paramString.substring("http://apache.org/xml/features/".length());
      if (str.equals("scanner/notify-builtin-refs"))
        this.fNotifyBuiltInRefs = paramBoolean; 
    } 
  }
  
  public String[] getRecognizedProperties() { return (String[])RECOGNIZED_PROPERTIES.clone(); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    super.setProperty(paramString, paramObject);
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "internal/entity-manager".length() && paramString.endsWith("internal/entity-manager")) {
        this.fEntityManager = (XMLEntityManager)paramObject;
        return;
      } 
      if (i == "internal/entity-resolver".length() && paramString.endsWith("internal/entity-resolver")) {
        this.fExternalSubsetResolver = (paramObject instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)paramObject : null;
        return;
      } 
    } 
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      String str = paramString.substring("http://apache.org/xml/properties/".length());
      if (str.equals("internal/entity-manager"))
        this.fEntityManager = (XMLEntityManager)paramObject; 
      return;
    } 
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      XMLSecurityPropertyManager xMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramObject;
      this.fAccessExternalDTD = xMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
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
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fEntityDepth == this.fEntityStack.length) {
      int[] arrayOfInt = new int[this.fEntityStack.length * 2];
      System.arraycopy(this.fEntityStack, 0, arrayOfInt, 0, this.fEntityStack.length);
      this.fEntityStack = arrayOfInt;
    } 
    this.fEntityStack[this.fEntityDepth] = this.fMarkupDepth;
    super.startEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    if (this.fStandalone && this.fEntityStore.isEntityDeclInExternalSubset(paramString1))
      reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { paramString1 }); 
    if (this.fDocumentHandler != null && !this.fScanningAttribute && !paramString1.equals("[xml]"))
      this.fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations); 
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations) throws IOException, XNIException {
    super.endEntity(paramString, paramAugmentations);
    if (this.fMarkupDepth != this.fEntityStack[this.fEntityDepth])
      reportFatalError("MarkupEntityMismatch", null); 
    if (this.fDocumentHandler != null && !this.fScanningAttribute && !paramString.equals("[xml]"))
      this.fDocumentHandler.endGeneralEntity(paramString, paramAugmentations); 
  }
  
  protected Driver createContentDriver() { return new FragmentContentDriver(); }
  
  protected void scanXMLDeclOrTextDecl(boolean paramBoolean) throws IOException, XNIException {
    scanXMLDeclOrTextDecl(paramBoolean, this.fStrings);
    this.fMarkupDepth--;
    String str1 = this.fStrings[0];
    String str2 = this.fStrings[1];
    String str3 = this.fStrings[2];
    this.fDeclaredEncoding = str2;
    this.fStandaloneSet = (str3 != null);
    this.fStandalone = (this.fStandaloneSet && str3.equals("yes"));
    this.fEntityManager.setStandalone(this.fStandalone);
    if (this.fDocumentHandler != null)
      if (paramBoolean) {
        this.fDocumentHandler.textDecl(str1, str2, null);
      } else {
        this.fDocumentHandler.xmlDecl(str1, str2, str3, null);
      }  
    if (str1 != null) {
      this.fEntityScanner.setVersion(str1);
      this.fEntityScanner.setXMLVersion(str1);
    } 
    if (str2 != null && !this.fEntityScanner.getCurrentEntity().isEncodingExternallySpecified())
      this.fEntityScanner.setEncoding(str2); 
  }
  
  public String getPITarget() { return this.fPITarget; }
  
  public XMLStringBuffer getPIData() { return this.fContentBuffer; }
  
  public XMLString getCharacterData() { return this.fUsebuffer ? this.fContentBuffer : this.fTempString; }
  
  protected void scanPIData(String paramString, XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    super.scanPIData(paramString, paramXMLStringBuffer);
    this.fPITarget = paramString;
    this.fMarkupDepth--;
  }
  
  protected void scanComment() {
    this.fContentBuffer.clear();
    scanComment(this.fContentBuffer);
    this.fUsebuffer = true;
    this.fMarkupDepth--;
  }
  
  public String getComment() { return this.fContentBuffer.toString(); }
  
  void addElement(String paramString) {
    if (this.fElementPointer < 200) {
      this.fElementArray[this.fElementPointer] = paramString;
      if (this.fElementStack.fDepth < 5) {
        short s = storePointerForADepth(this.fElementPointer);
        if (s > 0) {
          short s1 = getElementPointer((short)this.fElementStack.fDepth, (short)(s - 1));
          if (paramString == this.fElementArray[s1]) {
            this.fShouldSkip = true;
            this.fLastPointerLocation = s1;
            resetPointer((short)this.fElementStack.fDepth, s);
            this.fElementArray[this.fElementPointer] = null;
            return;
          } 
          this.fShouldSkip = false;
        } 
      } 
      this.fElementPointer = (short)(this.fElementPointer + 1);
    } 
  }
  
  void resetPointer(short paramShort1, short paramShort2) { this.fPointerInfo[paramShort1][paramShort2] = 0; }
  
  short storePointerForADepth(short paramShort) {
    short s1 = (short)this.fElementStack.fDepth;
    for (short s2 = 0; s2 < 4; s2 = (short)(s2 + 1)) {
      if (canStore(s1, s2)) {
        this.fPointerInfo[s1][s2] = paramShort;
        return s2;
      } 
    } 
    return -1;
  }
  
  boolean canStore(short paramShort1, short paramShort2) { return (this.fPointerInfo[paramShort1][paramShort2] == 0); }
  
  short getElementPointer(short paramShort1, short paramShort2) { return this.fPointerInfo[paramShort1][paramShort2]; }
  
  boolean skipFromTheBuffer(String paramString) throws IOException {
    if (this.fEntityScanner.skipString(paramString)) {
      char c = (char)this.fEntityScanner.peekChar();
      if (c == ' ' || c == '/' || c == '>') {
        this.fElementRawname = paramString;
        return true;
      } 
      return false;
    } 
    return false;
  }
  
  boolean skipQElement(String paramString) throws IOException {
    int i = this.fEntityScanner.getChar(paramString.length());
    return XMLChar.isName(i) ? false : this.fEntityScanner.skipString(paramString);
  }
  
  protected boolean skipElement() throws IOException {
    if (!this.fShouldSkip)
      return false; 
    if (this.fLastPointerLocation != 0) {
      String str = this.fElementArray[this.fLastPointerLocation + 1];
      if (str != null && skipFromTheBuffer(str)) {
        this.fLastPointerLocation = (short)(this.fLastPointerLocation + 1);
        return true;
      } 
      this.fLastPointerLocation = 0;
    } 
    return (this.fShouldSkip && skipElement((short)0));
  }
  
  boolean skipElement(short paramShort) throws IOException {
    short s1 = (short)this.fElementStack.fDepth;
    if (s1 > 5)
      return this.fShouldSkip = false; 
    for (short s2 = paramShort; s2 < 4; s2 = (short)(s2 + 1)) {
      short s = getElementPointer(s1, s2);
      if (s == 0)
        return this.fShouldSkip = false; 
      if (this.fElementArray[s] != null && skipFromTheBuffer(this.fElementArray[s])) {
        this.fLastPointerLocation = s;
        return this.fShouldSkip = true;
      } 
    } 
    return this.fShouldSkip = false;
  }
  
  protected boolean scanStartElement() throws IOException {
    if (this.fSkip && !this.fAdd) {
      QName qName = this.fElementStack.getNext();
      this.fSkip = this.fEntityScanner.skipString(qName.rawname);
      if (this.fSkip) {
        this.fElementStack.push();
        this.fElementQName = qName;
      } else {
        this.fElementStack.reposition();
      } 
    } 
    if (!this.fSkip || this.fAdd) {
      this.fElementQName = this.fElementStack.nextElement();
      if (this.fNamespaces) {
        this.fEntityScanner.scanQName(this.fElementQName, XMLScanner.NameType.ELEMENTSTART);
      } else {
        String str1 = this.fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
        this.fElementQName.setValues(null, str1, str1, null);
      } 
    } 
    if (this.fAdd)
      this.fElementStack.matchElement(this.fElementQName); 
    this.fCurrentElement = this.fElementQName;
    String str = this.fElementQName.rawname;
    this.fEmptyElement = false;
    this.fAttributes.removeAllAttributes();
    checkDepth(str);
    if (!seekCloseOfStartTag()) {
      this.fReadingAttributes = true;
      this.fAttributeCacheUsedCount = 0;
      this.fStringBufferIndex = 0;
      this.fAddDefaultAttr = true;
      do {
        scanAttribute(this.fAttributes);
        if (this.fSecurityManager == null || this.fSecurityManager.isNoLimit(this.fElementAttributeLimit) || this.fAttributes.getLength() <= this.fElementAttributeLimit)
          continue; 
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { str, Integer.valueOf(this.fElementAttributeLimit) }, (short)2);
      } while (!seekCloseOfStartTag());
      this.fReadingAttributes = false;
    } 
    if (this.fEmptyElement) {
      this.fMarkupDepth--;
      if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
        reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname }); 
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null); 
      this.fElementStack.popElement();
    } else {
      if (this.dtdGrammarUtil != null)
        this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes); 
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null); 
    } 
    return this.fEmptyElement;
  }
  
  protected boolean seekCloseOfStartTag() throws IOException {
    boolean bool = this.fEntityScanner.skipSpaces();
    int i = this.fEntityScanner.peekChar();
    if (i == 62) {
      this.fEntityScanner.scanChar(null);
      return true;
    } 
    if (i == 47) {
      this.fEntityScanner.scanChar(null);
      if (!this.fEntityScanner.skipChar(62, XMLScanner.NameType.ELEMENTEND))
        reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname }); 
      this.fEmptyElement = true;
      return true;
    } 
    if ((!isValidNameStartChar(i) || !bool) && (!isValidNameStartHighSurrogate(i) || !bool))
      reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname }); 
    return false;
  }
  
  public boolean hasAttributes() throws IOException { return (this.fAttributes.getLength() > 0); }
  
  public XMLAttributesIteratorImpl getAttributeIterator() {
    if (this.dtdGrammarUtil != null && this.fAddDefaultAttr) {
      this.dtdGrammarUtil.addDTDDefaultAttrs(this.fElementQName, this.fAttributes);
      this.fAddDefaultAttr = false;
    } 
    return this.fAttributes;
  }
  
  public boolean standaloneSet() throws IOException { return this.fStandaloneSet; }
  
  public boolean isStandAlone() throws IOException { return this.fStandalone; }
  
  protected void scanAttribute(XMLAttributes paramXMLAttributes) throws IOException, XNIException {
    if (this.fNamespaces) {
      this.fEntityScanner.scanQName(this.fAttributeQName, XMLScanner.NameType.ATTRIBUTENAME);
    } else {
      String str = this.fEntityScanner.scanName(XMLScanner.NameType.ATTRIBUTENAME);
      this.fAttributeQName.setValues(null, str, str, null);
    } 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(61, XMLScanner.NameType.ATTRIBUTE))
      reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    this.fEntityScanner.skipSpaces();
    int i = 0;
    boolean bool = (this.fHasExternalDTD && !this.fStandalone);
    XMLString xMLString = getString();
    scanAttributeValue(xMLString, this.fTempString2, this.fAttributeQName.rawname, paramXMLAttributes, i, bool, this.fCurrentElement.rawname, false);
    int j = paramXMLAttributes.getLength();
    i = paramXMLAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
    if (j == paramXMLAttributes.getLength())
      reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    paramXMLAttributes.setValue(i, null, xMLString);
    paramXMLAttributes.setSpecified(i, true);
  }
  
  protected int scanContent(XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    this.fTempString.length = 0;
    int i = this.fEntityScanner.scanContent(this.fTempString);
    paramXMLStringBuffer.append(this.fTempString);
    this.fTempString.length = 0;
    if (i == 13) {
      this.fEntityScanner.scanChar(null);
      paramXMLStringBuffer.append((char)i);
      i = -1;
    } else if (i == 93) {
      paramXMLStringBuffer.append((char)this.fEntityScanner.scanChar(null));
      this.fInScanContent = true;
      if (this.fEntityScanner.skipChar(93, null)) {
        paramXMLStringBuffer.append(']');
        while (this.fEntityScanner.skipChar(93, null))
          paramXMLStringBuffer.append(']'); 
        if (this.fEntityScanner.skipChar(62, null))
          reportFatalError("CDEndInContent", null); 
      } 
      this.fInScanContent = false;
      i = -1;
    } 
    if (this.fDocumentHandler == null || paramXMLStringBuffer.length > 0);
    return i;
  }
  
  protected boolean scanCDATASection(XMLStringBuffer paramXMLStringBuffer, boolean paramBoolean) throws IOException, XNIException { // Byte code:
    //   0: aload_0
    //   1: getfield fDocumentHandler : Lcom/sun/org/apache/xerces/internal/xni/XMLDocumentHandler;
    //   4: ifnull -> 7
    //   7: aload_0
    //   8: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   11: ldc_w ']]>'
    //   14: aload_1
    //   15: invokevirtual scanData : (Ljava/lang/String;Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;)Z
    //   18: ifne -> 24
    //   21: goto -> 100
    //   24: aload_0
    //   25: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   28: invokevirtual peekChar : ()I
    //   31: istore_3
    //   32: iload_3
    //   33: iconst_m1
    //   34: if_icmpeq -> 90
    //   37: aload_0
    //   38: iload_3
    //   39: invokevirtual isInvalidLiteral : (I)Z
    //   42: ifeq -> 90
    //   45: iload_3
    //   46: invokestatic isHighSurrogate : (I)Z
    //   49: ifeq -> 61
    //   52: aload_0
    //   53: aload_1
    //   54: invokevirtual scanSurrogates : (Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;)Z
    //   57: pop
    //   58: goto -> 90
    //   61: aload_0
    //   62: ldc_w 'InvalidCharInCDSect'
    //   65: iconst_1
    //   66: anewarray java/lang/Object
    //   69: dup
    //   70: iconst_0
    //   71: iload_3
    //   72: bipush #16
    //   74: invokestatic toString : (II)Ljava/lang/String;
    //   77: aastore
    //   78: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   81: aload_0
    //   82: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   85: aconst_null
    //   86: invokevirtual scanChar : (Lcom/sun/org/apache/xerces/internal/impl/XMLScanner$NameType;)I
    //   89: pop
    //   90: aload_0
    //   91: getfield fDocumentHandler : Lcom/sun/org/apache/xerces/internal/xni/XMLDocumentHandler;
    //   94: ifnull -> 97
    //   97: goto -> 7
    //   100: aload_0
    //   101: dup
    //   102: getfield fMarkupDepth : I
    //   105: iconst_1
    //   106: isub
    //   107: putfield fMarkupDepth : I
    //   110: aload_0
    //   111: getfield fDocumentHandler : Lcom/sun/org/apache/xerces/internal/xni/XMLDocumentHandler;
    //   114: ifnull -> 124
    //   117: aload_1
    //   118: getfield length : I
    //   121: ifle -> 124
    //   124: aload_0
    //   125: getfield fDocumentHandler : Lcom/sun/org/apache/xerces/internal/xni/XMLDocumentHandler;
    //   128: ifnull -> 131
    //   131: iconst_1
    //   132: ireturn }
  
  protected int scanEndElement() throws IOException, XNIException {
    QName qName = this.fElementStack.popElement();
    String str = qName.rawname;
    if (!this.fEntityScanner.skipString(qName.rawname))
      reportFatalError("ETagRequired", new Object[] { str }); 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(62, XMLScanner.NameType.ELEMENTEND))
      reportFatalError("ETagUnterminated", new Object[] { str }); 
    this.fMarkupDepth--;
    this.fMarkupDepth--;
    if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
      reportFatalError("ElementEntityMismatch", new Object[] { str }); 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endElement(qName, null); 
    if (this.dtdGrammarUtil != null)
      this.dtdGrammarUtil.endElement(qName); 
    return this.fMarkupDepth;
  }
  
  protected void scanCharReference() {
    this.fStringBuffer2.clear();
    int i = scanCharReferenceValue(this.fStringBuffer2, null);
    this.fMarkupDepth--;
    if (i != -1 && this.fDocumentHandler != null) {
      if (this.fNotifyCharRefs)
        this.fDocumentHandler.startGeneralEntity(this.fCharRefLiteral, null, null, null); 
      Augmentations augmentations = null;
      if (this.fValidation && i <= 32) {
        if (this.fTempAugmentations != null) {
          this.fTempAugmentations.removeAllItems();
        } else {
          this.fTempAugmentations = new AugmentationsImpl();
        } 
        augmentations = this.fTempAugmentations;
        augmentations.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
      } 
      if (this.fNotifyCharRefs)
        this.fDocumentHandler.endGeneralEntity(this.fCharRefLiteral, null); 
    } 
  }
  
  protected void scanEntityReference(XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    String str = this.fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
    if (str == null) {
      reportFatalError("NameRequiredInReference", null);
      return;
    } 
    if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE))
      reportFatalError("SemicolonRequiredInReference", new Object[] { str }); 
    if (this.fEntityStore.isUnparsedEntity(str))
      reportFatalError("ReferenceToUnparsedEntity", new Object[] { str }); 
    this.fMarkupDepth--;
    this.fCurrentEntityName = str;
    if (str == fAmpSymbol) {
      handleCharacter('&', fAmpSymbol, paramXMLStringBuffer);
      this.fScannerState = 41;
      return;
    } 
    if (str == fLtSymbol) {
      handleCharacter('<', fLtSymbol, paramXMLStringBuffer);
      this.fScannerState = 41;
      return;
    } 
    if (str == fGtSymbol) {
      handleCharacter('>', fGtSymbol, paramXMLStringBuffer);
      this.fScannerState = 41;
      return;
    } 
    if (str == fQuotSymbol) {
      handleCharacter('"', fQuotSymbol, paramXMLStringBuffer);
      this.fScannerState = 41;
      return;
    } 
    if (str == fAposSymbol) {
      handleCharacter('\'', fAposSymbol, paramXMLStringBuffer);
      this.fScannerState = 41;
      return;
    } 
    boolean bool = this.fEntityStore.isExternalEntity(str);
    if ((bool && !this.fSupportExternalEntities) || (!bool && !this.fReplaceEntityReferences) || this.foundBuiltInRefs) {
      this.fScannerState = 28;
      return;
    } 
    if (!this.fEntityStore.isDeclaredEntity(str)) {
      if (!this.fSupportDTD && this.fReplaceEntityReferences) {
        reportFatalError("EntityNotDeclared", new Object[] { str });
        return;
      } 
      if (this.fHasExternalDTD && !this.fStandalone) {
        if (this.fValidation)
          this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { str }, (short)1); 
      } else {
        reportFatalError("EntityNotDeclared", new Object[] { str });
      } 
    } 
    this.fEntityManager.startEntity(true, str, false);
  }
  
  void checkDepth(String paramString) {
    this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, paramString, this.fElementStack.fDepth);
    if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      reportFatalError("MaxElementDepthLimit", new Object[] { paramString, Integer.valueOf(this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT)), Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT)), "maxElementDepth" });
    } 
  }
  
  private void handleCharacter(char paramChar, String paramString, XMLStringBuffer paramXMLStringBuffer) throws XNIException {
    this.foundBuiltInRefs = true;
    checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
    paramXMLStringBuffer.append(paramChar);
    if (this.fDocumentHandler != null) {
      this.fSingleChar[0] = paramChar;
      if (this.fNotifyBuiltInRefs)
        this.fDocumentHandler.startGeneralEntity(paramString, null, null, null); 
      this.fTempString.setValues(this.fSingleChar, 0, 1);
      if (this.fNotifyBuiltInRefs)
        this.fDocumentHandler.endGeneralEntity(paramString, null); 
    } 
  }
  
  protected final void setScannerState(int paramInt) { this.fScannerState = paramInt; }
  
  protected final void setDriver(Driver paramDriver) { this.fDriver = paramDriver; }
  
  protected String getScannerStateName(int paramInt) {
    switch (paramInt) {
      case 24:
        return "SCANNER_STATE_DOCTYPE";
      case 26:
        return "SCANNER_STATE_ROOT_ELEMENT";
      case 21:
        return "SCANNER_STATE_START_OF_MARKUP";
      case 27:
        return "SCANNER_STATE_COMMENT";
      case 23:
        return "SCANNER_STATE_PI";
      case 22:
        return "SCANNER_STATE_CONTENT";
      case 28:
        return "SCANNER_STATE_REFERENCE";
      case 33:
        return "SCANNER_STATE_END_OF_INPUT";
      case 34:
        return "SCANNER_STATE_TERMINATED";
      case 35:
        return "SCANNER_STATE_CDATA";
      case 36:
        return "SCANNER_STATE_TEXT_DECL";
      case 29:
        return "SCANNER_STATE_ATTRIBUTE";
      case 30:
        return "SCANNER_STATE_ATTRIBUTE_VALUE";
      case 38:
        return "SCANNER_STATE_START_ELEMENT_TAG";
      case 39:
        return "SCANNER_STATE_END_ELEMENT_TAG";
      case 37:
        return "SCANNER_STATE_CHARACTER_DATA";
    } 
    return "??? (" + paramInt + ')';
  }
  
  public String getEntityName() { return this.fCurrentEntityName; }
  
  public String getDriverName(Driver paramDriver) { return "null"; }
  
  String checkAccess(String paramString1, String paramString2) throws IOException {
    String str1 = this.fEntityScanner.getBaseSystemId();
    String str2 = XMLEntityManager.expandSystemId(paramString1, str1, this.fStrictURI);
    return SecuritySupport.checkAccess(str2, paramString2, "all");
  }
  
  static void pr(String paramString) { System.out.println(paramString); }
  
  protected XMLString getString() {
    if (this.fAttributeCacheUsedCount < this.initialCacheCount || this.fAttributeCacheUsedCount < this.attributeValueCache.size())
      return (XMLString)this.attributeValueCache.get(this.fAttributeCacheUsedCount++); 
    XMLString xMLString = new XMLString();
    this.fAttributeCacheUsedCount++;
    this.attributeValueCache.add(xMLString);
    return xMLString;
  }
  
  public void refresh() { refresh(0); }
  
  public void refresh(int paramInt) {
    if (this.fReadingAttributes)
      this.fAttributes.refresh(); 
    if (this.fScannerState == 37) {
      this.fContentBuffer.append(this.fTempString);
      this.fTempString.length = 0;
      this.fUsebuffer = true;
    } 
  }
  
  protected static interface Driver {
    int next() throws IOException, XNIException;
  }
  
  protected static final class Element {
    public QName qname;
    
    public char[] fRawname;
    
    public Element next;
    
    public Element(QName param1QName, Element param1Element) {
      this.qname.setValues(param1QName);
      this.fRawname = param1QName.rawname.toCharArray();
      this.next = param1Element;
    }
  }
  
  protected class ElementStack {
    protected QName[] fElements = new QName[20];
    
    protected int[] fInt = new int[20];
    
    protected int fDepth;
    
    protected int fCount;
    
    protected int fPosition;
    
    protected int fMark;
    
    protected int fLastDepth;
    
    public ElementStack() {
      for (byte b = 0; b < this.fElements.length; b++)
        this.fElements[b] = new QName(); 
    }
    
    public QName pushElement(QName param1QName) {
      if (this.fDepth == this.fElements.length) {
        QName[] arrayOfQName = new QName[this.fElements.length * 2];
        System.arraycopy(this.fElements, 0, arrayOfQName, 0, this.fDepth);
        this.fElements = arrayOfQName;
        for (int i = this.fDepth; i < this.fElements.length; i++)
          this.fElements[i] = new QName(); 
      } 
      this.fElements[this.fDepth].setValues(param1QName);
      return this.fElements[this.fDepth++];
    }
    
    public QName getNext() {
      if (this.fPosition == this.fCount)
        this.fPosition = this.fMark; 
      return this.fElements[this.fPosition];
    }
    
    public void push() { this.fInt[++this.fDepth] = this.fPosition++; }
    
    public boolean matchElement(QName param1QName) {
      boolean bool = false;
      if (this.fLastDepth > this.fDepth && this.fDepth <= 3)
        if (param1QName.rawname == (this.fElements[this.fDepth - true]).rawname) {
          XMLDocumentFragmentScannerImpl.this.fAdd = false;
          this.fMark = this.fDepth - 1;
          this.fPosition = this.fMark;
          bool = true;
          this.fCount--;
        } else {
          XMLDocumentFragmentScannerImpl.this.fAdd = true;
        }  
      if (bool) {
        this.fInt[this.fDepth] = this.fPosition++;
      } else {
        this.fInt[this.fDepth] = this.fCount - 1;
      } 
      if (this.fCount == this.fElements.length) {
        XMLDocumentFragmentScannerImpl.this.fSkip = false;
        XMLDocumentFragmentScannerImpl.this.fAdd = false;
        reposition();
        return false;
      } 
      this.fLastDepth = this.fDepth;
      return bool;
    }
    
    public QName nextElement() {
      if (XMLDocumentFragmentScannerImpl.this.fSkip) {
        this.fDepth++;
        return this.fElements[this.fCount++];
      } 
      if (this.fDepth == this.fElements.length) {
        QName[] arrayOfQName = new QName[this.fElements.length * 2];
        System.arraycopy(this.fElements, 0, arrayOfQName, 0, this.fDepth);
        this.fElements = arrayOfQName;
        for (int i = this.fDepth; i < this.fElements.length; i++)
          this.fElements[i] = new QName(); 
      } 
      return this.fElements[this.fDepth++];
    }
    
    public QName popElement() { return (XMLDocumentFragmentScannerImpl.this.fSkip || XMLDocumentFragmentScannerImpl.this.fAdd) ? this.fElements[this.fInt[this.fDepth--]] : this.fElements[--this.fDepth]; }
    
    public void reposition() {
      for (byte b = 2; b <= this.fDepth; b++)
        this.fElements[b - 1] = this.fElements[this.fInt[b]]; 
    }
    
    public void clear() {
      this.fDepth = 0;
      this.fLastDepth = 0;
      this.fCount = 0;
      this.fPosition = this.fMark = 1;
    }
    
    public QName getLastPoppedElement() { return this.fElements[this.fDepth]; }
  }
  
  protected class ElementStack2 {
    protected QName[] fQName = new QName[20];
    
    protected int fDepth;
    
    protected int fCount;
    
    protected int fPosition;
    
    protected int fMark;
    
    protected int fLastDepth;
    
    public ElementStack2() {
      for (byte b = 0; b < this.fQName.length; b++)
        this.fQName[b] = new QName(); 
      this.fMark = this.fPosition = 1;
    }
    
    public void resize() {
      int i = this.fQName.length;
      QName[] arrayOfQName = new QName[i * 2];
      System.arraycopy(this.fQName, 0, arrayOfQName, 0, i);
      this.fQName = arrayOfQName;
      for (int j = i; j < this.fQName.length; j++)
        this.fQName[j] = new QName(); 
    }
    
    public boolean matchElement(QName param1QName) {
      boolean bool = false;
      if (this.fLastDepth > this.fDepth && this.fDepth <= 2)
        if (param1QName.rawname == (this.fQName[this.fDepth]).rawname) {
          XMLDocumentFragmentScannerImpl.this.fAdd = false;
          this.fMark = this.fDepth - 1;
          this.fPosition = this.fMark + 1;
          bool = true;
          this.fCount--;
        } else {
          XMLDocumentFragmentScannerImpl.this.fAdd = true;
        }  
      this.fLastDepth = this.fDepth++;
      return bool;
    }
    
    public QName nextElement() {
      if (this.fCount == this.fQName.length) {
        XMLDocumentFragmentScannerImpl.this.fShouldSkip = false;
        XMLDocumentFragmentScannerImpl.this.fAdd = false;
        return this.fQName[--this.fCount];
      } 
      return this.fQName[this.fCount++];
    }
    
    public QName getNext() {
      if (this.fPosition == this.fCount)
        this.fPosition = this.fMark; 
      return this.fQName[this.fPosition++];
    }
    
    public int popElement() throws IOException, XNIException { return this.fDepth--; }
    
    public void clear() {
      this.fLastDepth = 0;
      this.fDepth = 0;
      this.fCount = 0;
      this.fPosition = this.fMark = 1;
    }
  }
  
  protected class FragmentContentDriver implements Driver {
    private void startOfMarkup() {
      XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
      int i = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
      if (XMLDocumentFragmentScannerImpl.this.isValidNameStartChar(i) || XMLDocumentFragmentScannerImpl.this.isValidNameStartHighSurrogate(i)) {
        XMLDocumentFragmentScannerImpl.this.setScannerState(38);
      } else {
        switch (i) {
          case 63:
            XMLDocumentFragmentScannerImpl.this.setScannerState(23);
            XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(i, null);
            return;
          case 33:
            XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(i, null);
            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45, null)) {
              if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45, XMLScanner.NameType.COMMENT))
                XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCommentStart", null); 
              XMLDocumentFragmentScannerImpl.this.setScannerState(27);
            } else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString(cdata)) {
              XMLDocumentFragmentScannerImpl.this.setScannerState(35);
            } else if (!scanForDoctypeHook()) {
              XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
            } 
            return;
          case 47:
            XMLDocumentFragmentScannerImpl.this.setScannerState(39);
            XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(i, XMLScanner.NameType.ELEMENTEND);
            return;
        } 
        XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
      } 
    }
    
    private void startOfContent() {
      if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
        XMLDocumentFragmentScannerImpl.this.setScannerState(21);
      } else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE)) {
        XMLDocumentFragmentScannerImpl.this.setScannerState(28);
      } else {
        XMLDocumentFragmentScannerImpl.this.setScannerState(37);
      } 
    }
    
    public void decideSubState() {
      while (XMLDocumentFragmentScannerImpl.this.fScannerState == 22 || XMLDocumentFragmentScannerImpl.this.fScannerState == 21) {
        switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
          case 22:
            startOfContent();
          case 21:
            startOfMarkup();
        } 
      } 
    }
    
    public int next() throws IOException, XNIException {
      try {
        while (true) {
          int i;
          switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
            case 22:
              i = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
              if (i == 60) {
                XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                XMLDocumentFragmentScannerImpl.this.setScannerState(21);
              } else {
                if (i == 38) {
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
                  XMLDocumentFragmentScannerImpl.this.setScannerState(28);
                  break;
                } 
                XMLDocumentFragmentScannerImpl.this.setScannerState(37);
                break;
              } 
            case 21:
              startOfMarkup();
              break;
          } 
          if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
            XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
            if (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData) {
              if (XMLDocumentFragmentScannerImpl.this.fScannerState != 35 && XMLDocumentFragmentScannerImpl.this.fScannerState != 28 && XMLDocumentFragmentScannerImpl.this.fScannerState != 37) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                return 4;
              } 
            } else if ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) && XMLDocumentFragmentScannerImpl.this.fScannerState != 35 && XMLDocumentFragmentScannerImpl.this.fScannerState != 28 && XMLDocumentFragmentScannerImpl.this.fScannerState != 37) {
              XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
              XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
              return 4;
            } 
          } 
          switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
            case 7:
              return 7;
            case 38:
              XMLDocumentFragmentScannerImpl.this.fEmptyElement = XMLDocumentFragmentScannerImpl.this.scanStartElement();
              if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
                XMLDocumentFragmentScannerImpl.this.setScannerState(39);
              } else {
                XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              } 
              return 1;
            case 37:
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData);
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              } else {
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              } 
              this.this$0.fTempString.length = 0;
              i = XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanContent(XMLDocumentFragmentScannerImpl.this.fTempString);
              if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(47, XMLScanner.NameType.ELEMENTEND)) {
                  XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
                  XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                  XMLDocumentFragmentScannerImpl.this.setScannerState(39);
                } else if (XMLChar.isNameStart(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                  XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
                  XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                  XMLDocumentFragmentScannerImpl.this.setScannerState(38);
                } else {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                  if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                    XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                    XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                    XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                    this.this$0.fTempString.length = 0;
                    continue;
                  } 
                } 
                if (XMLDocumentFragmentScannerImpl.this.fUsebuffer) {
                  XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                  this.this$0.fTempString.length = 0;
                } 
                return (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null && XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer)) ? 6 : 4;
              } 
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
              this.this$0.fTempString.length = 0;
              if (i == 13) {
                XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)i);
                i = -1;
              } else if (i == 93) {
                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null));
                XMLDocumentFragmentScannerImpl.this.fInScanContent = true;
                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93, null)) {
                  XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']');
                  while (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93, null))
                    XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']'); 
                  if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(62, null))
                    XMLDocumentFragmentScannerImpl.this.reportFatalError("CDEndInContent", null); 
                } 
                i = -1;
                XMLDocumentFragmentScannerImpl.this.fInScanContent = false;
              } 
              while (true) {
                if (i == 60) {
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                  XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                  break;
                } 
                if (i == 38) {
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
                  XMLDocumentFragmentScannerImpl.this.setScannerState(28);
                  break;
                } 
                if (i != -1 && XMLDocumentFragmentScannerImpl.this.isInvalidLiteral(i)) {
                  if (XMLChar.isHighSurrogate(i)) {
                    XMLDocumentFragmentScannerImpl.this.scanSurrogates(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                    break;
                  } 
                  XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(i, 16) });
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                  break;
                } 
                i = XMLDocumentFragmentScannerImpl.this.scanContent(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                if (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  break;
                } 
              } 
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                continue;
              } 
              return (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null && XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer)) ? 6 : 4;
            case 39:
              if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
                XMLDocumentFragmentScannerImpl.this.fEmptyElement = false;
                XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                return (XMLDocumentFragmentScannerImpl.this.fMarkupDepth == 0 && elementDepthIsZeroHook()) ? 2 : 2;
              } 
              if (XMLDocumentFragmentScannerImpl.this.scanEndElement() == 0 && elementDepthIsZeroHook())
                return 2; 
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 2;
            case 27:
              XMLDocumentFragmentScannerImpl.this.scanComment();
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 5;
            case 23:
              XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              XMLDocumentFragmentScannerImpl.this.scanPI(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 3;
            case 35:
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
              } else {
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              } 
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              XMLDocumentFragmentScannerImpl.this.scanCDATASection(XMLDocumentFragmentScannerImpl.this.fContentBuffer, true);
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
                continue;
              } 
              return XMLDocumentFragmentScannerImpl.this.fReportCdataEvent ? 12 : 4;
            case 28:
              XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
              XMLDocumentFragmentScannerImpl.this.foundBuiltInRefs = false;
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
              } else {
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              } 
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE)) {
                XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
                XMLDocumentFragmentScannerImpl.this.fMarkupDepth--;
                if (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  return 4;
                } 
              } else {
                XMLDocumentFragmentScannerImpl.this.scanEntityReference(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                if (XMLDocumentFragmentScannerImpl.this.fScannerState == 41 && !XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  return 4;
                } 
                if (XMLDocumentFragmentScannerImpl.this.fScannerState == 36) {
                  XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                  continue;
                } 
                if (XMLDocumentFragmentScannerImpl.this.fScannerState == 28) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  if (XMLDocumentFragmentScannerImpl.this.fReplaceEntityReferences && XMLDocumentFragmentScannerImpl.this.fEntityStore.isDeclaredEntity(XMLDocumentFragmentScannerImpl.this.fCurrentEntityName))
                    continue; 
                  return 9;
                } 
              } 
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
              continue;
            case 36:
              if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
                if (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                  XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                  XMLDocumentFragmentScannerImpl.this.fStringBuffer.append("xml");
                  if (XMLDocumentFragmentScannerImpl.this.fNamespaces) {
                    while (XMLDocumentFragmentScannerImpl.this.isValidNCName(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar()))
                      XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null)); 
                  } else {
                    while (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar()))
                      XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null)); 
                  } 
                  String str = XMLDocumentFragmentScannerImpl.this.fSymbolTable.addSymbol(this.this$0.fStringBuffer.ch, this.this$0.fStringBuffer.offset, this.this$0.fStringBuffer.length);
                  XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                  XMLDocumentFragmentScannerImpl.this.scanPIData(str, XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                } else {
                  XMLDocumentFragmentScannerImpl.this.scanXMLDeclOrTextDecl(true);
                } 
              } 
              this.this$0.fEntityManager.fCurrentEntity.mayReadChunks = true;
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              continue;
            case 26:
              if (scanRootElementHook()) {
                XMLDocumentFragmentScannerImpl.this.fEmptyElement = true;
                return 1;
              } 
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 1;
            case 40:
              XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
              XMLDocumentFragmentScannerImpl.this.fMarkupDepth--;
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 4;
          } 
          break;
        } 
        throw new XNIException("Scanner State " + XMLDocumentFragmentScannerImpl.this.fScannerState + " not Recognized ");
      } catch (EOFException eOFException) {
        endOfFileHook(eOFException);
        return -1;
      } 
    }
    
    protected boolean scanForDoctypeHook() throws IOException { return false; }
    
    protected boolean elementDepthIsZeroHook() throws IOException { return false; }
    
    protected boolean scanRootElementHook() throws IOException { return false; }
    
    protected void endOfFileHook(EOFException param1EOFException) throws IOException, XNIException {
      if (XMLDocumentFragmentScannerImpl.this.fMarkupDepth != 0)
        XMLDocumentFragmentScannerImpl.this.reportFatalError("PrematureEOF", null); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLDocumentFragmentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */