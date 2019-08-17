package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class AbsoluteIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  public AbsoluteIterator(DTMAxisIterator paramDTMAxisIterator) { this._source = paramDTMAxisIterator; }
  
  public void setRestartable(boolean paramBoolean) {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    this._startNode = 0;
    if (this._isRestartable) {
      this._source.setStartNode(this._startNode);
      resetPosition();
    } 
    return this;
  }
  
  public int next() { return returnNode(this._source.next()); }
  
  public DTMAxisIterator cloneIterator() {
    try {
      AbsoluteIterator absoluteIterator = (AbsoluteIterator)clone();
      absoluteIterator._source = this._source.cloneIterator();
      absoluteIterator.resetPosition();
      absoluteIterator._isRestartable = false;
      return absoluteIterator;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public DTMAxisIterator reset() {
    this._source.reset();
    return resetPosition();
  }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\AbsoluteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */