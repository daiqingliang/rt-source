package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class FilterIterator extends DTMAxisIteratorBase {
  private DTMAxisIterator _source;
  
  private final DTMFilter _filter;
  
  private final boolean _isReverse;
  
  public FilterIterator(DTMAxisIterator paramDTMAxisIterator, DTMFilter paramDTMFilter) {
    this._source = paramDTMAxisIterator;
    this._filter = paramDTMFilter;
    this._isReverse = paramDTMAxisIterator.isReverse();
  }
  
  public boolean isReverse() { return this._isReverse; }
  
  public void setRestartable(boolean paramBoolean) {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator cloneIterator() {
    try {
      FilterIterator filterIterator = (FilterIterator)clone();
      filterIterator._source = this._source.cloneIterator();
      filterIterator._isRestartable = false;
      return filterIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  public DTMAxisIterator reset() {
    this._source.reset();
    return resetPosition();
  }
  
  public int next() {
    int i;
    while ((i = this._source.next()) != -1) {
      if (this._filter.acceptNode(i, -1) == 1)
        return returnNode(i); 
    } 
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      this._source.setStartNode(this._startNode = paramInt);
      return resetPosition();
    } 
    return this;
  }
  
  public void setMark() { this._source.setMark(); }
  
  public void gotoMark() { this._source.gotoMark(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\FilterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */