package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public class ArrayNodeListIterator implements DTMAxisIterator {
  private int _pos = 0;
  
  private int _mark = 0;
  
  private int[] _nodes;
  
  private static final int[] EMPTY = new int[0];
  
  public ArrayNodeListIterator(int[] paramArrayOfInt) { this._nodes = paramArrayOfInt; }
  
  public int next() { return (this._pos < this._nodes.length) ? this._nodes[this._pos++] : -1; }
  
  public DTMAxisIterator reset() {
    this._pos = 0;
    return this;
  }
  
  public int getLast() { return this._nodes.length; }
  
  public int getPosition() { return this._pos; }
  
  public void setMark() { this._mark = this._pos; }
  
  public void gotoMark() { this._pos = this._mark; }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (paramInt == -1)
      this._nodes = EMPTY; 
    return this;
  }
  
  public int getStartNode() { return -1; }
  
  public boolean isReverse() { return false; }
  
  public DTMAxisIterator cloneIterator() { return new ArrayNodeListIterator(this._nodes); }
  
  public void setRestartable(boolean paramBoolean) {}
  
  public int getNodeByPosition(int paramInt) { return this._nodes[paramInt - 1]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ArrayNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */