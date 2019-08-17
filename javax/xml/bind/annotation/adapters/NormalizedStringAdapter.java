package javax.xml.bind.annotation.adapters;

public final class NormalizedStringAdapter extends XmlAdapter<String, String> {
  public String unmarshal(String paramString) {
    if (paramString == null)
      return null; 
    int i;
    for (i = paramString.length() - 1; i >= 0 && !isWhiteSpaceExceptSpace(paramString.charAt(i)); i--);
    if (i < 0)
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[i--] = ' ';
    while (i >= 0) {
      if (isWhiteSpaceExceptSpace(arrayOfChar[i]))
        arrayOfChar[i] = ' '; 
      i--;
    } 
    return new String(arrayOfChar);
  }
  
  public String marshal(String paramString) { return paramString; }
  
  protected static boolean isWhiteSpaceExceptSpace(char paramChar) { return (paramChar >= ' ') ? false : ((paramChar == '\t' || paramChar == '\n' || paramChar == '\r')); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\adapters\NormalizedStringAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */