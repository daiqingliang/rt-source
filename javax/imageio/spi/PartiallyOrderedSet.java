package javax.imageio.spi;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class PartiallyOrderedSet extends AbstractSet {
  private Map poNodes = new HashMap();
  
  private Set nodes = this.poNodes.keySet();
  
  public int size() { return this.nodes.size(); }
  
  public boolean contains(Object paramObject) { return this.nodes.contains(paramObject); }
  
  public Iterator iterator() { return new PartialOrderIterator(this.poNodes.values().iterator()); }
  
  public boolean add(Object paramObject) {
    if (this.nodes.contains(paramObject))
      return false; 
    DigraphNode digraphNode = new DigraphNode(paramObject);
    this.poNodes.put(paramObject, digraphNode);
    return true;
  }
  
  public boolean remove(Object paramObject) {
    DigraphNode digraphNode = (DigraphNode)this.poNodes.get(paramObject);
    if (digraphNode == null)
      return false; 
    this.poNodes.remove(paramObject);
    digraphNode.dispose();
    return true;
  }
  
  public void clear() { this.poNodes.clear(); }
  
  public boolean setOrdering(Object paramObject1, Object paramObject2) {
    DigraphNode digraphNode1 = (DigraphNode)this.poNodes.get(paramObject1);
    DigraphNode digraphNode2 = (DigraphNode)this.poNodes.get(paramObject2);
    digraphNode2.removeEdge(digraphNode1);
    return digraphNode1.addEdge(digraphNode2);
  }
  
  public boolean unsetOrdering(Object paramObject1, Object paramObject2) {
    DigraphNode digraphNode1 = (DigraphNode)this.poNodes.get(paramObject1);
    DigraphNode digraphNode2 = (DigraphNode)this.poNodes.get(paramObject2);
    return (digraphNode1.removeEdge(digraphNode2) || digraphNode2.removeEdge(digraphNode1));
  }
  
  public boolean hasOrdering(Object paramObject1, Object paramObject2) {
    DigraphNode digraphNode1 = (DigraphNode)this.poNodes.get(paramObject1);
    DigraphNode digraphNode2 = (DigraphNode)this.poNodes.get(paramObject2);
    return digraphNode1.hasEdge(digraphNode2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\PartiallyOrderedSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */