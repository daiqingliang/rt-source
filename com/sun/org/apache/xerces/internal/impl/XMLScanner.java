package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.events.XMLEvent;

public abstract class XMLScanner implements XMLComponent {
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
  
  private boolean fNeedNonNormalizedValue = false;
  
  protected ArrayList<XMLString> attributeValueCache = new ArrayList();
  
  protected ArrayList<XMLStringBuffer> stringBufferCache = new ArrayList();
  
  protected int fStringBufferIndex = 0;
  
  protected boolean fAttributeCacheInitDone = false;
  
  protected int fAttributeCacheUsedCount = 0;
  
  protected boolean fValidation = false;
  
  protected boolean fNamespaces;
  
  protected boolean fNotifyCharRefs = false;
  
  protected boolean fParserSettings = true;
  
  protected PropertyManager fPropertyManager = null;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityManager fEntityManager = null;
  
  protected XMLEntityStorage fEntityStore = null;
  
  protected XMLSecurityManager fSecurityManager = null;
  
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  
  protected XMLEvent fEvent;
  
  protected XMLEntityScanner fEntityScanner = null;
  
  protected int fEntityDepth;
  
  protected String fCharRefLiteral = null;
  
  protected boolean fScanningAttribute;
  
  protected boolean fReportEntity;
  
  protected static final String fVersionSymbol = "version".intern();
  
  protected static final String fEncodingSymbol = "encoding".intern();
  
  protected static final String fStandaloneSymbol = "standalone".intern();
  
  protected static final String fAmpSymbol = "amp".intern();
  
  protected static final String fLtSymbol = "lt".intern();
  
  protected static final String fGtSymbol = "gt".intern();
  
  protected static final String fQuotSymbol = "quot".intern();
  
  protected static final String fAposSymbol = "apos".intern();
  
  private XMLString fString = new XMLString();
  
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  
  private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
  
  protected XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
  
  int initialCacheCount = 6;
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fParserSettings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!this.fParserSettings) {
      init();
      return;
    } 
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityManager = (XMLEntityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    this.fSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
    this.fEntityStore = this.fEntityManager.getEntityStore();
    this.fValidation = paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation", false);
    this.fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fNotifyCharRefs = paramXMLComponentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
    init();
  }
  
  protected void setPropertyManager(PropertyManager paramPropertyManager) { this.fPropertyManager = paramPropertyManager; }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    if (paramString.startsWith("http://apache.org/xml/properties/")) {
      String str = paramString.substring("http://apache.org/xml/properties/".length());
      if (str.equals("internal/symbol-table")) {
        this.fSymbolTable = (SymbolTable)paramObject;
      } else if (str.equals("internal/error-reporter")) {
        this.fErrorReporter = (XMLErrorReporter)paramObject;
      } else if (str.equals("internal/entity-manager")) {
        this.fEntityManager = (XMLEntityManager)paramObject;
      } 
    } 
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
      this.fSecurityManager = (XMLSecurityManager)paramObject; 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    if ("http://xml.org/sax/features/validation".equals(paramString)) {
      this.fValidation = paramBoolean;
    } else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(paramString)) {
      this.fNotifyCharRefs = paramBoolean;
    } 
  }
  
  public boolean getFeature(String paramString) throws XMLConfigurationException {
    if ("http://xml.org/sax/features/validation".equals(paramString))
      return this.fValidation; 
    if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(paramString))
      return this.fNotifyCharRefs; 
    throw new XMLConfigurationException(Status.NOT_RECOGNIZED, paramString);
  }
  
  protected void reset() {
    init();
    this.fValidation = true;
    this.fNotifyCharRefs = false;
  }
  
  public void reset(PropertyManager paramPropertyManager) {
    init();
    this.fSymbolTable = (SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityManager = (XMLEntityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    this.fEntityStore = this.fEntityManager.getEntityStore();
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fSecurityManager = (XMLSecurityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/security-manager");
    this.fValidation = false;
    this.fNotifyCharRefs = false;
  }
  
  protected void scanXMLDeclOrTextDecl(boolean paramBoolean, String[] paramArrayOfString) throws IOException, XNIException {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    boolean bool1 = false;
    boolean bool2 = true;
    byte b1 = 2;
    byte b2 = 3;
    byte b3 = 0;
    boolean bool3 = false;
    boolean bool4 = this.fEntityScanner.skipSpaces();
    Entity.ScannedEntity scannedEntity = this.fEntityManager.getCurrentEntity();
    boolean bool5 = scannedEntity.literal;
    scannedEntity.literal = false;
    while (this.fEntityScanner.peekChar() != 63) {
      bool3 = true;
      String str = scanPseudoAttribute(paramBoolean, this.fString);
      switch (b3) {
        case false:
          if (str.equals(fVersionSymbol)) {
            if (!bool4)
              reportFatalError(paramBoolean ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null); 
            str1 = this.fString.toString();
            b3 = 1;
            if (!versionSupported(str1))
              reportFatalError("VersionNotSupported", new Object[] { str1 }); 
            if (str1.equals("1.1")) {
              Entity.ScannedEntity scannedEntity1 = this.fEntityManager.getTopLevelEntity();
              if (scannedEntity1 != null && (scannedEntity1.version == null || scannedEntity1.version.equals("1.0")))
                reportFatalError("VersionMismatch", null); 
              this.fEntityManager.setScannerVersion((short)2);
            } 
            break;
          } 
          if (str.equals(fEncodingSymbol)) {
            if (!paramBoolean)
              reportFatalError("VersionInfoRequired", null); 
            if (!bool4)
              reportFatalError(paramBoolean ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null); 
            str2 = this.fString.toString();
            b3 = paramBoolean ? 3 : 2;
            break;
          } 
          if (paramBoolean) {
            reportFatalError("EncodingDeclRequired", null);
            break;
          } 
          reportFatalError("VersionInfoRequired", null);
          break;
        case true:
          if (str.equals(fEncodingSymbol)) {
            if (!bool4)
              reportFatalError(paramBoolean ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null); 
            str2 = this.fString.toString();
            b3 = paramBoolean ? 3 : 2;
            break;
          } 
          if (!paramBoolean && str.equals(fStandaloneSymbol)) {
            if (!bool4)
              reportFatalError("SpaceRequiredBeforeStandalone", null); 
            str3 = this.fString.toString();
            b3 = 3;
            if (!str3.equals("yes") && !str3.equals("no"))
              reportFatalError("SDDeclInvalid", new Object[] { str3 }); 
            break;
          } 
          reportFatalError("EncodingDeclRequired", null);
          break;
        case true:
          if (str.equals(fStandaloneSymbol)) {
            if (!bool4)
              reportFatalError("SpaceRequiredBeforeStandalone", null); 
            str3 = this.fString.toString();
            b3 = 3;
            if (!str3.equals("yes") && !str3.equals("no"))
              reportFatalError("SDDeclInvalid", new Object[] { str3 }); 
            break;
          } 
          reportFatalError("SDDeclNameInvalid", null);
          break;
        default:
          reportFatalError("NoMorePseudoAttributes", null);
          break;
      } 
      bool4 = this.fEntityScanner.skipSpaces();
    } 
    if (bool5)
      scannedEntity.literal = true; 
    if (paramBoolean && b3 != 3)
      reportFatalError("MorePseudoAttributes", null); 
    if (paramBoolean) {
      if (!bool3 && str2 == null)
        reportFatalError("EncodingDeclRequired", null); 
    } else if (!bool3 && str1 == null) {
      reportFatalError("VersionInfoRequired", null);
    } 
    if (!this.fEntityScanner.skipChar(63, null))
      reportFatalError("XMLDeclUnterminated", null); 
    if (!this.fEntityScanner.skipChar(62, null))
      reportFatalError("XMLDeclUnterminated", null); 
    paramArrayOfString[0] = str1;
    paramArrayOfString[1] = str2;
    paramArrayOfString[2] = str3;
  }
  
  protected String scanPseudoAttribute(boolean paramBoolean, XMLString paramXMLString) throws IOException, XNIException {
    String str = scanPseudoAttributeName();
    if (str == null)
      reportFatalError("PseudoAttrNameExpected", null); 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(61, null))
      reportFatalError(paramBoolean ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[] { str }); 
    this.fEntityScanner.skipSpaces();
    int i = this.fEntityScanner.peekChar();
    if (i != 39 && i != 34)
      reportFatalError(paramBoolean ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[] { str }); 
    this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
    int j = this.fEntityScanner.scanLiteral(i, paramXMLString, false);
    if (j != i) {
      this.fStringBuffer2.clear();
      do {
        this.fStringBuffer2.append(paramXMLString);
        if (j != -1)
          if (j == 38 || j == 37 || j == 60 || j == 93) {
            this.fStringBuffer2.append((char)this.fEntityScanner.scanChar(NameType.ATTRIBUTE));
          } else if (XMLChar.isHighSurrogate(j)) {
            scanSurrogates(this.fStringBuffer2);
          } else if (isInvalidLiteral(j)) {
            String str1 = paramBoolean ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
            reportFatalError(str1, new Object[] { Integer.toString(j, 16) });
            this.fEntityScanner.scanChar(null);
          }  
        j = this.fEntityScanner.scanLiteral(i, paramXMLString, false);
      } while (j != i);
      this.fStringBuffer2.append(paramXMLString);
      paramXMLString.setValues(this.fStringBuffer2);
    } 
    if (!this.fEntityScanner.skipChar(i, null))
      reportFatalError(paramBoolean ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[] { str }); 
    return str;
  }
  
  private String scanPseudoAttributeName() throws IOException, XNIException {
    int i = this.fEntityScanner.peekChar();
    switch (i) {
      case 118:
        if (this.fEntityScanner.skipString(fVersionSymbol))
          return fVersionSymbol; 
        break;
      case 101:
        if (this.fEntityScanner.skipString(fEncodingSymbol))
          return fEncodingSymbol; 
        break;
      case 115:
        if (this.fEntityScanner.skipString(fStandaloneSymbol))
          return fStandaloneSymbol; 
        break;
    } 
    return null;
  }
  
  protected void scanPI(XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    this.fReportEntity = false;
    String str = this.fEntityScanner.scanName(NameType.PI);
    if (str == null)
      reportFatalError("PITargetRequired", null); 
    scanPIData(str, paramXMLStringBuffer);
    this.fReportEntity = true;
  }
  
  protected void scanPIData(String paramString, XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    if (paramString.length() == 3) {
      char c1 = Character.toLowerCase(paramString.charAt(0));
      char c2 = Character.toLowerCase(paramString.charAt(1));
      char c3 = Character.toLowerCase(paramString.charAt(2));
      if (c1 == 'x' && c2 == 'm' && c3 == 'l')
        reportFatalError("ReservedPITarget", null); 
    } 
    if (!this.fEntityScanner.skipSpaces()) {
      if (this.fEntityScanner.skipString("?>"))
        return; 
      reportFatalError("SpaceRequiredInPI", null);
    } 
    if (this.fEntityScanner.scanData("?>", paramXMLStringBuffer))
      do {
        int i = this.fEntityScanner.peekChar();
        if (i == -1)
          continue; 
        if (XMLChar.isHighSurrogate(i)) {
          scanSurrogates(paramXMLStringBuffer);
        } else if (isInvalidLiteral(i)) {
          reportFatalError("InvalidCharInPI", new Object[] { Integer.toHexString(i) });
          this.fEntityScanner.scanChar(null);
        } 
      } while (this.fEntityScanner.scanData("?>", paramXMLStringBuffer)); 
  }
  
  protected void scanComment(XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    paramXMLStringBuffer.clear();
    while (this.fEntityScanner.scanData("--", paramXMLStringBuffer)) {
      int i = this.fEntityScanner.peekChar();
      if (i != -1) {
        if (XMLChar.isHighSurrogate(i)) {
          scanSurrogates(paramXMLStringBuffer);
          continue;
        } 
        if (isInvalidLiteral(i)) {
          reportFatalError("InvalidCharInComment", new Object[] { Integer.toHexString(i) });
          this.fEntityScanner.scanChar(NameType.COMMENT);
        } 
      } 
    } 
    if (!this.fEntityScanner.skipChar(62, NameType.COMMENT))
      reportFatalError("DashDashInComment", null); 
  }
  
  protected void scanAttributeValue(XMLString paramXMLString1, XMLString paramXMLString2, String paramString1, XMLAttributes paramXMLAttributes, int paramInt, boolean paramBoolean1, String paramString2, boolean paramBoolean2) throws IOException, XNIException {
    XMLStringBuffer xMLStringBuffer = null;
    int i = this.fEntityScanner.peekChar();
    if (i != 39 && i != 34)
      reportFatalError("OpenQuoteExpected", new Object[] { paramString2, paramString1 }); 
    this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
    int j = this.fEntityDepth;
    int k = this.fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
    if (this.fNeedNonNormalizedValue) {
      this.fStringBuffer2.clear();
      this.fStringBuffer2.append(paramXMLString1);
    } 
    if (this.fEntityScanner.whiteSpaceLen > 0)
      normalizeWhitespace(paramXMLString1); 
    if (k != i) {
      this.fScanningAttribute = true;
      xMLStringBuffer = getStringBuffer();
      xMLStringBuffer.clear();
      do {
        xMLStringBuffer.append(paramXMLString1);
        if (k == 38) {
          this.fEntityScanner.skipChar(38, NameType.REFERENCE);
          if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append('&'); 
          if (this.fEntityScanner.skipChar(35, NameType.REFERENCE)) {
            int n;
            if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
              this.fStringBuffer2.append('#'); 
            if (this.fNeedNonNormalizedValue) {
              n = scanCharReferenceValue(xMLStringBuffer, this.fStringBuffer2);
            } else {
              n = scanCharReferenceValue(xMLStringBuffer, null);
            } 
            if (n != -1);
          } else {
            String str = this.fEntityScanner.scanName(NameType.ENTITY);
            if (str == null) {
              reportFatalError("NameRequiredInReference", null);
            } else if (j == this.fEntityDepth && this.fNeedNonNormalizedValue) {
              this.fStringBuffer2.append(str);
            } 
            if (!this.fEntityScanner.skipChar(59, NameType.REFERENCE)) {
              reportFatalError("SemicolonRequiredInReference", new Object[] { str });
            } else if (j == this.fEntityDepth && this.fNeedNonNormalizedValue) {
              this.fStringBuffer2.append(';');
            } 
            if (resolveCharacter(str, xMLStringBuffer)) {
              checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
            } else if (this.fEntityStore.isExternalEntity(str)) {
              reportFatalError("ReferenceToExternalEntity", new Object[] { str });
            } else {
              if (!this.fEntityStore.isDeclaredEntity(str))
                if (paramBoolean1) {
                  if (this.fValidation)
                    this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { str }, (short)1); 
                } else {
                  reportFatalError("EntityNotDeclared", new Object[] { str });
                }  
              this.fEntityManager.startEntity(true, str, true);
            } 
          } 
        } else if (k == 60) {
          reportFatalError("LessthanInAttValue", new Object[] { paramString2, paramString1 });
          this.fEntityScanner.scanChar(null);
          if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append((char)k); 
        } else if (k == 37 || k == 93) {
          this.fEntityScanner.scanChar(null);
          xMLStringBuffer.append((char)k);
          if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append((char)k); 
        } else if (k == 10 || k == 13) {
          this.fEntityScanner.scanChar(null);
          xMLStringBuffer.append(' ');
          if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append('\n'); 
        } else if (k != -1 && XMLChar.isHighSurrogate(k)) {
          this.fStringBuffer3.clear();
          if (scanSurrogates(this.fStringBuffer3)) {
            xMLStringBuffer.append(this.fStringBuffer3);
            if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
              this.fStringBuffer2.append(this.fStringBuffer3); 
          } 
        } else if (k != -1 && isInvalidLiteral(k)) {
          reportFatalError("InvalidCharInAttValue", new Object[] { paramString2, paramString1, Integer.toString(k, 16) });
          this.fEntityScanner.scanChar(null);
          if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append((char)k); 
        } 
        k = this.fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
        if (j == this.fEntityDepth && this.fNeedNonNormalizedValue)
          this.fStringBuffer2.append(paramXMLString1); 
        if (this.fEntityScanner.whiteSpaceLen <= 0)
          continue; 
        normalizeWhitespace(paramXMLString1);
      } while (k != i || j != this.fEntityDepth);
      xMLStringBuffer.append(paramXMLString1);
      paramXMLString1.setValues(xMLStringBuffer);
      this.fScanningAttribute = false;
    } 
    if (this.fNeedNonNormalizedValue)
      paramXMLString2.setValues(this.fStringBuffer2); 
    int m = this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
    if (m != i)
      reportFatalError("CloseQuoteExpected", new Object[] { paramString2, paramString1 }); 
  }
  
  protected boolean resolveCharacter(String paramString, XMLStringBuffer paramXMLStringBuffer) {
    if (paramString == fAmpSymbol) {
      paramXMLStringBuffer.append('&');
      return true;
    } 
    if (paramString == fAposSymbol) {
      paramXMLStringBuffer.append('\'');
      return true;
    } 
    if (paramString == fLtSymbol) {
      paramXMLStringBuffer.append('<');
      return true;
    } 
    if (paramString == fGtSymbol) {
      checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
      paramXMLStringBuffer.append('>');
      return true;
    } 
    if (paramString == fQuotSymbol) {
      checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
      paramXMLStringBuffer.append('"');
      return true;
    } 
    return false;
  }
  
  protected void scanExternalID(String[] paramArrayOfString, boolean paramBoolean) throws IOException, XNIException {
    String str1 = null;
    String str2 = null;
    if (this.fEntityScanner.skipString("PUBLIC")) {
      if (!this.fEntityScanner.skipSpaces())
        reportFatalError("SpaceRequiredAfterPUBLIC", null); 
      scanPubidLiteral(this.fString);
      str2 = this.fString.toString();
      if (!this.fEntityScanner.skipSpaces() && !paramBoolean)
        reportFatalError("SpaceRequiredBetweenPublicAndSystem", null); 
    } 
    if (str2 != null || this.fEntityScanner.skipString("SYSTEM")) {
      if (str2 == null && !this.fEntityScanner.skipSpaces())
        reportFatalError("SpaceRequiredAfterSYSTEM", null); 
      int i = this.fEntityScanner.peekChar();
      if (i != 39 && i != 34) {
        if (str2 != null && paramBoolean) {
          paramArrayOfString[0] = null;
          paramArrayOfString[1] = str2;
          return;
        } 
        reportFatalError("QuoteRequiredInSystemID", null);
      } 
      this.fEntityScanner.scanChar(null);
      XMLString xMLString = this.fString;
      if (this.fEntityScanner.scanLiteral(i, xMLString, false) != i) {
        this.fStringBuffer.clear();
        do {
          this.fStringBuffer.append(xMLString);
          int j = this.fEntityScanner.peekChar();
          if (XMLChar.isMarkup(j) || j == 93) {
            this.fStringBuffer.append((char)this.fEntityScanner.scanChar(null));
          } else if (j != -1 && isInvalidLiteral(j)) {
            reportFatalError("InvalidCharInSystemID", new Object[] { Integer.toString(j, 16) });
          } 
        } while (this.fEntityScanner.scanLiteral(i, xMLString, false) != i);
        this.fStringBuffer.append(xMLString);
        xMLString = this.fStringBuffer;
      } 
      str1 = xMLString.toString();
      if (!this.fEntityScanner.skipChar(i, null))
        reportFatalError("SystemIDUnterminated", null); 
    } 
    paramArrayOfString[0] = str1;
    paramArrayOfString[1] = str2;
  }
  
  protected boolean scanPubidLiteral(XMLString paramXMLString) throws IOException, XNIException {
    int i = this.fEntityScanner.scanChar(null);
    if (i != 39 && i != 34) {
      reportFatalError("QuoteRequiredInPublicID", null);
      return false;
    } 
    this.fStringBuffer.clear();
    boolean bool = true;
    boolean bool1 = true;
    while (true) {
      int j = this.fEntityScanner.scanChar(null);
      if (j == 32 || j == 10 || j == 13) {
        if (!bool) {
          this.fStringBuffer.append(' ');
          bool = true;
        } 
        continue;
      } 
      if (j == i) {
        if (bool)
          this.fStringBuffer.length--; 
        paramXMLString.setValues(this.fStringBuffer);
        break;
      } 
      if (XMLChar.isPubid(j)) {
        this.fStringBuffer.append((char)j);
        bool = false;
        continue;
      } 
      if (j == -1) {
        reportFatalError("PublicIDUnterminated", null);
        return false;
      } 
      bool1 = false;
      reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(j) });
    } 
    return bool1;
  }
  
  protected void normalizeWhitespace(XMLString paramXMLString) {
    byte b = 0;
    int i = 0;
    int[] arrayOfInt = this.fEntityScanner.whiteSpaceLookup;
    int j = this.fEntityScanner.whiteSpaceLen;
    int k = paramXMLString.offset + paramXMLString.length;
    while (b < j) {
      i = arrayOfInt[b];
      if (i < k)
        paramXMLString.ch[i] = ' '; 
      b++;
    } 
  }
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    this.fEntityDepth++;
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fEntityStore = this.fEntityManager.getEntityStore();
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations) throws IOException, XNIException { this.fEntityDepth--; }
  
  protected int scanCharReferenceValue(XMLStringBuffer paramXMLStringBuffer1, XMLStringBuffer paramXMLStringBuffer2) throws IOException, XNIException {
    int i = paramXMLStringBuffer1.length;
    boolean bool = false;
    if (this.fEntityScanner.skipChar(120, NameType.REFERENCE)) {
      if (paramXMLStringBuffer2 != null)
        paramXMLStringBuffer2.append('x'); 
      bool = true;
      this.fStringBuffer3.clear();
      boolean bool1 = true;
      int k = this.fEntityScanner.peekChar();
      bool1 = ((k >= 48 && k <= 57) || (k >= 97 && k <= 102) || (k >= 65 && k <= 70)) ? 1 : 0;
      if (bool1) {
        if (paramXMLStringBuffer2 != null)
          paramXMLStringBuffer2.append((char)k); 
        this.fEntityScanner.scanChar(NameType.REFERENCE);
        this.fStringBuffer3.append((char)k);
        do {
          k = this.fEntityScanner.peekChar();
          bool1 = ((k >= 48 && k <= 57) || (k >= 97 && k <= 102) || (k >= 65 && k <= 70)) ? 1 : 0;
          if (!bool1)
            continue; 
          if (paramXMLStringBuffer2 != null)
            paramXMLStringBuffer2.append((char)k); 
          this.fEntityScanner.scanChar(NameType.REFERENCE);
          this.fStringBuffer3.append((char)k);
        } while (bool1);
      } else {
        reportFatalError("HexdigitRequiredInCharRef", null);
      } 
    } else {
      this.fStringBuffer3.clear();
      boolean bool1 = true;
      int k = this.fEntityScanner.peekChar();
      bool1 = (k >= 48 && k <= 57) ? 1 : 0;
      if (bool1) {
        if (paramXMLStringBuffer2 != null)
          paramXMLStringBuffer2.append((char)k); 
        this.fEntityScanner.scanChar(NameType.REFERENCE);
        this.fStringBuffer3.append((char)k);
        do {
          k = this.fEntityScanner.peekChar();
          bool1 = (k >= 48 && k <= 57) ? 1 : 0;
          if (!bool1)
            continue; 
          if (paramXMLStringBuffer2 != null)
            paramXMLStringBuffer2.append((char)k); 
          this.fEntityScanner.scanChar(NameType.REFERENCE);
          this.fStringBuffer3.append((char)k);
        } while (bool1);
      } else {
        reportFatalError("DigitRequiredInCharRef", null);
      } 
    } 
    if (!this.fEntityScanner.skipChar(59, NameType.REFERENCE))
      reportFatalError("SemicolonRequiredInCharRef", null); 
    if (paramXMLStringBuffer2 != null)
      paramXMLStringBuffer2.append(';'); 
    int j = -1;
    try {
      j = Integer.parseInt(this.fStringBuffer3.toString(), bool ? 16 : 10);
      if (isInvalid(j)) {
        StringBuffer stringBuffer = new StringBuffer(this.fStringBuffer3.length + 1);
        if (bool)
          stringBuffer.append('x'); 
        stringBuffer.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
        reportFatalError("InvalidCharRef", new Object[] { stringBuffer.toString() });
      } 
    } catch (NumberFormatException numberFormatException) {
      StringBuffer stringBuffer = new StringBuffer(this.fStringBuffer3.length + 1);
      if (bool)
        stringBuffer.append('x'); 
      stringBuffer.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
      reportFatalError("InvalidCharRef", new Object[] { stringBuffer.toString() });
    } 
    if (!XMLChar.isSupplemental(j)) {
      paramXMLStringBuffer1.append((char)j);
    } else {
      paramXMLStringBuffer1.append(XMLChar.highSurrogate(j));
      paramXMLStringBuffer1.append(XMLChar.lowSurrogate(j));
    } 
    if (this.fNotifyCharRefs && j != -1) {
      String str = "#" + (bool ? "x" : "") + this.fStringBuffer3.toString();
      if (!this.fScanningAttribute)
        this.fCharRefLiteral = str; 
    } 
    if (this.fEntityScanner.fCurrentEntity.isGE)
      checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, paramXMLStringBuffer1.length - i); 
    return j;
  }
  
  protected boolean isInvalid(int paramInt) { return XMLChar.isInvalid(paramInt); }
  
  protected boolean isInvalidLiteral(int paramInt) { return XMLChar.isInvalid(paramInt); }
  
  protected boolean isValidNameChar(int paramInt) { return XMLChar.isName(paramInt); }
  
  protected boolean isValidNCName(int paramInt) { return XMLChar.isNCName(paramInt); }
  
  protected boolean isValidNameStartChar(int paramInt) { return XMLChar.isNameStart(paramInt); }
  
  protected boolean isValidNameStartHighSurrogate(int paramInt) { return false; }
  
  protected boolean versionSupported(String paramString) throws XMLConfigurationException { return (paramString.equals("1.0") || paramString.equals("1.1")); }
  
  protected boolean scanSurrogates(XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    int i = this.fEntityScanner.scanChar(null);
    int j = this.fEntityScanner.peekChar();
    if (!XMLChar.isLowSurrogate(j)) {
      reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(i, 16) });
      return false;
    } 
    this.fEntityScanner.scanChar(null);
    int k = XMLChar.supplemental((char)i, (char)j);
    if (isInvalid(k)) {
      reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(k, 16) });
      return false;
    } 
    paramXMLStringBuffer.append((char)i);
    paramXMLStringBuffer.append((char)j);
    return true;
  }
  
  protected void reportFatalError(String paramString, Object[] paramArrayOfObject) throws XNIException { this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", paramString, paramArrayOfObject, (short)2); }
  
  private void init() {
    this.fEntityScanner = null;
    this.fEntityDepth = 0;
    this.fReportEntity = true;
    this.fResourceIdentifier.clear();
    if (!this.fAttributeCacheInitDone) {
      for (byte b = 0; b < this.initialCacheCount; b++) {
        this.attributeValueCache.add(new XMLString());
        this.stringBufferCache.add(new XMLStringBuffer());
      } 
      this.fAttributeCacheInitDone = true;
    } 
    this.fStringBufferIndex = 0;
    this.fAttributeCacheUsedCount = 0;
  }
  
  XMLStringBuffer getStringBuffer() {
    if (this.fStringBufferIndex < this.initialCacheCount || this.fStringBufferIndex < this.stringBufferCache.size())
      return (XMLStringBuffer)this.stringBufferCache.get(this.fStringBufferIndex++); 
    XMLStringBuffer xMLStringBuffer = new XMLStringBuffer();
    this.fStringBufferIndex++;
    this.stringBufferCache.add(xMLStringBuffer);
    return xMLStringBuffer;
  }
  
  void checkEntityLimit(boolean paramBoolean, String paramString, XMLString paramXMLString) { checkEntityLimit(paramBoolean, paramString, paramXMLString.length); }
  
  void checkEntityLimit(boolean paramBoolean, String paramString, int paramInt) {
    if (this.fLimitAnalyzer == null)
      this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer; 
    if (paramBoolean) {
      this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, "%" + paramString, paramInt);
      if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
        this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
        reportFatalError("MaxEntitySizeLimit", new Object[] { "%" + paramString, Integer.valueOf(this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)), Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT) });
      } 
    } else {
      this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, paramString, paramInt);
      if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
        this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
        reportFatalError("MaxEntitySizeLimit", new Object[] { paramString, Integer.valueOf(this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)), Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT) });
      } 
    } 
    if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      reportFatalError("TotalEntitySizeLimit", new Object[] { Integer.valueOf(this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) });
    } 
  }
  
  public enum NameType {
    ATTRIBUTE("attribute"),
    ATTRIBUTENAME("attribute name"),
    COMMENT("comment"),
    DOCTYPE("doctype"),
    ELEMENTSTART("startelement"),
    ELEMENTEND("endelement"),
    ENTITY("entity"),
    NOTATION("notation"),
    PI("pi"),
    REFERENCE("reference");
    
    final String literal;
    
    NameType(String param1String1) { this.literal = param1String1; }
    
    String literal() throws IOException, XNIException { return this.literal; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */