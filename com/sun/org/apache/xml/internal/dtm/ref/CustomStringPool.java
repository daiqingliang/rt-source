package com.sun.org.apache.xml.internal.dtm.ref;

import java.util.HashMap;
import java.util.Map;

public class CustomStringPool extends DTMStringPool {
  final Map<String, Integer> m_stringToInt = new HashMap();
  
  public static final int NULL = -1;
  
  public void removeAllElements() {
    this.m_intToString.removeAllElements();
    if (this.m_stringToInt != null)
      this.m_stringToInt.clear(); 
  }
  
  public String indexToString(int paramInt) throws ArrayIndexOutOfBoundsException { return (String)this.m_intToString.elementAt(paramInt); }
  
  public int stringToIndex(String paramString) {
    if (paramString == null)
      return -1; 
    Integer integer = (Integer)this.m_stringToInt.get(paramString);
    if (integer == null) {
      this.m_intToString.addElement(paramString);
      integer = Integer.valueOf(this.m_intToString.size());
      this.m_stringToInt.put(paramString, integer);
    } 
    return integer.intValue();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\CustomStringPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */