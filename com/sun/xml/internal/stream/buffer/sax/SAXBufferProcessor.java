package com.sun.xml.internal.stream.buffer.sax;

import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public class SAXBufferProcessor extends AbstractProcessor implements XMLReader {
  protected EntityResolver _entityResolver = DEFAULT_LEXICAL_HANDLER;
  
  protected DTDHandler _dtdHandler = DEFAULT_LEXICAL_HANDLER;
  
  protected ContentHandler _contentHandler = DEFAULT_LEXICAL_HANDLER;
  
  protected ErrorHandler _errorHandler = DEFAULT_LEXICAL_HANDLER;
  
  protected LexicalHandler _lexicalHandler = DEFAULT_LEXICAL_HANDLER;
  
  protected boolean _namespacePrefixesFeature = false;
  
  protected AttributesHolder _attributes = new AttributesHolder();
  
  protected String[] _namespacePrefixes = new String[16];
  
  protected int _namespacePrefixesIndex;
  
  protected int[] _namespaceAttributesStartingStack = new int[16];
  
  protected int[] _namespaceAttributesStack = new int[16];
  
  protected int _namespaceAttributesStackIndex;
  
  private static final DefaultWithLexicalHandler DEFAULT_LEXICAL_HANDLER = new DefaultWithLexicalHandler();
  
  public SAXBufferProcessor() {}
  
  public SAXBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer) { setXMLStreamBuffer(paramXMLStreamBuffer); }
  
  public SAXBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean) { setXMLStreamBuffer(paramXMLStreamBuffer, paramBoolean); }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/features/namespaces"))
      return true; 
    if (paramString.equals("http://xml.org/sax/features/namespace-prefixes"))
      return this._namespacePrefixesFeature; 
    if (paramString.equals("http://xml.org/sax/features/external-general-entities"))
      return true; 
    if (paramString.equals("http://xml.org/sax/features/external-parameter-entities"))
      return true; 
    if (paramString.equals("http://xml.org/sax/features/string-interning"))
      return this._stringInterningFeature; 
    throw new SAXNotRecognizedException("Feature not supported: " + paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/features/namespaces")) {
      if (!paramBoolean)
        throw new SAXNotSupportedException(paramString + ":" + paramBoolean); 
    } else if (paramString.equals("http://xml.org/sax/features/namespace-prefixes")) {
      this._namespacePrefixesFeature = paramBoolean;
    } else if (!paramString.equals("http://xml.org/sax/features/external-general-entities") && !paramString.equals("http://xml.org/sax/features/external-parameter-entities")) {
      if (paramString.equals("http://xml.org/sax/features/string-interning")) {
        if (paramBoolean != this._stringInterningFeature)
          throw new SAXNotSupportedException(paramString + ":" + paramBoolean); 
      } else {
        throw new SAXNotRecognizedException("Feature not supported: " + paramString);
      } 
    } 
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler"))
      return getLexicalHandler(); 
    throw new SAXNotRecognizedException("Property not recognized: " + paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler")) {
      if (paramObject instanceof LexicalHandler) {
        setLexicalHandler((LexicalHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
      } 
    } else {
      throw new SAXNotRecognizedException("Property not recognized: " + paramString);
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
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) { this._lexicalHandler = paramLexicalHandler; }
  
  public LexicalHandler getLexicalHandler() { return this._lexicalHandler; }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException { process(); }
  
  public void parse(String paramString) throws IOException, SAXException { process(); }
  
  public final void process(XMLStreamBuffer paramXMLStreamBuffer) {
    setXMLStreamBuffer(paramXMLStreamBuffer);
    process();
  }
  
  public final void process(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean) {
    setXMLStreamBuffer(paramXMLStreamBuffer);
    process();
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer) { setBuffer(paramXMLStreamBuffer); }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean) {
    if (!paramBoolean && this._treeCount > 1)
      throw new IllegalStateException("Can't write a forest to a full XML infoset"); 
    setBuffer(paramXMLStreamBuffer, paramBoolean);
  }
  
  public final void process() {
    if (!this._fragmentMode) {
      LocatorImpl locatorImpl = new LocatorImpl();
      locatorImpl.setSystemId(this._buffer.getSystemId());
      locatorImpl.setLineNumber(-1);
      locatorImpl.setColumnNumber(-1);
      this._contentHandler.setDocumentLocator(locatorImpl);
      this._contentHandler.startDocument();
    } 
    while (this._treeCount > 0) {
      String str3;
      String str2;
      String str1;
      int i = readEiiState();
      switch (i) {
        case 1:
          processDocument();
          this._treeCount--;
          continue;
        case 17:
          return;
        case 3:
          processElement(readStructureString(), readStructureString(), readStructureString(), isInscope());
          this._treeCount--;
          continue;
        case 4:
          str1 = readStructureString();
          str2 = readStructureString();
          str3 = readStructureString();
          processElement(str2, str3, getQName(str1, str3), isInscope());
          this._treeCount--;
          continue;
        case 5:
          str1 = readStructureString();
          str2 = readStructureString();
          processElement(str1, str2, str2, isInscope());
          this._treeCount--;
          continue;
        case 6:
          str1 = readStructureString();
          processElement("", str1, str1, isInscope());
          this._treeCount--;
          continue;
        case 12:
          processCommentAsCharArraySmall();
          continue;
        case 13:
          processCommentAsCharArrayMedium();
          continue;
        case 14:
          processCommentAsCharArrayCopy();
          continue;
        case 15:
          processComment(readContentString());
          continue;
        case 16:
          processProcessingInstruction(readStructureString(), readStructureString());
          continue;
      } 
      throw reportFatalError("Illegal state for DIIs: " + i);
    } 
    if (!this._fragmentMode)
      this._contentHandler.endDocument(); 
  }
  
  private void processCommentAsCharArraySmall() {
    int i = readStructure();
    int j = readContentCharactersBuffer(i);
    processComment(this._contentCharactersBuffer, j, i);
  }
  
  private SAXParseException reportFatalError(String paramString) throws SAXException {
    SAXParseException sAXParseException = new SAXParseException(paramString, null);
    if (this._errorHandler != null)
      this._errorHandler.fatalError(sAXParseException); 
    return sAXParseException;
  }
  
  private boolean isInscope() { return (this._buffer.getInscopeNamespaces().size() > 0); }
  
  private void processDocument() {
    int i;
    while (true) {
      String str3;
      String str2;
      String str1;
      i = readEiiState();
      switch (i) {
        case 3:
          processElement(readStructureString(), readStructureString(), readStructureString(), isInscope());
          continue;
        case 4:
          str1 = readStructureString();
          str2 = readStructureString();
          str3 = readStructureString();
          processElement(str2, str3, getQName(str1, str3), isInscope());
          continue;
        case 5:
          str1 = readStructureString();
          str2 = readStructureString();
          processElement(str1, str2, str2, isInscope());
          continue;
        case 6:
          str1 = readStructureString();
          processElement("", str1, str1, isInscope());
          continue;
        case 12:
          processCommentAsCharArraySmall();
          continue;
        case 13:
          processCommentAsCharArrayMedium();
          continue;
        case 14:
          processCommentAsCharArrayCopy();
          continue;
        case 15:
          processComment(readContentString());
          continue;
        case 16:
          processProcessingInstruction(readStructureString(), readStructureString());
          continue;
        case 17:
          return;
      } 
      break;
    } 
    throw reportFatalError("Illegal state for child of DII: " + i);
  }
  
  protected void processElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean) throws SAXException {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = peekStructure();
    HashSet hashSet = paramBoolean ? new HashSet() : Collections.emptySet();
    if ((i & 0xF0) == 64) {
      cacheNamespacePrefixStartingIndex();
      bool2 = true;
      i = processNamespaceAttributes(i, paramBoolean, hashSet);
    } 
    if (paramBoolean)
      readInscopeNamespaces(hashSet); 
    if ((i & 0xF0) == 48) {
      bool1 = true;
      processAttributes(i);
    } 
    this._contentHandler.startElement(paramString1, paramString2, paramString3, this._attributes);
    if (bool1)
      this._attributes.clear(); 
    do {
      String str5;
      String str4;
      int k;
      String str3;
      String str2;
      int j;
      CharSequence charSequence;
      char[] arrayOfChar;
      String str1;
      i = readEiiState();
      switch (i) {
        case 3:
          processElement(readStructureString(), readStructureString(), readStructureString(), false);
          break;
        case 4:
          str2 = readStructureString();
          str4 = readStructureString();
          str5 = readStructureString();
          processElement(str4, str5, getQName(str2, str5), false);
          break;
        case 5:
          str2 = readStructureString();
          str4 = readStructureString();
          processElement(str2, str4, str4, false);
          break;
        case 6:
          str2 = readStructureString();
          processElement("", str2, str2, false);
          break;
        case 7:
          j = readStructure();
          k = readContentCharactersBuffer(j);
          this._contentHandler.characters(this._contentCharactersBuffer, k, j);
          break;
        case 8:
          j = readStructure16();
          k = readContentCharactersBuffer(j);
          this._contentHandler.characters(this._contentCharactersBuffer, k, j);
          break;
        case 9:
          arrayOfChar = readContentCharactersCopy();
          this._contentHandler.characters(arrayOfChar, 0, arrayOfChar.length);
          break;
        case 10:
          str1 = readContentString();
          this._contentHandler.characters(str1.toCharArray(), 0, str1.length());
          break;
        case 11:
          charSequence = (CharSequence)readContentObject();
          str3 = charSequence.toString();
          this._contentHandler.characters(str3.toCharArray(), 0, str3.length());
          break;
        case 12:
          processCommentAsCharArraySmall();
          break;
        case 13:
          processCommentAsCharArrayMedium();
          break;
        case 14:
          processCommentAsCharArrayCopy();
          break;
        case 104:
          processComment(readContentString());
          break;
        case 16:
          processProcessingInstruction(readStructureString(), readStructureString());
          break;
        case 17:
          break;
        default:
          throw reportFatalError("Illegal state for child of EII: " + i);
      } 
    } while (i != 17);
    this._contentHandler.endElement(paramString1, paramString2, paramString3);
    if (bool2)
      processEndPrefixMapping(); 
  }
  
  private void readInscopeNamespaces(Set<String> paramSet) throws SAXException {
    for (Map.Entry entry : this._buffer.getInscopeNamespaces().entrySet()) {
      String str = fixNull((String)entry.getKey());
      if (!paramSet.contains(str))
        processNamespaceAttribute(str, (String)entry.getValue()); 
    } 
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  private void processCommentAsCharArrayCopy() {
    char[] arrayOfChar = readContentCharactersCopy();
    processComment(arrayOfChar, 0, arrayOfChar.length);
  }
  
  private void processCommentAsCharArrayMedium() {
    int i = readStructure16();
    int j = readContentCharactersBuffer(i);
    processComment(this._contentCharactersBuffer, j, i);
  }
  
  private void processEndPrefixMapping() {
    int i = this._namespaceAttributesStack[--this._namespaceAttributesStackIndex];
    int j = (this._namespaceAttributesStackIndex >= 0) ? this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] : 0;
    for (int k = i - 1; k >= j; k--)
      this._contentHandler.endPrefixMapping(this._namespacePrefixes[k]); 
    this._namespacePrefixesIndex = j;
  }
  
  private int processNamespaceAttributes(int paramInt, boolean paramBoolean, Set<String> paramSet) throws SAXException {
    do {
      String str;
      switch (getNIIState(paramInt)) {
        case 1:
          processNamespaceAttribute("", "");
          if (paramBoolean)
            paramSet.add(""); 
          break;
        case 2:
          str = readStructureString();
          processNamespaceAttribute(str, "");
          if (paramBoolean)
            paramSet.add(str); 
          break;
        case 3:
          str = readStructureString();
          processNamespaceAttribute(str, readStructureString());
          if (paramBoolean)
            paramSet.add(str); 
          break;
        case 4:
          processNamespaceAttribute("", readStructureString());
          if (paramBoolean)
            paramSet.add(""); 
          break;
        default:
          throw reportFatalError("Illegal state: " + paramInt);
      } 
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 64);
    cacheNamespacePrefixIndex();
    return paramInt;
  }
  
  private void processAttributes(int paramInt) throws SAXException {
    do {
      String str3;
      String str2;
      String str1;
      switch (getAIIState(paramInt)) {
        case 1:
          this._attributes.addAttributeWithQName(readStructureString(), readStructureString(), readStructureString(), readStructureString(), readContentString());
          break;
        case 2:
          str1 = readStructureString();
          str2 = readStructureString();
          str3 = readStructureString();
          this._attributes.addAttributeWithQName(str2, str3, getQName(str1, str3), readStructureString(), readContentString());
          break;
        case 3:
          str1 = readStructureString();
          str2 = readStructureString();
          this._attributes.addAttributeWithQName(str1, str2, str2, readStructureString(), readContentString());
          break;
        case 4:
          str1 = readStructureString();
          this._attributes.addAttributeWithQName("", str1, str1, readStructureString(), readContentString());
          break;
        default:
          throw reportFatalError("Illegal state: " + paramInt);
      } 
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 48);
  }
  
  private void processNamespaceAttribute(String paramString1, String paramString2) throws SAXException {
    this._contentHandler.startPrefixMapping(paramString1, paramString2);
    if (this._namespacePrefixesFeature)
      if (paramString1 != "") {
        this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", paramString1, getQName("xmlns", paramString1), "CDATA", paramString2);
      } else {
        this._attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", paramString2);
      }  
    cacheNamespacePrefix(paramString1);
  }
  
  private void cacheNamespacePrefix(String paramString) throws IOException, SAXException {
    if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
      String[] arrayOfString = new String[this._namespacePrefixesIndex * 3 / 2 + 1];
      System.arraycopy(this._namespacePrefixes, 0, arrayOfString, 0, this._namespacePrefixesIndex);
      this._namespacePrefixes = arrayOfString;
    } 
    this._namespacePrefixes[this._namespacePrefixesIndex++] = paramString;
  }
  
  private void cacheNamespacePrefixIndex() {
    if (this._namespaceAttributesStackIndex == this._namespaceAttributesStack.length) {
      int[] arrayOfInt = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
      System.arraycopy(this._namespaceAttributesStack, 0, arrayOfInt, 0, this._namespaceAttributesStackIndex);
      this._namespaceAttributesStack = arrayOfInt;
    } 
    this._namespaceAttributesStack[this._namespaceAttributesStackIndex++] = this._namespacePrefixesIndex;
  }
  
  private void cacheNamespacePrefixStartingIndex() {
    if (this._namespaceAttributesStackIndex == this._namespaceAttributesStartingStack.length) {
      int[] arrayOfInt = new int[this._namespaceAttributesStackIndex * 3 / 2 + 1];
      System.arraycopy(this._namespaceAttributesStartingStack, 0, arrayOfInt, 0, this._namespaceAttributesStackIndex);
      this._namespaceAttributesStartingStack = arrayOfInt;
    } 
    this._namespaceAttributesStartingStack[this._namespaceAttributesStackIndex] = this._namespacePrefixesIndex;
  }
  
  private void processComment(String paramString) throws IOException, SAXException { processComment(paramString.toCharArray(), 0, paramString.length()); }
  
  private void processComment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { this._lexicalHandler.comment(paramArrayOfChar, paramInt1, paramInt2); }
  
  private void processProcessingInstruction(String paramString1, String paramString2) throws SAXException { this._contentHandler.processingInstruction(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\sax\SAXBufferProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */