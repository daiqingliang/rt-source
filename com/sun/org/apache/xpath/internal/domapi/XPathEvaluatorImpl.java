package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;

public final class XPathEvaluatorImpl implements XPathEvaluator {
  private final Document m_doc;
  
  public XPathEvaluatorImpl(Document paramDocument) { this.m_doc = paramDocument; }
  
  public XPathEvaluatorImpl() { this.m_doc = null; }
  
  public XPathExpression createExpression(String paramString, XPathNSResolver paramXPathNSResolver) throws XPathException, DOMException {
    try {
      XPath xPath = new XPath(paramString, null, (null == paramXPathNSResolver) ? new DummyPrefixResolver(this) : (PrefixResolver)paramXPathNSResolver, 0);
      return new XPathExpressionImpl(xPath, this.m_doc);
    } catch (TransformerException transformerException) {
      if (transformerException instanceof XPathStylesheetDOM3Exception)
        throw new DOMException((short)14, transformerException.getMessageAndLocation()); 
      throw new XPathException((short)1, transformerException.getMessageAndLocation());
    } 
  }
  
  public XPathNSResolver createNSResolver(Node paramNode) { return new XPathNSResolverImpl((paramNode.getNodeType() == 9) ? ((Document)paramNode).getDocumentElement() : paramNode); }
  
  public Object evaluate(String paramString, Node paramNode, XPathNSResolver paramXPathNSResolver, short paramShort, Object paramObject) throws XPathException, DOMException {
    XPathExpression xPathExpression = createExpression(paramString, paramXPathNSResolver);
    return xPathExpression.evaluate(paramNode, paramShort, paramObject);
  }
  
  private class DummyPrefixResolver implements PrefixResolver {
    public String getNamespaceForPrefix(String param1String, Node param1Node) {
      String str = XPATHMessages.createXPATHMessage("ER_NULL_RESOLVER", null);
      throw new DOMException((short)14, str);
    }
    
    public String getNamespaceForPrefix(String param1String) { return getNamespaceForPrefix(param1String, null); }
    
    public boolean handlesNullPrefixes() { return false; }
    
    public String getBaseIdentifier() { return null; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathEvaluatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */