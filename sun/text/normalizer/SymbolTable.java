package sun.text.normalizer;

import java.text.ParsePosition;

@Deprecated
public interface SymbolTable {
  @Deprecated
  public static final char SYMBOL_REF = '$';
  
  @Deprecated
  char[] lookup(String paramString);
  
  @Deprecated
  UnicodeMatcher lookupMatcher(int paramInt);
  
  @Deprecated
  String parseReference(String paramString, ParsePosition paramParsePosition, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */