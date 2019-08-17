package javax.xml.crypto.dsig.dom;

import java.security.Key;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.w3c.dom.Node;

public class DOMValidateContext extends DOMCryptoContext implements XMLValidateContext {
  private Node node;
  
  public DOMValidateContext(KeySelector paramKeySelector, Node paramNode) {
    if (paramKeySelector == null)
      throw new NullPointerException("key selector is null"); 
    init(paramNode, paramKeySelector);
  }
  
  public DOMValidateContext(Key paramKey, Node paramNode) {
    if (paramKey == null)
      throw new NullPointerException("validatingKey is null"); 
    init(paramNode, KeySelector.singletonKeySelector(paramKey));
  }
  
  private void init(Node paramNode, KeySelector paramKeySelector) {
    if (paramNode == null)
      throw new NullPointerException("node is null"); 
    this.node = paramNode;
    setKeySelector(paramKeySelector);
    if (System.getSecurityManager() != null)
      setProperty("org.jcp.xml.dsig.secureValidation", Boolean.TRUE); 
  }
  
  public void setNode(Node paramNode) {
    if (paramNode == null)
      throw new NullPointerException(); 
    this.node = paramNode;
  }
  
  public Node getNode() { return this.node; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\dom\DOMValidateContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */