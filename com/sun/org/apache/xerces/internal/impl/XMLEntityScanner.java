package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;

public class XMLEntityScanner implements XMLLocator {
  protected Entity.ScannedEntity fCurrentEntity = null;
  
  protected int fBufferSize = 8192;
  
  protected XMLEntityManager fEntityManager;
  
  protected XMLSecurityManager fSecurityManager = null;
  
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  
  private static final boolean DEBUG_ENCODINGS = false;
  
  private ArrayList<XMLBufferListener> listeners = new ArrayList();
  
  private static final boolean[] VALID_NAMES = new boolean[127];
  
  private static final boolean DEBUG_BUFFER = false;
  
  private static final boolean DEBUG_SKIP_STRING = false;
  
  private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException() {
      private static final long serialVersionUID = 980337771224675268L;
      
      public Throwable fillInStackTrace() { return this; }
    };
  
  protected SymbolTable fSymbolTable = null;
  
  protected XMLErrorReporter fErrorReporter = null;
  
  int[] whiteSpaceLookup = new int[100];
  
  int whiteSpaceLen = 0;
  
  boolean whiteSpaceInfoNeeded = true;
  
  protected boolean fAllowJavaEncodings;
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected PropertyManager fPropertyManager = null;
  
  boolean isExternal = false;
  
  protected boolean xmlVersionSetExplicitly = false;
  
  boolean detectingVersion = false;
  
  public XMLEntityScanner() {}
  
  public XMLEntityScanner(PropertyManager paramPropertyManager, XMLEntityManager paramXMLEntityManager) {
    this.fEntityManager = paramXMLEntityManager;
    reset(paramPropertyManager);
  }
  
  public final void setBufferSize(int paramInt) { this.fBufferSize = paramInt; }
  
  public void reset(PropertyManager paramPropertyManager) {
    this.fSymbolTable = (SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    resetCommon();
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fAllowJavaEncodings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    resetCommon();
  }
  
  public final void reset(SymbolTable paramSymbolTable, XMLEntityManager paramXMLEntityManager, XMLErrorReporter paramXMLErrorReporter) {
    this.fCurrentEntity = null;
    this.fSymbolTable = paramSymbolTable;
    this.fEntityManager = paramXMLEntityManager;
    this.fErrorReporter = paramXMLErrorReporter;
    this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
    this.fSecurityManager = this.fEntityManager.fSecurityManager;
  }
  
  private void resetCommon() {
    this.fCurrentEntity = null;
    this.whiteSpaceLen = 0;
    this.whiteSpaceInfoNeeded = true;
    this.listeners.clear();
    this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
    this.fSecurityManager = this.fEntityManager.fSecurityManager;
  }
  
  public final String getXMLVersion() { return (this.fCurrentEntity != null) ? this.fCurrentEntity.xmlVersion : null; }
  
  public final void setXMLVersion(String paramString) {
    this.xmlVersionSetExplicitly = true;
    this.fCurrentEntity.xmlVersion = paramString;
  }
  
  public final void setCurrentEntity(Entity.ScannedEntity paramScannedEntity) {
    this.fCurrentEntity = paramScannedEntity;
    if (this.fCurrentEntity != null)
      this.isExternal = this.fCurrentEntity.isExternal(); 
  }
  
  public Entity.ScannedEntity getCurrentEntity() { return this.fCurrentEntity; }
  
  public final String getBaseSystemId() { return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null; }
  
  public void setBaseSystemId(String paramString) {}
  
  public final int getLineNumber() { return (this.fCurrentEntity != null) ? this.fCurrentEntity.lineNumber : -1; }
  
  public void setLineNumber(int paramInt) {}
  
  public final int getColumnNumber() { return (this.fCurrentEntity != null) ? this.fCurrentEntity.columnNumber : -1; }
  
  public void setColumnNumber(int paramInt) {}
  
  public final int getCharacterOffset() { return (this.fCurrentEntity != null) ? (this.fCurrentEntity.fTotalCountTillLastLoad + this.fCurrentEntity.position) : -1; }
  
  public final String getExpandedSystemId() { return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null; }
  
  public void setExpandedSystemId(String paramString) {}
  
  public final String getLiteralSystemId() { return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getLiteralSystemId() : null; }
  
  public void setLiteralSystemId(String paramString) {}
  
  public final String getPublicId() { return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null; }
  
  public void setPublicId(String paramString) {}
  
  public void setVersion(String paramString) { this.fCurrentEntity.version = paramString; }
  
  public String getVersion() { return (this.fCurrentEntity != null) ? this.fCurrentEntity.version : null; }
  
  public final String getEncoding() { return (this.fCurrentEntity != null) ? this.fCurrentEntity.encoding : null; }
  
  public final void setEncoding(String paramString) {
    if (this.fCurrentEntity.stream != null && (this.fCurrentEntity.encoding == null || !this.fCurrentEntity.encoding.equals(paramString))) {
      if (this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.startsWith("UTF-16")) {
        String str = paramString.toUpperCase(Locale.ENGLISH);
        if (str.equals("UTF-16"))
          return; 
        if (str.equals("ISO-10646-UCS-4")) {
          if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
          } else {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
          } 
          return;
        } 
        if (str.equals("ISO-10646-UCS-2")) {
          if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
          } else {
            this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
          } 
          return;
        } 
      } 
      this.fCurrentEntity.reader = createReader(this.fCurrentEntity.stream, paramString, null);
      this.fCurrentEntity.encoding = paramString;
    } 
  }
  
  public final boolean isExternal() { return this.fCurrentEntity.isExternal(); }
  
  public int getChar(int paramInt) throws IOException { return arrangeCapacity(paramInt + 1, false) ? this.fCurrentEntity.ch[this.fCurrentEntity.position + paramInt] : -1; }
  
  public int peekChar() {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    return this.isExternal ? ((c != '\r') ? c : 10) : c;
  }
  
  protected int scanChar(XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
    if (c == '\n' || (c == '\r' && this.isExternal)) {
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = (char)c;
        load(1, false, false);
        i = 0;
      } 
      if (c == '\r' && this.isExternal) {
        if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n')
          this.fCurrentEntity.position--; 
        c = '\n';
      } 
    } 
    this.fCurrentEntity.columnNumber++;
    if (!this.detectingVersion)
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i); 
    return c;
  }
  
  protected String scanNmtoken() {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    boolean bool = false;
    while (true) {
      char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (c < '') {
        bool = VALID_NAMES[c];
      } else {
        bool = XMLChar.isName(c);
      } 
      if (!bool)
        break; 
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        int k = this.fCurrentEntity.position - i;
        invokeListeners(k);
        if (k == this.fCurrentEntity.fBufferSize) {
          char[] arrayOfChar = new char[this.fCurrentEntity.fBufferSize * 2];
          System.arraycopy(this.fCurrentEntity.ch, i, arrayOfChar, 0, k);
          this.fCurrentEntity.ch = arrayOfChar;
          this.fCurrentEntity.fBufferSize *= 2;
        } else {
          System.arraycopy(this.fCurrentEntity.ch, i, this.fCurrentEntity.ch, 0, k);
        } 
        i = 0;
        if (load(k, false, false))
          break; 
      } 
    } 
    int j = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += j;
    String str = null;
    if (j > 0)
      str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, j); 
    return str;
  }
  
  protected String scanName(XMLScanner.NameType paramNameType) throws IOException {
    String str;
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    if (XMLChar.isNameStart(this.fCurrentEntity.ch[i])) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[i];
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
        } 
      } 
      boolean bool = false;
      while (true) {
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (c < '') {
          bool = VALID_NAMES[c];
        } else {
          bool = XMLChar.isName(c);
        } 
        if (!bool)
          break; 
        int k;
        if ((k = checkBeforeLoad(this.fCurrentEntity, i, i)) > 0) {
          i = 0;
          if (load(k, false, false))
            break; 
        } 
      } 
    } 
    int j = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += j;
    if (j > 0) {
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, j);
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, j);
      str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, j);
    } else {
      str = null;
    } 
    return str;
  }
  
  protected boolean scanQName(QName paramQName, XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    if (XMLChar.isNameStart(this.fCurrentEntity.ch[i])) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[i];
        i = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          String str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
          paramQName.setValues(null, str, str, null);
          checkEntityLimit(paramNameType, this.fCurrentEntity, 0, 1);
          return true;
        } 
      } 
      int j = -1;
      boolean bool = false;
      while (true) {
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (c < '') {
          bool = VALID_NAMES[c];
        } else {
          bool = XMLChar.isName(c);
        } 
        if (!bool)
          break; 
        if (c == ':') {
          if (j != -1)
            break; 
          j = this.fCurrentEntity.position;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, j - i);
        } 
        int m;
        if ((m = checkBeforeLoad(this.fCurrentEntity, i, j)) > 0) {
          if (j != -1)
            j -= i; 
          i = 0;
          if (load(m, false, false))
            break; 
        } 
      } 
      int k = this.fCurrentEntity.position - i;
      this.fCurrentEntity.columnNumber += k;
      if (k > 0) {
        String str1 = null;
        String str2 = null;
        String str3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, k);
        if (j != -1) {
          int m = j - i;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, m);
          str1 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, i, m);
          int n = k - m - 1;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, j + 1, n);
          str2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, j + 1, n);
        } else {
          str2 = str3;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, k);
        } 
        paramQName.setValues(str1, str2, str3, null);
        checkEntityLimit(paramNameType, this.fCurrentEntity, i, k);
        return true;
      } 
    } 
    return false;
  }
  
  protected int checkBeforeLoad(Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2) throws IOException {
    int i = 0;
    if (++paramScannedEntity.position == paramScannedEntity.count) {
      i = paramScannedEntity.position - paramInt1;
      int j = i;
      if (paramInt2 != -1) {
        paramInt2 -= paramInt1;
        j = i - paramInt2;
      } else {
        paramInt2 = paramInt1;
      } 
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, paramScannedEntity, paramInt2, j);
      invokeListeners(i);
      if (i == paramScannedEntity.ch.length) {
        char[] arrayOfChar = new char[paramScannedEntity.fBufferSize * 2];
        System.arraycopy(paramScannedEntity.ch, paramInt1, arrayOfChar, 0, i);
        paramScannedEntity.ch = arrayOfChar;
        paramScannedEntity.fBufferSize *= 2;
      } else {
        System.arraycopy(paramScannedEntity.ch, paramInt1, paramScannedEntity.ch, 0, i);
      } 
    } 
    return i;
  }
  
  protected void checkEntityLimit(XMLScanner.NameType paramNameType, Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2) {
    if (paramScannedEntity == null || !paramScannedEntity.isGE)
      return; 
    if (paramNameType != XMLScanner.NameType.REFERENCE)
      checkLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, paramScannedEntity, paramInt1, paramInt2); 
    if (paramNameType == XMLScanner.NameType.ELEMENTSTART || paramNameType == XMLScanner.NameType.ATTRIBUTENAME)
      checkNodeCount(paramScannedEntity); 
  }
  
  protected void checkNodeCount(Entity.ScannedEntity paramScannedEntity) {
    if (paramScannedEntity != null && paramScannedEntity.isGE)
      checkLimit(XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT, paramScannedEntity, 0, 1); 
  }
  
  protected void checkLimit(XMLSecurityManager.Limit paramLimit, Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2) {
    this.fLimitAnalyzer.addValue(paramLimit, paramScannedEntity.name, paramInt2);
    if (this.fSecurityManager.isOverLimit(paramLimit, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      new Object[3][0] = Integer.valueOf(this.fLimitAnalyzer.getValue(paramLimit));
      new Object[3][1] = Integer.valueOf(this.fSecurityManager.getLimit(paramLimit));
      new Object[3][2] = this.fSecurityManager.getStateLiteral(paramLimit);
      new Object[4][0] = paramScannedEntity.name;
      new Object[4][1] = Integer.valueOf(this.fLimitAnalyzer.getValue(paramLimit));
      new Object[4][2] = Integer.valueOf(this.fSecurityManager.getLimit(paramLimit));
      new Object[4][3] = this.fSecurityManager.getStateLiteral(paramLimit);
      Object[] arrayOfObject = (paramLimit == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT) ? new Object[3] : new Object[4];
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", paramLimit.key(), arrayOfObject, (short)2);
    } 
    if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "TotalEntitySizeLimit", new Object[] { Integer.valueOf(this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) }, (short)2);
    } 
  }
  
  protected int scanContent(XMLString paramXMLString) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
      invokeListeners(1);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
    } 
    int i = this.fCurrentEntity.position;
    byte b = this.fCurrentEntity.ch[i];
    int j = 0;
    boolean bool = false;
    if (b == 10 || (b == 13 && this.isExternal)) {
      do {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (b == 13 && this.isExternal) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            checkEntityLimit(null, this.fCurrentEntity, i, j);
            i = 0;
            this.fCurrentEntity.position = j;
            if (load(j, false, true)) {
              bool = true;
              break;
            } 
          } 
          if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
            this.fCurrentEntity.position++;
            i++;
          } else {
            j++;
          } 
        } else if (b == 10) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            checkEntityLimit(null, this.fCurrentEntity, i, j);
            i = 0;
            this.fCurrentEntity.position = j;
            if (load(j, false, true)) {
              bool = true;
              break;
            } 
          } 
        } else {
          this.fCurrentEntity.position--;
          break;
        } 
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      int m;
      for (m = i; m < this.fCurrentEntity.position; m++)
        this.fCurrentEntity.ch[m] = '\n'; 
      m = this.fCurrentEntity.position - i;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
        checkEntityLimit(null, this.fCurrentEntity, i, m);
        paramXMLString.setValues(this.fCurrentEntity.ch, i, m);
        return -1;
      } 
    } 
    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
      b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
      if (!XMLChar.isContent(b)) {
        this.fCurrentEntity.position--;
        break;
      } 
    } 
    int k = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += k - j;
    if (!bool)
      checkEntityLimit(null, this.fCurrentEntity, i, k); 
    paramXMLString.setValues(this.fCurrentEntity.ch, i, k);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
      b = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (b == 13 && this.isExternal)
        b = 10; 
    } else {
      b = -1;
    } 
    return b;
  }
  
  protected int scanLiteral(int paramInt, XMLString paramXMLString, boolean paramBoolean) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
      invokeListeners(1);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
    } 
    int i = this.fCurrentEntity.position;
    byte b = this.fCurrentEntity.ch[i];
    int j = 0;
    if (this.whiteSpaceInfoNeeded)
      this.whiteSpaceLen = 0; 
    if (b == 10 || (b == 13 && this.isExternal)) {
      do {
        b = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (b == 13 && this.isExternal) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            i = 0;
            this.fCurrentEntity.position = j;
            if (load(j, false, true))
              break; 
          } 
          if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
            this.fCurrentEntity.position++;
            i++;
          } else {
            j++;
          } 
        } else if (b == 10) {
          j++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            i = 0;
            this.fCurrentEntity.position = j;
            if (load(j, false, true))
              break; 
          } 
        } else {
          this.fCurrentEntity.position--;
          break;
        } 
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      int m = 0;
      for (m = i; m < this.fCurrentEntity.position; m++) {
        this.fCurrentEntity.ch[m] = '\n';
        storeWhiteSpace(m);
      } 
      int n = this.fCurrentEntity.position - i;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
        paramXMLString.setValues(this.fCurrentEntity.ch, i, n);
        return -1;
      } 
    } 
    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
      b = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if ((b == paramInt && (!this.fCurrentEntity.literal || this.isExternal)) || b == 37 || !XMLChar.isContent(b))
        break; 
      if (this.whiteSpaceInfoNeeded && b == 9)
        storeWhiteSpace(this.fCurrentEntity.position); 
      this.fCurrentEntity.position++;
    } 
    int k = this.fCurrentEntity.position - i;
    this.fCurrentEntity.columnNumber += k - j;
    checkEntityLimit(null, this.fCurrentEntity, i, k);
    if (paramBoolean)
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, i, k); 
    paramXMLString.setValues(this.fCurrentEntity.ch, i, k);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
      b = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (b == paramInt && this.fCurrentEntity.literal)
        byte b1 = -1; 
    } else {
      b = -1;
    } 
    return b;
  }
  
  private void storeWhiteSpace(int paramInt) {
    if (this.whiteSpaceLen >= this.whiteSpaceLookup.length) {
      int[] arrayOfInt = new int[this.whiteSpaceLookup.length + 100];
      System.arraycopy(this.whiteSpaceLookup, 0, arrayOfInt, 0, this.whiteSpaceLookup.length);
      this.whiteSpaceLookup = arrayOfInt;
    } 
    this.whiteSpaceLookup[this.whiteSpaceLen++] = paramInt;
  }
  
  protected boolean scanData(String paramString, XMLStringBuffer paramXMLStringBuffer) throws IOException {
    boolean bool = false;
    int i = paramString.length();
    char c = paramString.charAt(0);
    do {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count)
        load(0, true, false); 
      boolean bool1 = false;
      while (this.fCurrentEntity.position > this.fCurrentEntity.count - i && !bool1) {
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        bool1 = load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false);
        this.fCurrentEntity.position = 0;
        this.fCurrentEntity.startPosition = 0;
      } 
      if (this.fCurrentEntity.position > this.fCurrentEntity.count - i) {
        int n = this.fCurrentEntity.count - this.fCurrentEntity.position;
        checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, this.fCurrentEntity.position, n);
        paramXMLStringBuffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, n);
        this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
        this.fCurrentEntity.position = this.fCurrentEntity.count;
        this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
        load(0, true, false);
        return false;
      } 
      int j = this.fCurrentEntity.position;
      char c1 = this.fCurrentEntity.ch[j];
      int k = 0;
      if (c1 == '\n' || (c1 == '\r' && this.isExternal)) {
        do {
          c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
          if (c1 == '\r' && this.isExternal) {
            k++;
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              j = 0;
              this.fCurrentEntity.position = k;
              if (load(k, false, true))
                break; 
            } 
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
              this.fCurrentEntity.position++;
              j++;
            } else {
              k++;
            } 
          } else if (c1 == '\n') {
            k++;
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              j = 0;
              this.fCurrentEntity.position = k;
              this.fCurrentEntity.count = k;
              if (load(k, false, true))
                break; 
            } 
          } else {
            this.fCurrentEntity.position--;
            break;
          } 
        } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
        int n;
        for (n = j; n < this.fCurrentEntity.position; n++)
          this.fCurrentEntity.ch[n] = '\n'; 
        n = this.fCurrentEntity.position - j;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
          checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, n);
          paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, n);
          return true;
        } 
      } 
      label83: while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
        c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (c1 == c) {
          int n = this.fCurrentEntity.position - 1;
          for (int i1 = 1; i1 < i; i1++) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              this.fCurrentEntity.position -= i1;
              break label83;
            } 
            c1 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (paramString.charAt(i1) != c1) {
              this.fCurrentEntity.position -= i1;
              break;
            } 
          } 
          if (this.fCurrentEntity.position == n + i) {
            bool = true;
            break;
          } 
          continue;
        } 
        if (c1 == '\n' || (this.isExternal && c1 == '\r')) {
          this.fCurrentEntity.position--;
          break;
        } 
        if (XMLChar.isInvalid(c1)) {
          this.fCurrentEntity.position--;
          int n = this.fCurrentEntity.position - j;
          this.fCurrentEntity.columnNumber += n - k;
          checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, n);
          paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, n);
          return true;
        } 
      } 
      int m = this.fCurrentEntity.position - j;
      this.fCurrentEntity.columnNumber += m - k;
      checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, j, m);
      if (bool)
        m -= i; 
      paramXMLStringBuffer.append(this.fCurrentEntity.ch, j, m);
    } while (!bool);
    return !bool;
  }
  
  protected boolean skipChar(int paramInt, XMLScanner.NameType paramNameType) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int i = this.fCurrentEntity.position;
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (c == paramInt) {
      this.fCurrentEntity.position++;
      if (paramInt == 10) {
        this.fCurrentEntity.lineNumber++;
        this.fCurrentEntity.columnNumber = 1;
      } else {
        this.fCurrentEntity.columnNumber++;
      } 
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
      return true;
    } 
    if (paramInt == 10 && c == '\r' && this.isExternal) {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = (char)c;
        load(1, false, false);
      } 
      this.fCurrentEntity.position++;
      if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n')
        this.fCurrentEntity.position++; 
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      checkEntityLimit(paramNameType, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
      return true;
    } 
    return false;
  }
  
  public boolean isSpace(char paramChar) { return (paramChar == ' ' || paramChar == '\n' || paramChar == '\t' || paramChar == '\r'); }
  
  protected boolean skipSpaces() {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    if (this.fCurrentEntity == null)
      return false; 
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    int i = this.fCurrentEntity.position - 1;
    if (XMLChar.isSpace(c)) {
      do {
        boolean bool = false;
        if (c == '\n' || (this.isExternal && c == '\r')) {
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            invokeListeners(1);
            this.fCurrentEntity.ch[0] = (char)c;
            bool = load(1, true, false);
            if (!bool) {
              this.fCurrentEntity.position = 0;
            } else if (this.fCurrentEntity == null) {
              return true;
            } 
          } 
          if (c == '\r' && this.isExternal && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n')
            this.fCurrentEntity.position--; 
        } else {
          this.fCurrentEntity.columnNumber++;
        } 
        checkEntityLimit(null, this.fCurrentEntity, i, this.fCurrentEntity.position - i);
        i = this.fCurrentEntity.position;
        if (!bool)
          this.fCurrentEntity.position++; 
        if (this.fCurrentEntity.position != this.fCurrentEntity.count)
          continue; 
        load(0, true, true);
        if (this.fCurrentEntity == null)
          return true; 
      } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    } 
    return false;
  }
  
  public boolean arrangeCapacity(int paramInt) throws IOException { return arrangeCapacity(paramInt, false); }
  
  public boolean arrangeCapacity(int paramInt, boolean paramBoolean) throws IOException {
    if (this.fCurrentEntity.count - this.fCurrentEntity.position >= paramInt)
      return true; 
    boolean bool = false;
    while (this.fCurrentEntity.count - this.fCurrentEntity.position < paramInt) {
      if (this.fCurrentEntity.ch.length - this.fCurrentEntity.position < paramInt) {
        invokeListeners(0);
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        this.fCurrentEntity.count -= this.fCurrentEntity.position;
        this.fCurrentEntity.position = 0;
      } 
      if (this.fCurrentEntity.count - this.fCurrentEntity.position < paramInt) {
        int i = this.fCurrentEntity.position;
        invokeListeners(i);
        bool = load(this.fCurrentEntity.count, paramBoolean, false);
        this.fCurrentEntity.position = i;
        if (bool)
          break; 
      } 
    } 
    return (this.fCurrentEntity.count - this.fCurrentEntity.position >= paramInt);
  }
  
  protected boolean skipString(String paramString) throws IOException {
    int i = paramString.length();
    if (arrangeCapacity(i, false)) {
      int j = this.fCurrentEntity.position;
      int k = this.fCurrentEntity.position + i - 1;
      int m = i - 1;
      while (paramString.charAt(m--) == this.fCurrentEntity.ch[k]) {
        if (k-- == j) {
          this.fCurrentEntity.position += i;
          this.fCurrentEntity.columnNumber += i;
          if (!this.detectingVersion)
            checkEntityLimit(null, this.fCurrentEntity, j, i); 
          return true;
        } 
      } 
    } 
    return false;
  }
  
  protected boolean skipString(char[] paramArrayOfChar) throws IOException {
    int i = paramArrayOfChar.length;
    if (arrangeCapacity(i, false)) {
      int j = this.fCurrentEntity.position;
      for (byte b = 0; b < i; b++) {
        if (this.fCurrentEntity.ch[j++] != paramArrayOfChar[b])
          return false; 
      } 
      this.fCurrentEntity.position += i;
      this.fCurrentEntity.columnNumber += i;
      if (!this.detectingVersion)
        checkEntityLimit(null, this.fCurrentEntity, j, i); 
      return true;
    } 
    return false;
  }
  
  final boolean load(int paramInt, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramBoolean2)
      invokeListeners(paramInt); 
    this.fCurrentEntity.fTotalCountTillLastLoad += this.fCurrentEntity.fLastCount;
    int i = this.fCurrentEntity.ch.length - paramInt;
    if (!this.fCurrentEntity.mayReadChunks && i > 64)
      i = 64; 
    int j = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, paramInt, i);
    boolean bool = false;
    if (j != -1) {
      if (j != 0) {
        this.fCurrentEntity.fLastCount = j;
        this.fCurrentEntity.count = j + paramInt;
        this.fCurrentEntity.position = paramInt;
      } 
    } else {
      this.fCurrentEntity.count = paramInt;
      this.fCurrentEntity.position = paramInt;
      bool = true;
      if (paramBoolean1) {
        this.fEntityManager.endEntity();
        if (this.fCurrentEntity == null)
          throw END_OF_DOCUMENT_ENTITY; 
        if (this.fCurrentEntity.position == this.fCurrentEntity.count)
          load(0, true, false); 
      } 
    } 
    return bool;
  }
  
  protected Reader createReader(InputStream paramInputStream, String paramString, Boolean paramBoolean) throws IOException {
    if (paramString == null)
      paramString = "UTF-8"; 
    String str1 = paramString.toUpperCase(Locale.ENGLISH);
    if (str1.equals("UTF-8"))
      return new UTF8Reader(paramInputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (str1.equals("US-ASCII"))
      return new ASCIIReader(paramInputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (str1.equals("ISO-10646-UCS-4")) {
      if (paramBoolean != null) {
        boolean bool = paramBoolean.booleanValue();
        return bool ? new UCSReader(paramInputStream, (short)8) : new UCSReader(paramInputStream, (short)4);
      } 
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    } 
    if (str1.equals("ISO-10646-UCS-2")) {
      if (paramBoolean != null) {
        boolean bool = paramBoolean.booleanValue();
        return bool ? new UCSReader(paramInputStream, (short)2) : new UCSReader(paramInputStream, (short)1);
      } 
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    } 
    boolean bool1 = XMLChar.isValidIANAEncoding(paramString);
    boolean bool2 = XMLChar.isValidJavaEncoding(paramString);
    if (!bool1 || (this.fAllowJavaEncodings && !bool2)) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
      paramString = "ISO-8859-1";
    } 
    String str2 = EncodingMap.getIANA2JavaMapping(str1);
    if (str2 == null) {
      if (this.fAllowJavaEncodings) {
        str2 = paramString;
      } else {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
        str2 = "ISO8859_1";
      } 
    } else if (str2.equals("ASCII")) {
      return new ASCIIReader(paramInputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    } 
    return new InputStreamReader(paramInputStream, str2);
  }
  
  protected Object[] getEncodingName(byte[] paramArrayOfByte, int paramInt) {
    if (paramInt < 2)
      return new Object[] { "UTF-8", null }; 
    byte b1 = paramArrayOfByte[0] & 0xFF;
    byte b2 = paramArrayOfByte[1] & 0xFF;
    if (b1 == 254 && b2 == 255)
      return new Object[] { "UTF-16BE", new Boolean(true) }; 
    if (b1 == 255 && b2 == 254)
      return new Object[] { "UTF-16LE", new Boolean(false) }; 
    if (paramInt < 3)
      return new Object[] { "UTF-8", null }; 
    byte b3 = paramArrayOfByte[2] & 0xFF;
    if (b1 == 239 && b2 == 187 && b3 == 191)
      return new Object[] { "UTF-8", null }; 
    if (paramInt < 4)
      return new Object[] { "UTF-8", null }; 
    byte b4 = paramArrayOfByte[3] & 0xFF;
    return (b1 == 0 && b2 == 0 && b3 == 0 && b4 == 60) ? new Object[] { "ISO-10646-UCS-4", new Boolean(true) } : ((b1 == 60 && b2 == 0 && b3 == 0 && b4 == 0) ? new Object[] { "ISO-10646-UCS-4", new Boolean(false) } : ((b1 == 0 && b2 == 0 && b3 == 60 && b4 == 0) ? new Object[] { "ISO-10646-UCS-4", null } : ((b1 == 0 && b2 == 60 && b3 == 0 && b4 == 0) ? new Object[] { "ISO-10646-UCS-4", null } : ((b1 == 0 && b2 == 60 && b3 == 0 && b4 == 63) ? new Object[] { "UTF-16BE", new Boolean(true) } : ((b1 == 60 && b2 == 0 && b3 == 63 && b4 == 0) ? new Object[] { "UTF-16LE", new Boolean(false) } : ((b1 == 76 && b2 == 111 && b3 == 167 && b4 == 148) ? new Object[] { "CP037", null } : new Object[] { "UTF-8", null }))))));
  }
  
  final void print() {}
  
  public void registerListener(XMLBufferListener paramXMLBufferListener) {
    if (!this.listeners.contains(paramXMLBufferListener))
      this.listeners.add(paramXMLBufferListener); 
  }
  
  public void invokeListeners(int paramInt) {
    for (byte b = 0; b < this.listeners.size(); b++)
      ((XMLBufferListener)this.listeners.get(b)).refresh(paramInt); 
  }
  
  protected final boolean skipDeclSpaces() {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, false); 
    char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (XMLChar.isSpace(c)) {
      boolean bool = this.fCurrentEntity.isExternal();
      do {
        boolean bool1 = false;
        if (c == '\n' || (bool && c == '\r')) {
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = (char)c;
            bool1 = load(1, true, false);
            if (!bool1)
              this.fCurrentEntity.position = 0; 
          } 
          if (c == '\r' && bool && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n')
            this.fCurrentEntity.position--; 
        } else {
          this.fCurrentEntity.columnNumber++;
        } 
        if (!bool1)
          this.fCurrentEntity.position++; 
        if (this.fCurrentEntity.position != this.fCurrentEntity.count)
          continue; 
        load(0, true, false);
      } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    } 
    return false;
  }
  
  static  {
    byte b;
    for (b = 65; b <= 90; b++)
      VALID_NAMES[b] = true; 
    for (b = 97; b <= 122; b++)
      VALID_NAMES[b] = true; 
    for (b = 48; b <= 57; b++)
      VALID_NAMES[b] = true; 
    VALID_NAMES[45] = true;
    VALID_NAMES[46] = true;
    VALID_NAMES[58] = true;
    VALID_NAMES[95] = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */