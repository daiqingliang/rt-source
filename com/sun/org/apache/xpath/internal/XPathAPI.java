package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XPathAPI {
  public static Node selectSingleNode(Node paramNode, String paramString) throws TransformerException { return selectSingleNode(paramNode, paramString, paramNode); }
  
  public static Node selectSingleNode(Node paramNode1, String paramString, Node paramNode2) throws TransformerException {
    NodeIterator nodeIterator = selectNodeIterator(paramNode1, paramString, paramNode2);
    return nodeIterator.nextNode();
  }
  
  public static NodeIterator selectNodeIterator(Node paramNode, String paramString) throws TransformerException { return selectNodeIterator(paramNode, paramString, paramNode); }
  
  public static NodeIterator selectNodeIterator(Node paramNode1, String paramString, Node paramNode2) throws TransformerException {
    XObject xObject = eval(paramNode1, paramString, paramNode2);
    return xObject.nodeset();
  }
  
  public static NodeList selectNodeList(Node paramNode, String paramString) throws TransformerException { return selectNodeList(paramNode, paramString, paramNode); }
  
  public static NodeList selectNodeList(Node paramNode1, String paramString, Node paramNode2) throws TransformerException {
    XObject xObject = eval(paramNode1, paramString, paramNode2);
    return xObject.nodelist();
  }
  
  public static XObject eval(Node paramNode, String paramString) throws TransformerException { return eval(paramNode, paramString, paramNode); }
  
  public static XObject eval(Node paramNode1, String paramString, Node paramNode2) throws TransformerException {
    XPathContext xPathContext = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
    PrefixResolverDefault prefixResolverDefault = new PrefixResolverDefault((paramNode2.getNodeType() == 9) ? ((Document)paramNode2).getDocumentElement() : paramNode2);
    XPath xPath = new XPath(paramString, null, prefixResolverDefault, 0, null);
    int i = xPathContext.getDTMHandleFromNode(paramNode1);
    return xPath.execute(xPathContext, i, prefixResolverDefault);
  }
  
  public static XObject eval(Node paramNode, String paramString, PrefixResolver paramPrefixResolver) throws TransformerException {
    XPath xPath = new XPath(paramString, null, paramPrefixResolver, 0, null);
    XPathContext xPathContext = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
    int i = xPathContext.getDTMHandleFromNode(paramNode);
    return xPath.execute(xPathContext, i, paramPrefixResolver);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\XPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */