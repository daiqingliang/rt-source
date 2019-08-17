package javax.xml.crypto.dom;

import javax.xml.crypto.XMLStructure;
import org.w3c.dom.Node;

public class DOMStructure implements XMLStructure {
  private final Node node;
  
  public DOMStructure(Node paramNode) {
    if (paramNode == null)
      throw new NullPointerException("node cannot be null"); 
    this.node = paramNode;
  }
  
  public Node getNode() { return this.node; }
  
  public boolean isFeatureSupported(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dom\DOMStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */