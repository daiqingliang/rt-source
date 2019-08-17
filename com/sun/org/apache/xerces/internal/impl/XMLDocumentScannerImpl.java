package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;

public class XMLDocumentScannerImpl extends XMLDocumentFragmentScannerImpl {
  protected static final int SCANNER_STATE_XML_DECL = 42;
  
  protected static final int SCANNER_STATE_PROLOG = 43;
  
  protected static final int SCANNER_STATE_TRAILING_MISC = 44;
  
  protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 45;
  
  protected static final int SCANNER_STATE_DTD_EXTERNAL = 46;
  
  protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 47;
  
  protected static final int SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION = 48;
  
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
  
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl" };
  
  private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager" };
  
  private static final Object[] PROPERTY_DEFAULTS = { null, null };
  
  protected XMLDTDScanner fDTDScanner = null;
  
  protected ValidationManager fValidationManager;
  
  protected XMLStringBuffer fDTDDecl = null;
  
  protected boolean fReadingDTD = false;
  
  protected boolean fAddedListener = false;
  
  protected String fDoctypeName;
  
  protected String fDoctypePublicId;
  
  protected String fDoctypeSystemId;
  
  protected NamespaceContext fNamespaceContext = new NamespaceSupport();
  
  protected boolean fLoadExternalDTD = true;
  
  protected boolean fSeenDoctypeDecl;
  
  protected boolean fScanEndElement;
  
  protected XMLDocumentFragmentScannerImpl.Driver fXMLDeclDriver = new XMLDeclDriver();
  
  protected XMLDocumentFragmentScannerImpl.Driver fPrologDriver = new PrologDriver();
  
  protected XMLDocumentFragmentScannerImpl.Driver fDTDDriver = null;
  
  protected XMLDocumentFragmentScannerImpl.Driver fTrailingMiscDriver = new TrailingMiscDriver();
  
  protected int fStartPos = 0;
  
  protected int fEndPos = 0;
  
  protected boolean fSeenInternalSubset = false;
  
  private String[] fStrings = new String[3];
  
  private XMLInputSource fExternalSubsetSource = null;
  
  private final XMLDTDDescription fDTDDescription = new XMLDTDDescription(null, null, null, null, null);
  
  private static final char[] DOCTYPE = { 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };
  
  private static final char[] COMMENTSTRING = { '-', '-' };
  
  public void setInputSource(XMLInputSource paramXMLInputSource) throws IOException {
    this.fEntityManager.setEntityHandler(this);
    this.fEntityManager.startDocumentEntity(paramXMLInputSource);
    setScannerState(7);
  }
  
  public int getScannetState() { return this.fScannerState; }
  
  public void reset(PropertyManager paramPropertyManager) {
    super.reset(paramPropertyManager);
    this.fDoctypeName = null;
    this.fDoctypePublicId = null;
    this.fDoctypeSystemId = null;
    this.fSeenDoctypeDecl = false;
    this.fNamespaceContext.reset();
    this.fSupportDTD = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
    this.fLoadExternalDTD = !((Boolean)paramPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue();
    setScannerState(7);
    setDriver(this.fXMLDeclDriver);
    this.fSeenInternalSubset = false;
    if (this.fDTDScanner != null)
      ((XMLDTDScannerImpl)this.fDTDScanner).reset(paramPropertyManager); 
    this.fEndPos = 0;
    this.fStartPos = 0;
    if (this.fDTDDecl != null)
      this.fDTDDecl.clear(); 
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    super.reset(paramXMLComponentManager);
    this.fDoctypeName = null;
    this.fDoctypePublicId = null;
    this.fDoctypeSystemId = null;
    this.fSeenDoctypeDecl = false;
    this.fExternalSubsetSource = null;
    this.fLoadExternalDTD = paramXMLComponentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    this.fDisallowDoctype = paramXMLComponentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
    this.fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fSeenInternalSubset = false;
    this.fDTDScanner = (XMLDTDScanner)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner");
    this.fValidationManager = (ValidationManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null);
    try {
      this.fNamespaceContext = (NamespaceContext)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
    } catch (XMLConfigurationException xMLConfigurationException) {}
    if (this.fNamespaceContext == null)
      this.fNamespaceContext = new NamespaceSupport(); 
    this.fNamespaceContext.reset();
    this.fEndPos = 0;
    this.fStartPos = 0;
    if (this.fDTDDecl != null)
      this.fDTDDecl.clear(); 
    setScannerState(42);
    setDriver(this.fXMLDeclDriver);
  }
  
  public String[] getRecognizedFeatures() {
    String[] arrayOfString1 = super.getRecognizedFeatures();
    int i = (arrayOfString1 != null) ? arrayOfString1.length : 0;
    String[] arrayOfString2 = new String[i + RECOGNIZED_FEATURES.length];
    if (arrayOfString1 != null)
      System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, arrayOfString1.length); 
    System.arraycopy(RECOGNIZED_FEATURES, 0, arrayOfString2, i, RECOGNIZED_FEATURES.length);
    return arrayOfString2;
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    super.setFeature(paramString, paramBoolean);
    if (paramString.startsWith("http://apache.org/xml/features/")) {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if (i == "nonvalidating/load-external-dtd".length() && paramString.endsWith("nonvalidating/load-external-dtd")) {
        this.fLoadExternalDTD = paramBoolean;
        return;
      } 
      if (i == "disallow-doctype-decl".length() && paramString.endsWith("disallow-doctype-decl")) {
        this.fDisallowDoctype = paramBoolean;
        return;
      } 
    } 
  }
  
  public String[] getRecognizedProperties() {
    String[] arrayOfString1 = super.getRecognizedProperties();
    int i = (arrayOfString1 != null) ? arrayOfString1.length : 0;
    String[] arrayOfString2 = new String[i + RECOGNIZED_PROPERTIES.length];
    if (arrayOfString1 != null)
      System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, arrayOfString1.length); 
    System.arraycopy(RECOGNIZED_PROPERTIES, 0, arrayOfString2, i, RECOGNIZED_PROPERTIES.length);
    return arrayOfString2;
  }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    super.setProperty(paramString, paramObject);
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if (i == "internal/dtd-scanner".length() && paramString.endsWith("internal/dtd-scanner"))
        this.fDTDScanner = (XMLDTDScanner)paramObject; 
      if (i == "internal/namespace-context".length() && paramString.endsWith("internal/namespace-context") && paramObject != null)
        this.fNamespaceContext = (NamespaceContext)paramObject; 
      return;
    } 
  }
  
  public Boolean getFeatureDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_FEATURES.length; b++) {
      if (RECOGNIZED_FEATURES[b].equals(paramString))
        return FEATURE_DEFAULTS[b]; 
    } 
    return super.getFeatureDefault(paramString);
  }
  
  public Object getPropertyDefault(String paramString) {
    for (byte b = 0; b < RECOGNIZED_PROPERTIES.length; b++) {
      if (RECOGNIZED_PROPERTIES[b].equals(paramString))
        return PROPERTY_DEFAULTS[b]; 
    } 
    return super.getPropertyDefault(paramString);
  }
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    super.startEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    this.fEntityScanner.registerListener(this);
    if (!paramString1.equals("[xml]") && this.fEntityScanner.isExternal() && (paramAugmentations == null || !((Boolean)paramAugmentations.getItem("ENTITY_SKIPPED")).booleanValue()))
      setScannerState(36); 
    if (this.fDocumentHandler != null && paramString1.equals("[xml]"))
      this.fDocumentHandler.startDocument(this.fEntityScanner, paramString2, this.fNamespaceContext, null); 
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations) throws IOException, XNIException {
    super.endEntity(paramString, paramAugmentations);
    if (paramString.equals("[xml]"))
      if (this.fMarkupDepth == 0 && this.fDriver == this.fTrailingMiscDriver) {
        setScannerState(34);
      } else {
        throw new EOFException();
      }  
  }
  
  public XMLStringBuffer getDTDDecl() {
    Entity.ScannedEntity scannedEntity = this.fEntityScanner.getCurrentEntity();
    this.fDTDDecl.append(((Entity.ScannedEntity)scannedEntity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
    if (this.fSeenInternalSubset)
      this.fDTDDecl.append("]>"); 
    return this.fDTDDecl;
  }
  
  public String getCharacterEncodingScheme() { return this.fDeclaredEncoding; }
  
  public int next() { return this.fDriver.next(); }
  
  public NamespaceContext getNamespaceContext() { return this.fNamespaceContext; }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver() { return new ContentDriver(); }
  
  protected boolean scanDoctypeDecl(boolean paramBoolean) throws IOException, XNIException {
    if (!this.fEntityScanner.skipSpaces())
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null); 
    this.fDoctypeName = this.fEntityScanner.scanName(XMLScanner.NameType.DOCTYPE);
    if (this.fDoctypeName == null)
      reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null); 
    if (this.fEntityScanner.skipSpaces()) {
      scanExternalID(this.fStrings, false);
      this.fDoctypeSystemId = this.fStrings[0];
      this.fDoctypePublicId = this.fStrings[1];
      this.fEntityScanner.skipSpaces();
    } 
    this.fHasExternalDTD = (this.fDoctypeSystemId != null);
    if (paramBoolean && !this.fHasExternalDTD && this.fExternalSubsetResolver != null) {
      this.fDTDDescription.setValues(null, null, this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
      this.fDTDDescription.setRootName(this.fDoctypeName);
      this.fExternalSubsetSource = this.fExternalSubsetResolver.getExternalSubset(this.fDTDDescription);
      this.fHasExternalDTD = (this.fExternalSubsetSource != null);
    } 
    if (paramBoolean && this.fDocumentHandler != null)
      if (this.fExternalSubsetSource == null) {
        this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fDoctypePublicId, this.fDoctypeSystemId, null);
      } else {
        this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fExternalSubsetSource.getPublicId(), this.fExternalSubsetSource.getSystemId(), null);
      }  
    boolean bool = true;
    if (!this.fEntityScanner.skipChar(91, null)) {
      bool = false;
      this.fEntityScanner.skipSpaces();
      if (!this.fEntityScanner.skipChar(62, null))
        reportFatalError("DoctypedeclUnterminated", new Object[] { this.fDoctypeName }); 
      this.fMarkupDepth--;
    } 
    return bool;
  }
  
  protected void setEndDTDScanState() {
    setScannerState(43);
    setDriver(this.fPrologDriver);
    this.fEntityManager.setEntityHandler(this);
    this.fReadingDTD = false;
  }
  
  protected String getScannerStateName(int paramInt) {
    switch (paramInt) {
      case 42:
        return "SCANNER_STATE_XML_DECL";
      case 43:
        return "SCANNER_STATE_PROLOG";
      case 44:
        return "SCANNER_STATE_TRAILING_MISC";
      case 45:
        return "SCANNER_STATE_DTD_INTERNAL_DECLS";
      case 46:
        return "SCANNER_STATE_DTD_EXTERNAL";
      case 47:
        return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
    } 
    return super.getScannerStateName(paramInt);
  }
  
  public void refresh(int paramInt) {
    super.refresh(paramInt);
    if (this.fReadingDTD) {
      Entity.ScannedEntity scannedEntity = this.fEntityScanner.getCurrentEntity();
      if (scannedEntity instanceof Entity.ScannedEntity)
        this.fEndPos = ((Entity.ScannedEntity)scannedEntity).position; 
      this.fDTDDecl.append(((Entity.ScannedEntity)scannedEntity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
      this.fStartPos = paramInt;
    } 
  }
  
  protected class ContentDriver extends XMLDocumentFragmentScannerImpl.FragmentContentDriver {
    protected ContentDriver() { super(XMLDocumentScannerImpl.this); }
    
    protected boolean scanForDoctypeHook() throws IOException, XNIException {
      if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(DOCTYPE)) {
        XMLDocumentScannerImpl.this.setScannerState(24);
        return true;
      } 
      return false;
    }
    
    protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
      XMLDocumentScannerImpl.this.setScannerState(44);
      XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
      return true;
    }
    
    protected boolean scanRootElementHook() throws IOException, XNIException {
      if (XMLDocumentScannerImpl.this.scanStartElement()) {
        XMLDocumentScannerImpl.this.setScannerState(44);
        XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
        return true;
      } 
      return false;
    }
    
    protected void endOfFileHook(EOFException param1EOFException) throws IOException, XNIException { XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null); }
    
    protected void resolveExternalSubsetAndRead() {
      XMLDocumentScannerImpl.this.fDTDDescription.setValues(null, null, XMLDocumentScannerImpl.this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
      XMLDocumentScannerImpl.this.fDTDDescription.setRootName(this.this$0.fElementQName.rawname);
      XMLInputSource xMLInputSource = XMLDocumentScannerImpl.this.fExternalSubsetResolver.getExternalSubset(XMLDocumentScannerImpl.this.fDTDDescription);
      if (xMLInputSource != null) {
        XMLDocumentScannerImpl.this.fDoctypeName = this.this$0.fElementQName.rawname;
        XMLDocumentScannerImpl.this.fDoctypePublicId = xMLInputSource.getPublicId();
        XMLDocumentScannerImpl.this.fDoctypeSystemId = xMLInputSource.getSystemId();
        if (XMLDocumentScannerImpl.this.fDocumentHandler != null)
          XMLDocumentScannerImpl.this.fDocumentHandler.doctypeDecl(XMLDocumentScannerImpl.this.fDoctypeName, XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null); 
        try {
          XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(xMLInputSource);
          while (XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true));
        } finally {
          XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
        } 
      } 
    }
  }
  
  protected final class DTDDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() {
      dispatch(true);
      if (XMLDocumentScannerImpl.this.fPropertyManager != null)
        XMLDocumentScannerImpl.this.dtdGrammarUtil = new DTDGrammarUtil(((XMLDTDScannerImpl)XMLDocumentScannerImpl.this.fDTDScanner).getGrammar(), XMLDocumentScannerImpl.this.fSymbolTable, XMLDocumentScannerImpl.this.fNamespaceContext); 
      return 11;
    }
    
    public boolean dispatch(boolean param1Boolean) throws IOException, XNIException {
      XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(null);
      try {
        boolean bool;
        XMLResourceIdentifierImpl xMLResourceIdentifierImpl = new XMLResourceIdentifierImpl();
        if (XMLDocumentScannerImpl.this.fDTDScanner == null) {
          if (XMLDocumentScannerImpl.this.fEntityManager.getEntityScanner() instanceof XML11EntityScanner) {
            XMLDocumentScannerImpl.this.fDTDScanner = new XML11DTDScannerImpl();
          } else {
            XMLDocumentScannerImpl.this.fDTDScanner = new XMLDTDScannerImpl();
          } 
          ((XMLDTDScannerImpl)XMLDocumentScannerImpl.this.fDTDScanner).reset(XMLDocumentScannerImpl.this.fPropertyManager);
        } 
        XMLDocumentScannerImpl.this.fDTDScanner.setLimitAnalyzer(XMLDocumentScannerImpl.this.fLimitAnalyzer);
        do {
          Entity.ScannedEntity scannedEntity;
          boolean bool2;
          StaxXMLInputSource staxXMLInputSource;
          XMLInputSource xMLInputSource;
          boolean bool1;
          bool = false;
          switch (XMLDocumentScannerImpl.this.fScannerState) {
            case 45:
              bool1 = false;
              if (!XMLDocumentScannerImpl.this.fDTDScanner.skipDTD(XMLDocumentScannerImpl.this.fSupportDTD)) {
                boolean bool3 = true;
                bool1 = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDInternalSubset(bool3, XMLDocumentScannerImpl.this.fStandalone, (XMLDocumentScannerImpl.this.fHasExternalDTD && XMLDocumentScannerImpl.this.fLoadExternalDTD));
              } 
              scannedEntity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
              if (scannedEntity instanceof Entity.ScannedEntity)
                XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)scannedEntity).position; 
              XMLDocumentScannerImpl.this.fReadingDTD = false;
              if (!bool1) {
                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(93, null))
                  XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclNotClosed", new Object[] { XMLDocumentScannerImpl.this.fDoctypeName }); 
                XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(62, null))
                  XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclUnterminated", new Object[] { XMLDocumentScannerImpl.this.fDoctypeName }); 
                XMLDocumentScannerImpl.this.fMarkupDepth--;
                if (!XMLDocumentScannerImpl.this.fSupportDTD) {
                  XMLDocumentScannerImpl.this.fEntityStore = XMLDocumentScannerImpl.this.fEntityManager.getEntityStore();
                  XMLDocumentScannerImpl.this.fEntityStore.reset();
                } else if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null && (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD)) {
                  XMLDocumentScannerImpl.this.setScannerState(46);
                  break;
                } 
                XMLDocumentScannerImpl.this.setEndDTDScanState();
                return true;
              } 
              break;
            case 46:
              xMLResourceIdentifierImpl.setValues(XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null, null);
              xMLInputSource = null;
              staxXMLInputSource = XMLDocumentScannerImpl.this.fEntityManager.resolveEntityAsPerStax(xMLResourceIdentifierImpl);
              if (!staxXMLInputSource.hasResolver()) {
                String str = XMLDocumentScannerImpl.this.checkAccess(XMLDocumentScannerImpl.this.fDoctypeSystemId, XMLDocumentScannerImpl.this.fAccessExternalDTD);
                if (str != null)
                  XMLDocumentScannerImpl.this.reportFatalError("AccessExternalDTD", new Object[] { SecuritySupport.sanitizePath(XMLDocumentScannerImpl.this.fDoctypeSystemId), str }); 
              } 
              xMLInputSource = staxXMLInputSource.getXMLInputSource();
              XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(xMLInputSource);
              if (this.this$0.fEntityScanner.fCurrentEntity != null) {
                XMLDocumentScannerImpl.this.setScannerState(47);
              } else {
                XMLDocumentScannerImpl.this.setScannerState(43);
              } 
              bool = true;
              break;
            case 47:
              null = true;
              bool2 = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(null);
              if (!bool2) {
                XMLDocumentScannerImpl.this.setEndDTDScanState();
                return true;
              } 
              break;
            case 43:
              XMLDocumentScannerImpl.this.setEndDTDScanState();
              return true;
            default:
              throw new XNIException("DTDDriver#dispatch: scanner state=" + XMLDocumentScannerImpl.this.fScannerState + " (" + XMLDocumentScannerImpl.this.getScannerStateName(XMLDocumentScannerImpl.this.fScannerState) + ')');
          } 
        } while (param1Boolean || bool);
      } catch (EOFException eOFException) {
        eOFException.printStackTrace();
        XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        return false;
      } finally {
        XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
      } 
      return true;
    }
  }
  
  protected final class PrologDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() {
      try {
        do {
          switch (XMLDocumentScannerImpl.this.fScannerState) {
            case 43:
              XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                XMLDocumentScannerImpl.this.setScannerState(21);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE)) {
                XMLDocumentScannerImpl.this.setScannerState(28);
                break;
              } 
              XMLDocumentScannerImpl.this.setScannerState(22);
              break;
            case 21:
              XMLDocumentScannerImpl.this.fMarkupDepth++;
              if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar()) || XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                XMLDocumentScannerImpl.this.setScannerState(26);
                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                return XMLDocumentScannerImpl.this.fContentDriver.next();
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33, null)) {
                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45, null)) {
                  if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45, null))
                    XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null); 
                  XMLDocumentScannerImpl.this.setScannerState(27);
                  break;
                } 
                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(DOCTYPE)) {
                  XMLDocumentScannerImpl.this.setScannerState(24);
                  Entity.ScannedEntity scannedEntity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
                  if (scannedEntity instanceof Entity.ScannedEntity)
                    XMLDocumentScannerImpl.this.fStartPos = ((Entity.ScannedEntity)scannedEntity).position; 
                  XMLDocumentScannerImpl.this.fReadingDTD = true;
                  if (XMLDocumentScannerImpl.this.fDTDDecl == null)
                    XMLDocumentScannerImpl.this.fDTDDecl = new XMLStringBuffer(); 
                  XMLDocumentScannerImpl.this.fDTDDecl.append("<!DOCTYPE");
                  break;
                } 
                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63, null)) {
                XMLDocumentScannerImpl.this.setScannerState(23);
                break;
              } 
              XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
              break;
          } 
        } while (XMLDocumentScannerImpl.this.fScannerState == 43 || XMLDocumentScannerImpl.this.fScannerState == 21);
        switch (XMLDocumentScannerImpl.this.fScannerState) {
          case 27:
            XMLDocumentScannerImpl.this.scanComment();
            XMLDocumentScannerImpl.this.setScannerState(43);
            return 5;
          case 23:
            XMLDocumentScannerImpl.this.fContentBuffer.clear();
            XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
            XMLDocumentScannerImpl.this.setScannerState(43);
            return 3;
          case 24:
            if (XMLDocumentScannerImpl.this.fDisallowDoctype)
              XMLDocumentScannerImpl.this.reportFatalError("DoctypeNotAllowed", null); 
            if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl)
              XMLDocumentScannerImpl.this.reportFatalError("AlreadySeenDoctype", null); 
            XMLDocumentScannerImpl.this.fSeenDoctypeDecl = true;
            if (XMLDocumentScannerImpl.this.scanDoctypeDecl(XMLDocumentScannerImpl.this.fSupportDTD)) {
              XMLDocumentScannerImpl.this.setScannerState(45);
              XMLDocumentScannerImpl.this.fSeenInternalSubset = true;
              if (XMLDocumentScannerImpl.this.fDTDDriver == null)
                XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this); 
              XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
              return XMLDocumentScannerImpl.this.fDTDDriver.next();
            } 
            if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
              Entity.ScannedEntity scannedEntity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
              if (scannedEntity instanceof Entity.ScannedEntity)
                XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)scannedEntity).position; 
              XMLDocumentScannerImpl.this.fReadingDTD = false;
            } 
            if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
              if ((XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                if (XMLDocumentScannerImpl.this.fSupportDTD) {
                  XMLDocumentScannerImpl.this.setScannerState(46);
                } else {
                  XMLDocumentScannerImpl.this.setScannerState(43);
                } 
                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                if (XMLDocumentScannerImpl.this.fDTDDriver == null)
                  XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this); 
                return XMLDocumentScannerImpl.this.fDTDDriver.next();
              } 
            } else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null && (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
              XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
              XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
              if (XMLDocumentScannerImpl.this.fSupportDTD) {
                XMLDocumentScannerImpl.this.setScannerState(47);
              } else {
                XMLDocumentScannerImpl.this.setScannerState(43);
              } 
              XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
              if (XMLDocumentScannerImpl.this.fDTDDriver == null)
                XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this); 
              return XMLDocumentScannerImpl.this.fDTDDriver.next();
            } 
            if (XMLDocumentScannerImpl.this.fDTDScanner != null)
              XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null); 
            XMLDocumentScannerImpl.this.setScannerState(43);
            return 11;
          case 22:
            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInProlog", null);
            XMLDocumentScannerImpl.this.fEntityScanner.scanChar(null);
          case 28:
            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInProlog", null);
            break;
        } 
      } catch (EOFException eOFException) {
        XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        return -1;
      } 
      return -1;
    }
  }
  
  protected final class TrailingMiscDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() {
      if (XMLDocumentScannerImpl.this.fEmptyElement) {
        XMLDocumentScannerImpl.this.fEmptyElement = false;
        return 2;
      } 
      try {
        int i;
        if (XMLDocumentScannerImpl.this.fScannerState == 34)
          return 8; 
        do {
          switch (XMLDocumentScannerImpl.this.fScannerState) {
            case 44:
              XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
              if (XMLDocumentScannerImpl.this.fScannerState == 34)
                return 8; 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                XMLDocumentScannerImpl.this.setScannerState(21);
                break;
              } 
              XMLDocumentScannerImpl.this.setScannerState(22);
              break;
            case 21:
              XMLDocumentScannerImpl.this.fMarkupDepth++;
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63, null)) {
                XMLDocumentScannerImpl.this.setScannerState(23);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33, null)) {
                XMLDocumentScannerImpl.this.setScannerState(27);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(47, null)) {
                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                break;
              } 
              if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar()) || XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                XMLDocumentScannerImpl.this.scanStartElement();
                XMLDocumentScannerImpl.this.setScannerState(22);
                break;
              } 
              XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
              break;
          } 
        } while (XMLDocumentScannerImpl.this.fScannerState == 21 || XMLDocumentScannerImpl.this.fScannerState == 44);
        switch (XMLDocumentScannerImpl.this.fScannerState) {
          case 23:
            XMLDocumentScannerImpl.this.fContentBuffer.clear();
            XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 3;
          case 27:
            if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString(COMMENTSTRING))
              XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null); 
            XMLDocumentScannerImpl.this.scanComment();
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 5;
          case 22:
            i = XMLDocumentScannerImpl.this.fEntityScanner.peekChar();
            if (i == -1) {
              XMLDocumentScannerImpl.this.setScannerState(34);
              return 8;
            } 
            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInTrailingMisc", null);
            XMLDocumentScannerImpl.this.fEntityScanner.scanChar(null);
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 4;
          case 28:
            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInTrailingMisc", null);
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 9;
          case 34:
            XMLDocumentScannerImpl.this.setScannerState(48);
            return 8;
          case 48:
            throw new NoSuchElementException("No more events to be parsed");
        } 
        throw new XNIException("Scanner State " + XMLDocumentScannerImpl.this.fScannerState + " not Recognized ");
      } catch (EOFException eOFException) {
        if (XMLDocumentScannerImpl.this.fMarkupDepth != 0) {
          XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
          return -1;
        } 
        XMLDocumentScannerImpl.this.setScannerState(34);
        return 8;
      } 
    }
  }
  
  protected final class XMLDeclDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() {
      XMLDocumentScannerImpl.this.setScannerState(43);
      XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fPrologDriver);
      try {
        if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.xmlDecl)) {
          XMLDocumentScannerImpl.this.fMarkupDepth++;
          if (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
            XMLDocumentScannerImpl.this.fStringBuffer.clear();
            XMLDocumentScannerImpl.this.fStringBuffer.append("xml");
            while (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar()))
              XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar(null)); 
            String str = XMLDocumentScannerImpl.this.fSymbolTable.addSymbol(this.this$0.fStringBuffer.ch, this.this$0.fStringBuffer.offset, this.this$0.fStringBuffer.length);
            XMLDocumentScannerImpl.this.fContentBuffer.clear();
            XMLDocumentScannerImpl.this.scanPIData(str, XMLDocumentScannerImpl.this.fContentBuffer);
            this.this$0.fEntityManager.fCurrentEntity.mayReadChunks = true;
            return 3;
          } 
          XMLDocumentScannerImpl.this.scanXMLDeclOrTextDecl(false);
          this.this$0.fEntityManager.fCurrentEntity.mayReadChunks = true;
          return 7;
        } 
        this.this$0.fEntityManager.fCurrentEntity.mayReadChunks = true;
        return 7;
      } catch (EOFException eOFException) {
        XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        return -1;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLDocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */