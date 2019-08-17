package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public abstract class MultiValuedNodeHeapIterator extends DTMAxisIteratorBase {
  private static final int InitSize = 8;
  
  private int _heapSize = 0;
  
  private int _size = 8;
  
  private HeapNode[] _heap = new HeapNode[8];
  
  private int _free = 0;
  
  private int _returnedLast;
  
  private int _cachedReturnedLast = -1;
  
  private int _cachedHeapSize;
  
  public DTMAxisIterator cloneIterator() {
    this._isRestartable = false;
    HeapNode[] arrayOfHeapNode = new HeapNode[this._heap.length];
    try {
      MultiValuedNodeHeapIterator multiValuedNodeHeapIterator = (MultiValuedNodeHeapIterator)clone();
      for (byte b = 0; b < this._free; b++)
        arrayOfHeapNode[b] = this._heap[b].cloneHeapNode(); 
      multiValuedNodeHeapIterator.setRestartable(false);
      multiValuedNodeHeapIterator._heap = arrayOfHeapNode;
      return multiValuedNodeHeapIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  protected void addHeapNode(HeapNode paramHeapNode) {
    if (this._free == this._size) {
      HeapNode[] arrayOfHeapNode = new HeapNode[this._size *= 2];
      System.arraycopy(this._heap, 0, arrayOfHeapNode, 0, this._free);
      this._heap = arrayOfHeapNode;
    } 
    this._heapSize++;
    this._heap[this._free++] = paramHeapNode;
  }
  
  public int next() {
    while (this._heapSize > 0) {
      int i = (this._heap[0])._node;
      if (i == -1) {
        if (this._heapSize > 1) {
          HeapNode heapNode = this._heap[0];
          this._heap[0] = this._heap[--this._heapSize];
          this._heap[this._heapSize] = heapNode;
        } else {
          return -1;
        } 
      } else if (i == this._returnedLast) {
        this._heap[0].step();
      } else {
        this._heap[0].step();
        heapify(0);
        return returnNode(this._returnedLast = i);
      } 
      heapify(0);
    } 
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (this._isRestartable) {
      this._startNode = paramInt;
      int i;
      for (i = 0; i < this._free; i++) {
        if (!(this._heap[i])._isStartSet) {
          this._heap[i].setStartNode(paramInt);
          this._heap[i].step();
          (this._heap[i])._isStartSet = true;
        } 
      } 
      for (i = (this._heapSize = this._free) / 2; i >= 0; i--)
        heapify(i); 
      this._returnedLast = -1;
      return resetPosition();
    } 
    return this;
  }
  
  protected void init() {
    for (byte b = 0; b < this._free; b++)
      this._heap[b] = null; 
    this._heapSize = 0;
    this._free = 0;
  }
  
  private void heapify(int paramInt) {
    while (true) {
      int i = paramInt + 1 << 1;
      int j = i - 1;
      int k = (j < this._heapSize && this._heap[j].isLessThan(this._heap[paramInt])) ? j : paramInt;
      if (i < this._heapSize && this._heap[i].isLessThan(this._heap[k]))
        k = i; 
      if (k != paramInt) {
        HeapNode heapNode = this._heap[k];
        this._heap[k] = this._heap[paramInt];
        this._heap[paramInt] = heapNode;
        paramInt = k;
        continue;
      } 
      break;
    } 
  }
  
  public void setMark() {
    for (byte b = 0; b < this._free; b++)
      this._heap[b].setMark(); 
    this._cachedReturnedLast = this._returnedLast;
    this._cachedHeapSize = this._heapSize;
  }
  
  public void gotoMark() {
    int i;
    for (i = 0; i < this._free; i++)
      this._heap[i].gotoMark(); 
    for (i = (this._heapSize = this._cachedHeapSize) / 2; i >= 0; i--)
      heapify(i); 
    this._returnedLast = this._cachedReturnedLast;
  }
  
  public DTMAxisIterator reset() {
    int i;
    for (i = 0; i < this._free; i++) {
      this._heap[i].reset();
      this._heap[i].step();
    } 
    for (i = (this._heapSize = this._free) / 2; i >= 0; i--)
      heapify(i); 
    this._returnedLast = -1;
    return resetPosition();
  }
  
  public abstract class HeapNode implements Cloneable {
    protected int _node;
    
    protected int _markedNode;
    
    protected boolean _isStartSet = false;
    
    public abstract int step();
    
    public HeapNode cloneHeapNode() {
      HeapNode heapNode;
      try {
        heapNode = (HeapNode)clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
        return null;
      } 
      heapNode._node = this._node;
      heapNode._markedNode = this._node;
      return heapNode;
    }
    
    public void setMark() { this._markedNode = this._node; }
    
    public void gotoMark() { this._node = this._markedNode; }
    
    public abstract boolean isLessThan(HeapNode param1HeapNode);
    
    public abstract HeapNode setStartNode(int param1Int);
    
    public abstract HeapNode reset();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MultiValuedNodeHeapIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */