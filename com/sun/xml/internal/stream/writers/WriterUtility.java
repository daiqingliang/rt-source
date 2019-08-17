package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class WriterUtility {
  public static final String START_COMMENT = "<!--";
  
  public static final String END_COMMENT = "-->";
  
  public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
  
  public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
  
  public static final String DEFAULT_XML_VERSION = "1.0";
  
  public static final char CLOSE_START_TAG = '>';
  
  public static final char OPEN_START_TAG = '<';
  
  public static final String OPEN_END_TAG = "</";
  
  public static final char CLOSE_END_TAG = '>';
  
  public static final String START_CDATA = "<![CDATA[";
  
  public static final String END_CDATA = "]]>";
  
  public static final String CLOSE_EMPTY_ELEMENT = "/>";
  
  public static final String SPACE = " ";
  
  public static final String UTF_8 = "utf-8";
  
  static final boolean DEBUG_XML_CONTENT = false;
  
  boolean fEscapeCharacters = true;
  
  Writer fWriter = null;
  
  CharsetEncoder fEncoder;
  
  public WriterUtility() { this.fEncoder = getDefaultEncoder(); }
  
  public WriterUtility(Writer paramWriter) {
    this.fWriter = paramWriter;
    if (paramWriter instanceof OutputStreamWriter) {
      String str = ((OutputStreamWriter)paramWriter).getEncoding();
      if (str != null)
        this.fEncoder = Charset.forName(str).newEncoder(); 
    } else if (paramWriter instanceof FileWriter) {
      String str = ((FileWriter)paramWriter).getEncoding();
      if (str != null)
        this.fEncoder = Charset.forName(str).newEncoder(); 
    } else {
      this.fEncoder = getDefaultEncoder();
    } 
  }
  
  public void setWriter(Writer paramWriter) { this.fWriter = paramWriter; }
  
  public void setEscapeCharacters(boolean paramBoolean) { this.fEscapeCharacters = paramBoolean; }
  
  public boolean getEscapeCharacters() { return this.fEscapeCharacters; }
  
  public void writeXMLContent(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException { writeXMLContent(paramArrayOfChar, paramInt1, paramInt2, getEscapeCharacters()); }
  
  private void writeXMLContent(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException {
    int j = paramInt1 + paramInt2;
    int k = paramInt1;
    for (int i = paramInt1; i < j; i++) {
      char c = paramArrayOfChar[i];
      if (this.fEncoder != null && !this.fEncoder.canEncode(c)) {
        this.fWriter.write(paramArrayOfChar, k, i - k);
        this.fWriter.write("&#x");
        this.fWriter.write(Integer.toHexString(c));
        this.fWriter.write(59);
        k = i + 1;
      } 
      switch (c) {
        case '<':
          if (paramBoolean) {
            this.fWriter.write(paramArrayOfChar, k, i - k);
            this.fWriter.write("&lt;");
            k = i + 1;
          } 
          break;
        case '&':
          if (paramBoolean) {
            this.fWriter.write(paramArrayOfChar, k, i - k);
            this.fWriter.write("&amp;");
            k = i + 1;
          } 
          break;
        case '>':
          if (paramBoolean) {
            this.fWriter.write(paramArrayOfChar, k, i - k);
            this.fWriter.write("&gt;");
            k = i + 1;
          } 
          break;
      } 
    } 
    this.fWriter.write(paramArrayOfChar, k, j - k);
  }
  
  public void writeXMLContent(String paramString) throws IOException {
    if (paramString == null || paramString.length() == 0)
      return; 
    writeXMLContent(paramString.toCharArray(), 0, paramString.length());
  }
  
  public void writeXMLAttributeValue(String paramString) throws IOException { writeXMLContent(paramString.toCharArray(), 0, paramString.length(), true); }
  
  private CharsetEncoder getDefaultEncoder() {
    try {
      String str = SecuritySupport.getSystemProperty("file.encoding");
      if (str != null)
        return Charset.forName(str).newEncoder(); 
    } catch (Exception exception) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\writers\WriterUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */