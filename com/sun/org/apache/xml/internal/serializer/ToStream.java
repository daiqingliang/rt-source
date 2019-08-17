package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class ToStream extends SerializerBase {
  private static final String COMMENT_BEGIN = "<!--";
  
  private static final String COMMENT_END = "-->";
  
  protected BoolStack m_disableOutputEscapingStates = new BoolStack();
  
  EncodingInfo m_encodingInfo = new EncodingInfo(null, null);
  
  Method m_canConvertMeth;
  
  boolean m_triedToGetConverter = false;
  
  Object m_charToByteConverter = null;
  
  protected BoolStack m_preserves = new BoolStack();
  
  protected boolean m_ispreserve = false;
  
  protected boolean m_isprevtext = false;
  
  protected int m_maxCharacter = Encodings.getLastPrintable();
  
  protected char[] m_lineSep = SecuritySupport.getSystemProperty("line.separator").toCharArray();
  
  protected boolean m_lineSepUse = true;
  
  protected int m_lineSepLen = this.m_lineSep.length;
  
  protected CharInfo m_charInfo;
  
  boolean m_shouldFlush = true;
  
  protected boolean m_spaceBeforeClose = false;
  
  boolean m_startNewLine;
  
  protected boolean m_inDoctype = false;
  
  boolean m_isUTF8 = false;
  
  protected Properties m_format;
  
  protected boolean m_cdataStartCalled = false;
  
  private boolean m_expandDTDEntities = true;
  
  private boolean m_escaping = true;
  
  protected void closeCDATA() {
    try {
      this.m_writer.write("]]>");
      this.m_cdataTagOpen = false;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void serialize(Node paramNode) throws IOException {
    try {
      TreeWalker treeWalker = new TreeWalker(this);
      treeWalker.traverse(paramNode);
    } catch (SAXException sAXException) {
      throw new WrappedRuntimeException(sAXException);
    } 
  }
  
  static final boolean isUTF16Surrogate(char paramChar) { return ((paramChar & 0xFC00) == '?'); }
  
  protected final void flushWriter() {
    Writer writer = this.m_writer;
    if (null != writer)
      try {
        if (writer instanceof WriterToUTF8Buffered)
          if (this.m_shouldFlush) {
            ((WriterToUTF8Buffered)writer).flush();
          } else {
            ((WriterToUTF8Buffered)writer).flushBuffer();
          }  
        if (writer instanceof WriterToASCI) {
          if (this.m_shouldFlush)
            writer.flush(); 
        } else {
          writer.flush();
        } 
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      }  
  }
  
  public OutputStream getOutputStream() { return (this.m_writer instanceof WriterToUTF8Buffered) ? ((WriterToUTF8Buffered)this.m_writer).getOutputStream() : ((this.m_writer instanceof WriterToASCI) ? ((WriterToASCI)this.m_writer).getOutputStream() : null); }
  
  public void elementDecl(String paramString1, String paramString2) throws SAXException {
    if (this.m_inExternalDTD)
      return; 
    try {
      Writer writer = this.m_writer;
      DTDprolog();
      writer.write("<!ELEMENT ");
      writer.write(paramString1);
      writer.write(32);
      writer.write(paramString2);
      writer.write(62);
      writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void internalEntityDecl(String paramString1, String paramString2) throws SAXException {
    if (this.m_inExternalDTD)
      return; 
    try {
      DTDprolog();
      outputEntityDecl(paramString1, paramString2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  void outputEntityDecl(String paramString1, String paramString2) throws SAXException {
    Writer writer = this.m_writer;
    writer.write("<!ENTITY ");
    writer.write(paramString1);
    writer.write(" \"");
    writer.write(paramString2);
    writer.write("\">");
    writer.write(this.m_lineSep, 0, this.m_lineSepLen);
  }
  
  protected final void outputLineSep() { this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen); }
  
  public void setOutputFormat(Properties paramProperties) {
    boolean bool = this.m_shouldFlush;
    init(this.m_writer, paramProperties, false, false);
    this.m_shouldFlush = bool;
  }
  
  private void init(Writer paramWriter, Properties paramProperties, boolean paramBoolean1, boolean paramBoolean2) {
    this.m_shouldFlush = paramBoolean2;
    if (this.m_tracer != null && !(paramWriter instanceof SerializerTraceWriter)) {
      this.m_writer = new SerializerTraceWriter(paramWriter, this.m_tracer);
    } else {
      this.m_writer = paramWriter;
    } 
    this.m_format = paramProperties;
    setCdataSectionElements("cdata-section-elements", paramProperties);
    setIndentAmount(OutputPropertyUtils.getIntProperty("{http://xml.apache.org/xalan}indent-amount", paramProperties));
    setIndent(OutputPropertyUtils.getBooleanProperty("indent", paramProperties));
    String str1 = paramProperties.getProperty("{http://xml.apache.org/xalan}line-separator");
    if (str1 != null) {
      this.m_lineSep = str1.toCharArray();
      this.m_lineSepLen = str1.length();
    } 
    boolean bool = OutputPropertyUtils.getBooleanProperty("omit-xml-declaration", paramProperties);
    setOmitXMLDeclaration(bool);
    setDoctypeSystem(paramProperties.getProperty("doctype-system"));
    String str2 = paramProperties.getProperty("doctype-public");
    setDoctypePublic(str2);
    if (paramProperties.get("standalone") != null) {
      String str = paramProperties.getProperty("standalone");
      if (paramBoolean1) {
        setStandaloneInternal(str);
      } else {
        setStandalone(str);
      } 
    } 
    setMediaType(paramProperties.getProperty("media-type"));
    if (null != str2 && str2.startsWith("-//W3C//DTD XHTML"))
      this.m_spaceBeforeClose = true; 
    String str3 = getVersion();
    if (null == str3) {
      str3 = paramProperties.getProperty("version");
      setVersion(str3);
    } 
    String str4 = getEncoding();
    if (null == str4) {
      str4 = Encodings.getMimeEncoding(paramProperties.getProperty("encoding"));
      setEncoding(str4);
    } 
    this.m_isUTF8 = str4.equals("UTF-8");
    String str5 = (String)paramProperties.get("{http://xml.apache.org/xalan}entities");
    if (null != str5) {
      String str = (String)paramProperties.get("method");
      this.m_charInfo = CharInfo.getCharInfo(str5, str);
    } 
  }
  
  private void init(Writer paramWriter, Properties paramProperties) { init(paramWriter, paramProperties, false, false); }
  
  protected void init(OutputStream paramOutputStream, Properties paramProperties, boolean paramBoolean) throws UnsupportedEncodingException {
    String str = getEncoding();
    if (str == null) {
      str = Encodings.getMimeEncoding(paramProperties.getProperty("encoding"));
      setEncoding(str);
    } 
    if (str.equalsIgnoreCase("UTF-8")) {
      this.m_isUTF8 = true;
      init(new WriterToUTF8Buffered(paramOutputStream), paramProperties, paramBoolean, true);
    } else if (str.equals("WINDOWS-1250") || str.equals("US-ASCII") || str.equals("ASCII")) {
      init(new WriterToASCI(paramOutputStream), paramProperties, paramBoolean, true);
    } else {
      Writer writer;
      try {
        writer = Encodings.getWriter(paramOutputStream, str);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        System.out.println("Warning: encoding \"" + str + "\" not supported, using " + "UTF-8");
        str = "UTF-8";
        setEncoding(str);
        writer = Encodings.getWriter(paramOutputStream, str);
      } 
      init(writer, paramProperties, paramBoolean, true);
    } 
  }
  
  public Properties getOutputFormat() { return this.m_format; }
  
  public void setWriter(Writer paramWriter) {
    if (this.m_tracer != null && !(paramWriter instanceof SerializerTraceWriter)) {
      this.m_writer = new SerializerTraceWriter(paramWriter, this.m_tracer);
    } else {
      this.m_writer = paramWriter;
    } 
  }
  
  public boolean setLineSepUse(boolean paramBoolean) {
    boolean bool = this.m_lineSepUse;
    this.m_lineSepUse = paramBoolean;
    return bool;
  }
  
  public void setOutputStream(OutputStream paramOutputStream) {
    try {
      Properties properties;
      if (null == this.m_format) {
        properties = OutputPropertiesFactory.getDefaultMethodProperties("xml");
      } else {
        properties = this.m_format;
      } 
      init(paramOutputStream, properties, true);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
  }
  
  public boolean setEscaping(boolean paramBoolean) {
    boolean bool = this.m_escaping;
    this.m_escaping = paramBoolean;
    return bool;
  }
  
  protected void indent(int paramInt) throws IOException {
    if (this.m_startNewLine)
      outputLineSep(); 
    if (this.m_indentAmount > 0)
      printSpace(paramInt * this.m_indentAmount); 
  }
  
  protected void indent() { indent(this.m_elemContext.m_currentElemDepth); }
  
  private void printSpace(int paramInt) throws IOException {
    Writer writer = this.m_writer;
    for (byte b = 0; b < paramInt; b++)
      writer.write(32); 
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {
    if (this.m_inExternalDTD)
      return; 
    try {
      Writer writer = this.m_writer;
      DTDprolog();
      writer.write("<!ATTLIST ");
      writer.write(paramString1);
      writer.write(32);
      writer.write(paramString2);
      writer.write(32);
      writer.write(paramString3);
      if (paramString4 != null) {
        writer.write(32);
        writer.write(paramString4);
      } 
      writer.write(62);
      writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public Writer getWriter() { return this.m_writer; }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      DTDprolog();
      this.m_writer.write("<!ENTITY ");
      this.m_writer.write(paramString1);
      if (paramString2 != null) {
        this.m_writer.write(" PUBLIC \"");
        this.m_writer.write(paramString2);
      } else {
        this.m_writer.write(" SYSTEM \"");
        this.m_writer.write(paramString3);
      } 
      this.m_writer.write("\" >");
      this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  protected boolean escapingNotNeeded(char paramChar) {
    boolean bool;
    if (paramChar < '') {
      if (paramChar >= ' ' || '\n' == paramChar || '\r' == paramChar || '\t' == paramChar) {
        bool = true;
      } else {
        bool = false;
      } 
    } else {
      bool = this.m_encodingInfo.isInEncoding(paramChar);
    } 
    return bool;
  }
  
  protected int writeUTF16Surrogate(char paramChar, char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    int i = 0;
    if (paramInt1 + 1 >= paramInt2)
      throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) })); 
    char c1 = paramChar;
    char c2 = paramArrayOfChar[paramInt1 + 1];
    if (!Encodings.isLowUTF16Surrogate(c2))
      throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) + " " + Integer.toHexString(c2) })); 
    Writer writer = this.m_writer;
    if (this.m_encodingInfo.isInEncoding(paramChar, c2)) {
      writer.write(paramArrayOfChar, paramInt1, 2);
    } else {
      String str = getEncoding();
      if (str != null) {
        i = Encodings.toCodePoint(c1, c2);
        writer.write(38);
        writer.write(35);
        writer.write(Integer.toString(i));
        writer.write(59);
      } else {
        writer.write(paramArrayOfChar, paramInt1, 2);
      } 
    } 
    return i;
  }
  
  protected int accumDefaultEntity(Writer paramWriter, char paramChar, int paramInt1, char[] paramArrayOfChar, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    if (!paramBoolean2 && '\n' == paramChar) {
      paramWriter.write(this.m_lineSep, 0, this.m_lineSepLen);
    } else if ((paramBoolean1 && this.m_charInfo.isSpecialTextChar(paramChar)) || (!paramBoolean1 && this.m_charInfo.isSpecialAttrChar(paramChar))) {
      String str = this.m_charInfo.getOutputStringForChar(paramChar);
      if (null != str) {
        paramWriter.write(str);
      } else {
        return paramInt1;
      } 
    } else {
      return paramInt1;
    } 
    return paramInt1 + 1;
  }
  
  void writeNormalizedChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws IOException, SAXException {
    Writer writer = this.m_writer;
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      char c = paramArrayOfChar[j];
      if ('\n' == c && paramBoolean2) {
        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
      } else if (paramBoolean1 && !escapingNotNeeded(c)) {
        if (this.m_cdataTagOpen)
          closeCDATA(); 
        if (Encodings.isHighUTF16Surrogate(c)) {
          writeUTF16Surrogate(c, paramArrayOfChar, j, i);
          j++;
        } else {
          writer.write("&#");
          String str = Integer.toString(c);
          writer.write(str);
          writer.write(59);
        } 
      } else if (paramBoolean1 && j < i - 2 && ']' == c && ']' == paramArrayOfChar[j + 1] && '>' == paramArrayOfChar[j + 2]) {
        writer.write("]]]]><![CDATA[>");
        j += 2;
      } else if (escapingNotNeeded(c)) {
        if (paramBoolean1 && !this.m_cdataTagOpen) {
          writer.write("<![CDATA[");
          this.m_cdataTagOpen = true;
        } 
        writer.write(c);
      } else if (Encodings.isHighUTF16Surrogate(c)) {
        if (this.m_cdataTagOpen)
          closeCDATA(); 
        writeUTF16Surrogate(c, paramArrayOfChar, j, i);
        j++;
      } else {
        if (this.m_cdataTagOpen)
          closeCDATA(); 
        writer.write("&#");
        String str = Integer.toString(c);
        writer.write(str);
        writer.write(59);
      } 
    } 
  }
  
  public void endNonEscaping() { this.m_disableOutputEscapingStates.pop(); }
  
  public void startNonEscaping() { this.m_disableOutputEscapingStates.push(true); }
  
  protected void cdata(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      int i = paramInt1;
      if (this.m_elemContext.m_startTagOpen) {
        closeStartTag();
        this.m_elemContext.m_startTagOpen = false;
      } 
      this.m_ispreserve = true;
      if (shouldIndent())
        indent(); 
      boolean bool = (paramInt2 >= 1 && escapingNotNeeded(paramArrayOfChar[paramInt1])) ? 1 : 0;
      if (bool && !this.m_cdataTagOpen) {
        this.m_writer.write("<![CDATA[");
        this.m_cdataTagOpen = true;
      } 
      if (isEscapingDisabled()) {
        charactersRaw(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, true, this.m_lineSepUse);
      } 
      if (bool && paramArrayOfChar[paramInt1 + paramInt2 - 1] == ']')
        closeCDATA(); 
      if (this.m_tracer != null)
        fireCDATAEvent(paramArrayOfChar, i, paramInt2); 
    } catch (IOException iOException) {
      throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), iOException);
    } 
  }
  
  private boolean isEscapingDisabled() { return this.m_disableOutputEscapingStates.peekOrFalse(); }
  
  protected void charactersRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_inEntityRef)
      return; 
    try {
      if (this.m_elemContext.m_startTagOpen) {
        closeStartTag();
        this.m_elemContext.m_startTagOpen = false;
      } 
      this.m_ispreserve = true;
      this.m_writer.write(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 == 0 || (this.m_inEntityRef && !this.m_expandDTDEntities))
      return; 
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } else if (this.m_needToCallStartDocument) {
      startDocumentInternal();
    } 
    if (this.m_cdataStartCalled || this.m_elemContext.m_isCdataSection) {
      cdata(paramArrayOfChar, paramInt1, paramInt2);
      return;
    } 
    if (this.m_cdataTagOpen)
      closeCDATA(); 
    if (this.m_disableOutputEscapingStates.peekOrFalse() || !this.m_escaping) {
      charactersRaw(paramArrayOfChar, paramInt1, paramInt2);
      if (this.m_tracer != null)
        fireCharEvent(paramArrayOfChar, paramInt1, paramInt2); 
      return;
    } 
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } 
    try {
      int k = paramInt1 + paramInt2;
      int m = paramInt1 - 1;
      int i;
      char c;
      for (i = paramInt1; i < k && ((c = paramArrayOfChar[i]) == ' ' || (c == '\n' && this.m_lineSepUse) || c == '\r' || c == '\t'); i++) {
        if (!this.m_charInfo.isTextASCIIClean(c)) {
          m = processDirty(paramArrayOfChar, k, i, c, m, true);
          i = m;
        } 
      } 
      if (i < k)
        this.m_ispreserve = true; 
      boolean bool = "1.0".equals(getVersion());
      while (i < k) {
        char c1;
        while (i < k && (c1 = paramArrayOfChar[i]) < '' && this.m_charInfo.isTextASCIIClean(c1))
          i++; 
        if (i == k)
          break; 
        c1 = paramArrayOfChar[i];
        if ((isCharacterInC0orC1Range(c1) || (!bool && isNELorLSEPCharacter(c1)) || !escapingNotNeeded(c1) || this.m_charInfo.isSpecialTextChar(c1)) && '"' != c1) {
          m = processDirty(paramArrayOfChar, k, i, c1, m, true);
          i = m;
        } 
        i++;
      } 
      int j = m + 1;
      if (i > j) {
        int n = i - j;
        this.m_writer.write(paramArrayOfChar, j, n);
      } 
      this.m_isprevtext = true;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
    if (this.m_tracer != null)
      fireCharEvent(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  private static boolean isCharacterInC0orC1Range(char paramChar) { return (paramChar == '\t' || paramChar == '\n' || paramChar == '\r') ? false : (((paramChar >= '' && paramChar <= '') || (paramChar >= '\001' && paramChar <= '\037'))); }
  
  private static boolean isNELorLSEPCharacter(char paramChar) { return (paramChar == '' || paramChar == ' '); }
  
  private int processDirty(char[] paramArrayOfChar, int paramInt1, int paramInt2, char paramChar, int paramInt3, boolean paramBoolean) throws IOException {
    int i = paramInt3 + 1;
    if (paramInt2 > i) {
      int j = paramInt2 - i;
      this.m_writer.write(paramArrayOfChar, i, j);
    } 
    if ('\n' == paramChar && paramBoolean) {
      this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } else {
      i = accumDefaultEscape(this.m_writer, paramChar, paramInt2, paramArrayOfChar, paramInt1, paramBoolean, false);
      paramInt2 = i - 1;
    } 
    return paramInt2;
  }
  
  public void characters(String paramString) throws SAXException {
    if (this.m_inEntityRef && !this.m_expandDTDEntities)
      return; 
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    characters(this.m_charsBuff, 0, i);
  }
  
  protected int accumDefaultEscape(Writer paramWriter, char paramChar, int paramInt1, char[] paramArrayOfChar, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    int i = accumDefaultEntity(paramWriter, paramChar, paramInt1, paramArrayOfChar, paramInt2, paramBoolean1, paramBoolean2);
    if (paramInt1 == i)
      if (Encodings.isHighUTF16Surrogate(paramChar)) {
        int j = 0;
        if (paramInt1 + 1 >= paramInt2)
          throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) })); 
        char c = paramArrayOfChar[++paramInt1];
        if (!Encodings.isLowUTF16Surrogate(c))
          throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) + " " + Integer.toHexString(c) })); 
        j = Encodings.toCodePoint(paramChar, c);
        paramWriter.write("&#");
        paramWriter.write(Integer.toString(j));
        paramWriter.write(59);
        i += 2;
      } else {
        if (isCharacterInC0orC1Range(paramChar) || ("1.1".equals(getVersion()) && isNELorLSEPCharacter(paramChar))) {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(paramChar));
          paramWriter.write(59);
        } else if ((!escapingNotNeeded(paramChar) || (paramBoolean1 && this.m_charInfo.isSpecialTextChar(paramChar)) || (!paramBoolean1 && this.m_charInfo.isSpecialAttrChar(paramChar))) && this.m_elemContext.m_currentElemDepth > 0) {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(paramChar));
          paramWriter.write(59);
        } else {
          paramWriter.write(paramChar);
        } 
        i++;
      }  
    return i;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.m_inEntityRef)
      return; 
    if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } else if (this.m_cdataTagOpen) {
      closeCDATA();
    } 
    try {
      if (true == this.m_needToOutputDocTypeDecl && null != getDoctypeSystem())
        outputDocTypeDecl(paramString3, true); 
      this.m_needToOutputDocTypeDecl = false;
      if (this.m_elemContext.m_startTagOpen) {
        closeStartTag();
        this.m_elemContext.m_startTagOpen = false;
      } 
      if (paramString1 != null)
        ensurePrefixIsDeclared(paramString1, paramString3); 
      this.m_ispreserve = false;
      if (shouldIndent() && this.m_startNewLine)
        indent(); 
      this.m_startNewLine = true;
      Writer writer = this.m_writer;
      writer.write(60);
      writer.write(paramString3);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
    if (paramAttributes != null)
      addAttributes(paramAttributes); 
    this.m_elemContext = this.m_elemContext.push(paramString1, paramString2, paramString3);
    this.m_isprevtext = false;
    if (this.m_tracer != null)
      firePseudoAttributes(); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException { startElement(paramString1, paramString2, paramString3, null); }
  
  public void startElement(String paramString) throws SAXException { startElement(null, null, paramString, null); }
  
  void outputDocTypeDecl(String paramString, boolean paramBoolean) throws SAXException {
    if (this.m_cdataTagOpen)
      closeCDATA(); 
    try {
      Writer writer = this.m_writer;
      writer.write("<!DOCTYPE ");
      writer.write(paramString);
      String str1 = getDoctypePublic();
      if (null != str1) {
        writer.write(" PUBLIC \"");
        writer.write(str1);
        writer.write(34);
      } 
      String str2 = getDoctypeSystem();
      if (null != str2) {
        if (null == str1) {
          writer.write(" SYSTEM \"");
        } else {
          writer.write(" \"");
        } 
        writer.write(str2);
        if (paramBoolean) {
          writer.write("\">");
          writer.write(this.m_lineSep, 0, this.m_lineSepLen);
          paramBoolean = false;
        } else {
          writer.write(34);
        } 
      } 
      boolean bool = false;
      if (bool && paramBoolean) {
        writer.write(62);
        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void processAttributes(Writer paramWriter, int paramInt) throws IOException, SAXException {
    String str = getEncoding();
    for (byte b = 0; b < paramInt; b++) {
      String str1 = this.m_attributes.getQName(b);
      String str2 = this.m_attributes.getValue(b);
      paramWriter.write(32);
      paramWriter.write(str1);
      paramWriter.write("=\"");
      writeAttrString(paramWriter, str2, str);
      paramWriter.write(34);
    } 
  }
  
  public void writeAttrString(Writer paramWriter, String paramString1, String paramString2) throws IOException {
    int i = paramString1.length();
    if (i > this.m_attrBuff.length)
      this.m_attrBuff = new char[i * 2 + 1]; 
    paramString1.getChars(0, i, this.m_attrBuff, 0);
    char[] arrayOfChar = this.m_attrBuff;
    int j;
    for (j = 0; j < i; j = accumDefaultEscape(paramWriter, c, j, arrayOfChar, i, false, true)) {
      char c = arrayOfChar[j];
      if (escapingNotNeeded(c) && !this.m_charInfo.isSpecialAttrChar(c)) {
        paramWriter.write(c);
        j++;
        continue;
      } 
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_inEntityRef)
      return; 
    this.m_prefixMap.popNamespaces(this.m_elemContext.m_currentElemDepth, null);
    try {
      Writer writer = this.m_writer;
      if (this.m_elemContext.m_startTagOpen) {
        if (this.m_tracer != null)
          fireStartElem(this.m_elemContext.m_elementName); 
        int i = this.m_attributes.getLength();
        if (i > 0) {
          processAttributes(this.m_writer, i);
          this.m_attributes.clear();
        } 
        if (this.m_spaceBeforeClose) {
          writer.write(" />");
        } else {
          writer.write("/>");
        } 
      } else {
        if (this.m_cdataTagOpen)
          closeCDATA(); 
        if (shouldIndent())
          indent(this.m_elemContext.m_currentElemDepth - 1); 
        writer.write(60);
        writer.write(47);
        writer.write(paramString3);
        writer.write(62);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
    if (!this.m_elemContext.m_startTagOpen && this.m_doIndent)
      this.m_ispreserve = this.m_preserves.isEmpty() ? false : this.m_preserves.pop(); 
    this.m_isprevtext = false;
    if (this.m_tracer != null)
      fireEndElem(paramString3); 
    this.m_elemContext = this.m_elemContext.m_prev;
  }
  
  public void endElement(String paramString) throws SAXException { endElement(null, null, paramString); }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { startPrefixMapping(paramString1, paramString2, true); }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean) throws SAXException {
    int i;
    if (paramBoolean) {
      flushPending();
      i = this.m_elemContext.m_currentElemDepth + 1;
    } else {
      i = this.m_elemContext.m_currentElemDepth;
    } 
    boolean bool = this.m_prefixMap.pushNamespace(paramString1, paramString2, i);
    if (bool)
      if ("".equals(paramString1)) {
        String str = "xmlns";
        addAttributeAlways("http://www.w3.org/2000/xmlns/", str, str, "CDATA", paramString2, false);
      } else if (!"".equals(paramString2)) {
        String str = "xmlns:" + paramString1;
        addAttributeAlways("http://www.w3.org/2000/xmlns/", paramString1, str, "CDATA", paramString2, false);
      }  
    return bool;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    int i = paramInt1;
    if (this.m_inEntityRef)
      return; 
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } else if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } 
    try {
      if (shouldIndent() && this.m_isStandalone)
        indent(); 
      int j = paramInt1 + paramInt2;
      boolean bool = false;
      if (this.m_cdataTagOpen)
        closeCDATA(); 
      if (shouldIndent() && !this.m_isStandalone)
        indent(); 
      Writer writer = this.m_writer;
      writer.write("<!--");
      int k;
      for (k = paramInt1; k < j; k++) {
        if (bool && paramArrayOfChar[k] == '-') {
          writer.write(paramArrayOfChar, paramInt1, k - paramInt1);
          writer.write(" -");
          paramInt1 = k + 1;
        } 
        bool = (paramArrayOfChar[k] == '-') ? 1 : 0;
      } 
      if (paramInt2 > 0) {
        k = j - paramInt1;
        if (k > 0)
          writer.write(paramArrayOfChar, paramInt1, k); 
        if (paramArrayOfChar[j - 1] == '-')
          writer.write(32); 
      } 
      writer.write("-->");
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
    this.m_startNewLine = true;
    if (this.m_tracer != null)
      fireCommentEvent(paramArrayOfChar, i, paramInt2); 
  }
  
  public void endCDATA() {
    if (this.m_cdataTagOpen)
      closeCDATA(); 
    this.m_cdataStartCalled = false;
  }
  
  public void endDTD() {
    try {
      if (this.m_needToCallStartDocument)
        return; 
      if (this.m_needToOutputDocTypeDecl) {
        outputDocTypeDecl(this.m_elemContext.m_elementName, false);
        this.m_needToOutputDocTypeDecl = false;
      } 
      Writer writer = this.m_writer;
      if (!this.m_inDoctype) {
        writer.write("]>");
      } else {
        writer.write(62);
      } 
      writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (0 == paramInt2)
      return; 
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  public void startCDATA() { this.m_cdataStartCalled = true; }
  
  public void startEntity(String paramString) throws SAXException {
    if (paramString.equals("[dtd]"))
      this.m_inExternalDTD = true; 
    if (!this.m_expandDTDEntities && !this.m_inExternalDTD) {
      startNonEscaping();
      characters("&" + paramString + ';');
      endNonEscaping();
    } 
    this.m_inEntityRef = true;
  }
  
  protected void closeStartTag() {
    if (this.m_elemContext.m_startTagOpen) {
      try {
        if (this.m_tracer != null)
          fireStartElem(this.m_elemContext.m_elementName); 
        int i = this.m_attributes.getLength();
        if (i > 0) {
          processAttributes(this.m_writer, i);
          this.m_attributes.clear();
        } 
        this.m_writer.write(62);
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      } 
      if (this.m_cdataSectionElements != null)
        this.m_elemContext.m_isCdataSection = isCdataSection(); 
      if (this.m_doIndent) {
        this.m_isprevtext = false;
        this.m_preserves.push(this.m_ispreserve);
      } 
    } 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    setDoctypeSystem(paramString3);
    setDoctypePublic(paramString2);
    this.m_elemContext.m_elementName = paramString1;
    this.m_inDoctype = true;
  }
  
  public int getIndentAmount() { return this.m_indentAmount; }
  
  public void setIndentAmount(int paramInt) throws IOException { this.m_indentAmount = paramInt; }
  
  protected boolean shouldIndent() { return (this.m_doIndent && !this.m_ispreserve && !this.m_isprevtext && (this.m_elemContext.m_currentElemDepth > 0 || this.m_isStandalone)); }
  
  private void setCdataSectionElements(String paramString, Properties paramProperties) {
    String str = paramProperties.getProperty(paramString);
    if (null != str) {
      Vector vector = new Vector();
      int i = str.length();
      boolean bool = false;
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < i; b++) {
        char c = str.charAt(b);
        if (Character.isWhitespace(c)) {
          if (!bool) {
            if (stringBuffer.length() > 0) {
              addCdataSectionElement(stringBuffer.toString(), vector);
              stringBuffer.setLength(0);
            } 
            continue;
          } 
        } else if ('{' == c) {
          bool = true;
        } else if ('}' == c) {
          bool = false;
        } 
        stringBuffer.append(c);
        continue;
      } 
      if (stringBuffer.length() > 0) {
        addCdataSectionElement(stringBuffer.toString(), vector);
        stringBuffer.setLength(0);
      } 
      setCdataSectionElements(vector);
    } 
  }
  
  private void addCdataSectionElement(String paramString, Vector paramVector) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "{}", false);
    String str1 = stringTokenizer.nextToken();
    String str2 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null;
    if (null == str2) {
      paramVector.addElement(null);
      paramVector.addElement(str1);
    } else {
      paramVector.addElement(str1);
      paramVector.addElement(str2);
    } 
  }
  
  public void setCdataSectionElements(Vector paramVector) { this.m_cdataSectionElements = paramVector; }
  
  protected String ensureAttributesNamespaceIsDeclared(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (paramString1 != null && paramString1.length() > 0) {
      int i = 0;
      String str1 = ((i = paramString3.indexOf(":")) < 0) ? "" : paramString3.substring(0, i);
      if (i > 0) {
        String str = this.m_prefixMap.lookupNamespace(str1);
        if (str != null && str.equals(paramString1))
          return null; 
        startPrefixMapping(str1, paramString1, false);
        addAttribute("http://www.w3.org/2000/xmlns/", str1, "xmlns:" + str1, "CDATA", paramString1, false);
        return str1;
      } 
      String str2 = this.m_prefixMap.lookupPrefix(paramString1);
      if (str2 == null) {
        str2 = this.m_prefixMap.generateNextPrefix();
        startPrefixMapping(str2, paramString1, false);
        addAttribute("http://www.w3.org/2000/xmlns/", str2, "xmlns:" + str2, "CDATA", paramString1, false);
      } 
      return str2;
    } 
    return null;
  }
  
  void ensurePrefixIsDeclared(String paramString1, String paramString2) throws SAXException {
    if (paramString1 != null && paramString1.length() > 0) {
      int i;
      boolean bool = ((i = paramString2.indexOf(":")) < 0) ? 1 : 0;
      String str = bool ? "" : paramString2.substring(0, i);
      if (null != str) {
        String str1 = this.m_prefixMap.lookupNamespace(str);
        if (null == str1 || !str1.equals(paramString1)) {
          startPrefixMapping(str, paramString1);
          addAttributeAlways("http://www.w3.org/2000/xmlns/", bool ? "xmlns" : str, bool ? "xmlns" : ("xmlns:" + str), "CDATA", paramString1, false);
        } 
      } 
    } 
  }
  
  public void flushPending() {
    if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } 
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } 
    if (this.m_cdataTagOpen) {
      closeCDATA();
      this.m_cdataTagOpen = false;
    } 
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) {}
  
  public boolean addAttributeAlways(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) {
    boolean bool;
    int i = this.m_attributes.getIndex(paramString3);
    if (i >= 0) {
      String str = null;
      if (this.m_tracer != null) {
        str = this.m_attributes.getValue(i);
        if (paramString5.equals(str))
          str = null; 
      } 
      this.m_attributes.setValue(i, paramString5);
      bool = false;
      if (str != null)
        firePseudoAttributes(); 
    } else {
      if (paramBoolean) {
        int j = paramString3.indexOf(':');
        if (j > 0) {
          String str = paramString3.substring(0, j);
          NamespaceMappings.MappingRecord mappingRecord = this.m_prefixMap.getMappingFromPrefix(str);
          if (mappingRecord != null && mappingRecord.m_declarationDepth == this.m_elemContext.m_currentElemDepth && !mappingRecord.m_uri.equals(paramString1)) {
            str = this.m_prefixMap.lookupPrefix(paramString1);
            if (str == null)
              str = this.m_prefixMap.generateNextPrefix(); 
            paramString3 = str + ':' + paramString2;
          } 
        } 
        try {
          String str = ensureAttributesNamespaceIsDeclared(paramString1, paramString2, paramString3);
        } catch (SAXException sAXException) {
          sAXException.printStackTrace();
        } 
      } 
      this.m_attributes.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
      bool = true;
      if (this.m_tracer != null)
        firePseudoAttributes(); 
    } 
    return bool;
  }
  
  protected void firePseudoAttributes() {
    if (this.m_tracer != null)
      try {
        this.m_writer.flush();
        StringBuffer stringBuffer = new StringBuffer();
        int i = this.m_attributes.getLength();
        if (i > 0) {
          WritertoStringBuffer writertoStringBuffer = new WritertoStringBuffer(stringBuffer);
          processAttributes(writertoStringBuffer, i);
        } 
        stringBuffer.append('>');
        char[] arrayOfChar = stringBuffer.toString().toCharArray();
        this.m_tracer.fireGenerateEvent(11, arrayOfChar, 0, arrayOfChar.length);
      } catch (IOException iOException) {
      
      } catch (SAXException sAXException) {} 
  }
  
  public void setTransformer(Transformer paramTransformer) {
    super.setTransformer(paramTransformer);
    if (this.m_tracer != null && !(this.m_writer instanceof SerializerTraceWriter))
      this.m_writer = new SerializerTraceWriter(this.m_writer, this.m_tracer); 
  }
  
  public boolean reset() {
    boolean bool = false;
    if (super.reset()) {
      resetToStream();
      bool = true;
    } 
    return bool;
  }
  
  private void resetToStream() {
    this.m_cdataStartCalled = false;
    this.m_disableOutputEscapingStates.clear();
    this.m_escaping = true;
    this.m_inDoctype = false;
    this.m_ispreserve = false;
    this.m_ispreserve = false;
    this.m_isprevtext = false;
    this.m_isUTF8 = false;
    this.m_preserves.clear();
    this.m_shouldFlush = true;
    this.m_spaceBeforeClose = false;
    this.m_startNewLine = false;
    this.m_lineSepUse = true;
    this.m_expandDTDEntities = true;
  }
  
  public void setEncoding(String paramString) throws SAXException {
    String str = getEncoding();
    super.setEncoding(paramString);
    if (str == null || !str.equals(paramString)) {
      this.m_encodingInfo = Encodings.getEncodingInfo(paramString);
      if (paramString != null && this.m_encodingInfo.name == null) {
        String str1 = Utils.messages.createMessage("ER_ENCODING_NOT_SUPPORTED", new Object[] { paramString });
        try {
          Transformer transformer = getTransformer();
          if (transformer != null) {
            ErrorListener errorListener = transformer.getErrorListener();
            if (null != errorListener && this.m_sourceLocator != null) {
              errorListener.warning(new TransformerException(str1, this.m_sourceLocator));
            } else {
              System.out.println(str1);
            } 
          } else {
            System.out.println(str1);
          } 
        } catch (Exception exception) {}
      } 
    } 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      DTDprolog();
      this.m_writer.write("<!NOTATION ");
      this.m_writer.write(paramString1);
      if (paramString2 != null) {
        this.m_writer.write(" PUBLIC \"");
        this.m_writer.write(paramString2);
      } else {
        this.m_writer.write(" SYSTEM \"");
        this.m_writer.write(paramString3);
      } 
      this.m_writer.write("\" >");
      this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    try {
      DTDprolog();
      this.m_writer.write("<!ENTITY ");
      this.m_writer.write(paramString1);
      if (paramString2 != null) {
        this.m_writer.write(" PUBLIC \"");
        this.m_writer.write(paramString2);
      } else {
        this.m_writer.write(" SYSTEM \"");
        this.m_writer.write(paramString3);
      } 
      this.m_writer.write("\" NDATA ");
      this.m_writer.write(paramString4);
      this.m_writer.write(" >");
      this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  private void DTDprolog() {
    Writer writer = this.m_writer;
    if (this.m_needToOutputDocTypeDecl) {
      outputDocTypeDecl(this.m_elemContext.m_elementName, false);
      this.m_needToOutputDocTypeDecl = false;
    } 
    if (this.m_inDoctype) {
      writer.write(" [");
      writer.write(this.m_lineSep, 0, this.m_lineSepLen);
      this.m_inDoctype = false;
    } 
  }
  
  public void setDTDEntityExpansion(boolean paramBoolean) { this.m_expandDTDEntities = paramBoolean; }
  
  static final class BoolStack {
    private boolean[] m_values;
    
    private int m_allocatedSize;
    
    private int m_index;
    
    public BoolStack() { this(32); }
    
    public BoolStack(int param1Int) throws IOException {
      this.m_allocatedSize = param1Int;
      this.m_values = new boolean[param1Int];
      this.m_index = -1;
    }
    
    public final int size() { return this.m_index + 1; }
    
    public final void clear() { this.m_index = -1; }
    
    public final boolean push(boolean param1Boolean) {
      if (this.m_index == this.m_allocatedSize - 1)
        grow(); 
      this.m_values[++this.m_index] = param1Boolean;
      return param1Boolean;
    }
    
    public final boolean pop() { return this.m_values[this.m_index--]; }
    
    public final boolean popAndTop() {
      this.m_index--;
      return (this.m_index >= 0) ? this.m_values[this.m_index] : false;
    }
    
    public final void setTop(boolean param1Boolean) { this.m_values[this.m_index] = param1Boolean; }
    
    public final boolean peek() { return this.m_values[this.m_index]; }
    
    public final boolean peekOrFalse() { return (this.m_index > -1) ? this.m_values[this.m_index] : false; }
    
    public final boolean peekOrTrue() { return (this.m_index > -1) ? this.m_values[this.m_index] : true; }
    
    public boolean isEmpty() { return (this.m_index == -1); }
    
    private void grow() {
      this.m_allocatedSize *= 2;
      boolean[] arrayOfBoolean = new boolean[this.m_allocatedSize];
      System.arraycopy(this.m_values, 0, arrayOfBoolean, 0, this.m_index + 1);
      this.m_values = arrayOfBoolean;
    }
  }
  
  private class WritertoStringBuffer extends Writer {
    private final StringBuffer m_stringbuf;
    
    WritertoStringBuffer(StringBuffer param1StringBuffer) { this.m_stringbuf = param1StringBuffer; }
    
    public void write(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws SAXException { this.m_stringbuf.append(param1ArrayOfChar, param1Int1, param1Int2); }
    
    public void flush() {}
    
    public void close() {}
    
    public void write(int param1Int) throws IOException { this.m_stringbuf.append((char)param1Int); }
    
    public void write(String param1String) throws SAXException { this.m_stringbuf.append(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */