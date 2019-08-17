package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class DOM2Helper {
  public static String getLocalNameOfNode(Node paramNode) {
    String str = paramNode.getLocalName();
    return (null == str) ? getLocalNameOfNodeFallback(paramNode) : str;
  }
  
  private static String getLocalNameOfNodeFallback(Node paramNode) {
    String str = paramNode.getNodeName();
    int i = str.indexOf(':');
    return (i < 0) ? str : str.substring(i + 1);
  }
  
  public static String getNamespaceOfNode(Node paramNode) { return paramNode.getNamespaceURI(); }
  
  public static boolean isNodeAfter(Node paramNode1, Node paramNode2) {
    if (paramNode1 == paramNode2 || isNodeTheSame(paramNode1, paramNode2))
      return true; 
    boolean bool = true;
    Node node1 = getParentOfNode(paramNode1);
    Node node2 = getParentOfNode(paramNode2);
    if (node1 == node2 || isNodeTheSame(node1, node2)) {
      if (null != node1)
        bool = isNodeAfterSibling(node1, paramNode1, paramNode2); 
    } else {
      byte b1 = 2;
      byte b2 = 2;
      while (node1 != null) {
        b1++;
        node1 = getParentOfNode(node1);
      } 
      while (node2 != null) {
        b2++;
        node2 = getParentOfNode(node2);
      } 
      Node node3 = paramNode1;
      Node node4 = paramNode2;
      if (b1 < b2) {
        byte b3 = b2 - b1;
        for (byte b4 = 0; b4 < b3; b4++)
          node4 = getParentOfNode(node4); 
      } else if (b1 > b2) {
        byte b3 = b1 - b2;
        for (byte b4 = 0; b4 < b3; b4++)
          node3 = getParentOfNode(node3); 
      } 
      Node node5 = null;
      Node node6 = null;
      while (null != node3) {
        if (node3 == node4 || isNodeTheSame(node3, node4)) {
          if (null == node5) {
            bool = (b1 < b2);
            break;
          } 
          bool = isNodeAfterSibling(node3, node5, node6);
          break;
        } 
        node5 = node3;
        node3 = getParentOfNode(node3);
        node6 = node4;
        node4 = getParentOfNode(node4);
      } 
    } 
    return bool;
  }
  
  public static boolean isNodeTheSame(Node paramNode1, Node paramNode2) { return (paramNode1 instanceof DTMNodeProxy && paramNode2 instanceof DTMNodeProxy) ? ((DTMNodeProxy)paramNode1).equals((DTMNodeProxy)paramNode2) : ((paramNode1 == paramNode2) ? 1 : 0); }
  
  public static Node getParentOfNode(Node paramNode) {
    Node node = paramNode.getParentNode();
    if (node == null && 2 == paramNode.getNodeType())
      node = ((Attr)paramNode).getOwnerElement(); 
    return node;
  }
  
  private static boolean isNodeAfterSibling(Node paramNode1, Node paramNode2, Node paramNode3) {
    boolean bool = false;
    short s1 = paramNode2.getNodeType();
    short s2 = paramNode3.getNodeType();
    if (2 != s1 && 2 == s2) {
      bool = false;
    } else if (2 == s1 && 2 != s2) {
      bool = true;
    } else if (2 == s1) {
      NamedNodeMap namedNodeMap = paramNode1.getAttributes();
      int i = namedNodeMap.getLength();
      boolean bool1 = false;
      boolean bool2 = false;
      for (byte b = 0; b < i; b++) {
        Node node = namedNodeMap.item(b);
        if (paramNode2 == node || isNodeTheSame(paramNode2, node)) {
          if (bool2) {
            bool = false;
            break;
          } 
          bool1 = true;
        } else if (paramNode3 == node || isNodeTheSame(paramNode3, node)) {
          if (bool1) {
            bool = true;
            break;
          } 
          bool2 = true;
        } 
      } 
    } else {
      Node node = paramNode1.getFirstChild();
      boolean bool1 = false;
      boolean bool2 = false;
      while (null != node) {
        if (paramNode2 == node || isNodeTheSame(paramNode2, node)) {
          if (bool2) {
            bool = false;
            break;
          } 
          bool1 = true;
        } else if (paramNode3 == node || isNodeTheSame(paramNode3, node)) {
          if (bool1) {
            bool = true;
            break;
          } 
          bool2 = true;
        } 
        node = node.getNextSibling();
      } 
    } 
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\DOM2Helper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */