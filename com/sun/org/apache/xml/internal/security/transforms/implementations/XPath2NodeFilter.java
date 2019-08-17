package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XPath2NodeFilter implements NodeFilter {
  boolean hasUnionFilter;
  
  boolean hasSubtractFilter;
  
  boolean hasIntersectFilter;
  
  Set<Node> unionNodes;
  
  Set<Node> subtractNodes;
  
  Set<Node> intersectNodes;
  
  int inSubtract = -1;
  
  int inIntersect = -1;
  
  int inUnion = -1;
  
  XPath2NodeFilter(List<NodeList> paramList1, List<NodeList> paramList2, List<NodeList> paramList3) {
    this.hasUnionFilter = !paramList1.isEmpty();
    this.unionNodes = convertNodeListToSet(paramList1);
    this.hasSubtractFilter = !paramList2.isEmpty();
    this.subtractNodes = convertNodeListToSet(paramList2);
    this.hasIntersectFilter = !paramList3.isEmpty();
    this.intersectNodes = convertNodeListToSet(paramList3);
  }
  
  public int isNodeInclude(Node paramNode) {
    byte b = 1;
    if (this.hasSubtractFilter && rooted(paramNode, this.subtractNodes)) {
      b = -1;
    } else if (this.hasIntersectFilter && !rooted(paramNode, this.intersectNodes)) {
      b = 0;
    } 
    if (b == 1)
      return 1; 
    if (this.hasUnionFilter) {
      if (rooted(paramNode, this.unionNodes))
        return 1; 
      b = 0;
    } 
    return b;
  }
  
  public int isNodeIncludeDO(Node paramNode, int paramInt) {
    byte b = 1;
    if (this.hasSubtractFilter) {
      if (this.inSubtract == -1 || paramInt <= this.inSubtract)
        if (inList(paramNode, this.subtractNodes)) {
          this.inSubtract = paramInt;
        } else {
          this.inSubtract = -1;
        }  
      if (this.inSubtract != -1)
        b = -1; 
    } 
    if (b != -1 && this.hasIntersectFilter && (this.inIntersect == -1 || paramInt <= this.inIntersect))
      if (!inList(paramNode, this.intersectNodes)) {
        this.inIntersect = -1;
        b = 0;
      } else {
        this.inIntersect = paramInt;
      }  
    if (paramInt <= this.inUnion)
      this.inUnion = -1; 
    if (b == 1)
      return 1; 
    if (this.hasUnionFilter) {
      if (this.inUnion == -1 && inList(paramNode, this.unionNodes))
        this.inUnion = paramInt; 
      if (this.inUnion != -1)
        return 1; 
      b = 0;
    } 
    return b;
  }
  
  static boolean rooted(Node paramNode, Set<Node> paramSet) {
    if (paramSet.isEmpty())
      return false; 
    if (paramSet.contains(paramNode))
      return true; 
    for (Node node : paramSet) {
      if (XMLUtils.isDescendantOrSelf(node, paramNode))
        return true; 
    } 
    return false;
  }
  
  static boolean inList(Node paramNode, Set<Node> paramSet) { return paramSet.contains(paramNode); }
  
  private static Set<Node> convertNodeListToSet(List<NodeList> paramList) {
    HashSet hashSet = new HashSet();
    for (NodeList nodeList : paramList) {
      int i = nodeList.getLength();
      for (byte b = 0; b < i; b++) {
        Node node = nodeList.item(b);
        hashSet.add(node);
      } 
    } 
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\XPath2NodeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */