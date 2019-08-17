package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;

class XPathExpressionImpl implements XPathExpression {
  private final XPath m_xpath;
  
  private final Document m_doc;
  
  XPathExpressionImpl(XPath paramXPath, Document paramDocument) {
    this.m_xpath = paramXPath;
    this.m_doc = paramDocument;
  }
  
  public Object evaluate(Node paramNode, short paramShort, Object paramObject) throws XPathException, DOMException {
    if (this.m_doc != null) {
      if (paramNode != this.m_doc && !paramNode.getOwnerDocument().equals(this.m_doc)) {
        String str = XPATHMessages.createXPATHMessage("ER_WRONG_DOCUMENT", null);
        throw new DOMException((short)4, str);
      } 
      short s = paramNode.getNodeType();
      if (s != 9 && s != 1 && s != 2 && s != 3 && s != 4 && s != 8 && s != 7 && s != 13) {
        String str = XPATHMessages.createXPATHMessage("ER_WRONG_NODETYPE", null);
        throw new DOMException((short)9, str);
      } 
    } 
    if (!XPathResultImpl.isValidType(paramShort)) {
      String str = XPATHMessages.createXPATHMessage("ER_INVALID_XPATH_TYPE", new Object[] { new Integer(paramShort) });
      throw new XPathException((short)2, str);
    } 
    XPathContext xPathContext = new XPathContext();
    if (null != this.m_doc)
      xPathContext.getDTMHandleFromNode(this.m_doc); 
    XObject xObject = null;
    try {
      xObject = this.m_xpath.execute(xPathContext, paramNode, null);
    } catch (TransformerException transformerException) {
      throw new XPathException((short)1, transformerException.getMessageAndLocation());
    } 
    return new XPathResultImpl(paramShort, xObject, paramNode, this.m_xpath);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathExpressionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */