package org.w3c.dom.traversal;

import org.w3c.dom.Node;

public interface NodeIterator {
  Node getRoot();
  
  int getWhatToShow();
  
  NodeFilter getFilter();
  
  boolean getExpandEntityReferences();
  
  Node nextNode();
  
  Node previousNode();
  
  void detach();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\traversal\NodeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */