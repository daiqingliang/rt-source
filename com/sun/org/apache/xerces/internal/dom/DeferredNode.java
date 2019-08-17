package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public interface DeferredNode extends Node {
  public static final short TYPE_NODE = 20;
  
  int getNodeIndex();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */