package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public interface StringList extends List {
  int getLength();
  
  boolean contains(String paramString);
  
  String item(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\StringList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */