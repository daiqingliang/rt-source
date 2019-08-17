package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizerSpi;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public abstract class CanonicalizerBase extends CanonicalizerSpi {
  public static final String XML = "xml";
  
  public static final String XMLNS = "xmlns";
  
  protected static final AttrCompare COMPARE = new AttrCompare();
  
  private static final byte[] END_PI = { 63, 62 };
  
  private static final byte[] BEGIN_PI = { 60, 63 };
  
  private static final byte[] END_COMM = { 45, 45, 62 };
  
  private static final byte[] BEGIN_COMM = { 60, 33, 45, 45 };
  
  private static final byte[] XA = { 38, 35, 120, 65, 59 };
  
  private static final byte[] X9 = { 38, 35, 120, 57, 59 };
  
  private static final byte[] QUOT = { 38, 113, 117, 111, 116, 59 };
  
  private static final byte[] XD = { 38, 35, 120, 68, 59 };
  
  private static final byte[] GT = { 38, 103, 116, 59 };
  
  private static final byte[] LT = { 38, 108, 116, 59 };
  
  private static final byte[] END_TAG = { 60, 47 };
  
  private static final byte[] AMP = { 38, 97, 109, 112, 59 };
  
  private static final byte[] EQUALS_STR = { 61, 34 };
  
  protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
  
  protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
  
  protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
  
  private List<NodeFilter> nodeFilter;
  
  private boolean includeComments;
  
  private Set<Node> xpathNodeSet;
  
  private Node excludeNode;
  
  private OutputStream writer = new ByteArrayOutputStream();
  
  private Attr nullNode;
  
  public CanonicalizerBase(boolean paramBoolean) { this.includeComments = paramBoolean; }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode) throws CanonicalizationException { return engineCanonicalizeSubTree(paramNode, (Node)null); }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet) throws CanonicalizationException {
    this.xpathNodeSet = paramSet;
    return engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this.xpathNodeSet));
  }
  
  public byte[] engineCanonicalize(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException {
    try {
      if (paramXMLSignatureInput.isExcludeComments())
        this.includeComments = false; 
      if (paramXMLSignatureInput.isOctetStream())
        return engineCanonicalize(paramXMLSignatureInput.getBytes()); 
      if (paramXMLSignatureInput.isElement())
        return engineCanonicalizeSubTree(paramXMLSignatureInput.getSubNode(), paramXMLSignatureInput.getExcludeNode()); 
      if (paramXMLSignatureInput.isNodeSet()) {
        this.nodeFilter = paramXMLSignatureInput.getNodeFilters();
        circumventBugIfNeeded(paramXMLSignatureInput);
        return (paramXMLSignatureInput.getSubNode() != null) ? engineCanonicalizeXPathNodeSetInternal(paramXMLSignatureInput.getSubNode()) : engineCanonicalizeXPathNodeSet(paramXMLSignatureInput.getNodeSet());
      } 
      return null;
    } catch (CanonicalizationException canonicalizationException) {
      throw new CanonicalizationException("empty", canonicalizationException);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new CanonicalizationException("empty", parserConfigurationException);
    } catch (IOException iOException) {
      throw new CanonicalizationException("empty", iOException);
    } catch (SAXException sAXException) {
      throw new CanonicalizationException("empty", sAXException);
    } 
  }
  
  public void setWriter(OutputStream paramOutputStream) { this.writer = paramOutputStream; }
  
  protected byte[] engineCanonicalizeSubTree(Node paramNode1, Node paramNode2) throws CanonicalizationException {
    this.excludeNode = paramNode2;
    try {
      NameSpaceSymbTable nameSpaceSymbTable = new NameSpaceSymbTable();
      byte b = -1;
      if (paramNode1 != null && 1 == paramNode1.getNodeType()) {
        getParentNameSpaces((Element)paramNode1, nameSpaceSymbTable);
        b = 0;
      } 
      canonicalizeSubTree(paramNode1, nameSpaceSymbTable, paramNode1, b);
      this.writer.flush();
      if (this.writer instanceof ByteArrayOutputStream) {
        byte[] arrayOfByte = ((ByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((ByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        } 
        return arrayOfByte;
      } 
      if (this.writer instanceof UnsyncByteArrayOutputStream) {
        byte[] arrayOfByte = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((UnsyncByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        } 
        return arrayOfByte;
      } 
      this.writer.close();
      return null;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new CanonicalizationException("empty", unsupportedEncodingException);
    } catch (IOException iOException) {
      throw new CanonicalizationException("empty", iOException);
    } 
  }
  
  protected final void canonicalizeSubTree(Node paramNode1, NameSpaceSymbTable paramNameSpaceSymbTable, Node paramNode2, int paramInt) throws CanonicalizationException, IOException {
    if (isVisibleInt(paramNode1) == -1)
      return; 
    Node node1 = null;
    Element element = null;
    OutputStream outputStream = this.writer;
    Node node2 = this.excludeNode;
    boolean bool = this.includeComments;
    HashMap hashMap = new HashMap();
    while (true) {
      Iterator iterator;
      String str;
      Element element1;
      switch (paramNode1.getNodeType()) {
        case 2:
        case 6:
        case 12:
          throw new CanonicalizationException("empty");
        case 9:
        case 11:
          paramNameSpaceSymbTable.outputNodePush();
          node1 = paramNode1.getFirstChild();
          break;
        case 8:
          if (bool)
            outputCommentToWriter((Comment)paramNode1, outputStream, paramInt); 
          break;
        case 7:
          outputPItoWriter((ProcessingInstruction)paramNode1, outputStream, paramInt);
          break;
        case 3:
        case 4:
          outputTextToWriter(paramNode1.getNodeValue(), outputStream);
          break;
        case 1:
          paramInt = 0;
          if (paramNode1 == node2)
            break; 
          element1 = (Element)paramNode1;
          paramNameSpaceSymbTable.outputNodePush();
          outputStream.write(60);
          str = element1.getTagName();
          UtfHelpper.writeByte(str, outputStream, hashMap);
          iterator = handleAttributesSubtree(element1, paramNameSpaceSymbTable);
          if (iterator != null)
            while (iterator.hasNext()) {
              Attr attr = (Attr)iterator.next();
              outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), outputStream, hashMap);
            }  
          outputStream.write(62);
          node1 = paramNode1.getFirstChild();
          if (node1 == null) {
            outputStream.write((byte[])END_TAG.clone());
            UtfHelpper.writeStringToUtf8(str, outputStream);
            outputStream.write(62);
            paramNameSpaceSymbTable.outputNodePop();
            if (element != null)
              node1 = paramNode1.getNextSibling(); 
            break;
          } 
          element = element1;
          break;
      } 
      while (node1 == null && element != null) {
        outputStream.write((byte[])END_TAG.clone());
        UtfHelpper.writeByte(((Element)element).getTagName(), outputStream, hashMap);
        outputStream.write(62);
        paramNameSpaceSymbTable.outputNodePop();
        if (element == paramNode2)
          return; 
        node1 = element.getNextSibling();
        Node node = element.getParentNode();
        if (node == null || 1 != node.getNodeType()) {
          paramInt = 1;
          node = null;
        } 
      } 
      if (node1 == null)
        return; 
      paramNode1 = node1;
      node1 = paramNode1.getNextSibling();
    } 
  }
  
  private byte[] engineCanonicalizeXPathNodeSetInternal(Node paramNode) throws CanonicalizationException {
    try {
      canonicalizeXPathNodeSet(paramNode, paramNode);
      this.writer.flush();
      if (this.writer instanceof ByteArrayOutputStream) {
        byte[] arrayOfByte = ((ByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((ByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        } 
        return arrayOfByte;
      } 
      if (this.writer instanceof UnsyncByteArrayOutputStream) {
        byte[] arrayOfByte = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
        if (this.reset) {
          ((UnsyncByteArrayOutputStream)this.writer).reset();
        } else {
          this.writer.close();
        } 
        return arrayOfByte;
      } 
      this.writer.close();
      return null;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new CanonicalizationException("empty", unsupportedEncodingException);
    } catch (IOException iOException) {
      throw new CanonicalizationException("empty", iOException);
    } 
  }
  
  protected final void canonicalizeXPathNodeSet(Node paramNode1, Node paramNode2) throws CanonicalizationException, IOException {
    if (isVisibleInt(paramNode1) == -1)
      return; 
    boolean bool = false;
    NameSpaceSymbTable nameSpaceSymbTable = new NameSpaceSymbTable();
    if (paramNode1 != null && 1 == paramNode1.getNodeType())
      getParentNameSpaces((Element)paramNode1, nameSpaceSymbTable); 
    if (paramNode1 == null)
      return; 
    Node node = null;
    Element element = null;
    OutputStream outputStream = this.writer;
    byte b = -1;
    HashMap hashMap = new HashMap();
    while (true) {
      Iterator iterator;
      int i;
      String str;
      Element element1;
      switch (paramNode1.getNodeType()) {
        case 2:
        case 6:
        case 12:
          throw new CanonicalizationException("empty");
        case 9:
        case 11:
          nameSpaceSymbTable.outputNodePush();
          node = paramNode1.getFirstChild();
          break;
        case 8:
          if (this.includeComments && isVisibleDO(paramNode1, nameSpaceSymbTable.getLevel()) == 1)
            outputCommentToWriter((Comment)paramNode1, outputStream, b); 
          break;
        case 7:
          if (isVisible(paramNode1))
            outputPItoWriter((ProcessingInstruction)paramNode1, outputStream, b); 
          break;
        case 3:
        case 4:
          if (isVisible(paramNode1)) {
            outputTextToWriter(paramNode1.getNodeValue(), outputStream);
            for (Node node1 = paramNode1.getNextSibling(); node1 != null && (node1.getNodeType() == 3 || node1.getNodeType() == 4); node1 = node1.getNextSibling()) {
              outputTextToWriter(node1.getNodeValue(), outputStream);
              paramNode1 = node1;
              node = paramNode1.getNextSibling();
            } 
          } 
          break;
        case 1:
          b = 0;
          element1 = (Element)paramNode1;
          str = null;
          i = isVisibleDO(paramNode1, nameSpaceSymbTable.getLevel());
          if (i == -1) {
            node = paramNode1.getNextSibling();
            break;
          } 
          bool = (i == 1) ? 1 : 0;
          if (bool) {
            nameSpaceSymbTable.outputNodePush();
            outputStream.write(60);
            str = element1.getTagName();
            UtfHelpper.writeByte(str, outputStream, hashMap);
          } else {
            nameSpaceSymbTable.push();
          } 
          iterator = handleAttributes(element1, nameSpaceSymbTable);
          if (iterator != null)
            while (iterator.hasNext()) {
              Attr attr = (Attr)iterator.next();
              outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), outputStream, hashMap);
            }  
          if (bool)
            outputStream.write(62); 
          node = paramNode1.getFirstChild();
          if (node == null) {
            if (bool) {
              outputStream.write((byte[])END_TAG.clone());
              UtfHelpper.writeByte(str, outputStream, hashMap);
              outputStream.write(62);
              nameSpaceSymbTable.outputNodePop();
            } else {
              nameSpaceSymbTable.pop();
            } 
            if (element != null)
              node = paramNode1.getNextSibling(); 
            break;
          } 
          element = element1;
          break;
      } 
      while (node == null && element != null) {
        if (isVisible(element)) {
          outputStream.write((byte[])END_TAG.clone());
          UtfHelpper.writeByte(((Element)element).getTagName(), outputStream, hashMap);
          outputStream.write(62);
          nameSpaceSymbTable.outputNodePop();
        } else {
          nameSpaceSymbTable.pop();
        } 
        if (element == paramNode2)
          return; 
        node = element.getNextSibling();
        Node node1 = element.getParentNode();
        if (node1 == null || 1 != node1.getNodeType()) {
          node1 = null;
          b = 1;
        } 
      } 
      if (node == null)
        return; 
      paramNode1 = node;
      node = paramNode1.getNextSibling();
    } 
  }
  
  protected int isVisibleDO(Node paramNode, int paramInt) {
    if (this.nodeFilter != null) {
      Iterator iterator = this.nodeFilter.iterator();
      while (iterator.hasNext()) {
        int i = ((NodeFilter)iterator.next()).isNodeIncludeDO(paramNode, paramInt);
        if (i != 1)
          return i; 
      } 
    } 
    return (this.xpathNodeSet != null && !this.xpathNodeSet.contains(paramNode)) ? 0 : 1;
  }
  
  protected int isVisibleInt(Node paramNode) {
    if (this.nodeFilter != null) {
      Iterator iterator = this.nodeFilter.iterator();
      while (iterator.hasNext()) {
        int i = ((NodeFilter)iterator.next()).isNodeInclude(paramNode);
        if (i != 1)
          return i; 
      } 
    } 
    return (this.xpathNodeSet != null && !this.xpathNodeSet.contains(paramNode)) ? 0 : 1;
  }
  
  protected boolean isVisible(Node paramNode) {
    if (this.nodeFilter != null) {
      Iterator iterator = this.nodeFilter.iterator();
      while (iterator.hasNext()) {
        if (((NodeFilter)iterator.next()).isNodeInclude(paramNode) != 1)
          return false; 
      } 
    } 
    return !(this.xpathNodeSet != null && !this.xpathNodeSet.contains(paramNode));
  }
  
  protected void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) {
    if (!paramElement.hasAttributes() && paramElement.getNamespaceURI() == null)
      return; 
    NamedNodeMap namedNodeMap = paramElement.getAttributes();
    int i = namedNodeMap.getLength();
    for (byte b = 0; b < i; b++) {
      Attr attr = (Attr)namedNodeMap.item(b);
      String str1 = attr.getLocalName();
      String str2 = attr.getNodeValue();
      if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI()) && (!"xml".equals(str1) || !"http://www.w3.org/XML/1998/namespace".equals(str2)))
        paramNameSpaceSymbTable.addMapping(str1, str2, attr); 
    } 
    if (paramElement.getNamespaceURI() != null) {
      String str3;
      String str1 = paramElement.getPrefix();
      String str2 = paramElement.getNamespaceURI();
      if (str1 == null || str1.equals("")) {
        str1 = "xmlns";
        str3 = "xmlns";
      } else {
        str3 = "xmlns:" + str1;
      } 
      Attr attr = paramElement.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", str3);
      attr.setValue(str2);
      paramNameSpaceSymbTable.addMapping(str1, str2, attr);
    } 
  }
  
  protected final void getParentNameSpaces(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) {
    Node node1 = paramElement.getParentNode();
    if (node1 == null || 1 != node1.getNodeType())
      return; 
    ArrayList arrayList = new ArrayList();
    for (Node node2 = node1; node2 != null && 1 == node2.getNodeType(); node2 = node2.getParentNode())
      arrayList.add((Element)node2); 
    ListIterator listIterator = arrayList.listIterator(arrayList.size());
    while (listIterator.hasPrevious()) {
      Element element = (Element)listIterator.previous();
      handleParent(element, paramNameSpaceSymbTable);
    } 
    arrayList.clear();
    Attr attr;
    if ((attr = paramNameSpaceSymbTable.getMappingWithoutRendered("xmlns")) != null && "".equals(attr.getValue()))
      paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", getNullNode(attr.getOwnerDocument())); 
  }
  
  abstract Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException;
  
  abstract Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) throws CanonicalizationException;
  
  abstract void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException;
  
  protected static final void outputAttrToWriter(String paramString1, String paramString2, OutputStream paramOutputStream, Map<String, byte[]> paramMap) throws IOException {
    paramOutputStream.write(32);
    UtfHelpper.writeByte(paramString1, paramOutputStream, paramMap);
    paramOutputStream.write((byte[])EQUALS_STR.clone());
    int i = paramString2.length();
    byte b = 0;
    while (b < i) {
      byte[] arrayOfByte;
      char c = paramString2.charAt(b++);
      switch (c) {
        case '&':
          arrayOfByte = (byte[])AMP.clone();
          break;
        case '<':
          arrayOfByte = (byte[])LT.clone();
          break;
        case '"':
          arrayOfByte = (byte[])QUOT.clone();
          break;
        case '\t':
          arrayOfByte = (byte[])X9.clone();
          break;
        case '\n':
          arrayOfByte = (byte[])XA.clone();
          break;
        case '\r':
          arrayOfByte = (byte[])XD.clone();
          break;
        default:
          if (c < '') {
            paramOutputStream.write(c);
            continue;
          } 
          UtfHelpper.writeCharToUtf8(c, paramOutputStream);
          continue;
      } 
      paramOutputStream.write(arrayOfByte);
    } 
    paramOutputStream.write(34);
  }
  
  protected void outputPItoWriter(ProcessingInstruction paramProcessingInstruction, OutputStream paramOutputStream, int paramInt) throws IOException {
    if (paramInt == 1)
      paramOutputStream.write(10); 
    paramOutputStream.write((byte[])BEGIN_PI.clone());
    String str1 = paramProcessingInstruction.getTarget();
    int i = str1.length();
    for (byte b = 0; b < i; b++) {
      char c = str1.charAt(b);
      if (c == '\r') {
        paramOutputStream.write((byte[])XD.clone());
      } else if (c < '') {
        paramOutputStream.write(c);
      } else {
        UtfHelpper.writeCharToUtf8(c, paramOutputStream);
      } 
    } 
    String str2 = paramProcessingInstruction.getData();
    i = str2.length();
    if (i > 0) {
      paramOutputStream.write(32);
      for (byte b1 = 0; b1 < i; b1++) {
        char c = str2.charAt(b1);
        if (c == '\r') {
          paramOutputStream.write((byte[])XD.clone());
        } else {
          UtfHelpper.writeCharToUtf8(c, paramOutputStream);
        } 
      } 
    } 
    paramOutputStream.write((byte[])END_PI.clone());
    if (paramInt == -1)
      paramOutputStream.write(10); 
  }
  
  protected void outputCommentToWriter(Comment paramComment, OutputStream paramOutputStream, int paramInt) throws IOException {
    if (paramInt == 1)
      paramOutputStream.write(10); 
    paramOutputStream.write((byte[])BEGIN_COMM.clone());
    String str = paramComment.getData();
    int i = str.length();
    for (byte b = 0; b < i; b++) {
      char c = str.charAt(b);
      if (c == '\r') {
        paramOutputStream.write((byte[])XD.clone());
      } else if (c < '') {
        paramOutputStream.write(c);
      } else {
        UtfHelpper.writeCharToUtf8(c, paramOutputStream);
      } 
    } 
    paramOutputStream.write((byte[])END_COMM.clone());
    if (paramInt == -1)
      paramOutputStream.write(10); 
  }
  
  protected static final void outputTextToWriter(String paramString, OutputStream paramOutputStream) throws IOException {
    int i = paramString.length();
    for (byte b = 0;; b++) {
      if (b < i) {
        byte[] arrayOfByte;
        char c = paramString.charAt(b);
        switch (c) {
          case '&':
            arrayOfByte = (byte[])AMP.clone();
            break;
          case '<':
            arrayOfByte = (byte[])LT.clone();
            break;
          case '>':
            arrayOfByte = (byte[])GT.clone();
            break;
          case '\r':
            arrayOfByte = (byte[])XD.clone();
            break;
          default:
            if (c < '') {
              paramOutputStream.write(c);
            } else {
              UtfHelpper.writeCharToUtf8(c, paramOutputStream);
            } 
            b++;
            continue;
        } 
        paramOutputStream.write(arrayOfByte);
      } else {
        break;
      } 
    } 
  }
  
  protected Attr getNullNode(Document paramDocument) {
    if (this.nullNode == null)
      try {
        this.nullNode = paramDocument.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
        this.nullNode.setValue("");
      } catch (Exception exception) {
        throw new RuntimeException("Unable to create nullNode: " + exception);
      }  
    return this.nullNode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\CanonicalizerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */