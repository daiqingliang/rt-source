package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;

public class NodeSequence extends XObject implements DTMIterator, Cloneable, PathComponent {
  static final long serialVersionUID = 3866261934726581044L;
  
  protected int m_last = -1;
  
  protected int m_next = 0;
  
  private IteratorCache m_cache;
  
  protected DTMIterator m_iter;
  
  protected DTMManager m_dtmMgr;
  
  protected NodeVector getVector() { return (this.m_cache != null) ? this.m_cache.getVector() : null; }
  
  private IteratorCache getCache() { return this.m_cache; }
  
  protected void SetVector(NodeVector paramNodeVector) { setObject(paramNodeVector); }
  
  public boolean hasCache() {
    NodeVector nodeVector = getVector();
    return (nodeVector != null);
  }
  
  private boolean cacheComplete() {
    boolean bool;
    if (this.m_cache != null) {
      bool = this.m_cache.isComplete();
    } else {
      bool = false;
    } 
    return bool;
  }
  
  private void markCacheComplete() {
    NodeVector nodeVector = getVector();
    if (nodeVector != null)
      this.m_cache.setCacheComplete(true); 
  }
  
  public final void setIter(DTMIterator paramDTMIterator) { this.m_iter = paramDTMIterator; }
  
  public final DTMIterator getContainedIter() { return this.m_iter; }
  
  private NodeSequence(DTMIterator paramDTMIterator, int paramInt, XPathContext paramXPathContext, boolean paramBoolean) {
    setIter(paramDTMIterator);
    setRoot(paramInt, paramXPathContext);
    setShouldCacheNodes(paramBoolean);
  }
  
  public NodeSequence(Object paramObject) {
    super(paramObject);
    if (paramObject instanceof NodeVector)
      SetVector((NodeVector)paramObject); 
    if (null != paramObject) {
      assertion(paramObject instanceof NodeVector, "Must have a NodeVector as the object for NodeSequence!");
      if (paramObject instanceof DTMIterator) {
        setIter((DTMIterator)paramObject);
        this.m_last = ((DTMIterator)paramObject).getLength();
      } 
    } 
  }
  
  private NodeSequence(DTMManager paramDTMManager) {
    super(new NodeVector());
    this.m_last = 0;
    this.m_dtmMgr = paramDTMManager;
  }
  
  public NodeSequence() {}
  
  public DTM getDTM(int paramInt) {
    DTMManager dTMManager = getDTMManager();
    if (null != dTMManager)
      return getDTMManager().getDTM(paramInt); 
    assertion(false, "Can not get a DTM Unless a DTMManager has been set!");
    return null;
  }
  
  public DTMManager getDTMManager() { return this.m_dtmMgr; }
  
  public int getRoot() { return (null != this.m_iter) ? this.m_iter.getRoot() : -1; }
  
  public void setRoot(int paramInt, Object paramObject) {
    if (paramInt == -1)
      throw new RuntimeException("Unable to evaluate expression using this context"); 
    if (null != this.m_iter) {
      XPathContext xPathContext = (XPathContext)paramObject;
      this.m_dtmMgr = xPathContext.getDTMManager();
      this.m_iter.setRoot(paramInt, paramObject);
      if (!this.m_iter.isDocOrdered()) {
        if (!hasCache())
          setShouldCacheNodes(true); 
        runTo(-1);
        this.m_next = 0;
      } 
    } else {
      assertion(false, "Can not setRoot on a non-iterated NodeSequence!");
    } 
  }
  
  public void reset() { this.m_next = 0; }
  
  public int getWhatToShow() { return hasCache() ? -17 : this.m_iter.getWhatToShow(); }
  
  public boolean getExpandEntityReferences() { return (null != this.m_iter) ? this.m_iter.getExpandEntityReferences() : 1; }
  
  public int nextNode() {
    NodeVector nodeVector = getVector();
    if (null != nodeVector) {
      if (this.m_next < nodeVector.size()) {
        int j = nodeVector.elementAt(this.m_next);
        this.m_next++;
        return j;
      } 
      if (cacheComplete() || -1 != this.m_last || null == this.m_iter) {
        this.m_next++;
        return -1;
      } 
    } 
    if (null == this.m_iter)
      return -1; 
    int i = this.m_iter.nextNode();
    if (-1 != i) {
      if (hasCache()) {
        if (this.m_iter.isDocOrdered()) {
          getVector().addElement(i);
          this.m_next++;
        } else {
          int j = addNodeInDocOrder(i);
          if (j >= 0)
            this.m_next++; 
        } 
      } else {
        this.m_next++;
      } 
    } else {
      markCacheComplete();
      this.m_last = this.m_next;
      this.m_next++;
    } 
    return i;
  }
  
  public int previousNode() {
    if (hasCache()) {
      if (this.m_next <= 0)
        return -1; 
      this.m_next--;
      return item(this.m_next);
    } 
    int i = this.m_iter.previousNode();
    this.m_next = this.m_iter.getCurrentPos();
    return this.m_next;
  }
  
  public void detach() {
    if (null != this.m_iter)
      this.m_iter.detach(); 
    super.detach();
  }
  
  public void allowDetachToRelease(boolean paramBoolean) {
    if (false == paramBoolean && !hasCache())
      setShouldCacheNodes(true); 
    if (null != this.m_iter)
      this.m_iter.allowDetachToRelease(paramBoolean); 
    super.allowDetachToRelease(paramBoolean);
  }
  
  public int getCurrentNode() {
    if (hasCache()) {
      int i = this.m_next - 1;
      NodeVector nodeVector = getVector();
      return (i >= 0 && i < nodeVector.size()) ? nodeVector.elementAt(i) : -1;
    } 
    return (null != this.m_iter) ? this.m_iter.getCurrentNode() : -1;
  }
  
  public boolean isFresh() { return (0 == this.m_next); }
  
  public void setShouldCacheNodes(boolean paramBoolean) {
    if (paramBoolean) {
      if (!hasCache())
        SetVector(new NodeVector()); 
    } else {
      SetVector(null);
    } 
  }
  
  public boolean isMutable() { return hasCache(); }
  
  public int getCurrentPos() { return this.m_next; }
  
  public void runTo(int paramInt) {
    if (-1 == paramInt) {
      int j = this.m_next;
      int i;
      while (-1 != (i = nextNode()));
      this.m_next = j;
    } else {
      if (this.m_next == paramInt)
        return; 
      if (hasCache() && paramInt < getVector().size()) {
        this.m_next = paramInt;
      } else if (null == getVector() && paramInt < this.m_next) {
        int i;
        while (this.m_next >= paramInt && -1 != (i = previousNode()));
      } else {
        int i;
        while (this.m_next < paramInt && -1 != (i = nextNode()));
      } 
    } 
  }
  
  public void setCurrentPos(int paramInt) { runTo(paramInt); }
  
  public int item(int paramInt) {
    setCurrentPos(paramInt);
    int i = nextNode();
    this.m_next = paramInt;
    return i;
  }
  
  public void setItem(int paramInt1, int paramInt2) {
    NodeVector nodeVector = getVector();
    if (null != nodeVector) {
      int i = nodeVector.elementAt(paramInt2);
      if (i != paramInt1 && this.m_cache.useCount() > 1) {
        NodeVector nodeVector1;
        IteratorCache iteratorCache = new IteratorCache();
        try {
          nodeVector1 = (NodeVector)nodeVector.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
          cloneNotSupportedException.printStackTrace();
          RuntimeException runtimeException = new RuntimeException(cloneNotSupportedException.getMessage());
          throw runtimeException;
        } 
        iteratorCache.setVector(nodeVector1);
        iteratorCache.setCacheComplete(true);
        this.m_cache = iteratorCache;
        nodeVector = nodeVector1;
        super.setObject(nodeVector1);
      } 
      nodeVector.setElementAt(paramInt1, paramInt2);
      this.m_last = nodeVector.size();
    } else {
      this.m_iter.setItem(paramInt1, paramInt2);
    } 
  }
  
  public int getLength() {
    IteratorCache iteratorCache = getCache();
    if (iteratorCache != null) {
      if (iteratorCache.isComplete()) {
        NodeVector nodeVector = iteratorCache.getVector();
        return nodeVector.size();
      } 
      if (this.m_iter instanceof com.sun.org.apache.xpath.internal.NodeSetDTM)
        return this.m_iter.getLength(); 
      if (-1 == this.m_last) {
        int i = this.m_next;
        runTo(-1);
        this.m_next = i;
      } 
      return this.m_last;
    } 
    return (-1 == this.m_last) ? (this.m_last = this.m_iter.getLength()) : this.m_last;
  }
  
  public DTMIterator cloneWithReset() {
    NodeSequence nodeSequence = (NodeSequence)super.clone();
    nodeSequence.m_next = 0;
    if (this.m_cache != null)
      this.m_cache.increaseUseCount(); 
    return nodeSequence;
  }
  
  public Object clone() throws CloneNotSupportedException {
    NodeSequence nodeSequence = (NodeSequence)super.clone();
    if (null != this.m_iter)
      nodeSequence.m_iter = (DTMIterator)this.m_iter.clone(); 
    if (this.m_cache != null)
      this.m_cache.increaseUseCount(); 
    return nodeSequence;
  }
  
  public boolean isDocOrdered() { return (null != this.m_iter) ? this.m_iter.isDocOrdered() : 1; }
  
  public int getAxis() {
    if (null != this.m_iter)
      return this.m_iter.getAxis(); 
    assertion(false, "Can not getAxis from a non-iterated node sequence!");
    return 0;
  }
  
  public int getAnalysisBits() { return (null != this.m_iter && this.m_iter instanceof PathComponent) ? ((PathComponent)this.m_iter).getAnalysisBits() : 0; }
  
  public void fixupVariables(Vector paramVector, int paramInt) { super.fixupVariables(paramVector, paramInt); }
  
  protected int addNodeInDocOrder(int paramInt) {
    assertion(hasCache(), "addNodeInDocOrder must be done on a mutable sequence!");
    int i = -1;
    NodeVector nodeVector = getVector();
    int j = nodeVector.size();
    int k;
    for (k = j - 1; k >= 0; k--) {
      int m = nodeVector.elementAt(k);
      if (m == paramInt) {
        k = -2;
        break;
      } 
      DTM dTM = this.m_dtmMgr.getDTM(paramInt);
      if (!dTM.isNodeAfter(paramInt, m))
        break; 
    } 
    if (k != -2) {
      i = k + 1;
      nodeVector.insertElementAt(paramInt, i);
    } 
    return i;
  }
  
  protected void setObject(Object paramObject) {
    if (paramObject instanceof NodeVector) {
      super.setObject(paramObject);
      NodeVector nodeVector = (NodeVector)paramObject;
      if (this.m_cache != null) {
        this.m_cache.setVector(nodeVector);
      } else if (nodeVector != null) {
        this.m_cache.setVector(nodeVector);
      } 
    } else if (paramObject instanceof IteratorCache) {
      IteratorCache iteratorCache = (IteratorCache)paramObject;
      this.m_cache.increaseUseCount();
      super.setObject(iteratorCache.getVector());
    } else {
      super.setObject(paramObject);
    } 
  }
  
  protected IteratorCache getIteratorCache() { return this.m_cache; }
  
  private static final class IteratorCache {
    private NodeVector m_vec2 = null;
    
    private boolean m_isComplete2 = false;
    
    private int m_useCount2 = 1;
    
    private int useCount() { return this.m_useCount2; }
    
    private void increaseUseCount() {
      if (this.m_vec2 != null)
        this.m_useCount2++; 
    }
    
    private void setVector(NodeVector param1NodeVector) {
      this.m_vec2 = param1NodeVector;
      this.m_useCount2 = 1;
    }
    
    private NodeVector getVector() { return this.m_vec2; }
    
    private void setCacheComplete(boolean param1Boolean) { this.m_isComplete2 = param1Boolean; }
    
    private boolean isComplete() { return this.m_isComplete2; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\NodeSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */