package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.ranges.RangeException;

public class RangeImpl implements Range {
  DocumentImpl fDocument;
  
  Node fStartContainer;
  
  Node fEndContainer;
  
  int fStartOffset;
  
  int fEndOffset;
  
  boolean fIsCollapsed;
  
  boolean fDetach = false;
  
  Node fInsertNode = null;
  
  Node fDeleteNode = null;
  
  Node fSplitNode = null;
  
  boolean fInsertedFromRange = false;
  
  Node fRemoveChild = null;
  
  static final int EXTRACT_CONTENTS = 1;
  
  static final int CLONE_CONTENTS = 2;
  
  static final int DELETE_CONTENTS = 3;
  
  public RangeImpl(DocumentImpl paramDocumentImpl) {
    this.fDocument = paramDocumentImpl;
    this.fStartContainer = paramDocumentImpl;
    this.fEndContainer = paramDocumentImpl;
    this.fStartOffset = 0;
    this.fEndOffset = 0;
    this.fDetach = false;
  }
  
  public Node getStartContainer() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    return this.fStartContainer;
  }
  
  public int getStartOffset() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    return this.fStartOffset;
  }
  
  public Node getEndContainer() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    return this.fEndContainer;
  }
  
  public int getEndOffset() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    return this.fEndOffset;
  }
  
  public boolean getCollapsed() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    return (this.fStartContainer == this.fEndContainer && this.fStartOffset == this.fEndOffset);
  }
  
  public Node getCommonAncestorContainer() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    Vector vector1 = new Vector();
    Node node;
    for (node = this.fStartContainer; node != null; node = node.getParentNode())
      vector1.addElement(node); 
    Vector vector2 = new Vector();
    for (node = this.fEndContainer; node != null; node = node.getParentNode())
      vector2.addElement(node); 
    int i = vector1.size() - 1;
    int j = vector2.size() - 1;
    Object object = null;
    while (i >= 0 && j >= 0 && vector1.elementAt(i) == vector2.elementAt(j)) {
      object = vector1.elementAt(i);
      i--;
      j--;
    } 
    return (Node)object;
  }
  
  public void setStart(Node paramNode, int paramInt) throws RangeException, DOMException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!isLegalContainer(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    checkIndex(paramNode, paramInt);
    this.fStartContainer = paramNode;
    this.fStartOffset = paramInt;
    if (getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset))
      collapse(true); 
  }
  
  public void setEnd(Node paramNode, int paramInt) throws RangeException, DOMException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!isLegalContainer(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    checkIndex(paramNode, paramInt);
    this.fEndContainer = paramNode;
    this.fEndOffset = paramInt;
    if (getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset))
      collapse(false); 
  }
  
  public void setStartBefore(Node paramNode) throws RangeException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!hasLegalRootContainer(paramNode) || !isLegalContainedNode(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    this.fStartContainer = paramNode.getParentNode();
    byte b = 0;
    for (Node node = paramNode; node != null; node = node.getPreviousSibling())
      b++; 
    this.fStartOffset = b - 1;
    if (getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset))
      collapse(true); 
  }
  
  public void setStartAfter(Node paramNode) throws RangeException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!hasLegalRootContainer(paramNode) || !isLegalContainedNode(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    this.fStartContainer = paramNode.getParentNode();
    byte b = 0;
    for (Node node = paramNode; node != null; node = node.getPreviousSibling())
      b++; 
    this.fStartOffset = b;
    if (getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset))
      collapse(true); 
  }
  
  public void setEndBefore(Node paramNode) throws RangeException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!hasLegalRootContainer(paramNode) || !isLegalContainedNode(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    this.fEndContainer = paramNode.getParentNode();
    byte b = 0;
    for (Node node = paramNode; node != null; node = node.getPreviousSibling())
      b++; 
    this.fEndOffset = b - 1;
    if (getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset))
      collapse(false); 
  }
  
  public void setEndAfter(Node paramNode) throws RangeException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!hasLegalRootContainer(paramNode) || !isLegalContainedNode(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    this.fEndContainer = paramNode.getParentNode();
    byte b = 0;
    for (Node node = paramNode; node != null; node = node.getPreviousSibling())
      b++; 
    this.fEndOffset = b;
    if (getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset))
      collapse(false); 
  }
  
  public void collapse(boolean paramBoolean) {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    if (paramBoolean) {
      this.fEndContainer = this.fStartContainer;
      this.fEndOffset = this.fStartOffset;
    } else {
      this.fStartContainer = this.fEndContainer;
      this.fStartOffset = this.fEndOffset;
    } 
  }
  
  public void selectNode(Node paramNode) throws RangeException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!isLegalContainer(paramNode.getParentNode()) || !isLegalContainedNode(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    Node node = paramNode.getParentNode();
    if (node != null) {
      this.fStartContainer = node;
      this.fEndContainer = node;
      byte b = 0;
      for (Node node1 = paramNode; node1 != null; node1 = node1.getPreviousSibling())
        b++; 
      this.fStartOffset = b - 1;
      this.fEndOffset = this.fStartOffset + 1;
    } 
  }
  
  public void selectNodeContents(Node paramNode) throws RangeException {
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (!isLegalContainer(paramNode))
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument() && this.fDocument != paramNode)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    this.fStartContainer = paramNode;
    this.fEndContainer = paramNode;
    Node node = paramNode.getFirstChild();
    this.fStartOffset = 0;
    if (node == null) {
      this.fEndOffset = 0;
    } else {
      byte b = 0;
      for (Node node1 = node; node1 != null; node1 = node1.getNextSibling())
        b++; 
      this.fEndOffset = b;
    } 
  }
  
  public short compareBoundaryPoints(short paramShort, Range paramRange) throws DOMException {
    int j;
    int i;
    Node node2;
    Node node1;
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if ((this.fDocument != paramRange.getStartContainer().getOwnerDocument() && this.fDocument != paramRange.getStartContainer() && paramRange.getStartContainer() != null) || (this.fDocument != paramRange.getEndContainer().getOwnerDocument() && this.fDocument != paramRange.getEndContainer() && paramRange.getStartContainer() != null))
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
    } 
    if (paramShort == 0) {
      node1 = paramRange.getStartContainer();
      node2 = this.fStartContainer;
      i = paramRange.getStartOffset();
      j = this.fStartOffset;
    } else if (paramShort == 1) {
      node1 = paramRange.getStartContainer();
      node2 = this.fEndContainer;
      i = paramRange.getStartOffset();
      j = this.fEndOffset;
    } else if (paramShort == 3) {
      node1 = paramRange.getEndContainer();
      node2 = this.fStartContainer;
      i = paramRange.getEndOffset();
      j = this.fStartOffset;
    } else {
      node1 = paramRange.getEndContainer();
      node2 = this.fEndContainer;
      i = paramRange.getEndOffset();
      j = this.fEndOffset;
    } 
    if (node1 == node2)
      return (i < j) ? 1 : ((i == j) ? 0 : -1); 
    Node node3 = node2;
    Node node4;
    for (node4 = node3.getParentNode(); node4 != null; node4 = node4.getParentNode()) {
      if (node4 == node1) {
        int k = indexOf(node3, node1);
        return (i <= k) ? 1 : -1;
      } 
      node3 = node4;
    } 
    node3 = node1;
    for (node4 = node3.getParentNode(); node4 != null; node4 = node4.getParentNode()) {
      if (node4 == node2) {
        int k = indexOf(node3, node2);
        return (k < j) ? 1 : -1;
      } 
      node3 = node4;
    } 
    byte b = 0;
    for (node4 = node1; node4 != null; node4 = node4.getParentNode())
      b++; 
    for (node4 = node2; node4 != null; node4 = node4.getParentNode())
      b--; 
    while (b > 0) {
      node1 = node1.getParentNode();
      b--;
    } 
    while (b < 0) {
      node2 = node2.getParentNode();
      b++;
    } 
    node4 = node1.getParentNode();
    for (Node node5 = node2.getParentNode(); node4 != node5; node5 = node5.getParentNode()) {
      node1 = node4;
      node2 = node5;
      node4 = node4.getParentNode();
    } 
    for (node4 = node1.getNextSibling(); node4 != null; node4 = node4.getNextSibling()) {
      if (node4 == node2)
        return 1; 
    } 
    return -1;
  }
  
  public void deleteContents() throws DOMException { traverseContents(3); }
  
  public DocumentFragment extractContents() throws DOMException { return traverseContents(1); }
  
  public DocumentFragment cloneContents() throws DOMException { return traverseContents(2); }
  
  public void insertNode(Node paramNode) throws RangeException {
    if (paramNode == null)
      return; 
    short s = paramNode.getNodeType();
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (this.fDocument != paramNode.getOwnerDocument())
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
      if (s == 2 || s == 6 || s == 12 || s == 9)
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
    } 
    int i = 0;
    this.fInsertedFromRange = true;
    if (this.fStartContainer.getNodeType() == 3) {
      Node node2 = this.fStartContainer.getParentNode();
      i = node2.getChildNodes().getLength();
      Node node1 = this.fStartContainer.cloneNode(false);
      ((TextImpl)node1).setNodeValueInternal(node1.getNodeValue().substring(this.fStartOffset));
      ((TextImpl)this.fStartContainer).setNodeValueInternal(this.fStartContainer.getNodeValue().substring(0, this.fStartOffset));
      Node node3 = this.fStartContainer.getNextSibling();
      if (node3 != null) {
        if (node2 != null) {
          node2.insertBefore(paramNode, node3);
          node2.insertBefore(node1, node3);
        } 
      } else if (node2 != null) {
        node2.appendChild(paramNode);
        node2.appendChild(node1);
      } 
      if (this.fEndContainer == this.fStartContainer) {
        this.fEndContainer = node1;
        this.fEndOffset -= this.fStartOffset;
      } else if (this.fEndContainer == node2) {
        this.fEndOffset += node2.getChildNodes().getLength() - i;
      } 
      signalSplitData(this.fStartContainer, node1, this.fStartOffset);
    } else {
      if (this.fEndContainer == this.fStartContainer)
        i = this.fEndContainer.getChildNodes().getLength(); 
      Node node = this.fStartContainer.getFirstChild();
      byte b = 0;
      for (b = 0; b < this.fStartOffset && node != null; b++)
        node = node.getNextSibling(); 
      if (node != null) {
        this.fStartContainer.insertBefore(paramNode, node);
      } else {
        this.fStartContainer.appendChild(paramNode);
      } 
      if (this.fEndContainer == this.fStartContainer && this.fEndOffset != 0)
        this.fEndOffset += this.fEndContainer.getChildNodes().getLength() - i; 
    } 
    this.fInsertedFromRange = false;
  }
  
  public void surroundContents(Node paramNode) throws RangeException {
    if (paramNode == null)
      return; 
    short s = paramNode.getNodeType();
    if (this.fDocument.errorChecking) {
      if (this.fDetach)
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
      if (s == 2 || s == 6 || s == 12 || s == 10 || s == 9 || s == 11)
        throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null)); 
    } 
    Node node1 = this.fStartContainer;
    Node node2 = this.fEndContainer;
    if (this.fStartContainer.getNodeType() == 3)
      node1 = this.fStartContainer.getParentNode(); 
    if (this.fEndContainer.getNodeType() == 3)
      node2 = this.fEndContainer.getParentNode(); 
    if (node1 != node2)
      throw new RangeExceptionImpl((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "BAD_BOUNDARYPOINTS_ERR", null)); 
    DocumentFragment documentFragment = extractContents();
    insertNode(paramNode);
    paramNode.appendChild(documentFragment);
    selectNode(paramNode);
  }
  
  public Range cloneRange() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    Range range = this.fDocument.createRange();
    range.setStart(this.fStartContainer, this.fStartOffset);
    range.setEnd(this.fEndContainer, this.fEndOffset);
    return range;
  }
  
  public String toString() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    Node node1 = this.fStartContainer;
    Node node2 = this.fEndContainer;
    StringBuffer stringBuffer = new StringBuffer();
    if (this.fStartContainer.getNodeType() == 3 || this.fStartContainer.getNodeType() == 4) {
      if (this.fStartContainer == this.fEndContainer) {
        stringBuffer.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset, this.fEndOffset));
        return stringBuffer.toString();
      } 
      stringBuffer.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset));
      node1 = nextNode(node1, true);
    } else {
      node1 = node1.getFirstChild();
      if (this.fStartOffset > 0)
        for (byte b = 0; b < this.fStartOffset && node1 != null; b++)
          node1 = node1.getNextSibling();  
      if (node1 == null)
        node1 = nextNode(this.fStartContainer, false); 
    } 
    if (this.fEndContainer.getNodeType() != 3 && this.fEndContainer.getNodeType() != 4) {
      int i = this.fEndOffset;
      for (node2 = this.fEndContainer.getFirstChild(); i > 0 && node2 != null; node2 = node2.getNextSibling())
        i--; 
      if (node2 == null)
        node2 = nextNode(this.fEndContainer, false); 
    } 
    while (node1 != node2 && node1 != null) {
      if (node1.getNodeType() == 3 || node1.getNodeType() == 4)
        stringBuffer.append(node1.getNodeValue()); 
      node1 = nextNode(node1, true);
    } 
    if (this.fEndContainer.getNodeType() == 3 || this.fEndContainer.getNodeType() == 4)
      stringBuffer.append(this.fEndContainer.getNodeValue().substring(0, this.fEndOffset)); 
    return stringBuffer.toString();
  }
  
  public void detach() throws DOMException {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    this.fDetach = true;
    this.fDocument.removeRange(this);
  }
  
  void signalSplitData(Node paramNode1, Node paramNode2, int paramInt) {
    this.fSplitNode = paramNode1;
    this.fDocument.splitData(paramNode1, paramNode2, paramInt);
    this.fSplitNode = null;
  }
  
  void receiveSplitData(Node paramNode1, Node paramNode2, int paramInt) {
    if (paramNode1 == null || paramNode2 == null)
      return; 
    if (this.fSplitNode == paramNode1)
      return; 
    if (paramNode1 == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && this.fStartOffset > paramInt) {
      this.fStartOffset -= paramInt;
      this.fStartContainer = paramNode2;
    } 
    if (paramNode1 == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && this.fEndOffset > paramInt) {
      this.fEndOffset -= paramInt;
      this.fEndContainer = paramNode2;
    } 
  }
  
  void deleteData(CharacterData paramCharacterData, int paramInt1, int paramInt2) {
    this.fDeleteNode = paramCharacterData;
    paramCharacterData.deleteData(paramInt1, paramInt2);
    this.fDeleteNode = null;
  }
  
  void receiveDeletedText(Node paramNode, int paramInt1, int paramInt2) {
    if (paramNode == null)
      return; 
    if (this.fDeleteNode == paramNode)
      return; 
    if (paramNode == this.fStartContainer && this.fStartContainer.getNodeType() == 3)
      if (this.fStartOffset > paramInt1 + paramInt2) {
        this.fStartOffset = paramInt1 + this.fStartOffset - paramInt1 + paramInt2;
      } else if (this.fStartOffset > paramInt1) {
        this.fStartOffset = paramInt1;
      }  
    if (paramNode == this.fEndContainer && this.fEndContainer.getNodeType() == 3)
      if (this.fEndOffset > paramInt1 + paramInt2) {
        this.fEndOffset = paramInt1 + this.fEndOffset - paramInt1 + paramInt2;
      } else if (this.fEndOffset > paramInt1) {
        this.fEndOffset = paramInt1;
      }  
  }
  
  void insertData(CharacterData paramCharacterData, int paramInt, String paramString) {
    this.fInsertNode = paramCharacterData;
    paramCharacterData.insertData(paramInt, paramString);
    this.fInsertNode = null;
  }
  
  void receiveInsertedText(Node paramNode, int paramInt1, int paramInt2) {
    if (paramNode == null)
      return; 
    if (this.fInsertNode == paramNode)
      return; 
    if (paramNode == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && paramInt1 < this.fStartOffset)
      this.fStartOffset += paramInt2; 
    if (paramNode == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && paramInt1 < this.fEndOffset)
      this.fEndOffset += paramInt2; 
  }
  
  void receiveReplacedText(Node paramNode) throws RangeException {
    if (paramNode == null)
      return; 
    if (paramNode == this.fStartContainer && this.fStartContainer.getNodeType() == 3)
      this.fStartOffset = 0; 
    if (paramNode == this.fEndContainer && this.fEndContainer.getNodeType() == 3)
      this.fEndOffset = 0; 
  }
  
  public void insertedNodeFromDOM(Node paramNode) throws RangeException {
    if (paramNode == null)
      return; 
    if (this.fInsertNode == paramNode)
      return; 
    if (this.fInsertedFromRange)
      return; 
    Node node = paramNode.getParentNode();
    if (node == this.fStartContainer) {
      int i = indexOf(paramNode, this.fStartContainer);
      if (i < this.fStartOffset)
        this.fStartOffset++; 
    } 
    if (node == this.fEndContainer) {
      int i = indexOf(paramNode, this.fEndContainer);
      if (i < this.fEndOffset)
        this.fEndOffset++; 
    } 
  }
  
  Node removeChild(Node paramNode1, Node paramNode2) {
    this.fRemoveChild = paramNode2;
    Node node = paramNode1.removeChild(paramNode2);
    this.fRemoveChild = null;
    return node;
  }
  
  void removeNode(Node paramNode) throws RangeException {
    if (paramNode == null)
      return; 
    if (this.fRemoveChild == paramNode)
      return; 
    Node node = paramNode.getParentNode();
    if (node == this.fStartContainer) {
      int i = indexOf(paramNode, this.fStartContainer);
      if (i < this.fStartOffset)
        this.fStartOffset--; 
    } 
    if (node == this.fEndContainer) {
      int i = indexOf(paramNode, this.fEndContainer);
      if (i < this.fEndOffset)
        this.fEndOffset--; 
    } 
    if (node != this.fStartContainer || node != this.fEndContainer) {
      if (isAncestorOf(paramNode, this.fStartContainer)) {
        this.fStartContainer = node;
        this.fStartOffset = indexOf(paramNode, node);
      } 
      if (isAncestorOf(paramNode, this.fEndContainer)) {
        this.fEndContainer = node;
        this.fEndOffset = indexOf(paramNode, node);
      } 
    } 
  }
  
  private DocumentFragment traverseContents(int paramInt) throws DOMException {
    if (this.fStartContainer == null || this.fEndContainer == null)
      return null; 
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    if (this.fStartContainer == this.fEndContainer)
      return traverseSameContainer(paramInt); 
    byte b1 = 0;
    Node node1 = this.fEndContainer;
    Node node2;
    for (node2 = node1.getParentNode(); node2 != null; node2 = node2.getParentNode()) {
      if (node2 == this.fStartContainer)
        return traverseCommonStartContainer(node1, paramInt); 
      b1++;
      node1 = node2;
    } 
    byte b2 = 0;
    node2 = this.fStartContainer;
    Node node3;
    for (node3 = node2.getParentNode(); node3 != null; node3 = node3.getParentNode()) {
      if (node3 == this.fEndContainer)
        return traverseCommonEndContainer(node2, paramInt); 
      b2++;
      node2 = node3;
    } 
    byte b3 = b2 - b1;
    node3 = this.fStartContainer;
    while (b3 > 0) {
      node3 = node3.getParentNode();
      b3--;
    } 
    Node node4 = this.fEndContainer;
    while (b3 < 0) {
      node4 = node4.getParentNode();
      b3++;
    } 
    Node node5 = node3.getParentNode();
    for (Node node6 = node4.getParentNode(); node5 != node6; node6 = node6.getParentNode()) {
      node3 = node5;
      node4 = node6;
      node5 = node5.getParentNode();
    } 
    return traverseCommonAncestors(node3, node4, paramInt);
  }
  
  private DocumentFragment traverseSameContainer(int paramInt) throws DOMException {
    DocumentFragment documentFragment = null;
    if (paramInt != 3)
      documentFragment = this.fDocument.createDocumentFragment(); 
    if (this.fStartOffset == this.fEndOffset)
      return documentFragment; 
    if (this.fStartContainer.getNodeType() == 3) {
      String str1 = this.fStartContainer.getNodeValue();
      String str2 = str1.substring(this.fStartOffset, this.fEndOffset);
      if (paramInt != 2) {
        ((TextImpl)this.fStartContainer).deleteData(this.fStartOffset, this.fEndOffset - this.fStartOffset);
        collapse(true);
      } 
      if (paramInt == 3)
        return null; 
      documentFragment.appendChild(this.fDocument.createTextNode(str2));
      return documentFragment;
    } 
    Node node = getSelectedNode(this.fStartContainer, this.fStartOffset);
    int i = this.fEndOffset - this.fStartOffset;
    while (i > 0) {
      Node node1 = node.getNextSibling();
      Node node2 = traverseFullySelected(node, paramInt);
      if (documentFragment != null)
        documentFragment.appendChild(node2); 
      i--;
      node = node1;
    } 
    if (paramInt != 2)
      collapse(true); 
    return documentFragment;
  }
  
  private DocumentFragment traverseCommonStartContainer(Node paramNode, int paramInt) {
    DocumentFragment documentFragment = null;
    if (paramInt != 3)
      documentFragment = this.fDocument.createDocumentFragment(); 
    Node node = traverseRightBoundary(paramNode, paramInt);
    if (documentFragment != null)
      documentFragment.appendChild(node); 
    int i = indexOf(paramNode, this.fStartContainer);
    int j = i - this.fStartOffset;
    if (j <= 0) {
      if (paramInt != 2) {
        setEndBefore(paramNode);
        collapse(false);
      } 
      return documentFragment;
    } 
    for (node = paramNode.getPreviousSibling(); j > 0; node = node1) {
      Node node1 = node.getPreviousSibling();
      Node node2 = traverseFullySelected(node, paramInt);
      if (documentFragment != null)
        documentFragment.insertBefore(node2, documentFragment.getFirstChild()); 
      j--;
    } 
    if (paramInt != 2) {
      setEndBefore(paramNode);
      collapse(false);
    } 
    return documentFragment;
  }
  
  private DocumentFragment traverseCommonEndContainer(Node paramNode, int paramInt) {
    DocumentFragment documentFragment = null;
    if (paramInt != 3)
      documentFragment = this.fDocument.createDocumentFragment(); 
    Node node = traverseLeftBoundary(paramNode, paramInt);
    if (documentFragment != null)
      documentFragment.appendChild(node); 
    int i = indexOf(paramNode, this.fEndContainer);
    int j = this.fEndOffset - ++i;
    for (node = paramNode.getNextSibling(); j > 0; node = node1) {
      Node node1 = node.getNextSibling();
      Node node2 = traverseFullySelected(node, paramInt);
      if (documentFragment != null)
        documentFragment.appendChild(node2); 
      j--;
    } 
    if (paramInt != 2) {
      setStartAfter(paramNode);
      collapse(true);
    } 
    return documentFragment;
  }
  
  private DocumentFragment traverseCommonAncestors(Node paramNode1, Node paramNode2, int paramInt) {
    DocumentFragment documentFragment = null;
    if (paramInt != 3)
      documentFragment = this.fDocument.createDocumentFragment(); 
    Node node1 = traverseLeftBoundary(paramNode1, paramInt);
    if (documentFragment != null)
      documentFragment.appendChild(node1); 
    Node node2 = paramNode1.getParentNode();
    int i = indexOf(paramNode1, node2);
    int j = indexOf(paramNode2, node2);
    int k = j - ++i;
    Node node3 = paramNode1.getNextSibling();
    while (k > 0) {
      Node node = node3.getNextSibling();
      node1 = traverseFullySelected(node3, paramInt);
      if (documentFragment != null)
        documentFragment.appendChild(node1); 
      node3 = node;
      k--;
    } 
    node1 = traverseRightBoundary(paramNode2, paramInt);
    if (documentFragment != null)
      documentFragment.appendChild(node1); 
    if (paramInt != 2) {
      setStartAfter(paramNode1);
      collapse(true);
    } 
    return documentFragment;
  }
  
  private Node traverseRightBoundary(Node paramNode, int paramInt) {
    Node node1 = getSelectedNode(this.fEndContainer, this.fEndOffset - 1);
    boolean bool = (node1 != this.fEndContainer);
    if (node1 == paramNode)
      return traverseNode(node1, bool, false, paramInt); 
    Node node2 = node1.getParentNode();
    for (Node node3 = traverseNode(node2, false, false, paramInt); node2 != null; node3 = node) {
      while (node1 != null) {
        Node node4 = node1.getPreviousSibling();
        Node node5 = traverseNode(node1, bool, false, paramInt);
        if (paramInt != 3)
          node3.insertBefore(node5, node3.getFirstChild()); 
        bool = true;
        node1 = node4;
      } 
      if (node2 == paramNode)
        return node3; 
      node1 = node2.getPreviousSibling();
      node2 = node2.getParentNode();
      Node node = traverseNode(node2, false, false, paramInt);
      if (paramInt != 3)
        node.appendChild(node3); 
    } 
    return null;
  }
  
  private Node traverseLeftBoundary(Node paramNode, int paramInt) {
    Node node1 = getSelectedNode(getStartContainer(), getStartOffset());
    boolean bool = (node1 != getStartContainer());
    if (node1 == paramNode)
      return traverseNode(node1, bool, true, paramInt); 
    Node node2 = node1.getParentNode();
    for (Node node3 = traverseNode(node2, false, true, paramInt); node2 != null; node3 = node) {
      while (node1 != null) {
        Node node4 = node1.getNextSibling();
        Node node5 = traverseNode(node1, bool, true, paramInt);
        if (paramInt != 3)
          node3.appendChild(node5); 
        bool = true;
        node1 = node4;
      } 
      if (node2 == paramNode)
        return node3; 
      node1 = node2.getNextSibling();
      node2 = node2.getParentNode();
      Node node = traverseNode(node2, false, true, paramInt);
      if (paramInt != 3)
        node.appendChild(node3); 
    } 
    return null;
  }
  
  private Node traverseNode(Node paramNode, boolean paramBoolean1, boolean paramBoolean2, int paramInt) { return paramBoolean1 ? traverseFullySelected(paramNode, paramInt) : ((paramNode.getNodeType() == 3) ? traverseTextNode(paramNode, paramBoolean2, paramInt) : traversePartiallySelected(paramNode, paramInt)); }
  
  private Node traverseFullySelected(Node paramNode, int paramInt) {
    switch (paramInt) {
      case 2:
        return paramNode.cloneNode(true);
      case 1:
        if (paramNode.getNodeType() == 10)
          throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
        return paramNode;
      case 3:
        paramNode.getParentNode().removeChild(paramNode);
        return null;
    } 
    return null;
  }
  
  private Node traversePartiallySelected(Node paramNode, int paramInt) {
    switch (paramInt) {
      case 3:
        return null;
      case 1:
      case 2:
        return paramNode.cloneNode(false);
    } 
    return null;
  }
  
  private Node traverseTextNode(Node paramNode, boolean paramBoolean, int paramInt) {
    String str3;
    String str2;
    String str1 = paramNode.getNodeValue();
    if (paramBoolean) {
      int i = getStartOffset();
      str2 = str1.substring(i);
      str3 = str1.substring(0, i);
    } else {
      int i = getEndOffset();
      str2 = str1.substring(0, i);
      str3 = str1.substring(i);
    } 
    if (paramInt != 2)
      paramNode.setNodeValue(str3); 
    if (paramInt == 3)
      return null; 
    Node node = paramNode.cloneNode(false);
    node.setNodeValue(str2);
    return node;
  }
  
  void checkIndex(Node paramNode, int paramInt) throws RangeException, DOMException {
    if (paramInt < 0)
      throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null)); 
    short s = paramNode.getNodeType();
    if (s == 3 || s == 4 || s == 8 || s == 7) {
      if (paramInt > paramNode.getNodeValue().length())
        throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null)); 
    } else if (paramInt > paramNode.getChildNodes().getLength()) {
      throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
    } 
  }
  
  private Node getRootContainer(Node paramNode) {
    if (paramNode == null)
      return null; 
    while (paramNode.getParentNode() != null)
      paramNode = paramNode.getParentNode(); 
    return paramNode;
  }
  
  private boolean isLegalContainer(Node paramNode) {
    if (paramNode == null)
      return false; 
    while (paramNode != null) {
      switch (paramNode.getNodeType()) {
        case 6:
        case 10:
        case 12:
          return false;
      } 
      paramNode = paramNode.getParentNode();
    } 
    return true;
  }
  
  private boolean hasLegalRootContainer(Node paramNode) {
    if (paramNode == null)
      return false; 
    Node node = getRootContainer(paramNode);
    switch (node.getNodeType()) {
      case 2:
      case 9:
      case 11:
        return true;
    } 
    return false;
  }
  
  private boolean isLegalContainedNode(Node paramNode) {
    if (paramNode == null)
      return false; 
    switch (paramNode.getNodeType()) {
      case 2:
      case 6:
      case 9:
      case 11:
      case 12:
        return false;
    } 
    return true;
  }
  
  Node nextNode(Node paramNode, boolean paramBoolean) {
    if (paramNode == null)
      return null; 
    if (paramBoolean) {
      Node node = paramNode.getFirstChild();
      if (node != null)
        return node; 
    } 
    Node node1 = paramNode.getNextSibling();
    if (node1 != null)
      return node1; 
    for (Node node2 = paramNode.getParentNode(); node2 != null && node2 != this.fDocument; node2 = node2.getParentNode()) {
      node1 = node2.getNextSibling();
      if (node1 != null)
        return node1; 
    } 
    return null;
  }
  
  boolean isAncestorOf(Node paramNode1, Node paramNode2) {
    for (Node node = paramNode2; node != null; node = node.getParentNode()) {
      if (node == paramNode1)
        return true; 
    } 
    return false;
  }
  
  int indexOf(Node paramNode1, Node paramNode2) {
    if (paramNode1.getParentNode() != paramNode2)
      return -1; 
    byte b = 0;
    for (Node node = paramNode2.getFirstChild(); node != paramNode1; node = node.getNextSibling())
      b++; 
    return b;
  }
  
  private Node getSelectedNode(Node paramNode, int paramInt) {
    if (paramNode.getNodeType() == 3)
      return paramNode; 
    if (paramInt < 0)
      return paramNode; 
    Node node;
    for (node = paramNode.getFirstChild(); node != null && paramInt > 0; node = node.getNextSibling())
      paramInt--; 
    return (node != null) ? node : paramNode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\RangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */