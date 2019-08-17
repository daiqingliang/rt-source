package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class ForwardPositionIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  public ForwardPositionIterator(DTMAxisIterator paramDTMAxisIterator) { this._source = paramDTMAxisIterator; }
  
  public DTMAxisIterator cloneIterator() {
    try {
      ForwardPositionIterator forwardPositionIterator = (ForwardPositionIterator)clone();
      forwardPositionIterator._source = this._source.cloneIterator();
      forwardPositionIterator._isRestartable = false;
      return forwardPositionIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public int next() { return returnNode(this._source.next()); }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    this._source.setStartNode(paramInt);
    return this;
  }
  
  public DTMAxisIterator reset() {
    this._source.reset();
    return resetPosition();
  }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ForwardPositionIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */