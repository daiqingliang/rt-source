package com.sun.xml.internal.bind.v2.model.util;

import com.sun.xml.internal.bind.v2.TODO;
import javax.xml.namespace.QName;

public class ArrayInfoUtil {
  public static QName calcArrayTypeName(QName paramQName) {
    String str;
    if (paramQName.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
      TODO.checkSpec("this URI");
      str = "http://jaxb.dev.java.net/array";
    } else {
      str = paramQName.getNamespaceURI();
    } 
    return new QName(str, paramQName.getLocalPart() + "Array");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\mode\\util\ArrayInfoUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */