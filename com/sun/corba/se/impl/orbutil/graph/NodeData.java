package com.sun.corba.se.impl.orbutil.graph;

public class NodeData {
  private boolean visited;
  
  private boolean root;
  
  public NodeData() { clear(); }
  
  public void clear() {
    this.visited = false;
    this.root = true;
  }
  
  boolean isVisited() { return this.visited; }
  
  void visited() { this.visited = true; }
  
  boolean isRoot() { return this.root; }
  
  void notRoot() { this.root = false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\graph\NodeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */