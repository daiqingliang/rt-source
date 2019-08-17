package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface XPathEvaluator {
  XPathExpression createExpression(String paramString, XPathNSResolver paramXPathNSResolver) throws XPathException, DOMException;
  
  XPathNSResolver createNSResolver(Node paramNode);
  
  Object evaluate(String paramString, Node paramNode, XPathNSResolver paramXPathNSResolver, short paramShort, Object paramObject) throws XPathException, DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\xpath\XPathEvaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */