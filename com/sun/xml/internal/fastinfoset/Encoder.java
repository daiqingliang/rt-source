package com.sun.xml.internal.fastinfoset;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.ExternalVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Encoder extends DefaultHandler implements FastInfosetSerializer {
  public static final String CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.serializer.character-encoding-scheme";
  
  protected static final String _characterEncodingSchemeSystemDefault = getDefaultEncodingScheme();
  
  private static int[] NUMERIC_CHARACTERS_TABLE = new int[maxCharacter("0123456789-+.E ") + 1];
  
  private static int[] DATE_TIME_CHARACTERS_TABLE = new int[maxCharacter("0123456789-:TZ ") + 1];
  
  private boolean _ignoreDTD;
  
  private boolean _ignoreComments;
  
  private boolean _ignoreProcessingInstructions;
  
  private boolean _ignoreWhiteSpaceTextContent;
  
  private boolean _useLocalNameAsKeyForQualifiedNameLookup;
  
  private boolean _encodingStringsAsUtf8 = true;
  
  private int _nonIdentifyingStringOnThirdBitCES;
  
  private int _nonIdentifyingStringOnFirstBitCES;
  
  private Map _registeredEncodingAlgorithms = new HashMap();
  
  protected SerializerVocabulary _v;
  
  protected VocabularyApplicationData _vData;
  
  private boolean _vIsInternal;
  
  protected boolean _terminate = false;
  
  protected int _b;
  
  protected OutputStream _s;
  
  protected char[] _charBuffer = new char[512];
  
  protected byte[] _octetBuffer = new byte[1024];
  
  protected int _octetBufferIndex;
  
  protected int _markIndex = -1;
  
  protected int minAttributeValueSize = 0;
  
  protected int maxAttributeValueSize = 32;
  
  protected int attributeValueMapTotalCharactersConstraint = 1073741823;
  
  protected int minCharacterContentChunkSize = 0;
  
  protected int maxCharacterContentChunkSize = 32;
  
  protected int characterContentChunkMapTotalCharactersConstraint = 1073741823;
  
  private int _bitsLeftInOctet;
  
  private EncodingBufferOutputStream _encodingBufferOutputStream = new EncodingBufferOutputStream(null);
  
  private byte[] _encodingBuffer = new byte[512];
  
  private int _encodingBufferIndex;
  
  private static String getDefaultEncodingScheme() {
    String str = System.getProperty("com.sun.xml.internal.fastinfoset.serializer.character-encoding-scheme", "UTF-8");
    return str.equals("UTF-16BE") ? "UTF-16BE" : "UTF-8";
  }
  
  private static int maxCharacter(String paramString) {
    char c = Character.MIN_VALUE;
    for (byte b = 0; b < paramString.length(); b++) {
      if (c < paramString.charAt(b))
        c = paramString.charAt(b); 
    } 
    return c;
  }
  
  protected Encoder() { setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault); }
  
  protected Encoder(boolean paramBoolean) {
    setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault);
    this._useLocalNameAsKeyForQualifiedNameLookup = paramBoolean;
  }
  
  public final void setIgnoreDTD(boolean paramBoolean) { this._ignoreDTD = paramBoolean; }
  
  public final boolean getIgnoreDTD() { return this._ignoreDTD; }
  
  public final void setIgnoreComments(boolean paramBoolean) { this._ignoreComments = paramBoolean; }
  
  public final boolean getIgnoreComments() { return this._ignoreComments; }
  
  public final void setIgnoreProcesingInstructions(boolean paramBoolean) { this._ignoreProcessingInstructions = paramBoolean; }
  
  public final boolean getIgnoreProcesingInstructions() { return this._ignoreProcessingInstructions; }
  
  public final void setIgnoreWhiteSpaceTextContent(boolean paramBoolean) { this._ignoreWhiteSpaceTextContent = paramBoolean; }
  
  public final boolean getIgnoreWhiteSpaceTextContent() { return this._ignoreWhiteSpaceTextContent; }
  
  public void setCharacterEncodingScheme(String paramString) {
    if (paramString.equals("UTF-16BE")) {
      this._encodingStringsAsUtf8 = false;
      this._nonIdentifyingStringOnThirdBitCES = 132;
      this._nonIdentifyingStringOnFirstBitCES = 16;
    } else {
      this._encodingStringsAsUtf8 = true;
      this._nonIdentifyingStringOnThirdBitCES = 128;
      this._nonIdentifyingStringOnFirstBitCES = 0;
    } 
  }
  
  public String getCharacterEncodingScheme() { return this._encodingStringsAsUtf8 ? "UTF-8" : "UTF-16BE"; }
  
  public void setRegisteredEncodingAlgorithms(Map paramMap) {
    this._registeredEncodingAlgorithms = paramMap;
    if (this._registeredEncodingAlgorithms == null)
      this._registeredEncodingAlgorithms = new HashMap(); 
  }
  
  public Map getRegisteredEncodingAlgorithms() { return this._registeredEncodingAlgorithms; }
  
  public int getMinCharacterContentChunkSize() { return this.minCharacterContentChunkSize; }
  
  public void setMinCharacterContentChunkSize(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.minCharacterContentChunkSize = paramInt;
  }
  
  public int getMaxCharacterContentChunkSize() { return this.maxCharacterContentChunkSize; }
  
  public void setMaxCharacterContentChunkSize(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.maxCharacterContentChunkSize = paramInt;
  }
  
  public int getCharacterContentChunkMapMemoryLimit() { return this.characterContentChunkMapTotalCharactersConstraint * 2; }
  
  public void setCharacterContentChunkMapMemoryLimit(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.characterContentChunkMapTotalCharactersConstraint = paramInt / 2;
  }
  
  public boolean isCharacterContentChunkLengthMatchesLimit(int paramInt) { return (paramInt >= this.minCharacterContentChunkSize && paramInt < this.maxCharacterContentChunkSize); }
  
  public boolean canAddCharacterContentToTable(int paramInt, CharArrayIntMap paramCharArrayIntMap) { return (paramCharArrayIntMap.getTotalCharacterCount() + paramInt < this.characterContentChunkMapTotalCharactersConstraint); }
  
  public int getMinAttributeValueSize() { return this.minAttributeValueSize; }
  
  public void setMinAttributeValueSize(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.minAttributeValueSize = paramInt;
  }
  
  public int getMaxAttributeValueSize() { return this.maxAttributeValueSize; }
  
  public void setMaxAttributeValueSize(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.maxAttributeValueSize = paramInt;
  }
  
  public void setAttributeValueMapMemoryLimit(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.attributeValueMapTotalCharactersConstraint = paramInt / 2;
  }
  
  public int getAttributeValueMapMemoryLimit() { return this.attributeValueMapTotalCharactersConstraint * 2; }
  
  public boolean isAttributeValueLengthMatchesLimit(int paramInt) { return (paramInt >= this.minAttributeValueSize && paramInt < this.maxAttributeValueSize); }
  
  public boolean canAddAttributeToTable(int paramInt) { return (this._v.attributeValue.getTotalCharacterCount() + paramInt < this.attributeValueMapTotalCharactersConstraint); }
  
  public void setExternalVocabulary(ExternalVocabulary paramExternalVocabulary) {
    this._v = new SerializerVocabulary();
    SerializerVocabulary serializerVocabulary = new SerializerVocabulary(paramExternalVocabulary.vocabulary, this._useLocalNameAsKeyForQualifiedNameLookup);
    this._v.setExternalVocabulary(paramExternalVocabulary.URI, serializerVocabulary, false);
    this._vIsInternal = true;
  }
  
  public void setVocabularyApplicationData(VocabularyApplicationData paramVocabularyApplicationData) { this._vData = paramVocabularyApplicationData; }
  
  public VocabularyApplicationData getVocabularyApplicationData() { return this._vData; }
  
  public void reset() { this._terminate = false; }
  
  public void setOutputStream(OutputStream paramOutputStream) {
    this._octetBufferIndex = 0;
    this._markIndex = -1;
    this._s = paramOutputStream;
  }
  
  public void setVocabulary(SerializerVocabulary paramSerializerVocabulary) {
    this._v = paramSerializerVocabulary;
    this._vIsInternal = false;
  }
  
  protected final void encodeHeader(boolean paramBoolean) {
    if (paramBoolean)
      this._s.write(EncodingConstants.XML_DECLARATION_VALUES[0]); 
    this._s.write(EncodingConstants.BINARY_HEADER);
  }
  
  protected final void encodeInitialVocabulary() {
    if (this._v == null) {
      this._v = new SerializerVocabulary();
      this._vIsInternal = true;
    } else if (this._vIsInternal) {
      this._v.clear();
      if (this._vData != null)
        this._vData.clear(); 
    } 
    if (!this._v.hasInitialVocabulary() && !this._v.hasExternalVocabulary()) {
      write(0);
    } else if (this._v.hasInitialVocabulary()) {
      this._b = 32;
      write(this._b);
      SerializerVocabulary serializerVocabulary = this._v.getReadOnlyVocabulary();
      if (serializerVocabulary.hasExternalVocabulary()) {
        this._b = 16;
        write(this._b);
        write(0);
      } 
      if (serializerVocabulary.hasExternalVocabulary())
        encodeNonEmptyOctetStringOnSecondBit(this._v.getExternalVocabularyURI()); 
    } else if (this._v.hasExternalVocabulary()) {
      this._b = 32;
      write(this._b);
      this._b = 16;
      write(this._b);
      write(0);
      encodeNonEmptyOctetStringOnSecondBit(this._v.getExternalVocabularyURI());
    } 
  }
  
  protected final void encodeDocumentTermination() {
    encodeElementTermination();
    encodeTermination();
    _flush();
    this._s.flush();
  }
  
  protected final void encodeElementTermination() {
    this._terminate = true;
    switch (this._b) {
      case 240:
        this._b = 255;
        return;
      case 255:
        write(255);
        break;
    } 
    this._b = 240;
  }
  
  protected final void encodeTermination() {
    if (this._terminate) {
      write(this._b);
      this._b = 0;
      this._terminate = false;
    } 
  }
  
  protected final void encodeNamespaceAttribute(String paramString1, String paramString2) throws IOException {
    this._b = 204;
    if (paramString1.length() > 0)
      this._b |= 0x2; 
    if (paramString2.length() > 0)
      this._b |= 0x1; 
    write(this._b);
    if (paramString1.length() > 0)
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString1, this._v.prefix); 
    if (paramString2.length() > 0)
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString2, this._v.namespaceName); 
  }
  
  protected final void encodeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnThirdBit(paramArrayOfChar, paramInt1, paramInt2, this._v.characterContentChunk, bool, true);
  }
  
  protected final void encodeCharactersNoClone(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnThirdBit(paramArrayOfChar, paramInt1, paramInt2, this._v.characterContentChunk, bool, false);
  }
  
  protected final void encodeNumericFourBitCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws FastInfosetException, IOException { encodeFourBitCharacters(0, NUMERIC_CHARACTERS_TABLE, paramArrayOfChar, paramInt1, paramInt2, paramBoolean); }
  
  protected final void encodeDateTimeFourBitCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws FastInfosetException, IOException { encodeFourBitCharacters(1, DATE_TIME_CHARACTERS_TABLE, paramArrayOfChar, paramInt1, paramInt2, paramBoolean); }
  
  protected final void encodeFourBitCharacters(int paramInt1, int[] paramArrayOfInt, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean) throws FastInfosetException, IOException {
    if (paramBoolean) {
      boolean bool = canAddCharacterContentToTable(paramInt3, this._v.characterContentChunk);
      int i = bool ? this._v.characterContentChunk.obtainIndex(paramArrayOfChar, paramInt2, paramInt3, true) : this._v.characterContentChunk.get(paramArrayOfChar, paramInt2, paramInt3);
      if (i != -1) {
        this._b = 160;
        encodeNonZeroIntegerOnFourthBit(i);
        return;
      } 
      if (bool) {
        this._b = 152;
      } else {
        this._b = 136;
      } 
    } else {
      this._b = 136;
    } 
    write(this._b);
    this._b = paramInt1 << 2;
    encodeNonEmptyFourBitCharacterStringOnSeventhBit(paramArrayOfInt, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  protected final void encodeAlphabetCharacters(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws FastInfosetException, IOException {
    if (paramBoolean) {
      boolean bool = canAddCharacterContentToTable(paramInt2, this._v.characterContentChunk);
      int j = bool ? this._v.characterContentChunk.obtainIndex(paramArrayOfChar, paramInt1, paramInt2, true) : this._v.characterContentChunk.get(paramArrayOfChar, paramInt1, paramInt2);
      if (j != -1) {
        this._b = 160;
        encodeNonZeroIntegerOnFourthBit(j);
        return;
      } 
      if (bool) {
        this._b = 152;
      } else {
        this._b = 136;
      } 
    } else {
      this._b = 136;
    } 
    int i = this._v.restrictedAlphabet.get(paramString);
    if (i == -1)
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.restrictedAlphabetNotPresent")); 
    i += 32;
    this._b |= (i & 0xC0) >> 6;
    write(this._b);
    this._b = (i & 0x3F) << 2;
    encodeNonEmptyNBitCharacterStringOnSeventhBit(paramString, paramArrayOfChar, paramInt1, paramInt2);
  }
  
  protected final void encodeProcessingInstruction(String paramString1, String paramString2) throws IOException {
    write(225);
    encodeIdentifyingNonEmptyStringOnFirstBit(paramString1, this._v.otherNCName);
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramString2.length());
    encodeNonIdentifyingStringOnFirstBit(paramString2, this._v.otherString, bool);
  }
  
  protected final void encodeDocumentTypeDeclaration(String paramString1, String paramString2) throws IOException {
    this._b = 196;
    if (paramString1 != null && paramString1.length() > 0)
      this._b |= 0x2; 
    if (paramString2 != null && paramString2.length() > 0)
      this._b |= 0x1; 
    write(this._b);
    if (paramString1 != null && paramString1.length() > 0)
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString1, this._v.otherURI); 
    if (paramString2 != null && paramString2.length() > 0)
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString2, this._v.otherURI); 
  }
  
  protected final void encodeComment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    write(226);
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnFirstBit(paramArrayOfChar, paramInt1, paramInt2, this._v.otherString, bool, true);
  }
  
  protected final void encodeCommentNoClone(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    write(226);
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnFirstBit(paramArrayOfChar, paramInt1, paramInt2, this._v.otherString, bool, false);
  }
  
  protected final void encodeElementQualifiedNameOnThirdBit(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(paramString3);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        if ((paramString2 == (arrayOfQualifiedName[b]).prefix || paramString2.equals((arrayOfQualifiedName[b]).prefix)) && (paramString1 == (arrayOfQualifiedName[b]).namespaceName || paramString1.equals((arrayOfQualifiedName[b]).namespaceName))) {
          encodeNonZeroIntegerOnThirdBit((arrayOfQualifiedName[b]).index);
          return;
        } 
      } 
    } 
    encodeLiteralElementQualifiedNameOnThirdBit(paramString1, paramString2, paramString3, entry);
  }
  
  protected final void encodeLiteralElementQualifiedNameOnThirdBit(String paramString1, String paramString2, String paramString3, LocalNameQualifiedNamesMap.Entry paramEntry) throws IOException {
    QualifiedName qualifiedName = new QualifiedName(paramString2, paramString1, paramString3, "", this._v.elementName.getNextIndex());
    paramEntry.addQualifiedName(qualifiedName);
    int i = -1;
    int j = -1;
    if (paramString1.length() > 0) {
      i = this._v.namespaceName.get(paramString1);
      if (i == -1)
        throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { paramString1 })); 
      if (paramString2.length() > 0) {
        j = this._v.prefix.get(paramString2);
        if (j == -1)
          throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { paramString2 })); 
      } 
    } 
    int k = this._v.localName.obtainIndex(paramString3);
    this._b |= 0x3C;
    if (i >= 0) {
      this._b |= 0x1;
      if (j >= 0)
        this._b |= 0x2; 
    } 
    write(this._b);
    if (i >= 0) {
      if (j >= 0)
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j); 
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    } 
    if (k >= 0) {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(k);
    } else {
      encodeNonEmptyOctetStringOnSecondBit(paramString3);
    } 
  }
  
  protected final void encodeAttributeQualifiedNameOnSecondBit(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(paramString3);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        if ((paramString2 == (arrayOfQualifiedName[b]).prefix || paramString2.equals((arrayOfQualifiedName[b]).prefix)) && (paramString1 == (arrayOfQualifiedName[b]).namespaceName || paramString1.equals((arrayOfQualifiedName[b]).namespaceName))) {
          encodeNonZeroIntegerOnSecondBitFirstBitZero((arrayOfQualifiedName[b]).index);
          return;
        } 
      } 
    } 
    encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, paramString2, paramString3, entry);
  }
  
  protected final boolean encodeLiteralAttributeQualifiedNameOnSecondBit(String paramString1, String paramString2, String paramString3, LocalNameQualifiedNamesMap.Entry paramEntry) throws IOException {
    int i = -1;
    int j = -1;
    if (paramString1.length() > 0) {
      i = this._v.namespaceName.get(paramString1);
      if (i == -1) {
        if (paramString1 == "http://www.w3.org/2000/xmlns/" || paramString1.equals("http://www.w3.org/2000/xmlns/"))
          return false; 
        throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { paramString1 }));
      } 
      if (paramString2.length() > 0) {
        j = this._v.prefix.get(paramString2);
        if (j == -1)
          throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { paramString2 })); 
      } 
    } 
    int k = this._v.localName.obtainIndex(paramString3);
    QualifiedName qualifiedName = new QualifiedName(paramString2, paramString1, paramString3, "", this._v.attributeName.getNextIndex());
    paramEntry.addQualifiedName(qualifiedName);
    this._b = 120;
    if (paramString1.length() > 0) {
      this._b |= 0x1;
      if (paramString2.length() > 0)
        this._b |= 0x2; 
    } 
    write(this._b);
    if (i >= 0) {
      if (j >= 0)
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j); 
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    } else if (paramString1 != "") {
      encodeNonEmptyOctetStringOnSecondBit("xml");
      encodeNonEmptyOctetStringOnSecondBit("http://www.w3.org/XML/1998/namespace");
    } 
    if (k >= 0) {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(k);
    } else {
      encodeNonEmptyOctetStringOnSecondBit(paramString3);
    } 
    return true;
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(String paramString, StringIntMap paramStringIntMap, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramString == null || paramString.length() == 0) {
      write(255);
    } else if (paramBoolean1 || paramBoolean2) {
      boolean bool = (paramBoolean2 || canAddAttributeToTable(paramString.length())) ? 1 : 0;
      int i = bool ? paramStringIntMap.obtainIndex(paramString) : paramStringIntMap.get(paramString);
      if (i != -1) {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
      } else if (bool) {
        this._b = 0x40 | this._nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramString);
      } else {
        this._b = this._nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramString);
      } 
    } else {
      this._b = this._nonIdentifyingStringOnFirstBitCES;
      encodeNonEmptyCharacterStringOnFifthBit(paramString);
    } 
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(String paramString, CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean) throws IOException {
    if (paramString == null || paramString.length() == 0) {
      write(255);
    } else if (paramBoolean) {
      char[] arrayOfChar = paramString.toCharArray();
      int i = paramString.length();
      boolean bool = canAddCharacterContentToTable(i, paramCharArrayIntMap);
      int j = bool ? paramCharArrayIntMap.obtainIndex(arrayOfChar, 0, i, false) : paramCharArrayIntMap.get(arrayOfChar, 0, i);
      if (j != -1) {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j);
      } else if (bool) {
        this._b = 0x40 | this._nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(arrayOfChar, 0, i);
      } else {
        this._b = this._nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramString);
      } 
    } else {
      this._b = this._nonIdentifyingStringOnFirstBitCES;
      encodeNonEmptyCharacterStringOnFifthBit(paramString);
    } 
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(char[] paramArrayOfChar, int paramInt1, int paramInt2, CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramInt2 == 0) {
      write(255);
    } else if (paramBoolean1) {
      boolean bool = canAddCharacterContentToTable(paramInt2, paramCharArrayIntMap);
      int i = bool ? paramCharArrayIntMap.obtainIndex(paramArrayOfChar, paramInt1, paramInt2, paramBoolean2) : paramCharArrayIntMap.get(paramArrayOfChar, paramInt1, paramInt2);
      if (i != -1) {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
      } else if (bool) {
        this._b = 0x40 | this._nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        this._b = this._nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramArrayOfChar, paramInt1, paramInt2);
      } 
    } else {
      this._b = this._nonIdentifyingStringOnFirstBitCES;
      encodeNonEmptyCharacterStringOnFifthBit(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  protected final void encodeNumericNonIdentifyingStringOnFirstBit(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IOException, FastInfosetException { encodeNonIdentifyingStringOnFirstBit(0, NUMERIC_CHARACTERS_TABLE, paramString, paramBoolean1, paramBoolean2); }
  
  protected final void encodeDateTimeNonIdentifyingStringOnFirstBit(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IOException, FastInfosetException { encodeNonIdentifyingStringOnFirstBit(1, DATE_TIME_CHARACTERS_TABLE, paramString, paramBoolean1, paramBoolean2); }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(int paramInt, int[] paramArrayOfInt, String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IOException, FastInfosetException {
    if (paramString == null || paramString.length() == 0) {
      write(255);
      return;
    } 
    if (paramBoolean1 || paramBoolean2) {
      boolean bool = (paramBoolean2 || canAddAttributeToTable(paramString.length())) ? 1 : 0;
      int m = bool ? this._v.attributeValue.obtainIndex(paramString) : this._v.attributeValue.get(paramString);
      if (m != -1) {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(m);
        return;
      } 
      if (bool) {
        this._b = 96;
      } else {
        this._b = 32;
      } 
    } else {
      this._b = 32;
    } 
    write(this._b | (paramInt & 0xF0) >> 4);
    this._b = (paramInt & 0xF) << 4;
    int i = paramString.length();
    int j = i / 2;
    int k = i % 2;
    encodeNonZeroOctetStringLengthOnFifthBit(j + k);
    encodeNonEmptyFourBitCharacterString(paramArrayOfInt, paramString.toCharArray(), 0, j, k);
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(String paramString, int paramInt, Object paramObject) throws FastInfosetException, IOException {
    if (paramString != null) {
      paramInt = this._v.encodingAlgorithm.get(paramString);
      if (paramInt == -1)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { paramString })); 
      paramInt += 32;
      EncodingAlgorithm encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(paramString);
      if (encodingAlgorithm != null) {
        encodeAIIObjectAlgorithmData(paramInt, paramObject, encodingAlgorithm);
      } else if (paramObject instanceof byte[]) {
        byte[] arrayOfByte = (byte[])paramObject;
        encodeAIIOctetAlgorithmData(paramInt, arrayOfByte, 0, arrayOfByte.length);
      } else {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      } 
    } else if (paramInt <= 9) {
      int i = 0;
      switch (paramInt) {
        case 0:
        case 1:
          i = (byte[])paramObject.length;
          break;
        case 2:
          i = (short[])paramObject.length;
          break;
        case 3:
          i = (int[])paramObject.length;
          break;
        case 4:
        case 8:
          i = (long[])paramObject.length;
          break;
        case 5:
          i = (boolean[])paramObject.length;
          break;
        case 6:
          i = (float[])paramObject.length;
          break;
        case 7:
          i = (double[])paramObject.length;
          break;
        case 9:
          throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
        default:
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[] { Integer.valueOf(paramInt) }));
      } 
      encodeAIIBuiltInAlgorithmData(paramInt, paramObject, 0, i);
    } else if (paramInt >= 32) {
      if (paramObject instanceof byte[]) {
        byte[] arrayOfByte = (byte[])paramObject;
        encodeAIIOctetAlgorithmData(paramInt, arrayOfByte, 0, arrayOfByte.length);
      } else {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      } 
    } else {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    } 
  }
  
  protected final void encodeAIIOctetAlgorithmData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws IOException {
    write(0x30 | (paramInt1 & 0xF0) >> 4);
    this._b = (paramInt1 & 0xF) << 4;
    encodeNonZeroOctetStringLengthOnFifthBit(paramInt3);
    write(paramArrayOfByte, paramInt2, paramInt3);
  }
  
  protected final void encodeAIIObjectAlgorithmData(int paramInt, Object paramObject, EncodingAlgorithm paramEncodingAlgorithm) throws FastInfosetException, IOException {
    write(0x30 | (paramInt & 0xF0) >> 4);
    this._b = (paramInt & 0xF) << 4;
    this._encodingBufferOutputStream.reset();
    paramEncodingAlgorithm.encodeToOutputStream(paramObject, this._encodingBufferOutputStream);
    encodeNonZeroOctetStringLengthOnFifthBit(this._encodingBufferIndex);
    write(this._encodingBuffer, this._encodingBufferIndex);
  }
  
  protected final void encodeAIIBuiltInAlgorithmData(int paramInt1, Object paramObject, int paramInt2, int paramInt3) throws IOException {
    write(0x30 | (paramInt1 & 0xF0) >> 4);
    this._b = (paramInt1 & 0xF) << 4;
    int i = BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).getOctetLengthFromPrimitiveLength(paramInt3);
    encodeNonZeroOctetStringLengthOnFifthBit(i);
    ensureSize(i);
    BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).encodeToBytes(paramObject, paramInt2, paramInt3, this._octetBuffer, this._octetBufferIndex);
    this._octetBufferIndex += i;
  }
  
  protected final void encodeNonIdentifyingStringOnThirdBit(char[] paramArrayOfChar, int paramInt1, int paramInt2, CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (paramBoolean1) {
      boolean bool = canAddCharacterContentToTable(paramInt2, paramCharArrayIntMap);
      int i = bool ? paramCharArrayIntMap.obtainIndex(paramArrayOfChar, paramInt1, paramInt2, paramBoolean2) : paramCharArrayIntMap.get(paramArrayOfChar, paramInt1, paramInt2);
      if (i != -1) {
        this._b = 160;
        encodeNonZeroIntegerOnFourthBit(i);
      } else if (bool) {
        this._b = 0x10 | this._nonIdentifyingStringOnThirdBitCES;
        encodeNonEmptyCharacterStringOnSeventhBit(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        this._b = this._nonIdentifyingStringOnThirdBitCES;
        encodeNonEmptyCharacterStringOnSeventhBit(paramArrayOfChar, paramInt1, paramInt2);
      } 
    } else {
      this._b = this._nonIdentifyingStringOnThirdBitCES;
      encodeNonEmptyCharacterStringOnSeventhBit(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  protected final void encodeNonIdentifyingStringOnThirdBit(String paramString, int paramInt, Object paramObject) throws FastInfosetException, IOException {
    if (paramString != null) {
      paramInt = this._v.encodingAlgorithm.get(paramString);
      if (paramInt == -1)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { paramString })); 
      paramInt += 32;
      EncodingAlgorithm encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(paramString);
      if (encodingAlgorithm != null) {
        encodeCIIObjectAlgorithmData(paramInt, paramObject, encodingAlgorithm);
      } else if (paramObject instanceof byte[]) {
        byte[] arrayOfByte = (byte[])paramObject;
        encodeCIIOctetAlgorithmData(paramInt, arrayOfByte, 0, arrayOfByte.length);
      } else {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      } 
    } else if (paramInt <= 9) {
      int i = 0;
      switch (paramInt) {
        case 0:
        case 1:
          i = (byte[])paramObject.length;
          break;
        case 2:
          i = (short[])paramObject.length;
          break;
        case 3:
          i = (int[])paramObject.length;
          break;
        case 4:
        case 8:
          i = (long[])paramObject.length;
          break;
        case 5:
          i = (boolean[])paramObject.length;
          break;
        case 6:
          i = (float[])paramObject.length;
          break;
        case 7:
          i = (double[])paramObject.length;
          break;
        case 9:
          throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
        default:
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[] { Integer.valueOf(paramInt) }));
      } 
      encodeCIIBuiltInAlgorithmData(paramInt, paramObject, 0, i);
    } else if (paramInt >= 32) {
      if (paramObject instanceof byte[]) {
        byte[] arrayOfByte = (byte[])paramObject;
        encodeCIIOctetAlgorithmData(paramInt, arrayOfByte, 0, arrayOfByte.length);
      } else {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      } 
    } else {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    } 
  }
  
  protected final void encodeNonIdentifyingStringOnThirdBit(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws FastInfosetException, IOException {
    if (paramString != null) {
      paramInt1 = this._v.encodingAlgorithm.get(paramString);
      if (paramInt1 == -1)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { paramString })); 
      paramInt1 += 32;
    } 
    encodeCIIOctetAlgorithmData(paramInt1, paramArrayOfByte, paramInt2, paramInt3);
  }
  
  protected final void encodeCIIOctetAlgorithmData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws IOException {
    write(0x8C | (paramInt1 & 0xC0) >> 6);
    this._b = (paramInt1 & 0x3F) << 2;
    encodeNonZeroOctetStringLengthOnSenventhBit(paramInt3);
    write(paramArrayOfByte, paramInt2, paramInt3);
  }
  
  protected final void encodeCIIObjectAlgorithmData(int paramInt, Object paramObject, EncodingAlgorithm paramEncodingAlgorithm) throws FastInfosetException, IOException {
    write(0x8C | (paramInt & 0xC0) >> 6);
    this._b = (paramInt & 0x3F) << 2;
    this._encodingBufferOutputStream.reset();
    paramEncodingAlgorithm.encodeToOutputStream(paramObject, this._encodingBufferOutputStream);
    encodeNonZeroOctetStringLengthOnSenventhBit(this._encodingBufferIndex);
    write(this._encodingBuffer, this._encodingBufferIndex);
  }
  
  protected final void encodeCIIBuiltInAlgorithmData(int paramInt1, Object paramObject, int paramInt2, int paramInt3) throws IOException {
    write(0x8C | (paramInt1 & 0xC0) >> 6);
    this._b = (paramInt1 & 0x3F) << 2;
    int i = BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).getOctetLengthFromPrimitiveLength(paramInt3);
    encodeNonZeroOctetStringLengthOnSenventhBit(i);
    ensureSize(i);
    BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).encodeToBytes(paramObject, paramInt2, paramInt3, this._octetBuffer, this._octetBufferIndex);
    this._octetBufferIndex += i;
  }
  
  protected final void encodeCIIBuiltInAlgorithmDataAsCDATA(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    write(140);
    this._b = 36;
    paramInt2 = encodeUTF8String(paramArrayOfChar, paramInt1, paramInt2);
    encodeNonZeroOctetStringLengthOnSenventhBit(paramInt2);
    write(this._encodingBuffer, paramInt2);
  }
  
  protected final void encodeIdentifyingNonEmptyStringOnFirstBit(String paramString, StringIntMap paramStringIntMap) throws IOException {
    int i = paramStringIntMap.obtainIndex(paramString);
    if (i == -1) {
      encodeNonEmptyOctetStringOnSecondBit(paramString);
    } else {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    } 
  }
  
  protected final void encodeNonEmptyOctetStringOnSecondBit(String paramString) {
    int i = encodeUTF8String(paramString);
    encodeNonZeroOctetStringLengthOnSecondBit(i);
    write(this._encodingBuffer, i);
  }
  
  protected final void encodeNonZeroOctetStringLengthOnSecondBit(int paramInt) {
    if (paramInt < 65) {
      write(paramInt - 1);
    } else if (paramInt < 321) {
      write(64);
      write(paramInt - 65);
    } else {
      write(96);
      paramInt -= 321;
      write(paramInt >>> 24);
      write(paramInt >> 16 & 0xFF);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } 
  }
  
  protected final void encodeNonEmptyCharacterStringOnFifthBit(String paramString) {
    int i = this._encodingStringsAsUtf8 ? encodeUTF8String(paramString) : encodeUtf16String(paramString);
    encodeNonZeroOctetStringLengthOnFifthBit(i);
    write(this._encodingBuffer, i);
  }
  
  protected final void encodeNonEmptyCharacterStringOnFifthBit(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    paramInt2 = this._encodingStringsAsUtf8 ? encodeUTF8String(paramArrayOfChar, paramInt1, paramInt2) : encodeUtf16String(paramArrayOfChar, paramInt1, paramInt2);
    encodeNonZeroOctetStringLengthOnFifthBit(paramInt2);
    write(this._encodingBuffer, paramInt2);
  }
  
  protected final void encodeNonZeroOctetStringLengthOnFifthBit(int paramInt) {
    if (paramInt < 9) {
      write(this._b | paramInt - 1);
    } else if (paramInt < 265) {
      write(this._b | 0x8);
      write(paramInt - 9);
    } else {
      write(this._b | 0xC);
      paramInt -= 265;
      write(paramInt >>> 24);
      write(paramInt >> 16 & 0xFF);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } 
  }
  
  protected final void encodeNonEmptyCharacterStringOnSeventhBit(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    paramInt2 = this._encodingStringsAsUtf8 ? encodeUTF8String(paramArrayOfChar, paramInt1, paramInt2) : encodeUtf16String(paramArrayOfChar, paramInt1, paramInt2);
    encodeNonZeroOctetStringLengthOnSenventhBit(paramInt2);
    write(this._encodingBuffer, paramInt2);
  }
  
  protected final void encodeNonEmptyFourBitCharacterStringOnSeventhBit(int[] paramArrayOfInt, char[] paramArrayOfChar, int paramInt1, int paramInt2) throws FastInfosetException, IOException {
    int i = paramInt2 / 2;
    int j = paramInt2 % 2;
    encodeNonZeroOctetStringLengthOnSenventhBit(i + j);
    encodeNonEmptyFourBitCharacterString(paramArrayOfInt, paramArrayOfChar, paramInt1, i, j);
  }
  
  protected final void encodeNonEmptyFourBitCharacterString(int[] paramArrayOfInt, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) throws FastInfosetException, IOException {
    ensureSize(paramInt2 + paramInt3);
    int i = 0;
    for (byte b = 0; b < paramInt2; b++) {
      i = paramArrayOfInt[paramArrayOfChar[paramInt1++]] << 4 | paramArrayOfInt[paramArrayOfChar[paramInt1++]];
      if (i < 0)
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange")); 
      this._octetBuffer[this._octetBufferIndex++] = (byte)i;
    } 
    if (paramInt3 == 1) {
      i = paramArrayOfInt[paramArrayOfChar[paramInt1]] << 4 | 0xF;
      if (i < 0)
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange")); 
      this._octetBuffer[this._octetBufferIndex++] = (byte)i;
    } 
  }
  
  protected final void encodeNonEmptyNBitCharacterStringOnSeventhBit(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2) throws FastInfosetException, IOException {
    int i;
    for (i = 1; true << i <= paramString.length(); i++);
    int j = paramInt2 * i;
    int k = j / 8;
    int m = j % 8;
    int n = k + ((m > 0) ? 1 : 0);
    encodeNonZeroOctetStringLengthOnSenventhBit(n);
    resetBits();
    ensureSize(n);
    byte b = 0;
    for (int i1 = 0; i1 < paramInt2; i1++) {
      char c = paramArrayOfChar[paramInt1 + i1];
      for (b = 0; b < paramString.length() && c != paramString.charAt(b); b++);
      if (b == paramString.length())
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange")); 
      writeBits(i, b);
    } 
    if (m > 0) {
      this._b |= (1 << 8 - m) - 1;
      write(this._b);
    } 
  }
  
  private final void resetBits() {
    this._bitsLeftInOctet = 8;
    this._b = 0;
  }
  
  private final void writeBits(int paramInt1, int paramInt2) throws IOException {
    while (paramInt1 > 0) {
      int i = ((paramInt2 & 1 << --paramInt1) > 0) ? 1 : 0;
      this._b |= i << --this._bitsLeftInOctet;
      if (this._bitsLeftInOctet == 0) {
        write(this._b);
        this._bitsLeftInOctet = 8;
        this._b = 0;
      } 
    } 
  }
  
  protected final void encodeNonZeroOctetStringLengthOnSenventhBit(int paramInt) {
    if (paramInt < 3) {
      write(this._b | paramInt - 1);
    } else if (paramInt < 259) {
      write(this._b | 0x2);
      write(paramInt - 3);
    } else {
      write(this._b | 0x3);
      paramInt -= 259;
      write(paramInt >>> 24);
      write(paramInt >> 16 & 0xFF);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } 
  }
  
  protected final void encodeNonZeroIntegerOnSecondBitFirstBitOne(int paramInt) {
    if (paramInt < 64) {
      write(0x80 | paramInt);
    } else if (paramInt < 8256) {
      paramInt -= 64;
      this._b = 0xC0 | paramInt >> 8;
      write(this._b);
      write(paramInt & 0xFF);
    } else if (paramInt < 1048576) {
      paramInt -= 8256;
      this._b = 0xE0 | paramInt >> 16;
      write(this._b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } else {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[] { Integer.valueOf(1048576) }));
    } 
  }
  
  protected final void encodeNonZeroIntegerOnSecondBitFirstBitZero(int paramInt) {
    if (paramInt < 64) {
      write(paramInt);
    } else if (paramInt < 8256) {
      paramInt -= 64;
      this._b = 0x40 | paramInt >> 8;
      write(this._b);
      write(paramInt & 0xFF);
    } else {
      paramInt -= 8256;
      this._b = 0x60 | paramInt >> 16;
      write(this._b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } 
  }
  
  protected final void encodeNonZeroIntegerOnThirdBit(int paramInt) {
    if (paramInt < 32) {
      write(this._b | paramInt);
    } else if (paramInt < 2080) {
      paramInt -= 32;
      this._b |= 0x20 | paramInt >> 8;
      write(this._b);
      write(paramInt & 0xFF);
    } else if (paramInt < 526368) {
      paramInt -= 2080;
      this._b |= 0x28 | paramInt >> 16;
      write(this._b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } else {
      paramInt -= 526368;
      this._b |= 0x30;
      write(this._b);
      write(paramInt >> 16);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } 
  }
  
  protected final void encodeNonZeroIntegerOnFourthBit(int paramInt) {
    if (paramInt < 16) {
      write(this._b | paramInt);
    } else if (paramInt < 1040) {
      paramInt -= 16;
      this._b |= 0x10 | paramInt >> 8;
      write(this._b);
      write(paramInt & 0xFF);
    } else if (paramInt < 263184) {
      paramInt -= 1040;
      this._b |= 0x14 | paramInt >> 16;
      write(this._b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } else {
      paramInt -= 263184;
      this._b |= 0x18;
      write(this._b);
      write(paramInt >> 16);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    } 
  }
  
  protected final void encodeNonEmptyUTF8StringAsOctetString(int paramInt, String paramString, int[] paramArrayOfInt) throws IOException {
    char[] arrayOfChar = paramString.toCharArray();
    encodeNonEmptyUTF8StringAsOctetString(paramInt, arrayOfChar, 0, arrayOfChar.length, paramArrayOfInt);
  }
  
  protected final void encodeNonEmptyUTF8StringAsOctetString(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, int[] paramArrayOfInt) throws IOException {
    paramInt3 = encodeUTF8String(paramArrayOfChar, paramInt2, paramInt3);
    encodeNonZeroOctetStringLength(paramInt1, paramInt3, paramArrayOfInt);
    write(this._encodingBuffer, paramInt3);
  }
  
  protected final void encodeNonZeroOctetStringLength(int paramInt1, int paramInt2, int[] paramArrayOfInt) throws IOException {
    if (paramInt2 < paramArrayOfInt[0]) {
      write(paramInt1 | paramInt2 - 1);
    } else if (paramInt2 < paramArrayOfInt[1]) {
      write(paramInt1 | paramArrayOfInt[2]);
      write(paramInt2 - paramArrayOfInt[0]);
    } else {
      write(paramInt1 | paramArrayOfInt[3]);
      paramInt2 -= paramArrayOfInt[1];
      write(paramInt2 >>> 24);
      write(paramInt2 >> 16 & 0xFF);
      write(paramInt2 >> 8 & 0xFF);
      write(paramInt2 & 0xFF);
    } 
  }
  
  protected final void encodeNonZeroInteger(int paramInt1, int paramInt2, int[] paramArrayOfInt) throws IOException {
    if (paramInt2 < paramArrayOfInt[0]) {
      write(paramInt1 | paramInt2);
    } else if (paramInt2 < paramArrayOfInt[1]) {
      paramInt2 -= paramArrayOfInt[0];
      write(paramInt1 | paramArrayOfInt[3] | paramInt2 >> 8);
      write(paramInt2 & 0xFF);
    } else if (paramInt2 < paramArrayOfInt[2]) {
      paramInt2 -= paramArrayOfInt[1];
      write(paramInt1 | paramArrayOfInt[4] | paramInt2 >> 16);
      write(paramInt2 >> 8 & 0xFF);
      write(paramInt2 & 0xFF);
    } else if (paramInt2 < 1048576) {
      paramInt2 -= paramArrayOfInt[2];
      write(paramInt1 | paramArrayOfInt[5]);
      write(paramInt2 >> 16);
      write(paramInt2 >> 8 & 0xFF);
      write(paramInt2 & 0xFF);
    } else {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[] { Integer.valueOf(1048576) }));
    } 
  }
  
  protected final void mark() { this._markIndex = this._octetBufferIndex; }
  
  protected final void resetMark() { this._markIndex = -1; }
  
  protected final boolean hasMark() { return (this._markIndex != -1); }
  
  protected final void write(int paramInt) {
    if (this._octetBufferIndex < this._octetBuffer.length) {
      this._octetBuffer[this._octetBufferIndex++] = (byte)paramInt;
    } else if (this._markIndex == -1) {
      this._s.write(this._octetBuffer);
      this._octetBufferIndex = 1;
      this._octetBuffer[0] = (byte)paramInt;
    } else {
      resize(this._octetBuffer.length * 3 / 2);
      this._octetBuffer[this._octetBufferIndex++] = (byte)paramInt;
    } 
  }
  
  protected final void write(byte[] paramArrayOfByte, int paramInt) throws IOException { write(paramArrayOfByte, 0, paramInt); }
  
  protected final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this._octetBufferIndex + paramInt2 < this._octetBuffer.length) {
      System.arraycopy(paramArrayOfByte, paramInt1, this._octetBuffer, this._octetBufferIndex, paramInt2);
      this._octetBufferIndex += paramInt2;
    } else if (this._markIndex == -1) {
      this._s.write(this._octetBuffer, 0, this._octetBufferIndex);
      this._s.write(paramArrayOfByte, paramInt1, paramInt2);
      this._octetBufferIndex = 0;
    } else {
      resize((this._octetBuffer.length + paramInt2) * 3 / 2 + 1);
      System.arraycopy(paramArrayOfByte, paramInt1, this._octetBuffer, this._octetBufferIndex, paramInt2);
      this._octetBufferIndex += paramInt2;
    } 
  }
  
  private void ensureSize(int paramInt) {
    if (this._octetBufferIndex + paramInt > this._octetBuffer.length)
      resize((this._octetBufferIndex + paramInt) * 3 / 2 + 1); 
  }
  
  private void resize(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(this._octetBuffer, 0, arrayOfByte, 0, this._octetBufferIndex);
    this._octetBuffer = arrayOfByte;
  }
  
  private void _flush() {
    if (this._octetBufferIndex > 0) {
      this._s.write(this._octetBuffer, 0, this._octetBufferIndex);
      this._octetBufferIndex = 0;
    } 
  }
  
  protected final int encodeUTF8String(String paramString) {
    int i = paramString.length();
    if (i < this._charBuffer.length) {
      paramString.getChars(0, i, this._charBuffer, 0);
      return encodeUTF8String(this._charBuffer, 0, i);
    } 
    char[] arrayOfChar = paramString.toCharArray();
    return encodeUTF8String(arrayOfChar, 0, i);
  }
  
  private void ensureEncodingBufferSizeForUtf8String(int paramInt) {
    int i = 4 * paramInt;
    if (this._encodingBuffer.length < i)
      this._encodingBuffer = new byte[i]; 
  }
  
  protected final int encodeUTF8String(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    byte b = 0;
    ensureEncodingBufferSizeForUtf8String(paramInt2);
    int i = paramInt1 + paramInt2;
    while (i != paramInt1) {
      char c = paramArrayOfChar[paramInt1++];
      if (c < '') {
        this._encodingBuffer[b++] = (byte)c;
        continue;
      } 
      if (c < 'ࠀ') {
        this._encodingBuffer[b++] = (byte)(0xC0 | c >> '\006');
        this._encodingBuffer[b++] = (byte)(0x80 | c & 0x3F);
        continue;
      } 
      if (c <= Character.MAX_VALUE) {
        if (!XMLChar.isHighSurrogate(c) && !XMLChar.isLowSurrogate(c)) {
          this._encodingBuffer[b++] = (byte)(0xE0 | c >> '\f');
          this._encodingBuffer[b++] = (byte)(0x80 | c >> '\006' & 0x3F);
          this._encodingBuffer[b++] = (byte)(0x80 | c & 0x3F);
          continue;
        } 
        encodeCharacterAsUtf8FourByte(c, paramArrayOfChar, paramInt1, i, b);
        b += 4;
        paramInt1++;
      } 
    } 
    return b;
  }
  
  private void encodeCharacterAsUtf8FourByte(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, int paramInt4) throws IOException {
    if (paramInt2 == paramInt3)
      throw new IOException(""); 
    char c = paramArrayOfChar[paramInt2];
    if (!XMLChar.isLowSurrogate(c))
      throw new IOException(""); 
    int i = ((paramInt1 & 0x3FF) << 10 | c & 0x3FF) + 65536;
    if (i < 0 || i >= 2097152)
      throw new IOException(""); 
    this._encodingBuffer[paramInt4++] = (byte)(0xF0 | i >> 18);
    this._encodingBuffer[paramInt4++] = (byte)(0x80 | i >> 12 & 0x3F);
    this._encodingBuffer[paramInt4++] = (byte)(0x80 | i >> 6 & 0x3F);
    this._encodingBuffer[paramInt4++] = (byte)(0x80 | i & 0x3F);
  }
  
  protected final int encodeUtf16String(String paramString) {
    int i = paramString.length();
    if (i < this._charBuffer.length) {
      paramString.getChars(0, i, this._charBuffer, 0);
      return encodeUtf16String(this._charBuffer, 0, i);
    } 
    char[] arrayOfChar = paramString.toCharArray();
    return encodeUtf16String(arrayOfChar, 0, i);
  }
  
  private void ensureEncodingBufferSizeForUtf16String(int paramInt) {
    int i = 2 * paramInt;
    if (this._encodingBuffer.length < i)
      this._encodingBuffer = new byte[i]; 
  }
  
  protected final int encodeUtf16String(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    byte b = 0;
    ensureEncodingBufferSizeForUtf16String(paramInt2);
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      char c = paramArrayOfChar[j];
      this._encodingBuffer[b++] = (byte)(c >> '\b');
      this._encodingBuffer[b++] = (byte)(c & 0xFF);
    } 
    return b;
  }
  
  public static String getPrefixFromQualifiedName(String paramString) {
    int i = paramString.indexOf(':');
    String str = "";
    if (i != -1)
      str = paramString.substring(0, i); 
    return str;
  }
  
  public static boolean isWhiteSpace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (!XMLChar.isSpace(paramArrayOfChar[paramInt1]))
      return false; 
    int i = paramInt1 + paramInt2;
    while (++paramInt1 < i && XMLChar.isSpace(paramArrayOfChar[paramInt1]));
    return (paramInt1 == i);
  }
  
  public static boolean isWhiteSpace(String paramString) {
    if (!XMLChar.isSpace(paramString.charAt(0)))
      return false; 
    int i = paramString.length();
    byte b = 1;
    while (b < i && XMLChar.isSpace(paramString.charAt(b++)));
    return (b == i);
  }
  
  static  {
    byte b;
    for (b = 0; b < NUMERIC_CHARACTERS_TABLE.length; b++)
      NUMERIC_CHARACTERS_TABLE[b] = -1; 
    for (b = 0; b < DATE_TIME_CHARACTERS_TABLE.length; b++)
      DATE_TIME_CHARACTERS_TABLE[b] = -1; 
    for (b = 0; b < "0123456789-+.E ".length(); b++)
      NUMERIC_CHARACTERS_TABLE["0123456789-+.E ".charAt(b)] = b; 
    for (b = 0; b < "0123456789-:TZ ".length(); b++)
      DATE_TIME_CHARACTERS_TABLE["0123456789-:TZ ".charAt(b)] = b; 
  }
  
  private class EncodingBufferOutputStream extends OutputStream {
    private EncodingBufferOutputStream() {}
    
    public void write(int param1Int) {
      if (Encoder.this._encodingBufferIndex < Encoder.this._encodingBuffer.length) {
        Encoder.this._encodingBuffer[Encoder.this._encodingBufferIndex++] = (byte)param1Int;
      } else {
        byte[] arrayOfByte = new byte[Math.max(Encoder.this._encodingBuffer.length << 1, Encoder.this._encodingBufferIndex)];
        System.arraycopy(Encoder.this._encodingBuffer, 0, arrayOfByte, 0, Encoder.this._encodingBufferIndex);
        Encoder.this._encodingBuffer = arrayOfByte;
        Encoder.this._encodingBuffer[Encoder.this._encodingBufferIndex++] = (byte)param1Int;
      } 
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (param1Int1 < 0 || param1Int1 > param1ArrayOfByte.length || param1Int2 < 0 || param1Int1 + param1Int2 > param1ArrayOfByte.length || param1Int1 + param1Int2 < 0)
        throw new IndexOutOfBoundsException(); 
      if (param1Int2 == 0)
        return; 
      int i = Encoder.this._encodingBufferIndex + param1Int2;
      if (i > Encoder.this._encodingBuffer.length) {
        byte[] arrayOfByte = new byte[Math.max(Encoder.this._encodingBuffer.length << 1, i)];
        System.arraycopy(Encoder.this._encodingBuffer, 0, arrayOfByte, 0, Encoder.this._encodingBufferIndex);
        Encoder.this._encodingBuffer = arrayOfByte;
      } 
      System.arraycopy(param1ArrayOfByte, param1Int1, Encoder.this._encodingBuffer, Encoder.this._encodingBufferIndex, param1Int2);
      Encoder.this._encodingBufferIndex = i;
    }
    
    public int getLength() { return Encoder.this._encodingBufferIndex; }
    
    public void reset() { Encoder.this._encodingBufferIndex = 0; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */