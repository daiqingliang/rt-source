package com.sun.xml.internal.fastinfoset.alphabet;

public final class BuiltInRestrictedAlphabets {
  public static final char[][] table = new char[2][];
  
  static  {
    table[0] = "0123456789-+.E ".toCharArray();
    table[1] = "0123456789-:TZ ".toCharArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\alphabet\BuiltInRestrictedAlphabets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */