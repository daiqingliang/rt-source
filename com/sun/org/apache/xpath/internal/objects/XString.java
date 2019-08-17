package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Locale;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XString extends XObject implements XMLString {
  static final long serialVersionUID = 2020470518395094525L;
  
  public static final XString EMPTYSTRING = new XString("");
  
  protected XString(Object paramObject) { super(paramObject); }
  
  public XString(String paramString) { super(paramString); }
  
  public int getType() { return 3; }
  
  public String getTypeString() { return "#STRING"; }
  
  public boolean hasString() { return true; }
  
  public double num() { return toDouble(); }
  
  public double toDouble() {
    XMLString xMLString = trim();
    double d = NaND;
    for (b = 0; b < xMLString.length(); b++) {
      char c = xMLString.charAt(b);
      if (c != '-' && c != '.' && (c < '0' || c > '9'))
        return d; 
    } 
    try {
      d = Double.parseDouble(xMLString.toString());
    } catch (NumberFormatException b) {
      NumberFormatException numberFormatException;
    } 
    return d;
  }
  
  public boolean bool() { return (str().length() > 0); }
  
  public XMLString xstr() { return this; }
  
  public String str() { return (null != this.m_obj) ? (String)this.m_obj : ""; }
  
  public int rtf(XPathContext paramXPathContext) {
    DTM dTM = paramXPathContext.createDocumentFragment();
    dTM.appendTextChild(str());
    return dTM.getDocument();
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler) throws SAXException {
    String str = str();
    paramContentHandler.characters(str.toCharArray(), 0, str.length());
  }
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler) throws SAXException {
    String str = str();
    paramLexicalHandler.comment(str.toCharArray(), 0, str.length());
  }
  
  public int length() { return str().length(); }
  
  public char charAt(int paramInt) { return str().charAt(paramInt); }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) { str().getChars(paramInt1, paramInt2, paramArrayOfChar, paramInt3); }
  
  public boolean equals(XObject paramXObject) {
    int i = paramXObject.getType();
    try {
      if (4 == i)
        return paramXObject.equals(this); 
      if (1 == i)
        return (paramXObject.bool() == bool()); 
      if (2 == i)
        return (paramXObject.num() == num()); 
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } 
    return xstr().equals(paramXObject.xstr());
  }
  
  public boolean equals(String paramString) { return str().equals(paramString); }
  
  public boolean equals(XMLString paramXMLString) { return (paramXMLString != null) ? (!paramXMLString.hasString() ? paramXMLString.equals(str()) : str().equals(paramXMLString.toString())) : 0; }
  
  public boolean equals(Object paramObject) { return (null == paramObject) ? false : ((paramObject instanceof XNodeSet) ? paramObject.equals(this) : ((paramObject instanceof XNumber) ? paramObject.equals(this) : str().equals(paramObject.toString()))); }
  
  public boolean equalsIgnoreCase(String paramString) { return str().equalsIgnoreCase(paramString); }
  
  public int compareTo(XMLString paramXMLString) {
    int i = length();
    int j = paramXMLString.length();
    int k = Math.min(i, j);
    byte b1 = 0;
    for (byte b2 = 0; k-- != 0; b2++) {
      char c1 = charAt(b1);
      char c2 = paramXMLString.charAt(b2);
      if (c1 != c2)
        return c1 - c2; 
      b1++;
    } 
    return i - j;
  }
  
  public int compareToIgnoreCase(XMLString paramXMLString) { throw new WrappedRuntimeException(new NoSuchMethodException("Java 1.2 method, not yet implemented")); }
  
  public boolean startsWith(String paramString, int paramInt) { return str().startsWith(paramString, paramInt); }
  
  public boolean startsWith(String paramString) { return startsWith(paramString, 0); }
  
  public boolean startsWith(XMLString paramXMLString, int paramInt) {
    int i = paramInt;
    int j = length();
    byte b = 0;
    int k = paramXMLString.length();
    if (paramInt < 0 || paramInt > j - k)
      return false; 
    while (--k >= 0) {
      if (charAt(i) != paramXMLString.charAt(b))
        return false; 
      i++;
      b++;
    } 
    return true;
  }
  
  public boolean startsWith(XMLString paramXMLString) { return startsWith(paramXMLString, 0); }
  
  public boolean endsWith(String paramString) { return str().endsWith(paramString); }
  
  public int hashCode() { return str().hashCode(); }
  
  public int indexOf(int paramInt) { return str().indexOf(paramInt); }
  
  public int indexOf(int paramInt1, int paramInt2) { return str().indexOf(paramInt1, paramInt2); }
  
  public int lastIndexOf(int paramInt) { return str().lastIndexOf(paramInt); }
  
  public int lastIndexOf(int paramInt1, int paramInt2) { return str().lastIndexOf(paramInt1, paramInt2); }
  
  public int indexOf(String paramString) { return str().indexOf(paramString); }
  
  public int indexOf(XMLString paramXMLString) { return str().indexOf(paramXMLString.toString()); }
  
  public int indexOf(String paramString, int paramInt) { return str().indexOf(paramString, paramInt); }
  
  public int lastIndexOf(String paramString) { return str().lastIndexOf(paramString); }
  
  public int lastIndexOf(String paramString, int paramInt) { return str().lastIndexOf(paramString, paramInt); }
  
  public XMLString substring(int paramInt) { return new XString(str().substring(paramInt)); }
  
  public XMLString substring(int paramInt1, int paramInt2) { return new XString(str().substring(paramInt1, paramInt2)); }
  
  public XMLString concat(String paramString) { return new XString(str().concat(paramString)); }
  
  public XMLString toLowerCase(Locale paramLocale) { return new XString(str().toLowerCase(paramLocale)); }
  
  public XMLString toLowerCase() { return new XString(str().toLowerCase()); }
  
  public XMLString toUpperCase(Locale paramLocale) { return new XString(str().toUpperCase(paramLocale)); }
  
  public XMLString toUpperCase() { return new XString(str().toUpperCase()); }
  
  public XMLString trim() { return new XString(str().trim()); }
  
  private static boolean isSpace(char paramChar) { return XMLCharacterRecognizer.isWhiteSpace(paramChar); }
  
  public XMLString fixWhiteSpace(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    int i = length();
    char[] arrayOfChar = new char[i];
    getChars(0, i, arrayOfChar, 0);
    boolean bool1 = false;
    byte b1;
    for (b1 = 0; b1 < i && !isSpace(arrayOfChar[b1]); b1++);
    byte b2 = b1;
    boolean bool2 = false;
    while (b1 < i) {
      char c = arrayOfChar[b1];
      if (isSpace(c)) {
        if (!bool2) {
          if (' ' != c)
            bool1 = true; 
          arrayOfChar[b2++] = ' ';
          if (paramBoolean3 && b1 != 0) {
            char c1 = arrayOfChar[b1 - 1];
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
        arrayOfChar[b2++] = c;
        bool2 = false;
      } 
      b1++;
    } 
    if (paramBoolean2 && 1 <= b2 && ' ' == arrayOfChar[b2 - 1]) {
      bool1 = true;
      b2--;
    } 
    byte b3 = 0;
    if (paramBoolean1 && 0 < b2 && ' ' == arrayOfChar[0]) {
      bool1 = true;
      b3++;
    } 
    XMLStringFactory xMLStringFactory = XMLStringFactoryImpl.getFactory();
    return bool1 ? xMLStringFactory.newstr(new String(arrayOfChar, b3, b2 - b3)) : this;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) { paramXPathVisitor.visitStringLiteral(paramExpressionOwner, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */