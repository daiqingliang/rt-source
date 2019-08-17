package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class ClonedNodeListIterator extends DTMAxisIteratorBase {
  private CachedNodeListIterator _source;
  
  private int _index = 0;
  
  public ClonedNodeListIterator(CachedNodeListIterator paramCachedNodeListIterator) { this._source = paramCachedNodeListIterator; }
  
  public void setRestartable(boolean paramBoolean) {}
  
  public DTMAxisIterator setStartNode(int paramInt) { return this; }
  
  public int next() { return this._source.getNode(this._index++); }
  
  public int getPosition() { return (this._index == 0) ? 1 : this._index; }
  
  public int getNodeByPosition(int paramInt) { return this._source.getNode(paramInt); }
  
  public DTMAxisIterator cloneIterator() { return this._source.cloneIterator(); }
  
  public DTMAxisIterator reset() {
    this._index = 0;
    return this;
  }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ClonedNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */