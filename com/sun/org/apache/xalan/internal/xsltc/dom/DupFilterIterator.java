package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class DupFilterIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  private IntegerArray _nodes = new IntegerArray();
  
  private int _current = 0;
  
  private int _nodesSize = 0;
  
  private int _lastNext = -1;
  
  private int _markedLastNext = -1;
  
  public DupFilterIterator(DTMAxisIterator paramDTMAxisIterator) {
    this._source = paramDTMAxisIterator;
    if (paramDTMAxisIterator instanceof KeyIndex)
      setStartNode(0); 
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      boolean bool = this._source instanceof KeyIndex;
      if (bool && this._startNode == 0)
        return this; 
      if (paramInt != this._startNode) {
        this._source.setStartNode(this._startNode = paramInt);
        this._nodes.clear();
        while ((paramInt = this._source.next()) != -1)
          this._nodes.add(paramInt); 
        if (!bool)
          this._nodes.sort(); 
        this._nodesSize = this._nodes.cardinality();
        this._current = 0;
        this._lastNext = -1;
        resetPosition();
      } 
    } 
    return this;
  }
  
  public int next() {
    while (this._current < this._nodesSize) {
      int i = this._nodes.at(this._current++);
      if (i != this._lastNext)
        return returnNode(this._lastNext = i); 
    } 
    return -1;
  }
  
  public DTMAxisIterator cloneIterator() {
    try {
      DupFilterIterator dupFilterIterator = (DupFilterIterator)clone();
      dupFilterIterator._nodes = (IntegerArray)this._nodes.clone();
      dupFilterIterator._source = this._source.cloneIterator();
      dupFilterIterator._isRestartable = false;
      return dupFilterIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public void setRestartable(boolean paramBoolean) {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public void setMark() {
    this._markedNode = this._current;
    this._markedLastNext = this._lastNext;
  }
  
  public void gotoMark() {
    this._current = this._markedNode;
    this._lastNext = this._markedLastNext;
  }
  
  public DTMAxisIterator reset() {
    this._current = 0;
    this._lastNext = -1;
    return resetPosition();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DupFilterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */