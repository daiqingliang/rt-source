package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;

public abstract class DTMAxisIteratorBase implements DTMAxisIterator {
  protected int _last = -1;
  
  protected int _position = 0;
  
  protected int _markedNode;
  
  protected int _startNode = -1;
  
  protected boolean _includeSelf = false;
  
  protected boolean _isRestartable = true;
  
  public int getStartNode() { return this._startNode; }
  
  public DTMAxisIterator reset() {
    boolean bool = this._isRestartable;
    this._isRestartable = true;
    setStartNode(this._startNode);
    this._isRestartable = bool;
    return this;
  }
  
  public DTMAxisIterator includeSelf() {
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
  
  public DTMAxisIterator cloneIterator() {
    try {
      DTMAxisIteratorBase dTMAxisIteratorBase = (DTMAxisIteratorBase)clone();
      dTMAxisIteratorBase._isRestartable = false;
      return dTMAxisIteratorBase;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new WrappedRuntimeException(cloneNotSupportedException);
    } 
  }
  
  protected final int returnNode(int paramInt) {
    this._position++;
    return paramInt;
  }
  
  protected final DTMAxisIterator resetPosition() {
    this._position = 0;
    return this;
  }
  
  public boolean isDocOrdered() { return true; }
  
  public int getAxis() { return -1; }
  
  public void setRestartable(boolean paramBoolean) { this._isRestartable = paramBoolean; }
  
  public int getNodeByPosition(int paramInt) {
    if (paramInt > 0) {
      int i = isReverse() ? (getLast() - paramInt + 1) : paramInt;
      int j;
      while ((j = next()) != -1) {
        if (i == getPosition())
          return j; 
      } 
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMAxisIteratorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */