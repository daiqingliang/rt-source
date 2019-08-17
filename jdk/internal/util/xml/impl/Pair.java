package jdk.internal.util.xml.impl;

public class Pair {
  public String name;
  
  public String value;
  
  public int num;
  
  public char[] chars;
  
  public int id;
  
  public Pair list;
  
  public Pair next;
  
  public String qname() { return new String(this.chars, 1, this.chars.length - 1); }
  
  public String local() { return (this.chars[0] != '\000') ? new String(this.chars, this.chars[0] + '\001', this.chars.length - this.chars[0] - 1) : new String(this.chars, 1, this.chars.length - 1); }
  
  public String pref() { return (this.chars[0] != '\000') ? new String(this.chars, 1, this.chars[0] - '\001') : ""; }
  
  public boolean eqpref(char[] paramArrayOfChar) {
    if (this.chars[0] == paramArrayOfChar[0]) {
      char c = this.chars[0];
      for (char c1 = '\001'; c1 < c; c1 = (char)(c1 + true)) {
        if (this.chars[c1] != paramArrayOfChar[c1])
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public boolean eqname(char[] paramArrayOfChar) {
    char c = (char)this.chars.length;
    if (c == paramArrayOfChar.length) {
      for (char c1 = Character.MIN_VALUE; c1 < c; c1 = (char)(c1 + true)) {
        if (this.chars[c1] != paramArrayOfChar[c1])
          return false; 
      } 
      return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */