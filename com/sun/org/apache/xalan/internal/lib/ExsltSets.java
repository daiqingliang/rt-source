package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xml.internal.utils.DOM2Helper;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExsltSets extends ExsltBase {
  public static NodeList leading(NodeList paramNodeList1, NodeList paramNodeList2) {
    if (paramNodeList2.getLength() == 0)
      return paramNodeList1; 
    NodeSet nodeSet1 = new NodeSet(paramNodeList1);
    NodeSet nodeSet2 = new NodeSet();
    Node node = paramNodeList2.item(0);
    if (!nodeSet1.contains(node))
      return nodeSet2; 
    for (byte b = 0; b < paramNodeList1.getLength(); b++) {
      Node node1 = paramNodeList1.item(b);
      if (DOM2Helper.isNodeAfter(node1, node) && !DOM2Helper.isNodeTheSame(node1, node))
        nodeSet2.addElement(node1); 
    } 
    return nodeSet2;
  }
  
  public static NodeList trailing(NodeList paramNodeList1, NodeList paramNodeList2) {
    if (paramNodeList2.getLength() == 0)
      return paramNodeList1; 
    NodeSet nodeSet1 = new NodeSet(paramNodeList1);
    NodeSet nodeSet2 = new NodeSet();
    Node node = paramNodeList2.item(0);
    if (!nodeSet1.contains(node))
      return nodeSet2; 
    for (byte b = 0; b < paramNodeList1.getLength(); b++) {
      Node node1 = paramNodeList1.item(b);
      if (DOM2Helper.isNodeAfter(node, node1) && !DOM2Helper.isNodeTheSame(node, node1))
        nodeSet2.addElement(node1); 
    } 
    return nodeSet2;
  }
  
  public static NodeList intersection(NodeList paramNodeList1, NodeList paramNodeList2) {
    NodeSet nodeSet1 = new NodeSet(paramNodeList1);
    NodeSet nodeSet2 = new NodeSet(paramNodeList2);
    NodeSet nodeSet3 = new NodeSet();
    nodeSet3.setShouldCacheNodes(true);
    for (byte b = 0; b < nodeSet1.getLength(); b++) {
      Node node = nodeSet1.elementAt(b);
      if (nodeSet2.contains(node))
        nodeSet3.addElement(node); 
    } 
    return nodeSet3;
  }
  
  public static NodeList difference(NodeList paramNodeList1, NodeList paramNodeList2) {
    NodeSet nodeSet1 = new NodeSet(paramNodeList1);
    NodeSet nodeSet2 = new NodeSet(paramNodeList2);
    NodeSet nodeSet3 = new NodeSet();
    nodeSet3.setShouldCacheNodes(true);
    for (byte b = 0; b < nodeSet1.getLength(); b++) {
      Node node = nodeSet1.elementAt(b);
      if (!nodeSet2.contains(node))
        nodeSet3.addElement(node); 
    } 
    return nodeSet3;
  }
  
  public static NodeList distinct(NodeList paramNodeList) {
    NodeSet nodeSet = new NodeSet();
    nodeSet.setShouldCacheNodes(true);
    HashMap hashMap = new HashMap();
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      Node node = paramNodeList.item(b);
      String str = toString(node);
      if (str == null) {
        nodeSet.addElement(node);
      } else if (!hashMap.containsKey(str)) {
        hashMap.put(str, node);
        nodeSet.addElement(node);
      } 
    } 
    return nodeSet;
  }
  
  public static boolean hasSameNode(NodeList paramNodeList1, NodeList paramNodeList2) {
    NodeSet nodeSet1 = new NodeSet(paramNodeList1);
    NodeSet nodeSet2 = new NodeSet(paramNodeList2);
    for (byte b = 0; b < nodeSet1.getLength(); b++) {
      if (nodeSet2.contains(nodeSet1.elementAt(b)))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltSets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */