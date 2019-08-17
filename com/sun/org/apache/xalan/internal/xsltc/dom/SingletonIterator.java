package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class SingletonIterator extends DTMAxisIteratorBase {
  private int _node;
  
  private final boolean _isConstant;
  
  public SingletonIterator() { this(-2147483648, false); }
  
  public SingletonIterator(int paramInt) { this(paramInt, false); }
  
  public SingletonIterator(int paramInt, boolean paramBoolean) {
    this._node = this._startNode = paramInt;
    this._isConstant = paramBoolean;
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isConstant) {
      this._node = this._startNode;
      return resetPosition();
    } 
    if (this._isRestartable) {
      if (this._node <= 0)
        this._node = this._startNode = paramInt; 
      return resetPosition();
    } 
    return this;
  }
  
  public DTMAxisIterator reset() {
    if (this._isConstant) {
      this._node = this._startNode;
      return resetPosition();
    } 
    boolean bool = this._isRestartable;
    this._isRestartable = true;
    setStartNode(this._startNode);
    this._isRestartable = bool;
    return this;
  }
  
  public int next() {
    int i = this._node;
    this._node = -1;
    return returnNode(i);
  }
  
  public void setMark() { this._markedNode = this._node; }
  
  public void gotoMark() { this._node = this._markedNode; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SingletonIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */