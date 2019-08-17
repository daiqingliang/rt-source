package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXNotSupportedException;

public class ExsltDynamic extends ExsltBase {
  public static final String EXSL_URI = "http://exslt.org/common";
  
  public static double max(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString) throws SAXNotSupportedException {
    XPathContext xPathContext = null;
    if (paramExpressionContext instanceof XPathContext.XPathExpressionContext) {
      xPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    } 
    if (paramString == null || paramString.length() == 0)
      return NaND; 
    NodeSetDTM nodeSetDTM = new NodeSetDTM(paramNodeList, xPathContext);
    xPathContext.pushContextNodeList(nodeSetDTM);
    double d = -1.7976931348623157E308D;
    for (byte b = 0; b < nodeSetDTM.getLength(); b++) {
      int i = nodeSetDTM.item(b);
      xPathContext.pushCurrentNode(i);
      double d1 = 0.0D;
      try {
        XPath xPath = new XPath(paramString, xPathContext.getSAXLocator(), xPathContext.getNamespaceContext(), 0);
        d1 = xPath.execute(xPathContext, i, xPathContext.getNamespaceContext()).num();
      } catch (TransformerException transformerException) {
        xPathContext.popCurrentNode();
        xPathContext.popContextNodeList();
        return NaND;
      } 
      xPathContext.popCurrentNode();
      if (d1 > d)
        d = d1; 
    } 
    xPathContext.popContextNodeList();
    return d;
  }
  
  public static double min(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString) throws SAXNotSupportedException {
    XPathContext xPathContext = null;
    if (paramExpressionContext instanceof XPathContext.XPathExpressionContext) {
      xPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    } 
    if (paramString == null || paramString.length() == 0)
      return NaND; 
    NodeSetDTM nodeSetDTM = new NodeSetDTM(paramNodeList, xPathContext);
    xPathContext.pushContextNodeList(nodeSetDTM);
    double d = Double.MAX_VALUE;
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      int i = nodeSetDTM.item(b);
      xPathContext.pushCurrentNode(i);
      double d1 = 0.0D;
      try {
        XPath xPath = new XPath(paramString, xPathContext.getSAXLocator(), xPathContext.getNamespaceContext(), 0);
        d1 = xPath.execute(xPathContext, i, xPathContext.getNamespaceContext()).num();
      } catch (TransformerException transformerException) {
        xPathContext.popCurrentNode();
        xPathContext.popContextNodeList();
        return NaND;
      } 
      xPathContext.popCurrentNode();
      if (d1 < d)
        d = d1; 
    } 
    xPathContext.popContextNodeList();
    return d;
  }
  
  public static double sum(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString) throws SAXNotSupportedException {
    XPathContext xPathContext = null;
    if (paramExpressionContext instanceof XPathContext.XPathExpressionContext) {
      xPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    } 
    if (paramString == null || paramString.length() == 0)
      return NaND; 
    NodeSetDTM nodeSetDTM = new NodeSetDTM(paramNodeList, xPathContext);
    xPathContext.pushContextNodeList(nodeSetDTM);
    double d = 0.0D;
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      int i = nodeSetDTM.item(b);
      xPathContext.pushCurrentNode(i);
      double d1 = 0.0D;
      try {
        XPath xPath = new XPath(paramString, xPathContext.getSAXLocator(), xPathContext.getNamespaceContext(), 0);
        d1 = xPath.execute(xPathContext, i, xPathContext.getNamespaceContext()).num();
      } catch (TransformerException transformerException) {
        xPathContext.popCurrentNode();
        xPathContext.popContextNodeList();
        return NaND;
      } 
      xPathContext.popCurrentNode();
      d += d1;
    } 
    xPathContext.popContextNodeList();
    return d;
  }
  
  public static NodeList map(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString) throws SAXNotSupportedException {
    XPathContext xPathContext = null;
    Document document = null;
    if (paramExpressionContext instanceof XPathContext.XPathExpressionContext) {
      xPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    } 
    if (paramString == null || paramString.length() == 0)
      return new NodeSet(); 
    NodeSetDTM nodeSetDTM = new NodeSetDTM(paramNodeList, xPathContext);
    xPathContext.pushContextNodeList(nodeSetDTM);
    NodeSet nodeSet = new NodeSet();
    nodeSet.setShouldCacheNodes(true);
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      int i = nodeSetDTM.item(b);
      xPathContext.pushCurrentNode(i);
      XObject xObject = null;
      try {
        XPath xPath = new XPath(paramString, xPathContext.getSAXLocator(), xPathContext.getNamespaceContext(), 0);
        xObject = xPath.execute(xPathContext, i, xPathContext.getNamespaceContext());
        if (xObject instanceof XNodeSet) {
          NodeList nodeList = null;
          nodeList = ((XNodeSet)xObject).nodelist();
          for (byte b1 = 0; b1 < nodeList.getLength(); b1++) {
            Node node = nodeList.item(b1);
            if (!nodeSet.contains(node))
              nodeSet.addNode(node); 
          } 
        } else {
          if (document == null)
            document = JdkXmlUtils.getDOMDocument(); 
          Element element = null;
          if (xObject instanceof com.sun.org.apache.xpath.internal.objects.XNumber) {
            element = document.createElementNS("http://exslt.org/common", "exsl:number");
          } else if (xObject instanceof com.sun.org.apache.xpath.internal.objects.XBoolean) {
            element = document.createElementNS("http://exslt.org/common", "exsl:boolean");
          } else {
            element = document.createElementNS("http://exslt.org/common", "exsl:string");
          } 
          Text text = document.createTextNode(xObject.str());
          element.appendChild(text);
          nodeSet.addNode(element);
        } 
      } catch (Exception exception) {
        xPathContext.popCurrentNode();
        xPathContext.popContextNodeList();
        return new NodeSet();
      } 
      xPathContext.popCurrentNode();
    } 
    xPathContext.popContextNodeList();
    return nodeSet;
  }
  
  public static XObject evaluate(ExpressionContext paramExpressionContext, String paramString) throws SAXNotSupportedException {
    if (paramExpressionContext instanceof XPathContext.XPathExpressionContext) {
      XPathContext xPathContext = null;
      try {
        xPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
        XPath xPath = new XPath(paramString, xPathContext.getSAXLocator(), xPathContext.getNamespaceContext(), 0);
        return xPath.execute(xPathContext, paramExpressionContext.getContextNode(), xPathContext.getNamespaceContext());
      } catch (TransformerException transformerException) {
        return new XNodeSet(xPathContext.getDTMManager());
      } 
    } 
    throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
  }
  
  public static NodeList closure(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString) throws SAXNotSupportedException {
    XPathContext xPathContext = null;
    if (paramExpressionContext instanceof XPathContext.XPathExpressionContext) {
      xPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    } 
    if (paramString == null || paramString.length() == 0)
      return new NodeSet(); 
    NodeSet nodeSet = new NodeSet();
    nodeSet.setShouldCacheNodes(true);
    NodeList nodeList = paramNodeList;
    do {
      NodeSet nodeSet1 = new NodeSet();
      NodeSetDTM nodeSetDTM = new NodeSetDTM(nodeList, xPathContext);
      xPathContext.pushContextNodeList(nodeSetDTM);
      byte b;
      for (b = 0; b < nodeList.getLength(); b++) {
        int i = nodeSetDTM.item(b);
        xPathContext.pushCurrentNode(i);
        XObject xObject = null;
        try {
          XPath xPath = new XPath(paramString, xPathContext.getSAXLocator(), xPathContext.getNamespaceContext(), 0);
          xObject = xPath.execute(xPathContext, i, xPathContext.getNamespaceContext());
          if (xObject instanceof XNodeSet) {
            NodeList nodeList1 = null;
            nodeList1 = ((XNodeSet)xObject).nodelist();
            for (byte b1 = 0; b1 < nodeList1.getLength(); b1++) {
              Node node = nodeList1.item(b1);
              if (!nodeSet1.contains(node))
                nodeSet1.addNode(node); 
            } 
          } else {
            xPathContext.popCurrentNode();
            xPathContext.popContextNodeList();
            return new NodeSet();
          } 
        } catch (TransformerException transformerException) {
          xPathContext.popCurrentNode();
          xPathContext.popContextNodeList();
          return new NodeSet();
        } 
        xPathContext.popCurrentNode();
      } 
      xPathContext.popContextNodeList();
      nodeList = nodeSet1;
      for (b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        if (!nodeSet.contains(node))
          nodeSet.addNode(node); 
      } 
    } while (nodeList.getLength() > 0);
    return nodeSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltDynamic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */