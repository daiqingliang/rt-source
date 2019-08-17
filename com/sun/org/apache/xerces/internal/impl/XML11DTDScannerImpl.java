package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public class XML11DTDScannerImpl extends XMLDTDScannerImpl {
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  public XML11DTDScannerImpl() {}
  
  public XML11DTDScannerImpl(SymbolTable paramSymbolTable, XMLErrorReporter paramXMLErrorReporter, XMLEntityManager paramXMLEntityManager) { super(paramSymbolTable, paramXMLErrorReporter, paramXMLEntityManager); }
  
  protected boolean scanPubidLiteral(XMLString paramXMLString) throws IOException, XNIException {
    int i = this.fEntityScanner.scanChar(null);
    if (i != 39 && i != 34) {
      reportFatalError("QuoteRequiredInPublicID", null);
      return false;
    } 
    this.fStringBuffer.clear();
    boolean bool = true;
    boolean bool1 = true;
    while (true) {
      int j = this.fEntityScanner.scanChar(null);
      if (j == 32 || j == 10 || j == 13 || j == 133 || j == 8232) {
        if (!bool) {
          this.fStringBuffer.append(' ');
          bool = true;
        } 
        continue;
      } 
      if (j == i) {
        if (bool)
          this.fStringBuffer.length--; 
        paramXMLString.setValues(this.fStringBuffer);
        break;
      } 
      if (XMLChar.isPubid(j)) {
        this.fStringBuffer.append((char)j);
        bool = false;
        continue;
      } 
      if (j == -1) {
        reportFatalError("PublicIDUnterminated", null);
        return false;
      } 
      bool1 = false;
      reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(j) });
    } 
    return bool1;
  }
  
  protected void normalizeWhitespace(XMLString paramXMLString) {
    int i = paramXMLString.offset + paramXMLString.length;
    for (int j = paramXMLString.offset; j < i; j++) {
      char c = paramXMLString.ch[j];
      if (XMLChar.isSpace(c))
        paramXMLString.ch[j] = ' '; 
    } 
  }
  
  protected void normalizeWhitespace(XMLString paramXMLString, int paramInt) {
    int i = paramXMLString.offset + paramXMLString.length;
    for (int j = paramXMLString.offset + paramInt; j < i; j++) {
      char c = paramXMLString.ch[j];
      if (XMLChar.isSpace(c))
        paramXMLString.ch[j] = ' '; 
    } 
  }
  
  protected int isUnchangedByNormalization(XMLString paramXMLString) {
    int i = paramXMLString.offset + paramXMLString.length;
    for (int j = paramXMLString.offset; j < i; j++) {
      char c = paramXMLString.ch[j];
      if (XMLChar.isSpace(c))
        return j - paramXMLString.offset; 
    } 
    return -1;
  }
  
  protected boolean isInvalid(int paramInt) { return !XML11Char.isXML11Valid(paramInt); }
  
  protected boolean isInvalidLiteral(int paramInt) { return !XML11Char.isXML11ValidLiteral(paramInt); }
  
  protected boolean isValidNameChar(int paramInt) { return XML11Char.isXML11Name(paramInt); }
  
  protected boolean isValidNameStartChar(int paramInt) { return XML11Char.isXML11NameStart(paramInt); }
  
  protected boolean isValidNCName(int paramInt) { return XML11Char.isXML11NCName(paramInt); }
  
  protected boolean isValidNameStartHighSurrogate(int paramInt) { return XML11Char.isXML11NameHighSurrogate(paramInt); }
  
  protected boolean versionSupported(String paramString) { return (paramString.equals("1.1") || paramString.equals("1.0")); }
  
  protected String getVersionNotSupportedKey() { return "VersionNotSupported11"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11DTDScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */