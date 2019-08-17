package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;

public class XMLDeclarationParser {
  private String m_encoding;
  
  private PushbackReader m_pushbackReader;
  
  private boolean m_hasHeader;
  
  private String xmlDecl = null;
  
  static String gt16 = null;
  
  static String utf16Decl = null;
  
  public XMLDeclarationParser(PushbackReader paramPushbackReader) {
    this.m_pushbackReader = paramPushbackReader;
    this.m_encoding = "utf-8";
    this.m_hasHeader = false;
  }
  
  public String getEncoding() { return this.m_encoding; }
  
  public String getXmlDeclaration() { return this.xmlDecl; }
  
  public void parse() throws TransformerException, IOException {
    int i = 0;
    byte b1 = 0;
    char[] arrayOfChar = new char[65535];
    StringBuffer stringBuffer = new StringBuffer();
    while ((i = this.m_pushbackReader.read()) != -1) {
      arrayOfChar[b1] = (char)i;
      stringBuffer.append((char)i);
      b1++;
      if (i == 62)
        break; 
    } 
    byte b2 = b1;
    String str = stringBuffer.toString();
    boolean bool1 = false;
    boolean bool2 = false;
    int j = str.indexOf(utf16Decl);
    if (j > -1) {
      bool1 = true;
    } else {
      j = str.indexOf("<?xml");
      if (j > -1)
        bool2 = true; 
    } 
    if (!bool1 && !bool2) {
      this.m_pushbackReader.unread(arrayOfChar, 0, b2);
      return;
    } 
    this.m_hasHeader = true;
    if (bool1) {
      this.xmlDecl = new String(str.getBytes(), "utf-16");
      this.xmlDecl = this.xmlDecl.substring(this.xmlDecl.indexOf("<"));
    } else {
      this.xmlDecl = str;
    } 
    if (j != 0)
      throw new IOException("Unexpected characters before XML declaration"); 
    int k = this.xmlDecl.indexOf("version");
    if (k == -1)
      throw new IOException("Mandatory 'version' attribute Missing in XML declaration"); 
    int m = this.xmlDecl.indexOf("encoding");
    if (m == -1)
      return; 
    if (k > m)
      throw new IOException("The 'version' attribute should preceed the 'encoding' attribute in an XML Declaration"); 
    int n = this.xmlDecl.indexOf("standalone");
    if (n > -1 && (n < k || n < m))
      throw new IOException("The 'standalone' attribute should be the last attribute in an XML Declaration"); 
    int i1 = this.xmlDecl.indexOf("=", m);
    if (i1 == -1)
      throw new IOException("Missing '=' character after 'encoding' in XML declaration"); 
    this.m_encoding = parseEncoding(this.xmlDecl, i1);
    if (this.m_encoding.startsWith("\"")) {
      this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("\"") + 1, this.m_encoding.lastIndexOf("\""));
    } else if (this.m_encoding.startsWith("'")) {
      this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("'") + 1, this.m_encoding.lastIndexOf("'"));
    } 
  }
  
  public void writeTo(Writer paramWriter) throws IOException {
    if (!this.m_hasHeader)
      return; 
    paramWriter.write(this.xmlDecl.toString());
  }
  
  private String parseEncoding(String paramString, int paramInt) throws IOException {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString.substring(paramInt + 1));
    if (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      int i = str.indexOf("?");
      return (i > -1) ? str.substring(0, i) : str;
    } 
    throw new IOException("Error parsing 'encoding' attribute in XML declaration");
  }
  
  static  {
    try {
      gt16 = new String(">".getBytes("utf-16"));
      utf16Decl = new String("<?xml".getBytes("utf-16"));
    } catch (Exception exception) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\XMLDeclarationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */