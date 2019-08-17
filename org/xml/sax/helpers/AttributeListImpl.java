package org.xml.sax.helpers;

import java.util.Vector;
import org.xml.sax.AttributeList;

public class AttributeListImpl implements AttributeList {
  Vector names = new Vector();
  
  Vector types = new Vector();
  
  Vector values = new Vector();
  
  public AttributeListImpl() {}
  
  public AttributeListImpl(AttributeList paramAttributeList) { setAttributeList(paramAttributeList); }
  
  public void setAttributeList(AttributeList paramAttributeList) {
    int i = paramAttributeList.getLength();
    clear();
    for (byte b = 0; b < i; b++)
      addAttribute(paramAttributeList.getName(b), paramAttributeList.getType(b), paramAttributeList.getValue(b)); 
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3) {
    this.names.addElement(paramString1);
    this.types.addElement(paramString2);
    this.values.addElement(paramString3);
  }
  
  public void removeAttribute(String paramString) {
    int i = this.names.indexOf(paramString);
    if (i >= 0) {
      this.names.removeElementAt(i);
      this.types.removeElementAt(i);
      this.values.removeElementAt(i);
    } 
  }
  
  public void clear() {
    this.names.removeAllElements();
    this.types.removeAllElements();
    this.values.removeAllElements();
  }
  
  public int getLength() { return this.names.size(); }
  
  public String getName(int paramInt) {
    if (paramInt < 0)
      return null; 
    try {
      return (String)this.names.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
  
  public String getType(int paramInt) {
    if (paramInt < 0)
      return null; 
    try {
      return (String)this.types.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
  
  public String getValue(int paramInt) {
    if (paramInt < 0)
      return null; 
    try {
      return (String)this.values.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
  
  public String getType(String paramString) { return getType(this.names.indexOf(paramString)); }
  
  public String getValue(String paramString) { return getValue(this.names.indexOf(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\AttributeListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */