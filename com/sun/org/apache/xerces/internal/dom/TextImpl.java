package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl extends CharacterDataImpl implements CharacterData, Text {
  static final long serialVersionUID = -5294980852957403469L;
  
  public TextImpl() {}
  
  public TextImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) { super(paramCoreDocumentImpl, paramString); }
  
  public void setValues(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    this.flags = 0;
    this.nextSibling = null;
    this.previousSibling = null;
    setOwnerDocument(paramCoreDocumentImpl);
    this.data = paramString;
  }
  
  public short getNodeType() { return 3; }
  
  public String getNodeName() { return "#text"; }
  
  public void setIgnorableWhitespace(boolean paramBoolean) {
    if (needsSyncData())
      synchronizeData(); 
    isIgnorableWhitespace(paramBoolean);
  }
  
  public boolean isElementContentWhitespace() {
    if (needsSyncData())
      synchronizeData(); 
    return internalIsIgnorableWhitespace();
  }
  
  public String getWholeText() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.fBufferStr == null) {
      this.fBufferStr = new StringBuffer();
    } else {
      this.fBufferStr.setLength(0);
    } 
    if (this.data != null && this.data.length() != 0)
      this.fBufferStr.append(this.data); 
    getWholeTextBackward(getPreviousSibling(), this.fBufferStr, getParentNode());
    String str = this.fBufferStr.toString();
    this.fBufferStr.setLength(0);
    getWholeTextForward(getNextSibling(), this.fBufferStr, getParentNode());
    return str + this.fBufferStr.toString();
  }
  
  protected void insertTextContent(StringBuffer paramStringBuffer) throws DOMException {
    String str = getNodeValue();
    if (str != null)
      paramStringBuffer.insert(0, str); 
  }
  
  private boolean getWholeTextForward(Node paramNode1, StringBuffer paramStringBuffer, Node paramNode2) {
    boolean bool = false;
    if (paramNode2 != null)
      bool = (paramNode2.getNodeType() == 5) ? 1 : 0; 
    while (paramNode1 != null) {
      short s = paramNode1.getNodeType();
      if (s == 5) {
        if (getWholeTextForward(paramNode1.getFirstChild(), paramStringBuffer, paramNode1))
          return true; 
      } else if (s == 3 || s == 4) {
        ((NodeImpl)paramNode1).getTextContent(paramStringBuffer);
      } else {
        return true;
      } 
      paramNode1 = paramNode1.getNextSibling();
    } 
    if (bool) {
      getWholeTextForward(paramNode2.getNextSibling(), paramStringBuffer, paramNode2.getParentNode());
      return true;
    } 
    return false;
  }
  
  private boolean getWholeTextBackward(Node paramNode1, StringBuffer paramStringBuffer, Node paramNode2) {
    boolean bool = false;
    if (paramNode2 != null)
      bool = (paramNode2.getNodeType() == 5) ? 1 : 0; 
    while (paramNode1 != null) {
      short s = paramNode1.getNodeType();
      if (s == 5) {
        if (getWholeTextBackward(paramNode1.getLastChild(), paramStringBuffer, paramNode1))
          return true; 
      } else if (s == 3 || s == 4) {
        ((TextImpl)paramNode1).insertTextContent(paramStringBuffer);
      } else {
        return true;
      } 
      paramNode1 = paramNode1.getPreviousSibling();
    } 
    if (bool) {
      getWholeTextBackward(paramNode2.getPreviousSibling(), paramStringBuffer, paramNode2.getParentNode());
      return true;
    } 
    return false;
  }
  
  public Text replaceWholeText(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    Node node1 = getParentNode();
    if (paramString == null || paramString.length() == 0) {
      if (node1 != null)
        node1.removeChild(this); 
      return null;
    } 
    if ((ownerDocument()).errorChecking) {
      if (!canModifyPrev(this))
        throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
      if (!canModifyNext(this))
        throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
    } 
    Text text = null;
    if (isReadOnly()) {
      Text text1 = ownerDocument().createTextNode(paramString);
      if (node1 != null) {
        node1.insertBefore(text1, this);
        node1.removeChild(this);
        text = text1;
      } else {
        return text1;
      } 
    } else {
      setData(paramString);
      text = this;
    } 
    for (Node node2 = text.getPreviousSibling(); node2 != null && (node2.getNodeType() == 3 || node2.getNodeType() == 4 || (node2.getNodeType() == 5 && hasTextOnlyChildren(node2))); node2 = node2.getPreviousSibling()) {
      node1.removeChild(node2);
      node2 = text;
    } 
    for (Node node3 = text.getNextSibling(); node3 != null && (node3.getNodeType() == 3 || node3.getNodeType() == 4 || (node3.getNodeType() == 5 && hasTextOnlyChildren(node3))); node3 = node3.getNextSibling()) {
      node1.removeChild(node3);
      node3 = text;
    } 
    return text;
  }
  
  private boolean canModifyPrev(Node paramNode) {
    boolean bool = false;
    for (Node node = paramNode.getPreviousSibling(); node != null; node = node.getPreviousSibling()) {
      short s = node.getNodeType();
      if (s == 5) {
        Node node1 = node.getLastChild();
        if (node1 == null)
          return false; 
        while (node1 != null) {
          short s1 = node1.getNodeType();
          if (s1 == 3 || s1 == 4) {
            bool = true;
          } else if (s1 == 5) {
            if (!canModifyPrev(node1))
              return false; 
            bool = true;
          } else {
            return !bool;
          } 
          node1 = node1.getPreviousSibling();
        } 
      } else if (s != 3 && s != 4) {
        return true;
      } 
    } 
    return true;
  }
  
  private boolean canModifyNext(Node paramNode) {
    boolean bool = false;
    for (Node node = paramNode.getNextSibling(); node != null; node = node.getNextSibling()) {
      short s = node.getNodeType();
      if (s == 5) {
        Node node1 = node.getFirstChild();
        if (node1 == null)
          return false; 
        while (node1 != null) {
          short s1 = node1.getNodeType();
          if (s1 == 3 || s1 == 4) {
            bool = true;
          } else if (s1 == 5) {
            if (!canModifyNext(node1))
              return false; 
            bool = true;
          } else {
            return !bool;
          } 
          node1 = node1.getNextSibling();
        } 
      } else if (s != 3 && s != 4) {
        return true;
      } 
    } 
    return true;
  }
  
  private boolean hasTextOnlyChildren(Node paramNode) {
    Node node = paramNode;
    if (node == null)
      return false; 
    for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
      short s = node.getNodeType();
      if (s == 5)
        return hasTextOnlyChildren(node); 
      if (s != 3 && s != 4 && s != 5)
        return false; 
    } 
    return true;
  }
  
  public boolean isIgnorableWhitespace() {
    if (needsSyncData())
      synchronizeData(); 
    return internalIsIgnorableWhitespace();
  }
  
  public Text splitText(int paramInt) throws DOMException {
    if (isReadOnly())
      throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
    if (needsSyncData())
      synchronizeData(); 
    if (paramInt < 0 || paramInt > this.data.length())
      throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null)); 
    Text text = getOwnerDocument().createTextNode(this.data.substring(paramInt));
    setNodeValue(this.data.substring(0, paramInt));
    Node node = getParentNode();
    if (node != null)
      node.insertBefore(text, this.nextSibling); 
    return text;
  }
  
  public void replaceData(String paramString) { this.data = paramString; }
  
  public String removeData() {
    String str = this.data;
    this.data = "";
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\TextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */