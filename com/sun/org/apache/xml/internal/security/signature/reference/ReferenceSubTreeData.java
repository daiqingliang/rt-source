package com.sun.org.apache.xml.internal.security.signature.reference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ReferenceSubTreeData implements ReferenceNodeSetData {
  private boolean excludeComments;
  
  private Node root;
  
  public ReferenceSubTreeData(Node paramNode, boolean paramBoolean) {
    this.root = paramNode;
    this.excludeComments = paramBoolean;
  }
  
  public Iterator<Node> iterator() { return new DelayedNodeIterator(this.root, this.excludeComments); }
  
  public Node getRoot() { return this.root; }
  
  public boolean excludeComments() { return this.excludeComments; }
  
  static class DelayedNodeIterator extends Object implements Iterator<Node> {
    private Node root;
    
    private List<Node> nodeSet;
    
    private ListIterator<Node> li;
    
    private boolean withComments;
    
    DelayedNodeIterator(Node param1Node, boolean param1Boolean) {
      this.root = param1Node;
      this.withComments = !param1Boolean;
    }
    
    public boolean hasNext() {
      if (this.nodeSet == null) {
        this.nodeSet = dereferenceSameDocumentURI(this.root);
        this.li = this.nodeSet.listIterator();
      } 
      return this.li.hasNext();
    }
    
    public Node next() {
      if (this.nodeSet == null) {
        this.nodeSet = dereferenceSameDocumentURI(this.root);
        this.li = this.nodeSet.listIterator();
      } 
      if (this.li.hasNext())
        return (Node)this.li.next(); 
      throw new NoSuchElementException();
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    private List<Node> dereferenceSameDocumentURI(Node param1Node) {
      ArrayList arrayList = new ArrayList();
      if (param1Node != null)
        nodeSetMinusCommentNodes(param1Node, arrayList, null); 
      return arrayList;
    }
    
    private void nodeSetMinusCommentNodes(Node param1Node1, List<Node> param1List, Node param1Node2) {
      Node node2;
      Node node1;
      NamedNodeMap namedNodeMap;
      switch (param1Node1.getNodeType()) {
        case 1:
          param1List.add(param1Node1);
          namedNodeMap = param1Node1.getAttributes();
          if (namedNodeMap != null) {
            byte b = 0;
            int i = namedNodeMap.getLength();
            while (b < i) {
              param1List.add(namedNodeMap.item(b));
              b++;
            } 
          } 
          node1 = null;
          for (node2 = param1Node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            nodeSetMinusCommentNodes(node2, param1List, node1);
            node1 = node2;
          } 
          break;
        case 9:
          node1 = null;
          for (node2 = param1Node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            nodeSetMinusCommentNodes(node2, param1List, node1);
            node1 = node2;
          } 
          break;
        case 3:
        case 4:
          if (param1Node2 != null && (param1Node2.getNodeType() == 3 || param1Node2.getNodeType() == 4))
            return; 
          param1List.add(param1Node1);
          break;
        case 7:
          param1List.add(param1Node1);
          break;
        case 8:
          if (this.withComments)
            param1List.add(param1Node1); 
          break;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\reference\ReferenceSubTreeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */