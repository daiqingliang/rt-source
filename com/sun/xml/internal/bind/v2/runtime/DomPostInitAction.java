package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class DomPostInitAction implements Runnable {
  private final Node node;
  
  private final XMLSerializer serializer;
  
  DomPostInitAction(Node paramNode, XMLSerializer paramXMLSerializer) {
    this.node = paramNode;
    this.serializer = paramXMLSerializer;
  }
  
  public void run() {
    HashSet hashSet = new HashSet();
    for (Node node1 = this.node; node1 != null && node1.getNodeType() == 1; node1 = node1.getParentNode()) {
      NamedNodeMap namedNodeMap = node1.getAttributes();
      if (namedNodeMap != null)
        for (byte b = 0; b < namedNodeMap.getLength(); b++) {
          Attr attr = (Attr)namedNodeMap.item(b);
          String str = attr.getNamespaceURI();
          if (str != null && str.equals("http://www.w3.org/2000/xmlns/")) {
            String str1 = attr.getLocalName();
            if (str1 != null) {
              if (str1.equals("xmlns"))
                str1 = ""; 
              String str2 = attr.getValue();
              if (str2 != null && hashSet.add(str1))
                this.serializer.addInscopeBinding(str2, str1); 
            } 
          } 
        }  
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\DomPostInitAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */