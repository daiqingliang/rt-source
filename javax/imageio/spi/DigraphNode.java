package javax.imageio.spi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class DigraphNode implements Cloneable, Serializable {
  protected Object data;
  
  protected Set outNodes = new HashSet();
  
  protected int inDegree = 0;
  
  private Set inNodes = new HashSet();
  
  public DigraphNode(Object paramObject) { this.data = paramObject; }
  
  public Object getData() { return this.data; }
  
  public Iterator getOutNodes() { return this.outNodes.iterator(); }
  
  public boolean addEdge(DigraphNode paramDigraphNode) {
    if (this.outNodes.contains(paramDigraphNode))
      return false; 
    this.outNodes.add(paramDigraphNode);
    paramDigraphNode.inNodes.add(this);
    paramDigraphNode.incrementInDegree();
    return true;
  }
  
  public boolean hasEdge(DigraphNode paramDigraphNode) { return this.outNodes.contains(paramDigraphNode); }
  
  public boolean removeEdge(DigraphNode paramDigraphNode) {
    if (!this.outNodes.contains(paramDigraphNode))
      return false; 
    this.outNodes.remove(paramDigraphNode);
    paramDigraphNode.inNodes.remove(this);
    paramDigraphNode.decrementInDegree();
    return true;
  }
  
  public void dispose() {
    Object[] arrayOfObject1 = this.inNodes.toArray();
    for (byte b1 = 0; b1 < arrayOfObject1.length; b1++) {
      DigraphNode digraphNode = (DigraphNode)arrayOfObject1[b1];
      digraphNode.removeEdge(this);
    } 
    Object[] arrayOfObject2 = this.outNodes.toArray();
    for (byte b2 = 0; b2 < arrayOfObject2.length; b2++) {
      DigraphNode digraphNode = (DigraphNode)arrayOfObject2[b2];
      removeEdge(digraphNode);
    } 
  }
  
  public int getInDegree() { return this.inDegree; }
  
  private void incrementInDegree() { this.inDegree++; }
  
  private void decrementInDegree() { this.inDegree--; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\DigraphNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */