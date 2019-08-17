package com.sun.org.apache.xml.internal.security.utils;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HelperNodeList implements NodeList {
  List<Node> nodes = new ArrayList();
  
  boolean allNodesMustHaveSameParent = false;
  
  public HelperNodeList() { this(false); }
  
  public HelperNodeList(boolean paramBoolean) { this.allNodesMustHaveSameParent = paramBoolean; }
  
  public Node item(int paramInt) { return (Node)this.nodes.get(paramInt); }
  
  public int getLength() { return this.nodes.size(); }
  
  public void appendChild(Node paramNode) throws IllegalArgumentException {
    if (this.allNodesMustHaveSameParent && getLength() > 0 && item(false).getParentNode() != paramNode.getParentNode())
      throw new IllegalArgumentException("Nodes have not the same Parent"); 
    this.nodes.add(paramNode);
  }
  
  public Document getOwnerDocument() { return (getLength() == 0) ? null : XMLUtils.getOwnerDocument(item(0)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\HelperNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */