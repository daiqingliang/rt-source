package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class NodeSetDTM extends NodeVector implements DTMIterator, Cloneable {
  static final long serialVersionUID = 7686480133331317070L;
  
  DTMManager m_manager;
  
  protected int m_next = 0;
  
  protected boolean m_mutable = true;
  
  protected boolean m_cacheNodes = true;
  
  protected int m_root = -1;
  
  private int m_last = 0;
  
  public NodeSetDTM(DTMManager paramDTMManager) { this.m_manager = paramDTMManager; }
  
  public NodeSetDTM(int paramInt1, int paramInt2, DTMManager paramDTMManager) {
    super(paramInt1);
    this.m_manager = paramDTMManager;
  }
  
  public NodeSetDTM(NodeSetDTM paramNodeSetDTM) {
    this.m_manager = paramNodeSetDTM.getDTMManager();
    this.m_root = paramNodeSetDTM.getRoot();
    addNodes(paramNodeSetDTM);
  }
  
  public NodeSetDTM(DTMIterator paramDTMIterator) {
    this.m_manager = paramDTMIterator.getDTMManager();
    this.m_root = paramDTMIterator.getRoot();
    addNodes(paramDTMIterator);
  }
  
  public NodeSetDTM(NodeIterator paramNodeIterator, XPathContext paramXPathContext) {
    this.m_manager = paramXPathContext.getDTMManager();
    Node node;
    while (null != (node = paramNodeIterator.nextNode())) {
      int i = paramXPathContext.getDTMHandleFromNode(node);
      addNodeInDocOrder(i, paramXPathContext);
    } 
  }
  
  public NodeSetDTM(NodeList paramNodeList, XPathContext paramXPathContext) {
    this.m_manager = paramXPathContext.getDTMManager();
    int i = paramNodeList.getLength();
    for (byte b = 0; b < i; b++) {
      Node node = paramNodeList.item(b);
      int j = paramXPathContext.getDTMHandleFromNode(node);
      addNode(j);
    } 
  }
  
  public NodeSetDTM(int paramInt, DTMManager paramDTMManager) {
    this.m_manager = paramDTMManager;
    addNode(paramInt);
  }
  
  public void setEnvironment(Object paramObject) {}
  
  public int getRoot() { return (-1 == this.m_root) ? ((size() > 0) ? item(0) : -1) : this.m_root; }
  
  public void setRoot(int paramInt, Object paramObject) {}
  
  public Object clone() throws CloneNotSupportedException { return (NodeSetDTM)super.clone(); }
  
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    NodeSetDTM nodeSetDTM = (NodeSetDTM)clone();
    nodeSetDTM.reset();
    return nodeSetDTM;
  }
  
  public void reset() { this.m_next = 0; }
  
  public int getWhatToShow() { return -17; }
  
  public DTMFilter getFilter() { return null; }
  
  public boolean getExpandEntityReferences() { return true; }
  
  public DTM getDTM(int paramInt) { return this.m_manager.getDTM(paramInt); }
  
  public DTMManager getDTMManager() { return this.m_manager; }
  
  public int nextNode() {
    if (this.m_next < size()) {
      int i = elementAt(this.m_next);
      this.m_next++;
      return i;
    } 
    return -1;
  }
  
  public int previousNode() {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null)); 
    if (this.m_next - 1 > 0) {
      this.m_next--;
      return elementAt(this.m_next);
    } 
    return -1;
  }
  
  public void detach() {}
  
  public void allowDetachToRelease(boolean paramBoolean) {}
  
  public boolean isFresh() { return (this.m_next == 0); }
  
  public void runTo(int paramInt) {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_INDEX", null)); 
    if (paramInt >= 0 && this.m_next < this.m_firstFree) {
      this.m_next = paramInt;
    } else {
      this.m_next = this.m_firstFree - 1;
    } 
  }
  
  public int item(int paramInt) {
    runTo(paramInt);
    return elementAt(paramInt);
  }
  
  public int getLength() {
    runTo(-1);
    return size();
  }
  
  public void addNode(int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    addElement(paramInt);
  }
  
  public void insertNode(int paramInt1, int paramInt2) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    insertElementAt(paramInt1, paramInt2);
  }
  
  public void removeNode(int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    removeElement(paramInt);
  }
  
  public void addNodes(DTMIterator paramDTMIterator) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    if (null != paramDTMIterator) {
      int i;
      while (-1 != (i = paramDTMIterator.nextNode()))
        addElement(i); 
    } 
  }
  
  public void addNodesInDocOrder(DTMIterator paramDTMIterator, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    int i;
    while (-1 != (i = paramDTMIterator.nextNode()))
      addNodeInDocOrder(i, paramXPathContext); 
  }
  
  public int addNodeInDocOrder(int paramInt, boolean paramBoolean, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    int i = -1;
    if (paramBoolean) {
      int j = size();
      int k;
      for (k = j - 1; k >= 0; k--) {
        int m = elementAt(k);
        if (m == paramInt) {
          k = -2;
          break;
        } 
        DTM dTM = paramXPathContext.getDTM(paramInt);
        if (!dTM.isNodeAfter(paramInt, m))
          break; 
      } 
      if (k != -2) {
        i = k + 1;
        insertElementAt(paramInt, i);
      } 
    } else {
      i = size();
      boolean bool = false;
      for (byte b = 0; b < i; b++) {
        if (b == paramInt) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        addElement(paramInt); 
    } 
    return i;
  }
  
  public int addNodeInDocOrder(int paramInt, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    return addNodeInDocOrder(paramInt, true, paramXPathContext);
  }
  
  public int size() { return super.size(); }
  
  public void addElement(int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.addElement(paramInt);
  }
  
  public void insertElementAt(int paramInt1, int paramInt2) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.insertElementAt(paramInt1, paramInt2);
  }
  
  public void appendNodes(NodeVector paramNodeVector) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.appendNodes(paramNodeVector);
  }
  
  public void removeAllElements() {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.removeAllElements();
  }
  
  public boolean removeElement(int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    return super.removeElement(paramInt);
  }
  
  public void removeElementAt(int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.removeElementAt(paramInt);
  }
  
  public void setElementAt(int paramInt1, int paramInt2) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.setElementAt(paramInt1, paramInt2);
  }
  
  public void setItem(int paramInt1, int paramInt2) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null)); 
    super.setElementAt(paramInt1, paramInt2);
  }
  
  public int elementAt(int paramInt) {
    runTo(paramInt);
    return super.elementAt(paramInt);
  }
  
  public boolean contains(int paramInt) {
    runTo(-1);
    return super.contains(paramInt);
  }
  
  public int indexOf(int paramInt1, int paramInt2) {
    runTo(-1);
    return super.indexOf(paramInt1, paramInt2);
  }
  
  public int indexOf(int paramInt) {
    runTo(-1);
    return super.indexOf(paramInt);
  }
  
  public int getCurrentPos() { return this.m_next; }
  
  public void setCurrentPos(int paramInt) {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_INDEX", null)); 
    this.m_next = paramInt;
  }
  
  public int getCurrentNode() {
    if (!this.m_cacheNodes)
      throw new RuntimeException("This NodeSetDTM can not do indexing or counting functions!"); 
    int i = this.m_next;
    int j = (this.m_next > 0) ? (this.m_next - 1) : this.m_next;
    int k = (j < this.m_firstFree) ? elementAt(j) : -1;
    this.m_next = i;
    return k;
  }
  
  public boolean getShouldCacheNodes() { return this.m_cacheNodes; }
  
  public void setShouldCacheNodes(boolean paramBoolean) {
    if (!isFresh())
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_CANNOT_CALL_SETSHOULDCACHENODE", null)); 
    this.m_cacheNodes = paramBoolean;
    this.m_mutable = true;
  }
  
  public boolean isMutable() { return this.m_mutable; }
  
  public int getLast() { return this.m_last; }
  
  public void setLast(int paramInt) { this.m_last = paramInt; }
  
  public boolean isDocOrdered() { return true; }
  
  public int getAxis() { return -1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\NodeSetDTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */