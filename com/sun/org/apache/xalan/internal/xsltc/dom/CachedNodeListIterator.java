package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class CachedNodeListIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  private IntegerArray _nodes = new IntegerArray();
  
  private int _numCachedNodes = 0;
  
  private int _index = 0;
  
  private boolean _isEnded = false;
  
  public CachedNodeListIterator(DTMAxisIterator paramDTMAxisIterator) { this._source = paramDTMAxisIterator; }
  
  public void setRestartable(boolean paramBoolean) {}
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      this._startNode = paramInt;
      this._source.setStartNode(paramInt);
      resetPosition();
      this._isRestartable = false;
    } 
    return this;
  }
  
  public int next() { return getNode(this._index++); }
  
  public int getPosition() { return (this._index == 0) ? 1 : this._index; }
  
  public int getNodeByPosition(int paramInt) { return getNode(paramInt); }
  
  public int getNode(int paramInt) {
    if (paramInt < this._numCachedNodes)
      return this._nodes.at(paramInt); 
    if (!this._isEnded) {
      int i = this._source.next();
      if (i != -1) {
        this._nodes.add(i);
        this._numCachedNodes++;
      } else {
        this._isEnded = true;
      } 
      return i;
    } 
    return -1;
  }
  
  public DTMAxisIterator cloneIterator() { return new ClonedNodeListIterator(this); }
  
  public DTMAxisIterator reset() {
    this._index = 0;
    return this;
  }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CachedNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */