package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetWriter;
import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAXDocumentSerializer extends Encoder implements FastInfosetWriter {
  protected boolean _elementHasNamespaces = false;
  
  protected boolean _charactersAsCDATA = false;
  
  protected SAXDocumentSerializer(boolean paramBoolean) { super(paramBoolean); }
  
  public SAXDocumentSerializer() {}
  
  public void reset() {
    super.reset();
    this._elementHasNamespaces = false;
    this._charactersAsCDATA = false;
  }
  
  public final void startDocument() {
    try {
      reset();
      encodeHeader(false);
      encodeInitialVocabulary();
    } catch (IOException iOException) {
      throw new SAXException("startDocument", iOException);
    } 
  }
  
  public final void endDocument() {
    try {
      encodeDocumentTermination();
    } catch (IOException iOException) {
      throw new SAXException("endDocument", iOException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    try {
      if (!this._elementHasNamespaces) {
        encodeTermination();
        mark();
        this._elementHasNamespaces = true;
        write(56);
      } 
      encodeNamespaceAttribute(paramString1, paramString2);
    } catch (IOException iOException) {
      throw new SAXException("startElement", iOException);
    } 
  }
  
  public final void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    int i = (paramAttributes != null && paramAttributes.getLength() > 0) ? countAttributes(paramAttributes) : 0;
    try {
      if (this._elementHasNamespaces) {
        this._elementHasNamespaces = false;
        if (i > 0)
          this._octetBuffer[this._markIndex] = (byte)(this._octetBuffer[this._markIndex] | 0x40); 
        resetMark();
        write(240);
        this._b = 0;
      } else {
        encodeTermination();
        this._b = 0;
        if (i > 0)
          this._b |= 0x40; 
      } 
      encodeElement(paramString1, paramString3, paramString2);
      if (i > 0)
        encodeAttributes(paramAttributes); 
    } catch (IOException iOException) {
      throw new SAXException("startElement", iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException("startElement", fastInfosetException);
    } 
  }
  
  public final void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      encodeElementTermination();
    } catch (IOException iOException) {
      throw new SAXException("endElement", iOException);
    } 
  }
  
  public final void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(paramArrayOfChar, paramInt1, paramInt2))
      return; 
    try {
      encodeTermination();
      if (!this._charactersAsCDATA) {
        encodeCharacters(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        encodeCIIBuiltInAlgorithmDataAsCDATA(paramArrayOfChar, paramInt1, paramInt2);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (getIgnoreWhiteSpaceTextContent())
      return; 
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      if (getIgnoreProcesingInstructions())
        return; 
      if (paramString1.length() == 0)
        throw new SAXException(CommonResourceBundle.getInstance().getString("message.processingInstructionTargetIsEmpty")); 
      encodeTermination();
      encodeProcessingInstruction(paramString1, paramString2);
    } catch (IOException iOException) {
      throw new SAXException("processingInstruction", iOException);
    } 
  }
  
  public final void setDocumentLocator(Locator paramLocator) {}
  
  public final void skippedEntity(String paramString) throws SAXException {}
  
  public final void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      if (getIgnoreComments())
        return; 
      encodeTermination();
      encodeComment(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException("startElement", iOException);
    } 
  }
  
  public final void startCDATA() { this._charactersAsCDATA = true; }
  
  public final void endCDATA() { this._charactersAsCDATA = false; }
  
  public final void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (getIgnoreDTD())
      return; 
    try {
      encodeTermination();
      encodeDocumentTypeDeclaration(paramString2, paramString3);
      encodeElementTermination();
    } catch (IOException iOException) {
      throw new SAXException("startDTD", iOException);
    } 
  }
  
  public final void endDTD() {}
  
  public final void startEntity(String paramString) throws SAXException {}
  
  public final void endEntity(String paramString) throws SAXException {}
  
  public final void octets(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws SAXException {
    if (paramInt3 <= 0)
      return; 
    try {
      encodeTermination();
      encodeNonIdentifyingStringOnThirdBit(paramString, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void object(String paramString, int paramInt, Object paramObject) throws SAXException {
    try {
      encodeTermination();
      encodeNonIdentifyingStringOnThirdBit(paramString, paramInt, paramObject);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void bytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIOctetAlgorithmData(1, paramArrayOfByte, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public final void shorts(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(2, paramArrayOfShort, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void ints(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(3, paramArrayOfInt, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void longs(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(4, paramArrayOfLong, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void booleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(5, paramArrayOfBoolean, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void floats(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(6, paramArrayOfFloat, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public final void doubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(7, paramArrayOfDouble, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public void uuids(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(8, paramArrayOfLong, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public void numericCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
      encodeNumericFourBitCharacters(paramArrayOfChar, paramInt1, paramInt2, bool);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public void dateTimeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
      encodeDateTimeFourBitCharacters(paramArrayOfChar, paramInt1, paramInt2, bool);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public void alphabetCharacters(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    try {
      encodeTermination();
      boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
      encodeAlphabetCharacters(paramString, paramArrayOfChar, paramInt1, paramInt2, bool);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws SAXException {
    if (paramInt2 <= 0)
      return; 
    if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(paramArrayOfChar, paramInt1, paramInt2))
      return; 
    try {
      encodeTermination();
      if (!this._charactersAsCDATA) {
        encodeNonIdentifyingStringOnThirdBit(paramArrayOfChar, paramInt1, paramInt2, this._v.characterContentChunk, paramBoolean, true);
      } else {
        encodeCIIBuiltInAlgorithmDataAsCDATA(paramArrayOfChar, paramInt1, paramInt2);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } catch (FastInfosetException fastInfosetException) {
      throw new SAXException(fastInfosetException);
    } 
  }
  
  protected final int countAttributes(Attributes paramAttributes) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramAttributes.getLength(); b2++) {
      String str = paramAttributes.getURI(b2);
      if (str != "http://www.w3.org/2000/xmlns/" && !str.equals("http://www.w3.org/2000/xmlns/"))
        b1++; 
    } 
    return b1;
  }
  
  protected void encodeAttributes(Attributes paramAttributes) throws IOException, FastInfosetException {
    if (paramAttributes instanceof EncodingAlgorithmAttributes) {
      EncodingAlgorithmAttributes encodingAlgorithmAttributes = (EncodingAlgorithmAttributes)paramAttributes;
      for (byte b = 0; b < encodingAlgorithmAttributes.getLength(); b++) {
        if (encodeAttribute(paramAttributes.getURI(b), paramAttributes.getQName(b), paramAttributes.getLocalName(b))) {
          Object object = encodingAlgorithmAttributes.getAlgorithmData(b);
          if (object == null) {
            String str1 = encodingAlgorithmAttributes.getValue(b);
            boolean bool1 = isAttributeValueLengthMatchesLimit(str1.length());
            boolean bool2 = encodingAlgorithmAttributes.getToIndex(b);
            String str2 = encodingAlgorithmAttributes.getAlpababet(b);
            if (str2 == null) {
              encodeNonIdentifyingStringOnFirstBit(str1, this._v.attributeValue, bool1, bool2);
            } else if (str2 == "0123456789-:TZ ") {
              encodeDateTimeNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            } else if (str2 == "0123456789-+.E ") {
              encodeNumericNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            } else {
              encodeNonIdentifyingStringOnFirstBit(str1, this._v.attributeValue, bool1, bool2);
            } 
          } else {
            encodeNonIdentifyingStringOnFirstBit(encodingAlgorithmAttributes.getAlgorithmURI(b), encodingAlgorithmAttributes.getAlgorithmIndex(b), object);
          } 
        } 
      } 
    } else {
      for (byte b = 0; b < paramAttributes.getLength(); b++) {
        if (encodeAttribute(paramAttributes.getURI(b), paramAttributes.getQName(b), paramAttributes.getLocalName(b))) {
          String str = paramAttributes.getValue(b);
          boolean bool = isAttributeValueLengthMatchesLimit(str.length());
          encodeNonIdentifyingStringOnFirstBit(str, this._v.attributeValue, bool, false);
        } 
      } 
    } 
    this._b = 240;
    this._terminate = true;
  }
  
  protected void encodeElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(paramString2);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        QualifiedName qualifiedName = arrayOfQualifiedName[b];
        if (paramString1 == qualifiedName.namespaceName || paramString1.equals(qualifiedName.namespaceName)) {
          encodeNonZeroIntegerOnThirdBit((arrayOfQualifiedName[b]).index);
          return;
        } 
      } 
    } 
    encodeLiteralElementQualifiedNameOnThirdBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, entry);
  }
  
  protected boolean encodeAttribute(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(paramString2);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        if (paramString1 == (arrayOfQualifiedName[b]).namespaceName || paramString1.equals((arrayOfQualifiedName[b]).namespaceName)) {
          encodeNonZeroIntegerOnSecondBitFirstBitZero((arrayOfQualifiedName[b]).index);
          return true;
        } 
      } 
    } 
    return encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, entry);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\sax\SAXDocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */