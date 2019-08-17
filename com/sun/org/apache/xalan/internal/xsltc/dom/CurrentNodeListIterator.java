package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class CurrentNodeListIterator extends DTMAxisIteratorBase {
  private boolean _docOrder;
  
  private DTMAxisIterator _source;
  
  private final CurrentNodeListFilter _filter;
  
  private IntegerArray _nodes = new IntegerArray();
  
  private int _currentIndex;
  
  private final int _currentNode;
  
  private AbstractTranslet _translet;
  
  public CurrentNodeListIterator(DTMAxisIterator paramDTMAxisIterator, CurrentNodeListFilter paramCurrentNodeListFilter, int paramInt, AbstractTranslet paramAbstractTranslet) { this(paramDTMAxisIterator, !paramDTMAxisIterator.isReverse(), paramCurrentNodeListFilter, paramInt, paramAbstractTranslet); }
  
  public CurrentNodeListIterator(DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean, CurrentNodeListFilter paramCurrentNodeListFilter, int paramInt, AbstractTranslet paramAbstractTranslet) {
    this._source = paramDTMAxisIterator;
    this._filter = paramCurrentNodeListFilter;
    this._translet = paramAbstractTranslet;
    this._docOrder = paramBoolean;
    this._currentNode = paramInt;
  }
  
  public DTMAxisIterator forceNaturalOrder() {
    this._docOrder = true;
    return this;
  }
  
  public void setRestartable(boolean paramBoolean) {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public boolean isReverse() { return !this._docOrder; }
  
  public DTMAxisIterator cloneIterator() {
    try {
      CurrentNodeListIterator currentNodeListIterator = (CurrentNodeListIterator)clone();
      currentNodeListIterator._nodes = (IntegerArray)this._nodes.clone();
      currentNodeListIterator._source = this._source.cloneIterator();
      currentNodeListIterator._isRestartable = false;
      return currentNodeListIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public DTMAxisIterator reset() {
    this._currentIndex = 0;
    return resetPosition();
  }
  
  public int next() {
    int i = this._nodes.cardinality();
    int j = this._currentNode;
    AbstractTranslet abstractTranslet = this._translet;
    int k = this._currentIndex;
    while (k < i) {
      int m = this._docOrder ? (k + 1) : (i - k);
      int n = this._nodes.at(k++);
      if (this._filter.test(n, m, i, j, abstractTranslet, this)) {
        this._currentIndex = k;
        return returnNode(n);
      } 
    } 
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      this._source.setStartNode(this._startNode = paramInt);
      this._nodes.clear();
      while ((paramInt = this._source.next()) != -1)
        this._nodes.add(paramInt); 
      this._currentIndex = 0;
      resetPosition();
    } 
    return this;
  }
  
  public int getLast() {
    if (this._last == -1)
      this._last = computePositionOfLast(); 
    return this._last;
  }
  
  public void setMark() { this._markedNode = this._currentIndex; }
  
  public void gotoMark() { this._currentIndex = this._markedNode; }
  
  private int computePositionOfLast() {
    int i = this._nodes.cardinality();
    int j = this._currentNode;
    AbstractTranslet abstractTranslet = this._translet;
    int k = this._position;
    int m = this._currentIndex;
    while (m < i) {
      int n = this._docOrder ? (m + 1) : (i - m);
      int i1 = this._nodes.at(m++);
      if (this._filter.test(i1, n, i, j, abstractTranslet, this))
        k++; 
    } 
    return k;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CurrentNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */