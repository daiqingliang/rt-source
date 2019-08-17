package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.util.List;

public interface ObjectList extends List {
  int getLength();
  
  boolean contains(Object paramObject);
  
  Object item(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\ObjectList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */