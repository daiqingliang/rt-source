package com.sun.xml.internal.txw2.output;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter extends XMLFilterImpl implements LexicalHandler {
  private final HashMap locallyDeclaredPrefix = new HashMap();
  
  private final Attributes EMPTY_ATTS = new AttributesImpl();
  
  private boolean inCDATA = false;
  
  private int elementLevel = 0;
  
  private Writer output;
  
  private String encoding;
  
  private boolean writeXmlDecl = true;
  
  private String header = null;
  
  private final CharacterEscapeHandler escapeHandler;
  
  private boolean startTagIsClosed = true;
  
  public XMLWriter(Writer paramWriter, String paramString, CharacterEscapeHandler paramCharacterEscapeHandler) {
    init(paramWriter, paramString);
    this.escapeHandler = paramCharacterEscapeHandler;
  }
  
  public XMLWriter(Writer paramWriter, String paramString) { this(paramWriter, paramString, DumbEscapeHandler.theInstance); }
  
  private void init(Writer paramWriter, String paramString) { setOutput(paramWriter, paramString); }
  
  public void reset() {
    this.elementLevel = 0;
    this.startTagIsClosed = true;
  }
  
  public void flush() { this.output.flush(); }
  
  public void setOutput(Writer paramWriter, String paramString) {
    if (paramWriter == null) {
      this.output = new OutputStreamWriter(System.out);
    } else {
      this.output = paramWriter;
    } 
    this.encoding = paramString;
  }
  
  public void setEncoding(String paramString) { this.encoding = paramString; }
  
  public void setXmlDecl(boolean paramBoolean) { this.writeXmlDecl = paramBoolean; }
  
  public void setHeader(String paramString) { this.header = paramString; }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { this.locallyDeclaredPrefix.put(paramString1, paramString2); }
  
  public void startDocument() {
    try {
      reset();
      if (this.writeXmlDecl) {
        String str = "";
        if (this.encoding != null)
          str = " encoding=\"" + this.encoding + "\""; 
        write("<?xml version=\"1.0\"" + str + " standalone=\"yes\"?>\n");
      } 
      if (this.header != null)
        write(this.header); 
      super.startDocument();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endDocument() {
    try {
      if (!this.startTagIsClosed) {
        write("/>");
        this.startTagIsClosed = true;
      } 
      write('\n');
      super.endDocument();
      try {
        flush();
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      } 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    try {
      if (!this.startTagIsClosed)
        write(">"); 
      this.elementLevel++;
      write('<');
      writeName(paramString1, paramString2, paramString3, true);
      writeAttributes(paramAttributes);
      if (!this.locallyDeclaredPrefix.isEmpty()) {
        for (Map.Entry entry : this.locallyDeclaredPrefix.entrySet()) {
          String str1 = (String)entry.getKey();
          String str2 = (String)entry.getValue();
          if (str2 == null)
            str2 = ""; 
          write(' ');
          if ("".equals(str1)) {
            write("xmlns=\"");
          } else {
            write("xmlns:");
            write(str1);
            write("=\"");
          } 
          char[] arrayOfChar = str2.toCharArray();
          writeEsc(arrayOfChar, 0, arrayOfChar.length, true);
          write('"');
        } 
        this.locallyDeclaredPrefix.clear();
      } 
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
      this.startTagIsClosed = false;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      if (this.startTagIsClosed) {
        write("</");
        writeName(paramString1, paramString2, paramString3, true);
        write('>');
      } else {
        write("/>");
        this.startTagIsClosed = true;
      } 
      if (this.elementLevel == 1)
        write('\n'); 
      super.endElement(paramString1, paramString2, paramString3);
      this.elementLevel--;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      if (!this.startTagIsClosed) {
        write('>');
        this.startTagIsClosed = true;
      } 
      if (this.inCDATA) {
        this.output.write(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        writeEsc(paramArrayOfChar, paramInt1, paramInt2, false);
      } 
      super.characters(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      writeEsc(paramArrayOfChar, paramInt1, paramInt2, false);
      super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      if (!this.startTagIsClosed) {
        write('>');
        this.startTagIsClosed = true;
      } 
      write("<?");
      write(paramString1);
      write(' ');
      write(paramString2);
      write("?>");
      if (this.elementLevel < 1)
        write('\n'); 
      super.processingInstruction(paramString1, paramString2);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startElement(String paramString1, String paramString2) throws SAXException { startElement(paramString1, paramString2, "", this.EMPTY_ATTS); }
  
  public void startElement(String paramString) { startElement("", paramString, "", this.EMPTY_ATTS); }
  
  public void endElement(String paramString1, String paramString2) throws SAXException { endElement(paramString1, paramString2, ""); }
  
  public void endElement(String paramString) { endElement("", paramString, ""); }
  
  public void dataElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, String paramString4) throws SAXException {
    startElement(paramString1, paramString2, paramString3, paramAttributes);
    characters(paramString4);
    endElement(paramString1, paramString2, paramString3);
  }
  
  public void dataElement(String paramString1, String paramString2, String paramString3) throws SAXException { dataElement(paramString1, paramString2, "", this.EMPTY_ATTS, paramString3); }
  
  public void dataElement(String paramString1, String paramString2) throws SAXException { dataElement("", paramString1, "", this.EMPTY_ATTS, paramString2); }
  
  public void characters(String paramString) {
    try {
      if (!this.startTagIsClosed) {
        write('>');
        this.startTagIsClosed = true;
      } 
      char[] arrayOfChar = paramString.toCharArray();
      characters(arrayOfChar, 0, arrayOfChar.length);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void endDTD() {}
  
  public void startEntity(String paramString) {}
  
  public void endEntity(String paramString) {}
  
  public void startCDATA() {
    try {
      if (!this.startTagIsClosed) {
        write('>');
        this.startTagIsClosed = true;
      } 
      write("<![CDATA[");
      this.inCDATA = true;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endCDATA() {
    try {
      this.inCDATA = false;
      write("]]>");
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      this.output.write("<!--");
      this.output.write(paramArrayOfChar, paramInt1, paramInt2);
      this.output.write("-->");
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  private void write(char paramChar) throws IOException { this.output.write(paramChar); }
  
  private void write(String paramString) { this.output.write(paramString); }
  
  private void writeAttributes(Attributes paramAttributes) throws IOException, SAXException {
    int i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      char[] arrayOfChar = paramAttributes.getValue(b).toCharArray();
      write(' ');
      writeName(paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getQName(b), false);
      write("=\"");
      writeEsc(arrayOfChar, 0, arrayOfChar.length, true);
      write('"');
    } 
  }
  
  private void writeEsc(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws SAXException, IOException { this.escapeHandler.escape(paramArrayOfChar, paramInt1, paramInt2, paramBoolean, this.output); }
  
  private void writeName(String paramString1, String paramString2, String paramString3, boolean paramBoolean) throws IOException { write(paramString3); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\XMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */