package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {
  private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
  
  private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  
  private SortedSet<String> inclusiveNSSet;
  
  private final SortedSet<Attr> result = new TreeSet(COMPARE);
  
  public Canonicalizer20010315Excl(boolean paramBoolean) { super(paramBoolean); }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode) throws CanonicalizationException { return engineCanonicalizeSubTree(paramNode, "", null); }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString) throws CanonicalizationException { return engineCanonicalizeSubTree(paramNode, paramString, null); }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode1, String paramString, Node paramNode2) throws CanonicalizationException {
    this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(paramString);
    return engineCanonicalizeSubTree(paramNode1, paramNode2);
  }
  
  public byte[] engineCanonicalize(XMLSignatureInput paramXMLSignatureInput, String paramString) throws CanonicalizationException {
    this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(paramString);
    return engineCanonicalize(paramXMLSignatureInput);
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString) throws CanonicalizationException {
    this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(paramString);
    return engineCanonicalizeXPathNodeSet(paramSet);
  }
  
  protected Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException {
    SortedSet sortedSet = this.result;
    sortedSet.clear();
    TreeSet treeSet = new TreeSet();
    if (this.inclusiveNSSet != null && !this.inclusiveNSSet.isEmpty())
      treeSet.addAll(this.inclusiveNSSet); 
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str1 = attr.getLocalName();
        String str2 = attr.getNodeValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
          String str3 = attr.getPrefix();
          if (str3 != null && !str3.equals("xml") && !str3.equals("xmlns"))
            treeSet.add(str3); 
          sortedSet.add(attr);
        } else if ((!"xml".equals(str1) || !"http://www.w3.org/XML/1998/namespace".equals(str2)) && paramNameSpaceSymbTable.addMapping(str1, str2, attr) && C14nHelper.namespaceIsRelative(str2)) {
          Object[] arrayOfObject = { paramElement.getTagName(), str1, attr.getNodeValue() };
          throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
        } 
      } 
    } 
    String str = null;
    if (paramElement.getNamespaceURI() != null && paramElement.getPrefix() != null && paramElement.getPrefix().length() != 0) {
      str = paramElement.getPrefix();
    } else {
      str = "xmlns";
    } 
    treeSet.add(str);
    for (String str1 : treeSet) {
      Attr attr = paramNameSpaceSymbTable.getMapping(str1);
      if (attr != null)
        sortedSet.add(attr); 
    } 
    return sortedSet.iterator();
  }
  
  protected final Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException {
    SortedSet sortedSet = this.result;
    sortedSet.clear();
    TreeSet treeSet = null;
    boolean bool = (isVisibleDO(paramElement, paramNameSpaceSymbTable.getLevel()) == 1) ? 1 : 0;
    if (bool) {
      treeSet = new TreeSet();
      if (this.inclusiveNSSet != null && !this.inclusiveNSSet.isEmpty())
        treeSet.addAll(this.inclusiveNSSet); 
    } 
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str1 = attr.getLocalName();
        String str2 = attr.getNodeValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
          if (isVisible(attr) && bool) {
            String str = attr.getPrefix();
            if (str != null && !str.equals("xml") && !str.equals("xmlns"))
              treeSet.add(str); 
            sortedSet.add(attr);
          } 
        } else if (bool && !isVisible(attr) && !"xmlns".equals(str1)) {
          paramNameSpaceSymbTable.removeMappingIfNotRender(str1);
        } else {
          if (!bool && isVisible(attr) && this.inclusiveNSSet.contains(str1) && !paramNameSpaceSymbTable.removeMappingIfRender(str1)) {
            Node node = paramNameSpaceSymbTable.addMappingAndRender(str1, str2, attr);
            if (node != null) {
              sortedSet.add((Attr)node);
              if (C14nHelper.namespaceIsRelative(attr)) {
                Object[] arrayOfObject = { paramElement.getTagName(), str1, attr.getNodeValue() };
                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
              } 
            } 
          } 
          if (paramNameSpaceSymbTable.addMapping(str1, str2, attr) && C14nHelper.namespaceIsRelative(str2)) {
            Object[] arrayOfObject = { paramElement.getTagName(), str1, attr.getNodeValue() };
            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
          } 
        } 
      } 
    } 
    if (bool) {
      Attr attr = paramElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
      if (attr != null && !isVisible(attr))
        paramNameSpaceSymbTable.addMapping("xmlns", "", getNullNode(attr.getOwnerDocument())); 
      String str = null;
      if (paramElement.getNamespaceURI() != null && paramElement.getPrefix() != null && paramElement.getPrefix().length() != 0) {
        str = paramElement.getPrefix();
      } else {
        str = "xmlns";
      } 
      treeSet.add(str);
      for (String str1 : treeSet) {
        Attr attr1 = paramNameSpaceSymbTable.getMapping(str1);
        if (attr1 != null)
          sortedSet.add(attr1); 
      } 
    } 
    return sortedSet.iterator();
  }
  
  protected void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
    if (!paramXMLSignatureInput.isNeedsToBeExpanded() || this.inclusiveNSSet.isEmpty() || this.inclusiveNSSet.isEmpty())
      return; 
    Document document = null;
    if (paramXMLSignatureInput.getSubNode() != null) {
      document = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
    } else {
      document = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
    } 
    XMLUtils.circumventBug2650(document);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer20010315Excl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */