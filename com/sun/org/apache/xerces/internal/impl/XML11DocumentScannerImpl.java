package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public class XML11DocumentScannerImpl extends XMLDocumentScannerImpl {
  private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  
  private final XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
  
  protected int scanContent(XMLStringBuffer paramXMLStringBuffer) throws IOException, XNIException {
    this.fTempString.length = 0;
    int i = this.fEntityScanner.scanContent(this.fTempString);
    paramXMLStringBuffer.append(this.fTempString);
    if (i == 13 || i == 133 || i == 8232) {
      this.fEntityScanner.scanChar(null);
      paramXMLStringBuffer.append((char)i);
      i = -1;
    } 
    if (i == 93) {
      paramXMLStringBuffer.append((char)this.fEntityScanner.scanChar(null));
      this.fInScanContent = true;
      if (this.fEntityScanner.skipChar(93, null)) {
        paramXMLStringBuffer.append(']');
        while (this.fEntityScanner.skipChar(93, null))
          paramXMLStringBuffer.append(']'); 
        if (this.fEntityScanner.skipChar(62, null))
          reportFatalError("CDEndInContent", null); 
      } 
      this.fInScanContent = false;
      i = -1;
    } 
    return i;
  }
  
  protected boolean scanAttributeValue(XMLString paramXMLString1, XMLString paramXMLString2, String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2) throws IOException, XNIException {
    int i = this.fEntityScanner.peekChar();
    if (i != 39 && i != 34)
      reportFatalError("OpenQuoteExpected", new Object[] { paramString2, paramString1 }); 
    this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
    int j = this.fEntityDepth;
    int k = this.fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
    int m = 0;
    if (k == i && (m = isUnchangedByNormalization(paramXMLString1)) == -1) {
      paramXMLString2.setValues(paramXMLString1);
      int i1 = this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
      if (i1 != i)
        reportFatalError("CloseQuoteExpected", new Object[] { paramString2, paramString1 }); 
      return true;
    } 
    this.fStringBuffer2.clear();
    this.fStringBuffer2.append(paramXMLString1);
    normalizeWhitespace(paramXMLString1, m);
    if (k != i) {
      this.fScanningAttribute = true;
      this.fStringBuffer.clear();
      do {
        this.fStringBuffer.append(paramXMLString1);
        if (k == 38) {
          this.fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE);
          if (j == this.fEntityDepth)
            this.fStringBuffer2.append('&'); 
          if (this.fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE)) {
            if (j == this.fEntityDepth)
              this.fStringBuffer2.append('#'); 
            int i1 = scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2);
            if (i1 != -1);
          } else {
            String str = this.fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
            if (str == null) {
              reportFatalError("NameRequiredInReference", null);
            } else if (j == this.fEntityDepth) {
              this.fStringBuffer2.append(str);
            } 
            if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
              reportFatalError("SemicolonRequiredInReference", new Object[] { str });
            } else if (j == this.fEntityDepth) {
              this.fStringBuffer2.append(';');
            } 
            if (resolveCharacter(str, this.fStringBuffer)) {
              checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
            } else if (this.fEntityManager.isExternalEntity(str)) {
              reportFatalError("ReferenceToExternalEntity", new Object[] { str });
            } else {
              if (!this.fEntityManager.isDeclaredEntity(str))
                if (paramBoolean1) {
                  if (this.fValidation)
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { str }, (short)1); 
                } else {
                  reportFatalError("EntityNotDeclared", new Object[] { str });
                }  
              this.fEntityManager.startEntity(true, str, true);
            } 
          } 
        } else if (k == 60) {
          reportFatalError("LessthanInAttValue", new Object[] { paramString2, paramString1 });
          this.fEntityScanner.scanChar(null);
          if (j == this.fEntityDepth)
            this.fStringBuffer2.append((char)k); 
        } else if (k == 37 || k == 93) {
          this.fEntityScanner.scanChar(null);
          this.fStringBuffer.append((char)k);
          if (j == this.fEntityDepth)
            this.fStringBuffer2.append((char)k); 
        } else if (k == 10 || k == 13 || k == 133 || k == 8232) {
          this.fEntityScanner.scanChar(null);
          this.fStringBuffer.append(' ');
          if (j == this.fEntityDepth)
            this.fStringBuffer2.append('\n'); 
        } else if (k != -1 && XMLChar.isHighSurrogate(k)) {
          this.fStringBuffer3.clear();
          if (scanSurrogates(this.fStringBuffer3)) {
            this.fStringBuffer.append(this.fStringBuffer3);
            if (j == this.fEntityDepth)
              this.fStringBuffer2.append(this.fStringBuffer3); 
          } 
        } else if (k != -1 && isInvalidLiteral(k)) {
          reportFatalError("InvalidCharInAttValue", new Object[] { paramString2, paramString1, Integer.toString(k, 16) });
          this.fEntityScanner.scanChar(null);
          if (j == this.fEntityDepth)
            this.fStringBuffer2.append((char)k); 
        } 
        k = this.fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
        if (j == this.fEntityDepth)
          this.fStringBuffer2.append(paramXMLString1); 
        normalizeWhitespace(paramXMLString1);
      } while (k != i || j != this.fEntityDepth);
      this.fStringBuffer.append(paramXMLString1);
      paramXMLString1.setValues(this.fStringBuffer);
      this.fScanningAttribute = false;
    } 
    paramXMLString2.setValues(this.fStringBuffer2);
    int n = this.fEntityScanner.scanChar(null);
    if (n != i)
      reportFatalError("CloseQuoteExpected", new Object[] { paramString2, paramString1 }); 
    return paramXMLString2.equals(paramXMLString1.ch, paramXMLString1.offset, paramXMLString1.length);
  }
  
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
  
  protected boolean isInvalid(int paramInt) { return XML11Char.isXML11Invalid(paramInt); }
  
  protected boolean isInvalidLiteral(int paramInt) { return !XML11Char.isXML11ValidLiteral(paramInt); }
  
  protected boolean isValidNameChar(int paramInt) { return XML11Char.isXML11Name(paramInt); }
  
  protected boolean isValidNameStartChar(int paramInt) { return XML11Char.isXML11NameStart(paramInt); }
  
  protected boolean isValidNCName(int paramInt) { return XML11Char.isXML11NCName(paramInt); }
  
  protected boolean isValidNameStartHighSurrogate(int paramInt) { return XML11Char.isXML11NameHighSurrogate(paramInt); }
  
  protected boolean versionSupported(String paramString) { return (paramString.equals("1.1") || paramString.equals("1.0")); }
  
  protected String getVersionNotSupportedKey() { return "VersionNotSupported11"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11DocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */