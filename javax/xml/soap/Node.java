package javax.xml.soap;

import org.w3c.dom.Node;

public interface Node extends Node {
  String getValue();
  
  void setValue(String paramString);
  
  void setParentElement(SOAPElement paramSOAPElement) throws SOAPException;
  
  SOAPElement getParentElement();
  
  void detachNode();
  
  void recycleNode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */