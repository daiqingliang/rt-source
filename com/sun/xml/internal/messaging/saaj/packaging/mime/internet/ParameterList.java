package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ParameterList {
  private final HashMap list;
  
  public ParameterList() { this.list = new HashMap(); }
  
  private ParameterList(HashMap paramHashMap) { this.list = paramHashMap; }
  
  public ParameterList(String paramString) throws ParseException {
    HeaderTokenizer headerTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    this.list = new HashMap();
    while (true) {
      HeaderTokenizer.Token token = headerTokenizer.next();
      int i = token.getType();
      if (i == -4)
        return; 
      if ((char)i == ';') {
        token = headerTokenizer.next();
        if (token.getType() == -4)
          return; 
        if (token.getType() != -1)
          throw new ParseException(); 
        String str = token.getValue().toLowerCase();
        token = headerTokenizer.next();
        if ((char)token.getType() != '=')
          throw new ParseException(); 
        token = headerTokenizer.next();
        i = token.getType();
        if (i != -1 && i != -2)
          throw new ParseException(); 
        this.list.put(str, token.getValue());
        continue;
      } 
      break;
    } 
    throw new ParseException();
  }
  
  public int size() { return this.list.size(); }
  
  public String get(String paramString) { return (String)this.list.get(paramString.trim().toLowerCase()); }
  
  public void set(String paramString1, String paramString2) { this.list.put(paramString1.trim().toLowerCase(), paramString2); }
  
  public void remove(String paramString) throws ParseException { this.list.remove(paramString.trim().toLowerCase()); }
  
  public Iterator getNames() { return this.list.keySet().iterator(); }
  
  public String toString() { return toString(0); }
  
  public String toString(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    for (Map.Entry entry : this.list.entrySet()) {
      String str1 = (String)entry.getKey();
      String str2 = quote((String)entry.getValue());
      stringBuffer.append("; ");
      paramInt += 2;
      int i = str1.length() + str2.length() + 1;
      if (paramInt + i > 76) {
        stringBuffer.append("\r\n\t");
        paramInt = 8;
      } 
      stringBuffer.append(str1).append('=');
      paramInt += str1.length() + 1;
      if (paramInt + str2.length() > 76) {
        String str = MimeUtility.fold(paramInt, str2);
        stringBuffer.append(str);
        int j = str.lastIndexOf('\n');
        if (j >= 0) {
          paramInt += str.length() - j - 1;
          continue;
        } 
        paramInt += str.length();
        continue;
      } 
      stringBuffer.append(str2);
      paramInt += str2.length();
    } 
    return stringBuffer.toString();
  }
  
  private String quote(String paramString) { return "".equals(paramString) ? "\"\"" : MimeUtility.quote(paramString, "()<>@,;:\\\"\t []/?="); }
  
  public ParameterList copy() { return new ParameterList((HashMap)this.list.clone()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\ParameterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */