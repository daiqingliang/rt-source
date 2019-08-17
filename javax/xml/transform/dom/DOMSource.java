package javax.xml.transform.dom;

import javax.xml.transform.Source;
import org.w3c.dom.Node;

public class DOMSource implements Source {
  private Node node;
  
  private String systemID;
  
  public static final String FEATURE = "http://javax.xml.transform.dom.DOMSource/feature";
  
  public DOMSource() {}
  
  public DOMSource(Node paramNode) { setNode(paramNode); }
  
  public DOMSource(Node paramNode, String paramString) {
    setNode(paramNode);
    setSystemId(paramString);
  }
  
  public void setNode(Node paramNode) { this.node = paramNode; }
  
  public Node getNode() { return this.node; }
  
  public void setSystemId(String paramString) { this.systemID = paramString; }
  
  public String getSystemId() { return this.systemID; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\dom\DOMSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */