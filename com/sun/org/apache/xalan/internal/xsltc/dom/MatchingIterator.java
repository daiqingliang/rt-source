package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class MatchingIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  private final int _match;
  
  public MatchingIterator(int paramInt, DTMAxisIterator paramDTMAxisIterator) {
    this._source = paramDTMAxisIterator;
    this._match = paramInt;
  }
  
  public void setRestartable(boolean paramBoolean) {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator cloneIterator() {
    try {
      MatchingIterator matchingIterator = (MatchingIterator)clone();
      matchingIterator._source = this._source.cloneIterator();
      matchingIterator._isRestartable = false;
      return matchingIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      this._source.setStartNode(paramInt);
      this._position = 1;
      while ((paramInt = this._source.next()) != -1 && paramInt != this._match)
        this._position++; 
    } 
    return this;
  }
  
  public DTMAxisIterator reset() {
    this._source.reset();
    return resetPosition();
  }
  
  public int next() { return this._source.next(); }
  
  public int getLast() {
    if (this._last == -1)
      this._last = this._source.getLast(); 
    return this._last;
  }
  
  public int getPosition() { return this._position; }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MatchingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */