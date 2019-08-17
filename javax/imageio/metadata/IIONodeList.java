package javax.imageio.metadata;

import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class IIONodeList implements NodeList {
  List nodes;
  
  public IIONodeList(List paramList) { this.nodes = paramList; }
  
  public int getLength() { return this.nodes.size(); }
  
  public Node item(int paramInt) { return (paramInt < 0 || paramInt > this.nodes.size()) ? null : (Node)this.nodes.get(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIONodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */