package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformEnvelopedSignature extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
  
  protected String engineGetURI() { return "http://www.w3.org/2000/09/xmldsig#enveloped-signature"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws TransformationException {
    Element element = paramTransform.getElement();
    Node node = searchSignatureElement(element);
    paramXMLSignatureInput.setExcludeNode(node);
    paramXMLSignatureInput.addNodeFilter(new EnvelopedNodeFilter(node));
    return paramXMLSignatureInput;
  }
  
  private static Node searchSignatureElement(Node paramNode) throws TransformationException {
    boolean bool = false;
    while (paramNode != null && paramNode.getNodeType() != 9) {
      Element element = (Element)paramNode;
      if (element.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && element.getLocalName().equals("Signature")) {
        bool = true;
        break;
      } 
      paramNode = paramNode.getParentNode();
    } 
    if (!bool)
      throw new TransformationException("transform.envelopedSignatureTransformNotInSignatureElement"); 
    return paramNode;
  }
  
  static class EnvelopedNodeFilter implements NodeFilter {
    Node exclude;
    
    EnvelopedNodeFilter(Node param1Node) { this.exclude = param1Node; }
    
    public int isNodeIncludeDO(Node param1Node, int param1Int) { return (param1Node == this.exclude) ? -1 : 1; }
    
    public int isNodeInclude(Node param1Node) { return (param1Node == this.exclude || XMLUtils.isDescendantOrSelf(this.exclude, param1Node)) ? -1 : 1; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformEnvelopedSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */