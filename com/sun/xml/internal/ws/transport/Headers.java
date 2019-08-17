package com.sun.xml.internal.ws.transport;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Headers extends TreeMap<String, List<String>> {
  private static final InsensitiveComparator INSTANCE = new InsensitiveComparator(null);
  
  public Headers() { super(INSTANCE); }
  
  public void add(String paramString1, String paramString2) {
    List list = (List)get(paramString1);
    if (list == null) {
      list = new LinkedList();
      put(paramString1, list);
    } 
    list.add(paramString2);
  }
  
  public String getFirst(String paramString) {
    List list = (List)get(paramString);
    return (list == null) ? null : (String)list.get(0);
  }
  
  public void set(String paramString1, String paramString2) {
    LinkedList linkedList = new LinkedList();
    linkedList.add(paramString2);
    put(paramString1, linkedList);
  }
  
  private static final class InsensitiveComparator extends Object implements Comparator<String>, Serializable {
    private InsensitiveComparator() {}
    
    public int compare(String param1String1, String param1String2) { return (param1String1 == null && param1String2 == null) ? 0 : ((param1String1 == null) ? -1 : ((param1String2 == null) ? 1 : param1String1.compareToIgnoreCase(param1String2))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\Headers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */