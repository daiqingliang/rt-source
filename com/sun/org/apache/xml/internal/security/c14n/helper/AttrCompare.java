package com.sun.org.apache.xml.internal.security.c14n.helper;

import java.io.Serializable;
import java.util.Comparator;
import org.w3c.dom.Attr;

public class AttrCompare extends Object implements Comparator<Attr>, Serializable {
  private static final long serialVersionUID = -7113259629930576230L;
  
  private static final int ATTR0_BEFORE_ATTR1 = -1;
  
  private static final int ATTR1_BEFORE_ATTR0 = 1;
  
  private static final String XMLNS = "http://www.w3.org/2000/xmlns/";
  
  public int compare(Attr paramAttr1, Attr paramAttr2) {
    String str1 = paramAttr1.getNamespaceURI();
    String str2 = paramAttr2.getNamespaceURI();
    boolean bool1 = "http://www.w3.org/2000/xmlns/".equals(str1);
    boolean bool2 = "http://www.w3.org/2000/xmlns/".equals(str2);
    if (bool1) {
      if (bool2) {
        String str3 = paramAttr1.getLocalName();
        String str4 = paramAttr2.getLocalName();
        if ("xmlns".equals(str3))
          str3 = ""; 
        if ("xmlns".equals(str4))
          str4 = ""; 
        return str3.compareTo(str4);
      } 
      return -1;
    } 
    if (bool2)
      return 1; 
    if (str1 == null) {
      if (str2 == null) {
        String str3 = paramAttr1.getName();
        String str4 = paramAttr2.getName();
        return str3.compareTo(str4);
      } 
      return -1;
    } 
    if (str2 == null)
      return 1; 
    int i = str1.compareTo(str2);
    return (i != 0) ? i : paramAttr1.getLocalName().compareTo(paramAttr2.getLocalName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\helper\AttrCompare.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */