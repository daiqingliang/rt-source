package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import java.io.OutputStream;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformXPath extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xpath-19991116";
  
  protected String engineGetURI() { return "http://www.w3.org/TR/1999/REC-xpath-19991116"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws TransformationException {
    try {
      Element element = XMLUtils.selectDsNode(paramTransform.getElement().getFirstChild(), "XPath", 0);
      if (element == null) {
        Object[] arrayOfObject = { "ds:XPath", "Transform" };
        throw new TransformationException("xml.WrongContent", arrayOfObject);
      } 
      Node node = element.getChildNodes().item(0);
      String str = XMLUtils.getStrFromNode(node);
      paramXMLSignatureInput.setNeedsToBeExpanded(needsCircumvent(str));
      if (node == null)
        throw new DOMException((short)3, "Text must be in ds:Xpath"); 
      XPathFactory xPathFactory = XPathFactory.newInstance();
      XPathAPI xPathAPI = xPathFactory.newXPathAPI();
      paramXMLSignatureInput.addNodeFilter(new XPathNodeFilter(element, node, str, xPathAPI));
      paramXMLSignatureInput.setNodeSet(true);
      return paramXMLSignatureInput;
    } catch (DOMException dOMException) {
      throw new TransformationException("empty", dOMException);
    } 
  }
  
  private boolean needsCircumvent(String paramString) { return (paramString.indexOf("namespace") != -1 || paramString.indexOf("name()") != -1); }
  
  static class XPathNodeFilter implements NodeFilter {
    XPathAPI xPathAPI;
    
    Node xpathnode;
    
    Element xpathElement;
    
    String str;
    
    XPathNodeFilter(Element param1Element, Node param1Node, String param1String, XPathAPI param1XPathAPI) {
      this.xpathnode = param1Node;
      this.str = param1String;
      this.xpathElement = param1Element;
      this.xPathAPI = param1XPathAPI;
    }
    
    public int isNodeInclude(Node param1Node) {
      try {
        boolean bool = this.xPathAPI.evaluate(param1Node, this.xpathnode, this.str, this.xpathElement);
        return bool ? 1 : 0;
      } catch (TransformerException transformerException) {
        Object[] arrayOfObject = { param1Node };
        throw new XMLSecurityRuntimeException("signature.Transform.node", arrayOfObject, transformerException);
      } catch (Exception exception) {
        Object[] arrayOfObject = { param1Node, Short.valueOf(param1Node.getNodeType()) };
        throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType", arrayOfObject, exception);
      } 
    }
    
    public int isNodeIncludeDO(Node param1Node, int param1Int) { return isNodeInclude(param1Node); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformXPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */