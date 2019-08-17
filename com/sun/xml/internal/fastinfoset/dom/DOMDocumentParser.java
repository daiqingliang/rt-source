package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMDocumentParser extends Decoder {
  protected Document _document;
  
  protected Node _currentNode;
  
  protected Element _currentElement;
  
  protected Attr[] _namespaceAttributes = new Attr[16];
  
  protected int _namespaceAttributesIndex;
  
  protected int[] _namespacePrefixes = new int[16];
  
  protected int _namespacePrefixesIndex;
  
  public void parse(Document paramDocument, InputStream paramInputStream) throws FastInfosetException, IOException {
    this._currentNode = this._document = paramDocument;
    this._namespaceAttributesIndex = 0;
    parse(paramInputStream);
  }
  
  protected final void parse(InputStream paramInputStream) throws FastInfosetException, IOException {
    setInputStream(paramInputStream);
    parse();
  }
  
  protected void resetOnError() {
    this._namespacePrefixesIndex = 0;
    if (this._v == null)
      this._prefixTable.clearCompletely(); 
    this._duplicateAttributeVerifier.clear();
  }
  
  protected final void parse() {
    try {
      reset();
      decodeHeader();
      processDII();
    } catch (RuntimeException runtimeException) {
      resetOnError();
      throw new FastInfosetException(runtimeException);
    } catch (FastInfosetException fastInfosetException) {
      resetOnError();
      throw fastInfosetException;
    } catch (IOException iOException) {
      resetOnError();
      throw iOException;
    } 
  }
  
  protected final void processDII() {
    this._b = read();
    if (this._b > 0)
      processDIIOptionalProperties(); 
    boolean bool1 = false;
    boolean bool2 = false;
    while (!this._terminate || !bool1) {
      String str2;
      QualifiedName qualifiedName;
      String str1;
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
          qualifiedName = processLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
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
          str1 = ((this._b & 0x2) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
          str2 = ((this._b & true) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
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
          this._notations.clear();
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
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope")); 
    Node node = this._currentNode;
    this._currentNode = this._currentElement = createElement(paramQualifiedName.namespaceName, paramQualifiedName.qName, paramQualifiedName.localName);
    if (this._namespaceAttributesIndex > 0) {
      for (byte b = 0; b < this._namespaceAttributesIndex; b++) {
        this._currentElement.setAttributeNode(this._namespaceAttributes[b]);
        this._namespaceAttributes[b] = null;
      } 
      this._namespaceAttributesIndex = 0;
    } 
    if (paramBoolean)
      processAIIs(); 
    node.appendChild(this._currentElement);
    while (!this._terminate) {
      String str5;
      String str4;
      String str2;
      boolean bool;
      String str1;
      QualifiedName qualifiedName;
      String str3;
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
          qualifiedName = processLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
          this._elementNameTable.add(qualifiedName);
          processEII(qualifiedName, ((this._b & 0x40) > 0));
          continue;
        case 4:
          processEIIWithNamespaces();
          continue;
        case 6:
          this._octetBufferLength = (this._b & true) + 1;
          appendOrCreateTextData(processUtf8CharacterString());
          continue;
        case 7:
          this._octetBufferLength = read() + 3;
          appendOrCreateTextData(processUtf8CharacterString());
          continue;
        case 8:
          this._octetBufferLength = read() << 24 | read() << 16 | read() << 8 | read();
          this._octetBufferLength += 259;
          appendOrCreateTextData(processUtf8CharacterString());
          continue;
        case 9:
          this._octetBufferLength = (this._b & true) + 1;
          str3 = decodeUtf16StringAsString();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          appendOrCreateTextData(str3);
          continue;
        case 10:
          this._octetBufferLength = read() + 3;
          str3 = decodeUtf16StringAsString();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          appendOrCreateTextData(str3);
          continue;
        case 11:
          this._octetBufferLength = read() << 24 | read() << 16 | read() << 8 | read();
          this._octetBufferLength += 259;
          str3 = decodeUtf16StringAsString();
          if ((this._b & 0x10) > 0)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          appendOrCreateTextData(str3);
          continue;
        case 12:
          bool = ((this._b & 0x10) > 0) ? 1 : 0;
          this._identifier = (this._b & 0x2) << 6;
          this._b = read();
          this._identifier |= (this._b & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
          str4 = decodeRestrictedAlphabetAsString();
          if (bool)
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength); 
          appendOrCreateTextData(str4);
          continue;
        case 13:
          bool = ((this._b & 0x10) > 0) ? 1 : 0;
          this._identifier = (this._b & 0x2) << 6;
          this._b = read();
          this._identifier |= (this._b & 0xFC) >> 2;
          decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
          str4 = convertEncodingAlgorithmDataToCharacters(false);
          if (bool)
            this._characterContentChunkTable.add(str4.toCharArray(), str4.length()); 
          appendOrCreateTextData(str4);
          continue;
        case 14:
          str2 = this._characterContentChunkTable.getString(this._b & 0xF);
          appendOrCreateTextData(str2);
          continue;
        case 15:
          i = ((this._b & 0x3) << 8 | read()) + 16;
          str4 = this._characterContentChunkTable.getString(i);
          appendOrCreateTextData(str4);
          continue;
        case 16:
          i = (this._b & 0x3) << 16 | read() << 8 | read();
          i += 1040;
          str4 = this._characterContentChunkTable.getString(i);
          appendOrCreateTextData(str4);
          continue;
        case 17:
          i = read() << 16 | read() << 8 | read();
          i += 263184;
          str4 = this._characterContentChunkTable.getString(i);
          appendOrCreateTextData(str4);
          continue;
        case 18:
          processCommentII();
          continue;
        case 19:
          processProcessingII();
          continue;
        case 21:
          str1 = decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
          str4 = ((this._b & 0x2) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
          str5 = ((this._b & true) > 0) ? decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
          continue;
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
    this._currentNode = node;
  }
  
  private void appendOrCreateTextData(String paramString) {
    Node node = this._currentNode.getLastChild();
    if (node instanceof Text) {
      ((Text)node).appendData(paramString);
    } else {
      this._currentNode.appendChild(this._document.createTextNode(paramString));
    } 
  }
  
  private final String processUtf8CharacterString() throws FastInfosetException, IOException {
    if ((this._b & 0x10) > 0) {
      this._characterContentChunkTable.ensureSize(this._octetBufferLength);
      int i = this._characterContentChunkTable._arrayIndex;
      decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, i);
      this._characterContentChunkTable.add(this._charBufferLength);
      return this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex);
    } 
    decodeUtf8StringAsCharBuffer();
    return new String(this._charBuffer, 0, this._charBufferLength);
  }
  
  protected final void processEIIWithNamespaces() {
    QualifiedName qualifiedName;
    boolean bool = ((this._b & 0x40) > 0);
    if (++this._prefixTable._declarationId == Integer.MAX_VALUE)
      this._prefixTable.clearDeclarationIds(); 
    Attr attr = null;
    int i = this._namespacePrefixesIndex;
    int j;
    for (j = read(); (j & 0xFC) == 204; j = read()) {
      String str;
      if (this._namespaceAttributesIndex == this._namespaceAttributes.length) {
        Attr[] arrayOfAttr = new Attr[this._namespaceAttributesIndex * 3 / 2 + 1];
        System.arraycopy(this._namespaceAttributes, 0, arrayOfAttr, 0, this._namespaceAttributesIndex);
        this._namespaceAttributes = arrayOfAttr;
      } 
      if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
        int[] arrayOfInt = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
        System.arraycopy(this._namespacePrefixes, 0, arrayOfInt, 0, this._namespacePrefixesIndex);
        this._namespacePrefixes = arrayOfInt;
      } 
      switch (j & 0x3) {
        case 0:
          attr = createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
          attr.setValue("");
          this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
          this._prefixIndex = this._namespaceNameIndex = -1;
          break;
        case 1:
          attr = createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
          attr.setValue(decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false));
          this._prefixIndex = this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
          break;
        case 2:
          str = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
          attr = createAttribute("http://www.w3.org/2000/xmlns/", createQualifiedNameString(str), str);
          attr.setValue("");
          this._namespaceNameIndex = -1;
          this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
          break;
        case 3:
          str = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
          attr = createAttribute("http://www.w3.org/2000/xmlns/", createQualifiedNameString(str), str);
          attr.setValue(decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true));
          this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
          break;
      } 
      this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
      this._namespaceAttributes[this._namespaceAttributesIndex++] = attr;
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
        qualifiedName = processLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
        this._elementNameTable.add(qualifiedName);
        processEII(qualifiedName, bool);
        break;
      default:
        throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
    } 
    for (int m = i; m < k; m++)
      this._prefixTable.popScope(this._namespacePrefixes[m]); 
    this._namespacePrefixesIndex = i;
  }
  
  protected final QualifiedName processLiteralQualifiedName(int paramInt, QualifiedName paramQualifiedName) throws FastInfosetException, IOException {
    if (paramQualifiedName == null)
      paramQualifiedName = new QualifiedName(); 
    switch (paramInt) {
      case 0:
        return paramQualifiedName.set(null, null, decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
      case 1:
        return paramQualifiedName.set(null, decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
      case 2:
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
      case 3:
        return paramQualifiedName.set(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
    } 
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
  }
  
  protected final QualifiedName processLiteralQualifiedName(int paramInt) throws FastInfosetException, IOException {
    switch (paramInt) {
      case 0:
        return new QualifiedName(null, null, decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
      case 1:
        return new QualifiedName(null, decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
      case 2:
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
      case 3:
        return new QualifiedName(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
    } 
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
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
    //   27: invokevirtual read : ()I
    //   30: istore_2
    //   31: iload_2
    //   32: invokestatic AII : (I)I
    //   35: tableswitch default -> 202, 0 -> 72, 1 -> 85, 2 -> 116, 3 -> 156, 4 -> 194, 5 -> 189
    //   72: aload_0
    //   73: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   76: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   79: iload_2
    //   80: aaload
    //   81: astore_1
    //   82: goto -> 218
    //   85: iload_2
    //   86: bipush #31
    //   88: iand
    //   89: bipush #8
    //   91: ishl
    //   92: aload_0
    //   93: invokevirtual read : ()I
    //   96: ior
    //   97: bipush #64
    //   99: iadd
    //   100: istore #4
    //   102: aload_0
    //   103: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   106: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   109: iload #4
    //   111: aaload
    //   112: astore_1
    //   113: goto -> 218
    //   116: iload_2
    //   117: bipush #15
    //   119: iand
    //   120: bipush #16
    //   122: ishl
    //   123: aload_0
    //   124: invokevirtual read : ()I
    //   127: bipush #8
    //   129: ishl
    //   130: ior
    //   131: aload_0
    //   132: invokevirtual read : ()I
    //   135: ior
    //   136: sipush #8256
    //   139: iadd
    //   140: istore #4
    //   142: aload_0
    //   143: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   146: getfield _array : [Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   149: iload #4
    //   151: aaload
    //   152: astore_1
    //   153: goto -> 218
    //   156: aload_0
    //   157: iload_2
    //   158: iconst_3
    //   159: iand
    //   160: aload_0
    //   161: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   164: invokevirtual getNext : ()Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   167: invokevirtual processLiteralQualifiedName : (ILcom/sun/xml/internal/fastinfoset/QualifiedName;)Lcom/sun/xml/internal/fastinfoset/QualifiedName;
    //   170: astore_1
    //   171: aload_1
    //   172: sipush #256
    //   175: invokevirtual createAttributeValues : (I)V
    //   178: aload_0
    //   179: getfield _attributeNameTable : Lcom/sun/xml/internal/fastinfoset/util/QualifiedNameArray;
    //   182: aload_1
    //   183: invokevirtual add : (Lcom/sun/xml/internal/fastinfoset/QualifiedName;)V
    //   186: goto -> 218
    //   189: aload_0
    //   190: iconst_1
    //   191: putfield _doubleTerminate : Z
    //   194: aload_0
    //   195: iconst_1
    //   196: putfield _terminate : Z
    //   199: goto -> 1194
    //   202: new java/io/IOException
    //   205: dup
    //   206: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   209: ldc 'message.decodingAIIs'
    //   211: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   214: invokespecial <init> : (Ljava/lang/String;)V
    //   217: athrow
    //   218: aload_1
    //   219: getfield prefixIndex : I
    //   222: ifle -> 260
    //   225: aload_0
    //   226: getfield _prefixTable : Lcom/sun/xml/internal/fastinfoset/util/PrefixArray;
    //   229: getfield _currentInScope : [I
    //   232: aload_1
    //   233: getfield prefixIndex : I
    //   236: iaload
    //   237: aload_1
    //   238: getfield namespaceNameIndex : I
    //   241: if_icmpeq -> 260
    //   244: new com/sun/xml/internal/org/jvnet/fastinfoset/FastInfosetException
    //   247: dup
    //   248: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   251: ldc 'message.AIIqNameNotInScope'
    //   253: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   256: invokespecial <init> : (Ljava/lang/String;)V
    //   259: athrow
    //   260: aload_0
    //   261: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   264: aload_1
    //   265: getfield attributeHash : I
    //   268: aload_1
    //   269: getfield attributeId : I
    //   272: invokevirtual checkForDuplicateAttribute : (II)V
    //   275: aload_0
    //   276: aload_1
    //   277: getfield namespaceName : Ljava/lang/String;
    //   280: aload_1
    //   281: getfield qName : Ljava/lang/String;
    //   284: aload_1
    //   285: getfield localName : Ljava/lang/String;
    //   288: invokevirtual createAttribute : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/w3c/dom/Attr;
    //   291: astore #4
    //   293: aload_0
    //   294: invokevirtual read : ()I
    //   297: istore_2
    //   298: iload_2
    //   299: invokestatic NISTRING : (I)I
    //   302: tableswitch default -> 1178, 0 -> 364, 1 -> 430, 2 -> 497, 3 -> 593, 4 -> 659, 5 -> 726, 6 -> 822, 7 -> 914, 8 -> 1007, 9 -> 1043, 10 -> 1094, 11 -> 1154
    //   364: iload_2
    //   365: bipush #64
    //   367: iand
    //   368: ifle -> 375
    //   371: iconst_1
    //   372: goto -> 376
    //   375: iconst_0
    //   376: istore #5
    //   378: aload_0
    //   379: iload_2
    //   380: bipush #7
    //   382: iand
    //   383: iconst_1
    //   384: iadd
    //   385: putfield _octetBufferLength : I
    //   388: aload_0
    //   389: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   392: astore_3
    //   393: iload #5
    //   395: ifeq -> 407
    //   398: aload_0
    //   399: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   402: aload_3
    //   403: invokevirtual add : (Ljava/lang/String;)I
    //   406: pop
    //   407: aload #4
    //   409: aload_3
    //   410: invokeinterface setValue : (Ljava/lang/String;)V
    //   415: aload_0
    //   416: getfield _currentElement : Lorg/w3c/dom/Element;
    //   419: aload #4
    //   421: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   426: pop
    //   427: goto -> 1194
    //   430: iload_2
    //   431: bipush #64
    //   433: iand
    //   434: ifle -> 441
    //   437: iconst_1
    //   438: goto -> 442
    //   441: iconst_0
    //   442: istore #5
    //   444: aload_0
    //   445: aload_0
    //   446: invokevirtual read : ()I
    //   449: bipush #9
    //   451: iadd
    //   452: putfield _octetBufferLength : I
    //   455: aload_0
    //   456: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   459: astore_3
    //   460: iload #5
    //   462: ifeq -> 474
    //   465: aload_0
    //   466: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   469: aload_3
    //   470: invokevirtual add : (Ljava/lang/String;)I
    //   473: pop
    //   474: aload #4
    //   476: aload_3
    //   477: invokeinterface setValue : (Ljava/lang/String;)V
    //   482: aload_0
    //   483: getfield _currentElement : Lorg/w3c/dom/Element;
    //   486: aload #4
    //   488: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   493: pop
    //   494: goto -> 1194
    //   497: iload_2
    //   498: bipush #64
    //   500: iand
    //   501: ifle -> 508
    //   504: iconst_1
    //   505: goto -> 509
    //   508: iconst_0
    //   509: istore #5
    //   511: aload_0
    //   512: invokevirtual read : ()I
    //   515: bipush #24
    //   517: ishl
    //   518: aload_0
    //   519: invokevirtual read : ()I
    //   522: bipush #16
    //   524: ishl
    //   525: ior
    //   526: aload_0
    //   527: invokevirtual read : ()I
    //   530: bipush #8
    //   532: ishl
    //   533: ior
    //   534: aload_0
    //   535: invokevirtual read : ()I
    //   538: ior
    //   539: istore #6
    //   541: aload_0
    //   542: iload #6
    //   544: sipush #265
    //   547: iadd
    //   548: putfield _octetBufferLength : I
    //   551: aload_0
    //   552: invokevirtual decodeUtf8StringAsString : ()Ljava/lang/String;
    //   555: astore_3
    //   556: iload #5
    //   558: ifeq -> 570
    //   561: aload_0
    //   562: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   565: aload_3
    //   566: invokevirtual add : (Ljava/lang/String;)I
    //   569: pop
    //   570: aload #4
    //   572: aload_3
    //   573: invokeinterface setValue : (Ljava/lang/String;)V
    //   578: aload_0
    //   579: getfield _currentElement : Lorg/w3c/dom/Element;
    //   582: aload #4
    //   584: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   589: pop
    //   590: goto -> 1194
    //   593: iload_2
    //   594: bipush #64
    //   596: iand
    //   597: ifle -> 604
    //   600: iconst_1
    //   601: goto -> 605
    //   604: iconst_0
    //   605: istore #5
    //   607: aload_0
    //   608: iload_2
    //   609: bipush #7
    //   611: iand
    //   612: iconst_1
    //   613: iadd
    //   614: putfield _octetBufferLength : I
    //   617: aload_0
    //   618: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   621: astore_3
    //   622: iload #5
    //   624: ifeq -> 636
    //   627: aload_0
    //   628: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   631: aload_3
    //   632: invokevirtual add : (Ljava/lang/String;)I
    //   635: pop
    //   636: aload #4
    //   638: aload_3
    //   639: invokeinterface setValue : (Ljava/lang/String;)V
    //   644: aload_0
    //   645: getfield _currentElement : Lorg/w3c/dom/Element;
    //   648: aload #4
    //   650: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   655: pop
    //   656: goto -> 1194
    //   659: iload_2
    //   660: bipush #64
    //   662: iand
    //   663: ifle -> 670
    //   666: iconst_1
    //   667: goto -> 671
    //   670: iconst_0
    //   671: istore #5
    //   673: aload_0
    //   674: aload_0
    //   675: invokevirtual read : ()I
    //   678: bipush #9
    //   680: iadd
    //   681: putfield _octetBufferLength : I
    //   684: aload_0
    //   685: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   688: astore_3
    //   689: iload #5
    //   691: ifeq -> 703
    //   694: aload_0
    //   695: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   698: aload_3
    //   699: invokevirtual add : (Ljava/lang/String;)I
    //   702: pop
    //   703: aload #4
    //   705: aload_3
    //   706: invokeinterface setValue : (Ljava/lang/String;)V
    //   711: aload_0
    //   712: getfield _currentElement : Lorg/w3c/dom/Element;
    //   715: aload #4
    //   717: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   722: pop
    //   723: goto -> 1194
    //   726: iload_2
    //   727: bipush #64
    //   729: iand
    //   730: ifle -> 737
    //   733: iconst_1
    //   734: goto -> 738
    //   737: iconst_0
    //   738: istore #5
    //   740: aload_0
    //   741: invokevirtual read : ()I
    //   744: bipush #24
    //   746: ishl
    //   747: aload_0
    //   748: invokevirtual read : ()I
    //   751: bipush #16
    //   753: ishl
    //   754: ior
    //   755: aload_0
    //   756: invokevirtual read : ()I
    //   759: bipush #8
    //   761: ishl
    //   762: ior
    //   763: aload_0
    //   764: invokevirtual read : ()I
    //   767: ior
    //   768: istore #6
    //   770: aload_0
    //   771: iload #6
    //   773: sipush #265
    //   776: iadd
    //   777: putfield _octetBufferLength : I
    //   780: aload_0
    //   781: invokevirtual decodeUtf16StringAsString : ()Ljava/lang/String;
    //   784: astore_3
    //   785: iload #5
    //   787: ifeq -> 799
    //   790: aload_0
    //   791: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   794: aload_3
    //   795: invokevirtual add : (Ljava/lang/String;)I
    //   798: pop
    //   799: aload #4
    //   801: aload_3
    //   802: invokeinterface setValue : (Ljava/lang/String;)V
    //   807: aload_0
    //   808: getfield _currentElement : Lorg/w3c/dom/Element;
    //   811: aload #4
    //   813: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   818: pop
    //   819: goto -> 1194
    //   822: iload_2
    //   823: bipush #64
    //   825: iand
    //   826: ifle -> 833
    //   829: iconst_1
    //   830: goto -> 834
    //   833: iconst_0
    //   834: istore #5
    //   836: aload_0
    //   837: iload_2
    //   838: bipush #15
    //   840: iand
    //   841: iconst_4
    //   842: ishl
    //   843: putfield _identifier : I
    //   846: aload_0
    //   847: invokevirtual read : ()I
    //   850: istore_2
    //   851: aload_0
    //   852: dup
    //   853: getfield _identifier : I
    //   856: iload_2
    //   857: sipush #240
    //   860: iand
    //   861: iconst_4
    //   862: ishr
    //   863: ior
    //   864: putfield _identifier : I
    //   867: aload_0
    //   868: iload_2
    //   869: invokevirtual decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit : (I)V
    //   872: aload_0
    //   873: invokevirtual decodeRestrictedAlphabetAsString : ()Ljava/lang/String;
    //   876: astore_3
    //   877: iload #5
    //   879: ifeq -> 891
    //   882: aload_0
    //   883: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   886: aload_3
    //   887: invokevirtual add : (Ljava/lang/String;)I
    //   890: pop
    //   891: aload #4
    //   893: aload_3
    //   894: invokeinterface setValue : (Ljava/lang/String;)V
    //   899: aload_0
    //   900: getfield _currentElement : Lorg/w3c/dom/Element;
    //   903: aload #4
    //   905: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   910: pop
    //   911: goto -> 1194
    //   914: iload_2
    //   915: bipush #64
    //   917: iand
    //   918: ifle -> 925
    //   921: iconst_1
    //   922: goto -> 926
    //   925: iconst_0
    //   926: istore #5
    //   928: aload_0
    //   929: iload_2
    //   930: bipush #15
    //   932: iand
    //   933: iconst_4
    //   934: ishl
    //   935: putfield _identifier : I
    //   938: aload_0
    //   939: invokevirtual read : ()I
    //   942: istore_2
    //   943: aload_0
    //   944: dup
    //   945: getfield _identifier : I
    //   948: iload_2
    //   949: sipush #240
    //   952: iand
    //   953: iconst_4
    //   954: ishr
    //   955: ior
    //   956: putfield _identifier : I
    //   959: aload_0
    //   960: iload_2
    //   961: invokevirtual decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit : (I)V
    //   964: aload_0
    //   965: iconst_1
    //   966: invokevirtual convertEncodingAlgorithmDataToCharacters : (Z)Ljava/lang/String;
    //   969: astore_3
    //   970: iload #5
    //   972: ifeq -> 984
    //   975: aload_0
    //   976: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   979: aload_3
    //   980: invokevirtual add : (Ljava/lang/String;)I
    //   983: pop
    //   984: aload #4
    //   986: aload_3
    //   987: invokeinterface setValue : (Ljava/lang/String;)V
    //   992: aload_0
    //   993: getfield _currentElement : Lorg/w3c/dom/Element;
    //   996: aload #4
    //   998: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   1003: pop
    //   1004: goto -> 1194
    //   1007: aload_0
    //   1008: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   1011: getfield _array : [Ljava/lang/String;
    //   1014: iload_2
    //   1015: bipush #63
    //   1017: iand
    //   1018: aaload
    //   1019: astore_3
    //   1020: aload #4
    //   1022: aload_3
    //   1023: invokeinterface setValue : (Ljava/lang/String;)V
    //   1028: aload_0
    //   1029: getfield _currentElement : Lorg/w3c/dom/Element;
    //   1032: aload #4
    //   1034: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   1039: pop
    //   1040: goto -> 1194
    //   1043: iload_2
    //   1044: bipush #31
    //   1046: iand
    //   1047: bipush #8
    //   1049: ishl
    //   1050: aload_0
    //   1051: invokevirtual read : ()I
    //   1054: ior
    //   1055: bipush #64
    //   1057: iadd
    //   1058: istore #5
    //   1060: aload_0
    //   1061: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   1064: getfield _array : [Ljava/lang/String;
    //   1067: iload #5
    //   1069: aaload
    //   1070: astore_3
    //   1071: aload #4
    //   1073: aload_3
    //   1074: invokeinterface setValue : (Ljava/lang/String;)V
    //   1079: aload_0
    //   1080: getfield _currentElement : Lorg/w3c/dom/Element;
    //   1083: aload #4
    //   1085: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   1090: pop
    //   1091: goto -> 1194
    //   1094: iload_2
    //   1095: bipush #15
    //   1097: iand
    //   1098: bipush #16
    //   1100: ishl
    //   1101: aload_0
    //   1102: invokevirtual read : ()I
    //   1105: bipush #8
    //   1107: ishl
    //   1108: ior
    //   1109: aload_0
    //   1110: invokevirtual read : ()I
    //   1113: ior
    //   1114: sipush #8256
    //   1117: iadd
    //   1118: istore #5
    //   1120: aload_0
    //   1121: getfield _attributeValueTable : Lcom/sun/xml/internal/fastinfoset/util/StringArray;
    //   1124: getfield _array : [Ljava/lang/String;
    //   1127: iload #5
    //   1129: aaload
    //   1130: astore_3
    //   1131: aload #4
    //   1133: aload_3
    //   1134: invokeinterface setValue : (Ljava/lang/String;)V
    //   1139: aload_0
    //   1140: getfield _currentElement : Lorg/w3c/dom/Element;
    //   1143: aload #4
    //   1145: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   1150: pop
    //   1151: goto -> 1194
    //   1154: aload #4
    //   1156: ldc ''
    //   1158: invokeinterface setValue : (Ljava/lang/String;)V
    //   1163: aload_0
    //   1164: getfield _currentElement : Lorg/w3c/dom/Element;
    //   1167: aload #4
    //   1169: invokeinterface setAttributeNode : (Lorg/w3c/dom/Attr;)Lorg/w3c/dom/Attr;
    //   1174: pop
    //   1175: goto -> 1194
    //   1178: new java/io/IOException
    //   1181: dup
    //   1182: invokestatic getInstance : ()Lcom/sun/xml/internal/fastinfoset/CommonResourceBundle;
    //   1185: ldc 'message.decodingAIIValue'
    //   1187: invokevirtual getString : (Ljava/lang/String;)Ljava/lang/String;
    //   1190: invokespecial <init> : (Ljava/lang/String;)V
    //   1193: athrow
    //   1194: aload_0
    //   1195: getfield _terminate : Z
    //   1198: ifeq -> 26
    //   1201: aload_0
    //   1202: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   1205: aload_0
    //   1206: getfield _duplicateAttributeVerifier : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier;
    //   1209: getfield _poolHead : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier$Entry;
    //   1212: putfield _poolCurrent : Lcom/sun/xml/internal/fastinfoset/util/DuplicateAttributeVerifier$Entry;
    //   1215: aload_0
    //   1216: aload_0
    //   1217: getfield _doubleTerminate : Z
    //   1220: putfield _terminate : Z
    //   1223: aload_0
    //   1224: iconst_0
    //   1225: putfield _doubleTerminate : Z
    //   1228: return }
  
  protected final void processCommentII() {
    String str;
    switch (decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
        str = new String(this._charBuffer, 0, this._charBufferLength);
        if (this._addToTable)
          this._v.otherString.add(new CharArrayString(str, false)); 
        this._currentNode.appendChild(this._document.createComment(str));
        break;
      case 2:
        throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
      case 1:
        str = this._v.otherString.get(this._integer).toString();
        this._currentNode.appendChild(this._document.createComment(str));
        break;
      case 3:
        this._currentNode.appendChild(this._document.createComment(""));
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
          this._v.otherString.add(new CharArrayString(str2, false)); 
        this._currentNode.appendChild(this._document.createProcessingInstruction(str1, str2));
        break;
      case 2:
        throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
      case 1:
        str2 = this._v.otherString.get(this._integer).toString();
        this._currentNode.appendChild(this._document.createProcessingInstruction(str1, str2));
        break;
      case 3:
        this._currentNode.appendChild(this._document.createProcessingInstruction(str1, ""));
        break;
    } 
  }
  
  protected Element createElement(String paramString1, String paramString2, String paramString3) { return this._document.createElementNS(paramString1, paramString2); }
  
  protected Attr createAttribute(String paramString1, String paramString2, String paramString3) { return this._document.createAttributeNS(paramString1, paramString2); }
  
  protected String convertEncodingAlgorithmDataToCharacters(boolean paramBoolean) throws FastInfosetException, IOException {
    StringBuffer stringBuffer = new StringBuffer();
    if (this._identifier < 9) {
      Object object = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
      BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(object, stringBuffer);
    } else {
      if (this._identifier == 9) {
        if (!paramBoolean) {
          this._octetBufferOffset -= this._octetBufferLength;
          return decodeUtf8StringAsString();
        } 
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
      } 
      if (this._identifier >= 32) {
        String str = this._v.encodingAlgorithm.get(this._identifier - 32);
        EncodingAlgorithm encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(str);
        if (encodingAlgorithm != null) {
          Object object = encodingAlgorithm.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
          encodingAlgorithm.convertToCharacters(object, stringBuffer);
        } else {
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
        } 
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\dom\DOMDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */