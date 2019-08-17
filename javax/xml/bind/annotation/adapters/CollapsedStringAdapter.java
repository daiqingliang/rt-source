package javax.xml.bind.annotation.adapters;

public class CollapsedStringAdapter extends XmlAdapter<String, String> {
  public String unmarshal(String paramString) {
    if (paramString == null)
      return null; 
    int i = paramString.length();
    byte b1;
    for (b1 = 0; b1 < i && !isWhiteSpace(paramString.charAt(b1)); b1++);
    if (b1 == i)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder(i);
    if (b1 != 0) {
      for (byte b = 0; b < b1; b++)
        stringBuilder.append(paramString.charAt(b)); 
      stringBuilder.append(' ');
    } 
    boolean bool = true;
    for (byte b2 = b1 + 1; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      boolean bool1 = isWhiteSpace(c);
      if (!bool || !bool1) {
        bool = bool1;
        if (bool) {
          stringBuilder.append(' ');
        } else {
          stringBuilder.append(c);
        } 
      } 
    } 
    i = stringBuilder.length();
    if (i > 0 && stringBuilder.charAt(i - 1) == ' ')
      stringBuilder.setLength(i - 1); 
    return stringBuilder.toString();
  }
  
  public String marshal(String paramString) { return paramString; }
  
  protected static boolean isWhiteSpace(char paramChar) { return (paramChar > ' ') ? false : ((paramChar == '\t' || paramChar == '\n' || paramChar == '\r' || paramChar == ' ')); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\adapters\CollapsedStringAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */