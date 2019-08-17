package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.Node;

public interface PrefixResolver {
  String getNamespaceForPrefix(String paramString);
  
  String getNamespaceForPrefix(String paramString, Node paramNode);
  
  String getBaseIdentifier();
  
  boolean handlesNullPrefixes();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\PrefixResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */