package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XStringForFSB extends XString {
  static final long serialVersionUID = -1533039186550674548L;
  
  int m_start;
  
  int m_length;
  
  protected String m_strCache = null;
  
  protected int m_hash = 0;
  
  public XStringForFSB(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2) {
    super(paramFastStringBuffer);
    this.m_start = paramInt1;
    this.m_length = paramInt2;
    if (null == paramFastStringBuffer)
      throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null)); 
  }
  
  private XStringForFSB(String paramString) {
    super(paramString);
    throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FSB_CANNOT_TAKE_STRING", null));
  }
  
  public FastStringBuffer fsb() { return (FastStringBuffer)this.m_obj; }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer) { paramFastStringBuffer.append(str()); }
  
  public boolean hasString() { return (null != this.m_strCache); }
  
  public Object object() { return str(); }
  
  public String str() {
    if (null == this.m_strCache)
      this.m_strCache = fsb().getString(this.m_start, this.m_length); 
    return this.m_strCache;
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler) throws SAXException { fsb().sendSAXcharacters(paramContentHandler, this.m_start, this.m_length); }
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler) throws SAXException { fsb().sendSAXComment(paramLexicalHandler, this.m_start, this.m_length); }
  
  public int length() { return this.m_length; }
  
  public char charAt(int paramInt) { return fsb().charAt(this.m_start + paramInt); }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    int i = paramInt2 - paramInt1;
    if (i > this.m_length)
      i = this.m_length; 
    if (i > paramArrayOfChar.length - paramInt3)
      i = paramArrayOfChar.length - paramInt3; 
    int j = paramInt1 + this.m_start + i;
    int k = paramInt3;
    FastStringBuffer fastStringBuffer = fsb();
    for (int m = paramInt1 + this.m_start; m < j; m++)
      paramArrayOfChar[k++] = fastStringBuffer.charAt(m); 
  }
  
  public boolean equals(XMLString paramXMLString) {
    if (this == paramXMLString)
      return true; 
    int i = this.m_length;
    if (i == paramXMLString.length()) {
      FastStringBuffer fastStringBuffer = fsb();
      int j = this.m_start;
      for (byte b = 0; i-- != 0; b++) {
        if (fastStringBuffer.charAt(j) != paramXMLString.charAt(b))
          return false; 
        j++;
      } 
      return true;
    } 
    return false;
  }
  
  public boolean equals(XObject paramXObject) {
    if (this == paramXObject)
      return true; 
    if (paramXObject.getType() == 2)
      return paramXObject.equals(this); 
    String str = paramXObject.str();
    int i = this.m_length;
    if (i == str.length()) {
      FastStringBuffer fastStringBuffer = fsb();
      int j = this.m_start;
      for (byte b = 0; i-- != 0; b++) {
        if (fastStringBuffer.charAt(j) != str.charAt(b))
          return false; 
        j++;
      } 
      return true;
    } 
    return false;
  }
  
  public boolean equals(String paramString) {
    int i = this.m_length;
    if (i == paramString.length()) {
      FastStringBuffer fastStringBuffer = fsb();
      int j = this.m_start;
      for (byte b = 0; i-- != 0; b++) {
        if (fastStringBuffer.charAt(j) != paramString.charAt(b))
          return false; 
        j++;
      } 
      return true;
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) { return (null == paramObject) ? false : ((paramObject instanceof XNumber) ? paramObject.equals(this) : ((paramObject instanceof XNodeSet) ? paramObject.equals(this) : ((paramObject instanceof XStringForFSB) ? equals((XMLString)paramObject) : equals(paramObject.toString())))); }
  
  public boolean equalsIgnoreCase(String paramString) { return (this.m_length == paramString.length()) ? str().equalsIgnoreCase(paramString) : 0; }
  
  public int compareTo(XMLString paramXMLString) {
    int i = this.m_length;
    int j = paramXMLString.length();
    int k = Math.min(i, j);
    FastStringBuffer fastStringBuffer = fsb();
    int m = this.m_start;
    for (byte b = 0; k-- != 0; b++) {
      char c1 = fastStringBuffer.charAt(m);
      char c2 = paramXMLString.charAt(b);
      if (c1 != c2)
        return c1 - c2; 
      m++;
    } 
    return i - j;
  }
  
  public int compareToIgnoreCase(XMLString paramXMLString) {
    int i = this.m_length;
    int j = paramXMLString.length();
    int k = Math.min(i, j);
    FastStringBuffer fastStringBuffer = fsb();
    int m = this.m_start;
    for (byte b = 0; k-- != 0; b++) {
      char c1 = Character.toLowerCase(fastStringBuffer.charAt(m));
      char c2 = Character.toLowerCase(paramXMLString.charAt(b));
      if (c1 != c2)
        return c1 - c2; 
      m++;
    } 
    return i - j;
  }
  
  public int hashCode() { return super.hashCode(); }
  
  public boolean startsWith(XMLString paramXMLString, int paramInt) {
    FastStringBuffer fastStringBuffer = fsb();
    int i = this.m_start + paramInt;
    int j = this.m_start + this.m_length;
    byte b = 0;
    int k = paramXMLString.length();
    if (paramInt < 0 || paramInt > this.m_length - k)
      return false; 
    while (--k >= 0) {
      if (fastStringBuffer.charAt(i) != paramXMLString.charAt(b))
        return false; 
      i++;
      b++;
    } 
    return true;
  }
  
  public boolean startsWith(XMLString paramXMLString) { return startsWith(paramXMLString, 0); }
  
  public int indexOf(int paramInt) { return indexOf(paramInt, 0); }
  
  public int indexOf(int paramInt1, int paramInt2) {
    int i = this.m_start + this.m_length;
    FastStringBuffer fastStringBuffer = fsb();
    if (paramInt2 < 0) {
      paramInt2 = 0;
    } else if (paramInt2 >= this.m_length) {
      return -1;
    } 
    for (int j = this.m_start + paramInt2; j < i; j++) {
      if (fastStringBuffer.charAt(j) == paramInt1)
        return j - this.m_start; 
    } 
    return -1;
  }
  
  public XMLString substring(int paramInt) {
    int i = this.m_length - paramInt;
    if (i <= 0)
      return XString.EMPTYSTRING; 
    int j = this.m_start + paramInt;
    return new XStringForFSB(fsb(), j, i);
  }
  
  public XMLString substring(int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i > this.m_length)
      i = this.m_length; 
    if (i <= 0)
      return XString.EMPTYSTRING; 
    int j = this.m_start + paramInt1;
    return new XStringForFSB(fsb(), j, i);
  }
  
  public XMLString concat(String paramString) { return new XString(str().concat(paramString)); }
  
  public XMLString trim() { return fixWhiteSpace(true, true, false); }
  
  private static boolean isSpace(char paramChar) { return XMLCharacterRecognizer.isWhiteSpace(paramChar); }
  
  public XMLString fixWhiteSpace(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    int i = this.m_length + this.m_start;
    char[] arrayOfChar = new char[this.m_length];
    FastStringBuffer fastStringBuffer = fsb();
    boolean bool1 = false;
    int j = 0;
    boolean bool2 = false;
    int k;
    for (k = this.m_start; k < i; k++) {
      char c = fastStringBuffer.charAt(k);
      if (isSpace(c)) {
        if (!bool2) {
          if (' ' != c)
            bool1 = true; 
          arrayOfChar[j++] = ' ';
          if (paramBoolean3 && j != 0) {
            char c1 = arrayOfChar[j - 1];
            if (c1 != '.' && c1 != '!' && c1 != '?')
              bool2 = true; 
          } else {
            bool2 = true;
          } 
        } else {
          bool1 = true;
          bool2 = true;
        } 
      } else {
        arrayOfChar[j++] = c;
        bool2 = false;
      } 
    } 
    if (paramBoolean2 && 1 <= j && ' ' == arrayOfChar[j - 1]) {
      bool1 = true;
      j--;
    } 
    k = 0;
    if (paramBoolean1 && 0 < j && ' ' == arrayOfChar[0]) {
      bool1 = true;
      k++;
    } 
    XMLStringFactory xMLStringFactory = XMLStringFactoryImpl.getFactory();
    return bool1 ? xMLStringFactory.newstr(arrayOfChar, k, j - k) : this;
  }
  
  public double toDouble() {
    if (this.m_length == 0)
      return NaND; 
    String str = fsb().getString(this.m_start, this.m_length);
    byte b;
    for (b = 0; b < this.m_length && XMLCharacterRecognizer.isWhiteSpace(str.charAt(b)); b++);
    if (b == this.m_length)
      return NaND; 
    if (str.charAt(b) == '-')
      b++; 
    while (b < this.m_length) {
      char c = str.charAt(b);
      if (c != '.' && (c < '0' || c > '9'))
        break; 
      b++;
    } 
    while (b < this.m_length && XMLCharacterRecognizer.isWhiteSpace(str.charAt(b)))
      b++; 
    if (b != this.m_length)
      return NaND; 
    try {
      return Double.parseDouble(str);
    } catch (NumberFormatException numberFormatException) {
      return NaND;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XStringForFSB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */