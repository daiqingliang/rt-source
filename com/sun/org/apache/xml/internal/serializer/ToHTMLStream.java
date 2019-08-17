package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class ToHTMLStream extends ToStream {
  protected boolean m_inDTD = false;
  
  private boolean m_inBlockElem = false;
  
  private static final CharInfo m_htmlcharInfo = CharInfo.getCharInfoInternal("com.sun.org.apache.xml.internal.serializer.HTMLEntities", "html");
  
  static final Trie m_elementFlags = new Trie();
  
  private static final ElemDesc m_dummy;
  
  private boolean m_specialEscapeURLs = true;
  
  private boolean m_omitMetaTag = false;
  
  private Trie m_htmlInfo = new Trie(m_elementFlags);
  
  static void initTagReference(Trie paramTrie) {
    paramTrie.put("BASEFONT", new ElemDesc(2));
    paramTrie.put("FRAME", new ElemDesc(10));
    paramTrie.put("FRAMESET", new ElemDesc(8));
    paramTrie.put("NOFRAMES", new ElemDesc(8));
    paramTrie.put("ISINDEX", new ElemDesc(10));
    paramTrie.put("APPLET", new ElemDesc(2097152));
    paramTrie.put("CENTER", new ElemDesc(8));
    paramTrie.put("DIR", new ElemDesc(8));
    paramTrie.put("MENU", new ElemDesc(8));
    paramTrie.put("TT", new ElemDesc(4096));
    paramTrie.put("I", new ElemDesc(4096));
    paramTrie.put("B", new ElemDesc(4096));
    paramTrie.put("BIG", new ElemDesc(4096));
    paramTrie.put("SMALL", new ElemDesc(4096));
    paramTrie.put("EM", new ElemDesc(8192));
    paramTrie.put("STRONG", new ElemDesc(8192));
    paramTrie.put("DFN", new ElemDesc(8192));
    paramTrie.put("CODE", new ElemDesc(8192));
    paramTrie.put("SAMP", new ElemDesc(8192));
    paramTrie.put("KBD", new ElemDesc(8192));
    paramTrie.put("VAR", new ElemDesc(8192));
    paramTrie.put("CITE", new ElemDesc(8192));
    paramTrie.put("ABBR", new ElemDesc(8192));
    paramTrie.put("ACRONYM", new ElemDesc(8192));
    paramTrie.put("SUP", new ElemDesc(98304));
    paramTrie.put("SUB", new ElemDesc(98304));
    paramTrie.put("SPAN", new ElemDesc(98304));
    paramTrie.put("BDO", new ElemDesc(98304));
    paramTrie.put("BR", new ElemDesc(98314));
    paramTrie.put("BODY", new ElemDesc(8));
    paramTrie.put("ADDRESS", new ElemDesc(56));
    paramTrie.put("DIV", new ElemDesc(56));
    paramTrie.put("A", new ElemDesc(32768));
    paramTrie.put("MAP", new ElemDesc(98312));
    paramTrie.put("AREA", new ElemDesc(10));
    paramTrie.put("LINK", new ElemDesc(131082));
    paramTrie.put("IMG", new ElemDesc(2195458));
    paramTrie.put("OBJECT", new ElemDesc(2326528));
    paramTrie.put("PARAM", new ElemDesc(2));
    paramTrie.put("HR", new ElemDesc(58));
    paramTrie.put("P", new ElemDesc(56));
    paramTrie.put("H1", new ElemDesc(262152));
    paramTrie.put("H2", new ElemDesc(262152));
    paramTrie.put("H3", new ElemDesc(262152));
    paramTrie.put("H4", new ElemDesc(262152));
    paramTrie.put("H5", new ElemDesc(262152));
    paramTrie.put("H6", new ElemDesc(262152));
    paramTrie.put("PRE", new ElemDesc(1048584));
    paramTrie.put("Q", new ElemDesc(98304));
    paramTrie.put("BLOCKQUOTE", new ElemDesc(56));
    paramTrie.put("INS", new ElemDesc(0));
    paramTrie.put("DEL", new ElemDesc(0));
    paramTrie.put("DL", new ElemDesc(56));
    paramTrie.put("DT", new ElemDesc(8));
    paramTrie.put("DD", new ElemDesc(8));
    paramTrie.put("OL", new ElemDesc(524296));
    paramTrie.put("UL", new ElemDesc(524296));
    paramTrie.put("LI", new ElemDesc(8));
    paramTrie.put("FORM", new ElemDesc(8));
    paramTrie.put("LABEL", new ElemDesc(16384));
    paramTrie.put("INPUT", new ElemDesc(18434));
    paramTrie.put("SELECT", new ElemDesc(18432));
    paramTrie.put("OPTGROUP", new ElemDesc(0));
    paramTrie.put("OPTION", new ElemDesc(0));
    paramTrie.put("TEXTAREA", new ElemDesc(18432));
    paramTrie.put("FIELDSET", new ElemDesc(24));
    paramTrie.put("LEGEND", new ElemDesc(0));
    paramTrie.put("BUTTON", new ElemDesc(18432));
    paramTrie.put("TABLE", new ElemDesc(56));
    paramTrie.put("CAPTION", new ElemDesc(8));
    paramTrie.put("THEAD", new ElemDesc(8));
    paramTrie.put("TFOOT", new ElemDesc(8));
    paramTrie.put("TBODY", new ElemDesc(8));
    paramTrie.put("COLGROUP", new ElemDesc(8));
    paramTrie.put("COL", new ElemDesc(10));
    paramTrie.put("TR", new ElemDesc(8));
    paramTrie.put("TH", new ElemDesc(0));
    paramTrie.put("TD", new ElemDesc(0));
    paramTrie.put("HEAD", new ElemDesc(4194312));
    paramTrie.put("TITLE", new ElemDesc(8));
    paramTrie.put("BASE", new ElemDesc(10));
    paramTrie.put("META", new ElemDesc(131082));
    paramTrie.put("STYLE", new ElemDesc(131336));
    paramTrie.put("SCRIPT", new ElemDesc(229632));
    paramTrie.put("NOSCRIPT", new ElemDesc(56));
    paramTrie.put("HTML", new ElemDesc(8));
    paramTrie.put("FONT", new ElemDesc(4096));
    paramTrie.put("S", new ElemDesc(4096));
    paramTrie.put("STRIKE", new ElemDesc(4096));
    paramTrie.put("U", new ElemDesc(4096));
    paramTrie.put("NOBR", new ElemDesc(4096));
    paramTrie.put("IFRAME", new ElemDesc(56));
    paramTrie.put("LAYER", new ElemDesc(56));
    paramTrie.put("ILAYER", new ElemDesc(56));
    ElemDesc elemDesc = (ElemDesc)paramTrie.get("A");
    elemDesc.setAttr("HREF", 2);
    elemDesc.setAttr("NAME", 2);
    elemDesc = (ElemDesc)paramTrie.get("AREA");
    elemDesc.setAttr("HREF", 2);
    elemDesc.setAttr("NOHREF", 4);
    elemDesc = (ElemDesc)paramTrie.get("BASE");
    elemDesc.setAttr("HREF", 2);
    elemDesc = (ElemDesc)paramTrie.get("BUTTON");
    elemDesc.setAttr("DISABLED", 4);
    elemDesc = (ElemDesc)paramTrie.get("BLOCKQUOTE");
    elemDesc.setAttr("CITE", 2);
    elemDesc = (ElemDesc)paramTrie.get("DEL");
    elemDesc.setAttr("CITE", 2);
    elemDesc = (ElemDesc)paramTrie.get("DIR");
    elemDesc.setAttr("COMPACT", 4);
    elemDesc = (ElemDesc)paramTrie.get("DIV");
    elemDesc.setAttr("SRC", 2);
    elemDesc.setAttr("NOWRAP", 4);
    elemDesc = (ElemDesc)paramTrie.get("DL");
    elemDesc.setAttr("COMPACT", 4);
    elemDesc = (ElemDesc)paramTrie.get("FORM");
    elemDesc.setAttr("ACTION", 2);
    elemDesc = (ElemDesc)paramTrie.get("FRAME");
    elemDesc.setAttr("SRC", 2);
    elemDesc.setAttr("LONGDESC", 2);
    elemDesc.setAttr("NORESIZE", 4);
    elemDesc = (ElemDesc)paramTrie.get("HEAD");
    elemDesc.setAttr("PROFILE", 2);
    elemDesc = (ElemDesc)paramTrie.get("HR");
    elemDesc.setAttr("NOSHADE", 4);
    elemDesc = (ElemDesc)paramTrie.get("IFRAME");
    elemDesc.setAttr("SRC", 2);
    elemDesc.setAttr("LONGDESC", 2);
    elemDesc = (ElemDesc)paramTrie.get("ILAYER");
    elemDesc.setAttr("SRC", 2);
    elemDesc = (ElemDesc)paramTrie.get("IMG");
    elemDesc.setAttr("SRC", 2);
    elemDesc.setAttr("LONGDESC", 2);
    elemDesc.setAttr("USEMAP", 2);
    elemDesc.setAttr("ISMAP", 4);
    elemDesc = (ElemDesc)paramTrie.get("INPUT");
    elemDesc.setAttr("SRC", 2);
    elemDesc.setAttr("USEMAP", 2);
    elemDesc.setAttr("CHECKED", 4);
    elemDesc.setAttr("DISABLED", 4);
    elemDesc.setAttr("ISMAP", 4);
    elemDesc.setAttr("READONLY", 4);
    elemDesc = (ElemDesc)paramTrie.get("INS");
    elemDesc.setAttr("CITE", 2);
    elemDesc = (ElemDesc)paramTrie.get("LAYER");
    elemDesc.setAttr("SRC", 2);
    elemDesc = (ElemDesc)paramTrie.get("LINK");
    elemDesc.setAttr("HREF", 2);
    elemDesc = (ElemDesc)paramTrie.get("MENU");
    elemDesc.setAttr("COMPACT", 4);
    elemDesc = (ElemDesc)paramTrie.get("OBJECT");
    elemDesc.setAttr("CLASSID", 2);
    elemDesc.setAttr("CODEBASE", 2);
    elemDesc.setAttr("DATA", 2);
    elemDesc.setAttr("ARCHIVE", 2);
    elemDesc.setAttr("USEMAP", 2);
    elemDesc.setAttr("DECLARE", 4);
    elemDesc = (ElemDesc)paramTrie.get("OL");
    elemDesc.setAttr("COMPACT", 4);
    elemDesc = (ElemDesc)paramTrie.get("OPTGROUP");
    elemDesc.setAttr("DISABLED", 4);
    elemDesc = (ElemDesc)paramTrie.get("OPTION");
    elemDesc.setAttr("SELECTED", 4);
    elemDesc.setAttr("DISABLED", 4);
    elemDesc = (ElemDesc)paramTrie.get("Q");
    elemDesc.setAttr("CITE", 2);
    elemDesc = (ElemDesc)paramTrie.get("SCRIPT");
    elemDesc.setAttr("SRC", 2);
    elemDesc.setAttr("FOR", 2);
    elemDesc.setAttr("DEFER", 4);
    elemDesc = (ElemDesc)paramTrie.get("SELECT");
    elemDesc.setAttr("DISABLED", 4);
    elemDesc.setAttr("MULTIPLE", 4);
    elemDesc = (ElemDesc)paramTrie.get("TABLE");
    elemDesc.setAttr("NOWRAP", 4);
    elemDesc = (ElemDesc)paramTrie.get("TD");
    elemDesc.setAttr("NOWRAP", 4);
    elemDesc = (ElemDesc)paramTrie.get("TEXTAREA");
    elemDesc.setAttr("DISABLED", 4);
    elemDesc.setAttr("READONLY", 4);
    elemDesc = (ElemDesc)paramTrie.get("TH");
    elemDesc.setAttr("NOWRAP", 4);
    elemDesc = (ElemDesc)paramTrie.get("TR");
    elemDesc.setAttr("NOWRAP", 4);
    elemDesc = (ElemDesc)paramTrie.get("UL");
    elemDesc.setAttr("COMPACT", 4);
  }
  
  public void setSpecialEscapeURLs(boolean paramBoolean) { this.m_specialEscapeURLs = paramBoolean; }
  
  public void setOmitMetaTag(boolean paramBoolean) { this.m_omitMetaTag = paramBoolean; }
  
  public void setOutputFormat(Properties paramProperties) {
    this.m_specialEscapeURLs = OutputPropertyUtils.getBooleanProperty("{http://xml.apache.org/xalan}use-url-escaping", paramProperties);
    this.m_omitMetaTag = OutputPropertyUtils.getBooleanProperty("{http://xml.apache.org/xalan}omit-meta-tag", paramProperties);
    super.setOutputFormat(paramProperties);
  }
  
  private final boolean getSpecialEscapeURLs() { return this.m_specialEscapeURLs; }
  
  private final boolean getOmitMetaTag() { return this.m_omitMetaTag; }
  
  public static final ElemDesc getElemDesc(String paramString) {
    Object object = m_elementFlags.get(paramString);
    return (null != object) ? (ElemDesc)object : m_dummy;
  }
  
  private ElemDesc getElemDesc2(String paramString) {
    Object object = this.m_htmlInfo.get2(paramString);
    return (null != object) ? (ElemDesc)object : m_dummy;
  }
  
  protected void startDocumentInternal() {
    super.startDocumentInternal();
    this.m_needToCallStartDocument = false;
    this.m_needToOutputDocTypeDecl = true;
    this.m_startNewLine = false;
    setOmitXMLDeclaration(true);
    if (true == this.m_needToOutputDocTypeDecl) {
      String str1 = getDoctypeSystem();
      String str2 = getDoctypePublic();
      if (null != str1 || null != str2) {
        Writer writer = this.m_writer;
        try {
          writer.write("<!DOCTYPE html");
          if (null != str2) {
            writer.write(" PUBLIC \"");
            writer.write(str2);
            writer.write(34);
          } 
          if (null != str1) {
            if (null == str2) {
              writer.write(" SYSTEM \"");
            } else {
              writer.write(" \"");
            } 
            writer.write(str1);
            writer.write(34);
          } 
          writer.write(62);
          outputLineSep();
        } catch (IOException iOException) {
          throw new SAXException(iOException);
        } 
      } 
    } 
    this.m_needToOutputDocTypeDecl = false;
  }
  
  public final void endDocument() {
    flushPending();
    if (this.m_doIndent && !this.m_isprevtext)
      try {
        outputLineSep();
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      }  
    flushWriter();
    if (this.m_tracer != null)
      fireEndDoc(); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    ElemContext elemContext = this.m_elemContext;
    if (elemContext.m_startTagOpen) {
      closeStartTag();
      elemContext.m_startTagOpen = false;
    } else if (this.m_cdataTagOpen) {
      closeCDATA();
      this.m_cdataTagOpen = false;
    } else if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } 
    if (null != paramString1 && paramString1.length() > 0) {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
      return;
    } 
    try {
      ElemDesc elemDesc = getElemDesc2(paramString3);
      int i = elemDesc.getFlags();
      if (this.m_doIndent) {
        boolean bool = ((i & 0x8) != 0) ? 1 : 0;
        if (this.m_ispreserve) {
          this.m_ispreserve = false;
        } else if (null != elemContext.m_elementName && (!this.m_inBlockElem || bool)) {
          this.m_startNewLine = true;
          indent();
        } 
        this.m_inBlockElem = !bool;
      } 
      if (paramAttributes != null)
        addAttributes(paramAttributes); 
      this.m_isprevtext = false;
      Writer writer = this.m_writer;
      writer.write(60);
      writer.write(paramString3);
      if (this.m_tracer != null)
        firePseudoAttributes(); 
      if ((i & 0x2) != 0) {
        this.m_elemContext = elemContext.push();
        this.m_elemContext.m_elementName = paramString3;
        this.m_elemContext.m_elementDesc = elemDesc;
        return;
      } 
      elemContext = elemContext.push(paramString1, paramString2, paramString3);
      this.m_elemContext = elemContext;
      elemContext.m_elementDesc = elemDesc;
      elemContext.m_isRaw = ((i & 0x100) != 0);
      if ((i & 0x400000) != 0) {
        closeStartTag();
        elemContext.m_startTagOpen = false;
        if (!this.m_omitMetaTag) {
          if (this.m_doIndent)
            indent(); 
          writer.write("<META http-equiv=\"Content-Type\" content=\"text/html; charset=");
          String str1 = getEncoding();
          String str2 = Encodings.getMimeEncoding(str1);
          writer.write(str2);
          writer.write("\">");
        } 
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public final void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_cdataTagOpen)
      closeCDATA(); 
    if (null != paramString1 && paramString1.length() > 0) {
      super.endElement(paramString1, paramString2, paramString3);
      return;
    } 
    try {
      ElemContext elemContext = this.m_elemContext;
      ElemDesc elemDesc = elemContext.m_elementDesc;
      int i = elemDesc.getFlags();
      boolean bool = ((i & 0x2) != 0) ? 1 : 0;
      if (this.m_doIndent) {
        boolean bool1 = ((i & 0x8) != 0) ? 1 : 0;
        boolean bool2 = false;
        if (this.m_ispreserve) {
          this.m_ispreserve = false;
        } else if (this.m_doIndent && (!this.m_inBlockElem || bool1)) {
          this.m_startNewLine = true;
          bool2 = true;
        } 
        if (!elemContext.m_startTagOpen && bool2)
          indent(elemContext.m_currentElemDepth - 1); 
        this.m_inBlockElem = !bool1;
      } 
      Writer writer = this.m_writer;
      if (!elemContext.m_startTagOpen) {
        writer.write("</");
        writer.write(paramString3);
        writer.write(62);
      } else {
        if (this.m_tracer != null)
          fireStartElem(paramString3); 
        int j = this.m_attributes.getLength();
        if (j > 0) {
          processAttributes(this.m_writer, j);
          this.m_attributes.clear();
        } 
        if (!bool) {
          writer.write("></");
          writer.write(paramString3);
          writer.write(62);
        } else {
          writer.write(62);
        } 
      } 
      if ((i & 0x200000) != 0)
        this.m_ispreserve = true; 
      this.m_isprevtext = false;
      if (this.m_tracer != null)
        fireEndElem(paramString3); 
      if (bool) {
        this.m_elemContext = elemContext.m_prev;
        return;
      } 
      if (!elemContext.m_startTagOpen && this.m_doIndent && !this.m_preserves.isEmpty())
        this.m_preserves.pop(); 
      this.m_elemContext = elemContext.m_prev;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  protected void processAttribute(Writer paramWriter, String paramString1, String paramString2, ElemDesc paramElemDesc) throws IOException {
    paramWriter.write(32);
    if ((paramString2.length() == 0 || paramString2.equalsIgnoreCase(paramString1)) && paramElemDesc != null && paramElemDesc.isAttrFlagSet(paramString1, 4)) {
      paramWriter.write(paramString1);
    } else {
      paramWriter.write(paramString1);
      paramWriter.write("=\"");
      if (paramElemDesc != null && paramElemDesc.isAttrFlagSet(paramString1, 2)) {
        writeAttrURI(paramWriter, paramString2, this.m_specialEscapeURLs);
      } else {
        writeAttrString(paramWriter, paramString2, getEncoding());
      } 
      paramWriter.write(34);
    } 
  }
  
  private boolean isASCIIDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  private static String makeHHString(int paramInt) {
    String str = Integer.toHexString(paramInt).toUpperCase();
    if (str.length() == 1)
      str = "0" + str; 
    return str;
  }
  
  private boolean isHHSign(String paramString) {
    boolean bool = true;
    try {
      char c = (char)Integer.parseInt(paramString, 16);
    } catch (NumberFormatException numberFormatException) {
      bool = false;
    } 
    return bool;
  }
  
  public void writeAttrURI(Writer paramWriter, String paramString, boolean paramBoolean) throws IOException {
    int i = paramString.length();
    if (i > this.m_attrBuff.length)
      this.m_attrBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_attrBuff, 0);
    char[] arrayOfChar = this.m_attrBuff;
    byte b1 = 0;
    byte b2 = 0;
    char c = Character.MIN_VALUE;
    for (byte b3 = 0; b3 < i; b3++) {
      c = arrayOfChar[b3];
      if (c < ' ' || c > '~') {
        if (b2) {
          paramWriter.write(arrayOfChar, b1, b2);
          b2 = 0;
        } 
        if (paramBoolean) {
          if (c <= '') {
            paramWriter.write(37);
            paramWriter.write(makeHHString(c));
          } else if (c <= '߿') {
            char c1 = c >> '\006' | 0xC0;
            char c2 = c & 0x3F | 0x80;
            paramWriter.write(37);
            paramWriter.write(makeHHString(c1));
            paramWriter.write(37);
            paramWriter.write(makeHHString(c2));
          } else if (Encodings.isHighUTF16Surrogate(c)) {
            char c1 = c & 0x3FF;
            char c2 = (c1 & 0x3C0) >> '\006';
            char c3 = c2 + '\001';
            char c4 = (c1 & 0x3C) >> '\002';
            char c5 = (c1 & 0x3) << '\004' & 0x30;
            c = arrayOfChar[++b3];
            char c6 = c & 0x3FF;
            c5 |= (c6 & 0x3C0) >> '\006';
            char c7 = c6 & 0x3F;
            char c8 = 0xF0 | c3 >> '\002';
            char c9 = 0x80 | (c3 & 0x3) << '\004' & 0x30 | c4;
            char c10 = 0x80 | c5;
            char c11 = 0x80 | c7;
            paramWriter.write(37);
            paramWriter.write(makeHHString(c8));
            paramWriter.write(37);
            paramWriter.write(makeHHString(c9));
            paramWriter.write(37);
            paramWriter.write(makeHHString(c10));
            paramWriter.write(37);
            paramWriter.write(makeHHString(c11));
          } else {
            char c1 = c >> '\f' | 0xE0;
            char c2 = (c & 0xFC0) >> '\006' | 0x80;
            char c3 = c & 0x3F | 0x80;
            paramWriter.write(37);
            paramWriter.write(makeHHString(c1));
            paramWriter.write(37);
            paramWriter.write(makeHHString(c2));
            paramWriter.write(37);
            paramWriter.write(makeHHString(c3));
          } 
        } else if (escapingNotNeeded(c)) {
          paramWriter.write(c);
        } else {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(c));
          paramWriter.write(59);
        } 
        b1 = b3 + 1;
      } else if (c == '"') {
        if (b2 > 0) {
          paramWriter.write(arrayOfChar, b1, b2);
          b2 = 0;
        } 
        if (paramBoolean) {
          paramWriter.write("%22");
        } else {
          paramWriter.write("&quot;");
        } 
        b1 = b3 + 1;
      } else if (c == '&') {
        if (b2 > 0) {
          paramWriter.write(arrayOfChar, b1, b2);
          b2 = 0;
        } 
        paramWriter.write("&amp;");
        b1 = b3 + 1;
      } else {
        b2++;
      } 
    } 
    if (b2 > 1) {
      if (b1 == 0) {
        paramWriter.write(paramString);
      } else {
        paramWriter.write(arrayOfChar, b1, b2);
      } 
    } else if (b2 == 1) {
      paramWriter.write(c);
    } 
  }
  
  public void writeAttrString(Writer paramWriter, String paramString1, String paramString2) throws IOException {
    int i = paramString1.length();
    if (i > this.m_attrBuff.length)
      this.m_attrBuff = new char[i * 2 + 1]; 
    paramString1.getChars(0, i, this.m_attrBuff, 0);
    char[] arrayOfChar = this.m_attrBuff;
    int j = 0;
    byte b = 0;
    char c = Character.MIN_VALUE;
    for (int k = 0; k < i; k++) {
      c = arrayOfChar[k];
      if (escapingNotNeeded(c) && !this.m_charInfo.isSpecialAttrChar(c)) {
        b++;
      } else if ('<' == c || '>' == c) {
        b++;
      } else if ('&' == c && k + true < i && '{' == arrayOfChar[k + true]) {
        b++;
      } else {
        if (b > 0) {
          paramWriter.write(arrayOfChar, j, b);
          b = 0;
        } 
        int m = accumDefaultEntity(paramWriter, c, k, arrayOfChar, i, false, true);
        if (k != m) {
          k = m - 1;
        } else {
          if (Encodings.isHighUTF16Surrogate(c)) {
            writeUTF16Surrogate(c, arrayOfChar, k, i);
            k++;
          } 
          String str = this.m_charInfo.getOutputStringForChar(c);
          if (null != str) {
            paramWriter.write(str);
          } else if (escapingNotNeeded(c)) {
            paramWriter.write(c);
          } else {
            paramWriter.write("&#");
            paramWriter.write(Integer.toString(c));
            paramWriter.write(59);
          } 
        } 
        j = k + 1;
      } 
    } 
    if (b > 1) {
      if (j == 0) {
        paramWriter.write(paramString1);
      } else {
        paramWriter.write(arrayOfChar, j, b);
      } 
    } else if (b == 1) {
      paramWriter.write(c);
    } 
  }
  
  public final void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_elemContext.m_isRaw)
      try {
        if (this.m_elemContext.m_startTagOpen) {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        } 
        this.m_ispreserve = true;
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, false, this.m_lineSepUse);
        if (this.m_tracer != null)
          fireCharEvent(paramArrayOfChar, paramInt1, paramInt2); 
        return;
      } catch (IOException iOException) {
        throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), iOException);
      }  
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void cdata(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (null != this.m_elemContext.m_elementName && (this.m_elemContext.m_elementName.equalsIgnoreCase("SCRIPT") || this.m_elemContext.m_elementName.equalsIgnoreCase("STYLE"))) {
      try {
        if (this.m_elemContext.m_startTagOpen) {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        } 
        this.m_ispreserve = true;
        if (shouldIndent())
          indent(); 
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, true, this.m_lineSepUse);
      } catch (IOException iOException) {
        throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), iOException);
      } 
    } else {
      super.cdata(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    flushPending();
    if (paramString1.equals("javax.xml.transform.disable-output-escaping")) {
      startNonEscaping();
    } else if (paramString1.equals("javax.xml.transform.enable-output-escaping")) {
      endNonEscaping();
    } else {
      try {
        if (this.m_elemContext.m_startTagOpen) {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        } else if (this.m_needToCallStartDocument) {
          startDocumentInternal();
        } 
        if (shouldIndent())
          indent(); 
        Writer writer = this.m_writer;
        writer.write("<?");
        writer.write(paramString1);
        if (paramString2.length() > 0 && !Character.isSpaceChar(paramString2.charAt(0)))
          writer.write(32); 
        writer.write(paramString2);
        writer.write(62);
        if (this.m_elemContext.m_currentElemDepth <= 0)
          outputLineSep(); 
        this.m_startNewLine = true;
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      } 
    } 
    if (this.m_tracer != null)
      fireEscapingEvent(paramString1, paramString2); 
  }
  
  public final void entityReference(String paramString) throws SAXException {
    try {
      Writer writer = this.m_writer;
      writer.write(38);
      writer.write(paramString);
      writer.write(59);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public final void endElement(String paramString) throws SAXException { endElement(null, null, paramString); }
  
  public void processAttributes(Writer paramWriter, int paramInt) throws IOException, SAXException {
    for (byte b = 0; b < paramInt; b++)
      processAttribute(paramWriter, this.m_attributes.getQName(b), this.m_attributes.getValue(b), this.m_elemContext.m_elementDesc); 
  }
  
  protected void closeStartTag() {
    try {
      if (this.m_tracer != null)
        fireStartElem(this.m_elemContext.m_elementName); 
      int i = this.m_attributes.getLength();
      if (i > 0) {
        processAttributes(this.m_writer, i);
        this.m_attributes.clear();
      } 
      this.m_writer.write(62);
      if (this.m_cdataSectionElements != null)
        this.m_elemContext.m_isCdataSection = isCdataSection(); 
      if (this.m_doIndent) {
        this.m_isprevtext = false;
        this.m_preserves.push(this.m_ispreserve);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  protected void init(OutputStream paramOutputStream, Properties paramProperties) throws UnsupportedEncodingException {
    if (null == paramProperties)
      paramProperties = OutputPropertiesFactory.getDefaultMethodProperties("html"); 
    init(paramOutputStream, paramProperties, false);
  }
  
  public void setOutputStream(OutputStream paramOutputStream) {
    try {
      Properties properties;
      if (null == this.m_format) {
        properties = OutputPropertiesFactory.getDefaultMethodProperties("html");
      } else {
        properties = this.m_format;
      } 
      init(paramOutputStream, properties, true);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) throws SAXException {
    if (this.m_elemContext.m_elementURI == null) {
      String str = getPrefixPart(this.m_elemContext.m_elementName);
      if (str == null && "".equals(paramString1))
        this.m_elemContext.m_elementURI = paramString2; 
    } 
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    this.m_inDTD = true;
    super.startDTD(paramString1, paramString2, paramString3);
  }
  
  public void endDTD() { this.m_inDTD = false; }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {}
  
  public void elementDecl(String paramString1, String paramString2) throws SAXException {}
  
  public void internalEntityDecl(String paramString1, String paramString2) throws SAXException {}
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException {
    try {
      Writer writer = this.m_writer;
      if ((paramInt & true) > 0 && m_htmlcharInfo.onlyQuotAmpLtGt) {
        writer.write(32);
        writer.write(paramString1);
        writer.write("=\"");
        writer.write(paramString2);
        writer.write(34);
      } else if ((paramInt & 0x2) > 0 && (paramString2.length() == 0 || paramString2.equalsIgnoreCase(paramString1))) {
        writer.write(32);
        writer.write(paramString1);
      } else {
        writer.write(32);
        writer.write(paramString1);
        writer.write("=\"");
        if ((paramInt & 0x4) > 0) {
          writeAttrURI(writer, paramString2, this.m_specialEscapeURLs);
        } else {
          writeAttrString(writer, paramString2, getEncoding());
        } 
        writer.write(34);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_inDTD)
      return; 
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public boolean reset() {
    boolean bool = super.reset();
    if (!bool)
      return false; 
    initToHTMLStream();
    return true;
  }
  
  private void initToHTMLStream() {
    this.m_inBlockElem = false;
    this.m_inDTD = false;
    this.m_omitMetaTag = false;
    this.m_specialEscapeURLs = true;
  }
  
  static  {
    initTagReference(m_elementFlags);
    m_dummy = new ElemDesc(8);
  }
  
  static class Trie {
    public static final int ALPHA_SIZE = 128;
    
    final Node m_Root = new Node();
    
    private char[] m_charBuffer = new char[0];
    
    private final boolean m_lowerCaseOnly = false;
    
    public Trie() {}
    
    public Trie(boolean param1Boolean) {}
    
    public Object put(String param1String, Object param1Object) {
      int i = param1String.length();
      if (i > this.m_charBuffer.length)
        this.m_charBuffer = new char[i]; 
      Node node = this.m_Root;
      for (byte b = 0; b < i; b++) {
        Node node1 = node.m_nextChar[Character.toLowerCase(param1String.charAt(b))];
        if (node1 != null) {
          node = node1;
        } else {
          while (b < i) {
            Node node2 = new Node();
            if (this.m_lowerCaseOnly) {
              node.m_nextChar[Character.toLowerCase(param1String.charAt(b))] = node2;
            } else {
              node.m_nextChar[Character.toUpperCase(param1String.charAt(b))] = node2;
              node.m_nextChar[Character.toLowerCase(param1String.charAt(b))] = node2;
            } 
            node = node2;
            b++;
          } 
          break;
        } 
      } 
      Object object = node.m_Value;
      node.m_Value = param1Object;
      return object;
    }
    
    public Object get(String param1String) {
      int i = param1String.length();
      if (this.m_charBuffer.length < i)
        return null; 
      Node node = this.m_Root;
      switch (i) {
        case 0:
          return null;
        case 1:
          c = param1String.charAt(0);
          if (c < '') {
            node = node.m_nextChar[c];
            if (node != null)
              return node.m_Value; 
          } 
          return null;
      } 
      for (char c = Character.MIN_VALUE; c < i; c++) {
        char c1 = param1String.charAt(c);
        if ('' <= c1)
          return null; 
        node = node.m_nextChar[c1];
        if (node == null)
          return null; 
      } 
      return node.m_Value;
    }
    
    public Trie(Trie param1Trie) {
      int i = param1Trie.getLongestKeyLength();
      this.m_charBuffer = new char[i];
    }
    
    public Object get2(String param1String) {
      int i = param1String.length();
      if (this.m_charBuffer.length < i)
        return null; 
      Node node = this.m_Root;
      switch (i) {
        case 0:
          return null;
        case 1:
          c = param1String.charAt(0);
          if (c < '') {
            node = node.m_nextChar[c];
            if (node != null)
              return node.m_Value; 
          } 
          return null;
      } 
      param1String.getChars(0, i, this.m_charBuffer, 0);
      for (char c = Character.MIN_VALUE; c < i; c++) {
        char c1 = this.m_charBuffer[c];
        if ('' <= c1)
          return null; 
        node = node.m_nextChar[c1];
        if (node == null)
          return null; 
      } 
      return node.m_Value;
    }
    
    public int getLongestKeyLength() { return this.m_charBuffer.length; }
    
    private class Node {
      final Node[] m_nextChar = new Node[128];
      
      Object m_Value = null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToHTMLStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */