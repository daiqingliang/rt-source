package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class StreamReaderBufferProcessor extends AbstractProcessor implements XMLStreamReaderEx {
  private static final int CACHE_SIZE = 16;
  
  protected ElementStackEntry[] _stack = new ElementStackEntry[16];
  
  protected ElementStackEntry _stackTop;
  
  protected int _depth;
  
  protected String[] _namespaceAIIsPrefix = new String[16];
  
  protected String[] _namespaceAIIsNamespaceName = new String[16];
  
  protected int _namespaceAIIsEnd;
  
  protected InternalNamespaceContext _nsCtx = new InternalNamespaceContext(null);
  
  protected int _eventType;
  
  protected AttributesHolder _attributeCache;
  
  protected CharSequence _charSequence;
  
  protected char[] _characters;
  
  protected int _textOffset;
  
  protected int _textLen;
  
  protected String _piTarget;
  
  protected String _piData;
  
  private static final int PARSING = 1;
  
  private static final int PENDING_END_DOCUMENT = 2;
  
  private static final int COMPLETED = 3;
  
  private int _completionState;
  
  public StreamReaderBufferProcessor() {
    for (byte b = 0; b < this._stack.length; b++)
      this._stack[b] = new ElementStackEntry(null); 
    this._attributeCache = new AttributesHolder();
  }
  
  public StreamReaderBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer) throws XMLStreamException {
    this();
    setXMLStreamBuffer(paramXMLStreamBuffer);
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer) throws XMLStreamException {
    setBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment());
    this._completionState = 1;
    this._namespaceAIIsEnd = 0;
    this._characters = null;
    this._charSequence = null;
    this._eventType = 7;
  }
  
  public XMLStreamBuffer nextTagAndMark() throws XMLStreamException {
    do {
      int i = peekStructure();
      if ((i & 0xF0) == 32) {
        HashMap hashMap = new HashMap(this._namespaceAIIsEnd);
        for (byte b = 0; b < this._namespaceAIIsEnd; b++)
          hashMap.put(this._namespaceAIIsPrefix[b], this._namespaceAIIsNamespaceName[b]); 
        XMLStreamBufferMark xMLStreamBufferMark = new XMLStreamBufferMark(hashMap, this);
        next();
        return xMLStreamBufferMark;
      } 
      if ((i & 0xF0) == 16) {
        readStructure();
        XMLStreamBufferMark xMLStreamBufferMark = new XMLStreamBufferMark(new HashMap(this._namespaceAIIsEnd), this);
        next();
        return xMLStreamBufferMark;
      } 
    } while (next() != 2);
    return null;
  }
  
  public Object getProperty(String paramString) { return null; }
  
  public int next() throws XMLStreamException {
    int i;
    switch (this._completionState) {
      case 3:
        throw new XMLStreamException("Invalid State");
      case 2:
        this._namespaceAIIsEnd = 0;
        this._completionState = 3;
        return this._eventType = 8;
    } 
    switch (this._eventType) {
      case 2:
        if (this._depth > 1) {
          this._depth--;
          popElementStack(this._depth);
          break;
        } 
        if (this._depth == 1)
          this._depth--; 
        break;
    } 
    this._characters = null;
    this._charSequence = null;
    while (true) {
      String str3;
      String str2;
      String str1;
      i = readEiiState();
      switch (i) {
        case 1:
          continue;
        case 3:
          str1 = readStructureString();
          str2 = readStructureString();
          str3 = getPrefixFromQName(readStructureString());
          processElement(str3, str1, str2, isInscope(this._depth));
          return this._eventType = 1;
        case 4:
          processElement(readStructureString(), readStructureString(), readStructureString(), isInscope(this._depth));
          return this._eventType = 1;
        case 5:
          processElement(null, readStructureString(), readStructureString(), isInscope(this._depth));
          return this._eventType = 1;
        case 6:
          processElement(null, null, readStructureString(), isInscope(this._depth));
          return this._eventType = 1;
        case 7:
          this._textLen = readStructure();
          this._textOffset = readContentCharactersBuffer(this._textLen);
          this._characters = this._contentCharactersBuffer;
          return this._eventType = 4;
        case 8:
          this._textLen = readStructure16();
          this._textOffset = readContentCharactersBuffer(this._textLen);
          this._characters = this._contentCharactersBuffer;
          return this._eventType = 4;
        case 9:
          this._characters = readContentCharactersCopy();
          this._textLen = this._characters.length;
          this._textOffset = 0;
          return this._eventType = 4;
        case 10:
          this._eventType = 4;
          this._charSequence = readContentString();
          return this._eventType = 4;
        case 11:
          this._eventType = 4;
          this._charSequence = (CharSequence)readContentObject();
          return this._eventType = 4;
        case 12:
          this._textLen = readStructure();
          this._textOffset = readContentCharactersBuffer(this._textLen);
          this._characters = this._contentCharactersBuffer;
          return this._eventType = 5;
        case 13:
          this._textLen = readStructure16();
          this._textOffset = readContentCharactersBuffer(this._textLen);
          this._characters = this._contentCharactersBuffer;
          return this._eventType = 5;
        case 14:
          this._characters = readContentCharactersCopy();
          this._textLen = this._characters.length;
          this._textOffset = 0;
          return this._eventType = 5;
        case 15:
          this._charSequence = readContentString();
          return this._eventType = 5;
        case 16:
          this._piTarget = readStructureString();
          this._piData = readStructureString();
          return this._eventType = 3;
        case 17:
          if (this._depth > 1)
            return this._eventType = 2; 
          if (this._depth == 1) {
            if (this._fragmentMode && --this._treeCount == 0)
              this._completionState = 2; 
            return this._eventType = 2;
          } 
          this._namespaceAIIsEnd = 0;
          this._completionState = 3;
          return this._eventType = 8;
      } 
      break;
    } 
    throw new XMLStreamException("Internal XSB error: Invalid State=" + i);
  }
  
  public final void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException {
    if (paramInt != this._eventType)
      throw new XMLStreamException(""); 
    if (paramString1 != null && !paramString1.equals(getNamespaceURI()))
      throw new XMLStreamException(""); 
    if (paramString2 != null && !paramString2.equals(getLocalName()))
      throw new XMLStreamException(""); 
  }
  
  public final String getElementTextTrim() throws XMLStreamException { return getElementText().trim(); }
  
  public final String getElementText() throws XMLStreamException {
    if (this._eventType != 1)
      throw new XMLStreamException(""); 
    next();
    return getElementText(true);
  }
  
  public final String getElementText(boolean paramBoolean) throws XMLStreamException {
    if (!paramBoolean)
      throw new XMLStreamException(""); 
    int i = getEventType();
    StringBuilder stringBuilder = new StringBuilder();
    while (i != 2) {
      if (i == 4 || i == 12 || i == 6 || i == 9) {
        stringBuilder.append(getText());
      } else if (i != 3 && i != 5) {
        if (i == 8)
          throw new XMLStreamException(""); 
        if (i == 1)
          throw new XMLStreamException(""); 
        throw new XMLStreamException("");
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
      throw new XMLStreamException(""); 
    return i;
  }
  
  public final boolean hasNext() { return (this._eventType != 8); }
  
  public void close() {}
  
  public final boolean isStartElement() { return (this._eventType == 1); }
  
  public final boolean isEndElement() { return (this._eventType == 2); }
  
  public final boolean isCharacters() { return (this._eventType == 4); }
  
  public final boolean isWhiteSpace() {
    if (isCharacters() || this._eventType == 12) {
      char[] arrayOfChar = getTextCharacters();
      int i = getTextStart();
      int j = getTextLength();
      for (int k = i; k < j; k++) {
        char c = arrayOfChar[k];
        if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public final String getAttributeValue(String paramString1, String paramString2) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    if (paramString1 == null)
      paramString1 = ""; 
    return this._attributeCache.getValue(paramString1, paramString2);
  }
  
  public final int getAttributeCount() throws XMLStreamException {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    return this._attributeCache.getLength();
  }
  
  public final QName getAttributeName(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    String str1 = this._attributeCache.getPrefix(paramInt);
    String str2 = this._attributeCache.getLocalName(paramInt);
    String str3 = this._attributeCache.getURI(paramInt);
    return new QName(str3, str2, str1);
  }
  
  public final String getAttributeNamespace(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    return fixEmptyString(this._attributeCache.getURI(paramInt));
  }
  
  public final String getAttributeLocalName(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    return this._attributeCache.getLocalName(paramInt);
  }
  
  public final String getAttributePrefix(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    return fixEmptyString(this._attributeCache.getPrefix(paramInt));
  }
  
  public final String getAttributeType(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    return this._attributeCache.getType(paramInt);
  }
  
  public final String getAttributeValue(int paramInt) {
    if (this._eventType != 1)
      throw new IllegalStateException(""); 
    return this._attributeCache.getValue(paramInt);
  }
  
  public final boolean isAttributeSpecified(int paramInt) { return false; }
  
  public final int getNamespaceCount() throws XMLStreamException {
    if (this._eventType == 1 || this._eventType == 2)
      return this._stackTop.namespaceAIIsEnd - this._stackTop.namespaceAIIsStart; 
    throw new IllegalStateException("");
  }
  
  public final String getNamespacePrefix(int paramInt) {
    if (this._eventType == 1 || this._eventType == 2)
      return this._namespaceAIIsPrefix[this._stackTop.namespaceAIIsStart + paramInt]; 
    throw new IllegalStateException("");
  }
  
  public final String getNamespaceURI(int paramInt) {
    if (this._eventType == 1 || this._eventType == 2)
      return this._namespaceAIIsNamespaceName[this._stackTop.namespaceAIIsStart + paramInt]; 
    throw new IllegalStateException("");
  }
  
  public final String getNamespaceURI(String paramString) { return this._nsCtx.getNamespaceURI(paramString); }
  
  public final NamespaceContextEx getNamespaceContext() { return this._nsCtx; }
  
  public final int getEventType() throws XMLStreamException { return this._eventType; }
  
  public final String getText() throws XMLStreamException {
    if (this._characters != null) {
      String str = new String(this._characters, this._textOffset, this._textLen);
      this._charSequence = str;
      return str;
    } 
    if (this._charSequence != null)
      return this._charSequence.toString(); 
    throw new IllegalStateException();
  }
  
  public final char[] getTextCharacters() {
    if (this._characters != null)
      return this._characters; 
    if (this._charSequence != null) {
      this._characters = this._charSequence.toString().toCharArray();
      this._textLen = this._characters.length;
      this._textOffset = 0;
      return this._characters;
    } 
    throw new IllegalStateException();
  }
  
  public final int getTextStart() throws XMLStreamException {
    if (this._characters != null)
      return this._textOffset; 
    if (this._charSequence != null)
      return 0; 
    throw new IllegalStateException();
  }
  
  public final int getTextLength() throws XMLStreamException {
    if (this._characters != null)
      return this._textLen; 
    if (this._charSequence != null)
      return this._charSequence.length(); 
    throw new IllegalStateException();
  }
  
  public final int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException {
    if (this._characters == null)
      if (this._charSequence != null) {
        this._characters = this._charSequence.toString().toCharArray();
        this._textLen = this._characters.length;
        this._textOffset = 0;
      } else {
        throw new IllegalStateException("");
      }  
    try {
      int i = this._textLen - paramInt1;
      int j = (i > paramInt3) ? paramInt3 : i;
      paramInt1 += this._textOffset;
      System.arraycopy(this._characters, paramInt1, paramArrayOfChar, paramInt2, j);
      return j;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new XMLStreamException(indexOutOfBoundsException);
    } 
  }
  
  public final CharSequence getPCDATA() {
    if (this._characters != null)
      return new CharSequenceImpl(this._textOffset, this._textLen); 
    if (this._charSequence != null)
      return this._charSequence; 
    throw new IllegalStateException();
  }
  
  public final String getEncoding() throws XMLStreamException { return "UTF-8"; }
  
  public final boolean hasText() { return (this._characters != null || this._charSequence != null); }
  
  public final Location getLocation() { return new DummyLocation(null); }
  
  public final boolean hasName() { return (this._eventType == 1 || this._eventType == 2); }
  
  public final QName getName() { return this._stackTop.getQName(); }
  
  public final String getLocalName() throws XMLStreamException { return this._stackTop.localName; }
  
  public final String getNamespaceURI() throws XMLStreamException { return this._stackTop.uri; }
  
  public final String getPrefix() throws XMLStreamException { return this._stackTop.prefix; }
  
  public final String getVersion() throws XMLStreamException { return "1.0"; }
  
  public final boolean isStandalone() { return false; }
  
  public final boolean standaloneSet() { return false; }
  
  public final String getCharacterEncodingScheme() throws XMLStreamException { return "UTF-8"; }
  
  public final String getPITarget() throws XMLStreamException {
    if (this._eventType == 3)
      return this._piTarget; 
    throw new IllegalStateException("");
  }
  
  public final String getPIData() throws XMLStreamException {
    if (this._eventType == 3)
      return this._piData; 
    throw new IllegalStateException("");
  }
  
  protected void processElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    pushElementStack();
    this._stackTop.set(paramString1, paramString2, paramString3);
    this._attributeCache.clear();
    int i = peekStructure();
    if ((i & 0xF0) == 64 || paramBoolean)
      i = processNamespaceAttributes(i, paramBoolean); 
    if ((i & 0xF0) == 48)
      processAttributes(i); 
  }
  
  private boolean isInscope(int paramInt) { return (this._buffer.getInscopeNamespaces().size() > 0 && paramInt == 0); }
  
  private void resizeNamespaceAttributes() {
    String[] arrayOfString1 = new String[this._namespaceAIIsEnd * 2];
    System.arraycopy(this._namespaceAIIsPrefix, 0, arrayOfString1, 0, this._namespaceAIIsEnd);
    this._namespaceAIIsPrefix = arrayOfString1;
    String[] arrayOfString2 = new String[this._namespaceAIIsEnd * 2];
    System.arraycopy(this._namespaceAIIsNamespaceName, 0, arrayOfString2, 0, this._namespaceAIIsEnd);
    this._namespaceAIIsNamespaceName = arrayOfString2;
  }
  
  private int processNamespaceAttributes(int paramInt, boolean paramBoolean) {
    this._stackTop.namespaceAIIsStart = this._namespaceAIIsEnd;
    HashSet hashSet = paramBoolean ? new HashSet() : Collections.emptySet();
    while ((paramInt & 0xF0) == 64) {
      if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length)
        resizeNamespaceAttributes(); 
      switch (getNIIState(paramInt)) {
        case 1:
          this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
          this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = "";
          if (paramBoolean)
            hashSet.add(""); 
          break;
        case 2:
          this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = readStructureString();
          if (paramBoolean)
            hashSet.add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]); 
          this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
          break;
        case 3:
          this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = readStructureString();
          if (paramBoolean)
            hashSet.add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]); 
          this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = readStructureString();
          break;
        case 4:
          this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = "";
          if (paramBoolean)
            hashSet.add(""); 
          this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = readStructureString();
          break;
      } 
      readStructure();
      paramInt = peekStructure();
    } 
    if (paramBoolean)
      for (Map.Entry entry : this._buffer.getInscopeNamespaces().entrySet()) {
        String str = fixNull((String)entry.getKey());
        if (!hashSet.contains(str)) {
          if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length)
            resizeNamespaceAttributes(); 
          this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = str;
          this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = (String)entry.getValue();
        } 
      }  
    this._stackTop.namespaceAIIsEnd = this._namespaceAIIsEnd;
    return paramInt;
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  private void processAttributes(int paramInt) {
    do {
      String str3;
      String str2;
      String str1;
      switch (getAIIState(paramInt)) {
        case 1:
          str1 = readStructureString();
          str2 = readStructureString();
          str3 = getPrefixFromQName(readStructureString());
          this._attributeCache.addAttributeWithPrefix(str3, str1, str2, readStructureString(), readContentString());
          break;
        case 2:
          this._attributeCache.addAttributeWithPrefix(readStructureString(), readStructureString(), readStructureString(), readStructureString(), readContentString());
          break;
        case 3:
          this._attributeCache.addAttributeWithPrefix("", readStructureString(), readStructureString(), readStructureString(), readContentString());
          break;
        case 4:
          this._attributeCache.addAttributeWithPrefix("", "", readStructureString(), readStructureString(), readContentString());
          break;
        default:
          assert false : "Internal XSB Error: wrong attribute state, Item=" + paramInt;
          break;
      } 
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 48);
  }
  
  private void pushElementStack() {
    if (this._depth == this._stack.length) {
      ElementStackEntry[] arrayOfElementStackEntry = this._stack;
      this._stack = new ElementStackEntry[this._stack.length * 3 / 2 + 1];
      System.arraycopy(arrayOfElementStackEntry, 0, this._stack, 0, arrayOfElementStackEntry.length);
      for (int i = arrayOfElementStackEntry.length; i < this._stack.length; i++)
        this._stack[i] = new ElementStackEntry(null); 
    } 
    this._stackTop = this._stack[this._depth++];
  }
  
  private void popElementStack(int paramInt) {
    this._stackTop = this._stack[paramInt - 1];
    this._namespaceAIIsEnd = (this._stack[paramInt]).namespaceAIIsStart;
  }
  
  private static String fixEmptyString(String paramString) { return (paramString.length() == 0) ? null : paramString; }
  
  private class CharSequenceImpl implements CharSequence {
    private final int _offset;
    
    private final int _length;
    
    CharSequenceImpl(int param1Int1, int param1Int2) {
      this._offset = param1Int1;
      this._length = param1Int2;
    }
    
    public int length() throws XMLStreamException { return this._length; }
    
    public char charAt(int param1Int) {
      if (param1Int >= 0 && param1Int < StreamReaderBufferProcessor.this._textLen)
        return StreamReaderBufferProcessor.this._characters[StreamReaderBufferProcessor.this._textOffset + param1Int]; 
      throw new IndexOutOfBoundsException();
    }
    
    public CharSequence subSequence(int param1Int1, int param1Int2) {
      int i = param1Int2 - param1Int1;
      if (param1Int2 < 0 || param1Int1 < 0 || param1Int2 > i || param1Int1 > param1Int2)
        throw new IndexOutOfBoundsException(); 
      return new CharSequenceImpl(StreamReaderBufferProcessor.this, this._offset + param1Int1, i);
    }
    
    public String toString() throws XMLStreamException { return new String(StreamReaderBufferProcessor.this._characters, this._offset, this._length); }
  }
  
  private class DummyLocation implements Location {
    private DummyLocation() {}
    
    public int getLineNumber() throws XMLStreamException { return -1; }
    
    public int getColumnNumber() throws XMLStreamException { return -1; }
    
    public int getCharacterOffset() throws XMLStreamException { return -1; }
    
    public String getPublicId() throws XMLStreamException { return null; }
    
    public String getSystemId() throws XMLStreamException { return StreamReaderBufferProcessor.this._buffer.getSystemId(); }
  }
  
  private final class ElementStackEntry {
    String prefix;
    
    String uri;
    
    String localName;
    
    QName qname;
    
    int namespaceAIIsStart;
    
    int namespaceAIIsEnd;
    
    private ElementStackEntry() {}
    
    public void set(String param1String1, String param1String2, String param1String3) {
      this.prefix = param1String1;
      this.uri = param1String2;
      this.localName = param1String3;
      this.qname = null;
      this.namespaceAIIsStart = this.namespaceAIIsEnd = StreamReaderBufferProcessor.this._namespaceAIIsEnd;
    }
    
    public QName getQName() {
      if (this.qname == null)
        this.qname = new QName(fixNull(this.uri), this.localName, fixNull(this.prefix)); 
      return this.qname;
    }
    
    private String fixNull(String param1String) { return (param1String == null) ? "" : param1String; }
  }
  
  private final class InternalNamespaceContext implements NamespaceContextEx {
    private InternalNamespaceContext() {}
    
    public String getNamespaceURI(String param1String) {
      if (param1String == null)
        throw new IllegalArgumentException("Prefix cannot be null"); 
      if (StreamReaderBufferProcessor.this._stringInterningFeature) {
        param1String = param1String.intern();
        for (int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; i--) {
          if (param1String == StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i])
            return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i]; 
        } 
      } else {
        for (int i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; i--) {
          if (param1String.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]))
            return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i]; 
        } 
      } 
      return param1String.equals("xml") ? "http://www.w3.org/XML/1998/namespace" : (param1String.equals("xmlns") ? "http://www.w3.org/2000/xmlns/" : null);
    }
    
    public String getPrefix(String param1String) {
      Iterator iterator = getPrefixes(param1String);
      return iterator.hasNext() ? (String)iterator.next() : null;
    }
    
    public Iterator getPrefixes(final String namespaceURI) {
      if (param1String == null)
        throw new IllegalArgumentException("NamespaceURI cannot be null"); 
      return param1String.equals("http://www.w3.org/XML/1998/namespace") ? Collections.singletonList("xml").iterator() : (param1String.equals("http://www.w3.org/2000/xmlns/") ? Collections.singletonList("xmlns").iterator() : new Iterator() {
          private int i = this.this$1.this$0._namespaceAIIsEnd - 1;
          
          private boolean requireFindNext = true;
          
          private String p;
          
          private String findNext() throws XMLStreamException {
            while (this.i >= 0) {
              if (namespaceURI.equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i]) && StreamReaderBufferProcessor.InternalNamespaceContext.this.getNamespaceURI(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i]).equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i]))
                return this.p = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i]; 
              this.i--;
            } 
            return this.p = null;
          }
          
          public boolean hasNext() {
            if (this.requireFindNext) {
              findNext();
              this.requireFindNext = false;
            } 
            return (this.p != null);
          }
          
          public Object next() {
            if (this.requireFindNext)
              findNext(); 
            this.requireFindNext = true;
            if (this.p == null)
              throw new NoSuchElementException(); 
            return this.p;
          }
          
          public void remove() { throw new UnsupportedOperationException(); }
        });
    }
    
    public Iterator<NamespaceContextEx.Binding> iterator() { return new Iterator<NamespaceContextEx.Binding>() {
          private final int end = this.this$1.this$0._namespaceAIIsEnd - 1;
          
          private int current = this.end;
          
          private boolean requireFindNext = true;
          
          private NamespaceContextEx.Binding namespace;
          
          private NamespaceContextEx.Binding findNext() {
            while (this.current >= 0) {
              String str = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.current];
              int i;
              for (i = this.end; i > this.current && !str.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]); i--);
              if (i == this.current--)
                return this.namespace = new StreamReaderBufferProcessor.InternalNamespaceContext.BindingImpl(StreamReaderBufferProcessor.InternalNamespaceContext.this, str, StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.current]); 
            } 
            return this.namespace = null;
          }
          
          public boolean hasNext() {
            if (this.requireFindNext) {
              findNext();
              this.requireFindNext = false;
            } 
            return (this.namespace != null);
          }
          
          public NamespaceContextEx.Binding next() {
            if (this.requireFindNext)
              findNext(); 
            this.requireFindNext = true;
            if (this.namespace == null)
              throw new NoSuchElementException(); 
            return this.namespace;
          }
          
          public void remove() { throw new UnsupportedOperationException(); }
        }; }
    
    private class BindingImpl implements NamespaceContextEx.Binding {
      final String _prefix;
      
      final String _namespaceURI;
      
      BindingImpl(String param2String1, String param2String2) {
        this._prefix = param2String1;
        this._namespaceURI = param2String2;
      }
      
      public String getPrefix() throws XMLStreamException { return this._prefix; }
      
      public String getNamespaceURI() throws XMLStreamException { return this._namespaceURI; }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamReaderBufferProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */