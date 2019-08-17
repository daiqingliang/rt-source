package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxErrorReporter;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import com.sun.xml.internal.stream.events.EntityDeclarationImpl;
import com.sun.xml.internal.stream.events.NotationDeclarationImpl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderImpl implements XMLStreamReader {
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String READER_IN_DEFINED_STATE = "http://java.sun.com/xml/stream/properties/reader-in-defined-state";
  
  private SymbolTable fSymbolTable = new SymbolTable();
  
  protected XMLDocumentScannerImpl fScanner = new XMLNSDocumentScannerImpl();
  
  protected NamespaceContextWrapper fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
  
  protected XMLEntityManager fEntityManager = new XMLEntityManager();
  
  protected StaxErrorReporter fErrorReporter = new StaxErrorReporter();
  
  protected XMLEntityScanner fEntityScanner = null;
  
  protected XMLInputSource fInputSource = null;
  
  protected PropertyManager fPropertyManager = null;
  
  private int fEventType;
  
  static final boolean DEBUG = false;
  
  private boolean fReuse = true;
  
  private boolean fReaderInDefinedState = true;
  
  private boolean fBindNamespaces = true;
  
  private String fDTDDecl = null;
  
  private String versionStr = null;
  
  public XMLStreamReaderImpl(InputStream paramInputStream, PropertyManager paramPropertyManager) throws XMLStreamException {
    init(paramPropertyManager);
    XMLInputSource xMLInputSource = new XMLInputSource(null, null, null, paramInputStream, null);
    setInputSource(xMLInputSource);
  }
  
  public XMLDocumentScannerImpl getScanner() {
    System.out.println("returning scanner");
    return this.fScanner;
  }
  
  public XMLStreamReaderImpl(String paramString, PropertyManager paramPropertyManager) throws XMLStreamException {
    init(paramPropertyManager);
    XMLInputSource xMLInputSource = new XMLInputSource(null, paramString, null);
    setInputSource(xMLInputSource);
  }
  
  public XMLStreamReaderImpl(InputStream paramInputStream, String paramString, PropertyManager paramPropertyManager) throws XMLStreamException {
    init(paramPropertyManager);
    XMLInputSource xMLInputSource = new XMLInputSource(null, null, null, new BufferedInputStream(paramInputStream), paramString);
    setInputSource(xMLInputSource);
  }
  
  public XMLStreamReaderImpl(Reader paramReader, PropertyManager paramPropertyManager) throws XMLStreamException {
    init(paramPropertyManager);
    XMLInputSource xMLInputSource = new XMLInputSource(null, null, null, new BufferedReader(paramReader), null);
    setInputSource(xMLInputSource);
  }
  
  public XMLStreamReaderImpl(XMLInputSource paramXMLInputSource, PropertyManager paramPropertyManager) throws XMLStreamException {
    init(paramPropertyManager);
    setInputSource(paramXMLInputSource);
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource) throws XMLStreamException {
    this.fReuse = false;
    try {
      this.fScanner.setInputSource(paramXMLInputSource);
      if (this.fReaderInDefinedState) {
        this.fEventType = this.fScanner.next();
        if (this.versionStr == null)
          this.versionStr = getVersion(); 
        if (this.fEventType == 7 && this.versionStr != null && this.versionStr.equals("1.1"))
          switchToXML11Scanner(); 
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } catch (XNIException xNIException) {
      throw new XMLStreamException(xNIException.getMessage(), getLocation(), xNIException.getException());
    } 
  }
  
  void init(PropertyManager paramPropertyManager) throws XMLStreamException {
    this.fPropertyManager = paramPropertyManager;
    paramPropertyManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
    paramPropertyManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    paramPropertyManager.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
    reset();
  }
  
  public boolean canReuse() { return this.fReuse; }
  
  public void reset() {
    this.fReuse = true;
    this.fEventType = 0;
    this.fEntityManager.reset(this.fPropertyManager);
    this.fScanner.reset(this.fPropertyManager);
    this.fDTDDecl = null;
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fReaderInDefinedState = ((Boolean)this.fPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/reader-in-defined-state")).booleanValue();
    this.fBindNamespaces = ((Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue();
    this.versionStr = null;
  }
  
  public void close() { this.fReuse = true; }
  
  public String getCharacterEncodingScheme() { return this.fScanner.getCharacterEncodingScheme(); }
  
  public int getColumnNumber() { return this.fEntityScanner.getColumnNumber(); }
  
  public String getEncoding() { return this.fEntityScanner.getEncoding(); }
  
  public int getEventType() { return this.fEventType; }
  
  public int getLineNumber() { return this.fEntityScanner.getLineNumber(); }
  
  public String getLocalName() {
    if (this.fEventType == 1 || this.fEventType == 2)
      return (this.fScanner.getElementQName()).localpart; 
    if (this.fEventType == 9)
      return this.fScanner.getEntityName(); 
    throw new IllegalStateException("Method getLocalName() cannot be called for " + getEventTypeString(this.fEventType) + " event.");
  }
  
  public String getNamespaceURI() { return (this.fEventType == 1 || this.fEventType == 2) ? (this.fScanner.getElementQName()).uri : null; }
  
  public String getPIData() {
    if (this.fEventType == 3)
      return this.fScanner.getPIData().toString(); 
    throw new IllegalStateException("Current state of the parser is " + getEventTypeString(this.fEventType) + " But Expected state is " + '\003');
  }
  
  public String getPITarget() {
    if (this.fEventType == 3)
      return this.fScanner.getPITarget(); 
    throw new IllegalStateException("Current state of the parser is " + getEventTypeString(this.fEventType) + " But Expected state is " + '\003');
  }
  
  public String getPrefix() {
    if (this.fEventType == 1 || this.fEventType == 2) {
      String str = (this.fScanner.getElementQName()).prefix;
      return (str == null) ? "" : str;
    } 
    return null;
  }
  
  public char[] getTextCharacters() {
    if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6)
      return (this.fScanner.getCharacterData()).ch; 
    throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextCharacters() ");
  }
  
  public int getTextLength() {
    if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6)
      return (this.fScanner.getCharacterData()).length; 
    throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextLength() ");
  }
  
  public int getTextStart() {
    if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6)
      return (this.fScanner.getCharacterData()).offset; 
    throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextStart() ");
  }
  
  public String getValue() { return (this.fEventType == 3) ? this.fScanner.getPIData().toString() : ((this.fEventType == 5) ? this.fScanner.getComment() : ((this.fEventType == 1 || this.fEventType == 2) ? (this.fScanner.getElementQName()).localpart : ((this.fEventType == 4) ? this.fScanner.getCharacterData().toString() : null))); }
  
  public String getVersion() {
    String str = this.fEntityScanner.getXMLVersion();
    return ("1.0".equals(str) && !this.fEntityScanner.xmlVersionSetExplicitly) ? null : str;
  }
  
  public boolean hasAttributes() { return (this.fScanner.getAttributeIterator().getLength() > 0); }
  
  public boolean hasName() { return (this.fEventType == 1 || this.fEventType == 2); }
  
  public boolean hasNext() { return (this.fEventType == -1) ? false : ((this.fEventType != 8)); }
  
  public boolean hasValue() { return (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 9 || this.fEventType == 3 || this.fEventType == 5 || this.fEventType == 4); }
  
  public boolean isEndElement() { return (this.fEventType == 2); }
  
  public boolean isStandalone() { return this.fScanner.isStandAlone(); }
  
  public boolean isStartElement() { return (this.fEventType == 1); }
  
  public boolean isWhiteSpace() {
    if (isCharacters() || this.fEventType == 12) {
      char[] arrayOfChar = getTextCharacters();
      int i = getTextStart();
      int j = i + getTextLength();
      for (int k = i; k < j; k++) {
        if (!XMLChar.isSpace(arrayOfChar[k]))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public int next() {
    if (!hasNext()) {
      if (this.fEventType != -1)
        throw new NoSuchElementException("END_DOCUMENT reached: no more elements on the stream."); 
      throw new XMLStreamException("Error processing input source. The input stream is not complete.");
    } 
    try {
      this.fEventType = this.fScanner.next();
      if (this.versionStr == null)
        this.versionStr = getVersion(); 
      if (this.fEventType == 7 && this.versionStr != null && this.versionStr.equals("1.1"))
        switchToXML11Scanner(); 
      if (this.fEventType == 4 || this.fEventType == 9 || this.fEventType == 3 || this.fEventType == 5 || this.fEventType == 12)
        this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity); 
      return this.fEventType;
    } catch (IOException iOException) {
      this.fScanner;
      if (this.fScanner.fScannerState == 46) {
        Boolean bool = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isValidating");
        if (bool != null && !bool.booleanValue()) {
          this.fEventType = 11;
          this.fScanner;
          this.fScanner.setScannerState(43);
          this.fScanner.setDriver(this.fScanner.fPrologDriver);
          if (this.fDTDDecl == null || this.fDTDDecl.length() == 0)
            this.fDTDDecl = "<!-- Exception scanning External DTD Subset.  True contents of DTD cannot be determined.  Processing will continue as XMLInputFactory.IS_VALIDATING == false. -->"; 
          return 11;
        } 
      } 
      throw new XMLStreamException(iOException.getMessage(), getLocation(), iOException);
    } catch (XNIException xNIException) {
      throw new XMLStreamException(xNIException.getMessage(), getLocation(), xNIException.getException());
    } 
  }
  
  private void switchToXML11Scanner() {
    int i = this.fScanner.fEntityDepth;
    NamespaceContext namespaceContext = this.fScanner.fNamespaceContext;
    this.fScanner = new XML11NSDocumentScannerImpl();
    this.fScanner.reset(this.fPropertyManager);
    this.fScanner.setPropertyManager(this.fPropertyManager);
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fEntityManager.fCurrentEntity.mayReadChunks = true;
    this.fScanner.setScannerState(7);
    this.fScanner.fEntityDepth = i;
    this.fScanner.fNamespaceContext = namespaceContext;
    this.fEventType = this.fScanner.next();
  }
  
  static final String getEventTypeString(int paramInt) {
    switch (paramInt) {
      case 1:
        return "START_ELEMENT";
      case 2:
        return "END_ELEMENT";
      case 3:
        return "PROCESSING_INSTRUCTION";
      case 4:
        return "CHARACTERS";
      case 5:
        return "COMMENT";
      case 7:
        return "START_DOCUMENT";
      case 8:
        return "END_DOCUMENT";
      case 9:
        return "ENTITY_REFERENCE";
      case 10:
        return "ATTRIBUTE";
      case 11:
        return "DTD";
      case 12:
        return "CDATA";
      case 6:
        return "SPACE";
    } 
    return "UNKNOWN_EVENT_TYPE, " + String.valueOf(paramInt);
  }
  
  public int getAttributeCount() {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().getLength(); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeCount()");
  }
  
  public QName getAttributeName(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return convertXNIQNametoJavaxQName(this.fScanner.getAttributeIterator().getQualifiedName(paramInt)); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeName()");
  }
  
  public String getAttributeLocalName(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().getLocalName(paramInt); 
    throw new IllegalStateException();
  }
  
  public String getAttributeNamespace(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().getURI(paramInt); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeNamespace()");
  }
  
  public String getAttributePrefix(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().getPrefix(paramInt); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributePrefix()");
  }
  
  public QName getAttributeQName(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10) {
      String str1 = this.fScanner.getAttributeIterator().getLocalName(paramInt);
      String str2 = this.fScanner.getAttributeIterator().getURI(paramInt);
      return new QName(str2, str1);
    } 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeQName()");
  }
  
  public String getAttributeType(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().getType(paramInt); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeType()");
  }
  
  public String getAttributeValue(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().getValue(paramInt); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
  }
  
  public String getAttributeValue(String paramString1, String paramString2) {
    if (this.fEventType == 1 || this.fEventType == 10) {
      XMLAttributesIteratorImpl xMLAttributesIteratorImpl = this.fScanner.getAttributeIterator();
      return (paramString1 == null) ? xMLAttributesIteratorImpl.getValue(xMLAttributesIteratorImpl.getIndexByLocalName(paramString2)) : this.fScanner.getAttributeIterator().getValue((paramString1.length() == 0) ? null : paramString1, paramString2);
    } 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
  }
  
  public String getElementText() {
    if (getEventType() != 1)
      throw new XMLStreamException("parser must be on START_ELEMENT to read next text", getLocation()); 
    int i = next();
    StringBuffer stringBuffer = new StringBuffer();
    while (i != 2) {
      if (i == 4 || i == 12 || i == 6 || i == 9) {
        stringBuffer.append(getText());
      } else if (i != 3 && i != 5) {
        if (i == 8)
          throw new XMLStreamException("unexpected end of document when reading element text content"); 
        if (i == 1)
          throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", getLocation()); 
        throw new XMLStreamException("Unexpected event type " + i, getLocation());
      } 
      i = next();
    } 
    return stringBuffer.toString();
  }
  
  public Location getLocation() { return new Location() {
        String _systemId = XMLStreamReaderImpl.this.fEntityScanner.getExpandedSystemId();
        
        String _publicId = XMLStreamReaderImpl.this.fEntityScanner.getPublicId();
        
        int _offset = XMLStreamReaderImpl.this.fEntityScanner.getCharacterOffset();
        
        int _columnNumber = XMLStreamReaderImpl.this.fEntityScanner.getColumnNumber();
        
        int _lineNumber = XMLStreamReaderImpl.this.fEntityScanner.getLineNumber();
        
        public String getLocationURI() { return this._systemId; }
        
        public int getCharacterOffset() { return this._offset; }
        
        public int getColumnNumber() { return this._columnNumber; }
        
        public int getLineNumber() { return this._lineNumber; }
        
        public String getPublicId() { return this._publicId; }
        
        public String getSystemId() { return this._systemId; }
        
        public String toString() {
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("Line number = " + getLineNumber());
          stringBuffer.append("\n");
          stringBuffer.append("Column number = " + getColumnNumber());
          stringBuffer.append("\n");
          stringBuffer.append("System Id = " + getSystemId());
          stringBuffer.append("\n");
          stringBuffer.append("Public Id = " + getPublicId());
          stringBuffer.append("\n");
          stringBuffer.append("Location Uri= " + getLocationURI());
          stringBuffer.append("\n");
          stringBuffer.append("CharacterOffset = " + getCharacterOffset());
          stringBuffer.append("\n");
          return stringBuffer.toString();
        }
      }; }
  
  public QName getName() {
    if (this.fEventType == 1 || this.fEventType == 2)
      return convertXNIQNametoJavaxQName(this.fScanner.getElementQName()); 
    throw new IllegalStateException("Illegal to call getName() when event type is " + getEventTypeString(this.fEventType) + ". Valid states are " + getEventTypeString(1) + ", " + getEventTypeString(2));
  }
  
  public NamespaceContext getNamespaceContext() { return this.fNamespaceContextWrapper; }
  
  public int getNamespaceCount() {
    if (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 13)
      return this.fScanner.getNamespaceContext().getDeclaredPrefixCount(); 
    throw new IllegalStateException("Current event state is " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceCount().");
  }
  
  public String getNamespacePrefix(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 13) {
      String str = this.fScanner.getNamespaceContext().getDeclaredPrefixAt(paramInt);
      return str.equals("") ? null : str;
    } 
    throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespacePrefix().");
  }
  
  public String getNamespaceURI(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 13)
      return this.fScanner.getNamespaceContext().getURI(this.fScanner.getNamespaceContext().getDeclaredPrefixAt(paramInt)); 
    throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceURI().");
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    if (this.fPropertyManager != null) {
      this.fPropertyManager;
      if (paramString.equals("javax.xml.stream.notations"))
        return getNotationDecls(); 
      this.fPropertyManager;
      return paramString.equals("javax.xml.stream.entities") ? getEntityDecls() : this.fPropertyManager.getProperty(paramString);
    } 
    return null;
  }
  
  public String getText() {
    if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12 || this.fEventType == 6)
      return this.fScanner.getCharacterData().toString(); 
    if (this.fEventType == 9) {
      String str = this.fScanner.getEntityName();
      if (str != null) {
        if (this.fScanner.foundBuiltInRefs)
          return this.fScanner.getCharacterData().toString(); 
        XMLEntityStorage xMLEntityStorage = this.fEntityManager.getEntityStore();
        Entity entity = xMLEntityStorage.getEntity(str);
        return (entity == null) ? null : (entity.isExternal() ? ((Entity.ExternalEntity)entity).entityLocation.getExpandedSystemId() : ((Entity.InternalEntity)entity).text);
      } 
      return null;
    } 
    if (this.fEventType == 11) {
      if (this.fDTDDecl != null)
        return this.fDTDDecl; 
      XMLStringBuffer xMLStringBuffer = this.fScanner.getDTDDecl();
      this.fDTDDecl = xMLStringBuffer.toString();
      return this.fDTDDecl;
    } 
    throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states" + getEventTypeString(4) + ", " + getEventTypeString(5) + ", " + getEventTypeString(12) + ", " + getEventTypeString(6) + ", " + getEventTypeString(9) + ", " + getEventTypeString(11) + " valid for getText() ");
  }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException {
    if (paramInt != this.fEventType)
      throw new XMLStreamException("Event type " + getEventTypeString(paramInt) + " specified did not match with current parser event " + getEventTypeString(this.fEventType)); 
    if (paramString1 != null && !paramString1.equals(getNamespaceURI()))
      throw new XMLStreamException("Namespace URI " + paramString1 + " specified did not match with current namespace URI"); 
    if (paramString2 != null && !paramString2.equals(getLocalName()))
      throw new XMLStreamException("LocalName " + paramString2 + " specified did not match with current local name"); 
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException {
    if (paramArrayOfChar == null)
      throw new NullPointerException("target char array can't be null"); 
    if (paramInt2 < 0 || paramInt3 < 0 || paramInt1 < 0 || paramInt2 >= paramArrayOfChar.length || paramInt2 + paramInt3 > paramArrayOfChar.length)
      throw new IndexOutOfBoundsException(); 
    int i = 0;
    int j = getTextLength() - paramInt1;
    if (j < 0)
      throw new IndexOutOfBoundsException("sourceStart is greater thannumber of characters associated with this event"); 
    if (j < paramInt3) {
      i = j;
    } else {
      i = paramInt3;
    } 
    System.arraycopy(getTextCharacters(), getTextStart() + paramInt1, paramArrayOfChar, paramInt2, i);
    return i;
  }
  
  public boolean hasText() {
    if (this.fEventType == 4 || this.fEventType == 5 || this.fEventType == 12)
      return ((this.fScanner.getCharacterData()).length > 0); 
    if (this.fEventType == 9) {
      String str = this.fScanner.getEntityName();
      if (str != null) {
        if (this.fScanner.foundBuiltInRefs)
          return true; 
        XMLEntityStorage xMLEntityStorage = this.fEntityManager.getEntityStore();
        Entity entity = xMLEntityStorage.getEntity(str);
        return (entity == null) ? false : (entity.isExternal() ? ((((Entity.ExternalEntity)entity).entityLocation.getExpandedSystemId() != null)) : ((((Entity.InternalEntity)entity).text != null)));
      } 
      return false;
    } 
    return (this.fEventType == 11) ? this.fScanner.fSeenDoctypeDecl : 0;
  }
  
  public boolean isAttributeSpecified(int paramInt) {
    if (this.fEventType == 1 || this.fEventType == 10)
      return this.fScanner.getAttributeIterator().isSpecified(paramInt); 
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for isAttributeSpecified()");
  }
  
  public boolean isCharacters() { return (this.fEventType == 4); }
  
  public int nextTag() {
    int i;
    for (i = next(); (i == 4 && isWhiteSpace()) || (i == 12 && isWhiteSpace()) || i == 6 || i == 3 || i == 5; i = next());
    if (i != 1 && i != 2)
      throw new XMLStreamException("found: " + getEventTypeString(i) + ", expected " + getEventTypeString(1) + " or " + getEventTypeString(2), getLocation()); 
    return i;
  }
  
  public boolean standaloneSet() { return this.fScanner.standaloneSet(); }
  
  public QName convertXNIQNametoJavaxQName(QName paramQName) { return (paramQName == null) ? null : ((paramQName.prefix == null) ? new QName(paramQName.uri, paramQName.localpart) : new QName(paramQName.uri, paramQName.localpart, paramQName.prefix)); }
  
  public String getNamespaceURI(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("prefix cannot be null."); 
    return this.fScanner.getNamespaceContext().getURI(this.fSymbolTable.addSymbol(paramString));
  }
  
  protected void setPropertyManager(PropertyManager paramPropertyManager) throws XMLStreamException {
    this.fPropertyManager = paramPropertyManager;
    this.fScanner.setProperty("stax-properties", paramPropertyManager);
    this.fScanner.setPropertyManager(paramPropertyManager);
  }
  
  protected PropertyManager getPropertyManager() { return this.fPropertyManager; }
  
  static void pr(String paramString) { System.out.println(paramString); }
  
  protected List getEntityDecls() {
    if (this.fEventType == 11) {
      XMLEntityStorage xMLEntityStorage = this.fEntityManager.getEntityStore();
      ArrayList arrayList = null;
      if (xMLEntityStorage.hasEntities()) {
        EntityDeclarationImpl entityDeclarationImpl = null;
        arrayList = new ArrayList(xMLEntityStorage.getEntitySize());
        Enumeration enumeration = xMLEntityStorage.getEntityKeys();
        while (enumeration.hasMoreElements()) {
          String str = (String)enumeration.nextElement();
          Entity entity = xMLEntityStorage.getEntity(str);
          entityDeclarationImpl = new EntityDeclarationImpl();
          entityDeclarationImpl.setEntityName(str);
          if (entity.isExternal()) {
            entityDeclarationImpl.setXMLResourceIdentifier(((Entity.ExternalEntity)entity).entityLocation);
            entityDeclarationImpl.setNotationName(((Entity.ExternalEntity)entity).notation);
          } else {
            entityDeclarationImpl.setEntityReplacementText(((Entity.InternalEntity)entity).text);
          } 
          arrayList.add(entityDeclarationImpl);
        } 
      } 
      return arrayList;
    } 
    return null;
  }
  
  protected List getNotationDecls() {
    if (this.fEventType == 11) {
      if (this.fScanner.fDTDScanner == null)
        return null; 
      DTDGrammar dTDGrammar = ((XMLDTDScannerImpl)this.fScanner.fDTDScanner).getGrammar();
      if (dTDGrammar == null)
        return null; 
      List list = dTDGrammar.getNotationDecls();
      Iterator iterator = list.iterator();
      ArrayList arrayList = new ArrayList();
      while (iterator.hasNext()) {
        XMLNotationDecl xMLNotationDecl = (XMLNotationDecl)iterator.next();
        if (xMLNotationDecl != null)
          arrayList.add(new NotationDeclarationImpl(xMLNotationDecl)); 
      } 
      return arrayList;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLStreamReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */