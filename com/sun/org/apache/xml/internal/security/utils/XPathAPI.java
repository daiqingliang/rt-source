package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XPathAPI {
  NodeList selectNodeList(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException;
  
  boolean evaluate(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException;
  
  void clear();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\XPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */