package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class ToTextStream extends ToStream {
  protected void startDocumentInternal() {
    super.startDocumentInternal();
    this.m_needToCallStartDocument = false;
  }
  
  public void endDocument() {
    flushPending();
    flushWriter();
    if (this.m_tracer != null)
      fireEndDoc(); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.m_tracer != null) {
      fireStartElem(paramString3);
      firePseudoAttributes();
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_tracer != null)
      fireEndElem(paramString3); 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    flushPending();
    try {
      if (inTemporaryOutputState()) {
        this.m_writer.write(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, this.m_lineSepUse);
      } 
      if (this.m_tracer != null)
        fireCharEvent(paramArrayOfChar, paramInt1, paramInt2); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void charactersRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, this.m_lineSepUse);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  void writeNormalizedChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException, SAXException {
    String str = getEncoding();
    Writer writer = this.m_writer;
    int i = paramInt1 + paramInt2;
    byte b = 10;
    for (int j = paramInt1; j < i; j++) {
      char c = paramArrayOfChar[j];
      if ('\n' == c && paramBoolean) {
        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
      } else if (this.m_encodingInfo.isInEncoding(c)) {
        writer.write(c);
      } else if (Encodings.isHighUTF16Surrogate(c)) {
        int k = writeUTF16Surrogate(c, paramArrayOfChar, j, i);
        if (k != 0) {
          String str1 = Integer.toString(k);
          String str2 = Utils.messages.createMessage("ER_ILLEGAL_CHARACTER", new Object[] { str1, str });
          System.err.println(str2);
        } 
        j++;
      } else if (str != null) {
        writer.write(38);
        writer.write(35);
        writer.write(Integer.toString(c));
        writer.write(59);
        String str1 = Integer.toString(c);
        String str2 = Utils.messages.createMessage("ER_ILLEGAL_CHARACTER", new Object[] { str1, str });
        System.err.println(str2);
      } else {
        writer.write(c);
      } 
    } 
  }
  
  public void cdata(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, this.m_lineSepUse);
      if (this.m_tracer != null)
        fireCDATAEvent(paramArrayOfChar, paramInt1, paramInt2); 
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, this.m_lineSepUse);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    flushPending();
    if (this.m_tracer != null)
      fireEscapingEvent(paramString1, paramString2); 
  }
  
  public void comment(String paramString) throws SAXException {
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    comment(this.m_charsBuff, 0, i);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    flushPending();
    if (this.m_tracer != null)
      fireCommentEvent(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void entityReference(String paramString) throws SAXException {
    if (this.m_tracer != null)
      fireEntityReference(paramString); 
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) {}
  
  public void endCDATA() {}
  
  public void endElement(String paramString) throws SAXException {
    if (this.m_tracer != null)
      fireEndElem(paramString); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_needToCallStartDocument)
      startDocumentInternal(); 
    if (this.m_tracer != null) {
      fireStartElem(paramString3);
      firePseudoAttributes();
    } 
  }
  
  public void characters(String paramString) throws SAXException {
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    characters(this.m_charsBuff, 0, i);
  }
  
  public void addAttribute(String paramString1, String paramString2) throws SAXException {}
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException {}
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean) throws SAXException { return false; }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {}
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) throws SAXException {}
  
  public void flushPending() {
    if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToTextStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */