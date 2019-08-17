package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.NodeIterator;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;

public abstract class NodeIteratorBase implements NodeIterator {
  protected int _last = -1;
  
  protected int _position = 0;
  
  protected int _markedNode;
  
  protected int _startNode = -1;
  
  protected boolean _includeSelf = false;
  
  protected boolean _isRestartable = true;
  
  public void setRestartable(boolean paramBoolean) { this._isRestartable = paramBoolean; }
  
  public abstract NodeIterator setStartNode(int paramInt);
  
  public NodeIterator reset() {
    boolean bool = this._isRestartable;
    this._isRestartable = true;
    setStartNode(this._includeSelf ? (this._startNode + 1) : this._startNode);
    this._isRestartable = bool;
    return this;
  }
  
  public NodeIterator includeSelf() {
    this._includeSelf = true;
    return this;
  }
  
  public int getLast() {
    if (this._last == -1) {
      int i = this._position;
      setMark();
      reset();
      do {
        this._last++;
      } while (next() != -1);
      gotoMark();
      this._position = i;
    } 
    return this._last;
  }
  
  public int getPosition() { return (this._position == 0) ? 1 : this._position; }
  
  public boolean isReverse() { return false; }
  
  public NodeIterator cloneIterator() {
    try {
      NodeIteratorBase nodeIteratorBase = (NodeIteratorBase)clone();
      nodeIteratorBase._isRestartable = false;
      return nodeIteratorBase.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  protected final int returnNode(int paramInt) {
    this._position++;
    return paramInt;
  }
  
  protected final NodeIterator resetPosition() {
    this._position = 0;
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeIteratorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */