package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class UnionIterator extends MultiValuedNodeHeapIterator {
  private final DOM _dom;
  
  public UnionIterator(DOM paramDOM) { this._dom = paramDOM; }
  
  public UnionIterator addIterator(DTMAxisIterator paramDTMAxisIterator) {
    addHeapNode(new LookAheadIterator(paramDTMAxisIterator));
    return this;
  }
  
  private final class LookAheadIterator extends MultiValuedNodeHeapIterator.HeapNode {
    public DTMAxisIterator iterator;
    
    public LookAheadIterator(DTMAxisIterator param1DTMAxisIterator) {
      super(UnionIterator.this);
      this.iterator = param1DTMAxisIterator;
    }
    
    public int step() {
      this._node = this.iterator.next();
      return this._node;
    }
    
    public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode() {
      LookAheadIterator lookAheadIterator = (LookAheadIterator)super.cloneHeapNode();
      lookAheadIterator.iterator = this.iterator.cloneIterator();
      return lookAheadIterator;
    }
    
    public void setMark() {
      super.setMark();
      this.iterator.setMark();
    }
    
    public void gotoMark() {
      super.gotoMark();
      this.iterator.gotoMark();
    }
    
    public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode param1HeapNode) {
      LookAheadIterator lookAheadIterator = (LookAheadIterator)param1HeapNode;
      return UnionIterator.this._dom.lessThan(this._node, param1HeapNode._node);
    }
    
    public MultiValuedNodeHeapIterator.HeapNode setStartNode(int param1Int) {
      this.iterator.setStartNode(param1Int);
      return this;
    }
    
    public MultiValuedNodeHeapIterator.HeapNode reset() {
      this.iterator.reset();
      return this;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\UnionIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */