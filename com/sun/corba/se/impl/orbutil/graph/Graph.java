package com.sun.corba.se.impl.orbutil.graph;

import java.util.Set;

public interface Graph extends Set {
  NodeData getNodeData(Node paramNode);
  
  Set getRoots();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\graph\Graph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */