package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XMLUtils {
  private static boolean ignoreLineBreaks = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.ignoreLineBreaks")); }
      })).booleanValue();
  
  private static final Logger log = Logger.getLogger(XMLUtils.class.getName());
  
  public static void setDsPrefix(String paramString) {
    JavaUtils.checkRegisterPermission();
    dsPrefix = paramString;
  }
  
  public static void setDs11Prefix(String paramString) {
    JavaUtils.checkRegisterPermission();
    ds11Prefix = paramString;
  }
  
  public static void setXencPrefix(String paramString) {
    JavaUtils.checkRegisterPermission();
    xencPrefix = paramString;
  }
  
  public static void setXenc11Prefix(String paramString) {
    JavaUtils.checkRegisterPermission();
    xenc11Prefix = paramString;
  }
  
  public static Element getNextElement(Node paramNode) {
    Node node;
    for (node = paramNode; node != null && node.getNodeType() != 1; node = node.getNextSibling());
    return (Element)node;
  }
  
  public static void getSet(Node paramNode1, Set<Node> paramSet, Node paramNode2, boolean paramBoolean) {
    if (paramNode2 != null && isDescendantOrSelf(paramNode2, paramNode1))
      return; 
    getSetRec(paramNode1, paramSet, paramNode2, paramBoolean);
  }
  
  private static void getSetRec(Node paramNode1, Set<Node> paramSet, Node paramNode2, boolean paramBoolean) {
    Node node;
    Element element;
    if (paramNode1 == paramNode2)
      return; 
    switch (paramNode1.getNodeType()) {
      case 1:
        paramSet.add(paramNode1);
        element = (Element)paramNode1;
        if (element.hasAttributes()) {
          NamedNodeMap namedNodeMap = element.getAttributes();
          for (byte b = 0; b < namedNodeMap.getLength(); b++)
            paramSet.add(namedNodeMap.item(b)); 
        } 
      case 9:
        for (node = paramNode1.getFirstChild(); node != null; node = node.getNextSibling()) {
          if (node.getNodeType() == 3) {
            paramSet.add(node);
            while (node != null && node.getNodeType() == 3)
              node = node.getNextSibling(); 
            if (node == null)
              return; 
          } 
          getSetRec(node, paramSet, paramNode2, paramBoolean);
        } 
        return;
      case 8:
        if (paramBoolean)
          paramSet.add(paramNode1); 
        return;
      case 10:
        return;
    } 
    paramSet.add(paramNode1);
  }
  
  public static void outputDOM(Node paramNode, OutputStream paramOutputStream) { outputDOM(paramNode, paramOutputStream, false); }
  
  public static void outputDOM(Node paramNode, OutputStream paramOutputStream, boolean paramBoolean) {
    try {
      if (paramBoolean)
        paramOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8")); 
      paramOutputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(paramNode));
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, iOException.getMessage(), iOException); 
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, invalidCanonicalizerException.getMessage(), invalidCanonicalizerException); 
    } catch (CanonicalizationException canonicalizationException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, canonicalizationException.getMessage(), canonicalizationException); 
    } 
  }
  
  public static void outputDOMc14nWithComments(Node paramNode, OutputStream paramOutputStream) {
    try {
      paramOutputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(paramNode));
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, iOException.getMessage(), iOException); 
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, invalidCanonicalizerException.getMessage(), invalidCanonicalizerException); 
    } catch (CanonicalizationException canonicalizationException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, canonicalizationException.getMessage(), canonicalizationException); 
    } 
  }
  
  public static String getFullTextChildrenFromElement(Element paramElement) {
    StringBuilder stringBuilder = new StringBuilder();
    for (Node node = paramElement.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 3)
        stringBuilder.append(((Text)node).getData()); 
    } 
    return stringBuilder.toString();
  }
  
  public static Element createElementInSignatureSpace(Document paramDocument, String paramString) {
    if (paramDocument == null)
      throw new RuntimeException("Document is null"); 
    return (dsPrefix == null || dsPrefix.length() == 0) ? paramDocument.createElementNS("http://www.w3.org/2000/09/xmldsig#", paramString) : paramDocument.createElementNS("http://www.w3.org/2000/09/xmldsig#", dsPrefix + ":" + paramString);
  }
  
  public static Element createElementInSignature11Space(Document paramDocument, String paramString) {
    if (paramDocument == null)
      throw new RuntimeException("Document is null"); 
    return (ds11Prefix == null || ds11Prefix.length() == 0) ? paramDocument.createElementNS("http://www.w3.org/2009/xmldsig11#", paramString) : paramDocument.createElementNS("http://www.w3.org/2009/xmldsig11#", ds11Prefix + ":" + paramString);
  }
  
  public static Element createElementInEncryptionSpace(Document paramDocument, String paramString) {
    if (paramDocument == null)
      throw new RuntimeException("Document is null"); 
    return (xencPrefix == null || xencPrefix.length() == 0) ? paramDocument.createElementNS("http://www.w3.org/2001/04/xmlenc#", paramString) : paramDocument.createElementNS("http://www.w3.org/2001/04/xmlenc#", xencPrefix + ":" + paramString);
  }
  
  public static Element createElementInEncryption11Space(Document paramDocument, String paramString) {
    if (paramDocument == null)
      throw new RuntimeException("Document is null"); 
    return (xenc11Prefix == null || xenc11Prefix.length() == 0) ? paramDocument.createElementNS("http://www.w3.org/2009/xmlenc11#", paramString) : paramDocument.createElementNS("http://www.w3.org/2009/xmlenc11#", xenc11Prefix + ":" + paramString);
  }
  
  public static boolean elementIsInSignatureSpace(Element paramElement, String paramString) { return (paramElement == null) ? false : (("http://www.w3.org/2000/09/xmldsig#".equals(paramElement.getNamespaceURI()) && paramElement.getLocalName().equals(paramString))); }
  
  public static boolean elementIsInSignature11Space(Element paramElement, String paramString) { return (paramElement == null) ? false : (("http://www.w3.org/2009/xmldsig11#".equals(paramElement.getNamespaceURI()) && paramElement.getLocalName().equals(paramString))); }
  
  public static boolean elementIsInEncryptionSpace(Element paramElement, String paramString) { return (paramElement == null) ? false : (("http://www.w3.org/2001/04/xmlenc#".equals(paramElement.getNamespaceURI()) && paramElement.getLocalName().equals(paramString))); }
  
  public static boolean elementIsInEncryption11Space(Element paramElement, String paramString) { return (paramElement == null) ? false : (("http://www.w3.org/2009/xmlenc11#".equals(paramElement.getNamespaceURI()) && paramElement.getLocalName().equals(paramString))); }
  
  public static Document getOwnerDocument(Node paramNode) {
    if (paramNode.getNodeType() == 9)
      return (Document)paramNode; 
    try {
      return paramNode.getOwnerDocument();
    } catch (NullPointerException nullPointerException) {
      throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + nullPointerException.getMessage() + "\"");
    } 
  }
  
  public static Document getOwnerDocument(Set<Node> paramSet) {
    NullPointerException nullPointerException = null;
    for (Node node : paramSet) {
      short s = node.getNodeType();
      if (s == 9)
        return (Document)node; 
      try {
        return (s == 2) ? ((Attr)node).getOwnerElement().getOwnerDocument() : node.getOwnerDocument();
      } catch (NullPointerException nullPointerException1) {
        nullPointerException = nullPointerException1;
      } 
    } 
    throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + ((nullPointerException == null) ? "" : nullPointerException.getMessage()) + "\"");
  }
  
  public static Element createDSctx(Document paramDocument, String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.trim().length() == 0)
      throw new IllegalArgumentException("You must supply a prefix"); 
    Element element = paramDocument.createElementNS(null, "namespaceContext");
    element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + paramString1.trim(), paramString2);
    return element;
  }
  
  public static void addReturnToElement(Element paramElement) {
    if (!ignoreLineBreaks) {
      Document document = paramElement.getOwnerDocument();
      paramElement.appendChild(document.createTextNode("\n"));
    } 
  }
  
  public static void addReturnToElement(Document paramDocument, HelperNodeList paramHelperNodeList) {
    if (!ignoreLineBreaks)
      paramHelperNodeList.appendChild(paramDocument.createTextNode("\n")); 
  }
  
  public static void addReturnBeforeChild(Element paramElement, Node paramNode) {
    if (!ignoreLineBreaks) {
      Document document = paramElement.getOwnerDocument();
      paramElement.insertBefore(document.createTextNode("\n"), paramNode);
    } 
  }
  
  public static Set<Node> convertNodelistToSet(NodeList paramNodeList) {
    if (paramNodeList == null)
      return new HashSet(); 
    int i = paramNodeList.getLength();
    HashSet hashSet = new HashSet(i);
    for (byte b = 0; b < i; b++)
      hashSet.add(paramNodeList.item(b)); 
    return hashSet;
  }
  
  public static void circumventBug2650(Document paramDocument) {
    Element element = paramDocument.getDocumentElement();
    Attr attr = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
    if (attr == null)
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", ""); 
    circumventBug2650internal(paramDocument);
  }
  
  private static void circumventBug2650internal(Node paramNode) {
    Node node1 = null;
    for (Node node2 = null;; node2 = paramNode.getNextSibling()) {
      Element element;
      switch (paramNode.getNodeType()) {
        case 1:
          element = (Element)paramNode;
          if (!element.hasChildNodes())
            break; 
          if (element.hasAttributes()) {
            NamedNodeMap namedNodeMap = element.getAttributes();
            int i = namedNodeMap.getLength();
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
              if (node.getNodeType() == 1) {
                Element element1 = (Element)node;
                for (byte b = 0; b < i; b++) {
                  Attr attr = (Attr)namedNodeMap.item(b);
                  if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI()) && !element1.hasAttributeNS("http://www.w3.org/2000/xmlns/", attr.getLocalName()))
                    element1.setAttributeNS("http://www.w3.org/2000/xmlns/", attr.getName(), attr.getNodeValue()); 
                } 
              } 
            } 
          } 
        case 5:
        case 9:
          node1 = paramNode;
          node2 = paramNode.getFirstChild();
          break;
      } 
      while (node2 == null && node1 != null) {
        node2 = node1.getNextSibling();
        node1 = node1.getParentNode();
      } 
      if (node2 == null)
        return; 
      paramNode = node2;
    } 
  }
  
  public static Element selectDsNode(Node paramNode, String paramString, int paramInt) {
    while (paramNode != null) {
      if ("http://www.w3.org/2000/09/xmldsig#".equals(paramNode.getNamespaceURI()) && paramNode.getLocalName().equals(paramString)) {
        if (paramInt == 0)
          return (Element)paramNode; 
        paramInt--;
      } 
      paramNode = paramNode.getNextSibling();
    } 
    return null;
  }
  
  public static Element selectDs11Node(Node paramNode, String paramString, int paramInt) {
    while (paramNode != null) {
      if ("http://www.w3.org/2009/xmldsig11#".equals(paramNode.getNamespaceURI()) && paramNode.getLocalName().equals(paramString)) {
        if (paramInt == 0)
          return (Element)paramNode; 
        paramInt--;
      } 
      paramNode = paramNode.getNextSibling();
    } 
    return null;
  }
  
  public static Element selectXencNode(Node paramNode, String paramString, int paramInt) {
    while (paramNode != null) {
      if ("http://www.w3.org/2001/04/xmlenc#".equals(paramNode.getNamespaceURI()) && paramNode.getLocalName().equals(paramString)) {
        if (paramInt == 0)
          return (Element)paramNode; 
        paramInt--;
      } 
      paramNode = paramNode.getNextSibling();
    } 
    return null;
  }
  
  public static Text selectDsNodeText(Node paramNode, String paramString, int paramInt) {
    Element element = selectDsNode(paramNode, paramString, paramInt);
    if (element == null)
      return null; 
    Node node;
    for (node = element.getFirstChild(); node != null && node.getNodeType() != 3; node = node.getNextSibling());
    return (Text)node;
  }
  
  public static Text selectDs11NodeText(Node paramNode, String paramString, int paramInt) {
    Element element = selectDs11Node(paramNode, paramString, paramInt);
    if (element == null)
      return null; 
    Node node;
    for (node = element.getFirstChild(); node != null && node.getNodeType() != 3; node = node.getNextSibling());
    return (Text)node;
  }
  
  public static Text selectNodeText(Node paramNode, String paramString1, String paramString2, int paramInt) {
    Element element = selectNode(paramNode, paramString1, paramString2, paramInt);
    if (element == null)
      return null; 
    Node node;
    for (node = element.getFirstChild(); node != null && node.getNodeType() != 3; node = node.getNextSibling());
    return (Text)node;
  }
  
  public static Element selectNode(Node paramNode, String paramString1, String paramString2, int paramInt) {
    while (paramNode != null) {
      if (paramNode.getNamespaceURI() != null && paramNode.getNamespaceURI().equals(paramString1) && paramNode.getLocalName().equals(paramString2)) {
        if (paramInt == 0)
          return (Element)paramNode; 
        paramInt--;
      } 
      paramNode = paramNode.getNextSibling();
    } 
    return null;
  }
  
  public static Element[] selectDsNodes(Node paramNode, String paramString) { return selectNodes(paramNode, "http://www.w3.org/2000/09/xmldsig#", paramString); }
  
  public static Element[] selectDs11Nodes(Node paramNode, String paramString) { return selectNodes(paramNode, "http://www.w3.org/2009/xmldsig11#", paramString); }
  
  public static Element[] selectNodes(Node paramNode, String paramString1, String paramString2) {
    ArrayList arrayList = new ArrayList();
    while (paramNode != null) {
      if (paramNode.getNamespaceURI() != null && paramNode.getNamespaceURI().equals(paramString1) && paramNode.getLocalName().equals(paramString2))
        arrayList.add((Element)paramNode); 
      paramNode = paramNode.getNextSibling();
    } 
    return (Element[])arrayList.toArray(new Element[arrayList.size()]);
  }
  
  public static Set<Node> excludeNodeFromSet(Node paramNode, Set<Node> paramSet) {
    HashSet hashSet = new HashSet();
    for (Node node : paramSet) {
      if (!isDescendantOrSelf(paramNode, node))
        hashSet.add(node); 
    } 
    return hashSet;
  }
  
  public static String getStrFromNode(Node paramNode) {
    if (paramNode.getNodeType() == 3) {
      StringBuilder stringBuilder = new StringBuilder();
      for (Node node = paramNode.getParentNode().getFirstChild(); node != null; node = node.getNextSibling()) {
        if (node.getNodeType() == 3)
          stringBuilder.append(((Text)node).getData()); 
      } 
      return stringBuilder.toString();
    } 
    return (paramNode.getNodeType() == 2) ? ((Attr)paramNode).getNodeValue() : ((paramNode.getNodeType() == 7) ? ((ProcessingInstruction)paramNode).getNodeValue() : null);
  }
  
  public static boolean isDescendantOrSelf(Node paramNode1, Node paramNode2) {
    if (paramNode1 == paramNode2)
      return true; 
    for (Node node = paramNode2;; node = node.getParentNode()) {
      if (node == null)
        return false; 
      if (node == paramNode1)
        return true; 
      if (node.getNodeType() == 2) {
        node = ((Attr)node).getOwnerElement();
        continue;
      } 
    } 
  }
  
  public static boolean ignoreLineBreaks() { return ignoreLineBreaks; }
  
  public static String getAttributeValue(Element paramElement, String paramString) {
    Attr attr = paramElement.getAttributeNodeNS(null, paramString);
    return (attr == null) ? null : attr.getValue();
  }
  
  public static boolean protectAgainstWrappingAttack(Node paramNode, String paramString) {
    Node node1 = paramNode.getParentNode();
    Node node2 = null;
    Element element = null;
    String str = paramString.trim();
    if (!str.isEmpty() && str.charAt(0) == '#')
      str = str.substring(1); 
    while (paramNode != null) {
      if (paramNode.getNodeType() == 1) {
        Element element1 = (Element)paramNode;
        NamedNodeMap namedNodeMap = element1.getAttributes();
        if (namedNodeMap != null)
          for (byte b = 0; b < namedNodeMap.getLength(); b++) {
            Attr attr = (Attr)namedNodeMap.item(b);
            if (attr.isId() && str.equals(attr.getValue()))
              if (element == null) {
                element = attr.getOwnerElement();
              } else {
                log.log(Level.FINE, "Multiple elements with the same 'Id' attribute value!");
                return false;
              }  
          }  
      } 
      node2 = paramNode;
      paramNode = paramNode.getFirstChild();
      if (paramNode == null)
        paramNode = node2.getNextSibling(); 
      while (paramNode == null) {
        node2 = node2.getParentNode();
        if (node2 == node1)
          return true; 
        paramNode = node2.getNextSibling();
      } 
    } 
    return true;
  }
  
  public static boolean protectAgainstWrappingAttack(Node paramNode, Element paramElement, String paramString) {
    Node node1 = paramNode.getParentNode();
    Node node2 = null;
    String str = paramString.trim();
    if (!str.isEmpty() && str.charAt(0) == '#')
      str = str.substring(1); 
    while (paramNode != null) {
      if (paramNode.getNodeType() == 1) {
        Element element = (Element)paramNode;
        NamedNodeMap namedNodeMap = element.getAttributes();
        if (namedNodeMap != null)
          for (byte b = 0; b < namedNodeMap.getLength(); b++) {
            Attr attr = (Attr)namedNodeMap.item(b);
            if (attr.isId() && str.equals(attr.getValue()) && element != paramElement) {
              log.log(Level.FINE, "Multiple elements with the same 'Id' attribute value!");
              return false;
            } 
          }  
      } 
      node2 = paramNode;
      paramNode = paramNode.getFirstChild();
      if (paramNode == null)
        paramNode = node2.getNextSibling(); 
      while (paramNode == null) {
        node2 = node2.getParentNode();
        if (node2 == node1)
          return true; 
        paramNode = node2.getNextSibling();
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\XMLUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */