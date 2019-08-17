package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JAXPPrefixResolver implements PrefixResolver {
  private NamespaceContext namespaceContext;
  
  public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
  
  public JAXPPrefixResolver(NamespaceContext paramNamespaceContext) { this.namespaceContext = paramNamespaceContext; }
  
  public String getNamespaceForPrefix(String paramString) { return this.namespaceContext.getNamespaceURI(paramString); }
  
  public String getBaseIdentifier() { return null; }
  
  public boolean handlesNullPrefixes() { return false; }
  
  public String getNamespaceForPrefix(String paramString, Node paramNode) {
    Node node = paramNode;
    String str = null;
    if (paramString.equals("xml")) {
      str = "http://www.w3.org/XML/1998/namespace";
    } else {
      short s;
      while (null != node && null == str && ((s = node.getNodeType()) == 1 || s == 5)) {
        if (s == 1) {
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\JAXPPrefixResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */