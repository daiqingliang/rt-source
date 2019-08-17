package com.sun.jndi.toolkit.dir;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidSearchFilterException;

public class SearchFilter implements AttrFilter {
  String filter;
  
  int pos;
  
  private StringFilter rootFilter;
  
  protected static final boolean debug = false;
  
  protected static final char BEGIN_FILTER_TOKEN = '(';
  
  protected static final char END_FILTER_TOKEN = ')';
  
  protected static final char AND_TOKEN = '&';
  
  protected static final char OR_TOKEN = '|';
  
  protected static final char NOT_TOKEN = '!';
  
  protected static final char EQUAL_TOKEN = '=';
  
  protected static final char APPROX_TOKEN = '~';
  
  protected static final char LESS_TOKEN = '<';
  
  protected static final char GREATER_TOKEN = '>';
  
  protected static final char EXTEND_TOKEN = ':';
  
  protected static final char WILDCARD_TOKEN = '*';
  
  static final int EQUAL_MATCH = 1;
  
  static final int APPROX_MATCH = 2;
  
  static final int GREATER_MATCH = 3;
  
  static final int LESS_MATCH = 4;
  
  public SearchFilter(String paramString) throws InvalidSearchFilterException {
    this.filter = paramString;
    this.pos = 0;
    normalizeFilter();
    this.rootFilter = createNextFilter();
  }
  
  public boolean check(Attributes paramAttributes) throws NamingException { return (paramAttributes == null) ? false : this.rootFilter.check(paramAttributes); }
  
  protected void normalizeFilter() {
    skipWhiteSpace();
    if (getCurrentChar() != '(')
      this.filter = '(' + this.filter + ')'; 
  }
  
  private void skipWhiteSpace() {
    while (Character.isWhitespace(getCurrentChar()))
      consumeChar(); 
  }
  
  protected StringFilter createNextFilter() throws InvalidSearchFilterException {
    AtomicFilter atomicFilter;
    skipWhiteSpace();
    try {
      CompoundFilter compoundFilter;
      NotFilter notFilter;
      if (getCurrentChar() != '(')
        throw new InvalidSearchFilterException("expected \"(\" at position " + this.pos); 
      consumeChar();
      skipWhiteSpace();
      switch (getCurrentChar()) {
        case '&':
          compoundFilter = new CompoundFilter(true);
          compoundFilter.parse();
          break;
        case '|':
          compoundFilter = new CompoundFilter(false);
          compoundFilter.parse();
          break;
        case '!':
          notFilter = new NotFilter();
          notFilter.parse();
          break;
        default:
          atomicFilter = new AtomicFilter();
          atomicFilter.parse();
          break;
      } 
      skipWhiteSpace();
      if (getCurrentChar() != ')')
        throw new InvalidSearchFilterException("expected \")\" at position " + this.pos); 
      consumeChar();
    } catch (InvalidSearchFilterException invalidSearchFilterException) {
      throw invalidSearchFilterException;
    } catch (Exception exception) {
      throw new InvalidSearchFilterException("Unable to parse character " + this.pos + " in \"" + this.filter + "\"");
    } 
    return atomicFilter;
  }
  
  protected char getCurrentChar() { return this.filter.charAt(this.pos); }
  
  protected char relCharAt(int paramInt) { return this.filter.charAt(this.pos + paramInt); }
  
  protected void consumeChar() { this.pos++; }
  
  protected void consumeChars(int paramInt) { this.pos += paramInt; }
  
  protected int relIndexOf(int paramInt) { return this.filter.indexOf(paramInt, this.pos) - this.pos; }
  
  protected String relSubstring(int paramInt1, int paramInt2) { return this.filter.substring(paramInt1 + this.pos, paramInt2 + this.pos); }
  
  public static String format(Attributes paramAttributes) throws NamingException {
    if (paramAttributes == null || paramAttributes.size() == 0)
      return "objectClass=*"; 
    null = "(& ";
    NamingEnumeration namingEnumeration = paramAttributes.getAll();
    while (namingEnumeration.hasMore()) {
      Attribute attribute = (Attribute)namingEnumeration.next();
      if (attribute.size() == 0 || (attribute.size() == 1 && attribute.get() == null)) {
        null = null + "(" + attribute.getID() + "=*)";
        continue;
      } 
      NamingEnumeration namingEnumeration1 = attribute.getAll();
      while (namingEnumeration1.hasMore()) {
        String str = getEncodedStringRep(namingEnumeration1.next());
        if (str != null)
          null = null + "(" + attribute.getID() + "=" + str + ")"; 
      } 
    } 
    return null + ")";
  }
  
  private static void hexDigit(StringBuffer paramStringBuffer, byte paramByte) {
    char c = (char)(paramByte >> 4 & 0xF);
    if (c > '\t') {
      c = (char)(c - '\n' + 'A');
    } else {
      c = (char)(c + '0');
    } 
    paramStringBuffer.append(c);
    c = (char)(paramByte & 0xF);
    if (c > '\t') {
      c = (char)(c - '\n' + 'A');
    } else {
      c = (char)(c + '0');
    } 
    paramStringBuffer.append(c);
  }
  
  private static String getEncodedStringRep(Object paramObject) throws NamingException {
    String str;
    if (paramObject == null)
      return null; 
    if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      StringBuffer stringBuffer1 = new StringBuffer(arrayOfByte.length * 3);
      for (byte b1 = 0; b1 < arrayOfByte.length; b1++) {
        stringBuffer1.append('\\');
        hexDigit(stringBuffer1, arrayOfByte[b1]);
      } 
      return stringBuffer1.toString();
    } 
    if (!(paramObject instanceof String)) {
      str = paramObject.toString();
    } else {
      str = (String)paramObject;
    } 
    int i = str.length();
    StringBuffer stringBuffer = new StringBuffer(i);
    for (byte b = 0; b < i; b++) {
      char c;
      switch (c = str.charAt(b)) {
        case '*':
          stringBuffer.append("\\2a");
          break;
        case '(':
          stringBuffer.append("\\28");
          break;
        case ')':
          stringBuffer.append("\\29");
          break;
        case '\\':
          stringBuffer.append("\\5c");
          break;
        case '\000':
          stringBuffer.append("\\00");
          break;
        default:
          stringBuffer.append(c);
          break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static int findUnescaped(char paramChar, String paramString, int paramInt) {
    int i = paramString.length();
    while (paramInt < i) {
      int j = paramString.indexOf(paramChar, paramInt);
      if (j == paramInt || j == -1 || paramString.charAt(j - 1) != '\\')
        return j; 
      paramInt = j + 1;
    } 
    return -1;
  }
  
  public static String format(String paramString, Object[] paramArrayOfObject) throws NamingException {
    int i = 0;
    int j = 0;
    StringBuffer stringBuffer = new StringBuffer(paramString.length());
    while ((i = findUnescaped('{', paramString, j)) >= 0) {
      int k;
      int m = i + 1;
      int n = paramString.indexOf('}', m);
      if (n < 0)
        throw new InvalidSearchFilterException("unbalanced {: " + paramString); 
      try {
        k = Integer.parseInt(paramString.substring(m, n));
      } catch (NumberFormatException numberFormatException) {
        throw new InvalidSearchFilterException("integer expected inside {}: " + paramString);
      } 
      if (k >= paramArrayOfObject.length)
        throw new InvalidSearchFilterException("number exceeds argument list: " + k); 
      stringBuffer.append(paramString.substring(j, i)).append(getEncodedStringRep(paramArrayOfObject[k]));
      j = n + 1;
    } 
    if (j < paramString.length())
      stringBuffer.append(paramString.substring(j)); 
    return stringBuffer.toString();
  }
  
  public static Attributes selectAttributes(Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    if (paramArrayOfString == null)
      return paramAttributes; 
    BasicAttributes basicAttributes = new BasicAttributes();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      Attribute attribute = paramAttributes.get(paramArrayOfString[b]);
      if (attribute != null)
        basicAttributes.put(attribute); 
    } 
    return basicAttributes;
  }
  
  final class AtomicFilter implements StringFilter {
    private String attrID;
    
    private String value;
    
    private int matchType;
    
    public void parse() {
      SearchFilter.this.skipWhiteSpace();
      try {
        int i = SearchFilter.this.relIndexOf(41);
        int j = SearchFilter.this.relIndexOf(61);
        char c = SearchFilter.this.relCharAt(j - 1);
        switch (c) {
          case '~':
            this.matchType = 2;
            this.attrID = SearchFilter.this.relSubstring(0, j - 1);
            this.value = SearchFilter.this.relSubstring(j + 1, i);
            break;
          case '>':
            this.matchType = 3;
            this.attrID = SearchFilter.this.relSubstring(0, j - 1);
            this.value = SearchFilter.this.relSubstring(j + 1, i);
            break;
          case '<':
            this.matchType = 4;
            this.attrID = SearchFilter.this.relSubstring(0, j - 1);
            this.value = SearchFilter.this.relSubstring(j + 1, i);
            break;
          case ':':
            throw new OperationNotSupportedException("Extensible match not supported");
          default:
            this.matchType = 1;
            this.attrID = SearchFilter.this.relSubstring(0, j);
            this.value = SearchFilter.this.relSubstring(j + 1, i);
            break;
        } 
        this.attrID = this.attrID.trim();
        this.value = this.value.trim();
        SearchFilter.this.consumeChars(i);
      } catch (Exception exception) {
        InvalidSearchFilterException invalidSearchFilterException = new InvalidSearchFilterException("Unable to parse character " + SearchFilter.this.pos + " in \"" + SearchFilter.this.filter + "\"");
        invalidSearchFilterException.setRootCause(exception);
        throw invalidSearchFilterException;
      } 
    }
    
    public boolean check(Attributes param1Attributes) throws NamingException {
      NamingEnumeration namingEnumeration;
      try {
        Attribute attribute = param1Attributes.get(this.attrID);
        if (attribute == null)
          return false; 
        namingEnumeration = attribute.getAll();
      } catch (NamingException namingException) {
        return false;
      } 
      while (namingEnumeration.hasMoreElements()) {
        String str = namingEnumeration.nextElement().toString();
        switch (this.matchType) {
          case 1:
          case 2:
            if (substringMatch(this.value, str))
              return true; 
          case 3:
            if (str.compareTo(this.value) >= 0)
              return true; 
          case 4:
            if (str.compareTo(this.value) <= 0)
              return true; 
        } 
      } 
      return false;
    }
    
    private boolean substringMatch(String param1String1, String param1String2) {
      if (param1String1.equals((new Character('*')).toString()))
        return true; 
      if (param1String1.indexOf('*') == -1)
        return param1String1.equalsIgnoreCase(param1String2); 
      int i = 0;
      StringTokenizer stringTokenizer = new StringTokenizer(param1String1, "*", false);
      if (param1String1.charAt(0) != '*' && !param1String2.toLowerCase(Locale.ENGLISH).startsWith(stringTokenizer.nextToken().toLowerCase(Locale.ENGLISH)))
        return false; 
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        i = param1String2.toLowerCase(Locale.ENGLISH).indexOf(str.toLowerCase(Locale.ENGLISH), i);
        if (i == -1)
          return false; 
        i += str.length();
      } 
      return !(param1String1.charAt(param1String1.length() - 1) != '*' && i != param1String2.length());
    }
  }
  
  final class CompoundFilter implements StringFilter {
    private Vector<SearchFilter.StringFilter> subFilters = new Vector();
    
    private boolean polarity;
    
    CompoundFilter(boolean param1Boolean) { this.polarity = param1Boolean; }
    
    public void parse() {
      SearchFilter.this.consumeChar();
      while (SearchFilter.this.getCurrentChar() != ')') {
        SearchFilter.StringFilter stringFilter = SearchFilter.this.createNextFilter();
        this.subFilters.addElement(stringFilter);
        SearchFilter.this.skipWhiteSpace();
      } 
    }
    
    public boolean check(Attributes param1Attributes) throws NamingException {
      for (byte b = 0; b < this.subFilters.size(); b++) {
        SearchFilter.StringFilter stringFilter = (SearchFilter.StringFilter)this.subFilters.elementAt(b);
        if (stringFilter.check(param1Attributes) != this.polarity)
          return !this.polarity; 
      } 
      return this.polarity;
    }
  }
  
  final class NotFilter implements StringFilter {
    private SearchFilter.StringFilter filter;
    
    public void parse() {
      SearchFilter.this.consumeChar();
      this.filter = SearchFilter.this.createNextFilter();
    }
    
    public boolean check(Attributes param1Attributes) throws NamingException { return !this.filter.check(param1Attributes); }
  }
  
  static interface StringFilter extends AttrFilter {
    void parse();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\dir\SearchFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */