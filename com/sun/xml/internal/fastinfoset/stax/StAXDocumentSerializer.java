package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EmptyStackException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StAXDocumentSerializer extends Encoder implements XMLStreamWriter, LowLevelFastInfosetStreamWriter {
  protected StAXManager _manager;
  
  protected String _encoding;
  
  protected String _currentLocalName;
  
  protected String _currentUri;
  
  protected String _currentPrefix;
  
  protected boolean _inStartElement = false;
  
  protected boolean _isEmptyElement = false;
  
  protected String[] _attributesArray = new String[64];
  
  protected int _attributesArrayIndex = 0;
  
  protected boolean[] _nsSupportContextStack = new boolean[32];
  
  protected int _stackCount = -1;
  
  protected NamespaceContextImplementation _nsContext = new NamespaceContextImplementation();
  
  protected String[] _namespacesArray = new String[16];
  
  protected int _namespacesArrayIndex = 0;
  
  public StAXDocumentSerializer() {
    super(true);
    this._manager = new StAXManager(2);
  }
  
  public StAXDocumentSerializer(OutputStream paramOutputStream) {
    super(true);
    setOutputStream(paramOutputStream);
    this._manager = new StAXManager(2);
  }
  
  public StAXDocumentSerializer(OutputStream paramOutputStream, StAXManager paramStAXManager) {
    super(true);
    setOutputStream(paramOutputStream);
    this._manager = paramStAXManager;
  }
  
  public void reset() {
    super.reset();
    this._attributesArrayIndex = 0;
    this._namespacesArrayIndex = 0;
    this._nsContext.reset();
    this._stackCount = -1;
    this._currentUri = this._currentPrefix = null;
    this._currentLocalName = null;
    this._inStartElement = this._isEmptyElement = false;
  }
  
  public void writeStartDocument() { writeStartDocument("finf", "1.0"); }
  
  public void writeStartDocument(String paramString) throws XMLStreamException { writeStartDocument("finf", paramString); }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
    reset();
    try {
      encodeHeader(false);
      encodeInitialVocabulary();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeEndDocument() {
    try {
      while (this._stackCount >= 0) {
        writeEndElement();
        this._stackCount--;
      } 
      encodeDocumentTermination();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void close() { reset(); }
  
  public void flush() {
    try {
      this._s.flush();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeStartElement(String paramString) throws XMLStreamException { writeStartElement("", paramString, ""); }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException { writeStartElement("", paramString2, paramString1); }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    encodeTerminationAndCurrentElement(false);
    this._inStartElement = true;
    this._isEmptyElement = false;
    this._currentLocalName = paramString2;
    this._currentPrefix = paramString1;
    this._currentUri = paramString3;
    this._stackCount++;
    if (this._stackCount == this._nsSupportContextStack.length) {
      boolean[] arrayOfBoolean = new boolean[this._stackCount * 2];
      System.arraycopy(this._nsSupportContextStack, 0, arrayOfBoolean, 0, this._nsSupportContextStack.length);
      this._nsSupportContextStack = arrayOfBoolean;
    } 
    this._nsSupportContextStack[this._stackCount] = false;
  }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException { writeEmptyElement("", paramString, ""); }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException { writeEmptyElement("", paramString2, paramString1); }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    encodeTerminationAndCurrentElement(false);
    this._isEmptyElement = this._inStartElement = true;
    this._currentLocalName = paramString2;
    this._currentPrefix = paramString1;
    this._currentUri = paramString3;
    this._stackCount++;
    if (this._stackCount == this._nsSupportContextStack.length) {
      boolean[] arrayOfBoolean = new boolean[this._stackCount * 2];
      System.arraycopy(this._nsSupportContextStack, 0, arrayOfBoolean, 0, this._nsSupportContextStack.length);
      this._nsSupportContextStack = arrayOfBoolean;
    } 
    this._nsSupportContextStack[this._stackCount] = false;
  }
  
  public void writeEndElement() {
    if (this._inStartElement)
      encodeTerminationAndCurrentElement(false); 
    try {
      encodeElementTermination();
      if (this._nsSupportContextStack[this._stackCount--] == true)
        this._nsContext.popContext(); 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } catch (EmptyStackException emptyStackException) {
      throw new XMLStreamException(emptyStackException);
    } 
  }
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException { writeAttribute("", "", paramString1, paramString2); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    String str = "";
    if (paramString1.length() > 0) {
      str = this._nsContext.getNonDefaultPrefix(paramString1);
      if (str == null || str.length() == 0) {
        if (paramString1 == "http://www.w3.org/2000/xmlns/" || paramString1.equals("http://www.w3.org/2000/xmlns/"))
          return; 
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.URIUnbound", new Object[] { paramString1 }));
      } 
    } 
    writeAttribute(str, paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException {
    if (!this._inStartElement)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed")); 
    if (paramString2 == "http://www.w3.org/2000/xmlns/" || paramString2.equals("http://www.w3.org/2000/xmlns/"))
      return; 
    if (this._attributesArrayIndex == this._attributesArray.length) {
      String[] arrayOfString = new String[this._attributesArrayIndex * 2];
      System.arraycopy(this._attributesArray, 0, arrayOfString, 0, this._attributesArrayIndex);
      this._attributesArray = arrayOfString;
    } 
    this._attributesArray[this._attributesArrayIndex++] = paramString2;
    this._attributesArray[this._attributesArrayIndex++] = paramString1;
    this._attributesArray[this._attributesArrayIndex++] = paramString3;
    this._attributesArray[this._attributesArrayIndex++] = paramString4;
  }
  
  public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString1 == null || paramString1.length() == 0 || paramString1.equals("xmlns")) {
      writeDefaultNamespace(paramString2);
    } else {
      if (!this._inStartElement)
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed")); 
      if (this._namespacesArrayIndex == this._namespacesArray.length) {
        String[] arrayOfString = new String[this._namespacesArrayIndex * 2];
        System.arraycopy(this._namespacesArray, 0, arrayOfString, 0, this._namespacesArrayIndex);
        this._namespacesArray = arrayOfString;
      } 
      this._namespacesArray[this._namespacesArrayIndex++] = paramString1;
      this._namespacesArray[this._namespacesArrayIndex++] = paramString2;
      setPrefix(paramString1, paramString2);
    } 
  }
  
  public void writeDefaultNamespace(String paramString) throws XMLStreamException {
    if (!this._inStartElement)
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed")); 
    if (this._namespacesArrayIndex == this._namespacesArray.length) {
      String[] arrayOfString = new String[this._namespacesArrayIndex * 2];
      System.arraycopy(this._namespacesArray, 0, arrayOfString, 0, this._namespacesArrayIndex);
      this._namespacesArray = arrayOfString;
    } 
    this._namespacesArray[this._namespacesArrayIndex++] = "";
    this._namespacesArray[this._namespacesArrayIndex++] = paramString;
    setPrefix("", paramString);
  }
  
  public void writeComment(String paramString) throws XMLStreamException {
    try {
      if (getIgnoreComments())
        return; 
      encodeTerminationAndCurrentElement(true);
      encodeComment(paramString.toCharArray(), 0, paramString.length());
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeProcessingInstruction(String paramString) throws XMLStreamException { writeProcessingInstruction(paramString, ""); }
  
  public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException {
    try {
      if (getIgnoreProcesingInstructions())
        return; 
      encodeTerminationAndCurrentElement(true);
      encodeProcessingInstruction(paramString1, paramString2);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeCData(String paramString) throws XMLStreamException {
    try {
      int i = paramString.length();
      if (i == 0)
        return; 
      if (i < this._charBuffer.length) {
        if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(paramString))
          return; 
        encodeTerminationAndCurrentElement(true);
        paramString.getChars(0, i, this._charBuffer, 0);
        encodeCIIBuiltInAlgorithmDataAsCDATA(this._charBuffer, 0, i);
      } else {
        char[] arrayOfChar = paramString.toCharArray();
        if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(arrayOfChar, 0, i))
          return; 
        encodeTerminationAndCurrentElement(true);
        encodeCIIBuiltInAlgorithmDataAsCDATA(arrayOfChar, 0, i);
      } 
    } catch (Exception exception) {
      throw new XMLStreamException(exception);
    } 
  }
  
  public void writeDTD(String paramString) throws XMLStreamException { throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented")); }
  
  public void writeEntityRef(String paramString) throws XMLStreamException { throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented")); }
  
  public void writeCharacters(String paramString) throws XMLStreamException {
    try {
      int i = paramString.length();
      if (i == 0)
        return; 
      if (i < this._charBuffer.length) {
        if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(paramString))
          return; 
        encodeTerminationAndCurrentElement(true);
        paramString.getChars(0, i, this._charBuffer, 0);
        encodeCharacters(this._charBuffer, 0, i);
      } else {
        char[] arrayOfChar = paramString.toCharArray();
        if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(arrayOfChar, 0, i))
          return; 
        encodeTerminationAndCurrentElement(true);
        encodeCharactersNoClone(arrayOfChar, 0, i);
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    try {
      if (paramInt2 <= 0)
        return; 
      if (getIgnoreWhiteSpaceTextContent() && isWhiteSpace(paramArrayOfChar, paramInt1, paramInt2))
        return; 
      encodeTerminationAndCurrentElement(true);
      encodeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this._nsContext.getPrefix(paramString); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException {
    if (this._stackCount > -1 && !this._nsSupportContextStack[this._stackCount]) {
      this._nsSupportContextStack[this._stackCount] = true;
      this._nsContext.pushContext();
    } 
    this._nsContext.declarePrefix(paramString1, paramString2);
  }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException { setPrefix("", paramString); }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { throw new UnsupportedOperationException("setNamespaceContext"); }
  
  public NamespaceContext getNamespaceContext() { return this._nsContext; }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return (this._manager != null) ? this._manager.getProperty(paramString) : null; }
  
  public void setManager(StAXManager paramStAXManager) { this._manager = paramStAXManager; }
  
  public void setEncoding(String paramString) throws XMLStreamException { this._encoding = paramString; }
  
  public void writeOctets(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws XMLStreamException {
    try {
      if (paramInt2 == 0)
        return; 
      encodeTerminationAndCurrentElement(true);
      encodeCIIOctetAlgorithmData(1, paramArrayOfByte, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  protected void encodeTerminationAndCurrentElement(boolean paramBoolean) throws XMLStreamException {
    try {
      encodeTermination();
      if (this._inStartElement) {
        this._b = 0;
        if (this._attributesArrayIndex > 0)
          this._b |= 0x40; 
        if (this._namespacesArrayIndex > 0) {
          write(this._b | 0x38);
          byte b1 = 0;
          while (b1 < this._namespacesArrayIndex)
            encodeNamespaceAttribute(this._namespacesArray[b1++], this._namespacesArray[b1++]); 
          this._namespacesArrayIndex = 0;
          write(240);
          this._b = 0;
        } 
        if (this._currentPrefix.length() == 0)
          if (this._currentUri.length() == 0) {
            this._currentUri = this._nsContext.getNamespaceURI("");
          } else {
            String str = getPrefix(this._currentUri);
            if (str != null)
              this._currentPrefix = str; 
          }  
        encodeElementQualifiedNameOnThirdBit(this._currentUri, this._currentPrefix, this._currentLocalName);
        byte b = 0;
        while (b < this._attributesArrayIndex) {
          encodeAttributeQualifiedNameOnSecondBit(this._attributesArray[b++], this._attributesArray[b++], this._attributesArray[b++]);
          String str = this._attributesArray[b];
          this._attributesArray[b++] = null;
          boolean bool = isAttributeValueLengthMatchesLimit(str.length());
          encodeNonIdentifyingStringOnFirstBit(str, this._v.attributeValue, bool, false);
          this._b = 240;
          this._terminate = true;
        } 
        this._attributesArrayIndex = 0;
        this._inStartElement = false;
        if (this._isEmptyElement) {
          encodeElementTermination();
          if (this._nsSupportContextStack[this._stackCount--] == true)
            this._nsContext.popContext(); 
          this._isEmptyElement = false;
        } 
        if (paramBoolean)
          encodeTermination(); 
      } 
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  public final void initiateLowLevelWriting() { encodeTerminationAndCurrentElement(false); }
  
  public final int getNextElementIndex() { return this._v.elementName.getNextIndex(); }
  
  public final int getNextAttributeIndex() { return this._v.attributeName.getNextIndex(); }
  
  public final int getLocalNameIndex() { return this._v.localName.getIndex(); }
  
  public final int getNextLocalNameIndex() { return this._v.localName.getNextIndex(); }
  
  public final void writeLowLevelTerminationAndMark() {
    encodeTermination();
    mark();
  }
  
  public final void writeLowLevelStartElementIndexed(int paramInt1, int paramInt2) throws IOException {
    this._b = paramInt1;
    encodeNonZeroIntegerOnThirdBit(paramInt2);
  }
  
  public final boolean writeLowLevelStartElement(int paramInt, String paramString1, String paramString2, String paramString3) throws IOException {
    boolean bool = encodeElement(paramInt, paramString3, paramString1, paramString2);
    if (!bool)
      encodeLiteral(paramInt | 0x3C, paramString3, paramString1, paramString2); 
    return bool;
  }
  
  public final void writeLowLevelStartNamespaces() { write(56); }
  
  public final void writeLowLevelNamespace(String paramString1, String paramString2) throws XMLStreamException { encodeNamespaceAttribute(paramString1, paramString2); }
  
  public final void writeLowLevelEndNamespaces() { write(240); }
  
  public final void writeLowLevelStartAttributes() {
    if (hasMark()) {
      this._octetBuffer[this._markIndex] = (byte)(this._octetBuffer[this._markIndex] | 0x40);
      resetMark();
    } 
  }
  
  public final void writeLowLevelAttributeIndexed(int paramInt) throws IOException { encodeNonZeroIntegerOnSecondBitFirstBitZero(paramInt); }
  
  public final boolean writeLowLevelAttribute(String paramString1, String paramString2, String paramString3) throws IOException {
    boolean bool = encodeAttribute(paramString2, paramString1, paramString3);
    if (!bool)
      encodeLiteral(120, paramString2, paramString1, paramString3); 
    return bool;
  }
  
  public final void writeLowLevelAttributeValue(String paramString) throws XMLStreamException {
    boolean bool = isAttributeValueLengthMatchesLimit(paramString.length());
    encodeNonIdentifyingStringOnFirstBit(paramString, this._v.attributeValue, bool, false);
  }
  
  public final void writeLowLevelStartNameLiteral(int paramInt, String paramString1, byte[] paramArrayOfByte, String paramString2) throws IOException {
    encodeLiteralHeader(paramInt, paramString2, paramString1);
    encodeNonZeroOctetStringLengthOnSecondBit(paramArrayOfByte.length);
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final void writeLowLevelStartNameLiteral(int paramInt1, String paramString1, int paramInt2, String paramString2) throws IOException {
    encodeLiteralHeader(paramInt1, paramString2, paramString1);
    encodeNonZeroIntegerOnSecondBitFirstBitOne(paramInt2);
  }
  
  public final void writeLowLevelEndStartElement() {
    if (hasMark()) {
      resetMark();
    } else {
      this._b = 240;
      this._terminate = true;
    } 
  }
  
  public final void writeLowLevelEndElement() { encodeElementTermination(); }
  
  public final void writeLowLevelText(char[] paramArrayOfChar, int paramInt) throws IOException {
    if (paramInt == 0)
      return; 
    encodeTermination();
    encodeCharacters(paramArrayOfChar, 0, paramInt);
  }
  
  public final void writeLowLevelText(String paramString) throws XMLStreamException {
    int i = paramString.length();
    if (i == 0)
      return; 
    encodeTermination();
    if (i < this._charBuffer.length) {
      paramString.getChars(0, i, this._charBuffer, 0);
      encodeCharacters(this._charBuffer, 0, i);
    } else {
      char[] arrayOfChar = paramString.toCharArray();
      encodeCharactersNoClone(arrayOfChar, 0, i);
    } 
  }
  
  public final void writeLowLevelOctets(byte[] paramArrayOfByte, int paramInt) throws IOException {
    if (paramInt == 0)
      return; 
    encodeTermination();
    encodeCIIOctetAlgorithmData(1, paramArrayOfByte, 0, paramInt);
  }
  
  private boolean encodeElement(int paramInt, String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(paramString3);
    for (byte b = 0; b < entry._valueIndex; b++) {
      QualifiedName qualifiedName = entry._value[b];
      if ((paramString2 == qualifiedName.prefix || paramString2.equals(qualifiedName.prefix)) && (paramString1 == qualifiedName.namespaceName || paramString1.equals(qualifiedName.namespaceName))) {
        this._b = paramInt;
        encodeNonZeroIntegerOnThirdBit(qualifiedName.index);
        return true;
      } 
    } 
    entry.addQualifiedName(new QualifiedName(paramString2, paramString1, paramString3, "", this._v.elementName.getNextIndex()));
    return false;
  }
  
  private boolean encodeAttribute(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(paramString3);
    for (byte b = 0; b < entry._valueIndex; b++) {
      QualifiedName qualifiedName = entry._value[b];
      if ((paramString2 == qualifiedName.prefix || paramString2.equals(qualifiedName.prefix)) && (paramString1 == qualifiedName.namespaceName || paramString1.equals(qualifiedName.namespaceName))) {
        encodeNonZeroIntegerOnSecondBitFirstBitZero(qualifiedName.index);
        return true;
      } 
    } 
    entry.addQualifiedName(new QualifiedName(paramString2, paramString1, paramString3, "", this._v.attributeName.getNextIndex()));
    return false;
  }
  
  private void encodeLiteralHeader(int paramInt, String paramString1, String paramString2) throws IOException {
    if (paramString1 != "") {
      paramInt |= 0x1;
      if (paramString2 != "")
        paramInt |= 0x2; 
      write(paramInt);
      if (paramString2 != "")
        encodeNonZeroIntegerOnSecondBitFirstBitOne(this._v.prefix.get(paramString2)); 
      encodeNonZeroIntegerOnSecondBitFirstBitOne(this._v.namespaceName.get(paramString1));
    } else {
      write(paramInt);
    } 
  }
  
  private void encodeLiteral(int paramInt, String paramString1, String paramString2, String paramString3) throws IOException {
    encodeLiteralHeader(paramInt, paramString1, paramString2);
    int i = this._v.localName.obtainIndex(paramString3);
    if (i == -1) {
      encodeNonEmptyOctetStringOnSecondBit(paramString3);
    } else {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\StAXDocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */