package com.sun.xml.internal.ws.encoding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceException;

final class ParameterList {
  private final Map<String, String> list;
  
  ParameterList(String paramString) {
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
          throw new WebServiceException(); 
        String str = token.getValue().toLowerCase();
        token = headerTokenizer.next();
        if ((char)token.getType() != '=')
          throw new WebServiceException(); 
        token = headerTokenizer.next();
        i = token.getType();
        if (i != -1 && i != -2)
          throw new WebServiceException(); 
        this.list.put(str, token.getValue());
        continue;
      } 
      break;
    } 
    throw new WebServiceException();
  }
  
  int size() { return this.list.size(); }
  
  String get(String paramString) { return (String)this.list.get(paramString.trim().toLowerCase()); }
  
  Iterator<String> getNames() { return this.list.keySet().iterator(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\ParameterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */