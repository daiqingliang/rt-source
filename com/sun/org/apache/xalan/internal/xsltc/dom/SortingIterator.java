package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class SortingIterator extends DTMAxisIteratorBase {
  private static final int INIT_DATA_SIZE = 16;
  
  private DTMAxisIterator _source;
  
  private NodeSortRecordFactory _factory;
  
  private NodeSortRecord[] _data;
  
  private int _free = 0;
  
  private int _current;
  
  public SortingIterator(DTMAxisIterator paramDTMAxisIterator, NodeSortRecordFactory paramNodeSortRecordFactory) {
    this._source = paramDTMAxisIterator;
    this._factory = paramNodeSortRecordFactory;
  }
  
  public int next() { return (this._current < this._free) ? this._data[this._current++].getNode() : -1; }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    try {
      this._source.setStartNode(this._startNode = paramInt);
      this._data = new NodeSortRecord[16];
      this._free = 0;
      while ((paramInt = this._source.next()) != -1)
        addRecord(this._factory.makeNodeSortRecord(paramInt, this._free)); 
      quicksort(0, this._free - 1);
      this._current = 0;
      return this;
    } catch (Exception exception) {
      return this;
    } 
  }
  
  public int getPosition() { return (this._current == 0) ? 1 : this._current; }
  
  public int getLast() { return this._free; }
  
  public void setMark() {
    this._source.setMark();
    this._markedNode = this._current;
  }
  
  public void gotoMark() {
    this._source.gotoMark();
    this._current = this._markedNode;
  }
  
  public DTMAxisIterator cloneIterator() {
    try {
      SortingIterator sortingIterator = (SortingIterator)clone();
      sortingIterator._source = this._source.cloneIterator();
      sortingIterator._factory = this._factory;
      sortingIterator._data = this._data;
      sortingIterator._free = this._free;
      sortingIterator._current = this._current;
      sortingIterator.setRestartable(false);
      return sortingIterator.reset();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
      return null;
    } 
  }
  
  private void addRecord(NodeSortRecord paramNodeSortRecord) {
    if (this._free == this._data.length) {
      NodeSortRecord[] arrayOfNodeSortRecord = new NodeSortRecord[this._data.length * 2];
      System.arraycopy(this._data, 0, arrayOfNodeSortRecord, 0, this._free);
      this._data = arrayOfNodeSortRecord;
    } 
    this._data[this._free++] = paramNodeSortRecord;
  }
  
  private void quicksort(int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      int i = partition(paramInt1, paramInt2);
      quicksort(paramInt1, i);
      paramInt1 = i + 1;
    } 
  }
  
  private int partition(int paramInt1, int paramInt2) {
    NodeSortRecord nodeSortRecord = this._data[paramInt1 + paramInt2 >>> 1];
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    while (true) {
      if (nodeSortRecord.compareTo(this._data[--j]) < 0)
        continue; 
      while (nodeSortRecord.compareTo(this._data[++i]) > 0);
      if (i < j) {
        NodeSortRecord nodeSortRecord1 = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = nodeSortRecord1;
        continue;
      } 
      break;
    } 
    return j;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SortingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */