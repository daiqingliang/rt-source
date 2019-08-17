package com.sun.org.apache.xerces.internal.xs;

import java.util.List;
import org.w3c.dom.ls.LSInput;

public interface LSInputList extends List {
  int getLength();
  
  LSInput item(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\LSInputList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */