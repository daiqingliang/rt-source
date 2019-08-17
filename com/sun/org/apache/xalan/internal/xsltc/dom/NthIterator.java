package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class NthIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  private final int _position;
  
  private boolean _ready;
  
  public NthIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt) {
    this._source = paramDTMAxisIterator;
    this._position = paramInt;
  }
  
  public void setRestartable(boolean paramBoolean) {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator cloneIterator() {
    try {
      NthIterator nthIterator = (NthIterator)clone();
      nthIterator._source = this._source.cloneIterator();
      nthIterator._isRestartable = false;
      return nthIterator;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public int next() {
    if (this._ready) {
      this._ready = false;
      return this._source.getNodeByPosition(this._position);
    } 
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      this._source.setStartNode(paramInt);
      this._ready = true;
    } 
    return this;
  }
  
  public DTMAxisIterator reset() {
    this._source.reset();
    this._ready = true;
    return this;
  }
  
  public int getLast() { return 1; }
  
  public int getPosition() { return 1; }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NthIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */