package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import java.util.Vector;

public abstract class NodeCounter {
  public static final int END = -1;
  
  protected int _node = -1;
  
  protected int _nodeType = -1;
  
  protected double _value = -2.147483648E9D;
  
  public final DOM _document;
  
  public final DTMAxisIterator _iterator;
  
  public final Translet _translet;
  
  protected String _format;
  
  protected String _lang;
  
  protected String _letterValue;
  
  protected String _groupSep;
  
  protected int _groupSize;
  
  private boolean _separFirst = true;
  
  private boolean _separLast = false;
  
  private Vector _separToks = new Vector();
  
  private Vector _formatToks = new Vector();
  
  private int _nSepars = 0;
  
  private int _nFormats = 0;
  
  private static final String[] Thousands = { "", "m", "mm", "mmm" };
  
  private static final String[] Hundreds = { "", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm" };
  
  private static final String[] Tens = { "", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc" };
  
  private static final String[] Ones = { "", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix" };
  
  private StringBuilder _tempBuffer = new StringBuilder();
  
  protected boolean _hasFrom;
  
  protected NodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) {
    this._translet = paramTranslet;
    this._document = paramDOM;
    this._iterator = paramDTMAxisIterator;
  }
  
  protected NodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean) {
    this._translet = paramTranslet;
    this._document = paramDOM;
    this._iterator = paramDTMAxisIterator;
    this._hasFrom = paramBoolean;
  }
  
  public abstract NodeCounter setStartNode(int paramInt);
  
  public NodeCounter setValue(double paramDouble) {
    this._value = paramDouble;
    return this;
  }
  
  protected void setFormatting(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    this._lang = paramString2;
    this._groupSep = paramString4;
    this._letterValue = paramString3;
    this._groupSize = parseStringToAnInt(paramString5);
    setTokens(paramString1);
  }
  
  private int parseStringToAnInt(String paramString) {
    if (paramString == null)
      return 0; 
    int i = 0;
    boolean bool = false;
    int j = 10;
    byte b = 0;
    int k = paramString.length();
    if (k > 0) {
      int m;
      if (paramString.charAt(0) == '-') {
        bool = true;
        m = Integer.MIN_VALUE;
        b++;
      } else {
        m = -2147483647;
      } 
      int n = m / j;
      if (b < k) {
        int i1 = Character.digit(paramString.charAt(b++), j);
        if (i1 < 0)
          return 0; 
        i = -i1;
      } 
      while (b < k) {
        int i1 = Character.digit(paramString.charAt(b++), j);
        if (i1 < 0)
          return 0; 
        if (i < n)
          return 0; 
        i *= j;
        if (i < m + i1)
          return 0; 
        i -= i1;
      } 
    } else {
      return 0;
    } 
    return bool ? ((b > 1) ? i : 0) : -i;
  }
  
  private final void setTokens(String paramString) {
    if (this._format != null && paramString.equals(this._format))
      return; 
    this._format = paramString;
    int i = this._format.length();
    boolean bool = true;
    this._separFirst = true;
    this._separLast = false;
    this._nSepars = 0;
    this._nFormats = 0;
    this._separToks.clear();
    this._formatToks.clear();
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < i) {
      char c = paramString.charAt(b2);
      b1 = b2;
      while (Character.isLetterOrDigit(c) && ++b2 != i)
        c = paramString.charAt(b2); 
      if (b2 > b1) {
        if (bool) {
          this._separToks.addElement(".");
          bool = this._separFirst = false;
        } 
        this._formatToks.addElement(paramString.substring(b1, b2));
      } 
      if (b2 == i)
        break; 
      c = paramString.charAt(b2);
      b1 = b2;
      while (!Character.isLetterOrDigit(c) && ++b2 != i) {
        c = paramString.charAt(b2);
        bool = false;
      } 
      if (b2 > b1)
        this._separToks.addElement(paramString.substring(b1, b2)); 
    } 
    this._nSepars = this._separToks.size();
    this._nFormats = this._formatToks.size();
    if (this._nSepars > this._nFormats)
      this._separLast = true; 
    if (this._separFirst)
      this._nSepars--; 
    if (this._separLast)
      this._nSepars--; 
    if (this._nSepars == 0) {
      this._separToks.insertElementAt(".", 1);
      this._nSepars++;
    } 
    if (this._separFirst)
      this._nSepars++; 
  }
  
  public NodeCounter setDefaultFormatting() {
    setFormatting("1", "en", "alphabetic", null, null);
    return this;
  }
  
  public abstract String getCounter();
  
  public String getCounter(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    setFormatting(paramString1, paramString2, paramString3, paramString4, paramString5);
    return getCounter();
  }
  
  public boolean matchesCount(int paramInt) { return (this._nodeType == this._document.getExpandedTypeID(paramInt)); }
  
  public boolean matchesFrom(int paramInt) { return false; }
  
  protected String formatNumbers(int paramInt) { return formatNumbers(new int[] { paramInt }); }
  
  protected String formatNumbers(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    boolean bool = true;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      if (paramArrayOfInt[b1] != Integer.MIN_VALUE)
        bool = false; 
    } 
    if (bool)
      return ""; 
    b1 = 1;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 1;
    this._tempBuffer.setLength(0);
    StringBuilder stringBuilder = this._tempBuffer;
    if (this._separFirst)
      stringBuilder.append((String)this._separToks.elementAt(0)); 
    while (b3 < i) {
      int j = paramArrayOfInt[b3];
      if (j != Integer.MIN_VALUE) {
        if (b1 == 0)
          stringBuilder.append((String)this._separToks.elementAt(b4++)); 
        formatValue(j, (String)this._formatToks.elementAt(b2++), stringBuilder);
        if (b2 == this._nFormats)
          b2--; 
        if (b4 >= this._nSepars)
          b4--; 
        b1 = 0;
      } 
      b3++;
    } 
    if (this._separLast)
      stringBuilder.append((String)this._separToks.lastElement()); 
    return stringBuilder.toString();
  }
  
  private void formatValue(int paramInt, String paramString, StringBuilder paramStringBuilder) {
    char c = paramString.charAt(0);
    if (Character.isDigit(c)) {
      char c1 = (char)(c - Character.getNumericValue(c));
      StringBuilder stringBuilder = paramStringBuilder;
      if (this._groupSize > 0)
        stringBuilder = new StringBuilder(); 
      String str = "";
      int i;
      for (i = paramInt; i > 0; i /= 10)
        str = (char)(c1 + i % 10) + str; 
      int j;
      for (j = 0; j < paramString.length() - str.length(); j++)
        stringBuilder.append(c1); 
      stringBuilder.append(str);
      if (this._groupSize > 0)
        for (j = 0; j < stringBuilder.length(); j++) {
          if (j != 0 && (stringBuilder.length() - j) % this._groupSize == 0)
            paramStringBuilder.append(this._groupSep); 
          paramStringBuilder.append(stringBuilder.charAt(j));
        }  
    } else if (c == 'i' && !this._letterValue.equals("alphabetic")) {
      paramStringBuilder.append(romanValue(paramInt));
    } else if (c == 'I' && !this._letterValue.equals("alphabetic")) {
      paramStringBuilder.append(romanValue(paramInt).toUpperCase());
    } else {
      char c1 = c;
      char c2 = c;
      if (c >= 'α' && c <= 'ω') {
        c2 = 'ω';
      } else {
        while (Character.isLetterOrDigit((char)(c2 + '\001')))
          c2++; 
      } 
      paramStringBuilder.append(alphaValue(paramInt, c1, c2));
    } 
  }
  
  private String alphaValue(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 <= 0)
      return "" + paramInt1; 
    int i = paramInt3 - paramInt2 + 1;
    char c = (char)((paramInt1 - 1) % i + paramInt2);
    return (paramInt1 > i) ? (alphaValue((paramInt1 - 1) / i, paramInt2, paramInt3) + c) : ("" + c);
  }
  
  private String romanValue(int paramInt) { return (paramInt <= 0 || paramInt > 4000) ? ("" + paramInt) : (Thousands[paramInt / 1000] + Hundreds[paramInt / 100 % 10] + Tens[paramInt / 10 % 10] + Ones[paramInt % 10]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */