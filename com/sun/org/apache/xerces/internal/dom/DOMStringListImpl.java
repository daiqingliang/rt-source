package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMStringList;

public class DOMStringListImpl implements DOMStringList {
  private Vector fStrings = new Vector();
  
  public DOMStringListImpl() {}
  
  public DOMStringListImpl(Vector paramVector) {}
  
  public String item(int paramInt) {
    try {
      return (String)this.fStrings.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
  
  public int getLength() { return this.fStrings.size(); }
  
  public boolean contains(String paramString) { return this.fStrings.contains(paramString); }
  
  public void add(String paramString) { this.fStrings.add(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMStringListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */