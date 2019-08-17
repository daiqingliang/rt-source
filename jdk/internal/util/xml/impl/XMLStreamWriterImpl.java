package jdk.internal.util.xml.impl;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import jdk.internal.util.xml.XMLStreamException;
import jdk.internal.util.xml.XMLStreamWriter;

public class XMLStreamWriterImpl implements XMLStreamWriter {
  static final int STATE_XML_DECL = 1;
  
  static final int STATE_PROLOG = 2;
  
  static final int STATE_DTD_DECL = 3;
  
  static final int STATE_ELEMENT = 4;
  
  static final int ELEMENT_STARTTAG_OPEN = 10;
  
  static final int ELEMENT_STARTTAG_CLOSE = 11;
  
  static final int ELEMENT_ENDTAG_OPEN = 12;
  
  static final int ELEMENT_ENDTAG_CLOSE = 13;
  
  public static final char CLOSE_START_TAG = '>';
  
  public static final char OPEN_START_TAG = '<';
  
  public static final String OPEN_END_TAG = "</";
  
  public static final char CLOSE_END_TAG = '>';
  
  public static final String START_CDATA = "<![CDATA[";
  
  public static final String END_CDATA = "]]>";
  
  public static final String CLOSE_EMPTY_ELEMENT = "/>";
  
  public static final String ENCODING_PREFIX = "&#x";
  
  public static final char SPACE = ' ';
  
  public static final char AMPERSAND = '&';
  
  public static final char DOUBLEQUOT = '"';
  
  public static final char SEMICOLON = ';';
  
  private int _state = 0;
  
  private Element _currentEle;
  
  private XMLWriter _writer;
  
  private String _encoding;
  
  boolean _escapeCharacters = true;
  
  private boolean _doIndent = true;
  
  private char[] _lineSep = System.getProperty("line.separator").toCharArray();
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream) throws XMLStreamException { this(paramOutputStream, "UTF-8"); }
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream, String paramString) throws XMLStreamException {
    Charset charset = null;
    if (paramString == null) {
      this._encoding = "UTF-8";
    } else {
      try {
        charset = getCharset(paramString);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new XMLStreamException(unsupportedEncodingException);
      } 
      this._encoding = paramString;
    } 
    this._writer = new XMLWriter(paramOutputStream, paramString, charset);
  }
  
  public void writeStartDocument() throws XMLStreamException { writeStartDocument(this._encoding, "1.0"); }
  
  public void writeStartDocument(String paramString) throws XMLStreamException { writeStartDocument(this._encoding, paramString, null); }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException { writeStartDocument(paramString1, paramString2, null); }
  
  public void writeStartDocument(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    if (this._state > 0)
      throw new XMLStreamException("XML declaration must be as the first line in the XML document."); 
    this._state = 1;
    String str = paramString1;
    if (str == null) {
      str = this._encoding;
    } else {
      try {
        getCharset(paramString1);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new XMLStreamException(unsupportedEncodingException);
      } 
    } 
    if (paramString2 == null)
      paramString2 = "1.0"; 
    this._writer.write("<?xml version=\"");
    this._writer.write(paramString2);
    this._writer.write(34);
    if (str != null) {
      this._writer.write(" encoding=\"");
      this._writer.write(str);
      this._writer.write(34);
    } 
    if (paramString3 != null) {
      this._writer.write(" standalone=\"");
      this._writer.write(paramString3);
      this._writer.write(34);
    } 
    this._writer.write("?>");
    writeLineSeparator();
  }
  
  public void writeDTD(String paramString) throws XMLStreamException {
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    this._writer.write(paramString);
    writeLineSeparator();
  }
  
  public void writeStartElement(String paramString) throws XMLStreamException {
    if (paramString == null || paramString.length() == 0)
      throw new XMLStreamException("Local Name cannot be null or empty"); 
    this._state = 4;
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    this._currentEle = new Element(this._currentEle, paramString, false);
    openStartTag();
    this._writer.write(paramString);
  }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException {
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    this._currentEle = new Element(this._currentEle, paramString, true);
    openStartTag();
    this._writer.write(paramString);
  }
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException {
    if (this._currentEle.getState() != 10)
      throw new XMLStreamException("Attribute not associated with any element"); 
    this._writer.write(32);
    this._writer.write(paramString1);
    this._writer.write("=\"");
    writeXMLContent(paramString2, true, true);
    this._writer.write(34);
  }
  
  public void writeEndDocument() throws XMLStreamException {
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    while (this._currentEle != null) {
      if (!this._currentEle.isEmpty()) {
        this._writer.write("</");
        this._writer.write(this._currentEle.getLocalName());
        this._writer.write(62);
      } 
      this._currentEle = this._currentEle.getParent();
    } 
  }
  
  public void writeEndElement() throws XMLStreamException {
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    if (this._currentEle == null)
      throw new XMLStreamException("No element was found to write"); 
    if (this._currentEle.isEmpty())
      return; 
    this._writer.write("</");
    this._writer.write(this._currentEle.getLocalName());
    this._writer.write(62);
    writeLineSeparator();
    this._currentEle = this._currentEle.getParent();
  }
  
  public void writeCData(String paramString) throws XMLStreamException {
    if (paramString == null)
      throw new XMLStreamException("cdata cannot be null"); 
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    this._writer.write("<![CDATA[");
    this._writer.write(paramString);
    this._writer.write("]]>");
  }
  
  public void writeCharacters(String paramString) throws XMLStreamException {
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    writeXMLContent(paramString);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    if (this._currentEle != null && this._currentEle.getState() == 10)
      closeStartTag(); 
    writeXMLContent(paramArrayOfChar, paramInt1, paramInt2, this._escapeCharacters);
  }
  
  public void close() throws XMLStreamException {
    if (this._writer != null)
      this._writer.close(); 
    this._writer = null;
    this._currentEle = null;
    this._state = 0;
  }
  
  public void flush() throws XMLStreamException {
    if (this._writer != null)
      this._writer.flush(); 
  }
  
  public void setDoIndent(boolean paramBoolean) { this._doIndent = paramBoolean; }
  
  private void writeXMLContent(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws XMLStreamException {
    if (!paramBoolean) {
      this._writer.write(paramArrayOfChar, paramInt1, paramInt2);
      return;
    } 
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++) {
      char c = paramArrayOfChar[k];
      if (!this._writer.canEncode(c)) {
        this._writer.write(paramArrayOfChar, i, k - i);
        this._writer.write("&#x");
        this._writer.write(Integer.toHexString(c));
        this._writer.write(59);
        i = k + 1;
      } else {
        switch (c) {
          case '<':
            this._writer.write(paramArrayOfChar, i, k - i);
            this._writer.write("&lt;");
            i = k + 1;
            break;
          case '&':
            this._writer.write(paramArrayOfChar, i, k - i);
            this._writer.write("&amp;");
            i = k + 1;
            break;
          case '>':
            this._writer.write(paramArrayOfChar, i, k - i);
            this._writer.write("&gt;");
            i = k + 1;
            break;
        } 
      } 
    } 
    this._writer.write(paramArrayOfChar, i, j - i);
  }
  
  private void writeXMLContent(String paramString) throws XMLStreamException {
    if (paramString != null && paramString.length() > 0)
      writeXMLContent(paramString, this._escapeCharacters, false); 
  }
  
  private void writeXMLContent(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws XMLStreamException {
    if (!paramBoolean1) {
      this._writer.write(paramString);
      return;
    } 
    int i = 0;
    int j = paramString.length();
    for (byte b = 0; b < j; b++) {
      char c = paramString.charAt(b);
      if (!this._writer.canEncode(c)) {
        this._writer.write(paramString, i, b - i);
        this._writer.write("&#x");
        this._writer.write(Integer.toHexString(c));
        this._writer.write(59);
        i = b + 1;
      } else {
        switch (c) {
          case '<':
            this._writer.write(paramString, i, b - i);
            this._writer.write("&lt;");
            i = b + 1;
            break;
          case '&':
            this._writer.write(paramString, i, b - i);
            this._writer.write("&amp;");
            i = b + 1;
            break;
          case '>':
            this._writer.write(paramString, i, b - i);
            this._writer.write("&gt;");
            i = b + 1;
            break;
          case '"':
            this._writer.write(paramString, i, b - i);
            if (paramBoolean2) {
              this._writer.write("&quot;");
            } else {
              this._writer.write(34);
            } 
            i = b + 1;
            break;
        } 
      } 
    } 
    this._writer.write(paramString, i, j - i);
  }
  
  private void openStartTag() throws XMLStreamException {
    this._currentEle.setState(10);
    this._writer.write(60);
  }
  
  private void closeStartTag() throws XMLStreamException {
    if (this._currentEle.isEmpty()) {
      this._writer.write("/>");
    } else {
      this._writer.write(62);
    } 
    if (this._currentEle.getParent() == null)
      writeLineSeparator(); 
    this._currentEle.setState(11);
  }
  
  private void writeLineSeparator() throws XMLStreamException {
    if (this._doIndent)
      this._writer.write(this._lineSep, 0, this._lineSep.length); 
  }
  
  private Charset getCharset(String paramString) throws UnsupportedEncodingException {
    Charset charset;
    if (paramString.equalsIgnoreCase("UTF-32"))
      throw new UnsupportedEncodingException("The basic XMLWriter does not support " + paramString); 
    try {
      charset = Charset.forName(paramString);
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new UnsupportedEncodingException(paramString);
    } 
    return charset;
  }
  
  protected class Element {
    protected Element _parent;
    
    protected short _Depth;
    
    boolean _isEmptyElement = false;
    
    String _localpart;
    
    int _state;
    
    public Element() {}
    
    public Element(Element param1Element, String param1String, boolean param1Boolean) {
      this._parent = param1Element;
      this._localpart = param1String;
      this._isEmptyElement = param1Boolean;
    }
    
    public Element getParent() { return this._parent; }
    
    public String getLocalName() { return this._localpart; }
    
    public int getState() { return this._state; }
    
    public void setState(int param1Int) { this._state = param1Int; }
    
    public boolean isEmpty() { return this._isEmptyElement; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\XMLStreamWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */