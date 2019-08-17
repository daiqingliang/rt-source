package com.sun.org.apache.xml.internal.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Hashtree2Node {
  public static void appendHashToNode(Hashtable paramHashtable, String paramString, Node paramNode, Document paramDocument) {
    if (null == paramNode || null == paramDocument || null == paramHashtable)
      return; 
    String str = null;
    if (null == paramString || "".equals(paramString)) {
      str = "appendHashToNode";
    } else {
      str = paramString;
    } 
    try {
      Element element = paramDocument.createElement(str);
      paramNode.appendChild(element);
      Enumeration enumeration = paramHashtable.keys();
      Vector vector = new Vector();
      while (enumeration.hasMoreElements()) {
        Object object1 = enumeration.nextElement();
        String str1 = object1.toString();
        Object object2 = paramHashtable.get(object1);
        if (object2 instanceof Hashtable) {
          vector.addElement(str1);
          vector.addElement((Hashtable)object2);
          continue;
        } 
        try {
          Element element1 = paramDocument.createElement("item");
          element1.setAttribute("key", str1);
          element1.appendChild(paramDocument.createTextNode((String)object2));
          element.appendChild(element1);
        } catch (Exception exception) {
          Element element1 = paramDocument.createElement("item");
          element1.setAttribute("key", str1);
          element1.appendChild(paramDocument.createTextNode("ERROR: Reading " + object1 + " threw: " + exception.toString()));
          element.appendChild(element1);
        } 
      } 
      enumeration = vector.elements();
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        Hashtable hashtable = (Hashtable)enumeration.nextElement();
        appendHashToNode(hashtable, str1, element, paramDocument);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\Hashtree2Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */