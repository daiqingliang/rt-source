package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface DocumentTraversal {
  NodeIterator createNodeIterator(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean) throws DOMException;
  
  TreeWalker createTreeWalker(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\traversal\DocumentTraversal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */