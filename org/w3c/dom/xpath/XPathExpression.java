package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface XPathExpression {
  Object evaluate(Node paramNode, short paramShort, Object paramObject) throws XPathException, DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\xpath\XPathExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */