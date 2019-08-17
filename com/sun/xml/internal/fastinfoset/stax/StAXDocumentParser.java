package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.OctetBufferListener;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.internal.fastinfoset.sax.AttributesHolder;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXDocumentParser extends Decoder implements XMLStreamReader, FastInfosetStreamReader, OctetBufferListener {
  private static final Logger logger = Logger.getLogger(StAXDocumentParser.class.getName());
  
  protected static final int INTERNAL_STATE_START_DOCUMENT = 0;
  
  protected static final int INTERNAL_STATE_START_ELEMENT_TERMINATE = 1;
  
  protected static final int INTERNAL_STATE_SINGLE_TERMINATE_ELEMENT_WITH_NAMESPACES = 2;
  
  protected static final int INTERNAL_STATE_DOUBLE_TERMINATE_ELEMENT = 3;
  
  protected static final int INTERNAL_STATE_END_DOCUMENT = 4;
  
  protected static final int INTERNAL_STATE_VOID = -1;
  
  protected int _internalState;
  
  protected int _eventType;
  
  protected QualifiedName[] _qNameStack = new QualifiedName[32];
  
  protected int[] _namespaceAIIsStartStack = new int[32];
  
  protected int[] _namespaceAIIsEndStack = new int[32];
  
  protected int _stackCount = -1;
  
  protected String[] _namespaceAIIsPrefix = new String[32];
  
  protected String[] _namespaceAIIsNamespaceName = new String[32];
  
  protected int[] _namespaceAIIsPrefixIndex = new int[32];
  
  protected int _namespaceAIIsIndex;
  
  protected int _currentNamespaceAIIsStart;
  
  protected int _currentNamespaceAIIsEnd;
  
  protected QualifiedName _qualifiedName;
  
  protected AttributesHolder _attributes = new AttributesHolder();
  
  protected boolean _clearAttributes = false;
  
  protected char[] _characters;
  
  protected int _charactersOffset;
  
  protected String _algorithmURI;
  
  protected int _algorithmId;
  
  protected boolean _isAlgorithmDataCloned;
  
  protected byte[] _algorithmData;
  
  protected int _algorithmDataOffset;
  
  protected int _algorithmDataLength;
  
  protected String _piTarget;
  
  protected String _piData;
  
  protected NamespaceContextImpl _nsContext = new NamespaceContextImpl();
  
  protected String _characterEncodingScheme;
  
  protected StAXManager _manager;
  
  private byte[] base64TaleBytes = new byte[3];
  
  private int base64TaleLength;
  
  public StAXDocumentParser() {
    reset();
    this._manager = new StAXManager(1);
  }
  
  public StAXDocumentParser(InputStream paramInputStream) {
    this();
    setInputStream(paramInputStream);
    this._manager = new StAXManager(1);
  }
  
  public StAXDocumentParser(InputStream paramInputStream, StAXManager paramStAXManager) {
    this(paramInputStream);
    this._manager = paramStAXManager;
  }
  
  public void setInputStream(InputStream paramInputStream) {
    super.setInputStream(paramInputStream);
    reset();
  }
  
  public void reset() {
    super.reset();
    if (this._internalState != 0 && this._internalState != 4) {
      for (int i = this._namespaceAIIsIndex - 1; i >= 0; i--)
        this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]); 
      this._stackCount = -1;
      this._namespaceAIIsIndex = 0;
      this._characters = null;
      this._algorithmData = null;
    } 
    this._characterEncodingScheme = "UTF-8";
    this._eventType = 7;
    this._internalState = 0;
  }
  
  protected void resetOnError() {
    super.reset();
    if (this._v != null)
      this._prefixTable.clearCompletely(); 
    this._duplicateAttributeVerifier.clear();
    this._stackCount = -1;
    this._namespaceAIIsIndex = 0;
    this._characters = null;
    this._algorithmData = null;
    this._eventType = 7;
    this._internalState = 0;
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return (this._manager != null) ? this._manager.getProperty(paramString) : null; }
  
  public int next() throws XMLStreamException {
    try {
      int k;
      boolean bool;
      QualifiedName qualifiedName;
      int j;
      if (this._internalState != -1) {
        int m;
        switch (this._internalState) {
          case 0:
            decodeHeader();
            processDII();
            this._internalState = -1;
            break;
          case 1:
            if (this._currentNamespaceAIIsEnd > 0) {
              for (int n = this._currentNamespaceAIIsEnd - 1; n >= this._currentNamespaceAIIsStart; n--)
                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[n]); 
              this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
            } 
            popStack();
            this._internalState = -1;
            return this._eventType = 2;
          case 2:
            for (m = this._currentNamespaceAIIsEnd - 1; m >= this._currentNamespaceAIIsStart; m--)
              this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[m]); 
            this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
            this._internalState = -1;
            break;
          case 3:
            if (this._currentNamespaceAIIsEnd > 0) {
              for (m = this._currentNamespaceAIIsEnd - 1; m >= this._currentNamespaceAIIsStart; m--)
                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[m]); 
              this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
            } 
            if (this._stackCount == -1) {
              this._internalState = 4;
              return this._eventType = 8;
            } 
            popStack();
            this._internalState = (this._currentNamespaceAIIsEnd > 0) ? 2 : -1;
            return this._eventType = 2;
          case 4:
            throw new NoSuchElementException(CommonResourceBundle.getInstance().getString("message.noMoreEvents"));
        } 
      } 
      this._characters = null;
      this._algorithmData = null;
      this._currentNamespaceAIIsEnd = 0;
      int i = read();
      switch (DecoderStateTables.EII(i)) {
        case 0:
          processEII(this._elementNameTable._array[i], false);
          return this._eventType;
        case 1:
          processEII(this._elementNameTable._array[i & 0x1F], true);
          return this._eventType;
        case 2:
          processEII(processEIIIndexMedium(i), ((i & 0x40) > 0));
          return this._eventType;
        case 3:
          processEII(processEIIIndexLarge(i), ((i & 0x40) > 0));
          return this._eventType;
        case 5:
          qualifiedName = processLiteralQualifiedName(i & 0x3, this._elementNameTable.getNext());
          this._elementNameTable.add(qualifiedName);
          processEII(qualifiedName, ((i & 0x40) > 0));
          return this._eventType;
        case 4:
          processEIIWithNamespaces(((i & 0x40) > 0));
          return this._eventType;
        case 6:
          this._octetBufferLength = (i & true) + 1;
          processUtf8CharacterString(i);
          return this._eventType = 4;
        case 7:
          this._octetBufferLength = read() + 3;
          processUtf8CharacterString(i);
          return this._eventType = 4;
        case 8:
          this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read()) + 259;
          processUtf8CharacterString(i);
          return this._eventType = 4;
        case 9:
          this._octetBufferLength = (i & true) + 1;
          processUtf16CharacterString(i);
          return this._eventType = 4;
        case 10:
          this._octetBufferLength = read() + 3;
          processUtf16CharacterString(i);
          return this._eventType = 4;
        case 11:
          this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read()) + 259;
          processUtf16CharacterString(i);
          return this._eventType = 4;
        case 12:
          bool = ((i & 0x10) > 0);
          this._identifier = (i & 0x2) << 6;
          k = read();
          this._identifier |= (k & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(k);
          decodeRestrictedAlphabetAsCharBuffer();
          if (bool) {
            this._charactersOffset = this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            this._characters = this._characterContentChunkTable._array;
          } else {
            this._characters = this._charBuffer;
            this._charactersOffset = 0;
          } 
          return this._eventType = 4;
        case 13:
          bool = ((i & 0x10) > 0);
          this._algorithmId = (i & 0x2) << 6;
          k = read();
          this._algorithmId |= (k & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(k);
          processCIIEncodingAlgorithm(bool);
          return (this._algorithmId == 9) ? (this._eventType = 12) : (this._eventType = 4);
        case 14:
          j = i & 0xF;
          this._characterContentChunkTable._cachedIndex = j;
          this._characters = this._characterContentChunkTable._array;
          this._charactersOffset = this._characterContentChunkTable._offset[j];
          this._charBufferLength = this._characterContentChunkTable._length[j];
          return this._eventType = 4;
        case 15:
          j = ((i & 0x3) << 8 | read()) + 16;
          this._characterContentChunkTable._cachedIndex = j;
          this._characters = this._characterContentChunkTable._array;
          this._charactersOffset = this._characterContentChunkTable._offset[j];
          this._charBufferLength = this._characterContentChunkTable._length[j];
          return this._eventType = 4;
        case 16:
          j = ((i & 0x3) << 16 | read() << 8 | read()) + 1040;
          this._characterContentChunkTable._cachedIndex = j;
          this._characters = this._characterContentChunkTable._array;
          this._charactersOffset = this._characterContentChunkTable._offset[j];
          this._charBufferLength = this._characterContentChunkTable._length[j];
          return this._eventType = 4;
        case 17:
          j = (read() << 16 | read() << 8 | read()) + 263184;
          this._characterContentChunkTable._cachedIndex = j;
          this._characters = this._characterContentChunkTable._array;
          this._charactersOffset = this._characterContentChunkTable._offset[j];
          this._charBufferLength = this._characterContentChunkTable._length[j];
          return this._eventType = 4;
        case 18:
          processCommentII();
          return this._eventType;
        case 19:
          processProcessingII();
          return this._eventType;
        case 21:
          processUnexpandedEntityReference(i);
          return next();
        case 23:
          if (this._stackCount != -1) {
            popStack();
            this._internalState = 3;
            return this._eventType = 2;
          } 
          this._internalState = 4;
          return this._eventType = 8;
        case 22:
          if (this._stackCount != -1) {
            popStack();
            if (this._currentNamespaceAIIsEnd > 0)
              this._internalState = 2; 
            return this._eventType = 2;
          } 
          this._internalState = 4;
          return this._eventType = 8;
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
    } catch (IOException iOException) {
      resetOnError();
      logger.log(Level.FINE, "next() exception", iOException);
      throw new XMLStreamException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      resetOnError();
      logger.log(Level.FINE, "next() exception", fastInfosetException);
      throw new XMLStreamException(fastInfosetException);
    } catch (RuntimeException runtimeException) {
      resetOnError();
      logger.log(Level.FINE, "next() exception", runtimeException);
      throw runtimeException;
    } 
  }
  
  private final void processUtf8CharacterString(int paramInt) throws IOException {
    if ((paramInt & 0x10) > 0) {
      this._characterContentChunkTable.ensureSize(this._octetBufferLength);
      this._characters = this._characterContentChunkTable._array;
      this._charactersOffset = this._characterContentChunkTable._arrayIndex;
      decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, this._charactersOffset);
      this._characterContentChunkTable.add(this._charBufferLength);
    } else {
      decodeUtf8StringAsCharBuffer();
      this._characters = this._charBuffer;
      this._charactersOffset = 0;
    } 
  }
  
  private final void processUtf16CharacterString(int paramInt) throws IOException {
    decodeUtf16StringAsCharBuffer();
    if ((paramInt & 0x10) > 0) {
      this._charactersOffset = this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
      this._characters = this._characterContentChunkTable._array;
    } else {
      this._characters = this._charBuffer;
      this._charactersOffset = 0;
    } 
  }
  
  private void popStack() {
    this._qualifiedName = this._qNameStack[this._stackCount];
    this._currentNamespaceAIIsStart = this._namespaceAIIsStartStack[this._stackCount];
    this._currentNamespaceAIIsEnd = this._namespaceAIIsEndStack[this._stackCount];
    this._qNameStack[this._stackCount--] = null;
  }
  
  public final void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException {
    if (paramInt != this._eventType)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotMatch", new Object[] { getEventTypeString(paramInt) })); 
    if (paramString1 != null && !paramString1.equals(getNamespaceURI()))
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.namespaceURINotMatch", new Object[] { paramString1 })); 
    if (paramString2 != null && !paramString2.equals(getLocalName()))
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.localNameNotMatch", new Object[] { paramString2 })); 
  }
  
  public final String getElementText() throws XMLStreamException {
    if (getEventType() != 1)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), getLocation()); 
    next();
    return getElementText(true);
  }
  
  public final String getElementText(boolean paramBoolean) throws XMLStreamException {
    if (!paramBoolean)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), getLocation()); 
    int i = getEventType();
    StringBuilder stringBuilder = new StringBuilder();
    while (i != 2) {
      if (i == 4 || i == 12 || i == 6 || i == 9) {
        stringBuilder.append(getText());
      } else if (i != 3 && i != 5) {
        if (i == 8)
          throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEOF")); 
        if (i == 1)
          throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"), getLocation()); 
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEventType") + getEventTypeString(i), getLocation());
      } 
      i = next();
    } 
    return stringBuilder.toString();
  }
  
  public final int nextTag() throws XMLStreamException {
    next();
    return nextTag(true);
  }
  
  public final int nextTag(boolean paramBoolean) throws XMLStreamException {
    int i = getEventType();
    if (!paramBoolean)
      i = next(); 
    while ((i == 4 && isWhiteSpace()) || (i == 12 && isWhiteSpace()) || i == 6 || i == 3 || i == 5)
      i = next(); 
    if (i != 1 && i != 2)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.expectedStartOrEnd"), getLocation()); 
    return i;
  }
  
  public final boolean hasNext() throws XMLStreamException { return (this._eventType != 8); }
  
  public void close() {
    try {
      closeIfRequired();
    } catch (IOException iOException) {}
  }
  
  public final String getNamespaceURI(String paramString) {
    String str = getNamespaceDecl(paramString);
    if (str == null) {
      if (paramString == null)
        throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPrefix")); 
      return null;
    } 
    return str;
  }
  
  public final boolean isStartElement() throws XMLStreamException { return (this._eventType == 1); }
  
  public final boolean isEndElement() throws XMLStreamException { return (this._eventType == 2); }
  
  public final boolean isCharacters() throws XMLStreamException { return (this._eventType == 4); }
  
  public final boolean isWhiteSpace() throws XMLStreamException {
    if (isCharacters() || this._eventType == 12) {
      char[] arrayOfChar = getTextCharacters();
      int i = getTextStart();
      int j = getTextLength();
      for (int k = i; k < i + j; k++) {
        if (!XMLChar.isSpace(arrayOfChar[k]))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public final String getAttributeValue(String paramString1, String paramString2) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    if (paramString2 == null)
      throw new IllegalArgumentException(); 
    if (paramString1 != null) {
      for (byte b = 0; b < this._attributes.getLength(); b++) {
        if (this._attributes.getLocalName(b).equals(paramString2) && this._attributes.getURI(b).equals(paramString1))
          return this._attributes.getValue(b); 
      } 
    } else {
      for (byte b = 0; b < this._attributes.getLength(); b++) {
        if (this._attributes.getLocalName(b).equals(paramString2))
          return this._attributes.getValue(b); 
      } 
    } 
    return null;
  }
  
  public final int getAttributeCount() throws XMLStreamException {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getLength();
  }
  
  public final QName getAttributeName(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getQualifiedName(paramInt).getQName();
  }
  
  public final String getAttributeNamespace(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getURI(paramInt);
  }
  
  public final String getAttributeLocalName(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getLocalName(paramInt);
  }
  
  public final String getAttributePrefix(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getPrefix(paramInt);
  }
  
  public final String getAttributeType(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getType(paramInt);
  }
  
  public final String getAttributeValue(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getValue(paramInt);
  }
  
  public final boolean isAttributeSpecified(int paramInt) { return false; }
  
  public final int getNamespaceCount() throws XMLStreamException {
    if (this._eventType == 1 || this._eventType == 2)
      return (this._currentNamespaceAIIsEnd > 0) ? (this._currentNamespaceAIIsEnd - this._currentNamespaceAIIsStart) : 0; 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceCount"));
  }
  
  public final String getNamespacePrefix(int paramInt) {
    if (this._eventType == 1 || this._eventType == 2)
      return this._namespaceAIIsPrefix[this._currentNamespaceAIIsStart + paramInt]; 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
  }
  
  public final String getNamespaceURI(int paramInt) {
    if (this._eventType == 1 || this._eventType == 2)
      return this._namespaceAIIsNamespaceName[this._currentNamespaceAIIsStart + paramInt]; 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
  }
  
  public final NamespaceContext getNamespaceContext() { return this._nsContext; }
  
  public final int getEventType() throws XMLStreamException { return this._eventType; }
  
  public final String getText() throws XMLStreamException {
    if (this._characters == null)
      checkTextState(); 
    return (this._characters == this._characterContentChunkTable._array) ? this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex) : new String(this._characters, this._charactersOffset, this._charBufferLength);
  }
  
  public final char[] getTextCharacters() {
    if (this._characters == null)
      checkTextState(); 
    return this._characters;
  }
  
  public final int getTextStart() throws XMLStreamException {
    if (this._characters == null)
      checkTextState(); 
    return this._charactersOffset;
  }
  
  public final int getTextLength() throws XMLStreamException {
    if (this._characters == null)
      checkTextState(); 
    return this._charBufferLength;
  }
  
  public final int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException {
    if (this._characters == null)
      checkTextState(); 
    try {
      int i = Math.min(this._charBufferLength, paramInt3);
      System.arraycopy(this._characters, this._charactersOffset + paramInt1, paramArrayOfChar, paramInt2, i);
      return i;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new XMLStreamException(indexOutOfBoundsException);
    } 
  }
  
  protected final void checkTextState() {
    if (this._algorithmData == null)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText")); 
    try {
      convertEncodingAlgorithmDataToCharacters();
    } catch (Exception exception) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
    } 
  }
  
  public final String getEncoding() throws XMLStreamException { return this._characterEncodingScheme; }
  
  public final boolean hasText() throws XMLStreamException { return (this._characters != null); }
  
  public final Location getLocation() { return EventLocation.getNilLocation(); }
  
  public final QName getName() {
    if (this._eventType == 1 || this._eventType == 2)
      return this._qualifiedName.getQName(); 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
  }
  
  public final String getLocalName() throws XMLStreamException {
    if (this._eventType == 1 || this._eventType == 2)
      return this._qualifiedName.localName; 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetLocalName"));
  }
  
  public final boolean hasName() throws XMLStreamException { return (this._eventType == 1 || this._eventType == 2); }
  
  public final String getNamespaceURI() throws XMLStreamException {
    if (this._eventType == 1 || this._eventType == 2)
      return this._qualifiedName.namespaceName; 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceURI"));
  }
  
  public final String getPrefix() throws XMLStreamException {
    if (this._eventType == 1 || this._eventType == 2)
      return this._qualifiedName.prefix; 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPrefix"));
  }
  
  public final String getVersion() throws XMLStreamException { return null; }
  
  public final boolean isStandalone() throws XMLStreamException { return false; }
  
  public final boolean standaloneSet() throws XMLStreamException { return false; }
  
  public final String getCharacterEncodingScheme() throws XMLStreamException { return null; }
  
  public final String getPITarget() throws XMLStreamException {
    if (this._eventType != 3)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPITarget")); 
    return this._piTarget;
  }
  
  public final String getPIData() throws XMLStreamException {
    if (this._eventType != 3)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPIData")); 
    return this._piData;
  }
  
  public final String getNameString() throws XMLStreamException {
    if (this._eventType == 1 || this._eventType == 2)
      return this._qualifiedName.getQNameString(); 
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
  }
  
  public final String getAttributeNameString(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue")); 
    return this._attributes.getQualifiedName(paramInt).getQNameString();
  }
  
  public final String getTextAlgorithmURI() throws XMLStreamException { return this._algorithmURI; }
  
  public final int getTextAlgorithmIndex() throws XMLStreamException { return this._algorithmId; }
  
  public final boolean hasTextAlgorithmBytes() throws XMLStreamException { return (this._algorithmData != null); }
  
  public final byte[] getTextAlgorithmBytes() {
    if (this._algorithmData == null)
      return null; 
    byte[] arrayOfByte = new byte[this._algorithmData.length];
    System.arraycopy(this._algorithmData, 0, arrayOfByte, 0, this._algorithmData.length);
    return arrayOfByte;
  }
  
  public final byte[] getTextAlgorithmBytesClone() {
    if (this._algorithmData == null)
      return null; 
    byte[] arrayOfByte = new byte[this._algorithmDataLength];
    System.arraycopy(this._algorithmData, this._algorithmDataOffset, arrayOfByte, 0, this._algorithmDataLength);
    return arrayOfByte;
  }
  
  public final int getTextAlgorithmStart() throws XMLStreamException { return this._algorithmDataOffset; }
  
  public final int getTextAlgorithmLength() throws XMLStreamException { return this._algorithmDataLength; }
  
  public final int getTextAlgorithmBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws XMLStreamException {
    try {
      System.arraycopy(this._algorithmData, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
      return paramInt3;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new XMLStreamException(indexOutOfBoundsException);
    } 
  }
  
  public final int peekNext() throws XMLStreamException {
    try {
      switch (DecoderStateTables.EII(peek(this))) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
          return 1;
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
        case 17:
          return 4;
        case 18:
          return 5;
        case 19:
          return 3;
        case 21:
          return 9;
        case 22:
        case 23:
          return (this._stackCount != -1) ? 2 : 8;
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new XMLStreamException(fastInfosetException);
    } 
  }
  
  public void onBeforeOctetBufferOverwrite() {
    if (this._algorithmData != null) {
      this._algorithmData = getTextAlgorithmBytesClone();
      this._algorithmDataOffset = 0;
      this._isAlgorithmDataCloned = true;
    } 
  }
  
  public final int accessNamespaceCount() throws XMLStreamException { return (this._currentNamespaceAIIsEnd > 0) ? (this._currentNamespaceAIIsEnd - this._currentNamespaceAIIsStart) : 0; }
  
  public final String accessLocalName() throws XMLStreamException { return this._qualifiedName.localName; }
  
  public final String accessNamespaceURI() throws XMLStreamException { return this._qualifiedName.namespaceName; }
  
  public final String accessPrefix() throws XMLStreamException { return this._qualifiedName.prefix; }
  
  public final char[] accessTextCharacters() {
    if (this._characters == null)
      return null; 
    char[] arrayOfChar = new char[this._characters.length];
    System.arraycopy(this._characters, 0, arrayOfChar, 0, this._characters.length);
    return arrayOfChar;
  }
  
  public final int accessTextStart() throws XMLStreamException { return this._charactersOffset; }
  
  public final int accessTextLength() throws XMLStreamException { return this._charBufferLength; }
  
  protected final void processDII() {
    int i = read();
    if (i > 0)
      processDIIOptionalProperties(i); 
  }
  
  protected final void processDIIOptionalProperties(int paramInt) throws IOException {
    if (paramInt == 32) {
      decodeInitialVocabulary();
      return;
    } 
    if ((paramInt & 0x40) > 0)
      decodeAdditionalData(); 
    if ((paramInt & 0x20) > 0)
      decodeInitialVocabulary(); 
    if ((paramInt & 0x10) > 0)
      decodeNotations(); 
    if ((paramInt & 0x8) > 0)
      decodeUnparsedEntities(); 
    if ((paramInt & 0x4) > 0)
      this._characterEncodingScheme = decodeCharacterEncodingScheme(); 
    if ((paramInt & 0x2) > 0)
      boolean bool = (read() > 0) ? 1 : 0; 
    if ((paramInt & true) > 0)
      decodeVersion(); 
  }
  
  protected final void resizeNamespaceAIIs() {
    String[] arrayOfString1 = new String[this._namespaceAIIsIndex * 2];
    System.arraycopy(this._namespaceAIIsPrefix, 0, arrayOfString1, 0, this._namespaceAIIsIndex);
    this._namespaceAIIsPrefix = arrayOfString1;
    String[] arrayOfString2 = new String[this._namespaceAIIsIndex * 2];
    System.arraycopy(this._namespaceAIIsNamespaceName, 0, arrayOfString2, 0, this._namespaceAIIsIndex);
    this._namespaceAIIsNamespaceName = arrayOfString2;
    int[] arrayOfInt = new int[this._namespaceAIIsIndex * 2];
    System.arraycopy(this._namespaceAIIsPrefixIndex, 0, arrayOfInt, 0, this._namespaceAIIsIndex);
    this._namespaceAIIsPrefixIndex = arrayOfInt;
  }
  
  protected final void processEIIWithNamespaces(boolean paramBoolean) throws FastInfosetException, IOException {
    QualifiedName qualifiedName;
    if (++this._prefixTable._declarationId == Integer.MAX_VALUE)
      this._prefixTable.clearDeclarationIds(); 
    this._currentNamespaceAIIsStart = this._namespaceAIIsIndex;
    String str1 = "";
    String str2 = "";
    int i;
    for (i = read(); (i & 0xFC) == 204; i = read()) {
      if (this._namespaceAIIsIndex == this._namespaceAIIsPrefix.length)
        resizeNamespaceAIIs(); 
      switch (i & 0x3) {
        case 0:
          this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = "";
          this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = "";
          str1 = str2 = "";
          this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = -1;
          this._namespaceNameIndex = this._prefixIndex = -1;
          break;
        case 1:
          str1 = this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = "";
          str2 = this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
          this._prefixIndex = this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = -1;
          break;
        case 2:
          str1 = this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
          str2 = this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = "";
          this._namespaceNameIndex = -1;
          this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = this._prefixIndex;
          break;
        case 3:
          str1 = this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
          str2 = this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
          this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = this._prefixIndex;
          break;
      } 
      this._prefixTable.pushScopeWithPrefixEntry(str1, str2, this._prefixIndex, this._namespaceNameIndex);
    } 
    if (i != 240)
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly")); 
    this._currentNamespaceAIIsEnd = this._namespaceAIIsIndex;
    i = read();
    switch (DecoderStateTables.EII(i)) {
      case 0:
        processEII(this._elementNameTable._array[i], paramBoolean);
        return;
      case 2:
        processEII(processEIIIndexMedium(i), paramBoolean);
        return;
      case 3:
        processEII(processEIIIndexLarge(i), paramBoolean);
        return;
      case 5:
        qualifiedName = processLiteralQualifiedName(i & 0x3, this._elementNameTable.getNext());
        this._elementNameTable.add(qualifiedName);
        processEII(qualifiedName, paramBoolean);
        return;
    } 
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
  }
  
  protected final void processEII(QualifiedName paramQualifiedName, boolean paramBoolean) throws FastInfosetException, IOException {
    if (this._prefixTable._currentInScope[paramQualifiedName.prefixIndex] != paramQualifiedName.namespaceNameIndex)
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope")); 
    this._eventType = 1;
    this._qualifiedName = paramQualifiedName;
    if (this._clearAttributes) {
      this._attributes.clear();
      this._clearAttributes = false;
    } 
    if (paramBoolean)
      processAIIs(); 
    this._stackCount++;
    if (this._stackCount == this._qNameStack.length) {
      QualifiedName[] arrayOfQualifiedName = new QualifiedName[this._qNameStack.length * 2];
      System.arraycopy(this._qNameStack, 0, arrayOfQualifiedName, 0, this._qNameStack.length);
      this._qNameStack = arrayOfQualifiedName;
      int[] arrayOfInt1 = new int[this._namespaceAIIsStartStack.length * 2];
      System.arraycopy(this._namespaceAIIsStartStack, 0, arrayOfInt1, 0, this._namespaceAIIsStartStack.length);
      this._namespaceAIIsStartStack = arrayOfInt1;
      int[] arrayOfInt2 = new int[this._namespaceAIIsEndStack.length * 2];
      System.arraycopy(this._namespaceAIIsEndStack, 0, arrayOfInt2, 0, this._namespaceAIIsEndStack.length);
      this._namespaceAIIsEndStack = arrayOfInt2;
    } 
    this._qNameStack[this._stackCount] = this._qualifiedName;
    this._namespaceAIIsStartStack[this._stackCount] = this._currentNamespaceAIIsStart;
    this._namespaceAIIsEndStack[this._stackCount] = this._currentNamespaceAIIsEnd;
  }
  
  protected final void processAIIs() { // Byte code:
    //   0: aload_0
    //   1: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   4: dup
    //   5: getfield _currentIteration : I
    //   8: iconst_1
    //   9: iadd
    //   10: dup_x1
    //   11: putfield _currentIteration : I
    //   14: ldc 2147483647
    //   16: if_icmpne -> 26
    //   19: aload_0
    //   20: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   23: invokevirtual clear : ()V
    //   26: aload_0
    //   27: iconst_1
    //   28: putfield _clearAttributes : Z
    //   31: iconst_0
    //   32: istore #4
    //   34: aload_0
    //   35: invokevirtual read : ()I
    //   38: istore_2
    //   39: iload_2
    //   40: invokestatic AII : (I)I
    //   43: tableswitch default -> 208, 0 -> 80, 1 -> 93, 2 -> 124, 3 -> 164, 4 -> 202, 5 -> 197
    //   80: aload_0
    //   81: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   84: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   87: iload_2
    //   88: aaload
    //   89: astore_1
    //   90: goto -> 224
    //   93: iload_2
    //   94: bipush #31
    //   96: iand
    //   97: bipush #8
    //   99: ishl
    //   100: aload_0
    //   101: invokevirtual read : ()I
    //   104: ior
    //   105: bipush #64
    //   107: iadd
    //   108: istore #5
    //   110: aload_0
    //   111: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   114: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   117: iload #5
    //   119: aaload
    //   120: astore_1
    //   121: goto -> 224
    //   124: iload_2
    //   125: bipush #15
    //   127: iand
    //   128: bipush #16
    //   130: ishl
    //   131: aload_0
    //   132: invokevirtual read : ()I
    //   135: bipush #8
    //   137: ishl
    //   138: ior
    //   139: aload_0
    //   140: invokevirtual read : ()I
    //   143: ior
    //   144: sipush #8256
    //   147: iadd
    //   148: istore #5
    //   150: aload_0
    //   151: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   154: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   157: iload #5
    //   159: aaload
    //   160: astore_1
    //   161: goto -> 224
    //   164: aload_0
    //   165: iload_2
    //   166: iconst_3
    //   167: iand
    //   168: aload_0
    //   169: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   172: invokevirtual getNext : ()Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   175: invokevirtual processLiteralQualifiedName : (ILcom/sun/xml/internal/fastinfoset/QualifiedName;)Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   178: astore_1
    //   179: aload_1
    //   180: sipush #256
    //   183: invokevirtual createAttributeValues : (I)V
    //   186: aload_0
    //   187: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   190: aload_1
    //   191: invokevirtual add : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;)V
    //   194: goto -> 224
    //   197: aload_0
    //   198: iconst_1
    //   199: putfield _internalState : I
    //   202: iconst_1
    //   203: istore #4
    //   205: goto -> 942
    //   208: new com/sun/xml/internal/org/jvnet/fastinfoset/FastInfosetException
    //   211: dup
    //   212: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   215: ldc 'message.decodingAIIs'
    //   217: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   220: invokespecial <init> : (Ljava/lang/String;)V
    //   223: athrow
    //   224: aload_1
    //   225: getfield prefixIndex : I
    //   228: ifle -> 266
    //   231: aload_0
    //   232: getfield _prefixTable : Lcom/sun/xml/internal/fastinfoset/util/PrefixArray;
    //   235: getfield _currentInScope : [I
    //   238: aload_1
    //   239: getfield prefixIndex : I
    //   242: iaload
    //   243: aload_1
    //   244: getfield namespaceNameIndex : I
    //   247: if_icmpeq -> 266
    //   250: new com/sun/xml/internal/org/jvnet/fastinfoset/FastInfosetException
    //   253: dup
    //   254: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   257: ldc 'message.AIIqNameNotInScope'
    //   259: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   262: invokespecial <init> : (Ljava/lang/String;)V
    //   265: athrow
    //   266: aload_0
    //   267: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   270: aload_1
    //   271: getfield attributeHash : I
    //   274: aload_1
    //   275: getfield attributeId : I
    //   278: invokevirtual checkForDuplicateAttribute : (II)V
    //   281: aload_0
    //   282: invokevirtual read : ()I
    //   285: istore_2
    //   286: iload_2
    //   287: invokestatic NISTRING : (I)I
    //   290: tableswitch default -> 926, 0 -> 352, 1 -> 395, 2 -> 439, 3 -> 508, 4 -> 551, 5 -> 595, 6 -> 664, 7 -> 745, 8 -> 805, 9 -> 828, 10 -> 866, 11 -> 913
    //   352: aload_0
    //   353: iload_2
    //   354: bipush #7
    //   356: iand
    //   357: iconst_1
    //   358: iadd
    //   359: putfield _octetBufferLength : I
    //   362: aload_0
    //   363: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   366: astore_3
    //   367: iload_2
    //   368: bipush #64
    //   370: iand
    //   371: ifle -> 383
    //   374: aload_0
    //   375: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   378: aload_3
    //   379: invokevirtual add : (Ljava/lang/String;)I
    //   382: pop
    //   383: aload_0
    //   384: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   387: aload_1
    //   388: aload_3
    //   389: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   392: goto -> 942
    //   395: aload_0
    //   396: aload_0
    //   397: invokevirtual read : ()I
    //   400: bipush #9
    //   402: iadd
    //   403: putfield _octetBufferLength : I
    //   406: aload_0
    //   407: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   410: astore_3
    //   411: iload_2
    //   412: bipush #64
    //   414: iand
    //   415: ifle -> 427
    //   418: aload_0
    //   419: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   422: aload_3
    //   423: invokevirtual add : (Ljava/lang/String;)I
    //   426: pop
    //   427: aload_0
    //   428: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   431: aload_1
    //   432: aload_3
    //   433: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   436: goto -> 942
    //   439: aload_0
    //   440: aload_0
    //   441: invokevirtual read : ()I
    //   444: bipush #24
    //   446: ishl
    //   447: aload_0
    //   448: invokevirtual read : ()I
    //   451: bipush #16
    //   453: ishl
    //   454: ior
    //   455: aload_0
    //   456: invokevirtual read : ()I
    //   459: bipush #8
    //   461: ishl
    //   462: ior
    //   463: aload_0
    //   464: invokevirtual read : ()I
    //   467: ior
    //   468: sipush #265
    //   471: iadd
    //   472: putfield _octetBufferLength : I
    //   475: aload_0
    //   476: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   479: astore_3
    //   480: iload_2
    //   481: bipush #64
    //   483: iand
    //   484: ifle -> 496
    //   487: aload_0
    //   488: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   491: aload_3
    //   492: invokevirtual add : (Ljava/lang/String;)I
    //   495: pop
    //   496: aload_0
    //   497: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   500: aload_1
    //   501: aload_3
    //   502: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   505: goto -> 942
    //   508: aload_0
    //   509: iload_2
    //   510: bipush #7
    //   512: iand
    //   513: iconst_1
    //   514: iadd
    //   515: putfield _octetBufferLength : I
    //   518: aload_0
    //   519: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   522: astore_3
    //   523: iload_2
    //   524: bipush #64
    //   526: iand
    //   527: ifle -> 539
    //   530: aload_0
    //   531: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   534: aload_3
    //   535: invokevirtual add : (Ljava/lang/String;)I
    //   538: pop
    //   539: aload_0
    //   540: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   543: aload_1
    //   544: aload_3
    //   545: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   548: goto -> 942
    //   551: aload_0
    //   552: aload_0
    //   553: invokevirtual read : ()I
    //   556: bipush #9
    //   558: iadd
    //   559: putfield _octetBufferLength : I
    //   562: aload_0
    //   563: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   566: astore_3
    //   567: iload_2
    //   568: bipush #64
    //   570: iand
    //   571: ifle -> 583
    //   574: aload_0
    //   575: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   578: aload_3
    //   579: invokevirtual add : (Ljava/lang/String;)I
    //   582: pop
    //   583: aload_0
    //   584: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   587: aload_1
    //   588: aload_3
    //   589: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   592: goto -> 942
    //   595: aload_0
    //   596: aload_0
    //   597: invokevirtual read : ()I
    //   600: bipush #24
    //   602: ishl
    //   603: aload_0
    //   604: invokevirtual read : ()I
    //   607: bipush #16
    //   609: ishl
    //   610: ior
    //   611: aload_0
    //   612: invokevirtual read : ()I
    //   615: bipush #8
    //   617: ishl
    //   618: ior
    //   619: aload_0
    //   620: invokevirtual read : ()I
    //   623: ior
    //   624: sipush #265
    //   627: iadd
    //   628: putfield _octetBufferLength : I
    //   631: aload_0
    //   632: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   635: astore_3
    //   636: iload_2
    //   637: bipush #64
    //   639: iand
    //   640: ifle -> 652
    //   643: aload_0
    //   644: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   647: aload_3
    //   648: invokevirtual add : (Ljava/lang/String;)I
    //   651: pop
    //   652: aload_0
    //   653: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   656: aload_1
    //   657: aload_3
    //   658: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   661: goto -> 942
    //   664: iload_2
    //   665: bipush #64
    //   667: iand
    //   668: ifle -> 675
    //   671: iconst_1
    //   672: goto -> 676
    //   675: iconst_0
    //   676: istore #5
    //   678: aload_0
    //   679: iload_2
    //   680: bipush #15
    //   682: iand
    //   683: iconst_4
    //   684: ishl
    //   685: putfield _identifier : I
    //   688: aload_0
    //   689: invokevirtual read : ()I
    //   692: istore_2
    //   693: aload_0
    //   694: dup
    //   695: getfield _identifier : I
    //   698: iload_2
    //   699: sipush #240
    //   702: iand
    //   703: iconst_4
    //   704: ishr
    //   705: ior
    //   706: putfield _identifier : I
    //   709: aload_0
    //   710: iload_2
    //   711: invokevirtual decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit : (I)V
    //   714: aload_0
    //   715: invokevirtual decodeRestrictedAlphabetAsString : ()Ljava/lang/String;
    //   718: astore_3
    //   719: iload #5
    //   721: ifeq -> 733
    //   724: aload_0
    //   725: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   728: aload_3
    //   729: invokevirtual add : (Ljava/lang/String;)I
    //   732: pop
    //   733: aload_0
    //   734: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   737: aload_1
    //   738: aload_3
    //   739: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   742: goto -> 942
    //   745: iload_2
    //   746: bipush #64
    //   748: iand
    //   749: ifle -> 756
    //   752: iconst_1
    //   753: goto -> 757
    //   756: iconst_0
    //   757: istore #5
    //   759: aload_0
    //   760: iload_2
    //   761: bipush #15
    //   763: iand
    //   764: iconst_4
    //   765: ishl
    //   766: putfield _identifier : I
    //   769: aload_0
    //   770: invokevirtual read : ()I
    //   773: istore_2
    //   774: aload_0
    //   775: dup
    //   776: getfield _identifier : I
    //   779: iload_2
    //   780: sipush #240
    //   783: iand
    //   784: iconst_4
    //   785: ishr
    //   786: ior
    //   787: putfield _identifier : I
    //   790: aload_0
    //   791: iload_2
    //   792: invokevirtual decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit : (I)V
    //   795: aload_0
    //   796: aload_1
    //   797: iload #5
    //   799: invokevirtual processAIIEncodingAlgorithm : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Z)V
    //   802: goto -> 942
    //   805: aload_0
    //   806: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   809: aload_1
    //   810: aload_0
    //   811: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   814: getfield _array : [Ljava/lang/String;
    //   817: iload_2
    //   818: bipush #63
    //   820: iand
    //   821: aaload
    //   822: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   825: goto -> 942
    //   828: iload_2
    //   829: bipush #31
    //   831: iand
    //   832: bipush #8
    //   834: ishl
    //   835: aload_0
    //   836: invokevirtual read : ()I
    //   839: ior
    //   840: bipush #64
    //   842: iadd
    //   843: istore #5
    //   845: aload_0
    //   846: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   849: aload_1
    //   850: aload_0
    //   851: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   854: getfield _array : [Ljava/lang/String;
    //   857: iload #5
    //   859: aaload
    //   860: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   863: goto -> 942
    //   866: iload_2
    //   867: bipush #15
    //   869: iand
    //   870: bipush #16
    //   872: ishl
    //   873: aload_0
    //   874: invokevirtual read : ()I
    //   877: bipush #8
    //   879: ishl
    //   880: ior
    //   881: aload_0
    //   882: invokevirtual read : ()I
    //   885: ior
    //   886: sipush #8256
    //   889: iadd
    //   890: istore #5
    //   892: aload_0
    //   893: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   896: aload_1
    //   897: aload_0
    //   898: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   901: getfield _array : [Ljava/lang/String;
    //   904: iload #5
    //   906: aaload
    //   907: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   910: goto -> 942
    //   913: aload_0
    //   914: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   917: aload_1
    //   918: ldc ''
    //   920: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   923: goto -> 942
    //   926: new com/sun/xml/internal/org/jvnet/fastinfoset/FastInfosetException
    //   929: dup
    //   930: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   933: ldc 'message.decodingAIIValue'
    //   935: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   938: invokespecial <init> : (Ljava/lang/String;)V
    //   941: athrow
    //   942: iload #4
    //   944: ifeq -> 34
    //   947: aload_0
    //   948: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   951: aload_0
    //   952: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   955: getfield _poolHead : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier$Entry;
    //   958: putfield _poolCurrent : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier$Entry;
    //   961: return }
  
  protected final QualifiedName processEIIIndexMedium(int paramInt) throws FastInfosetException, IOException {
    int i = ((paramInt & 0x7) << 8 | read()) + 32;
    return this._elementNameTable._array[i];
  }
  
  protected final QualifiedName processEIIIndexLarge(int paramInt) throws FastInfosetException, IOException {
    int i;
    if ((paramInt & 0x30) == 32) {
      i = ((paramInt & 0x7) << 16 | read() << 8 | read()) + 2080;
    } else {
      i = ((read() & 0xF) << 16 | read() << 8 | read()) + 526368;
    } 
    return this._elementNameTable._array[i];
  }
  
  protected final QualifiedName processLiteralQualifiedName(int paramInt, QualifiedName paramQualifiedName) throws FastInfosetException, IOException {
    if (paramQualifiedName == null)
      paramQualifiedName = new QualifiedName(); 
    switch (paramInt) {
      case 0:
        return paramQualifiedName.set("", "", decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, -1, -1, this._identifier);
      case 1:
        return paramQualifiedName.set("", decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, -1, this._namespaceNameIndex, this._identifier);
      case 2:
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
      case 3:
        return paramQualifiedName.set(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, this._prefixIndex, this._namespaceNameIndex, this._identifier);
    } 
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
  }
  
  protected final void processCommentII() {
    CharArray charArray;
    this._eventType = 5;
    switch (decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
        if (this._addToTable)
          this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true)); 
        this._characters = this._charBuffer;
        this._charactersOffset = 0;
        break;
      case 2:
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
      case 1:
        charArray = this._v.otherString.get(this._integer);
        this._characters = charArray.ch;
        this._charactersOffset = charArray.start;
        this._charBufferLength = charArray.length;
        break;
      case 3:
        this._characters = this._charBuffer;
        this._charactersOffset = 0;
        this._charBufferLength = 0;
        break;
    } 
  }
  
  protected final void processProcessingII() {
    this._eventType = 3;
    this._piTarget = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
    switch (decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
        this._piData = new String(this._charBuffer, 0, this._charBufferLength);
        if (this._addToTable)
          this._v.otherString.add(new CharArrayString(this._piData)); 
        break;
      case 2:
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
      case 1:
        this._piData = this._v.otherString.get(this._integer).toString();
        break;
      case 3:
        this._piData = "";
        break;
    } 
  }
  
  protected final void processUnexpandedEntityReference(int paramInt) throws IOException {
    this._eventType = 9;
    String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
    String str2 = ((paramInt & 0x2) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
    String str3 = ((paramInt & true) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
    if (logger.isLoggable(Level.FINEST))
      logger.log(Level.FINEST, "processUnexpandedEntityReference: entity_reference_name={0} system_identifier={1}public_identifier={2}", new Object[] { str1, str2, str3 }); 
  }
  
  protected final void processCIIEncodingAlgorithm(boolean paramBoolean) throws FastInfosetException, IOException {
    this._algorithmData = this._octetBuffer;
    this._algorithmDataOffset = this._octetBufferStart;
    this._algorithmDataLength = this._octetBufferLength;
    this._isAlgorithmDataCloned = false;
    if (this._algorithmId >= 32) {
      this._algorithmURI = this._v.encodingAlgorithm.get(this._algorithmId - 32);
      if (this._algorithmURI == null)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(this._identifier) })); 
    } else if (this._algorithmId > 9) {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    } 
    if (paramBoolean) {
      convertEncodingAlgorithmDataToCharacters();
      this._characterContentChunkTable.add(this._characters, this._characters.length);
    } 
  }
  
  protected final void processAIIEncodingAlgorithm(QualifiedName paramQualifiedName, boolean paramBoolean) throws FastInfosetException, IOException {
    byte[] arrayOfByte;
    EncodingAlgorithm encodingAlgorithm = null;
    String str = null;
    if (this._identifier >= 32) {
      str = this._v.encodingAlgorithm.get(this._identifier - 32);
      if (str == null)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(this._identifier) })); 
      if (this._registeredEncodingAlgorithms != null)
        encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(str); 
    } else {
      if (this._identifier >= 9) {
        if (this._identifier == 9)
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported")); 
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
      } 
      encodingAlgorithm = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier);
    } 
    if (encodingAlgorithm != null) {
      arrayOfByte = encodingAlgorithm.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
    } else {
      byte[] arrayOfByte1 = new byte[this._octetBufferLength];
      System.arraycopy(this._octetBuffer, this._octetBufferStart, arrayOfByte1, 0, this._octetBufferLength);
      arrayOfByte = arrayOfByte1;
    } 
    this._attributes.addAttributeWithAlgorithmData(paramQualifiedName, str, this._identifier, arrayOfByte);
    if (paramBoolean)
      this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(paramQualifiedName.qName))); 
  }
  
  protected final void convertEncodingAlgorithmDataToCharacters() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this._algorithmId == 1) {
      convertBase64AlorithmDataToCharacters(stringBuffer);
    } else if (this._algorithmId < 9) {
      Object object = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._algorithmId).decodeFromBytes(this._algorithmData, this._algorithmDataOffset, this._algorithmDataLength);
      BuiltInEncodingAlgorithmFactory.getAlgorithm(this._algorithmId).convertToCharacters(object, stringBuffer);
    } else {
      if (this._algorithmId == 9) {
        this._octetBufferOffset -= this._octetBufferLength;
        decodeUtf8StringIntoCharBuffer();
        this._characters = this._charBuffer;
        this._charactersOffset = 0;
        return;
      } 
      if (this._algorithmId >= 32) {
        EncodingAlgorithm encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(this._algorithmURI);
        if (encodingAlgorithm != null) {
          Object object = encodingAlgorithm.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          encodingAlgorithm.convertToCharacters(object, stringBuffer);
        } else {
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
        } 
      } 
    } 
    this._characters = new char[stringBuffer.length()];
    stringBuffer.getChars(0, stringBuffer.length(), this._characters, 0);
    this._charactersOffset = 0;
    this._charBufferLength = this._characters.length;
  }
  
  protected void convertBase64AlorithmDataToCharacters(StringBuffer paramStringBuffer) throws EncodingAlgorithmException, IOException {
    int i = 0;
    if (this.base64TaleLength > 0) {
      int k = Math.min(3 - this.base64TaleLength, this._algorithmDataLength);
      System.arraycopy(this._algorithmData, this._algorithmDataOffset, this.base64TaleBytes, this.base64TaleLength, k);
      if (this.base64TaleLength + k == 3) {
        base64DecodeWithCloning(paramStringBuffer, this.base64TaleBytes, 0, 3);
      } else {
        if (!isBase64Follows()) {
          base64DecodeWithCloning(paramStringBuffer, this.base64TaleBytes, 0, this.base64TaleLength + k);
          return;
        } 
        this.base64TaleLength += k;
        return;
      } 
      i = k;
      this.base64TaleLength = 0;
    } 
    int j = isBase64Follows() ? ((this._algorithmDataLength - i) % 3) : 0;
    if (this._isAlgorithmDataCloned) {
      base64DecodeWithoutCloning(paramStringBuffer, this._algorithmData, this._algorithmDataOffset + i, this._algorithmDataLength - i - j);
    } else {
      base64DecodeWithCloning(paramStringBuffer, this._algorithmData, this._algorithmDataOffset + i, this._algorithmDataLength - i - j);
    } 
    if (j > 0) {
      System.arraycopy(this._algorithmData, this._algorithmDataOffset + this._algorithmDataLength - j, this.base64TaleBytes, 0, j);
      this.base64TaleLength = j;
    } 
  }
  
  private void base64DecodeWithCloning(StringBuffer paramStringBuffer, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    Object object = BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.decodeFromBytes(paramArrayOfByte, paramInt1, paramInt2);
    BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(object, paramStringBuffer);
  }
  
  private void base64DecodeWithoutCloning(StringBuffer paramStringBuffer, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException { BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(paramArrayOfByte, paramInt1, paramInt2, paramStringBuffer); }
  
  public boolean isBase64Follows() throws XMLStreamException {
    int k;
    int j;
    int i = peek(this);
    switch (DecoderStateTables.EII(i)) {
      case 13:
        j = (i & 0x2) << 6;
        k = peek2(this);
        j |= (k & 0xFC) >> 2;
        return (j == 1);
    } 
    return false;
  }
  
  public final String getNamespaceDecl(String paramString) { return this._prefixTable.getNamespaceFromPrefix(paramString); }
  
  public final String getURI(String paramString) { return getNamespaceDecl(paramString); }
  
  public final Iterator getPrefixes() { return this._prefixTable.getPrefixes(); }
  
  public final AttributesHolder getAttributesHolder() { return this._attributes; }
  
  public final void setManager(StAXManager paramStAXManager) { this._manager = paramStAXManager; }
  
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
    } 
    return "UNKNOWN_EVENT_TYPE";
  }
  
  protected class NamespaceContextImpl implements NamespaceContext {
    public final String getNamespaceURI(String param1String) { return StAXDocumentParser.this._prefixTable.getNamespaceFromPrefix(param1String); }
    
    public final String getPrefix(String param1String) { return StAXDocumentParser.this._prefixTable.getPrefixFromNamespace(param1String); }
    
    public final Iterator getPrefixes(String param1String) { return StAXDocumentParser.this._prefixTable.getPrefixesFromNamespace(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\StAXDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */