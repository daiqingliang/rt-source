package org.jcp.xml.dsig.internal.dom;

import java.security.spec.AlgorithmParameterSpec;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtils {
  public static Document getOwnerDocument(Node paramNode) { return (paramNode.getNodeType() == 9) ? (Document)paramNode : paramNode.getOwnerDocument(); }
  
  public static Element createElement(Document paramDocument, String paramString1, String paramString2, String paramString3) {
    String str = (paramString3 == null || paramString3.length() == 0) ? paramString1 : (paramString3 + ":" + paramString1);
    return paramDocument.createElementNS(paramString2, str);
  }
  
  public static void setAttribute(Element paramElement, String paramString1, String paramString2) {
    if (paramString2 == null)
      return; 
    paramElement.setAttributeNS(null, paramString1, paramString2);
  }
  
  public static void setAttributeID(Element paramElement, String paramString1, String paramString2) {
    if (paramString2 == null)
      return; 
    paramElement.setAttributeNS(null, paramString1, paramString2);
    paramElement.setIdAttributeNS(null, paramString1, true);
  }
  
  public static Element getFirstChildElement(Node paramNode) {
    Node node;
    for (node = paramNode.getFirstChild(); node != null && node.getNodeType() != 1; node = node.getNextSibling());
    return (Element)node;
  }
  
  public static Element getFirstChildElement(Node paramNode, String paramString) throws MarshalException { return verifyElement(getFirstChildElement(paramNode), paramString); }
  
  private static Element verifyElement(Element paramElement, String paramString) throws MarshalException {
    if (paramElement == null)
      throw new MarshalException("Missing " + paramString + " element"); 
    String str = paramElement.getLocalName();
    if (!str.equals(paramString))
      throw new MarshalException("Invalid element name: " + str + ", expected " + paramString); 
    return paramElement;
  }
  
  public static Element getLastChildElement(Node paramNode) {
    Node node;
    for (node = paramNode.getLastChild(); node != null && node.getNodeType() != 1; node = node.getPreviousSibling());
    return (Element)node;
  }
  
  public static Element getNextSiblingElement(Node paramNode) {
    Node node;
    for (node = paramNode.getNextSibling(); node != null && node.getNodeType() != 1; node = node.getNextSibling());
    return (Element)node;
  }
  
  public static Element getNextSiblingElement(Node paramNode, String paramString) throws MarshalException { return verifyElement(getNextSiblingElement(paramNode), paramString); }
  
  public static String getAttributeValue(Element paramElement, String paramString) {
    Attr attr = paramElement.getAttributeNodeNS(null, paramString);
    return (attr == null) ? null : attr.getValue();
  }
  
  public static Set<Node> nodeSet(NodeList paramNodeList) { return new NodeSet(paramNodeList); }
  
  public static String getNSPrefix(XMLCryptoContext paramXMLCryptoContext, String paramString) { return (paramXMLCryptoContext != null) ? paramXMLCryptoContext.getNamespacePrefix(paramString, paramXMLCryptoContext.getDefaultNamespacePrefix()) : null; }
  
  public static String getSignaturePrefix(XMLCryptoContext paramXMLCryptoContext) { return getNSPrefix(paramXMLCryptoContext, "http://www.w3.org/2000/09/xmldsig#"); }
  
  public static void removeAllChildren(Node paramNode) {
    NodeList nodeList = paramNode.getChildNodes();
    byte b = 0;
    int i = nodeList.getLength();
    while (b < i) {
      paramNode.removeChild(nodeList.item(b));
      b++;
    } 
  }
  
  public static boolean nodesEqual(Node paramNode1, Node paramNode2) { return (paramNode1 == paramNode2) ? true : (!(paramNode1.getNodeType() != paramNode2.getNodeType())); }
  
  public static void appendChild(Node paramNode1, Node paramNode2) {
    Document document = getOwnerDocument(paramNode1);
    if (paramNode2.getOwnerDocument() != document) {
      paramNode1.appendChild(document.importNode(paramNode2, true));
    } else {
      paramNode1.appendChild(paramNode2);
    } 
  }
  
  public static boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec1, AlgorithmParameterSpec paramAlgorithmParameterSpec2) { return (paramAlgorithmParameterSpec1 == paramAlgorithmParameterSpec2) ? true : ((paramAlgorithmParameterSpec1 instanceof XPathFilter2ParameterSpec && paramAlgorithmParameterSpec2 instanceof XPathFilter2ParameterSpec) ? paramsEqual((XPathFilter2ParameterSpec)paramAlgorithmParameterSpec1, (XPathFilter2ParameterSpec)paramAlgorithmParameterSpec2) : ((paramAlgorithmParameterSpec1 instanceof ExcC14NParameterSpec && paramAlgorithmParameterSpec2 instanceof ExcC14NParameterSpec) ? paramsEqual((ExcC14NParameterSpec)paramAlgorithmParameterSpec1, (ExcC14NParameterSpec)paramAlgorithmParameterSpec2) : ((paramAlgorithmParameterSpec1 instanceof XPathFilterParameterSpec && paramAlgorithmParameterSpec2 instanceof XPathFilterParameterSpec) ? paramsEqual((XPathFilterParameterSpec)paramAlgorithmParameterSpec1, (XPathFilterParameterSpec)paramAlgorithmParameterSpec2) : ((paramAlgorithmParameterSpec1 instanceof XSLTTransformParameterSpec && paramAlgorithmParameterSpec2 instanceof XSLTTransformParameterSpec) ? paramsEqual((XSLTTransformParameterSpec)paramAlgorithmParameterSpec1, (XSLTTransformParameterSpec)paramAlgorithmParameterSpec2) : 0)))); }
  
  private static boolean paramsEqual(XPathFilter2ParameterSpec paramXPathFilter2ParameterSpec1, XPathFilter2ParameterSpec paramXPathFilter2ParameterSpec2) {
    List list1 = paramXPathFilter2ParameterSpec1.getXPathList();
    List list2 = paramXPathFilter2ParameterSpec2.getXPathList();
    int i = list1.size();
    if (i != list2.size())
      return false; 
    for (byte b = 0; b < i; b++) {
      XPathType xPathType1 = (XPathType)list1.get(b);
      XPathType xPathType2 = (XPathType)list2.get(b);
      if (!xPathType1.getExpression().equals(xPathType2.getExpression()) || !xPathType1.getNamespaceMap().equals(xPathType2.getNamespaceMap()) || xPathType1.getFilter() != xPathType2.getFilter())
        return false; 
    } 
    return true;
  }
  
  private static boolean paramsEqual(ExcC14NParameterSpec paramExcC14NParameterSpec1, ExcC14NParameterSpec paramExcC14NParameterSpec2) { return paramExcC14NParameterSpec1.getPrefixList().equals(paramExcC14NParameterSpec2.getPrefixList()); }
  
  private static boolean paramsEqual(XPathFilterParameterSpec paramXPathFilterParameterSpec1, XPathFilterParameterSpec paramXPathFilterParameterSpec2) { return (paramXPathFilterParameterSpec1.getXPath().equals(paramXPathFilterParameterSpec2.getXPath()) && paramXPathFilterParameterSpec1.getNamespaceMap().equals(paramXPathFilterParameterSpec2.getNamespaceMap())); }
  
  private static boolean paramsEqual(XSLTTransformParameterSpec paramXSLTTransformParameterSpec1, XSLTTransformParameterSpec paramXSLTTransformParameterSpec2) {
    XMLStructure xMLStructure1 = paramXSLTTransformParameterSpec2.getStylesheet();
    if (!(xMLStructure1 instanceof DOMStructure))
      return false; 
    Node node1 = ((DOMStructure)xMLStructure1).getNode();
    XMLStructure xMLStructure2 = paramXSLTTransformParameterSpec1.getStylesheet();
    Node node2 = ((DOMStructure)xMLStructure2).getNode();
    return nodesEqual(node2, node1);
  }
  
  static class NodeSet extends AbstractSet<Node> {
    private NodeList nl;
    
    public NodeSet(NodeList param1NodeList) { this.nl = param1NodeList; }
    
    public int size() { return this.nl.getLength(); }
    
    public Iterator<Node> iterator() { return new Iterator<Node>() {
          int index = 0;
          
          public void remove() { throw new UnsupportedOperationException(); }
          
          public Node next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            return DOMUtils.NodeSet.this.nl.item(this.index++);
          }
          
          public boolean hasNext() { return (this.index < DOMUtils.NodeSet.this.nl.getLength()); }
        }; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */