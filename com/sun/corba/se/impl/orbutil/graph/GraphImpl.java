package com.sun.corba.se.impl.orbutil.graph;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GraphImpl extends AbstractSet implements Graph {
  private Map nodeToData = new HashMap();
  
  public GraphImpl() {}
  
  public GraphImpl(Collection paramCollection) {
    this();
    addAll(paramCollection);
  }
  
  public boolean add(Object paramObject) {
    if (!(paramObject instanceof Node))
      throw new IllegalArgumentException("Graphs must contain only Node instances"); 
    Node node = (Node)paramObject;
    boolean bool = this.nodeToData.keySet().contains(paramObject);
    if (!bool) {
      NodeData nodeData = new NodeData();
      this.nodeToData.put(node, nodeData);
    } 
    return !bool;
  }
  
  public Iterator iterator() { return this.nodeToData.keySet().iterator(); }
  
  public int size() { return this.nodeToData.keySet().size(); }
  
  public NodeData getNodeData(Node paramNode) { return (NodeData)this.nodeToData.get(paramNode); }
  
  private void clearNodeData() {
    for (Map.Entry entry : this.nodeToData.entrySet()) {
      NodeData nodeData = (NodeData)entry.getValue();
      nodeData.clear();
    } 
  }
  
  void visitAll(NodeVisitor paramNodeVisitor) {
    boolean bool = false;
    do {
      bool = true;
      Entry[] arrayOfEntry = (Entry[])this.nodeToData.entrySet().toArray(new Map.Entry[0]);
      for (byte b = 0; b < arrayOfEntry.length; b++) {
        Entry entry = arrayOfEntry[b];
        Node node = (Node)entry.getKey();
        NodeData nodeData = (NodeData)entry.getValue();
        if (!nodeData.isVisited()) {
          nodeData.visited();
          bool = false;
          paramNodeVisitor.visit(this, node, nodeData);
        } 
      } 
    } while (!bool);
  }
  
  private void markNonRoots() { visitAll(new NodeVisitor() {
          public void visit(Graph param1Graph, Node param1Node, NodeData param1NodeData) {
            for (Node node : param1Node.getChildren()) {
              param1Graph.add(node);
              NodeData nodeData = param1Graph.getNodeData(node);
              nodeData.notRoot();
            } 
          }
        }); }
  
  private Set collectRootSet() {
    HashSet hashSet = new HashSet();
    for (Map.Entry entry : this.nodeToData.entrySet()) {
      Node node = (Node)entry.getKey();
      NodeData nodeData = (NodeData)entry.getValue();
      if (nodeData.isRoot())
        hashSet.add(node); 
    } 
    return hashSet;
  }
  
  public Set getRoots() {
    clearNodeData();
    markNonRoots();
    return collectRootSet();
  }
  
  static interface NodeVisitor {
    void visit(Graph param1Graph, Node param1Node, NodeData param1NodeData);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\graph\GraphImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */