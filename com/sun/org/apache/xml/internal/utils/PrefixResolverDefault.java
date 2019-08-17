package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PrefixResolverDefault implements PrefixResolver {
  Node m_context;
  
  public PrefixResolverDefault(Node paramNode) { this.m_context = paramNode; }
  
  public String getNamespaceForPrefix(String paramString) { return getNamespaceForPrefix(paramString, this.m_context); }
  
  public String getNamespaceForPrefix(String paramString, Node paramNode) {
    Node node = paramNode;
    String str = null;
    if (paramString.equals("xml")) {
      str = "http://www.w3.org/XML/1998/namespace";
    } else {
      short s;
      while (null != node && null == str && ((s = node.getNodeType()) == 1 || s == 5)) {
        if (s == 1) {
          if (node.getNodeName().indexOf(paramString + ":") == 0)
            return node.getNamespaceURI(); 
          NamedNodeMap namedNodeMap = node.getAttributes();
          for (byte b = 0; b < namedNodeMap.getLength(); b++) {
            Node node1 = namedNodeMap.item(b);
            String str1 = node1.getNodeName();
            boolean bool = str1.startsWith("xmlns:");
            if (bool || str1.equals("xmlns")) {
              int i = str1.indexOf(':');
              String str2 = bool ? str1.substring(i + 1) : "";
              if (str2.equals(paramString)) {
                str = node1.getNodeValue();
                break;
              } 
            } 
          } 
        } 
        node = node.getParentNode();
      } 
    } 
    return str;
  }
  
  public String getBaseIdentifier() { return null; }
  
  public boolean handlesNullPrefixes() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\PrefixResolverDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */