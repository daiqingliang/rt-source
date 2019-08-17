package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.EncodingConstants;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmState;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetReader;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXDocumentParser extends Decoder implements FastInfosetReader {
  private static final Logger logger = Logger.getLogger(SAXDocumentParser.class.getName());
  
  protected boolean _namespacePrefixesFeature = false;
  
  protected EntityResolver _entityResolver;
  
  protected DTDHandler _dtdHandler;
  
  protected ContentHandler _contentHandler;
  
  protected ErrorHandler _errorHandler;
  
  protected LexicalHandler _lexicalHandler;
  
  protected DeclHandler _declHandler;
  
  protected EncodingAlgorithmContentHandler _algorithmHandler;
  
  protected PrimitiveTypeContentHandler _primitiveHandler;
  
  protected BuiltInEncodingAlgorithmState builtInAlgorithmState = new BuiltInEncodingAlgorithmState();
  
  protected AttributesHolder _attributes;
  
  protected int[] _namespacePrefixes = new int[16];
  
  protected int _namespacePrefixesIndex;
  
  protected boolean _clearAttributes = false;
  
  public SAXDocumentParser() {
    DefaultHandler defaultHandler = new DefaultHandler();
    this._attributes = new AttributesHolder(this._registeredEncodingAlgorithms);
    this._entityResolver = defaultHandler;
    this._dtdHandler = defaultHandler;
    this._contentHandler = defaultHandler;
    this._errorHandler = defaultHandler;
    this._lexicalHandler = new LexicalHandlerImpl(null);
    this._declHandler = new DeclHandlerImpl(null);
  }
  
  protected void resetOnError() {
    this._clearAttributes = false;
    this._attributes.clear();
    this._namespacePrefixesIndex = 0;
    if (this._v != null)
      this._v.prefix.clearCompletely(); 
    this._duplicateAttributeVerifier.clear();
  }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/features/namespaces"))
      return true; 
    if (paramString.equals("http://xml.org/sax/features/namespace-prefixes"))
      return this._namespacePrefixesFeature; 
    if (paramString.equals("http://xml.org/sax/features/string-interning") || paramString.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning"))
      return getStringInterning(); 
    throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/features/namespaces")) {
      if (!paramBoolean)
        throw new SAXNotSupportedException(paramString + ":" + paramBoolean); 
    } else if (paramString.equals("http://xml.org/sax/features/namespace-prefixes")) {
      this._namespacePrefixesFeature = paramBoolean;
    } else if (paramString.equals("http://xml.org/sax/features/string-interning") || paramString.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
      setStringInterning(paramBoolean);
    } else {
      throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + paramString);
    } 
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler"))
      return getLexicalHandler(); 
    if (paramString.equals("http://xml.org/sax/properties/declaration-handler"))
      return getDeclHandler(); 
    if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies"))
      return getExternalVocabularies(); 
    if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms"))
      return getRegisteredEncodingAlgorithms(); 
    if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler"))
      return getEncodingAlgorithmContentHandler(); 
    if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler"))
      return getPrimitiveTypeContentHandler(); 
    throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[] { paramString }));
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler")) {
      if (paramObject instanceof LexicalHandler) {
        setLexicalHandler((LexicalHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
      } 
    } else if (paramString.equals("http://xml.org/sax/properties/declaration-handler")) {
      if (paramObject instanceof DeclHandler) {
        setDeclHandler((DeclHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
      } 
    } else if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
      if (paramObject instanceof Map) {
        setExternalVocabularies((Map)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies");
      } 
    } else if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
      if (paramObject instanceof Map) {
        setRegisteredEncodingAlgorithms((Map)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms");
      } 
    } else if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
      if (paramObject instanceof EncodingAlgorithmContentHandler) {
        setEncodingAlgorithmContentHandler((EncodingAlgorithmContentHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler");
      } 
    } else if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
      if (paramObject instanceof PrimitiveTypeContentHandler) {
        setPrimitiveTypeContentHandler((PrimitiveTypeContentHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler");
      } 
    } else if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/buffer-size")) {
      if (paramObject instanceof Integer) {
        setBufferSize(((Integer)paramObject).intValue());
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/buffer-size");
      } 
    } else {
      throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[] { paramString }));
    } 
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) { this._entityResolver = paramEntityResolver; }
  
  public EntityResolver getEntityResolver() { return this._entityResolver; }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) { this._dtdHandler = paramDTDHandler; }
  
  public DTDHandler getDTDHandler() { return this._dtdHandler; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this._contentHandler = paramContentHandler; }
  
  public ContentHandler getContentHandler() { return this._contentHandler; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this._errorHandler = paramErrorHandler; }
  
  public ErrorHandler getErrorHandler() { return this._errorHandler; }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException {
    try {
      InputStream inputStream = paramInputSource.getByteStream();
      if (inputStream == null) {
        String str = paramInputSource.getSystemId();
        if (str == null)
          throw new SAXException(CommonResourceBundle.getInstance().getString("message.inputSource")); 
        parse(str);
      } else {
        parse(inputStream);
      } 
    } catch (FastInfosetException fastInfosetException) {
      logger.log(Level.FINE, "parsing error", fastInfosetException);
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public void parse(String paramString) throws IOException, SAXException {
    try {
      paramString = SystemIdResolver.getAbsoluteURI(paramString);
      parse((new URL(paramString)).openStream());
    } catch (FastInfosetException fastInfosetException) {
      logger.log(Level.FINE, "parsing error", fastInfosetException);
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void parse(InputStream paramInputStream) throws IOException, FastInfosetException, SAXException {
    setInputStream(paramInputStream);
    parse();
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) { this._lexicalHandler = paramLexicalHandler; }
  
  public LexicalHandler getLexicalHandler() { return this._lexicalHandler; }
  
  public void setDeclHandler(DeclHandler paramDeclHandler) { this._declHandler = paramDeclHandler; }
  
  public DeclHandler getDeclHandler() { return this._declHandler; }
  
  public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler paramEncodingAlgorithmContentHandler) { this._algorithmHandler = paramEncodingAlgorithmContentHandler; }
  
  public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler() { return this._algorithmHandler; }
  
  public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler paramPrimitiveTypeContentHandler) { this._primitiveHandler = paramPrimitiveTypeContentHandler; }
  
  public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler() { return this._primitiveHandler; }
  
  public final void parse() {
    if (this._octetBuffer.length < this._bufferSize)
      this._octetBuffer = new byte[this._bufferSize]; 
    try {
      reset();
      decodeHeader();
      if (this._parseFragments) {
        processDIIFragment();
      } else {
        processDII();
      } 
    } catch (RuntimeException runtimeException) {
      try {
        this._errorHandler.fatalError(new SAXParseException(runtimeException.getClass().getName(), null, runtimeException));
      } catch (Exception exception) {}
      resetOnError();
      throw new FastInfosetException(runtimeException);
    } catch (FastInfosetException fastInfosetException) {
      try {
        this._errorHandler.fatalError(new SAXParseException(fastInfosetException.getClass().getName(), null, fastInfosetException));
      } catch (Exception exception) {}
      resetOnError();
      throw fastInfosetException;
    } catch (IOException iOException) {
      try {
        this._errorHandler.fatalError(new SAXParseException(iOException.getClass().getName(), null, iOException));
      } catch (Exception exception) {}
      resetOnError();
      throw iOException;
    } 
  }
  
  protected final void processDII() {
    try {
      this._contentHandler.startDocument();
    } catch (SAXException sAXException) {
      throw new FastInfosetException("processDII", sAXException);
    } 
    this._b = read();
    if (this._b > 0)
      processDIIOptionalProperties(); 
    boolean bool1 = false;
    boolean bool2 = false;
    while (!this._terminate || !bool1) {
      String str2;
      String str1;
      QualifiedName qualifiedName;
      this._b = read();
      switch (DecoderStateTables.DII(this._b)) {
        case 0:
          processEII(this._elementNameTable._array[this._b], false);
          bool1 = true;
          continue;
        case 1:
          processEII(this._elementNameTable._array[this._b & 0x1F], true);
          bool1 = true;
          continue;
        case 2:
          processEII(decodeEIIIndexMedium(), ((this._b & 0x40) > 0));
          bool1 = true;
          continue;
        case 3:
          processEII(decodeEIIIndexLarge(), ((this._b & 0x40) > 0));
          bool1 = true;
          continue;
        case 5:
          qualifiedName = decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
          this._elementNameTable.add(qualifiedName);
          processEII(qualifiedName, ((this._b & 0x40) > 0));
          bool1 = true;
          continue;
        case 4:
          processEIIWithNamespaces();
          bool1 = true;
          continue;
        case 20:
          if (bool2)
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII")); 
          bool2 = true;
          str1 = ((this._b & 0x2) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
          str2 = ((this._b & true) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
          this._b = read();
          while (this._b == 225) {
            switch (decodeNonIdentifyingStringOnFirstBit()) {
              case 0:
                if (this._addToTable)
                  this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true)); 
                break;
              case 2:
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            } 
            this._b = read();
          } 
          if ((this._b & 0xF0) != 240)
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly")); 
          if (this._b == 255)
            this._terminate = true; 
          if (this._notations != null)
            this._notations.clear(); 
          if (this._unparsedEntities != null)
            this._unparsedEntities.clear(); 
          continue;
        case 18:
          processCommentII();
          continue;
        case 19:
          processProcessingII();
          continue;
        case 23:
          this._doubleTerminate = true;
        case 22:
          this._terminate = true;
          continue;
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
    } 
    while (!this._terminate) {
      this._b = read();
      switch (DecoderStateTables.DII(this._b)) {
        case 18:
          processCommentII();
          continue;
        case 19:
          processProcessingII();
          continue;
        case 23:
          this._doubleTerminate = true;
        case 22:
          this._terminate = true;
          continue;
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
    } 
    try {
      this._contentHandler.endDocument();
    } catch (SAXException sAXException) {
      throw new FastInfosetException("processDII", sAXException);
    } 
  }
  
  protected final void processDIIFragment() {
    try {
      this._contentHandler.startDocument();
    } catch (SAXException sAXException) {
      throw new FastInfosetException("processDII", sAXException);
    } 
    this._b = read();
    if (this._b > 0)
      processDIIOptionalProperties(); 
    while (!this._terminate) {
      String str3;
      String str2;
      String str1;
      boolean bool;
      int i;
      this._b = read();
      switch (DecoderStateTables.EII(this._b)) {
        case 0:
          processEII(this._elementNameTable._array[this._b], false);
          continue;
        case 1:
          processEII(this._elementNameTable._array[this._b & 0x1F], true);
          continue;
        case 2:
          processEII(decodeEIIIndexMedium(), ((this._b & 0x40) > 0));
          continue;
        case 3:
          processEII(decodeEIIIndexLarge(), ((this._b & 0x40) > 0));
          continue;
        case 5:
          qualifiedName = decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
          this._elementNameTable.add(qualifiedName);
          processEII(qualifiedName, ((this._b & 0x40) > 0));
          continue;
        case 4:
          processEIIWithNamespaces();
          continue;
        case 6:
          this._octetBufferLength = (this._b & true) + 1;
          processUtf8CharacterString();
          continue;
        case 7:
          this._octetBufferLength = read() + 3;
          processUtf8CharacterString();
          continue;
        case 8:
          this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read()) + 259;
          processUtf8CharacterString();
          continue;
        case 9:
          this._octetBufferLength = (this._b & true) + 1;
          decodeUtf16StringAsCharBuffer();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException qualifiedName) {
            throw new FastInfosetException("processCII", qualifiedName);
          } 
        case 10:
          this._octetBufferLength = read() + 3;
          decodeUtf16StringAsCharBuffer();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException qualifiedName) {
            throw new FastInfosetException("processCII", qualifiedName);
          } 
        case 11:
          this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read()) + 259;
          decodeUtf16StringAsCharBuffer();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException qualifiedName) {
            throw new FastInfosetException("processCII", qualifiedName);
          } 
        case 12:
          bool = ((this._b & 0x10) > 0);
          this._identifier = (this._b & 0x2) << 6;
          this._b = read();
          this._identifier |= (this._b & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
          decodeRestrictedAlphabetAsCharBuffer();
          if (bool)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 13:
          bool = ((this._b & 0x10) > 0);
          this._identifier = (this._b & 0x2) << 6;
          this._b = read();
          this._identifier |= (this._b & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
          processCIIEncodingAlgorithm(bool);
          continue;
        case 14:
          i = this._b & 0xF;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 15:
          i = ((this._b & 0x3) << 8 | read()) + 16;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 16:
          i = ((this._b & 0x3) << 16 | read() << 8 | read()) + 1040;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 17:
          i = (read() << 16 | read() << 8 | read()) + 263184;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 18:
          processCommentII();
          continue;
        case 19:
          processProcessingII();
          continue;
        case 21:
          str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
          str2 = ((this._b & 0x2) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
          str3 = ((this._b & true) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
          try {
            this._contentHandler.skippedEntity(str1);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processUnexpandedEntityReferenceII", sAXException);
          } 
        case 23:
          this._doubleTerminate = true;
        case 22:
          this._terminate = true;
          continue;
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
    } 
    try {
      this._contentHandler.endDocument();
    } catch (SAXException sAXException) {
      throw new FastInfosetException("processDII", sAXException);
    } 
  }
  
  protected final void processDIIOptionalProperties() {
    if (this._b == 32) {
      decodeInitialVocabulary();
      return;
    } 
    if ((this._b & 0x40) > 0)
      decodeAdditionalData(); 
    if ((this._b & 0x20) > 0)
      decodeInitialVocabulary(); 
    if ((this._b & 0x10) > 0)
      decodeNotations(); 
    if ((this._b & 0x8) > 0)
      decodeUnparsedEntities(); 
    if ((this._b & 0x4) > 0)
      decodeCharacterEncodingScheme(); 
    if ((this._b & 0x2) > 0)
      read(); 
    if ((this._b & true) > 0)
      decodeVersion(); 
  }
  
  protected final void processEII(QualifiedName paramQualifiedName, boolean paramBoolean) throws FastInfosetException, IOException {
    if (this._prefixTable._currentInScope[paramQualifiedName.prefixIndex] != paramQualifiedName.namespaceNameIndex)
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameOfEIINotInScope")); 
    if (paramBoolean)
      processAIIs(); 
    try {
      this._contentHandler.startElement(paramQualifiedName.namespaceName, paramQualifiedName.localName, paramQualifiedName.qName, this._attributes);
    } catch (SAXException sAXException) {
      logger.log(Level.FINE, "processEII error", sAXException);
      throw new FastInfosetException("processEII", sAXException);
    } 
    if (this._clearAttributes) {
      this._attributes.clear();
      this._clearAttributes = false;
    } 
    while (!this._terminate) {
      String str3;
      String str2;
      String str1;
      int i;
      boolean bool;
      this._b = read();
      switch (DecoderStateTables.EII(this._b)) {
        case 0:
          processEII(this._elementNameTable._array[this._b], false);
          continue;
        case 1:
          processEII(this._elementNameTable._array[this._b & 0x1F], true);
          continue;
        case 2:
          processEII(decodeEIIIndexMedium(), ((this._b & 0x40) > 0));
          continue;
        case 3:
          processEII(decodeEIIIndexLarge(), ((this._b & 0x40) > 0));
          continue;
        case 5:
          qualifiedName = decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
          this._elementNameTable.add(qualifiedName);
          processEII(qualifiedName, ((this._b & 0x40) > 0));
          continue;
        case 4:
          processEIIWithNamespaces();
          continue;
        case 6:
          this._octetBufferLength = (this._b & true) + 1;
          processUtf8CharacterString();
          continue;
        case 7:
          this._octetBufferLength = read() + 3;
          processUtf8CharacterString();
          continue;
        case 8:
          this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read()) + 259;
          processUtf8CharacterString();
          continue;
        case 9:
          this._octetBufferLength = (this._b & true) + 1;
          decodeUtf16StringAsCharBuffer();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException qualifiedName) {
            throw new FastInfosetException("processCII", qualifiedName);
          } 
        case 10:
          this._octetBufferLength = read() + 3;
          decodeUtf16StringAsCharBuffer();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException qualifiedName) {
            throw new FastInfosetException("processCII", qualifiedName);
          } 
        case 11:
          this._octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read()) + 259;
          decodeUtf16StringAsCharBuffer();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException qualifiedName) {
            throw new FastInfosetException("processCII", qualifiedName);
          } 
        case 12:
          bool = ((this._b & 0x10) > 0);
          this._identifier = (this._b & 0x2) << 6;
          this._b = read();
          this._identifier |= (this._b & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
          decodeRestrictedAlphabetAsCharBuffer();
          if (bool)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 13:
          bool = ((this._b & 0x10) > 0);
          this._identifier = (this._b & 0x2) << 6;
          this._b = read();
          this._identifier |= (this._b & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
          processCIIEncodingAlgorithm(bool);
          continue;
        case 14:
          i = this._b & 0xF;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 15:
          i = ((this._b & 0x3) << 8 | read()) + 16;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 16:
          i = ((this._b & 0x3) << 16 | read() << 8 | read()) + 1040;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 17:
          i = (read() << 16 | read() << 8 | read()) + 263184;
          try {
            this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[i], this._characterContentChunkTable._length[i]);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processCII", sAXException);
          } 
        case 18:
          processCommentII();
          continue;
        case 19:
          processProcessingII();
          continue;
        case 21:
          str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
          str2 = ((this._b & 0x2) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
          str3 = ((this._b & true) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
          try {
            this._contentHandler.skippedEntity(str1);
            continue;
          } catch (SAXException sAXException) {
            throw new FastInfosetException("processUnexpandedEntityReferenceII", sAXException);
          } 
        case 23:
          this._doubleTerminate = true;
        case 22:
          this._terminate = true;
          continue;
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
    } 
    this._terminate = this._doubleTerminate;
    this._doubleTerminate = false;
    try {
      this._contentHandler.endElement(paramQualifiedName.namespaceName, paramQualifiedName.localName, paramQualifiedName.qName);
    } catch (SAXException sAXException) {
      throw new FastInfosetException("processEII", sAXException);
    } 
  }
  
  private final void processUtf8CharacterString() {
    if ((this._b & 0x10) > 0) {
      this._characterContentChunkTable.ensureSize(this._octetBufferLength);
      int i = this._characterContentChunkTable._arrayIndex;
      decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, i);
      this._characterContentChunkTable.add(this._charBufferLength);
      try {
        this._contentHandler.characters(this._characterContentChunkTable._array, i, this._charBufferLength);
      } catch (SAXException sAXException) {
        throw new FastInfosetException("processCII", sAXException);
      } 
    } else {
      decodeUtf8StringAsCharBuffer();
      try {
        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
      } catch (SAXException sAXException) {
        throw new FastInfosetException("processCII", sAXException);
      } 
    } 
  }
  
  protected final void processEIIWithNamespaces() {
    boolean bool = ((this._b & 0x40) > 0);
    this._clearAttributes = this._namespacePrefixesFeature;
    if (++this._prefixTable._declarationId == Integer.MAX_VALUE)
      this._prefixTable.clearDeclarationIds(); 
    String str1 = "";
    String str2 = "";
    int i = this._namespacePrefixesIndex;
    int j;
    for (j = read(); (j & 0xFC) == 204; j = read()) {
      if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
        int[] arrayOfInt = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
        System.arraycopy(this._namespacePrefixes, 0, arrayOfInt, 0, this._namespacePrefixesIndex);
        this._namespacePrefixes = arrayOfInt;
      } 
      switch (j & 0x3) {
        case 0:
          str1 = str2 = "";
          this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
          this._namespaceNameIndex = this._prefixIndex = -1;
          break;
        case 1:
          str1 = "";
          str2 = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
          this._prefixIndex = this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
          break;
        case 2:
          str1 = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
          str2 = "";
          this._namespaceNameIndex = -1;
          this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
          break;
        case 3:
          str1 = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
          str2 = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
          this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
          break;
      } 
      this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
      if (this._namespacePrefixesFeature)
        if (str1 != "") {
          this._attributes.addAttribute(new QualifiedName("xmlns", "http://www.w3.org/2000/xmlns/", str1), str2);
        } else {
          this._attributes.addAttribute(EncodingConstants.DEFAULT_NAMESPACE_DECLARATION, str2);
        }  
      try {
        this._contentHandler.startPrefixMapping(str1, str2);
      } catch (SAXException sAXException) {
        throw new IOException("processStartNamespaceAII");
      } 
    } 
    if (j != 240)
      throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly")); 
    int k = this._namespacePrefixesIndex;
    this._b = read();
    switch (DecoderStateTables.EII(this._b)) {
      case 0:
        processEII(this._elementNameTable._array[this._b], bool);
        break;
      case 2:
        processEII(decodeEIIIndexMedium(), bool);
        break;
      case 3:
        processEII(decodeEIIIndexLarge(), bool);
        break;
      case 5:
        qualifiedName = decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
        this._elementNameTable.add(qualifiedName);
        processEII(qualifiedName, bool);
        break;
      default:
        throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
    } 
    try {
      for (int m = k - 1; m >= i; m--) {
        int n = this._namespacePrefixes[m];
        this._prefixTable.popScope(n);
        str1 = (n > 0) ? this._prefixTable.get(n - 1) : ((n == -1) ? "" : "xml");
        this._contentHandler.endPrefixMapping(str1);
      } 
      this._namespacePrefixesIndex = i;
    } catch (SAXException qualifiedName) {
      throw new IOException("processStartNamespaceAII");
    } 
  }
  
  protected final void processAIIs() { // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield _clearAttributes : Z
    //   5: aload_0
    //   6: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   9: dup
    //   10: getfield _currentIteration : I
    //   13: iconst_1
    //   14: iadd
    //   15: dup_x1
    //   16: putfield _currentIteration : I
    //   19: ldc 2147483647
    //   21: if_icmpne -> 31
    //   24: aload_0
    //   25: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   28: invokevirtual clear : ()V
    //   31: aload_0
    //   32: invokevirtual read : ()I
    //   35: istore_2
    //   36: iload_2
    //   37: invokestatic AII : (I)I
    //   40: tableswitch default -> 215, 0 -> 80, 1 -> 93, 2 -> 124, 3 -> 164, 4 -> 207, 5 -> 202
    //   80: aload_0
    //   81: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   84: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   87: iload_2
    //   88: aaload
    //   89: astore_1
    //   90: goto -> 231
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
    //   108: istore #4
    //   110: aload_0
    //   111: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   114: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   117: iload #4
    //   119: aaload
    //   120: astore_1
    //   121: goto -> 231
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
    //   148: istore #4
    //   150: aload_0
    //   151: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   154: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   157: iload #4
    //   159: aaload
    //   160: astore_1
    //   161: goto -> 231
    //   164: aload_0
    //   165: iload_2
    //   166: iconst_3
    //   167: iand
    //   168: aload_0
    //   169: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   172: invokevirtual getNext : ()Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   175: invokevirtual decodeLiteralQualifiedName : (ILcom/sun/xml/internal/fastinfoset/QualifiedName;)Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   178: astore_1
    //   179: aload_1
    //   180: aload_0
    //   181: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   184: pop
    //   185: sipush #256
    //   188: invokevirtual createAttributeValues : (I)V
    //   191: aload_0
    //   192: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   195: aload_1
    //   196: invokevirtual add : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;)V
    //   199: goto -> 231
    //   202: aload_0
    //   203: iconst_1
    //   204: putfield _doubleTerminate : Z
    //   207: aload_0
    //   208: iconst_1
    //   209: putfield _terminate : Z
    //   212: goto -> 950
    //   215: new java/io/IOException
    //   218: dup
    //   219: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   222: ldc 'message.decodingAIIs'
    //   224: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   227: invokespecial <init> : (Ljava/lang/String;)V
    //   230: athrow
    //   231: aload_1
    //   232: getfield prefixIndex : I
    //   235: ifle -> 273
    //   238: aload_0
    //   239: getfield _prefixTable : Lcom/sun/xml/internal/fastinfoset/util/PrefixArray;
    //   242: getfield _currentInScope : [I
    //   245: aload_1
    //   246: getfield prefixIndex : I
    //   249: iaload
    //   250: aload_1
    //   251: getfield namespaceNameIndex : I
    //   254: if_icmpeq -> 273
    //   257: new com/sun/xml/internal/org/jvnet/fastinfoset/FastInfosetException
    //   260: dup
    //   261: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   264: ldc 'message.AIIqNameNotInScope'
    //   266: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   269: invokespecial <init> : (Ljava/lang/String;)V
    //   272: athrow
    //   273: aload_0
    //   274: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   277: aload_1
    //   278: getfield attributeHash : I
    //   281: aload_1
    //   282: getfield attributeId : I
    //   285: invokevirtual checkForDuplicateAttribute : (II)V
    //   288: aload_0
    //   289: invokevirtual read : ()I
    //   292: istore_2
    //   293: iload_2
    //   294: invokestatic NISTRING : (I)I
    //   297: tableswitch default -> 934, 0 -> 360, 1 -> 403, 2 -> 447, 3 -> 516, 4 -> 559, 5 -> 603, 6 -> 672, 7 -> 753, 8 -> 813, 9 -> 836, 10 -> 874, 11 -> 921
    //   360: aload_0
    //   361: iload_2
    //   362: bipush #7
    //   364: iand
    //   365: iconst_1
    //   366: iadd
    //   367: putfield _octetBufferLength : I
    //   370: aload_0
    //   371: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   374: astore_3
    //   375: iload_2
    //   376: bipush #64
    //   378: iand
    //   379: ifle -> 391
    //   382: aload_0
    //   383: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   386: aload_3
    //   387: invokevirtual add : (Ljava/lang/String;)I
    //   390: pop
    //   391: aload_0
    //   392: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   395: aload_1
    //   396: aload_3
    //   397: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   400: goto -> 950
    //   403: aload_0
    //   404: aload_0
    //   405: invokevirtual read : ()I
    //   408: bipush #9
    //   410: iadd
    //   411: putfield _octetBufferLength : I
    //   414: aload_0
    //   415: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   418: astore_3
    //   419: iload_2
    //   420: bipush #64
    //   422: iand
    //   423: ifle -> 435
    //   426: aload_0
    //   427: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   430: aload_3
    //   431: invokevirtual add : (Ljava/lang/String;)I
    //   434: pop
    //   435: aload_0
    //   436: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   439: aload_1
    //   440: aload_3
    //   441: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   444: goto -> 950
    //   447: aload_0
    //   448: aload_0
    //   449: invokevirtual read : ()I
    //   452: bipush #24
    //   454: ishl
    //   455: aload_0
    //   456: invokevirtual read : ()I
    //   459: bipush #16
    //   461: ishl
    //   462: ior
    //   463: aload_0
    //   464: invokevirtual read : ()I
    //   467: bipush #8
    //   469: ishl
    //   470: ior
    //   471: aload_0
    //   472: invokevirtual read : ()I
    //   475: ior
    //   476: sipush #265
    //   479: iadd
    //   480: putfield _octetBufferLength : I
    //   483: aload_0
    //   484: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   487: astore_3
    //   488: iload_2
    //   489: bipush #64
    //   491: iand
    //   492: ifle -> 504
    //   495: aload_0
    //   496: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   499: aload_3
    //   500: invokevirtual add : (Ljava/lang/String;)I
    //   503: pop
    //   504: aload_0
    //   505: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   508: aload_1
    //   509: aload_3
    //   510: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   513: goto -> 950
    //   516: aload_0
    //   517: iload_2
    //   518: bipush #7
    //   520: iand
    //   521: iconst_1
    //   522: iadd
    //   523: putfield _octetBufferLength : I
    //   526: aload_0
    //   527: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   530: astore_3
    //   531: iload_2
    //   532: bipush #64
    //   534: iand
    //   535: ifle -> 547
    //   538: aload_0
    //   539: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   542: aload_3
    //   543: invokevirtual add : (Ljava/lang/String;)I
    //   546: pop
    //   547: aload_0
    //   548: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   551: aload_1
    //   552: aload_3
    //   553: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   556: goto -> 950
    //   559: aload_0
    //   560: aload_0
    //   561: invokevirtual read : ()I
    //   564: bipush #9
    //   566: iadd
    //   567: putfield _octetBufferLength : I
    //   570: aload_0
    //   571: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   574: astore_3
    //   575: iload_2
    //   576: bipush #64
    //   578: iand
    //   579: ifle -> 591
    //   582: aload_0
    //   583: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   586: aload_3
    //   587: invokevirtual add : (Ljava/lang/String;)I
    //   590: pop
    //   591: aload_0
    //   592: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   595: aload_1
    //   596: aload_3
    //   597: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   600: goto -> 950
    //   603: aload_0
    //   604: aload_0
    //   605: invokevirtual read : ()I
    //   608: bipush #24
    //   610: ishl
    //   611: aload_0
    //   612: invokevirtual read : ()I
    //   615: bipush #16
    //   617: ishl
    //   618: ior
    //   619: aload_0
    //   620: invokevirtual read : ()I
    //   623: bipush #8
    //   625: ishl
    //   626: ior
    //   627: aload_0
    //   628: invokevirtual read : ()I
    //   631: ior
    //   632: sipush #265
    //   635: iadd
    //   636: putfield _octetBufferLength : I
    //   639: aload_0
    //   640: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   643: astore_3
    //   644: iload_2
    //   645: bipush #64
    //   647: iand
    //   648: ifle -> 660
    //   651: aload_0
    //   652: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   655: aload_3
    //   656: invokevirtual add : (Ljava/lang/String;)I
    //   659: pop
    //   660: aload_0
    //   661: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   664: aload_1
    //   665: aload_3
    //   666: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   669: goto -> 950
    //   672: iload_2
    //   673: bipush #64
    //   675: iand
    //   676: ifle -> 683
    //   679: iconst_1
    //   680: goto -> 684
    //   683: iconst_0
    //   684: istore #4
    //   686: aload_0
    //   687: iload_2
    //   688: bipush #15
    //   690: iand
    //   691: iconst_4
    //   692: ishl
    //   693: putfield _identifier : I
    //   696: aload_0
    //   697: invokevirtual read : ()I
    //   700: istore_2
    //   701: aload_0
    //   702: dup
    //   703: getfield _identifier : I
    //   706: iload_2
    //   707: sipush #240
    //   710: iand
    //   711: iconst_4
    //   712: ishr
    //   713: ior
    //   714: putfield _identifier : I
    //   717: aload_0
    //   718: iload_2
    //   719: invokevirtual decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit : (I)V
    //   722: aload_0
    //   723: invokevirtual decodeRestrictedAlphabetAsString : ()Ljava/lang/String;
    //   726: astore_3
    //   727: iload #4
    //   729: ifeq -> 741
    //   732: aload_0
    //   733: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   736: aload_3
    //   737: invokevirtual add : (Ljava/lang/String;)I
    //   740: pop
    //   741: aload_0
    //   742: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   745: aload_1
    //   746: aload_3
    //   747: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   750: goto -> 950
    //   753: iload_2
    //   754: bipush #64
    //   756: iand
    //   757: ifle -> 764
    //   760: iconst_1
    //   761: goto -> 765
    //   764: iconst_0
    //   765: istore #4
    //   767: aload_0
    //   768: iload_2
    //   769: bipush #15
    //   771: iand
    //   772: iconst_4
    //   773: ishl
    //   774: putfield _identifier : I
    //   777: aload_0
    //   778: invokevirtual read : ()I
    //   781: istore_2
    //   782: aload_0
    //   783: dup
    //   784: getfield _identifier : I
    //   787: iload_2
    //   788: sipush #240
    //   791: iand
    //   792: iconst_4
    //   793: ishr
    //   794: ior
    //   795: putfield _identifier : I
    //   798: aload_0
    //   799: iload_2
    //   800: invokevirtual decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit : (I)V
    //   803: aload_0
    //   804: aload_1
    //   805: iload #4
    //   807: invokevirtual processAIIEncodingAlgorithm : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Z)V
    //   810: goto -> 950
    //   813: aload_0
    //   814: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   817: aload_1
    //   818: aload_0
    //   819: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   822: getfield _array : [Ljava/lang/String;
    //   825: iload_2
    //   826: bipush #63
    //   828: iand
    //   829: aaload
    //   830: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   833: goto -> 950
    //   836: iload_2
    //   837: bipush #31
    //   839: iand
    //   840: bipush #8
    //   842: ishl
    //   843: aload_0
    //   844: invokevirtual read : ()I
    //   847: ior
    //   848: bipush #64
    //   850: iadd
    //   851: istore #4
    //   853: aload_0
    //   854: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   857: aload_1
    //   858: aload_0
    //   859: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   862: getfield _array : [Ljava/lang/String;
    //   865: iload #4
    //   867: aaload
    //   868: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   871: goto -> 950
    //   874: iload_2
    //   875: bipush #15
    //   877: iand
    //   878: bipush #16
    //   880: ishl
    //   881: aload_0
    //   882: invokevirtual read : ()I
    //   885: bipush #8
    //   887: ishl
    //   888: ior
    //   889: aload_0
    //   890: invokevirtual read : ()I
    //   893: ior
    //   894: sipush #8256
    //   897: iadd
    //   898: istore #4
    //   900: aload_0
    //   901: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   904: aload_1
    //   905: aload_0
    //   906: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   909: getfield _array : [Ljava/lang/String;
    //   912: iload #4
    //   914: aaload
    //   915: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   918: goto -> 950
    //   921: aload_0
    //   922: getfield _attributes : Lcom/sun/xml/internal/fastinfoset/sax/AttributesHolder;
    //   925: aload_1
    //   926: ldc ''
    //   928: invokevirtual addAttribute : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;Ljava/lang/String;)V
    //   931: goto -> 950
    //   934: new java/io/IOException
    //   937: dup
    //   938: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   941: ldc 'message.decodingAIIValue'
    //   943: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   946: invokespecial <init> : (Ljava/lang/String;)V
    //   949: athrow
    //   950: aload_0
    //   951: getfield _terminate : Z
    //   954: ifeq -> 31
    //   957: aload_0
    //   958: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   961: aload_0
    //   962: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   965: getfield _poolHead : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier$Entry;
    //   968: putfield _poolCurrent : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier$Entry;
    //   971: aload_0
    //   972: aload_0
    //   973: getfield _doubleTerminate : Z
    //   976: putfield _terminate : Z
    //   979: aload_0
    //   980: iconst_0
    //   981: putfield _doubleTerminate : Z
    //   984: return }
  
  protected final void processCommentII() {
    CharArray charArray;
    switch (decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
        if (this._addToTable)
          this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true)); 
        try {
          this._lexicalHandler.comment(this._charBuffer, 0, this._charBufferLength);
        } catch (SAXException sAXException) {
          throw new FastInfosetException("processCommentII", sAXException);
        } 
        break;
      case 2:
        throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
      case 1:
        charArray = this._v.otherString.get(this._integer);
        try {
          this._lexicalHandler.comment(charArray.ch, charArray.start, charArray.length);
        } catch (SAXException sAXException) {
          throw new FastInfosetException("processCommentII", sAXException);
        } 
        break;
      case 3:
        try {
          this._lexicalHandler.comment(this._charBuffer, 0, 0);
        } catch (SAXException sAXException) {
          throw new FastInfosetException("processCommentII", sAXException);
        } 
        break;
    } 
  }
  
  protected final void processProcessingII() {
    String str2;
    String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
    switch (decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
        str2 = new String(this._charBuffer, 0, this._charBufferLength);
        if (this._addToTable)
          this._v.otherString.add(new CharArrayString(str2)); 
        try {
          this._contentHandler.processingInstruction(str1, str2);
        } catch (SAXException sAXException) {
          throw new FastInfosetException("processProcessingII", sAXException);
        } 
        break;
      case 2:
        throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
      case 1:
        try {
          this._contentHandler.processingInstruction(str1, this._v.otherString.get(this._integer).toString());
        } catch (SAXException sAXException) {
          throw new FastInfosetException("processProcessingII", sAXException);
        } 
        break;
      case 3:
        try {
          this._contentHandler.processingInstruction(str1, "");
        } catch (SAXException sAXException) {
          throw new FastInfosetException("processProcessingII", sAXException);
        } 
        break;
    } 
  }
  
  protected final void processCIIEncodingAlgorithm(boolean paramBoolean) throws FastInfosetException, IOException {
    if (this._identifier < 9) {
      if (this._primitiveHandler != null) {
        processCIIBuiltInEncodingAlgorithmAsPrimitive();
      } else if (this._algorithmHandler != null) {
        Object object = processBuiltInEncodingAlgorithmAsObject();
        try {
          this._algorithmHandler.object(null, this._identifier, object);
        } catch (SAXException sAXException) {
          throw new FastInfosetException(sAXException);
        } 
      } else {
        StringBuffer stringBuffer = new StringBuffer();
        processBuiltInEncodingAlgorithmAsCharacters(stringBuffer);
        try {
          this._contentHandler.characters(stringBuffer.toString().toCharArray(), 0, stringBuffer.length());
        } catch (SAXException sAXException) {
          throw new FastInfosetException(sAXException);
        } 
      } 
      if (paramBoolean) {
        StringBuffer stringBuffer = new StringBuffer();
        processBuiltInEncodingAlgorithmAsCharacters(stringBuffer);
        this._characterContentChunkTable.add(stringBuffer.toString().toCharArray(), stringBuffer.length());
      } 
    } else if (this._identifier == 9) {
      this._octetBufferOffset -= this._octetBufferLength;
      decodeUtf8StringIntoCharBuffer();
      try {
        this._lexicalHandler.startCDATA();
        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
        this._lexicalHandler.endCDATA();
      } catch (SAXException sAXException) {
        throw new FastInfosetException(sAXException);
      } 
      if (paramBoolean)
        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
    } else if (this._identifier >= 32 && this._algorithmHandler != null) {
      String str = this._v.encodingAlgorithm.get(this._identifier - 32);
      if (str == null)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(this._identifier) })); 
      EncodingAlgorithm encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(str);
      if (encodingAlgorithm != null) {
        Object object = encodingAlgorithm.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        try {
          this._algorithmHandler.object(str, this._identifier, object);
        } catch (SAXException sAXException) {
          throw new FastInfosetException(sAXException);
        } 
      } else {
        try {
          this._algorithmHandler.octets(str, this._identifier, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        } catch (SAXException sAXException) {
          throw new FastInfosetException(sAXException);
        } 
      } 
      if (paramBoolean)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.addToTableNotSupported")); 
    } else {
      if (this._identifier >= 32)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported")); 
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    } 
  }
  
  protected final void processCIIBuiltInEncodingAlgorithmAsPrimitive() {
    try {
      int i;
      switch (this._identifier) {
        case 0:
        case 1:
          this._primitiveHandler.bytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          return;
        case 2:
          i = BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
          if (i > this.builtInAlgorithmState.shortArray.length) {
            short[] arrayOfShort = new short[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.shortArray, 0, arrayOfShort, 0, this.builtInAlgorithmState.shortArray.length);
            this.builtInAlgorithmState.shortArray = arrayOfShort;
          } 
          BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.decodeFromBytesToShortArray(this.builtInAlgorithmState.shortArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.shorts(this.builtInAlgorithmState.shortArray, 0, i);
          return;
        case 3:
          i = BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
          if (i > this.builtInAlgorithmState.intArray.length) {
            int[] arrayOfInt = new int[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.intArray, 0, arrayOfInt, 0, this.builtInAlgorithmState.intArray.length);
            this.builtInAlgorithmState.intArray = arrayOfInt;
          } 
          BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.decodeFromBytesToIntArray(this.builtInAlgorithmState.intArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.ints(this.builtInAlgorithmState.intArray, 0, i);
          return;
        case 4:
          i = BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
          if (i > this.builtInAlgorithmState.longArray.length) {
            long[] arrayOfLong = new long[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.longArray, 0, arrayOfLong, 0, this.builtInAlgorithmState.longArray.length);
            this.builtInAlgorithmState.longArray = arrayOfLong;
          } 
          BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.longs(this.builtInAlgorithmState.longArray, 0, i);
          return;
        case 5:
          i = BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength, this._octetBuffer[this._octetBufferStart] & 0xFF);
          if (i > this.builtInAlgorithmState.booleanArray.length) {
            boolean[] arrayOfBoolean = new boolean[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.booleanArray, 0, arrayOfBoolean, 0, this.builtInAlgorithmState.booleanArray.length);
            this.builtInAlgorithmState.booleanArray = arrayOfBoolean;
          } 
          BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.decodeFromBytesToBooleanArray(this.builtInAlgorithmState.booleanArray, 0, i, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.booleans(this.builtInAlgorithmState.booleanArray, 0, i);
          return;
        case 6:
          i = BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
          if (i > this.builtInAlgorithmState.floatArray.length) {
            float[] arrayOfFloat = new float[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.floatArray, 0, arrayOfFloat, 0, this.builtInAlgorithmState.floatArray.length);
            this.builtInAlgorithmState.floatArray = arrayOfFloat;
          } 
          BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.decodeFromBytesToFloatArray(this.builtInAlgorithmState.floatArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.floats(this.builtInAlgorithmState.floatArray, 0, i);
          return;
        case 7:
          i = BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
          if (i > this.builtInAlgorithmState.doubleArray.length) {
            double[] arrayOfDouble = new double[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.doubleArray, 0, arrayOfDouble, 0, this.builtInAlgorithmState.doubleArray.length);
            this.builtInAlgorithmState.doubleArray = arrayOfDouble;
          } 
          BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.decodeFromBytesToDoubleArray(this.builtInAlgorithmState.doubleArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.doubles(this.builtInAlgorithmState.doubleArray, 0, i);
          return;
        case 8:
          i = BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
          if (i > this.builtInAlgorithmState.longArray.length) {
            long[] arrayOfLong = new long[i * 3 / 2 + 1];
            System.arraycopy(this.builtInAlgorithmState.longArray, 0, arrayOfLong, 0, this.builtInAlgorithmState.longArray.length);
            this.builtInAlgorithmState.longArray = arrayOfLong;
          } 
          BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          this._primitiveHandler.uuids(this.builtInAlgorithmState.longArray, 0, i);
          return;
        case 9:
          throw new UnsupportedOperationException("CDATA");
      } 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unsupportedAlgorithm", new Object[] { Integer.valueOf(this._identifier) }));
    } catch (SAXException sAXException) {
      throw new FastInfosetException(sAXException);
    } 
  }
  
  protected final void processAIIEncodingAlgorithm(QualifiedName paramQualifiedName, boolean paramBoolean) throws FastInfosetException, IOException {
    if (this._identifier < 9) {
      if (this._primitiveHandler != null || this._algorithmHandler != null) {
        Object object = processBuiltInEncodingAlgorithmAsObject();
        this._attributes.addAttributeWithAlgorithmData(paramQualifiedName, null, this._identifier, object);
      } else {
        StringBuffer stringBuffer = new StringBuffer();
        processBuiltInEncodingAlgorithmAsCharacters(stringBuffer);
        this._attributes.addAttribute(paramQualifiedName, stringBuffer.toString());
      } 
    } else if (this._identifier >= 32 && this._algorithmHandler != null) {
      String str = this._v.encodingAlgorithm.get(this._identifier - 32);
      if (str == null)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(this._identifier) })); 
      EncodingAlgorithm encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(str);
      if (encodingAlgorithm != null) {
        Object object = encodingAlgorithm.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        this._attributes.addAttributeWithAlgorithmData(paramQualifiedName, str, this._identifier, object);
      } else {
        byte[] arrayOfByte = new byte[this._octetBufferLength];
        System.arraycopy(this._octetBuffer, this._octetBufferStart, arrayOfByte, 0, this._octetBufferLength);
        this._attributes.addAttributeWithAlgorithmData(paramQualifiedName, str, this._identifier, arrayOfByte);
      } 
    } else {
      if (this._identifier >= 32)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported")); 
      if (this._identifier == 9)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported")); 
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    } 
    if (paramBoolean)
      this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(paramQualifiedName.qName))); 
  }
  
  protected final void processBuiltInEncodingAlgorithmAsCharacters(StringBuffer paramStringBuffer) throws FastInfosetException, IOException {
    Object object = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
    BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(object, paramStringBuffer);
  }
  
  protected final Object processBuiltInEncodingAlgorithmAsObject() throws FastInfosetException, IOException { return BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength); }
  
  private static final class DeclHandlerImpl implements DeclHandler {
    private DeclHandlerImpl() {}
    
    public void elementDecl(String param1String1, String param1String2) throws SAXException {}
    
    public void attributeDecl(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5) throws SAXException {}
    
    public void internalEntityDecl(String param1String1, String param1String2) throws SAXException {}
    
    public void externalEntityDecl(String param1String1, String param1String2, String param1String3) throws SAXException {}
  }
  
  private static final class LexicalHandlerImpl implements LexicalHandler {
    private LexicalHandlerImpl() {}
    
    public void comment(char[] param1ArrayOfChar, int param1Int1, int param1Int2) {}
    
    public void startDTD(String param1String1, String param1String2, String param1String3) throws SAXException {}
    
    public void endDTD() {}
    
    public void startEntity(String param1String) throws IOException, SAXException {}
    
    public void endEntity(String param1String) throws IOException, SAXException {}
    
    public void startCDATA() {}
    
    public void endCDATA() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\sax\SAXDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */