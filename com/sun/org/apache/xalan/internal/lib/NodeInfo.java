package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeInfo {
  public static String systemId(ExpressionContext paramExpressionContext) {
    Node node = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getSystemId() : null;
  }
  
  public static String systemId(NodeList paramNodeList) {
    if (paramNodeList == null || paramNodeList.getLength() == 0)
      return null; 
    Node node = paramNodeList.item(0);
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getSystemId() : null;
  }
  
  public static String publicId(ExpressionContext paramExpressionContext) {
    Node node = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getPublicId() : null;
  }
  
  public static String publicId(NodeList paramNodeList) {
    if (paramNodeList == null || paramNodeList.getLength() == 0)
      return null; 
    Node node = paramNodeList.item(0);
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getPublicId() : null;
  }
  
  public static int lineNumber(ExpressionContext paramExpressionContext) {
    Node node = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getLineNumber() : -1;
  }
  
  public static int lineNumber(NodeList paramNodeList) {
    if (paramNodeList == null || paramNodeList.getLength() == 0)
      return -1; 
    Node node = paramNodeList.item(0);
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getLineNumber() : -1;
  }
  
  public static int columnNumber(ExpressionContext paramExpressionContext) {
    Node node = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getColumnNumber() : -1;
  }
  
  public static int columnNumber(NodeList paramNodeList) {
    if (paramNodeList == null || paramNodeList.getLength() == 0)
      return -1; 
    Node node = paramNodeList.item(0);
    int i = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator sourceLocator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(i);
    return (sourceLocator != null) ? sourceLocator.getColumnNumber() : -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\NodeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */