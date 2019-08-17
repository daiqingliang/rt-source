package javax.imageio.metadata;

import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class IIONamedNodeMap implements NamedNodeMap {
  List nodes;
  
  public IIONamedNodeMap(List paramList) { this.nodes = paramList; }
  
  public int getLength() { return this.nodes.size(); }
  
  public Node getNamedItem(String paramString) {
    for (Node node : this.nodes) {
      if (paramString.equals(node.getNodeName()))
        return node; 
    } 
    return null;
  }
  
  public Node item(int paramInt) { return (Node)this.nodes.get(paramInt); }
  
  public Node removeNamedItem(String paramString) { throw new DOMException((short)7, "This NamedNodeMap is read-only!"); }
  
  public Node setNamedItem(Node paramNode) { throw new DOMException((short)7, "This NamedNodeMap is read-only!"); }
  
  public Node getNamedItemNS(String paramString1, String paramString2) { return getNamedItem(paramString2); }
  
  public Node setNamedItemNS(Node paramNode) { return setNamedItem(paramNode); }
  
  public Node removeNamedItemNS(String paramString1, String paramString2) { return removeNamedItem(paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIONamedNodeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */