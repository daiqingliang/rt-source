package com.sun.org.apache.xml.internal.security.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOMNamespaceContext implements NamespaceContext {
  private Map<String, String> namespaceMap = new HashMap();
  
  public DOMNamespaceContext(Node paramNode) { addNamespaces(paramNode); }
  
  public String getNamespaceURI(String paramString) { return (String)this.namespaceMap.get(paramString); }
  
  public String getPrefix(String paramString) {
    for (String str1 : this.namespaceMap.keySet()) {
      String str2 = (String)this.namespaceMap.get(str1);
      if (str2.equals(paramString))
        return str1; 
    } 
    return null;
  }
  
  public Iterator<String> getPrefixes(String paramString) { return this.namespaceMap.keySet().iterator(); }
  
  private void addNamespaces(Node paramNode) {
    if (paramNode.getParentNode() != null)
      addNamespaces(paramNode.getParentNode()); 
    if (paramNode instanceof Element) {
      Element element = (Element)paramNode;
      NamedNodeMap namedNodeMap = element.getAttributes();
      for (byte b = 0; b < namedNodeMap.getLength(); b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        if ("xmlns".equals(attr.getPrefix()))
          this.namespaceMap.put(attr.getLocalName(), attr.getValue()); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\DOMNamespaceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */