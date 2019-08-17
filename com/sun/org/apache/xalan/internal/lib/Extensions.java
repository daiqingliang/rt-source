package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.StringTokenizer;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXNotSupportedException;

public class Extensions {
  public static NodeSet nodeset(ExpressionContext paramExpressionContext, Object paramObject) {
    String str;
    if (paramObject instanceof NodeIterator)
      return new NodeSet((NodeIterator)paramObject); 
    if (paramObject instanceof String) {
      str = (String)paramObject;
    } else if (paramObject instanceof Boolean) {
      str = (new XBoolean(((Boolean)paramObject).booleanValue())).str();
    } else if (paramObject instanceof Double) {
      str = (new XNumber(((Double)paramObject).doubleValue())).str();
    } else {
      str = paramObject.toString();
    } 
    Document document = JdkXmlUtils.getDOMDocument();
    Text text = document.createTextNode(str);
    DocumentFragment documentFragment = document.createDocumentFragment();
    documentFragment.appendChild(text);
    return new NodeSet(documentFragment);
  }
  
  public static NodeList intersection(NodeList paramNodeList1, NodeList paramNodeList2) { return ExsltSets.intersection(paramNodeList1, paramNodeList2); }
  
  public static NodeList difference(NodeList paramNodeList1, NodeList paramNodeList2) { return ExsltSets.difference(paramNodeList1, paramNodeList2); }
  
  public static NodeList distinct(NodeList paramNodeList) { return ExsltSets.distinct(paramNodeList); }
  
  public static boolean hasSameNodes(NodeList paramNodeList1, NodeList paramNodeList2) {
    NodeSet nodeSet1 = new NodeSet(paramNodeList1);
    NodeSet nodeSet2 = new NodeSet(paramNodeList2);
    if (nodeSet1.getLength() != nodeSet2.getLength())
      return false; 
    for (byte b = 0; b < nodeSet1.getLength(); b++) {
      Node node = nodeSet1.elementAt(b);
      if (!nodeSet2.contains(node))
        return false; 
    } 
    return true;
  }
  
  public static XObject evaluate(ExpressionContext paramExpressionContext, String paramString) throws SAXNotSupportedException { return ExsltDynamic.evaluate(paramExpressionContext, paramString); }
  
  public static NodeList tokenize(String paramString1, String paramString2) {
    Document document = JdkXmlUtils.getDOMDocument();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString1, paramString2);
    NodeSet nodeSet = new NodeSet();
    synchronized (document) {
      while (stringTokenizer.hasMoreTokens())
        nodeSet.addNode(document.createTextNode(stringTokenizer.nextToken())); 
    } 
    return nodeSet;
  }
  
  public static NodeList tokenize(String paramString) { return tokenize(paramString, " \t\n\r"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\Extensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */