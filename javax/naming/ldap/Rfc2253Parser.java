package javax.naming.ldap;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InvalidNameException;

final class Rfc2253Parser {
  private final String name;
  
  private final char[] chars;
  
  private final int len;
  
  private int cur = 0;
  
  Rfc2253Parser(String paramString) {
    this.name = paramString;
    this.len = paramString.length();
    this.chars = paramString.toCharArray();
  }
  
  List<Rdn> parseDn() throws InvalidNameException {
    this.cur = 0;
    ArrayList arrayList = new ArrayList(this.len / 3 + 10);
    if (this.len == 0)
      return arrayList; 
    arrayList.add(doParse(new Rdn()));
    while (this.cur < this.len) {
      if (this.chars[this.cur] == ',' || this.chars[this.cur] == ';') {
        this.cur++;
        arrayList.add(0, doParse(new Rdn()));
        continue;
      } 
      throw new InvalidNameException("Invalid name: " + this.name);
    } 
    return arrayList;
  }
  
  Rdn parseRdn() throws InvalidNameException { return parseRdn(new Rdn()); }
  
  Rdn parseRdn(Rdn paramRdn) throws InvalidNameException {
    paramRdn = doParse(paramRdn);
    if (this.cur < this.len)
      throw new InvalidNameException("Invalid RDN: " + this.name); 
    return paramRdn;
  }
  
  private Rdn doParse(Rdn paramRdn) throws InvalidNameException {
    while (this.cur < this.len) {
      consumeWhitespace();
      String str1 = parseAttrType();
      consumeWhitespace();
      if (this.cur >= this.len || this.chars[this.cur] != '=')
        throw new InvalidNameException("Invalid name: " + this.name); 
      this.cur++;
      consumeWhitespace();
      String str2 = parseAttrValue();
      consumeWhitespace();
      paramRdn.put(str1, Rdn.unescapeValue(str2));
      if (this.cur >= this.len || this.chars[this.cur] != '+')
        break; 
      this.cur++;
    } 
    paramRdn.sort();
    return paramRdn;
  }
  
  private String parseAttrType() throws InvalidNameException {
    int i = this.cur;
    while (this.cur < this.len) {
      char c = this.chars[this.cur];
      if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == ' ')
        this.cur++; 
    } 
    while (this.cur > i && this.chars[this.cur - 1] == ' ')
      this.cur--; 
    if (i == this.cur)
      throw new InvalidNameException("Invalid name: " + this.name); 
    return new String(this.chars, i, this.cur - i);
  }
  
  private String parseAttrValue() throws InvalidNameException { return (this.cur < this.len && this.chars[this.cur] == '#') ? parseBinaryAttrValue() : ((this.cur < this.len && this.chars[this.cur] == '"') ? parseQuotedAttrValue() : parseStringAttrValue()); }
  
  private String parseBinaryAttrValue() throws InvalidNameException {
    int i = this.cur;
    this.cur++;
    while (this.cur < this.len && Character.isLetterOrDigit(this.chars[this.cur]))
      this.cur++; 
    return new String(this.chars, i, this.cur - i);
  }
  
  private String parseQuotedAttrValue() throws InvalidNameException {
    int i = this.cur;
    this.cur++;
    while (this.cur < this.len && this.chars[this.cur] != '"') {
      if (this.chars[this.cur] == '\\')
        this.cur++; 
      this.cur++;
    } 
    if (this.cur >= this.len)
      throw new InvalidNameException("Invalid name: " + this.name); 
    this.cur++;
    return new String(this.chars, i, this.cur - i);
  }
  
  private String parseStringAttrValue() throws InvalidNameException {
    int i = this.cur;
    int j = -1;
    while (this.cur < this.len && !atTerminator()) {
      if (this.chars[this.cur] == '\\')
        j = ++this.cur; 
      this.cur++;
    } 
    if (this.cur > this.len)
      throw new InvalidNameException("Invalid name: " + this.name); 
    int k;
    for (k = this.cur; k > i && isWhitespace(this.chars[k - 1]) && j != k - 1; k--);
    return new String(this.chars, i, k - i);
  }
  
  private void consumeWhitespace() {
    while (this.cur < this.len && isWhitespace(this.chars[this.cur]))
      this.cur++; 
  }
  
  private boolean atTerminator() { return (this.cur < this.len && (this.chars[this.cur] == ',' || this.chars[this.cur] == ';' || this.chars[this.cur] == '+')); }
  
  private static boolean isWhitespace(char paramChar) { return (paramChar == ' ' || paramChar == '\r'); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\Rfc2253Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */